package tech.lightclient.ui.hud.star.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Identifier;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.modules.api.Function;
import tech.lightclient.ui.hud.NotificationsOverlay;
import tech.lightclient.ui.hud.star.HudListElement;
import tech.lightclient.ui.hud.star.PopupMenu;
import tech.lightclient.ui.hud.star.SAnim;
import tech.lightclient.ui.hud.star.SEasing;
import tech.lightclient.ui.hud.star.StarUtil;

public class EffectsElement extends HudListElement {
    private static final String SETTING_ALWAYS = "always";
    private static final String SETTING_ALERT = "alert";

    /** Цвет таймера/свечения для положительных эффектов. */
    private static final int POSITIVE_COLOR = 0xFF34D26B;
    /** Цвет таймера/свечения для негативных эффектов. */
    private static final int NEGATIVE_COLOR = 0xFFE5484D;

    private final Map<String, Entry> effects = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<StatusEffect, Boolean> ended = new HashMap<>();

    private static final class Entry {
        StatusEffectInstance instance;
        // Начальное значение 0 — у каждого эффекта своя анимация появления
        // (0 -> 1), а не только общий scale-in панели. Иначе новые эффекты,
        // добавленные пока панель уже видна (например после того как быстрый
        // эффект истёк), возникали бы мгновенно, без анимации.
        final SAnim anim = new SAnim(300L, 0.0f, SEasing.BAKEK);
        int maxDuration;
    }

    public EffectsElement(Function module) {
        super(module, "Star Эффекты", "icons/hud/potion.png", 10.0f, 60.0f);
    }

    @Override
    protected void update(EventRender.Screen.Hud event) {
        width = MIN_WIDTH;
        height = ROWS_TOP;

        List<StatusEffectInstance> original = mc.player == null ? List.of() : new ArrayList<>(mc.player.getStatusEffects());

        boolean alertOn = boolSetting(scope(), SETTING_ALERT, true);

        for (Map.Entry<String, Entry> existing : effects.entrySet()) {
            Entry entry = existing.getValue();
            if (entry.instance == null) {
                continue;
            }
            StatusEffect potion = potionOf(entry.instance);
            boolean contained = containsInstance(original, entry.instance);
            if (alertOn && !contained && !ended.getOrDefault(potion, false)
                    && potion.getCategory() != StatusEffectCategory.HARMFUL && mc.player != null
                    && !hasEffect(original, entry.instance)) {
                NotificationsOverlay.push("Эффект " + potion.getName().getString() + " закончился", 2400,
                        NotificationsOverlay.Kind.INFO);
                ended.put(potion, true);
            } else if (contained) {
                ended.put(potion, false);
            }
        }

        for (StatusEffectInstance instance : original) {
            String key = keyOf(instance);
            Entry entry = effects.computeIfAbsent(key, k -> new Entry());
            entry.instance = instance;
        }

        for (Entry entry : effects.values()) {
            boolean contained = containsInstance(original, entry.instance);
            entry.anim.update(contained ? 1.0f : 0.0f);
            float nameWidth = FontRegistry.DELTA_HUD.getWidth(baseName(entry.instance), 7.0f);
            float lvlWidth = levelLabel(entry.instance).isEmpty()
                    ? 0.0f : FontRegistry.DELTA_HUD.getWidth(" " + levelLabel(entry.instance), 7.0f);
            width = Math.max(width, nameWidth + lvlWidth + 60.0f);
            height += (ROW_HEIGHT + ROW_GAP) * entry.anim.getValue();
        }

        // Keep the element alive while an effect is animating out. This is
        // important when the last potion expires: the original HUD keeps the
        // panel visible until its row animation finishes.
        boolean hasAnimatedRows = effects.values().stream()
                .anyMatch(entry -> entry.anim.getValue() > 0.001f);
        setSelfVisible(mc.player != null && mc.world != null
                && (hasAnimatedRows || mc.currentScreen instanceof ChatScreen
                || boolSetting(scope(), SETTING_ALWAYS, false)));
    }

    private static boolean hasEffect(List<StatusEffectInstance> list, StatusEffectInstance probe) {
        StatusEffect effect = potionOf(probe);
        for (StatusEffectInstance candidate : list) {
            if (potionOf(candidate) == effect) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean show() {
        return mc.player != null && mc.world != null
                && (!mc.player.getStatusEffects().isEmpty() || mc.currentScreen instanceof ChatScreen || boolSetting(scope(), SETTING_ALWAYS, false));
    }

    @Override
    protected void drawRows(EventRender.Screen.Hud event, float alpha) {
        Renderer2D r = event.getRenderer();

        List<Entry> ordered = new ArrayList<>(effects.values());
        float offset = ROWS_TOP;
        for (Entry entry : ordered) {
            float anim = entry.anim.getValue();
            if (anim <= 0.02f) {
                continue;
            }
            float off = -4.5f + 4.5f * anim;
            float rowY = y + offset + off;

            // Каждый эффект — собственный прямоугольник тех же габаритов, что и шапка.
            drawRow(r, rowY, ROW_HEIGHT, alpha * anim);
            r.pushRoundedClipRect((int) x, (int) rowY, (int) Math.ceil(width), (int) Math.ceil(ROW_HEIGHT),
                    rounding(), rounding(), rounding(), rounding());
            try {
                Identifier texture = InGameHud.getEffectTexture(entry.instance.getEffectType());
                Sprite sprite = null;
                if (texture != null) {
                    SpriteAtlasTexture atlas = mc.getAtlasManager().getAtlasTexture(Identifier.ofVanilla("gui"));
                    sprite = atlas == null ? null : atlas.getSprite(texture);
                }
                if (sprite != null) {
                    int tint = ClientColors.applyAlpha(0xFFFFFFFF, anim * alpha);
                    r.drawTextureRegion(sprite.getAtlasId(),
                            x + 7.0f, rowY + middle(8.0f), 8.0f, 8.0f,
                            sprite.getMinU(), sprite.getMinV(), sprite.getMaxU(), sprite.getMaxV(), tint);
                }

                // Стиль нового HUD: имя — белым (у вредных эффектов — красным),
                // уровень — акцентным цветом клиента, таймер — круглый индикатор проекта.
                boolean harmful = potionOf(entry.instance).getCategory() == StatusEffectCategory.HARMFUL;
                int nameColor = ClientColors.applyAlpha(harmful ? 0xFFFF3A3A : 0xFFFFFFFF, anim * alpha);
                String name = baseName(entry.instance);
                r.text(FontRegistry.DELTA_HUD, x + 20.0f, baselineMedium(rowY, ROW_HEIGHT, 7.0f), 7.0f, name,
                        nameColor);
                float cursor = x + 20.0f + FontRegistry.DELTA_HUD.getWidth(name, 7.0f);
                String lvl = levelLabel(entry.instance);
                if (!lvl.isEmpty()) {
                    r.text(FontRegistry.DELTA_HUD, cursor, baselineMedium(rowY, ROW_HEIGHT, 7.0f), 7.0f, " " + lvl,
                            accent(anim * alpha));
                }

                drawTime(event, r, entry, rowY, anim, alpha);
            } finally {
                r.popClipRect();
            }

            offset += ROW_HEIGHT * anim + ROW_GAP * anim;
        }

        effects.values().removeIf(entry -> entry.anim.getValue() <= 0.001f && !containsInstance(
                mc.player == null ? List.of() : new ArrayList<>(mc.player.getStatusEffects()), entry.instance));
    }

    private void drawTime(EventRender.Screen.Hud event, Renderer2D r, Entry entry, float rowY, float anim, float alpha) {
        // Таймер в стиле уведомлений динамического острова: тающий по кругу
        // индикатор со свечением под ним. Расположен на том же уровне (правый
        // край строки), где раньше был текстовый таймер.
        float indicatorAlpha = alpha * anim;
        int color = effectColor(entry.instance);
        float radius = 3.5f;
        float cx = x + width - 7.0f - radius;
        float cy = rowY + ROW_HEIGHT * 0.5f;

        drawGlow(r, cx, cy, color, indicatorAlpha);

        if (!entry.instance.isInfinite() && entry.instance.getDuration() < 999999999) {
            int duration = entry.instance.getDuration();
            if (duration > entry.maxDuration) {
                entry.maxDuration = duration;
            }
            float progress = entry.maxDuration > 0 ? (float) duration / (float) entry.maxDuration : 1.0f;
            progress = Math.max(0.0f, Math.min(1.0f, progress));
            r.circleOutline(cx, cy, radius, -90.0f, progress,
                    ClientColors.applyAlpha(color, indicatorAlpha), 1.0f);
        } else {
            r.circleOutline(cx, cy, radius, -90.0f, 1.0f,
                    ClientColors.applyAlpha(color, indicatorAlpha), 1.0f);
        }
    }

    private int effectColor(StatusEffectInstance instance) {
        return potionOf(instance).getCategory() == StatusEffectCategory.HARMFUL ? NEGATIVE_COLOR : POSITIVE_COLOR;
    }

    private void drawGlow(Renderer2D r, float cx, float cy, int color, float alpha) {
        drawIconGlow(r, cx, cy, 6.5f, alpha, color);
    }

    @Override
    public void fillSettingsPopup(PopupMenu popup) {
        popup.checkbox("Показывать всегда", () -> boolSetting(scope(), SETTING_ALWAYS, false),
                value -> setBoolSetting(scope(), SETTING_ALWAYS, value));
        popup.checkbox("Оповещать об окончании", () -> boolSetting(scope(), SETTING_ALERT, true),
                value -> setBoolSetting(scope(), SETTING_ALERT, value));
    }

    @Override
    public String settingName() {
        return "Эффекты";
    }

    @Override
    protected String title() {
        return "Активные Эффекты";
    }

    private static String keyOf(StatusEffectInstance instance) {
        return potionOf(instance).getName().getString();
    }

    private static StatusEffect potionOf(StatusEffectInstance instance) {
        return instance.getEffectType().value();
    }

    private static boolean containsInstance(List<StatusEffectInstance> list, StatusEffectInstance instance) {
        for (StatusEffectInstance candidate : list) {
            if (candidate == instance) {
                return true;
            }
        }
        return false;
    }

    private static String baseName(StatusEffectInstance instance) {
        return potionOf(instance).getName().getString();
    }

    /** Метка уровня в стиле нового HUD: "2 lvl". Пустая для эффектов первого уровня. */
    private static String levelLabel(StatusEffectInstance instance) {
        return instance.getAmplifier() > 0 ? (instance.getAmplifier() + 1) + " lvl" : "";
    }

    private static float middle(float boxSize) {
        return (ROW_HEIGHT - boxSize) * 0.5f;
    }
}
