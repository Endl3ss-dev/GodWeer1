/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.chars.Char2IntArrayMap
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_3532
 *  org.lwjgl.opengl.GL11
 */
package ru.decide.utils.colors;

import it.unimi.dsi.fastutil.chars.Char2IntArrayMap;
import java.awt.Color;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3532;
import org.lwjgl.opengl.GL11;
import ru.decide.manager.Theme;
import ru.decide.module.impl.display.Hud;
import ru.decide.screen.Menu;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.math.Interpolator;

@Environment(value=EnvType.CLIENT)
public final class ColorUtil {
    private static final long CACHE_EXPIRATION_TIME = 60000L;
    private static final ConcurrentHashMap<ColorKey, CacheEntry> colorCache = new ConcurrentHashMap();
    private static final ScheduledExecutorService cacheCleaner = Executors.newScheduledThreadPool(1);
    private static final DelayQueue<CacheEntry> cleanupQueue = new DelayQueue();
    public static Char2IntArrayMap colorCodes = new Char2IntArrayMap(){
        {
            this.put('0', 0);
            this.put('1', 170);
            this.put('2', 43520);
            this.put('3', 43690);
            this.put('4', 0xAA0000);
            this.put('5', 0xAA00AA);
            this.put('6', 0xFFAA00);
            this.put('7', 0xAAAAAA);
            this.put('8', 0x555555);
            this.put('9', 0x5555FF);
            this.put('A', 0x55FF55);
            this.put('B', 0x55FFFF);
            this.put('C', 0xFF5555);
            this.put('D', 0xFF55FF);
            this.put('E', 0xFFFF55);
            this.put('F', 0xFFFFFF);
        }
    };
    public static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9a-f-or]");
    public static final int RED;
    public static final int GREEN;
    public static final int BLUE;
    public static final int YELLOW;
    public static final int WHITE;
    public static final int BLACK;

    public static int[] solid8(int color) {
        return new int[]{color, color, color, color, color, color, color, color};
    }

    public static int interpolateColorsBackAndForth(int speed, int index, int startColor, int endColor, boolean trueColor) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle >= 180 ? 360 - angle : angle) * 2;
        return ColorUtil.interpolateColor(startColor, endColor, (float)angle / 360.0f, trueColor);
    }

    public static int getHealthColor(float health, float maxHealth) {
        float progress = class_3532.method_15363((float)(health / maxHealth), (float)0.0f, (float)1.0f);
        if (progress <= 0.5f) {
            return ColorUtil.interpolateColor2(new Color(255, 0, 0).getRGB(), new Color(255, 255, 0).getRGB(), progress / 0.5f);
        }
        return ColorUtil.interpolateColor2(new Color(255, 255, 0).getRGB(), new Color(0, 255, 0).getRGB(), (progress - 0.5f) / 0.5f);
    }

    public static int client() {
        Theme theme = ColorUtil.getTheme();
        Theme preTheme = ColorUtil.getPreTheme();
        return ColorUtil.interpolate(theme.getClient(), preTheme.getClient(), 1.0f - Menu.animation14.getOutput());
    }

    public static int background() {
        Theme theme = ColorUtil.getTheme();
        Theme preTheme = ColorUtil.getPreTheme();
        return ColorUtil.interpolate(theme.getRect(), preTheme.getRect(), 1.0f - Menu.animation14.getOutput());
    }

    private static Theme getTheme() {
        return Menu.selectedTheme != null ? Menu.selectedTheme : Theme.NIGHT;
    }

    private static Theme getPreTheme() {
        return Menu.preSelectedTheme != null ? Menu.preSelectedTheme : Theme.NIGHT;
    }

    public static int interpolateColor2(int color1, int color2, float amount) {
        amount = class_3532.method_15363((float)amount, (float)0.0f, (float)1.0f);
        Color c1 = new Color(color1, true);
        Color c2 = new Color(color2, true);
        int red = (int)((float)c1.getRed() + (float)(c2.getRed() - c1.getRed()) * amount);
        int green = (int)((float)c1.getGreen() + (float)(c2.getGreen() - c1.getGreen()) * amount);
        int blue = (int)((float)c1.getBlue() + (float)(c2.getBlue() - c1.getBlue()) * amount);
        int alpha = (int)((float)c1.getAlpha() + (float)(c2.getAlpha() - c1.getAlpha()) * amount);
        return new Color(red, green, blue, alpha).getRGB();
    }

    public static int interpolateColor(int color1, int color2, float amount, boolean trueColor) {
        amount = Math.min(1.0f, Math.max(0.0f, amount));
        Color c1 = new Color(color1, true);
        Color c2 = new Color(color2, true);
        return new Color((int)((float)c1.getRed() + (float)(c2.getRed() - c1.getRed()) * amount), (int)((float)c1.getGreen() + (float)(c2.getGreen() - c1.getGreen()) * amount), (int)((float)c1.getBlue() + (float)(c2.getBlue() - c1.getBlue()) * amount), (int)((float)c1.getAlpha() + (float)(c2.getAlpha() - c1.getAlpha()) * amount)).getRGB();
    }

    public static int applyOpacity(int color, float opacity) {
        int alpha = color >> 24 & 0xFF;
        int newAlpha = (int)((float)alpha * opacity);
        newAlpha = Math.max(0, Math.min(255, newAlpha));
        return color & 0xFFFFFF | newAlpha << 24;
    }

    public static int interpolateColor(int color1, int color2, float factor) {
        factor = Math.min(1.0f, Math.max(0.0f, factor));
        int a1 = color1 >> 24 & 0xFF;
        int r1 = color1 >> 16 & 0xFF;
        int g1 = color1 >> 8 & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = color2 >> 24 & 0xFF;
        int r2 = color2 >> 16 & 0xFF;
        int g2 = color2 >> 8 & 0xFF;
        int b2 = color2 & 0xFF;
        int a = (int)((float)a1 + (float)(a2 - a1) * factor);
        int r = (int)((float)r1 + (float)(r2 - r1) * factor);
        int g = (int)((float)g1 + (float)(g2 - g1) * factor);
        int b = (int)((float)b1 + (float)(b2 - b1) * factor);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int[] solid(int color) {
        return new int[]{color, color, color, color, color, color, color, color, color};
    }

    public static String removeFormatting(String text) {
        return text == null || text.isEmpty() ? null : FORMATTING_CODE_PATTERN.matcher(text).replaceAll("");
    }

    public static int multRed(int color, float percent01) {
        return ColorUtil.getColorRaw(ColorUtil.red(color), Math.min(255, Math.round((float)ColorUtil.green(color) / percent01)), Math.min(255, Math.round((float)ColorUtil.blue(color) / percent01)), ColorUtil.alpha(color));
    }

    public static int reAlphaInt(int color, int alpha) {
        return class_3532.method_15340((int)alpha, (int)0, (int)255) << 24 | color & 0xFFFFFF;
    }

    public static int swapAlpha(int color, float alpha) {
        int f = color >> 16 & 0xFF;
        int f1 = color >> 8 & 0xFF;
        int f2 = color & 0xFF;
        return ColorUtil.getColor2(f, f1, f2, (int)alpha);
    }

    public static int getColor2(int red, int green, int blue, int alpha) {
        int color = 0;
        color |= alpha << 24;
        color |= red << 16;
        color |= green << 8;
        return color |= blue;
    }

    public static Color TwoColoreffect(Color color, Color color2, double n) {
        float clamp = class_3532.method_15363((float)((float)Math.sin(Math.PI * 6 * (n / 4.0 % 1.0)) / 2.0f + 0.5f), (float)0.0f, (float)1.0f);
        return new Color(class_3532.method_16439((float)((float)color.getRed() / 255.0f), (float)((float)color2.getRed() / 255.0f), (float)clamp), class_3532.method_16439((float)((float)color.getGreen() / 255.0f), (float)((float)color2.getGreen() / 255.0f), (float)clamp), class_3532.method_16439((float)((float)color.getBlue() / 255.0f), (float)((float)color2.getBlue() / 255.0f), (float)clamp), class_3532.method_16439((float)((float)color.getAlpha() / 255.0f), (float)((float)color2.getAlpha() / 255.0f), (float)clamp));
    }

    public static int interpolateInt(int oldValue, int newValue, double interpolationValue) {
        return ColorUtil.interpolateD(oldValue, newValue, (float)interpolationValue).intValue();
    }

    public static Double interpolateD(double oldValue, double newValue, double interpolationValue) {
        return oldValue + (newValue - oldValue) * interpolationValue;
    }

    public static int glColor(int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f((float)red, (float)green, (float)blue, (float)alpha);
        return color;
    }

    public static int skyRainbow(int speed, int index) {
        double d;
        double angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        return Color.getHSBColor(d / 360.0 < 0.5 ? -((float)(angle / 360.0)) : (float)((angle %= 360.0) / 360.0), 0.5f, 1.0f).hashCode();
    }

    public static float[] normalize(Color color) {
        return new float[]{(float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f};
    }

    public static float[] normalize(int color) {
        int[] components = ColorUtil.unpack(color);
        return new float[]{(float)components[0] / 255.0f, (float)components[1] / 255.0f, (float)components[2] / 255.0f, (float)components[3] / 255.0f};
    }

    public static int[] unpack(int color) {
        return new int[]{color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >> 24 & 0xFF};
    }

    public static int fadeBetween(float speed, int offset, int color1, int color2) {
        long time = System.currentTimeMillis() + (long)offset;
        double factor = (Math.sin((double)time * 0.001 * (double)speed) + 1.0) / 2.0;
        int r1 = color1 >> 16 & 0xFF;
        int g1 = color1 >> 8 & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = color2 >> 16 & 0xFF;
        int g2 = color2 >> 8 & 0xFF;
        int b2 = color2 & 0xFF;
        int r = (int)((double)r1 + (double)(r2 - r1) * factor);
        int g = (int)((double)g1 + (double)(g2 - g1) * factor);
        int b = (int)((double)b1 + (double)(b2 - b1) * factor);
        return r << 16 | g << 8 | b;
    }

    public static int fadeBetween(int from, int to, float fraction) {
        fraction = ColorUtil.clamp01(fraction);
        int a1 = from >> 24 & 0xFF;
        int r1 = from >> 16 & 0xFF;
        int g1 = from >> 8 & 0xFF;
        int b1 = from & 0xFF;
        int a2 = to >> 24 & 0xFF;
        int r2 = to >> 16 & 0xFF;
        int g2 = to >> 8 & 0xFF;
        int b2 = to & 0xFF;
        int a = (int)((float)a1 + (float)(a2 - a1) * fraction);
        int r = (int)((float)r1 + (float)(r2 - r1) * fraction);
        int g = (int)((float)g1 + (float)(g2 - g1) * fraction);
        int b = (int)((float)b1 + (float)(b2 - b1) * fraction);
        return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    private static float clamp01(float v) {
        if (v < 0.0f) {
            return 0.0f;
        }
        if (v > 1.0f) {
            return 1.0f;
        }
        return v;
    }

    public static float[] rgba(int color) {
        return new float[]{(float)(color >> 16 & 0xFF) / 255.0f, (float)(color >> 8 & 0xFF) / 255.0f, (float)(color & 0xFF) / 255.0f, (float)(color >> 24 & 0xFF) / 255.0f};
    }

    public static int[] rgbas(int color) {
        return new int[]{(int)((float)(color >> 16 & 0xFF) / 255.0f), (int)((float)(color >> 8 & 0xFF) / 255.0f), (int)((float)(color & 0xFF) / 255.0f), (int)((float)(color >> 24 & 0xFF) / 255.0f)};
    }

    public static int red(int c) {
        return c >> 16 & 0xFF;
    }

    public static int green(int c) {
        return c >> 8 & 0xFF;
    }

    public static int blue(int c) {
        return c & 0xFF;
    }

    public static int alpha(int c) {
        return c >> 24 & 0xFF;
    }

    public static float redf(int c) {
        return (float)ColorUtil.red(c) / 255.0f;
    }

    public static float greenf(int c) {
        return (float)ColorUtil.green(c) / 255.0f;
    }

    public static float bluef(int c) {
        return (float)ColorUtil.blue(c) / 255.0f;
    }

    public static float alphaf(int c) {
        return (float)ColorUtil.alpha(c) / 255.0f;
    }

    public static int[] getRGBA(int c) {
        return new int[]{ColorUtil.red(c), ColorUtil.green(c), ColorUtil.blue(c), ColorUtil.alpha(c)};
    }

    public static int[] getRGB(int c) {
        return new int[]{ColorUtil.red(c), ColorUtil.green(c), ColorUtil.blue(c)};
    }

    public static float[] getRGBAf(int c) {
        return new float[]{ColorUtil.redf(c), ColorUtil.greenf(c), ColorUtil.bluef(c), ColorUtil.alphaf(c)};
    }

    public static float[] getRGBf(int c) {
        return new float[]{ColorUtil.redf(c), ColorUtil.greenf(c), ColorUtil.bluef(c)};
    }

    public static int getColor(float red, float green, float blue, float alpha) {
        return ColorUtil.getColor(Math.round(red * 255.0f), Math.round(green * 255.0f), Math.round(blue * 255.0f), Math.round(alpha * 255.0f));
    }

    public static int getColor(int red, int green, int blue, float alpha) {
        return ColorUtil.getColor(red, green, blue, Math.round(alpha * 255.0f));
    }

    public static int getColor(float red, float green, float blue) {
        return ColorUtil.getColor(red, green, blue, 1.0f);
    }

    public static int getColor(int brightness, int alpha) {
        return ColorUtil.getColor(brightness, brightness, brightness, alpha);
    }

    public static int getColor(int brightness, float alpha) {
        return ColorUtil.getColor(brightness, Math.round(alpha * 255.0f));
    }

    public static int getColor(int brightness) {
        return ColorUtil.getColor(brightness, brightness, brightness);
    }

    public static int replAlpha(int color, int alpha) {
        return ColorUtil.getColorRaw(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), alpha);
    }

    public static int replAlpha(int color, float alpha) {
        return ColorUtil.getColorRaw(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), Math.round(alpha * 255.0f));
    }

    public static int multAlpha(int color, float percent01) {
        return ColorUtil.getColorRaw(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), Math.round((float)ColorUtil.alpha(color) * percent01));
    }

    public static int toGray(int color, float percent01) {
        int r = ColorUtil.red(color);
        int g = ColorUtil.green(color);
        int b = ColorUtil.blue(color);
        int a = ColorUtil.alpha(color);
        int target = 128;
        r = Math.round((float)r + (float)(target - r) * percent01);
        g = Math.round((float)g + (float)(target - g) * percent01);
        b = Math.round((float)b + (float)(target - b) * percent01);
        float darkFactor = percent01 / 2.0f;
        r = Math.round((float)r * darkFactor);
        g = Math.round((float)g * darkFactor);
        b = Math.round((float)b * darkFactor);
        return ColorUtil.getColorRaw(r, g, b, a);
    }

    public static int multDark(int color, float percent01) {
        return ColorUtil.getColorRaw(Math.round((float)ColorUtil.red(color) * percent01), Math.round((float)ColorUtil.green(color) * percent01), Math.round((float)ColorUtil.blue(color) * percent01), ColorUtil.alpha(color));
    }

    public static int multBright(int color, float percent01) {
        return ColorUtil.getColorRaw(Math.min(255, Math.round((float)ColorUtil.red(color) / percent01)), Math.min(255, Math.round((float)ColorUtil.green(color) / percent01)), Math.min(255, Math.round((float)ColorUtil.blue(color) / percent01)), ColorUtil.alpha(color));
    }

    public static int overCol(int color1, int color2, float percent01) {
        float percent = class_3532.method_15363((float)percent01, (float)0.0f, (float)1.0f);
        return ColorUtil.getColorRaw(Interpolator.lerp(ColorUtil.red(color1), ColorUtil.red(color2), percent), Interpolator.lerp(ColorUtil.green(color1), ColorUtil.green(color2), percent), Interpolator.lerp(ColorUtil.blue(color1), ColorUtil.blue(color2), percent), Interpolator.lerp(ColorUtil.alpha(color1), ColorUtil.alpha(color2), percent));
    }

    public static int overCol(int color1, int color2) {
        return ColorUtil.overCol(color1, color2, 0.5f);
    }

    public static int[] genGradientForText(int color1, int color2, int length) {
        int[] gradient = new int[length];
        for (int i = 0; i < length; ++i) {
            float pc = (float)i / (float)(length - 1);
            gradient[i] = ColorUtil.overCol(color1, color2, pc);
        }
        return gradient;
    }

    public static int interpolate(int color1, int color2, double amount) {
        amount = (float)class_3532.method_15350((double)amount, (double)0.0, (double)1.0);
        return ColorUtil.getColorRaw(Interpolator.lerp(ColorUtil.red(color1), ColorUtil.red(color2), amount), Interpolator.lerp(ColorUtil.green(color1), ColorUtil.green(color2), amount), Interpolator.lerp(ColorUtil.blue(color1), ColorUtil.blue(color2), amount), Interpolator.lerp(ColorUtil.alpha(color1), ColorUtil.alpha(color2), amount));
    }

    public static int rainbow(int speed, int index, float saturation, float brightness, float opacity) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        float hue = (float)angle / 360.0f;
        int color = Color.HSBtoRGB(hue, saturation, brightness);
        return ColorUtil.getColor(ColorUtil.red(color), ColorUtil.green(color), ColorUtil.blue(color), Math.round(opacity * 255.0f));
    }

    public static int fade(int speed, int index, int first, int second) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = angle >= 180 ? 360 - angle : angle;
        return ColorUtil.overCol(first, second, (float)angle / 180.0f);
    }

    public static int fade(int index) {
        return ColorUtil.fade(10, index, ColorUtil.getClientColor1(index), ColorUtil.multBright(ColorUtil.getClientColor1(index), 0.75f));
    }

    public static int getClientColor1(int index) {
        return Hud.getVisualsColor();
    }

    public static int getRectColor(float alpha) {
        return ColorUtil.replAlpha(ThemeColor.getBackgroundColor(), alpha * Hud.getAlpha());
    }

    public static int getClientColor(int index) {
        return Hud.getHudColor();
    }

    public static int gradient(int c1, int c2, int c3, int c4, int c5, int index, int speed) {
        float localRatio;
        int color2;
        int color1;
        double time = (System.currentTimeMillis() + (long)index * 50L) % (long)speed;
        float ratio = (float)(time / (double)speed);
        if (ratio < 0.2f) {
            color1 = c1;
            color2 = c2;
            localRatio = ratio / 0.2f;
        } else if (ratio < 0.4f) {
            color1 = c2;
            color2 = c3;
            localRatio = (ratio - 0.2f) / 0.2f;
        } else if (ratio < 0.6f) {
            color1 = c3;
            color2 = c4;
            localRatio = (ratio - 0.4f) / 0.2f;
        } else if (ratio < 0.8f) {
            color1 = c4;
            color2 = c5;
            localRatio = (ratio - 0.6f) / 0.2f;
        } else {
            color1 = c5;
            color2 = c1;
            localRatio = (ratio - 0.8f) / 0.2f;
        }
        return ColorUtil.getHSBColor(color1, color2, localRatio);
    }

    private static int getHSBColor(int color1, int color2, float ratio) {
        float[] hsb1 = Color.RGBtoHSB(color1 >> 16 & 0xFF, color1 >> 8 & 0xFF, color1 & 0xFF, null);
        float[] hsb2 = Color.RGBtoHSB(color2 >> 16 & 0xFF, color2 >> 8 & 0xFF, color2 & 0xFF, null);
        float smooth = (float)(1.0 - Math.cos((double)ratio * Math.PI)) / 2.0f;
        float h = ColorUtil.interpolateFloat(hsb1[0], hsb2[0], smooth);
        float s = ColorUtil.interpolateFloat(hsb1[1], hsb2[1], smooth);
        float b = ColorUtil.interpolateFloat(hsb1[2], hsb2[2], smooth);
        return Color.HSBtoRGB(h, s, b);
    }

    private static float interpolateFloat(float f1, float f2, float ratio) {
        float res;
        if ((double)Math.abs(f2 - f1) > 0.5) {
            if (f2 > f1) {
                f1 += 1.0f;
            } else {
                f2 += 1.0f;
            }
        }
        return (res = f1 + (f2 - f1) * ratio) > 1.0f ? res - 1.0f : res;
    }

    private static int interpolateChannel(int b1, int b2, float fraction) {
        return (int)((float)b1 + (float)(b2 - b1) * fraction);
    }

    public static int getColorRectMain(float alpha) {
        return ColorUtil.replAlpha(ColorUtil.getClientColor(1), alpha);
    }

    public static int gradient(int first, int second, int third, int index, int speed) {
        float angle = ((float)System.currentTimeMillis() / (float)speed + (float)index) % 360.0f / 360.0f;
        int color = angle < 0.5f ? ColorUtil.interpolate(first, second, angle * 2.0f) : ColorUtil.interpolate(second, third, (angle - 0.5f) * 2.0f);
        float[] hs = ColorUtil.rgba(color);
        float[] hsb = Color.RGBtoHSB((int)(hs[0] * 255.0f), (int)(hs[1] * 255.0f), (int)(hs[2] * 255.0f), null);
        hsb[1] = hsb[1] * 1.5f;
        hsb[1] = Math.min(hsb[1], 1.0f);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static int gradient(int start, int end, int index, int speed) {
        int angle = (int)((System.currentTimeMillis() / (long)speed + (long)index) % 360L);
        angle = (angle > 180 ? 360 - angle : angle) + 180;
        int color = ColorUtil.interpolate(start, end, class_3532.method_15363((float)((float)angle / 180.0f - 1.0f), (float)0.0f, (float)1.0f));
        float[] hs = ColorUtil.rgba(color);
        float[] hsb = Color.RGBtoHSB((int)(hs[0] * 255.0f), (int)(hs[1] * 255.0f), (int)(hs[2] * 255.0f), null);
        hsb[1] = hsb[1] * 1.5f;
        hsb[1] = Math.min(hsb[1], 1.0f);
        return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
    }

    public static int getColor(int red, int green, int blue, int alpha) {
        ColorKey key = new ColorKey(red, green, blue, alpha);
        CacheEntry cacheEntry = colorCache.computeIfAbsent(key, k -> {
            CacheEntry newEntry = new CacheEntry((ColorKey)k, ColorUtil.computeColor(red, green, blue, alpha), 60000L);
            cleanupQueue.offer(newEntry);
            return newEntry;
        });
        return cacheEntry.getColor();
    }

    public static int getColorRaw(int red, int green, int blue, int alpha) {
        return class_3532.method_15340((int)alpha, (int)0, (int)255) << 24 | class_3532.method_15340((int)red, (int)0, (int)255) << 16 | class_3532.method_15340((int)green, (int)0, (int)255) << 8 | class_3532.method_15340((int)blue, (int)0, (int)255);
    }

    public static int getColorRaw(float red, float green, float blue, float alpha) {
        return class_3532.method_15340((int)Math.round(alpha), (int)0, (int)255) << 24 | class_3532.method_15340((int)Math.round(red), (int)0, (int)255) << 16 | class_3532.method_15340((int)Math.round(green), (int)0, (int)255) << 8 | class_3532.method_15340((int)Math.round(blue), (int)0, (int)255);
    }

    public static int getColor(int red, int green, int blue) {
        return ColorUtil.getColor(red, green, blue, 255);
    }

    private static int computeColor(int red, int green, int blue, int alpha) {
        return class_3532.method_15340((int)alpha, (int)0, (int)255) << 24 | class_3532.method_15340((int)red, (int)0, (int)255) << 16 | class_3532.method_15340((int)green, (int)0, (int)255) << 8 | class_3532.method_15340((int)blue, (int)0, (int)255);
    }

    private static String generateKey(int red, int green, int blue, int alpha) {
        return red + "," + green + "," + blue + "," + alpha;
    }

    public static void shutdownCacheCleaner() {
        cacheCleaner.shutdown();
    }

    @Generated
    private ColorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    static {
        cacheCleaner.scheduleWithFixedDelay(() -> {
            CacheEntry entry = (CacheEntry)cleanupQueue.poll();
            while (entry != null) {
                if (entry.isExpired()) {
                    colorCache.remove(entry.getKey());
                }
                entry = (CacheEntry)cleanupQueue.poll();
            }
        }, 0L, 1L, TimeUnit.SECONDS);
        RED = ColorUtil.getColor(255, 0, 0);
        GREEN = ColorUtil.getColor(0, 255, 0);
        BLUE = ColorUtil.getColor(0, 0, 255);
        YELLOW = ColorUtil.getColor(255, 255, 0);
        WHITE = ColorUtil.getColor(255);
        BLACK = ColorUtil.getColor(0);
    }

    @Environment(value=EnvType.CLIENT)
    private static class ColorKey {
        final int red;
        final int green;
        final int blue;
        final int alpha;

        @Generated
        public int getRed() {
            return this.red;
        }

        @Generated
        public int getGreen() {
            return this.green;
        }

        @Generated
        public int getBlue() {
            return this.blue;
        }

        @Generated
        public int getAlpha() {
            return this.alpha;
        }

        @Generated
        public ColorKey(int red, int green, int blue, int alpha) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.alpha = alpha;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof ColorKey)) {
                return false;
            }
            ColorKey other = (ColorKey)o;
            if (!other.canEqual(this)) {
                return false;
            }
            if (this.getRed() != other.getRed()) {
                return false;
            }
            if (this.getGreen() != other.getGreen()) {
                return false;
            }
            if (this.getBlue() != other.getBlue()) {
                return false;
            }
            return this.getAlpha() == other.getAlpha();
        }

        @Generated
        protected boolean canEqual(Object other) {
            return other instanceof ColorKey;
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            result = result * 59 + this.getRed();
            result = result * 59 + this.getGreen();
            result = result * 59 + this.getBlue();
            result = result * 59 + this.getAlpha();
            return result;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static class CacheEntry
    implements Delayed {
        private final ColorKey key;
        private final int color;
        private final long expirationTime;

        CacheEntry(ColorKey key, int color, long ttl) {
            this.key = key;
            this.color = color;
            this.expirationTime = System.currentTimeMillis() + ttl;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long delay = this.expirationTime - System.currentTimeMillis();
            return unit.convert(delay, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            if (other instanceof CacheEntry) {
                return Long.compare(this.expirationTime, ((CacheEntry)other).expirationTime);
            }
            return 0;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > this.expirationTime;
        }

        @Generated
        public ColorKey getKey() {
            return this.key;
        }

        @Generated
        public int getColor() {
            return this.color;
        }

        @Generated
        public long getExpirationTime() {
            return this.expirationTime;
        }
    }
}

