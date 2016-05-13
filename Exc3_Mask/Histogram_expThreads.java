package Exc3_Mask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by max on 13.05.16.
 */
public class Histogram_expThreads extends Histogram {
    Thread[] threads = new Thread[3];

    public Histogram_expThreads(BufferedImage image) {
        super(image);
    }

    public void computeRGB_a() throws InterruptedException {
        (threads[0] = new Thread(() -> red = getHistogram(0xFF0000))).run();
        (threads[1] = new Thread(() -> green = getHistogram(0xFF00))).run();
        (threads[2] = new Thread(() -> blue = getHistogram(0xFF))).run();
        for (Thread t : threads)
            t.join();
    }

    public void computeRGB_b() throws InterruptedException {
        int tCount = 4;
        (threads[0] = new Thread(() -> red = getHistogramThreaded(0xFF0000, tCount))).run();
        (threads[1] = new Thread(() -> green = getHistogramThreaded(0xFF00, tCount))).run();
        (threads[2] = new Thread(() -> blue = getHistogramThreaded(0xFF, tCount))).run();
        for (Thread t : threads)
            t.join();
    }

//    private class Task implements Runnable {
//        private int mask;
//        private int[] array;
//
//        Task(int mask, int[] array) {
//            this.mask = mask;
//            this.array = array;
//        }
//
//        public void run() {
//            array = Histogram_expThreads.this.getHistogram(mask);
//        }
//    }

    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage image = ImageIO.read(Histogram.class.getResource("test.png"));
        Histogram_expThreads histo = new Histogram_expThreads(image);
        histo.computeRGB_b();
        System.out.println(Arrays.toString(histo.red));
        System.out.println(Arrays.toString(histo.green));
        System.out.println(Arrays.toString(histo.blue));
    }
}
