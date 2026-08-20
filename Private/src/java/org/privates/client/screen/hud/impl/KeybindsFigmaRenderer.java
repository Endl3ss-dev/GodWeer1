package org.privates.client.screen.hud.impl;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class KeybindsFigmaRenderer extends AbstractHud implements IRenderer {
    private static final float FIGMA_DIVIDER = 1.6f;
    private static final float GLOBAL_SIZE_MUL = 0.88f;
    private final DragSetting drag;
    private final List<Module> modules = new ArrayList<>();
    private float scale = 1f;
    private boolean blurEnabled = true;
    private float blurStrength = 7.5f;
    private float animatedLeftW = -1f;
    private float animatedRightW = -1f;
    private float animatedBodyH = -1f;
    private float animatedTotalW = -1f;
    private float animatedTotalH = -1f;
    private float scaleV = 0f;

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setBlurEnabled(boolean blurEnabled) {
        this.blurEnabled = blurEnabled;
    }

    public void setBlurStrength(float blurStrength) {
        this.blurStrength = blurStrength;
    }

    @Override
    public void render(Render2DEvent event) {
        collectModules();
        scaleV = Mathf.fast(scaleV, modules.isEmpty() ? 0f : 1f, 14f);
        if (scaleV < 0.02f) {
            return;
        }

        float sc = (scale / FIGMA_DIVIDER) * GLOBAL_SIZE_MUL;
        float x = drag.position.x;
        float y = drag.position.y;
        float headerH = 38f * sc;
        float rowH = 18f * sc;
        float splitGap = 2f * sc;
        float minBodyH = rowH + 8f * sc;
        float minLeftW = 146f * sc;
        float minRightW = 22f * sc;

        float nameSize = 12f * sc;
        float keySize = 12f * sc;
        float maxNameW = 0f;
        float maxKeyW = 0f;
        for (Module module : modules) {
            maxNameW = Math.max(maxNameW, Fonts.SF_BOLD.getWidth(module.getName(), nameSize));
            maxKeyW = Math.max(maxKeyW, Fonts.SF_BOLD.getWidth(compactKey(Keyboard.keyName(module.getKey())), keySize));
        }

        float targetLeftW = Math.max(minLeftW, 38f * sc + maxNameW + 12f * sc);
        float targetRightW = Math.max(minRightW, maxKeyW + 14f * sc);
        float targetBodyH = Math.max(minBodyH, modules.size() * rowH + 8f * sc);

        if (animatedLeftW < 0f) animatedLeftW = targetLeftW;
        if (animatedRightW < 0f) animatedRightW = targetRightW;
        if (animatedBodyH < 0f) animatedBodyH = targetBodyH;

        animatedLeftW = Mathf.fast(animatedLeftW, targetLeftW, 12f);
        animatedRightW = Mathf.fast(animatedRightW, targetRightW, 12f);
        animatedBodyH = Mathf.fast(animatedBodyH, targetBodyH, 12f);

        float leftW = animatedLeftW;
        float rightW = animatedRightW;
        float bodyH = animatedBodyH;
        float targetTotalW = leftW + splitGap + rightW;
        float targetTotalH = headerH + splitGap + bodyH;
        if (animatedTotalW < 0f) animatedTotalW = targetTotalW;
        if (animatedTotalH < 0f) animatedTotalH = targetTotalH;
        animatedTotalW = Mathf.fast(animatedTotalW, targetTotalW, 12f);
        animatedTotalH = Mathf.fast(animatedTotalH, targetTotalH, 12f);
        float totalW = animatedTotalW;
        float totalH = animatedTotalH;
        float rightX = x + leftW + splitGap;
        float bodyY = y + headerH + splitGap;

        MatrixStack ms = event.getMatrix();
        int splitColor = ColorUtil.getColor(174, 174, 174, 92);
        int titleColor = ColorUtil.getColor(255, 255, 255);
        int keyColor = ColorUtil.getColor(188, 188, 188);
        int accent = ColorUtil.multAlpha(theme().clientColor(), 1f);
        int shadowColor = ColorUtil.multAlpha(theme().clientColor(), 0.32f);

        ms.push();
        float cx = x + totalW / 2f;
        float cy = y + totalH / 2f;
        ms.translate(cx, cy, 0);
        ms.scale(scaleV, scaleV, 1f);
        ms.translate(-cx, -cy, 0);

        RenderUtil.Shadow.drawShadow(ms, x, y, totalW, totalH, 8f * sc, 0.45f, shadowColor, Round.of(10f * sc));

        RenderUtil.CustomStyled.draw(ms, x, y, leftW, headerH, Round.of(0f, 10f * sc, 0f, 0f), blurEnabled, blurStrength);
        RenderUtil.CustomStyled.draw(ms, rightX, y, rightW, headerH, Round.of(0f, 0f, 0f, 10f * sc), blurEnabled, blurStrength);
        RenderUtil.CustomStyled.draw(ms, x, bodyY, leftW, bodyH, Round.of(10f * sc, 0f, 0f, 0f), blurEnabled, blurStrength);
        RenderUtil.CustomStyled.draw(ms, rightX, bodyY, rightW, bodyH, Round.of(0f, 0f, 10f * sc, 0f), blurEnabled, blurStrength);

        RenderUtil.Rounded.roundedRect(ms, x + leftW, y, splitGap, totalH, splitColor, Round.zero());
        RenderUtil.Rounded.roundedRect(ms, x, y + headerH, totalW, splitGap, splitColor, Round.zero());

        float glow = 18f * sc;
        RenderUtil.Shadow.drawShadow(ms, x + 7f * sc, y + 9f * sc, glow, glow, 4f * sc, 0.4f, ColorUtil.multAlpha(accent, 0.6f), Round.of(8f * sc));
        Fonts.icon_new.draw(ms, "f", x + 10f * sc, y + 13f * sc, accent, 12f * sc);
        RenderUtil.Rounded.roundedRect(ms, x + 27f * sc, y + 11f * sc, 1f * sc, 16f * sc, ColorUtil.getColor(174, 174, 174, 150), Round.zero());
        Fonts.SF_BOLD.draw(ms, "HotKeys", x + 33f * sc, y + 10f * sc, titleColor, 15f * sc);
        String arrow = "⌄";
        float aw = Fonts.SF_BOLD.getWidth(arrow, 14f * sc);
        Fonts.SF_BOLD.draw(ms, arrow, rightX + rightW / 2f - aw / 2f, y + 9f * sc, keyColor, 14f * sc);

        float rowY = bodyY + 6f * sc;
        for (Module module : modules) {
            float a = (float) module.getAnimation().getValue();
            if (a <= 0.01f) {
                continue;
            }
            drawRow(ms, x, leftW, rightW, rowY, rowH * a, module, accent, titleColor, keyColor, splitColor, sc, a);
            rowY += rowH * a;
        }

        drag.size.set(totalW, totalH);
        ms.pop();
    }

    private void drawRow(MatrixStack ms, float x, float leftW, float rightW, float rowY, float rowH, Module module, int iconColor, int nameColor, int keyColor, int splitColor, float sc, float alpha) {
        if (rowH < 2f * sc) {
            return;
        }
        int rowNameColor = ColorUtil.multAlpha(nameColor, alpha);
        int rowKeyColor = ColorUtil.multAlpha(keyColor, alpha);
        int rowIconColor = ColorUtil.multAlpha(iconColor, alpha);
        int rowSplitColor = ColorUtil.multAlpha(splitColor, alpha);
        float sepX = x + 10f * sc;
        RenderUtil.Rounded.roundedRect(ms, sepX, rowY + rowH / 2f - 4f * sc, 1f * sc, 8f * sc, rowSplitColor, Round.zero());
        renderModuleIcon(ms, module, x + 15f * sc, rowY + rowH / 2f - 6f * sc, rowIconColor, 12f * sc);
        float nameX = x + 29.5f * sc;
        float nameW = leftW - 37.5f * sc;
        ScissorUtil.enableScissor(nameX, rowY, nameW, rowH);
        Fonts.SF_BOLD.draw(ms, module.getName(), nameX, rowY + rowH / 2f - 7f * sc, rowNameColor, 12f * sc);
        ScissorUtil.disableScissor();

        String keyText = compactKey(Keyboard.keyName(module.getKey()));
        float keySize = 12f * sc;
        float kw = Fonts.SF_BOLD.getWidth(keyText, keySize);
        float keyX = x + leftW + rightW / 2f - kw / 2f;
        Fonts.SF_BOLD.draw(ms, keyText, keyX, rowY + rowH / 2f - 7f * sc, rowKeyColor, keySize);
    }

    private void renderModuleIcon(MatrixStack ms, Module mod, float x, float y, int color, float size) {
        if (mod.getCategory() == Category.COMBAT) {
            Fonts.iconf.draw(ms, "c", x, y, color, size);
            return;
        }
        if (mod.getCategory() == Category.MOVEMENT) {
            Fonts.iconf.draw(ms, "e", x, y, color, size);
            return;
        }
        Fonts.CLICKGUI.draw(ms, mod.getCategory().getIcon(), x, y, color, size);
    }

    private String compactKey(String key) {
        if (key == null || key.equalsIgnoreCase("NONE")) {
            return "-";
        }
        if (key.startsWith("MOUSE")) {
            String d = key.replace("MOUSE", "");
            return d.isEmpty() ? "M" : "M" + d;
        }
        if (key.length() <= 2) {
            return key;
        }
        return key.substring(0, 1);
    }

    private void collectModules() {
        modules.clear();
        Excellent.inst().moduleManager().values().forEach(m -> m.getAnimation().update());
        List<Module> active = Excellent.inst().moduleManager().values().stream()
                .filter(this::shouldDisplay)
                .sorted(Comparator.comparing(Module::getName))
                .limit(8)
                .collect(Collectors.toList());
        modules.addAll(active);

        if (!modules.isEmpty()) {
            return;
        }
        if (!(mc.currentScreen instanceof ChatScreen)) {
            return;
        }

        List<Module> preview = Excellent.inst().moduleManager().values().stream()
                .filter(m -> m.isAllowDisable() && m.getKey() != Keyboard.KEY_NONE.getKey())
                .sorted(Comparator.comparing(Module::getName))
                .limit(3)
                .collect(Collectors.toList());
        modules.addAll(preview);
    }

    private boolean shouldDisplay(Module module) {
        return module.isAllowDisable()
                && module.getKey() != Keyboard.KEY_NONE.getKey()
                && (module.isEnabled() || module.getAnimation().getValue() > 0.01);
    }
}
