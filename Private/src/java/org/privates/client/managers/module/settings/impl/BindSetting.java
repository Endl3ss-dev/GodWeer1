package org.privates.client.managers.module.settings.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.Setting;

import java.util.function.Supplier;

public class BindSetting extends Setting<Integer> {
    public final boolean allowMouse;

    private Integer cachedValue;

    private static long lastBindTime = 0;
    private static final int BIND_COOLDOWN_MS = 200;

    public BindSetting(Module parent, String name) {
        super(parent, name, -1);
        this.allowMouse = true;
        cachedValue = -1;
    }

    public BindSetting(Module parent, String name, boolean allowMouse) {
        super(parent, name, -1);
        this.allowMouse = allowMouse;
        cachedValue = -1;
    }

    public BindSetting(Module parent, String name, Integer value) {
        super(parent, name, value);
        this.allowMouse = true;
        cachedValue = value;
    }

    public BindSetting(Module parent, String name, Integer value, boolean allowMouse) {
        super(parent, name, value);
        this.allowMouse = allowMouse;
        cachedValue = value;
    }

    @Override
    public BindSetting setVisible(Supplier<Boolean> value) {
        return (BindSetting) super.setVisible(value);
    }

    @Override
    public BindSetting set(Integer value) {
        if (System.currentTimeMillis() - lastBindTime < BIND_COOLDOWN_MS) {
            return this;
        }

        if (isInContainerScreen()) {
            return this;
        }

        if (value != null && (value == 500 || value == 501)) {
            return this;
        }

        if (!allowMouse && isMouseButton(value)) {
            return this;
        }

        BindSetting set = (BindSetting) super.set(value);
        this.cachedValue = super.getValue();
        lastBindTime = System.currentTimeMillis();
        return set;
    }

    public BindSetting setConfigValue(Integer value) {
        BindSetting set = (BindSetting) super.set(value);
        this.cachedValue = super.getValue();
        return set;
    }

    @Override
    public BindSetting onAction(Runnable action) {
        return (BindSetting) super.onAction(() -> {
            if (!isInContainerScreen()) {
                action.run();
                this.cachedValue = super.getValue();
            }
        });
    }

    @Override
    public BindSetting onSetVisible(Runnable action) {
        return (BindSetting) super.onSetVisible(action);
    }

    @Override
    public Integer getValue() {
        Integer value = super.getValue();
        cachedValue = value;
        return value;
    }

    private boolean isInContainerScreen() {
        Minecraft mc = Minecraft.getInstance();
        return mc.currentScreen instanceof ChatScreen;
    }

    private boolean isMouseButton(Integer key) {
        return key != null && key >= 500;
    }
}
