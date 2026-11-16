package tech.lightclient.mixins.modules;


import net.minecraft.client.MinecraftClient;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.modules.impl.combat.AimBot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MinecraftClient.class, priority = 999)
public class AimBotTickMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        AimBot aimBot = AimBot.getInstance();
        if (aimBot != null && aimBot.isState()) {
            aimBot.onUpdate(new TickEvent());
        }
    }
}
