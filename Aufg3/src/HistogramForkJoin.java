import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class HistogramForkJoin {

    final BufferedImage image;
    int height;
    int width;
    int[] red = new int[256];
    int[] green = new int[256];
    int[] blue = new int[256];

    public HistogramForkJoin(BufferedImage image) {
        this.image = image;
        if (image != null) {
            this.height = image.getHeight();
            this.width = image.getWidth();
        }
    }

    public int[] getRed() {
        return red;
    }

    public int[] getGreen() {
        return green;
    }

    public int[] getBlue() {
        return blue;
    }

    //a.)
    public void computeHistogramRGB() throws InterruptedException, ExecutionException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<int[]>[] tasks = new ForkJoinTask[3];
        tasks[0] = forkJoinPool.submit(new HistogramForkJoinTask(image, 0xff, 0, 0, height));
        tasks[1] = forkJoinPool.submit(new HistogramForkJoinTask(image, 0xff00, 0, 0, height));
        tasks[2] = forkJoinPool.submit(new HistogramForkJoinTask(image, 0xff0000, 0, 0, height));
        blue = tasks[0].join();
        green = tasks[1].join();
        red = tasks[2].join();
    }

    //b.)
    // Da die Threads sich ein Array teilen, muss die Thread-sichere-Variante
    // zur Historgrammberechnung verwendet werden.
    public int[] computeHistogramThreaded(int mask) throws InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        int[] result = new int[256];
        RecursiveAction action = new HistogramForkJoinTask_SharedMemory(image, mask, 0, 0, height, result);
        forkJoinPool.submit(action);
        action.join();
        return result;
    }

    //c.)
    public int[] computeHistogramThreaded2(int mask) throws InterruptedException, ExecutionException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        RecursiveTask<int[]> task = new HistogramForkJoinTask(image, mask, 0, 0, height);
        forkJoinPool.submit(task);
        return task.join();
    }

    class HistogramForkJoinTask extends RecursiveTask<int[]> {
        private final BufferedImage image;
        private final int mask;
        private final int rowsToCheck;
        private final int xStart;
        private final int yStart;
        private final int threshold = 5; //bis zu 5 zeilen pro task

        public HistogramForkJoinTask(BufferedImage image, int mask, int xStart, int yStart, int rowsToCheck) {
            this.image = image;
            this.mask = mask;
            this.rowsToCheck = rowsToCheck;
            this.xStart = xStart;
            this.yStart = yStart;
        }

        @Override
        protected int[] compute() {
            int[] res = new int[256];
            if (rowsToCheck <= threshold) {
                HistogramSequential.computeHistogram(res, image, mask, xStart, yStart, width, yStart + rowsToCheck);
            } else {
                int[][] subResults = new int[2][256];
                List<HistogramForkJoinTask> subTasks = new ArrayList<>();
                int split = rowsToCheck / 2;
                int top = split;
                int bottom = rowsToCheck - split;
                subTasks.add(new HistogramForkJoinTask(image, mask, xStart, yStart, top));
                subTasks.add(new HistogramForkJoinTask(image, mask, xStart, yStart + split, bottom));
                subTasks.forEach(HistogramForkJoinTask::fork);
                for (int i = 0; i < subTasks.size(); i++) {
                    subResults[i] = subTasks.get(i).join();
                }
                for (int[] subRes : subResults) {
                    for (int i = 0; i < subRes.length; i++) {
                        res[i] += subRes[i];
                    }
                }
            }
            return res;
        }
    }

    class HistogramForkJoinTask_SharedMemory extends RecursiveAction {
        private final BufferedImage image;
        private final int mask;
        private final int rowsToCheck;
        private final int xStart;
        private final int yStart;
        private final int[] dest;
        private final int threshold = 5; //bis zu 4 zeilen pro task

        public HistogramForkJoinTask_SharedMemory(BufferedImage image, int mask, int xStart, int yStart,
                                                  int rowsToCheck, int[] dest) {
            this.image = image;
            this.mask = mask;
            this.rowsToCheck = rowsToCheck;
            this.xStart = xStart;
            this.yStart = yStart;
            this.dest = dest;
        }

        @Override
        protected void compute() {
            if (rowsToCheck <= threshold) {
                HistogramSequential.computeHistogramThreadSafe(dest, image, mask, xStart, yStart, width, yStart + rowsToCheck);
            } else {
                int split = rowsToCheck / 2;
                int top = split;
                int bottom = rowsToCheck - split;
                invokeAll(
                        new HistogramForkJoinTask_SharedMemory(image, mask, xStart, yStart, top, dest),
                        new HistogramForkJoinTask_SharedMemory(image, mask, xStart, yStart + split, bottom, dest));
            }
        }
    }

    public static void main(String[] args) throws ExecutionException, IOException, InterruptedException {
        BufferedImage image = ImageIO.read(HistogramSequential.class.getResource("fki_start.jpg"));
        HistogramForkJoin ht = new HistogramForkJoin(image);

        ht.computeHistogramRGB();
        System.out.println("RED: " + Arrays.toString(ht.getRed()));
        System.out.println("GREEN: " + Arrays.toString(ht.getGreen()));
        System.out.println("BLUE: " + Arrays.toString(ht.getBlue()));

        System.out.println(Arrays.toString(ht.computeHistogramThreaded(0xff)));

        System.out.println(Arrays.toString(ht.computeHistogramThreaded2(0xff)));
    }
}
