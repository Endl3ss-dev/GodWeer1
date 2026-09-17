package godweer.utils.render.blur;

/**
 * Порт {@code org.privates.client.utils.render.draw.GaussianKernel} из Private.
 * <p>
 * Ядро по-прежнему считается на CPU (для отладки и сверки весов), но в шейдере веса
 * выводятся из сигмы — так не нужен uniform-массив ядра, который в 1.21.4 требует
 * FloatBuffer на каждый проход и рвёт batching.
 */
public final class GaussianKernel {

    private static final float SQRT_TWO_PI = 2.50662827463F;

    private final int size;
    private final float[] kernel;

    public GaussianKernel(int size) {
        this.size = Math.max(1, size);
        this.kernel = new float[this.size];
    }

    public void compute() {
        float sigma = sigmaFor(this.size);
        float kernelSum = 0.0F;

        for (int i = 0; i < this.size; i++) {
            float multiplier = i / sigma;
            this.kernel[i] = 1.0F / (Math.abs(sigma) * SQRT_TWO_PI) * (float) Math.exp(-0.5D * multiplier * multiplier);
            kernelSum += i > 0 ? this.kernel[i] * 2.0F : this.kernel[0];
        }

        for (int i = 0; i < this.size; i++) {
            this.kernel[i] /= kernelSum;
        }
    }

    public int getSize() {
        return this.size;
    }

    public float[] getKernel() {
        return this.kernel;
    }

    public static float sigmaFor(int size) {
        return size / 2.0F;
    }

    /**
     * Сколько тапов нужно, чтобы визуальный радиус размытия совпал с запрошенным.
     * Связь взята из исходного клиента: {@code sigma = size / 2}.
     */
    public static int tapsFor(float radius) {
        if (radius <= 1.0F) {
            return 1;
        }
        return Math.min(MAX_TAPS, Math.round(radius / 2.0F) + 1);
    }

    public static final int MAX_TAPS = 24;
}
