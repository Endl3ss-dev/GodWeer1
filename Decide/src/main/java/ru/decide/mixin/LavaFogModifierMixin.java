/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_11401
 *  net.minecraft.class_4184
 *  net.minecraft.class_638
 *  net.minecraft.class_7285
 *  net.minecraft.class_9779
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11401;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_7285;
import net.minecraft.class_9779;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.NoRender;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_11401.class})
public class LavaFogModifierMixin {
    @Inject(method={"method_42591"}, at={@At(value="HEAD")}, cancellable=true)
    private void onApplyStartEnd(class_7285 fogData, class_4184 camera, class_638 world, float viewDistance, class_9779 tickCounter, CallbackInfo ci) {
        NoRender noRender = NoRender.getInstance();
        if (noRender != null && noRender.isEnabled() && noRender.ignoreLava.getValue().booleanValue()) {
            fogData.field_60582 = Float.MAX_VALUE;
            fogData.field_60584 = Float.MAX_VALUE;
            ci.cancel();
        }
    }
}

