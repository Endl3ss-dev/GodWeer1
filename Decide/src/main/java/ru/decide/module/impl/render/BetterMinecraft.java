/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_1657
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_408
 */
package ru.decide.module.impl.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_1657;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_408;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.utils.other.Instance;

@ModuleInfo(name="Better Minecraft", desc="\u041d\u0435\u0431\u043e\u043b\u044c\u0448\u0438\u0435 \u0432\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u044b\u0435 \u0443\u043b\u0443\u0447\u0448\u0435\u043d\u0438\u044f \u0432\u0430\u043d\u0438\u043b\u044c\u043d\u043e\u0433\u043e \u0440\u0435\u043d\u0434\u0435\u0440\u0430", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public final class BetterMinecraft
extends Module {
    public final BooleanSetting chatAnimations = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u0447\u0430\u0442\u0430", true);
    public final BooleanSetting tabAnimation = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0442\u0430\u0431\u0430", true);
    public final BooleanSetting inventoryAnimation = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0438\u043d\u0432\u0435\u043d\u0442\u0430\u0440\u044f", true);
    public final BooleanSetting itemMoveAnimation = new BooleanSetting((Module)this, "\u041f\u0435\u0440\u0435\u0442\u0430\u0441\u043a\u0438\u0432\u0430\u043d\u0438\u0435 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", true);
    public final BooleanSetting hotbarAnimation = new BooleanSetting((Module)this, "\u0410\u043d\u0438\u043c\u0430\u0446\u0438\u044f \u0445\u043e\u0442\u0431\u0430\u0440\u0430", true);
    public final BooleanSetting hotbarChatLift = new BooleanSetting((Module)this, "\u0425\u043e\u0442\u0431\u0430\u0440 \u043f\u0440\u0438 \u0447\u0430\u0442\u0435", true);
    public final BooleanSetting saturationDisplay = new BooleanSetting((Module)this, "\u041e\u0442\u043e\u0431\u0440\u0430\u0436\u0435\u043d\u0438\u0435 \u043d\u0430\u0441\u044b\u0449\u0435\u043d\u043d\u043e\u0441\u0442\u0438", true);
    private static final class_2960 SATURATION_FULL_SPRITE = class_2960.method_60656((String)"hud/food_full");
    private static final class_2960 SATURATION_HALF_SPRITE = class_2960.method_60656((String)"hud/food_half");
    private static final float CHAT_LIFT_PX = 14.0f;
    private static final long HOTBAR_STALE_NANOS = 250000000L;
    private static final DecelerateValue chatLift = new DecelerateValue(260);
    private static final DecelerateValue hotbarSelection = new DecelerateValue(180);
    private static long hotbarSelectionFrameNanos;
    private static long inventoryOpenTime;

    private static BetterMinecraft getInstanceIfReady() {
        try {
            return Instance.get(BetterMinecraft.class);
        }
        catch (RuntimeException ignored) {
            return null;
        }
    }

    public static boolean chatAnimationsEnabled() {
        BetterMinecraft m = BetterMinecraft.getInstanceIfReady();
        return m != null && m.isEnabled() && m.chatAnimations.getValue() != false;
    }

    public static boolean tabAnimationEnabled() {
        BetterMinecraft m = BetterMinecraft.getInstanceIfReady();
        return m != null && m.isEnabled() && m.tabAnimation.getValue() != false;
    }

    public static boolean inventoryAnimationEnabled() {
        BetterMinecraft m = BetterMinecraft.getInstanceIfReady();
        return m != null && m.isEnabled() && m.inventoryAnimation.getValue() != false;
    }

    public static boolean itemMoveAnimationEnabled() {
        BetterMinecraft m = BetterMinecraft.getInstanceIfReady();
        return m != null && m.isEnabled() && m.itemMoveAnimation.getValue() != false;
    }

    public static boolean hotbarAnimationEnabled() {
        BetterMinecraft m = BetterMinecraft.getInstanceIfReady();
        return m != null && m.isEnabled() && m.hotbarAnimation.getValue() != false;
    }

    public static boolean hotbarChatLiftEnabled() {
        BetterMinecraft m = BetterMinecraft.getInstanceIfReady();
        return m != null && m.isEnabled() && m.hotbarChatLift.getValue() != false;
    }

    public static void renderSaturation(class_332 graphics, class_1657 player, int top, int right) {
        float units;
        BetterMinecraft m = BetterMinecraft.getInstanceIfReady();
        if (m == null || !m.isEnabled() || !m.saturationDisplay.getValue().booleanValue()) {
            return;
        }
        float saturation = player.method_7344().method_7589();
        int size = 9;
        for (int index = 0; index < 10 && !((units = saturation - (float)index * 2.0f) <= 0.0f); ++index) {
            class_2960 sprite = units > 1.0f ? SATURATION_FULL_SPRITE : SATURATION_HALF_SPRITE;
            int x = right - index * 8 - 10 + (10 - size) / 2;
            graphics.method_52706(class_10799.field_56883, sprite, x, top - size - 1, size, size);
        }
    }

    public static float chatHotbarLiftOffset() {
        boolean lifted = BetterMinecraft.hotbarChatLiftEnabled() && class_310.method_1551().field_1755 instanceof class_408;
        return chatLift.update(lifted ? 14.0f : 0.0f);
    }

    public static int animateHotbarSelectionX(int targetX) {
        long now = System.nanoTime();
        long elapsed = now - hotbarSelectionFrameNanos;
        hotbarSelectionFrameNanos = now;
        if (!BetterMinecraft.hotbarAnimationEnabled() || elapsed <= 0L || elapsed > 250000000L) {
            hotbarSelection.snap(targetX);
            return targetX;
        }
        return Math.round(hotbarSelection.update(targetX));
    }

    public static void markInventoryOpen() {
        inventoryOpenTime = System.currentTimeMillis();
    }

    public static float inventorySlideOffset() {
        if (!BetterMinecraft.inventoryAnimationEnabled()) {
            return 0.0f;
        }
        if (inventoryOpenTime == 0L) {
            inventoryOpenTime = System.currentTimeMillis();
        }
        float progress = Math.min(1.0f, (float)(System.currentTimeMillis() - inventoryOpenTime) / 350.0f);
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        float t = progress - 1.0f;
        float ease = 1.0f + c3 * t * t * t + c1 * t * t;
        return -50.0f * (1.0f - ease);
    }

    @Environment(value=EnvType.CLIENT)
    private static final class DecelerateValue {
        private final long ms;
        private float from;
        private float to;
        private float current;
        private boolean initialised;
        private long startNanos;

        DecelerateValue(int ms) {
            this.ms = ms;
        }

        float update(float target) {
            if (!this.initialised) {
                this.initialised = true;
                this.from = target;
                this.to = target;
                this.current = target;
                this.startNanos = System.nanoTime();
                return this.current;
            }
            if (target != this.to) {
                this.from = this.current;
                this.to = target;
                this.startNanos = System.nanoTime();
            }
            double value = (double)(System.nanoTime() - this.startNanos) / 1000000.0;
            double x = Math.min(1.0, value / (double)this.ms);
            double output = 1.0 - (x - 1.0) * (x - 1.0);
            this.current = (float)((double)this.from + (double)(this.to - this.from) * output);
            return this.current;
        }

        void snap(float target) {
            this.from = target;
            this.to = target;
            this.current = target;
            this.initialised = true;
            this.startNanos = System.nanoTime();
        }
    }
}

