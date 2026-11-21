/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.melee;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.melee.MeleeVec3;

@Environment(value=EnvType.CLIENT)
public final class MeleeVertex {
    public MeleeVec3 position;
    public float u;
    public float v;

    public MeleeVertex(float x, float y, float z, float u, float v) {
        this(new MeleeVec3(x, y, z), u, v);
    }

    public MeleeVertex(MeleeVec3 position, float u, float v) {
        this.position = position;
        this.u = u;
        this.v = v;
    }
}

