/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
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
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.projectile.ArrowEntity
 *  net.minecraft.entity.projectile.TridentEntity
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemConvertible
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Position
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  org.joml.Matrix4f
 *  org.joml.Vector2f
 *  sLM.4ZXYEo
 */
package minced.module.visual;
import minced.core.EventHandler;

import com.mojang.blaze3d.systems.RenderSystem;
import java.lang.invoke.CallSite;
import java.text.DecimalFormat;
import minced.core.ColorUtil;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.ProjectionUtil;
import minced.core.EventRender3D;
import minced.core.Render2DBatch;
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
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import sLM._4ZXYEo;

public class Prediction
extends Module {
    static String cfr_renamed_400;

    static float field2433;
    static String field2434;
    static String field2435;
    static String field2436;
    static String field2437;
    static String field2438;
    static String field2439;
    MultiBooleanSetting field2440 = new MultiBooleanSetting(field2436, new BooleanSetting(field2437, true), new BooleanSetting(field2438, true), new BooleanSetting(field2439, true));
    static String field2441;
    static DecimalFormat field2442;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field2443;
    static String field2444;
    static String field2445;
    static float field2446;
    static float field2447;
    static double field2448;
    static double field2449;
    static double field2450;
    static float field2451;
    static float field2452;
    static float field2453;
    static float field2454;
    static float field2455;
    static float field2456;
    static float field2457;
    static float field2458;
    static float field2459;
    static float field2460;
    static float field2461;
    static float field2462;
    static float field2463;
    static float field2464;
    static float field2465;
    static double field2466;
    static double field2467;
    static double field2468;
    static float field2469;
    static float field2470;
    static float field2471;
    static float field2472;
    static float field2473;
    static float field2474;
    static float field2475;
    static float field2476;
    static float field2477;
    static float field2478;
    static float field2479;
    static float field2480;
    static float field2481;
    static double field2482;
    static double field2483;
    static double field2484;
    static double field2485;
    static double field2486;
    static double field2487;
    static float field2488;
    static String field2489;
    static String field2490;
    static String field2491;

    private float method652(float param1, float param2, float param3) {
        float v4 = MathHelper.clamp((float)((param3 - param1) / (param2 - param1)), (float)0.0f, (float)1.0f);
        return v4 * v4 * (field2433 - 2.0f * v4);
    }

    public Prediction() {
        super(field2434, field2435, Combat.Visuals);
        this.addSettings(this.field2440);
    }

    public static void method653() {
        field2442 = new DecimalFormat(field2441);
    }

    @EventHandler(2)
    public void method654(EventRender2D param1) {
        for (Entity v3 : Prediction.I.world.getEntities()) {
            if (!(v3 instanceof EnderPearlEntity && this.field2440.method1898(field2443) != false || v3 instanceof ArrowEntity && this.field2440.method1898(field2444) != false) && (!(v3 instanceof TridentEntity) || !this.field2440.method1898(field2445).booleanValue()) || v3.prevY == v3.getY() && v3.prevX == v3.getX() && v3.prevZ == v3.getZ()) continue;
            Item v4 = v3 instanceof EnderPearlEntity ? Items.ENDER_PEARL : (v3 instanceof ArrowEntity ? Items.ARROW : Items.TRIDENT);
            Vec3d v5 = v3.getPos();
            Vec3d v6 = v3.getVelocity();
            Vec3d v7 = v5;
            int v8 = 0;
            for (int v9 = 0; v9 <= 300; ++v9) {
                ++v8;
                v7 = v5;
                v5 = v5.add(v6);
                Vec3d v10 = v6;
                if ((v3.isTouchingWater() || Prediction.I.world.getBlockState(BlockPos.ofFloored((Position)v5)).getBlock() == Blocks.WATER) && !(v3 instanceof TridentEntity)) {
                    float v11 = v3 instanceof EnderPearlEntity ? field2446 : field2447;
                    v10 = v10.multiply((double)v11);
                } else {
                    v10 = v10.multiply(field2448);
                }
                if (!v3.hasNoGravity()) {
                    v10 = new Vec3d(v10.x, v10.y - (v3 instanceof EnderPearlEntity ? field2449 : field2450), v10.z);
                }
                v6 = v10;
                RaycastContext v10s = new RaycastContext(v7, v5, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Prediction.I.player);
                BlockHitResult v11 = Prediction.I.world.raycast((RaycastContext)v10s);
                if (v11.getType() == HitResult.Type.BLOCK || v5.y <= 0.0) break;
            }
            Vector2f v9 = ProjectionUtil.method2008(v7.x, v7.y, v7.z);
            if (v9.x == field2451 && v9.y == field2452) continue;
            float v10 = (float)v8 * field2453;
            float v11 = v9.x;
            float v12 = v9.y + field2454;
            String v13 = _4ZXYEo.cfr_renamed_399("makeConcatWithConstants", new Object[]{}, (String)field2442.format(v10));
            float v14 = Fonts.field12486[14].method2719((String)((Object)v13));
            Render2DBatch.method2664(param1.method2997(), v11 - field2455, v12 - field2456, v14 + field2457, field2458, ColorUtil.method1852(0, 0, 0, 120));
            RenderUtil.method398(param1.method2997(), new ItemStack((ItemConvertible)v4), v11 - field2459, v12 - 2.0f, field2460, -1, 1.0f);
            Fonts.field12486[14].method2711(param1.method2997(), (String)((Object)v13), v11 - v14 / 2.0f + field2461, v12 + field2462, -1);
        }
    }

    private void method655(MatrixStack param1, BufferBuilder param2, Entity param3) {
        Vec3d v4 = param3.getPos();
        Vec3d v5 = param3.getVelocity();
        Matrix4f v7 = param1.peek().getPositionMatrix();
        float v8 = 0.0f;
        for (int v10 = 0; v10 <= 300; ++v10) {
            Vec3d v6 = v4;
            v4 = v4.add(v5);
            Vec3d v11 = v5;
            if ((param3.isTouchingWater() || Prediction.I.world.getBlockState(BlockPos.ofFloored((Position)v6)).getBlock() == Blocks.WATER) && !(param3 instanceof TridentEntity)) {
                float v12 = param3 instanceof EnderPearlEntity ? field2464 : field2465;
                v11 = v11.multiply((double)v12);
            } else {
                v11 = v11.multiply(field2466);
            }
            if (!param3.hasNoGravity()) {
                v11 = new Vec3d(v11.x, v11.y - (param3 instanceof EnderPearlEntity ? field2467 : field2468), v11.z);
            }
            v5 = v11;
            RaycastContext v11s = new RaycastContext(v6, v4, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Prediction.I.player);
            BlockHitResult v12 = Prediction.I.world.raycast((RaycastContext)v11s);
            if (v12.getType() == HitResult.Type.BLOCK || v4.y <= 0.0) break;
            float v13 = (float)v6.distanceTo(v4);
            float v14 = this.method652(0.0f, field2469, v8);
            float v15 = this.method652(0.0f, field2470, v8 + v13);
            int v16 = ColorUtil.method1857(3, v10 * 8, ThemeColors.method3199(MAIN.MODULE_VISUAL), ColorUtil.method1851(ThemeColors.method3199(MAIN.MODULE_VISUAL), field2471));
            int v17 = ColorUtil.method1857(3, (v10 + 1) * 8, ThemeColors.method3199(MAIN.MODULE_VISUAL), ColorUtil.method1851(ThemeColors.method3199(MAIN.MODULE_VISUAL), field2472));
            int v18 = ColorUtil.method1848(v16, v14);
            int v19 = ColorUtil.method1848(v17, v15);
            float v20 = (float)ColorUtil.method1854(v18) / field2473;
            float v21 = (float)ColorUtil.method1846(v18) / field2474;
            float v22 = (float)ColorUtil.method1847(v18) / field2475;
            float v23 = (float)ColorUtil.cfr_renamed_0(v18) / field2476;
            float v24 = (float)ColorUtil.method1854(v19) / field2477;
            float v25 = (float)ColorUtil.method1846(v19) / field2478;
            float v26 = (float)ColorUtil.method1847(v19) / field2479;
            float v27 = (float)ColorUtil.cfr_renamed_0(v19) / field2480;
            float v28 = (float)(v4.x - v6.x);
            float v29 = (float)(v4.y - v6.y);
            float v30 = (float)(v4.z - v6.z);
            float v31 = (float)Math.sqrt(v28 * v28 + v29 * v29 + v30 * v30);
            float v32 = 0.0f;
            float v33 = 1.0f;
            float v34 = 0.0f;
            if (v31 > field2481) {
                v32 = v28 / v31;
                v33 = v29 / v31;
                v34 = v30 / v31;
            }
            param2.vertex(v7, (float)v6.x, (float)v6.y, (float)v6.z).color(v20, v21, v22, v23).normal(param1.peek(), v32, v33, v34);
            param2.vertex(v7, (float)v4.x, (float)v4.y, (float)v4.z).color(v24, v25, v26, v27).normal(param1.peek(), v32, v33, v34);
            v8 += v13;
        }
    }

    public static Vec3d method656(Vec3d param0, Vec3d param1) {
        if (Prediction.I.world == null || Prediction.I.player == null) {
            return param0;
        }
        Vec3d v2 = param0;
        Vec3d v3 = param1;
        Vec3d v4 = v2;
        for (int v5 = 0; v5 <= 300; ++v5) {
            v4 = v2;
            v2 = v2.add(v3);
            Vec3d v6 = v3;
            v6 = Prediction.I.world.getBlockState(BlockPos.ofFloored((Position)v2)).getBlock() == Blocks.WATER ? v6.multiply(field2482) : v6.multiply(field2483);
            v3 = v6 = new Vec3d(v6.x, v6.y - field2484, v6.z);
            RaycastContext v7 = new RaycastContext(v4, v2, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)Prediction.I.player);
            BlockHitResult v8 = Prediction.I.world.raycast(v7);
            if (v8.getType() != HitResult.Type.BLOCK && !(v2.y <= 0.0)) continue;
            return v8.getPos();
        }
        return v4;
    }

    public static Vec3d method657(EnderPearlEntity param0) {
        if (Prediction.I.world == null || param0 == null) {
            return Vec3d.ZERO;
        }
        Vec3d v1 = param0.getPos();
        Vec3d v2 = param0.getVelocity();
        Vec3d v3 = v1;
        for (int v4 = 0; v4 <= 300; ++v4) {
            v3 = v1;
            v1 = v1.add(v2);
            Vec3d v5 = v2;
            v5 = param0.isTouchingWater() || Prediction.I.world.getBlockState(BlockPos.ofFloored((Position)v1)).getBlock() == Blocks.WATER ? v5.multiply(field2485) : v5.multiply(field2486);
            if (!param0.hasNoGravity()) {
                v5 = new Vec3d(v5.x, v5.y - field2487, v5.z);
            }
            v2 = v5;
            RaycastContext v6 = new RaycastContext(v3, v1, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, (Entity)(Prediction.I.player == null ? param0 : Prediction.I.player));
            BlockHitResult v7 = Prediction.I.world.raycast(v6);
            if (v7.getType() != HitResult.Type.BLOCK && !(v1.y <= 0.0)) continue;
            return v7.getPos();
        }
        return v3;
    }

    @EventHandler(2)
    public void method658(EventRender3D param1) {
        MatrixStack v2 = param1.method2348();
        Vec3d v3 = Prediction.I.gameRenderer.getCamera().getPos();
        v2.push();
        v2.translate(-v3.x, -v3.y, -v3.z);
        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader((ShaderProgramKey)ShaderProgramKeys.RENDERTYPE_LINES);
        RenderSystem.lineWidth((float)field2488);
        BufferBuilder v4 = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        for (Entity v6 : Prediction.I.world.getEntities()) {
            if (!(v6 instanceof EnderPearlEntity && this.field2440.method1898(field2489) != false || v6 instanceof ArrowEntity && this.field2440.method1898(field2490) != false) && (!(v6 instanceof TridentEntity) || !this.field2440.method1898(field2491).booleanValue()) || v6.prevY == v6.getY() && v6.prevX == v6.getX() && v6.prevZ == v6.getZ()) continue;
            this.method655(v2, v4, v6);
        }
        BuiltBuffer v5 = v4.endNullable();
        if (v5 != null) {
            BufferRenderer.drawWithGlobalProgram((BuiltBuffer)v5);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        v2.pop();
    }

    static {
        field2482 = 0.8f;
        field2483 = 0.99f;
        field2484 = 0.03;
        field2485 = 0.8f;
        field2486 = 0.99f;
        field2487 = 0.03;
        field2434 = "Prediction";
        field2435 = "Предугадывает куда и за сколько времени упадет предмет";
        field2436 = "Снаряды";
        field2437 = "Эндер Пёрл";
        field2438 = "Стрела";
        field2439 = "Трезубец";
        field2443 = "Эндер Пёрл";
        field2444 = "Стрела";
        field2445 = "Трезубец";
        field2446 = 0.8f;
        field2447 = 0.6f;
        field2448 = 0.99f;
        field2449 = 0.03;
        field2450 = 0.05;
        field2451 = Float.MAX_VALUE;
        field2452 = Float.MAX_VALUE;
        field2453 = 0.05f;
        field2454 = 3.0f;
        cfr_renamed_400 = "\u0001 сек.";
        field2455 = 20.0f;
        field2456 = 3.0f;
        field2457 = 15.5f;
        field2458 = 11.0f;
        field2459 = 18.0f;
        field2460 = 0.5f;
        field2461 = 6.0f;
        field2462 = 0.5f;
        field2488 = 3.0f;
        field2489 = "Эндер Пёрл";
        field2490 = "Стрела";
        field2491 = "Трезубец";
        field2463 = 6.0f;
        field2464 = 0.8f;
        field2465 = 0.6f;
        field2466 = 0.99f;
        field2467 = 0.03;
        field2468 = 0.05;
        field2469 = 6.0f;
        field2470 = 6.0f;
        field2471 = 0.5f;
        field2472 = 0.5f;
        field2473 = 255.0f;
        field2474 = 255.0f;
        field2475 = 255.0f;
        field2476 = 255.0f;
        field2477 = 255.0f;
        field2478 = 255.0f;
        field2479 = 255.0f;
        field2480 = 255.0f;
        field2481 = 1.0E-6f;
        field2433 = 3.0f;
        field2441 = "0.0";
        Prediction.method653();
    }
}



