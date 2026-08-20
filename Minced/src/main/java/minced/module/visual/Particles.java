/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.ArrowEntity
 *  net.minecraft.entity.projectile.TridentEntity
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.util.Identifier
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import minced.core.ColorUtil;
import minced.core.MathUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventRender3D;
import minced.core.EventUpdate;
import minced.core.EventSwapWorld;
import minced.core.EventTotemPop;
import minced.core.EventAttack;
import minced.core.EventMotion;
import minced.core.ParticleSpawner;
import minced.core.Render2DBatch;
import minced.core.ThemeColors;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Bloom;
import minced.enums.Combat;
import minced.enums.MAIN;
import minced.manager.Theme;
import minced.mixin.accessor.IWorldRendererMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Particles
extends Module {
    static MinecraftClient I = MinecraftClient.getInstance();
    MultiBooleanSetting field6393 = new MultiBooleanSetting(field6506, new BooleanSetting(field6507, true), new BooleanSetting(field6508, false), new BooleanSetting(field6509, false), new BooleanSetting(field6510, false), new BooleanSetting(field6511, false), new BooleanSetting(field6512, false), new BooleanSetting(field6513, false));
    static String field6394;
    static double field6395;
    static double field6396;
    static double field6397;
    static double field6398;
    static double field6399;
    static double field6400;
    static double field6401;
    static float field6402;
    SliderSetting field6403;
    static float field6404;
    static float field6405;
    static float field6406;
    static float field6407;
    static float field6408;
    static float field6409;
    static float field6410;
    static float field6411;
    SliderSetting field6412;
    static double field6413;
    static float field6414;
    static float field6415;
    static float field6416;
    static double field6417;
    static float field6418;
    static float field6419;
    static float field6420;
    static float field6421;
    static float field6422;
    static float field6423;
    static double field6424;
    static float field6425;
    static float field6426;
    static float field6427;
    static float field6428;
    static float field6429;
    static double field6430;
    static float field6431;
    static String field6432;
    static String field6433;
    static String field6434;
    ArrayList field6435;
    static double field6436;
    static double field6437;
    static float field6438;
    static float field6439;
    static float field6440;
    static float field6441;
    static float field6442;
    static float field6443;
    static float field6444;
    static float field6445;
    static float field6446;
    static double field6447;
    static float field6448;
    static float field6449;
    static double field6450;
    static double field6451;
    static float field6452;
    static int field6453;
    static float field6454;
    static float field6455;
    static float field6456;
    static float field6457;
    static float field6458;
    BooleanSetting field6459 = new BooleanSetting(field6514, true);
    static float field6460;
    static float field6461;
    static float field6462;
    static float field6463;
    static String field6464;
    static double field6465;
    static float field6466;
    static float field6467;
    static float field6468;
    static float field6469;
    static float field6470;
    static float field6471;
    static float field6472;
    static float field6473;
    static float field6474;
    static float field6475;
    static float field6476;
    static float field6477;
    static float field6478;
    static float field6479;
    static float field6480;
    static float field6481;
    static float field6482;
    static double field6483;
    static float field6484;
    static float field6485;
    static float field6486;
    static float field6487;
    static float field6488;
    static double field6489;
    static float field6490;
    static float field6491;
    static double field6492;
    static float field6493;
    static float field6494;
    static double field6495;
    static float field6496;
    static float field6497;
    static double field6498;
    static double field6499;
    static float field6500;
    static double field6501;
    static double field6502;
    static double field6503;
    static String field6504;
    static String field6505;
    static String field6506;
    static String field6507;
    static String field6508;
    static String field6509;
    static String field6510;
    static String field6511;
    static String field6512;
    static String field6513;
    static String field6514;
    static String field6515;
    static float field6516;
    static float field6517;
    static float field6518;
    static String field6519;
    static float field6520;
    static float field6521;
    static float field6522;
    static String field6523;
    static float field6524;
    static float field6525;
    static float field6526;
    static float field6527;
    static float field6528;
    static float field6529;
    static float field6530;
    static double field6531;
    static float field6532;
    static float field6533;
    static float field6534;
    static float field6535;
    static float field6536;
    static float field6537;
    static float field6538;
    static float field6539;
    static float field6540;
    static float field6541;
    static float field6542;
    static float field6543;
    static float field6544;
    static double field6545;
    static String field6546;
    static double field6547;
    static float field6548;

    @EventHandler(2)
    private void method1523(EventMotion param1) {
        if (Particles.I.player == null || Particles.I.world == null) {
            return;
        }
        if (this.field6393.method1898(field6394).booleanValue()) {
            Vec3d v2 = Particles.I.player.getVelocity();
            double v3 = Math.sqrt(v2.x * v2.x + v2.z * v2.z);
            Vec3d v5 = v3 < field6395 ? Particles.I.player.getRotationVec(1.0f).multiply(field6396) : (Particles.I.player.isGliding() ? v2.normalize().multiply(field6397) : new Vec3d(-v2.x / v3, 0.0, -v2.z / v3));
            double v6 = (Particles.I.player.isGliding() ? field6398 : field6399) + (v3 > field6400 ? v3 * field6401 : 0.0);
            int v8 = (int)(field6402 * ((Float)this.field6403.getValue()).floatValue());
            for (int v9 = 0; v9 < v8; ++v9) {
                double v22;
                double v20;
                double v10 = v6 + (double)MathUtil.method1864(field6404, field6405);
                double v12 = MathUtil.method1864(field6406, field6407);
                double v14 = MathUtil.method1864(field6408, field6409);
                long v16 = (long)(MathUtil.method1864(field6410, field6411) * ((Float)this.field6412.getValue()).floatValue() / 2.0f);
                double v18 = Particles.I.player.getX() + v5.x * v10 + v12;
                if (!this.method1531(v18, v20 = Particles.I.player.isGliding() ? Particles.I.player.getY() + (double)Particles.I.player.getHeight() / field6413 + v5.y * v10 + (double)MathUtil.method1864(field6414, field6415) : Particles.I.player.getY() + (double)MathUtil.method1864(field6416, Particles.I.player.getHeight()), v22 = Particles.I.player.getZ() + v5.z * v10 + v14, field6417)) continue;
                float v24 = MathUtil.method1864(field6418, field6419);
                float v25 = MathUtil.method1864(field6420, field6421);
                float v26 = MathUtil.method1864(field6422, field6423);
                Vec3d v27 = v5.multiply(field6424).add(new Vec3d((double)(v24 * field6425), (double)(v25 * field6426), (double)(v26 * field6427)));
                this.method1538(v18, v20, v22, v27, this.method1524(), field6428, v16, field6429, field6430, field6431);
            }
        }
        for (Entity v3 : Particles.I.world.getEntities()) {
            EnderPearlEntity v4;
            if (this.field6393.method1898(field6432).booleanValue() && v3 instanceof EnderPearlEntity && !(v4 = (EnderPearlEntity)v3).isOnGround()) {
                this.method1527(v4.getPos());
            }
            if (this.field6393.method1898(field6433).booleanValue() && v3 instanceof TridentEntity && this.method1530(v3)) {
                this.method1527(v3.getPos());
            }
            if (!this.field6393.method1898(field6434).booleanValue() || !(v3 instanceof ArrowEntity) || !this.method1530(v3)) continue;
            this.method1527(v3.getPos());
        }
    }

    private int method1524() {
        return ThemeColors.method3199(MAIN.MODULE_VISUAL);
    }

    @Override
    public void onDisable() {
        this.field6435.clear();
        super.onDisable();
    }

    private void method1525(double param1, double param3, double param5, Vec3d param7, int param8, float param9, long param10, float param12, double param13) {
        Vec3d v15 = ParticleSpawner.method2554(param1, param3, param5, param9);
        if (v15 != null) {
            this.field6435.add(new ParticleSpawner(v15.x, v15.y, v15.z, param7, param8, param9, param10, param12, param13, 0.0f, this.method1526()));
        }
    }

    private Frustum cfr_renamed_0() {
        Frustum v1 = Particles.I.worldRenderer.getCapturedFrustum();
        return v1 != null ? v1 : ((IWorldRendererMixin)Particles.I.worldRenderer).getFrustum();
    }

    private int method1526() {
        Theme v1 = this.method1534();
        if (v1 == null || !v1.cfr_renamed_359()) {
            return 0;
        }
        return Math.random() < field6436 ? 1 : 0;
    }

    private void method1527(Vec3d param1) {
        if (!this.method1531(param1.x, param1.y, param1.z, field6437)) {
            return;
        }
        int v2 = (int)(field6438 * ((Float)this.field6403.getValue()).floatValue());
        int v3 = this.method1524();
        for (int v4 = 0; v4 < v2; ++v4) {
            double v5 = MathUtil.method1864(field6439, field6440);
            Vec3d v7 = new Vec3d(param1.x, param1.y + v5, param1.z);
            long v8 = (long)(MathUtil.method1864(field6441, field6442) * ((Float)this.field6412.getValue()).floatValue() / 2.0f);
            float v10 = MathUtil.method1864(field6443, field6444);
            float v11 = MathUtil.method1864(field6445, field6446);
            double v12 = MathUtil.method1864(v10, v11);
            double v14 = v12 * field6447;
            double v16 = Math.toRadians(MathUtil.method1864(0.0f, field6448));
            double v18 = Math.cos(v16) * v12;
            double v20 = Math.sin(v16) * v12;
            double v22 = MathUtil.method1864((float)(-v14), (float)v14);
            this.method1525(v7.x, v7.y, v7.z, new Vec3d(v18, v22, v20), v3, field6449, v8, 2.0f, field6450);
        }
    }

    private Identifier method1528(int param1) {
        Theme v2 = this.method1534();
        if (v2 == null) {
            return Bloom.Bloom.field1079;
        }
        return v2.cfr_renamed_0(param1);
    }

    @EventHandler(2)
    private void cfr_renamed_4(EventRender3D param1) {
        if (Particles.I.player == null || Particles.I.world == null) {
            return;
        }
        this.field6435.removeIf(param0 -> ((ParticleSpawner)param0).field9046.method3113(((ParticleSpawner)param0).field9047));
        if (this.field6435.isEmpty()) {
            return;
        }
        Camera v2 = Particles.I.gameRenderer.getCamera();
        Vec3d v3 = v2.getPos();
        Quaternionf v4 = new Quaternionf();
        v4.rotateY((float)Math.toRadians(-v2.getYaw()));
        v4.rotateX((float)Math.toRadians(v2.getPitch()));
        Vector3f v5 = new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f v6 = new Vector3f(0.0f, 1.0f, 0.0f);
        v4.transform(v5);
        v4.transform(v6);
        MatrixStack v7 = param1.method2348();
        v7.push();
        v7.translate(-v3.x, -v3.y, -v3.z);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate((int)770, (int)1, (int)0, (int)1);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask((boolean)false);
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.POSITION_TEX_COLOR);
        Matrix4f v8 = v7.peek().getPositionMatrix();
        HashMap<Identifier, List> v9 = new HashMap<Identifier, List>();
        for (ParticleSpawner clsClass426 : (Iterable<ParticleSpawner>)(this.field6435)) {
            clsClass426.method2553();
            if (!this.method1531(clsClass426.field9050.x, clsClass426.field9050.y, clsClass426.field9050.z, (double)clsClass426.field9051 * field6451)) continue;
            v9.computeIfAbsent(this.method1528(clsClass426.field9063), param0 -> new ArrayList()).add(clsClass426);
        }
        for (Map.Entry entry : v9.entrySet()) {
            RenderSystem.setShaderTexture((int)0, (Identifier)((Identifier)entry.getKey()));
            Render2DBatch.method2657(v8);
            for (ParticleSpawner v13 : (Iterable<ParticleSpawner>)((List)entry.getValue())) {
                float v14 = v13.field9051 * field6452;
                Vec3d v15 = new Vec3d((double)v5.x(), (double)v5.y(), (double)v5.z()).multiply((double)v14);
                Vec3d v16 = new Vec3d((double)v6.x(), (double)v6.y(), (double)v6.z()).multiply((double)v14);
                Vec3d v17 = v13.field9050;
                Vec3d v18 = v17.subtract(v15).subtract(v16);
                Vec3d v19 = v17.add(v15).subtract(v16);
                Vec3d v20 = v17.add(v15).add(v16);
                Vec3d v21 = v17.subtract(v15).add(v16);
                int v22 = v13.field9062 & field6453 | (int)(v13.field9061 * field6454) << 24;
                float v23 = (float)(v22 >> 16 & 0xFF) / field6455;
                float v24 = (float)(v22 >> 8 & 0xFF) / field6456;
                float v25 = (float)(v22 & 0xFF) / field6457;
                float v26 = (float)(v22 >> 24 & 0xFF) / field6458;
                Render2DBatch.method2653((float)v18.x, (float)v18.y, (float)v18.z, (float)v19.x, (float)v19.y, (float)v19.z, (float)v20.x, (float)v20.y, (float)v20.z, (float)v21.x, (float)v21.y, (float)v21.z, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, v23, v24, v25, v26);
                if (!((Boolean)this.field6459.getValue()).booleanValue()) continue;
                int v27 = ColorUtil.method1856(v22, 90);
                float v28 = (float)(v27 >> 16 & 0xFF) / field6460;
                float v29 = (float)(v27 >> 8 & 0xFF) / field6461;
                float v30 = (float)(v27 & 0xFF) / field6462;
                float v31 = (float)(v27 >> 24 & 0xFF) / field6463;
                Render2DBatch.method2653((float)v18.x, (float)v18.y, (float)v18.z, (float)v19.x, (float)v19.y, (float)v19.z, (float)v20.x, (float)v20.y, (float)v20.z, (float)v21.x, (float)v21.y, (float)v21.z, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, v28, v29, v30, v31);
            }
            Render2DBatch.method2675();
        }
        RenderSystem.depthMask((boolean)true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        v7.pop();
    }

    @EventHandler(2)
    private void cfr_renamed_0(EventTotemPop param1) {
        if (Particles.I.player == null || Particles.I.world == null) {
            return;
        }
        if (!this.field6393.method1898(field6464).booleanValue() || param1.method2450() == Particles.I.player) {
            return;
        }
        Entity v2 = param1.method2450();
        if (v2 == null) {
            return;
        }
        Vec3d v3 = v2.getPos().add(0.0, (double)v2.getHeight() / field6465, 0.0);
        int v4 = (int)(field6466 * ((Float)this.field6403.getValue()).floatValue());
        int v5 = this.method1524();
        for (int v6 = 0; v6 < v4; ++v6) {
            double v7 = v3.x + (double)MathUtil.method1864(field6467, field6468);
            double v9 = v3.y + (double)MathUtil.method1864(field6469, field6470);
            double v11 = v3.z + (double)MathUtil.method1864(field6471, field6472);
            long v13 = (long)(MathUtil.method1864(field6473, field6474) * ((Float)this.field6412.getValue()).floatValue() / 2.0f);
            double v15 = MathUtil.method1864(field6475, field6476);
            double v17 = MathUtil.method1864(field6477, field6478);
            double v19 = MathUtil.method1864(field6479, field6480);
            this.method1525(v7, v9, v11, new Vec3d(v15, v17, v19), v5, field6481, v13, field6482, field6483);
        }
    }

    private void method1529(Vec3d param1) {
        double v2 = MathUtil.method1864(field6484, field6485);
        double v4 = Math.toRadians(MathUtil.method1864(0.0f, field6486));
        double v6 = MathUtil.method1864(field6487, field6488);
        Vec3d v8 = new Vec3d(Math.cos(v4) * v2, v6, Math.sin(v4) * v2);
        Vec3d v9 = param1.add(v8);
        if (!this.method1531(v9.x, v9.y, v9.z, field6489)) {
            return;
        }
        long v10 = (long)(MathUtil.method1864(field6490, field6491) * ((Float)this.field6412.getValue()).floatValue() / 2.0f);
        double v12 = Math.random() < field6492 ? (double)MathUtil.method1864(field6493, field6494) : field6495;
        double v14 = Math.toRadians(MathUtil.method1864(0.0f, field6496));
        float v16 = field6497;
        Vec3d v17 = new Vec3d(Math.cos(v14) * v12, (double)MathUtil.method1864((float)(-v12 * field6498), (float)(v12 * field6499)), Math.sin(v14) * v12);
        this.method1525(v9.x, v9.y, v9.z, v17, this.method1524(), field6500, v10, v16, field6501);
    }

    private boolean method1530(Entity param1) {
        if (param1.isOnGround() || param1.getVelocity().lengthSquared() <= field6502) {
            return false;
        }
        Vec3d v2 = param1.getPos();
        Vec3d v3 = param1.getVelocity().normalize().multiply(field6503);
        BlockPos v4 = BlockPos.ofFloored((Position)v2);
        BlockPos v5 = BlockPos.ofFloored((Position)v2.add(v3));
        return Particles.I.world.getBlockState(v4).isAir() && Particles.I.world.getBlockState(v5).isAir();
    }

    private boolean method1531(double param1, double param3, double param5, double param7) {
        Frustum v9 = this.cfr_renamed_0();
        if (v9 == null) {
            return true;
        }
        return v9.isVisible(new Box(param1 - param7, param3 - param7, param5 - param7, param1 + param7, param3 + param7, param5 + param7));
    }

    public Particles() {
        super(field6504, field6505, Combat.Visuals);
        this.field6403 = new SliderSetting(field6515, 1.0f, field6516, field6517, field6518);
        this.field6412 = new SliderSetting(field6519, 2.0f, field6520, field6521, field6522);
        this.field6435 = new ArrayList();
        this.addSettings(this.field6393, this.field6459, this.field6403, this.field6412);
    }

    @EventHandler(2)
    private void method1532(EventAttack param1) {
        if (Particles.I.player == null || Particles.I.world == null) {
            return;
        }
        if (!this.field6393.method1898(field6523).booleanValue() || param1.method2471() == null) {
            return;
        }
        Entity v2 = param1.method2471();
        int v3 = (int)(field6524 * ((Float)this.field6403.getValue()).floatValue());
        int v4 = this.method1524();
        this.field6435.ensureCapacity(this.field6435.size() + v3);
        for (int v5 = 0; v5 < v3; ++v5) {
            double v10;
            double v8;
            double v6 = v2.getX() + (double)MathUtil.method1864(field6525, field6526);
            if (!this.method1531(v6, v8 = v2.getY() + (double)MathUtil.method1864(field6527, v2.getHeight() + field6528), v10 = v2.getZ() + (double)MathUtil.method1864(field6529, field6530), field6531)) continue;
            float v12 = MathUtil.method1864(field6532, field6533) * 2.0f;
            float v13 = MathUtil.method1864(field6534, field6535);
            float v14 = MathUtil.method1864(field6536, field6537) * 2.0f;
            float v15 = field6538;
            long v16 = (long)(MathUtil.method1864(field6539, field6540) * ((Float)this.field6412.getValue()).floatValue() / 2.0f);
            Vec3d v18 = new Vec3d((double)(v12 * field6541), (double)(v13 * field6542), (double)(v14 * field6543));
            this.method1525(v6, v8, v10, v18, v4, field6544, v16, v15, field6545);
        }
    }

    private Theme method1534() {
        if (Minced.getInstance() == null) {
            return null;
        }
        return Minced.getInstance().getThemeManager();
    }

    @EventHandler(2)
    private void method1535(EventUpdate param1) {
        if (Particles.I.player == null || Particles.I.world == null) {
            return;
        }
        if (!this.field6393.method1898(field6546).booleanValue()) {
            return;
        }
        Vec3d v2 = new Vec3d(Particles.I.player.getX(), Particles.I.player.getY() + (double)Particles.I.player.getHeight() / field6547, Particles.I.player.getZ());
        int v3 = (int)(field6548 * ((Float)this.field6403.getValue()).floatValue());
        this.field6435.ensureCapacity(this.field6435.size() + v3);
        for (int v4 = 0; v4 < v3; ++v4) {
            this.method1529(v2);
        }
    }

    @EventHandler(2)
    private void method1536(EventSwapWorld param1) {
        this.field6435.clear();
    }

    private void method1538(double param1, double param3, double param5, Vec3d param7, int param8, float param9, long param10, float param12, double param13, float param15) {
        Vec3d v16 = ParticleSpawner.method2554(param1, param3, param5, param9);
        if (v16 != null) {
            this.field6435.add(new ParticleSpawner(v16.x, v16.y, v16.z, param7, param8, param9, param10, param12, param13, param15, this.method1526()));
        }
    }

    static {
        field6504 = "Particles";
        field6505 = "Кастомные партиклы в мире";
        field6506 = "Добавлять при";
        field6507 = "Бездействии";
        field6508 = "Беге";
        field6509 = "Атаке";
        field6510 = "Падении перла";
        field6511 = "Падении трезубца";
        field6512 = "Падении стрелы";
        field6513 = "Сносе тотема";
        field6514 = "Свечение";
        field6515 = "Количество";
        field6516 = 0.1f;
        field6517 = 3.0f;
        field6518 = 0.1f;
        field6519 = "Время жизни (сек)";
        field6520 = 0.3f;
        field6521 = 10.0f;
        field6522 = 0.1f;
        field6451 = 0.5;
        field6452 = 0.5f;
        field6453 = 0xFFFFFF;
        field6454 = 255.0f;
        field6455 = 255.0f;
        field6456 = 255.0f;
        field6457 = 255.0f;
        field6458 = 255.0f;
        field6460 = 255.0f;
        field6461 = 255.0f;
        field6462 = 255.0f;
        field6463 = 255.0f;
        field6394 = "Беге";
        field6395 = 0.01;
        field6396 = -1.0;
        field6397 = -1.0;
        field6398 = 0.8;
        field6399 = 0.35;
        field6400 = 0.1;
        field6401 = 0.6;
        field6402 = 6.0f;
        field6404 = -0.1f;
        field6405 = 0.1f;
        field6406 = -0.2f;
        field6407 = 0.2f;
        field6408 = -0.2f;
        field6409 = 0.2f;
        field6410 = 600.0f;
        field6411 = 1100.0f;
        field6413 = 2.0;
        field6414 = -0.25f;
        field6415 = 0.25f;
        field6416 = 0.1f;
        field6417 = 0.5;
        field6418 = -0.3f;
        field6419 = 0.3f;
        field6420 = -0.1f;
        field6421 = 0.5f;
        field6422 = -0.3f;
        field6423 = 0.3f;
        field6424 = 0.04;
        field6425 = 0.03f;
        field6426 = 0.03f;
        field6427 = 0.03f;
        field6428 = 0.25f;
        field6429 = 2.5f;
        field6430 = 0.001f;
        field6431 = 0.3f;
        field6432 = "Падении перла";
        field6433 = "Падении трезубца";
        field6434 = "Падении стрелы";
        field6502 = 1.0E-4;
        field6503 = 0.5;
        field6437 = 0.5;
        field6438 = 4.0f;
        field6439 = 0.1f;
        field6440 = 0.35f;
        field6441 = 2000.0f;
        field6442 = 2500.0f;
        field6443 = 0.015f;
        field6444 = 0.0375f;
        field6445 = 0.05f;
        field6446 = 0.075f;
        field6447 = 0.4;
        field6448 = 360.0f;
        field6449 = 0.3f;
        field6450 = 5.0E-5f;
        field6546 = "Бездействии";
        field6547 = 2.0;
        field6548 = 10.0f;
        field6484 = 7.0f;
        field6485 = 40.0f;
        field6486 = 360.0f;
        field6487 = -7.0f;
        field6488 = 25.0f;
        field6489 = 0.5;
        field6490 = 1500.0f;
        field6491 = 2000.0f;
        field6492 = 0.8;
        field6493 = 0.015f;
        field6494 = 0.03f;
        field6495 = 0.125;
        field6496 = 360.0f;
        field6497 = 3.0f;
        field6498 = 0.1f;
        field6499 = 0.1f;
        field6500 = 0.4f;
        field6501 = 5.0E-6f;
        field6523 = "Атаке";
        field6524 = 50.0f;
        field6525 = -0.4f;
        field6526 = 0.4f;
        field6527 = -0.4f;
        field6528 = 0.4f;
        field6529 = -0.4f;
        field6530 = 0.4f;
        field6531 = 0.5;
        field6532 = -0.8f;
        field6533 = 0.8f;
        field6534 = -0.4f;
        field6535 = 1.3f;
        field6536 = -0.8f;
        field6537 = 0.8f;
        field6538 = 0.75f;
        field6539 = 1000.0f;
        field6540 = 1250.0f;
        field6541 = 0.07f;
        field6542 = 0.07f;
        field6543 = 0.07f;
        field6544 = 0.25f;
        field6545 = 5.0E-4f;
        field6464 = "Сносе тотема";
        field6465 = 2.0;
        field6466 = 30.0f;
        field6467 = -0.5f;
        field6468 = 0.5f;
        field6469 = -0.5f;
        field6470 = 0.5f;
        field6471 = -0.5f;
        field6472 = 0.5f;
        field6473 = 1500.0f;
        field6474 = 2000.0f;
        field6475 = -0.05f;
        field6476 = 0.05f;
        field6477 = 0.02f;
        field6478 = 0.08f;
        field6479 = -0.05f;
        field6480 = 0.05f;
        field6481 = 0.3f;
        field6482 = 0.6f;
        field6483 = 2.0E-4;
        field6436 = 0.5;
    }
}



