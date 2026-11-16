package bench.prestige.portable.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets="net.minecraft.class_310", remap=false)
public abstract class WindowTitleMixin {
    @Inject(method="method_24287()Ljava/lang/String;", at=@At("HEAD"), cancellable=true, remap=false)
    private void portableTitle(CallbackInfoReturnable<String> result) {
        result.setReturnValue("Cracked by @Kazah_Teams and Farelian");
    }
}
