/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1937
 *  net.minecraft.class_638
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1937;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.Weather;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_1937.class})
public abstract class WeatherRainGradientMixin {
    @Inject(method={"method_8430"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void decide$visualRain(float delta, CallbackInfoReturnable<Float> cir) {
        if (!(this instanceof class_638)) {
            return;
        }
        Weather m = Weather.get();
        if (m == null || !m.isEnabled() || m.mode() == Weather.Mode.OFF) {
            return;
        }
        cir.setReturnValue((Object)Float.valueOf(m.mode() == Weather.Mode.RAIN ? m.rainIntensity.getValue().floatValue() / 100.0f : 0.0f));
    }
}

