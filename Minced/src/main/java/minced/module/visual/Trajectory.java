/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  it.unimi.dsi.fastutil.objects.Object2IntMap$Entry
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gl.ShaderProgramKey
 *  net.minecraft.client.gl.ShaderProgramKeys
 *  net.minecraft.client.render.BufferBuilder
 *  net.minecraft.client.render.BufferRenderer
 *  net.minecraft.client.render.BuiltBuffer
 *  net.minecraft.client.render.Tessellator
 *  net.minecraft.client.render.VertexFormat$DrawMode
 *  net.minecraft.client.render.VertexFormats
 *  net.minecraft.client.util.math.MatrixStack
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.ItemEnchantmentsComponent
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.ProjectileUtil
 *  net.minecraft.item.BowItem
 *  net.minecraft.item.CrossbowItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.particle.DustParticleEffect
 *  net.minecraft.particle.ParticleEffect
 *  net.minecraft.predicate.entity.EntityPredicates
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.util.Arm
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.EntityHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector2f
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 *  sLM.4ZXYEo
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.lang.invoke.CallSite;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.ProjectionUtil;
import minced.core.EventRender3D;
import minced.core.Render2DBatch;
import minced.core.TrajectoryPoint;
import minced.core.EventRender2D;
import minced.core.ThemeColors;
import minced.core.Fonts;
import minced.core.RenderUtil;
import minced.core.Module;
import minced.enums.Combat;
import minced.enums.MAIN;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderProgramKey;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.BowItem;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Arm;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import sLM._4ZXYEo;

public class Trajectory
extends Module {
    static String вь;

    static String field6267 = "Trajectory";
    static String field6268 = "Показывает траекторию бросаемых предметов";
    static String field6269 = "Предметы";
    static String field6270 = "Лук";
    static String field6271 = "Арбалет";
    static String field6272 = "Эндер Пёрл";
    static String field6273 = "Снежок";
    static String field6274 = "Яйцо";
    static String field6275 = "Зелье";
    static String field6276 = "Опыт";
    static String field6277 = "Трезубец";
    MultiBooleanSetting field6278 = new MultiBooleanSetting(field6269, new BooleanSetting(field6270, true), new BooleanSetting(field6271, true), new BooleanSetting(field6272, true), new BooleanSetting(field6273, true), new BooleanSetting(field6274, true), new BooleanSetting(field6275, true), new BooleanSetting(field6276, true), new BooleanSetting(field6277, true));
    static String field6279 = "Время падения";
    BooleanSetting field6280 = new BooleanSetting(field6279, false);
    static String field6281 = "Партиклы зоны сплеша";
    BooleanSetting field6282 = new BooleanSetting(field6281, false, () -> Boolean.TRUE.equals(this.field6278.method1898(field6353)));
    List<TrajectoryPoint> field6283 = new ArrayList<>();
    int field6284 = -1;
    static float field6285;
    static float field6286;
    static float field6287;
    static float field6288;
    static float field6289;
    static float field6290;
    static double field6291;
    static double field6292;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field6293;
    static float field6294;
    static float field6295;
    static DecimalFormat field6296;
    static float field6297;
    static float field6298;
    static float field6299;
    static float field6300;
    static float field6301;
    static float field6302;
    static float field6303;
    static float field6304;
    static String field6305;
    static float field6306;
    static float field6307;
    static float field6308;
    static float field6309;
    static String field6310;
    static float field6311;
    static float field6312;
    static float field6313;
    static double field6314;
    static float field6315;
    static double field6316;
    static double field6317;
    static double field6318;
    static float field6319;
    static float field6320;
    static double field6321;
    static double field6322;
    static double field6323;
    static float field6324;
    static double field6325;
    static String field6326;
    static String field6327;
    static String field6328;
    static float field6329;
    static float field6330;
    static String field6331;
    static float field6332;
    static float field6333;
    static String field6334;
    static float field6335;
    static float field6336;
    static String field6337;
    static float field6338;
    static float field6339;
    static float field6340;
    static String field6341;
    static float field6342;
    static float field6343;
    static float field6344;
    static float field6345;
    static float field6346;
    static float field6347;
    static double field6348;
    static float field6349;
    static double field6350;
    static String field6351;
    static float field6352;
    static String field6353;
    static double field6354;
    static double field6355;
    static double field6356;
    static int field6357;
    static int field6358;
    static float field6359;
    static double field6360;
    static double field6361;
    static double field6362;
    static double field6363;
    static double field6364;
    static double field6365;
    static double field6366;
    static double field6367;
    static double field6368;
    static double field6369;
    static double field6370;
    static double field6371;
    static float field6372;
    static double field6373;
    static float field6374;
    static float field6375;
    static double field6376;
    static double field6377;
    static float field6378;
    static float field6379;
    static float field6380;
    static float field6381;
    static float field6382;
    static float field6383;
    static float field6384;
    static float field6385;
    static float field6386;
    static float field6387;
    static float field6388;
    static float field6389;
    static float field6390;
    static String field6391;
    static int field6392;

    public Trajectory() {
        super(field6267, field6268, Combat.Visuals);
        this.addSettings(this.field6278, this.field6280, this.field6282);
    }

    @Override
    public void onDisable() {
        this.field6283 = Collections.emptyList();
        this.field6284 = -1;
        super.onDisable();
    }

    private Vector3f method1504(float param1, float param2, float param3, float param4, float param5, float param6) {
        float v7 = param4 - param1;
        float v8 = param5 - param2;
        float v9 = param6 - param3;
        float v10 = (float)Math.sqrt(v7 * v7 + v8 * v8 + v9 * v9);
        if (v10 < field6285) {
            return new Vector3f(0.0f, 1.0f, 0.0f);
        }
        return new Vector3f(v7 / v10, v8 / v10, v9 / v10);
    }

    private void method1505(MatrixStack param1, BufferBuilder param2, Vec3d param3, float param4, Vec3d param5, int param6) {
        Matrix4f v7 = param1.peek().getPositionMatrix();
        float v8 = (float)ColorUtil.method1854(param6) / field6286;
        float v9 = (float)ColorUtil.method1846(param6) / field6287;
        float v10 = (float)ColorUtil.method1847(param6) / field6288;
        float v11 = (float)ColorUtil.cfr_renamed_0(param6) / field6289;
        Vector3f v12 = this.method1504((float)param3.x, (float)param3.y, (float)param3.z, (float)(param3.x + param5.x), (float)(param3.y + param5.y), (float)(param3.z + param5.z));
        Vector3f v13 = Math.abs(v12.y()) < field6290 ? new Vector3f(0.0f, 1.0f, 0.0f) : new Vector3f(1.0f, 0.0f, 0.0f);
        Vector3f v14 = new Vector3f((Vector3fc)v13).cross((Vector3fc)v12).normalize();
        Vector3f v15 = new Vector3f((Vector3fc)v12).cross((Vector3fc)v14).normalize();
        for (int v17 = 0; v17 < 64; ++v17) {
            double v18 = field6291 * (double)v17 / (double)64;
            double v20 = field6292 * (double)(v17 + 1) / (double)64;
            float v22 = (float)Math.cos(v18);
            float v23 = (float)Math.sin(v18);
            float v24 = (float)Math.cos(v20);
            float v25 = (float)Math.sin(v20);
            Vector3f v26 = new Vector3f((Vector3fc)v14).mul(param4 * v22).add((Vector3fc)new Vector3f((Vector3fc)v15).mul(param4 * v23)).add((float)param3.x, (float)param3.y, (float)param3.z);
            Vector3f v27 = new Vector3f((Vector3fc)v14).mul(param4 * v24).add((Vector3fc)new Vector3f((Vector3fc)v15).mul(param4 * v25)).add((float)param3.x, (float)param3.y, (float)param3.z);
            param2.vertex(v7, v26.x(), v26.y(), v26.z()).color(v8, v9, v10, v11).normal(param1.peek(), v12.x(), v12.y(), v12.z());
            param2.vertex(v7, v27.x(), v27.y(), v27.z()).color(v8, v9, v10, v11).normal(param1.peek(), v12.x(), v12.y(), v12.z());
        }
    }

    @EventHandler(2)
    public void method1506(EventRender2D param1) {
        if (!((Boolean)this.field6280.getValue()).booleanValue() || Trajectory.I.player == null || Trajectory.I.world == null) {
            return;
        }
        if (this.field6283 == null || this.field6283.isEmpty()) {
            return;
        }
        for (TrajectoryPoint v3 : this.field6283) {
            if (!v3.cfr_renamed_224 || v3.У == null) continue;
            Vec3d v4 = v3.У;
            Vector2f v5 = ProjectionUtil.method2008((float)v4.x, (float)v4.y, (float)v4.z);
            if (v5.x == field6293 && v5.y == field6294) continue;
            float v6 = v5.x;
            float v7 = v5.y + field6295;
            String v8 = _4ZXYEo.В("makeConcatWithConstants", new Object[]{}, (String)field6296.format(v3.cfr_renamed_225));
            float v9 = Fonts.field12486[14].method2719((String)((Object)v8));
            Render2DBatch.method2664(param1.method2997(), v6 - field6297, v7 - field6298, v9 + field6299, field6300, ColorUtil.method1852(0, 0, 0, 120));
            RenderUtil.method398(param1.method2997(), Trajectory.I.player.getMainHandStack(), v6 - field6301, v7 - 2.0f, field6302, -1, 1.0f);
            Fonts.field12486[14].method2711(param1.method2997(), (String)((Object)v8), v6 - v9 / 2.0f + field6303, v7 + field6304, -1);
        }
    }

    private List method1507(ItemStack param1, float param2) {
        Item v3 = param1.getItem();
        ArrayList<TrajectoryPoint> v4 = new ArrayList<TrajectoryPoint>();
        Vec3d v5 = Trajectory.I.player.getLerpedPos(param2);
        double v6 = Trajectory.I.player.getEyeY() - Trajectory.I.player.getY();
        Vec3d v8 = v5.add(0.0, v6, 0.0);
        float v9 = Trajectory.I.player.getYaw();
        float v10 = Trajectory.I.player.getPitch();
        if (v3 == Items.BOW && this.field6278.method1898(field6305).booleanValue()) {
            if (!Trajectory.I.player.isUsingItem() || !ItemStack.areItemsAndComponentsEqual(Trajectory.I.player.getActiveItem(), param1)) {
                return v4;
            }
            int v11 = Trajectory.I.player.getItemUseTime();
            float v12 = BowItem.getPullProgress((int)v11);
            if (v12 < field6306) {
                v12 = field6307;
            }
            float v13 = field6308;
            float v14 = v13 * v12;
            v4.add(this.method1511(v8, v9, v10, v14, field6309, param2));
            return v4;
        }
        if (v3 == Items.CROSSBOW && this.field6278.method1898(field6310).booleanValue()) {
            if (!CrossbowItem.isCharged((ItemStack)param1)) {
                return v4;
            }
            float v11 = field6311;
            float v12 = field6312;
            boolean v13 = false;
            ItemEnchantmentsComponent v14 = param1.getEnchantments();
            for (Object2IntMap.Entry ev : v14.getEnchantmentEntries()) {
                if (!((net.minecraft.registry.entry.RegistryEntry)ev.getKey()).matchesKey(net.minecraft.enchantment.Enchantments.MULTISHOT) || ev.getIntValue() <= 0) continue;
                v13 = true;
                break;
            }
            Vec3d v15 = this.method1516(v8, v9);
            if (v13) {
                float v162_f = field6313;
                Vec3d v17 = this.method1510(0.0f);
                Vec3d v18 = v8.add(v17.multiply(field6314));
                Vec3d v19 = v17.multiply((double)v11);
                TrajectoryPoint v20 = new TrajectoryPoint(v18, v19, v12, field6315, 160, param2);
                v20.k2 = v15.add(v17.multiply(field6316));
                v4.add(v20);
                Vec3d v21 = this.method1510(-v162_f);
                Vec3d v22 = this.method1510(v162_f);
                Vec3d v23 = v8.add(v21.multiply(field6317));
                Vec3d v24 = v8.add(v22.multiply(field6318));
                Vec3d v25 = v21.multiply((double)v11);
                Vec3d v26 = v22.multiply((double)v11);
                TrajectoryPoint v27 = new TrajectoryPoint(v23, v25, v12, field6319, 160, param2);
                TrajectoryPoint v28 = new TrajectoryPoint(v24, v26, v12, field6320, 160, param2);
                v27.k2 = v15.add(v21.multiply(field6321));
                v28.k2 = v15.add(v22.multiply(field6322));
                v4.add(v27);
                v4.add(v28);
                return v4;
            }
            Vec3d v162_vec = this.method1510(0.0f);
            Vec3d v17 = v8.add(v162_vec.multiply(field6323));
            Vec3d v18 = v162_vec.multiply((double)v11);
            TrajectoryPoint v19 = new TrajectoryPoint(v17, v18, v12, field6324, 160, param2);
            v19.k2 = v15.add(v162_vec.multiply(field6325));
            v4.add(v19);
            return v4;
        }
        if (v3 == Items.ENDER_PEARL && this.field6278.method1898(field6326) != false || v3 == Items.SNOWBALL && this.field6278.method1898(field6327) != false || v3 == Items.EGG && this.field6278.method1898(field6328).booleanValue()) {
            v4.add(this.method1511(v8, v9, v10, field6329, field6330, param2));
            return v4;
        }
        if (v3 == Items.TRIDENT && this.field6278.method1898(field6331).booleanValue()) {
            v4.add(this.method1511(v8, v9, v10, field6332, field6333, param2));
            return v4;
        }
        if (v3 == Items.EXPERIENCE_BOTTLE && this.field6278.method1898(field6334).booleanValue()) {
            v4.add(this.method1518(v8, v9, v10, field6335, field6336, param2));
            return v4;
        }
        if (v3 == Items.LINGERING_POTION && this.field6278.method1898(field6337).booleanValue()) {
            TrajectoryPoint v11 = this.method1518(v8, v9, v10, field6338, field6339, param2);
            v11.cfr_renamed_223 = field6340;
            v4.add(v11);
            return v4;
        }
        if (v3 == Items.SPLASH_POTION && this.field6278.method1898(field6341).booleanValue()) {
            TrajectoryPoint v11 = this.method1518(v8, v9, v10, field6342, field6343, param2);
            v11.cfr_renamed_223 = field6344;
            v4.add(v11);
            return v4;
        }
        return v4;
    }

    private void method1508(MatrixStack param1, BufferBuilder param2, Entity param3, float param4, int param5) {
        Vec3d v6 = param3.getPos();
        Vec3d v7 = param3.getLerpedPos(param4);
        Vec3d v8 = v7.subtract(v6);
        Box v9 = param3.getBoundingBox().offset(v8);
        float v10 = (float)ColorUtil.method1854(param5) / field6345;
        float v11 = (float)ColorUtil.method1846(param5) / field6346;
        float v12 = (float)ColorUtil.method1847(param5) / field6347;
        float v14 = (float)v9.minX;
        float v15 = (float)v9.minY;
        float v16 = (float)v9.minZ;
        float v17 = (float)v9.maxX;
        float v18 = (float)v9.maxY;
        float v19 = (float)v9.maxZ;
        Matrix4f v20 = param1.peek().getPositionMatrix();
        this.method1520(param2, param1, v20, v14, v15, v16, v17, v15, v16, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v17, v15, v16, v17, v15, v19, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v17, v15, v19, v14, v15, v19, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v14, v15, v19, v14, v15, v16, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v14, v18, v16, v17, v18, v16, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v17, v18, v16, v17, v18, v19, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v17, v18, v19, v14, v18, v19, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v14, v18, v19, v14, v18, v16, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v14, v15, v16, v14, v18, v16, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v17, v15, v16, v17, v18, v16, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v17, v15, v19, v17, v18, v19, v10, v11, v12, 1.0f);
        this.method1520(param2, param1, v20, v14, v15, v19, v14, v18, v19, v10, v11, v12, 1.0f);
    }

    private Vec3d method1509(float param1, float param2) {
        float v3 = (float)Math.toRadians(param1);
        float v4 = (float)Math.toRadians(param2);
        float v5 = -MathHelper.sin((float)v3) * MathHelper.cos((float)v4);
        float v6 = -MathHelper.sin((float)v4);
        float v7 = MathHelper.cos((float)v3) * MathHelper.cos((float)v4);
        return new Vec3d((double)v5, (double)v6, (double)v7).normalize();
    }

    private Vec3d method1510(float param1) {
        if (Trajectory.I.player == null) {
            return this.method1509(0.0f, 0.0f);
        }
        Vec3d v2 = Trajectory.I.player.getOppositeRotationVector(1.0f);
        Quaternionf v3 = new Quaternionf().setAngleAxis((double)((float)Math.toRadians(param1)), v2.x, v2.y, v2.z);
        Vec3d v4 = Trajectory.I.player.getRotationVec(1.0f);
        Vector3f v5 = v4.toVector3f().rotate((Quaternionfc)v3);
        return new Vec3d((double)v5.x(), (double)v5.y(), (double)v5.z()).normalize();
    }

    private TrajectoryPoint method1511(Vec3d param1, float param2, float param3, float param4, float param5, float param6) {
        Vec3d v7 = this.method1509(param2, param3);
        Vec3d v8 = param1.add(v7.multiply(field6348));
        Vec3d v9 = Trajectory.I.player.getVelocity();
        Vec3d v10 = v7.multiply((double)param4).add(v9.x, Trajectory.I.player.isOnGround() ? 0.0 : v9.y, v9.z);
        TrajectoryPoint v11 = new TrajectoryPoint(v8, v10, param5, field6349, 160, param6);
        v11.k2 = this.method1516(param1, param2).add(v7.multiply(field6350));
        return v11;
    }

    public static void method1512() {
        field6296 = new DecimalFormat(field6351);
    }

    private float method1514(float param1, float param2, float param3) {
        float v4 = MathHelper.clamp((float)((param3 - param1) / (param2 - param1)), (float)0.0f, (float)1.0f);
        return v4 * v4 * (field6352 - 2.0f * v4);
    }

    private Vec3d method1516(Vec3d param1, float param2) {
        float v3 = (float)Math.toRadians(param2);
        double v4 = Trajectory.I.player.getMainArm() == Arm.RIGHT ? field6354 : field6355;
        double v6 = param1.x + v4 * (double)MathHelper.cos((float)v3);
        double v8 = param1.z + v4 * (double)MathHelper.sin((float)v3);
        double v10 = param1.y - field6356;
        return new Vec3d(v6, v10, v8);
    }

    private void method1517(TrajectoryPoint param1, int param2) {
        if (Trajectory.I.world == null) {
            return;
        }
        int v3 = field6357 | param2 & field6358;
        ThreadLocalRandom v4 = ThreadLocalRandom.current();
        Vec3d v5 = param1.У;
        float v6 = param1.cfr_renamed_223;
        DustParticleEffect v7 = new DustParticleEffect(v3, field6359);
        for (int v9 = 0; v9 < 14; ++v9) {
            double v10 = v4.nextDouble() * field6360 * field6361;
            double v12 = (double)v6 * Math.sqrt(v4.nextDouble());
            double v14 = Math.cos(v10) * v12;
            double v16 = Math.sin(v10) * v12;
            double v18 = v5.x + v14;
            double v20 = v5.y + field6362 + v4.nextDouble() * field6363;
            double v22 = v5.z + v16;
            double v24 = (v4.nextDouble() - field6364) * field6365;
            double v26 = field6366 + v4.nextDouble() * field6367;
            double v28 = (v4.nextDouble() - field6368) * field6369;
            Trajectory.I.world.addParticle((ParticleEffect)v7, v18, v20, v22, v24, v26, v28);
        }
    }

    private TrajectoryPoint method1518(Vec3d param1, float param2, float param3, float param4, float param5, float param6) {
        float v7 = (float)Math.toRadians(param3);
        float v8 = (float)Math.toRadians(param2);
        float v9 = (float)Math.toRadians(field6370);
        float v10 = -MathHelper.sin((float)v8) * MathHelper.cos((float)v7);
        float v11 = -MathHelper.sin((float)(v7 + v9));
        float v12 = MathHelper.cos((float)v8) * MathHelper.cos((float)v7);
        Vec3d v13 = new Vec3d((double)v10, (double)v11, (double)v12).normalize();
        Vec3d v14 = param1.add(0.0, field6371, 0.0);
        Vec3d v15 = Trajectory.I.player.getVelocity();
        Vec3d v16 = v13.multiply((double)param4).add(v15.x, Trajectory.I.player.isOnGround() ? 0.0 : v15.y, v15.z);
        TrajectoryPoint v17 = new TrajectoryPoint(v14, v16, param5, field6372, 160, param6);
        v17.k2 = this.method1516(param1, param2).add(0.0, field6373, 0.0);
        return v17;
    }

    private void method1519(MatrixStack param1, BufferBuilder param2, TrajectoryPoint param3) {
        Vec3d v4 = param3.Р;
        Vec3d v5 = param3.t2;
        Matrix4f v7 = param1.peek().getPositionMatrix();
        float v8 = 0.0f;
        int v10 = ThemeColors.method3199(MAIN.MODULE_VISUAL);
        int v11 = ColorUtil.method1851(v10, field6375);
        for (int v12 = 0; v12 <= param3.cfr_renamed_221; ++v12) {
            EntityHitResult v22;
            Direction v21;
            Vec3d v14;
            Vec3d v6 = v4;
            v4 = v4.add(v5);
            Vec3d v13 = v6;
            Vec3d v15 = v14 = v4;
            Entity v16 = null;
            boolean v17 = false;
            Vec3d v18 = null;
            RaycastContext v19 = new RaycastContext(v13, v14, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Trajectory.I.player);
            BlockHitResult v20 = Trajectory.I.world.raycast(v19);
            if (v20.getType() == HitResult.Type.BLOCK) {
                v15 = v20.getPos();
                v17 = true;
                v21 = v20.getSide();
                v18 = new Vec3d((double)v21.getOffsetX(), (double)v21.getOffsetY(), (double)v21.getOffsetZ());
            }
            if ((v22 = ProjectileUtil.raycast((Entity)Trajectory.I.player, (Vec3d)v13, (Vec3d)v14, new Box(v13, v14).expand(field6376), param0 -> EntityPredicates.CAN_HIT.test(param0) && param0 != Trajectory.I.player, (double)v13.squaredDistanceTo(v14))) != null) {
                Vec3d v23 = v22.getPos();
                double v24 = v23.squaredDistanceTo(v13);
                double v26 = v15.squaredDistanceTo(v13);
                if (!v17 || v24 < v26) {
                    v15 = v23;
                    v17 = true;
                    v16 = v22.getEntity();
                }
            }
            if (v15.y <= 0.0) {
                v15 = new Vec3d(v15.x, 0.0, v15.z);
                v17 = true;
            }
            v5 = v5.multiply((double)param3.cfr_renamed_17);
            v5 = v5.add(0.0, (double)(-param3.cfr_renamed_16), 0.0);
            if (Trajectory.I.world.getBlockState(BlockPos.ofFloored((Position)v6)).getBlock() == Blocks.WATER) {
                v5 = v5.multiply(field6377);
            }
            float v23 = (float)v6.distanceTo(v15);
            float v24 = this.method1514(0.0f, field6378, v8);
            float v25 = this.method1514(0.0f, field6379, v8 + v23);
            int v26 = ColorUtil.method1857(3, v12 * 8, v10, v11);
            int v27 = ColorUtil.method1857(3, (v12 + 1) * 8, v10, v11);
            int v28 = ColorUtil.method1848(v26, v24);
            int v29 = ColorUtil.method1848(v27, v25);
            float v30 = (float)ColorUtil.method1854(v28) / field6380;
            float v31 = (float)ColorUtil.method1846(v28) / field6381;
            float v32 = (float)ColorUtil.method1847(v28) / field6382;
            float v33 = (float)ColorUtil.cfr_renamed_0(v28) / field6383;
            float v34 = (float)ColorUtil.method1854(v29) / field6384;
            float v35 = (float)ColorUtil.method1846(v29) / field6385;
            float v36 = (float)ColorUtil.method1847(v29) / field6386;
            float v37 = (float)ColorUtil.cfr_renamed_0(v29) / field6387;
            Vec3d v38 = v12 == 0 && param3.k2 != null ? param3.k2 : v6;
            Vector3f v39 = this.method1504((float)v38.x, (float)v38.y, (float)v38.z, (float)v15.x, (float)v15.y, (float)v15.z);
            param2.vertex(v7, (float)v38.x, (float)v38.y, (float)v38.z).color(v30, v31, v32, v33).normal(param1.peek(), v39.x(), v39.y(), v39.z());
            param2.vertex(v7, (float)v15.x, (float)v15.y, (float)v15.z).color(v34, v35, v36, v37).normal(param1.peek(), v39.x(), v39.y(), v39.z());
            v8 += v23;
            if (v16 != null) {
                param3.У = v15;
                param3.cfr_renamed_225 = (float)v12 * field6388;
                param3.cfr_renamed_224 = true;
                this.method1508(param1, param2, v16, param3.cfr_renamed_222, v29);
                if (!(param3.cfr_renamed_223 > 0.0f)) break;
                Vec3d v40 = v16.getPos();
                Vec3d v41 = v16.getLerpedPos(param3.cfr_renamed_222);
                Vec3d v42 = v41.subtract(v40);
                Box v43 = v16.getBoundingBox().offset(v42);
                Vec3d v44 = new Vec3d(v41.x, v43.minY, v41.z);
                this.method1505(param1, param2, v44, param3.cfr_renamed_223, new Vec3d(0.0, 1.0, 0.0), v29);
                break;
            }
            if (!v17) continue;
            param3.У = v15;
            param3.cfr_renamed_225 = (float)v12 * field6389;
            param3.cfr_renamed_224 = true;
            if (!(param3.cfr_renamed_223 > 0.0f)) break;
            Vec3d v40 = v18 != null ? v18 : new Vec3d(0.0, 1.0, 0.0);
            this.method1505(param1, param2, v15, param3.cfr_renamed_223, v40, v29);
            break;
        }
    }

    private void method1520(BufferBuilder param1, MatrixStack param2, Matrix4f param3, float param4, float param5, float param6, float param7, float param8, float param9, float param10, float param11, float param12, float param13) {
        Vector3f v14 = this.method1504(param4, param5, param6, param7, param8, param9);
        param1.vertex(param3, param4, param5, param6).color(param10, param11, param12, param13).normal(param2.peek(), v14.x(), v14.y(), v14.z());
        param1.vertex(param3, param7, param8, param9).color(param10, param11, param12, param13).normal(param2.peek(), v14.x(), v14.y(), v14.z());
    }

    @EventHandler(2)
    public void method1521(EventRender3D param1) {
        if (Trajectory.I.player == null || Trajectory.I.world == null) {
            return;
        }
        ItemStack v2 = Trajectory.I.player.getMainHandStack();
        if (v2.isEmpty()) {
            this.field6283 = Collections.emptyList();
            return;
        }
        List v3 = this.method1507(v2, param1.O());
        if (v3.isEmpty()) {
            this.field6283 = Collections.emptyList();
            return;
        }
        this.field6283 = v3;
        MatrixStack v4 = param1.method2348();
        Vec3d v5 = Trajectory.I.gameRenderer.getCamera().getPos();
        v4.push();
        v4.translate(-v5.x, -v5.y, -v5.z);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.RENDERTYPE_LINES);
        RenderSystem.lineWidth((float)field6390);
        BufferBuilder v6 = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        for (TrajectoryPoint v8 : (Iterable<TrajectoryPoint>)(v3)) {
            this.method1519(v4, v6, v8);
        }
        // end() throws when the point list was empty — draw only if something was buffered.
        BuiltBuffer v9 = v6.endNullable();
        if (v9 != null) {
            BufferRenderer.drawWithGlobalProgram(v9);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        v4.pop();
        if (((Boolean)this.field6282.getValue()).booleanValue() && Boolean.TRUE.equals(this.field6278.method1898(field6391)) && Trajectory.I.player.age % 5 == 0 && this.field6284 != Trajectory.I.player.age) {
            this.field6284 = Trajectory.I.player.age;
            Item v7 = v2.getItem();
            if (v7 == Items.SPLASH_POTION || v7 == Items.LINGERING_POTION) {
                int v8 = Trajectory.method1522(v2);
                for (TrajectoryPoint v10 : (Iterable<TrajectoryPoint>)(v3)) {
                    if (!v10.cfr_renamed_224 || !(v10.cfr_renamed_223 > 0.0f) || v10.У == null) continue;
                    this.method1517(v10, v8);
                }
            }
        }
    }

    private static int method1522(ItemStack param0) {
        PotionContentsComponent v1 = (PotionContentsComponent)param0.getOrDefault(DataComponentTypes.POTION_CONTENTS, (Object)PotionContentsComponent.DEFAULT);
        return v1.getColor() & field6392;
    }

    static {
        field6390 = 3.0f;
        field6391 = "Зелье";
        field6293 = Float.MAX_VALUE;
        field6294 = Float.MAX_VALUE;
        field6295 = 3.0f;
        вь = "\u0001 сек.";
        field6297 = 20.0f;
        field6298 = 3.0f;
        field6299 = 15.5f;
        field6300 = 11.0f;
        field6301 = 18.0f;
        field6302 = 0.5f;
        field6303 = 6.0f;
        field6304 = 0.5f;
        field6305 = "Лук";
        field6306 = 0.1f;
        field6307 = 0.1f;
        field6308 = 3.0f;
        field6309 = 0.05f;
        field6310 = "Арбалет";
        field6311 = 3.15f;
        field6312 = 0.05f;
        field6313 = 10.0f;
        field6314 = 0.16;
        field6315 = 0.99f;
        field6316 = 0.16;
        field6317 = 0.16;
        field6318 = 0.16;
        field6319 = 0.99f;
        field6320 = 0.99f;
        field6321 = 0.16;
        field6322 = 0.16;
        field6323 = 0.16;
        field6324 = 0.99f;
        field6325 = 0.16;
        field6326 = "Эндер Пёрл";
        field6327 = "Снежок";
        field6328 = "Яйцо";
        field6329 = 1.5f;
        field6330 = 0.03f;
        field6331 = "Трезубец";
        field6332 = 2.5f;
        field6333 = 0.05f;
        field6334 = "Опыт";
        field6335 = 0.7f;
        field6336 = 0.07f;
        field6337 = "Зелье";
        field6338 = 0.5f;
        field6339 = 0.05f;
        field6340 = 3.0f;
        field6341 = "Зелье";
        field6342 = 0.5f;
        field6343 = 0.05f;
        field6344 = 4.0f;
        field6374 = 6.0f;
        field6375 = 0.5f;
        field6376 = 0.3;
        field6377 = 0.8f;
        field6378 = 6.0f;
        field6379 = 6.0f;
        field6380 = 255.0f;
        field6381 = 255.0f;
        field6382 = 255.0f;
        field6383 = 255.0f;
        field6384 = 255.0f;
        field6385 = 255.0f;
        field6386 = 255.0f;
        field6387 = 255.0f;
        field6388 = 0.05f;
        field6389 = 0.05f;
        field6345 = 255.0f;
        field6346 = 255.0f;
        field6347 = 255.0f;
        field6286 = 255.0f;
        field6287 = 255.0f;
        field6288 = 255.0f;
        field6289 = 255.0f;
        field6290 = 0.999f;
        field6291 = Math.PI * 2;
        field6292 = Math.PI * 2;
        field6354 = -0.3;
        field6355 = 0.3;
        field6356 = 0.1;
        field6348 = 0.16;
        field6349 = 0.99f;
        field6350 = 0.16;
        field6370 = -20.0;
        field6371 = -0.1;
        field6372 = 0.99f;
        field6373 = -0.1;
        field6392 = 0xFFFFFF;
        field6357 = -16777216;
        field6358 = 0xFFFFFF;
        field6359 = 1.2f;
        field6360 = Math.PI;
        field6361 = 2.0;
        field6362 = 0.08;
        field6363 = 0.12;
        field6364 = 0.5;
        field6365 = 0.02;
        field6366 = 0.015;
        field6367 = 0.04;
        field6368 = 0.5;
        field6369 = 0.02;
        field6352 = 3.0f;
        field6285 = 1.0E-6f;
        field6353 = "Зелье";
        field6351 = "0.0";
        Trajectory.method1512();
    }
}



