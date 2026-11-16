/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2596
 *  net.minecraft.class_2846
 *  net.minecraft.class_2846$class_2847
 *  net.minecraft.class_2868
 *  net.minecraft.class_304
 *  net.minecraft.class_437
 *  net.minecraft.class_490
 *  net.minecraft.class_9279
 *  net.minecraft.class_9334
 *  org.lwjgl.glfw.GLFW
 */
package ru.decide.module.impl.utils;

import java.util.function.Predicate;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2596;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_304;
import net.minecraft.class_437;
import net.minecraft.class_490;
import net.minecraft.class_9279;
import net.minecraft.class_9334;
import org.lwjgl.glfw.GLFW;
import ru.decide.manager.event_impl.EventKey;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.utils.AutoSwapItems;
import ru.decide.screen.AutoSwapScreen;
import ru.decide.utils.notification.NotificationManager;

@ModuleInfo(name="Auto Swap", category=Category.UTILITIES, desc="\u0421\u0432\u0430\u043f \u0434\u0432\u0443\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432 \u0438\u043b\u0438 \u0432\u044b\u0431\u043e\u0440 \u0447\u0435\u0440\u0435\u0437 \u043a\u0440\u0443\u0433\u043e\u0432\u043e\u0435 \u043c\u0435\u043d\u044e")
@Environment(value=EnvType.CLIENT)
public final class AutoSwapModule
extends Module {
    private static final String[] ITEMS = new String[]{"\u0429\u0438\u0442", "\u0422\u043e\u0442\u0435\u043c", "\u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0438", "\u041b\u044e\u0431\u0430\u044f \u0435\u0434\u0430", "\u0413\u0435\u043f\u043b\u044b", "\u0421\u0444\u0435\u0440\u0430", "\u0420\u0443\u043d\u044b Sunrise"};
    public final BindSetting swapKey = new BindSetting((Module)this, "\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u0441\u0432\u0430\u043f\u0430", -1);
    public final ModeSetting mode = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c", "\u041e\u0431\u044b\u0447\u043d\u044b\u0439", "\u041c\u0443\u043b\u044c\u0442\u0438");
    public final ModeSetting firstItem = new ModeSetting((Module)this, "\u041f\u0435\u0440\u0432\u044b\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442", ITEMS).setVisible(() -> this.mode.is("\u041e\u0431\u044b\u0447\u043d\u044b\u0439"));
    public final ModeSetting secondItem = new ModeSetting((Module)this, "\u0412\u0442\u043e\u0440\u043e\u0439 \u043f\u0440\u0435\u0434\u043c\u0435\u0442", ITEMS).setVisible(() -> this.mode.is("\u041e\u0431\u044b\u0447\u043d\u044b\u0439"));
    public final SliderSetting slotCount = new SliderSetting(this, "\u041a\u043e\u043b-\u0432\u043e \u0441\u043b\u043e\u0442\u043e\u0432", 3.0f, 3.0f, 6.0f, 1.0f).setVisible(() -> this.mode.is("\u041c\u0443\u043b\u044c\u0442\u0438"));
    public final SliderSetting delay = new SliderSetting(this, "\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 (\u0442\u0438\u043a)", 4.0f, 0.0f, 20.0f, 1.0f);
    public final BooleanSetting logSwappedItem = new BooleanSetting((Module)this, "\u041f\u043e\u043a\u0430\u0437\u044b\u0432\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442", false);
    private final AutoSwapItems savedItems = new AutoSwapItems();
    private boolean pendingDefault;
    private int pickSlot = -1;
    private int ticks;
    private int lastHotbarSwap;
    private int freezeTicks;
    private int scheduledSlot = -1;
    private boolean[] savedKeyState;

    public AutoSwapItems savedItems() {
        return this.savedItems;
    }

    public int getSlotCount() {
        return this.slotCount.getValue().intValue();
    }

    @EventHandler
    public void onKey(EventKey event) {
        if (event.getKey() == -1 || event.getKey() != this.swapKey.get().intValue() || AutoSwapModule.mc.field_1755 != null) {
            return;
        }
        if (AutoSwapModule.mc.field_1724 == null || AutoSwapModule.mc.field_1687 == null || AutoSwapModule.mc.field_1761 == null) {
            return;
        }
        if (AutoSwapModule.mc.field_1724.method_7325() || !AutoSwapModule.mc.field_1724.method_5805()) {
            return;
        }
        if (this.mode.is("\u041c\u0443\u043b\u044c\u0442\u0438")) {
            this.savedItems.setCount(this.getSlotCount());
            this.savedItems.load();
            mc.method_1507((class_437)new AutoSwapScreen(this, this.swapKey.get()));
        } else {
            int s;
            if (this.freezeTicks > 0) {
                return;
            }
            Predicate<class_1799> first = this.matcher(this.firstItem.getValue());
            Predicate<class_1799> second = this.matcher(this.secondItem.getValue());
            class_1799 offhand = AutoSwapModule.mc.field_1724.method_6079();
            Predicate<class_1799> target = first.test(offhand) ? second : (second.test(offhand) ? first : ((s = this.find(first, true)) >= 0 ? first : second));
            int slot = this.find(target, true);
            if (slot >= 0) {
                this.scheduleSwap(slot);
            }
        }
    }

    public void scheduleSwap(int slot) {
        if (slot < 0 || this.freezeTicks > 0) {
            return;
        }
        this.freezeTicks = Math.max(1, this.delay.getValue().intValue());
        this.scheduledSlot = slot;
        class_304[] keys = this.getMovementKeys();
        this.savedKeyState = new boolean[keys.length];
        for (int i = 0; i < keys.length; ++i) {
            this.savedKeyState[i] = keys[i].method_1434();
        }
    }

    @EventHandler
    public void onTick(EventTick event) {
        ++this.ticks;
        if (this.freezeTicks > 0) {
            long handle;
            class_304[] keys;
            --this.freezeTicks;
            for (class_304 key : keys = this.getMovementKeys()) {
                key.method_23481(false);
            }
            if (AutoSwapModule.mc.field_1724 != null && GLFW.glfwGetKey((long)(handle = mc.method_22683().method_4490()), (int)341) != 1) {
                AutoSwapModule.mc.field_1724.method_5728(false);
            }
            if (this.freezeTicks <= 0 && this.scheduledSlot >= 0) {
                int slot = this.scheduledSlot;
                this.scheduledSlot = -1;
                this.restoreKeys();
                this.performSwap(slot);
            }
            return;
        }
        if (!this.canSwap() || AutoSwapModule.mc.field_1755 != null) {
            return;
        }
        if (this.pendingDefault) {
            this.pendingDefault = false;
            this.swapDefault();
        }
    }

    public void startPick(int index) {
        if (index < 0 || index >= this.getSlotCount() || AutoSwapModule.mc.field_1724 == null) {
            return;
        }
        this.pickSlot = index;
        this.savedItems.setCount(this.getSlotCount());
        this.savedItems.load();
        mc.method_1507((class_437)new class_490((class_1657)AutoSwapModule.mc.field_1724));
    }

    public boolean isPicking() {
        return this.pickSlot >= 0;
    }

    public void finishPick(class_1799 stack) {
        if (this.pickSlot < 0 || this.pickSlot >= this.getSlotCount() || stack.method_7960()) {
            return;
        }
        if (this.savedItems.set(this.pickSlot, stack)) {
            NotificationManager.send("\u041f\u0440\u0435\u0434\u043c\u0435\u0442 " + (this.pickSlot + 1) + " \u0441\u043e\u0445\u0440\u0430\u043d\u0451\u043d \u0432 Auto Swap", NotificationManager.Type.MODULE);
        }
        this.pickSlot = -1;
    }

    public void removeAt(int index) {
        if (index < 0 || index >= this.getSlotCount()) {
            return;
        }
        if (this.savedItems.set(index, class_1799.field_8037)) {
            NotificationManager.send("\u041f\u0440\u0435\u0434\u043c\u0435\u0442 " + (index + 1) + " \u0443\u0434\u0430\u043b\u0451\u043d \u0438\u0437 Auto Swap", NotificationManager.Type.INFO);
        }
    }

    public void select(int index) {
        if (index < 0 || index >= this.getSlotCount() || this.savedItems.get(index).method_7960()) {
            return;
        }
        int slot = this.find(this.savedItems.predicate(index), false);
        if (slot >= 0) {
            this.scheduleSwap(slot);
        }
    }

    public boolean available(int index) {
        return this.find(this.savedItems.predicate(index), false) >= 0;
    }

    private boolean canSwap() {
        return AutoSwapModule.mc.field_1724 != null && AutoSwapModule.mc.field_1687 != null && AutoSwapModule.mc.field_1761 != null && mc.method_1562() != null && !AutoSwapModule.mc.field_1724.method_7325() && AutoSwapModule.mc.field_1724.method_5805() && AutoSwapModule.mc.field_1724.field_7512 == AutoSwapModule.mc.field_1724.field_7498 && AutoSwapModule.mc.field_1724.field_7512.method_34255().method_7960();
    }

    private void swapDefault() {
        Predicate<class_1799> first = this.matcher(this.firstItem.getValue());
        Predicate<class_1799> second = this.matcher(this.secondItem.getValue());
        class_1799 offhand = AutoSwapModule.mc.field_1724.method_6079();
        if (first.test(offhand)) {
            this.swap(this.find(second, true));
        } else if (second.test(offhand)) {
            this.swap(this.find(first, true));
        } else {
            int slot = this.find(first, true);
            this.swap(slot >= 0 ? slot : this.find(second, true));
        }
    }

    private int find(Predicate<class_1799> predicate, boolean preferEnchanted) {
        if (AutoSwapModule.mc.field_1724 == null) {
            return -1;
        }
        int first = -1;
        for (int slot = 0; slot < 36; ++slot) {
            class_1799 stack = AutoSwapModule.mc.field_1724.method_31548().method_5438(slot);
            if (stack.method_7960() || !predicate.test(stack)) continue;
            if (!preferEnchanted || stack.method_7942()) {
                return slot;
            }
            if (first != -1) continue;
            first = slot;
        }
        return first;
    }

    private void performSwap(int slot) {
        if (slot < 0 || !this.canSwap()) {
            return;
        }
        if (this.logSwappedItem.getValue().booleanValue()) {
            NotificationManager.send(AutoSwapModule.mc.field_1724.method_31548().method_5438(slot).method_7964().getString(), NotificationManager.Type.INFO, AutoSwapModule.mc.field_1724.method_31548().method_5438(slot).method_7972(), 1500L);
        }
        if (slot < 9 && this.ticks - this.lastHotbarSwap > 15) {
            int selected = AutoSwapModule.mc.field_1724.method_31548().method_67532();
            if (selected != slot) {
                mc.method_1562().method_52787((class_2596)new class_2868(slot));
            }
            mc.method_1562().method_52787((class_2596)new class_2846(class_2846.class_2847.field_12969, class_2338.field_10980, class_2350.field_11033));
            if (selected != slot) {
                mc.method_1562().method_52787((class_2596)new class_2868(selected));
            }
            this.lastHotbarSwap = this.ticks;
        } else {
            AutoSwapModule.mc.field_1761.method_2906(AutoSwapModule.mc.field_1724.field_7498.field_7763, slot < 9 ? slot + 36 : slot, 40, class_1713.field_7791, (class_1657)AutoSwapModule.mc.field_1724);
        }
    }

    private void swap(int slot) {
        this.performSwap(slot);
    }

    private class_304[] getMovementKeys() {
        return new class_304[]{AutoSwapModule.mc.field_1690.field_1894, AutoSwapModule.mc.field_1690.field_1881, AutoSwapModule.mc.field_1690.field_1913, AutoSwapModule.mc.field_1690.field_1849, AutoSwapModule.mc.field_1690.field_1903, AutoSwapModule.mc.field_1690.field_1867, AutoSwapModule.mc.field_1690.field_1832};
    }

    private void restoreKeys() {
        int i;
        if (this.savedKeyState == null) {
            return;
        }
        class_304[] keys = this.getMovementKeys();
        int[] glKeys = new int[]{87, 83, 65, 68, 32, 341, 340};
        long handle = mc.method_22683().method_4490();
        for (i = 0; i < keys.length && i < this.savedKeyState.length; ++i) {
            keys[i].method_23481(false);
        }
        for (i = 0; i < keys.length && i < this.savedKeyState.length; ++i) {
            if (!this.savedKeyState[i] || i >= glKeys.length || GLFW.glfwGetKey((long)handle, (int)glKeys[i]) != 1) continue;
            keys[i].method_23481(true);
        }
        this.savedKeyState = null;
    }

    private Predicate<class_1799> matcher(String name) {
        return switch (name) {
            case "\u0429\u0438\u0442" -> stack -> stack.method_31574(class_1802.field_8255);
            case "\u0422\u043e\u0442\u0435\u043c" -> stack -> stack.method_31574(class_1802.field_8288);
            case "\u0424\u0435\u0439\u0435\u0440\u0432\u0435\u0440\u043a\u0438" -> stack -> stack.method_31574(class_1802.field_8639);
            case "\u041b\u044e\u0431\u0430\u044f \u0435\u0434\u0430" -> stack -> stack.method_7909().method_57347().method_57832(class_9334.field_50075);
            case "\u0413\u0435\u043f\u043b\u044b" -> stack -> stack.method_31574(class_1802.field_8463);
            case "\u0420\u0443\u043d\u044b Sunrise" -> stack -> (stack.method_31574(class_1802.field_8450) || stack.method_31574(class_1802.field_8882)) && stack.method_7942();
            case "\u0421\u0444\u0435\u0440\u0430" -> stack -> stack.method_31574(class_1802.field_8575) && (stack.method_57826(class_9334.field_49636) || ((class_9279)stack.method_58695(class_9334.field_49628, (Object)class_9279.field_49302)).method_57461().method_10545("sphereEffect"));
            default -> stack -> false;
        };
    }

    @Override
    public void onDisable() {
        this.reset();
    }

    private void reset() {
        AutoSwapScreen screen;
        this.pendingDefault = false;
        this.pickSlot = -1;
        this.freezeTicks = 0;
        this.scheduledSlot = -1;
        this.restoreKeys();
        class_437 class_4372 = AutoSwapModule.mc.field_1755;
        if (class_4372 instanceof AutoSwapScreen && (screen = (AutoSwapScreen)class_4372).belongsTo(this)) {
            mc.method_1507(null);
        }
    }
}

