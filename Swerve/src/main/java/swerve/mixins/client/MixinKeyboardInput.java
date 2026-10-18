package swerve.mixins.client;

import swerve.events.movement.MoveInputEvent;
import swerve.managers.ModuleManager;
import swerve.modules.impl.movement.ElytraFlight;
import swerve.utils.movement.ElytraFlightRotation;
import swerve.utils.render.spatial.SpatialMath;
import swerve.utils.render.spatial.SpatialState;

import net.minecraft.client.player.ClientInput;
import net.minecraft.client.player.KeyboardInput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// SpatialGui: при довороте камеры относительно игрока WASD остаётся «относительно камеры»;
// признак движения вперёд (спринт) берётся до поворота импульсов.
@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput extends ClientInput {

    @Unique
    private final MoveInputEvent swerve$moveInput = new MoveInputEvent();

    // Adapted from AlphaDLC/src/main/java/zov/alphadlc/mixin/KeyboardInputMixin.java
    @Inject(method = "tick", at = @At("TAIL"), order = 1100)
    private void swerve$elytraMoveInput(CallbackInfo ci) {
        ElytraFlight flight = ModuleManager.get(ElytraFlight.class);
        if (!ElytraFlightRotation.isActive() && (flight == null || !flight.isEnable())) return;
        swerve$moveInput.read(this);
        ElytraFlightRotation.onMoveInput(swerve$moveInput);
        if (flight != null && flight.isEnable()) flight.onMoveInput(swerve$moveInput);
        swerve$moveInput.apply(this);
    }

    @Unique
    private boolean swerve$rotating;

    @Unique
    private boolean swerve$forwardHeld;

    @Inject(method = "tick", at = @At("TAIL"))
    private void swerve$spatialCameraRelative(CallbackInfo ci) {
        swerve$rotating = false;
        if (!SpatialState.active() || SpatialState.hooked == null || !SpatialState.shouldCapture()) {
            return;
        }
        float delta = SpatialMath.rad(SpatialState.cameraYawOffset);
        if (delta == 0.0F) {
            return;
        }
        float sin = (float) Math.sin(delta);
        float cos = (float) Math.cos(delta);
        float left = this.leftImpulse;
        float forward = this.forwardImpulse;
        this.leftImpulse = left * cos - forward * sin;
        this.forwardImpulse = forward * cos + left * sin;
        swerve$forwardHeld = forward > 1.0E-5F;
        swerve$rotating = true;
    }

    @Override
    public boolean hasForwardImpulse() {
        return swerve$rotating ? swerve$forwardHeld : super.hasForwardImpulse();
    }
}
