package fun.volon.client.modules.impl.render.base.implement;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import fun.volon.Volon;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.animation.AnimationUtils;
import fun.volon.api.utils.animation.Easings;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.draggable.Draggable;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.fonts.msdf.Font;
import fun.volon.api.utils.render.fonts.msdf.Fonts;
import fun.volon.api.utils.scissor.ScissorUtils;
import fun.volon.client.modules.impl.render.Interface;
import fun.volon.client.modules.impl.render.base.InterfaceProcessing;
import fun.volon.client.ui.preview.PreviewState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Potions1 extends InterfaceProcessing {

    private static final class PotionSnapshot {
        RegistryEntry<StatusEffect> entry;
        String baseName;
        int amplifier;
        int duration;
        boolean infinite;
    }

    private final Map<StatusEffect, AnimationUtils> animations = new LinkedHashMap<>();
    private final Map<StatusEffect, PotionSnapshot> snapshots = new HashMap<>();
    private final AnimationUtils widthAnimation = new AnimationUtils(76, 10.5f, Easings.QUAD_OUT);

    private static final String HEADER_GLYPH = "v";
    private static final String TITLE        = "Potions";

    private Font text(int size) { return Fonts.getFont("suisse", size); }
    private Font icon(int size) { return Fonts.getFont("energy", size); }

    private static final float RADIUS       = 4f;
    private static final float PAD_X        = 5f;
    private static final float HEADER_H     = 13f;
    private static final float ROW_H        = 13f;
    private static final float BODY_PAD_TOP = 1f;
    private static final float BODY_PAD_BOT = 2f;
    private static final float ICON_GAP     = 2f;
    private static final float MIN_WIDTH    = 76f;
    private static final int   TEXT_FONT_SIZE  = 12;
    private static final int   ICON_FONT_SIZE  = 15;
    private static final int   LEVEL_FONT_SIZE = 11;
    private static final float ROW_SPRITE_SIZE = 7.5f;

    private static final float TEXT_BASE_Y = 5.3f;
    private static final float ICON_BASE_Y = 5.3f;

    private static final int TEXT_WHITE = ColorUtils.rgba(255, 255, 255, 255);

    private static int alphaFromPercent(float percent) {
        int a = (int) (255f * percent / 100f);
        if (a < 0) return 0;
        if (a > 255) return 255;
        return a;
    }
    private static int bodyBg() {
        return ColorUtils.rgba(7, 7, 9,
                alphaFromPercent(Interface.INSTANCE != null ? Interface.INSTANCE.potions1BodyAlpha.get() : 57f));
    }
    private static int headerBg() {
        return ColorUtils.rgba(10, 10, 12,
                alphaFromPercent(Interface.INSTANCE != null ? Interface.INSTANCE.potions1HeaderAlpha.get() : 85f));
    }

    private static int accentColor() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) return ColorUtils.getThemeColor();
        var theme = Volon.INSTANCE.themeStorage.getThemes().getTheme();
        if (theme == null) return ColorUtils.getThemeColor();
        if ("Rainbow".equals(theme.getName())) return ColorUtils.getThemeColor();
        return theme.color != null && theme.color.length > 0 ? theme.color[0] : ColorUtils.getThemeColor();
    }

    public Potions1(Draggable draggable) {
        super(draggable);
    }

    private AnimationUtils getAnimation(StatusEffect effect) {
        return animations.computeIfAbsent(effect, e -> new AnimationUtils(0, 10.5f, Easings.QUAD_OUT));
    }

    private static String formatDuration(int duration, boolean infinite) {
        if (infinite) return "inf";
        int seconds = Math.max(0, duration / 20);
        int minutes = seconds / 60;
        int secs = seconds % 60;
        return minutes + ":" + (secs < 10 ? "0" + secs : String.valueOf(secs));
    }

    private static String toRoman(int num) {
        String[] thousands = {"", "M", "MM", "MMM"};
        String[] hundreds  = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
        String[] tens      = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
        String[] ones      = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};
        if (num <= 0 || num >= 4000) return String.valueOf(num);
        return thousands[num / 1000] + hundreds[(num % 1000) / 100] + tens[(num % 100) / 10] + ones[num % 10];
    }

    private void updateSnapshot(StatusEffectInstance effect) {
        StatusEffect type = effect.getEffectType().value();
        PotionSnapshot snapshot = snapshots.computeIfAbsent(type, e -> new PotionSnapshot());
        snapshot.entry = effect.getEffectType();
        snapshot.baseName = I18n.translate(effect.getTranslationKey());
        snapshot.amplifier = effect.getAmplifier() + 1;
        snapshot.duration = effect.getDuration();
        snapshot.infinite = effect.isInfinite();
    }

    private List<StatusEffect> buildRenderOrder(Collection<StatusEffectInstance> effects, Set<StatusEffect> active) {
        List<StatusEffect> order = new ArrayList<>();
        Set<StatusEffect> seen = new HashSet<>();
        for (StatusEffectInstance effect : effects) {
            StatusEffect type = effect.getEffectType().value();
            if (seen.add(type)) order.add(type);
        }
        for (StatusEffect type : animations.keySet()) {
            if (!active.contains(type) && seen.add(type)) order.add(type);
        }
        return order;
    }

    @Override
    public void onRender(EventRender.Default eventRender) {
        MatrixStack m = eventRender.getContext().getMatrices();
        float x = draggable.getX();
        float y = draggable.getY();

        Font hIcon  = icon(ICON_FONT_SIZE);
        Font hTitle = text(TEXT_FONT_SIZE);
        Font rName  = text(TEXT_FONT_SIZE);
        Font rLevel = text(LEVEL_FONT_SIZE);
        Font rTime  = text(TEXT_FONT_SIZE);

        Collection<StatusEffectInstance> effects = PreviewState.isActive()
                ? PreviewState.getEffects()
                : (mc != null && mc.player != null
                        ? mc.player.getStatusEffects()
                        : java.util.List.<StatusEffectInstance>of());

        Set<StatusEffect> active = new HashSet<>();
        for (StatusEffectInstance e : effects) {
            StatusEffect type = e.getEffectType().value();
            active.add(type);
            getAnimation(type).update(1);
            updateSnapshot(e);
        }
        for (Map.Entry<StatusEffect, AnimationUtils> entry : animations.entrySet()) {
            if (!active.contains(entry.getKey())) entry.getValue().update(0);
        }

        List<StatusEffect> renderOrder = buildRenderOrder(effects, active);

        float headerGroupW = hIcon.getWidth(HEADER_GLYPH) + ICON_GAP + hTitle.getWidth(TITLE);
        float width = Math.max(MIN_WIDTH, PAD_X + headerGroupW + PAD_X);
        float bodyH = 0f;
        int visible = 0;

        for (StatusEffect type : renderOrder) {
            float a = getAnimation(type).getValue();
            if (a <= 0.01f) continue;
            PotionSnapshot s = snapshots.get(type);
            if (s == null) continue;
            visible++;

            String baseName = s.baseName != null ? s.baseName : I18n.translate(type.getTranslationKey());
            String time = formatDuration(s.duration, s.infinite);

            float nameW = rName.getWidth(baseName);
            if (s.amplifier > 1) nameW += rLevel.getWidth(" " + toRoman(s.amplifier));
            float timeW = rTime.getWidth(time);
            float rowW = PAD_X + ROW_SPRITE_SIZE + ICON_GAP + nameW + 6f + timeW + PAD_X;
            if (rowW > width) width = rowW;
            bodyH += ROW_H * a;
        }

        widthAnimation.update(width);
        width = widthAnimation.getValue();

        float height = HEADER_H + (visible > 0 ? BODY_PAD_TOP + bodyH + BODY_PAD_BOT : 0f);

        float scaleMul = 1f;
        if (Interface.INSTANCE != null) {
            scaleMul = Interface.INSTANCE.potions1Scale.get() / 100f;
            if (scaleMul < 0.5f) scaleMul = 0.5f;
            if (scaleMul > 2.0f) scaleMul = 2.0f;
        }

        int bodyBg = bodyBg();
        int headerBg = headerBg();
        int headerAlpha = (headerBg >>> 24) & 0xFF;
        int bodyAlpha   = (bodyBg   >>> 24) & 0xFF;
        boolean shadow  = Interface.INSTANCE != null && Interface.INSTANCE.potions1Shadow.isState();
        boolean outline = Interface.INSTANCE != null && Interface.INSTANCE.potions1Outline.isState();
        float panelH = (visible == 0) ? HEADER_H : height;

        m.push();
        m.translate(x, y, 0);
        m.scale(scaleMul, scaleMul, 1f);
        m.translate(-x, -y, 0);

        float scaledWidth = width * scaleMul;
        float scaledPanelH = panelH * scaleMul;

        if (isBlurEnabled()) {
            // blur отключён — вызывает краш
        }
        if (visible == 0) {
            if (headerAlpha > 0) RenderUtils.drawRoundedRect(m, x, y, width, HEADER_H, RADIUS, headerBg);
        } else {
            float bodyY = y + HEADER_H;
            float bodyHeight = height - HEADER_H;
            if (headerAlpha > 0) {
                ScissorUtils.push();
                ScissorUtils.setFromComponentCoordinates(x, y, scaledWidth, HEADER_H * scaleMul);
                RenderUtils.drawRoundedRect(m, x, y, width, HEADER_H + 1f, RADIUS, RADIUS, 0f, 0f, headerBg);
                ScissorUtils.pop();
                ScissorUtils.unset();
            }
            if (bodyAlpha > 0) {
                ScissorUtils.push();
                ScissorUtils.setFromComponentCoordinates(x, bodyY, scaledWidth, bodyHeight * scaleMul);
                RenderUtils.drawRoundedRect(m, x, bodyY - 1f, width, bodyHeight + 1f, 0f, 0f, RADIUS, RADIUS, bodyBg);
                ScissorUtils.pop();
                ScissorUtils.unset();
            }
        }
        if (outline) {
            int outlineColor = ColorUtils.rgba(0, 0, 0, 220);
            RenderUtils.drawRoundedRectOutline(m, x, y, width, panelH, RADIUS, RADIUS, RADIUS, RADIUS, 0.6f, outlineColor);
        }
        if (shadow) {
            float strength = Interface.INSTANCE != null ? Interface.INSTANCE.potions1ShadowStrength.get() : 70f;
            int sa = (int) (255f * strength / 100f);
            if (sa < 0) sa = 0; if (sa > 255) sa = 255;
            RenderUtils.drawShadowOuter(m, x, y, width, panelH, RADIUS, 10f, ColorUtils.rgba(0, 0, 0, sa));
        }

        int accent = accentColor();
        float gx = x + (width - headerGroupW) / 2f;
        hIcon.drawStringWithShadow(m, HEADER_GLYPH, gx, y + ICON_BASE_Y, accent);
        hTitle.drawStringWithShadow(m, TITLE,
                gx + hIcon.getWidth(HEADER_GLYPH) + ICON_GAP, y + TEXT_BASE_Y, TEXT_WHITE);

        if (visible > 0) {
            ScissorUtils.push();
            ScissorUtils.setFromComponentCoordinates(x, y, scaledWidth, height * scaleMul);

            float rowY = y + HEADER_H + BODY_PAD_TOP;
            for (StatusEffect type : renderOrder) {
                float a = getAnimation(type).getValue();
                if (a <= 0.01f) continue;
                PotionSnapshot s = snapshots.get(type);
                if (s == null) continue;

                int alpha = (int) (255 * a);
                int rowTextColor = ColorUtils.setAlphaColor(TEXT_WHITE, alpha);
                int accentColor  = ColorUtils.setAlphaColor(accent, alpha);

                float spriteY = rowY + (ROW_H - ROW_SPRITE_SIZE) / 2f;
                if (s.entry != null && mc != null && mc.getStatusEffectSpriteManager() != null) {
                    Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(s.entry);
                    if (sprite != null) {
                        RenderUtils.drawSprite(m, sprite, x + PAD_X, spriteY,
                                ROW_SPRITE_SIZE, ColorUtils.rgba(255, 255, 255, alpha));
                    }
                }

                float textX = x + PAD_X + ROW_SPRITE_SIZE + ICON_GAP;
                float textBaseY = rowY + TEXT_BASE_Y;
                String baseName = s.baseName != null ? s.baseName : I18n.translate(type.getTranslationKey());
                rName.drawStringWithShadow(m, baseName, textX, textBaseY, rowTextColor);
                if (s.amplifier > 1) {
                    float nameW = rName.getWidth(baseName);
                    rLevel.drawStringWithShadow(m, " " + toRoman(s.amplifier),
                            textX + nameW, textBaseY, accentColor);
                }

                String time = formatDuration(s.duration, s.infinite);
                float timeX = x + width - PAD_X - rTime.getWidth(time);
                rTime.drawStringWithShadow(m, time, timeX, textBaseY, accentColor);

                rowY += ROW_H * a;
            }
            ScissorUtils.pop();
            ScissorUtils.unset();
        }

        animations.entrySet().removeIf(entry ->
                !active.contains(entry.getKey()) && entry.getValue().getValue() <= 0.01f);
        snapshots.keySet().removeIf(type -> !animations.containsKey(type));

        m.pop();

        draggable.setWidth(scaledWidth);
        draggable.setHeight(height * scaleMul);
    }
}
