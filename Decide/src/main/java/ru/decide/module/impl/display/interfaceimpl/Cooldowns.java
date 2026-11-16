/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1792
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_2960
 *  net.minecraft.class_408
 */
package ru.decide.module.impl.display.interfaceimpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_2960;
import net.minecraft.class_408;
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
public class Cooldowns
implements IMinecraft {
    private static float S = 1.0f;
    private static float MIN_W = 50.0f * S;
    private static float RADIUS = 5.0f * S;
    private static float PILL_H = 14.0f * S;
    private static float PILL_GAP = 3.0f * S;
    private static float PILL_RADIUS = 7.0f * S;
    private static float PILL_TEXT = 6.5f * S;
    private static float ROW_HEIGHT = 14.0f * S;
    private static float ROW_TEXT = 6.5f * S;
    private static float ICON_SIZE = 7.0f * S;
    private static float ROW_PADDING_X = 5.0f * S;
    private static float ROW_START_Y = 5.0f * S;
    private static float ICON_PADDING = 4.0f * S;
    private final ru.decide.utils.animation.satoshi.Animation animation1 = new EaseInOutQuad(300, 1.0);
    private final ru.decide.utils.animation.satoshi.Animation animation2 = new EaseInOutQuad(300, 1.0);
    private final Map<String, CooldownData> displayedCooldowns = new LinkedHashMap<String, CooldownData>();
    private final List<CooldownData> sortedBuffer = new ArrayList<CooldownData>();
    private final List<String> removeBuffer = new ArrayList<String>();
    private static final class_1792[] TRACKED_ITEMS = new class_1792[]{class_1802.field_8634, class_1802.field_8463, class_1802.field_8367, class_1802.field_8233, class_1802.field_8551, class_1802.field_8255};
    private static final Object[][] PLACEHOLDERS = new Object[][]{{"\u042d\u043d\u0434\u0435\u0440 \u0436\u0435\u043c\u0447\u0443\u0433", "0:03", class_1802.field_8634}, {"\u0417\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", "0:05", class_1802.field_8463}, {"\u0417\u0430\u0447\u0430\u0440. \u0437\u043e\u043b\u043e\u0442\u043e\u0435 \u044f\u0431\u043b\u043e\u043a\u043e", "0:10", class_1802.field_8367}, {"\u041f\u043b\u043e\u0434 \u0445\u043e\u0440\u0443\u0441\u0430", "0:02", class_1802.field_8233}, {"\u0429\u0438\u0442", "0:10", class_1802.field_8255}, {"\u0421\u0443\u0448. \u043b\u0430\u043c\u0438\u043d\u0430\u0440\u0438\u044f", "0:01", class_1802.field_8551}};
    private int placeholderIndex = 0;
    private long placeholderLastTick = 0L;
    private static final long PLACEHOLDER_INTERVAL_TICKS = 40L;
    private final Map<String, Long> cooldownStartTick = new LinkedHashMap<String, Long>();

    public void onRender(DragSetting dragSetting, InterFace interFace, EventDisplay eventDisplay) {
        float rowPad;
        float pillIconSize;
        boolean inChat;
        S = InterFace.getInstance().sizeHud.getValue().floatValue();
        MIN_W = 50.0f * S;
        RADIUS = 5.0f * S;
        PILL_H = 14.0f * S;
        PILL_GAP = 3.0f * S;
        PILL_RADIUS = 7.0f * S;
        PILL_TEXT = 6.5f * S;
        ROW_HEIGHT = 14.0f * S;
        ROW_TEXT = 6.5f * S;
        ICON_SIZE = 7.0f * S;
        ROW_PADDING_X = 5.0f * S;
        ROW_START_Y = 5.0f * S;
        ICON_PADDING = 4.0f * S;
        float chatBoost = Cooldowns.mc.field_1755 instanceof class_408 ? 0.15f : 0.0f;
        this.displayedCooldowns.values().forEach(data -> {
            data.active = false;
        });
        if (Cooldowns.mc.field_1724 != null) {
            long currentTick = Cooldowns.mc.field_1687 != null ? Cooldowns.mc.field_1687.method_75260() : 0L;
            for (class_1792 item : TRACKED_ITEMS) {
                class_1799 stack = new class_1799((class_1935)item);
                String name = item.method_63680().getString();
                if (Cooldowns.mc.field_1724.method_7357().method_7904(stack)) {
                    String timeStr;
                    long startTick;
                    long elapsed;
                    float progress = Cooldowns.mc.field_1724.method_7357().method_7905(stack, 1.0f);
                    if (!(progress > 0.0f)) continue;
                    if (!this.cooldownStartTick.containsKey(name)) {
                        this.cooldownStartTick.put(name, currentTick);
                    }
                    if ((elapsed = currentTick - (startTick = this.cooldownStartTick.get(name).longValue())) > 0L && progress < 1.0f) {
                        long totalTicks = Math.round((float)elapsed / (1.0f - progress));
                        long remaining = Math.max(0L, totalTicks - elapsed);
                        int secs = (int)Math.round((double)remaining / 20.0);
                        timeStr = String.format("%d:%02d", secs / 60, secs % 60);
                    } else {
                        timeStr = "0:01";
                    }
                    CooldownData data2 = this.displayedCooldowns.computeIfAbsent(name, k -> new CooldownData(name, timeStr, stack));
                    data2.time = timeStr;
                    data2.progress = progress;
                    data2.active = true;
                    continue;
                }
                this.cooldownStartTick.remove(name);
            }
        }
        boolean isEmpty = this.displayedCooldowns.isEmpty();
        boolean showPlaceholders = inChat = Cooldowns.mc.field_1755 instanceof class_408;
        float x = dragSetting.position.x;
        float pillY = dragSetting.position.y;
        float y = pillY + PILL_H + PILL_GAP;
        boolean closeCondition = isEmpty && !inChat;
        this.animation1.setDirection(closeCondition ? Direction.BACKWARDS : Direction.FORWARDS);
        this.animation2.setDirection(inChat && isEmpty ? Direction.FORWARDS : Direction.BACKWARDS);
        dragSetting.active = !closeCondition;
        float alpha = this.animation1.getOutput();
        float alpha2 = this.animation2.getOutput();
        Font font = Fonts.sf_regular;
        float pillW = 60.0f * S;
        String pillText = "\u041a\u0443\u043b\u0434\u0430\u0443\u043d\u044b";
        float pillTextW = font.getWidth(pillText, PILL_TEXT);
        float pillIconW = pillIconSize = 8.0f * S;
        float totalPillW2 = pillIconW + 3.0f * S + pillTextW + 8.0f * S;
        pillW = Math.max(pillW, totalPillW2);
        float h = rowPad = 1.5f * S;
        float w = 0.0f;
        if (showPlaceholders) {
            long currentTick;
            long l = currentTick = Cooldowns.mc.field_1687 != null ? Cooldowns.mc.field_1687.method_75260() : 0L;
            if (currentTick - this.placeholderLastTick >= 40L) {
                this.placeholderLastTick = currentTick;
                this.placeholderIndex = (this.placeholderIndex + 1) % PLACEHOLDERS.length;
            }
            for (int i = 0; i < 2; ++i) {
                int idx = (this.placeholderIndex + i) % PLACEHOLDERS.length;
                String pName = (String)PLACEHOLDERS[idx][0];
                String pTime = (String)PLACEHOLDERS[idx][1];
                float rowW = ROW_PADDING_X + ICON_SIZE + ICON_PADDING + font.getWidth(pName, ROW_TEXT) + ICON_PADDING + font.getWidth(pTime, ROW_TEXT) + ROW_PADDING_X;
                w = Math.max(w, rowW);
                h += ROW_HEIGHT;
            }
        } else {
            this.sortedBuffer.clear();
            this.sortedBuffer.addAll(this.displayedCooldowns.values());
            for (CooldownData data3 : this.sortedBuffer) {
                data3.animation.update();
                data3.animation.run(data3.active ? 1.0 : 0.0, (double)0.12f, Easings.QUAD_OUT);
                float a = data3.animation.get();
                if (a <= 0.01f && !data3.active) continue;
                float rowW = ROW_PADDING_X + ICON_SIZE + ICON_PADDING + font.getWidth(data3.name, ROW_TEXT) + ICON_PADDING + font.getWidth(data3.time, ROW_TEXT) + ROW_PADDING_X;
                w = Math.max(w, rowW * a);
                h += ROW_HEIGHT * a;
            }
        }
        h -= ROW_HEIGHT - ROW_TEXT - 3.5f * S;
        w = Math.max(w, MIN_W);
        float pillX = x + (w - pillW) / 2.0f;
        float totalW = Math.max(w, MIN_W);
        float totalX = Math.min(x, pillX);
        float totalRight = Math.max(x + totalW, pillX + pillW);
        float totalWidth = totalRight - totalX;
        dragSetting.size.set(totalWidth, h + PILL_H + PILL_GAP);
        if (closeCondition && alpha == 0.0f && alpha2 == 0.0f) {
            return;
        }
        float renderAlpha = Math.max(alpha, alpha2);
        RenderUtil.Render2D.glow(pillX, pillY, pillW, PILL_H, ColorUtil.getColor(0, 0.1f * renderAlpha), PILL_RADIUS, 8.0f, 1.0f);
        RenderUtil.Blur.blur(pillX, pillY, pillW, PILL_H, renderAlpha, PILL_RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(renderAlpha * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.5f + chatBoost, 1.0f)));
        float pillContentX = pillX + (pillW - pillIconW - 3.0f * S - pillTextW) / 2.0f;
        try {
            RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/cooldown.png"), pillContentX, pillY + (PILL_H - pillIconSize) / 2.0f, pillIconSize, pillIconSize, ColorUtil.replAlpha(ColorUtil.client(), renderAlpha));
        }
        catch (Exception exception) {
            // empty catch block
        }
        font.draw(pillText, pillContentX + pillIconW + 3.0f * S, pillY + (PILL_H - PILL_TEXT) / 2.0f, PILL_TEXT, ColorUtil.multAlpha(ColorUtil.getColor(240), renderAlpha));
        RenderUtil.Render2D.glow(x, y, w, h - 0.5f, ColorUtil.getColor(0, 0.1f * renderAlpha), RADIUS, 8.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, w, h, renderAlpha, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(renderAlpha * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.5f + chatBoost, 1.0f)));
        float offsetY = y + rowPad;
        if (showPlaceholders) {
            for (int i = 0; i < 2; ++i) {
                int idx = (this.placeholderIndex + i) % PLACEHOLDERS.length;
                String pName = (String)PLACEHOLDERS[idx][0];
                String pTime = (String)PLACEHOLDERS[idx][1];
                class_1792 pItem = (class_1792)PLACEHOLDERS[idx][2];
                float iconX = x + ROW_PADDING_X;
                float iconY = offsetY + (ROW_TEXT - ICON_SIZE) * 0.3f;
                ItemRender.drawItemWithContext(eventDisplay.getDrawContext(), new class_1799((class_1935)pItem), iconX, iconY, ICON_SIZE / 16.0f, alpha);
                float textX = iconX + ICON_SIZE + ICON_PADDING;
                font.draw(pName, textX, offsetY, ROW_TEXT, ColorUtil.getColor(240, alpha));
                float timeW = font.getWidth(pTime, ROW_TEXT);
                font.draw(pTime, x + w - ROW_PADDING_X - timeW, offsetY, ROW_TEXT, ColorUtil.getColor(200, alpha));
                offsetY += ROW_HEIGHT;
            }
        } else {
            this.sortedBuffer.clear();
            this.sortedBuffer.addAll(this.displayedCooldowns.values());
            this.removeBuffer.clear();
            for (CooldownData data4 : this.sortedBuffer) {
                float a = data4.animation.get();
                if (a <= 0.01f && !data4.active) {
                    this.removeBuffer.add(data4.name);
                    continue;
                }
                float iconX = x + ROW_PADDING_X;
                float iconY = offsetY + (ROW_TEXT - ICON_SIZE) * 0.3f;
                ItemRender.drawItemWithContext(eventDisplay.getDrawContext(), data4.itemStack, iconX, iconY, ICON_SIZE / 16.0f, a * alpha);
                float textX = iconX + ICON_SIZE + ICON_PADDING;
                font.draw(data4.name, textX, offsetY, ROW_TEXT, ColorUtil.getColor(240, alpha * a));
                float timeW = font.getWidth(data4.time, ROW_TEXT);
                font.draw(data4.time, x + w - ROW_PADDING_X - timeW, offsetY, ROW_TEXT, ColorUtil.getColor(200, alpha * a));
                offsetY += ROW_HEIGHT * a;
            }
            this.removeBuffer.forEach(this.displayedCooldowns::remove);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static class CooldownData {
        final String name;
        final class_1799 itemStack;
        String time;
        float progress;
        boolean active;
        final Animation animation = new Animation();

        CooldownData(String name, String time, class_1799 itemStack) {
            this.name = name;
            this.time = time;
            this.itemStack = itemStack;
            this.active = true;
        }
    }
}

