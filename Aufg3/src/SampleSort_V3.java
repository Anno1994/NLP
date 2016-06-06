import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by max on 19.05.16.
 */
public class SampleSort_V3 {

    public static void main(String[] args) {
        SampleSort_V3 sort_v3 = new SampleSort_V3();
        int[] test = {5, 1, 4, 10, 2, 8, 6};
        sort_v3.sampleSort(test, 2);
        System.out.println(Arrays.toString(test));
    }

    public int[] sampleSort(int[] array, int pivotCount) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        RecursiveTask<int[]> task = new SampleSort_V3.SampleTask(array, pivotCount);
        forkJoinPool.submit(task);
        return task.join();
    }

    protected class SampleTask extends RecursiveTask<int[]> {
        private int[] array;
        private int pivotCount;

        public SampleTask(int[] array, int pivotCount) {
            this.array = array;
            this.pivotCount = pivotCount;
        }

        private int[] computePivots() {
            if (pivotCount > array.length) {
                pivotCount = 2;
            }
            int[] pivots = new int[pivotCount];
            for (int i = 0; i < pivotCount; i++) {
                pivots[i] = array[i];
            }
            Arrays.parallelSort(pivots);
            return pivots;
        }

        @Override
        protected int[] compute() {
            if (array.length <= 1) {
                return array;
            } else if (array.length == 2) {
                if (array[0] < array[1])
                    return array;
                else {
                    int tmp = array[0];
                    array[0] = array[1];
                    array[1] = tmp;
                    return array;
                }

            } else {

                int[] pivots = computePivots();

                int[][] partitionsArray = new int[pivotCount + 1][];

                int lastArraySize = 0;
                for (int i = 0; i < pivots.length; i++) {
                    int arraySize = 0;
                    for (int j = 0; j < array.length; j++) {
                        if (i > 0) {
                            if (array[j] <= pivots[i] && array[j] > pivots[i - 1]) {
                                arraySize++;
                            } else if (array[j] > pivots[pivots.length - 1]) {
                                lastArraySize++;
                            }
                        } else if (i == 0) {
                            if (array[j] <= pivots[i]) {
                                arraySize++;
                            }
                        }

                    }
                    partitionsArray[i] = new int[arraySize];
                }
                partitionsArray[pivotCount] = new int[lastArraySize];

                int lastArrayPointer = 0;
                for (int i = 0; i < pivots.length; i++) {
                    int arrayPointer = 0;
                    for (int j = 0; j < array.length; j++) {
                        if (i > 0) {
                            if (array[j] <= pivots[i] && array[j] > pivots[i - 1]) {
                                partitionsArray[i][arrayPointer] = array[j];
                                arrayPointer++;
                            }
                        } else if (i == 0) {
                            if (array[j] <= pivots[i]) {
                                partitionsArray[i][arrayPointer] = array[j];
                                arrayPointer++;
                            } else if (array[j] > pivots[pivots.length - 1]) {
                                partitionsArray[partitionsArray.length - 1][lastArrayPointer] = array[j];
                                lastArrayPointer++;
                            }
                        }

                    }
                }

                RecursiveTask<int[]>[] tasks = new RecursiveTask[partitionsArray.length];

                for (int i = 0; i < partitionsArray.length; i++) {
                    tasks[i] = new SampleTask(partitionsArray[i], pivotCount);
                }

                for (int i = 0; i < tasks.length; i++) {
                    tasks[i].fork();
                }

                int[] result = new int[array.length];
                for (int i = 0; i < tasks.length; i++) {
                    int arrayPointer = 0;
                    int[] tmp = tasks[i].join();
                    System.out.println(Arrays.toString(tmp));
                    for (int j = 0; j < tmp.length; j++) {
                        result[arrayPointer] = tmp[j];
                        arrayPointer++;
                    }
                }
                return result;
            }
        }
    }
}