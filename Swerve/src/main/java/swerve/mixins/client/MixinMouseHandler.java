package swerve.mixins.client;

import swerve.modules.impl.render.ViewModel;
import swerve.utils.input.KeyBoardUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void swerve$onMousePress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (window != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }
        if (ViewModel.onMouseButton(button, action)) {
            ci.cancel();
            return;
        }
        KeyBoardUtils.callMouse(button, action);
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void swerve$onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (window == Minecraft.getInstance().getWindow().getWindow() && ViewModel.onScroll(vertical)) {
            ci.cancel();
        }
    }
}
