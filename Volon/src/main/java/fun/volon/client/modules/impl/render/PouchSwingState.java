package fun.volon.client.modules.impl.render;

/**
 * Tracks pouch swing state for alternating left/right sword tilt animation.
 */
public class PouchSwingState {
    private static boolean currentDirection = true; // true = right, false = left
    private static float lastSwingProgress = 0f;
    private static boolean wasSwinging = false;

    /**
     * Updates swing state and alternates direction on new swings.
     * @param swingProgress Current swing progress (0.0 to 1.0)
     */
    public static void updateSwing(float swingProgress) {
        boolean isSwinging = swingProgress > 0.01f;
        
        // Detect new swing start (transition from not swinging to swinging)
        if (isSwinging && !wasSwinging) {
            // New swing detected - alternate direction
            currentDirection = !currentDirection;
        }
        
        wasSwinging = isSwinging;
        lastSwingProgress = swingProgress;
    }

    /**
     * Gets current tilt direction.
     * @return true for right tilt, false for left tilt
     */
    public static boolean getCurrentDirection() {
        return currentDirection;
    }

    /**
     * Resets state (useful for testing or module toggle).
     */
    public static void reset() {
        currentDirection = true;
        lastSwingProgress = 0f;
        wasSwinging = false;
    }
}
