/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 *  org.joml.Vector4f
 *  sLM.4ZXYEo
 */
package minced.core;

import java.awt.Color;
import minced.core.ColorUtil;
import minced.core.ColorSetting;
import minced.core.MathUtil;
import minced.core.ColorPickerRenderer;
import minced.core.ThemeColors;
import minced.core.AnimatedFloat;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.enums.MAIN;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Vector4f;
import sLM._4ZXYEo;

public class ColorPickerElement
implements ColorPickerRenderer {
    static String cfr_renamed_284;

    float field11456;
    static ColorPickerElement field11457;
    ColorSetting field11458;
    static float field11459;
    static float field11460;
    static float field11461;
    static float field11462;
    static float field11463;
    float field11464;
    static float field11465;
    static float field11466;
    static float field11467;
    static float field11468;
    static float field11469;
    boolean field11470 = false;
    static float field11471;
    static float field11472;
    static float field11473;
    static float field11474;
    static float field11475;
    boolean field11476 = false;
    static float field11477;
    static float field11478;
    static float field11479;
    static float field11480;
    static float field11481;
    static float field11482;
    boolean field11483 = false;
    static float field11484;
    boolean field11485 = false;
    float field11486;
    boolean field11487 = false;
    static float field11488;
    AnimatedFloat field11489 = new AnimatedFloat(0.0f, field11488);
    float field11490;
    static float field11491;
    float field11492;
    float field11493;
    static int field11494;
    static float field11495;
    static float field11496;
    static float field11497;
    static float field11498;
    static float field11499;
    static float field11500;
    static float field11501;
    static float field11502;
    static float field11503;
    static float field11504;
    static String field11505;
    static float field11506;
    static float field11507;
    static float field11508;
    static float field11509;
    static float field11510;
    static float field11511;
    static float field11512;
    static float field11513;
    static float field11514;
    static float field11515;
    static float field11516;
    static float field11517;
    static float field11518;
    static float field11519;
    static float field11520;
    static float field11521;
    static float field11522;
    static float field11523;
    static float field11524;
    static float field11525;
    static float field11526;
    static String field11527;
    static String field11528;
    static float field11529;
    static float field11530;
    static float field11531;
    static float field11532;
    static float field11533;
    static float field11534;
    static float field11535;
    static float field11536;
    static float field11537;
    static float field11538;
    static float field11539;
    static String field11540;
    static String field11541;
    static float field11542;
    static float field11543;
    static float field11544;
    static float field11545;
    static float field11546;
    static float field11547;
    static float field11548;
    static float field11549;
    static float field11550;
    static float field11551;
    static float field11552;
    static float field11553;
    static float field11554;
    static int field11555;
    static int field11556;
    static float field11557;
    static float field11558;
    static float field11559;
    static float field11560;

    public float method3218() {
        return this.field11456;
    }

    public static void method3219() {
        if (field11457 != null) {
            field11457.method3244(false);
        }
    }

    public boolean method3220(float param1, float param2) {
        boolean v3 = this.field11458.field7599;
        float v4 = v3 ? 0.0f : field11459;
        float v5 = field11460 + v4;
        float v6 = field11461;
        float v7 = field11462;
        float v8 = this.field11456 + v5 - field11463 - v7 / 2.0f;
        float v9 = this.field11464 + field11465 - v7 / 2.0f;
        if (MathUtil.cfr_underscore(param1, param2, v8, v9, v7, v7)) {
            this.method3244(false);
            return true;
        }
        float v10 = this.field11456 + field11466;
        float v11 = this.field11464 + field11467;
        float v12 = field11468;
        float v13 = field11469;
        if (MathUtil.cfr_underscore(param1, param2, v10, v11, v12, v13)) {
            this.field11470 = true;
            this.method3251(param1, param2);
            return true;
        }
        float v14 = this.field11456 + field11471;
        float v15 = this.field11464 + field11472 + field11473;
        float v16 = field11474;
        float v17 = field11475;
        if (MathUtil.cfr_underscore(param1, param2, v14, v15, v16, v17)) {
            this.field11476 = true;
            this.cfr_renamed_4(param1);
            return true;
        }
        float v18 = this.field11456 + field11477;
        float v19 = this.field11464 + field11478 + field11479 + field11480;
        float v20 = field11481;
        float v21 = field11482;
        if (v3 && MathUtil.cfr_underscore(param1, param2, v18, v19, v20, v21)) {
            this.field11483 = true;
            this.method3227(param1);
            return true;
        }
        return MathUtil.cfr_underscore(param1, param2, this.field11456 + field11484, this.field11464, v5, v6);
    }

    public String toString() {
        return _4ZXYEo.ЦЦ("makeConcatWithConstants", new Object[]{}, (boolean)this.method3233(), (boolean)this.method3231(), (float)this.method3218(), (float)this.method3228(), (String)String.valueOf(this.method3243()), (float)this.method3239(), (float)this.method3235(), (float)this.method3222(), (float)this.method3226(), (boolean)this.method3250(), (boolean)this.method3232(), (boolean)this.method3234(), (String)String.valueOf(this.method3230()));
    }

    public void method3221(boolean param1) {
        this.field11485 = param1;
    }

    public float method3222() {
        return this.field11486;
    }

    public ColorPickerElement(ColorSetting param1) {
        this.field11458 = param1;
        this.method3246();
    }

    public static void method3223() {
        field11457 = null;
    }

    private void cfr_renamed_282() {
        int v1 = (int)(this.field11490 * field11491);
        if (!this.field11458.field7599) {
            v1 = 255;
        }
        int v2 = Color.HSBtoRGB(this.field11492, this.field11493, this.field11486);
        this.field11458.setValue(v1 << 24 | v2 & field11494);
    }

    public void method3224(float param1) {
        this.field11492 = param1;
    }

    public void method3225(DrawContext param1, float param2, float param3, float param4) {
        float v31;
        float v30;
        MatrixStack v28;
        float v27;
        float v26;
        this.field11489.setTarget(this.field11487 ? 1.0f : 0.0f);
        float v5 = this.field11489.getValue();
        if (v5 <= field11495) {
            return;
        }
        float v6 = param4 * v5;
        boolean v7 = this.field11458.field7599;
        float v8 = v7 ? 0.0f : field11496;
        float v9 = field11497 + v8;
        float v10 = field11498;
        int v11 = ThemeColors.method3199(MAIN.HEADER);
        int v12 = ThemeColors.method3199(MAIN.OUTLINE);
        int v13 = (int)(ThemeColors.method3214(MAIN.OUTLINE) * v6);
        RenderUtil.method390(this.field11456, this.field11464, v9, v10, field11499, v11, v6);
        RenderUtil.method392(this.field11456, this.field11464, v9, v10, field11500, field11501, v12, v13);
        int v14 = ColorUtil.method1853(ThemeColors.method3199(MAIN.HEADER_TEXT), v6);
        Fonts.I[13].method2706(param1, this.field11458.getName(), this.field11456 + field11502, this.field11464 + field11503, v14, field11504);
        Fonts.field12504[16].method2711(param1, field11505, this.field11456 + v9 - field11506, this.field11464 + field11507, v14);
        float v15 = this.field11456 + field11508;
        float v16 = this.field11464 + field11509;
        float v17 = field11510;
        float v18 = field11511;
        RenderUtil.method399(v15, v16, v17, v18, new Vector4f(field11512, field11513, field11514, field11515), ColorUtil.method1852(255, 255, 255, 255), Color.BLACK.getRGB(), new Color(Color.HSBtoRGB(this.field11492, 1.0f, 1.0f)).getRGB(), Color.BLACK.getRGB(), v6);
        float v19 = field11516;
        float v20 = v15 + this.field11493 * v17;
        float v21 = v16 + (1.0f - this.field11486) * v18;
        v20 = Math.max(v15, Math.min(v20, v15 + v17 - v19));
        v21 = Math.max(v16, Math.min(v21, v16 + v18 - v19));
        RenderUtil.method392(v20, v21, v19, v19, 1.0f, 1.0f, -1, (int)(field11517 * v6));
        float v22 = this.field11456 + field11518;
        float v23 = this.field11464 + field11519 + field11520;
        float v24 = this.field11456 + field11521;
        float v25 = this.field11464 + field11522 + field11523 + field11524;
        if (v7) {
            v26 = field11525;
            v27 = field11526;
            v28 = param1.getMatrices();
            v28.push();
            RenderUtil.method388(v28, Identifier.of((String)field11527, (String)field11528), v24, v25, v26, v27, new Color(255, 255, 255, (int)(field11529 * v6)));
            int v29 = Color.HSBtoRGB(this.field11492, this.field11493, this.field11486);
            RenderUtil.method399(v24 - field11530, v25 - field11531, v26 + 1.0f, v27 + 1.0f, new Vector4f(2.0f, 2.0f, 2.0f, 2.0f), new Color(255, 255, 255, 0).getRGB(), new Color(255, 255, 255, 0).getRGB(), v29, v29, v6);
            v28.pop();
            v30 = field11532;
            v31 = v24 + this.field11490 * v26 - v30 / 2.0f;
            float v32 = v25 + v27 / 2.0f - v30 / 2.0f;
            v31 = Math.max(v24 - v30 / 2.0f + 2.0f, Math.min(v31, v24 + v26 - v30 / 2.0f) - 2.0f);
            int v33 = ColorUtil.method1853(-1, v6);
            RenderUtil.method397(v31, v32, v30, v30, v30 / field11533, v33);
            RenderUtil.method392(v31 - field11534, v32 - field11535, v30 + 1.0f, v30 + 1.0f, v30 / 2.0f, field11536, ColorUtil.method1858(80, 80, 80), (int)(field11537 * v6));
        }
        this.method3241(param2, param3);
        v26 = field11538;
        v27 = field11539;
        v28 = param1.getMatrices();
        v28.push();
        RenderUtil.method388(v28, Identifier.of((String)field11540, (String)field11541), v22, v23, v26, v27, new Color(255, 255, 255, (int)(field11542 * v6)));
        v28.pop();
        float v29 = field11543;
        v30 = v22 + this.field11492 * v26 - v29 / 2.0f;
        v31 = v23 + v27 / 2.0f - v29 / 2.0f;
        v30 = Math.max(v22 - v29 / 2.0f + 2.0f, Math.min(v30, v22 + v26 - v29 / 2.0f - 2.0f));
        int v32 = ColorUtil.method1853(-1, v6);
        RenderUtil.method397(v30, v31, v29, v29, v29 / field11544, v32);
        RenderUtil.method392(v30 - field11545, v31 - field11546, v29 + 1.0f, v29 + 1.0f, v29 / 2.0f, field11547, ColorUtil.method1858(80, 80, 80), (int)(field11548 * v6));
    }

    private void cfr_renamed_4(float param1) {
        float v2 = this.field11456 + field11549;
        float v3 = field11550;
        float v4 = Math.max(v2, Math.min(param1, v2 + v3));
        this.field11492 = (v4 - v2) / v3;
        this.field11492 = Math.max(0.0f, Math.min(1.0f, this.field11492));
        this.cfr_renamed_282();
    }

    public float method3226() {
        return this.field11490;
    }

    private void method3227(float param1) {
        if (!this.field11458.field7599) {
            return;
        }
        float v2 = this.field11456 + field11551;
        float v3 = field11552;
        float v4 = Math.max(v2, Math.min(param1, v2 + v3));
        this.field11490 = (v4 - v2) / v3;
        this.field11490 = Math.max(0.0f, Math.min(1.0f, this.field11490));
        this.cfr_renamed_282();
    }

    public float method3228() {
        return this.field11464;
    }

    public static boolean method3229() {
        return field11457 != null && ColorPickerElement.field11457.field11487;
    }

    public AnimatedFloat method3230() {
        return this.field11489;
    }

    public boolean method3231() {
        return this.field11485;
    }

    public boolean method3232() {
        return this.field11476;
    }

    public boolean method3233() {
        return this.field11487;
    }

    public boolean method3234() {
        return this.field11483;
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof ColorPickerElement)) {
            return false;
        }
        ColorPickerElement v2 = (ColorPickerElement)param1;
        if (!v2.method3249(this)) {
            return false;
        }
        if (this.method3233() != v2.method3233()) {
            return false;
        }
        if (this.method3231() != v2.method3231()) {
            return false;
        }
        if (Float.compare(this.method3218(), v2.method3218()) != 0) {
            return false;
        }
        if (Float.compare(this.method3228(), v2.method3228()) != 0) {
            return false;
        }
        if (Float.compare(this.method3239(), v2.method3239()) != 0) {
            return false;
        }
        if (Float.compare(this.method3235(), v2.method3235()) != 0) {
            return false;
        }
        if (Float.compare(this.method3222(), v2.method3222()) != 0) {
            return false;
        }
        if (Float.compare(this.method3226(), v2.method3226()) != 0) {
            return false;
        }
        if (this.method3250() != v2.method3250()) {
            return false;
        }
        if (this.method3232() != v2.method3232()) {
            return false;
        }
        if (this.method3234() != v2.method3234()) {
            return false;
        }
        ColorSetting v3 = this.method3243();
        ColorSetting v4 = v2.method3243();
        if (v3 == null ? v4 != null : !v3.equals(v4)) {
            return false;
        }
        AnimatedFloat v5 = this.method3230();
        AnimatedFloat v6 = v2.method3230();
        return !(v5 == null ? v6 != null : !v5.equals(v6));
    }

    public float method3235() {
        return this.field11493;
    }

    public static void method3236() {
        if (field11457 != null) {
            ColorPickerElement.field11457.field11487 = false;
            ColorPickerElement.field11457.field11485 = false;
            ColorPickerElement.field11457.field11470 = false;
            ColorPickerElement.field11457.field11476 = false;
            ColorPickerElement.field11457.field11483 = false;
            ColorPickerElement.field11457.field11489.setValue(0.0f);
            field11457 = null;
        }
    }

    public void method3237(boolean param1) {
        this.field11483 = param1;
    }

    public boolean cfr_renamed_283() {
        return this.field11487 || this.field11489.getValue() > field11553;
    }

    public void method3238(float param1) {
        this.field11456 = param1;
    }

    public float method3239() {
        return this.field11492;
    }

    public void method3240() {
        this.field11470 = false;
        this.field11476 = false;
        this.field11483 = false;
        this.cfr_renamed_282();
    }

    private void method3241(float param1, float param2) {
        if (this.field11470) {
            this.method3251(param1, param2);
        }
        if (this.field11476) {
            this.cfr_renamed_4(param1);
        }
        if (this.field11483) {
            this.method3227(param1);
        }
    }

    public void method3242(float param1) {
        this.field11486 = param1;
    }

    public ColorSetting method3243() {
        return this.field11458;
    }

    public void method3244(boolean param1) {
        if (param1 && field11457 != this) {
            if (field11457 != null) {
                ColorPickerElement.field11457.field11487 = false;
            }
            field11457 = this;
            this.method3246();
        } else if (!param1 && field11457 == this && field11457 == this) {
            field11457 = null;
        }
        this.field11487 = param1;
        if (param1) {
            this.field11470 = false;
            this.field11476 = false;
            this.field11483 = false;
        }
    }

    public void method3245(boolean param1) {
        this.field11470 = param1;
    }

    public void method3246() {
        Color v1 = new Color((Integer)this.field11458.getValue(), true);
        float[] v2 = Color.RGBtoHSB(v1.getRed(), v1.getGreen(), v1.getBlue(), null);
        this.field11492 = v2[0];
        this.field11493 = v2[1];
        this.field11486 = v2[2];
        if (this.field11458.field7599) {
            this.field11490 = (float)v1.getAlpha() / field11554;
        } else {
            this.field11490 = 1.0f;
            int v3 = (Integer)this.field11458.getValue() & field11555;
            this.field11458.setValue(field11556 | v3);
        }
    }

    public void method3247(boolean param1) {
        this.field11476 = param1;
    }

    public void method3248(float param1) {
        this.field11493 = param1;
    }

    protected boolean method3249(Object param1) {
        return param1 instanceof ColorPickerElement;
    }

    public boolean method3250() {
        return this.field11470;
    }

    private void method3251(float param1, float param2) {
        float v3 = this.field11456 + field11557;
        float v4 = this.field11464 + field11558;
        float v5 = field11559;
        float v6 = field11560;
        float v7 = Math.max(v3, Math.min(param1, v3 + v5));
        float v8 = Math.max(v4, Math.min(param2, v4 + v6));
        this.field11493 = (v7 - v3) / v5;
        this.field11486 = 1.0f - (v8 - v4) / v6;
        this.field11493 = Math.max(0.0f, Math.min(1.0f, this.field11493));
        this.field11486 = Math.max(0.0f, Math.min(1.0f, this.field11486));
        this.cfr_renamed_282();
    }

    public int hashCode() {
        int v2 = 59 + (this.method3233() ? 79 : 97);
        v2 = v2 * 59 + (this.method3231() ? 79 : 97);
        v2 = v2 * 59 + Float.floatToIntBits(this.method3218());
        v2 = v2 * 59 + Float.floatToIntBits(this.method3228());
        v2 = v2 * 59 + Float.floatToIntBits(this.method3239());
        v2 = v2 * 59 + Float.floatToIntBits(this.method3235());
        v2 = v2 * 59 + Float.floatToIntBits(this.method3222());
        v2 = v2 * 59 + Float.floatToIntBits(this.method3226());
        v2 = v2 * 59 + (this.method3250() ? 79 : 97);
        v2 = v2 * 59 + (this.method3232() ? 79 : 97);
        v2 = v2 * 59 + (this.method3234() ? 79 : 97);
        ColorSetting v3 = this.method3243();
        v2 = v2 * 59 + (v3 == null ? 43 : v3.hashCode());
        AnimatedFloat v4 = this.method3230();
        v2 = v2 * 59 + (v4 == null ? 43 : v4.hashCode());
        return v2;
    }

    public void method3252(float param1) {
        this.field11464 = param1;
    }

    public void method3253(float param1) {
        this.field11490 = param1;
    }

    static {
        field11488 = 12.0f;
        field11553 = 0.01f;
        field11495 = 0.01f;
        field11496 = -3.5f;
        field11497 = 101.5f;
        field11498 = 92.5f;
        field11499 = 3.0f;
        field11500 = 3.0f;
        field11501 = 0.6f;
        field11502 = 5.0f;
        field11503 = 7.5f;
        field11504 = 57.0f;
        field11505 = "h";
        field11506 = 12.5f;
        field11507 = 7.5f;
        field11508 = 5.0f;
        field11509 = 17.5f;
        field11510 = 91.0f;
        field11511 = 54.0f;
        field11512 = 3.0f;
        field11513 = 3.0f;
        field11514 = 3.0f;
        field11515 = 3.0f;
        field11516 = 4.0f;
        field11517 = 255.0f;
        field11518 = 5.0f;
        field11519 = 20.0f;
        field11520 = 54.0f;
        field11521 = 5.0f;
        field11522 = 20.0f;
        field11523 = 54.0f;
        field11524 = 8.0f;
        field11525 = 91.0f;
        field11526 = 5.0f;
        field11527 = "minced";
        field11528 = "icons/colorpicker/opacityy.png";
        field11529 = 255.0f;
        field11530 = 0.5f;
        field11531 = 0.5f;
        field11532 = 4.5f;
        field11533 = 2.5f;
        field11534 = 0.5f;
        field11535 = 0.5f;
        field11536 = 0.8f;
        field11537 = 255.0f;
        field11538 = 91.0f;
        field11539 = 5.0f;
        field11540 = "minced";
        field11541 = "icons/colorpicker/slider.png";
        field11542 = 255.0f;
        field11543 = 4.5f;
        field11544 = 2.5f;
        field11545 = 0.5f;
        field11546 = 0.5f;
        field11547 = 0.8f;
        field11548 = 255.0f;
        field11459 = -3.5f;
        field11460 = 101.5f;
        field11461 = 92.5f;
        field11462 = 16.0f;
        field11463 = 12.5f;
        field11465 = 7.5f;
        field11466 = 5.0f;
        field11467 = 17.5f;
        field11468 = 91.0f;
        field11469 = 54.0f;
        field11471 = 5.0f;
        field11472 = 20.0f;
        field11473 = 54.0f;
        field11474 = 91.0f;
        field11475 = 5.0f;
        field11477 = 5.0f;
        field11478 = 20.0f;
        field11479 = 54.0f;
        field11480 = 8.0f;
        field11481 = 91.0f;
        field11482 = 5.0f;
        field11484 = 3.5f;
        field11557 = 5.0f;
        field11558 = 20.0f;
        field11559 = 91.0f;
        field11560 = 54.0f;
        field11549 = 5.0f;
        field11550 = 91.0f;
        field11551 = 5.0f;
        field11552 = 91.0f;
        field11554 = 255.0f;
        field11555 = 0xFFFFFF;
        field11556 = -16777216;
        field11491 = 255.0f;
        field11494 = 0xFFFFFF;
        cfr_renamed_284 = "ColorPickerElement(colorPickMode=\u0001, eyeDropperActive=\u0001, x=\u0001, y=\u0001, setting=\u0001, hue=\u0001, saturation=\u0001, brightness=\u0001, alpha=\u0001, isDraggingPicker=\u0001, isDraggingHue=\u0001, isDraggingAlpha=\u0001, alphaAnimation=\u0001)";
        ColorPickerElement.method3223();
    }
}



