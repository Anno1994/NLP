import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Arrays;

public class Histogram {

    //sequential
    public static int[] getHistogram(BufferedImage image, int mask) {
        BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        img.setData(image.getRaster());
        int[] result = new int[256];
        Arrays.stream(((DataBufferInt) image.getRaster().getDataBuffer()).getData())
                .map(pixel -> {
                    pixel = pixel & mask;
                    if (pixel > 0xff && pixel <= 0xffff)
                        return pixel >> 8;
                    else if (pixel > 0xffff)
                        return pixel >> 16;
                    else
                        return pixel;
                }).forEach(i -> result[i]++);
        return result;
    }


    public static void main(String[] args) {
        try {
            BufferedImage image = ImageIO.read(Histogram.class.getResource("fki_start.jpg"));
            System.out.println(Arrays.toString(getHistogram(image, 0xff)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
