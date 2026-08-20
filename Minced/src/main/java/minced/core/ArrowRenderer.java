/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.RotationAxis
 *  sLM.4ZXYEo
 */
package minced.core;

import java.awt.Color;
import java.lang.invoke.CallSite;
import minced.core.CommandGps;
import minced.core.Render2DBatch;
import minced.core.EventRender2D;
import minced.core.ThemeColors;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import sLM._4ZXYEo;

public class ArrowRenderer
{
    static String cfr_renamed_130;

    static MinecraftClient I = MinecraftClient.getInstance();
    static float field753;
    static double field754;
    static double field755;
    static double field756;
    static float field757;
    static float field758;
    static float field759;
    static String field760;
    static String field761;
    static float field762;

    @EventHandler(2)
    public void method199(EventRender2D param1) {
        if (!CommandGps.field7516 || ArrowRenderer.I.player == null) {
            return;
        }
        Render2DBatch.method2668(param1.method2997());
        float v2 = field753;
        float v3 = (float)(Math.toDegrees(Math.atan2(CommandGps.field7519 - ArrowRenderer.I.player.getZ(), CommandGps.field7518 - ArrowRenderer.I.player.getX())) - (double)ArrowRenderer.I.player.getYaw() - field754);
        int v4 = (int)Math.sqrt(Math.pow(CommandGps.field7518 - ArrowRenderer.I.player.getX(), field755) + Math.pow(CommandGps.field7519 - ArrowRenderer.I.player.getZ(), field756));
        int v5 = I.getWindow().getScaledWidth();
        int v6 = I.getWindow().getScaledHeight();
        float v7 = ((float)v5 - v2) / 2.0f;
        float v8 = (float)v6 / 2.0f - field757;
        MatrixStack v9 = param1.method2997().getMatrices();
        v9.push();
        v9.translate(v7 + v2 / 2.0f, v8 + v2 / field758, 0.0f);
        v9.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(v3 + field759));
        v9.translate(-v2 / 2.0f, -v2 / 2.0f, 0.0f);
        RenderUtil.method388(v9, Identifier.of((String)field760, (String)field761), 0.0, 0.0, v2, v2, new Color(ThemeColors.method3199(MAIN.MODULE_VISUAL)));
        v9.pop();
        Object v10 = _4ZXYEo.cfr_renamed_129("makeConcatWithConstants", new Object[]{}, (int)v4);
        float v11 = Fonts.field12486[14].method2719((String)((Object)v10));
        float v12 = v7 + v2 / 2.0f;
        float v13 = v8 + field762;
        float v14 = v12 - v11 / 2.0f;
        Fonts.field12486[14].method2711(param1.method2997(), (String)((Object)v10), v14, v13, -1);
        Render2DBatch.method2651();
    }

    static {
        field753 = 32.0f;
        field754 = 180.0;
        field755 = 2.0;
        field756 = 2.0;
        field757 = 150.0f;
        field758 = 6.0f;
        field759 = 90.0f;
        field760 = "minced";
        field761 = "textures/arrows.png";
        cfr_renamed_130 = "\u0001m";
        field762 = 17.0f;
    }
}



