/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.theme.Theme;
import ru.decide.utils.animation.Animation;

@Environment(value=EnvType.CLIENT)
final class GhostChip {
    Theme theme;
    final Animation anim = new Animation();

    GhostChip() {
    }
}

