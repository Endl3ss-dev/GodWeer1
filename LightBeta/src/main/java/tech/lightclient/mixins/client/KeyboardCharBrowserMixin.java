package tech.lightclient.mixins.client;

import net.minecraft.client.Keyboard;
import net.minecraft.client.input.CharInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.lightclient.client.utils.window.WorldWindow;
import tech.lightclient.modules.impl.render.Portals;

@Mixin(Keyboard.class)
public class KeyboardCharBrowserMixin {

    @Inject(method = "onChar", at = @At("HEAD"), cancellable = true)
    private void light$charToBrowser(long window, CharInput input, CallbackInfo ci) {
        WorldWindow address = Portals.addressWindow();
        if (address != null && address.addressFocused) {
            Portals.addressChar(input.codepoint());
            ci.cancel();
            return;
        }

        WorldWindow focused = Portals.keyboardTarget();
        if (focused != null && focused.browser != null) {
            try {
                focused.browser.onCharTyped(input);
            } catch (Throwable ignored) {
            }
            ci.cancel();
        }
    }
}
