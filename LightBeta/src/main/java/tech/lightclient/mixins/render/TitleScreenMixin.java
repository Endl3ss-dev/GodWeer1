package tech.lightclient.mixins.render;


import tech.lightclient.LightClient;
import tech.lightclient.client.utils.client.IMinecraft;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin implements IMinecraft {
    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void light$openCustomMainMenu(CallbackInfo ci) {
        if (LightClient.isHide()) {
            return;
        }

        var target = LightClient.getOrCreateMainMenuScreen();
        if (target != null && mc.currentScreen != target) {
            mc.setScreen(target);
            ci.cancel();
        }
    }
}

