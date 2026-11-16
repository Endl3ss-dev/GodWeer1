package tech.lightclient.modules.impl.player;

import tech.lightclient.client.utils.player.rotation.RotationTask;

final class WardenFarmRotationAdapter {
    void invoke4() { RotationTask.remove("warden_farm"); }
    void invoke3(WardenFarmRotation rotation, float yawSpeed, float pitchSpeed, float returnYaw, float returnPitch, int timeout, int priority) {
        if (rotation != null) RotationTask.create("warden_farm", priority);
        if (rotation != null) RotationTask.setTargetRotation(rotation.floatValue, rotation.floatValue2, yawSpeed, pitchSpeed, returnYaw, returnPitch, timeout, priority, 0L);
    }
    static void reset() { RotationTask.remove("warden_farm"); }
}
