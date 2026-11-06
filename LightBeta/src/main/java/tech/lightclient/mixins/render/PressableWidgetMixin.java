package tech.lightclient.mixins.render;


import tech.lightclient.modules.impl.render.Beautifully;
import tech.lightclient.ui.screen.MainMenuScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin extends ClickableWidget {
    public PressableWidgetMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void light$renderBeautifulButton(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean forceCustomWidgets = client.currentScreen instanceof MainMenuScreen mainMenu && mainMenu.shouldForceCustomWidgets();
        if (!Beautifully.shouldRenderCustomButtonWidgets() && !forceCustomWidgets) {
            return;
        }

        if (forceCustomWidgets) {
            Beautifully.queueButtonForced(this);
        } else {
            Beautifully.queueButton(this);
        }
        ci.cancel();
    }
}
