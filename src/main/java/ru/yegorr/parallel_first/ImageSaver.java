package ru.yegorr.parallel_first;

import ij.*;
import ij.process.*;

import javax.imageio.*;
import java.awt.image.*;
import java.io.*;

/**
 * User: RyazantsevEV<br>
 * Date: 11.10.2021<br>
 * Time: 21:17<br>
 * Класс для сохранения изображения
 */
public class ImageSaver {

    public void saveImage(float[] result, int rowLength, String name) {
        try {
            int[] intResult = new int[result.length];
            for (int i = 0; i < result.length; i++) {
                int value = (int)result[i];
                intResult[i] = value | (value << 8) | (value << 16);
            }
            BufferedImage bi = new BufferedImage(rowLength, result.length / rowLength, BufferedImage.TYPE_INT_RGB);
            WritableRaster raster = bi.getRaster();
            raster.setDataElements(0, 0, rowLength, result.length / rowLength, intResult);
            ImageIO.write(bi, "jpg", new File(name + ".jpg"));
        } catch (IOException ex) {
            System.err.println("saveImage error");
            ex.printStackTrace();
        }
    }
}
