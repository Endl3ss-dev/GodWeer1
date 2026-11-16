/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_1291
 *  net.minecraft.class_1293
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2960
 *  net.minecraft.class_408
 *  net.minecraft.class_4081
 *  org.joml.Matrix3x2fStack
 */
package ru.decide.module.impl.display.interfaceimpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2960;
import net.minecraft.class_408;
import net.minecraft.class_4081;
import org.joml.Matrix3x2fStack;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.ItemRender;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class Potions
implements IMinecraft {
    private static float S = 1.0f;
    private static float PILL_H = 14.0f * S;
    private static float PILL_GAP = 3.0f * S;
    private static float PILL_RADIUS = 7.0f * S;
    private static float PILL_TEXT = 6.5f * S;
    private static float MIN_W = 44.0f * S;
    private static float RADIUS = 5.0f * S;
    private static float ROW_TEXT = 6.5f * S;
    private static float ICON = 7.0f * S;
    private static float ROW_HEIGHT = 14.0f * S;
    private static float ROW_PADDING_X = 5.0f * S;
    private static float ROW_START_Y = 5.0f * S;
    private static float ICON_PADDING = 4.0f * S;
    private static float SCROLL_OFFSET_Y = 4.0f * S;
    private final ru.decide.utils.animation.satoshi.Animation animation1 = new EaseInOutQuad(300, 1.0);
    private final Map<String, EffectData> displayedEffects = new LinkedHashMap<String, EffectData>();
    private final List<EffectData> sortedBuffer = new ArrayList<EffectData>();
    private final List<String> removeBuffer = new ArrayList<String>();
    private static final String[][] PLACEHOLDERS = new String[][]{{"\u0423\u0434\u0430\u0447\u0430", "0:23", "mob_effect/luck"}, {"\u0421\u043f\u0435\u0448\u043a\u0430", "1:49", "mob_effect/haste"}, {"\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", "0:45", "mob_effect/speed"}, {"\u0420\u0435\u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u044f", "0:33", "mob_effect/regeneration"}, {"\u0421\u0438\u043b\u0430", "0:18", "mob_effect/strength"}, {"\u041d\u043e\u0447\u043d\u043e\u0435 \u0437\u0440\u0435\u043d\u0438\u0435", "4:10", "mob_effect/night_vision"}, {"\u0421\u043e\u043f\u0440\u043e\u0442\u0438\u0432\u043b\u0435\u043d\u0438\u0435", "0:55", "mob_effect/resistance"}, {"\u041e\u0433\u043d. \u0441\u043e\u043f\u0440\u043e\u0442\u0438\u0432\u043b\u0435\u043d\u0438\u0435", "3:20", "mob_effect/fire_resistance"}};
    private int placeholderIndex = 0;
    private long placeholderLastTick = 0L;
    private static final long PLACEHOLDER_INTERVAL_TICKS = 50L;

    public void onRender(DragSetting dragSetting, InterFace interFace, EventDisplay eventDisplay) {
        float pillIconSize;
        float rowPad;
        boolean inChat;
        S = InterFace.getInstance().sizeHud.getValue().floatValue();
        PILL_H = 14.0f * S;
        PILL_GAP = 3.0f * S;
        PILL_RADIUS = 7.0f * S;
        PILL_TEXT = 6.5f * S;
        MIN_W = 44.0f * S;
        RADIUS = 5.0f * S;
        ROW_TEXT = 6.5f * S;
        ICON = 7.0f * S;
        ROW_HEIGHT = 14.0f * S;
        ROW_PADDING_X = 5.0f * S;
        ROW_START_Y = 5.0f * S;
        ICON_PADDING = 4.0f * S;
        SCROLL_OFFSET_Y = 4.0f * S;
        float chatBoost = Potions.mc.field_1755 instanceof class_408 ? 0.15f : 0.0f;
        Collection currentEffects = Potions.mc.field_1724.method_6026();
        this.displayedEffects.values().forEach(data -> {
            data.active = false;
        });
        for (class_1293 effect : currentEffects) {
            String name = ((class_1291)effect.method_5579().comp_349()).method_5560().getString();
            int amplifier = effect.method_5578() + 1;
            String duration = this.getDurationString(effect);
            boolean isNegative = ((class_1291)effect.method_5579().comp_349()).method_18792() == class_4081.field_18272;
            EffectData data2 = this.displayedEffects.computeIfAbsent(name, k -> new EffectData(name, duration, amplifier, isNegative, effect.method_5584(), effect));
            if (!duration.equals(data2.duration)) {
                data2.prevDuration = data2.duration;
                data2.duration = duration;
                data2.digitAnim.set(0.0);
                data2.digitAnim.run(1.0, 0.18, Easings.QUAD_OUT);
            }
            data2.durationTicks = effect.method_5584();
            data2.negative = isNegative;
            data2.effectInstance = effect;
            data2.active = true;
        }
        boolean isEmpty = this.displayedEffects.isEmpty();
        boolean showPlaceholders = inChat = Potions.mc.field_1755 instanceof class_408;
        float x = dragSetting.position.x;
        float pillY = dragSetting.position.y;
        float y = pillY + PILL_H + PILL_GAP;
        boolean closeCondition = isEmpty && !inChat;
        this.animation1.setDirection(closeCondition ? Direction.BACKWARDS : Direction.FORWARDS);
        dragSetting.active = !closeCondition;
        float alpha = this.animation1.getOutput();
        if (closeCondition && alpha == 0.0f) {
            return;
        }
        Font font = Fonts.sf_regular;
        float h = rowPad = 1.5f * S;
        float w = 0.0f;
        this.sortedBuffer.clear();
        this.sortedBuffer.addAll(this.displayedEffects.values());
        this.sortedBuffer.sort(Comparator.comparingInt(data -> data.name.length() + data.duration.length()).reversed());
        if (showPlaceholders) {
            long currentTick;
            long l = currentTick = Potions.mc.field_1687 != null ? Potions.mc.field_1687.method_75260() : 0L;
            if (currentTick - this.placeholderLastTick >= 50L) {
                this.placeholderLastTick = currentTick;
                this.placeholderIndex = (this.placeholderIndex + 1) % PLACEHOLDERS.length;
            }
            for (int i = 0; i < 2; ++i) {
                int idx = (this.placeholderIndex + i) % PLACEHOLDERS.length;
                String pName = PLACEHOLDERS[idx][0];
                String pTime = PLACEHOLDERS[idx][1];
                float nameW = font.getWidth(pName, ROW_TEXT);
                float durW = font.getWidth(pTime, ROW_TEXT);
                float rowW = ROW_PADDING_X + ICON + ICON_PADDING + nameW + ICON_PADDING + durW + ROW_PADDING_X;
                w = Math.max(w, rowW);
                h += ROW_HEIGHT;
            }
        } else {
            this.removeBuffer.clear();
            for (EffectData data3 : this.sortedBuffer) {
                data3.animation.update();
                data3.animation.run(data3.active ? 1.0 : 0.0, (double)0.12f, Easings.QUAD_OUT);
                data3.digitAnim.update();
                float a = data3.animation.get();
                if (a <= 0.01f && !data3.active) {
                    this.removeBuffer.add(data3.name);
                    continue;
                }
                float nameW = font.getWidth(this.label(data3), ROW_TEXT);
                float durW = font.getWidth(data3.duration, ROW_TEXT);
                float rowW = ROW_PADDING_X + ICON + ICON_PADDING + nameW + ICON_PADDING + durW + ROW_PADDING_X;
                w = Math.max(w, rowW * a);
                h += ROW_HEIGHT * a;
            }
        }
        h -= ROW_HEIGHT - ROW_TEXT - 3.5f * S;
        w = Math.max(w, MIN_W);
        float pillW = 55.0f * S;
        String pillText = "\u0417\u0435\u043b\u044c\u044f";
        float pillTextW = font.getWidth(pillText, PILL_TEXT);
        float pillIconW = pillIconSize = 8.0f * S;
        float pillX = x + (w - pillW) / 2.0f;
        RenderUtil.Render2D.glow(pillX, pillY, pillW, PILL_H, ColorUtil.getColor(0, 0.1f * alpha), PILL_RADIUS, 8.0f, 1.0f);
        RenderUtil.Blur.blur(pillX, pillY, pillW, PILL_H, alpha, PILL_RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(alpha * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.5f + chatBoost, 1.0f)));
        float pillContentX = pillX + (pillW - pillIconW - 3.0f * S - pillTextW) / 2.0f;
        ItemRender.drawItemWithContext(eventDisplay.getDrawContext(), new class_1799((class_1935)class_1802.field_8574), pillContentX, pillY + (PILL_H - pillIconSize) / 2.0f, pillIconSize / 16.0f, alpha);
        font.draw(pillText, pillContentX + pillIconW + 3.0f * S, pillY + (PILL_H - PILL_TEXT) / 2.0f, PILL_TEXT, ColorUtil.multAlpha(ColorUtil.getColor(240), alpha));
        RenderUtil.Render2D.glow(x, y, w, h - 0.5f, ColorUtil.replAlpha(ColorUtil.getColor(0), alpha * 0.1f), RADIUS, 12.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, w, h, alpha, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(alpha * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.5f + chatBoost, 1.0f)));
        float offsetY = y + rowPad;
        if (showPlaceholders) {
            for (int i = 0; i < 2; ++i) {
                int idx = (this.placeholderIndex + i) % PLACEHOLDERS.length;
                String pName = PLACEHOLDERS[idx][0];
                String pTime = PLACEHOLDERS[idx][1];
                String pTexId = PLACEHOLDERS[idx][2];
                class_2960 effectTex = class_2960.method_60655((String)"minecraft", (String)pTexId);
                Matrix3x2fStack matrices = eventDisplay.getDrawContext().method_51448();
                matrices.pushMatrix();
                float iconX = x + ROW_PADDING_X;
                float iconY = offsetY + (ROW_TEXT - ICON) * 0.3f;
                matrices.translate(iconX, iconY);
                matrices.scale(ICON / 12.0f, ICON / 12.0f);
                eventDisplay.getDrawContext().method_52707(class_10799.field_56883, effectTex, 0, 0, 12, 12, ColorUtil.getColor(255, alpha));
                matrices.popMatrix();
                font.draw(pName, x + ROW_PADDING_X + ICON + ICON_PADDING, offsetY, ROW_TEXT, ColorUtil.getColor(240, alpha));
                float timeWidth = font.getWidth(pTime, ROW_TEXT);
                float timeX = x + w - ROW_PADDING_X - timeWidth;
                font.draw(pTime, timeX, offsetY, ROW_TEXT, ColorUtil.getColor(200, alpha));
                offsetY += ROW_HEIGHT;
            }
        } else {
            for (EffectData data4 : this.sortedBuffer) {
                float a = data4.animation.get();
                if (a <= 0.01f && !data4.active) continue;
                boolean bad = data4.negative;
                int nameColor = bad ? ColorUtil.getColor(235, 70, 70, alpha * a) : ColorUtil.getColor(240, alpha * a);
                int timeColor = bad ? ColorUtil.getColor(200, 60, 60, alpha * a) : ColorUtil.getColor(200, alpha * a);
                class_2960 effectTex = data4.effectInstance.method_5579().method_40230().map(key -> key.method_29177().method_45138("mob_effect/")).orElse(class_2960.method_60656((String)"mob_effect/speed"));
                Matrix3x2fStack matrices = eventDisplay.getDrawContext().method_51448();
                matrices.pushMatrix();
                float iconX = x + ROW_PADDING_X;
                float iconY = offsetY + (ROW_TEXT - ICON) * 0.3f;
                matrices.translate(iconX, iconY);
                matrices.scale(ICON / 12.0f, ICON / 12.0f);
                eventDisplay.getDrawContext().method_52707(class_10799.field_56883, effectTex, 0, 0, 12, 12, ColorUtil.getColor(255, alpha * a));
                matrices.popMatrix();
                String effectname = this.label(data4);
                font.draw(effectname, x + ROW_PADDING_X + ICON + ICON_PADDING, offsetY, ROW_TEXT, nameColor);
                String key2 = data4.duration;
                float timeWidth = font.getWidth(key2, ROW_TEXT);
                float timeX = x + w - ROW_PADDING_X - timeWidth;
                this.drawDuration(font, data4, timeX, offsetY, ROW_TEXT, timeColor);
                offsetY += ROW_HEIGHT * a;
            }
            this.removeBuffer.forEach(this.displayedEffects::remove);
        }
        float totalW = Math.max(w, MIN_W);
        float totalX = Math.min(x, pillX);
        float totalRight = Math.max(x + totalW, pillX + pillW);
        float totalWidth = totalRight - totalX;
        dragSetting.size.set(totalWidth, h + PILL_H + PILL_GAP);
    }

    private void drawDuration(Font font, EffectData data, float x, float y, float size, int color) {
        float t = data.digitAnim.get();
        String now = data.duration;
        String was = data.prevDuration == null ? now : data.prevDuration;
        float cx = x;
        for (int i = 0; i < now.length(); ++i) {
            String old;
            String ch = now.substring(i, i + 1);
            int j = was.length() - (now.length() - i);
            String string = old = j >= 0 && j < was.length() ? was.substring(j, j + 1) : null;
            if (t >= 1.0f || ch.equals(old)) {
                font.draw(ch, cx, y, size, color);
            } else {
                font.draw(ch, cx, y + SCROLL_OFFSET_Y * (1.0f - t), size, ColorUtil.multAlpha(color, t));
                if (old != null) {
                    font.draw(old, cx, y - SCROLL_OFFSET_Y * t, size, ColorUtil.multAlpha(color, 1.0f - t));
                }
            }
            cx += font.getWidth(ch, size);
        }
    }

    private String label(EffectData data) {
        int lvl = data.effectInstance.method_5578() + 1;
        return lvl > 1 ? data.name + " " + lvl : data.name;
    }

    private String getDurationString(class_1293 effect) {
        if (effect.method_48559()) {
            return "\u221e";
        }
        int t = effect.method_5584() / 20;
        return String.format("%d:%02d", t / 60, t % 60);
    }

    @Environment(value=EnvType.CLIENT)
    private static class EffectData {
        String name;
        String duration;
        String prevDuration;
        final Animation digitAnim = new Animation();
        Animation animation;
        boolean active;
        boolean negative;
        int durationTicks;
        int lvl;
        class_1293 effectInstance;

        public EffectData(String name, String duration, int lvl, boolean negative, int durationTicks, class_1293 effectInstance) {
            this.name = name;
            this.duration = duration;
            this.prevDuration = duration;
            this.lvl = lvl;
            this.negative = negative;
            this.durationTicks = durationTicks;
            this.animation = new Animation();
            this.active = true;
            this.effectInstance = effectInstance;
        }
    }
}

