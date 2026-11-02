/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_4587
 *  net.minecraft.class_7833
 *  org.joml.Quaternionfc
 */
package ru.decide.cosmetics.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_7833;
import org.joml.Quaternionfc;

@Environment(value=EnvType.CLIENT)
public class RenderStack {
    private class_4587 stack;

    public void update(class_4587 stack) {
        this.stack = stack;
    }

    public void push() {
        this.stack.method_22903();
    }

    public void pop() {
        this.stack.method_22909();
    }

    public class_4587 get() {
        return this.stack;
    }

    public void translate(float x, float y, float z) {
        this.stack.method_46416(x, y, z);
    }

    public void scale(float x, float y, float z) {
        this.stack.method_22905(x, y, z);
    }

    public void rotateX(float degrees) {
        this.stack.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(degrees));
    }

    public void rotateY(float degrees) {
        this.stack.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(degrees));
    }

    public void rotateZ(float degrees) {
        this.stack.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(degrees));
    }

    public void rotate(float x, float y, float z) {
        if (z != 0.0f) {
            this.rotateZ(z);
        }
        if (y != 0.0f) {
            this.rotateY(y);
        }
        if (x != 0.0f) {
            this.rotateX(x);
        }
    }

    public void rotateDegrees(float x, float y, float z) {
        this.rotate(x, y, z);
    }

    public void rotateXDegrees(float degrees) {
        this.rotateX(degrees);
    }

    public void rotateYDegrees(float degrees) {
        this.rotateY(degrees);
    }

    public void rotateZDegrees(float degrees) {
        this.rotateZ(degrees);
    }
}

