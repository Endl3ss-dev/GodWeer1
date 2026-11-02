/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 */
package ru.decide.cosmetics.melee;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
final class BoneMatrices {
    Matrix4f[] world = new Matrix4f[0];
    Matrix4f[] combined = new Matrix4f[0];
    Matrix3f[] normals = new Matrix3f[0];
    boolean[] visible = new boolean[0];

    BoneMatrices() {
    }

    void ensureCapacity(int size) {
        if (this.world.length >= size) {
            return;
        }
        this.world = new Matrix4f[size];
        this.combined = new Matrix4f[size];
        this.normals = new Matrix3f[size];
        this.visible = new boolean[size];
        for (int i = 0; i < size; ++i) {
            this.world[i] = new Matrix4f();
            this.combined[i] = new Matrix4f();
            this.normals[i] = new Matrix3f();
        }
    }
}

