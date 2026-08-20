package org.privates.client.screen.hud;

/**
 * Интерфейс для элементов HUD, поддерживающих масштабирование
 */
public interface IScalable {
    /**
     * Включает или выключает масштабирование для элемента
     *
     * @param enabled true - включить масштабирование, false - выключить
     */
    void setScalingEnabled(boolean enabled);

    /**
     * Проверяет, включено ли масштабирование для элемента
     *
     * @return true если масштабирование включено
     */
    boolean isScalingEnabled();

    /**
     * Устанавливает коэффициент масштабирования
     *
     * @param scale коэффициент масштабирования (1.0 = нормальный размер)
     */
    void setScale(float scale);

    /**
     * Получает текущий коэффициент масштабирования
     *
     * @return текущий коэффициент масштабирования
     */
    float getScale();

    /**
     * Применяет масштабирование к матрице рендеринга
     *
     * @param matrixStack матрица для применения масштабирования
     * @param x           координата X центра масштабирования
     * @param y           координата Y центра масштабирования
     */
    default void applyScale(net.mojang.blaze3d.matrix.MatrixStack matrixStack, float x, float y) {
        if (isScalingEnabled() && getScale() != 1.0f) {
            float scale = getScale();
            matrixStack.push();
            matrixStack.translate(x, y, 0);
            matrixStack.scale(scale, scale, scale);
            matrixStack.translate(-x, -y, 0);
        }
    }

    /**
     * Убирает масштабирование с матрицы рендеринга
     *
     * @param matrixStack матрица для отмены масштабирования
     */
    default void removeScale(net.mojang.blaze3d.matrix.MatrixStack matrixStack) {
        if (isScalingEnabled() && getScale() != 1.0f) {
            matrixStack.pop();
        }
    }
}