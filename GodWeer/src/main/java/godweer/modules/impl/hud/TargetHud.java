package godweer.modules.impl.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import godweer.managers.FriendManager;
import godweer.managers.ModuleManager;
import godweer.managers.ThemeManager;
import godweer.modules.Module;
import godweer.modules.ModuleCategory;
import godweer.modules.api.setting.BooleanSetting;
import godweer.modules.api.setting.ModeSetting;
import godweer.ui.newgui.GuiFonts;
import godweer.ui.newgui.GuiRender;
import godweer.utils.color.ColorUtils;
import godweer.utils.render.msdf.MsdfFont;
import godweer.utils.render.msdf.MsdfGlyph;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.lwjgl.glfw.GLFW;

public final class TargetHud extends Module {

    private static final EquipmentSlot[] DISPLAY_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.OFFHAND,
            EquipmentSlot.MAINHAND
    };

    private static final String ELLIPSIS = "...";
    private static final String EMPTY_SLOT_MARK = "×";
    private static final String QUESTION_MARK = "?";
    private static final String HEALTH_PREFIX = "HP ";
    private static final long TARGET_HOLD_TIME_MS = 2_000L;
    private static final float HUD_SCALE = 0.75f;
    private static final float HUD_OPACITY = 0.90f;
    private static final float TEXT_SCALE = HUD_SCALE;
    private static final float ANIMATION_SPEED = 12.0f;
    private static final float POSITION_SMOOTHING = 18.0f;
    private static final int WHITE = ColorUtils.getColor(255, 255, 255, 255);
    private static final int MUTED_WHITE = ColorUtils.getColor(205, 211, 222, 255);
    private static final int HEALTH_RED = ColorUtils.rgba(242, 83, 91, 255);
    private static final int HEALTH_YELLOW = ColorUtils.rgba(255, 194, 76, 255);
    private static final int HEALTH_GREEN = ColorUtils.rgba(91, 220, 145, 255);
    private static final int ABSORPTION_COLOR = ColorUtils.rgba(255, 202, 82, 255);

    private static boolean eventsRegistered;

    private final BooleanSetting showItems = new BooleanSetting("Предметы", true);
    private final ModeSetting hpStyle = new ModeSetting("Стиль HP", "Тема", "Тема", "Здоровье");

    private LivingEntity target;
    private LivingEntity cachedTarget;
    private long lastSeen;
    private long lastFrameNanos;
    private float anim;
    private float displayHealth;

    private float posX = -1.0f;
    private float posY = -1.0f;
    private float renderX = -1.0f;
    private float renderY = -1.0f;
    private boolean dragging;
    private float grabX;
    private float grabY;

    private String targetName = "";
    private String displayName = "";
    private float cachedNameWidth = -1.0f;
    private float cachedNameSize = -1.0f;

    private int cachedHealthTenths = Integer.MIN_VALUE;
    private int cachedMaxHealthTenths = Integer.MIN_VALUE;
    private int cachedAbsorptionTenths = Integer.MIN_VALUE;
    private String healthText = "";
    private String absorptionText = "";
    private float cachedVitalsSize = -1.0f;
    private float healthPrefixWidth;
    private float healthTextWidth;
    private float absorptionTextWidth;

    private float cachedEmptyMarkerSize = -1.0f;
    private float cachedEmptyMarkerBox = -1.0f;
    private float emptyMarkerOffsetX;
    private float emptyMarkerOffsetY;

    public TargetHud() {
        super("TargetHud", "Панель с информацией о враге", ModuleCategory.INTERFACE);
        addSettings(showItems, hpStyle);
        registerEvents();
    }

    private static void registerEvents() {
        if (eventsRegistered) {
            return;
        }
        eventsRegistered = true;
        HudRenderCallback.EVENT.register((guiGraphics, deltaTracker) -> {
            TargetHud module = ModuleManager.get(TargetHud.class);
            if (module != null && module.isEnable()) {
                module.onHudRender(guiGraphics, guiGraphics.pose());
            }
        });
    }

    @Override
    public void onDisable() {
        resetTargetState();
        super.onDisable();
    }

    private void onHudRender(GuiGraphics context, PoseStack stack) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) {
            resetTargetState();
            return;
        }

        float frameDelta = nextFrameDelta();
        long now = System.currentTimeMillis();
        boolean hovered = false;
        Entity picked = mc.crosshairPickEntity;
        if (picked instanceof LivingEntity living && living != mc.getCameraEntity() && living.isAlive()
                && !(living instanceof Player player && FriendManager.isFriend(player))) {
            target = living;
            lastSeen = now;
            hovered = true;
        }

        boolean targetAlive = target != null && target.isAlive();
        boolean visible = targetAlive
                && (hovered || now - lastSeen <= TARGET_HOLD_TIME_MS);
        anim = Mth.lerp(smoothingFactor(frameDelta, ANIMATION_SPEED), anim, visible ? 1.0f : 0.0f);

        if (target == null || anim <= 0.004f) {
            if (!visible || target == null) {
                resetTargetState();
            }
            dragging = false;
            return;
        }

        if (target != cachedTarget) {
            updateTargetCache(target);
        }
        displayHealth = Mth.lerp(smoothingFactor(frameDelta, 18.0f), displayHealth,
                Math.max(0.0f, target.getHealth()));

        float width = 154.0f * HUD_SCALE;
        float cardHeight = 44.0f * HUD_SCALE;
        float slotSize = 20.0f * HUD_SCALE;
        float slotGap = 3.0f * HUD_SCALE;
        float equipmentGap = 4.0f * HUD_SCALE;
        float padding = 6.0f * HUD_SCALE;
        boolean drawItems = showItems.get();
        int slotCount = drawItems ? 6 : 4;
        float totalHeight = cardHeight + equipmentGap + slotSize;

        int scaledWidth = mc.getWindow().getGuiScaledWidth();
        int scaledHeight = mc.getWindow().getGuiScaledHeight();
        if (posX < 0.0f || posY < 0.0f) {
            posX = 20.0f;
            posY = scaledHeight * 0.32f;
        }
        if (renderX < 0.0f || renderY < 0.0f) {
            renderX = posX;
            renderY = posY;
        }

        double windowScale = Math.max(1.0, mc.getWindow().getGuiScale());
        double mouseX = mc.mouseHandler.xpos() / windowScale;
        double mouseY = mc.mouseHandler.ypos() / windowScale;
        boolean mouseDown = GLFW.glfwGetMouseButton(mc.getWindow().getWindow(),
                GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (mouseDown && !dragging && anim > 0.5f
                && mouseX >= renderX && mouseX <= renderX + width
                && mouseY >= renderY && mouseY <= renderY + totalHeight) {
            dragging = true;
            grabX = (float) mouseX - renderX;
            grabY = (float) mouseY - renderY;
        }
        if (!mouseDown) {
            dragging = false;
        }

        float maxX = Math.max(2.0f, scaledWidth - width - 2.0f);
        float maxY = Math.max(2.0f, scaledHeight - totalHeight - 2.0f);
        if (dragging) {
            posX = Mth.clamp((float) mouseX - grabX, 2.0f, maxX);
            posY = Mth.clamp((float) mouseY - grabY, 2.0f, maxY);
        } else {
            posX = Mth.clamp(posX, 2.0f, maxX);
            posY = Mth.clamp(posY, 2.0f, maxY);
        }

        float positionFactor = smoothingFactor(frameDelta, POSITION_SMOOTHING);
        renderX = Mth.lerp(positionFactor, renderX, posX);
        renderY = Mth.lerp(positionFactor, renderY, posY);

        float x = renderX;
        float y = renderY;
        float animationAlpha = smoothStep(anim);
        float opacityFactor = animationAlpha * HUD_OPACITY;
        float visualScale = 0.96f + 0.04f * anim;
        float centerX = x + width * 0.5f;
        float centerY = y + totalHeight * 0.5f;
        stack.pushPose();
        stack.translate(centerX, centerY, 0.0f);
        stack.scale(visualScale, visualScale, 1.0f);
        stack.translate(-centerX, -centerY, 0.0f);

        float healthRatio = Mth.clamp(displayHealth / Math.max(1.0f, target.getMaxHealth()), 0.0f, 1.0f);
        int themeColor = ThemeManager.getColor();
        int accentColor = getAccentColor(healthRatio, themeColor);
        int accent = ColorUtils.multAlpha(accentColor, opacityFactor);
        int panelTop = ColorUtils.multAlpha(ColorUtils.multDark(themeColor, 0.30f), opacityFactor);
        int panelBottom = ColorUtils.multAlpha(ColorUtils.multDark(themeColor, 0.14f), opacityFactor);
        int outline = ColorUtils.multAlpha(WHITE, 0.12f * opacityFactor);
        int slotBackground = ColorUtils.multAlpha(WHITE, 0.045f * opacityFactor);
        int textColor = ColorUtils.multAlpha(WHITE, opacityFactor);
        int mutedColor = ColorUtils.multAlpha(MUTED_WHITE, opacityFactor);
        int trackColor = ColorUtils.multAlpha(WHITE, 0.12f * opacityFactor);
        float cardRadius = 8.0f * HUD_SCALE;

        GuiRender.drawGradientRound(stack, x, y, width, cardHeight, cardRadius,
                panelTop, panelBottom, panelTop, panelBottom);
        GuiRender.roundedOutline(stack, x, y, width, cardHeight,
                0.8f * HUD_SCALE, outline, cardRadius);

        float markerWidth = 30.0f * HUD_SCALE;
        GuiRender.roundedRect(stack, x + (width - markerWidth) * 0.5f,
                y + cardHeight - 2.0f * HUD_SCALE, markerWidth, 1.5f * HUD_SCALE,
                HUD_SCALE, accent);

        float headSize = 32.0f * HUD_SCALE;
        float headX = x + padding;
        float headY = y + (cardHeight - headSize) * 0.5f;
        float headRadius = 7.0f * HUD_SCALE;
        int headBackground = ColorUtils.multAlpha(ColorUtils.multDark(themeColor, 0.12f), opacityFactor);
        GuiRender.roundedWithOutline(stack, headX, headY, headSize, headSize,
                0.8f * HUD_SCALE, headBackground, outline, headRadius);
        drawHead(stack, target, headX, headY, headSize, headRadius, opacityFactor, textColor);

        float infoX = headX + headSize + 7.0f * HUD_SCALE;
        float infoRight = x + width - padding;
        float infoWidth = infoRight - infoX;
        float nameSize = 8.0f * TEXT_SCALE;
        float healthSize = 7.0f * TEXT_SCALE;
        updateDisplayName(infoWidth, nameSize);
        if (!displayName.isEmpty()) {
            GuiFonts.SF_MEDIUM.draw(stack, displayName, infoX, y + 7.0f * HUD_SCALE,
                    textColor, nameSize);
        }

        updateVitalsText(healthSize);
        float detailsY = y + 20.5f * HUD_SCALE;
        float textX = infoX;
        GuiFonts.SF_MEDIUM.draw(stack, HEALTH_PREFIX, textX, detailsY, mutedColor, healthSize);
        textX += healthPrefixWidth;
        GuiFonts.SF_MEDIUM.draw(stack, healthText, textX, detailsY, textColor, healthSize);
        textX += healthTextWidth + 3.0f * HUD_SCALE;
        if (cachedAbsorptionTenths > 0 && textX + absorptionTextWidth <= infoRight) {
            GuiFonts.SF_MEDIUM.draw(stack, absorptionText, textX, detailsY,
                    ColorUtils.multAlpha(ABSORPTION_COLOR, opacityFactor), healthSize);
        }

        float barX = infoX;
        float barY = y + cardHeight - 10.0f * HUD_SCALE;
        float barWidth = infoWidth;
        float barHeight = 4.5f * HUD_SCALE;
        float innerWidth = Math.max(0.0f, barWidth - 2.0f * HUD_SCALE);
        float innerHeight = Math.max(0.0f, barHeight - 2.0f * HUD_SCALE);
        float barRadius = barHeight * 0.5f;
        GuiRender.roundedRect(stack, barX, barY, barWidth, barHeight, barRadius, trackColor);

        float healthWidth = innerWidth * healthRatio;
        float innerX = barX + HUD_SCALE;
        float innerY = barY + HUD_SCALE;
        if (healthWidth > 0.5f && innerHeight > 0.0f) {
            GuiRender.roundedRect(stack, innerX, innerY, healthWidth, innerHeight,
                    innerHeight * 0.5f, accent);
        }

        float absorption = Math.max(0.0f, target.getAbsorptionAmount());
        if (innerWidth > healthWidth && innerHeight > 0.0f) {
            float absorptionWidth = Math.min(innerWidth - healthWidth,
                    innerWidth * absorption / Math.max(1.0f, target.getMaxHealth()));
            if (absorptionWidth > 0.5f) {
                int absorptionColor = ColorUtils.multAlpha(ABSORPTION_COLOR, opacityFactor);
                GuiRender.roundedRect(stack, innerX + healthWidth, innerY, absorptionWidth, innerHeight,
                        innerHeight * 0.5f, absorptionColor);
            }
        }

        float rowWidth = slotCount * slotSize + (slotCount - 1) * slotGap;
        float rowX = x + (width - rowWidth) * 0.5f;
        float rowY = y + cardHeight + equipmentGap;
        float slotRadius = 5.0f * HUD_SCALE;
        float emptyMarkerSize = 9.5f * TEXT_SCALE;
        updateEmptyMarkerMetrics(emptyMarkerSize, slotSize);
        int slotOutline = ColorUtils.multAlpha(WHITE, 0.10f * opacityFactor);
        int emptyMarkerColor = ColorUtils.multAlpha(MUTED_WHITE, 0.72f * opacityFactor);
        int slotIndex = 0;

        for (int i = 0; i < DISPLAY_SLOTS.length; i++) {
            if (i >= 4 && !drawItems) {
                continue;
            }
            float slotX = rowX + slotIndex * (slotSize + slotGap);
            drawSlot(context, stack, slotX, rowY, slotSize, target.getItemBySlot(DISPLAY_SLOTS[i]),
                    slotBackground, slotOutline, slotRadius, emptyMarkerColor, emptyMarkerSize,
                    opacityFactor);
            slotIndex++;
        }

        stack.popPose();
    }

    private float nextFrameDelta() {
        long now = System.nanoTime();
        if (lastFrameNanos == 0L) {
            lastFrameNanos = now;
            return 1.0f / 60.0f;
        }
        float delta = (now - lastFrameNanos) / 1_000_000_000.0f;
        lastFrameNanos = now;
        return Mth.clamp(delta, 0.0f, 0.05f);
    }

    private static float smoothingFactor(float delta, float speed) {
        return 1.0f - (float) Math.exp(-delta * speed);
    }

    private static float smoothStep(float value) {
        float clamped = Mth.clamp(value, 0.0f, 1.0f);
        return clamped * clamped * (3.0f - 2.0f * clamped);
    }

    private void updateTargetCache(LivingEntity currentTarget) {
        cachedTarget = currentTarget;
        targetName = currentTarget.getName().getString();
        displayName = targetName;
        cachedNameWidth = -1.0f;
        cachedNameSize = -1.0f;
        cachedHealthTenths = Integer.MIN_VALUE;
        cachedMaxHealthTenths = Integer.MIN_VALUE;
        cachedAbsorptionTenths = Integer.MIN_VALUE;
        cachedVitalsSize = -1.0f;
        displayHealth = Math.max(0.0f, currentTarget.getHealth());
    }

    private void updateDisplayName(float maxWidth, float size) {
        if (cachedNameWidth == maxWidth && cachedNameSize == size) {
            return;
        }
        displayName = fitName(targetName, maxWidth, size);
        cachedNameWidth = maxWidth;
        cachedNameSize = size;
    }

    private String fitName(String value, float maxWidth, float size) {
        if (GuiFonts.SF_MEDIUM.getWidth(value, size) <= maxWidth) {
            return value;
        }
        if (GuiFonts.SF_MEDIUM.getWidth(ELLIPSIS, size) > maxWidth) {
            return "";
        }

        int end = value.length();
        while (end > 0) {
            end = value.offsetByCodePoints(end, -1);
            String candidate = value.substring(0, end) + ELLIPSIS;
            if (GuiFonts.SF_MEDIUM.getWidth(candidate, size) <= maxWidth) {
                return candidate;
            }
        }
        return ELLIPSIS;
    }

    private void updateVitalsText(float size) {
        int healthTenths = Math.max(0, Math.round(target.getHealth() * 10.0f));
        int maxHealthTenths = Math.max(10, Math.round(target.getMaxHealth() * 10.0f));
        int absorptionTenths = Math.max(0, Math.round(target.getAbsorptionAmount() * 10.0f));

        if (cachedHealthTenths != healthTenths || cachedMaxHealthTenths != maxHealthTenths
                || cachedAbsorptionTenths != absorptionTenths) {
            cachedHealthTenths = healthTenths;
            cachedMaxHealthTenths = maxHealthTenths;
            cachedAbsorptionTenths = absorptionTenths;
            healthText = formatTenths(healthTenths) + " / " + formatTenths(maxHealthTenths);
            absorptionText = absorptionTenths > 0 ? "+" + formatTenths(absorptionTenths) : "";
            cachedVitalsSize = -1.0f;
        }

        if (cachedVitalsSize != size) {
            healthPrefixWidth = GuiFonts.SF_MEDIUM.getWidth(HEALTH_PREFIX, size);
            healthTextWidth = GuiFonts.SF_MEDIUM.getWidth(healthText, size);
            absorptionTextWidth = GuiFonts.SF_MEDIUM.getWidth(absorptionText, size);
            cachedVitalsSize = size;
        }
    }

    private static String formatTenths(int value) {
        return value / 10 + "." + value % 10;
    }

    private int getAccentColor(float healthRatio, int themeColor) {
        if (!hpStyle.is("Здоровье")) {
            return themeColor;
        }
        if (healthRatio >= 0.5f) {
            return ColorUtils.interpolateColor(HEALTH_YELLOW, HEALTH_GREEN, (healthRatio - 0.5f) * 2.0f);
        }
        return ColorUtils.interpolateColor(HEALTH_RED, HEALTH_YELLOW, healthRatio * 2.0f);
    }

    private void drawHead(PoseStack stack, LivingEntity target, float x, float y, float size,
                          float radius, float opacityFactor, int textColor) {
        if (target instanceof AbstractClientPlayer player) {
            PlayerSkin skin = player.getSkin();
            float inset = 1.5f * HUD_SCALE;
            float overlayInset = 0.9f * HUD_SCALE;
            int faceColor = ColorUtils.multAlpha(WHITE, opacityFactor);
            GuiRender.roundedTexture(stack, skin.texture(),
                    x + inset, y + inset, size - inset * 2.0f, size - inset * 2.0f,
                    radius - inset, 8.0f / 64.0f, 8.0f / 64.0f,
                    16.0f / 64.0f, 16.0f / 64.0f, faceColor);
            GuiRender.roundedTexture(stack, skin.texture(),
                    x + overlayInset, y + overlayInset,
                    size - overlayInset * 2.0f, size - overlayInset * 2.0f,
                    radius - overlayInset, 40.0f / 64.0f, 8.0f / 64.0f,
                    48.0f / 64.0f, 16.0f / 64.0f, faceColor);
        } else {
            drawCenteredGlyph(stack, QUESTION_MARK, '?', x, y, size, 11.0f * TEXT_SCALE, textColor);
        }
    }

    private void drawSlot(GuiGraphics context, PoseStack stack, float x, float y, float box,
                          ItemStack item, int background, int outline, float radius, int emptyColor,
                          float emptyMarkerSize, float opacityFactor) {
        GuiRender.roundedWithOutline(stack, x, y, box, box, 0.75f * HUD_SCALE,
                background, outline, radius);
        if (item.isEmpty()) {
            GuiFonts.SF_MEDIUM.draw(stack, EMPTY_SLOT_MARK,
                    x + emptyMarkerOffsetX, y + emptyMarkerOffsetY,
                    emptyColor, emptyMarkerSize);
            return;
        }

        float itemSize = box - 6.0f * HUD_SCALE;
        float itemScale = itemSize / 16.0f;
        stack.pushPose();
        try {
            stack.translate(x + (box - itemSize) * 0.5f, y + (box - itemSize) * 0.5f, 0.0f);
            stack.scale(itemScale, itemScale, 1.0f);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, opacityFactor);
            try {
                context.renderItem(item, 0, 0);
            } finally {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            }
        } finally {
            stack.popPose();
        }
    }

    private void updateEmptyMarkerMetrics(float size, float box) {
        if (cachedEmptyMarkerSize == size && cachedEmptyMarkerBox == box) {
            return;
        }
        cachedEmptyMarkerSize = size;
        cachedEmptyMarkerBox = box;
        MsdfFont font = GuiFonts.SF_MEDIUM.atlas();
        MsdfGlyph glyph = font == null ? null : font.getGlyph(EMPTY_SLOT_MARK.codePointAt(0));
        if (glyph == null) {
            float markerWidth = GuiFonts.SF_MEDIUM.getWidth(EMPTY_SLOT_MARK, size);
            float markerHeight = GuiFonts.SF_MEDIUM.getHeight(EMPTY_SLOT_MARK, size);
            emptyMarkerOffsetX = (box - markerWidth) * 0.5f;
            emptyMarkerOffsetY = (box - markerHeight) * 0.5f;
            return;
        }
        float markerWidth = glyph.getPlaneWidth() * size;
        float markerHeight = glyph.getPlaneHeight() * size;
        emptyMarkerOffsetX = (box - markerWidth) * 0.5f;
        emptyMarkerOffsetY = (box - markerHeight) * 0.5f
                - (font.getBaselineHeight() - glyph.getTopPosition()) * size;
    }

    private void drawCenteredGlyph(PoseStack stack, String text, int codePoint,
                                   float x, float y, float box, float size, int color) {
        MsdfFont font = GuiFonts.SF_MEDIUM.atlas();
        MsdfGlyph glyph = font == null ? null : font.getGlyph(codePoint);
        if (glyph == null) {
            return;
        }
        float glyphWidth = glyph.getPlaneWidth() * size;
        float glyphHeight = glyph.getPlaneHeight() * size;
        float glyphX = x + (box - glyphWidth) * 0.5f;
        float glyphY = y + (box - glyphHeight) * 0.5f
                - (font.getBaselineHeight() - glyph.getTopPosition()) * size;
        GuiFonts.SF_MEDIUM.draw(stack, text, glyphX, glyphY, color, size);
    }

    private void resetTargetState() {
        target = null;
        cachedTarget = null;
        lastSeen = 0L;
        lastFrameNanos = 0L;
        anim = 0.0f;
        displayHealth = 0.0f;
        dragging = false;
        targetName = "";
        displayName = "";
        cachedNameWidth = -1.0f;
        cachedNameSize = -1.0f;
        cachedHealthTenths = Integer.MIN_VALUE;
        cachedMaxHealthTenths = Integer.MIN_VALUE;
        cachedAbsorptionTenths = Integer.MIN_VALUE;
        healthText = "";
        absorptionText = "";
        cachedVitalsSize = -1.0f;
    }
}