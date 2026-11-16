/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2f
 *  org.joml.Vector2fc
 */
package ru.decide.ui.lv.settings;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.ui.fonts.FontsLV;
import ru.decide.ui.lv.settings.LvRenderHelper;
import ru.decide.ui.lv.settings.SettingWidget;
import ru.decide.ui.theme.ThemeManager;
import ru.decide.ui.util.Position;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.render.Draw;

@Environment(value=EnvType.CLIENT)
public class LvDragSetting
extends SettingWidget {
    public static final float ROW_H = 16.0f;
    public static final float BAR_H = 3.0f;
    public static final float FIRST_BAR_TOP = 14.0f;
    public static final float SECOND_BAR_TOP = 24.0f;
    public static final float AXIS_LABEL_W = 13.0f;
    private final DragSetting backend;
    private final Vector2f defaultValue = new Vector2f();
    private int axis;
    private final float[] visValue = new float[2];
    private long lastNs = System.nanoTime();

    public LvDragSetting(DragSetting dragSetting) {
        this.backend = dragSetting;
        Vector2f value = dragSetting.getValue();
        this.defaultValue.set((Vector2fc)value);
        this.visValue[0] = value.x;
        this.visValue[1] = value.y;
    }

    @Override
    public String name() {
        return this.backend.getName();
    }

    @Override
    public float height() {
        return 34.0f;
    }

    private static float clamp01(float f) {
        return f < 0.0f ? 0.0f : (f > 1.0f ? 1.0f : f);
    }

    private static int rgba(int n, int n2, int n3, float f) {
        int n4 = Math.max(0, Math.min(255, Math.round(f)));
        return n4 <= 0 ? 0 : new Color(n, n2, n3, n4).getRGB();
    }

    @Override
    public void render(float f, float f2, float f3, float f4) {
        long l = System.nanoTime();
        float dt = Math.min(0.1f, (float)(l - this.lastNs) / 1.0E9f);
        this.lastNs = l;
        Vector2f value = this.backend.getValue();
        if (this.axis == 1) {
            value.x = this.applyAxis(1, f, f3, Position.mouseX());
        } else if (this.axis == 2) {
            value.y = this.applyAxis(2, f, f3, Position.mouseX());
        }
        if (this.axis != 0) {
            this.backend.targetPosition.set((Vector2fc)value);
        }
        float maxX = Math.max(1.0f, Position.screenWidth());
        float maxY = Math.max(1.0f, Position.screenHeight());
        float targetX = LvDragSetting.clamp01((value.x - 0.0f) / maxX);
        float targetY = LvDragSetting.clamp01((value.y - 0.0f) / maxY);
        float lerp = 1.0f - (float)Math.exp(-dt * 18.0f);
        this.visValue[0] = this.visValue[0] + (targetX - this.visValue[0]) * lerp;
        this.visValue[1] = this.visValue[1] + (targetY - this.visValue[1]) * lerp;
        if (Math.abs(targetX - this.visValue[0]) < 0.002f) {
            this.visValue[0] = targetX;
        }
        if (Math.abs(targetY - this.visValue[1]) < 0.002f) {
            this.visValue[1] = targetY;
        }
        String string = "X: " + Math.round(value.x) + "  Y: " + Math.round(value.y);
        float tw = FontsLV.MONTSERRAT_MEDIUM.width(string, 6.0f);
        LvRenderHelper.drawName(this.backend.getName(), f, f2, f3 - tw - 12.0f, f4);
        FontsLV.MONTSERRAT_MEDIUM.draw(string, f + f3 - tw, f2 + 5.0f, 6.0f, LvDragSetting.rgba(255, 255, 255, 230.0f * f4));
        this.renderBar(f, f2, f3, f4, "X", this.visValue[0], value.x, 14.0f);
        this.renderBar(f, f2, f3, f4, "Y", this.visValue[1], value.y, 24.0f);
    }

    private void renderBar(float f, float f2, float f3, float f4, String label, float progress, float value, float top) {
        float barX = f + 6.0f;
        float barW = f3 - 12.0f - 13.0f;
        float barY = f2 + top;
        FontsLV.MONTSERRAT_MEDIUM.draw(label, barX, barY - 2.0f, 5.0f, ThemeManager.accentSoft(200.0f * f4));
        float trackX = barX + 13.0f;
        float trackW = Math.max(4.0f, barW);
        Draw.rect(trackX, barY, trackW, 3.0f, LvDragSetting.rgba(16, 16, 16, 64.0f * f4), 1.5f);
        float fill = trackW * LvDragSetting.clamp01(progress);
        if (fill > 0.6f) {
            int n = ThemeManager.gradientA(215.0f * f4);
            int n2 = ThemeManager.gradientB(215.0f * f4);
            Draw.gradientRect(trackX, barY, fill, 3.0f, new int[]{n, n2, n2, n}, 1.5f);
        }
        float knobX = trackX + trackW * LvDragSetting.clamp01(progress) - 3.0f;
        float knobY = barY + 1.5f - 3.0f;
        Draw.rect(knobX, knobY, 6.0f, 6.0f, LvDragSetting.rgba(255, 255, 255, 245.0f * f4), 2.0f);
        String v = String.valueOf(Math.round(value));
        float vw = FontsLV.MONTSERRAT_MEDIUM.width(v, 5.0f);
        FontsLV.MONTSERRAT_MEDIUM.draw(v, trackX + trackW - vw, barY - 2.0f, 5.0f, LvDragSetting.rgba(255, 255, 255, 110.0f * f4));
    }

    private float applyAxis(int id, float f, float f3, float mouseX) {
        float trackX = f + 6.0f + 13.0f;
        float trackW = Math.max(4.0f, f3 - 12.0f - 13.0f);
        float progress = LvDragSetting.clamp01((mouseX - trackX) / trackW);
        return id == 1 ? progress * Position.screenWidth() : progress * Position.screenHeight();
    }

    @Override
    public boolean isVisible() {
        return this.backend.getVisible().get();
    }

    @Override
    public boolean click(float f, float f2, float f3, float f4, float f5) {
        int id = this.hitBar(f, f2, f3, f5);
        if (id == 0) {
            return false;
        }
        this.axis = id;
        GuiSounds.sliderGrab();
        Vector2f value = this.backend.getValue();
        if (id == 1) {
            value.x = this.applyAxis(1, f, f3, f4);
        } else {
            value.y = this.applyAxis(2, f, f3, f4);
        }
        this.backend.targetPosition.set((Vector2fc)value);
        return true;
    }

    @Override
    public boolean middleClick(float f, float f2, float f3, float f4, float f5) {
        int id = this.hitBar(f, f2, f3, f5);
        if (id == 0) {
            return false;
        }
        this.axis = 0;
        Vector2f value = this.backend.getValue();
        if (id == 1) {
            value.x = this.defaultValue.x;
        } else {
            value.y = this.defaultValue.y;
        }
        this.backend.targetPosition.set((Vector2fc)value);
        GuiSounds.sliderGrab();
        return true;
    }

    @Override
    public void releaseDrag() {
        if (this.axis != 0) {
            this.axis = 0;
            GuiSounds.sliderRelease();
        }
    }

    private int hitBar(float f, float f2, float f3, float mouseY) {
        float barX = f + 6.0f + 13.0f;
        float barW = Math.max(4.0f, f3 - 12.0f - 13.0f);
        if (mouseY >= f2 + 14.0f - 4.0f && mouseY <= f2 + 14.0f + 3.0f + 4.0f) {
            return 1;
        }
        if (mouseY >= f2 + 24.0f - 4.0f && mouseY <= f2 + 24.0f + 3.0f + 4.0f) {
            return 2;
        }
        return 0;
    }

    @Override
    public float preferredWidth() {
        return 6.0f + FontsLV.MONTSERRAT_MEDIUM.width(this.backend.getName(), 6.5f) + 6.0f + 70.0f + 8.0f;
    }
}

