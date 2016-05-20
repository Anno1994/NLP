import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by max on 19.05.16.
 */
public class SampleSort_V3 {

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
//                System.out.println("Too much pivots. we will use 2 pivot elements to sort the list");
                pivotCount = 2;
            }
            int[] pivots = new int[pivotCount];
            for (int i = 0; i < pivotCount; i++) {
//                Random random = new Random();
//                boolean contains = true;
//                while (contains) {
//                    int pivot = random.nextInt(list.size());
//                    if (pivots.contains(list.get(pivot))){ }
//                    else {
//                        pivots.add(list.get(pivot));
//                        contains = false;
//                    }
//                }
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
                    for (int j = 0; j < array.length; j++) {
                        int arrayPointer = 0;
                        if (i == 0) {
                            if (array[j] <= pivots[i]) {
                                partitionsArray[j][arrayPointer] = array[i];
                                arrayPointer++;
                                continue;
                            } else if (array[j] > pivots[pivots.length - 1]) {
                                partitionsArray[j][lastArrayPointer] = array[i];
                                continue;
                            }
                        } else {
                            if (array[j] <= pivots[i] && array[j] > pivots[i - 1]) {
                                partitionsArray[j][arrayPointer] = array[i];
                                continue;
                            } else if (array[j] > pivots[pivots.length - 1]) {
                                partitionsArray[j][lastArrayPointer] = array[i];
                                continue;
                            }
                        }
                    }
                }
//                for (int i = 0; i < array.length; i++) {
//                    for (int j = 0; j < pivots.length; j++) {
//                        if (array[i] <= pivots[j]) {
//                            partitionsArray[j][arrayPointer] = array[i];
//                            arrayPointer++;
//                            break;
//                        } else if (array[i] > pivots[pivots.length - 1]) {
//                            partitionsArray[partitionsArray.length - 1][lastArrayPointer] = array[i];
//                            lastArrayPointer++;
//                            break;
//                        }
//                    }
//                }
                System.out.println("nlp");


                List<RecursiveTask<List<Integer>>> tasks = new ArrayList<>();

//                for (int i = 0; i<partitionLists.size(); i++) {
//                    if(!partitionLists.get(i).isEmpty())
//                        tasks.add(new SampleSort_V2.SampleTask(partitionLists.get(i), pivotCount));
//                }

                for (int i = 0; i < tasks.size(); i++) {
                    tasks.get(i).fork();
                }

                int[] result = new int[array.length];
//                for (int i = 0; i < tasks.size(); i++) {
//                    result.addAll(tasks.get(i).join());
//                }
                return result;
            }
        }
    }

    public static void main(String[] args) {
        SampleSort_V3 sort_v3 = new SampleSort_V3();
        int[] test = {5, 1, 4, 10, 2, 8, 6};
        List<Integer> list = IntStream.of(test).boxed().collect(Collectors.toList());

        sort_v3.sampleSort(test, 2);
    }
}
