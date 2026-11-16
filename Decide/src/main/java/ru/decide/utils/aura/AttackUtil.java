/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1291
 *  net.minecraft.class_1294
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_238
 *  net.minecraft.class_4050
 *  net.minecraft.class_6880
 */
package ru.decide.utils.aura;

import java.util.function.Predicate;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1291;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_4050;
import net.minecraft.class_6880;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.math.MathUtil;
import ru.decide.utils.math.ServerUtil;
import ru.decide.utils.player.SimulatedPlayer;

@Environment(value=EnvType.CLIENT)
public final class AttackUtil
implements IMinecraft {
    private static int count = 0;

    public static void attackEntity(class_1297 entity) {
        AttackUtil.mc.field_1761.method_2918((class_1657)AttackUtil.mc.field_1724, entity);
        AttackUtil.mc.field_1724.method_6104(class_1268.field_5808);
        ++count;
    }

    public static boolean hasMovementRestrictions() {
        return AttackUtil.mc.field_1724.method_6059(class_1294.field_5919) || AttackUtil.mc.field_1724.method_6059(class_1294.field_5902) || AttackUtil.isPlayerInBlock(class_2246.field_10343) || AttackUtil.mc.field_1724.method_5869() || AttackUtil.mc.field_1724.method_5771() || AttackUtil.mc.field_1724.method_6101() || !AttackUtil.canChangeIntoPose(class_4050.field_18076) && AttackUtil.mc.field_1724.method_18276() || AttackUtil.mc.field_1724.method_31549().field_7479;
    }

    public static boolean hasPreMovementRestrictions(SimulatedPlayer simulatedPlayer) {
        return simulatedPlayer.hasStatusEffect((class_6880<class_1291>)class_1294.field_5919) || simulatedPlayer.hasStatusEffect((class_6880<class_1291>)class_1294.field_5902) || AttackUtil.isBoxInBlock(simulatedPlayer.boundingBox, class_2246.field_10343) || simulatedPlayer.isSubmergedInWater() || simulatedPlayer.isInLava() || simulatedPlayer.isClimbing() || !AttackUtil.canChangeIntoPose(class_4050.field_18076) && AttackUtil.mc.field_1724.method_18276() || AttackUtil.mc.field_1724.method_31549().field_7479;
    }

    public static boolean canChangeIntoPose(class_4050 pose) {
        return AttackUtil.mc.field_1724.method_73183().method_8587((class_1297)AttackUtil.mc.field_1724, AttackUtil.mc.field_1724.method_18377(pose).method_30757(AttackUtil.mc.field_1724.method_73189()).method_1011(1.0E-7));
    }

    public static boolean isPlayerInBlock(class_2248 block) {
        return AttackUtil.isBoxInBlock(AttackUtil.mc.field_1724.method_5829().method_1014(-0.001), block);
    }

    public static boolean isBoxInBlock(class_238 box, class_2248 block) {
        return AttackUtil.isBox(box, pos -> AttackUtil.mc.field_1687.method_8320(pos).method_26204().equals(block));
    }

    public static boolean isBox(class_238 box, Predicate<class_2338> pos) {
        return class_2338.method_29715((class_238)box).anyMatch(pos);
    }

    public static boolean isPlayerInCriticalState() {
        boolean crit = AttackUtil.mc.field_1724.field_6017 > (double)(ServerUtil.isCopyTime() || ServerUtil.isFunTime() ? MathUtil.random(0.01f, 0.08f) : 0.0f) && (AttackUtil.mc.field_1724.field_6017 < 0.08 || !SimulatedPlayer.simulateLocalPlayer((int)1).onGround);
        return !AttackUtil.mc.field_1724.method_24828() && crit;
    }

    public static boolean isPrePlayerInCriticalState(SimulatedPlayer simulatedPlayer) {
        boolean crit = simulatedPlayer.fallDistance > 0.0f && ((double)simulatedPlayer.fallDistance < 0.08 || !SimulatedPlayer.simulateLocalPlayer((int)2).onGround);
        return !simulatedPlayer.onGround && crit;
    }

    @Generated
    private AttackUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

