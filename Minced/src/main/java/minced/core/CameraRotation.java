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
import minced.core.AimRotation;
import minced.enums.CameraRotationPhase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class CameraRotation
extends RotationBase
{
    static MinecraftClient I = MinecraftClient.getInstance();
    int field55;
    Rotation field56;
    static float field57;
    static float field58;
    int field59;
    CameraRotationPhase field60 = CameraRotationPhase.IDLE;
    float field61;
    float field62;
    float field63;
    int field64;
    static float field65;
    static float field66;
    float field67;

    public static void method37(Rotation param0, float param1, float param2, float param3, float param4, int param5, int param6, boolean param7) {
        CameraRotation v8 = CameraRotation.cfr_renamed_288();
        if (v8.method48() > param6) {
            return;
        }
        if (param0 == null) {
            v8.method45(param3);
            v8.cfr_renamed_0(param4);
            v8.cfr_renamed_4(CameraRotationPhase.RESET);
            return;
        }
        if (v8.cfr_renamed_289().equals(CameraRotationPhase.IDLE) && !param7) {
            if (CameraRotation.I.player != null) {
                RotationManager.method252(CameraRotation.I.player.getYaw());
                RotationManager.method257(CameraRotation.I.player.getPitch());
            }
            RotationManager.method254(true);
        }
        v8.cfr_renamed_4(param1);
        v8.method42(param2);
        v8.method45(param3);
        v8.cfr_renamed_0(param4);
        v8.method46(param5);
        v8.cfr_renamed_4(param6);
        v8.cfr_renamed_4(CameraRotationPhase.AIM);
        v8.cfr_renamed_4(param0);
        v8.method40(param0, param1, param2);
    }

    @EventHandler(2)
    public void method38(EventUpdate param1) {
        if (this.cfr_renamed_289().equals(CameraRotationPhase.AIM) && this.method54() > this.method41()) {
            this.cfr_renamed_4(CameraRotationPhase.RESET);
        }
        if (this.cfr_renamed_289().equals(CameraRotationPhase.RESET)) {
            this.method49();
        }
        ++this.field55;
    }

    public Rotation method39() {
        return this.field56;
    }

    private boolean method40(Rotation param1, float param2, float param3) {
        if (CameraRotation.I.player == null) {
            return false;
        }
        CameraRotation.I.player.setYaw(this.cfr_renamed_4(CameraRotation.I.player.getYaw(), param1.method3140(), param2));
        CameraRotation.I.player.setPitch(MathHelper.clamp((float)this.cfr_renamed_4(CameraRotation.I.player.getPitch(), param1.method3136(), param3), (float)field57, (float)field58));
        this.method55(0);
        return new Rotation((Entity)CameraRotation.I.player).method3141(param1) < 1.0f;
    }

    public int method41() {
        return this.field59;
    }

    public CameraRotation method42(float param1) {
        this.field61 = param1;
        return this;
    }

    public CameraRotation cfr_renamed_4(float param1) {
        this.field62 = param1;
        return this;
    }

    public static void method43(Rotation param0, float param1, float param2, int param3, int param4) {
        CameraRotation.method37(param0, param1, param1, param2, param2, param3, param4, false);
    }

    public float method44() {
        return this.field63;
    }

    public CameraRotation method45(float param1) {
        this.field63 = param1;
        return this;
    }

    public CameraRotation method46(int param1) {
        this.field59 = param1;
        return this;
    }

    public boolean method47() {
        return !this.field60.equals(CameraRotationPhase.IDLE);
    }

    public int method48() {
        return this.field64;
    }

    private void method49() {
        if (CameraRotation.I.player == null) {
            this.method53();
            return;
        }
        Rotation v1 = new Rotation(RotationManager.method255(), RotationManager.method245());
        if (this.method40(v1, this.method44(), this.method52())) {
            CameraRotation.I.player.setYaw(v1.method3140());
            CameraRotation.I.player.setPitch(MathHelper.clamp((float)v1.method3136(), (float)field65, (float)field66));
            RotationManager.method252(CameraRotation.I.player.getYaw());
            RotationManager.method257(CameraRotation.I.player.getPitch());
            this.method53();
        }
    }

    public CameraRotation cfr_renamed_4(CameraRotationPhase param1) {
        this.field60 = param1;
        return this;
    }

    public float method50() {
        return this.field61;
    }

    public CameraRotation cfr_renamed_4(int param1) {
        this.field64 = param1;
        return this;
    }

    public float method51() {
        return this.field62;
    }

    public CameraRotationPhase cfr_renamed_289() {
        return this.field60;
    }

    public CameraRotation cfr_renamed_4(Rotation param1) {
        this.field56 = param1;
        return this;
    }

    private float cfr_renamed_4(float param1, double param2, float param4) {
        float v5 = (float)MathHelper.wrapDegrees((double)(param2 - (double)param1));
        float v6 = Math.abs(v5 / param4);
        float v7 = v6 * Math.signum(Math.signum(v5));
        if (Math.abs(v7) > Math.abs(v5)) {
            v7 = v5;
        }
        return param1 + RotationTargetUtil.method1981(v7) - RotationTargetUtil.method1981(v7) % RotationTargetUtil.method1975();
    }

    public CameraRotation cfr_renamed_0(float param1) {
        this.field67 = param1;
        return this;
    }

    public static CameraRotation cfr_renamed_288() {
        return (CameraRotation)RotationCache.method1997(CameraRotation.class);
    }

    public float method52() {
        return this.field67;
    }

    public void method53() {
        if (CameraRotation.I.player != null) {
            RotationManager.method252(CameraRotation.I.player.getYaw());
            RotationManager.method257(CameraRotation.I.player.getPitch());
        }
        this.cfr_renamed_4((Rotation)null);
        this.cfr_renamed_4(CameraRotationPhase.IDLE);
        this.cfr_renamed_4(0);
        if (!AimRotation.method357().method368()) {
            RotationManager.method254(false);
        }
    }

    public int method54() {
        return this.field55;
    }

    public CameraRotation method55(int param1) {
        this.field55 = param1;
        return this;
    }

    static {
        field65 = -90.0f;
        field66 = 90.0f;
        field57 = -90.0f;
        field58 = 90.0f;
    }
}



