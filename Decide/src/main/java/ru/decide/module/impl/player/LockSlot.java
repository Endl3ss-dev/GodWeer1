/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1747
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2480
 *  net.minecraft.class_7923
 */
package ru.decide.module.impl.player;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2480;
import net.minecraft.class_7923;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.utils.math.ServerUtil;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Lock Slot", desc="\u0411\u043b\u043e\u043a\u0438\u0440\u0443\u0435\u0442 \u0441\u043c\u0435\u043d\u0443 \u0441\u043b\u043e\u0442\u0430 \u0445\u043e\u0442\u0431\u0430\u0440\u0430 \u0438 \u0437\u0430\u0449\u0438\u0449\u0430\u0435\u0442 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u043e\u0442 \u0432\u044b\u0431\u0440\u043e\u0441\u0430", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public class LockSlot
extends Module {
    public final BooleanSetting slot1 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 1", false);
    public final BooleanSetting slot2 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 2", false);
    public final BooleanSetting slot3 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 3", false);
    public final BooleanSetting slot4 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 4", false);
    public final BooleanSetting slot5 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 5", false);
    public final BooleanSetting slot6 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 6", false);
    public final BooleanSetting slot7 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 7", false);
    public final BooleanSetting slot8 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 8", false);
    public final BooleanSetting slot9 = new BooleanSetting((Module)this, "\u0421\u043b\u043e\u0442 9", false);
    public final BooleanSetting onlyPvp = new BooleanSetting((Module)this, "\u0422\u043e\u043b\u044c\u043a\u043e PvP", true);
    public final BooleanSetting swords = new BooleanSetting((Module)this, "\u041c\u0435\u0447\u0438", false);
    public final BooleanSetting mace = new BooleanSetting((Module)this, "\u0411\u0443\u043b\u0430\u0432\u0430", false);
    public final BooleanSetting shulkers = new BooleanSetting((Module)this, "\u0428\u0430\u043b\u043a\u0435\u0440\u044b", false);
    private final BooleanSetting[] slots = new BooleanSetting[]{this.slot1, this.slot2, this.slot3, this.slot4, this.slot5, this.slot6, this.slot7, this.slot8, this.slot9};

    public static LockSlot get() {
        return Instance.get(LockSlot.class);
    }

    public boolean isSlotLocked(int slot) {
        if (slot < 0 || slot > 8) {
            return false;
        }
        return this.slots[slot].getValue();
    }

    public boolean isDropProtected(class_1799 stack, int inventorySlot) {
        if (!this.isEnabled() || stack == null || stack.method_7960()) {
            return false;
        }
        if (this.onlyPvp.getValue().booleanValue() && !ServerUtil.isPvp()) {
            return false;
        }
        if (this.isShulker(stack)) {
            return this.shulkers.getValue();
        }
        if (inventorySlot < 0 || inventorySlot > 8) {
            return false;
        }
        return this.swords.getValue() != false && this.isSword(stack) || this.mace.getValue() != false && stack.method_31574(class_1802.field_49814);
    }

    private boolean isShulker(class_1799 stack) {
        class_1747 blockItem;
        class_1792 class_17922 = stack.method_7909();
        return class_17922 instanceof class_1747 && (blockItem = (class_1747)class_17922).method_7711() instanceof class_2480;
    }

    private boolean isSword(class_1799 stack) {
        return class_7923.field_41178.method_10221((Object)stack.method_7909()).method_12832().endsWith("_sword");
    }
}

