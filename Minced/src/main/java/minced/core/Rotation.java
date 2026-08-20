/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.RaycastContext
 *  net.minecraft.world.RaycastContext$FluidHandling
 *  net.minecraft.world.RaycastContext$ShapeType
 *  org.joml.Vector2f
 *  sLM.4ZXYEo
 */
package minced.core;

import minced.core.AuraModeHandler;
import minced.mixin.accessor.IClientPlayerEntityMixin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.joml.Vector2f;
import sLM._4ZXYEo;

public class Rotation
implements AuraModeHandler {
    static String cfr_renamed_269;

    float field11129;
    float field11130;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field11131;
    static float field11132;
    static float field11133;

    public Rotation(float param1, float param2) {
        this.field11129 = param1;
        this.field11130 = param2;
    }

    public String toString() {
        return _4ZXYEo.дк("makeConcatWithConstants", new Object[]{}, (float)this.method3140(), (float)this.method3136());
    }

    public BlockHitResult method3132(double param1, float param3, float param4, Entity param5, RaycastContext.FluidHandling param6) {
        Vec3d v7 = Rotation.I.player.getCameraPosVec(I.getRenderTickCounter().getTickDelta(true));
        Vec3d v8 = Rotation.method3137(param4, param3);
        Vec3d v9 = v7.add(v8.x * param1, v8.y * param1, v8.z * param1);
        return Rotation.I.world.raycast(new RaycastContext(v7, v9, RaycastContext.ShapeType.COLLIDER, param6, param5));
    }

    public static Rotation method3133(Rotation param0, Rotation param1) {
        float v2 = MathHelper.wrapDegrees((float)(param1.method3140() - param0.method3140()));
        float v3 = MathHelper.wrapDegrees((float)(param1.method3136() - param0.method3136()));
        return new Rotation(v2, v3);
    }

    public static float method3134() {
        return (float)(Rotation.I.gameRenderer.getCamera().isThirdPerson() ? -1 : 1) * Rotation.I.gameRenderer.getCamera().getPitch();
    }

    public void method3135(float param1) {
        this.field11129 = param1;
    }

    public float method3136() {
        return this.field11130;
    }

    public static Vec3d method3137(float param0, float param1) {
        float v2 = -param1 * field11131 - field11132;
        float v3 = -param0 * field11133;
        float v4 = (float)Math.cos(v2);
        float v5 = (float)Math.sin(v2);
        float v6 = (float)(-Math.cos(v3));
        float v7 = (float)Math.sin(v3);
        return new Vec3d((double)(v5 * v6), (double)v7, (double)(v4 * v6));
    }

    public static Vector2f method3138() {
        return new Vector2f(Rotation.method3143(), Rotation.method3134());
    }

    protected boolean method3139(Object param1) {
        return param1 instanceof Rotation;
    }

    public float method3140() {
        return this.field11129;
    }

    public Rotation() {
    }

    public int hashCode() {
        int v2 = 59 + Float.floatToIntBits(this.method3140());
        v2 = v2 * 59 + Float.floatToIntBits(this.method3136());
        return v2;
    }

    public Rotation(Entity param1) {
        this.field11129 = param1.getYaw();
        this.field11130 = param1.getPitch();
    }

    public boolean equals(Object param1) {
        if (param1 == this) {
            return true;
        }
        if (!(param1 instanceof Rotation)) {
            return false;
        }
        Rotation v2 = (Rotation)param1;
        if (!v2.method3139(this)) {
            return false;
        }
        if (Float.compare(this.method3140(), v2.method3140()) != 0) {
            return false;
        }
        return Float.compare(this.method3136(), v2.method3136()) == 0;
    }

    public float method3141(Rotation param1) {
        float v2 = MathHelper.wrapDegrees((float)(param1.method3140() - this.field11129));
        float v3 = param1.method3136() - this.field11130;
        return (float)Math.hypot(Math.abs(v2), Math.abs(v3));
    }

    public static Rotation method3142() {
        return new Rotation(((IClientPlayerEntityMixin)Rotation.I.player).getLastYaw(), ((IClientPlayerEntityMixin)Rotation.I.player).getLastPitch());
    }

    public static float method3143() {
        return MathHelper.wrapDegrees((float)(Rotation.I.gameRenderer.getCamera().getYaw() + (float)(Rotation.I.gameRenderer.getCamera().isThirdPerson() ? 180 : 0)));
    }

    public void method3144(float param1) {
        this.field11130 = param1;
    }

    static {
        field11131 = (float)Math.PI / 180;
        field11132 = (float)Math.PI;
        field11133 = (float)Math.PI / 180;
        cfr_renamed_269 = "Rotation(yaw=\u0001, pitch=\u0001)";
    }
}



