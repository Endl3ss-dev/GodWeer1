/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_437
 *  net.minecraft.class_5498
 */
package ru.decide.module.impl.render.emotions;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_437;
import net.minecraft.class_5498;
import ru.decide.module.impl.render.Emotions;
import ru.decide.module.impl.render.emotions.EmoteCatalog;
import ru.decide.module.impl.render.emotions.EmotePreviews;
import ru.decide.module.impl.render.emotions.EmoteRuntime;
import ru.decide.module.impl.render.emotions.EmotionSlots;
import ru.decide.screen.EmotionSettingsScreen;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.render.Draw;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public final class EmotionWheelManager {
    private static final int SLOT_COUNT = 7;
    private static final Font TITLE_FONT = Fonts.sf_bold;
    private static final Font SUB_FONT = Fonts.sf_regular;
    private static final Font HUB_FONT = Fonts.sf_medium;
    private static EmotionWheelManager instance;
    private boolean wheelOpen;
    private float wheelAnim;
    private long lastAnimMs;
    private final float[] slotHover = new float[7];
    private double cursorX;
    private double cursorY;
    private int stickySlot = -1;
    private EmoteCatalog.Entry hovered;
    private boolean perspectiveForced;
    private class_5498 savedPerspective = class_5498.field_26664;

    private EmotionWheelManager() {
    }

    public static EmotionWheelManager getInstance() {
        if (instance == null) {
            instance = new EmotionWheelManager();
        }
        return instance;
    }

    public boolean isWheelOpen() {
        return this.wheelOpen || this.wheelAnim > 0.01f;
    }

    public boolean isMouseCaptured() {
        return this.wheelOpen;
    }

    public void onKeyPress(int key) {
        Emotions module = Emotions.getInstance();
        if (module == null || !module.isEnabled()) {
            return;
        }
        int openKey = module.getWheelBind().getValue();
        if (openKey < 0 || key != openKey) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null) {
            this.openWheel();
        }
    }

    public void onKeyRelease(int key) {
        Emotions module = Emotions.getInstance();
        if (module == null || !module.isEnabled()) {
            return;
        }
        int openKey = module.getWheelBind().getValue();
        if (openKey < 0 || key != openKey) {
            return;
        }
        if (this.wheelOpen) {
            EmoteCatalog.Entry pick = this.hovered;
            this.closeWheel(false);
            if (pick != null) {
                this.playEmote(pick);
            }
        }
    }

    public void onMousePress(int button) {
        EmoteCatalog.Entry entry;
        if (!this.wheelOpen || button != 1) {
            return;
        }
        EmoteCatalog.Entry entry2 = entry = this.stickySlot >= 0 && this.stickySlot < 7 ? EmotionSlots.instance().get(this.stickySlot) : null;
        if (entry != null) {
            EmotionSlots.instance().clear(this.stickySlot);
            this.hovered = null;
        } else {
            int target = this.stickySlot >= 0 && this.stickySlot < 7 ? this.stickySlot : 0;
            this.closeWheel(true);
            class_310.method_1551().method_1507((class_437)new EmotionSettingsScreen(null, target, true));
        }
    }

    public void onMouseMove(double cursorDeltaX, double cursorDeltaY) {
        if (!this.wheelOpen) {
            return;
        }
        this.cursorX += cursorDeltaX * 0.85;
        this.cursorY += cursorDeltaY * 0.85;
        double max = 160.0;
        double len = Math.hypot(this.cursorX, this.cursorY);
        if (len > max) {
            this.cursorX = this.cursorX / len * max;
            this.cursorY = this.cursorY / len * max;
        }
    }

    public void onTick() {
        Emotions module = Emotions.getInstance();
        class_310 mc = class_310.method_1551();
        if (module == null || !module.isEnabled()) {
            this.stopEmote();
            this.closeWheel(true);
            return;
        }
        if (mc.field_1724 == null || mc.field_1687 == null) {
            this.stopEmote();
            this.closeWheel(true);
            return;
        }
        if (mc.field_1755 != null && this.wheelOpen) {
            this.closeWheel(true);
        }
        EmoteRuntime.cancelOnMove = module.getNoCancelOnMove().getValue() == false;
        EmoteRuntime.tick();
        if (this.perspectiveForced && !EmoteRuntime.playing()) {
            mc.field_1690.method_31043(this.savedPerspective);
            this.perspectiveForced = false;
        }
    }

    public void render() {
        Emotions module = Emotions.getInstance();
        if (module == null || !module.isEnabled() || class_310.method_1551().field_1687 == null) {
            return;
        }
        long now = System.currentTimeMillis();
        float dt = this.lastAnimMs == 0L ? 0.016f : class_3532.method_15363((float)((float)(now - this.lastAnimMs) / 1000.0f), (float)0.001f, (float)0.05f);
        this.lastAnimMs = now;
        float target = this.wheelOpen ? 1.0f : 0.0f;
        this.wheelAnim = EmotionWheelManager.approach(this.wheelAnim, target, dt, 10.0f);
        if (this.wheelAnim <= 0.001f && !this.wheelOpen) {
            return;
        }
        this.updateHoverStable();
        for (int i = 0; i < 7; ++i) {
            float slotTarget = this.stickySlot == i ? 1.0f : 0.0f;
            this.slotHover[i] = EmotionWheelManager.approach(this.slotHover[i], slotTarget, dt, 14.0f);
        }
        class_310 mc = class_310.method_1551();
        if (mc.method_22683() == null) {
            return;
        }
        EmotePreviews.beginFrame();
        this.drawWheel(mc);
    }

    public void closeWheel() {
        this.closeWheel(true);
    }

    public void closeWheel(boolean force) {
        this.wheelOpen = false;
        if (force) {
            this.hovered = null;
            this.stickySlot = -1;
        }
    }

    public void openFromModule() {
        Emotions module = Emotions.getInstance();
        class_310 mc = class_310.method_1551();
        if (module == null || !module.isEnabled()) {
            return;
        }
        if (mc.field_1724 == null || mc.field_1687 == null || mc.field_1755 != null) {
            return;
        }
        this.openWheel();
    }

    private void playEmote(EmoteCatalog.Entry entry) {
        class_310 mc = class_310.method_1551();
        if (entry == null || mc.field_1724 == null) {
            return;
        }
        Emotions module = Emotions.getInstance();
        if (module != null && module.getF5OnEmotion().getValue().booleanValue() && !this.perspectiveForced) {
            this.savedPerspective = mc.field_1690.method_31044();
            mc.field_1690.method_31043(class_5498.field_26666);
            this.perspectiveForced = true;
        }
        EmoteRuntime.play(entry);
    }

    public void stopEmote() {
        EmoteRuntime.stop();
        if (this.perspectiveForced) {
            class_310.method_1551().field_1690.method_31043(this.savedPerspective);
            this.perspectiveForced = false;
        }
    }

    private void openWheel() {
        this.wheelOpen = true;
        this.cursorX = 0.0;
        this.cursorY = 0.0;
        this.stickySlot = -1;
        this.hovered = null;
        this.lastAnimMs = System.currentTimeMillis();
    }

    private void updateHoverStable() {
        double dSticky;
        double dRaw;
        double len = Math.hypot(this.cursorX, this.cursorY);
        if (len < 28.0) {
            this.hovered = this.stickySlot >= 0 && this.stickySlot < 7 ? EmotionSlots.instance().get(this.stickySlot) : null;
            return;
        }
        double deg = Math.toDegrees(Math.atan2(this.cursorY, this.cursorX));
        int raw = EmotionWheelManager.nearestSlot(deg);
        if (this.stickySlot < 0) {
            this.stickySlot = raw;
        } else if (raw != this.stickySlot && (dRaw = EmotionWheelManager.angularDist(deg, EmotionWheelManager.slotAngle(raw))) + 8.0 < (dSticky = EmotionWheelManager.angularDist(deg, EmotionWheelManager.slotAngle(this.stickySlot)))) {
            this.stickySlot = raw;
        }
        this.hovered = this.stickySlot >= 0 && this.stickySlot < 7 ? EmotionSlots.instance().get(this.stickySlot) : null;
    }

    private static double slotAngle(int slot) {
        return -60.0 + (double)slot * 51.42857142857143;
    }

    private static int nearestSlot(double deg) {
        int best = 0;
        double bestDist = Double.MAX_VALUE;
        for (int i = 0; i < 7; ++i) {
            double d = EmotionWheelManager.angularDist(deg, EmotionWheelManager.slotAngle(i));
            if (!(d < bestDist)) continue;
            bestDist = d;
            best = i;
        }
        return best;
    }

    private void drawWheel(class_310 mc) {
        float guiW = mc.method_22683().method_4486();
        float guiH = mc.method_22683().method_4502();
        float anim = EmotionWheelManager.easeOutCubic(class_3532.method_15363((float)this.wheelAnim, (float)0.0f, (float)1.0f));
        Draw.rect(0.0f, 0.0f, guiW, guiH, ColorUtil.getColor(0, 0.5f * anim));
        float cx = guiW * 0.5f;
        float cy = guiH * 0.5f;
        float avail = Math.min(guiW, guiH) * 0.5f - 36.0f;
        float outerR = class_3532.method_15363((float)avail, (float)84.0f, (float)142.0f);
        float slotR = outerR * 0.25f;
        float ringR = outerR - slotR - 13.0f;
        float hubR = slotR * 1.32f;
        Draw.glow(cx - outerR, cy - outerR, outerR * 2.0f, outerR * 2.0f, ColorUtil.replAlpha(ColorUtil.client(), 0.22f * anim), outerR, 16.0f, 1.0f);
        Draw.glass(cx - outerR, cy - outerR, outerR * 2.0f, outerR * 2.0f, anim, outerR, outerR, outerR, outerR, ColorUtil.multAlpha(ColorUtil.multDark(ColorUtil.background(), 0.6f), anim));
        Draw.glassOutline(cx - outerR, cy - outerR, outerR * 2.0f, outerR * 2.0f, 0.6f, outerR, anim, 0.8f);
        Draw.rect(cx - hubR, cy - hubR, hubR * 2.0f, hubR * 2.0f, ColorUtil.multAlpha(ColorUtil.background(), 0.96f * anim), hubR);
        Draw.outline(cx - hubR, cy - hubR, hubR * 2.0f, hubR * 2.0f, 0.6f, ColorUtil.replAlpha(ColorUtil.client(), 0.55f * anim), hubR);
        String hubTitle = this.hovered != null ? this.hovered.name() : "\u042d\u043c\u043e\u0446\u0438\u0438";
        HUB_FONT.drawCentered(hubTitle, cx, cy - 6.0f, 12.5f, ColorUtil.getColor(255, anim));
        SUB_FONT.drawCentered(this.hovered != null ? "\u041e\u0442\u043f\u0443\u0441\u0442\u0438 \u2014 \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c" : "\u041d\u0430\u0432\u0435\u0434\u0438 \u043d\u0430 \u0441\u043b\u043e\u0442", cx, cy + 7.0f, 7.5f, ColorUtil.getColor(175, anim));
        for (int i = 0; i < 7; ++i) {
            int fill;
            float hover = this.slotHover[i];
            double ang = Math.toRadians(EmotionWheelManager.slotAngle(i));
            float sx = cx + (float)Math.cos(ang) * ringR;
            float sy = cy + (float)Math.sin(ang) * ringR;
            EmoteCatalog.Entry entry = EmotionSlots.instance().get(i);
            float r = slotR * (0.94f + 0.16f * hover) * (0.7f + 0.3f * anim);
            int n = fill = entry != null ? ColorUtil.overCol(ColorUtil.getColor(35, 0.85f), ColorUtil.client(), hover) : ColorUtil.getColor(22, 0.7f);
            if (hover > 0.02f) {
                Draw.glow(sx - r, sy - r, r * 2.0f, r * 2.0f, ColorUtil.replAlpha(ColorUtil.client(), 0.35f * anim * hover), r, 8.0f * hover + 2.0f, 1.0f);
            }
            Draw.rect(sx - r, sy - r, r * 2.0f, r * 2.0f, EmotionWheelManager.withAlpha(fill, anim), r);
            Draw.outline(sx - r, sy - r, r * 2.0f, r * 2.0f, 0.6f, ColorUtil.replAlpha(ColorUtil.client(), anim * (entry != null ? 0.45f + 0.55f * hover : 0.22f + 0.28f * hover)), r);
            if (entry != null) {
                this.drawPreview(entry, sx, sy, r, anim * (0.75f + 0.25f * hover));
                continue;
            }
            EmotionWheelManager.drawPlus(sx, sy, r * 0.6f, Math.max(1.2f, r * 0.1f), ColorUtil.getColor(150, anim * (0.5f + 0.5f * hover)));
        }
        float titleY = cy - outerR - 26.0f;
        TITLE_FONT.drawCentered("\u0412\u044b\u0431\u0435\u0440\u0438 \u043d\u0443\u0436\u043d\u0443\u044e \u044d\u043c\u043e\u0446\u0438\u044e", cx, titleY, 15.0f, ColorUtil.getColor(255, anim));
        SUB_FONT.drawCentered("\u041e\u0442\u043f\u0443\u0441\u0442\u0438 \u2014 \u043f\u0440\u0438\u043c\u0435\u043d\u0438\u0442\u044c \u00b7 \u041f\u041a\u041c \u043f\u043e \u0441\u043b\u043e\u0442\u0443 \u2014 \u0443\u0431\u0440\u0430\u0442\u044c / \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0438", cx, cy + outerR + 12.0f, 9.0f, ColorUtil.getColor(170, anim));
        float mx = (float)((double)cx + this.cursorX);
        float my = (float)((double)cy + this.cursorY);
        Draw.rect(mx - 4.0f, my - 4.0f, 8.0f, 8.0f, ColorUtil.getColor(255, 0.95f * anim), 4.0f);
        Draw.outline(mx - 4.0f, my - 4.0f, 8.0f, 8.0f, 0.8f, ColorUtil.replAlpha(ColorUtil.client(), anim), 4.0f);
        Draw.flush();
    }

    private void drawPreview(EmoteCatalog.Entry entry, float cx, float cy, float r, float alpha) {
        class_2960 preview = EmotePreviews.preview(entry.id());
        if (preview != null) {
            float h = r * 1.9f;
            float w = h * 0.8f;
            Draw.texture(preview, cx - w / 2.0f, cy - h / 2.0f, w, h, ColorUtil.getColor(255, alpha));
        } else {
            HUB_FONT.drawCentered(entry.name().isEmpty() ? "?" : entry.name().substring(0, 1).toUpperCase(), cx, cy - 5.0f, r * 0.9f, ColorUtil.getColor(230, alpha));
        }
    }

    private static void drawPlus(float cx, float cy, float size, float thickness, int color) {
        float half = size * 0.5f;
        Draw.rect(cx - half, cy - thickness * 0.5f, size, thickness, color, thickness * 0.5f);
        Draw.rect(cx - thickness * 0.5f, cy - half, thickness, size, color, thickness * 0.5f);
    }

    private static float approach(float current, float target, float dt, float speed) {
        float t = 1.0f - (float)Math.exp(-speed * dt);
        return current + (target - current) * t;
    }

    private static float easeOutCubic(float x) {
        float inv = 1.0f - x;
        return 1.0f - inv * inv * inv;
    }

    private static double angularDist(double a, double b) {
        double d = Math.abs(a - b) % 360.0;
        return d > 180.0 ? 360.0 - d : d;
    }

    private static int withAlpha(int argb, float alpha) {
        int baseA = argb >>> 24 & 0xFF;
        int a = baseA == 0 ? class_3532.method_15340((int)((int)(255.0f * class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f))), (int)0, (int)255) : class_3532.method_15340((int)((int)((float)baseA * class_3532.method_15363((float)alpha, (float)0.0f, (float)1.0f))), (int)0, (int)255);
        return a << 24 | argb & 0xFFFFFF;
    }
}

