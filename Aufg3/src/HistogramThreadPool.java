
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;


public class HistogramThreadPool {

    final BufferedImage image;
    int height;
    int width;
    int[] red = new int[256];
    int[] green = new int[256];
    int[] blue = new int[256];

    public HistogramThreadPool(BufferedImage image) {
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
        ExecutorService executor = Executors.newFixedThreadPool(3);
        executor.submit(() -> blue = HistogramSequential.getHistogram(image, 0xff));
        executor.submit(() -> green = HistogramSequential.getHistogram(image, 0xff00));
        executor.submit(() -> red = HistogramSequential.getHistogram(image, 0xff0000));
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
    }

    //b.)
    // Da die Threads sich ein Array teilen, muss die Thread-sichere-Variante
    // zur Historgrammberechnung verwendet werden.
    public int[] computeHistogramThreaded(int mask, int threadCount) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        int[] result = new int[256];
        int x, y, xStep, yStep;
        if (width >= threadCount) {
            x = xStep = width / threadCount;
            y = 0;
            yStep = height;
        } else if (height >= threadCount) {
            x = 0;
            xStep = width;
            y = yStep = height / threadCount;
        } else { //Threshold: 1 Thread pro Zeile/Spalte
            if (width >= height) {
                x = xStep = 1;
                y = 0;
                yStep = height;
            } else {
                x = 0;
                xStep = width;
                y = yStep = 1;
            }
        }
        for (int i = 0; i < threadCount; i++) {
            int xStart = x * i;
            int yStart = y * i;
            int xEnd = i == threadCount - 1 ? width : xStart + xStep;
            int yEnd = i == threadCount - 1 ? height : yStart + yStep;
            executor.submit(() -> HistogramSequential.computeHistogramThreadSafe(result, image, mask, xStart, yStart, xEnd, yEnd));
        }
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        return result;
    }

    //c.)
    public int[] computeHistogramThreaded2(int mask, int threadCount) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        List<Future<int[]>> subResults = new ArrayList<>(threadCount);
        int[] result = new int[256];
        int x, y, xStep, yStep;
        if (width >= threadCount) {
            x = xStep = width / threadCount;
            y = 0;
            yStep = height;
        } else if (height >= threadCount) {
            x = 0;
            xStep = width;
            y = yStep = height / threadCount;
        } else { //Threshold: 1 Thread pro Zeile/Spalte
            if (width >= height) {
                x = xStep = 1;
                y = 0;
                yStep = height;
            } else {
                x = 0;
                xStep = width;
                y = yStep = 1;
            }
        }
        for (int i = 0; i < threadCount; i++) {
            int xStart = x * i;
            int yStart = y * i;
            int xEnd = i == threadCount - 1 ? width : xStart + xStep;
            int yEnd = i == threadCount - 1 ? height : yStart + yStep;
            subResults.add(executor.submit(new HistogramTask(image, mask, xStart, yStart, xEnd, yEnd)));
        }
        executor.shutdown();
        // Weil get() wartet, bis ein ergebnis zurück kommt, ist es nicht nötig zustätzlich auf
        // die beendigung der tasks zu warten.
        for (Future<int[]> f : subResults) {
            int[] subRes = f.get();
            for (int i = 0; i < subRes.length; i++) {
                result[i] += subRes[i];/**/
            }
        }
        return result;
    }

    class HistogramTask implements Callable<int[]> {
        private final BufferedImage image;
        private final int mask;
        private final int xStart;
        private final int yStart;
        private final int xEnd;
        private final int yEnd;

        public HistogramTask(BufferedImage image, int mask, int xStart, int yStart, int xEnd, int yEnd) {
            this.image = image;
            this.mask = mask;
            this.xStart = xStart;
            this.yStart = yStart;
            this.xEnd = xEnd;
            this.yEnd = yEnd;
        }

        @Override
        public int[] call() throws Exception {
            int[] result = new int[256];
            HistogramSequential.computeHistogram(result, image, mask, xStart, yStart, xEnd, yEnd);
            return result;
        }
    }

    public static void main(String[] args) throws ExecutionException, IOException, InterruptedException {
        BufferedImage image = ImageIO.read(HistogramSequential.class.getResource("fki_start.jpg"));
        HistogramThreadPool ht = new HistogramThreadPool(image);

        ht.computeHistogramRGB();
        System.out.println("RED: " + Arrays.toString(ht.getRed()));
        System.out.println("GREEN: " + Arrays.toString(ht.getGreen()));
        System.out.println("BLUE: " + Arrays.toString(ht.getBlue()));

        System.out.println(Arrays.toString(ht.computeHistogramThreaded(0xff, 4)));

        System.out.println(Arrays.toString(ht.computeHistogramThreaded2(0xff, 4)));
    }
}
