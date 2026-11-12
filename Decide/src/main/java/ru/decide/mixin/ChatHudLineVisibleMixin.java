/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_303$class_7590
 *  net.minecraft.class_7591
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_303;
import net.minecraft.class_7591;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.module.impl.render.BetterMinecraft;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_303.class_7590.class})
public abstract class ChatHudLineVisibleMixin {
    @Inject(method={"comp_897"}, at={@At(value="HEAD")}, cancellable=true)
    private void decide$removeIndicator(CallbackInfoReturnable<class_7591> cir) {
        if (BetterMinecraft.chatAnimationsEnabled()) {
            cir.setReturnValue(null);
        }
    }
}

