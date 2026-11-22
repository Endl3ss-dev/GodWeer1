/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10014
 *  net.minecraft.class_10444
 *  net.minecraft.class_11566
 *  net.minecraft.class_11659
 *  net.minecraft.class_1799
 *  net.minecraft.class_1935
 *  net.minecraft.class_1937
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4608
 *  net.minecraft.class_7833
 *  net.minecraft.class_811
 *  net.minecraft.class_9334
 *  org.joml.Quaternionfc
 */
package ru.decide.cosmetics.itemskins;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10014;
import net.minecraft.class_10444;
import net.minecraft.class_11566;
import net.minecraft.class_11659;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_811;
import net.minecraft.class_9334;
import org.joml.Quaternionfc;
import ru.decide.cosmetics.itemskins.ItemSkin;
import ru.decide.cosmetics.itemskins.ItemSkinRegistry;

@Environment(value=EnvType.CLIENT)
public final class EndCrystal3DRenderer {
    private static class_10444 state;
    private static int selected;

    private EndCrystal3DRenderer() {
    }

    public static void render(class_10014 entityState, class_4587 matrices, class_11659 queue) {
        ItemSkin skin = ItemSkinRegistry.equipped(ItemSkin.Kind.END_CRYSTAL);
        if (skin == null) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.field_1687 == null) {
            return;
        }
        int index = skin.getCosmeticIndex();
        if (state == null || selected != index) {
            class_1799 stack = new class_1799((class_1935)ItemSkin.Kind.END_CRYSTAL.item());
            stack.method_57379(class_9334.field_49637, (Object)ItemSkinRegistry.componentForSkin(skin));
            state = new class_10444();
            client.method_65386().method_65598(state, stack, class_811.field_4319, (class_1937)client.field_1687, (class_11566)client.field_1724, 0);
            selected = index;
        }
        float bob = class_3532.method_15374((double)(entityState.field_53328 * 0.08f)) * 0.12f + 0.12f;
        matrices.method_22903();
        matrices.method_46416(0.0f, bob, 0.0f);
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(entityState.field_53328 * 2.4f));
        matrices.method_22905(1.0f, 1.0f, 1.0f);
        state.method_65604(matrices, queue, entityState.field_61820, class_4608.field_21444, entityState.field_61821);
        matrices.method_22909();
    }

    static {
        selected = Integer.MIN_VALUE;
    }
}

