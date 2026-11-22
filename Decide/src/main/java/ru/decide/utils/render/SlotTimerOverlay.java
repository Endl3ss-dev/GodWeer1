/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1293
 *  net.minecraft.class_1294
 *  net.minecraft.class_1657
 *  net.minecraft.class_1792
 *  net.minecraft.class_1796
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1844
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_332
 *  net.minecraft.class_746
 *  net.minecraft.class_9334
 *  org.joml.Matrix3x2fStack
 */
package ru.decide.utils.render;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1293;
import net.minecraft.class_1294;
import net.minecraft.class_1657;
import net.minecraft.class_1792;
import net.minecraft.class_1796;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1844;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_746;
import net.minecraft.class_9334;
import org.joml.Matrix3x2fStack;
import ru.decide.mixin.SlotTimerCooldownAccessor;
import ru.decide.mixin.SlotTimerEntryAccessor;
import ru.decide.module.impl.render.SlotTimer;

@Environment(value=EnvType.CLIENT)
public final class SlotTimerOverlay {
    private static final long HEAL_COOLDOWN_MS = 18500L;
    private static final Map<class_1792, Long> HEAL_STARTED = new HashMap<class_1792, Long>();

    private SlotTimerOverlay() {
    }

    public static void onFinishUsing(class_1799 stack) {
        SlotTimer module = SlotTimer.getInstance();
        if (module == null || !module.isEnabled() || !module.healTracking.getValue().booleanValue() || stack == null) {
            return;
        }
        if (SlotTimerOverlay.isHealPotion(stack)) {
            HEAL_STARTED.put(stack.method_7909(), System.currentTimeMillis());
        }
    }

    public static void clear() {
        HEAL_STARTED.clear();
    }

    public static void draw(class_332 context, class_327 font, class_1799 stack, int x, int y) {
        SlotTimer module = SlotTimer.getInstance();
        if (module == null || !module.isEnabled() || stack == null || stack.method_7960()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        class_746 player = mc.field_1724;
        if (player == null) {
            return;
        }
        boolean[] customHeal = new boolean[1];
        float[] state = SlotTimerOverlay.resolve((class_1657)player, stack, module, customHeal);
        if (state == null) {
            return;
        }
        float progress = state[0];
        float seconds = state[1];
        if (module.fill.getValue().booleanValue() && customHeal[0] && progress > 0.0f) {
            int height = Math.round(16.0f * progress);
            int top = y + Math.round(16.0f * (1.0f - progress));
            context.method_25294(x, top, x + 16, top + height, -1879048193);
        }
        if (!module.numbers.getValue().booleanValue() || seconds <= 0.0f) {
            return;
        }
        String text = module.convertDelays.getValue() != false ? (seconds < 10.0f ? String.format(Locale.US, "%.1f", Float.valueOf(seconds)) : String.format(Locale.US, "%.0f", Float.valueOf(seconds))) : String.valueOf(Math.round(seconds * 20.0f));
        int color = progress <= 0.33f ? 65280 : (progress <= 0.66f ? 0xFFFF00 : 0xFF0000);
        SlotTimerOverlay.drawCentered(context, font, text, color, x, y, module.fontSize.getValue().floatValue());
    }

    private static void drawCentered(class_332 context, class_327 font, String text, int color, int x, int y, float fontSize) {
        if (fontSize <= 0.0f) {
            return;
        }
        Matrix3x2fStack matrices = context.method_51448();
        matrices.pushMatrix();
        matrices.translate((float)x + 8.0f, (float)y + 8.0f);
        matrices.scale(fontSize, fontSize);
        int width = font.method_1727(text);
        context.method_51433(font, text, -width / 2, -4, color | 0xFF000000, true);
        matrices.popMatrix();
    }

    private static float[] resolve(class_1657 player, class_1799 stack, SlotTimer module, boolean[] customHeal) {
        Long startedAt;
        float progress = 0.0f;
        float seconds = 0.0f;
        class_1796 manager = player.method_7357();
        try {
            SlotTimerCooldownAccessor accessor = (SlotTimerCooldownAccessor)manager;
            class_2960 group = manager.method_62836(stack);
            Object entry = accessor.decide$getEntries().get(group);
            if (entry != null) {
                int now = accessor.decide$getTick();
                int start = ((SlotTimerEntryAccessor)entry).decide$getStartTick();
                int end = ((SlotTimerEntryAccessor)entry).decide$getEndTick();
                int remaining = Math.max(0, end - now);
                int total = Math.max(1, end - start);
                progress = (float)remaining / (float)total;
                seconds = (float)remaining / 20.0f;
            }
        }
        catch (Throwable accessor) {
            // empty catch block
        }
        if (module.healTracking.getValue().booleanValue() && (startedAt = HEAL_STARTED.get(stack.method_7909())) != null) {
            long elapsed = System.currentTimeMillis() - startedAt;
            if (elapsed >= 18500L) {
                HEAL_STARTED.remove(stack.method_7909());
            } else {
                float healProgress = 1.0f - (float)elapsed / 18500.0f;
                if (healProgress > progress) {
                    progress = healProgress;
                    seconds = (float)(18500L - elapsed) / 1000.0f;
                    customHeal[0] = true;
                }
            }
        }
        if (progress <= 0.0f && seconds <= 0.0f) {
            return null;
        }
        return new float[]{progress, seconds};
    }

    private static boolean isHealPotion(class_1799 stack) {
        if (stack.method_7909() != class_1802.field_8574 && stack.method_7909() != class_1802.field_8436 && stack.method_7909() != class_1802.field_8150) {
            return false;
        }
        class_1844 contents = (class_1844)stack.method_58694(class_9334.field_49651);
        if (contents == null) {
            return false;
        }
        for (class_1293 effect : contents.method_57397()) {
            if (!effect.method_5579().equals((Object)class_1294.field_5915)) continue;
            return true;
        }
        return false;
    }
}

