/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import minced.core.RotationTargetUtil;
import minced.core.RotationCache;
import minced.core.EventUpdate;
import minced.core.RotationManager;
import minced.core.RotationBase;
import minced.core.Rotation;
import minced.core.CameraRotation;
import minced.enums.RotationPhase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class AimRotation
extends RotationBase
{
    RotationPhase field1083 = RotationPhase.IDLE;
    float field1084;
    Rotation field1085;
    float field1086;
    int field1087;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field1088;
    static float field1089;
    float field1090;
    int field1091;
    float field1092;
    int field1093;
    static float field1094;
    static float field1095;
    static float field1096;
    static float field1097;

    public RotationPhase method353() {
        return this.field1083;
    }

    public AimRotation method354(float param1) {
        this.field1084 = param1;
        return this;
    }

    public Rotation method355() {
        return this.field1085;
    }

    public AimRotation method356(float param1) {
        this.field1086 = param1;
        return this;
    }

    public static AimRotation method357() {
        return (AimRotation)RotationCache.method1997(AimRotation.class);
    }

    public int method358() {
        return this.field1087;
    }

    private void method359() {
        if (AimRotation.I.player == null) {
            this.method370();
            return;
        }
        Rotation v1 = new Rotation(RotationManager.method255(), RotationManager.method245());
        if (this.method373(v1, this.I(), this.method372())) {
            AimRotation.I.player.setYaw(v1.method3140());
            AimRotation.I.player.setPitch(MathHelper.clamp((float)v1.method3136(), (float)field1088, (float)field1089));
            RotationManager.method252(AimRotation.I.player.getYaw());
            RotationManager.method257(AimRotation.I.player.getPitch());
            this.method370();
        }
    }

    public float method360() {
        return this.field1090;
    }

    public AimRotation method361(int param1) {
        this.field1087 = param1;
        return this;
    }

    public AimRotation method362(float param1) {
        this.field1090 = param1;
        return this;
    }

    public AimRotation method363(int param1) {
        this.field1091 = param1;
        return this;
    }

    public int method364() {
        return this.field1091;
    }

    public AimRotation method365(float param1) {
        this.field1092 = param1;
        return this;
    }

    public float I() {
        return this.field1086;
    }

    public float cfr_underscore() {
        return this.field1084;
    }

    public AimRotation method366(int param1) {
        this.field1093 = param1;
        return this;
    }

    public static void method367(Rotation param0, float param1, int param2, int param3) {
        AimRotation.method369(param0, param1, param1, param2, param3);
    }

    public boolean method368() {
        return !this.field1083.equals(RotationPhase.IDLE);
    }

    public static void method369(Rotation param0, float param1, float param2, int param3, int param4) {
        AimRotation.method376(param0, param1, param1, param2, param2, param3, param4, false);
    }

    public void method370() {
        if (AimRotation.I.player != null) {
            RotationManager.method252(AimRotation.I.player.getYaw());
            RotationManager.method257(AimRotation.I.player.getPitch());
        }
        this.method375(null);
        this.method371(RotationPhase.IDLE);
        this.method361(0);
        if (!CameraRotation.cfr_renamed_288().method47()) {
            RotationManager.method254(false);
        }
    }

    public AimRotation method371(RotationPhase param1) {
        this.field1083 = param1;
        return this;
    }

    public float method372() {
        return this.field1092;
    }

    private boolean method373(Rotation param1, float param2, float param3) {
        if (AimRotation.I.player == null) {
            return false;
        }
        Rotation v4 = new Rotation((Entity)AimRotation.I.player);
        float v5 = MathHelper.wrapDegrees((float)(param1.method3140() - v4.method3140()));
        float v6 = param1.method3136() - v4.method3136();
        float v7 = Math.min(Math.abs(v5), param2);
        float v8 = Math.min(Math.abs(v6), param3);
        AimRotation.I.player.setYaw(AimRotation.I.player.getYaw() + RotationTargetUtil.method1981(MathHelper.clamp((float)v5, (float)(-v7), (float)v7)) - RotationTargetUtil.method1981(MathHelper.clamp((float)v5, (float)(-v7), (float)v7)) % RotationTargetUtil.method1975());
        AimRotation.I.player.setPitch(MathHelper.clamp((float)(AimRotation.I.player.getPitch() + RotationTargetUtil.method1981(MathHelper.clamp((float)v6, (float)(-v8), (float)v8))), (float)field1094, (float)field1095) - MathHelper.clamp((float)(AimRotation.I.player.getPitch() + RotationTargetUtil.method1981(MathHelper.clamp((float)v6, (float)(-v8), (float)v8))), (float)field1096, (float)field1097) % RotationTargetUtil.method1975());
        this.method366(0);
        return new Rotation((Entity)AimRotation.I.player).method3141(param1) < 1.0f;
    }

    public int method374() {
        return this.field1093;
    }

    public AimRotation method375(Rotation param1) {
        this.field1085 = param1;
        return this;
    }

    public static void method376(Rotation param0, float param1, float param2, float param3, float param4, int param5, int param6, boolean param7) {
        AimRotation v8 = AimRotation.method357();
        if (v8.method358() > param6) {
            return;
        }
        if (param0 == null) {
            v8.method356(param3);
            v8.method365(param4);
            v8.method371(RotationPhase.RESET);
            return;
        }
        if (v8.method353().equals(RotationPhase.IDLE) && !param7) {
            if (AimRotation.I.player != null) {
                RotationManager.method252(AimRotation.I.player.getYaw());
                RotationManager.method257(AimRotation.I.player.getPitch());
            }
            RotationManager.method254(true);
        }
        v8.method354(param1);
        v8.method362(param2);
        v8.method356(param3);
        v8.method365(param4);
        v8.method363(param5);
        v8.method361(param6);
        v8.method371(RotationPhase.AIM);
        v8.method375(param0);
        v8.method373(param0, param1, param2);
    }

    @EventHandler(2)
    public void method377(EventUpdate param1) {
        if (this.method353().equals(RotationPhase.AIM) && this.method374() > this.method364()) {
            this.method371(RotationPhase.RESET);
        }
        if (this.method353().equals(RotationPhase.RESET)) {
            this.method359();
        }
        ++this.field1093;
    }

    static {
        field1088 = -90.0f;
        field1089 = 90.0f;
        field1094 = -90.0f;
        field1095 = 90.0f;
        field1096 = -90.0f;
        field1097 = 90.0f;
    }
}



