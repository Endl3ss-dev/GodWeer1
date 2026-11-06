/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_310
 *  net.minecraft.class_433
 *  net.minecraft.class_437
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package ru.decide.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_433;
import net.minecraft.class_437;
import net.minecraft.class_638;
import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.decide.Client;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.screen.GuiCloseAnimation3D;
import ru.decide.screen.GuiCloseAnimationShatter;
import ru.decide.utils.math.DarkUtils;
import ru.decide.utils.other.GuiMovement;
import ru.decide.utils.render.HitColorOverlay;

@Environment(value=EnvType.CLIENT)
@Mixin(value={class_310.class})
public class ClientMixin {
    @Inject(method={"method_1507"}, at={@At(value="HEAD")}, cancellable=true)
    private void preventPauseDuringAnimation(class_437 screen, CallbackInfo ci) {
        if (screen instanceof class_433 && (GuiCloseAnimation3D.isActive() || GuiCloseAnimationShatter.isActive())) {
            ci.cancel();
        }
    }

    @Inject(method={"method_15993"}, at={@At(value="TAIL")})
    private void applyDarkMode(CallbackInfo ci) {
        class_310 client = class_310.method_1551();
        DarkUtils.enableImmersiveDarkMode(client.method_22683().method_4490());
    }

    @Inject(method={"method_1574"}, at={@At(value="TAIL")})
    private void publishClientTick(CallbackInfo ci) {
        class_310 client = (class_310)this;
        if (client.method_1493()) {
            return;
        }
        class_746 player = client.field_1724;
        class_638 world = client.field_1687;
        if (player == null || world == null) {
            return;
        }
        Client.eventHandler().post(new EventTick());
        GuiMovement.tick();
        HitColorOverlay.tick();
    }

    @Inject(method={"method_24287"}, at={@At(value="RETURN")}, cancellable=true)
    private void getWindowTitle(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue((Object)"Minecraft 1.21.11");
    }
}

