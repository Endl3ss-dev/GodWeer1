/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11659
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_4587
 *  net.minecraft.class_742
 *  net.minecraft.class_746
 *  net.minecraft.class_759
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11659;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_746;
import net.minecraft.class_759;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.cosmetics.melee.MeleeFirstPersonRenderer;
import ru.decide.cosmetics.melee.MeleeRules;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_759.class})
public abstract class MeleeHeldItemRendererMixin {
    @Inject(method={"method_22976"}, at={@At(value="HEAD")})
    private void decide$beginMeleeHands(float tickProgress, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo ci) {
        if (MeleeRules.active()) {
            MeleeFirstPersonRenderer.getInstance().beginHands();
        }
    }

    @Inject(method={"method_22976"}, at={@At(value="RETURN")})
    private void decide$flushMeleeHandItem(float tickProgress, class_4587 matrices, class_11659 queue, class_746 player, int light, CallbackInfo ci) {
        if (MeleeRules.active()) {
            MeleeFirstPersonRenderer.getInstance().flushHandItem((class_742)player, matrices, queue, light);
        }
    }

    @Inject(method={"method_3228"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$meleeFirstPersonItem(class_742 player, float tickProgress, float pitch, class_1268 hand, float swingProgress, class_1799 item, float equipProgress, class_4587 matrices, class_11659 queue, int light, CallbackInfo ci) {
        if (!MeleeRules.active()) {
            return;
        }
        if (MeleeRules.hidesEmptyMainHand(player, hand, item)) {
            ci.cancel();
            return;
        }
        if (!MeleeRules.rendersFirstPerson(item)) {
            if (MeleeRules.attachesHandItem(player, hand, item) && MeleeFirstPersonRenderer.getInstance().deferHandItem(item, equipProgress)) {
                ci.cancel();
            }
            return;
        }
        if (player.method_31550() || MeleeRules.usesHand((class_1657)player, hand)) {
            return;
        }
        if (MeleeFirstPersonRenderer.getInstance().renderFirstPerson(player, hand, matrices, queue, light, equipProgress)) {
            ci.cancel();
        }
    }
}

