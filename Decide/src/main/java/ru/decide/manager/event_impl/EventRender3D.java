/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4587
 *  org.joml.Matrix4f
 */
package ru.decide.manager.event_impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import org.joml.Matrix4f;
import ru.decide.manager.events.Event;

@Environment(value=EnvType.CLIENT)
public class EventRender3D
extends Event {
    private final class_4587 matrixStack;
    private final float tickDelta;
    private final Matrix4f projectionMatrix;

    public EventRender3D(class_4587 matrixStack, float tickDelta) {
        this(matrixStack, tickDelta, null);
    }

    public EventRender3D(class_4587 matrixStack, float tickDelta, Matrix4f projectionMatrix) {
        this.matrixStack = matrixStack;
        this.tickDelta = tickDelta;
        this.projectionMatrix = projectionMatrix;
    }

    public class_4587 getMatrixStack() {
        return this.matrixStack;
    }

    public float getTickDelta() {
        return this.tickDelta;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }
}

