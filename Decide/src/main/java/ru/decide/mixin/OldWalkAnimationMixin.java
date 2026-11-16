/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10034
 *  net.minecraft.class_572
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10034;
import net.minecraft.class_572;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.OldWalkAnimation;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_572.class})
public abstract class OldWalkAnimationMixin {
    @Inject(method={"method_17087"}, at={@At(value="TAIL")}, require=0)
    private void decide$oldWalkAnimation(class_10034 state, CallbackInfo ci) {
        OldWalkAnimation.apply((class_572)this, state);
    }
}

