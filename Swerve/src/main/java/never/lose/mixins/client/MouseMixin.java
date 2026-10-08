package never.lose.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import never.lose.managers.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void neverlose$onMouseButton(long window, int button, int action, int modifiers, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen != null || button < 0 || action != 1) {
            return;
        }
        ModuleManager.get().handleKey(button);
    }
}
