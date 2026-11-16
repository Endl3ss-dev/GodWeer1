/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10055
 *  net.minecraft.class_11659
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 *  net.minecraft.class_972
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin.cosmetics;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import net.minecraft.class_972;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.cosmetics.render.CosmeticCapes;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_972.class})
public abstract class CapeFeatureRendererMixin {
    @Inject(method={"method_4177"}, at={@At(value="HEAD")}, cancellable=true)
    private void cancelVanillaCape(class_4587 matrices, class_11659 queue, int light, class_10055 state, float limbAngle, float limbDistance, CallbackInfo ci) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) {
            return;
        }
        if (mc.field_1687.method_8469(state.field_53528) != mc.field_1724) {
            return;
        }
        if (CosmeticCapes.hasCape()) {
            ci.cancel();
        }
    }
}

