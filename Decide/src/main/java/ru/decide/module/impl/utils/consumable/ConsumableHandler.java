/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10124
 *  net.minecraft.class_1268
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1839
 *  net.minecraft.class_1937
 *  net.minecraft.class_2596
 *  net.minecraft.class_2663
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_3419
 *  net.minecraft.class_3532
 *  net.minecraft.class_5819
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.module.impl.utils.consumable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10124;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1839;
import net.minecraft.class_1937;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_3532;
import net.minecraft.class_5819;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.utils.ConsumableOptimizer;
import ru.decide.module.impl.utils.consumable.ConsumptionStateHandler;

@Environment(value=EnvType.CLIENT)
public final class ConsumableHandler {
    public static final ConsumptionStateHandler STATE = new ConsumptionStateHandler();
    private static final class_310 CLIENT = class_310.method_1551();

    public static boolean isEnabled() {
        ConsumableOptimizer mod = ConsumableOptimizer.get();
        return mod != null && mod.isEnabled();
    }

    public static void handleItemStackUsage(class_1799 stack, CallbackInfo ci) {
        class_746 player = ConsumableHandler.CLIENT.field_1724;
        if (player == null) {
            return;
        }
        if (player.method_6058() != class_1268.field_5808) {
            return;
        }
        class_10124 component = (class_10124)stack.method_58694(class_9334.field_53964);
        if (component == null) {
            return;
        }
        if (!component.method_62844((class_1309)player, stack) || player.method_31549().field_7479) {
            ConsumableHandler.CLIENT.field_1761.method_2897((class_1657)player);
        }
        if (player.method_6014() <= 0) {
            ConsumableHandler.processAudio(player, stack, component);
            ConsumableHandler.consume(player);
            ci.cancel();
        }
    }

    public static void handlePacket(class_2596<?> packet, CallbackInfo ci) {
        class_2663 status;
        if (ConsumableHandler.CLIENT.field_1687 == null || ConsumableHandler.CLIENT.field_1724 == null) {
            return;
        }
        if (packet instanceof class_2663 && (status = (class_2663)packet).method_11469((class_1937)ConsumableHandler.CLIENT.field_1687) == ConsumableHandler.CLIENT.field_1724 && status.method_11470() == 9 && STATE.isWaitingForServer()) {
            STATE.stopServerWait();
            ci.cancel();
        }
    }

    public static void handleServerSounds(class_3414 sound, CallbackInfo ci) {
        if (STATE.shouldSuppressBurp() && sound == class_3417.field_19149) {
            STATE.resetBurpSuppression();
            ci.cancel();
        } else if (STATE.isSoundSuppressed(sound)) {
            STATE.clearSoundSuppression();
            ci.cancel();
        }
    }

    public static boolean shouldSkipHandAnimationOnSwap() {
        return STATE.shouldSuppressEquipmentAnimation();
    }

    private static void processAudio(class_746 player, class_1799 stack, class_10124 comp) {
        class_3414 sound = (class_3414)comp.comp_3087().comp_349();
        STATE.setSoundSuppression(sound);
        ConsumableHandler.playSound(player, sound, true);
        if (stack.method_7976() == class_1839.field_8950 || stack.method_31574(class_1802.field_8463)) {
            ConsumableHandler.playSound(player, class_3417.field_19149, false, 0.5f, class_3532.method_15344((class_5819)player.method_59922(), (float)0.9f, (float)1.0f));
            STATE.suppressBurp();
        }
    }

    private static void consume(class_746 player) {
        if (player.method_6115()) {
            class_1268 hand = player.method_6058();
            if (!player.method_6047().equals(player.method_5998(hand))) {
                ConsumableHandler.CLIENT.field_1761.method_2897((class_1657)player);
            } else if (!player.method_6047().method_7960() && player.method_6115()) {
                ConsumableHandler.finishUsing(player);
                player.method_6021();
                STATE.startServerWait();
            }
        }
    }

    public static void finishUsing(class_746 user) {
        class_1799 stack = user.method_6047();
        class_10124 component = (class_10124)stack.method_58694(class_9334.field_53964);
        if (component != null) {
            component.method_62843(user.method_59922(), (class_1309)user, stack, 16);
            STATE.suppressEquipmentAnimation();
        }
    }

    private static void playSound(class_746 player, class_3414 sound, boolean randomPitch) {
        float pitch = randomPitch ? class_3532.method_15344((class_5819)player.method_59922(), (float)0.9f, (float)1.1f) : 1.0f;
        ConsumableHandler.playSound(player, sound, randomPitch, 1.0f, pitch);
    }

    private static void playSound(class_746 player, class_3414 sound, boolean randomPitch, float volume, float pitch) {
        ConsumableHandler.CLIENT.field_1687.method_43128((class_1297)player, player.method_23317(), player.method_23318(), player.method_23321(), sound, class_3419.field_15248, volume, pitch);
    }
}

