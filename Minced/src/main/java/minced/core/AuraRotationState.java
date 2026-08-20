/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.math.MathHelper
 */
package minced.core;

import java.util.concurrent.ThreadLocalRandom;
import minced.core.RotationTargetUtil;
import minced.core.AuraColorState;
import minced.core.RotationManager;
import minced.core.AuraModeHandler;
import minced.core.AuraMotionUtil;
import minced.core.AuraItemUtil;
import minced.core.Rotation;
import minced.core.RotationHistory;
import minced.core.RecordClass231;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;

public final class AuraRotationState
extends AuraMotionUtil
implements AuraModeHandler {
    static boolean field10766 = true;
    static long field10767 = 0L;
    static Rotation field10768;
    AuraItemUtil field10769;
    static MinecraftClient I = MinecraftClient.getInstance();
    static double field10770;
    static String field10771 = "field10771";
    static long field10772;
    static double field10773;
    static double field10774;
    static double field10775;
    static float field10776;
    static float field10777;
    static String field10778 = "field10778";
    static float field10779;
    static float field10780;
    static float field10781;
    static float field10782;
    static float field10783;
    static float field10784;
    static float field10785;
    static float field10786;
    static float field10787;
    static double field10788;

    public void method3047() {
        field10766 = true;
        field10767 = 0L;
        field10768 = new Rotation(0.0f, 0.0f);
        this.method3070();
    }

    public Rotation method3048(Rotation param1, LivingEntity param2) {
        float v15;
        if (this.field10769 == null || AuraRotationState.I.player == null) {
            return param1;
        }
        this.method3072(this.field10769, param2);
        long v3 = System.currentTimeMillis();
        RecordClass231 v5 = this.method3067(param1, param2, field10770, field10771, field10772, (double)param2.getWidth() * field10773, (double)param2.getHeight() * field10774, (double)param2.getWidth() * field10775, field10776, field10777);
        float v6 = v5.yawDelta();
        float v7 = v5.pitchDelta();
        float v8 = v5.finalYawSpeed();
        float v9 = v5.finalPitchSpeed();
        boolean v10 = this.field10769.method3096(param2);
        if (!field10766) {
            field10767 = 0L;
        }
        AuraColorState v11 = this.method3068(field10778, 100);
        if (!this.field10769.method3091()) {
            v11.method1993();
        } else {
            v11.method1990();
        }
        if (this.field10769.method3081()) {
            v11.method1990();
        }
        float v12 = param1.method3140() + (v6 > 0.0f ? v8 : -v8);
        float v13 = param1.method3136() + (v7 > 0.0f ? v9 : -v9);
        v12 = RotationHistory.method3559(param1.method3140(), v12, v11.method1992());
        v13 = RotationHistory.method3559(param1.method3136(), v13, v11.method1992() * field10779);
        boolean v14 = AuraRotationState.I.player.isTouchingWater() || AuraRotationState.I.player.isSubmergedInWater() || AuraRotationState.I.player.isInLava();
        float f = v15 = this.field10769.method3093() > 0 || v14 ? field10780 : 0.0f;
        if (v10) {
            v15 = field10781;
        }
        float v16 = v15 * field10782;
        float v17 = v15 * field10783;
        if (v10) {
            v12 = RotationHistory.method3560(param1.method3140(), AuraRotationState.method3050(), 1.0f);
            if (this.field10769.method3083()) {
                v13 = RotationHistory.method3560(param1.method3136(), v13 + 2.0f, field10784);
            }
        }
        if (v10 || AuraRotationState.I.player.distanceTo((Entity)param2) < field10785) {
            v12 = param1.method3140() + RotationHistory.method3558(v12 - param1.method3140(), -v16, v16);
            v13 = RotationHistory.method3558(param1.method3136() + RotationHistory.method3558(v13 - param1.method3136(), -v17, v17), field10786, field10787);
        }
        double v18 = field10788;
        v12 += (float)Math.ceil((double)MathHelper.lerp((float)ThreadLocalRandom.current().nextFloat(), (float)3.0f, (float)6.0f) * Math.cos((double)v3 / v18));
        v13 += (float)Math.ceil((double)MathHelper.lerp((float)ThreadLocalRandom.current().nextFloat(), (float)1.5f, (float)3.0f) * Math.sin((double)v3 / v18));
        float v21 = RotationTargetUtil.method1975();
        v12 -= (v12 - param1.method3140()) % v21;
        v13 -= (v13 - param1.method3136()) % v21;
        field10768 = new Rotation(v12, v13);
        return field10768;
    }

    public static void method3049() {
        field10766 = true;
        field10767 = 0L;
        field10768 = new Rotation(0.0f, 0.0f);
    }

    private static float method3050() {
        if (RotationManager.method250()) {
            return RotationManager.method255();
        }
        return AuraRotationState.I.player.getYaw();
    }

    static {
        field10770 = 0.6;
        field10772 = 200L;
        field10773 = 0.1;
        field10774 = 0.04;
        field10775 = 0.1;
        field10776 = 10000.0f;
        field10777 = 10000.0f;
        field10779 = 0.8f;
        field10780 = 105.0f;
        field10781 = 22.0f;
        field10782 = 1.2f;
        field10783 = 1.4f;
        field10784 = 0.7f;
        field10785 = 1.3f;
        field10786 = -90.0f;
        field10787 = 90.0f;
        field10788 = 50.0;
        AuraRotationState.method3049();
    }
}



