package org.privates.client.utils.animation;

import lombok.Getter;
import org.privates.client.utils.animation.util.Easing;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.player.AnimationType;
import org.privates.client.utils.player.Bezier;
import org.privates.client.utils.player.CubicBezier;
import org.privates.lib.util.time.StopWatch;

@Getter
public class Animation {
    private long start;
    private double duration;
    private double fromValue;
    private double toValue;
    private double value;
    private double prevValue;
    private Easing easing = Easings.LINEAR;
    private boolean debug = false;
    private Runnable finishAction;
    public final StopWatch timer;
    public int speed;
    private Bezier bezier;

    private AnimationType type;


    public Animation() {
        this.timer = new StopWatch();
        this.speed = 1;
        this.bezier = new CubicBezier();

        this.size = 1.0;
        this.forward = false;
        this.type = AnimationType.EASING;

        this.easing = Easings.LINEAR;
    }


    public StopWatch getTimer() {
        return this.timer;
    }


    public Animation run(double valueTo, double duration) {
        return this.run(valueTo, duration, Easings.LINEAR, false);
    }

    public Animation run(double valueTo, double duration, Easing easing) {
        return this.run(valueTo, duration, easing, false);
    }

    public Animation run(double valueTo, double duration, boolean safe) {
        return this.run(valueTo, duration, Easings.LINEAR, safe);
    }

    public Animation run(double valueTo, double duration, Easing easing, boolean safe) {
        if (this.check(safe, valueTo)) {
            if (this.isDebug()) {
                System.out.println("Animate cancelled due to target val equals from val");
            }
        } else {
            this.setEasing(easing).setDuration(duration * 1000.0D).setStart(System.currentTimeMillis()).setFromValue(this.getValue()).setToValue(valueTo);
            if (this.isDebug()) {
                System.out.println("#animate {\n    to value: " + this.getToValue() + "\n    from value: " + this.getValue() + "\n    duration: " + this.getDuration() + "\n}");
            }
        }
        return this;
    }

    public Animation setBezier(Bezier bezier) {
        this.bezier = bezier;
        return this;
    }

    public Animation setSpeed(int speed) {
        this.speed = speed;
        return this;
    }

    public double size;


    public Animation setSize(float size) {
        this.size = size;
        return this;
    }

    public boolean update() {
        this.setPrevValue(this.getValue());
        boolean alive = this.isAlive();
        if (alive) {
            this.setValue(this.interpolate(this.getFromValue(), this.getToValue(), this.getEasing().ease(this.calculatePart())));
        } else {
            this.setStart(0L);
            this.setValue(this.getToValue());
            if (this.finishAction != null) {
                this.finishAction.run();
                this.finishAction = null;
            }
        }
        return alive;
    }

    public Animation animate(double valueTo, double duration, Easing easing, boolean safe) {
        if (this.check(safe, valueTo)) {
            if (this.isDebug()) {
                System.out.println("Animate cancelled due to target val equals from val");
            }

            return this;
        } else {
            this.setType(AnimationType.EASING).setEasing(easing).setDuration(duration * 1000.0).setStart(System.currentTimeMillis()).setFromValue(this.getValue()).setToValue(valueTo);
            if (this.isDebug()) {
                System.out.println("#animate {\n    to value: " + this.getToValue() + "\n    from value: " + this.getValue() + "\n    duration: " + this.getDuration() + "\n}");
            }

            return this;
        }
    }

    public Animation animate(double valueTo, double duration, Bezier bezier, boolean safe) {
        if (this.check(safe, valueTo)) {
            if (this.isDebug()) {
                System.out.println("Animate cancelled due to target val equals from val");
            }

            return this;
        } else {
            this.setType(AnimationType.BEZIER).setBezier(bezier).setDuration(duration * 1000.0).setStart(System.currentTimeMillis()).setFromValue(this.getValue()).setToValue(valueTo);
            if (this.isDebug()) {
                System.out.println("#animate {\n    to value: " + this.getToValue() + "\n    from value: " + this.getValue() + "\n    duration: " + this.getDuration() + "\n    type: " + this.getType().name() + "\n}");
            }

            return this;
        }
    }

    public Animation setType(AnimationType type) {
        this.type = type;
        return this;
    }

    public AnimationType getType() {
        return this.type;
    }

    public boolean isAlive() {
        return !this.isFinished();
    }

    public boolean isFinished() {
        return this.calculatePart() >= 1.0D;
    }

    public double calculatePart() {
        if (this.getStart() == 0L || this.getDuration() == 0.0) {
            return 1.0D;
        }
        return (double) (System.currentTimeMillis() - this.getStart()) / this.getDuration();
    }

    public boolean check(boolean safe, double valueTo) {
        return safe && this.isAlive() && (valueTo == this.getFromValue() || valueTo == this.getToValue() || valueTo == this.getValue());
    }

    public double interpolate(double start, double end, double pct) {
        return start + (end - start) * pct;
    }

    public Animation setStart(long start) {
        this.start = start;
        return this;
    }

    public Animation setDuration(double duration) {
        this.duration = duration;
        return this;
    }

    public Animation setFromValue(double fromValue) {
        this.fromValue = fromValue;
        return this;
    }

    public Animation setToValue(double toValue) {
        this.toValue = toValue;
        return this;
    }

    public Animation setValue(double value) {
        this.value = value;
        return this;
    }

    public Animation setPrevValue(double prevValue) {
        this.prevValue = prevValue;
        return this;
    }

    public Animation setEasing(Easing easing) {
        this.easing = easing;
        return this;
    }

    public Animation setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }

    public boolean forward;


    public Animation onFinished(Runnable action) {
        this.finishAction = action;
        return this;
    }

    public boolean finished(boolean param) {
        return this.timer.elapsedTime() >= this.speed &&
                (param == this.forward);
    }

    public boolean finished() {
        return this.timer.elapsedTime() >= (long) this.speed && this.forward;
    }

    public Animation setForward(boolean forward) {
        if (this.forward != forward) {
            this.forward = forward;
            long currentTime = System.currentTimeMillis();
            double adjustedTime = currentTime -
                    Math.min(this.size, (double) this.timer.getStartTime()) + this.size;
            this.timer.setMs((long) adjustedTime);
        }
        return this;
    }

    public float get() {
        return (float) this.getValue();
    }

    public float getPrev() {
        return (float) this.getPrevValue();
    }

    public Animation set(double value) {
        this.run(value, 0E-100D);
        this.update();
        this.setValue(value);
        return this;
    }

    public void cycle() {
        if (this.finished()) {
            this.setForward(false);
        } else if (this.finished(false)) {
            this.setForward(true);
        }
    }
}