package tech.lightclient.mixins.client;


import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventKey;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
        if (window != client.getWindow().getHandle()) {
            return;
        }

        if (client.currentScreen == null) {
            boolean f3Down = GLFW.glfwGetKey(client.getWindow().getHandle(), GLFW.GLFW_KEY_F3) == GLFW.GLFW_PRESS;
            client.options.debugModifierKey.setPressed(f3Down);
        }

        if (LightClient.getEventBus() == null || client.currentScreen != null) {
            return;
        }
        InputUtil.Type type = input.key() >= 1000 ? InputUtil.Type.MOUSE : InputUtil.Type.KEYSYM;
        EventKey event = new EventKey(input.key(), action, type);
        LightClient.getEventBus().post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
