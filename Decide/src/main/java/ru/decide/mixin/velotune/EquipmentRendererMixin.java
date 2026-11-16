/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10197
 *  net.minecraft.class_1799
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Redirect
 */
package ru.decide.mixin.velotune;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10197;
import net.minecraft.class_1799;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import ru.decide.optimization.velotune.VeloTuneManager;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_10197.class})
abstract class EquipmentRendererMixin {
    EquipmentRendererMixin() {
    }

    @Redirect(method={"method_64077"}, at=@At(value="INVOKE", target="Lnet/minecraft/class_1799;method_7958()Z"), require=0)
    private boolean velotune$skipEquipmentGlint(class_1799 stack) {
        return !EquipmentRendererMixin.velotune$simplifyEquipment() && stack.method_7958();
    }

    private static boolean velotune$simplifyEquipment() {
        return VeloTuneManager.enabled() && VeloTuneManager.config().extreme.enabled && VeloTuneManager.config().extreme.simplifyEquipmentRendering;
    }
}

