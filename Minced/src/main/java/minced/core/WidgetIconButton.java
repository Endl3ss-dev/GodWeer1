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

public class WidgetIconButton
extends Widget
{
    boolean field9429 = false;
    Runnable field9430;
    int field9431 = 14;
    Animation field9432;
    float field9433;
    static int field9434;
    float field9435;
    static float field9436;
    static float field9437;
    int field9438;
    int field9439;
    WidgetStyle field9440;
    WidgetStyle field9441;
    static float field9442;
    int field9443;
    WidgetStyle field9444;
    static float field9445;
    float field9446 = 1.0f;
    String field9447;
    static float field9448;
    int field9449;
    static float field9450;
    float field9451 = field9455;
    static float field9452;
    float field9453 = 1.0f;
    WidgetStyle field9454;
    static float field9455;
    static int field9456;
    static float field9457;
    static float field9458;
    static float field9459;
    static float field9460;
    static String field9461;
    static float field9462;
    static long field9463;
    static long field9464;
    static long field9465;
    static long field9466;
    static long field9467;

    @Override
    public boolean method2728(double param1, double param3, int param5) {
        if (!this.field9347 || !this.cfr_underscore) {
            return false;
        }
        if (param5 == 0 && this.field9429) {
            this.field9429 = false;
            if (this.method2759(param1, param3) && this.field9430 != null) {
                this.field9430.run();
            }
            return true;
        }
        return false;
    }

    public WidgetIconButton method2768(int param1) {
        this.field9431 = param1;
        return this;
    }

    @Override
    public void method2738(DrawContext param1, int param2, int param3, float param4) {
        if (!this.field9347) {
            return;
        }
        boolean v5 = this.method2759(param2, param3) && this.cfr_underscore;
        this.field9432.animate(v5 ? 1.0f : 0.0f);
        this.field9432.getAnimatedValue();
        RenderUtil.method390(this.field9388, this.field9389, this.field9386, this.field9387, this.field9433, field9434, this.field9435);
        int v7 = ThemeColors.method3199(MAIN.LOGO);
        Color v8 = new Color(ColorUtil.method1853(v7, field9436), true);
        Color v9 = new Color(ColorUtil.method1853(v7, field9437), true);
        Color v10 = new Color(this.field9438, true);
        Color v11 = new Color(this.field9439, true);
        this.field9440.method2784(v5 ? v8 : v10);
        this.field9441.method2784(v5 ? v9 : v11);
        int v12 = this.field9440.method2788().getRGB();
        int v13 = this.field9441.method2788().getRGB();
        RenderUtil.method395(this.field9388, this.field9389, this.field9386, this.field9387, new Vector4f(this.field9433, this.field9433, this.field9433, this.field9433), v12, v13, v12, v13);
        Color v14 = new Color(ColorUtil.method1853(v7, field9442), true);
        Color v15 = new Color(this.field9443, true);
        this.field9444.method2784(v5 ? v14 : v15);
        int v16 = this.field9444.method2788().getRGB();
        float v17 = (float)this.field9444.method2788().getAlpha() / field9445;
        RenderUtil.method402(this.field9388, this.field9389, this.field9386, this.field9387, this.field9433, this.field9446, v16, v16, v16, v16, v17);
        FontRenderer v18 = this.method2769();
        if (v18 == null || this.field9447 == null) {
            return;
        }
        float v19 = v18.method2719(this.field9447);
        float v20 = v18.method2718();
        float v21 = this.field9388 + (this.field9386 - v19) / 2.0f;
        float v22 = this.field9389 + (this.field9387 - v20) / 2.0f + 1.0f;
        float v23 = this.cfr_underscore ? 1.0f : field9448;
        Color v24 = new Color(this.field9449 >> 16 & 0xFF, this.field9449 >> 8 & 0xFF, this.field9449 & 0xFF, (int)(field9450 * this.field9451 * v23));
        Color v25 = new Color(this.field9449 >> 16 & 0xFF, this.field9449 >> 8 & 0xFF, this.field9449 & 0xFF, (int)(field9452 * this.field9453 * v23));
        this.field9454.method2784(v5 ? v25 : v24);
        v18.method2711(param1, this.field9447, v21, v22, this.field9454.method2788().getRGB());
    }

    private FontRenderer method2769() {
        if (this.field9431 >= 1 && this.field9431 < Fonts.field12502.length) {
            return Fonts.field12502[this.field9431];
        }
        return Fonts.field12502[14];
    }

    @Override
    public boolean method2736(double param1, double param3, int param5) {
        if (!this.field9347 || !this.cfr_underscore) {
            return false;
        }
        if (param5 == 0 && this.method2759(param1, param3)) {
            this.field9429 = true;
            return true;
        }
        return false;
    }

    public WidgetIconButton method2770(float param1) {
        this.field9433 = param1;
        return this;
    }

    public WidgetIconButton(float param1, float param2, float param3, float param4, String param5, Runnable param6) {
        super(param1, param2, param3, param4);
        this.field9449 = Color.WHITE.getRGB() & field9456;
        this.field9433 = field9457;
        this.field9435 = field9458;
        this.field9438 = HexColor.method2750(Color.WHITE, field9459);
        this.field9439 = HexColor.method2750(Color.WHITE, field9460);
        this.field9443 = HexColor.method2751(field9461, field9462);
        this.field9432 = new Animation(Easings.field11959, field9463);
        this.field9440 = new WidgetStyle(field9464);
        this.field9441 = new WidgetStyle(field9465);
        this.field9444 = new WidgetStyle(field9466);
        this.field9454 = new WidgetStyle(field9467);
        this.field9447 = param5;
        this.field9430 = param6;
    }

    static {
        field9455 = 0.5f;
        field9456 = 0xFFFFFF;
        field9457 = 5.0f;
        field9458 = 0.5f;
        field9459 = 0.01f;
        field9460 = 0.02f;
        field9461 = "FFFFFF";
        field9462 = 0.02f;
        field9463 = 150L;
        field9464 = 150L;
        field9465 = 150L;
        field9466 = 150L;
        field9467 = 150L;
        field9434 = 0xFFFFFF;
        field9436 = 0.02f;
        field9437 = 0.04f;
        field9442 = 0.04f;
        field9445 = 255.0f;
        field9448 = 0.5f;
        field9450 = 255.0f;
        field9452 = 255.0f;
    }
}




