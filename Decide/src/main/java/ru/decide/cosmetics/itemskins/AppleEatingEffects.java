/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2392
 *  net.minecraft.class_2394
 *  net.minecraft.class_2398
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  net.minecraft.class_746
 */
package ru.decide.cosmetics.itemskins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2392;
import net.minecraft.class_2394;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_746;
import ru.decide.cosmetics.itemskins.ItemSkin;
import ru.decide.cosmetics.itemskins.ItemSkinRegistry;

@Environment(value=EnvType.CLIENT)
public final class AppleEatingEffects {
    private AppleEatingEffects() {
    }

    public static void tick(class_310 client) {
        ItemSkin.Kind kind;
        if (client == null || client.field_1687 == null) {
            return;
        }
        class_746 player = client.field_1724;
        if (player == null || !player.method_6115()) {
            return;
        }
        class_1799 stack = player.method_6030();
        boolean golden = stack.method_31574(class_1802.field_8463);
        boolean enchanted = stack.method_31574(class_1802.field_8367);
        if (!golden && !enchanted) {
            return;
        }
        ItemSkin.Kind kind2 = kind = golden ? ItemSkin.Kind.GOLDEN_APPLE : ItemSkin.Kind.ENCHANTED_GOLDEN_APPLE;
        if (ItemSkinRegistry.equipped(kind) == null) {
            return;
        }
        int use = player.method_6048();
        if (use <= 0) {
            return;
        }
        class_243 look = player.method_5828(1.0f).method_1021(0.32);
        class_243 pos = player.method_73189().method_1031(0.0, 1.32, 0.0).method_1019(look);
        if (use % 3 == 0) {
            for (int i = 0; i < 2; ++i) {
                double dx = (player.method_59922().method_43058() - 0.5) * 0.18;
                double dy = (player.method_59922().method_43058() - 0.2) * 0.16;
                double dz = (player.method_59922().method_43058() - 0.5) * 0.18;
                client.field_1687.method_8406((class_2394)new class_2392(class_2398.field_11218, stack.method_7972()), pos.field_1352 + dx, pos.field_1351 + dy, pos.field_1350 + dz, look.field_1352 * 0.08 + dx, 0.03 + dy * 0.2, look.field_1350 * 0.08 + dz);
            }
            if (enchanted) {
                client.field_1687.method_8406((class_2394)class_2398.field_11207, pos.field_1352, pos.field_1351 + 0.1, pos.field_1350, 0.01, 0.02, 0.01);
            }
        }
        if (use % 6 == 0) {
            client.method_1483().method_4873((class_1113)class_1109.method_24877((class_3414)((class_3414)class_3417.field_20614.comp_349()), (float)0.65f, (float)1.35f));
            client.method_1483().method_4873((class_1113)class_1109.method_24877((class_3414)((class_3414)class_3417.field_20614.comp_349()), (float)0.45f, (float)1.65f));
        }
    }
}

