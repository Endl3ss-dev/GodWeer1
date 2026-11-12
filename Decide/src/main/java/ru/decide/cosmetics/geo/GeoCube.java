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
import ru.decide.cosmetics.geo.GeoQuad;
import ru.decide.cosmetics.geo.Vec3F;

@Environment(value=EnvType.CLIENT)
public class GeoCube {
    public GeoQuad[] quads = new GeoQuad[6];
    public Vec3F size;
    public Vec3F pivot;
    public Vec3F rotation;
    public float inflate;
    public boolean mirror;

    public GeoCube(float x, float y, float z) {
        this.size = new Vec3F(x, y, z);
        this.pivot = new Vec3F(0.0f, 0.0f, 0.0f);
        this.rotation = new Vec3F(0.0f, 0.0f, 0.0f);
        this.inflate = 0.0f;
        this.mirror = false;
    }
}

