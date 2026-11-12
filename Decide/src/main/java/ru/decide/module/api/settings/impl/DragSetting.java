/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 */
package ru.decide.module.api.settings.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.utils.animation.Animation;

@Environment(value=EnvType.CLIENT)
public class DragSetting
extends Setting<Vector2f> {
    public final Vector2f targetPosition = new Vector2f();
    public final Vector2f position = new Vector2f();
    public final Vector2f size = new Vector2f();
    public boolean active = true;
    public boolean structure = false;
    public boolean lockX = false;
    public Animation animationX = new Animation();
    public Animation animationY = new Animation();

    public DragSetting(Module parent, String name) {
        super(parent, name, new Vector2f());
    }

    public DragSetting(Module parent, String name, Vector2f value) {
        super(parent, name, value);
        this.position.set((Vector2fc)value);
        this.targetPosition.set((Vector2fc)value);
    }

    public DragSetting(Module parent, String name, Vector2f value, boolean active) {
        super(parent, name, value);
        this.position.set((Vector2fc)value);
        this.targetPosition.set((Vector2fc)value);
        this.active = active;
    }

    public DragSetting(Module parent, String name, Vector2f value, boolean active, boolean structure) {
        super(parent, name, value);
        this.position.set((Vector2fc)value);
        this.targetPosition.set((Vector2fc)value);
        this.active = active && !structure;
        this.structure = structure;
    }

    @Override
    public Setting<?> set(Vector2f value) {
        this.position.set((Vector2fc)value);
        this.targetPosition.set((Vector2fc)value);
        return super.set(value);
    }

    @Override
    public Vector2f getValue() {
        return this.position;
    }

    public DragSetting onAction(Runnable action) {
        return (DragSetting)super.onAction(action);
    }

    public DragSetting onSetVisible(Runnable action) {
        return (DragSetting)super.onSetVisible(action);
    }
}

