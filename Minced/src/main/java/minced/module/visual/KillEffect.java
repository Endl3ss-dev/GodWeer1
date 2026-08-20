/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventRender3D;
import minced.core.EventUpdate;
import minced.core.EventSwapWorld;
import minced.core.EventAttack;
import minced.core.Render2DBatch;
import minced.core.KillEffectParticle;
import minced.core.ThemeColors;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.LOAD;
import minced.enums.MAIN;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class KillEffect
extends Module {
    static String field5757 = "field5757";
    static String field5758 = "field5758";
    static Identifier field5759;
    MultiBooleanSetting field5760 = new MultiBooleanSetting(field5764, new BooleanSetting(field5765, true), new BooleanSetting(field5766, true), new BooleanSetting(field5767, true));
    static String field5761;
    static String field5762;
    static String field5763;
    static String field5764;
    static String field5765;
    static String field5766;
    static String field5767;
    static String field5768;
    static float field5769;
    static float field5770;
    static float field5771;
    static float field5772;
    SliderSetting field5773 = new SliderSetting(field5768, field5769, field5770, field5771, field5772);
    static String field5774;
    static float field5775;
    static float field5776;
    static float field5777;
    SliderSetting field5778 = new SliderSetting(field5774, field5775, field5776, 2.0f, field5777);
    static String field5779;
    static float field5780;
    static float field5781;
    static float field5782;
    static float field5783;
    SliderSetting field5784 = new SliderSetting(field5779, field5780, field5781, field5782, field5783, () -> Boolean.TRUE.equals(this.field5760.method1898(field5761)));
    static String field5785;
    static float field5786;
    static float field5787;
    static float field5788;
    static float field5789;
    SliderSetting field5790 = new SliderSetting(field5785, field5786, field5787, field5788, field5789, () -> Boolean.TRUE.equals(this.field5760.method1898(field5845)));
    static String field5791;
    static float field5792;
    static float field5793;
    static float field5794;
    static float field5795;
    SliderSetting field5796 = new SliderSetting(field5791, field5792, field5793, field5794, field5795, () -> Boolean.TRUE.equals(this.field5760.method1898(field5851)));
    List O = new ArrayList();
    int field5797 = -1;
    static float field5798;
    static float field5799;
    static float field5800;
    static float field5801;
    static float field5802;
    static float field5803;
    static float field5804;
    static float field5805;
    static float field5806;
    static float field5807;
    static float field5808;
    static float field5809;
    static float field5810;
    static double field5811;
    static double field5812;
    static float field5813;
    static float field5814;
    static float field5815;
    static float field5816;
    static float field5817;
    static float field5818;
    static float field5819;
    static float field5820;
    static float field5821;
    static float field5822;
    static float field5823;
    static float field5824;
    static double field5825;
    static float field5826;
    static float field5827;
    static float field5828;
    static double field5829;
    static float field5830;
    static float field5831;
    static float field5832;
    static float field5833;
    static double field5834;
    static float field5835;
    static float field5836;
    static float field5837;
    static float field5838;
    long field5839;
    Vec3d field5840;
    float field5841;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field5842;
    static String field5843;
    static String field5844;
    static String field5845;
    static long field5846;
    static float field5847;
    static float field5848;
    static float field5849;
    static float field5850;
    static String field5851;

    public static void method1386() {
        field5759 = Identifier.of((String)field5757, (String)field5758);
    }

    public KillEffect() {
        super(field5762, field5763, Combat.Visuals);
        this.addSettings(this.field5760, this.field5773, this.field5778, this.field5784, this.field5790, this.field5796);
    }

    private void method1388(KillEffectParticle param1, float param2, float param3, int param4, float param5, float param6) {
        float v7 = ((Float)this.field5784.getValue()).floatValue() * (field5798 + field5799 * MathHelper.sin((float)(param5 * field5800)));
        float v8 = field5801 + field5802 * (1.0f - param5);
        float v9 = v8 * field5803;
        float v10 = param5 * field5804 + param1.field9290;
        float v11 = field5805 + field5806 * MathHelper.sin((float)v10);
        float v12 = MathHelper.clamp((float)(param6 * field5807 * v11), (float)0.0f, (float)1.0f);
        float v13 = (float)ColorUtil.method1854(param4) / field5808;
        float v14 = (float)ColorUtil.method1846(param4) / field5809;
        float v15 = (float)ColorUtil.method1847(param4) / field5810;
        double v16 = param1.field9286.x;
        double v18 = param1.field9286.y;
        double v20 = param1.field9286.z;
        float v22 = v9 * param2;
        float v23 = v9 * param3;
        float v24 = v9 * -param3;
        float v25 = v9 * param2;
        this.method1391((float)(v16 - (double)v22), (float)(v18 + (double)v7), (float)(v20 - (double)v23), (float)(v16 + (double)v22), (float)(v18 + (double)v7), (float)(v20 + (double)v23), (float)(v16 + (double)v22), (float)v18, (float)(v20 + (double)v23), (float)(v16 - (double)v22), (float)v18, (float)(v20 - (double)v23), 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, v13, v14, v15, v12);
        this.method1391((float)(v16 - (double)v24), (float)(v18 + (double)v7), (float)(v20 - (double)v25), (float)(v16 + (double)v24), (float)(v18 + (double)v7), (float)(v20 + (double)v25), (float)(v16 + (double)v24), (float)v18, (float)(v20 + (double)v25), (float)(v16 - (double)v24), (float)v18, (float)(v20 - (double)v25), 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, v13, v14, v15, v12);
    }

    private void method1389(KillEffectParticle param1, Vector3f param2, Vector3f param3, int param4, float param5, float param6) {
        float v7 = ((Float)this.field5790.getValue()).floatValue();
        float v8 = (float)(param1.field9286.y + Math.max(field5811, (double)param1.field9288 * field5812));
        float v9 = field5813 + field5814 * (1.0f - param5);
        float v10 = MathHelper.clamp((float)param5, (float)0.0f, (float)1.0f);
        int v11 = ColorUtil.method1856(param4, 20);
        float v12 = param5 * field5815 + param1.field9290 * 2.0f;
        float v13 = field5816 + field5817 * MathHelper.sin((float)v12);
        float v14 = MathHelper.clamp((float)(param6 * v13), (float)0.0f, (float)1.0f);
        float v15 = (float)ColorUtil.method1854(v11) / field5818;
        float v16 = (float)ColorUtil.method1846(v11) / field5819;
        float v17 = (float)ColorUtil.method1847(v11) / field5820;
        double v18 = param1.field9286.x;
        double v20 = param1.field9286.z;
        for (int v22 = 0; v22 < 3; ++v22) {
            float v23 = v10 + (float)v22 * field5821;
            if (v23 > 1.0f) continue;
            float v24 = v8 + v23 * v7;
            float v25 = v9 * (1.0f - v23 * field5822);
            float v26 = (float)Math.toRadians(v23 * field5823 + param1.field9290 * field5824);
            float v27 = (float)Math.cos(v26);
            float v28 = (float)Math.sin(v26);
            float v29 = param2.x() * v27 + param3.x() * v28;
            float v30 = param2.y() * v27 + param3.y() * v28;
            float v31 = param2.z() * v27 + param3.z() * v28;
            float v32 = -param2.x() * v28 + param3.x() * v27;
            float v33 = -param2.y() * v28 + param3.y() * v27;
            float v34 = -param2.z() * v28 + param3.z() * v27;
            float v35 = v29 * v25;
            float v36 = v30 * v25;
            float v37 = v31 * v25;
            float v38 = v32 * v25;
            float v39 = v33 * v25;
            float v40 = v34 * v25;
            this.method1391((float)(v18 - (double)v35 - (double)v38), v24 - v36 - v39, (float)(v20 - (double)v37 - (double)v40), (float)(v18 - (double)v35 + (double)v38), v24 - v36 + v39, (float)(v20 - (double)v37 + (double)v40), (float)(v18 + (double)v35 + (double)v38), v24 + v36 + v39, (float)(v20 + (double)v37 + (double)v40), (float)(v18 + (double)v35 - (double)v38), v24 + v36 - v39, (float)(v20 + (double)v37 - (double)v40), 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, v15, v16, v17, v14);
        }
    }

    private void method1390(KillEffectParticle param1, int param2, float param3, float param4) {
        float v5 = ((Float)this.field5796.getValue()).floatValue();
        float v6 = 1.0f - param3;
        this.method1398((float)param1.field9286.x, (float)(param1.field9286.y + field5825), (float)param1.field9286.z, v5 * field5826, param1.field9290 * field5827 + param3 * field5828, ColorUtil.method1856(param2, 30), param4 * v6);
        this.method1398((float)param1.field9286.x, (float)(param1.field9286.y + field5829), (float)param1.field9286.z, v5 * field5830, -param1.field9290 * field5831 - param3 * field5832, param2, param4 * v6 * field5833);
        this.method1398((float)param1.field9286.x, (float)(param1.field9286.y + field5834), (float)param1.field9286.z, v5 * field5835, param1.field9290 * field5836 + param3 * field5837, param2, param4 * v6 * field5838);
    }

    private void method1391(float param1, float param2, float param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11, float param12, float param13, float param14, float param15, float param16, float param17, float param18, float param19, float param20, float param21, float param22, float param23, float param24) {
        Render2DBatch.method2653(param1, param2, param3, param4, param5, param6, param7, param8, param9, param10, param11, param12, param13, param14, param15, param16, param17, param18, param19, param20, param21, param22, param23, param24);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.O.clear();
        this.field5797 = -1;
        this.field5839 = 0L;
        this.field5840 = null;
        this.field5841 = 0.0f;
    }

    @EventHandler(2)
    private void method1392(EventRender3D param1) {
        if (this.O.isEmpty() || KillEffect.I.world == null) {
            return;
        }
        long v2 = System.currentTimeMillis();
        this.O.removeIf(param2 -> ((KillEffectParticle)param2).method2688(v2));
        if (this.O.isEmpty()) {
            return;
        }
        Camera v4 = KillEffect.I.gameRenderer.getCamera();
        Vec3d v5 = v4.getPos();
        int v6 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        MatrixStack v7 = param1.method2348();
        v7.push();
        v7.translate(-v5.x, -v5.y, -v5.z);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        RenderSystem.setShaderTexture((int)0, (Identifier)field5759);
        Matrix4f v8 = v7.peek().getPositionMatrix();
        Render2DBatch.method2657(v8);
        Quaternionf v9 = new Quaternionf();
        v9.rotateY((float)Math.toRadians(-v4.getYaw()));
        v9.rotateX((float)Math.toRadians(v4.getPitch()));
        Vector3f v10 = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f v11 = new Vector3f(0.0f, 1.0f, 0.0f);
        v9.transform(v10);
        v9.transform(v11);
        float v12 = (float)Math.toRadians(v4.getYaw());
        float v13 = (float)Math.cos(v12);
        float v14 = (float)Math.sin(v12);
        boolean v15 = Boolean.TRUE.equals(this.field5760.method1898(field5842));
        boolean v16 = Boolean.TRUE.equals(this.field5760.method1898(field5843));
        boolean v17 = Boolean.TRUE.equals(this.field5760.method1898(field5844));
        for (KillEffectParticle v19 : (Iterable<KillEffectParticle>)(this.O)) {
            float v20 = v19.method2689(v2);
            float v21 = MathHelper.clamp((float)((1.0f - v20) * ((Float)this.field5778.getValue()).floatValue()), (float)0.0f, (float)1.0f);
            if (v15) {
                this.method1388(v19, v13, v14, v6, v20, v21);
            }
            if (v16) {
                this.method1389(v19, v10, v11, v6, v20, v21);
            }
            if (!v17) continue;
            this.method1390(v19, v6, v20, v21);
        }
        Render2DBatch.method2675();
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        v7.pop();
    }

    @EventHandler(2)
    private void method1395(EventAttack param1) {
        if (KillEffect.I.world == null || KillEffect.I.player == null) {
            return;
        }
        Entity v2 = param1.method2471();
        if (!(v2 instanceof LivingEntity)) {
            return;
        }
        if (v2 == KillEffect.I.player) {
            return;
        }
        this.field5797 = v2.getId();
        this.field5839 = System.currentTimeMillis();
        this.field5840 = v2.getPos();
        this.field5841 = v2.getHeight();
    }

    @EventHandler(2)
    private void method1396(EventSwapWorld param1) {
        if (param1.method2421() == LOAD.CHANGED) {
            this.O.clear();
            this.field5797 = -1;
            this.field5840 = null;
        }
    }

    @EventHandler(2)
    private void method1397(EventUpdate param1) {
        if (KillEffect.I.world == null || this.field5797 == -1) {
            return;
        }
        long v2 = System.currentTimeMillis();
        if (v2 - this.field5839 > field5846) {
            this.field5797 = -1;
            this.field5840 = null;
            return;
        }
        Entity v4 = KillEffect.I.world.getEntityById(this.field5797);
        if (v4 == null) {
            if (this.field5840 != null) {
                this.O.add(new KillEffectParticle(this.field5840, this.field5841, v2, ((Float)this.field5773.getValue()).intValue()));
            }
            this.field5797 = -1;
            this.field5840 = null;
            return;
        }
        boolean v5 = false;
        if (v4 instanceof LivingEntity) {
            LivingEntity v6 = (LivingEntity)v4;
            if (v6.getHealth() <= 0.0f || !v6.isAlive()) {
                v5 = true;
            }
        } else if (!v4.isAlive()) {
            v5 = true;
        }
        if (v5) {
            if (this.field5840 != null) {
                this.O.add(new KillEffectParticle(this.field5840, this.field5841, v2, ((Float)this.field5773.getValue()).intValue()));
            }
            this.field5797 = -1;
            this.field5840 = null;
        } else {
            this.field5840 = v4.getPos();
            this.field5841 = v4.getHeight();
        }
    }

    private void method1398(float param1, float param2, float param3, float param4, float param5, int param6, float param7) {
        float v8 = MathHelper.clamp((float)param7, (float)0.0f, (float)1.0f);
        float v9 = (float)ColorUtil.method1854(param6) / field5847;
        float v10 = (float)ColorUtil.method1846(param6) / field5848;
        float v11 = (float)ColorUtil.method1847(param6) / field5849;
        float v12 = param4 * field5850;
        float v13 = (float)Math.cos(Math.toRadians(param5));
        float v14 = (float)Math.sin(Math.toRadians(param5));
        float v15 = param1 + (-v12 * v13 + v12 * v14);
        float v16 = param3 + (-v12 * v14 - v12 * v13);
        float v17 = param1 + (v12 * v13 + v12 * v14);
        float v18 = param3 + (v12 * v14 - v12 * v13);
        float v19 = param1 + (v12 * v13 - v12 * v14);
        float v20 = param3 + (v12 * v14 + v12 * v13);
        float v21 = param1 + (-v12 * v13 - v12 * v14);
        float v22 = param3 + (-v12 * v14 + v12 * v13);
        this.method1391(v15, param2, v16, v17, param2, v18, v19, param2, v20, v21, param2, v22, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, v9, v10, v11, v8);
    }

    static {
        field5762 = "Kill Effect";
        field5763 = "Отображает различные эффекты при убийстве игрока";
        field5764 = "Эффекты";
        field5765 = "Луч";
        field5766 = "Душа";
        field5767 = "Руны";
        field5768 = "Длительность, мс";
        field5769 = 1200.0f;
        field5770 = 400.0f;
        field5771 = 3500.0f;
        field5772 = 50.0f;
        field5774 = "Интенсивность";
        field5775 = 1.2f;
        field5776 = 0.1f;
        field5777 = 0.05f;
        field5779 = "Высота луча";
        field5780 = 10.0f;
        field5781 = 3.0f;
        field5782 = 25.0f;
        field5783 = 0.5f;
        field5785 = "Высота души";
        field5786 = 2.5f;
        field5787 = 0.6f;
        field5788 = 6.0f;
        field5789 = 0.1f;
        field5791 = "Размер рун";
        field5792 = 3.2f;
        field5793 = 0.8f;
        field5794 = 8.0f;
        field5795 = 0.1f;
        field5846 = 3000L;
        field5842 = "Луч";
        field5843 = "Душа";
        field5844 = "Руны";
        field5798 = 0.85f;
        field5799 = 0.15f;
        field5800 = (float)Math.PI;
        field5801 = 0.6f;
        field5802 = 0.4f;
        field5803 = 0.5f;
        field5804 = 6.0f;
        field5805 = 0.7f;
        field5806 = 0.3f;
        field5807 = 0.9f;
        field5808 = 255.0f;
        field5809 = 255.0f;
        field5810 = 255.0f;
        field5811 = 0.2;
        field5812 = 0.3;
        field5813 = 0.45f;
        field5814 = 0.25f;
        field5815 = 7.0f;
        field5816 = 0.7f;
        field5817 = 0.3f;
        field5818 = 255.0f;
        field5819 = 255.0f;
        field5820 = 255.0f;
        field5821 = 0.12f;
        field5822 = 0.4f;
        field5823 = 360.0f;
        field5824 = 180.0f;
        field5825 = 0.02;
        field5826 = 1.15f;
        field5827 = 30.0f;
        field5828 = 60.0f;
        field5829 = 0.022;
        field5830 = 0.85f;
        field5831 = 45.0f;
        field5832 = 90.0f;
        field5833 = 0.85f;
        field5834 = 0.024;
        field5835 = 0.55f;
        field5836 = 75.0f;
        field5837 = 120.0f;
        field5838 = 0.85f;
        field5847 = 255.0f;
        field5848 = 255.0f;
        field5849 = 255.0f;
        field5850 = 0.5f;
        field5851 = "Руны";
        field5845 = "Душа";
        field5761 = "Луч";
    }
}



