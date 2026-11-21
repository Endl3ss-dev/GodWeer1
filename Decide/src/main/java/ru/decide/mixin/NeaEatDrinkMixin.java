/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_591
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.NotEnoughAnimations;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_591.class})
public abstract class NeaEatDrinkMixin {
    @Inject(method={"method_62110"}, at={@At(value="RETURN")}, require=0)
    private void decide$neaEatDrink(class_10055 state, CallbackInfo ci) {
        class_591 model = (class_591)this;
        if (NotEnoughAnimations.applyItemSwap(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyElytra(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyEatDrink(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyHugging(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyPetting(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyHorse(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyBoat(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyLadder(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyBurning(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyFreezing(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyNaruto(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyFalling(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyCrawling(model, state)) {
            return;
        }
        if (NotEnoughAnimations.applyMapHolding(model, state)) {
            return;
        }
        NotEnoughAnimations.applyLookAtItem(model, state);
        NotEnoughAnimations.applySmoothingAndRotation(model, state);
    }
}

