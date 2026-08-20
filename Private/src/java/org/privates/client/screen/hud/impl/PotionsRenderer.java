package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.texture.PotionSpriteUploader;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.StringUtils;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PotionsRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private static final String POINT = " • ";
    private static final int DOT_COLOR = new Color(180, 180, 200, 255).getRGB();
    private static final List<Effect> NEGATIVE_EFFECT_LIST = List.of(Effects.SLOWNESS, Effects.MINING_FATIGUE, Effects.NAUSEA, Effects.BLINDNESS, Effects.HUNGER, Effects.WEAKNESS, Effects.POISON, Effects.WITHER, Effects.LEVITATION, Effects.UNLUCK);

    private final DragSetting drag;
    private final Map<Effect, Long> lastSeenPotions = new HashMap<>();
    private final Map<Effect, Long> endedEffects = new HashMap<>();
    private final List<EffectInstance> currentEffects = new ArrayList<>();
    private final List<EndedPotion> endedPotions = new ArrayList<>();

    private float animatedWidth, animatedHeight;
    @Getter
    @Setter
    private float scaleV, blurStrength = 10f, outlineWidth = 1.0f;
    @Getter
    @Setter
    private boolean blurEnabled = true, enableOutline = false;

    public float scale = 0.9f, titleDarkness = 0.0f, dynamicRectGap = 3.0f, newRectGap = 3.0f, iconRectGap = 2.0f;
    public boolean showTitleIcon = true, showPotionIcon = true, showCount = true, showTitle = true, dynamicRects = false, newRect = false, newRender = false, separateIconRect = false;

    private float lastX, lastY, smoothVelocityX, smoothVelocityY, currentRotation, targetRotation, springOffsetX, springOffsetY, dragScaleEffect = 1.0f;
    private boolean isDragging;
    private long lastMoveTime;

    private static class EndedPotion {
        Effect effect;
        int amplifier;
        long endTime;

        EndedPotion(Effect e, int a, long et) {
            effect = e;
            amplifier = a;
            endTime = et;
        }
    }

    public PotionsRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER, () -> scale, v -> scale = v, 0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Title Darkness", DragComponent.SettingType.SLIDER, () -> titleDarkness, v -> titleDarkness = v, 0.0f, 1.0f, 0.05f),
                new DragComponent.HudSetting("Title Icon", DragComponent.SettingType.BOOLEAN, () -> showTitleIcon ? 1f : 0f, v -> showTitleIcon = v > 0.5f),
                new DragComponent.HudSetting("Potion Icon", DragComponent.SettingType.BOOLEAN, () -> showPotionIcon ? 1f : 0f, v -> showPotionIcon = v > 0.5f),
                new DragComponent.HudSetting("Show Count", DragComponent.SettingType.BOOLEAN, () -> showCount ? 1f : 0f, v -> showCount = v > 0.5f),
                new DragComponent.HudSetting("Show Title", DragComponent.SettingType.BOOLEAN, () -> showTitle ? 1f : 0f, v -> showTitle = v > 0.5f),
                new DragComponent.HudSetting("Dynamic Rects", DragComponent.SettingType.BOOLEAN, () -> dynamicRects ? 1f : 0f, v -> dynamicRects = v > 0.5f),
                new DragComponent.HudSetting("Dynamic Rect Gap", DragComponent.SettingType.SLIDER, () -> dynamicRectGap, v -> dynamicRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("Separate Icon Rect", DragComponent.SettingType.BOOLEAN, () -> separateIconRect ? 1f : 0f, v -> separateIconRect = v > 0.5f),
                new DragComponent.HudSetting("Icon Rect Gap", DragComponent.SettingType.SLIDER, () -> iconRectGap, v -> iconRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("New Rect", DragComponent.SettingType.BOOLEAN, () -> newRect ? 1f : 0f, v -> newRect = v > 0.5f),
                new DragComponent.HudSetting("New Rect Gap", DragComponent.SettingType.SLIDER, () -> newRectGap, v -> newRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("New Render", DragComponent.SettingType.BOOLEAN, () -> newRender ? 1f : 0f, v -> newRender = v > 0.5f),
                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN, () -> enableOutline ? 1f : 0f, v -> enableOutline = v > 0.5f),
                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER, () -> outlineWidth, v -> outlineWidth = v, 0.5f, 3.0f, 0.1f)
        );
    }

    @Override
    public void render(Render2DEvent event) {
        render(event, false);
    }

    private void addPreviewPotions() {
        currentEffects.clear();
        endedPotions.clear();
        currentEffects.add(new EffectInstance(Effects.SPEED, 120 * 20, 1));
        currentEffects.add(new EffectInstance(Effects.STRENGTH, 90 * 20, 0));
        currentEffects.add(new EffectInstance(Effects.REGENERATION, 60 * 20, 0));
        currentEffects.add(new EffectInstance(Effects.FIRE_RESISTANCE, 180 * 20, 0));
        currentEffects.add(new EffectInstance(Effects.SLOWNESS, 30 * 20, 0));
    }

    private float smoothLerp(float c, float t, float s) {
        return c + (t - c) * (1.0f - (float) Math.pow(1.0f - s, 1.5f));
    }

    private float elasticLerp(float c, float t, float s, float e) {
        float d = t - c, v = d * s;
        if (Math.abs(d) < 0.5f) v += d * e * 0.3f;
        return c + v;
    }

    private void updateDragAnimation(float x, float y, float w, float h) {
        float ivx = x - lastX, ivy = y - lastY;
        boolean moving = Math.abs(ivx) > 0.01f || Math.abs(ivy) > 0.01f;
        if (moving) {
            lastMoveTime = System.currentTimeMillis();
            isDragging = true;
        } else if (System.currentTimeMillis() - lastMoveTime > 50) isDragging = false;
        smoothVelocityX = smoothLerp(smoothVelocityX, ivx, isDragging ? 0.25f : 0.08f);
        smoothVelocityY = smoothLerp(smoothVelocityY, ivy, isDragging ? 0.25f : 0.08f);
        targetRotation = Math.max(-15f, Math.min(15f, smoothVelocityX * 1.8f - smoothVelocityY * 0.3f));
        currentRotation = elasticLerp(currentRotation, targetRotation, isDragging ? 0.18f : 0.06f, 0.15f);
        if (!isDragging && Math.abs(currentRotation) < 0.1f) currentRotation *= 0.9f;
        springOffsetX = Math.max(-5f, Math.min(5f, springOffsetX * 0.85f + (-smoothVelocityX * 0.3f - springOffsetX) * 0.12f));
        springOffsetY = Math.max(-5f, Math.min(5f, springOffsetY * 0.85f + (-smoothVelocityY * 0.2f - springOffsetY) * 0.12f));
        dragScaleEffect = smoothLerp(dragScaleEffect, isDragging ? 1.02f : 1.0f, 0.15f);
        lastX = x;
        lastY = y;
    }

    private void applyDragTransform(MatrixStack m, float x, float y, float w, float h) {
        m.translate(springOffsetX, springOffsetY, 0);
        if (Math.abs(currentRotation) > 0.01f) {
            float px = x + w / 2f, py = y + h * 0.15f;
            m.translate(px, py, 0);
            m.rotate(net.minecraft.util.math.vector.Vector3f.ZP.rotationDegrees(currentRotation));
            m.translate(-px, -py, 0);
        }
    }

    public void render(Render2DEvent event, boolean glow) {
        boolean isPrev = mc.currentScreen instanceof ChatScreen;
        Collection<EffectInstance> active = mc.player != null ? mc.player.getActivePotionEffects() : Collections.emptyList();
        boolean showPrev = isPrev && active.isEmpty();
        if (showPrev) {
            addPreviewPotions();
            endedPotions.clear();
        } else {
            updateEffectsList(active);
            long ct = System.currentTimeMillis();
            endedPotions.clear();
            for (Map.Entry<Effect, Long> e : endedEffects.entrySet()) {
                if (ct - e.getValue() <= 3000) {
                    EffectInstance orig = currentEffects.stream().filter(ef -> ef.getPotion() == e.getKey()).findFirst().orElse(null);
                    int amp = orig != null ? orig.getAmplifier() : 0;
                    endedPotions.add(new EndedPotion(e.getKey(), amp, e.getValue()));
                }
            }
        }

        if (newRender) {
            renderNewStyle(event, isPrev);
            return;
        }

        long ct = System.currentTimeMillis();
        List<Object> all = new ArrayList<>();
        all.addAll(currentEffects);
        all.addAll(endedPotions);

        // Sort by width (widest first) for dynamic rects, otherwise alphabetically
        if (dynamicRects) {
            all.sort(Comparator.comparingDouble(o -> {
                String n = getPotionName(o), d = getDurationText(o, ct);
                float piw = showPotionIcon ? (10f * scale + 6f * scale) : 0f;
                float pw = font.getWidth(POINT, 7f * scale);
                float powid = 6f * scale + piw + (showPotionIcon ? pw : 0) + font.getWidth(n, 7f * scale) + pw + font.getWidth(d, 7f * scale) + 6f * scale;
                return -powid; // Negative for descending order (widest first)
            }));
        } else {
            all.sort(Comparator.comparing(o -> {
                if (o instanceof EffectInstance) return ((EffectInstance) o).getPotion().getDisplayName().getString();
                else return ((EndedPotion) o).effect.getDisplayName().getString();
            }));
        }

        float sc = scale;
        MatrixStack m = event.getMatrix();
        float x = drag.position.x, y = drag.position.y;
        float p = 6f * sc, fs = 7f * sc, r = 6f * sc, tbh = showTitle ? 18f * sc : 0f, irh = 18f * sc, is = 10f * sc;
        float gai = 6f * sc, sw = 0.25f * sc, gas = 6f * sc, gbd = 15f * sc;
        float tis = 10f * sc, tiw = showTitleIcon ? (tis + 6f * sc) : 0f, ttw = font.getWidth("Potions", fs), titg = 7.5f * sc;
        float cbw = showCount ? (font.getWidth(String.valueOf(currentEffects.size() + endedPotions.size()), 6f * sc) + 8f * sc) : 0f;
        float tw = p + tiw + ttw + (showCount && (dynamicRects || newRect) ? (titg + cbw) : 0f) + p;
        float mw = tw;
        float pw = font.getWidth(POINT, fs);

        if (newRect) {
            float miw = 0f;
            for (Object o : all) {
                String n = getPotionName(o), d = getDurationText(o, ct);
                float piw = showPotionIcon ? (is + gai) : 0f, psw = showPotionIcon ? (sw + gas) : 0f;
                float powid = p + piw + (showPotionIcon ? pw : 0) + font.getWidth(n, fs) + pw + font.getWidth(d, fs) + p;
                miw = Math.max(miw, powid);
            }
            mw = Math.max(tw, miw);
        } else if (dynamicRects) {
            // For dynamic rects, make title width match the widest effect (first in sorted list)
            if (!all.isEmpty()) {
                Object widest = all.get(0);
                String n = getPotionName(widest), d = getDurationText(widest, ct);

                float powid;
                if (separateIconRect && showPotionIcon) {
                    // Width calculation for separate icon rect mode
                    float iconRectSize = is + p * 2;
                    float textRectWidth = p + font.getWidth(n, fs) + pw + font.getWidth(d, fs) + p;
                    powid = iconRectSize + iconRectGap * sc + textRectWidth;
                } else {
                    // Original width calculation
                    float piw = showPotionIcon ? (is + gai) : 0f;
                    powid = p + piw + (showPotionIcon ? pw : 0) + font.getWidth(n, fs) + pw + font.getWidth(d, fs) + p;
                }

                tw = powid;
                mw = powid;
            }
        } else if (!dynamicRects) {
            for (Object o : all) {
                float a = isPrev ? 1.0f : getAnimationValue(o);
                if (a <= 0.01f) continue;
                String n = getPotionName(o), d = getDurationText(o, ct);
                float piw = showPotionIcon ? (is + gai) : 0f, psw = showPotionIcon ? (sw + gas) : 0f;
                float powid = p + piw + (showPotionIcon ? pw : 0) + font.getWidth(n, fs) + pw + font.getWidth(d, fs) + p;
                mw = Math.max(mw, powid);
            }
        }

        float th = tbh;
        if (!all.isEmpty()) {
            for (Object o : all) {
                float a = isPrev ? 1.0f : getAnimationValue(o);
                if (a <= 0.01f) continue;
                if (dynamicRects) th += (irh + dynamicRectGap * sc) * a;
                else if (newRect) th += (irh + newRectGap * sc) * a;
                else th += irh * a;
            }
        }

        animatedWidth = Mathf.fast(animatedWidth, mw, 14);
        animatedHeight = Mathf.fast(animatedHeight, th, 14);

        if (x == 0 && y == 0) {
            float sw2 = mc.getMainWindow().getScaledWidth(), nx = sw2 - (animatedWidth > 0 ? animatedWidth : 120 * sc) - 4 * sc, ny = 4 * sc;
            drag.position.set(nx, ny);
            x = nx;
            y = ny;
            lastX = x;
            lastY = y;
        }

        updateDragAnimation(x, y, animatedWidth, animatedHeight);
        scaleV = Mathf.fast(scaleV, (all.stream().anyMatch(o -> isPrev || getAnimationValue(o) > 0.01) || isPrev) ? 1 : 0, 16);
        if (Mathf.round(scaleV, .1d) <= 0) return;

        boolean ar = x > mc.getMainWindow().getScaledWidth() / 2f;

        m.push();
        m.translate(x + animatedWidth / 2F, y + animatedHeight / 2f, 0);
        m.scale(scaleV * dragScaleEffect, scaleV * dragScaleEffect, scaleV);
        m.translate(-(x + animatedWidth / 2F), -(y + animatedHeight / 2f), 0);
        applyDragTransform(m, x, y, animatedWidth, animatedHeight);

        if (!dynamicRects && !newRect)
            RenderUtil.CustomStyled.draw(m, x, y, animatedWidth, animatedHeight, Round.of(r), blurEnabled, blurStrength);

        float cy = y;
        if (showTitle) {
            if (dynamicRects || newRect) {
                float tx = ar ? x + (animatedWidth - tw) : x;
                RenderUtil.CustomStyled.draw(m, tx, cy, tw, tbh, Round.of(r), blurEnabled, blurStrength);
                ScissorUtil.enableScissor(tx, cy, tw, tbh);
                RenderUtil.Rounded.smooth(m, tx, cy, tw, tbh, ColorUtil.replAlpha(0xFF000000, titleDarkness), Round.of(0, r, 0, r));
                ScissorUtil.disableScissor();
                int tc = currentEffects.size() + endedPotions.size();
                renderTitle(m, tx, cy, tw, tbh, "Potions", fs, tis, sc, tc, titg, true);
                if (enableOutline) {
                    int thm = Theme.getInstance().clientColor(), oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), thm, 0.5f);
                    float hw = outlineWidth * sc / 2f;
                    RenderUtil.Rounded.roundedOutline(m, tx - hw, cy - hw, tw + outlineWidth * sc, tbh + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
                }
            } else {
                ScissorUtil.enableScissor(x, cy, animatedWidth, tbh);
                RenderUtil.Rounded.smooth(m, x, cy, animatedWidth, tbh, ColorUtil.replAlpha(0xFF000000, titleDarkness), Round.of(0, r, 0, r));
                ScissorUtil.disableScissor();
                int tc = currentEffects.size() + endedPotions.size();
                renderTitle(m, x, cy, animatedWidth, tbh, "Potions", fs, tis, sc, tc, titg, false);
            }
        }

        cy += tbh + (dynamicRects ? dynamicRectGap * sc : (newRect ? newRectGap * sc : 0));

        for (Object o : all) {
            float a = isPrev ? 1.0f : getAnimationValue(o);
            if (a <= 0.01f) continue;
            float ih = irh * a;
            if (dynamicRects) {
                String n = getPotionName(o), d = getDurationText(o, ct);
                float piw = showPotionIcon ? (is + gai) : 0f, psw = showPotionIcon ? (sw + gas) : 0f;

                float powid;
                if (separateIconRect && showPotionIcon) {
                    // Width calculation for separate icon rect mode
                    float iconRectSize = is + p * 2;
                    float textRectWidth = p + font.getWidth(n, fs) + pw + font.getWidth(d, fs) + p;
                    powid = iconRectSize + iconRectGap * sc + textRectWidth;
                } else {
                    // Original width calculation
                    powid = p + piw + (showPotionIcon ? pw : 0) + font.getWidth(n, fs) + pw + font.getWidth(d, fs) + p;
                }

                float px = ar ? x + (animatedWidth - powid) : x;
                renderPotDyn(m, px, cy, powid, ih, o, fs, is, a, sc, ct, gai, sw, gas, gbd, pw, r, ar);
                cy += ih + dynamicRectGap * sc;
            } else if (newRect) {
                renderPotNew(m, x, cy, animatedWidth, ih, o, fs, is, a, sc, ct, gai, sw, gas, gbd, pw, r, ar);
                cy += ih + newRectGap * sc;
            } else {
                renderPot(m, x, cy, animatedWidth, ih, o, fs, is, a, sc, ct, gai, sw, gas, gbd, pw);
                cy += ih;
            }
        }

        if (enableOutline && !dynamicRects && !newRect) {
            int thm = Theme.getInstance().clientColor(), oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), thm, 0.5f);
            float hw = outlineWidth * sc / 2f;
            RenderUtil.Rounded.roundedOutline(m, x - hw, y - hw, animatedWidth + outlineWidth * sc, animatedHeight + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
        }

        drag.size.set(animatedWidth, animatedHeight);
        m.pop();
    }

    private void renderNewStyle(Render2DEvent event, boolean isPrev) {
        List<Object> all = new ArrayList<>();
        all.addAll(currentEffects);
        all.addAll(endedPotions);
        all.sort(Comparator.comparing(o -> {
            if (o instanceof EffectInstance) return ((EffectInstance) o).getPotion().getDisplayName().getString();
            else return ((EndedPotion) o).effect.getDisplayName().getString();
        }));

        float sc = scale;
        MatrixStack m = event.getMatrix();
        float x = drag.position.x, y = drag.position.y;
        float p = 3f * sc, is = 18f * sc, ih = is + p * 2, ig = 2f * sc, r = 6f * sc, fs = 6f * sc, tg = 5f * sc;

        float miw = 0f;
        for (Object o : all) {
            String en = getEffect(o).getDisplayName().getString();
            if (o instanceof EffectInstance) {
                int amp = ((EffectInstance) o).getAmplifier();
                if (amp > 0) en += " " + (amp + 1);
            } else if (o instanceof EndedPotion) {
                int amp = ((EndedPotion) o).amplifier;
                if (amp > 0) en += " " + (amp + 1);
            }
            String dt = "";
            if (o instanceof EffectInstance ins) {
                if (ins.getIsPotionDurationMax()) dt = "**:**";
                else {
                    int ts = ins.getDuration() / 20;
                    dt = ts >= 60 ? String.format("%d:%02d", ts / 60, ts % 60) : ts + "s";
                }
            } else dt = "ENDED";
            float nw = font.getWidth(en, fs), dw = font.getWidth(dt, fs), twid = Math.max(nw, dw);
            float totw = p + is + tg + twid + p;
            miw = Math.max(miw, totw);
        }

        float tw = miw, th = 0f;
        for (Object o : all) {
            float a = isPrev ? 1.0f : getAnimationValue(o);
            if (a <= 0.01f) continue;
            th += ih * a;
            if (all.indexOf(o) < all.size() - 1) th += ig * a;
        }

        animatedWidth = Mathf.fast(animatedWidth, tw, 14);
        animatedHeight = Mathf.fast(animatedHeight, th, 14);

        if (x == 0 && y == 0) {
            float sw = mc.getMainWindow().getScaledWidth(), nx = sw - animatedWidth - 4 * sc, ny = 4 * sc;
            drag.position.set(nx, ny);
            x = nx;
            y = ny;
            lastX = x;
            lastY = y;
        }

        updateDragAnimation(x, y, animatedWidth, animatedHeight);
        scaleV = Mathf.fast(scaleV, (all.stream().anyMatch(o -> isPrev || getAnimationValue(o) > 0.01) || isPrev) ? 1 : 0, 16);
        if (Mathf.round(scaleV, .1d) <= 0) return;

        m.push();
        m.translate(x + animatedWidth / 2F, y + animatedHeight / 2f, 0);
        m.scale(scaleV * dragScaleEffect, scaleV * dragScaleEffect, scaleV);
        m.translate(-(x + animatedWidth / 2F), -(y + animatedHeight / 2f), 0);
        applyDragTransform(m, x, y, animatedWidth, animatedHeight);

        float cy = y;
        long ct = System.currentTimeMillis();

        for (int i = 0; i < all.size(); i++) {
            Object o = all.get(i);
            float a = isPrev ? 1.0f : getAnimationValue(o);
            if (a <= 0.01f) continue;
            float sa = ease3(a), dh = ih * a;

            String en = getEffect(o).getDisplayName().getString();
            if (o instanceof EffectInstance) {
                int amp = ((EffectInstance) o).getAmplifier();
                if (amp > 0) en += " " + (amp + 1);
            } else if (o instanceof EndedPotion) {
                int amp = ((EndedPotion) o).amplifier;
                if (amp > 0) en += " " + (amp + 1);
            }
            String dt = "";
            if (o instanceof EffectInstance ins) {
                if (ins.getIsPotionDurationMax()) dt = "**:**";
                else {
                    int ts = ins.getDuration() / 20;
                    dt = ts >= 60 ? String.format("%d:%02d", ts / 60, ts % 60) : ts + "s";
                }
            } else dt = "ENDED";

            float nw = font.getWidth(en, fs), dw = font.getWidth(dt, fs), twid = Math.max(nw, dw);
            float iw = p + is + tg + twid + p;

            RenderUtil.CustomStyled.draw(m, x, cy, iw, dh, Round.of(r), blurEnabled, blurStrength);
            if (enableOutline) {
                int thm = Theme.getInstance().clientColor(), oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), thm, 0.5f);
                float hw = outlineWidth * sc / 2f;
                RenderUtil.Rounded.roundedOutline(m, x - hw, cy - hw, iw + outlineWidth * sc, dh + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
            }

            m.push();
            m.translate(x, cy + dh / 2f, 0);
            m.scale(1f, sa, 1f);
            m.translate(-x, -(cy + dh / 2f), 0);

            Effect eff = getEffect(o);
            PotionSpriteUploader upl = mc.getPotionSpriteUploader();
            TextureAtlasSprite spr = upl.getSprite(eff);
            mc.getTextureManager().bindTexture(spr.getAtlasTexture().getTextureLocation());
            float ix = x + p, iy = cy + (ih - is) / 2f;
            AbstractGui.blit(m, (int) ix, (int) iy, 0, (int) is, (int) is, spr);

            float tx = ix + is + tg, tth = fs * 2 + sc, tsy = cy + (ih - tth) / 2f;
            int nc = getEffectTextColor(o, 255);
            font.draw(m, en, tx, tsy, nc, fs);
            float dy = tsy + fs + sc;
            int dc = getDurationColor(o, 180, ct);
            font.draw(m, dt, tx, dy, dc, fs);

            m.pop();

            cy += dh;
            if (i < all.size() - 1) cy += ig * a;
        }

        drag.size.set(animatedWidth, animatedHeight);
        m.pop();
    }

    private void renderTitle(MatrixStack m, float x, float y, float w, float h, String t, float fs, float is, float sc, int cnt, float gap, boolean dyn) {
        float p = 6f * sc, oy = 0.25f * sc;
        int hud = Theme.getInstance().clientColor();
        if (dyn) {
            // Align with effect layout: icon at p, text after icon+gap+point, count at right
            float cx = x + p;
            if (showTitleIcon) {
                Fonts.HUD.draw(m, "d", cx, y + (h - is) / 2f - oy, hud, is);
                cx += is + 6f * sc; // Same gap as effects (gai)
            }

            // Add point spacing if potion icons are shown (to align with effect text)
            if (showPotionIcon) {
                cx += font.getWidth(POINT, fs);
            }

            font.draw(m, t, cx, y + (h - fs) / 2f - oy, -1, fs);

            // Count aligned with duration text position
            if (showCount) {
                float cfs = 6f * sc;
                String ct = String.valueOf(cnt);
                float cw = font.getWidth(ct, cfs);
                float crx = x + w - cw - p;
                float cry = y + (h - cfs) / 2f;
                font.draw(m, ct, crx, cry - 0.45f + sc, hud, cfs);
            }
        } else {
            float ox = 2f * sc, cx = x + p - ox;
            if (showTitleIcon) {
                Fonts.HUD.draw(m, "d", cx, y + (h - is) / 2f - oy, hud, is);
                cx += is + 6f * sc;
            }
            font.draw(m, t, cx + 7.5f * sc, y + (h - fs) / 2f - oy, -1, fs);
            if (showCount) {
                float cfs = 6f * sc;
                String ct = String.valueOf(cnt);
                float cw = font.getWidth(ct, cfs), ch = cfs, crx = x + w - p - cw - 4f * sc, cry = y + (h - ch) / 2f, crw = cw + 8f * sc, crh = ch + 4f * sc, crr = 3f * sc;
                font.draw(m, ct, crx + 1.5f * sc, cry - 1 + sc, hud, cfs);
            }
        }
    }

    private void renderPotDyn(MatrixStack m, float x, float y, float w, float h, Object o, float fs, float is, float a, float sc, long ct, float gai, float sw, float gas, float gbd, float pw, float r, boolean ar) {
        float p = 6f * sc;
        String pn = getPotionName(o), dur = getDurationText(o, ct);
        float sa = ease3(a), ta = ease4(a);

        if (separateIconRect && showPotionIcon) {
            // Render icon in separate rect
            float iconRectSize = is + p * 2;
            float iconX = x;
            float textX = x + iconRectSize + iconRectGap * sc;
            float textW = w - iconRectSize - iconRectGap * sc;

            // Icon rect
            RenderUtil.CustomStyled.draw(m, iconX, y, iconRectSize, h, Round.of(r), blurEnabled, blurStrength);
            if (enableOutline) {
                int thm = Theme.getInstance().clientColor(), oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), thm, 0.5f);
                float hw = outlineWidth * sc / 2f;
                RenderUtil.Rounded.roundedOutline(m, iconX - hw, y - hw, iconRectSize + outlineWidth * sc, h + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
            }

            m.push();
            m.translate(ar ? (sa - 1) * 10f * sc : (1 - sa) * 10f * sc, 0, 0);
            m.translate(iconX, y + h / 2f, 0);
            m.scale(1f, 0.3f + sa * 0.7f, 1f);
            m.translate(-iconX, -(y + h / 2f), 0);

            float iy = y + (h - is) / 2f;
            Effect eff = getEffect(o);
            PotionSpriteUploader upl = mc.getPotionSpriteUploader();
            TextureAtlasSprite spr = upl.getSprite(eff);
            mc.getTextureManager().bindTexture(spr.getAtlasTexture().getTextureLocation());
            AbstractGui.blit(m, (int) (iconX + p), (int) iy, 0, (int) is, (int) is, spr);

            m.pop();

            // Text rect
            RenderUtil.CustomStyled.draw(m, textX, y, textW, h, Round.of(r), blurEnabled, blurStrength);
            if (enableOutline) {
                int thm = Theme.getInstance().clientColor(), oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), thm, 0.5f);
                float hw = outlineWidth * sc / 2f;
                RenderUtil.Rounded.roundedOutline(m, textX - hw, y - hw, textW + outlineWidth * sc, h + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
            }

            m.push();
            m.translate(ar ? (sa - 1) * 10f * sc : (1 - sa) * 10f * sc, 0, 0);
            m.translate(textX, y + h / 2f, 0);
            m.scale(1f, 0.3f + sa * 0.7f, 1f);
            m.translate(-textX, -(y + h / 2f), 0);

            float ty = y + (h - fs) / 2f, dw = font.getWidth(dur, fs), dx = textX + textW - dw - p;
            int nc = getEffectTextColor(o, (int) (235 * ta));
            font.draw(m, pn, textX + p, ty, nc, fs);

            int dc = getDurationColor(o, (int) (255 * ta), ct);
            font.draw(m, dur, dx, ty, dc, fs);

            m.pop();
        } else {
            // Original rendering (icon and text in same rect)
            RenderUtil.CustomStyled.draw(m, x, y, w, h, Round.of(r), blurEnabled, blurStrength);
            if (enableOutline) {
                int thm = Theme.getInstance().clientColor(), oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), thm, 0.5f);
                float hw = outlineWidth * sc / 2f;
                RenderUtil.Rounded.roundedOutline(m, x - hw, y - hw, w + outlineWidth * sc, h + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
            }

            m.push();
            m.translate(ar ? (sa - 1) * 10f * sc : (1 - sa) * 10f * sc, 0, 0);
            m.translate(x, y + h / 2f, 0);
            m.scale(1f, 0.3f + sa * 0.7f, 1f);
            m.translate(-x, -(y + h / 2f), 0);

            float cx = x + p;
            if (showPotionIcon) {
                float iy = y + (h - is) / 2f;
                Effect eff = getEffect(o);
                PotionSpriteUploader upl = mc.getPotionSpriteUploader();
                TextureAtlasSprite spr = upl.getSprite(eff);
                mc.getTextureManager().bindTexture(spr.getAtlasTexture().getTextureLocation());
                AbstractGui.blit(m, (int) cx, (int) iy, 0, (int) is, (int) is, spr);
                cx += is + gai;
                font.draw(m, POINT, cx - 2.5f, y + (h - fs) / 2f, ColorUtil.getColor(DOT_COLOR >> 16 & 0xFF, DOT_COLOR >> 8 & 0xFF, DOT_COLOR & 0xFF, (int) (((DOT_COLOR >> 24 & 0xFF) * ta))), fs);
                cx += pw;
            }

            float ty = y + (h - fs) / 2f, dw = font.getWidth(dur, fs), dx = x + w - dw - p;
            int nc = getEffectTextColor(o, (int) (235 * ta));
            font.draw(m, pn, cx, ty, nc, fs);

            int dc = getDurationColor(o, (int) (255 * ta), ct);
            font.draw(m, dur, dx, ty, dc, fs);

            m.pop();
        }
    }

    private void renderPotNew(MatrixStack m, float x, float y, float w, float h, Object o, float fs, float is, float a, float sc, long ct, float gai, float sw, float gas, float gbd, float pw, float r, boolean ar) {
        float p = 6f * sc;
        String pn = getPotionName(o), dur = getDurationText(o, ct);
        float sa = ease3(a), ta = ease4(a);

        RenderUtil.CustomStyled.draw(m, x, y, w, h, Round.of(r), blurEnabled, blurStrength);
        if (enableOutline) {
            int thm = Theme.getInstance().clientColor(), oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), thm, 0.5f);
            float hw = outlineWidth * sc / 2f;
            RenderUtil.Rounded.roundedOutline(m, x - hw, y - hw, w + outlineWidth * sc, h + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
        }

        m.push();
        m.translate((1 - sa) * 10f * sc, 0, 0);
        m.translate(x, y + h / 2f, 0);
        m.scale(1f, 0.3f + sa * 0.7f, 1f);
        m.translate(-x, -(y + h / 2f), 0);

        float cx = x + p;
        if (showPotionIcon) {
            float iy = y + (h - is) / 2f;
            Effect eff = getEffect(o);
            PotionSpriteUploader upl = mc.getPotionSpriteUploader();
            TextureAtlasSprite spr = upl.getSprite(eff);
            mc.getTextureManager().bindTexture(spr.getAtlasTexture().getTextureLocation());
            AbstractGui.blit(m, (int) cx, (int) iy, 0, (int) is, (int) is, spr);
            cx += is + gai;
            font.draw(m, POINT, cx - 2.5f, y + (h - fs) / 2f, ColorUtil.getColor(DOT_COLOR >> 16 & 0xFF, DOT_COLOR >> 8 & 0xFF, DOT_COLOR & 0xFF, (int) (((DOT_COLOR >> 24 & 0xFF) * ta))), fs);
            cx += pw;
        }

        float ty = y + (h - fs) / 2f, dw = font.getWidth(dur, fs), dx = x + w - dw - p, nmw = dx - cx - gbd;
        int nc = getEffectTextColor(o, (int) (235 * ta));
        ScissorUtil.enableScissor(cx, y, nmw + 4, h);
        font.draw(m, pn, cx, ty, nc, fs);
        ScissorUtil.disableScissor();
        cx += font.getWidth(pn, fs);

        int dc = getDurationColor(o, (int) (255 * ta), ct);
        font.draw(m, dur, dx, ty, dc, fs);

        m.pop();
    }

    private void renderPot(MatrixStack m, float x, float y, float w, float h, Object o, float fs, float is, float a, float sc, long ct, float gai, float sw, float gas, float gbd, float pw) {
        float p = 6f * sc;
        String pn = getPotionName(o), dur = getDurationText(o, ct);
        float sa = ease3(a), ta = ease4(a);

        m.push();
        m.translate((1 - sa) * 10f * sc, 0, 0);
        m.translate(x, y + h / 2f, 0);
        m.scale(1f, 0.3f + sa * 0.7f, 1f);
        m.translate(-x, -(y + h / 2f), 0);

        float cx = x + p;
        if (showPotionIcon) {
            float iy = y + (h - is) / 2f;
            Effect eff = getEffect(o);
            PotionSpriteUploader upl = mc.getPotionSpriteUploader();
            TextureAtlasSprite spr = upl.getSprite(eff);
            mc.getTextureManager().bindTexture(spr.getAtlasTexture().getTextureLocation());
            AbstractGui.blit(m, (int) cx, (int) iy, 0, (int) is, (int) is, spr);
            cx += is + gai;
            font.draw(m, POINT, cx - 2.5f, y + (h - fs) / 2f, ColorUtil.getColor(DOT_COLOR >> 16 & 0xFF, DOT_COLOR >> 8 & 0xFF, DOT_COLOR & 0xFF, (int) (((DOT_COLOR >> 24 & 0xFF) * ta))), fs);
            cx += pw;
        }

        float ty = y + (h - fs) / 2f, dw = font.getWidth(dur, fs), dx = x + w - dw - p, nmw = dx - cx - gbd;
        int nc = getEffectTextColor(o, (int) (235 * ta));
        ScissorUtil.enableScissor(cx, y, nmw + 4, h);
        font.draw(m, pn, cx, ty, nc, fs);
        ScissorUtil.disableScissor();
        cx += font.getWidth(pn, fs);

        int dc = getDurationColor(o, (int) (255 * ta), ct);
        font.draw(m, dur, dx, ty, dc, fs);

        m.pop();
    }

    private float ease3(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private float ease4(float t) {
        return 1 - (float) Math.pow(1 - t, 4);
    }

    private int getEffectTextColor(Object o, int a) {
        Effect e = getEffect(o);
        if (NEGATIVE_EFFECT_LIST.contains(e)) return ColorUtil.getColor(255, 85, 85, a);
        return ColorUtil.getColor(255, 255, 255, a);
    }

    private int getDurationColor(Object o, int a, long ct) {
        if (o instanceof EndedPotion ep) {
            long el = ct - ep.endTime;
            float fa = 1f;
            if (el < 300) fa = el / 300f;
            else if (el > 2700) fa = (3000f - el) / 300f;
            int tc = Theme.getInstance().clientColor();
            return ColorUtil.getColor((tc >> 16) & 0xFF, (tc >> 8) & 0xFF, tc & 0xFF, (int) (a * fa));
        }
        EffectInstance p = (EffectInstance) o;
        int d = p.getDuration();
        if (d / 20 <= 5) return ColorUtil.getColor(255, 85, 85, a);
        int tc = Theme.getInstance().clientColor();
        return ColorUtil.getColor((tc >> 16) & 0xFF, (tc >> 8) & 0xFF, tc & 0xFF, a);
    }

    private float getAnimationValue(Object o) {
        if (o instanceof EffectInstance) return (float) ((EffectInstance) o).getAnimation().getValue();
        return 1f;
    }

    private Effect getEffect(Object o) {
        if (o instanceof EffectInstance) return ((EffectInstance) o).getPotion();
        else if (o instanceof EndedPotion) return ((EndedPotion) o).effect;
        return Effects.LUCK;
    }

    private String getPotionName(Object o) {
        if (o instanceof EffectInstance p)
            return p.getPotion().getDisplayName().getString() + " " + (p.getAmplifier() + 1);
        else if (o instanceof EndedPotion e) return e.effect.getDisplayName().getString() + " " + (e.amplifier + 1);
        return "";
    }

    private String getDurationText(Object o, long ct) {
        if (o instanceof EndedPotion) return "ENDED";
        EffectInstance p = (EffectInstance) o;
        if (p.getIsPotionDurationMax()) return "**:**";
        return StringUtils.ticksToElapsedTime(p.getDuration());
    }

    private void updateEffectsList(Collection<EffectInstance> active) {
        long ct = System.currentTimeMillis();
        Set<Effect> curr = new HashSet<>();
        for (EffectInstance e : active) {
            Effect pe = e.getPotion();
            if (pe == Effects.UNLUCK) continue;
            curr.add(pe);
            lastSeenPotions.put(pe, ct);
            if (!endedEffects.containsKey(pe)) endedEffects.remove(pe);
            e.getAnimation().update();
            e.getAnimation().run((e.getDuration() / 20F) <= 1F ? 0 : 1, 0.5F, Easings.EXPO_OUT, false);
        }
        currentEffects.clear();
        for (EffectInstance e : active) if (e.getPotion() != Effects.UNLUCK) currentEffects.add(e);
        Set<Effect> seen = new HashSet<>(lastSeenPotions.keySet());
        for (Effect s : seen) if (!curr.contains(s) && !endedEffects.containsKey(s)) endedEffects.put(s, ct);
        currentEffects.sort(Comparator.comparingInt(e -> e.getPotion().getDisplayName().getString().length()));
    }
}