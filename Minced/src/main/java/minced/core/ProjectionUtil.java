/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.render.Frustum
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector2f
 *  org.joml.Vector4f
 */
package minced.core;

import java.util.function.Function;
import minced.core.ClientUtil;
import minced.core.Minced;
import minced.mixin.accessor.IGameRendererMixin;
import minced.mixin.accessor.IWorldRendererMixin;
import minced.module.visual.Removals;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class ProjectionUtil
{
    static double field7995;
    static double field7996;
    static double field7997;
    static float field7998;
    static float field7999;
    static double field8000;
    static String field8001;
    static float field8002;
    static float field8003;
    static float field8004;
    static float field8005;
    static float field8006;
    static double field8007;
    static float field8008;
    static float field8009;
    static float field8010;
    static float field8011;
    static float field8012;
    static float field8013;
    static float field8014;
    static float field8015;
    static float field8016;
    static float field8017;
    static float field8018;
    static float field8019;
    static float field8020;
    static float field8021;

    public static Vec3d[] method2000(Box param0) {
        return new Vec3d[]{new Vec3d(param0.minX, param0.minY, param0.minZ), new Vec3d(param0.minX, param0.minY, param0.maxZ), new Vec3d(param0.minX, param0.maxY, param0.minZ), new Vec3d(param0.minX, param0.maxY, param0.maxZ), new Vec3d(param0.maxX, param0.minY, param0.minZ), new Vec3d(param0.maxX, param0.minY, param0.maxZ), new Vec3d(param0.maxX, param0.maxY, param0.minZ), new Vec3d(param0.maxX, param0.maxY, param0.maxZ)};
    }

    private static Vector2f method2001(Vector4f param0) {
        if (param0.w <= 0.0f) {
            return new Vector2f(field8016, field8017);
        }
        param0.div(param0.w);
        float v1 = param0.x;
        float v2 = param0.y;
        int v3 = ClientUtil.I.getWindow().getScaledWidth();
        int v4 = ClientUtil.I.getWindow().getScaledHeight();
        float v5 = (v1 * field8018 + field8019) * (float)v3;
        float v6 = (1.0f - (v2 * field8020 + field8021)) * (float)v4;
        return new Vector2f(v5, v6);
    }

    private static Vector4f method2002(double param0, double param2, double param4, float param6) {
        Camera v7 = ClientUtil.I.getEntityRenderDispatcher().camera;
        Vec3d v8 = v7.getPos();
        float v9 = ((IGameRendererMixin)ClientUtil.I.gameRenderer).callGetFov(v7, param6, true);
        Matrix4f v10 = ClientUtil.I.gameRenderer.getBasicProjectionMatrix(v9);
        Quaternionf v11 = new Quaternionf((Quaternionfc)v7.getRotation());
        v11.conjugate();
        Matrix4f v12 = new Matrix4f().rotation((Quaternionfc)v11).translate(-((float)v8.x), -((float)v8.y), -((float)v8.z));
        Matrix4f v13 = ProjectionUtil.method2007(param6);
        Matrix4f v14 = ProjectionUtil.method2011(param6);
        Matrix4f v15 = new Matrix4f((Matrix4fc)v10).mul((Matrix4fc)v13).mul((Matrix4fc)v14).mul((Matrix4fc)v12);
        Vector4f v16 = new Vector4f((float)param0, (float)param2, (float)param4, 1.0f);
        v15.transform(v16);
        return v16;
    }

    public static boolean method2003(Entity param0) {
        Entity v1 = ClientUtil.I.getCameraEntity();
        if (v1 == null) {
            return false;
        }
        if (param0 == v1 && ClientUtil.I.options.getPerspective().isFirstPerson()) {
            return false;
        }
        Camera v2 = ClientUtil.I.getEntityRenderDispatcher().camera;
        Vec3d v3 = v2.getPos();
        Frustum v4 = ProjectionUtil.method2010();
        return ClientUtil.I.getEntityRenderDispatcher().shouldRender(param0, v4, v3.x, v3.y, v3.z);
    }

    public static Box method2004(Entity param0, Vec3d param1) {
        Vec3d v2 = new Vec3d(param0.getBoundingBox().maxX - param0.getBoundingBox().minX, param0.getBoundingBox().maxY - param0.getBoundingBox().minY, param0.getBoundingBox().maxZ - param0.getBoundingBox().minZ);
        return new Box(param1.x - v2.x / field7995, param1.y, param1.z - v2.z / field7996, param1.x + v2.x / field7997, param1.y + v2.y + (double)(field7998 - (param0.isSneaking() && !ClientUtil.I.player.getAbilities().flying ? field7999 : 0.0f)), param1.z + v2.z / field8000);
    }

    public static Vector2f method2005(Vec3d param0) {
        return ProjectionUtil.method2008(param0.x, param0.y, param0.z);
    }

    public static Vector2f method2006(double param0, double param2, double param4, float param6) {
        Vector4f v7 = ProjectionUtil.method2002(param0, param2, param4, param6);
        return ProjectionUtil.method2001(v7);
    }

    private static Matrix4f method2007(float param0) {
        Matrix4f v1 = new Matrix4f();
        Entity v2 = ClientUtil.I.getCameraEntity();
        if (v2 instanceof LivingEntity) {
            float v7;
            float v6;
            LivingEntity v3 = (LivingEntity)v2;
            Removals v4 = (Removals)Minced.getInstance().getModuleManager().method187(Removals.class);
            if (v4.isEnabled() && Removals.field4534.method1898(field8001).booleanValue()) {
                return v1;
            }
            float v5 = (float)v3.hurtTime - param0;
            if (v3.isDead()) {
                v6 = Math.min((float)v3.deathTime + param0, field8002);
                v7 = field8003 - field8004 / (v6 + field8005);
                v1.rotateZ((float)Math.toRadians(v7));
            }
            if (v5 < 0.0f) {
                return v1;
            }
            v5 /= (float)v3.maxHurtTime;
            v5 = MathHelper.sin((float)(v5 * v5 * v5 * v5 * field8006));
            v6 = v3.getDamageTiltYaw();
            v1.rotateY((float)Math.toRadians(-v6));
            v7 = (float)((double)(-v5) * field8007 * (Double)ClientUtil.I.options.getDamageTiltStrength().getValue());
            v1.rotateZ((float)Math.toRadians(v7));
            v1.rotateY((float)Math.toRadians(v6));
        }
        return v1;
    }

    public static Vector2f method2008(double param0, double param2, double param4) {
        Vector4f v6 = ProjectionUtil.method2002(param0, param2, param4, ClientUtil.I.getRenderTickCounter().getTickDelta(true));
        return ProjectionUtil.method2001(v6);
    }

    public static Vector2f method2009(Vec3d param0, float param1) {
        return ProjectionUtil.method2006(param0.x, param0.y, param0.z, param1);
    }

    private static Frustum method2010() {
        Frustum v0 = ClientUtil.I.worldRenderer.getCapturedFrustum();
        return v0 != null ? v0 : ((IWorldRendererMixin)ClientUtil.I.worldRenderer).getFrustum();
    }

    private static Matrix4f method2011(float param0) {
        Matrix4f v1 = new Matrix4f();
        if (!((Boolean)ClientUtil.I.options.getBobView().getValue()).booleanValue()) {
            return v1;
        }
        Entity v2 = ClientUtil.I.getCameraEntity();
        if (v2 instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity v3 = (AbstractClientPlayerEntity)v2;
            float v4 = v3.distanceMoved - v3.lastDistanceMoved;
            float v5 = -(v3.distanceMoved + v4 * param0);
            float v6 = MathHelper.lerp((float)param0, (float)v3.prevStrideDistance, (float)v3.strideDistance);
            v1.translate(MathHelper.sin((float)(v5 * field8008)) * v6 * field8009, -Math.abs(MathHelper.cos((float)(v5 * field8010)) * v6), 0.0f);
            float v7 = MathHelper.sin((float)(v5 * field8011)) * v6 * field8012;
            v1.rotateZ((float)Math.toRadians(v7));
            float v8 = Math.abs(MathHelper.cos((float)(v5 * field8013 - field8014)) * v6) * field8015;
            v1.rotateX((float)Math.toRadians(v8));
        }
        return v1;
    }

    static {
        field8008 = (float)Math.PI;
        field8009 = 0.5f;
        field8010 = (float)Math.PI;
        field8011 = (float)Math.PI;
        field8012 = 3.0f;
        field8013 = (float)Math.PI;
        field8014 = 0.2f;
        field8015 = 5.0f;
        field8001 = "Тряска камеры";
        field8002 = 20.0f;
        field8003 = 40.0f;
        field8004 = 8000.0f;
        field8005 = 200.0f;
        field8006 = (float)Math.PI;
        field8007 = 14.0;
        field8016 = Float.MAX_VALUE;
        field8017 = Float.MAX_VALUE;
        field8018 = 0.5f;
        field8019 = 0.5f;
        field8020 = 0.5f;
        field8021 = 0.5f;
        field7995 = 2.0;
        field7996 = 2.0;
        field7997 = 2.0;
        field7998 = 0.2f;
        field7999 = 0.1f;
        field8000 = 2.0;
    }
}



