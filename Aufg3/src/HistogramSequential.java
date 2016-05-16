import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

public class HistogramSequential {

    /**
     * untersucht 'image' anhand von 'mask' im angegebenen Bereich,
     * und schreibt die entsprechenden Werte ins result-Array.
     */
    public static void computeHistogram(int[] result, BufferedImage image, int mask,
                                        int xStart, int yStart, int xEnd, int yEnd) {
        for (int y = yStart; y < yEnd && y < image.getHeight(); y++) {
            for (int x = xStart; x < xEnd && x < image.getWidth(); x++) {
                int i = image.getRGB(x, y) & mask;
                if (i > 0xff && i <= 0xffff)
                    i = i >> 8;
                else if (i > 0xffff)
                    i = i >> 16;
                result[i]++;
            }
        }
    }

    /**
     * Wie oben, nur das für das schreiben ein monitor auf dem result-array vorhanden ist.
     */
    public static void computeHistogramThreadSafe(int[] result, BufferedImage image, int mask,
                                        int xStart, int yStart, int xEnd, int yEnd) {
        for (int y = yStart; y < yEnd && y < image.getHeight(); y++) {
            for (int x = xStart; x < xEnd && x < image.getWidth(); x++) {
                int i = image.getRGB(x, y) & mask;
                if (i > 0xff && i <= 0xffff)
                    i = i >> 8;
                else if (i > 0xffff)
                    i = i >> 16;
                synchronized (result) {
                    result[i]++;
                }
            }
        }
    }

    public static int[] getHistogram(BufferedImage image, int mask) {
        int[] result = new int[256];
        final int xEnd = image.getWidth();
        final int yEnd = image.getHeight();
        computeHistogram(result, image, mask, 0, 0, xEnd, yEnd);
        return result;
    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(HistogramSequential.class.getResource("fki_start.jpg"));
        int[] histogram = getHistogram(image, 0xff);
        System.out.println(Arrays.toString(histogram));
    }

}
