package minced.core;

public final class RecordClass231 {
    public final float yawToTarget;
    public final float pitchToTarget;
    public final float yawDelta;
    public final float pitchDelta;
    public final float finalYawSpeed;
    public final float finalPitchSpeed;

    public RecordClass231(float yawToTarget, float pitchToTarget, float yawDelta, float pitchDelta, float finalYawSpeed, float finalPitchSpeed) {
        this.yawToTarget = yawToTarget;
        this.pitchToTarget = pitchToTarget;
        this.yawDelta = yawDelta;
        this.pitchDelta = pitchDelta;
        this.finalYawSpeed = finalYawSpeed;
        this.finalPitchSpeed = finalPitchSpeed;
    }

    public float yawToTarget() { return yawToTarget; }
    public float pitchToTarget() { return pitchToTarget; }
    public float yawDelta() { return yawDelta; }
    public float pitchDelta() { return pitchDelta; }
    public float finalYawSpeed() { return finalYawSpeed; }
    public float finalPitchSpeed() { return finalPitchSpeed; }
}
