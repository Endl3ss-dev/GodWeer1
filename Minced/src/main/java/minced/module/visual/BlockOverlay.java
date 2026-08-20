/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
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
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.shape.VoxelShape
 *  net.minecraft.world.BlockView
 *  org.joml.Matrix4f
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.BooleanSetting;
import minced.core.ModeSetting;
import minced.core.Shaders;
import minced.core.EventRender3D;
import minced.core.ThemeColors;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;

public class BlockOverlay
extends Module {
    Box field3683 = null;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field3684;
    static float field3685;
    static float field3686;
    static float field3687;
    static float field3688;
    static float field3689;
    static float field3690;
    static float field3691;
    static float field3692;
    static float field3693;
    static float field3694;
    static String field3695 = "Block Overlay";
    static String field3696 = "Подсвечивает блок под прицелом";
    static String field3697 = "Mode";
    static String field3698 = "Обычный";
    static String field3699 = "Обычный";
    static String field3700 = "Шейдер";
    ModeSetting field3701 = new ModeSetting(field3697, field3698, field3699, field3700);
    static String field3702 = "Видеть сквозь блоки";
    BooleanSetting field3703 = new BooleanSetting(field3702, false);
    static String field3704 = "Плавность";
    BooleanSetting field3705 = new BooleanSetting(field3704, true, () -> (Boolean)this.field3703.getValue());
    public static BlockOverlay field3706;
    static double field3707;
    static double field3708;
    static double field3709;
    static double field3710;
    static double field3711;
    static double field3712;
    static String field3713;
    static String field3714;
    static String field3715;
    static String field3716;
    static long field3717;
    static float field3718;
    static String field3719;
    static String field3720;
    static String field3721;
    static float field3722;
    static float field3723;
    static float field3724;
    static String field3725;
    static double field3726;

    @Override
    public void onDisable() {
        this.field3683 = null;
        super.onDisable();
    }

    private void method1028(Box param1, int param2, float param3, boolean param4, boolean param5, boolean param6, MatrixStack param7) {
        float v19;
        float v18;
        float v17;
        Vec3d v8 = BlockOverlay.I.getEntityRenderDispatcher().camera.getPos();
        param1 = param1.expand(field3684);
        float v9 = (float)(param1.minX - v8.x);
        float v10 = (float)(param1.minY - v8.y);
        float v11 = (float)(param1.minZ - v8.z);
        float v12 = (float)(param1.maxX - v8.x);
        float v13 = (float)(param1.maxY - v8.y);
        float v14 = (float)(param1.maxZ - v8.z);
        Matrix4f v15 = param7.peek().getPositionMatrix();
        if (param5) {
            int v16 = ColorUtil.method1855(param2, (int)((float)ColorUtil.cfr_renamed_0(param2) * field3685));
            v17 = (float)ColorUtil.method1854(v16) / field3686;
            v18 = (float)ColorUtil.method1846(v16) / field3687;
            v19 = (float)ColorUtil.method1847(v16) / field3688;
            float v20 = (float)ColorUtil.cfr_renamed_0(v16) / field3689;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            if (param6) {
                RenderSystem.enableDepthTest();
            } else {
                RenderSystem.disableDepthTest();
            }
            RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_COLOR);
            BufferBuilder v21 = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            v21.vertex(v15, v9, v10, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v10, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v10, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v10, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v13, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v13, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v13, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v13, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v10, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v13, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v13, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v10, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v10, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v10, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v13, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v13, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v10, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v10, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v13, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v9, v13, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v10, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v13, v11).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v13, v14).color(v17, v18, v19, v20);
            v21.vertex(v15, v12, v10, v14).color(v17, v18, v19, v20);
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v21.end());
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
        if (param4) {
            float v16 = (float)ColorUtil.method1854(param2) / field3690;
            v17 = (float)ColorUtil.method1846(param2) / field3691;
            v18 = (float)ColorUtil.method1847(param2) / field3692;
            v19 = (float)ColorUtil.cfr_renamed_0(param2) / field3693;
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            if (param6) {
                RenderSystem.enableDepthTest();
                RenderSystem.depthMask((boolean)false);
            } else {
                RenderSystem.disableDepthTest();
            }
            RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.RENDERTYPE_LINES);
            RenderSystem.lineWidth((float)param3);
            BufferBuilder v20 = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
            this.method1029(v20, param7, v15, v9, v10, v11, v12, v10, v11, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v12, v10, v11, v12, v10, v14, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v12, v10, v14, v9, v10, v14, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v9, v10, v14, v9, v10, v11, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v9, v10, v14, v9, v13, v14, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v9, v10, v11, v9, v13, v11, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v12, v10, v14, v12, v13, v14, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v12, v10, v11, v12, v13, v11, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v9, v13, v11, v12, v13, v11, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v12, v13, v11, v12, v13, v14, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v12, v13, v14, v9, v13, v14, v16, v17, v18, v19);
            this.method1029(v20, param7, v15, v9, v13, v14, v9, v13, v11, v16, v17, v18, v19);
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v20.end());
            if (param6) {
                RenderSystem.depthMask((boolean)true);
            }
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    private void method1029(BufferBuilder param1, MatrixStack param2, Matrix4f param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11, float param12, float param13) {
        float v14 = param7 - param4;
        float v15 = param8 - param5;
        float v16 = param9 - param6;
        float v17 = (float)Math.sqrt(v14 * v14 + v15 * v15 + v16 * v16);
        float v18 = 0.0f;
        float v19 = 1.0f;
        float v20 = 0.0f;
        if (v17 > field3694) {
            v18 = v14 / v17;
            v19 = v15 / v17;
            v20 = v16 / v17;
        }
        param1.vertex(param3, param4, param5, param6).color(param10, param11, param12, param13).normal(param2.peek(), v18, v19, v20);
        param1.vertex(param3, param7, param8, param9).color(param10, param11, param12, param13).normal(param2.peek(), v18, v19, v20);
    }

    public BlockOverlay() {
        super(field3695, field3696, Combat.Visuals);
        field3706 = this;
        this.addSettings(this.field3701, this.field3703, this.field3705);
    }

    @EventHandler(2)
    public void method1030(EventRender3D param1) {
        BlockHitResult v2;
        if (!this.isEnabled() || BlockOverlay.I.world == null || BlockOverlay.I.player == null) {
            this.field3683 = null;
            return;
        }
        HitResult v3 = BlockOverlay.I.crosshairTarget;
        if (!(v3 instanceof BlockHitResult) || (v2 = (BlockHitResult)v3).getType() != HitResult.Type.BLOCK) {
            this.field3683 = null;
            return;
        }
        BlockPos v3_pos = v2.getBlockPos();
        if (v3_pos == null) {
            this.field3683 = null;
            return;
        }
        VoxelShape v4 = BlockOverlay.I.world.getBlockState(v3_pos).getOutlineShape((BlockView)BlockOverlay.I.world, v3_pos);
        if (v4.isEmpty()) {
            this.field3683 = null;
            return;
        }
        Box v5 = v4.getBoundingBox().offset(v3_pos);
        this.field3683 = this.field3683 == null || (Boolean)this.field3705.getValue() == false || (Boolean)this.field3703.getValue() == false ? v5 : new Box(MathUtil.method1865(this.field3683.minX, v5.minX, field3707), MathUtil.method1865(this.field3683.minY, v5.minY, field3708), MathUtil.method1865(this.field3683.minZ, v5.minZ, field3709), MathUtil.method1865(this.field3683.maxX, v5.maxX, field3710), MathUtil.method1865(this.field3683.maxY, v5.maxY, field3711), MathUtil.method1865(this.field3683.maxZ, v5.maxZ, field3712));
        int v6 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        if (this.field3701.isMode(field3713)) {
            this.method1028(this.field3683, v6, 2.0f, true, true, (Boolean)this.field3703.getValue() == false, param1.method2348());
        } else if (this.field3701.isMode(field3714)) {
            float v11;
            Vec3d v7 = BlockOverlay.I.getEntityRenderDispatcher().camera.getPos();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableCull();
            if (((Boolean)this.field3703.getValue()).booleanValue()) {
                RenderSystem.disableDepthTest();
            } else {
                RenderSystem.enableDepthTest();
            }
            ShaderProgram v8 = RenderSystem.setShader((ShaderProgramKey)Shaders.field8359);
            if (v8 != null) {
                if (v8.getUniform(field3715) != null) {
                    v8.getUniform(field3716).set((float)(System.currentTimeMillis() % field3717) / field3718);
                }
                if (v8.getUniform(field3719) != null) {
                    v8.getUniform(field3720).set((float)I.getWindow().getWidth(), (float)I.getWindow().getHeight());
                }
                if (v8.getUniform(field3721) != null) {
                    float v9 = (float)(v6 >> 16 & 0xFF) / field3722;
                    float v10 = (float)(v6 >> 8 & 0xFF) / field3723;
                    v11 = (float)(v6 & 0xFF) / field3724;
                    v8.getUniform(field3725).set(v9, v10, v11);
                }
            }
            Tessellator v9 = Tessellator.getInstance();
            Box v10 = this.field3683.offset(-v7.x, -v7.y, -v7.z).expand(field3726);
            v11 = (float)v10.minX;
            float v12 = (float)v10.minY;
            float v13 = (float)v10.minZ;
            float v14 = (float)v10.maxX;
            float v15 = (float)v10.maxY;
            float v16 = (float)v10.maxZ;
            BufferBuilder v17 = v9.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            Matrix4f v18 = param1.method2348().peek().getPositionMatrix();
            v17.vertex(v18, v11, v12, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v12, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v12, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v12, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v15, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v15, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v15, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v15, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v12, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v15, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v15, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v12, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v12, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v12, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v15, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v15, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v12, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v12, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v15, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v11, v15, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v12, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v15, v13).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v15, v16).color(255, 255, 255, 255);
            v17.vertex(v18, v14, v12, v16).color(255, 255, 255, 255);
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v17.end());
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.disableBlend();
        }
    }

    static {
        field3707 = 15.0;
        field3708 = 15.0;
        field3709 = 15.0;
        field3710 = 15.0;
        field3711 = 15.0;
        field3712 = 15.0;
        field3713 = "Обычный";
        field3714 = "Шейдер";
        field3715 = "time";
        field3716 = "time";
        field3717 = 1000000L;
        field3718 = 1000.0f;
        field3719 = "resolution";
        field3720 = "resolution";
        field3721 = "themeColor";
        field3722 = 255.0f;
        field3723 = 255.0f;
        field3724 = 255.0f;
        field3725 = "themeColor";
        field3726 = 0.005;
        field3684 = 0.001;
        field3685 = 0.1f;
        field3686 = 255.0f;
        field3687 = 255.0f;
        field3688 = 255.0f;
        field3689 = 255.0f;
        field3690 = 255.0f;
        field3691 = 255.0f;
        field3692 = 255.0f;
        field3693 = 255.0f;
        field3694 = 1.0E-6f;
    }
}



