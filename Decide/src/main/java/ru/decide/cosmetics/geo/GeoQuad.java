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
import ru.decide.cosmetics.geo.GeoVertex;
import ru.decide.cosmetics.geo.Vec3F;

@Environment(value=EnvType.CLIENT)
public class GeoQuad {
    public GeoVertex[] vertices;
    public Vec3F normal;

    public GeoQuad(GeoVertex[] vertices, Vec3F normal) {
        this.vertices = vertices;
        this.normal = normal;
    }

    public GeoQuad(GeoVertex[] vertices, float nx, float ny, float nz) {
        this.vertices = vertices;
        this.normal = new Vec3F(nx, ny, nz);
    }
}

