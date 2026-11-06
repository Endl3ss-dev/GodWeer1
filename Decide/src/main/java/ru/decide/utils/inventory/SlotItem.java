/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1291
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1844
 *  net.minecraft.class_2561
 *  net.minecraft.class_6880
 *  net.minecraft.class_9290
 *  net.minecraft.class_9334
 */
package ru.decide.utils.inventory;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_2561;
import net.minecraft.class_6880;
import net.minecraft.class_9290;
import net.minecraft.class_9334;

@Environment(value=EnvType.CLIENT)
public enum SlotItem {
    POTION_HLOPUSHKA("\u0425\u043b\u043e\u043f\u0443\u0448\u043a\u0430", 0xFF3232, List.of(SlotItem.sig((class_6880<class_1291>)class_1294.field_5909, 200, 9), SlotItem.sig((class_6880<class_1291>)class_1294.field_5904, 400, 4), SlotItem.sig((class_6880<class_1291>)class_1294.field_5919, 100, 9), SlotItem.sig((class_6880<class_1291>)class_1294.field_5912, 3600, 0))),
    POTION_RADIATION("\u0420\u0430\u0434\u0438\u0430\u0446\u0438\u044f", 0xFF3232, List.of(SlotItem.sig((class_6880<class_1291>)class_1294.field_5899, 1200, 1), SlotItem.sig((class_6880<class_1291>)class_1294.field_5920, 1200, 1), SlotItem.sig((class_6880<class_1291>)class_1294.field_5909, 1800, 2), SlotItem.sig((class_6880<class_1291>)class_1294.field_5903, 1200, 4), SlotItem.sig((class_6880<class_1291>)class_1294.field_5912, 2400, 0))),
    POTION_SLEEP("\u0421\u043d\u043e\u0442\u0432\u043e\u0440\u043d\u043e\u0435", 0xFF3232, List.of(SlotItem.sig((class_6880<class_1291>)class_1294.field_5911, 1800, 1), SlotItem.sig((class_6880<class_1291>)class_1294.field_5901, 200, 1), SlotItem.sig((class_6880<class_1291>)class_1294.field_5920, 1800, 2), SlotItem.sig((class_6880<class_1291>)class_1294.field_5919, 200, 0))),
    POTION_HOLY_WATER("\u0421\u0432\u044f\u0442\u0430\u044f \u0432\u043e\u0434\u0430", 3329330, List.of(SlotItem.sig((class_6880<class_1291>)class_1294.field_5924, 900, 1), SlotItem.sig((class_6880<class_1291>)class_1294.field_5905, 12000, 1), SlotItem.sig((class_6880<class_1291>)class_1294.field_5915, 0, 1))),
    POTION_RAGE("\u0413\u043d\u0435\u0432\u0430", 3329330, List.of(SlotItem.sig((class_6880<class_1291>)class_1294.field_5910, 600, 4), SlotItem.sig((class_6880<class_1291>)class_1294.field_5909, 600, 3))),
    POTION_PALADIN("\u041f\u0430\u043b\u043b\u0430\u0434\u0438\u043d\u0430", 3329330, List.of(SlotItem.sig((class_6880<class_1291>)class_1294.field_5907, 12000, 0), SlotItem.sig((class_6880<class_1291>)class_1294.field_5918, 12000, 0), SlotItem.sig((class_6880<class_1291>)class_1294.field_5914, 1200, 2), SlotItem.sig((class_6880<class_1291>)class_1294.field_5905, 18000, 0))),
    POTION_ASSASSIN("\u0410\u0441\u0441\u0430\u0441\u0438\u043d\u0430", 3329330, List.of(SlotItem.sig((class_6880<class_1291>)class_1294.field_5910, 1200, 3), SlotItem.sig((class_6880<class_1291>)class_1294.field_5904, 6000, 2), SlotItem.sig((class_6880<class_1291>)class_1294.field_5917, 1200, 0), SlotItem.sig((class_6880<class_1291>)class_1294.field_5921, 0, 1)));

    private final String displayName;
    private final int defaultColor;
    private final List<EffectSignature> effectSignatures;

    private SlotItem(String displayName, int defaultColor, List<EffectSignature> effectSignatures) {
        this.displayName = displayName;
        this.defaultColor = defaultColor;
        this.effectSignatures = effectSignatures;
    }

    private static EffectSignature sig(class_6880<class_1291> effect, int duration, int amplifier) {
        return new EffectSignature(effect, duration, amplifier);
    }

    public static SlotItem match(class_1799 stack, Group group) {
        if (group == Group.POTION && stack != null && !stack.method_7960()) {
            if (stack.method_7909() != class_1802.field_8436 && stack.method_7909() != class_1802.field_8150) {
                return null;
            }
            if (SlotItem.isPotionSectionButton(stack)) {
                return null;
            }
            for (SlotItem slotItem : SlotItem.values()) {
                if (!slotItem.matchesEffects(stack)) continue;
                return slotItem;
            }
            return null;
        }
        return null;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getDefaultColor() {
        return this.defaultColor;
    }

    public Group group() {
        return Group.POTION;
    }

    private static boolean isPotionSectionButton(class_1799 stack) {
        class_9290 loreComponent = (class_9290)stack.method_58694(class_9334.field_49632);
        if (loreComponent != null && !loreComponent.comp_2400().isEmpty()) {
            for (class_2561 text : loreComponent.comp_2400()) {
                String string = text.getString();
                if (!string.contains("\u041d\u0430\u0436\u043c\u0438\u0442\u0435, \u0447\u0442\u043e\u0431\u044b \u043f\u0435\u0440\u0435\u0439\u0442\u0438") && !string.contains("\u0420\u0430\u0437\u0434\u0435\u043b \u043e\u0442\u043a\u0440\u044b\u0442")) continue;
                return true;
            }
        }
        return false;
    }

    private boolean matchesEffects(class_1799 stack) {
        class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
        if (contents == null) {
            return false;
        }
        ArrayList<class_1293> effects = new ArrayList<class_1293>();
        for (class_1293 effect : contents.method_57397()) {
            effects.add(effect);
        }
        if (effects.size() != this.effectSignatures.size()) {
            return false;
        }
        for (EffectSignature signature : this.effectSignatures) {
            boolean found = false;
            for (class_1293 instance : effects) {
                if (!signature.matches(instance)) continue;
                found = true;
                break;
            }
            if (found) continue;
            return false;
        }
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public record EffectSignature(class_6880<class_1291> effect, int duration, int amplifier) {
        public boolean matches(class_1293 instance) {
            return instance.method_5579().equals(this.effect) && instance.method_5584() == this.duration && instance.method_5578() == this.amplifier;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Group {
        POTION;

    }
}

