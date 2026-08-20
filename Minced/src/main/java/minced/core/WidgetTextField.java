/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  sLM.4ZXYEo
 */
package minced.core;

import java.awt.Color;
import minced.core.ColorUtil;
import minced.core.FontRenderer;
import minced.core.HexColor;
import minced.core.Widget;
import minced.core.Animation;
import minced.core.Easings;
import minced.core.Fonts;
import minced.core.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import sLM._4ZXYEo;

public class WidgetTextField
extends Widget {
    static String cfr_renamed_239;

    boolean field9468 = false;
    String field9469 = "";
    static MinecraftClient I = MinecraftClient.getInstance();
    int field9470 = 16;
    float field9471;
    int field9472 = 14;
    float field9473;
    static String field9474;
    static float field9475;
    static int field9476;
    static String field9477;
    static float field9478;
    static int field9479;
    static String field9480;
    static float field9481;
    static int field9482;
    static int field9483;
    static String field9484;
    static float field9485;
    static int field9486;
    boolean field9487 = true;
    long field9488 = 0L;
    String field9489 = field9490;
    static String field9490;
    static float field9491;
    static float field9492;
    static long field9493;
    Animation field9494;
    static long field9495;
    static float field9496;
    static float field9497;
    static float field9498;
    static float field9499;
    static float field9500;

    public boolean cfr_renamed_0(int param1, int param2, int param3) {
        if (!this.field9468) {
            return false;
        }
        if (param1 == 259 && !this.field9469.isEmpty()) {
            this.field9469 = this.field9469.substring(0, this.field9469.length() - 1);
            this.method2777();
            return true;
        }
        if (param1 == 86 && (param3 & 2) != 0) {
            String v4 = WidgetTextField.I.keyboard.getClipboard();
            if (v4 != null) {
                StringBuilder v5 = new StringBuilder(this.field9469);
                for (char v9 : v4.toCharArray()) {
                    if (!this.cfr_renamed_0(v9) || v5.length() >= this.field9470) continue;
                    v5.append(v9);
                }
                this.field9469 = v5.toString();
                this.method2777();
            }
            return true;
        }
        return true;
    }

    private boolean cfr_renamed_0(char param1) {
        return param1 >= 'a' && param1 <= 'z' || param1 >= 'A' && param1 <= 'Z' || param1 >= '0' && param1 <= '9' || param1 == '_' || param1 == '.' || param1 == '-';
    }

    public void method2771(boolean param1) {
        this.field9468 = param1;
        if (param1) {
            this.method2777();
        }
    }

    public WidgetTextField cfr_renamed_0(float param1) {
        this.field9471 = param1;
        return this;
    }

    private FontRenderer cfr_renamed_0() {
        if (this.field9472 >= 1 && this.field9472 < Fonts.field12486.length) {
            return Fonts.field12486[this.field9472];
        }
        return Fonts.field12486[18];
    }

    public WidgetTextField cfr_renamed_0(int param1) {
        this.field9470 = param1;
        return this;
    }

    public boolean method2772() {
        return this.field9468;
    }

    public WidgetTextField method2773(int param1) {
        this.field9472 = param1;
        return this;
    }

    public WidgetTextField method2774(float param1) {
        this.field9473 = param1;
        return this;
    }

    public boolean method2775(char param1, int param2) {
        if (!this.field9468) {
            return false;
        }
        if (this.cfr_renamed_0(param1) && this.field9469.length() < this.field9470) {
            this.field9469 = _4ZXYEo.фМ("makeConcatWithConstants", new Object[]{}, (String)this.field9469, (char)param1);
            this.method2777();
            return true;
        }
        return true;
    }

    public static void method2776() {
        field9476 = HexColor.method2751(field9474, field9475);
        field9479 = HexColor.method2751(field9477, field9478);
        field9482 = HexColor.method2751(field9480, field9481);
        field9483 = new Color(255, 255, 255).getRGB();
        field9486 = HexColor.method2751(field9484, field9485);
    }

    private void method2777() {
        this.field9487 = true;
        this.field9488 = System.currentTimeMillis();
    }

    public void method2778(String param1) {
        this.field9469 = param1 != null ? param1 : "";
    }

    public WidgetTextField cfr_renamed_0(String param1) {
        this.field9489 = param1;
        return this;
    }

    public WidgetTextField(float param1, float param2, float param3, float param4) {
        super(param1, param2, param3, param4);
        this.field9471 = field9491;
        this.field9473 = field9492;
        this.field9494 = new Animation(Easings.field11959, field9493);
    }

    @Override
    public boolean method2736(double param1, double param3, int param5) {
        if (!this.field9347 || !this.cfr_underscore || param5 != 0) {
            return false;
        }
        boolean v6 = this.method2759(param1, param3);
        this.method2771(v6);
        return v6;
    }

    @Override
    public void method2738(DrawContext param1, int param2, int param3, float param4) {
        if (!this.field9347) {
            return;
        }
        long v5 = System.currentTimeMillis();
        if (v5 - this.field9488 > field9495) {
            this.field9487 = !this.field9487;
            this.field9488 = v5;
        }
        this.field9494.animate(this.field9468 ? 1.0f : 0.0f);
        float v7 = this.field9494.getAnimatedValue();
        RenderUtil.method397(this.field9388, this.field9389, this.field9386, this.field9387, this.field9471, field9476);
        int v8 = ColorUtil.method1850(field9479, field9482, v7);
        float v9 = field9496 + v7 * field9497;
        RenderUtil.method392(this.field9388, this.field9389, this.field9386, this.field9387, this.field9471, 1.0f, v8, v9);
        FontRenderer v10 = this.cfr_renamed_0();
        float v11 = this.field9389 + (this.field9387 - v10.method2718()) / 2.0f + field9498;
        float v12 = this.field9388 + this.field9473;
        if (this.field9469.isEmpty() && !this.field9468) {
            v10.method2711(param1, this.field9489, v12, v11, field9486);
        } else {
            v10.method2711(param1, this.field9469, v12, v11, field9483);
            if (this.field9468 && this.field9487) {
                float v13 = v12 + v10.method2719(this.field9469);
                RenderUtil.method397(v13 + 2.0f, this.field9389 + field9499, 1.0f, this.field9387 - field9500, 0.0f, field9483);
            }
        }
    }

    public String method2779() {
        return this.field9469;
    }

    static {
        field9476 = 0x7FFFFFF;
        field9479 = 0xFFFFFFF;
        field9482 = 0x1EFFFFFF;
        field9483 = -1;
        field9486 = 0x4CFFFFFF;
        field9490 = "Username";
        field9491 = 6.0f;
        field9492 = 12.0f;
        field9493 = 150L;
        field9495 = 500L;
        field9496 = 0.06f;
        field9497 = 0.06f;
        field9498 = 0.75f;
        field9499 = 9.0f;
        field9500 = 18.0f;
        cfr_renamed_239 = "\u0001\u0001";
        field9474 = "FFFFFF";
        field9475 = 0.03f;
        field9477 = "FFFFFF";
        field9478 = 0.06f;
        field9480 = "FFFFFF";
        field9481 = 0.12f;
        field9484 = "FFFFFF";
        field9485 = 0.3f;
        WidgetTextField.method2776();
    }
}




