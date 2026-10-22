package swerve.events.movement;

import net.minecraft.client.player.ClientInput;
import net.minecraft.world.entity.player.Input;

// Adapted from AlphaDLC/src/main/java/zov/alphadlc/event/list/MoveInputEvent.java
// and mixin/KeyboardInputMixin.java. Reused by the input mixin rather than allocated every tick.
public final class MoveInputEvent {

    private static final Input[] INPUTS = createInputs();

    public float forward;
    public float strafe;
    /** Прыжок: меняется авто-прыжком ауры, иначе повторяет нажатую клавишу. */
    public boolean jump;

    public void read(ClientInput input) {
        forward = input.forwardImpulse;
        strafe = input.leftImpulse;
        jump = input.keyPresses.jump();
    }

    public void apply(ClientInput input) {
        input.forwardImpulse = forward;
        input.leftImpulse = strafe;
        Input original = input.keyPresses;
        int mask = (forward > 0 ? 1 : 0) | (forward < 0 ? 2 : 0)
                | (strafe > 0 ? 4 : 0) | (strafe < 0 ? 8 : 0)
                | (jump ? 16 : 0) | (original.shift() ? 32 : 0)
                | (original.sprint() ? 64 : 0);
        input.keyPresses = INPUTS[mask];
    }

    private static Input[] createInputs() {
        Input[] inputs = new Input[128];
        for (int mask = 0; mask < inputs.length; mask++) {
            inputs[mask] = new Input((mask & 1) != 0, (mask & 2) != 0, (mask & 4) != 0,
                    (mask & 8) != 0, (mask & 16) != 0, (mask & 32) != 0, (mask & 64) != 0);
        }
        return inputs;
    }
}
