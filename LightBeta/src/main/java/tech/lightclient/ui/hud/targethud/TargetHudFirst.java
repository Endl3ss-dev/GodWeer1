package tech.lightclient.ui.hud.targethud;

import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.HudShadowRenderer;
import tech.lightclient.ui.hud.api.HudElement;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.List;

import static tech.lightclient.client.ClientColors.applyAlpha;

public class TargetHudFirst extends TargetHudBase implements HudElement {
    private static final float PANEL_RADIUS = 7.5f;
    private static final float OUTLINE = 0.9f;
    private static final float HUD_WIDTH = 92f;
    private static final float HUD_HEIGHT = 31f;
    private static final float SIDE_PADDING = 6.75f;
    private static final float FACE_SIZE = 23f;
    private static final float TITLE_SIZE = 6.2f;
    private static final float INFO_SIZE = 5.7f;
    private static final float ITEM_SIZE = 8.0f;
    private static final float ITEM_STEP = 8.0f;
    private static final float CONTENT_TEXT_Y = 12.5f;
    private static final float ITEM_ROW_Y = 20.1f;

    private static final float WIDTH_GAIN_SPEED = 7.8f;
    private static final float WIDTH_LOSS_SPEED = 6.0f;
    private static final float HEALTH_GAIN_SPEED = 7.2f;
    private static final float HEALTH_LOSS_SPEED = 3.2f;
    private static final float ABSORPTION_GAIN_SPEED = 7.4f;
    private static final float ABSORPTION_LOSS_SPEED = 3.8f;
    private static final float BAR_GAIN_SPEED = 7.8f;
    private static final float BAR_LOSS_SPEED = 4.0f;
    private static final float DEATH_FADE_SPEED = 24.0f;

    private static final int PANEL_BG = rgba(29, 29, 31, 236);
    private static final int OUTLINE_COLOR = rgba(56, 56, 60, 160);
    private static final int LEFT_TOP = rgba(34, 34, 40, 202);
    private static final int LEFT_BOTTOM = rgba(28, 28, 34, 202);
    private static final int TEXT_COLOR = rgba(255, 255, 255, 255);
    private static final int MUTED_COLOR = rgba(255, 255, 255, 255);
    private static final int SEPARATOR_COLOR = rgba(255, 255, 255, 12);
    private static final int HEALTH_WHITE = rgba(245, 245, 245, 245);
    private static final int HEALTH_GOLD = rgba(255, 215, 0, 245);

    private float animatedWidth = HUD_WIDTH;
    private float displayedHealth = 0f;
    private float displayedAbsorption = 0f;
    private float displayedProgress = 0f;
    private int trackedTargetId = Integer.MIN_VALUE;

    public TargetHudFirst(Dragging dragging) {
        super(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        updateTarget();

        float startScale = Interface.isBounceAnimation() ? 0f : 0.8f;
        animation.setDuration(Interface.getAlphaDurationMs());
        float alpha = animation.getProgress();
        boolean hiding = animation.getTarget() == 0f;
        float scale = Interface.animatedScale(startScale, alpha, hiding);

        if (alpha <= 0f && animation.get() == 0) {
            lastTarget = null;
            trackedTargetId = Integer.MIN_VALUE;
            return;
        }
        if (lastTarget == null) {
            return;
        }

        long currentTime = System.nanoTime();
        float deltaTime = Math.min((currentTime - lastTime) / 1_000_000_000.0f, 0.1f);
        lastTime = currentTime;

        updateFaceDamageTint(currentTime, deltaTime);

        if (lastTarget.getId() != trackedTargetId) {
            trackedTargetId = lastTarget.getId();
            displayedHealth = Math.max(0f, PlayerUtils.getHealthFloat(lastTarget));
            displayedAbsorption = Math.max(0f, lastTarget.getAbsorptionAmount());
            float maxHealth = Math.max(1f, lastTarget.getMaxHealth());
            displayedProgress = Math.min(1f, (displayedHealth + displayedAbsorption) / maxHealth);
            animatedWidth = HUD_WIDTH;
        }

        float health = Math.max(0f, PlayerUtils.getHealthFloat(lastTarget));
        float maxHealth = Math.max(1f, lastTarget.getMaxHealth());
        float absorption = Math.max(0f, lastTarget.getAbsorptionAmount());
        float progressTarget = Math.max(0f, Math.min(1f, (health + absorption) / maxHealth));
        boolean depleted = !lastTarget.isAlive() || (health <= 0f && absorption <= 0f);

        if (depleted) {
            displayedHealth = animateValue(displayedHealth, 0f, deltaTime, DEATH_FADE_SPEED, DEATH_FADE_SPEED);
            displayedAbsorption = animateValue(displayedAbsorption, 0f, deltaTime, DEATH_FADE_SPEED, DEATH_FADE_SPEED);
            displayedProgress = animateValue(displayedProgress, 0f, deltaTime, DEATH_FADE_SPEED, DEATH_FADE_SPEED);
        } else {
            displayedHealth = animateValue(displayedHealth, health, deltaTime, HEALTH_GAIN_SPEED, HEALTH_LOSS_SPEED);
            displayedAbsorption = animateValue(displayedAbsorption, absorption, deltaTime, ABSORPTION_GAIN_SPEED, ABSORPTION_LOSS_SPEED);
            displayedProgress = animateValue(displayedProgress, progressTarget, deltaTime, BAR_GAIN_SPEED, BAR_LOSS_SPEED);
        }

        String healthText = formatHealthText(displayedHealth + displayedAbsorption);
        String displayName = getDisplayName(lastTarget, 48);
        float targetWidth = calculateTargetWidth(displayName, healthText, getTargetItems(lastTarget));
        animatedWidth = animateValue(animatedWidth, Math.max(HUD_WIDTH, targetWidth), deltaTime, WIDTH_GAIN_SPEED, WIDTH_LOSS_SPEED);

        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());
        float centerX = x + animatedWidth * 0.5f;
        float centerY = y + HUD_HEIGHT * 0.5f;

        event.getRenderer().pushScale(scale, scale, centerX, centerY);
        drawPanel(event, x, y, alpha);
        drawHead(event, x, y, alpha);
        drawContent(event, x, y, alpha, scale, displayName, healthText);
        drawItems(event, x, y, alpha);
        drawHealthBar(event, x, y, alpha, maxHealth);
        event.getRenderer().popScale();

        width = animatedWidth;
        height = HUD_HEIGHT;
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private void drawPanel(EventRender.Screen.Hud event, float x, float y, float alpha) {
        HudShadowRenderer.render(event.getRenderer(), SETTINGS_SCOPE, x, y, animatedWidth, HUD_HEIGHT, PANEL_RADIUS, alpha, Interface.getHudScale(SETTINGS_SCOPE));
        Interface.renderHudBackground(event.getRenderer(), x, y, animatedWidth, HUD_HEIGHT, PANEL_RADIUS, alpha);
        event.getRenderer().rect(x, y, animatedWidth, HUD_HEIGHT, PANEL_RADIUS, applyAlpha(OUTLINE_COLOR, alpha));
        if (!Interface.isHudBlurBackground()) {
            event.getRenderer().rect(
                    x + OUTLINE,
                    y + OUTLINE,
                    animatedWidth - OUTLINE * 2f,
                    HUD_HEIGHT - OUTLINE * 2f,
                    PANEL_RADIUS - 0.75f,
                    applyAlpha(PANEL_BG, alpha)
            );
        }

        float faceX = x + 4.8f;
        float separatorX = faceX + FACE_SIZE + 4.4f;
        float leftSectionWidth = separatorX - x - 0.5f;
        if (!Interface.isHudBlurBackground()) {
            event.getRenderer().gradient(
                    x + OUTLINE,
                    y + OUTLINE,
                    Math.max(1f, leftSectionWidth - OUTLINE),
                    HUD_HEIGHT - OUTLINE * 2f,
                    PANEL_RADIUS - 0.75f, 0f, 0f, PANEL_RADIUS - 0.75f,
                    applyAlpha(LEFT_TOP, alpha),
                    applyAlpha(LEFT_TOP, alpha),
                    applyAlpha(LEFT_BOTTOM, alpha),
                    applyAlpha(LEFT_BOTTOM, alpha)
            );
        }
        event.getRenderer().rect(
                x + leftSectionWidth - 0.5f,
                y + 2.8f,
                0.5f,
                HUD_HEIGHT - 5.6f,
                0f,
                applyAlpha(SEPARATOR_COLOR, alpha)
        );
    }

    private void drawHead(EventRender.Screen.Hud event, float x, float y, float alpha) {
        float faceX = x + 4.8f;
        float faceY = y + 3.9f;
        int skinTint = applyAlpha(headColor.getRGB(), alpha);

        if (lastTarget instanceof AbstractClientPlayerEntity player) {
            initialColor = new Color(255, 255, 255, 255);
            Identifier skin = player.getSkin().body().texturePath();
            drawSkinRegionNearest(event.getRenderer(), skin, faceX, faceY, FACE_SIZE, FACE_SIZE,
                    8f / 64f, 8f / 64f, 16f / 64f, 16f / 64f, skinTint, 4.5f);
            drawSkinRegionNearest(event.getRenderer(), skin, faceX - 0.4f, faceY - 0.4f, FACE_SIZE + 0.8f, FACE_SIZE + 0.8f,
                    40f / 64f, 8f / 64f, 48f / 64f, 16f / 64f, skinTint, 4.5f);
            return;
        }

        Identifier texture = resolveEntityTexture(lastTarget);
        FaceUv faceUv = texture == null ? null : resolveMobFaceUv(texture);
        initialColor = new Color(255, 255, 255, 255);
        if (texture != null && faceUv != null) {
            drawSkinRegionNearest(event.getRenderer(), texture, faceX, faceY, FACE_SIZE, FACE_SIZE,
                    faceUv.u0(), faceUv.v0(), faceUv.u1(), faceUv.v1(), skinTint, 4.5f);
        } else {
            event.getRenderer().rect(faceX, faceY, FACE_SIZE, FACE_SIZE, 4.5f, skinTint);
        }
    }

    private void drawContent(EventRender.Screen.Hud event, float x, float y, float alpha, float scale, String rawName, String healthText) {
        float separatorX = x + 4.8f + FACE_SIZE + 4.4f;
        float nameX = separatorX + 5.0f;
        float healthWidth = FontRegistry.DELTA_HUD.getWidth(healthText, INFO_SIZE + 0.2f);
        float healthX = x + animatedWidth - SIDE_PADDING - healthWidth;
        float nameMaxWidth = Math.max(18f, healthX - nameX - 7f);
        String name = trimToWidth(rawName, nameMaxWidth);

        float nameY = y + CONTENT_TEXT_Y;
        float healthY = nameY + 0.1f;

        beginScaledScissor(nameX, y + 7.8f, Math.max(1f, healthX - nameX - 3f), 9.0f,
                x + animatedWidth * 0.5f, y + HUD_HEIGHT * 0.5f, scale);
        event.getRenderer().text(FontRegistry.DELTA_HUD, nameX, nameY, TITLE_SIZE, name, applyAlpha(TEXT_COLOR, alpha));
        endScissor();

        drawHealthText(event, healthX, healthY, healthText, alpha);
    }

    private void drawHealthText(EventRender.Screen.Hud event, float x, float y, String healthText, float alpha) {
        if (!healthText.endsWith("hp") || healthText.length() <= 2) {
            event.getRenderer().text(FontRegistry.DELTA_HUD, x, y, INFO_SIZE + 0.2f, healthText, applyAlpha(TEXT_COLOR, alpha));
            return;
        }

        String valuePart = healthText.substring(0, healthText.length() - 2);
        event.getRenderer().text(FontRegistry.DELTA_HUD, x, y, INFO_SIZE + 0.2f, valuePart, applyAlpha(TEXT_COLOR, alpha));
        float valueWidth = FontRegistry.DELTA_HUD.getWidth(valuePart, INFO_SIZE + 0.2f);
        event.getRenderer().text(FontRegistry.DELTA_HUD, x + valueWidth, y, INFO_SIZE + 0.2f, "hp", applyAlpha(MUTED_COLOR, alpha));
    }

    private void drawItems(EventRender.Screen.Hud event, float x, float y, float alpha) {
        List<ItemStack> items = getTargetItems(lastTarget);
        float itemX = x + 4.8f + FACE_SIZE + 8.7f;
        float itemY = y + ITEM_ROW_Y;

        for (int i = 0; i < 6; i++) {
            float slotX = itemX + i * ITEM_STEP;
            ItemStack stack = i < items.size() ? items.get(i) : ItemStack.EMPTY;
            if (stack == null || stack.isEmpty()) {
                continue;
            }
            renderItemViaContext(event.getRenderer(), stack, slotX, itemY - 5, ITEM_SIZE, alpha);
        }
    }

    private void drawHealthBar(EventRender.Screen.Hud event, float x, float y, float alpha, float maxHealth) {
        float barX = x + 4.8f + FACE_SIZE + 4.4f - 0.5f;
        float barHeight = 2.3f;
        float barY = y + HUD_HEIGHT - barHeight;
        float barEndX = x + animatedWidth - SIDE_PADDING;
        float barWidth = Math.max(10.0f, barEndX - barX);
        float totalRatio = Math.max(0f, Math.min(1f, displayedProgress));
        if (totalRatio <= 0.001f) {
            return;
        }

        float totalWidth = barWidth * totalRatio;
        float healthWidth = Math.min(totalWidth, barWidth * Math.max(0f, Math.min(1f, displayedHealth / maxHealth)));
        float absorptionWidth = Math.max(0f, totalWidth - healthWidth);

        if (healthWidth > 0f) {
            float rightRound = absorptionWidth <= 0f ? 1.1f : 0f;
            event.getRenderer().rect(barX, barY, healthWidth, barHeight, 0f, rightRound, rightRound, 0f, applyAlpha(HEALTH_WHITE, alpha));
        }
        if (absorptionWidth > 0f) {
            event.getRenderer().rect(barX + healthWidth, barY, absorptionWidth, barHeight, 0f, 1.1f, 1.1f, 0f, applyAlpha(HEALTH_GOLD, alpha));
        }
    }

    private float calculateTargetWidth(String name, String healthText, List<ItemStack> items) {
        float healthWidth = FontRegistry.DELTA_HUD.getWidth(healthText, INFO_SIZE + 0.2f);
        float textWidth = FontRegistry.DELTA_HUD.getWidth(name, TITLE_SIZE) + 10.0f + healthWidth;
        float itemsWidth = 24.0f;
        if (items != null && !items.isEmpty()) {
            itemsWidth = Math.max(itemsWidth, Math.min(6, items.size()) * ITEM_STEP);
        }
        float contentWidth = Math.max(textWidth, itemsWidth + 12.0f);
        return 4.8f + FACE_SIZE + 4.4f + 5.0f + contentWidth + SIDE_PADDING;
    }

    private String trimToWidth(String text, float maxWidth) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        if (FontRegistry.DELTA_HUD.getWidth(text, TITLE_SIZE) <= maxWidth) {
            return text;
        }

        String result = text;
        while (result.length() > 2 && FontRegistry.DELTA_HUD.getWidth(result + "...", TITLE_SIZE) > maxWidth) {
            result = result.substring(0, result.length() - 1);
        }
        return result + "...";
    }

    private String formatHealthText(float health) {
        if (lastTarget == null) {
            return "0hp";
        }
        if (PlayerUtils.shouldHideHealth(lastTarget)) {
            return "????";
        }
        return formatHealthValue(Math.max(0f, health)) + "hp";
    }

    private void updateFaceDamageTint(long currentTime, float deltaTime) {
        float hurtTime = lastTarget.hurtTime / 7f;
        float timeSinceDamage = (currentTime - lastDamageTime) / 1_000_000_000.0f;
        Color redColor = new Color(255, 70, 70, 255);
        if (hurtTime > 1f) {
            lastDamageTime = currentTime;
            damageAnimationProgress = lerp(damageAnimationProgress, 1f, deltaTime / 0.1f);
        } else if (hurtTime < 1f) {
            damageAnimationProgress = lerp(damageAnimationProgress, 0f, deltaTime / 0.15f);
        } else if (timeSinceDamage < RED_DURATION) {
            damageAnimationProgress = 1f;
        }
        headColor = lerpColorWithAlpha(initialColor, redColor, damageAnimationProgress);
    }

    private float animateValue(float current, float target, float deltaTime, float gainSpeed, float lossSpeed) {
        float speed = target > current ? gainSpeed : lossSpeed;
        float factor = (float) (1.0 - Math.pow(0.001, deltaTime * speed));
        float value = current + (target - current) * factor;
        if (Math.abs(value - target) < 0.02f) {
            return target;
        }
        return value;
    }

    private static int rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a).getRGB();
    }
}
