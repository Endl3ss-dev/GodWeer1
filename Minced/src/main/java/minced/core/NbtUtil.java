/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.NbtComponent
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NbtCompound
 *  net.minecraft.text.CharacterVisitor
 *  net.minecraft.text.Style
 *  net.minecraft.text.Text
 *  net.minecraft.text.TextColor
 *  net.minecraft.util.Formatting
 *  sLM.4ZXYEo
 */
package minced.core;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import sLM._4ZXYEo;

public class NbtUtil
implements CharacterVisitor {
    @Override
    public boolean accept(int p0, net.minecraft.text.Style p1, int p2) { return false; }

    static String cfr_renamed_290;

    static String field12247;
    static String field12248;
    static int field12249;
    static int field12250;

    public static NbtCompound method3506(ItemStack param0) {
        if (param0 == null || param0.isEmpty()) {
            return null;
        }
        NbtCompound v1 = ((NbtComponent)param0.getOrDefault(DataComponentTypes.CUSTOM_DATA, (Object)NbtComponent.DEFAULT)).copyNbt();
        return v1.isEmpty() ? null : v1;
    }

    public static String method3507(ItemStack param0) {
        if (param0 == null || param0.isEmpty()) {
            return "";
        }
        Text v1 = param0.getName();
        String v2 = v1.getString();
        return v2.replace(field12247, "").replace(field12248, "").trim();
    }

    public static int method3508(Text param0) {
        if (param0 == null) {
            return -1;
        }
        Style v1 = param0.getStyle();
        TextColor v2 = v1.getColor();
        if (v2 != null) {
            return field12249 | v2.getRgb();
        }
        Formatting v3 = NbtUtil.method3509(param0.getString());
        if (v3 != null && v3.getColorValue() != null) {
            return field12250 | v3.getColorValue();
        }
        return -1;
    }

    private static Formatting method3509(String param0) {
        if (param0 == null || param0.isEmpty()) {
            return null;
        }
        for (Formatting v4 : Formatting.values()) {
            if (!v4.isColor() || !param0.contains((CharSequence)((Object)_4ZXYEo.фП("makeConcatWithConstants", new Object[]{}, (char)v4.getCode())))) continue;
            return v4;
        }
        return null;
    }

    static {
        field12247 = "[";
        field12248 = "]";
        field12249 = -16777216;
        field12250 = -16777216;
        cfr_renamed_290 = "§\u0001";
    }
}



