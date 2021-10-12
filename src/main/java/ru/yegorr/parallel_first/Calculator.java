package ru.yegorr.parallel_first;

/**
 * User: RyazantsevEV<br>
 * Date: 10.10.2021<br>
 * Time: 16:06<br>
 * Фильтрует изображение
 */
public interface Calculator {
    float[][] GAUSS_FILTER = {{1.0f/16, 2.0f/16, 1.0f/16}, {2.0f/16, 4.0f/16, 2.0f/16}, {1.0f/16, 2.0f/16, 1.0f/16}};

    int FILTER_WIDTH = 3;

    int FILTER_HEIGHT = 3;

    ResultDto calculate(float[][] intensity);

    void release();

    String getName();
}
