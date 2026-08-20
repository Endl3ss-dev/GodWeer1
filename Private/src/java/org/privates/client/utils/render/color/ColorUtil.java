package org.privates.client.utils.render.color;

import lombok.experimental.UtilityClass;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Interpolator;
import org.privates.common.impl.fastrandom.FastRandom;

import java.awt.*;
import java.util.concurrent.*;

@SuppressWarnings("unused")
@UtilityClass
public class ColorUtil {
    private final FastRandom RANDOM = new FastRandom();
    private final long CACHE_EXPIRATION_TIME = 60 * 1000;
    private final ConcurrentHashMap<ColorKey, CacheEntry> colorCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService cacheCleaner = Executors.newScheduledThreadPool(1);
    private final DelayQueue<CacheEntry> cleanupQueue = new DelayQueue<>();

    static {
        cacheCleaner.scheduleWithFixedDelay(() -> {
            CacheEntry entry = cleanupQueue.poll();
            while (entry != null) {
                if (entry.isExpired()) {
                    colorCache.remove(entry.key());
                }
                entry = cleanupQueue.poll();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public final int TRANSPARENT = getColor(0, 0, 0, 0);
    public final int RED = getColor(255, 0, 0);
    public final int GREEN = getColor(0, 255, 0);
    public final int BLUE = getColor(0, 0, 255);
    public final int YELLOW = getColor(255, 255, 0);
    public final int WHITE = getColor(255);
    public final int BLACK = getColor(0);
    public final int LIGHT_GRAY = getColor(211, 211, 211);
    public final int GRAY = getColor(128, 128, 128);
    public final int DARK_GRAY = getColor(64, 64, 64);
    public final int CYAN = getColor(0, 255, 255);
    public final int MAGENTA = getColor(255, 0, 255);
    public final int ORANGE = getColor(255, 165, 0);

    public int red(int c) {
        return (c >> 16) & 0xFF;
    }

    public static int rgba(int r, int g, int b, int a) {
        return getColor(r, g, b, a);
    }

    public int green(int c) {
        return (c >> 8) & 0xFF;
    }

    public int blue(int c) {
        return c & 0xFF;
    }

    public int alpha(int c) {
        return (c >> 24) & 0xFF;
    }

    public float redf(int c) {
        return red(c) / 255.0f;
    }

    public float greenf(int c) {
        return green(c) / 255.0f;
    }

    public float bluef(int c) {
        return blue(c) / 255.0f;
    }

    public float alphaf(int c) {
        return alpha(c) / 255.0f;
    }

    public int[] getRGBA(int c) {
        return new int[]{red(c), green(c), blue(c), alpha(c)};
    }

    public int[] getRGB(int c) {
        return new int[]{red(c), green(c), blue(c)};
    }

    public float[] getRGBAf(int c) {
        return new float[]{redf(c), greenf(c), bluef(c), alphaf(c)};
    }

    public float[] getRGBf(int c) {
        return new float[]{redf(c), greenf(c), bluef(c)};
    }

    public int getColor(float red, float green, float blue, float alpha) {
        return getColor(Math.round(red * 255), Math.round(green * 255), Math.round(blue * 255), Math.round(alpha * 255));
    }

    public int getColor(int red, int green, int blue, float alpha) {
        return getColor(red, green, blue, Math.round(alpha * 255));
    }

    public int getColor(float red, float green, float blue) {
        return getColor(red, green, blue, 1.0F);
    }

    public int getColor(int brightness, int alpha) {
        return getColor(brightness, brightness, brightness, alpha);
    }

    public int getColor(int brightness, float alpha) {
        return getColor(brightness, Math.round(alpha * 255));
    }

    public int getColor(int brightness) {
        return getColor(brightness, brightness, brightness);
    }

    public static int HUEtoRGB(int value) {
        float hue = (float) value / 360.0F;
        return Color.HSBtoRGB(hue, 1.0F, 1.0F);
    }

    public int replAlpha(int color, int alpha) {
        return getColor(red(color), green(color), blue(color), alpha);
    }

    public int replAlpha(int color, float alpha) {
        return getColor(red(color), green(color), blue(color), alpha);
    }

    public int multAlpha(int color, float percent01) {
        return getColor(red(color), green(color), blue(color), Math.round(alpha(color) * percent01));
    }

    public static TextFormatting intToTextFormatting(int color) {
        TextFormatting[] formats = {
                TextFormatting.RED, TextFormatting.GREEN, TextFormatting.BLUE,
                TextFormatting.YELLOW, TextFormatting.AQUA, TextFormatting.LIGHT_PURPLE
        };
        return formats[Math.abs(color) % formats.length];
    }

    public int fade() {
        Theme interFace = Theme.getInstance();
        return interFace.clientColor();
    }

    public int setAlpha(int color, int alpha) {
        return getColor(red(color), green(color), blue(color), alpha);
    }

    public int setAlpha(int color, float alpha) {
        return getColor(red(color), green(color), blue(color), Math.round(alpha * 255));
    }

    public int multDark(int color, float percent01) {
        return getColor(
                Math.round(red(color) * percent01),
                Math.round(green(color) * percent01),
                Math.round(blue(color) * percent01),
                alpha(color)
        );
    }

    public static int hslToRgb(float h, float s, float l, float a) {
        float r, g, b;

        if (s == 0) {
            r = g = b = l;
        } else {
            float q = l < 0.5f ? l * (1 + s) : l + s - l * s;
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1f / 3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3f);
        }

        return getColor(Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), Math.round(a * 255));
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0) t += 1;
        if (t > 1) t -= 1;
        if (t < 1f / 6f) return p + (q - p) * 6 * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6;
        return p;
    }

    public int brighter(int color, float factor) {
        int r = red(color);
        int g = green(color);
        int b = blue(color);
        int alpha = alpha(color);

        int i = (int) (1.0 / (1.0 - factor));
        if (r == 0 && g == 0 && b == 0) {
            return getColor(i, i, i, alpha);
        }
        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return getColor(Math.min((int) (r / factor), 255),
                Math.min((int) (g / factor), 255),
                Math.min((int) (b / factor), 255),
                alpha);
    }

    public int darker(int color, float factor) {
        return getColor(Math.max((int) (red(color) * factor), 0),
                Math.max((int) (green(color) * factor), 0),
                Math.max((int) (blue(color) * factor), 0),
                alpha(color));
    }

    public static int getHealthColor(float current, float max) {
        if (max <= 0) max = 1;
        float percentage = Math.min(1.0f, current / max);

        float hue = percentage * 0.333F;

        return Color.HSBtoRGB(hue, 0.8f, 0.9f);
    }

    public int multBright(int color, float percent01) {
        return getColor(
                Math.min(255, Math.round(red(color) / percent01)),
                Math.min(255, Math.round(green(color) / percent01)),
                Math.min(255, Math.round(blue(color) / percent01)),
                alpha(color)
        );
    }

    public int overCol(int color1, int color2, float percent01) {
        final float percent = MathHelper.clamp(percent01, 0F, 1F);
        return getColor(
                Interpolator.lerp(red(color1), red(color2), percent),
                Interpolator.lerp(green(color1), green(color2), percent),
                Interpolator.lerp(blue(color1), blue(color2), percent),
                Interpolator.lerp(alpha(color1), alpha(color2), percent)
        );
    }

    public int overCol(int color1, int color2) {
        return overCol(color1, color2, 0.5f);
    }

    public int[] genGradientForText(int color1, int color2, int length) {
        int[] gradient = new int[length];
        for (int i = 0; i < length; i++) {
            float pc = (float) i / (length - 1);
            gradient[i] = overCol(color1, color2, pc);
        }
        return gradient;
    }

    public static int interpolateColor(int color1, int color2, float factor) {
        factor = MathHelper.clamp(factor, 0f, 1f);
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;
        int a = (int) (a1 + (a2 - a1) * factor);
        int r = (int) (r1 + (r2 - r1) * factor);
        int g = (int) (g1 + (g2 - g1) * factor);
        int b = (int) (b1 + (b2 - b1) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public int randomColor() {
        return Color.HSBtoRGB(RANDOM.nextFloat(), 0.5F + (RANDOM.nextFloat() / 2F), 1F);
    }

    public int rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        float hue = angle / 360f;
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return getColor(red(color), green(color), blue(color), Math.round(opacity * 255));
    }

    public int fade(int speed, int index, int first, int second) {
        int angle = (int) ((System.currentTimeMillis() / speed + index) % 360);
        angle = angle >= 180 ? 360 - angle : angle;
        return overCol(first, second, angle / 180f);
    }

    public static int shiftHue(int rgb, float hueShift) {
        float[] hsb = Color.RGBtoHSB(red(rgb), green(rgb), blue(rgb), null);
        hsb[0] = (hsb[0] + hueShift) % 1.0f;
        if (hsb[0] < 0) hsb[0] += 1.0f;
        int shiftedRGB = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
        return (rgb & 0xFF000000) | (shiftedRGB & 0x00FFFFFF);
    }

    public int fade(int index) {
        Theme theme = Theme.getInstance();
        return fade(theme.getSpeed(), index, theme.clientColor(), theme.darkColor());
    }

    public int getColor(int red, int green, int blue, int alpha) {
        ColorKey key = new ColorKey(red, green, blue, alpha);
        CacheEntry cacheEntry = colorCache.computeIfAbsent(key, k -> {
            CacheEntry newEntry = new CacheEntry(k, computeColor(red, green, blue, alpha), CACHE_EXPIRATION_TIME);
            cleanupQueue.offer(newEntry);
            return newEntry;
        });
        return cacheEntry.color();
    }

    public int getColor(int red, int green, int blue) {
        return getColor(red, green, blue, 255);
    }

    private int computeColor(int red, int green, int blue, int alpha) {
        return ((MathHelper.clamp(alpha, 0, 255) << 24) |
                (MathHelper.clamp(red, 0, 255) << 16) |
                (MathHelper.clamp(green, 0, 255) << 8) |
                MathHelper.clamp(blue, 0, 255));
    }

    public static Color random() {
        return new Color(Color.HSBtoRGB((float) Math.random(), (float) (0.75F + (Math.random() / 4F)), (float) (0.75F + (Math.random() / 4F))));
    }

    private record ColorKey(int red, int green, int blue, int alpha) {
    }

    private record CacheEntry(ColorKey key, int color, long expirationTime) implements Delayed {
        private CacheEntry(ColorKey key, int color, long expirationTime) {
            this.key = key;
            this.color = color;
            this.expirationTime = System.currentTimeMillis() + expirationTime;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long delay = expirationTime - System.currentTimeMillis();
            return unit.convert(delay, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            if (other instanceof CacheEntry) {
                return Long.compare(this.expirationTime, ((CacheEntry) other).expirationTime);
            }
            return 0;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

    }

    public void shutdownCacheCleaner() {
        cacheCleaner.shutdown();
    }

    public int interpolate(int color1, int color2, float factor) {
        factor = MathHelper.clamp(factor, 0f, 1f);
        return getColor(
                Interpolator.lerp(red(color1), red(color2), factor),
                Interpolator.lerp(green(color1), green(color2), factor),
                Interpolator.lerp(blue(color1), blue(color2), factor),
                Interpolator.lerp(alpha(color1), alpha(color2), factor)
        );
    }
}