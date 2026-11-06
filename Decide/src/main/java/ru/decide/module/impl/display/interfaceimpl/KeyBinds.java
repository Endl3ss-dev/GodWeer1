/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_408
 */
package ru.decide.module.impl.display.interfaceimpl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_408;
import ru.decide.Client;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.interfaceimpl.element;
import ru.decide.utils.animation.satoshi.Animation;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.Keyboard;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class KeyBinds
implements element {
    private static float S = 1.0f;
    private static float PILL_H = 14.0f * S;
    private static float PILL_GAP = 3.0f * S;
    private static float PILL_RADIUS = 7.0f * S;
    private static float PILL_TEXT = 6.5f * S;
    private static float MIN_W = 50.0f * S;
    private static float RADIUS = 5.0f * S;
    private static float ROW_TEXT = 6.5f * S;
    private static float ROW_HEIGHT = 14.0f * S;
    private static float ROW_PADDING_X = 5.0f * S;
    private static float ROW_START_Y = 5.0f * S;
    private static float ICON_PADDING = 4.0f * S;
    private static float ICON_OFFSET_Y = 0.9f * S;
    private final Animation animation1 = new EaseInOutQuad(300, 1.0);
    private static final String[][] PLACEHOLDERS = new String[][]{{"\u0410\u0442\u043c\u043e\u0441\u0444\u0435\u0440\u0430", "R", "Q"}, {"\u0421\u043f\u0440\u0438\u043d\u0442", "X", "Q"}, {"\u041f\u043e\u043b\u043d\u0430\u044f \u044f\u0440\u043a\u043e\u0441\u0442\u044c", "H", "Q"}, {"\u041a\u043b\u0438\u043a \u0413\u0423\u0418", "RIGHT_SHIFT", "H"}, {"\u0411\u0435\u0437 \u043f\u0430\u0434\u0435\u043d\u0438\u044f", "V", "Q"}, {"\u041f\u043e\u043b\u0451\u0442", "G", "L"}, {"\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c", "B", "L"}, {"\u041e\u0442\u0434\u0430\u0447\u0430", "J", "L"}, {"\u0410\u043d\u0442\u0438-\u0433\u043e\u043b\u043e\u0434", "N", "L"}, {"\u0411\u044b\u0441\u0442\u0440\u044b\u0439 \u0431\u043b\u043e\u043a", "F", "L"}};
    private int placeholderIndex = 0;
    private long placeholderLastTick = 0L;
    private static final long PLACEHOLDER_INTERVAL_TICKS = 50L;
    private final List<Module> modulesBuffer = new ArrayList<Module>();

    @Override
    public void onRender(DragSetting dragSetting, InterFace interFace) {
        float pillIconSize;
        float rowPad;
        boolean inChat;
        S = InterFace.getInstance().sizeHud.getValue().floatValue();
        PILL_H = 14.0f * S;
        PILL_GAP = 3.0f * S;
        PILL_RADIUS = 7.0f * S;
        PILL_TEXT = 6.5f * S;
        MIN_W = 50.0f * S;
        RADIUS = 5.0f * S;
        ROW_TEXT = 6.5f * S;
        ROW_HEIGHT = 14.0f * S;
        ROW_PADDING_X = 5.0f * S;
        ROW_START_Y = 5.0f * S;
        ICON_PADDING = 4.0f * S;
        ICON_OFFSET_Y = 0.9f * S;
        this.modulesBuffer.clear();
        for (Module m2 : Client.get().moduleManager().values()) {
            if (m2.getKey() <= 0 || m2.getName().equalsIgnoreCase("ClickGui") || !m2.isEnabled() && !(m2.getAnimation().getOutput() > 0.0f)) continue;
            this.modulesBuffer.add(m2);
        }
        this.modulesBuffer.sort(Comparator.comparingInt(m -> m.getName().length() + Keyboard.keyName(m.getKey()).length()).reversed());
        List<Module> modules = this.modulesBuffer;
        boolean isEmpty = modules.isEmpty();
        boolean showPlaceholders = inChat = KeyBinds.mc.field_1755 instanceof class_408;
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
        Font cat = Fonts.decide_2;
        float h = rowPad = 1.5f * S;
        float w = 0.0f;
        if (showPlaceholders) {
            long currentTick;
            long l = currentTick = KeyBinds.mc.field_1687 != null ? KeyBinds.mc.field_1687.method_75260() : 0L;
            if (currentTick - this.placeholderLastTick >= 50L) {
                this.placeholderLastTick = currentTick;
                this.placeholderIndex = (this.placeholderIndex + 1) % PLACEHOLDERS.length;
            }
            for (int i = 0; i < 2; ++i) {
                int idx = (this.placeholderIndex + i) % PLACEHOLDERS.length;
                String pName = PLACEHOLDERS[idx][0];
                String pKey = PLACEHOLDERS[idx][1];
                String pIcon = PLACEHOLDERS[idx][2];
                float rowW = ROW_PADDING_X + cat.getWidth(pIcon, ROW_TEXT) + ICON_PADDING + font.getWidth(pName, ROW_TEXT) + ICON_PADDING + font.getWidth(pKey, ROW_TEXT) + ROW_PADDING_X;
                w = Math.max(w, rowW);
                h += ROW_HEIGHT;
            }
        } else {
            for (Module m3 : modules) {
                m3.animation.setDirection(m3.isEnabled() ? Direction.FORWARDS : Direction.BACKWARDS);
                float mAnim = m3.getAnimation().getOutput();
                if (mAnim <= 0.0f) continue;
                String icon = m3.getCategory().getIcon();
                float rowW = ROW_PADDING_X + cat.getWidth(icon, ROW_TEXT) + ICON_PADDING + font.getWidth(m3.getBigName(), ROW_TEXT) + ICON_PADDING + font.getWidth(Keyboard.keyName(m3.getKey()), ROW_TEXT) + ROW_PADDING_X;
                w = Math.max(w, rowW * mAnim);
                h += ROW_HEIGHT * mAnim;
            }
        }
        h -= ROW_HEIGHT - ROW_TEXT - 3.5f * S;
        w = Math.max(w, MIN_W);
        String pillText = "\u0411\u0438\u043d\u0434\u044b";
        float pillTextW = font.getWidth(pillText, PILL_TEXT);
        float pillIconW = pillIconSize = 8.0f * S;
        float totalPillContentW2 = pillIconW + 3.0f * S + pillTextW + 8.0f * S;
        float pillW = Math.max(60.0f * S, totalPillContentW2);
        float pillX = x + (w - pillW) / 2.0f;
        RenderUtil.Render2D.glow(pillX, pillY, pillW, PILL_H, ColorUtil.getColor(0, 0.1f * alpha), PILL_RADIUS, 8.0f, 1.0f);
        float chatBoost = inChat ? 0.15f : 0.0f;
        RenderUtil.Blur.blur(pillX, pillY, pillW, PILL_H, alpha, PILL_RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(alpha * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.5f + chatBoost, 1.0f)));
        float pillContentX = pillX + (pillW - pillIconW - 3.0f * S - pillTextW) / 2.0f;
        try {
            RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/frame/binds.png"), pillContentX, pillY + (PILL_H - pillIconSize) / 2.0f, pillIconSize, pillIconSize, ColorUtil.replAlpha(ColorUtil.client(), alpha));
        }
        catch (Exception exception) {
            // empty catch block
        }
        font.draw(pillText, pillContentX + pillIconW + 3.0f * S, pillY + (PILL_H - PILL_TEXT) / 2.0f, PILL_TEXT, ColorUtil.multAlpha(ColorUtil.getColor(240), alpha));
        RenderUtil.Render2D.glow(x, y, w, h - 0.5f, ColorUtil.replAlpha(ColorUtil.getColor(0), alpha * 0.1f), RADIUS, 12.0f, 1.0f);
        RenderUtil.Blur.blur(x, y, w, h, alpha, RADIUS, ColorUtil.replAlpha(ColorUtil.background(), Math.min(alpha * InterFace.getInstance().alphaHUD.getValue().floatValue() + 0.5f + chatBoost, 1.0f)));
        float offsetY = y + rowPad;
        if (showPlaceholders) {
            for (int i = 0; i < 2; ++i) {
                int idx = (this.placeholderIndex + i) % PLACEHOLDERS.length;
                String pName = PLACEHOLDERS[idx][0];
                String pKey = PLACEHOLDERS[idx][1];
                String pIcon = PLACEHOLDERS[idx][2];
                float textX = x + ROW_PADDING_X;
                cat.draw(pIcon, textX, offsetY + ICON_OFFSET_Y, ROW_TEXT, ColorUtil.replAlpha(ColorUtil.client(), alpha));
                font.draw(pName, textX += cat.getWidth(pIcon, ROW_TEXT) + ICON_PADDING, offsetY, ROW_TEXT, ColorUtil.getColor(240, alpha));
                float keyWidth = font.getWidth(pKey, ROW_TEXT);
                font.draw(pKey, x + w - ROW_PADDING_X - keyWidth, offsetY, ROW_TEXT, ColorUtil.getColor(200, alpha));
                offsetY += ROW_HEIGHT;
            }
        } else {
            for (Module m4 : modules) {
                float mAnim = m4.getAnimation().getOutput();
                if (mAnim <= 0.0f) continue;
                String icon = m4.getCategory().getIcon();
                float textX = x + ROW_PADDING_X;
                cat.draw(icon, textX, offsetY + ICON_OFFSET_Y, ROW_TEXT, ColorUtil.replAlpha(ColorUtil.client(), alpha * mAnim));
                font.draw(m4.getBigName(), textX += cat.getWidth(icon, ROW_TEXT) + ICON_PADDING, offsetY, ROW_TEXT, ColorUtil.getColor(240, alpha * mAnim));
                String key = Keyboard.keyName(m4.getKey());
                float keyWidth = font.getWidth(key, ROW_TEXT);
                font.draw(key, x + w - ROW_PADDING_X - keyWidth, offsetY, ROW_TEXT, ColorUtil.getColor(200, alpha * mAnim));
                offsetY += ROW_HEIGHT * mAnim;
            }
        }
        float totalW = Math.max(w, MIN_W);
        float totalX = Math.min(x, pillX);
        float totalRight = Math.max(x + totalW, pillX + pillW);
        float totalWidth = totalRight - totalX;
        dragSetting.size.set(totalWidth, h + PILL_H + PILL_GAP);
    }
}

