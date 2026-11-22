/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_332
 *  net.minecraft.class_418
 *  net.minecraft.class_437
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_332;
import net.minecraft.class_418;
import net.minecraft.class_437;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.decide.module.impl.render.WastedDeath;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_418.class})
public abstract class DeathScreenMixin {
    @Inject(method={"method_25426"}, at={@At(value="HEAD")}, cancellable=true)
    private void onInit(CallbackInfo ci) {
        if (!WastedDeath.interceptDeathScreen((class_437)((class_418)this))) {
            return;
        }
        ci.cancel();
    }

    @Inject(method={"method_25394"}, at={@At(value="HEAD")}, cancellable=true)
    private void onRender(class_332 graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (!WastedDeath.isRunning()) {
            return;
        }
        WastedDeath module = WastedDeath.getInstance();
        if (module != null) {
            module.renderOverlay(graphics);
        }
        ci.cancel();
    }
}

