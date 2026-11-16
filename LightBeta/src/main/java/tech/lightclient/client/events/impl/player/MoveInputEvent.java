package tech.lightclient.client.events.impl.player;

import tech.lightclient.client.events.api.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MoveInputEvent extends Event {
    public boolean forward;
    public boolean sideways;
    public float yaw;
    public float targetYaw = Float.NaN;
    public boolean needFix;
    public boolean sneaking;
    public boolean overrideForwardBackward;
    public boolean forwardPressed;
    public boolean backwardPressed;
    public boolean overrideLeftRight;
    public boolean left;
    public boolean right;
    public boolean overrideJump;
    public boolean jumpPressed;
    public boolean overrideSneak;
    public boolean sneakPressed;

    /**
     * Sprint override, published as a separate {@link SprintEvent} by the input mixin. Kept here
     * so the scripting API can offer the single flat "input for this tick" view its docs describe.
     */
    public boolean overrideSprint;
    public boolean sprintPressed;

    // ---- scripting-friendly accessors -------------------------------------------------------
    // These read and write the same state as the fields above, but using the plain names the
    // scripting docs use, and setting a value implies the matching override flag so a script does
    // not have to remember to set both.

    public boolean forward() {
        return overrideForwardBackward ? forwardPressed : forward;
    }

    public void forward(boolean value) {
        overrideForwardBackward = true;
        forwardPressed = value;
    }

    public boolean backward() {
        return backwardPressed;
    }

    public void backward(boolean value) {
        overrideForwardBackward = true;
        backwardPressed = value;
    }

    public boolean left() {
        return left;
    }

    public void left(boolean value) {
        overrideLeftRight = true;
        left = value;
    }

    public boolean right() {
        return right;
    }

    public void right(boolean value) {
        overrideLeftRight = true;
        right = value;
    }

    public boolean jump() {
        return jumpPressed;
    }

    public void jump(boolean value) {
        overrideJump = true;
        jumpPressed = value;
    }

    public boolean sneak() {
        return overrideSneak ? sneakPressed : sneaking;
    }

    public void sneak(boolean value) {
        overrideSneak = true;
        sneakPressed = value;
    }

    public boolean sprint() {
        return sprintPressed;
    }

    public void sprint(boolean value) {
        overrideSprint = true;
        sprintPressed = value;
    }
}
