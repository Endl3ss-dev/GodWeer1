/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1304
 *  net.minecraft.class_1799
 *  net.minecraft.class_332
 *  net.minecraft.class_408
 */
package ru.decide.module.impl.display.interfaceimpl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1304;
import net.minecraft.class_1799;
import net.minecraft.class_332;
import net.minecraft.class_408;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.interfaceimpl.element;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.ItemRender;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class ArmorHud
implements element {
    private static float S = 1.0f;
    private static float RADIUS = 4.0f * S;
    private static float ROW_HEIGHT = 16.0f * S;
    private static float ROW_TEXT = 6.5f * S;
    private static float ICON_SIZE = 8.0f * S;
    private static float PAD_X = 5.0f * S;
    private static float PAD_Y = 3.0f * S;
    private static float BAR_H = 2.0f * S;
    private static float BAR_GAP = 1.5f * S;
    private static float ICON_TEXT_GAP = 3.0f * S;
    private final Animation[] rowAnims = new Animation[]{new Animation(), new Animation(), new Animation(), new Animation()};
    private final float[] smoothDur = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    private ru.decide.utils.animation.satoshi.Animation openAnim = new EaseInOutQuad(300, 1.0);

    @Override
    public void onRender(DragSetting dragSetting, InterFace interFace) {
        this.onRender(dragSetting, interFace, null);
    }

    public void onRender(DragSetting dragSetting, InterFace interFace, EventDisplay eventDisplay) {
        S = InterFace.getInstance().sizeHud.getValue().floatValue();
        RADIUS = 4.0f * S;
        ROW_HEIGHT = 16.0f * S;
        ROW_TEXT = 6.5f * S;
        ICON_SIZE = 8.0f * S;
        PAD_X = 5.0f * S;
        PAD_Y = 3.0f * S;
        BAR_H = 2.0f * S;
        BAR_GAP = 1.5f * S;
        ICON_TEXT_GAP = 3.0f * S;
        float x = dragSetting.position.x;
        float y = dragSetting.position.y;
        class_1304[] slots = new class_1304[]{class_1304.field_6169, class_1304.field_6174, class_1304.field_6172, class_1304.field_6166};
        float alpha = 0.0f;
        for (int i = 0; i < 4; ++i) {
            this.rowAnims[i].update();
            class_1799 stack = ArmorHud.mc.field_1724.method_6118(slots[i]);
            boolean hasItem = !stack.method_7960();
            this.rowAnims[i].run(hasItem ? 1.0 : 0.0, (double)0.15f, Easings.SINE_OUT);
            alpha = Math.max(alpha, this.rowAnims[i].get());
        }
        boolean closeCondition = alpha < 0.01f && !(ArmorHud.mc.field_1755 instanceof class_408);
        this.openAnim.setDirection(closeCondition ? Direction.BACKWARDS : Direction.FORWARDS);
        float oa = this.openAnim.getOutput();
        boolean bl = dragSetting.active = oa > 0.01f;
        if (oa <= 0.01f) {
            return;
        }
        float totalH = 0.0f;
        for (int i = 0; i < 4; ++i) {
            totalH += ROW_HEIGHT * this.rowAnims[i].get();
        }
        if (totalH < ROW_HEIGHT) {
            totalH = ROW_HEIGHT;
        }
        totalH += PAD_Y * 2.0f * oa;
        float maxW = 50.0f * S;
        Font font = Fonts.sf_medium;
        for (int i = 0; i < 4; ++i) {
            float a = this.rowAnims[i].get();
            if (a <= 0.01f) continue;
            class_1799 stack = ArmorHud.mc.field_1724.method_6118(slots[i]);
            int dur = stack.method_7960() ? 0 : stack.method_7936() - stack.method_7919();
            int n = i;
            this.smoothDur[n] = this.smoothDur[n] + ((float)dur - this.smoothDur[i]) * 0.15f;
            String durStr = String.valueOf(Math.round(this.smoothDur[i]));
            float w = ICON_SIZE + ICON_TEXT_GAP + font.getWidth(durStr, ROW_TEXT) + PAD_X * 2.0f;
            if (!(w > maxW)) continue;
            maxW = w;
        }
        float barMaxW = maxW - PAD_X * 2.0f;
        RenderUtil.Render2D.glow(x, y, maxW, totalH - 0.5f, ColorUtil.getColor(0, 0.1f * oa), RADIUS, 8.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, maxW, totalH, 1.0f, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(oa * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.35f, 1.0f)));
        float cy = y + PAD_Y * oa;
        for (int i = 0; i < 4; ++i) {
            class_332 context;
            float pct;
            float a = this.rowAnims[i].get();
            if (a <= 0.01f) continue;
            class_1799 stack = ArmorHud.mc.field_1724.method_6118(slots[i]);
            int dur = stack.method_7960() ? 0 : stack.method_7936() - stack.method_7919();
            int maxDur = stack.method_7960() ? 1 : stack.method_7936();
            float f = pct = stack.method_7960() ? 0.0f : (float)dur / (float)maxDur;
            int durColor = stack.method_7960() ? ColorUtil.getColor(150, a * oa * 0.3f) : (pct > 0.5f ? ColorUtil.replAlpha(ColorUtil.client(), a * oa) : (pct > 0.2f ? ColorUtil.getColor(255, 200, 50, a * oa) : ColorUtil.getColor(255, 70, 70, a * oa)));
            float textY = cy + 1.5f * S;
            float iconX = x + PAD_X;
            float iconY = textY + (ROW_TEXT - ICON_SIZE) * 0.5f;
            class_332 class_3322 = context = eventDisplay != null ? eventDisplay.getDrawContext() : null;
            if (context != null) {
                ItemRender.drawItemWithContext(context, stack, iconX, iconY, ICON_SIZE / 16.0f, a * oa);
            } else {
                ItemRender.drawItem(stack, iconX, iconY, ICON_SIZE / 16.0f, a * oa);
            }
            float textX = x + PAD_X + ICON_SIZE + ICON_TEXT_GAP;
            font.draw(String.valueOf(Math.round(this.smoothDur[i])), textX, textY, ROW_TEXT, durColor);
            float barY = textY + ROW_TEXT + BAR_GAP;
            float barW = barMaxW * pct * a;
            int barBg = ColorUtil.getColor(255, 0.12f * a * oa);
            int barFill = stack.method_7960() ? ColorUtil.getColor(150, a * oa * 0.3f) : (pct > 0.5f ? ColorUtil.replAlpha(ColorUtil.client(), a * oa) : (pct > 0.2f ? ColorUtil.getColor(255, 200, 50, a * oa) : ColorUtil.getColor(255, 70, 70, a * oa)));
            RenderUtil.Render2D.rect(x + PAD_X, barY, barMaxW, BAR_H, barBg, BAR_H / 2.0f);
            if (barW > 0.5f) {
                RenderUtil.Render2D.rect(x + PAD_X, barY, Math.max(barW, BAR_H), BAR_H, barFill, BAR_H / 2.0f);
            }
            cy += ROW_HEIGHT * a;
        }
        dragSetting.size.set(maxW, totalH);
    }
}

