/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_1661
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  net.minecraft.class_746
 */
package ru.decide.module.impl.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_1661;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_746;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.mixin.MinecraftClientAccessor;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.SliderSetting;

@ModuleInfo(name="Spear Helper", desc="\u0410\u0432\u0442\u043e\u0443\u0434\u0430\u0440 \u043a\u043e\u043f\u044c\u0451\u043c \u0441 \u0440\u044b\u0432\u043a\u043e\u043c: \u0434\u043e\u0441\u0442\u0430\u0451\u0442 \u043a\u043e\u043f\u044c\u0451, \u0431\u044c\u0451\u0442 \u0438 \u0432\u043e\u0437\u0432\u0440\u0430\u0449\u0430\u0435\u0442 \u0441\u043b\u043e\u0442", category=Category.UTILITIES)
@Environment(value=EnvType.CLIENT)
public final class SpearHelper
extends Module {
    private static final class_1792[] LUNGE_SPEARS = new class_1792[]{class_1802.field_63384, class_1802.field_63385, class_1802.field_63386, class_1802.field_63388, class_1802.field_63387, class_1802.field_63389, class_1802.field_63390};
    public final BindSetting swapKey = new BindSetting((Module)this, "\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u043a\u043e\u043f\u044c\u044f", 86);
    public final BooleanSetting requireFullCharge = new BooleanSetting((Module)this, "\u0422\u043e\u043b\u044c\u043a\u043e \u043f\u043e\u043b\u043d\u044b\u0439 \u0437\u0430\u0440\u044f\u0434", true);
    public final SliderSetting chargeThreshold = new SliderSetting(this, "\u041f\u043e\u0440\u043e\u0433 \u0437\u0430\u0440\u044f\u0434\u0430", 1.0f, 0.1f, 1.0f, 0.05f).setVisible(() -> this.requireFullCharge.getValue() == false);
    public final BooleanSetting includeOffhand = new BooleanSetting((Module)this, "\u0418\u0441\u043a\u0430\u0442\u044c \u0432\u043e \u0432\u0442\u043e\u0440\u043e\u0439 \u0440\u0443\u043a\u0435", false);
    private int originalSlot = -1;
    private boolean pressed;

    @Override
    protected void onDisable() {
        this.releaseSlot();
        this.pressed = false;
    }

    @EventHandler
    public void onKey(EventKey event) {
        int key = this.swapKey.get();
        if (key <= 0 || event.getKey() != key) {
            return;
        }
        if (SpearHelper.mc.field_1755 != null) {
            return;
        }
        this.pressed = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @EventHandler
    public void onTick(EventTick event) {
        float threshold;
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || client.field_1687 == null || !player.method_5805()) {
            this.releaseSlot();
            return;
        }
        int key = this.swapKey.get();
        if (key <= 0) {
            this.releaseSlot();
            return;
        }
        if (!this.pressed && !SpearHelper.isKeyDown(client, key)) {
            this.releaseSlot();
            return;
        }
        this.pressed = false;
        float charge = player.method_7261(0.0f);
        float f = threshold = this.requireFullCharge.getValue() != false ? 1.0f : this.chargeThreshold.getValue().floatValue();
        if (charge < threshold) {
            return;
        }
        class_1661 inventory = player.method_31548();
        int spearSlot = this.findSpearSlot(inventory);
        if (spearSlot == -1) {
            if (this.spearInOffhand()) {
                ((MinecraftClientAccessor)client).decide$doAttack();
            }
            return;
        }
        int previous = this.originalSlot;
        if (previous == -1) {
            this.originalSlot = previous = inventory.method_67532();
        }
        try {
            inventory.method_61496(spearSlot);
            ((MinecraftClientAccessor)client).decide$doAttack();
        }
        finally {
            inventory.method_61496(previous);
            this.originalSlot = -1;
        }
    }

    private void releaseSlot() {
        class_746 player = class_310.method_1551().field_1724;
        if (this.originalSlot != -1 && player != null) {
            player.method_31548().method_61496(this.originalSlot);
        }
        this.originalSlot = -1;
    }

    private static boolean isKeyDown(class_310 client, int key) {
        return class_3675.method_15987((class_1041)client.method_22683(), (int)key);
    }

    private int findSpearSlot(class_1661 inventory) {
        for (int slot = 0; slot < 9; ++slot) {
            if (!this.isSpear(inventory.method_5438(slot))) continue;
            return slot;
        }
        return -1;
    }

    private boolean spearInOffhand() {
        if (!this.includeOffhand.getValue().booleanValue()) {
            return false;
        }
        class_746 player = class_310.method_1551().field_1724;
        return player != null && this.isSpear(player.method_6079());
    }

    private boolean isSpear(class_1799 stack) {
        if (stack == null || stack.method_7960()) {
            return false;
        }
        return SpearHelper.isLungeSpear(stack.method_7909());
    }

    private static boolean isLungeSpear(class_1792 item) {
        for (class_1792 spear : LUNGE_SPEARS) {
            if (spear != item) continue;
            return true;
        }
        return false;
    }
}

