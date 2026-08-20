package org.privates.client.managers.module.impl.movement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Rotation {
    private float yaw;
    private float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    // Вспомогательные методы для работы с вращением
    public void add(float yaw, float pitch) {
        this.yaw += yaw;
        this.pitch += pitch;
    }

    public void subtract(float yaw, float pitch) {
        this.yaw -= yaw;
        this.pitch -= pitch;
    }

    public Rotation copy() {
        return new Rotation(this.yaw, this.pitch);
    }
}
