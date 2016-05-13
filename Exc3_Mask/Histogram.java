package Exc3_Mask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.RunnableFuture;


/**
 * Created by max on 13.05.16.
 */
public class Histogram {
    private int[][] pixels;
    protected int[] red = new int[256];
    protected int[] green = new int[256];
    protected int[] blue = new int[256];
    private final int width;
    private final int height;

    public Histogram(BufferedImage image) {
        width = image.getWidth();
        height = image.getHeight();
        pixels = new int[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                pixels[row][col] = image.getRGB(col, row);
            }
        }
    }

    private void computeHistogram(int[] result,int mask, int start, int stop){            //calculate histogram
        for (int row = start; row < stop; row++) {                                       //from start to stop
            for (int col = 0; col < width; col++) {
                int i = pixels[row][col] & mask;
                if (i > 0xff && i <0xff0000) {
                    i = i >> 8;
                }
                if (i > 0xff00) {
                    i = i >> 16;
                }
                result[i]++;
            }
        }
    }

    public int[] getHistogram(int mask) {                                               //Single threaded
        int[] result = new int[256];
        computeHistogram(result, mask, 0, height);
        return result;
    }


    public int[] getHistogramThreaded(int mask, int threadCount){
        int range = height/threadCount;
        if (range < 1) {
            System.out.println("Too much threads for this picture!");                  //Go for singlethreaded method if
            return getHistogram(mask);                                                 //there are too many threads
        }
        Thread[] threads = new Thread[threadCount];
        int[] result = new int[256];
        for (int i=0; i<threadCount; i++) {
            final int start = i * range;
            final int end;
            if (i == threadCount - 1)
                end = height;
            else end =  (i + 1) * range;
            (threads[i] = new Thread(() -> {
                computeHistogram(result, mask, start, end);
            })).run();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public int[] getRedCopy() {
        return Arrays.copyOf(red, red.length);
    }

    protected int[] getRed() {
        return red;
    }

    public int[] getGreenCopy() {
        return Arrays.copyOf(green, green.length);
    }

    protected  int[] getGreen() {
        return green;
    }

    public int[] getBlueCopy() {
        return Arrays.copyOf(blue, blue.length);
    }

    protected int[] getBlue() {
        return blue;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        BufferedImage image = ImageIO.read(Histogram.class.getResource("test.png"));
        Histogram histo = new Histogram(image);
        System.out.println(Arrays.toString(histo.getHistogram(0xFF0000)));
    }



}
