/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.module.api.settings;

import java.util.function.Supplier;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.ISetting;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public class Setting<Value>
implements ISetting,
IMinecraft {
    private Runnable onAction;
    private Runnable onSetVisible;
    private Value value;
    public final String name;
    private String configKey;
    private Supplier<Boolean> visible = () -> true;
    private Module parent;
    private final Animation animation = new Animation();
    private final ru.decide.utils.animation.satoshi.Animation animation1 = new EaseInOutQuad(300, 1.0);

    public Setting(String name, Value value) {
        this.name = name;
        this.configKey = name;
        this.value = value;
    }

    public Setting(Module parent, String name, Value value) {
        this.parent = parent;
        this.name = name;
        this.configKey = name;
        this.value = value;
        parent.getSettings().add(this);
    }

    public Setting<?> configKey(String key) {
        this.configKey = key;
        return this;
    }

    public Setting<?> set(Value value) {
        this.value = value;
        if (Setting.mc.field_1687 != null && Setting.mc.field_1724 != null && this.onAction != null) {
            this.onAction.run();
        }
        return this;
    }

    @Override
    public Setting<?> setVisible(Supplier<Boolean> value) {
        this.visible = value;
        if (Setting.mc.field_1687 != null && Setting.mc.field_1724 != null && this.onSetVisible != null) {
            this.onSetVisible.run();
        }
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

    @Generated
    public Runnable getOnAction() {
        return this.onAction;
    }

    @Generated
    public Runnable getOnSetVisible() {
        return this.onSetVisible;
    }

    @Generated
    public Value getValue() {
        return this.value;
    }

    @Generated
    public String getConfigKey() {
        return this.configKey;
    }

    @Generated
    public Supplier<Boolean> getVisible() {
        return this.visible;
    }

    @Generated
    public Module getParent() {
        return this.parent;
    }

    @Generated
    public Animation getAnimation() {
        return this.animation;
    }

    @Generated
    public ru.decide.utils.animation.satoshi.Animation getAnimation1() {
        return this.animation1;
    }

    @Generated
    public String getName() {
        return this.name;
    }
}

