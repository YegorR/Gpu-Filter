package ru.yegorr.parallel_first;

/**
 * User: RyazantsevEV<br>
 * Date: 10.10.2021<br>
 * Time: 16:06<br>
 * DTO с результатом вычисления и временем работы
 */
public class ResultDto {
    private final float[] result;

    private final double time;

    public ResultDto(float[] result, double time) {
        this.result = result;
        this.time = time;
    }

    public float[] getResult() {
        return result;
    }

    public double getTime() {
        return time;
    }

}
