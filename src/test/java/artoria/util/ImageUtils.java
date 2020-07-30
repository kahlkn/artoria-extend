package artoria.util;

import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    @Test
    public void test1() throws IOException {
        File originalImg = new File("F:\\test.png");
        File grayImg = new File("F:\\test22.png");
        BufferedImage image = ImageIO.read(originalImg);
        int width = image.getWidth();
        int height = image.getHeight();

        BufferedImage grayImageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY); // 灰度化
//        BufferedImage grayImageBuffer = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY); // 二值化
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = image.getRGB(i, j);
                grayImageBuffer.setRGB(i, j, rgb);
            }
        }

        ImageIO.write(grayImageBuffer, grayImg.getName().split("\\.")[1], grayImg);
    }


}
