/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.Drawable
 *  net.minecraft.client.gui.screen.Overlay
 *  net.minecraft.client.render.RenderLayer
 *  net.minecraft.resource.ResourceReload
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.Util
 *  net.minecraft.util.math.ColorHelper
 *  net.minecraft.util.math.MathHelper
 */
package minced.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Optional;
import java.util.function.Consumer;
import minced.core.Render2DBatch;
import minced.core.Animation;
import minced.core.Easings;
import minced.core.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.Overlay;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

public class MincedLoadingOverlay
extends Overlay
implements Drawable {
    static float n4;
    static float Р;
    static float t2;

    static String field12566 = "minced";
    static String field12567 = "textures/loading_logo.png";
    static Identifier field12568;
    static int field12569;
    boolean field12570;
    long field12571;
    static long field12572;
    long field12573 = field12596;
    static long field12574;
    static float field12575;
    static float field12576;
    static long field12577;
    static float field12578;
    static float field12579;
    MinecraftClient field12580;
    static float field12581;
    static float field12582;
    static double field12583;
    static double field12584;
    static long field12585;
    Animation I;
    static float field12586;
    Animation field12587;
    static float field12588;
    static float field12589;
    long field12590;
    static float field12591;
    static float field12592;
    static long field12593;
    ResourceReload field12594;
    Consumer field12595;
    static long field12596;
    static long field12597;
    static float field12598;
    static long field12599;
    static long field12600;
    static int field12601;

    public static void initializeLogoTexture() {
        field12568 = Identifier.of((String)field12566, (String)field12567);
        field12569 = ColorHelper.getArgb((int)255, (int)0, (int)0, (int)0);
    }

    public void render(DrawContext param1, int param2, int param3, float param4) {
        int v122;
        float v11;
        float v10;
        int v5 = param1.getScaledWindowWidth();
        int v6 = param1.getScaledWindowHeight();
        long v7 = Util.getMeasuringTimeMs();
        if (this.field12570 && this.field12571 == field12572) {
            this.field12571 = v7;
        }
        float v9 = this.field12573 > field12574 ? (float)(v7 - this.field12573) / field12575 : field12576;
        float f = v10 = this.field12571 > field12577 ? (float)(v7 - this.field12571) / field12578 : field12579;
        if (v9 >= 1.0f) {
            if (this.field12580.currentScreen != null) {
                this.field12580.currentScreen.render(param1, 0, 0, param4);
            }
            v11 = 1.0f - MathHelper.clamp((float)(v9 - 1.0f), (float)0.0f, (float)1.0f);
            v122 = MathHelper.ceil((float)(v11 * field12581));
            param1.fill(RenderLayer.getGuiOverlay(), 0, 0, v5, v6, MincedLoadingOverlay.withAlpha(field12569, v122));
        } else if (this.field12570) {
            if (this.field12580.currentScreen != null && v10 < 1.0f) {
                this.field12580.currentScreen.render(param1, param2, param3, param4);
            }
            v11 = MathHelper.clamp((float)v10, (float)0.0f, (float)field12582);
            v122 = MathHelper.ceil((double)(MathHelper.clamp((double)v10, (double)field12583, (double)1.0) * field12584));
            Render2DBatch.method2668(param1);
            Render2DBatch.method2672(0.0f, 0.0f, v5, v6, MincedLoadingOverlay.withAlpha(ColorHelper.getArgb((int)255, (int)12, (int)12, (int)12), v122));
            Render2DBatch.method2651();
        } else {
            GlStateManager._clearColor((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GlStateManager._clear((int)16384);
            v11 = 1.0f;
        }
        if (this.field12573 != field12585) {
            this.I.setEasing(Easings.field11980);
            this.I.animate(field12586);
            this.field12587.setEasing(Easings.field11956);
            this.field12587.animate(0.0f);
        } else {
            this.I.setEasing(Easings.field11957);
            this.I.animate(1.0f);
            this.field12587.setEasing(Easings.field11957);
            this.field12587.animate(1.0f);
        }
        float v122_131 = this.I.getAnimatedValue();
        float v13 = this.field12587.getAnimatedValue();
        float v14 = field12588 * v122_131;
        float v15 = field12589 * v122_131;
        float v16 = ((float)v5 - v14) / 2.0f;
        float v17 = ((float)v6 - v15) / 2.0f;
        float v18 = (float)(System.currentTimeMillis() - this.field12590) / field12591;
        RenderUtil.method401(field12568, v16, v17, v14, v15, field12592 * v122_131, v11 * v13, v18);
        if (v9 >= 2.0f) {
            this.field12580.setOverlay(null);
        }
        if (this.field12573 == field12593 && this.field12594.isComplete() && (!this.field12570 || v10 >= 2.0f)) {
            this.field12594.throwException();
            this.field12595.accept(Optional.empty());
            this.field12573 = Util.getMeasuringTimeMs();
            if (this.field12580.currentScreen != null) {
                this.field12580.currentScreen.init(this.field12580, param1.getScaledWindowWidth(), param1.getScaledWindowHeight());
            }
        }
    }

    public MincedLoadingOverlay(MinecraftClient param1, ResourceReload param2, Consumer param3, boolean param4) {
        this.field12571 = field12597;
        this.field12580 = param1;
        this.field12594 = param2;
        this.field12595 = param3;
        this.field12570 = param4;
        this.field12590 = System.currentTimeMillis();
        this.I = new Animation(Easings.field11952, field12598, field12599);
        this.field12587 = new Animation(Easings.field11952, 0.0f, field12600);
    }

    private static int withAlpha(int param0, int param1) {
        return param0 & field12601 | param1 << 24;
    }

    public boolean pausesGame() {
        return true;
    }

    static {
        MincedLoadingOverlay.initializeLogoTexture();
        field12569 = -16777216;
        n4 = 0.0f;
        Р = 0.0f;
        t2 = 0.0f;
        field12596 = -1L;
        field12597 = -1L;
        field12598 = 0.8f;
        field12599 = 400L;
        field12600 = 400L;
        field12601 = 0xFFFFFF;
        field12572 = -1L;
        field12574 = -1L;
        field12575 = 1000.0f;
        field12576 = -1.0f;
        field12577 = -1L;
        field12578 = 500.0f;
        field12579 = -1.0f;
        field12581 = 255.0f;
        field12582 = 0.5f;
        field12583 = 0.15;
        field12584 = 255.0;
        field12585 = -1L;
        field12586 = 0.8f;
        field12588 = 128.0f;
        field12589 = 80.0f;
        field12591 = 1000.0f;
        field12592 = 3.0f;
        field12593 = -1L;
    }
}





