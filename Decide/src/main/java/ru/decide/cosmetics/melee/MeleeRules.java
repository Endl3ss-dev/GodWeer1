/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_742
 *  net.minecraft.class_9334
 */
package ru.decide.cosmetics.melee;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_742;
import net.minecraft.class_9334;
import ru.decide.cosmetics.melee.MeleeRegistry;
import ru.decide.cosmetics.melee.MeleeSettings;

@Environment(value=EnvType.CLIENT)
public final class MeleeRules {
    private MeleeRules() {
    }

    public static boolean active() {
        return MeleeRegistry.equipped() != null && (MeleeRules.holdsSkinnedItem(class_1268.field_5808) || MeleeRules.holdsSkinnedItem(class_1268.field_5810));
    }

    public static boolean appliesTo(class_1799 stack) {
        return stack != null && !stack.method_7960() && stack.method_31573(class_3489.field_42611);
    }

    public static boolean holdsSkinnedItem(class_1268 hand) {
        class_310 client = class_310.method_1551();
        return client.field_1724 != null && MeleeRules.appliesTo(client.field_1724.method_5998(hand));
    }

    public static boolean handlesHand(class_1268 hand) {
        return MeleeRules.active() && MeleeRules.holdsSkinnedItem(hand);
    }

    public static boolean handlesAnyHand() {
        return MeleeRules.handlesHand(class_1268.field_5808) || MeleeRules.handlesHand(class_1268.field_5810);
    }

    public static boolean rendersFirstPerson(class_1799 stack) {
        return MeleeRules.active() && MeleeRules.appliesTo(stack) && MeleeSettings.get().isFirstPerson();
    }

    public static boolean rendersThirdPerson(class_1799 stack) {
        return MeleeRules.active() && MeleeRules.appliesTo(stack) && MeleeSettings.get().isThirdPerson();
    }

    public static boolean hidesEmptyMainHand(class_742 player, class_1268 hand, class_1799 stack) {
        return player != null && hand == class_1268.field_5808 && stack != null && stack.method_7960() && MeleeRules.rendersFirstPerson(player.method_6079());
    }

    public static boolean keepsVanillaPose(class_1799 stack) {
        return stack != null && stack.method_57826(class_9334.field_56396);
    }

    public static boolean usesHand(class_1657 player, class_1268 hand) {
        return player != null && player.method_6115() && player.method_6058() == hand;
    }

    public static boolean attachesHandItem(class_742 player, class_1268 hand, class_1799 stack) {
        if (!MeleeRules.active() || stack == null || stack.method_7960()) {
            return false;
        }
        if (MeleeRules.holdsSkinnedItem(hand) || MeleeRules.usesHand((class_1657)player, hand)) {
            return false;
        }
        return !stack.method_57826(class_9334.field_49646) && !MeleeRules.keepsVanillaPose(stack);
    }
}

