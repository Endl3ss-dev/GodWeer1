/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1802
 *  net.minecraft.class_239$class_240
 *  net.minecraft.class_310
 *  net.minecraft.class_437
 */
package ru.decide.module.impl.render;

import java.awt.Color;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1802;
import net.minecraft.class_239;
import net.minecraft.class_310;
import net.minecraft.class_437;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.ButtonSetting;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.PixelGridSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.impl.render.emotions.EmotionWheelManager;
import ru.decide.screen.CrosshairEditor;
import ru.decide.screen.Menu;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.animation.Animation;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.other.GuiSounds;
import ru.decide.utils.other.Instance;
import ru.decide.utils.render.RenderUtil;

@ModuleInfo(name="Cross Hair", desc="\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439 \u043f\u0440\u0438\u0446\u0435\u043b", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class CrossHair
extends Module {
    public static final int GRID_SIZE = 21;
    public ButtonSetting openEditor = new ButtonSetting((Module)this, "\u0420\u0435\u0434\u0430\u043a\u0442\u043e\u0440 \u043f\u0440\u0438\u0446\u0435\u043b\u0430", () -> {
        class_437 patt0$temp = CrossHair.mc.field_1755;
        if (patt0$temp instanceof Menu) {
            Menu menu = (Menu)patt0$temp;
            menu.openCrosshairEditor();
        } else {
            GuiSounds.editor();
            Menu.returnToGlassAfterEditor = true;
            class_310.method_1551().method_1507(null);
            class_310.method_1551().method_63588(() -> {
                Menu menu = new Menu();
                class_310.method_1551().method_1507((class_437)menu);
                class_310.method_1551().method_63588(menu::openCrosshairEditor);
            });
        }
    }).setVisible(() -> this.isDrawing() && !this.isEditorOpen());
    public ModeSetting type = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c", "\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439", "\u0422\u043e\u0447\u043a\u0430", "\u041a\u0440\u0443\u0436\u043e\u0447\u0435\u043a", "\u041a\u0440\u0435\u0441\u0442");
    public final SliderSetting crossScale = new SliderSetting(this, "\u041c\u0430\u0441\u0448\u0442\u0430\u0431", 100.0f, 50.0f, 200.0f, 5.0f);
    public final SliderSetting crossOffsetX = new SliderSetting(this, "\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 X", 0.0f, -40.0f, 40.0f, 1.0f);
    public final SliderSetting crossOffsetY = new SliderSetting(this, "\u0421\u043c\u0435\u0449\u0435\u043d\u0438\u0435 Y", 0.0f, -40.0f, 40.0f, 1.0f);
    public final BooleanSetting dotEnabled = new BooleanSetting((Module)this, "\u0422\u043e\u0447\u043a\u0430 \u0432 \u0446\u0435\u043d\u0442\u0440\u0435", false);
    public final SliderSetting dotSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u0442\u043e\u0447\u043a\u0438", 1.5f, 0.5f, 5.0f, 0.5f).setVisible(() -> this.dotEnabled.getValue());
    public final ColorSetting dotColor = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0442\u043e\u0447\u043a\u0438", -1).setVisible(() -> this.dotEnabled.getValue());
    public final SliderSetting crossLength = new SliderSetting(this, "\u0414\u043b\u0438\u043d\u0430 \u043b\u0443\u0447\u0430", 4.0f, 1.0f, 12.0f, 1.0f).setVisible(() -> this.type.is("\u041a\u0440\u0435\u0441\u0442"));
    public final SliderSetting crossThickness = new SliderSetting(this, "\u0422\u043e\u043b\u0449\u0438\u043d\u0430", 1.0f, 0.5f, 4.0f, 0.5f).setVisible(() -> this.type.is("\u041a\u0440\u0435\u0441\u0442"));
    public final SliderSetting crossGap = new SliderSetting(this, "\u0417\u0430\u0437\u043e\u0440", 3.0f, 0.0f, 12.0f, 1.0f).setVisible(() -> this.type.is("\u041a\u0440\u0435\u0441\u0442"));
    public final ColorSetting outlineTint = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043e\u0431\u0432\u043e\u0434\u043a\u0438", -16777216).setVisible(this::isEditorSettingVisible);
    public final SliderSetting outlineThickness = new SliderSetting(this, "\u0422\u043e\u043b\u0449\u0438\u043d\u0430 \u043e\u0431\u0432\u043e\u0434\u043a\u0438", 1.0f, 0.5f, 3.0f, 0.5f).setVisible(this::isEditorSettingVisible);
    public final PixelGridSetting grid = new PixelGridSetting((Module)this, "\u0420\u0438\u0441\u0443\u043d\u043e\u043a", 21);
    public final SliderSetting pixelSize = new SliderSetting(this, "\u0420\u0430\u0437\u043c\u0435\u0440 \u043f\u0438\u043a\u0441\u0435\u043b\u044f", 1.0f, 0.5f, 4.0f, 0.5f).setVisible(this::isEditorSettingVisible);
    public final BooleanSetting pixelOutline = new BooleanSetting((Module)this, "\u041e\u0431\u0432\u043e\u0434\u043a\u0430 \u0440\u0438\u0441\u0443\u043d\u043a\u0430", true).setVisible(this::isEditorSettingVisible);
    public final ModeSetting pixelColorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0440\u0438\u0441\u0443\u043d\u043a\u0430", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439", "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442", "\u0420\u0430\u0434\u0443\u0433\u0430").setVisible(this::isEditorSettingVisible);
    public final SliderSetting rainbowSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0440\u0430\u0434\u0443\u0433\u0438", 1.0f, 0.1f, 4.0f, 0.1f).setVisible(() -> this.isEditorSettingVisible() && "\u0420\u0430\u0434\u0443\u0433\u0430".equalsIgnoreCase(this.pixelColorMode.getValue()));
    public final ColorSetting pixelTint = new ColorSetting((Module)this, "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442", -1).setVisible(() -> this.isEditorSettingVisible() && "\u0421\u0432\u043e\u0439".equalsIgnoreCase(this.pixelColorMode.getValue()));
    public final ModeSetting gradientMode = new ModeSetting((Module)this, "\u0420\u0435\u0436\u0438\u043c \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442\u0430", "\u0421\u0442\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439", "\u0410\u043d\u0438\u043c\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439").setVisible(() -> this.isEditorSettingVisible() && "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442".equalsIgnoreCase(this.pixelColorMode.getValue()));
    public final ColorSetting gradientColor1 = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442\u0430 1", -65536).setVisible(() -> this.isEditorSettingVisible() && "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442".equalsIgnoreCase(this.pixelColorMode.getValue()));
    public final ColorSetting gradientColor2 = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442\u0430 2", -16776961).setVisible(() -> this.isEditorSettingVisible() && "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442".equalsIgnoreCase(this.pixelColorMode.getValue()));
    public final SliderSetting gradientSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0433\u0440\u0430\u0434\u0438\u0435\u043d\u0442\u0430", 1.0f, 0.1f, 4.0f, 0.1f).setVisible(() -> this.isEditorSettingVisible() && "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442".equalsIgnoreCase(this.pixelColorMode.getValue()) && "\u0410\u043d\u0438\u043c\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439".equalsIgnoreCase(this.gradientMode.getValue()));
    public final BooleanSetting targetColorToggle = new BooleanSetting((Module)this, "\u0426\u0432\u0435\u0442 \u043f\u0440\u0438 \u043d\u0430\u0432\u0435\u0434\u0435\u043d\u0438\u0438", false).setVisible(this::isEditorSettingVisible);
    public final ModeSetting targetColorMode = new ModeSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438", "\u0422\u0435\u043c\u0430", "\u0421\u0432\u043e\u0439", "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442").setVisible(() -> this.isEditorSettingVisible() && this.targetColorToggle.getValue() != false);
    public final ColorSetting targetTint = new ColorSetting((Module)this, "\u0421\u0432\u043e\u0439 \u0446\u0432\u0435\u0442 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438", -65536).setVisible(() -> this.isEditorSettingVisible() && this.targetColorToggle.getValue() != false && "\u0421\u0432\u043e\u0439".equalsIgnoreCase(this.targetColorMode.getValue()));
    public final ModeSetting targetGradientMode = new ModeSetting((Module)this, "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438", "\u0421\u0442\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u0439", "\u0410\u043d\u0438\u043c\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439").setVisible(() -> this.isEditorSettingVisible() && this.targetColorToggle.getValue() != false && "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442".equalsIgnoreCase(this.targetColorMode.getValue()));
    public final ColorSetting targetGradient1 = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438 1", -65536).setVisible(() -> this.isEditorSettingVisible() && this.targetColorToggle.getValue() != false && "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442".equalsIgnoreCase(this.targetColorMode.getValue()));
    public final ColorSetting targetGradient2 = new ColorSetting((Module)this, "\u0426\u0432\u0435\u0442 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438 2", -256).setVisible(() -> this.isEditorSettingVisible() && this.targetColorToggle.getValue() != false && "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442".equalsIgnoreCase(this.targetColorMode.getValue()));
    public final SliderSetting targetGradientSpeed = new SliderSetting(this, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438", 1.0f, 0.1f, 4.0f, 0.1f).setVisible(() -> this.isEditorSettingVisible() && this.targetColorToggle.getValue() != false && "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442".equalsIgnoreCase(this.targetColorMode.getValue()) && "\u0410\u043d\u0438\u043c\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439".equalsIgnoreCase(this.targetGradientMode.getValue()));
    public final SliderSetting silaAnim = new SliderSetting(this, "\u0421\u0438\u043b\u0430 \u0430\u043d\u0438\u043c\u0430\u0446\u0438\u0438 \u043f\u0440\u0438 \u0443\u0434\u0430\u0440\u0435", 5.0f, 0.0f, 10.0f, 1.0f).setVisible(this::isEditorSettingVisible);
    public Animation animation = new Animation();

    public static CrossHair getInstance() {
        return Instance.get(CrossHair.class);
    }

    public boolean isDrawing() {
        return this.type.is("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439");
    }

    public boolean isEditorOpen() {
        return CrosshairEditor.getInstance() != null && CrosshairEditor.getInstance().isActive();
    }

    public boolean isEditorSettingVisible() {
        if (!this.isDrawing()) {
            return false;
        }
        boolean inNormalClickGui = CrossHair.mc.field_1755 instanceof Menu && !this.isEditorOpen();
        return !inNormalClickGui;
    }

    public float expandFor(float animValue) {
        return this.silaAnim.getValue().floatValue() * animValue;
    }

    @EventHandler
    public void onRender(EventDisplay eventDisplay) {
        if (CrossHair.mc.field_1755 != null) {
            return;
        }
        if (EmotionWheelManager.getInstance().isWheelOpen()) {
            return;
        }
        float targetScale = 2.0f;
        float currentScale = mc.method_22683().method_4495();
        float scaleFix = targetScale / currentScale;
        int screenWidth = (int)((float)mc.method_22683().method_4486() / scaleFix);
        int screenHeight = (int)((float)mc.method_22683().method_4502() / scaleFix);
        float x = (float)screenWidth / 2.0f + this.crossOffsetX.getValue().floatValue();
        float y = (float)screenHeight / 2.0f + this.crossOffsetY.getValue().floatValue();
        float scaleMul = Math.max(0.1f, this.crossScale.getValue().floatValue() / 100.0f);
        float cooldown = 1.0f - CrossHair.mc.field_1724.method_7261(0.0f);
        float bowExpand = 0.0f;
        if (CrossHair.mc.field_1724.method_6115() && CrossHair.mc.field_1724.method_6030().method_31574(class_1802.field_8102)) {
            bowExpand = (float)CrossHair.mc.field_1724.method_6048() / 20.0f;
        }
        if (this.type.is("\u041a\u0430\u0441\u0442\u043e\u043c\u043d\u044b\u0439")) {
            this.animation.update();
            this.animation.run(cooldown, 0.001f);
            this.drawPixelCrosshair(x, y, this.expandFor(this.animation.get()) * scaleMul + bowExpand * 3.0f, 1.0f, scaleMul);
            this.drawDot(x, y, 1.0f);
            return;
        }
        if (this.type.is("\u041a\u0440\u0443\u0436\u043e\u0447\u0435\u043a")) {
            this.animation.update();
            this.animation.run(cooldown, 0.01f);
            RenderUtil.Render2D.roundedCircleProgress(eventDisplay.getDrawContext(), x, y, 4.0f * scaleMul, 1.0f, this.animation.get(), ThemeColor.getHudColor(), ColorUtil.getColor(0), ColorUtil.getColor(0));
            this.drawDot(x, y, 1.0f);
            return;
        }
        if (this.type.is("\u041a\u0440\u0435\u0441\u0442")) {
            this.animation.update();
            this.animation.run(cooldown, 0.001f);
            this.drawCross(x, y, scaleMul, this.expandFor(this.animation.get()) * scaleMul + bowExpand * 3.0f, 1.0f);
            this.drawDot(x, y, 1.0f);
            return;
        }
        if (this.type.is("\u0422\u043e\u0447\u043a\u0430")) {
            float half = 1.5f * scaleMul;
            int white = ColorUtil.getColor(255);
            RenderUtil.Render2D.gradientRect(x - half, y - half, half * 2.0f, half * 2.0f, new int[]{white, white, white, white}, 4.0f);
            RenderUtil.Render2D.outline(x - half - 0.5f, y - half - 0.5f, half * 2.0f + 1.0f, half * 2.0f + 1.0f, 0.5f, ColorUtil.getColor(0), 4.0f);
            this.drawDot(x, y, 1.0f);
        }
    }

    private void drawDot(float x, float y, float alphaMul) {
        if (!this.dotEnabled.getValue().booleanValue() || alphaMul <= 0.01f) {
            return;
        }
        float half = Math.max(0.5f, this.dotSize.getValue().floatValue() / 2.0f);
        boolean target = CrossHair.mc.field_1765 != null && CrossHair.mc.field_1765.method_17783() == class_239.class_240.field_1331;
        int col = ColorUtil.replAlpha(target && this.targetColorToggle.getValue() != false ? this.resolvePixelColor(1.0f) : this.dotColor.getValue().intValue(), alphaMul);
        RenderUtil.Render2D.rect(x - half, y - half, half * 2.0f, half * 2.0f, col, 4.0f);
    }

    private void drawCross(float x, float y, float scaleMul, float expand, float alphaMul) {
        if (alphaMul <= 0.01f) {
            return;
        }
        float len = this.crossLength.getValue().floatValue() * scaleMul;
        float th = Math.max(0.5f, this.crossThickness.getValue().floatValue() * scaleMul);
        float gap = Math.max(0.0f, this.crossGap.getValue().floatValue() * scaleMul + expand);
        int fill = this.resolvePixelColor(1.0f);
        fill = ColorUtil.replAlpha(fill, alphaMul);
        int outline = ColorUtil.replAlpha((int)this.outlineTint.getValue(), alphaMul * 0.9f);
        float ot = Math.max(0.5f, this.outlineThickness.getValue().floatValue() * scaleMul);
        RenderUtil.Render2D.rect(x - th / 2.0f - ot, y - gap - len - ot, th + ot * 2.0f, len + ot * 2.0f, outline);
        RenderUtil.Render2D.rect(x - th / 2.0f - ot, y + gap - ot, th + ot * 2.0f, len + ot * 2.0f, outline);
        RenderUtil.Render2D.rect(x - gap - len - ot, y - th / 2.0f - ot, len + ot * 2.0f, th + ot * 2.0f, outline);
        RenderUtil.Render2D.rect(x + gap - ot, y - th / 2.0f - ot, len + ot * 2.0f, th + ot * 2.0f, outline);
        RenderUtil.Render2D.rect(x - th / 2.0f, y - gap - len, th, len, fill);
        RenderUtil.Render2D.rect(x - th / 2.0f, y + gap, th, len, fill);
        RenderUtil.Render2D.rect(x - gap - len, y - th / 2.0f, len, th, fill);
        RenderUtil.Render2D.rect(x + gap, y - th / 2.0f, len, th, fill);
    }

    public void drawPixelCrosshair(float centerX, float centerY, float expand, float alphaMul) {
        this.drawPixelCrosshair(centerX, centerY, expand, alphaMul, 1.0f);
    }

    public void drawPixelCrosshair(float centerX, float centerY, float expand, float alphaMul, float scaleMul) {
        if (alphaMul <= 0.01f) {
            return;
        }
        float ps = Math.max(0.5f, this.pixelSize.getValue().floatValue() * scaleMul);
        int c = this.grid.center();
        if (this.pixelOutline.getValue().booleanValue()) {
            float ot = Math.max(0.5f, this.outlineThickness.getValue().floatValue() * scaleMul);
            int outline = ColorUtil.replAlpha((int)this.outlineTint.getValue(), alphaMul * 0.9f);
            this.forEachPixel(centerX, centerY, ps, c, expand, (px, py, gx, gy) -> RenderUtil.Render2D.rect(px - ot, py - ot, ps + ot * 2.0f, ps + ot * 2.0f, outline));
        }
        this.forEachPixel(centerX, centerY, ps, c, expand, (px, py, gx, gy) -> {
            int fill = this.resolvePixelColor(gx, gy, alphaMul);
            RenderUtil.Render2D.rect(px, py, ps, ps, fill);
        });
    }

    private void forEachPixel(float centerX, float centerY, float ps, int c, float expand, PixelConsumer consumer) {
        for (int gy = 0; gy < this.grid.size; ++gy) {
            for (int gx = 0; gx < this.grid.size; ++gx) {
                if (!this.grid.get(gx, gy)) continue;
                int dx = gx - c;
                int dy = gy - c;
                float px = centerX + (float)dx * ps + Math.signum(dx) * expand - ps / 2.0f;
                float py = centerY + (float)dy * ps + Math.signum(dy) * expand - ps / 2.0f;
                consumer.accept(px, py, gx, gy);
            }
        }
    }

    public int resolvePixelColor(int gx, int gy, float alphaMul) {
        String mode;
        boolean isTarget = this.targetColorToggle.getValue() != false && CrossHair.mc.field_1765 != null && CrossHair.mc.field_1765.method_17783() == class_239.class_240.field_1331;
        int base = switch (mode = isTarget ? this.targetColorMode.getValue() : this.pixelColorMode.getValue()) {
            case "\u0421\u0432\u043e\u0439" -> isTarget ? this.targetTint.getValue() : this.pixelTint.getValue();
            case "\u0413\u0440\u0430\u0434\u0438\u0435\u043d\u0442" -> {
                float factor;
                String gMode;
                float pos = (float)(gx + gy) / (float)Math.max(1, this.grid.size * 2 - 2);
                String v1 = gMode = isTarget ? this.targetGradientMode.getValue() : this.gradientMode.getValue();
                if ("\u0410\u043d\u0438\u043c\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439".equalsIgnoreCase(gMode)) {
                    double speedVal = (isTarget ? this.targetGradientSpeed : this.gradientSpeed).getValue().doubleValue();
                    double time = (double)System.currentTimeMillis() * speedVal / 1000.0;
                    factor = (float)((Math.sin(((double)pos + time) * Math.PI * 2.0) + 1.0) / 2.0);
                } else {
                    factor = pos;
                }
                int c1 = isTarget ? this.targetGradient1.getValue() : this.gradientColor1.getValue();
                int c2 = isTarget ? this.targetGradient2.getValue() : this.gradientColor2.getValue();
                yield ColorUtil.interpolateColor(c1, c2, factor);
            }
            case "\u0420\u0430\u0434\u0443\u0433\u0430" -> {
                double speedVal = this.rainbowSpeed.getValue().doubleValue();
                float hue = (float)(((double)System.currentTimeMillis() * speedVal / 1000.0 / 4.0 + (double)((float)(gx + gy) * 0.02f)) % 1.0);
                yield Color.HSBtoRGB(hue, 0.85f, 1.0f);
            }
            default -> ColorUtil.getClientColor1(1);
        };
        return ColorUtil.replAlpha(base, alphaMul);
    }

    public int resolvePixelColor(float alphaMul) {
        return this.resolvePixelColor(this.grid.center(), this.grid.center(), alphaMul);
    }

    @FunctionalInterface
    @Environment(value=EnvType.CLIENT)
    private static interface PixelConsumer {
        public void accept(float var1, float var2, int var3, int var4);
    }
}

