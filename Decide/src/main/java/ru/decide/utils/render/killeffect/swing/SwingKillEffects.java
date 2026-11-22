/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_243
 *  net.minecraft.class_4587
 */
package ru.decide.utils.render.killeffect.swing;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import ru.decide.utils.render.killeffect.swing.SwingCoinsEffect;
import ru.decide.utils.render.killeffect.swing.SwingGraveEffect;
import ru.decide.utils.render.killeffect.swing.SwingLightningEffect;
import ru.decide.utils.render.killeffect.swing.SwingShatterEffect;

@Environment(value=EnvType.CLIENT)
public final class SwingKillEffects {
    private SwingKillEffects() {
    }

    public static void tickAll() {
        SwingLightningEffect.get().tick();
        SwingGraveEffect.get().tick();
        SwingCoinsEffect.get().tick();
        SwingShatterEffect.get().tick();
    }

    public static void renderAll(class_4587 matrices, class_243 cam, float partialTick) {
        SwingLightningEffect.get().render(matrices, cam, partialTick);
        SwingGraveEffect.get().render(matrices, cam, partialTick);
        SwingCoinsEffect.get().render(matrices, cam, partialTick);
        SwingShatterEffect.get().render(matrices, cam, partialTick);
    }

    public static void clearAll() {
        SwingLightningEffect.get().clear();
        SwingGraveEffect.get().clear();
        SwingCoinsEffect.get().clear();
        SwingShatterEffect.get().clear();
    }
}

