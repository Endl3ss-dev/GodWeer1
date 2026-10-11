package swerve.mixins.client;

import swerve.utils.input.KeyBoardUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @Inject(method = "onPress", at = @At("HEAD"))
    private void swerve$onMousePress(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (window != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }
        KeyBoardUtils.callMouse(button, action);
    }

    // Колесо перехватывается, только когда ViewModel тащит руку (иначе скролл остаётся ванильным).
    // require = 0: имя колбэка GLFW может отличаться — тогда просто нет перехвата, без краша.
    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true, require = 0)
    private void swerve$onMouseScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        if (window != Minecraft.getInstance().getWindow().getWindow()) {
            return;
        }
        if (KeyBoardUtils.callScroll(yOffset)) {
            ci.cancel();
        }
    }
}
