/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1041
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 */
package ru.decide.utils.other;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1041;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import ru.decide.module.impl.display.ClickGui;
import ru.decide.screen.Menu;

@Environment(value=EnvType.CLIENT)
public final class GuiMovement {
    private static boolean active = false;
    private static boolean jumpWasDown = false;

    private GuiMovement() {
    }

    public static void tick() {
        boolean allowed;
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.field_1724 == null || mc.method_22683() == null) {
            GuiMovement.release(mc);
            return;
        }
        ClickGui module = ClickGui.getInstance();
        boolean bl = allowed = module != null && module.runWithGui.getValue() != false && mc.field_1755 instanceof Menu;
        if (!allowed) {
            GuiMovement.release(mc);
            return;
        }
        active = true;
        class_1041 window = mc.method_22683();
        boolean forward = GuiMovement.down(window, 87);
        boolean back = GuiMovement.down(window, 83);
        boolean left = GuiMovement.down(window, 65);
        boolean right = GuiMovement.down(window, 68);
        boolean jump = GuiMovement.down(window, 32);
        boolean sneak = GuiMovement.down(window, 340) || GuiMovement.down(window, 344);
        boolean sprint = GuiMovement.down(window, 341) || GuiMovement.down(window, 345);
        mc.field_1690.field_1894.method_23481(forward);
        mc.field_1690.field_1881.method_23481(back);
        mc.field_1690.field_1913.method_23481(left);
        mc.field_1690.field_1849.method_23481(right);
        mc.field_1690.field_1903.method_23481(jump);
        mc.field_1690.field_1832.method_23481(sneak);
        mc.field_1690.field_1867.method_23481(sprint || forward && !jumpWasDown);
        jumpWasDown = jump;
    }

    private static boolean down(class_1041 window, int key) {
        return class_3675.method_15987((class_1041)window, (int)key);
    }

    private static void release(class_310 mc) {
        if (!active || mc == null) {
            return;
        }
        active = false;
        jumpWasDown = false;
        if (mc.field_1690 == null) {
            return;
        }
        mc.field_1690.field_1894.method_23481(false);
        mc.field_1690.field_1881.method_23481(false);
        mc.field_1690.field_1913.method_23481(false);
        mc.field_1690.field_1849.method_23481(false);
        mc.field_1690.field_1903.method_23481(false);
        mc.field_1690.field_1832.method_23481(false);
        mc.field_1690.field_1867.method_23481(false);
    }
}

