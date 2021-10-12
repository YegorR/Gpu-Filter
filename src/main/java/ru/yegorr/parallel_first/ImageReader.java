package ru.yegorr.parallel_first;

import ij.*;

/**
 * User: RyazantsevEV<br>
 * Date: 06.10.2021<br>
 * Time: 20:44<br>
 * Класс для чтения изображения
 */
public class ImageReader {
    private final float[][] intensity;


    public ImageReader(String filename) throws Exception {
        String path = ImageReader.class.getClassLoader().getResource(filename).getPath();
        ImagePlus image = IJ.openImage(path);
        if (image == null) {
            throw new Exception("ImageReader exception: cannot read image");
        }
        int width = image.getWidth();
        int height = image.getHeight();
        intensity = new float[width][height];
        for (int i = 0; i < width; ++i) {
            for (int j = 0; j < height; ++j) {
                int[] rgb = image.getPixel(i, j);
                intensity[i][j] = (rgb[0] + rgb[1] + rgb[2]) / 3.0f;
            }
        }
    }

    public float[][] getIntensity() {
        return intensity;
    }
}
