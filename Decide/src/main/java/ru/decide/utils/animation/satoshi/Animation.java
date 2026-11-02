/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.utils.animation.satoshi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.utils.animation.satoshi.Counter;
import ru.decide.utils.animation.satoshi.Direction;

@Environment(value=EnvType.CLIENT)
public abstract class Animation {
    public Counter timerUtil = new Counter();
    protected int duration;
    protected double endPoint;
    protected Direction direction;

    public Animation(int ms, double endPoint) {
        this.duration = ms;
        this.endPoint = endPoint;
        this.direction = Direction.FORWARDS;
    }

    public Animation(int ms, double endPoint, Direction direction) {
        this.duration = ms;
        this.endPoint = endPoint;
        this.direction = direction;
    }

    public boolean finished(Direction direction) {
        return this.isDone() && this.direction.equals((Object)direction);
    }

    public double getLinearOutput() {
        return 1.0 - (double)this.timerUtil.getTimePassed() / (double)this.duration * this.endPoint;
    }

    public double getEndPoint() {
        return this.endPoint;
    }

    public void setEndPoint(double endPoint) {
        this.endPoint = endPoint;
    }

    public void reset() {
        this.timerUtil.reset();
    }

    public boolean isDone() {
        return this.timerUtil.hasReached(this.duration);
    }

    public void changeDirection() {
        this.setDirection(this.direction.opposite());
    }

    public Direction getDirection() {
        return this.direction;
    }

    public void setDirection(Direction direction) {
        if (this.direction != direction) {
            this.direction = direction;
            this.timerUtil.setTime(System.currentTimeMillis() - ((long)this.duration - Math.min((long)this.duration, this.timerUtil.getTimePassed())));
        }
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    protected boolean correctOutput() {
        return false;
    }

    public long getTimePassed() {
        return this.timerUtil.getTimePassed();
    }

    public float getOutput() {
        if (this.direction == Direction.FORWARDS) {
            if (this.isDone()) {
                return (float)this.endPoint;
            }
            return (float)(this.getEquation(this.timerUtil.getTimePassed()) * this.endPoint);
        }
        if (this.isDone()) {
            return 0.0f;
        }
        if (this.correctOutput()) {
            double revTime = Math.min((long)this.duration, Math.max(0L, (long)this.duration - this.timerUtil.getTimePassed()));
            return (float)(this.getEquation(revTime) * this.endPoint);
        }
        return (float)((1.0 - this.getEquation(this.timerUtil.getTimePassed())) * this.endPoint);
    }

    protected abstract double getEquation(double var1);
}

