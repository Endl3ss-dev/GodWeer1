package antileak.base.client.modules.impl.render.base.implement;

import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.entry.RegistryEntry;
import antileak.base.Punch;
import antileak.base.api.events.implement.EventRender;
import antileak.base.api.utils.animation.AnimationUtils;
import antileak.base.api.utils.animation.Easings;
import antileak.base.api.utils.color.ColorUtils;
import antileak.base.api.utils.draggable.Draggable;
import antileak.base.api.utils.render.RenderUtils;
import antileak.base.api.utils.render.fonts.msdf.Font;
import antileak.base.api.utils.render.fonts.msdf.Fonts;
import antileak.base.client.modules.impl.render.base.InterfaceProcessing;

import java.util.*;

public class Potions extends InterfaceProcessing {

    private static final float PANEL_H    = 18f;
    private static final float ICON_SZ    = 12f;
    private static final float ICON_PAD   = 2.5f;
    private static final float TIME_BOX_W = 24f;
    private static final float SPACING    = 2f;
    private static final int EXPIRING_TICKS = 5 * 20;

    private static final RegistryEntry<StatusEffect>[] NEGATIVE_EFFECTS = new RegistryEntry[] {
            net.minecraft.entity.effect.StatusEffects.POISON,
            net.minecraft.entity.effect.StatusEffects.WITHER,
            net.minecraft.entity.effect.StatusEffects.NAUSEA,
            net.minecraft.entity.effect.StatusEffects.BLINDNESS,
            net.minecraft.entity.effect.StatusEffects.HUNGER,
            net.minecraft.entity.effect.StatusEffects.SLOWNESS,
            net.minecraft.entity.effect.StatusEffects.MINING_FATIGUE,
            net.minecraft.entity.effect.StatusEffects.INSTANT_DAMAGE,
            net.minecraft.entity.effect.StatusEffects.WEAKNESS,
            net.minecraft.entity.effect.StatusEffects.LEVITATION,
            net.minecraft.entity.effect.StatusEffects.UNLUCK,
            net.minecraft.entity.effect.StatusEffects.BAD_OMEN
    };

    private static final class PotionData {
        RegistryEntry<StatusEffect> entry;
        String baseName;
        int amplifier, duration;
        boolean infinite;
    }

    private Font bold(int s) { return Fonts.getFont("suisse_bold", s); }
    private Font reg(int s)  { return Fonts.getFont("suisse", s); }

    private final Map<StatusEffect, AnimationUtils> animations  = new LinkedHashMap<>();
    private final Map<StatusEffect, PotionData>     snapshots   = new HashMap<>();
    private final Map<StatusEffect, Integer>        maxDurations = new HashMap<>();
    private final Set<StatusEffect>                 renderOrderSeen = new HashSet<>();

    public Potions(Draggable draggable) { super(draggable); }

    private AnimationUtils getAnim(StatusEffect e) {
        return animations.computeIfAbsent(e, k -> new AnimationUtils(0, 10f, Easings.QUAD_OUT));
    }

    private static String getLevelSuffix(int lvl) {
        int n = Math.max(1, lvl);
        return switch (n) {
            case 1 -> "I";  case 2 -> "II";  case 3 -> "III"; case 4 -> "IV";
            case 5 -> "V";  case 6 -> "VI";  case 7 -> "VII"; case 8 -> "VIII";
            case 9 -> "IX"; case 10 -> "X";
            default -> "X".repeat(n / 10) + getLevelSuffix(n % 10 == 0 ? 10 : n % 10);
        };
    }

    private static String formatDuration(StatusEffectInstance e) {
        return formatDuration(e.getDuration(), e.isInfinite());
    }

    private static String formatDuration(int dur, boolean inf) {
        if (inf) return "**:**";
        int sec = Math.max(0, dur / 20);
        int mins = sec / 60;
        return mins > 60 ? "**:**" : mins + ":" + String.format("%02d", sec % 60);
    }

    private boolean isBad(RegistryEntry<StatusEffect> e) {
        for (var n : NEGATIVE_EFFECTS) if (e == n) return true;
        return false;
    }

    @Override
    public void onRender(EventRender.Default event) {
        renderStyle(event);
        super.onRender(event);
    }

    public void renderStyle(EventRender.Default event) {
        float x = draggable.getX(), y = draggable.getY();
        int colorTheme;
        if (!Punch.INSTANCE.themeStorage.getThemes().getTheme().getName().equals("Rainbow")) {
            colorTheme = Punch.INSTANCE.themeStorage.getThemes().getTheme().color[0];
        } else {
            colorTheme = ColorUtils.getThemeColor();
        }

        Collection<StatusEffectInstance> effects = mc != null && mc.player != null
                ? mc.player.getStatusEffects() : List.of();

        Set<StatusEffect> active = new HashSet<>();
        for (StatusEffectInstance e : effects) {
            StatusEffect type = e.getEffectType().value();
            active.add(type);
            getAnim(type).update(1);
            PotionData d = snapshots.computeIfAbsent(type, k -> new PotionData());
            d.entry     = e.getEffectType();
            d.baseName  = I18n.translate(e.getTranslationKey());
            d.amplifier = e.getAmplifier() + 1;
            d.duration  = e.getDuration();
            d.infinite  = e.isInfinite();
            Integer pm = maxDurations.get(type);
            if (pm == null || e.getDuration() > pm) maxDurations.put(type, e.getDuration());
        }
        for (var e : animations.entrySet())
            if (!active.contains(e.getKey())) e.getValue().update(0);

        List<StatusEffect> renderOrder = new ArrayList<>();
        renderOrderSeen.clear();
        for (StatusEffectInstance e : effects) {
            StatusEffect t = e.getEffectType().value();
            if (renderOrderSeen.add(t)) renderOrder.add(t);
        }
        for (StatusEffect t : animations.keySet())
            if (!active.contains(t)) renderOrder.add(t);

        float curY = y;
        float maxW = 0;
        int visible = 0;
        Font nameFont = bold(13);
        if (nameFont == null) nameFont = reg(13);
        Font lvlFont  = reg(11);
        if (lvlFont == null) lvlFont = reg(12);
        Font timeFont = reg(11);
        for (StatusEffect type : renderOrder) {
            float anim = getAnim(type).getValue();
            PotionData d = snapshots.get(type);
            if (anim <= 0.01f || d == null) continue;

            String name = d.baseName != null ? d.baseName : I18n.translate(type.getTranslationKey());
            String lvl  = getLevelSuffix(d.amplifier);
            String fullName = lvl.isEmpty() ? name : name + " " + lvl;
            float nameW = nameFont.getWidth(fullName);
            float minNameW = 50f;
            float namePanelW = Math.max(minNameW, 8f + nameW + 8f);
            float totalW = ICON_PAD + ICON_SZ + 2.5f + namePanelW + 2.5f + TIME_BOX_W + 3f;
            if (totalW > maxW) maxW = totalW;
            visible++;
        }

        if (visible == 0) {
            draggable.setWidth(80);
            draggable.setHeight(23);
            return;
        }

        StatusEffect lastVisible = null;
        for (StatusEffect type : renderOrder) {
            float anim = getAnim(type).getValue();
            if (anim > 0.01f && snapshots.get(type) != null) {
                lastVisible = type;
            }
        }

        for (StatusEffect type : renderOrder) {
            float anim = getAnim(type).getValue();
            PotionData d = snapshots.get(type);
            if (anim <= 0.01f || d == null) continue;

            String name = d.baseName != null ? d.baseName : I18n.translate(type.getTranslationKey());
            String lvl  = getLevelSuffix(d.amplifier);
            String fullName = lvl.isEmpty() ? name : name + " " + lvl;
            float nameW = nameFont.getWidth(fullName);
            float minNameW = 50f;
            // Использовать максимальную ширину для всех зелий, чтобы выровнять блок!
            float totalW = maxW;
            
            // Но мы всё ещё вычисляем ширину текущей панели для внутреннего выравнивания текста
            float namePanelW = Math.max(minNameW, 8f + nameFont.getWidth(fullName) + 8f);

            boolean bad  = d.entry != null && isBad(d.entry);
            boolean exp  = !d.infinite && d.duration > 0 && d.duration <= EXPIRING_TICKS;
            int alpha    = (int)(255 * anim);

            int nameColor;
            if (exp) nameColor = ColorUtils.rgba(255, 50, 50, alpha);
            else if (bad) nameColor = ColorUtils.rgba(255, 85, 75, alpha);
            else nameColor = ColorUtils.rgba(255, 255, 255, alpha);
            int lvlColor = ColorUtils.rgba(180, 180, 180, alpha);
            int timeColor = ColorUtils.rgba(200, 200, 200, alpha);

            String time = formatDuration(d.duration, d.infinite);

            float cx = x + totalW * 0.5f;
            float cy = curY + PANEL_H * 0.5f;

            var ms = event.getContext().getMatrices();
            ms.push();
            float scale = 0.85f + 0.15f * anim;
            ms.translate(cx, cy, 0);
            ms.scale(scale, scale, 1);
            ms.translate(-cx, -cy, 0);

            int shadowAlpha = (int)(80 * anim);
            int bgAlpha = (int)(230 * anim);
            RenderUtils.drawShadow(ms, x, curY, totalW, PANEL_H, 6f, 12f, ColorUtils.setAlphaColor(colorTheme, shadowAlpha));
            RenderUtils.drawRoundedRect(ms, x, curY, totalW, PANEL_H, 6f, ColorUtils.rgba(20, 20, 20, bgAlpha));
            
            if (type == lastVisible) {
                RenderUtils.drawRoundedRect(ms, x + totalW / 2f - 17f, curY + PANEL_H - 2f, 24f, 2f, 1f, ColorUtils.setAlphaColor(colorTheme, alpha));
            }

            float iconX = x + ICON_PAD;
            float iconY = curY + (PANEL_H - ICON_SZ) * 0.5f;

            int subBg = ColorUtils.setAlphaColor(ColorUtils.rgba(0, 0, 0, 255), 60);
            RenderUtils.drawRoundedRect(ms, iconX, iconY, ICON_SZ, ICON_SZ, 2.5f, 2.5f, 3.5f, 3.5f, subBg);
            if (d.entry != null) {
                Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(d.entry);
                RenderUtils.drawSprite(ms, sprite, iconX + 2f, iconY + 2f, 8, ColorUtils.rgba(255, 255, 255, alpha));
            }

            float namePanelX = iconX + ICON_SZ + 2.5f;
            float namePanelH = 13f;
            float namePanelY = curY + (PANEL_H - namePanelH) * 0.5f;
            
            // Растянем namePanelW чтобы заполнить свободное пространство до конца, так как ширина теперь единая!
            float availableNameW = totalW - (ICON_PAD + ICON_SZ + 2.5f + 2.5f + TIME_BOX_W + 3f);
            RenderUtils.drawRoundedRect(ms, namePanelX, namePanelY, availableNameW, namePanelH, 3f, subBg);

            float textY = namePanelY + namePanelH * 0.5f - 1f;
            
            float fullTextW = nameFont.getWidth(fullName);
            float textX = namePanelX + (availableNameW - fullTextW) * 0.5f;
            
            nameFont.draw(ms, name, textX, textY, nameColor);
            if (!lvl.isEmpty()) {
                float lvlX = textX + nameFont.getWidth(name + " ");
                nameFont.draw(ms, lvl, lvlX, textY, lvlColor);
            }

            float timeBoxH = 13f;
            float timeBoxX = x + totalW - TIME_BOX_W - 3f;
            float timeBoxY = curY + (PANEL_H - timeBoxH) * 0.5f;
            RenderUtils.drawRoundedRect(ms, timeBoxX, timeBoxY, TIME_BOX_W, timeBoxH, 3.5f, 3.5f, 2.5f, 2.5f, subBg);

            float tW = timeFont.getWidth(time);
            float tY = timeBoxY + timeBoxH * 0.5f;
            timeFont.draw(ms, time, timeBoxX + (TIME_BOX_W - tW) * 0.5f, tY, timeColor);

            ms.pop();

            curY += (PANEL_H + SPACING) * anim;
        }

        animations.entrySet().removeIf(e -> !active.contains(e.getKey()) && e.getValue().getValue() <= 0.01f);
        snapshots.keySet().removeIf(t -> !animations.containsKey(t));
        maxDurations.keySet().removeIf(t -> !animations.containsKey(t));

        draggable.setWidth(maxW);
        draggable.setHeight(Math.max(23, curY - y));
    }
}