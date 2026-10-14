package swerve.mixins.client;

import com.mojang.blaze3d.platform.InputConstants;

import swerve.utils.render.spatial.SpatialMouse;

import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: в режиме прицела от первого лица курсор остаётся захваченным при открытии экрана.
@Mixin(InputConstants.class)
public class MixinInputConstants {

    @Inject(method = "grabOrReleaseMouse", at = @At("HEAD"), cancellable = true)
    private static void swerve$spatialKeepGrab(long window, int cursorValue, double x, double y, CallbackInfo ci) {
        if (cursorValue != GLFW.GLFW_CURSOR_NORMAL) {
            return;
        }
        if (GLFW.glfwGetInputMode(window, GLFW.GLFW_CURSOR) != GLFW.GLFW_CURSOR_DISABLED) {
            return;
        }
        if (SpatialMouse.shouldKeepGrabbed()) {
            ci.cancel();
        }
    }
}
