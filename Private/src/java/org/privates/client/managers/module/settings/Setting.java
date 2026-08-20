package org.privates.client.managers.module.settings;

import lombok.Getter;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.Module;
import org.privates.client.utils.animation.Animation;

import java.util.function.Supplier;

@Getter
public class Setting<Value> implements ISetting, IMinecraft {
    private Runnable onAction;
    private Runnable onSetVisible;
    private Value value;
    @Getter
    public final String name;
    private String description = "";
    private Supplier<Boolean> visible = () -> true;
    private Module parent;
    private final Animation animation = new Animation();

    public Setting(String name, Value value) {
        this.name = name;
        this.value = value;
    }

    public Setting(String name, Value value, String description) {
        this.name = name;
        this.value = value;
        this.description = description;
    }

    public Setting(Module parent, String name, Value value) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        parent.getSettings().add(this);
    }

    public Setting(Module parent, String name, Value value, String description) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.description = description;
        parent.getSettings().add(this);
    }

    public Setting<?> set(Value value) {
        this.value = value;
        if (mc.world != null && mc.player != null && onAction != null) {
            onAction.run();
        }
        return this;
    }

    @Override
    public Setting<?> setVisible(Supplier<Boolean> value) {
        visible = value;
        if (mc.world != null && mc.player != null && onSetVisible != null) {
            onSetVisible.run();
        }
        return this;
    }

    public Setting<?> setDescription(String description) {
        this.description = description;
        return this;
    }

    public Setting<?> onAction(Runnable action) {
        this.onAction = action;
        return this;
    }

    public Setting<?> onSetVisible(Runnable action) {
        this.onSetVisible = action;
        return this;
    }
}