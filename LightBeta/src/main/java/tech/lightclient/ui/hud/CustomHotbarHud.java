package tech.lightclient.ui.hud;

import tech.lightclient.LightClient;
import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontObject;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.FunctionManager;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.impl.render.Interface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import tech.lightclient.ui.hud.star.HudElement;

import java.nio.charset.StandardCharsets;

public final class CustomHotbarHud {
    public static final String ELEMENT_NAME = "Кастом хотбар";
    public static final String HEARTS_SETTING_NAME = "Кастомные сердца";
    public static final String ARMOR_SETTING_NAME = "Кастомная броня";
    public static final String FOOD_SETTING_NAME = "Кастомная еда";
    private static final float VISUAL_SCALE = 1.5f;
    private static final int SLOT_COUNT = 9;
    /** Шаг слотов нового хотбара: компактная сетка как в дизайне. */
    private static final float SLOT_STRIDE = 20f;
    private static final float BOTTOM_MARGIN = 8f;
    private static final float STATUS_SEGMENT_SIZE = 7f;
    private static final float STATUS_SEGMENT_GAP = 2f;
    private static final float STATUS_ROW_GAP = 3f;

    private static float selectedX = Float.NaN;
    private static float chatOffsetY = 0f;
    private static float animatedArmor = Float.NaN;
    private static float animatedHealth = Float.NaN;
    private static float animatedAbsorption = Float.NaN;
    private static float animatedFood = Float.NaN;

    private CustomHotbarHud() {
    }

    public static boolean shouldUseCustomHotbar() {
        FunctionManager manager = LightClient.getFunctionManager();
        if (manager == null || manager.getAnInterface() == null || !manager.getAnInterface().isState()) {
            return false;
        }
        BooleanSetting setting = findElementSetting(ELEMENT_NAME);
        return setting != null && setting.get();
    }

    public static boolean shouldRenderCustomHearts() {
        BooleanSetting setting = findElementSetting(HEARTS_SETTING_NAME);
        return shouldUseCustomHotbar() && setting != null && setting.get();
    }

    public static boolean shouldRenderCustomArmor() {
        BooleanSetting setting = findElementSetting(ARMOR_SETTING_NAME);
        return shouldUseCustomHotbar() && setting != null && setting.get();
    }

    public static boolean shouldRenderCustomFood() {
        BooleanSetting setting = findElementSetting(FOOD_SETTING_NAME);
        return shouldUseCustomHotbar() && setting != null && setting.get();
    }

    private static BooleanSetting findElementSetting(String name) {
        if (Interface.elements == null || name == null || name.isBlank()) {
            return null;
        }

        BooleanSetting exact = Interface.elements.getByName(name);
        if (exact != null) {
            return exact;
        }

        String mojibake = new String(name.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        BooleanSetting encoded = Interface.elements.getByName(mojibake);
        if (encoded != null) {
            return encoded;
        }

        for (BooleanSetting setting : Interface.elements.get()) {
            if (setting == null || setting.getName() == null) {
                continue;
            }

            String settingName = setting.getName();
            if (settingName.equalsIgnoreCase(name) || settingName.equalsIgnoreCase(mojibake)) {
                return setting;
            }
        }

        return null;
    }

    public static void render(EventRender.Screen.Hud event) {
        if (!shouldUseCustomHotbar()) {
            selectedX = Float.NaN;
            resetStatusAnimations();
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.world == null || mc.options.hudHidden) {
            resetStatusAnimations();
            return;
        }

        DrawContext context = LightClient.getCurrentDrawContext();

        Renderer2D renderer = event.getRenderer();
        renderer.resetPipelineState();
        float scale = Math.max(0.01f, Interface.getInterfaceScale());
        float viewportWidth = event.getViewportWidth() / scale;
        float viewportHeight = event.getViewportHeight() / scale;
        boolean chatOpen = MinecraftClient.getInstance().currentScreen instanceof net.minecraft.client.gui.screen.ChatScreen;
        float chatTarget = chatOpen ? -13f : 0f;
        chatOffsetY = animate(chatOffsetY, chatTarget, 0.35f);
        if (Math.abs(chatOffsetY - chatTarget) < 0.5f) {
            chatOffsetY = chatTarget;
        }

        // Габариты нового хотбара — компактная панель ванильного размера.
        float hotbarWidth = SLOT_COUNT * SLOT_STRIDE;
        float panelWidth = hotbarWidth;
        float panelHeight = 23f;
        float x = (viewportWidth - panelWidth) * 0.5f;
        float y = viewportHeight - panelHeight - BOTTOM_MARGIN + chatOffsetY;
        int selectedSlot = mc.player.getInventory().getSelectedSlot();
        float targetSelectedX = animate(selectedX, x + selectedSlot * SLOT_STRIDE, 0.35f);
        selectedX = Float.isNaN(selectedX) ? x + selectedSlot * SLOT_STRIDE : targetSelectedX;

        renderStatusBars(renderer, mc, viewportWidth, y, chatOpen);
        renderer.resetPipelineState();

        // Панель: тень проекта, жидкое стекло проекта, тонкая обводка нового HUD.
        float rounding = 4f;
        HudShadowRenderer.render(renderer, ELEMENT_NAME, x, y, panelWidth, panelHeight, rounding, 1f, scale);
        Interface.renderHudBackground(renderer, x, y, panelWidth, panelHeight, rounding, 1f);
        if (!Interface.isHudBlurBackground()) {
            renderer.rect(x, y, panelWidth, panelHeight, rounding,
                    ClientColors.applyAlpha(0xFF0C0C0F, 0.85f));
        }
        renderer.rectOutline(x, y, panelWidth, panelHeight, rounding,
                ClientColors.applyAlpha(0xFFFFFFFF, 0.12f), 1f);

        // Мягкое свечение фирменного цвета вдоль нижней кромки панели.
        HudElement.drawPanelBottomGlow(renderer, x, y, panelWidth, panelHeight,
                0.0225f, ClientColors.GRADIENT_START.getRGB());

        // Разделители слотов с затуханием к краям.
        int dividerBase = ClientColors.applyAlpha(0xFFFFFFFF, 0.086f);
        for (int i = 1; i < 9; i++) {
            float dx = x + i * SLOT_STRIDE;
            drawFadedVLine(renderer, Math.round(dx), Math.round(y + 1.0f), Math.round(panelHeight - 2.0f),
                    dividerBase, 6);
        }

        // Акцентный слот выбранного предмета.
        float slotX = x + Math.round(selectedX - x) + 1.2f;
        float slotY = y + 1.0f;
        float slotW = 18.0f;
        float slotH = 20.5f;
        int accentColor = ClientColors.GRADIENT_START.getRGB();
        int fill = ClientColors.applyAlpha(accentColor, 0.25f);
        int fill2 = ClientColors.applyAlpha(accentColor, 0.18f);
        renderer.gradient(slotX, slotY, slotW, slotH, 3.0f, fill, fill, fill2, fill2);
        renderer.rectOutline(slotX, slotY, slotW, slotH, 3.0f,
                ClientColors.applyAlpha(accentColor, 0.5f), 1.2f);

        for (int i = 0; i < SLOT_COUNT; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != null && !stack.isEmpty()) {
                drawItem(renderer, context, stack, x + i * SLOT_STRIDE + 2.0f, y + 3.0f, 16f);
            }
        }

        ItemStack offhandStack = mc.player.getOffHandStack();
        if (offhandStack != null && !offhandStack.isEmpty()) {
            float offhandSize = 23f;
            float offhandX = mc.player.getMainArm().equals(net.minecraft.util.Arm.RIGHT)
                    ? x - 6.5f - offhandSize
                    : x + panelWidth + 6.5f;
            HudShadowRenderer.render(renderer, ELEMENT_NAME, offhandX, y, offhandSize, offhandSize, 3f, 1f, scale);
            Interface.renderHudBackground(renderer, offhandX, y, offhandSize, offhandSize, 3f, 1f);
            if (!Interface.isHudBlurBackground()) {
                renderer.rect(offhandX, y, offhandSize, offhandSize, 3f,
                        ClientColors.applyAlpha(0xFF0C0C0F, 0.85f));
            }
            renderer.rectOutline(offhandX, y, offhandSize, offhandSize, 3f,
                    ClientColors.applyAlpha(0xFFFFFFFF, 0.12f), 1f);
            drawItem(renderer, context, offhandStack, offhandX + 3.5f, y + 3.5f, 16f);
        }

        if (!mc.player.isSpectator() && !mc.player.isCreative()) {
            drawExperienceLevel(renderer, mc, viewportWidth, y - 14f);
        }
    }

    /** Вертикальный разделитель слотов с плавным затуханием сверху и снизу. */
    private static void drawFadedVLine(Renderer2D renderer, int x, int top, int h, int baseColor, int fade) {
        int bottom = top + h;
        int coreTop = top + fade;
        int coreBottom = bottom - fade;

        if (coreBottom > coreTop) {
            renderer.rect(x, coreTop, 1f, coreBottom - coreTop, 0.5f, baseColor);
        }

        for (int i = 0; i < fade; i++) {
            float t = i / (float) Math.max(1, fade - 1);
            int alpha = Math.max(1, Math.round(22 * t));
            int c = (baseColor & 0x00FFFFFF) | (alpha << 24);
            renderer.rect(x, top + i, 1f, 1f, 0.5f, c);
            renderer.rect(x, bottom - 1 - i, 1f, 1f, 0.5f, c);
        }
    }

    private static void drawExperienceLevel(Renderer2D renderer, MinecraftClient mc, float viewportWidth, float y) {
        FontObject font = FontRegistry.DELTA_HUD;
        if (font == null || mc.player.experienceLevel <= 0) {
            return;
        }

        String level = String.valueOf(mc.player.experienceLevel);
        float textSize = 7f;
        float textWidth = renderer.measureText(font, Text.literal(level), textSize).width;
        renderer.text(font, viewportWidth * 0.5f - textWidth * 0.5f,
                y + FontRegistry.centeredBaselineOffset(font, 'H', textSize),
                textSize, level, 0xFF7CE87C);
    }

    private static void renderStatusBars(Renderer2D renderer, MinecraftClient mc, float viewportWidth, float hotbarY, boolean chatOpen) {
        if (mc.interactionManager == null || mc.interactionManager.getCurrentGameMode().isCreative() || mc.player.isSpectator()) {
            resetStatusAnimations();
            return;
        }

        boolean renderArmor = shouldRenderCustomArmor();
        boolean renderHearts = shouldRenderCustomHearts();
        boolean renderFood = shouldRenderCustomFood();

        if (!renderArmor && !renderHearts && !renderFood) {
            resetStatusAnimations();
            return;
        }

        float scale = VISUAL_SCALE;
        float segmentSize = STATUS_SEGMENT_SIZE * scale;
        float gap = 3.5f * scale;
        float rowGap = STATUS_ROW_GAP * scale;
        float rowWidth = 10f * segmentSize + 9f * gap;
        float leftX = viewportWidth * 0.5f - rowWidth - 18f * scale;
        float rightX = viewportWidth * 0.5f + 18f * scale;
        float baseY = hotbarY - 23f * scale - (chatOpen ? 2f * scale : 0f);

        int armor = Math.max(0, Math.min(20, mc.player.getArmor()));
        int health = (int) Math.ceil(mc.player.getHealth());
        int maxHealth = Math.max(1, (int) Math.ceil(mc.player.getAttributeValue(EntityAttributes.MAX_HEALTH)));
        int absorption = Math.max(0, (int) Math.ceil(mc.player.getAbsorptionAmount()));
        int food = Math.max(0, Math.min(20, mc.player.getHungerManager().getFoodLevel()));

        if (renderArmor) {
            animatedArmor = animateStatus(animatedArmor, armor);
            drawSegmentRow(renderer, leftX, baseY, 10, animatedArmor, 20, 0xFFF2F2F2, segmentSize, gap, false);
        } else {
            animatedArmor = Float.NaN;
        }

        if (renderHearts) {
            animatedHealth = animateStatus(animatedHealth, health);
            animatedAbsorption = animateStatus(animatedAbsorption, absorption);
            drawSegmentRow(renderer, leftX, baseY + segmentSize + rowGap, 10, animatedHealth, maxHealth, 0xFFFF3B30, segmentSize, gap, false);
            if (animatedAbsorption > 0.05f) {
                drawAbsorptionOverlay(renderer, leftX, baseY + segmentSize + rowGap, 10, animatedHealth,
                        animatedAbsorption, maxHealth, segmentSize, gap);
            }
        } else {
            animatedHealth = Float.NaN;
            animatedAbsorption = Float.NaN;
        }

        if (renderFood) {
            animatedFood = animateStatus(animatedFood, food);
            drawSegmentRow(renderer, rightX, baseY + segmentSize + rowGap, 10, animatedFood, 20, 0xFFE8B65A, segmentSize, gap, true);
        } else {
            animatedFood = Float.NaN;
        }
    }

    private static void drawSegmentRow(Renderer2D renderer, float x, float y, int segments, float value, int maxValue,
                                       int fillColor, float segmentSize, float gap, boolean reverse) {
        int clampedMax = Math.max(1, maxValue);
        float rowWidth = segments * segmentSize + Math.max(0, segments - 1) * gap;
        float progress = Math.max(0f, Math.min(1f, value / clampedMax));
        float fillWidth = rowWidth * progress;
        float fillX = reverse ? x + rowWidth - fillWidth : x;
        float rounding = Interface.getHudRounding(1.5f) + 2;

        renderer.rect(x, y, rowWidth, segmentSize, rounding, ClientColors.applyAlpha(0xFF050506, 0.72f));
        if (fillWidth > 0.01f) {
            renderer.rect(fillX, y, fillWidth, segmentSize, rounding,
                    ClientColors.applyAlpha(fillColor, Math.max(0.15f, progress)));
        }
    }

    private static void drawAbsorptionOverlay(Renderer2D renderer, float x, float y, int segments, float health,
                                              float absorption, int maxHealth, float segmentSize, float gap) {
        int clampedMax = Math.max(1, maxHealth);
        float rowWidth = segments * segmentSize + Math.max(0, segments - 1) * gap;
        float fillWidth = rowWidth * Math.max(0f, Math.min(1f, absorption / clampedMax));
        if (fillWidth > 0.01f) {
            float fillX = x + rowWidth - fillWidth;
            float alpha = Math.max(0.2f, Math.min(1f, absorption / clampedMax));
            renderer.rect(fillX, y, fillWidth, segmentSize, Interface.getHudRounding(1.5f),
                    ClientColors.applyAlpha(0xFFFFD23F, alpha));
        }
    }

    private static void drawItem(Renderer2D renderer, DrawContext context, ItemStack stack, float x, float y, float size) {
        if (stack == null || stack.isEmpty() || context == null) {
            return;
        }

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

        if (!Float.isFinite(absX) || !Float.isFinite(absY) || !Float.isFinite(absSize) || absSize <= 0f) {
            return;
        }

        double windowScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
        if (windowScale <= 0.0) {
            return;
        }

        float guiX = (float) (absX / windowScale);
        float guiY = (float) (absY / windowScale);
        float guiScale = (float) (absSize / (16f * windowScale));
        if (!Float.isFinite(guiX) || !Float.isFinite(guiY) || !Float.isFinite(guiScale) || guiScale <= 0f) {
            return;
        }

        renderer.flush();
        var matrices = context.getMatrices();
        matrices.pushMatrix();
        try {
            matrices.translate(guiX, guiY);
            matrices.scale(guiScale, guiScale);
            context.drawItem(stack, 0, 0);
            context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, stack, 0, 0);
        } catch (Throwable ignored) {
        } finally {
            matrices.popMatrix();
            renderer.resetPipelineState();
        }

    }

    private static float animate(float value, float target, float speed) {
        float delta = target - value;
        if (Math.abs(delta) < 0.01f) {
            return target;
        }
        return value + delta * Math.max(0.01f, Math.min(1f, speed));
    }

    private static float animateStatus(float value, float target) {
        if (!Float.isFinite(value)) {
            return target;
        }
        return animate(value, target, target < value ? 0.18f : 0.32f);
    }

    private static void resetStatusAnimations() {
        animatedArmor = Float.NaN;
        animatedHealth = Float.NaN;
        animatedAbsorption = Float.NaN;
        animatedFood = Float.NaN;
    }
}
