/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.shaders.ShaderType
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10151$class_10170
 *  net.minecraft.class_2960
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import com.mojang.blaze3d.shaders.ShaderType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10151;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.utils.render.shader.ShaderStore;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10151.class_10170.class})
public class ShaderLoaderMixin {
    @Inject(method={"method_68498"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$provideEmbeddedSource(class_2960 id, ShaderType type, CallbackInfoReturnable<String> cir) {
        String source = ShaderStore.getSource(id, type);
        if (source != null) {
            cir.setReturnValue((Object)source);
        }
    }
}

