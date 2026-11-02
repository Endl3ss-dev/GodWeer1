/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_4063
 *  net.minecraft.class_9955
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.optimizer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_4063;
import net.minecraft.class_9955;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.Optimizer;
import ru.decide.module.impl.render.ShaderSky;
import ru.decide.module.impl.render.Weather;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_9955.class})
public abstract class CloudRendererMixin {
    @Inject(method={"method_62168"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$cullClouds(int x, class_4063 mode, float tickDelta, class_243 pos, long seed, float bright, CallbackInfo ci) {
        if (Optimizer.cullClouds() || ShaderSky.hidesClouds() || Weather.hidesVanillaClouds()) {
            ci.cancel();
        }
    }
}

