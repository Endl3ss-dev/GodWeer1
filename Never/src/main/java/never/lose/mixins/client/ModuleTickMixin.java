package never.lose.mixins.client;

import net.minecraft.client.MinecraftClient;
import never.lose.managers.ModuleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class ModuleTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void endless$dispatchTick(CallbackInfo ci) {
        if (MinecraftClient.getInstance().player == null) {
            return;
        }
        ModuleManager.get().dispatchTick();
    }
}
