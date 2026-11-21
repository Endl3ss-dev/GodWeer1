/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1293
 *  net.minecraft.class_1657
 *  net.minecraft.class_408
 */
package ru.decide.module.impl.display.interfaceimpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1293;
import net.minecraft.class_1657;
import net.minecraft.class_408;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.combat.UseTracker;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.interfaceimpl.element;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.other.UseCooldowns;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.RollingText;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class UseTrackerHud
implements element {
    private static float S = 1.0f;
    private static float H = 16.0f * S;
    private static float MIN_W = 60.0f * S;
    private static float RADIUS = 5.0f * S;
    private static float BASE_H = 4.0f * S;
    private static float TITLE_TEXT = 7.0f * S;
    private static float TITLE_ICON = 5.0f * S;
    private static float ROW_TEXT = 6.5f * S;
    private static float TITLE_ICON_X = 5.0f * S;
    private static float TITLE_ICON_Y = 5.5f * S;
    private static float TITLE_TEXT_X = 12.5f * S;
    private static float TITLE_TEXT_Y = 3.4f * S;
    private static float ROW_HEIGHT = 14.0f * S;
    private static float ROW_BASE_W = 22.0f * S;
    private static float ROW_PADDING_X = 5.0f * S;
    private static float ROW_START_Y = 5.0f * S;
    private static float ROW_ANIM_OFFSET = 15.0f * S;
    private static float HEAD_TEXT_X = 15.0f * S;
    private static float HEAD_ICON_Y = 0.9f * S;
    private static float SEP_PADDING_X = 4.0f * S;
    private static float SEP_OFFSET_Y = 3.2f * S;
    private final ru.decide.utils.animation.satoshi.Animation animation1 = new EaseInOutQuad(300, 1.0);
    private final ru.decide.utils.animation.satoshi.Animation animation2 = new EaseInOutQuad(300, 1.0);
    private float widthAnim = 0.0f;
    private final Animation headAnimation = new Animation();
    private String head = "";
    private final Map<String, Row> rows = new LinkedHashMap<String, Row>();

    @Override
    public void onRender(DragSetting dragSetting, InterFace interFace) {
        String targetName;
        UseTracker tracker = UseTracker.getInstance();
        S = InterFace.getInstance().sizeHud.getValue().floatValue();
        H = 16.0f * S;
        MIN_W = 60.0f * S;
        RADIUS = 5.0f * S;
        BASE_H = 4.0f * S;
        TITLE_TEXT = 7.0f * S;
        TITLE_ICON = 5.0f * S;
        ROW_TEXT = 6.5f * S;
        TITLE_ICON_X = 5.0f * S;
        TITLE_ICON_Y = 5.5f * S;
        TITLE_TEXT_X = 12.5f * S;
        TITLE_TEXT_Y = 3.4f * S;
        ROW_HEIGHT = 14.0f * S;
        ROW_BASE_W = 22.0f * S;
        ROW_PADDING_X = 5.0f * S;
        ROW_START_Y = 5.0f * S;
        ROW_ANIM_OFFSET = 15.0f * S;
        HEAD_TEXT_X = 15.0f * S;
        HEAD_ICON_Y = 0.9f * S;
        SEP_PADDING_X = 4.0f * S;
        SEP_OFFSET_Y = 3.2f * S;
        this.rows.values().forEach(row -> {
            row.active = false;
        });
        boolean hasTarget = tracker.hasTarget();
        if (hasTarget) {
            for (UseCooldowns.Item item : UseCooldowns.Item.values()) {
                int left = tracker.remaining(item);
                if (left <= 0) continue;
                Row row2 = this.rows.computeIfAbsent(item.name(), k -> new Row());
                row2.label = item.label;
                row2.color = ColorUtil.getColor(240);
                row2.time.set(UseTrackerHud.time(left));
                row2.active = true;
            }
            class_1657 player = tracker.getTargetPlayer();
            if (player != null) {
                for (UseCooldowns.Buff buff : UseCooldowns.Buff.values()) {
                    class_1293 effect;
                    if (!tracker.buffs.getValue(buff.label) || (effect = player.method_6112(buff.effect)) == null) continue;
                    Row row3 = this.rows.computeIfAbsent(buff.name(), k -> new Row());
                    row3.label = buff.label + UseTrackerHud.roman(effect.method_5578() + 1);
                    row3.color = buff.color;
                    row3.time.set(UseTrackerHud.time(effect.method_5584() / 20));
                    row3.active = true;
                }
            }
        }
        String string = targetName = hasTarget ? tracker.getTargetName() : "";
        if (!targetName.isEmpty()) {
            this.head = targetName;
        }
        this.headAnimation.update();
        this.headAnimation.run(targetName.isEmpty() ? 0.0 : 1.0, (double)0.12f, Easings.QUAD_OUT);
        float ha = this.headAnimation.get();
        boolean isEmpty = ha <= 0.01f && this.rows.values().stream().noneMatch(row -> row.active);
        float x = dragSetting.position.x;
        float y = dragSetting.position.y;
        boolean closeCondition = isEmpty && !(UseTrackerHud.mc.field_1755 instanceof class_408);
        this.animation1.setDirection(closeCondition ? Direction.BACKWARDS : Direction.FORWARDS);
        this.animation2.setDirection(UseTrackerHud.mc.field_1755 instanceof class_408 && isEmpty ? Direction.FORWARDS : Direction.BACKWARDS);
        dragSetting.active = !closeCondition;
        float alpha = this.animation1.getOutput();
        if (closeCondition && alpha == 0.0f) {
            this.rows.clear();
            this.head = "";
            return;
        }
        float alpha2 = this.animation2.getOutput();
        RenderUtil.Render2D.glow(x, y, MIN_W, H, ColorUtil.replAlpha(ColorUtil.getColor(0), alpha2 * 0.1f), RADIUS, 12.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, MIN_W, H, alpha2, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(alpha2 * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.35f, 1.0f)));
        Font font = Fonts.sf_regular;
        Fonts.decide_2.draw("V", x + TITLE_ICON_X, y + TITLE_ICON_Y, TITLE_ICON, ColorUtil.replAlpha(ColorUtil.client(), alpha2));
        font.draw("User Tracker", x + TITLE_TEXT_X, y + TITLE_TEXT_Y, TITLE_TEXT, ColorUtil.multAlpha(ColorUtil.getColor(240), alpha2));
        float h = BASE_H;
        float w = 0.0f;
        ArrayList<String> toRemove = new ArrayList<String>();
        for (Map.Entry<String, Row> entry : this.rows.entrySet()) {
            Row row4 = entry.getValue();
            row4.animation.update();
            row4.animation.run(row4.active ? 1.0 : 0.0, (double)0.12f, Easings.QUAD_OUT);
            float a = row4.animation.get();
            if (a <= 0.01f && !row4.active) {
                toRemove.add(entry.getKey());
                continue;
            }
            float rowW = ROW_BASE_W + font.getWidth(row4.label, ROW_TEXT) + row4.time.width(font, ROW_TEXT);
            w = Math.max(w, rowW * a);
            h += ROW_HEIGHT * a;
        }
        toRemove.forEach(this.rows::remove);
        if (ha > 0.01f) {
            w = Math.max(w, (ROW_BASE_W + font.getWidth(this.head, ROW_TEXT)) * ha);
            h += ROW_HEIGHT * ha;
        }
        this.widthAnim += (w - this.widthAnim) * 0.2f;
        RenderUtil.Render2D.glow(x, y, this.widthAnim, h - 0.5f, ColorUtil.replAlpha(ColorUtil.getColor(0), alpha * 0.1f), RADIUS, 12.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, this.widthAnim, h, alpha, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(alpha * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.35f, 1.0f)));
        float offsetY = y + ROW_START_Y;
        float offsetY2 = 0.0f;
        if (ha > 0.01f) {
            float addX = 0.0f;
            Fonts.category.draw("Q", x + ROW_PADDING_X - addX, offsetY + HEAD_ICON_Y, ROW_TEXT, ColorUtil.replAlpha(ColorUtil.client(), alpha * ha));
            font.draw(this.head, x + HEAD_TEXT_X - addX, offsetY, ROW_TEXT, ColorUtil.getColor(240, alpha * ha));
            offsetY += ROW_HEIGHT * ha;
            offsetY2 += ROW_HEIGHT * ha;
        }
        boolean firstRow = ha <= 0.01f;
        for (Row row5 : this.rows.values()) {
            float a = row5.animation.get();
            if (a <= 0.01f && !row5.active) continue;
            float addX = ROW_ANIM_OFFSET - ROW_ANIM_OFFSET * a;
            font.draw(row5.label, x + ROW_PADDING_X - addX, offsetY, ROW_TEXT, ColorUtil.replAlpha(row5.color, alpha * a));
            row5.time.draw(font, x + addX - ROW_PADDING_X + this.widthAnim - row5.time.width(font, ROW_TEXT), offsetY, ROW_TEXT, ColorUtil.getColor(200, alpha * a));
            if (!firstRow) {
                RenderUtil.Render2D.rect(x + SEP_PADDING_X + addX, offsetY - SEP_OFFSET_Y, this.widthAnim - SEP_PADDING_X * 2.0f - addX, 0.5f, ColorUtil.getColor(255, 0.05f * alpha * a), 1.0f);
            }
            firstRow = false;
            offsetY += ROW_HEIGHT * a;
            offsetY2 += ROW_HEIGHT * a;
        }
        dragSetting.size.set((float)ColorUtil.overCol((int)Math.max(this.widthAnim, 20.0f * S), (int)MIN_W, alpha2), (float)ColorUtil.overCol((int)offsetY2, (int)H, alpha2));
    }

    private static String time(int seconds) {
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private static String roman(int level) {
        return switch (level) {
            case 1 -> "";
            case 2 -> " II";
            case 3 -> " III";
            case 4 -> " IV";
            case 5 -> " V";
            default -> " " + level;
        };
    }

    @Environment(value=EnvType.CLIENT)
    private static class Row {
        final RollingText time = new RollingText();
        final Animation animation = new Animation();
        boolean active;
        String label = "";
        int color;

        private Row() {
        }
    }
}

