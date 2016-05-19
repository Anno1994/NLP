import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SampleSort {

    public List<Integer> sampleSort(List<Integer> list) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        RecursiveTask<List<Integer>> task = new SampleTask(list);
        forkJoinPool.submit(task);
        return task.join();
    }

    private class SampleTask extends RecursiveTask<List<Integer>> {
        private List<Integer> list;

        public SampleTask(List<Integer> list) {
            this.list = list;
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
                int pivot1 = list.get(0);
                int pivot2 = list.get(list.size() - 1);

                if (pivot1 > pivot2) {
                    int tmp = pivot1;
                    pivot1 = pivot2;
                    pivot2 = tmp;
                }

                List<Integer> left = new ArrayList<Integer>();
                List<Integer> middle = new ArrayList<Integer>();
                List<Integer> right = new ArrayList<Integer>();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i) < pivot1)
                        left.add(list.get(i));
                    else if (list.get(i) > pivot2)
                        right.add(list.get(i));
                    else
                        middle.add(list.get(i));
                }

                List<RecursiveTask<List<Integer>>> tasks = new ArrayList<>();
                tasks.add(new SampleTask(left));
                tasks.add(new SampleTask(middle));
                tasks.add(new SampleTask(right));

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
        int[] test = {5, 1, 4, 10, 2, 8, 6};
        List<Integer> list = IntStream.of(test).boxed().collect(Collectors.toList());
        SampleSort sorter = new SampleSort();


    }
}

