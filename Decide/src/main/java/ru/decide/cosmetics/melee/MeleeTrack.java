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
import ru.decide.cosmetics.melee.MeleeKeyframe;

@Environment(value=EnvType.CLIENT)
public final class MeleeTrack {
    public static final int ROTATION_OFFSET = 0;
    public static final int POSITION_OFFSET = 3;
    public static final int SCALE_OFFSET = 6;
    private final int target;
    private final MeleeKeyframe rotation;
    private final MeleeKeyframe position;
    private final MeleeKeyframe scale;

    MeleeTrack(int target, MeleeKeyframe rotation, MeleeKeyframe position, MeleeKeyframe scale) {
        this.target = target;
        this.rotation = rotation;
        this.position = position;
        this.scale = scale;
    }

    public int getTarget() {
        return this.target;
    }

    public void sample(float time, float[] out) {
        int base = this.target * 9;
        if (this.rotation != null) {
            this.rotation.sample(time, out, base + 0);
        }
        if (this.position != null) {
            this.position.sample(time, out, base + 3);
        }
        if (this.scale != null) {
            this.scale.sample(time, out, base + 6);
        }
    }
}

