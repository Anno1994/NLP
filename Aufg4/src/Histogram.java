import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class Histogram {

    //sequential
    /*
    Get every pixel as integer inside an int[]. In order to do so, it is necessary to
    convert the BufferedImageType to 'TYPE_INT_RGB'.
    Next step takes the int[] and turns it into a stream.
    Map is used to check which values apply for the given mask and,
    if necessary, shift them accordingly to their size.
    forEach is used to increment values in result[] for each
    previously masked and shifted pixel.
     */
    public static int[] getHistogram(BufferedImage image, int mask) {
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        img.setData(image.getRaster());
        int[] result = new int[256];
        Arrays.stream(((DataBufferInt) img.getRaster().getDataBuffer()).getData())
                .map(pixel -> {
                    pixel = pixel & mask;
                    if (pixel > 0xff && pixel <= 0xffff)
                        return pixel >> 8;
                    else if (pixel > 0xffff)
                        return pixel >> 16;
                    else
                        return pixel;
                })
                .forEach(i -> result[i]++);
        return result;
    }

    //parallel
    /*
    Same as sequential implementation, except that after the int[] is
    converted into a stream, the 'parallel()'-method is called. This leads
    to a parallel masking and shifting of the pixels. However, the fact that
    each thread shares the same result[] makes it necessary to use some sort
    of synchronization-mechanism. In this case we listed three options. The first
    uses the 'sequential()' method, which stops the parallelism of the stream and
    makes is proceed without race-conditions.
    The second approach uses a monitor inside the 'forEach()'-method to make sure,
    only one thread at a time can write inside die result[], while the others wait
    for their turn. The third option simply uses an iterator.
     */
    public static int[] getHistogramParallel(BufferedImage image, int mask) {
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        img.setData(image.getRaster());
        int[] result = new int[256];
        Arrays.stream(((DataBufferInt) img.getRaster().getDataBuffer()).getData())
                .parallel()
                .map(pixel -> {
                    pixel = pixel & mask;
                    if (pixel > 0xff && pixel <= 0xffff)
                        return pixel >> 8;
                    else if (pixel > 0xffff)
                        return pixel >> 16;
                    else
                        return pixel;
                })
                .sequential()
                .forEach(i -> result[i]++);
        return result;
    }

    public static int[] getHistogramParallel2(BufferedImage image, int mask) {
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        img.setData(image.getRaster());
        int[] result = new int[256];
        Arrays.stream(((DataBufferInt) img.getRaster().getDataBuffer()).getData())
                .parallel()
                .map(pixel -> {
                    pixel = pixel & mask;
                    if (pixel > 0xff && pixel <= 0xffff)
                        return pixel >> 8;
                    else if (pixel > 0xffff)
                        return pixel >> 16;
                    else
                        return pixel;
                })
                .forEach(i -> {
                    synchronized (result) {
                        result[i]++;
                    }
                });
        return result;
    }

    public static int[] getHistogramParallel3(BufferedImage image, int mask) {
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        img.setData(image.getRaster());
        int[] result = new int[256];
        Iterator<Integer> iterator = Arrays.stream(((DataBufferInt) img.getRaster().getDataBuffer()).getData())
                .parallel()
                .map(pixel -> {
                    pixel = pixel & mask;
                    if (pixel > 0xff && pixel <= 0xffff)
                        return pixel >> 8;
                    else if (pixel > 0xffff)
                        return pixel >> 16;
                    else
                        return pixel;
                })
                .iterator();
        iterator.forEachRemaining(i -> result[i]++);
        return result;
    }


    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(Histogram.class.getResource("fki_start.jpg"));
            System.out.println(Arrays.toString(getHistogram(image, 0xff)));
            System.out.println(Arrays.toString(getHistogramParallel(image, 0xff)));
            System.out.println(Arrays.toString(getHistogramParallel2(image, 0xff)));
            System.out.println(Arrays.toString(getHistogramParallel3(image, 0xff)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
