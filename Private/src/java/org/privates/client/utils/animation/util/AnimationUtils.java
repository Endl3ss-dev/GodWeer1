package org.privates.client.utils.animation.util;

public class AnimationUtils {
    public static double animate(double current, double target, double speed) {
        double diff = target - current;
        if (diff > speed) {
            return current + speed;
        } else if (diff < -speed) {
            return current - speed;
        }
        return target;
    }
}