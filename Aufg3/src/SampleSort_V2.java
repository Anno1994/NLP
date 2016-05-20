import java.sql.Time;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SampleSort_V2 {

    public List<Integer> sampleSort(List<Integer> list, int pivotCount) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        RecursiveTask<List<Integer>> task = new SampleTask(list, pivotCount);
        forkJoinPool.submit(task);
        return task.join();
    }

    protected class SampleTask extends RecursiveTask<List<Integer>> {
        private List<Integer> list;
        private int pivotCount;

        public SampleTask(List<Integer> list, int pivotCount) {
            this.list = list;
            this.pivotCount = pivotCount;
        }

        protected List<Integer> computePivots() {
            if (pivotCount > list.size()){
//                System.out.println("Too much pivots. we will use 2 pivot elements to sort the list");
                pivotCount = 2;
            }
            List<Integer> pivots = new ArrayList<Integer>();
            for (int i=0; i<pivotCount; i++){
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
                pivots.add(list.get(i));
            }
            Collections.sort(pivots);
            return pivots;
        }

        @Override
        protected List<Integer> compute() {
            if (list.size() <= 1) {
                return list;
            } else if (list.size() == 2) {
                if (list.get(0) < list.get(1))
                    return list;
                else {
                    int tmp = list.get(0);
                    list.set(0, list.get(1));
                    list.set(1, tmp);
                    return list;
                }

            } else {

                List<Integer> pivots = computePivots();

                List<List<Integer>> partitionLists = new ArrayList<List<Integer>>();

                for (int i = 0; i < pivotCount+1; i++) {
                    partitionLists.add(new ArrayList<Integer>());
                }

                for (int i = 0; i < list.size(); i++) {
                    for (int j = 0; j < pivots.size(); j++) {
                        if (list.get(i) <= pivots.get(j)) {
                            partitionLists.get(j).add(list.get(i));
                            break;
                        } else if(list.get(i) > pivots.get(pivots.size()-1)) {
                            partitionLists.get(partitionLists.size()-1).add(list.get(i));
                            break;
                        }
                    }
                }

                List<RecursiveTask<List<Integer>>> tasks = new ArrayList<>();

                for (int i = 0; i<partitionLists.size(); i++) {
                    if(!partitionLists.get(i).isEmpty())
                        tasks.add(new SampleTask(partitionLists.get(i), pivotCount));
                }

                for (int i = 0; i < tasks.size(); i++) {
                    tasks.get(i).fork();
                }

                List<Integer> result = new ArrayList<>();
                for (int i = 0; i < tasks.size(); i++) {
                    result.addAll(tasks.get(i).join());
                }
                return result;
            }
        }
    }

    public static void main(String[] args) {
        Random rn = new Random();
        List<Integer> list = new ArrayList<Integer>();
        for (int i = 0; i < 10000; i++) {
            int num = rn.nextInt(1000000000);
            if (!list.contains(num)) {
                list.add(num);
            }
        }
        SampleSort_V2 sorter_v2 = new SampleSort_V2();
        SampleSort sorter = new SampleSort();

        long starT = System.currentTimeMillis();
        List<Integer> result = sorter_v2.sampleSort(list, 4);
        long endT = System.currentTimeMillis();
        System.out.println("samplesortv2: "+(endT-starT)+"ms");
//        System.out.println(result);

        starT = System.currentTimeMillis();
        List<Integer> res = sorter.sampleSort(list);

        endT = System.currentTimeMillis();
        System.out.println("samplesortv1: "+(endT-starT)+"ms");


        int[] array = new int[list.size()];
        for (int i = 0; i<list.size(); i++) {
            array[i] = list.get(i);
        }
        starT = System.currentTimeMillis();
        Arrays.parallelSort(array);
        endT = System.currentTimeMillis();
        System.out.println("Arrays.parallesort: "+(endT-starT)+"ms");
//        System.out.println(Arrays.toString(array));


        for (int i = 0; i<list.size(); i++) {
            array[i] = list.get(i);
        }
        starT = System.currentTimeMillis();
        Arrays.sort(array);
        endT = System.currentTimeMillis();
        System.out.println("Arrays.sort: "+(endT-starT)+"ms");
    }

}

