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
import ru.decide.cosmetics.melee.MeleeFace;
import ru.decide.cosmetics.melee.MeleeVec3;

@Environment(value=EnvType.CLIENT)
public final class MeleeCube {
    public MeleeVec3 size;
    public MeleeFace[] faces = new MeleeFace[6];
    public MeleeVec3 pivot = new MeleeVec3(0.0f, 0.0f, 0.0f);
    public MeleeVec3 rotation = new MeleeVec3(0.0f, 0.0f, 0.0f);
    public float inflate = 0.0f;
    public boolean mirror = false;

    public MeleeCube(float sizeX, float sizeY, float sizeZ) {
        this.size = new MeleeVec3(sizeX, sizeY, sizeZ);
    }
}

