/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12131
 *  net.minecraft.class_4587
 *  net.minecraft.class_9975
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12131;
import net.minecraft.class_4587;
import net.minecraft.class_9975;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.ShaderSky;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_9975.class})
public class SkyRenderingMixin {
    @Inject(method={"method_62307"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void hideCelestialObjectsForShaderSky(class_4587 matrices, float pitch, float yaw, float roll, class_12131 moonPhase, float risingLevel, float settingLevel, CallbackInfo ci) {
        ShaderSky shaderSky = ShaderSky.getInstance();
        if (shaderSky != null && shaderSky.isEnabled() && !shaderSky.mode.is("Blur")) {
            ci.cancel();
        }
    }
}

