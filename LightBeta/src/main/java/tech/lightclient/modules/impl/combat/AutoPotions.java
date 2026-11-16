package tech.lightclient.modules.impl.combat;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventKey;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.player.PlayerInventoryUtil;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.render.animations.Easings;
import tech.lightclient.client.utils.render.animations.impl.SimpleLinearAnimation;
import tech.lightclient.client.utils.render.color.ColorUtils;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.ui.hud.NotificationsOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

@FunctionAdd(name = "AutoPotions", alias = "Auto Potions", category = Category.Combat, description = "Автоматически кидает взрывные зелья по бинду или через колесо")
public class AutoPotions extends Function {

    // Типы зелий и их отличительные эффекты + цвет для колеса.
    private enum Potion {
        ASSASSIN("Зелье ассасина", ColorUtils.rgba(20, 20, 20, 255)) {
            @Override
            boolean matches(List<StatusEffectInstance> effects) {
                return has(effects, StatusEffects.STRENGTH)
                        && has(effects, StatusEffects.HASTE)
                        && has(effects, StatusEffects.INSTANT_DAMAGE);
            }
        },
        PALADIN("Зелье палладина", ColorUtils.rgba(86, 198, 225, 255)) {
            @Override
            boolean matches(List<StatusEffectInstance> effects) {
                return has(effects, StatusEffects.RESISTANCE)
                        && has(effects, StatusEffects.FIRE_RESISTANCE)
                        && has(effects, StatusEffects.HEALTH_BOOST);
            }
        },
        HOLY_WATER("Святая вода", ColorUtils.rgba(245, 245, 245, 255)) {
            @Override
            boolean matches(List<StatusEffectInstance> effects) {
                return has(effects, StatusEffects.REGENERATION)
                        && has(effects, StatusEffects.INVISIBILITY)
                        && has(effects, StatusEffects.INSTANT_HEALTH);
            }
        },
        RADIATION("Зелье радиаций", ColorUtils.rgba(60, 200, 60, 255)) {
            @Override
            boolean matches(List<StatusEffectInstance> effects) {
                return has(effects, StatusEffects.POISON)
                        && has(effects, StatusEffects.WITHER)
                        && has(effects, StatusEffects.SLOWNESS)
                        && has(effects, StatusEffects.HUNGER);
            }
        };

        final String displayName;
        final int color;

        Potion(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }

        abstract boolean matches(List<StatusEffectInstance> effects);

        static boolean has(List<StatusEffectInstance> effects, RegistryEntry<net.minecraft.entity.effect.StatusEffect> type) {
            for (StatusEffectInstance e : effects) {
                if (e.getEffectType() == type) return true;
            }
            return false;
        }
    }

    ModeSetting mode = new ModeSetting("Режим", "По бинду", "По бинду", "Колесо", "Квадратики");

    // Бинды для режима "По бинду".
    BindSetting assassinBind = new BindSetting("Ассасин", -1).setVisible(() -> mode.is("По бинду"));
    BindSetting paladinBind = new BindSetting("Палладин", -1).setVisible(() -> mode.is("По бинду"));
    BindSetting holyWaterBind = new BindSetting("Святая вода", -1).setVisible(() -> mode.is("По бинду"));
    BindSetting radiationBind = new BindSetting("Радиация", -1).setVisible(() -> mode.is("По бинду"));

    // Кнопка открытия колеса / квадратиков.
    BindSetting wheelBind = new BindSetting("Бинд колеса", -1).setVisible(() -> mode.is("Колесо") || mode.is("Квадратики"));

    private static final float BASE_OUTER_RADIUS = 78f;
    private static final float BASE_INNER_RADIUS = 46f;
    private static final float SEGMENT_GAP_DEG = 3.5f;

    // Квадратики.
    private static final float SQUARE_SIZE = 28f;
    private static final float SQUARE_ORBIT_RADIUS = 66f;
    private static final float SQUARE_ROUNDING = 6f;
    private static final float SQUARE_OFFSET_X = 10f; // сдвиг влево
    private static final float SQUARE_OFFSET_Y = 14f; // сдвиг вверх

    private boolean wheelOpen;
    private int hoverIndex = -1;
    private boolean cursorUnlocked;

    // Анимация появления (плавное раскрытие колеса / вылет квадратиков).
    private final SimpleLinearAnimation openAnim = new SimpleLinearAnimation(260, Easings.EASE_OUT_CUBIC);
    private final SimpleLinearAnimation squareAnim = new SimpleLinearAnimation(320, Easings.EASE_OUT_BACK);

    private float wheelCenterX;
    private float wheelCenterY;
    private float wheelInnerRadius;
    private float wheelOuterRadius;
    private int wheelViewportWidth;
    private int wheelViewportHeight;

    private long lastActionMs;

    public AutoPotions() {
        addSettings(mode, assassinBind, paladinBind, holyWaterBind, radiationBind, wheelBind);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        closeWheel();
        PlayerUtils.enableMoveKeys();
    }

    @EventHandler
    public void onKey(EventKey e) {
        if (nullCheck()) return;

        if (isOpenMode()) {
            int wheelKey = wheelBind.get();
            if (wheelKey > 0 && e.getConvertedKey() == wheelKey) {
                if (e.getAction() == 1) {
                    openWheel();
                } else if (e.getAction() == 0) {
                    releaseWheel();
                }
            }
            return;
        }

        // Режим "По бинду".
        if (e.getAction() != 1) return;

        if (assassinBind.get() != -1 && e.isKeyDown(assassinBind.get())) {
            usePotion(Potion.ASSASSIN);
        } else if (paladinBind.get() != -1 && e.isKeyDown(paladinBind.get())) {
            usePotion(Potion.PALADIN);
        } else if (holyWaterBind.get() != -1 && e.isKeyDown(holyWaterBind.get())) {
            usePotion(Potion.HOLY_WATER);
        } else if (radiationBind.get() != -1 && e.isKeyDown(radiationBind.get())) {
            usePotion(Potion.RADIATION);
        }
    }

    private void openWheel() {
        if (mc.currentScreen != null) return;
        if (getWheelPotions().isEmpty()) {
            NotificationsOverlay.push(Text.literal("Вы ничего не забиндили"), 1500, NotificationsOverlay.Kind.OFF, ItemStack.EMPTY);
            return;
        }
        wheelOpen = true;
        hoverIndex = -1;
        openAnim.setImmediate(false);
        openAnim.show();
        squareAnim.setImmediate(false);
        squareAnim.show();
        updateWheelCursorState(true);
    }

    private void releaseWheel() {
        if (!wheelOpen) return;

        int selected = hoverIndex;
        List<Potion> potions = getWheelPotions();
        wheelOpen = false;
        hoverIndex = -1;
        updateWheelCursorState(false);

        if (selected < 0 || selected >= potions.size()) return;
        usePotion(potions.get(selected));
    }

    private boolean isOpenMode() {
        return mode.is("Колесо") || mode.is("Квадратики");
    }

    private List<Potion> getWheelPotions() {
        List<Potion> list = new ArrayList<>();
        for (Potion p : Potion.values()) list.add(p);
        return list;
    }

    // ==== Активация зелья ====

    private void usePotion(Potion potion) {
        if (mc.player == null || mc.interactionManager == null) return;
        if (System.currentTimeMillis() - lastActionMs < 250L) return;

        Slot slot = findPotionSlot(potion);
        if (slot == null) {
            NotificationsOverlay.push(Text.literal(potion.displayName + ": не найдено"), 1200, NotificationsOverlay.Kind.OFF, ItemStack.EMPTY);
            return;
        }
        if (PlayerInventoryUtil.isItemOnCooldown(slot.getStack())) return;

        lastActionMs = System.currentTimeMillis();
        NotificationsOverlay.push(Text.literal("Кинул: ").append(slot.getStack().getName()), 1200, NotificationsOverlay.Kind.INFORMATION, slot.getStack());
        throwFromSlot(slot);
    }

    private Slot findPotionSlot(Potion potion) {
        if (mc.player == null) return null;
        for (Slot slot : mc.player.currentScreenHandler.slots) {
            if (slot.id == 45) continue; // офф-хенд
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            List<StatusEffectInstance> effects = getEffects(stack);
            if (effects.isEmpty()) continue;
            if (potion.matches(effects)) return slot;
        }
        return null;
    }

    private List<StatusEffectInstance> getEffects(ItemStack stack) {
        PotionContentsComponent contents = stack.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
        List<StatusEffectInstance> list = new ArrayList<>();
        for (StatusEffectInstance e : contents.getEffects()) list.add(e);
        return list;
    }

    /**
     * Кидает (использует правым кликом) зелье из указанного слота, затем возвращает предыдущий слот.
     */
    private void throwFromSlot(Slot slot) {
        int selected = mc.player.getInventory().getSelectedSlot();
        PlayerUtils.disableMoveKeys();

        if (isHotbar(slot.id)) {
            int hotbar = slot.id - 36;
            mc.player.getInventory().setSelectedSlot(hotbar);
            PlayerInventoryUtil.sendSelectedSlotPacket(hotbar);
            PlayerInventoryUtil.useMainHandItem(true);
            PlayerUtils.postScript.addTickStep(3, () -> {
                mc.player.getInventory().setSelectedSlot(selected);
                PlayerInventoryUtil.sendSelectedSlotPacket(selected);
                PlayerInventoryUtil.updateSlots();
                PlayerUtils.enableMoveKeys();
            });
        } else {
            // Зелье в инвентаре: свапаем в текущий слот хотбара, кидаем, свапаем обратно.
            PlayerInventoryUtil.clickSlot(slot.id, selected, SlotActionType.SWAP);
            PlayerInventoryUtil.useMainHandItem(true);
            PlayerUtils.postScript.addTickStep(3, () -> {
                PlayerInventoryUtil.clickSlot(slot.id, selected, SlotActionType.SWAP);
                PlayerInventoryUtil.updateSlots();
                PlayerUtils.enableMoveKeys();
            });
        }
    }

    private boolean isHotbar(int slotId) {
        return slotId >= 36 && slotId <= 44;
    }

    // ==== Отрисовка колеса ====

    @EventHandler
    public void onRenderWheel(EventRender.Screen.Hud e) {
        if (nullCheck()) return;
        if (!isOpenMode() || !wheelOpen) return;
        if (mc.currentScreen != null) return;

        updateWheelCursorState(true);

        Renderer2D renderer = e.getRenderer();
        if (renderer == null) return;

        if (mode.is("Квадратики")) {
            renderSquares(renderer, e);
        } else {
            renderWheel(renderer, e);
        }
    }

    private void renderWheel(Renderer2D renderer, EventRender.Screen.Hud e) {
        List<Potion> potions = getWheelPotions();
        int count = potions.size();
        if (count == 0) return;

        float guiScale = (float) mc.getWindow().getScaleFactor();
        if (guiScale <= 0f) guiScale = 1f;

        // Центрируем строго по прицелу.
        float cx = (mc.getWindow().getScaledWidth() / 2f) * guiScale;
        float cy = (mc.getWindow().getScaledHeight() / 2f) * guiScale;
        float outerR = BASE_OUTER_RADIUS * guiScale;
        float innerR = BASE_INNER_RADIUS * guiScale;

        wheelCenterX = cx;
        wheelCenterY = cy;
        wheelInnerRadius = innerR;
        wheelOuterRadius = outerR;
        wheelViewportWidth = e.getViewportWidth();
        wheelViewportHeight = e.getViewportHeight();

        float mouseX = toWheelMouseX(e.getViewportWidth());
        float mouseY = toWheelMouseY(e.getViewportHeight());
        hoverIndex = getHoverIndex(mouseX, mouseY, cx, cy, innerR, outerR, count);

        float segmentSpan = 360f / count;
        float gap = Math.min(SEGMENT_GAP_DEG, segmentSpan * 0.25f);
        float ringMid = (outerR + innerR) * 0.5f;

        // Анимация появления: масштаб от центра + прозрачность.
        float p = openAnim.getProgress();
        float scale = 0.65f + 0.35f * p;
        renderer.pushScale(scale, cx, cy);
        renderer.pushAlpha(p);
        try {
            drawWheelBody(renderer, e, potions, count, guiScale, cx, cy, outerR, innerR,
                    segmentSpan, gap, ringMid);
        } finally {
            renderer.popAlpha();
            renderer.popScale();
        }
    }

    private void drawWheelBody(Renderer2D renderer, EventRender.Screen.Hud e, List<Potion> potions,
                               int count, float guiScale, float cx, float cy, float outerR, float innerR,
                               float segmentSpan, float gap, float ringMid) {

        // Blur фона под колесом (как в CsGui).
        float blurSize = (outerR + 3f * guiScale) * 2f;
        float blurX = cx - blurSize * 0.5f;
        float blurY = cy - blurSize * 0.5f;
        boolean prevSuppressed = renderer.isHudBlurSuppressed();
        renderer.setHudBlurSuppressed(false);
        try {
            renderer.blur(blurX, blurY, blurSize, blurSize, blurSize * 0.5f, 1f);
        } finally {
            renderer.setHudBlurSuppressed(prevSuppressed);
        }

        renderer.circle(cx, cy, outerR + 3f * guiScale, 0f, 1f, ColorUtils.rgba(17, 17, 24, 140));

        renderer.setHudBlurSuppressed(false);
        try {
            renderer.blur(blurX, blurY, blurSize, blurSize, blurSize * 0.5f, 0.5f);
        } finally {
            renderer.setHudBlurSuppressed(prevSuppressed);
        }

        // Сегменты кольца (нейтральные; цвет несёт сам бутылёк зелья).
        for (int i = 0; i < count; i++) {
            boolean isHover = i == hoverIndex;
            int color = isHover ? ColorUtils.rgba(255, 209, 47, 170) : ColorUtils.rgba(70, 72, 78, 130);

            float startDeg = -90f + segmentSpan * i + gap * 0.5f;
            float endDeg = -90f + segmentSpan * (i + 1) - gap * 0.5f;
            drawSector(renderer, cx, cy, innerR, outerR, startDeg, endDeg, color);
        }

        // Внутренний вырез.
        float innerBlurSize = (innerR - 1f) * 2f;
        renderer.setHudBlurSuppressed(false);
        try {
            renderer.blur(cx - innerBlurSize * 0.5f, cy - innerBlurSize * 0.5f,
                    innerBlurSize, innerBlurSize, innerBlurSize * 0.5f, 0.5f);
        } finally {
            renderer.setHudBlurSuppressed(prevSuppressed);
        }
        renderer.circle(cx, cy, innerR - 1f, 0f, 1f, ColorUtils.rgba(20, 20, 24, 150));
        renderer.circle(cx, cy, innerR * 0.16f, 0f, 1f, ColorUtils.rgba(255, 209, 47, hoverIndex >= 0 ? 235 : 90));

        // Иконки зелий + количество цифрой рядом.
        DrawContext context = LightClient.getCurrentDrawContext();
        FontObject font = FontRegistry.SF_SEMIBOLD != null ? FontRegistry.SF_SEMIBOLD : FontRegistry.SF_MEDIUM;
        float iconRadius = ringMid;
        float iconSize = 16f * guiScale;
        for (int i = 0; i < count; i++) {
            Potion potion = potions.get(i);
            Slot slot = findPotionSlot(potion);
            ItemStack stack = slot != null ? slot.getStack() : Items.SPLASH_POTION.getDefaultStack();
            if (stack.isEmpty()) continue;

            // Перекрашиваем жидкость бутылька в цвет баффа.
            stack = tintPotion(stack, potion.color);

            float midDeg = -90f + segmentSpan * (i + 0.5f);
            double midRad = Math.toRadians(midDeg);
            float ix = cx + (float) Math.cos(midRad) * iconRadius;
            float iy = cy + (float) Math.sin(midRad) * iconRadius;
            drawItem(renderer, context, stack, ix - iconSize * 0.5f, iy - iconSize * 0.5f, iconSize);

            // Количество зелий данного типа — по центру под иконкой.
            int amount = countPotions(potion);
            if (font != null && amount > 0) {
                String text = "x" + amount;
                float textSize = 7f * guiScale;
                var metrics = renderer.measureText(font, text, textSize);
                float textX = ix - metrics.width * 0.5f;
                float textY = iy + iconSize * 0.5f + FontRegistry.centeredBaselineOffset(font, 'H', textSize) + 2f * guiScale;
                renderer.text(font, textX, textY, textSize, text, 0xFFFFFFFF);
            }
        }
    }

    // ==== Отрисовка режима "Квадратики" ====

    private void renderSquares(Renderer2D renderer, EventRender.Screen.Hud e) {
        List<Potion> potions = getWheelPotions();
        int count = potions.size();
        if (count == 0) return;

        float guiScale = (float) mc.getWindow().getScaleFactor();
        if (guiScale <= 0f) guiScale = 1f;

        float cx = (mc.getWindow().getScaledWidth() / 2f) * guiScale - SQUARE_OFFSET_X * guiScale;
        float cy = (mc.getWindow().getScaledHeight() / 2f) * guiScale - SQUARE_OFFSET_Y * guiScale;
        float size = SQUARE_SIZE * guiScale;
        float orbit = SQUARE_ORBIT_RADIUS * guiScale;
        float rounding = SQUARE_ROUNDING * guiScale;

        wheelViewportWidth = e.getViewportWidth();
        wheelViewportHeight = e.getViewportHeight();

        float mouseX = toWheelMouseX(e.getViewportWidth());
        float mouseY = toWheelMouseY(e.getViewportHeight());

        boolean prevSuppressed = renderer.isHudBlurSuppressed();
        DrawContext context = LightClient.getCurrentDrawContext();
        FontObject font = FontRegistry.SF_SEMIBOLD != null ? FontRegistry.SF_SEMIBOLD : FontRegistry.SF_MEDIUM;

        float p = squareAnim.getProgress();   // прогресс вылета
        float a = openAnim.getProgress();      // прогресс прозрачности
        int hover = -1;

        // Точка старта анимации — прицел (реальный центр экрана).
        float crosshairX = (mc.getWindow().getScaledWidth() / 2f) * guiScale;
        float crosshairY = (mc.getWindow().getScaledHeight() / 2f) * guiScale;

        for (int i = 0; i < count; i++) {
            // Финальная позиция квадрата по кругу (относительно смещённого центра).
            float deg = -90f + 360f / count * i;
            double rad = Math.toRadians(deg);
            float finalCx = cx + (float) Math.cos(rad) * orbit;
            float finalCy = cy + (float) Math.sin(rad) * orbit;

            // Hover считаем по финальной позиции (стабильно во время анимации).
            boolean isHover = mouseX >= finalCx - size * 0.5f && mouseX <= finalCx + size * 0.5f
                    && mouseY >= finalCy - size * 0.5f && mouseY <= finalCy + size * 0.5f;
            if (isHover) hover = i;

            // Текущая (анимированная) позиция: вылет от прицела к финальной.
            float scx = crosshairX + (finalCx - crosshairX) * p;
            float scy = crosshairY + (finalCy - crosshairY) * p;
            float curSize = size * (0.4f + 0.6f * p);
            float curRounding = rounding * (0.4f + 0.6f * p);

            float sx = scx - curSize * 0.5f;
            float sy = scy - curSize * 0.5f;

            // Блюр под квадратом.
            renderer.setHudBlurSuppressed(false);
            try {
                renderer.blur(sx, sy, curSize, curSize, curRounding, (isHover ? 1f : 0.6f) * a);
            } finally {
                renderer.setHudBlurSuppressed(prevSuppressed);
            }

            // Сам квадрат (без обводки, полупрозрачный; при наведении светлее).
            int baseFill = isHover ? ColorUtils.rgba(45, 45, 52, 200) : ColorUtils.rgba(22, 22, 28, 155);
            renderer.rect(sx, sy, curSize, curSize, curRounding, applyAlpha(baseFill, a));

            // Иконка зелья (окрашенный бутылёк), отцентрованная в квадрате.
            Potion potion = potions.get(i);
            Slot slot = findPotionSlot(potion);
            ItemStack stack = slot != null ? slot.getStack() : Items.SPLASH_POTION.getDefaultStack();
            if (!stack.isEmpty()) {
                ItemStack tinted = tintPotion(stack, potion.color);
                float iconSize = 16f * guiScale * (0.4f + 0.6f * p);
                drawItem(renderer, context, tinted, scx - iconSize * 0.5f, scy - iconSize * 0.5f, iconSize);

                int amount = countPotions(potion);
                if (font != null && amount > 0 && p > 0.85f) {
                    String text = "x" + amount;
                    float textSize = 6.5f * guiScale;
                    var metrics = renderer.measureText(font, text, textSize);
                    float textX = scx - metrics.width * 0.5f;
                    float textY = scy + iconSize * 0.5f + FontRegistry.centeredBaselineOffset(font, 'H', textSize) + 1f * guiScale;
                    renderer.text(font, textX, textY, textSize, text, 0xFFFFFFFF);
                }
            }
        }

        hoverIndex = hover;
    }

    private int applyAlpha(int rgba, float factor) {
        int a = (int) (((rgba >>> 24) & 0xFF) * Math.max(0f, Math.min(1f, factor)));
        return (a << 24) | (rgba & 0x00FFFFFF);
    }

    /**
     * Возвращает копию зелья с перекрашенной жидкостью (customColor) в заданный цвет.
     */
    private ItemStack tintPotion(ItemStack stack, int rgba) {
        try {
            ItemStack copy = stack.copy();
            PotionContentsComponent current = copy.getOrDefault(DataComponentTypes.POTION_CONTENTS, PotionContentsComponent.DEFAULT);
            int rgb = rgba & 0x00FFFFFF;
            List<StatusEffectInstance> customEffects = new ArrayList<>();
            for (StatusEffectInstance e : current.customEffects()) customEffects.add(e);
            PotionContentsComponent tinted = new PotionContentsComponent(
                    current.potion(),
                    java.util.Optional.of(rgb),
                    customEffects,
                    current.customName());
            copy.set(DataComponentTypes.POTION_CONTENTS, tinted);
            return copy;
        } catch (Throwable ignored) {
            return stack;
        }
    }

    private int countPotions(Potion potion) {
        if (mc.player == null) return 0;
        int total = 0;
        for (Slot slot : mc.player.currentScreenHandler.slots) {
            if (slot.id == 45) continue;
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            List<StatusEffectInstance> effects = getEffects(stack);
            if (effects.isEmpty()) continue;
            if (potion.matches(effects)) total += stack.getCount();
        }
        return total;
    }

    private void closeWheel() {
        wheelOpen = false;
        hoverIndex = -1;
        updateWheelCursorState(false);
    }

    private void updateWheelCursorState(boolean shouldBeUnlocked) {
        if (mc == null || mc.mouse == null) return;

        if (shouldBeUnlocked) {
            if (!cursorUnlocked) {
                mc.mouse.unlockCursor();
                cursorUnlocked = true;
            }
            return;
        }

        if (cursorUnlocked) {
            if (mc.currentScreen == null) {
                mc.mouse.lockCursor();
            }
            cursorUnlocked = false;
        }
    }

    private float toWheelMouseX(int viewportWidth) {
        int windowWidth = mc.getWindow().getWidth();
        if (windowWidth <= 0) return 0f;
        return (float) (mc.mouse.getX() * viewportWidth / windowWidth);
    }

    private float toWheelMouseY(int viewportHeight) {
        int windowHeight = mc.getWindow().getHeight();
        if (windowHeight <= 0) return 0f;
        return (float) (mc.mouse.getY() * viewportHeight / windowHeight);
    }

    private int getHoverIndex(float mouseX, float mouseY, float cx, float cy, float innerR, float outerR, int count) {
        if (outerR <= 0f) return -1;

        float dx = mouseX - cx;
        float dy = mouseY - cy;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist < innerR || dist > outerR) return -1;

        double ang = Math.atan2(dy, dx) + Math.PI / 2d;
        if (ang < 0) ang += Math.PI * 2d;
        ang %= Math.PI * 2d;

        int idx = (int) Math.floor((ang / (Math.PI * 2d)) * count);
        if (idx < 0 || idx >= count) return -1;
        return idx;
    }

    private void drawSector(Renderer2D renderer, float cx, float cy, float innerR, float outerR,
                            float startDeg, float endDeg, int color) {
        float spanDeg = endDeg - startDeg;
        if (spanDeg <= 0f) return;

        float height = outerR - innerR;
        if (height <= 0f) return;

        float arcLen = (float) (Math.toRadians(spanDeg) * outerR);
        int steps = Math.max(2, Math.round(arcLen / 2f));
        float stepDeg = spanDeg / steps;
        float sliceWidth = (float) (Math.toRadians(stepDeg) * outerR) + 1.5f;

        renderer.pushTranslation(cx, cy);
        try {
            for (int i = 0; i < steps; i++) {
                float midDeg = startDeg + stepDeg * (i + 0.5f);
                renderer.pushRotation(midDeg - 90f);
                try {
                    renderer.rect(-sliceWidth * 0.5f, innerR, sliceWidth, height, color);
                } finally {
                    renderer.popRotation();
                }
            }
        } finally {
            renderer.popTransform();
        }
    }

    private void drawItem(Renderer2D renderer, DrawContext context, ItemStack stack, float x, float y, float size) {
        if (renderer == null || context == null || stack == null || stack.isEmpty()) return;

        var transform = renderer.getTransformStack().current();
        float absX = x;
        float absY = y;
        float absSize = size;

        if (transform != null && transform.length >= 6) {
            float scaleX = (float) Math.sqrt(transform[0] * transform[0] + transform[3] * transform[3]);
            float scaleY = (float) Math.sqrt(transform[1] * transform[1] + transform[4] * transform[4]);
            absX = transform[0] * x + transform[1] * y + transform[2];
            absY = transform[3] * x + transform[4] * y + transform[5];
            absSize = size * Math.min(scaleX, scaleY);
        }
        if (!Float.isFinite(absX) || !Float.isFinite(absY) || !Float.isFinite(absSize) || absSize <= 0f) return;

        double windowScale = mc.getWindow().getScaleFactor();
        if (windowScale <= 0.0) return;

        float guiX = (float) (absX / windowScale);
        float guiY = (float) (absY / windowScale);
        float guiScale = (float) (absSize / (16f * windowScale));
        if (!Float.isFinite(guiX) || !Float.isFinite(guiY) || !Float.isFinite(guiScale) || guiScale <= 0f) return;

        renderer.flush();
        var matrices = context.getMatrices();
        matrices.pushMatrix();
        try {
            matrices.translate(guiX, guiY);
            matrices.scale(guiScale, guiScale);
            context.drawItem(stack, 0, 0);
        } catch (Throwable ignored) {
        } finally {
            matrices.popMatrix();
            renderer.resetPipelineState();
        }
    }
}
