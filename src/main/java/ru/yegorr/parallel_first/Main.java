package ru.yegorr.parallel_first;

import static ru.yegorr.parallel_first.Calculator.FILTER_WIDTH;

/**
 * User: RyazantsevEV<br>
 * Date: 03.10.2021<br>
 * Time: 22:18<br>
 */
public class Main {

    private static final int ATTEMPTS = 3;

    public static void main(String[] args) {

        ImageReader imageReader1;
        ImageReader imageReader2;
        ImageReader imageReader3;
        try {
            imageReader1 = new ImageReader("1.jpg");
            imageReader2 = new ImageReader("2.jpg");
            imageReader3 = new ImageReader("3.jpg");
        } catch (Exception ex) {
            System.err.println("ImageReader error");
            ex.printStackTrace();
            return;
        }

        Calculator simpleCalculator = new CpuCalculator();
        Calculator gpuCalculator = new GpuCalculator();
        double simpleTime1 = 0, simpleTime2 = 0, simpleTime3 = 0;
        double gpuTime1 = 0, gpuTime2 = 0, gpuTime3 = 0;
        for (int attempt = 0; attempt < ATTEMPTS; ++attempt) {
            simpleTime1 += calculateAndSave(simpleCalculator, imageReader1.getIntensity(), "1.jpg", attempt);
            simpleTime2 += calculateAndSave(simpleCalculator, imageReader2.getIntensity(), "2.jpg", attempt);
            simpleTime3 += calculateAndSave(simpleCalculator, imageReader3.getIntensity(), "3.jpg", attempt);

            gpuTime1 += calculateAndSave(gpuCalculator, imageReader1.getIntensity(), "1.jpg", attempt);
            gpuTime2 += calculateAndSave(gpuCalculator, imageReader2.getIntensity(), "2.jpg", attempt);
            gpuTime3 += calculateAndSave(gpuCalculator, imageReader3.getIntensity(), "3.jpg", attempt);
        }
        simpleCalculator.release();
        gpuCalculator.release();
        System.out.println("------------------------------------------------");

        System.out.println("Simple avg time 1 - " + (simpleTime1 / ATTEMPTS));
        System.out.println("Simple avg time 2 - " + (simpleTime2 / ATTEMPTS));
        System.out.println("Simple avg time 3 - " + (simpleTime3 / ATTEMPTS));

        System.out.println("Gpu avg time 1 - " + (gpuTime1 / ATTEMPTS));
        System.out.println("Gpu avg time 2 - " + (gpuTime2 / ATTEMPTS));
        System.out.println("Gpu avg time 3 - " + (gpuTime3 / ATTEMPTS));

    }

    private static double calculateAndSave(Calculator calculator, float[][] intensity, String filename, int attempt) {
        ResultDto result = calculator.calculate(intensity);
        ImageSaver imageSaver = new ImageSaver();
        imageSaver.saveImage(result.getResult(), intensity.length - FILTER_WIDTH + 1, calculator.getName() + "_" + attempt + "_" + filename);
        System.out.println(calculator.getName() + "_" + attempt + "_" + filename + " - " + result.getTime());
        return result.getTime();
    }
}
