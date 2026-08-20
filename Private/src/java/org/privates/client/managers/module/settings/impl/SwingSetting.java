package org.privates.client.managers.module.settings.impl;

import lombok.Getter;
import lombok.Setter;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.Setting;

import java.util.function.Supplier;

@Getter
@Setter
public class SwingSetting extends Setting<SwingSetting.SwingData> {

    @Getter
    @Setter
    public static class SwingData {
        private float leftArmSupportX = 0f;
        private float leftArmSupportY = 0f;
        private float leftArmSupportZ = 0f;
        private float leftArmDeviationX = 0f;
        private float leftArmDeviationY = 0f;
        private float leftArmDeviationZ = 0f;
        private float leftArmRotationX = 0f;
        private float leftArmRotationY = 0f;
        private float leftArmRotationZ = 0f;

        private float rightArmSupportX = 0f;
        private float rightArmSupportY = 0f;
        private float rightArmSupportZ = 0f;
        private float rightArmDeviationX = 0f;
        private float rightArmDeviationY = 0f;
        private float rightArmDeviationZ = 0f;
        private float rightArmRotationX = 0f;
        private float rightArmRotationY = 0f;
        private float rightArmRotationZ = 0f;

        private float swingSpeed = 1f;
        private boolean returnToOriginal = true;
    }

    private SwingData cachedValue;

    public SwingSetting(Module parent, String name) {
        super(parent, name, new SwingData());
        this.cachedValue = new SwingData();
    }

    public SwingSetting(Module parent, String name, Supplier<Boolean> visible) {
        super(parent, name, new SwingData(), visible.toString());
        this.cachedValue = new SwingData();
    }

    @Override
    public SwingSetting set(SwingData value) {
        SwingSetting set = (SwingSetting) super.set(value);
        this.cachedValue = value;
        return set;
    }

    @Override
    public SwingSetting setVisible(Supplier<Boolean> value) {
        return (SwingSetting) super.setVisible(value);
    }

    @Override
    public SwingSetting onAction(Runnable action) {
        return (SwingSetting) super.onAction(() -> {
            action.run();
            this.cachedValue = super.getValue();
        });
    }

    @Override
    public SwingSetting onSetVisible(Runnable action) {
        return (SwingSetting) super.onSetVisible(action);
    }

    @Override
    public SwingData getValue() {
        if (cachedValue == null) {
            cachedValue = super.getValue();
        }
        return cachedValue;
    }

    public void setLeftArmSettings(float supportX, float supportY, float supportZ,
                                   float deviationX, float deviationY, float deviationZ,
                                   float rotationX, float rotationY, float rotationZ) {
        SwingData data = getValue();
        data.setLeftArmSupportX(supportX);
        data.setLeftArmSupportY(supportY);
        data.setLeftArmSupportZ(supportZ);
        data.setLeftArmDeviationX(deviationX);
        data.setLeftArmDeviationY(deviationY);
        data.setLeftArmDeviationZ(deviationZ);
        data.setLeftArmRotationX(rotationX);
        data.setLeftArmRotationY(rotationY);
        data.setLeftArmRotationZ(rotationZ);
        set(data);
    }

    public void setRightArmSettings(float supportX, float supportY, float supportZ,
                                    float deviationX, float deviationY, float deviationZ,
                                    float rotationX, float rotationY, float rotationZ) {
        SwingData data = getValue();
        data.setRightArmSupportX(supportX);
        data.setRightArmSupportY(supportY);
        data.setRightArmSupportZ(supportZ);
        data.setRightArmDeviationX(deviationX);
        data.setRightArmDeviationY(deviationY);
        data.setRightArmDeviationZ(deviationZ);
        data.setRightArmRotationX(rotationX);
        data.setRightArmRotationY(rotationY);
        data.setRightArmRotationZ(rotationZ);
        set(data);
    }

    public void setSwingSpeed(float speed) {
        SwingData data = getValue();
        data.setSwingSpeed(speed);
        set(data);
    }

    public void setReturnToOriginal(boolean returnToOriginal) {
        SwingData data = getValue();
        data.setReturnToOriginal(returnToOriginal);
        set(data);
    }
}