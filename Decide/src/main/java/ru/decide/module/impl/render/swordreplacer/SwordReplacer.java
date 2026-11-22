/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_9280
 *  net.minecraft.class_9334
 */
package ru.decide.module.impl.render.swordreplacer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_9280;
import net.minecraft.class_9334;
import ru.decide.module.impl.render.swordreplacer.WeaponCatalog;

@Environment(value=EnvType.CLIENT)
public final class SwordReplacer {
    private static final String RENDER_KEY_PREFIX = "decide:item_replacer:";
    private static final Map<String, class_9280> MODEL_COMPONENTS = SwordReplacer.createModelComponents();
    private static volatile String diamondModel = "Katana";
    private static volatile String netheriteModel = "Katana";
    private static volatile boolean enabled = false;

    public static void setEnabled(boolean v) {
        enabled = v;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setDiamondModel(String model) {
        if (WeaponCatalog.contains(model)) {
            diamondModel = model;
        }
    }

    public static void setNetheriteModel(String model) {
        if (WeaponCatalog.contains(model)) {
            netheriteModel = model;
        }
    }

    public static String getDiamondModel() {
        return diamondModel;
    }

    public static String getNetheriteModel() {
        return netheriteModel;
    }

    public static String[] allModelNames() {
        return WeaponCatalog.allModels().toArray(new String[0]);
    }

    public static class_1799 getRenderStack(class_1799 stack) {
        String modelName;
        if (!enabled || stack == null || stack.method_7960()) {
            return stack;
        }
        SwordType type = SwordReplacer.resolveType(stack);
        if (type == null) {
            return stack;
        }
        String string = modelName = type == SwordType.DIAMOND ? diamondModel : netheriteModel;
        if ("Vanilla".equals(modelName)) {
            return stack;
        }
        class_9280 component = MODEL_COMPONENTS.get(modelName);
        if (component == null) {
            return stack;
        }
        class_9280 existing = (class_9280)stack.method_58694(class_9334.field_49637);
        if (existing != null && !existing.comp_3356().isEmpty() && component.method_65366(0).equals(existing.method_65366(0))) {
            return stack;
        }
        class_1799 copy = stack.method_7972();
        copy.method_57379(class_9334.field_49637, (Object)SwordReplacer.mergeSelection(existing, component));
        return copy;
    }

    private static SwordType resolveType(class_1799 stack) {
        if (stack.method_31574(class_1802.field_8802)) {
            return SwordType.DIAMOND;
        }
        if (stack.method_31574(class_1802.field_22022)) {
            return SwordType.NETHERITE;
        }
        return null;
    }

    private static class_9280 mergeSelection(class_9280 existing, class_9280 replacement) {
        if (existing == null) {
            return replacement;
        }
        String replacementKey = replacement.method_65366(0);
        ArrayList<String> newStrings = new ArrayList<String>(Math.max(1, existing.comp_3356().size()));
        newStrings.addAll(existing.comp_3356());
        if (newStrings.isEmpty()) {
            newStrings.add(replacementKey);
        } else {
            newStrings.set(0, replacementKey);
        }
        return new class_9280(existing.comp_3354(), existing.comp_3355(), List.copyOf(newStrings), existing.comp_3357());
    }

    private static Map<String, class_9280> createModelComponents() {
        ConcurrentHashMap<String, class_9280> map = new ConcurrentHashMap<String, class_9280>();
        for (String name : WeaponCatalog.customModels()) {
            map.put(name, new class_9280(List.of(), List.of(), List.of(RENDER_KEY_PREFIX + name), List.of()));
        }
        return map;
    }

    @Environment(value=EnvType.CLIENT)
    public static enum SwordType {
        DIAMOND,
        NETHERITE;

    }
}

