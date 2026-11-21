/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1297
 *  net.minecraft.class_3532
 *  org.joml.Vector2f
 */
package ru.decide.manager.rotation;

import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1297;
import net.minecraft.class_3532;
import org.joml.Vector2f;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public class Rotation
implements IMinecraft {
    public float yaw;
    public float pitch;

    public Rotation(class_1297 entity) {
        this.yaw = entity.method_36454();
        this.pitch = entity.method_36455();
    }

    public Rotation(float yawN, float pitchN) {
        this.yaw = yawN;
        this.pitch = pitchN;
    }

    public float getDelta(Rotation target) {
        float yawDelta = class_3532.method_15393((float)(target.yaw - this.yaw));
        float pitchDelta = target.pitch - this.pitch;
        return (float)Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));
    }

    public double getDeltaDouble(Rotation target) {
        double yawDelta = class_3532.method_15393((float)(target.yaw - this.yaw));
        double pitchDelta = class_3532.method_15393((float)(target.pitch - this.pitch));
        return Math.hypot(yawDelta, pitchDelta);
    }

    public static Vector2f camera() {
        return new Vector2f(Rotation.cameraYaw(), Rotation.cameraPitch());
    }

    public static float cameraYaw() {
        return class_3532.method_15393((float)(Rotation.mc.field_1773.method_19418().method_19330() + (float)(Rotation.mc.field_1690.method_31044().method_31035() ? 180 : 0)));
    }

    public static float cameraPitch() {
        return (float)(Rotation.mc.field_1690.method_31044().method_31035() ? -1 : 1) * Rotation.mc.field_1773.method_19418().method_19329();
    }

    @Generated
    public float getYaw() {
        return this.yaw;
    }

    @Generated
    public float getPitch() {
        return this.pitch;
    }

    @Generated
    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    @Generated
    public void setPitch(float pitch) {
        this.pitch = pitch;
    }
}

