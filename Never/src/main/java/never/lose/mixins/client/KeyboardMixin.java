package never.lose.mixins.client;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import never.lose.managers.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"))
    private void endless$onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen != null || key == -1 || action != 1) {
            return;
        }
        ModuleManager.get().handleKey(key);
    }
}
