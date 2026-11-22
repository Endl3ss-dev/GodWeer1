/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.cosmetics.melee;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.cosmetics.melee.MeleeBone;

@Environment(value=EnvType.CLIENT)
public class MeleeGeoModel {
    public List<MeleeBone> roots = new ArrayList<MeleeBone>();
    public int textureWidth = 64;
    public int textureHeight = 64;

    public Optional<MeleeBone> findPart(String name) {
        for (MeleeBone root : this.roots) {
            MeleeBone found = this.findPart(name, root);
            if (found == null) continue;
            return Optional.of(found);
        }
        return Optional.empty();
    }

    private MeleeBone findPart(String name, MeleeBone bone) {
        if (bone.name.equals(name)) {
            return bone;
        }
        for (MeleeBone child : bone.children) {
            MeleeBone found = this.findPart(name, child);
            if (found == null) continue;
            return found;
        }
        return null;
    }
}

