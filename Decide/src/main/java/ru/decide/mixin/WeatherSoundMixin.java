/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1113
 *  net.minecraft.class_1140
 *  net.minecraft.class_1140$class_11518
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1113;
import net.minecraft.class_1140;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.Weather;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1140.class})
public abstract class WeatherSoundMixin {
    @Inject(method={"method_4854"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void decide$muteWinterAmbience(class_1113 sound, CallbackInfoReturnable<class_1140.class_11518> cir) {
        if (sound == null) {
            return;
        }
        Weather m = Weather.get();
        if (m != null && m.muteWinterAmbience(sound.method_4775())) {
            cir.setReturnValue((Object)class_1140.class_11518.field_60956);
        }
    }
}

