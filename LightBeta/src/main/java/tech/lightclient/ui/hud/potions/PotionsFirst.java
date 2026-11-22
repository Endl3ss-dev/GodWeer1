package tech.lightclient.ui.hud.potions;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.client.utils.render.drag.api.Dragging;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.ui.hud.api.HudElement;
import tech.lightclient.ui.hud.first.FirstHudTheme;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionsFirst extends PotionsBase implements HudElement {
    private static final float ICON_SIZE = 8.2f;
    private static final float ICON_TO_DIVIDER_GAP = 3.8f;
    private static final float DIVIDER_TO_TEXT_GAP = 4.6f;
    private static final float DIVIDER_HEIGHT = 8f;

    public PotionsFirst(Dragging dragging) {
        super(dragging);
    }

    @Override
    public void render(EventRender.Screen.Hud event) {
        if (mc.player == null || mc.world == null) {
            return;
        }

        float startScale = Interface.isBounceAnimation() ? 0f : 0.8f;
        float scaleFactor = Interface.getHudScale(SETTINGS_SCOPE);
        float x = Interface.scalePos(SETTINGS_SCOPE, dragging.getX());
        float y = Interface.scalePos(SETTINGS_SCOPE, dragging.getY());
        applyDragDelta(x, y);

        float dt = updateAnimTime();
        boolean chatOpen = MinecraftClient.getInstance().currentScreen instanceof ChatScreen;
        boolean showIcons = DraggingManager.getHudBoolean(SETTINGS_SCOPE, SETTING_SHOW_EFFECT_ICON, true);
        List<StatusEffectInstance> active = new ArrayList<>(mc.player.getStatusEffects());
        active.sort(Comparator.comparing(instance -> instance.getEffectType().getIdAsString()));

        List<RegistryEntry<StatusEffect>> visible = new ArrayList<>();
        List<RegistryEntry<StatusEffect>> activeList = new ArrayList<>();
        float targetMaxWidth = getHeaderMinWidth();

        for (StatusEffectInstance instance : active) {
            RegistryEntry<StatusEffect> effect = instance.getEffectType();
            lastEffects.put(effect, instance);
            float anim = effectAnim.getOrDefault(effect, 0f);
            anim = animate(anim, 1f, ITEM_ANIM_DURATION_MS, dt);
            effectAnim.put(effect, anim);
            activeList.add(effect);
            visible.add(effect);
            targetMaxWidth = Math.max(targetMaxWidth, getRowWidth(instance, showIcons));
        }

        for (RegistryEntry<StatusEffect> effect : new ArrayList<>(effectAnim.keySet())) {
            if (activeList.contains(effect)) {
                continue;
            }
            float anim = effectAnim.getOrDefault(effect, 0f);
            anim = animate(anim, 0f, ITEM_ANIM_DURATION_MS, dt);
            if (anim <= 0.01f) {
                effectAnim.remove(effect);
                effectY.remove(effect);
                lastEffects.remove(effect);
                timeTextCurrent.remove(effect);
                timeTextPrevious.remove(effect);
                timeTextStartMs.remove(effect);
                continue;
            }
            effectAnim.put(effect, anim);
            visible.add(effect);
            StatusEffectInstance last = lastEffects.get(effect);
            if (last != null) {
                targetMaxWidth = Math.max(targetMaxWidth, getRowWidth(last, showIcons));
            }
        }

        if (Float.isNaN(width) || width == 0f) {
            width = getHeaderMinWidth();
        }
        width = animate(width, targetMaxWidth, ITEM_ANIM_DURATION_MS * 1.5f, dt);

        boolean shouldShow = DraggingManager.getHudBoolean(SETTINGS_SCOPE, SETTING_ALWAYS_SHOW, false) || chatOpen || !visible.isEmpty();
        animation.setDuration(Interface.getAlphaDurationMs());
        if (shouldShow) {
            animation.show();
        } else {
            animation.hide();
        }

        float alpha = animation.getProgress();
        boolean hiding = animation.getTarget() == 0f;
        if (alpha <= 0f && animation.get() == 0) {
            width = 0f;
            height = 0f;
            dragging.setWidth(0f);
            dragging.setHeight(0f);
            return;
        }

        float listStartY = y + FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.ROW_GAP;
        float targetY = listStartY;
        Map<RegistryEntry<StatusEffect>, Float> targetPositions = new HashMap<>();
        Map<RegistryEntry<StatusEffect>, Float> rowHeights = new HashMap<>();
        for (int i = 0; i < visible.size(); i++) {
            RegistryEntry<StatusEffect> effect = visible.get(i);
            float anim = Math.max(0f, Math.min(1f, effectAnim.getOrDefault(effect, 0f)));
            float rowHeight = FirstHudTheme.ROW_HEIGHT * anim;
            targetPositions.put(effect, targetY);
            rowHeights.put(effect, rowHeight);
            targetY += rowHeight + (i == visible.size() - 1 ? 0f : FirstHudTheme.ROW_GAP * anim);
        }

        float containerHeight = Math.max(FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING, targetY - y + FirstHudTheme.SIDE_PADDING);
        float scale = Interface.animatedScale(startScale, alpha, hiding);
        float centerX = x + width * 0.5f;
        float centerY = y + containerHeight * 0.5f;
        event.getRenderer().pushScale(scale, scale, centerX, centerY);

        FirstHudTheme.drawPanel(event, SETTINGS_SCOPE, x, y, width, containerHeight, alpha);
        FirstHudTheme.drawHeader(event, x, y, width, alpha, "q", "Potions");

        float maxBottom = y + FirstHudTheme.HEADER_HEIGHT;
        for (int i = 0; i < visible.size(); i++) {
            RegistryEntry<StatusEffect> effect = visible.get(i);
            float anim = effectAnim.getOrDefault(effect, 0f);
            if (anim <= 0.01f) {
                continue;
            }
            StatusEffectInstance instance = lastEffects.get(effect);
            if (instance == null) {
                continue;
            }

            Float currentY = effectY.get(effect);
            float targetRowY = targetPositions.getOrDefault(effect, listStartY);
            if (currentY == null) {
                currentY = targetRowY;
            }
            currentY = animate(currentY, targetRowY, 25f, dt);
            effectY.put(effect, currentY);

            float rowAlpha = Math.min(1f, anim) * alpha;
            float rowHeight = rowHeights.getOrDefault(effect, FirstHudTheme.ROW_HEIGHT);
            float nameX = x + FirstHudTheme.SIDE_PADDING;
            if (showIcons) {
                float iconY = currentY + (rowHeight - ICON_SIZE) * 0.5f;
                renderEffectIcon(event, effect, nameX, iconY, rowAlpha);
                float dividerX = nameX + ICON_SIZE + ICON_TO_DIVIDER_GAP;
                float dividerY = currentY + rowHeight * 0.5f - DIVIDER_HEIGHT * 0.5f;
                event.getRenderer().rect(dividerX, dividerY, 0.8f, DIVIDER_HEIGHT, 0.3f, ClientColors.applyAlpha(FirstHudTheme.ROW_DIVIDER, rowAlpha));
                nameX = dividerX + DIVIDER_TO_TEXT_GAP;
            }

            String leftText = formatEffectName(instance);
            String rightText = formatDuration(instance.getDuration());
            AnimatedText animatedTime = getAnimatedTimeText(effect, rightText, FirstHudTheme.ROW_TEXT_SIZE);
            float textY = currentY + rowHeight * 0.5f + FontRegistry.centeredBaselineOffset(FontRegistry.DELTA_HUD, 'H', FirstHudTheme.ROW_TEXT_SIZE) + 0.3f;
            float timeWidth = animatedTime.maxWidth > 0f ? animatedTime.maxWidth : FontRegistry.DELTA_HUD.getWidth(rightText, FirstHudTheme.ROW_TEXT_SIZE);
            float timeX = x + width - FirstHudTheme.SIDE_PADDING - timeWidth;

            event.getRenderer().text(
                    FontRegistry.DELTA_HUD,
                    nameX,
                    textY,
                    FirstHudTheme.ROW_TEXT_SIZE,
                    leftText,
                    ClientColors.applyAlpha(FirstHudTheme.TEXT_COLOR, rowAlpha)
            );
            renderAnimatedTime(event, timeX, textY, FirstHudTheme.ROW_TEXT_SIZE, animatedTime, rowAlpha, 1f);

            if (i < visible.size() - 1) {
                float separatorY = currentY + rowHeight + FirstHudTheme.ROW_GAP * 0.5f - 0.35f;
                event.getRenderer().rect(x + FirstHudTheme.SIDE_PADDING, separatorY, width - FirstHudTheme.SIDE_PADDING * 2f, 0.7f, 0.25f, ClientColors.applyAlpha(FirstHudTheme.SEPARATOR, rowAlpha));
            }
            maxBottom = Math.max(maxBottom, currentY + rowHeight);
        }

        event.getRenderer().popScale();

        height = Math.max(FirstHudTheme.HEADER_HEIGHT + FirstHudTheme.SIDE_PADDING, maxBottom - y + FirstHudTheme.SIDE_PADDING);
        dragging.setWidth(width * scaleFactor);
        dragging.setHeight(height * scaleFactor);
    }

    private void renderEffectIcon(EventRender.Screen.Hud event, RegistryEntry<StatusEffect> effect, float x, float y, float alpha) {
        Identifier effectTexture = InGameHud.getEffectTexture(effect);
        if (effectTexture == null) {
            return;
        }

        SpriteAtlasTexture guiAtlas = mc.getAtlasManager().getAtlasTexture(Identifier.ofVanilla("gui"));
        Sprite sprite = guiAtlas.getSprite(effectTexture);
        if (sprite == null) {
            return;
        }

        event.getRenderer().drawTextureRegion(
                sprite.getAtlasId(),
                x,
                y,
                ICON_SIZE,
                ICON_SIZE,
                sprite.getMinU(),
                sprite.getMinV(),
                sprite.getMaxU(),
                sprite.getMaxV(),
                ClientColors.applyAlpha(0xFFFFFFFF, alpha)
        );
    }

    private float getHeaderMinWidth() {
        return FirstHudTheme.getHeaderMinWidth("Potions", "q", 18f);
    }

    private float getRowWidth(StatusEffectInstance instance, boolean showIcons) {
        float iconPart = showIcons ? ICON_SIZE + ICON_TO_DIVIDER_GAP + DIVIDER_TO_TEXT_GAP + 0.8f : 0f;
        return FirstHudTheme.SIDE_PADDING * 2f
                + iconPart
                + FontRegistry.DELTA_HUD.getWidth(formatEffectName(instance), FirstHudTheme.ROW_TEXT_SIZE)
                + FontRegistry.DELTA_HUD.getWidth(formatDuration(instance.getDuration()), FirstHudTheme.ROW_TEXT_SIZE)
                + 16f;
    }
}
