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
import ru.decide.module.impl.render.emotions.EmoteRuntime;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_591.class})
public abstract class EmotionPlayerModelMixin {
    @Inject(method={"method_62110"}, at={@At(value="HEAD")})
    private void decidevisuals$emoteRestore(class_10055 state, CallbackInfo ci) {
        EmoteRuntime.restore((class_591)this);
    }

    @Inject(method={"method_62110"}, at={@At(value="RETURN")})
    private void decidevisuals$emoteApply(class_10055 state, CallbackInfo ci) {
        EmoteRuntime.apply((class_591)this, state);
    }
}

