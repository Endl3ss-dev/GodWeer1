/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.render.entity.model.BipedEntityModel
 *  net.minecraft.client.render.entity.model.EntityModel
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  org.joml.Matrix4f
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.ColorUtil;
import minced.core.BooleanSetting;
import minced.core.EventLayerHead;
import minced.core.ThemeColors;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import minced.module.visual.Emotions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class ChinaHat
extends Module {
    static float Эъ;

    static MinecraftClient I = MinecraftClient.getInstance();
    BooleanSetting field4350 = new BooleanSetting(field4368, true);
    static float field4351;
    static float field4352;
    static float field4353;
    static float field4354;
    static float field4355;
    static float field4356;
    static float field4357;
    static float field4358;
    static float field4359;
    static float field4360;
    static float field4361;
    static float field4362;
    static float field4363;
    static float field4364;
    static float field4365;
    static String field4366;
    static String field4367;
    static String field4368;

    @EventHandler(2)
    public void method1106(EventLayerHead param1) {
        Emotions v2 = Emotions.method1578();
        if (v2 != null && v2.method1581() != null) {
            return;
        }
        EntityModel v52 = param1.method2335();
        if (v52 instanceof BipedEntityModel) {
            BipedEntityModel v3 = (BipedEntityModel)v52;
            LivingEntity v52s = param1.method2337();
            if (v52s instanceof PlayerEntity) {
                boolean v6;
                PlayerEntity v4 = (PlayerEntity)v52s;
                boolean v52_93 = Minced.getInstance().getFriendManager().cfr_renamed_4(v4.getGameProfile().getName());
                boolean bl = v6 = v4 == ChinaHat.I.player;
                if (v6 || ((Boolean)this.field4350.getValue()).booleanValue() && v52_93) {
                    MatrixStack v7 = param1.method2336();
                    Box v8 = param1.method2337().getBoundingBox();
                    double v9 = v8.maxX - v8.minX;
                    float v11 = ((ItemStack)v4.getInventory().armor.get(3)).isEmpty() ? field4351 : field4352;
                    v7.push();
                    v3.head.rotate(v7);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    RenderSystem.disableCull();
                    RenderSystem.enableDepthTest();
                    v7.translate(0.0f, -v11, 0.0f);
                    v7.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(field4353));
                    v7.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(field4354));
                    Matrix4f v12 = v7.peek().getPositionMatrix();
                    int v13 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
                    ColorUtil.method1851(v13, field4355);
                    float v15 = field4356;
                    float v16 = field4357;
                    float v17 = field4358;
                    float v18 = (float)(v9 + (double)v17);
                    float v19 = (float)(v9 - (double)v17);
                    BufferBuilder v20 = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
                    for (int v28 = 0; v28 <= 180; ++v28) {
                        float v21 = (float)v28 * field4359;
                        int v22 = v28 * 8;
                        int v29 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
                        float v23 = (float)((double)MathHelper.sin((float)v21) * v9);
                        float v25 = (float)((double)MathHelper.cos((float)v21) * v9);
                        int v27 = ColorUtil.method1857(3, v22, ThemeColors.method3199(MAIN.MODULE_VISUAL), ColorUtil.method1851(ThemeColors.method3199(MAIN.MODULE_VISUAL), field4360));
                        int v26 = ColorUtil.method1853(v27, (float)ColorUtil.cfr_renamed_0(v27) / field4361 * field4362);
                        v20.vertex(v12, v23, 0.0f, v25).color(v26);
                        v20.vertex(v12, 0.0f, field4363, 0.0f).color(v29);
                    }
                    RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
                    BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v20.end());
                    BufferBuilder v28 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                    float v29 = MathHelper.sin((float)0.0f) * v18;
                    float v30 = MathHelper.cos((float)0.0f) * v18;
                    float v31 = MathHelper.sin((float)0.0f) * v19;
                    float v32 = MathHelper.cos((float)0.0f) * v19;
                    for (int v33 = 1; v33 <= 181; ++v33) {
                        float v34 = (float)v33 * field4364;
                        int v35 = (v33 - 1) * 8;
                        float v36 = MathHelper.sin((float)v34) * v18;
                        float v37 = MathHelper.cos((float)v34) * v18;
                        float v38 = MathHelper.sin((float)v34) * v19;
                        float v39 = MathHelper.cos((float)v34) * v19;
                        int v40 = ColorUtil.method1857(3, v35, ThemeColors.method3199(MAIN.MODULE_VISUAL), ColorUtil.method1851(ThemeColors.method3199(MAIN.MODULE_VISUAL), field4365));
                        v28.vertex(v12, v29, v15, v30).color(v40);
                        v28.vertex(v12, v29, v15 + v16, v30).color(v40);
                        v28.vertex(v12, v36, v15 + v16, v37).color(v40);
                        v28.vertex(v12, v36, v15, v37).color(v40);
                        v28.vertex(v12, v31, v15 + v16, v32).color(v40);
                        v28.vertex(v12, v38, v15 + v16, v39).color(v40);
                        v28.vertex(v12, v36, v15 + v16, v37).color(v40);
                        v28.vertex(v12, v29, v15 + v16, v30).color(v40);
                        v29 = v36;
                        v30 = v37;
                        v31 = v38;
                        v32 = v39;
                    }
                    BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v28.end());
                    RenderSystem.enableCull();
                    RenderSystem.disableBlend();
                    v7.pop();
                }
            }
        }
    }

    public ChinaHat() {
        super(field4366, field4367, Combat.Visuals);
        this.addSettings(this.field4350);
    }

    static {
        Эъ = 0.0f;
        field4366 = "China Hat";
        field4367 = "Добавляет китайскую шапку, изменяя внешний вид персонажа";
        field4368 = "Отображать на друзьях";
        field4351 = 0.42f;
        field4352 = 0.5f;
        field4353 = 180.0f;
        field4354 = 90.0f;
        field4355 = 0.5f;
        field4356 = -0.01f;
        field4357 = 0.01f;
        field4358 = 0.01f;
        field4359 = 0.06981317f;
        field4360 = 0.5f;
        field4361 = 255.0f;
        field4362 = 0.5f;
        field4363 = 0.3f;
        field4364 = 0.06981317f;
        field4365 = 0.5f;
    }
}



