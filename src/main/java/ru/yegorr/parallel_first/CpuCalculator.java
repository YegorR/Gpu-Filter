package ru.yegorr.parallel_first;

/**
 * User: RyazantsevEV<br>
 * Date: 10.10.2021<br>
 * Time: 16:10<br>
 * Делает обычную фильтрацию
 */
public class CpuCalculator implements Calculator {

    @Override
    public ResultDto calculate(float[][] intensity) {
        long startTime = System.nanoTime();
        int width = (intensity.length - FILTER_WIDTH + 1);
        int height = (intensity[0].length - FILTER_HEIGHT + 1);


        float[] result = new float[width * height];
        int pointer = 0;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                result[pointer++] = calculateCell(intensity, i, j);
            }
        }
        long finishTime = System.nanoTime();

        return new ResultDto(result, (finishTime - startTime) / 1_000_000.0);
    }

    private float calculateCell(float[][] intensity, int x, int y) {
        float result = 0f;
        for (int i = 0; i < FILTER_WIDTH; ++i) {
            for (int j = 0; j < FILTER_HEIGHT; ++j) {
                result += intensity[y + i][x + j] * GAUSS_FILTER[j][i];
            }
        }
        return result;
    }

    @Override
    public void release() {
        // nothing
    }

    @Override
    public String getName() {
        return "processor_calculator";
    }
}
