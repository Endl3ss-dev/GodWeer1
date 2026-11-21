/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10042
 *  net.minecraft.class_1297
 *  net.minecraft.class_1304
 *  net.minecraft.class_1309
 *  net.minecraft.class_1657
 *  net.minecraft.class_1799
 *  net.minecraft.class_266
 *  net.minecraft.class_2960
 *  net.minecraft.class_3532
 *  net.minecraft.class_408
 *  net.minecraft.class_5250
 *  net.minecraft.class_8646
 *  net.minecraft.class_897
 *  net.minecraft.class_9013
 *  net.minecraft.class_9015
 *  net.minecraft.class_9022
 *  net.minecraft.class_9025
 *  net.minecraft.class_922
 *  org.joml.Matrix3x2fStack
 */
package ru.decide.module.impl.display.interfaceimpl;

import java.awt.Color;
import java.util.ArrayList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10042;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_266;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_408;
import net.minecraft.class_5250;
import net.minecraft.class_8646;
import net.minecraft.class_897;
import net.minecraft.class_9013;
import net.minecraft.class_9015;
import net.minecraft.class_9022;
import net.minecraft.class_9025;
import net.minecraft.class_922;
import org.joml.Matrix3x2fStack;
import ru.decide.Client;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.impl.display.InterFace;
import ru.decide.module.impl.display.interfaceimpl.element;
import ru.decide.module.impl.utils.NameProtect;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.animation.Easings;
import ru.decide.utils.animation.satoshi.Direction;
import ru.decide.utils.animation.satoshi.EaseInOutQuad;
import ru.decide.utils.colors.ColorFormatting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.ServerUtil;
import ru.decide.utils.render.ItemRender;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Fonts;
import ru.decide.utils.taskript.StopWatch;

@Environment(value=EnvType.CLIENT)
public class TargetHud
implements element {
    private static float S = 1.0f;
    private static float ITEM_SIZE = 8.0f * S;
    private static float ARMOR_PAD = 2.5f * S;
    private static float ARMOR_X = 10.0f * S;
    private static float ARMOR_Y = 12.0f * S;
    private static float W_BASE = 100.0f * S;
    private static float H_BASE = 30.0f * S;
    private static float RADIUS = 6.0f * S;
    private static float ANIM_OFFSET = 8.0f * S;
    private static float HEAD_CONTAINER_W = 30.0f * S;
    private static float HEAD_OFFSET_X = 4.0f * S;
    private static float HEAD_OFFSET_Y = 4.0f * S;
    private static float CONTENT_OFFSET_X = 35.0f * S;
    private static float BAR_OFFSET_X = 5.0f * S;
    private static float BAR_OFFSET_Y = 7.0f * S;
    private static float BAR_MARGIN_R = 10.0f * S;
    private static float BAR_HEIGHT = 3.0f * S;
    private static float NAME_Y = 4.5f * S;
    private static float HP_Y = 13.5f * S;
    private static float NAME_SIZE = 7.0f * S;
    private static float HP_SIZE = 6.0f * S;
    private static float GLOW_RADIUS = 12.0f * S;
    private static float BLUR_RADIUS = 20.0f * S;
    private static float FACE_SIZE = 22.0f * S;
    private final ru.decide.utils.animation.satoshi.Animation openAnimation = new EaseInOutQuad(200, 1.0);
    private final Animation settingsAnimation = new Animation();
    private final Animation animHP = new Animation();
    private final Animation animHpText = new Animation();
    private final StopWatch time = new StopWatch();
    private class_1309 target;
    private boolean inWorld;
    private boolean settingsOpen;
    private float lastX;
    private float lastY;
    private float lastW;
    private float lastH;
    private float popupX;
    private float popupY;
    private float popupW;
    private float popupH;
    private float hpColorX;
    private float hpColorY;
    private float hpColorW;
    private float hpColorH;
    private float view1X;
    private float view1Y;
    private float view1W;
    private float view1H;
    private float view2X;
    private float view2Y;
    private float view2W;
    private float view2H;

    @Override
    public void onRender(DragSetting drag, InterFace interFace) {
    }

    public void onRender(DragSetting drag, InterFace interFace, EventDisplay eventDisplay) {
        S = InterFace.getInstance().sizeHud.getValue().floatValue();
        ITEM_SIZE = 8.0f * S;
        ARMOR_PAD = 2.5f * S;
        ARMOR_X = 10.0f * S;
        ARMOR_Y = 12.0f * S;
        W_BASE = 100.0f * S;
        H_BASE = 30.0f * S;
        RADIUS = 6.0f * S;
        ANIM_OFFSET = 8.0f * S;
        HEAD_CONTAINER_W = 30.0f * S;
        HEAD_OFFSET_X = 4.0f * S;
        HEAD_OFFSET_Y = 4.0f * S;
        CONTENT_OFFSET_X = 35.0f * S;
        BAR_OFFSET_X = 5.0f * S;
        BAR_OFFSET_Y = 7.0f * S;
        BAR_MARGIN_R = 10.0f * S;
        BAR_HEIGHT = 3.0f * S;
        NAME_Y = 4.5f * S;
        HP_Y = 13.5f * S;
        NAME_SIZE = 7.0f * S;
        HP_SIZE = 6.0f * S;
        GLOW_RADIUS = 12.0f * S;
        BLUR_RADIUS = 20.0f * S;
        FACE_SIZE = 22.0f * S;
        if (TargetHud.mc.field_1724 == null || TargetHud.mc.field_1687 == null) {
            return;
        }
        if (TargetHud.mc.field_1692 != null && TargetHud.mc.field_1692.method_5709()) {
            this.target = TargetHud.mc.field_1692.method_72393();
            this.time.reset();
        }
        if (TargetHud.mc.field_1755 instanceof class_408) {
            this.target = TargetHud.mc.field_1724;
            this.time.reset();
        }
        this.inWorld = this.target != null && TargetHud.mc.field_1687 != null && !this.target.method_31481();
        boolean out = !this.inWorld || this.time.finished(400.0);
        this.openAnimation.setDirection(out ? Direction.BACKWARDS : Direction.FORWARDS);
        if ((double)this.openAnimation.getOutput() <= 0.0 || this.target == null) {
            drag.active = false;
            return;
        }
        drag.active = true;
        drag.size.set(W_BASE, H_BASE);
        float x = drag.position.x;
        float y = drag.position.y;
        float w = drag.size.x;
        float h = drag.size.y;
        float alpha = this.openAnimation.getOutput();
        this.lastX = x;
        this.lastY = y;
        this.lastW = w;
        this.lastH = h;
        float rad = RADIUS;
        float addXHEADP = -ANIM_OFFSET + ANIM_OFFSET * alpha;
        float addALL = ANIM_OFFSET - ANIM_OFFSET * alpha;
        RenderUtil.Render2D.glow(x + addXHEADP, y, HEAD_CONTAINER_W, h, ColorUtil.getColor(0, 0.1f * alpha), rad, GLOW_RADIUS, 1.0f);
        RenderUtil.Blur.glass(x + addXHEADP, y, HEAD_CONTAINER_W, h, alpha, rad, ColorUtil.replAlpha(ColorUtil.background(), Math.min(InterFace.getInstance().alphaHUD.getValue().floatValue() * alpha + 0.35f, 1.0f)), BLUR_RADIUS, 1.0f, 1.0f, 4.0f);
        RenderUtil.Render2D.glow(x + CONTENT_OFFSET_X + addALL, y, w - CONTENT_OFFSET_X, h, ColorUtil.getColor(0, 0.1f * alpha), rad, GLOW_RADIUS, 1.0f);
        RenderUtil.Blur.glass(x + CONTENT_OFFSET_X + addALL, y, w - CONTENT_OFFSET_X, h, alpha, rad, ColorUtil.replAlpha(ColorUtil.background(), Math.min(InterFace.getInstance().alphaHUD.getValue().floatValue() * alpha + 0.35f, 1.0f)), BLUR_RADIUS, 1.0f, 1.0f, 4.0f);
        this.drawFace(this.target, eventDisplay.getPartialTicks(), x + HEAD_OFFSET_X + addXHEADP, y + HEAD_OFFSET_Y, alpha);
        this.animHP.update();
        float barW = w - CONTENT_OFFSET_X - BAR_MARGIN_R;
        this.animHP.run((double)Math.round(this.getHealth(this.target) / this.target.method_6063() * barW), 0.5, Easings.BACK_OUT);
        this.animHpText.update();
        this.animHpText.run((double)this.getHealth(this.target), (double)0.15f, Easings.LINEAR);
        float barX = x + CONTENT_OFFSET_X + BAR_OFFSET_X + addALL;
        float barY = y + h - BAR_OFFSET_Y;
        int hpColor = ColorUtil.getClientColor(1);
        float displayHp = this.animHpText.get();
        String hpText = ServerUtil.isCopyTime() ? String.format("%.0f", Float.valueOf(displayHp)) : (this.target.method_5767() ? "null" : String.format("%.0f", Float.valueOf(displayHp)));
        String name = this.target.method_5477().getString().replace(TargetHud.mc.field_1724.method_5477().getString(), Client.get().moduleManager().get(NameProtect.class).isEnabled() ? "decidevisuals.fun" : TargetHud.mc.field_1724.method_5477().getString());
        RenderUtil.Render2D.rect(barX, barY, barW, BAR_HEIGHT, ColorUtil.multAlpha(ColorUtil.getColor(255, 0.1f), alpha), 2.0f);
        RenderUtil.Render2D.gradientRect(barX, barY, Math.min(barW, this.animHP.get()), BAR_HEIGHT, new int[]{ColorUtil.multDark(ColorUtil.replAlpha(hpColor, alpha), 0.5f), ColorUtil.replAlpha(hpColor, alpha), ColorUtil.replAlpha(hpColor, alpha), ColorUtil.multDark(ColorUtil.replAlpha(hpColor, alpha), 0.5f)}, 2.0f);
        Fonts.sf_regular.drawFadingText(name, x + addALL + CONTENT_OFFSET_X + BAR_OFFSET_X, y + NAME_Y, w - CONTENT_OFFSET_X - BAR_OFFSET_X, ColorUtil.getColor(255, alpha), NAME_SIZE);
        Fonts.sf_regular.draw("\u0417\u0434\u043e\u0440\u043e\u0432\u044c\u044f: " + ColorFormatting.getColor(ColorUtil.replAlpha(ColorUtil.client(), alpha)) + hpText, x + addALL + CONTENT_OFFSET_X + BAR_OFFSET_X, y + HP_Y, HP_SIZE, ColorUtil.getColor(255, alpha));
        this.renderArmor(eventDisplay, this.target, x + ARMOR_X, y - ARMOR_Y - ANIM_OFFSET + ANIM_OFFSET * alpha, alpha);
    }

    private void renderArmor(EventDisplay eventDisplay, class_1309 entity, float armorX, float armorY, float alpha) {
        ArrayList<class_1799> armorItems = new ArrayList<class_1799>();
        for (class_1304 slot : new class_1304[]{class_1304.field_6173, class_1304.field_6171, class_1304.field_6166, class_1304.field_6172, class_1304.field_6174, class_1304.field_6169}) {
            class_1799 stack = entity.method_6118(slot);
            if (stack.method_7960()) continue;
            armorItems.add(stack);
        }
        if (armorItems.isEmpty()) {
            return;
        }
        float bgX = armorX - ITEM_SIZE / 2.0f - ARMOR_PAD;
        float bgY = armorY - ITEM_SIZE / 2.0f - ARMOR_PAD + 0.5f * S;
        float bgW = (float)armorItems.size() * ITEM_SIZE + ARMOR_PAD * 2.0f;
        float bgH = ITEM_SIZE + ARMOR_PAD * 2.0f;
        RenderUtil.Render2D.glow(bgX, bgY, bgW, bgH - 0.5f * S, ColorUtil.getColor(0, 0.1f * alpha), 4.0f * S, GLOW_RADIUS, 1.0f);
        RenderUtil.Blur.glass(bgX, bgY, bgW, bgH, alpha, 4.0f * S, ColorUtil.replAlpha(ColorUtil.background(), Math.min(InterFace.getInstance().alphaHUD.getValue().floatValue() * alpha + 0.35f, 1.0f)), BLUR_RADIUS, 1.0f, 1.0f, 4.0f);
        for (class_1799 stack : armorItems) {
            Matrix3x2fStack matrix = eventDisplay.getDrawContext().method_51448();
            matrix.pushMatrix();
            matrix.translate(armorX, armorY);
            matrix.scale(0.5f * S, 0.5f * S);
            ItemRender.drawItemWithContext(eventDisplay.getDrawContext(), stack, -8.0f, -8.0f, alpha, alpha);
            matrix.popMatrix();
            armorX += ITEM_SIZE;
        }
    }

    private int getHealthColor(class_1309 entity) {
        float pct = class_3532.method_15363((float)(this.getHealth(entity) / Math.max(1.0f, entity.method_6063())), (float)0.0f, (float)1.0f);
        int red = ColorUtil.getColor(255, 75, 75, 255);
        int yellow = ColorUtil.getColor(255, 205, 85, 255);
        int green = ColorUtil.getColor(95, 220, 115, 255);
        return pct < 0.5f ? ColorUtil.overCol(red, yellow, pct * 2.0f) : ColorUtil.overCol(yellow, green, (pct - 0.5f) * 2.0f);
    }

    private float getHealth(class_1309 entity) {
        float hp = entity.method_6032() + entity.method_6067();
        if (entity instanceof class_1657) {
            class_266 scoreBoard;
            class_1657 player = (class_1657)entity;
            if (TargetHud.mc.field_1687 != null && (scoreBoard = TargetHud.mc.field_1687.method_8428().method_1189(class_8646.field_45158)) != null) {
                class_5250 text = class_9013.method_55398((class_9013)TargetHud.mc.field_1687.method_8428().method_55430((class_9015)player, scoreBoard), (class_9022)scoreBoard.method_55380((class_9022)class_9025.field_47566));
                try {
                    hp = Float.parseFloat(ColorUtil.removeFormatting(text.getString()));
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
        }
        return class_3532.method_15363((float)hp, (float)0.0f, (float)(entity.method_6063() + entity.method_6067()));
    }

    private void drawFace(class_1309 lastTarget, float lastTickDelta, float x, float y, float alpha) {
        try {
            class_897 baseRenderer = mc.method_1561().method_3953((class_1297)lastTarget);
            if (!(baseRenderer instanceof class_922)) {
                return;
            }
            class_922 renderer = (class_922)baseRenderer;
            class_10042 state = (class_10042)renderer.method_62425((class_1297)lastTarget, lastTickDelta);
            class_2960 textureLocation = renderer.method_3885(state);
            float hurtPercent = lastTarget.field_6235 > 0 ? (float)lastTarget.field_6235 / 10.0f : 0.0f;
            int r = 255;
            int g = (int)(255.0f * (1.0f - hurtPercent));
            int b = (int)(255.0f * (1.0f - hurtPercent));
            int color = new Color(r, g, b, (int)(255.0f * alpha)).getRGB();
            RenderUtil.Images.texture(textureLocation, x, y, FACE_SIZE, FACE_SIZE, 0.125f, 0.125f, 0.25f, 0.25f, color, 0.0f, 4.0f);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private boolean inRect(float mouseX, float mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
    }
}

