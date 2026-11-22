/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.theme;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class Theme {
    public static final int ACCENT = 0;
    public static final int BACKGROUND = 1;
    public static final int OUTLINE = 2;
    public static final int TEXT = 3;
    public static final int TEXT2 = 4;
    public static final int ELEMENT = 5;
    public static final int SEPARATOR = 6;
    public static final int LIGHT_BG = 7;
    public static final int COLOR_COUNT = 8;
    public static final String[] NAMES = new String[]{"\u0426\u0432\u0435\u0442 \u0432\u0438\u0437\u0443\u0430\u043b\u043e\u0432", "\u0426\u0432\u0435\u0442 \u0444\u043e\u043d\u0430", "\u041e\u0431\u0432\u043e\u0434\u043a\u0430", "\u0421\u0432\u0435\u0442\u043b\u044b\u0439 \u0442\u0435\u043a\u0441\u0442", "\u0422\u0451\u043c\u043d\u044b\u0439 \u0442\u0435\u043a\u0441\u0442", "\u0426\u0432\u0435\u0442 \u0445\u0443\u0434\u0430", "\u0426\u0432\u0435\u0442 \u0440\u0430\u0437\u0434\u0435\u043b\u0438\u0442\u0435\u043b\u0435\u0439", "\u0426\u0432\u0435\u0442 \u0441\u0432\u0435\u0442\u043b\u043e\u0433\u043e \u0444\u043e\u043d\u0430"};
    public static final int OPACITY = 0;
    public static final int BLUR = 1;
    public static final String[] SLIDER_NAMES = new String[]{"\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c", "\u0421\u0438\u043b\u0430 \u0440\u0430\u0437\u043c\u044b\u0442\u0438\u044f"};
    public static final float[] SLIDER_MIN = new float[]{0.1f, 0.0f};
    public static final float[] SLIDER_MAX = new float[]{1.0f, 6.0f};
    public static final float[] SLIDER_DEF = new float[]{1.0f, 0.0f};
    public static final float[] SLIDER_STEP = new float[]{0.0f, 1.0f};
    public static final int SHADOW = 0;
    public static final int DISTORTION = 1;
    public static final String[] BOOL_NAMES = new String[]{"\u042d\u0444\u0444\u0435\u043a\u0442 \u0442\u0435\u043d\u0438", "\u042d\u0444\u0444\u0435\u043a\u0442 \u0438\u0441\u043a\u0430\u0436\u0435\u043d\u0438\u044f"};
    public static final boolean[] BOOL_DEF = new boolean[]{true, false};
    public String name;
    public final int[] colors = new int[8];
    public final float[] sliders = new float[2];
    public final boolean[] booleans = new boolean[BOOL_NAMES.length];
    public boolean builtin = false;

    public static int rgb(int r, int g, int b) {
        return 0xFF000000 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
    }

    public Theme(String name) {
        this.name = name;
        this.colors[0] = -1;
        this.colors[1] = -15856110;
        this.colors[2] = -1;
        this.colors[3] = -1;
        this.colors[4] = -1;
        this.colors[5] = -1;
        this.colors[6] = -1;
        this.colors[7] = -15066592;
        System.arraycopy(SLIDER_DEF, 0, this.sliders, 0, this.sliders.length);
        System.arraycopy(BOOL_DEF, 0, this.booleans, 0, this.booleans.length);
    }

    public int get(int index) {
        return this.colors[index];
    }

    public void set(int index, int argb) {
        this.colors[index] = argb;
    }

    public float getSlider(int index) {
        return Theme.clampSlider(index, this.sliders[index]);
    }

    public void setSlider(int index, float value) {
        this.sliders[index] = Theme.clampSlider(index, value);
    }

    private static float clampSlider(int index, float value) {
        float step = SLIDER_STEP[index];
        if (step > 0.0f) {
            value = (float)Math.round(value / step) * step;
        }
        if (value < SLIDER_MIN[index]) {
            return SLIDER_MIN[index];
        }
        if (value > SLIDER_MAX[index]) {
            return SLIDER_MAX[index];
        }
        return value;
    }

    public boolean getBool(int index) {
        return this.booleans[index];
    }

    public void setBool(int index, boolean value) {
        this.booleans[index] = value;
    }

    public Theme copy(String newName) {
        Theme t = new Theme(newName);
        System.arraycopy(this.colors, 0, t.colors, 0, 8);
        System.arraycopy(this.sliders, 0, t.sliders, 0, this.sliders.length);
        System.arraycopy(this.booleans, 0, t.booleans, 0, this.booleans.length);
        return t;
    }
}

