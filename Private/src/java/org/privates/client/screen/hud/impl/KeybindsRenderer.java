package org.privates.client.screen.hud.impl;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KeybindsRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private static final String POINT = " • ";
    private static final int DOT_COLOR = new Color(180, 180, 200, 255).getRGB();

    private final List<Module> bindings = Lists.newArrayList();
    private final DragSetting drag;
    private float animatedWidth, animatedHeight;

    @Getter
    @Setter
    private float scaleV, blurStrength = 10f, outlineWidth = 1.0f;
    @Getter
    @Setter
    private float glowOutlineRadius = 8f, glowOutlineAlpha = 0.6f;
    @Getter
    @Setter
    private float titleIconShadowRadius = 8f, titleIconShadowAlpha = 0.5f;
    @Getter
    @Setter
    private float moduleIconShadowRadius = 6f, moduleIconShadowAlpha = 0.4f;
    @Getter
    @Setter
    private float keyShadowRadius = 6f, keyShadowAlpha = 0.4f;
    @Getter
    @Setter
    private float counterShadowRadius = 6f, counterShadowAlpha = 0.4f;
    @Getter
    @Setter
    private boolean blurEnabled = true, enableOutline = false, glowOutline = false;
    @Getter
    @Setter
    private boolean titleIconShadow = true, moduleIconShadow = true;
    @Getter
    @Setter
    private boolean keyShadow = true, counterShadow = true;
    public float scale = 0.9f, titleDarkness = 0.0f, dynamicRectGap = 3.0f, newRectGap = 3.0f, keyRectGap = 2.0f;
    public boolean showTitleIcon = true, showModuleIcon = true, showCount = true, showTitle = true;
    public boolean dynamicRects = false, newRect = false, showKeyRect = true, separateKeyRect = false;
    public boolean showAllBinds = false, showColumnRect = true;

    private float lastX, lastY, smoothVelocityX, smoothVelocityY;
    private float currentRotation, targetRotation, springOffsetX, springOffsetY;
    private float dragScaleEffect = 1.0f;
    private boolean isDragging;
    private long lastMoveTime;

    public enum LayoutMode {DEFAULT, RIGHT_LEFT}

    private final LayoutMode layoutMode = LayoutMode.DEFAULT;

    public KeybindsRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return Arrays.asList(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER, () -> scale, v -> scale = v, 0.5f, 2.0f, 0.1f),
                new DragComponent.HudSetting("Title Darkness", DragComponent.SettingType.SLIDER, () -> titleDarkness, v -> titleDarkness = v, 0.0f, 1.0f, 0.05f),
                new DragComponent.HudSetting("Title Icon", DragComponent.SettingType.BOOLEAN, () -> showTitleIcon ? 1f : 0f, v -> showTitleIcon = v > 0.5f),
                new DragComponent.HudSetting("Module Icon", DragComponent.SettingType.BOOLEAN, () -> showModuleIcon ? 1f : 0f, v -> showModuleIcon = v > 0.5f),
                new DragComponent.HudSetting("Show Count", DragComponent.SettingType.BOOLEAN, () -> showCount ? 1f : 0f, v -> showCount = v > 0.5f),
                new DragComponent.HudSetting("Show Title", DragComponent.SettingType.BOOLEAN, () -> showTitle ? 1f : 0f, v -> showTitle = v > 0.5f),
                new DragComponent.HudSetting("Dynamic Rects", DragComponent.SettingType.BOOLEAN, () -> dynamicRects ? 1f : 0f, v -> dynamicRects = v > 0.5f),
                new DragComponent.HudSetting("Dynamic Rect Gap", DragComponent.SettingType.SLIDER, () -> dynamicRectGap, v -> dynamicRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("Separate Key Rect", DragComponent.SettingType.BOOLEAN, () -> separateKeyRect ? 1f : 0f, v -> separateKeyRect = v > 0.5f),
                new DragComponent.HudSetting("Key Rect Gap", DragComponent.SettingType.SLIDER, () -> keyRectGap, v -> keyRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("New Rect", DragComponent.SettingType.BOOLEAN, () -> newRect ? 1f : 0f, v -> newRect = v > 0.5f),
                new DragComponent.HudSetting("New Rect Gap", DragComponent.SettingType.SLIDER, () -> newRectGap, v -> newRectGap = v, 0f, 10f, 0.5f),
                new DragComponent.HudSetting("Show Key Rect", DragComponent.SettingType.BOOLEAN, () -> showKeyRect ? 1f : 0f, v -> showKeyRect = v > 0.5f),
                new DragComponent.HudSetting("Show All Binds", DragComponent.SettingType.BOOLEAN, () -> showAllBinds ? 1f : 0f, v -> showAllBinds = v > 0.5f),
                new DragComponent.HudSetting("Show Column Rect", DragComponent.SettingType.BOOLEAN, () -> showColumnRect ? 1f : 0f, v -> showColumnRect = v > 0.5f),
                new DragComponent.HudSetting("Enable Outline", DragComponent.SettingType.BOOLEAN, () -> enableOutline ? 1f : 0f, v -> enableOutline = v > 0.5f),
                new DragComponent.HudSetting("Outline Width", DragComponent.SettingType.SLIDER, () -> outlineWidth, v -> outlineWidth = v, 0.5f, 3.0f, 0.1f),
                new DragComponent.HudSetting("Glow Outline", DragComponent.SettingType.BOOLEAN, () -> glowOutline ? 1f : 0f, v -> glowOutline = v > 0.5f),
                new DragComponent.HudSetting("Glow Outline Radius", DragComponent.SettingType.SLIDER, () -> glowOutlineRadius, v -> glowOutlineRadius = v, 2f, 20f, 1f),
                new DragComponent.HudSetting("Glow Outline Alpha", DragComponent.SettingType.SLIDER, () -> glowOutlineAlpha, v -> glowOutlineAlpha = v, 0f, 1f, 0.05f),
                new DragComponent.HudSetting("Title Icon Shadow", DragComponent.SettingType.BOOLEAN, () -> titleIconShadow ? 1f : 0f, v -> titleIconShadow = v > 0.5f),
                new DragComponent.HudSetting("Title Icon Shadow Radius", DragComponent.SettingType.SLIDER, () -> titleIconShadowRadius, v -> titleIconShadowRadius = v, 2f, 20f, 1f),
                new DragComponent.HudSetting("Title Icon Shadow Alpha", DragComponent.SettingType.SLIDER, () -> titleIconShadowAlpha, v -> titleIconShadowAlpha = v, 0f, 1f, 0.05f),
                new DragComponent.HudSetting("Module Icon Shadow", DragComponent.SettingType.BOOLEAN, () -> moduleIconShadow ? 1f : 0f, v -> moduleIconShadow = v > 0.5f),
                new DragComponent.HudSetting("Module Icon Shadow Radius", DragComponent.SettingType.SLIDER, () -> moduleIconShadowRadius, v -> moduleIconShadowRadius = v, 2f, 20f, 1f),
                new DragComponent.HudSetting("Module Icon Shadow Alpha", DragComponent.SettingType.SLIDER, () -> moduleIconShadowAlpha, v -> moduleIconShadowAlpha = v, 0f, 1f, 0.05f),
                new DragComponent.HudSetting("Key Shadow", DragComponent.SettingType.BOOLEAN, () -> keyShadow ? 1f : 0f, v -> keyShadow = v > 0.5f),
                new DragComponent.HudSetting("Key Shadow Radius", DragComponent.SettingType.SLIDER, () -> keyShadowRadius, v -> keyShadowRadius = v, 2f, 20f, 1f),
                new DragComponent.HudSetting("Key Shadow Alpha", DragComponent.SettingType.SLIDER, () -> keyShadowAlpha, v -> keyShadowAlpha = v, 0f, 1f, 0.05f),
                new DragComponent.HudSetting("Counter Shadow", DragComponent.SettingType.BOOLEAN, () -> counterShadow ? 1f : 0f, v -> counterShadow = v > 0.5f),
                new DragComponent.HudSetting("Counter Shadow Radius", DragComponent.SettingType.SLIDER, () -> counterShadowRadius, v -> counterShadowRadius = v, 2f, 20f, 1f),
                new DragComponent.HudSetting("Counter Shadow Alpha", DragComponent.SettingType.SLIDER, () -> counterShadowAlpha, v -> counterShadowAlpha = v, 0f, 1f, 0.05f)
        );
    }

    @Override
    public void render(Render2DEvent event) {
        render(event, false);
    }

    public void render(Render2DEvent event, boolean glow) {
        boolean hasActive = Excellent.inst().moduleManager().values().stream()
                .anyMatch(m -> m.isEnabled() && m.getKey() != Keyboard.KEY_NONE.getKey() && m.isAllowDisable());
        boolean isPrev = (mc.currentScreen instanceof ChatScreen) && !hasActive;

        if (isPrev) addPreviewKeybinds();
        else {
            Excellent.inst().moduleManager().values().forEach(m -> m.getAnimation().update());
            updateModulesList();
        }

        RenderContext ctx = new RenderContext(scale, drag.position.x, drag.position.y);
        Dimensions dim = calculateDimensions(ctx, isPrev);

        animatedWidth = Mathf.fast(animatedWidth, dim.width, 14);
        animatedHeight = Mathf.fast(animatedHeight, dim.height, 14);

        if (ctx.x == 0 && ctx.y == 0) {
            drag.position.set(187, 250);
            ctx.x = 187;
            ctx.y = 250;
            lastX = ctx.x;
            lastY = ctx.y;
        }

        updateDragAnimation(ctx.x, ctx.y, animatedWidth, animatedHeight);
        scaleV = Mathf.fast(scaleV, !bindings.isEmpty() ? 1 : 0, 16);
        if (Mathf.round(scaleV, .1d) <= 0) return;

        MatrixStack m = event.getMatrix();
        boolean alignRight = ctx.x > mc.getMainWindow().getScaledWidth() / 2f;

        m.push();
        applyScaleTransform(m, ctx.x, ctx.y, animatedWidth, animatedHeight);
        applyDragTransform(m, ctx.x, ctx.y, animatedWidth, animatedHeight);

        renderBackground(m, ctx, dim, alignRight);
        float currentY = renderTitle(m, ctx, dim, alignRight);
        renderBindings(m, ctx, currentY, dim, isPrev, alignRight);
        renderOutlines(m, ctx, dim, alignRight);

        drag.size.set(animatedWidth, animatedHeight);
        m.pop();
    }

    private static class RenderContext {
        float sc, x, y;
        float p, fs, r, tbh, irh, is, gap, tis, tiw, ttw, titg, cbw, pw;

        RenderContext(float scale, float x, float y) {
            this.sc = scale;
            this.x = x;
            this.y = y;
            this.p = 6f * sc;
            this.fs = 7f * sc;
            this.r = 6f * sc;
            this.irh = 18f * sc;
            this.is = 10f * sc;
            this.gap = 20f * sc;
            this.tis = 10f * sc;
        }

        void updateForTitle(KeybindsRenderer kr, int bindCount) {
            this.tbh = kr.showTitle ? 18f * sc : 0f;
            this.tiw = kr.showTitleIcon ? (tis + 6f * sc) : 0f;
            this.ttw = IRenderer.font.getWidth("Keybinds", fs);
            this.titg = 7.5f * sc;
            this.cbw = kr.showCount ? (IRenderer.font.getWidth(String.valueOf(bindCount), 6f * sc) + 8f * sc) : 0f;
            this.pw = IRenderer.font.getWidth(POINT, fs);
        }
    }

    private static class Dimensions {
        float width, height;

        Dimensions(float w, float h) {
            this.width = w;
            this.height = h;
        }
    }

    private Dimensions calculateDimensions(RenderContext ctx, boolean isPrev) {
        ctx.updateForTitle(this, bindings.size());

        float titleWidth = ctx.p + ctx.tiw + ctx.ttw +
                ((showCount && (dynamicRects || newRect)) ? (ctx.titg + ctx.cbw) : 0f) + ctx.p;
        float maxWidth = titleWidth;

        if (newRect) {
            for (Module mod : bindings) {
                float w = calculateModuleWidth(ctx, mod);
                maxWidth = Math.max(maxWidth, w);
            }
        } else if (dynamicRects) {
            // For dynamic rects, make title width match the widest module (first in sorted list)
            if (!bindings.isEmpty()) {
                Module widest = bindings.get(0);
                float widestWidth = calculateModuleWidth(ctx, widest);
                titleWidth = widestWidth;
                maxWidth = widestWidth;
            }
        } else if (!dynamicRects) {
            for (Module mod : bindings) {
                float a = isPrev ? 1.0f : (float) mod.getAnimation().getValue();
                if (a <= 0.01f && !isPrev) continue;
                float w = calculateModuleWidth(ctx, mod);
                maxWidth = Math.max(maxWidth, w);
            }
        }

        float totalHeight = ctx.tbh;
        for (Module mod : bindings) {
            float a = isPrev ? 1.0f : (float) mod.getAnimation().getValue();
            if (a <= 0.01f && !isPrev) continue;

            float gap = dynamicRects ? (ctx.irh + dynamicRectGap * ctx.sc) * a :
                    newRect ? (ctx.irh + newRectGap * ctx.sc) * a :
                            ctx.irh * a;
            totalHeight += gap;
        }

        return new Dimensions(maxWidth, totalHeight);
    }

    private float calculateModuleWidth(RenderContext ctx, Module mod) {
        String keyText = Keyboard.keyName(mod.getKey());
        float iconWidth = showModuleIcon ? (ctx.is + 6f * ctx.sc) : 0f;
        float pointWidth = showModuleIcon ? ctx.pw : 0;

        if (separateKeyRect && (dynamicRects || newRect)) {
            // Separate key rect mode: key rect + gap + text rect
            float keyRectWidth = ctx.p * 2 + font.getWidth(keyText, ctx.fs);
            float textRectWidth = ctx.p + iconWidth + pointWidth + font.getWidth(mod.getName(), ctx.fs) + ctx.p;
            return keyRectWidth + keyRectGap * ctx.sc + textRectWidth;
        } else {
            // Combined mode: everything in one rect
            return ctx.p + iconWidth + pointWidth + font.getWidth(mod.getName(), ctx.fs) +
                    ctx.gap + font.getWidth(keyText, ctx.fs) + ctx.p;
        }
    }

    private void applyScaleTransform(MatrixStack m, float x, float y, float w, float h) {
        m.translate(x + w / 2f, y + h / 2f, 0);
        m.scale(scaleV * dragScaleEffect, scaleV * dragScaleEffect, scaleV);
        m.translate(-(x + w / 2f), -(y + h / 2f), 0);
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

    private void renderBackground(MatrixStack m, RenderContext ctx, Dimensions dim, boolean alignRight) {
        if (!dynamicRects && !newRect) {
            RenderUtil.CustomStyled.draw(m, ctx.x, ctx.y, animatedWidth, animatedHeight,
                    Round.of(ctx.r), blurEnabled, blurStrength);
        }
    }

    private float renderTitle(MatrixStack m, RenderContext ctx, Dimensions dim, boolean alignRight) {
        if (!showTitle) return ctx.y;

        float titleWidth;
        if (dynamicRects && !bindings.isEmpty()) {
            // For dynamic rects, use the width of the widest module
            titleWidth = calculateModuleWidth(ctx, bindings.get(0));
        } else {
            titleWidth = ctx.p + ctx.tiw + ctx.ttw +
                    ((showCount && (dynamicRects || newRect)) ? (ctx.titg + ctx.cbw) : 0f) + ctx.p;
        }

        if (dynamicRects || newRect) {
            float tx = alignRight ? ctx.x + (animatedWidth - titleWidth) : ctx.x;
            RenderUtil.CustomStyled.draw(m, tx, ctx.y, titleWidth, ctx.tbh,
                    Round.of(ctx.r), blurEnabled, blurStrength);
            ScissorUtil.enableScissor(tx, ctx.y, titleWidth, ctx.tbh);
            RenderUtil.Rounded.smooth(m, tx, ctx.y, titleWidth, ctx.tbh,
                    ColorUtil.replAlpha(0xFF000000, titleDarkness), Round.of(0, ctx.r, 0, ctx.r));
            ScissorUtil.disableScissor();
            renderTitleContent(m, tx, ctx.y, titleWidth, ctx.tbh, ctx, true);
            renderTitleOutline(m, tx, ctx.y, titleWidth, ctx.tbh, ctx.r, ctx.sc);
        } else {
            ScissorUtil.enableScissor(ctx.x, ctx.y, animatedWidth, ctx.tbh);
            RenderUtil.Rounded.smooth(m, ctx.x, ctx.y, animatedWidth, ctx.tbh,
                    ColorUtil.replAlpha(0xFF000000, titleDarkness), Round.of(0, ctx.r, 0, ctx.r));
            ScissorUtil.disableScissor();
            renderTitleContent(m, ctx.x, ctx.y, animatedWidth, ctx.tbh, ctx, false);
        }

        return ctx.y + ctx.tbh + (dynamicRects ? dynamicRectGap * ctx.sc :
                (newRect ? newRectGap * ctx.sc : 0));
    }

    private void renderTitleContent(MatrixStack m, float x, float y, float w, float h,
                                    RenderContext ctx, boolean dyn) {
        float cx = x + ctx.p;
        float oy = 0.25f * ctx.sc;
        int hud = Theme.getInstance().clientColor();

        if (showTitleIcon && titleIconShadow) {
            RenderUtil.Shadow.drawShadow(m, cx, y + (h - ctx.tis) / 2f - oy,
                    Fonts.icon_new.getWidth("f", ctx.tis), Fonts.icon_new.getHeight("f", ctx.tis),
                    titleIconShadowRadius, titleIconShadowAlpha,
                    ColorUtil.multAlpha(hud, titleIconShadowAlpha), Round.of(3));
        }

        if (showTitleIcon) {
            Fonts.icon_new.draw(m, "f", cx, y + (h - ctx.tis) / 2f - oy, hud, ctx.tis);
            cx += ctx.tis + 6f * ctx.sc;
        }

        if (showModuleIcon) {
            cx += ctx.pw;
        }

        font.draw(m, "Keybinds", cx, y + (h - ctx.fs) / 2f - oy, -1, ctx.fs);

        if (showCount) {
            renderCounter(m, cx, y, h, w, ctx, dyn, x);
        }
    }

    private void renderCounter(MatrixStack m, float cx, float y, float h, float w,
                               RenderContext ctx, boolean dyn, float baseX) {
        float cfs = dyn ? 6f * ctx.sc : 7f * ctx.sc;
        String ct = String.valueOf(bindings.size());
        float cw = font.getWidth(ct, cfs);
        float ch = cfs;

        float crx, cry, crw, crh, crr;
        int hud = Theme.getInstance().clientColor();

        if (dyn) {
            // Count aligned with key bind position (right side)
            crx = baseX + w - cw - ctx.p;
            cry = y + (h - ch) / 2f;
            crw = cw + 8f * ctx.sc;
            crh = ch + 4f * ctx.sc;
            crr = 3f * ctx.sc;

            font.draw(m, ct, crx, cry - 0.45f + ctx.sc, hud, cfs);
        } else {
            crx = baseX + w - ctx.p - cw - 4f * ctx.sc;
            cry = y + (h - ch) / 2f;
            crw = cw + 8f * ctx.sc;
            crh = ch + 4f * ctx.sc;
            crr = 3f * ctx.sc;

            font.draw(m, ct, crx + 1.5f * ctx.sc, cry - 1 + ctx.sc, hud, cfs);
        }
    }

    private void renderTitleOutline(MatrixStack m, float x, float y, float w, float h,
                                    float r, float sc) {
        int tc = Theme.getInstance().clientColor();

        if (glowOutline) {
            int gc = ColorUtil.multAlpha(tc, glowOutlineAlpha);
            RenderUtil.Shadow.drawShadow(m, x - glowOutlineRadius / 2f, y - glowOutlineRadius / 2f,
                    w + glowOutlineRadius, h + glowOutlineRadius, glowOutlineRadius,
                    glowOutlineAlpha, gc, Round.of(r));
        } else if (enableOutline) {
            int oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), tc, 0.5f);
            float hw = outlineWidth * sc / 2f;
            RenderUtil.Rounded.roundedOutline(m, x - hw, y - hw, w + outlineWidth * sc,
                    h + outlineWidth * sc, outlineWidth * sc, oc, Round.of(r));
        }
    }

    private void renderBindings(MatrixStack m, RenderContext ctx, float startY,
                                Dimensions dim, boolean isPrev, boolean alignRight) {
        float cy = startY;

        for (Module mod : bindings) {
            float a = isPrev ? 1.0f : (float) mod.getAnimation().getValue();
            if (a <= 0.01f && !isPrev) continue;

            float ih = ctx.irh * a;

            if (dynamicRects) {
                float mw = calculateModuleWidth(ctx, mod);
                float mx = alignRight ? ctx.x + (animatedWidth - mw) : ctx.x;
                renderBindDyn(m, mx, cy, mw, ih, mod, isPrev, ctx, alignRight);
                cy += ih + dynamicRectGap * ctx.sc;
            } else if (newRect) {
                renderBindNew(m, ctx.x, cy, animatedWidth, ih, mod, isPrev, ctx, alignRight);
                cy += ih + newRectGap * ctx.sc;
            } else {
                renderBind(m, ctx.x, cy, animatedWidth, ih, mod, isPrev, ctx);
                cy += ih;
            }
        }
    }

    private void renderBindDyn(MatrixStack m, float x, float y, float w, float h,
                               Module mod, boolean isPrev, RenderContext ctx, boolean alignRight) {
        float a = isPrev ? 1.0f : (float) mod.getAnimation().getValue();

        if (!separateKeyRect) {
            RenderUtil.CustomStyled.draw(m, x, y, w, h, Round.of(ctx.r), blurEnabled, blurStrength);
            renderBindOutline(m, x, y, w, h, ctx.r, ctx.sc, a);
        }
        renderBindContent(m, x, y, w, h, mod, isPrev, ctx, alignRight, a);
    }

    private void renderBindNew(MatrixStack m, float x, float y, float w, float h,
                               Module mod, boolean isPrev, RenderContext ctx, boolean alignRight) {
        float a = isPrev ? 1.0f : (float) mod.getAnimation().getValue();

        if (!separateKeyRect) {
            RenderUtil.CustomStyled.draw(m, x, y, w, h, Round.of(ctx.r), blurEnabled, blurStrength);
            renderBindOutline(m, x, y, w, h, ctx.r, ctx.sc, a);
        }
        renderBindContent(m, x, y, w, h, mod, isPrev, ctx, false, a);
    }

    private void renderBind(MatrixStack m, float x, float y, float w, float h,
                            Module mod, boolean isPrev, RenderContext ctx) {
        float a = isPrev ? 1.0f : (float) mod.getAnimation().getValue();
        renderBindContent(m, x, y, w, h, mod, isPrev, ctx, false, a);
    }

    private void renderBindOutline(MatrixStack m, float x, float y, float w, float h,
                                   float r, float sc, float alpha) {
        int tc = Theme.getInstance().clientColor();

        if (glowOutline) {
            int gc = ColorUtil.multAlpha(tc, glowOutlineAlpha * alpha);
            RenderUtil.Shadow.drawShadow(m, x - glowOutlineRadius / 2f, y - glowOutlineRadius / 2f,
                    w + glowOutlineRadius, h + glowOutlineRadius, glowOutlineRadius,
                    glowOutlineAlpha * alpha, gc, Round.of(r));
        } else if (enableOutline) {
            int oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), tc, 0.5f);
            float hw = outlineWidth * sc / 2f;
            RenderUtil.Rounded.roundedOutline(m, x - hw, y - hw, w + outlineWidth * sc,
                    h + outlineWidth * sc, outlineWidth * sc, ColorUtil.multAlpha(oc, alpha), Round.of(r));
        }
    }

    private void renderBindContent(MatrixStack m, float x, float y, float w, float h,
                                   Module mod, boolean isPrev, RenderContext ctx,
                                   boolean alignRight, float alpha) {
        float sa = ease3(alpha);
        float ta = ease4(alpha);

        m.push();
        m.translate(alignRight ? (sa - 1) * 10f * ctx.sc : (1 - sa) * 10f * ctx.sc, 0, 0);
        m.translate(x, y + h / 2f, 0);
        m.scale(1f, 0.3f + sa * 0.7f, 1f);
        m.translate(-x, -(y + h / 2f), 0);

        boolean enabled = mod.isEnabled();
        int textAlpha = enabled ? (int) (235 * ta) : (int) (80 * ta);
        int iconAlpha = enabled ? (int) (255 * ta) : (int) (80 * ta);

        if (layoutMode == LayoutMode.DEFAULT) {
            renderDefLayout(m, x, y, w, h, mod, ctx, textAlpha, iconAlpha);
        } else {
            renderRLLayout(m, x, y, w, h, mod, ctx, textAlpha, iconAlpha);
        }

        m.pop();
    }

    private void renderDefLayout(MatrixStack m, float x, float y, float w, float h,
                                 Module mod, RenderContext ctx, int textAlpha, int iconAlpha) {
        float cx = x + ctx.p;
        int clientColor = Theme.getInstance().clientColor();
        int fullIconColor = (iconAlpha << 24) | (clientColor & 0x00FFFFFF);
        String keyText = Keyboard.keyName(mod.getKey());
        float kw = font.getWidth(keyText, ctx.fs);

        if (separateKeyRect && (dynamicRects || newRect)) {
            // Separate key rect mode
            float keyRectWidth = ctx.p * 2 + kw;
            float textRectWidth = w - keyRectWidth - keyRectGap * ctx.sc;

            // Render text rect (left side)
            float textX = x;
            RenderUtil.CustomStyled.draw(m, textX, y, textRectWidth, h, Round.of(ctx.r), blurEnabled, blurStrength);
            renderBindOutline(m, textX, y, textRectWidth, h, ctx.r, ctx.sc, 1.0f);

            // Render module icon and name in text rect
            float tcx = textX + ctx.p;
            if (showModuleIcon) {
                renderModuleIcon(m, mod, tcx, y + (h - ctx.is) / 2f, ctx.is, clientColor, iconAlpha);
                tcx += ctx.is + 6f * ctx.sc;
                renderPoint(m, tcx - 2.5f, y + (h - ctx.fs) / 2f, ctx.fs, textAlpha);
                tcx += ctx.pw;
            }

            float ty = y + (h - ctx.fs) / 2f;
            float nameWidth = textRectWidth - (tcx - textX) - ctx.p;
            ScissorUtil.enableScissor(tcx, y, nameWidth + 2, h);
            font.draw(m, mod.getName(), tcx, ty, ColorUtil.getColor(255, 255, 255, textAlpha), ctx.fs);
            ScissorUtil.disableScissor();

            // Render key rect (right side)
            float keyX = textX + textRectWidth + keyRectGap * ctx.sc;
            RenderUtil.CustomStyled.draw(m, keyX, y, keyRectWidth, h, Round.of(ctx.r), blurEnabled, blurStrength);
            renderBindOutline(m, keyX, y, keyRectWidth, h, ctx.r, ctx.sc, 1.0f);

            // Render key text in key rect
            float kx = keyX + ctx.p;
            renderKeyBind(m, kx, ty, keyText, kw, ctx, clientColor, iconAlpha);
        } else {
            // Combined mode (original)
            if (showModuleIcon) {
                renderModuleIcon(m, mod, cx, y + (h - ctx.is) / 2f, ctx.is, clientColor, iconAlpha);
                cx += ctx.is + 6f * ctx.sc;
                renderPoint(m, cx - 2.5f, y + (h - ctx.fs) / 2f, ctx.fs, textAlpha);
                cx += ctx.pw;
            }

            float ty = y + (h - ctx.fs) / 2f;
            float kx = x + w - kw - ctx.p;
            float nameWidth = kx - cx - ctx.gap;

            ScissorUtil.enableScissor(cx, y, nameWidth + 2, h);
            font.draw(m, mod.getName(), cx, ty, ColorUtil.getColor(255, 255, 255, textAlpha), ctx.fs);
            ScissorUtil.disableScissor();

            renderKeyBind(m, kx, ty, keyText, kw, ctx, clientColor, iconAlpha);
        }
    }

    private void renderRLLayout(MatrixStack m, float x, float y, float w, float h,
                                Module mod, RenderContext ctx, int textAlpha, int iconAlpha) {
        float ty = y + (h - ctx.fs) / 2f;
        String keyText = Keyboard.keyName(mod.getKey());
        float kw = font.getWidth(keyText, ctx.fs);
        float kx = x + ctx.p;
        int clientColor = Theme.getInstance().clientColor();

        renderKeyBind(m, kx, ty, keyText, kw, ctx, clientColor, iconAlpha);

        float nx = kx + kw + ctx.gap;
        float endX = x + w - ctx.p;

        if (showModuleIcon) {
            float ix = endX - ctx.is;
            renderModuleIcon(m, mod, ix, y + (h - ctx.is) / 2f, ctx.is, clientColor, iconAlpha);

            float separatorX = ix - 6f * ctx.sc - 0.25f * ctx.sc;
            float separatorY = y + (h - ctx.is + 2.5f * ctx.sc) / 2f;
            float separatorH = ctx.is - 2.5f * ctx.sc;
            int sepAlpha = (int) ((13.0f / 255.0f) * iconAlpha);
            RenderUtil.Rounded.roundedRect(m, separatorX, separatorY, 0.25f * ctx.sc, separatorH,
                    ColorUtil.getColor(255, 255, 255, sepAlpha), Round.of(0.25f * ctx.sc));
            endX = separatorX - 6f * ctx.sc;
        }

        float nameWidth = endX - nx;
        ScissorUtil.enableScissor(nx, y, nameWidth + 2, h);
        font.draw(m, mod.getName(), nx, ty, ColorUtil.getColor(255, 255, 255, textAlpha), ctx.fs);
        ScissorUtil.disableScissor();
    }

    private void renderModuleIcon(MatrixStack m, Module mod, float x, float y, float size,
                                  int clientColor, int alpha) {
        int iconColor = (alpha << 24) | (clientColor & 0x00FFFFFF);

        if (moduleIconShadow) {
            RenderUtil.Shadow.drawShadow(m, x, y, size, size, moduleIconShadowRadius,
                    moduleIconShadowAlpha * (alpha / 255f),
                    ColorUtil.multAlpha(clientColor, moduleIconShadowAlpha * (alpha / 255f)),
                    Round.of(3));
        }

        if (mod.getCategory() == Category.COMBAT) {
            Fonts.iconf.draw(m, "c", x, y, iconColor, size);
        } else if (mod.getCategory() == Category.MOVEMENT) {
            Fonts.iconf.draw(m, "e", x, y, iconColor, size);
        } else {
            Fonts.CLICKGUI.draw(m, mod.getCategory().getIcon(), x, y, iconColor, size);
        }
    }

    private void renderPoint(MatrixStack m, float x, float y, float size, int textAlpha) {
        int dotAlpha = (int) (((DOT_COLOR >> 24 & 0xFF) * (textAlpha / 235f)));
        int pointColor = ColorUtil.getColor(
                DOT_COLOR >> 16 & 0xFF,
                DOT_COLOR >> 8 & 0xFF,
                DOT_COLOR & 0xFF,
                dotAlpha
        );
        font.draw(m, POINT, x, y, pointColor, size);
    }

    private void renderKeyBind(MatrixStack m, float x, float y, String keyText, float width,
                               RenderContext ctx, int clientColor, int alpha) {
        if (showKeyRect) {
        }

        int keyColor = (alpha << 24) | (clientColor & 0x00FFFFFF);


        font.draw(m, keyText, x, y, keyColor, ctx.fs);
    }

    private void renderOutlines(MatrixStack m, RenderContext ctx, Dimensions dim, boolean alignRight) {
        if (dynamicRects || newRect) return;

        int tc = Theme.getInstance().clientColor();

        if (glowOutline) {
            int gc = ColorUtil.multAlpha(tc, glowOutlineAlpha);
            RenderUtil.Shadow.drawShadow(m,
                    ctx.x - glowOutlineRadius / 2f, ctx.y - glowOutlineRadius / 2f,
                    animatedWidth + glowOutlineRadius, animatedHeight + glowOutlineRadius,
                    glowOutlineRadius, glowOutlineAlpha, gc, Round.of(ctx.r));
        } else if (enableOutline) {
            int oc = ColorUtil.interpolateColor(ColorUtil.getColor(255, 255, 255, 255), tc, 0.5f);
            float hw = outlineWidth * ctx.sc / 2f;
            RenderUtil.Rounded.roundedOutline(m,
                    ctx.x - hw, ctx.y - hw,
                    animatedWidth + outlineWidth * ctx.sc, animatedHeight + outlineWidth * ctx.sc,
                    outlineWidth * ctx.sc, oc, Round.of(ctx.r));
        }
    }

    private void updateDragAnimation(float x, float y, float w, float h) {
        float velocityX = x - lastX;
        float velocityY = y - lastY;
        boolean moving = Math.abs(velocityX) > 0.01f || Math.abs(velocityY) > 0.01f;

        if (moving) {
            lastMoveTime = System.currentTimeMillis();
            isDragging = true;
        } else if (System.currentTimeMillis() - lastMoveTime > 50) {
            isDragging = false;
        }

        float smoothSpeed = isDragging ? 0.25f : 0.08f;
        smoothVelocityX = smoothLerp(smoothVelocityX, velocityX, smoothSpeed);
        smoothVelocityY = smoothLerp(smoothVelocityY, velocityY, smoothSpeed);

        targetRotation = Math.max(-15f, Math.min(15f, smoothVelocityX * 1.8f - smoothVelocityY * 0.3f));
        currentRotation = elasticLerp(currentRotation, targetRotation, isDragging ? 0.18f : 0.06f, 0.15f);

        if (!isDragging && Math.abs(currentRotation) < 0.1f) {
            currentRotation *= 0.9f;
        }

        springOffsetX = Math.max(-5f, Math.min(5f,
                springOffsetX * 0.85f + (-smoothVelocityX * 0.3f - springOffsetX) * 0.12f));
        springOffsetY = Math.max(-5f, Math.min(5f,
                springOffsetY * 0.85f + (-smoothVelocityY * 0.2f - springOffsetY) * 0.12f));

        dragScaleEffect = smoothLerp(dragScaleEffect, isDragging ? 1.02f : 1.0f, 0.15f);

        lastX = x;
        lastY = y;
    }

    private float smoothLerp(float current, float target, float speed) {
        return current + (target - current) * (1.0f - (float) Math.pow(1.0f - speed, 1.5f));
    }

    private float elasticLerp(float current, float target, float speed, float elasticity) {
        float delta = target - current;
        float velocity = delta * speed;
        if (Math.abs(delta) < 0.5f) {
            velocity += delta * elasticity * 0.3f;
        }
        return current + velocity;
    }

    private float ease3(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private float ease4(float t) {
        return 1 - (float) Math.pow(1 - t, 4);
    }

    private void addPreviewKeybinds() {
        bindings.clear();
        List<Module> preview = Excellent.inst().moduleManager().values().stream()
                .filter(m -> m.getKey() != Keyboard.KEY_NONE.getKey())
                .limit(4)
                .collect(Collectors.toList());

        // Sort by width (widest first) for dynamic rects, otherwise alphabetically
        if (dynamicRects) {
            RenderContext tempCtx = new RenderContext(scale, 0, 0);
            preview.sort(Comparator.comparingDouble(m -> -calculateModuleWidth(tempCtx, m)));
        } else {
            preview.sort(Comparator.comparing(Module::getName));
        }

        for (Module m : preview) {
            m.getAnimation().setValue(1.0);
            bindings.add(m);
        }
    }

    private void updateModulesList() {
        bindings.clear();
        if (showAllBinds) {
            List<Module> modules = Excellent.inst().moduleManager().values().stream()
                    .filter(m -> m.isAllowDisable() && m.getKey() != Keyboard.KEY_NONE.getKey())
                    .collect(Collectors.toList());

            // Sort by width (widest first) for dynamic rects, otherwise alphabetically
            if (dynamicRects) {
                RenderContext tempCtx = new RenderContext(scale, 0, 0);
                modules.sort(Comparator.comparingDouble(m -> -calculateModuleWidth(tempCtx, m)));
            } else {
                modules.sort(Comparator.comparing(Module::getName));
            }

            bindings.addAll(modules);
            for (Module m : bindings) {
                m.getAnimation().setValue(1.0);
            }
        } else {
            List<Module> modules = Excellent.inst().moduleManager().values().stream()
                    .filter(this::shouldDisplay)
                    .collect(Collectors.toList());

            // Sort by width (widest first) for dynamic rects, otherwise alphabetically
            if (dynamicRects) {
                RenderContext tempCtx = new RenderContext(scale, 0, 0);
                modules.sort(Comparator.comparingDouble(m -> -calculateModuleWidth(tempCtx, m)));
            } else {
                modules.sort(Comparator.comparing(Module::getName));
            }

            bindings.addAll(modules);
        }
    }

    public boolean shouldDisplay(Module m) {
        return m.isAllowDisable() &&
                (m.isEnabled() || m.getAnimation().getValue() != 0.0) &&
                m.getKey() != Keyboard.KEY_NONE.getKey();
    }
}