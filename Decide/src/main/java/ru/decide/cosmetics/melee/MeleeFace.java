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
import ru.decide.cosmetics.melee.MeleeVertex;

@Environment(value=EnvType.CLIENT)
public final class MeleeFace {
    public MeleeVertex[] corners;
    public MeleeVec3 normal;

    public MeleeFace(MeleeVertex[] corners, MeleeVec3 normal) {
        this.corners = corners;
        this.normal = normal;
    }

    public MeleeFace(MeleeVertex[] corners, float nx, float ny, float nz) {
        this(corners, new MeleeVec3(nx, ny, nz));
    }
}

