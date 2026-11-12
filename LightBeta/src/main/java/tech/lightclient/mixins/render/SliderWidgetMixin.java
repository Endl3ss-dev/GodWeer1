package tech.lightclient.mixins.render;


import tech.lightclient.modules.impl.render.Beautifully;
import tech.lightclient.ui.screen.MainMenuScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SliderWidget.class)
public abstract class SliderWidgetMixin extends ClickableWidget {
    @Shadow
    protected double value;

    public SliderWidgetMixin(int x, int y, int width, int height, Text message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "renderWidget", at = @At("HEAD"), cancellable = true)
    private void light$renderBeautifulSlider(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        boolean forceCustomWidgets = client.currentScreen instanceof MainMenuScreen mainMenu && mainMenu.shouldForceCustomWidgets();
        if (!Beautifully.shouldRenderCustomButtonWidgets() && !forceCustomWidgets) {
            return;
        }

        if (forceCustomWidgets) {
            Beautifully.queueSliderForced(this, value);
        } else {
            Beautifully.queueSlider(this, value);
        }
        ci.cancel();
    }
}
