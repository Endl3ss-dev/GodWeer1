package never.lose.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import never.lose.modules.impl.Render.CustomSwing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class HeldItemRendererMixin {

    @Inject(method = "swingArm(FFLnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/util/Arm;)V",
            at = @At("HEAD"), cancellable = true, require = 0)
    private void endless$customSwing(float swingProgress, float equipProgress, MatrixStack matrices, int armX, Arm arm, CallbackInfo ci) {
        CustomSwing module = CustomSwing.INSTANCE;
        if (module == null || !module.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || arm != client.player.getMainArm()) {
            return;
        }
        if (module.applyTo(matrices, arm, swingProgress, equipProgress)) {
            ci.cancel();
        }
    }
}
