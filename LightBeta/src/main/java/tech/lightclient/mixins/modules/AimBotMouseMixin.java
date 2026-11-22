package tech.lightclient.mixins.modules;


import net.minecraft.client.Mouse;
import net.minecraft.util.math.MathHelper;
import tech.lightclient.client.events.impl.client.EventAimAssist;
import tech.lightclient.modules.impl.combat.AimBot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mouse.class, priority = 999)
public class AimBotMouseMixin {

    @Shadow
    private double cursorDeltaX;

    @Shadow
    private double cursorDeltaY;

    @Unique
    private long light$lastAimAssistNs = 0L;

    @Inject(method = "updateMouse", at = @At("HEAD"))
    private void onUpdateMouse(double timeDelta, CallbackInfo ci) {
        long now = System.nanoTime();
        double deltaSeconds;
        if (this.light$lastAimAssistNs == 0L) {
            deltaSeconds = 1.0 / 120.0;
        } else {
            deltaSeconds = MathHelper.clamp((now - this.light$lastAimAssistNs) / 1_000_000_000.0, 0.001, 0.2);
        }
        this.light$lastAimAssistNs = now;

        EventAimAssist event = new EventAimAssist(this.cursorDeltaX, this.cursorDeltaY, deltaSeconds);
        AimBot aimBot = AimBot.getInstance();
        if (aimBot != null && aimBot.isState() && !aimBot.isAimAssistHandledThisFrame()) {
            aimBot.onAimAssist(event);
            this.cursorDeltaX = event.getDeltaX();
            this.cursorDeltaY = event.getDeltaY();
        }
    }
}
