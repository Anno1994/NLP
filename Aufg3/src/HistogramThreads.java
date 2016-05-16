import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class HistogramThreads {

    final BufferedImage image;
    int height;
    int width;
    int[] red = new int[256];
    int[] green = new int[256];
    int[] blue = new int[256];

    public HistogramThreads(BufferedImage image) {
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
    // red, green u. blue werden von 3 Threads parallel berechnet. Da die Threads sich untereinander
    // nicht in die Quere kommen sind keine Synchronisierungs-Mechanismen nötig, außer 'join' um
    // sicherzustellen, dass alle Threads fertig werden.
    public void computeHistogramRGB() throws InterruptedException {
        Thread[] threads = new Thread[3];
        (threads[0] = new Thread(() -> blue = HistogramSequential.getHistogram(image, 0xff))).start();
        (threads[1] = new Thread(() -> green = HistogramSequential.getHistogram(image, 0xff00))).start();
        (threads[2] = new Thread(() -> red = HistogramSequential.getHistogram(image, 0xff0000))).start();
        for (Thread t : threads)
            t.join();
    }

    //b.)
    // Es wird versucht das Pixel-Array in etwa gleich große Breiche aufzuteilen,
    // die jeweils von einem eigenen Thread untersucht werden. Da alle Threads in das selbe
    // array schreiben, muss die Thread-sichere Methode in 'HistogramSequential' verwendet werden.
    public int[] computeHistogramThreaded(int mask, int threadCount) throws InterruptedException {
        Thread[] threads = new Thread[threadCount];
        int[] result = new int[256];
        int x, y, xStep, yStep;
        // nur zur Aufteilung des Bildes...
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
        // ...
        for (int i = 0; i < threadCount; i++) {
            int xStart = x * i;
            int yStart = y * i;
            int xEnd = i == threadCount - 1 ? width : xStart + xStep;
            int yEnd = i == threadCount - 1 ? height : yStart + yStep;
            (threads[i] = new Thread(() -> {
                HistogramSequential.computeHistogramThreadSafe(result, image, mask, xStart, yStart, xEnd, yEnd);
            })).start();
        }
        for (Thread t : threads) {
            t.join();
        }
        return result;
    }

    //c.)
    // Same as b.), but each HistogrammThread is initialized with a reference to
    // the sub-result-list, so it can save the sub-result before dying. Therefore
    // in is not necessary to use the thread-safe-method.
    public int[] computeHistogramThreaded2(int mask, int threadCount) throws InterruptedException {
        Thread[] threads = new Thread[threadCount];
        int[][] subResults = new int[threadCount][256];
        int[] result = new int[256];
        int x, y, xStep, yStep;
        // Aufteilung des Bildes
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
        // ...
        for (int i = 0; i < threadCount; i++) {
            int xStart = x * i;
            int yStart = y * i;
            int xEnd = i == threadCount - 1 ? width : xStart + xStep;
            int yEnd = i == threadCount - 1 ? height : yStart + yStep;
            (threads[i] = new HistogramThread(subResults, i, image, mask, xStart, yStart, xEnd, yEnd)).start();
        }
        for (Thread t : threads) {
            t.join();
        }
        for (int[] subRes : subResults) {
            for (int i = 0; i < 256; i++) {
                result[i] += subRes[i];
            }
        }
        return result;
    }

    class HistogramThread extends Thread {
        private int[] result;
        private BufferedImage image;
        private int mask;
        private int xStart;
        private int xEnd;
        private int yStart;
        private int yEnd;

        public HistogramThread(int[][] resultList, int threadNumber, BufferedImage image, int mask,
                               int xStart, int yStart, int xEnd, int yEnd) {
            this.result = new int[256];
            resultList[threadNumber] = result;
            this.image = image;
            this.mask = mask;
            this.xStart = xStart;
            this.yStart = yStart;
            this.xEnd = xEnd;
            this.yEnd = yEnd;
        }

        @Override
        public void run() {
            HistogramSequential.computeHistogram(result, image, mask, xStart, yStart, xEnd, yEnd);
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        BufferedImage image = ImageIO.read(HistogramSequential.class.getResource("fki_start.jpg"));
        HistogramThreads ht = new HistogramThreads(image);

        ht.computeHistogramRGB();
        System.out.println("RED: " + Arrays.toString(ht.getRed()));
        System.out.println("GREEN: " + Arrays.toString(ht.getGreen()));
        System.out.println("BLUE: " + Arrays.toString(ht.getBlue()));

        System.out.println(Arrays.toString(ht.computeHistogramThreaded(0xff, 4)));

        System.out.println(Arrays.toString(ht.computeHistogramThreaded2(0xff, 4)));
    }
}
