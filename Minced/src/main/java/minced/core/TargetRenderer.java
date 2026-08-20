/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.GlStateManager$DstFactor
 *  com.mojang.blaze3d.platform.GlStateManager$SrcFactor
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgram
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RotationAxis
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package minced.core;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.ColorUtil;
import minced.core.RenderPrimitives;
import minced.core.Shaders;
import minced.core.CancellableEvent;
import minced.core.EventRender3D;
import minced.core.Render2DBatch;
import minced.core.ThemeColors;
import minced.core.Easings;
import minced.core.AnimatedFloat;
import minced.core.Minced;
import minced.enums.MAIN;
import minced.module.combat.AttackAura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class TargetRenderer
implements CancellableEvent {
    static long f13;

    static String field8582;
    AnimatedFloat field8583;
    static float field8584;
    LivingEntity field8585;
    long field8586;
    static float field8587;
    float field8588;
    static float field8589;
    static float field8590;
    static float field8591;
    static float field8592;
    AnimatedFloat field8593 = new AnimatedFloat(0.0f, field8592, Easings.field11959);
    static float field8594;
    AnimatedFloat field8595 = new AnimatedFloat(0.0f, field8594, Easings.field11952);
    static float field8596;
    AnimatedFloat field8597 = new AnimatedFloat(1.0f, field8596, Easings.field11952);
    static float field8598;
    AnimatedFloat field8599 = new AnimatedFloat(0.0f, field8598, Easings.field11952);
    static float field8600;
    AnimatedFloat field8601 = new AnimatedFloat(1.0f, field8600, Easings.field11952);
    static float field8602;
    AnimatedFloat field8603 = new AnimatedFloat(0.0f, field8602, Easings.field11952);
    static float field8604;
    AnimatedFloat field8605 = new AnimatedFloat(0.0f, field8604, Easings.field11952);
    static float field8606;
    AnimatedFloat field8607 = new AnimatedFloat(0.0f, field8606, Easings.field11952);
    static float field8608;
    static float field8609;
    AnimatedFloat field8610;
    static float field8611;
    AnimatedFloat field8612;
    static float field8613;
    AnimatedFloat field8614;
    static float field8615;
    AnimatedFloat field8616;
    static float field8617;
    AnimatedFloat field8618;
    int field8619 = 200;
    static String field8620;
    LivingEntity field8621;
    static float field8622;
    static float field8623;
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    static double field8624;
    static long field8625;
    static float field8626;
    static float field8627;
    static float field8628;
    static String field8629;
    static String field8630;
    static String field8631;
    static String field8632;
    static String field8633;
    static String field8634;
    static float field8635;
    static float field8636;
    static float field8637;
    static float field8638;
    static String field8639;
    static String field8640;
    static String field8641;
    static String field8642;
    static float field8643;
    static float field8644;
    static String field8645;
    static float field8646;
    LivingEntity var_net_minecraft_entity_LivingEntity_I;
    static double field8647;
    static double field8648;
    static float field8649;
    static float field8650;
    static float field8651;
    static float field8652;
    static double field8653;
    static double field8654;
    static double field8655;
    static float field8656;
    static double field8657;
    static float field8658;
    static float field8659;
    static float field8660;
    static float field8661;
    static float field8662;
    static float field8663;
    static double field8664;
    static String field8665 = "minced";
    static String field8666 = "icons/world_render/glow.png";
    static float field8667;
    static float field8668;
    static float field8669;
    static float field8670;
    static float field8671;
    static float field8672;
    static float field8673;
    static float field8674;
    static float field8675;
    static float field8676;
    static String field8677;
    static float field8678;
    boolean field8679;
    Entity var_net_minecraft_entity_Entity_I;
    Vec3d field8680;
    float field8681;
    static float field8682;
    long field8683;
    static float field8684;
    double field8685;
    static double field8686;
    static double field8687;
    static double field8688;
    static float field8689;
    static float field8690;
    static float field8691;
    static float field8692;
    static float field8693;
    static String field8694;
    static String field8695;
    static String field8696;
    static float field8697;
    static float field8698;
    Entity field8699;
    float field8700;
    long field8701;
    static float field8702;
    static float field8703;
    static String field8704;
    static String field8705;
    static float field8706;
    static float field8707;
    static float field8708;
    static double field8709;
    static float field8710;
    static float field8711;
    static float field8712;
    static float field8713;
    static float field8714;
    static float field8715;
    static double field8716;
    static float field8717;
    static float field8718;
    static float field8719;
    static float field8720;
    static float field8721;
    static float field8722;
    static float field8723;
    static float field8724;
    static float field8725;
    static float field8726;
    static float field8727;
    static float field8728;
    static float field8729;
    static float field8730;
    static float field8731;
    static String field8732;
    LivingEntity field8733;
    long field8734;
    static long field8735;
    static float field8736;
    Vec3d field8737;
    static float field8738;
    static double field8739;
    long field8740;
    static float field8741;
    static float field8742;
    float field8743;
    static double field8744;
    static float field8745;
    static float field8746;
    static float field8747;
    static float field8748;
    static String field8749;
    static String field8750;
    static float field8751;
    static float field8752;
    static float field8753;
    static float field8754;
    static float field8755;
    static float field8756;
    static float field8757;
    static float field8758;
    static float field8759;
    static float field8760;
    static double field8761;
    static float field8762;
    static float field8763;
    static float field8764;
    static float field8765;
    static float field8766;
    static float field8767;
    static float field8768;
    static float field8769;
    static float field8770;
    static float field8771;

    @EventHandler(2)
    private void method2423(EventRender3D param1) {
        AttackAura v2 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        boolean v3 = v2.isEnabled() && v2.method788().isMode(field8582);
        LivingEntity v4 = v3 ? AttackAura.method861() : null;
        boolean v5 = v4 != null && v4.isAlive();
        this.field8583.setTarget(v3 && v5 ? 1.0f : 0.0f);
        if (this.field8583.getValue() <= field8584) {
            this.field8585 = null;
            this.field8586 = 0L;
            return;
        }
        if (v5) {
            if (this.field8585 == null) {
                this.field8586 = System.currentTimeMillis();
            }
            this.field8585 = v4;
        }
        if (this.field8585 == null) {
            return;
        }
        long v6 = System.currentTimeMillis();
        if (this.field8586 != 0L) {
            float v8 = (float)(v6 - this.field8586) / field8587;
            this.field8588 += v8 * field8589;
            if (this.field8588 >= field8590) {
                this.field8588 -= field8591;
            }
        }
        this.field8586 = v6;
        this.cfr_renamed_4(param1.method2348(), this.field8585, param1.O());
    }

    public TargetRenderer() {
        this.field8583 = new AnimatedFloat(0.0f, field8608, Easings.field11952);
        this.field8610 = new AnimatedFloat(0.0f, field8609, Easings.field11959);
        this.field8612 = new AnimatedFloat(0.0f, field8611, Easings.field11959);
        this.field8614 = new AnimatedFloat(0.0f, field8613, Easings.field11952);
        this.field8616 = new AnimatedFloat(1.0f, field8615, Easings.field11952);
        this.field8618 = new AnimatedFloat(0.0f, field8617, Easings.field11952);
    }

    @EventHandler(2)
    private void method2424(EventRender3D param1) {
        LivingEntity v4;
        AttackAura v2 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        boolean v3 = v2.isEnabled() && v2.method788().isMode(field8620);
        Object object = v4 = v3 ? AttackAura.method861() : null;
        if (v4 != null) {
            this.field8621 = v4;
        }
        this.field8614.setTarget(v4 != null ? 1.0f : 0.0f);
        if (this.field8621 == null || this.field8614.getValue() < field8622) {
            if (v4 == null) {
                this.field8621 = null;
            }
            return;
        }
        float v5 = this.field8621.maxHurtTime > 0 ? MathHelper.clamp((float)((float)this.field8621.hurtTime / (float)this.field8621.maxHurtTime), (float)0.0f, (float)1.0f) : 0.0f;
        this.field8616.setTarget(MathHelper.lerp((float)v5, (float)1.0f, (float)field8623));
        this.field8618.setTarget(v5);
        Vec3d v6 = this.field8621.getLerpedPos(param1.O());
        Vec3d v7 = TargetRenderer.var_net_minecraft_client_MinecraftClient_I.gameRenderer.getCamera().getPos();
        double v8 = v6.x - v7.x;
        double v10 = v6.y - v7.y + field8624;
        double v12 = v6.z - v7.z;
        float v14 = this.field8614.getValue();
        float v15 = (float)(System.currentTimeMillis() % field8625) / field8626;
        float v16 = this.field8621.getWidth() * field8627 * this.field8616.getValue();
        MatrixStack v17 = param1.method2348();
        v17.push();
        v17.translate((float)v8, (float)v10, (float)v12);
        v17.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(v15 * field8628));
        Matrix4f v18 = new Matrix4f((Matrix4fc)v17.peek().getPositionMatrix());
        v17.pop();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
        RenderSystem.disableCull();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        int v19 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        float v20 = this.field8618.getValue();
        RenderSystem.setShaderTexture((int)0, (int)0);
        ShaderProgram v21 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8373);
        if (v21 != null) {
            if (v21.getUniform(field8629) != null) {
                v21.getUniform(field8630).set(RenderSystem.getModelViewMatrix());
            }
            if (v21.getUniform(field8631) != null) {
                v21.getUniform(field8632).set(RenderSystem.getProjectionMatrix());
            }
            if (v21.getUniform(field8633) != null) {
                v21.getUniform(field8634).set((float)ColorUtil.method1854(v19) / field8635, (float)ColorUtil.method1846(v19) / field8636, (float)ColorUtil.method1847(v19) / field8637, ThemeColors.method3214(MAIN.MODULE_VISUAL) / field8638);
            }
            if (v21.getUniform(field8639) != null) {
                v21.getUniform(field8640).set(v14);
            }
            if (v21.getUniform(field8641) != null) {
                v21.getUniform(field8642).set(v20);
            }
        }
        float v22 = v16 * field8643;
        float v23 = field8644;
        BufferBuilder v24 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        v24.vertex(v18, -v22, v23, v22).texture(0.0f, 1.0f);
        v24.vertex(v18, v22, v23, v22).texture(1.0f, 1.0f);
        v24.vertex(v18, v22, v23, -v22).texture(1.0f, 0.0f);
        v24.vertex(v18, -v22, v23, -v22).texture(0.0f, 0.0f);
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v24.end());
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (int)0);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableCull();
    }

    @EventHandler(2)
    private void method2425(EventRender3D param1) {
        AttackAura v2 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        boolean v3 = v2.isEnabled() && v2.method788().isMode(field8645);
        LivingEntity v4 = v3 ? AttackAura.method861() : null;
        boolean v5 = v4 != null && v4.isAlive();
        this.field8610.setTarget(v3 && v5 ? 1.0f : 0.0f);
        if (this.field8610.getValue() <= field8646) {
            this.var_net_minecraft_entity_LivingEntity_I = null;
            return;
        }
        if (v5) {
            this.var_net_minecraft_entity_LivingEntity_I = v4;
        }
        if (this.var_net_minecraft_entity_LivingEntity_I == null) {
            return;
        }
        Vec3d v6 = TargetRenderer.var_net_minecraft_client_MinecraftClient_I.gameRenderer.getCamera().getPos();
        float v7 = param1.O();
        double v8 = MathHelper.lerp((double)v7, (double)this.var_net_minecraft_entity_LivingEntity_I.prevX, (double)this.var_net_minecraft_entity_LivingEntity_I.getX()) - v6.x;
        double v10 = MathHelper.lerp((double)v7, (double)this.var_net_minecraft_entity_LivingEntity_I.prevZ, (double)this.var_net_minecraft_entity_LivingEntity_I.getZ()) - v6.z;
        double v12 = MathHelper.lerp((double)v7, (double)this.var_net_minecraft_entity_LivingEntity_I.prevY, (double)this.var_net_minecraft_entity_LivingEntity_I.getY()) - v6.y + Math.min(Math.sin((double)System.currentTimeMillis() / field8647) + field8648, (double)this.var_net_minecraft_entity_LivingEntity_I.getHeight());
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
        Matrix4f v14 = param1.method2348().peek().getPositionMatrix();
        BufferBuilder v15 = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        int v16 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        float v17 = (float)ColorUtil.method1854(v16) / field8649;
        float v18 = (float)ColorUtil.method1846(v16) / field8650;
        float v19 = (float)ColorUtil.method1847(v16) / field8651;
        float v20 = this.field8610.getValue();
        float v21 = this.var_net_minecraft_entity_LivingEntity_I.getWidth() * field8652;
        float v22 = 0.0f;
        while ((double)v22 <= field8653) {
            double v23 = v8 + (double)v21 * Math.cos(v22);
            double v25 = v10 + (double)v21 * Math.sin(v22);
            v15.vertex(v14, (float)v23, (float)(v12 - Math.cos((double)System.currentTimeMillis() / field8654) / field8655), (float)v25).color(v17, v18, v19, field8656 * v20);
            v15.vertex(v14, (float)v23, (float)v12, (float)v25).color(v17, v18, v19, v20);
            v22 = (float)((double)v22 + field8657);
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v15.end());
        RenderSystem.depthMask((boolean)true);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    private void cfr_renamed_4(MatrixStack param1, LivingEntity param2, float param3) {
        Matrix4f v18;
        float v15;
        float v14;
        float v12;
        Vec3d v4 = TargetRenderer.var_net_minecraft_client_MinecraftClient_I.gameRenderer.getCamera().getPos();
        int v5 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v6 = ColorUtil.method1855(v5, (int)(field8658 * this.field8583.getValue()));
        float v7 = param2.getWidth() * field8659;
        Vec3d v8 = this.cfr_renamed_4(param2, param3);
        param1.push();
        param1.translate(v8.x - v4.x, v8.y - v4.y, v8.z - v4.z);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        BufferBuilder v9 = RenderPrimitives.method1986();
        for (int v10 = 0; v10 < 360; v10 += 20) {
            float v11 = field8660 - field8661 * this.field8583.getValue();
            v12 = (float)Math.toRadians((float)v10 + this.field8588);
            float v13 = (float)(Math.sin(v12) * (double)v7 * (double)v11);
            v14 = (float)(Math.cos(v12) * (double)v7 * (double)v11);
            v15 = field8662;
            param1.push();
            param1.translate(v13, field8663 + param2.getHeight() * Math.abs(MathHelper.sin((float)v10)), v14);
            Vec3d v16 = v8.add((double)v13, 1.0, (double)v14);
            Vec3d v17 = param2.getPos().add(0.0, (double)param2.getHeight() / field8664, 0.0);
            Vector3f v18v = new Vector3f((float)(v17.x - v16.x), (float)(v17.y - v16.y), (float)(v17.z - v16.z)).normalize();
            Vector3f v19 = new Vector3f(0.0f, 1.0f, 0.0f);
            Quaternionf v20 = new Quaternionf().rotationTo((Vector3fc)v19, (Vector3fc)v18v);
            param1.multiply(v20);
            RenderPrimitives.method1984(param1, v9, 0.0f, 0.0f, 0.0f, v15, v6);
            param1.pop();
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v9.end());
        Identifier v10 = Identifier.of((String)field8665, (String)field8666);
        RenderSystem.setShaderTexture((int)0, (Identifier)v10);
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        BufferBuilder v11 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        v12 = field8667;
        for (int v13 = 0; v13 < 360; v13 += 20) {
            v14 = field8668 - field8669 * this.field8583.getValue();
            v15 = (float)Math.toRadians((float)v13 + this.field8588);
            float v16 = (float)(Math.sin(v15) * (double)v7 * (double)v14);
            float v17 = (float)(Math.cos(v15) * (double)v7 * (double)v14);
            param1.push();
            param1.translate(v16, field8670 + param2.getHeight() * Math.abs(MathHelper.sin((float)v13)), v17);
            param1.multiply(TargetRenderer.var_net_minecraft_client_MinecraftClient_I.gameRenderer.getCamera().getRotation());
            v18 = param1.peek().getPositionMatrix();
            int v19 = ColorUtil.method1855(v6, (int)(field8671 * this.field8583.getValue() * field8672));
            float v20 = (float)ColorUtil.method1854(v19) / field8673;
            float v21 = (float)ColorUtil.method1846(v19) / field8674;
            float v22 = (float)ColorUtil.method1847(v19) / field8675;
            float v23 = (float)ColorUtil.cfr_renamed_0(v19) / field8676;
            float v24 = v12 / 2.0f;
            v11.vertex(v18, -v24, -v24, 0.0f).texture(0.0f, 0.0f).color(v20, v21, v22, v23);
            v11.vertex(v18, -v24, v24, 0.0f).texture(0.0f, 1.0f).color(v20, v21, v22, v23);
            v11.vertex(v18, v24, v24, 0.0f).texture(1.0f, 1.0f).color(v20, v21, v22, v23);
            v11.vertex(v18, v24, -v24, 0.0f).texture(1.0f, 0.0f).color(v20, v21, v22, v23);
            param1.pop();
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v11.end());
        RenderSystem.depthMask((boolean)true);
        RenderSystem.setShaderTexture((int)0, (int)0);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        param1.pop();
    }

    @EventHandler(2)
    private void method2426(EventRender3D param1) {
        LivingEntity v7;
        AttackAura v2 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        LivingEntity v3 = v2.isEnabled() && v2.method788().isMode(field8677) ? AttackAura.method861() : null;
        boolean v4 = v3 != null;
        this.field8599.setTarget(v4 ? 1.0f : 0.0f);
        this.field8597.setTarget(v4 ? 1.0f : 2.0f);
        if (!v4 && this.field8599.getValue() <= field8678) {
            this.field8679 = false;
            this.var_net_minecraft_entity_Entity_I = null;
            return;
        }
        Vec3d v5 = this.field8680;
        float v6 = this.field8681;
        Object object = v7 = v4 ? v3 : (LivingEntity)this.var_net_minecraft_entity_Entity_I;
        if (v7 != null && v7.isAlive()) {
            v5 = v7.getLerpedPos(param1.O());
            v6 = v7.getHeight() / 2.0f;
        } else if (this.field8680 == null || !this.field8679) {
            return;
        }
        if (v4) {
            this.field8680 = v5;
            this.field8681 = v6;
            this.field8679 = true;
            this.var_net_minecraft_entity_Entity_I = v3;
        }
        MatrixStack v8 = param1.method2348();
        Vec3d v9 = TargetRenderer.var_net_minecraft_client_MinecraftClient_I.gameRenderer.getCamera().getPos();
        v8.push();
        v8.translate(v5.x - v9.x, v5.y + (double)v6 - v9.y, v5.z - v9.z);
        float v10 = v4 && v3.hurtTime > 0 ? MathHelper.clamp((float)((float)v3.hurtTime / (float)v3.maxHurtTime), (float)0.0f, (float)1.0f) : 0.0f;
        this.field8601.setTarget(MathHelper.lerp((float)v10, (float)1.0f, (float)field8682));
        this.field8603.setTarget(v10);
        float v11 = this.field8597.getValue() * this.field8601.getValue();
        float v12 = v11 / 2.0f;
        double v13 = this.field8683 == 0L ? 0.0 : (double)((float)(System.currentTimeMillis() - this.field8683) / field8684);
        this.field8683 = System.currentTimeMillis();
        this.field8685 += field8686 * (v4 ? 1.0 : field8687) * v13;
        v8.multiply(TargetRenderer.var_net_minecraft_client_MinecraftClient_I.getEntityRenderDispatcher().camera.getRotation());
        v8.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)(Math.sin(this.field8685) * field8688)));
        Matrix4f v15 = new Matrix4f((Matrix4fc)v8.peek().getPositionMatrix());
        RenderSystem.enableBlend();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        int v16 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v17 = ColorUtil.method1856(ColorUtil.method1852(MathHelper.clamp((int)((int)((float)ColorUtil.method1854(v16) * (1.0f - this.field8603.getValue()) + field8689 * this.field8603.getValue())), (int)0, (int)255), MathHelper.clamp((int)((int)((float)ColorUtil.method1846(v16) * (1.0f - this.field8603.getValue()))), (int)0, (int)255), MathHelper.clamp((int)((int)((float)ColorUtil.method1847(v16) * (1.0f - this.field8603.getValue()))), (int)0, (int)255), (int)(ThemeColors.method3214(MAIN.MODULE_VISUAL) * MathHelper.clamp((float)this.field8599.getValue(), (float)0.0f, (float)1.0f))), 45);
        float v18 = (float)ColorUtil.method1854(v17) / field8690;
        float v19 = (float)ColorUtil.method1846(v17) / field8691;
        float v20 = (float)ColorUtil.method1847(v17) / field8692;
        float v21 = (float)ColorUtil.cfr_renamed_0(v17) / field8693;
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture((int)0, (Identifier)Identifier.of((String)field8694, (String)field8695));
        Render2DBatch.method2657(v15);
        Render2DBatch.method2673(-v12, -v12 + v11, v12, -v12 + v11, v12, -v12, -v12, -v12, v18, v19, v20, v21);
        Render2DBatch.method2673(-v12, -v12 + v11, v12, -v12 + v11, v12, -v12, -v12, -v12, v18, v19, v20, v21);
        Render2DBatch.method2675();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableCull();
        v8.pop();
    }

    @EventHandler(2)
    private void method2427(EventRender3D param1) {
        AttackAura v2 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        boolean v3 = v2.isEnabled() && v2.method788().isMode(field8696);
        LivingEntity v4 = v3 ? AttackAura.method861() : null;
        boolean v5 = v4 != null && v4.isAlive();
        this.field8593.setTarget(v3 && v5 ? 1.0f : 0.0f);
        this.field8595.setTarget(v3 && v5 ? 1.0f : 0.0f);
        if (this.field8593.getValue() <= field8697 && this.field8595.getValue() <= field8698) {
            this.field8699 = null;
            this.field8700 = 0.0f;
            this.field8701 = 0L;
            return;
        }
        if (v5) {
            if (this.field8699 == null) {
                this.field8701 = System.currentTimeMillis();
            }
            this.field8699 = v4;
        }
        if (this.field8699 == null) {
            return;
        }
        this.field8700 += field8702 * (float)(System.currentTimeMillis() - this.field8701) / field8703;
        this.field8701 = System.currentTimeMillis();
        Vec3d v6 = this.field8699.getLerpedPos(param1.O());
        Vec3d v7 = TargetRenderer.var_net_minecraft_client_MinecraftClient_I.gameRenderer.getCamera().getPos();
        float v8 = v5 && v4.hurtTime > 0 ? MathHelper.clamp((float)((float)v4.hurtTime / (float)v4.maxHurtTime), (float)0.0f, (float)1.0f) : 0.0f;
        this.field8605.setTarget(v8);
        RenderSystem.enableBlend();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture((int)0, (Identifier)Identifier.of((String)field8704, (String)field8705));
        BufferBuilder v9 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        int v10 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v11 = ColorUtil.method1852(MathHelper.clamp((int)((int)((float)ColorUtil.method1854(v10) * (1.0f - this.field8605.getValue()) + field8706 * this.field8605.getValue())), (int)0, (int)255), MathHelper.clamp((int)((int)((float)ColorUtil.method1846(v10) * (1.0f - this.field8605.getValue()))), (int)0, (int)255), MathHelper.clamp((int)((int)((float)ColorUtil.method1847(v10) * (1.0f - this.field8605.getValue()))), (int)0, (int)255), ColorUtil.cfr_renamed_0(v10));
        MatrixStack v12 = param1.method2348();
        for (int v13 = 0; v13 < 9; v13 += 3) {
            for (int v14 = 0; v14 < 12; ++v14) {
                v12.push();
                v12.translate((float)(v6.x - v7.x) + field8707 * MathHelper.sin((float)(this.field8700 + (float)v14 * field8708 + (float)((int)Math.pow(v13, field8709)))), (float)(v6.y - v7.y) + field8710 + field8711 * MathHelper.sin((float)(this.field8700 + (float)v14 * field8712)) + field8713 * (float)v13, (float)(v6.z - v7.z) + field8714 * MathHelper.cos((float)(this.field8700 + (float)v14 * field8715 - (float)((int)Math.pow(v13, field8716)))));
                float v15 = this.field8595.getValue() * (field8717 + (float)v14 / field8718);
                v12.scale(v15, v15, v15);
                v12.multiply(TargetRenderer.var_net_minecraft_client_MinecraftClient_I.getEntityRenderDispatcher().camera.getRotation());
                int v16 = ColorUtil.method1855(v11, (int)(this.field8595.getValue() * field8719));
                float v17 = (float)ColorUtil.method1854(v16) / field8720;
                float v18 = (float)ColorUtil.method1846(v16) / field8721;
                float v19 = (float)ColorUtil.method1847(v16) / field8722;
                float v20 = (float)ColorUtil.cfr_renamed_0(v16) / field8723;
                Matrix4f v21 = new Matrix4f((Matrix4fc)v12.peek().getPositionMatrix());
                v9.vertex(v21, field8724, field8725, 0.0f).texture(0.0f, 1.0f).color(v17, v18, v19, v20);
                v9.vertex(v21, field8726, field8727, 0.0f).texture(1.0f, 1.0f).color(v17, v18, v19, v20);
                v9.vertex(v21, field8728, field8729, 0.0f).texture(1.0f, 0.0f).color(v17, v18, v19, v20);
                v9.vertex(v21, field8730, field8731, 0.0f).texture(0.0f, 0.0f).color(v17, v18, v19, v20);
                v12.pop();
            }
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v9.end());
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableCull();
    }

    private Vec3d cfr_renamed_4(LivingEntity param1, float param2) {
        return new Vec3d(MathHelper.lerp((double)param2, (double)param1.prevX, (double)param1.getX()), MathHelper.lerp((double)param2, (double)param1.prevY, (double)param1.getY()), MathHelper.lerp((double)param2, (double)param1.prevZ, (double)param1.getZ()));
    }

    @EventHandler(2)
    private void method2428(EventRender3D param1) {
        AttackAura v2 = (AttackAura)Minced.getInstance().getModuleManager().method187(AttackAura.class);
        boolean v3 = v2.isEnabled() && v2.method788().isMode(field8732);
        LivingEntity v4 = v3 ? AttackAura.method861() : null;
        boolean v5 = v4 != null && v4.isAlive();
        long v6 = System.currentTimeMillis();
        if (v5) {
            this.field8733 = v4;
            this.field8734 = v6;
        }
        boolean v8 = this.field8733 != null && v6 - this.field8734 <= field8735;
        this.field8612.setTarget(v3 && (v5 || v8) ? 1.0f : 0.0f);
        if (!v3 && this.field8612.getValue() <= field8736) {
            this.field8733 = null;
            this.field8737 = null;
            this.field8734 = 0L;
            return;
        }
        if (!v5 && !v8 && this.field8612.getValue() <= field8738) {
            this.field8733 = null;
            this.field8737 = null;
            this.field8734 = 0L;
            return;
        }
        if (this.field8733 == null) {
            return;
        }
        float v9 = param1.O();
        Vec3d v10 = this.cfr_renamed_4(this.field8733, v9);
        this.field8737 = this.field8737 == null ? v10 : this.field8737.lerp(v10, field8739);
        Vec3d v11 = TargetRenderer.var_net_minecraft_client_MinecraftClient_I.gameRenderer.getCamera().getPos();
        MatrixStack v12 = param1.method2348();
        if (this.field8740 == 0L) {
            this.field8740 = v6;
        }
        float v13 = MathHelper.clamp((float)((float)(v6 - this.field8740) / field8741), (float)0.0f, (float)field8742);
        this.field8740 = v6;
        this.field8743 += v13 * 2.0f;
        if ((double)this.field8743 > field8744) {
            this.field8743 -= field8745;
        }
        int v14 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        float v15 = this.field8612.getValue();
        float v16 = field8746 + field8747 * MathHelper.sin((float)(this.field8743 * field8748));
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture((int)0, (Identifier)Identifier.of((String)field8749, (String)field8750));
        BufferBuilder v17 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        for (int v18 = 0; v18 < 3; ++v18) {
            float v19 = field8751 + (float)v18 * field8752;
            float v20 = (this.field8733.getWidth() * field8753 + field8754 * (float)v18) * v16;
            float v21 = (float)(this.field8737.y - v11.y + (double)(this.field8733.getHeight() * (field8755 + (float)v18 * field8756)));
            int v22 = ColorUtil.method1855(v14, (int)((float)(120 - v18 * 26) * v15));
            float v23 = (float)ColorUtil.method1854(v22) / field8757;
            float v24 = (float)ColorUtil.method1846(v22) / field8758;
            float v25 = (float)ColorUtil.method1847(v22) / field8759;
            float v26 = (float)ColorUtil.cfr_renamed_0(v22) / field8760;
            for (int v27 = 0; v27 < 8; ++v27) {
                float v28 = (float)((double)v27 * field8761 + (double)(this.field8743 * (field8762 + (float)v18 * field8763)));
                float v29 = (float)(this.field8737.x - v11.x + Math.cos(v28) * (double)v20);
                float v30 = (float)(this.field8737.z - v11.z + Math.sin(v28) * (double)v20);
                v12.push();
                v12.translate(v29, v21, v30);
                v12.multiply(TargetRenderer.var_net_minecraft_client_MinecraftClient_I.getEntityRenderDispatcher().camera.getRotation());
                v12.scale(v19, v19, v19);
                Matrix4f v31 = v12.peek().getPositionMatrix();
                v17.vertex(v31, field8764, field8765, 0.0f).texture(0.0f, 0.0f).color(v23, v24, v25, v26);
                v17.vertex(v31, field8766, field8767, 0.0f).texture(0.0f, 1.0f).color(v23, v24, v25, v26);
                v17.vertex(v31, field8768, field8769, 0.0f).texture(1.0f, 1.0f).color(v23, v24, v25, v26);
                v17.vertex(v31, field8770, field8771, 0.0f).texture(1.0f, 0.0f).color(v23, v24, v25, v26);
                v12.pop();
            }
        }
        BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v17.end());
        RenderSystem.depthMask((boolean)true);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc((GlStateManager.SrcFactor)GlStateManager.SrcFactor.SRC_ALPHA, (GlStateManager.DstFactor)GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }

    static {
        f13 = 0L;
        field8592 = 10.0f;
        field8594 = 6.0f;
        field8596 = 6.0f;
        field8598 = 6.0f;
        field8600 = 6.0f;
        field8602 = 6.0f;
        field8604 = 6.0f;
        field8606 = 6.0f;
        field8608 = 6.0f;
        field8609 = 6.0f;
        field8611 = 6.0f;
        field8613 = 6.0f;
        field8615 = 6.0f;
        field8617 = 6.0f;
        field8677 = "Ромб";
        field8678 = 0.01f;
        field8682 = 0.25f;
        field8684 = 1000.0f;
        field8686 = 2.0;
        field8687 = 1.5;
        field8688 = 180.0;
        field8689 = 200.0f;
        field8690 = 255.0f;
        field8691 = 255.0f;
        field8692 = 255.0f;
        field8693 = 255.0f;
        field8694 = "minced";
        field8695 = "icons/world_render/target.png";
        field8696 = "Призраки";
        field8697 = 0.01f;
        field8698 = 0.01f;
        field8702 = 4.0f;
        field8703 = 600.0f;
        field8704 = "minced";
        field8705 = "icons/world_render/glow.png";
        field8706 = 200.0f;
        field8707 = 0.8f;
        field8708 = 0.1f;
        field8709 = 2.0;
        field8710 = 0.5f;
        field8711 = 0.3f;
        field8712 = 0.2f;
        field8713 = 0.2f;
        field8714 = 0.8f;
        field8715 = 0.1f;
        field8716 = 2.0;
        field8717 = 0.0035f;
        field8718 = 2000.0f;
        field8719 = 255.0f;
        field8720 = 255.0f;
        field8721 = 255.0f;
        field8722 = 255.0f;
        field8723 = 255.0f;
        field8724 = -25.0f;
        field8725 = 25.0f;
        field8726 = 25.0f;
        field8727 = 25.0f;
        field8728 = 25.0f;
        field8729 = -25.0f;
        field8730 = -25.0f;
        field8731 = -25.0f;
        field8582 = "Кристаллы";
        field8584 = 0.01f;
        field8587 = 1000.0f;
        field8589 = 60.0f;
        field8590 = 360.0f;
        field8591 = 360.0f;
        field8658 = 255.0f;
        field8659 = 1.5f;
        field8660 = 1.2f;
        field8661 = 0.5f;
        field8662 = 0.1f;
        field8663 = 0.1f;
        field8664 = 2.0;
        field8667 = 0.8f;
        field8668 = 1.2f;
        field8669 = 0.5f;
        field8670 = 0.1f;
        field8671 = 255.0f;
        field8672 = 0.2f;
        field8673 = 255.0f;
        field8674 = 255.0f;
        field8675 = 255.0f;
        field8676 = 255.0f;
        field8645 = "Кружок";
        field8646 = 0.01f;
        field8647 = 400.0;
        field8648 = 0.95;
        field8649 = 255.0f;
        field8650 = 255.0f;
        field8651 = 255.0f;
        field8652 = 0.8f;
        field8653 = 6.440264939859076;
        field8654 = 400.0;
        field8655 = 2.0;
        field8656 = 0.01f;
        field8657 = 0.15707963267948966;
        field8732 = "Сияние";
        field8735 = 420L;
        field8736 = 0.01f;
        field8738 = 0.01f;
        field8739 = 0.24;
        field8741 = 1000.0f;
        field8742 = 0.05f;
        field8744 = Math.PI * 2;
        field8745 = (float)Math.PI * 2;
        field8746 = 0.9f;
        field8747 = 0.1f;
        field8748 = 2.2f;
        field8749 = "minced";
        field8750 = "icons/world_render/glow.png";
        field8751 = 0.45f;
        field8752 = 0.2f;
        field8753 = 0.52f;
        field8754 = 0.16f;
        field8755 = 0.3f;
        field8756 = 0.19f;
        field8757 = 255.0f;
        field8758 = 255.0f;
        field8759 = 255.0f;
        field8760 = 255.0f;
        field8761 = 0.7853981633974483;
        field8762 = 0.9f;
        field8763 = 0.25f;
        field8764 = -0.5f;
        field8765 = -0.5f;
        field8766 = -0.5f;
        field8767 = 0.5f;
        field8768 = 0.5f;
        field8769 = 0.5f;
        field8770 = 0.5f;
        field8771 = -0.5f;
        field8620 = "Пентаграмма";
        field8622 = 0.01f;
        field8623 = 0.25f;
        field8624 = 0.05;
        field8625 = 100000L;
        field8626 = 1000.0f;
        field8627 = 1.3f;
        field8628 = 35.0f;
        field8629 = "ModelViewMat";
        field8630 = "ModelViewMat";
        field8631 = "ProjMat";
        field8632 = "ProjMat";
        field8633 = "u_Color";
        field8634 = "u_Color";
        field8635 = 255.0f;
        field8636 = 255.0f;
        field8637 = 255.0f;
        field8638 = 255.0f;
        field8639 = "u_Alpha";
        field8640 = "u_Alpha";
        field8641 = "u_HurtMix";
        field8642 = "u_HurtMix";
        field8643 = 1.35f;
        field8644 = 0.01f;
    }
}



