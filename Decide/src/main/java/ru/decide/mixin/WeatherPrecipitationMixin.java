/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_9976
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_9976;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.Weather;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_9976.class})
public abstract class WeatherPrecipitationMixin {
    @Inject(method={"method_62320", "method_62319"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void decide$replacePrecipitation(CallbackInfo ci) {
        Weather m = Weather.get();
        if (m != null && m.isEnabled() && m.mode() != Weather.Mode.OFF) {
            ci.cancel();
        }
    }
}

