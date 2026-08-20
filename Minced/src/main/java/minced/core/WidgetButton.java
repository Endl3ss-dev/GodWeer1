/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  org.joml.Vector4f
 */
package minced.core;

import java.awt.Color;
import minced.core.ColorUtil;
import minced.core.FontRenderer;
import minced.core.HexColor;
import minced.core.Widget;
import minced.core.WidgetStyle;
import minced.core.Animation;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.enums.MAIN;
import net.minecraft.client.gui.DrawContext;
import org.joml.Vector4f;

public class WidgetButton
extends Widget {
    Runnable field9339;
    int field9340;
    int field9341;
    Integer field9342;
    Integer I;
    String field9343;
    int field9344;
    Integer field9345;
    float field9346;
    boolean field9348;
    int field9349 = 18;
    static float field9350 = 12.0f;
    int field9351;
    static float field9352 = 0.5f;
    float field9353;
    static float field9354 = 8.0f;
    float field9355;
    int field9356;
    static float field9357 = 0.01f;
    static float field9358 = 0.02f;
    static String field9359 = "FFFFFF";
    static float field9360 = 0.02f;
    int field9361;
    float field9362;
    static long field9363 = 150L;
    Animation field9364;
    static long field9365 = 100L;
    Animation l;
    static long field9366 = 150L;
    WidgetStyle field9367;
    static long field9368 = 150L;
    WidgetStyle field9369;
    static long field9370 = 150L;
    WidgetStyle field9371;
    String field9372;
    static float field9373 = 12.0f;
    static float field9374 = 0.5f;
    static float field9375 = 8.0f;
    static float field9376 = 0.01f;
    static float field9377 = 0.02f;
    static String field9378 = "FFFFFF";
    static float field9379 = 0.02f;
    static long field9380 = 150L;
    static long field9381 = 100L;
    static long field9382 = 150L;
    static long field9383 = 150L;
    static long field9384 = 150L;
    static float field9385 = 0.02f;
    static float field9390 = 0.15f;
    static int field9391 = 0xFFFFFF;
    static float field9392 = 0.02f;
    static float field9393 = 0.04f;
    static float field9394 = 0.04f;
    static float field9395 = 255.0f;
    static float field9396;
    static float field9397;
    static float field9398;

    public WidgetButton method2721(Runnable param1) {
        this.field9339 = param1;
        return this;
    }

    public WidgetButton method2722(int param1, int param2) {
        this.field9340 = param1;
        this.field9341 = param2;
        return this;
    }

    public WidgetButton method2723(int param1, int param2) {
        this.field9342 = param1;
        this.I = param2;
        return this;
    }

    public WidgetButton method2724(String param1) {
        this.field9343 = param1;
        return this;
    }

    public WidgetButton method2725(int param1) {
        this.field9344 = param1;
        return this;
    }

    public WidgetButton method2726(int param1) {
        this.field9345 = param1;
        return this;
    }

    public WidgetButton method2727(float param1) {
        this.field9346 = param1;
        return this;
    }

    @Override
    public boolean method2728(double param1, double param3, int param5) {
        if (!this.field9347 || !this.cfr_underscore) {
            return false;
        }
        if (param5 == 0 && this.field9348) {
            this.field9348 = false;
            if (this.method2759(param1, param3) && this.field9339 != null) {
                this.field9339.run();
            }
            return true;
        }
        return false;
    }

    public WidgetButton(float param1, float param2, float param3, float param4, String param5, Runnable param6) {
        super(param1, param2, param3, param4);
        this.field9346 = field9350;
        this.field9351 = Color.WHITE.getRGB();
        this.field9344 = Color.WHITE.getRGB();
        this.field9353 = field9352;
        this.field9355 = field9354;
        this.field9356 = 14;
        this.field9340 = HexColor.method2750(Color.WHITE, field9357);
        this.field9341 = HexColor.method2750(Color.WHITE, field9358);
        this.field9342 = null;
        this.I = null;
        this.field9345 = null;
        this.field9361 = HexColor.method2751(field9359, field9360);
        this.field9362 = 1.0f;
        this.field9364 = new Animation(Easings.field11959, field9363);
        this.l = new Animation(Easings.field11959, field9365);
        this.field9367 = new WidgetStyle(field9366);
        this.field9369 = new WidgetStyle(field9368);
        this.field9371 = new WidgetStyle(field9370);
        this.field9348 = false;
        this.field9343 = param5;
        this.field9339 = param6;
    }

    public WidgetButton method2729(float param1) {
        this.field9353 = param1;
        return this;
    }

    public WidgetButton method2730(String param1) {
        this.field9372 = param1;
        return this;
    }

    private FontRenderer method2731() {
        if (this.field9356 >= 1 && this.field9356 < Fonts.field12486.length) {
            return Fonts.field12486[this.field9356];
        }
        return Fonts.field12486[14];
    }

    public float method2732() {
        return this.field9346;
    }

    private FontRenderer method2733() {
        if (this.field9372 == null) {
            return null;
        }
        if (this.field9349 >= 1 && this.field9349 < Fonts.field12502.length) {
            return Fonts.field12502[this.field9349];
        }
        return Fonts.field12502[18];
    }

    public String method2734() {
        return this.field9372;
    }

    public WidgetButton(float param1, float param2, float param3, float param4, String param5, String param6, Runnable param7) {
        super(param1, param2, param3, param4);
        this.field9346 = field9373;
        this.field9351 = Color.WHITE.getRGB();
        this.field9344 = Color.WHITE.getRGB();
        this.field9353 = field9374;
        this.field9355 = field9375;
        this.field9356 = 14;
        this.field9340 = HexColor.method2750(Color.WHITE, field9376);
        this.field9341 = HexColor.method2750(Color.WHITE, field9377);
        this.field9342 = null;
        this.I = null;
        this.field9345 = null;
        this.field9361 = HexColor.method2751(field9378, field9379);
        this.field9362 = 1.0f;
        this.field9364 = new Animation(Easings.field11959, field9380);
        this.l = new Animation(Easings.field11959, field9381);
        this.field9367 = new WidgetStyle(field9382);
        this.field9369 = new WidgetStyle(field9383);
        this.field9371 = new WidgetStyle(field9384);
        this.field9348 = false;
        this.field9343 = param5;
        this.field9372 = param6;
        this.field9339 = param7;
    }

    public String method2735() {
        return this.field9343;
    }

    @Override
    public boolean method2736(double param1, double param3, int param5) {
        if (!this.field9347 || !this.cfr_underscore) {
            return false;
        }
        if (param5 == 0 && this.method2759(param1, param3)) {
            this.field9348 = true;
            return true;
        }
        return false;
    }

    public WidgetButton method2737(int param1) {
        this.field9349 = param1;
        return this;
    }

    @Override
    public void method2738(DrawContext param1, int param2, int param3, float param4) {
        int v38;
        float v37;
        float v36;
        float v35;
        if (!this.field9347) {
            return;
        }
        boolean v5 = this.method2759(param2, param3) && this.cfr_underscore;
        this.field9364.animate(v5 ? 1.0f : 0.0f);
        this.l.animate(this.field9348 ? 1.0f : 0.0f);
        float v6 = this.field9364.getAnimatedValue();
        float v7 = this.l.getAnimatedValue();
        float v8 = 1.0f - v7 * field9385;
        float v9 = this.field9386 * v8;
        float v10 = this.field9387 * v8;
        float v11 = this.field9388 + (this.field9386 - v9) / 2.0f;
        float v12 = this.field9389 + (this.field9387 - v10) / 2.0f;
        float cfr_ignored_0 = this.field9353 + v6 * field9390;
        RenderUtil.method390(v11, v12, v9, v10, this.field9346, field9391, this.field9353);
        int v14 = ThemeColors.method3199(MAIN.LOGO);
        Color v15 = this.field9342 != null ? new Color(this.field9342, true) : new Color(ColorUtil.method1853(v14, field9392), true);
        Color v16 = this.I != null ? new Color(this.I, true) : new Color(ColorUtil.method1853(v14, field9393), true);
        Color v17 = new Color(this.field9340, true);
        Color v18 = new Color(this.field9341, true);
        this.field9367.method2784(v5 ? v15 : v17);
        this.field9369.method2784(v5 ? v16 : v18);
        int v19 = this.field9367.method2788().getRGB();
        int v20 = this.field9369.method2788().getRGB();
        RenderUtil.method395(v11, v12, v9, v10, new Vector4f(this.field9346, this.field9346, this.field9346, this.field9346), v19, v20, v19, v20);
        Color v21 = this.field9345 != null ? new Color(this.field9345, true) : new Color(ColorUtil.method1853(v14, field9394), true);
        Color v22 = new Color(this.field9361, true);
        this.field9371.method2784(v5 ? v21 : v22);
        int v23 = this.field9371.method2788().getRGB();
        float v24 = (float)this.field9371.method2788().getAlpha() / field9395;
        RenderUtil.method402(v11, v12, v9, v10, this.field9346, this.field9362, v23, v23, v23, v23, v24);
        FontRenderer v25 = this.method2731();
        FontRenderer v26 = this.method2733();
        boolean v27 = this.field9343 != null && !this.field9343.isEmpty();
        boolean v28 = this.field9372 != null && v26 != null;
        float v29 = v27 ? v25.method2719(this.field9343) : 0.0f;
        float v30 = v28 ? v26.method2719(this.field9372) : 0.0f;
        float v31 = v28 && v27 ? this.field9355 : 0.0f;
        float v32 = v30 + v31 + v29;
        float v33 = v11 + (v9 - v32) / 2.0f;
        float v34 = v12 + v10 / 2.0f + 1.0f;
        float f = v35 = this.cfr_underscore ? 1.0f : field9396;
        if (v28) {
            v36 = v33;
            v37 = v34 - v26.method2718() / 2.0f;
            v38 = new Color(this.field9344 >> 16 & 0xFF, this.field9344 >> 8 & 0xFF, this.field9344 & 0xFF, (int)(field9397 * v35)).getRGB();
            v26.method2711(param1, this.field9372, v36, v37, v38);
            v33 += v30 + v31;
        }
        if (v27) {
            v36 = v33;
            v37 = v34 - v25.method2718() / 2.0f;
            v38 = new Color(this.field9351 >> 16 & 0xFF, this.field9351 >> 8 & 0xFF, this.field9351 & 0xFF, (int)(field9398 * v35)).getRGB();
            v25.method2711(param1, this.field9343, v36, v37, v38);
        }
    }

    public WidgetButton method2739(int param1) {
        this.field9356 = param1;
        return this;
    }

    public WidgetButton method2740(float param1) {
        this.field9355 = param1;
        return this;
    }

    public WidgetButton method2741(int param1) {
        this.field9351 = param1;
        return this;
    }

    static {
        field9396 = 0.5f;
        field9397 = 255.0f;
        field9398 = 255.0f;
    }
}




