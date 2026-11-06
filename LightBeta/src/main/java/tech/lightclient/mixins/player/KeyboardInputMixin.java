package tech.lightclient.mixins.player;


import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.player.MoveInputEvent;
import tech.lightclient.client.events.impl.player.SprintEvent;
import tech.lightclient.client.utils.baritone.BaritoneRotationHook;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.client.utils.player.rotation.CorrectionType;
import tech.lightclient.client.events.impl.player.RotationEvent;
import tech.lightclient.client.utils.player.rotation.RotationTask;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public abstract class KeyboardInputMixin implements IMinecraft {
    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("MixinKeyboardInput");

    @ModifyExpressionValue(
            method = "tick",
            at = @At(value = "NEW", target = "(ZZZZZZZ)Lnet/minecraft/util/PlayerInput;")
    )
    private PlayerInput modifyInput(PlayerInput original) {
        var player = mc.player;

        if (player == null) return original;

        MoveInputEvent event = new MoveInputEvent();
        LightClient.getEventBus().post(event);

        SprintEvent eventS = new SprintEvent(original.sprint());
        LightClient.getEventBus().post(eventS);
        // A script that wrote sprint(...) on the move input event wins, since it ran last.
        if (event.isOverrideSprint()) {
            eventS.setSprinting(event.isSprintPressed());
        }
        if (BaritoneRotationHook.isActive()) {
        }
        boolean jump = event.isOverrideJump() ? event.isJumpPressed() : original.jump();
        boolean sneak = event.isOverrideSneak()
                ? event.isSneakPressed()
                : (event.isCancelled() ? event.sneaking : original.sneak());

        boolean shouldCorrect = (event.isNeedFix() || RotationTask.shouldFixMovement() || event.isCancelled()) && !player.isRiding();
        float yaw = (shouldCorrect && event.getYaw() != 0.0f) ? event.getYaw() : player.getYaw();
        RotationEvent rot = null;
        boolean focusedCorrection = false;

        if (shouldCorrect) {
            rot = new RotationEvent(mc.player.getYaw(), mc.player.getPitch(), CorrectionType.FREE);
            LightClient.getEventBus().post(rot);
            focusedCorrection = rot.getCorrectionType() == CorrectionType.FOCUSED;
        }

        if (!shouldCorrect || Float.isNaN(yaw) || focusedCorrection) {
            boolean forward = event.isOverrideForwardBackward() ? event.isForwardPressed() : original.forward();
            boolean backward = event.isOverrideForwardBackward() ? event.isBackwardPressed() : original.backward();
            boolean left = event.isOverrideLeftRight() ? event.isLeft() : original.left();
            boolean right = event.isOverrideLeftRight() ? event.isRight() : original.right();
            return new PlayerInput(
                    forward,
                    backward,
                    left,
                    right,
                    jump,
                    sneak,
                    eventS.isSprinting()
            );
        }

        float z = getMovementMultiplier(original.forward(), original.backward());
        float x = getMovementMultiplier(original.left(), original.right());

        float deltaYaw;
        if (rot.getCorrectionType() == CorrectionType.TARGET && rot.hasTargetYaw()) {
            deltaYaw = rot.getTargetYaw() - yaw;
        } else if (rot.getCorrectionType() == CorrectionType.TARGET && !Float.isNaN(event.getTargetYaw())) {
            deltaYaw = rot.getTargetYaw() - yaw;
        } else {
            deltaYaw = player.getYaw() - yaw;
        }

        float newX = x * MathHelper.cos(deltaYaw * 0.017453292f) - z * MathHelper.sin(deltaYaw * 0.017453292f);
        float newZ = z * MathHelper.cos(deltaYaw * 0.017453292f) + x * MathHelper.sin(deltaYaw * 0.017453292f);

        int movementSideways = Math.round(newX);
        int movementForward = Math.round(newZ);

        boolean forward = event.isCancelled() ? event.isForward() : movementForward > 0;
        boolean backward = event.isCancelled() ? event.isForward() : movementForward < 0;
        boolean left = event.isCancelled() ? event.isSideways() : movementSideways > 0;
        boolean right = event.isCancelled() ? event.isSideways() : movementSideways < 0;

        if (event.isOverrideForwardBackward()) {
            forward = event.isForwardPressed();
            backward = event.isBackwardPressed();
        }
        if (event.isOverrideLeftRight()) {
            left = event.isLeft();
            right = event.isRight();
        }

        return new PlayerInput(
                forward,
                backward,
                left,
                right,
                jump,
                sneak,
                eventS.isSprinting()
        );
    }

    @Inject(method = "tick", at = @At("RETURN"), cancellable = true)
    public void onTick(CallbackInfo ci) {
        MoveInputEvent MoveInputEvent = new MoveInputEvent();
        LightClient.getEventBus().post(MoveInputEvent);

        if (MoveInputEvent.isCancelled()) {
            ci.cancel();
        }
    }

    @Unique
    private static float getMovementMultiplier(boolean positive, boolean negative) {
        if (positive == negative) {
            return 0.0f;
        }
        return positive ? 1.0f : -1.0f;
    }
}
