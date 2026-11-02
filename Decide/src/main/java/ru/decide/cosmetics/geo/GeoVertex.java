/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.geo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.geo.Vec3F;

@Environment(value=EnvType.CLIENT)
public class GeoVertex {
    public Vec3F position;
    public float textureU;
    public float textureV;

    public GeoVertex(float x, float y, float z, float u, float v) {
        this.position = new Vec3F(x, y, z);
        this.textureU = u;
        this.textureV = v;
    }

    public GeoVertex(Vec3F position, float u, float v) {
        this.position = position;
        this.textureU = u;
        this.textureV = v;
    }
}

