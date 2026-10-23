package sg.mx;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.destra.animation.TimedAnimation;
import ru.destra.core.Module;
import ru.destra.module.NotificationsHudModule;
import ru.destra.util.Direction;

@Mixin(targets = "ru/destra/hud/DraggableHudElement", remap = false)
public abstract class DestraHudDragFixMixin {

    @Shadow
    public Module module;

    @Shadow
    public float width;

    @Shadow
    public float height;

    @Shadow
    public boolean isDragging;

    @Shadow
    public float dragOffsetX;

    @Shadow
    public float dragOffsetY;

    @Shadow
    public TimedAnimation dragHighlightAnim;

    @Shadow
    public abstract float getRenderX();

    @Shadow
    public abstract float getRenderY();

    @Shadow
    public abstract void openSettingsPanel(float x, float y);

    @Inject(method = "onMouseClicked", at = @At("HEAD"), cancellable = true, remap = false)
    private void destra$dragDisabledInEditor(double mouseX, double mouseY, int button,
                                             CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || !(mc.currentScreen instanceof ChatScreen)) {
            return;
        }
        Module mod = this.module;
        if (mod == null) {
            return;
        }
        if (mod.enabled) {
            return;
        }
        if (mod instanceof NotificationsHudModule) {
            return;
        }

        float rx = getRenderX();
        float ry = getRenderY();
        boolean inside = mouseX >= rx && mouseX <= rx + width
                && mouseY >= ry && mouseY <= ry + height;
        if (!inside) {
            return;
        }

        if (button == 1) {
            openSettingsPanel((float) mouseX, (float) mouseY);
            cir.setReturnValue(true);
        } else if (button == 0) {
            isDragging = true;
            dragOffsetX = (float) (mouseX - rx);
            dragOffsetY = (float) (mouseY - ry);
            if (dragHighlightAnim != null) {
                dragHighlightAnim.setDirection(Direction.FORWARDS);
            }
            cir.setReturnValue(true);
        }
    }
}
