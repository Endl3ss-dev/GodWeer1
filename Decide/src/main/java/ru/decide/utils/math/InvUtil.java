/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_1268
 *  net.minecraft.class_1657
 *  net.minecraft.class_1713
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_2596
 *  net.minecraft.class_2868
 *  net.minecraft.class_2886
 *  net.minecraft.class_304
 *  net.minecraft.class_3675
 */
package ru.decide.utils.math;

import java.util.stream.IntStream;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_1268;
import net.minecraft.class_1657;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2596;
import net.minecraft.class_2868;
import net.minecraft.class_2886;
import net.minecraft.class_304;
import net.minecraft.class_3675;
import ru.decide.utils.annotation.IMinecraft;

@Environment(value=EnvType.CLIENT)
public final class InvUtil
implements IMinecraft {
    public static int getItemInHotBar(class_1792 item) {
        return IntStream.range(0, 9).filter(i -> InvUtil.mc.field_1724.method_31548().method_5438(i).method_7909().equals(item)).findFirst().orElse(-1);
    }

    public static int getItemIndex(class_1792 item) {
        if (InvUtil.mc.field_1724 == null) {
            return -1;
        }
        for (int i = 0; i < InvUtil.mc.field_1724.method_31548().method_5439(); ++i) {
            if (InvUtil.mc.field_1724.method_31548().method_5438(i).method_7909() != item) continue;
            return i;
        }
        return -1;
    }

    public static void addTaskWithLock(Runnable task, long delayMs) {
        if (InvUtil.mc.field_1724 == null) {
            return;
        }
        InvUtil.setMovementKeys(false);
        new Thread(() -> {
            try {
                Thread.sleep(30L);
                mc.execute(() -> {
                    if (task != null) {
                        task.run();
                    }
                });
                Thread.sleep(delayMs);
                mc.execute(() -> InvUtil.setMovementKeys(true));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
                mc.execute(() -> InvUtil.setMovementKeys(true));
            }
        }).start();
    }

    private static void setMovementKeys(boolean state) {
        class_304[] movementKeys = new class_304[]{InvUtil.mc.field_1690.field_1894, InvUtil.mc.field_1690.field_1881, InvUtil.mc.field_1690.field_1913, InvUtil.mc.field_1690.field_1849, InvUtil.mc.field_1690.field_1903, InvUtil.mc.field_1690.field_1867};
        long handle = mc.method_22683().method_4490();
        for (class_304 keyBinding : movementKeys) {
            boolean pressed = state && class_3675.method_15987((class_1041)mc.method_22683(), (int)keyBinding.method_1429().method_1444());
            keyBinding.method_23481(pressed);
        }
    }

    public static boolean doesHotbarHaveItem(class_1792 item) {
        if (InvUtil.mc.field_1724 == null) {
            return false;
        }
        for (int i = 0; i < 9; ++i) {
            if (InvUtil.mc.field_1724.method_31548().method_5438(i).method_7909() != item) continue;
            return true;
        }
        return false;
    }

    public static void inventorySwapClick(class_1792 item, boolean rotation) {
        if (InvUtil.mc.field_1724 == null || InvUtil.mc.field_1761 == null) {
            return;
        }
        int index = InvUtil.getItemIndex(item);
        if (index == -1) {
            return;
        }
        int currentSlot = InvUtil.mc.field_1724.method_31548().method_67532();
        if (index < 9) {
            if (index != currentSlot) {
                InvUtil.mc.field_1724.field_3944.method_52787((class_2596)new class_2868(index));
            }
            InvUtil.mc.field_1724.field_3944.method_52787((class_2596)new class_2886(class_1268.field_5808, 0, InvUtil.mc.field_1724.method_36454(), InvUtil.mc.field_1724.method_36455()));
            if (index != currentSlot) {
                InvUtil.mc.field_1724.field_3944.method_52787((class_2596)new class_2868(currentSlot));
            }
            return;
        }
        int swapSlot = currentSlot;
        InvUtil.mc.field_1761.method_2906(InvUtil.mc.field_1724.field_7512.field_7763, index, swapSlot, class_1713.field_7791, (class_1657)InvUtil.mc.field_1724);
        InvUtil.mc.field_1724.field_3944.method_52787((class_2596)new class_2868(swapSlot));
        InvUtil.mc.field_1724.field_3944.method_52787((class_2596)new class_2886(class_1268.field_5808, 0, InvUtil.mc.field_1724.method_36454(), InvUtil.mc.field_1724.method_36455()));
        InvUtil.mc.field_1724.field_3944.method_52787((class_2596)new class_2868(currentSlot));
        InvUtil.mc.field_1761.method_2906(InvUtil.mc.field_1724.field_7512.field_7763, index, swapSlot, class_1713.field_7791, (class_1657)InvUtil.mc.field_1724);
    }

    public static int find(class_1792 item) {
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            class_1799 stack = InvUtil.mc.field_1724.method_31548().method_5438(i);
            if (stack.method_7960() || stack.method_7909() != item) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    public static int find(class_1792 item, boolean ignoreEnchanted) {
        return InvUtil.find(item, ignoreEnchanted, false);
    }

    public static int find(class_1792 item, boolean ignoreEnchanted, boolean onlyEnchanted) {
        int slot = -1;
        if (onlyEnchanted) {
            for (int i = 0; i < 36; ++i) {
                class_1799 stack = InvUtil.mc.field_1724.method_31548().method_5438(i);
                if (stack.method_7960() || stack.method_7909() != item || !stack.method_7942()) continue;
                slot = i;
                break;
            }
        } else {
            class_1799 stack;
            int i;
            for (i = 0; i < 36; ++i) {
                stack = InvUtil.mc.field_1724.method_31548().method_5438(i);
                if (stack.method_7960() || stack.method_7909() != item || stack.method_7942()) continue;
                slot = i;
                break;
            }
            if (slot == -1 && !ignoreEnchanted) {
                for (i = 0; i < 36; ++i) {
                    stack = InvUtil.mc.field_1724.method_31548().method_5438(i);
                    if (stack.method_7960() || stack.method_7909() != item) continue;
                    slot = i;
                    break;
                }
            }
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }

    @Generated
    private InvUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

