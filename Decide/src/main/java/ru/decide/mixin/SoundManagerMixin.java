/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1113
 *  net.minecraft.class_1140$class_11518
 *  net.minecraft.class_1144
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import net.minecraft.class_1144;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.utils.EffectSounds;
import ru.decide.module.impl.utils.SoundController;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1144.class})
public abstract class SoundManagerMixin {
    @Inject(method={"method_4873"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$suppressGameSoundDuringEffectSound(class_1113 sound, CallbackInfoReturnable<class_1140.class_11518> info) {
        if (EffectSounds.isSuppressingGameSounds()) {
            info.setReturnValue((Object)class_1140.class_11518.field_60955);
        }
    }

    @Inject(method={"method_4872"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$suppressDelayedGameSoundDuringEffectSound(class_1113 sound, int delay, CallbackInfo info) {
        if (EffectSounds.isSuppressingGameSounds()) {
            info.cancel();
        }
    }

    @Inject(method={"method_4873"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$scaleSoundVolume(class_1113 sound, CallbackInfoReturnable<class_1140.class_11518> info) {
        class_1113 scaled = SoundController.scale(sound);
        if (scaled != sound) {
            info.setReturnValue((Object)((class_1144)this).method_4873(scaled));
        }
    }

    @Inject(method={"method_4872"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$scaleSoundVolumeDelayed(class_1113 sound, int delay, CallbackInfo info) {
        class_1113 scaled = SoundController.scale(sound);
        if (scaled != sound) {
            ((class_1144)this).method_4872(scaled, delay);
            info.cancel();
        }
    }
}

