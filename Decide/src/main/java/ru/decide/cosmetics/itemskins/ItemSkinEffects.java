/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1268
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2394
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.decide.cosmetics.itemskins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.decide.cosmetics.itemskins.ItemSkin;
import ru.decide.cosmetics.itemskins.ItemSkinRegistry;

@Environment(value=EnvType.CLIENT)
public final class ItemSkinEffects {
    private static int ticks;

    private ItemSkinEffects() {
    }

    public static void tick(class_310 client) {
        ItemSkin.Kind kind;
        if (client == null || client.field_1724 == null || client.field_1687 == null) {
            return;
        }
        if (++ticks % 3 != 0) {
            return;
        }
        class_746 player = client.field_1724;
        class_1799 stack = player.method_6047();
        if (stack.method_31574(class_1802.field_8102)) {
            kind = ItemSkin.Kind.BOW;
        } else if (stack.method_31574(class_1802.field_8399)) {
            kind = ItemSkin.Kind.CROSSBOW;
        } else {
            stack = player.method_5998(class_1268.field_5810);
            if (stack.method_31574(class_1802.field_8102)) {
                kind = ItemSkin.Kind.BOW;
            } else if (stack.method_31574(class_1802.field_8399)) {
                kind = ItemSkin.Kind.CROSSBOW;
            } else {
                return;
            }
        }
        ItemSkin skin = ItemSkinRegistry.equipped(kind);
        if (skin == null) {
            return;
        }
        class_243 look = player.method_5828(1.0f);
        class_243 pos = player.method_73189().method_1031(0.0, 1.25, 0.0).method_1019(look.method_1021(0.38));
        double ox = (player.method_59922().method_43058() - 0.5) * 0.55;
        double oy = (player.method_59922().method_43058() - 0.3) * 0.45;
        double oz = (player.method_59922().method_43058() - 0.5) * 0.55;
        class_2394 particle = ItemSkinEffects.particleFor(skin);
        client.field_1687.method_8406(particle, pos.field_1352 + ox, pos.field_1351 + oy, pos.field_1350 + oz, look.field_1352 * 0.015, 0.025, look.field_1350 * 0.015);
        if (player.method_6048() > 0 && ticks % 6 == 0) {
            client.field_1687.method_8406(particle, pos.field_1352 - ox, pos.field_1351 + oy * 1.3, pos.field_1350 - oz, -look.field_1352 * 0.01, 0.03, -look.field_1350 * 0.01);
        }
    }

    private static class_2394 particleFor(ItemSkin skin) {
        String key = skin.getKey();
        if (key.contains("_ny_")) {
            return class_2398.field_28013;
        }
        int variant = 0;
        int us = key.lastIndexOf(95);
        if (us >= 0) {
            try {
                variant = Integer.parseInt(key.substring(us + 1));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return switch (variant % 15) {
            case 0 -> class_2398.field_11240;
            case 1 -> class_2398.field_28013;
            case 2 -> class_2398.field_11214;
            case 3 -> class_2398.field_11207;
            case 4 -> class_2398.field_11207;
            case 5 -> class_2398.field_28479;
            case 6 -> class_2398.field_11205;
            case 7 -> class_2398.field_29644;
            case 8 -> class_2398.field_43379;
            case 9 -> class_2398.field_11207;
            case 10 -> class_2398.field_22246;
            case 11 -> class_2398.field_11248;
            case 12 -> class_2398.field_11247;
            case 13 -> class_2398.field_29644;
            default -> class_2398.field_43379;
        };
    }
}

