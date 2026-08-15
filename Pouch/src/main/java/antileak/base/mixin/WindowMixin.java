package antileak.base.mixin;

import antileak.base.api.events.implement.EventAspectRatio;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.client.modules.impl.render.Aspect;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public abstract class WindowMixin {

    @Shadow
    public abstract int getFramebufferWidth();

    @Shadow
    public abstract int getFramebufferHeight();

    @Inject(method = "getFramebufferWidth", at = @At("RETURN"), cancellable = true)
    private void punch$modifyAspectRatioWidth(CallbackInfoReturnable<Integer> cir) {
        if (ModuleClass.INSTANCE == null) return;

        Aspect aspect = ModuleClass.aspect;
        if (aspect == null || !aspect.isEnable()) return;

        EventAspectRatio event = new EventAspectRatio();
        event.call();

        if (event.isCancelled()) {
            float customRatio = aspect.getCustomAspectRatio();
            if (customRatio > 0f) {
                int height = getFramebufferHeight();
                int modifiedWidth = (int) (height * customRatio);
                cir.setReturnValue(modifiedWidth);
            }
        }
    }
}
