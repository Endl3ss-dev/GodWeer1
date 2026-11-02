/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1011
 *  net.minecraft.class_1043
 */
package ru.decide.utils.render;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1011;
import net.minecraft.class_1043;
import ru.decide.module.impl.render.HitColor;

@Environment(value=EnvType.CLIENT)
public final class HitColorOverlay {
    private static final int VANILLA = -1291911168;
    private static final List<Target> TARGETS = new CopyOnWriteArrayList<Target>();

    private HitColorOverlay() {
    }

    public static void attach(class_1043 texture) {
        if (texture == null) {
            return;
        }
        TARGETS.add(new Target(texture));
        HitColorOverlay.apply();
    }

    public static void tick() {
        if (!TARGETS.isEmpty()) {
            HitColorOverlay.apply();
        }
    }

    private static void apply() {
        HitColor module = HitColor.getInstance();
        int argb = module != null && module.isEnabled() ? module.overlayColor() : -1291911168;
        for (Target target : TARGETS) {
            class_1011 image;
            if (target.applied == argb || (image = target.texture.method_4525()) == null) continue;
            for (int y = 0; y < 8; ++y) {
                for (int x = 0; x < 16; ++x) {
                    image.method_61941(x, y, argb);
                }
            }
            target.texture.method_4524();
            target.applied = argb;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Target {
        final class_1043 texture;
        int applied = Integer.MIN_VALUE;

        Target(class_1043 texture) {
            this.texture = texture;
        }
    }
}

