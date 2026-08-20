package fun.volon.api.utils.math;

import net.minecraft.util.math.MathHelper;

/**
 * Лёгкий 1D Perlin-подобный шум для ротаций (без внешних зависимостей).
 */
public final class PerlinNoise {

    private PerlinNoise() {
    }

    public static float noise(float x) {
        int i0 = MathHelper.floor(x);
        int i1 = i0 + 1;
        float f = x - i0;
        float u = f * f * (3.0F - 2.0F * f);
        return MathHelper.lerp(u, grad(i0), grad(i1));
    }

    private static float grad(int hash) {
        int h = hash * 1103515245 + 12345;
        return ((h >> 16) & 0xFF) / 127.5F - 1.0F;
    }
}
