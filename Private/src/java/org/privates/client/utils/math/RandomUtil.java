package org.privates.client.utils.math;

import java.util.Random;

/**
 * Утилита для генерации случайных чисел
 */
public class RandomUtil {
    private static final Random random = new Random();

    /**
     * Генерирует случайное число с плавающей точкой в заданном диапазоне
     *
     * @param min Минимальное значение
     * @param max Максимальное значение
     * @return Случайное число в указанном диапазоне
     */
    public static float nextFloat(float min, float max) {
        return min + random.nextFloat() * (max - min);
    }

    /**
     * Генерирует случайное целое число в заданном диапазоне
     *
     * @param min Минимальное значение
     * @param max Максимальное значение
     * @return Случайное целое число в указанном диапазоне
     */
    public static int nextInt(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }

    /**
     * Генерирует случайное число с плавающей точкой по Гауссовому (нормальному) распределению
     * с заданным средним и стандартным отклонением
     *
     * @param mean   Среднее значение
     * @param stdDev Стандартное отклонение
     * @return Случайное число по Гауссовому распределению
     */
    public static float nextGaussian(float mean, float stdDev) {
        return (float) (mean + random.nextGaussian() * stdDev);
    }

    /**
     * Возвращает случайное булево значение с заданной вероятностью true
     *
     * @param probability Вероятность выпадения true (от 0.0 до 1.0)
     * @return Случайное булево значение
     */
    public static boolean chance(float probability) {
        return random.nextFloat() < probability;
    }
} 