package swerve.ui.hud;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.modules.Module;
import swerve.modules.api.setting.BindSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ListSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.api.setting.Setting;
import swerve.modules.api.setting.TextSetting;
import swerve.ui.newgui.GuiBlur;
import swerve.ui.newgui.GuiFonts;
import swerve.ui.newgui.GuiRender;
import swerve.ui.newgui.GuiStyle;
import swerve.ui.newgui.GuiTheme;
import swerve.ui.newgui.Mathf;
import swerve.utils.color.ColorUtils;
import swerve.utils.input.KeyBoardUtils;
import swerve.utils.render.ScissorUtils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import org.lwjgl.glfw.GLFW;

import java.util.List;

// Меню настроек HUD-элемента: открывается ПКМ по элементу (в чате или любом открытом экране).
// Стиль повторяет newgui: «стеклянная» панель, MSDF-шрифт, анимации Mathf.fast/GuiStyle.anim.
// Интерактивны Boolean (тумблер), Float (слайдер) и Mode (переключатель «‹ значение ›»);
// List/Bind/Text показаны строкой только для чтения — их правка остаётся в ClickGui.
public final class HudSettingsScreen extends Screen {

    private static final float PANEL_WIDTH = 152.0f;
    private static final float PANEL_RADIUS = 8.0f;
    private static final float SIDE_PADDING = 8.0f;
    private static final float HEADER_HEIGHT = 12.5f;
    private static final float BOTTOM_PADDING = 6.0f;
    private static final float ROW_GAP = 1.0f;
    private static final float ROW_RADIUS = 4.0f;
    private static final float BOOLEAN_HEIGHT = 11.0f;
    private static final float SLIDER_HEIGHT = 17.0f;
    private static final float MODE_HEIGHT = 13.0f;
    private static final float PLAIN_HEIGHT = 10.0f;
    private static final float LABEL_SIZE = 7.0f;
    private static final float HINT_SIZE = 6.0f;
    private static final float TOGGLE_WIDTH = 17.0f;
    private static final float TOGGLE_HEIGHT = 9.0f;
    private static final float TOGGLE_KNOB = 6.5f;
    private static final float ARROW_SIZE = 9.0f;
    private static final float TRACK_HEIGHT = 3.5f;
    private static final float KNOB_WIDTH = 5.0f;
    private static final float KNOB_HEIGHT = 6.5f;
    private static final float SCROLL_STEP = 14.0f;
    private static final int SCRIM_ALPHA = 115;

    // [+] «Таблетка» переноса: за неё панель тащат, во время переноса виден только её контур.
    private static final float PILL_WIDTH = 30.0f;
    private static final float PILL_HEIGHT = 4.5f;
    private static final float PILL_Y = 4.0f;
    private static final float PILL_DOT = 1.6f;
    private static final float PANEL_MARGIN = 4.0f;
    private static final float PANEL_STIFFNESS = 900.0f;
    private static final float PANEL_DAMPING = 54.0f;
    private static final float PANEL_MAX_SPEED = 6_000.0f;
    private static final float DIM_ALPHA = 0.35f;
    private static final float GHOST_OUTLINE_WIDTH = 1.2f;
    private static final float SPAWN_SCALE = 0.94f;
    private static final float MAX_FRAME_DELTA = 0.05f;
    private static final float MAX_SUB_STEP = 0.008f;
    private static final int MAX_SUB_STEPS = 6;

    private static final int WHITE_16 = ColorUtils.getColor(255, 255, 255, 41);
    private static final int PILL_DOT_COLOR = ColorUtils.getColor(0, 0, 0, 150);
    private static final int GHOST_FILL = ColorUtils.getColor(0, 0, 0, 70);

    private final Module module;
    private final List<Setting> settings;
    private final Row[] rows;

    private final float anchorX;
    private final float anchorY;
    private final float anchorWidth;
    private final float anchorHeight;

    private float panelX;
    private float panelY;
    private float panelTargetX;
    private float panelTargetY;
    private float panelVelocityX;
    private float panelVelocityY;
    private float panelHeight;
    private float contentHeight;
    private float maxScroll;
    private float scroll;
    private float scrollTarget;
    private float appear;

    private boolean spawned;
    private boolean draggingPanel;
    private float grabOffsetX;
    private float grabOffsetY;
    private float ghostX;
    private float ghostY;
    private boolean ghostValid;
    private float pillHoverAnim;
    private boolean hoveredPill;

    private long lastFrameNanos;

    private int draggingSlider = -1;
    private boolean closing;

    private float cachedValue = Float.NaN;
    private String cachedValueText = "0.00";

    public HudSettingsScreen(Module module, float anchorX, float anchorY,
                             float anchorWidth, float anchorHeight) {
        super(Component.literal(module.getName()));
        this.module = module;
        this.settings = module.getSettings();
        this.rows = new Row[this.settings.size()];
        for (int i = 0; i < this.rows.length; i++) {
            this.rows[i] = new Row();
        }
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.anchorWidth = anchorWidth;
        this.anchorHeight = anchorHeight;
    }

    @Override
    protected void init() {
        super.init();
        this.appear = 0.0f;
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.draggingSlider = -1;
        this.draggingPanel = false;
        this.ghostValid = false;
        this.closing = false;
        this.lastFrameNanos = 0L;
        layout();

        // [+] Меню открывается ровно в точке клика по элементу интерфейса: левый верхний угол
        // панели ставится в левый верхний угол элемента (вырожденный прямоугольник — центр экрана).
        // При повторном init() (ресайз окна) позиция сохраняется.
        if (!this.spawned) {
            this.spawned = true;
            this.panelTargetX = Mth.clamp(this.anchorWidth > 0.0f
                    ? this.anchorX : (this.width - PANEL_WIDTH) * 0.5f, PANEL_MARGIN, maxPanelX());
            this.panelTargetY = Mth.clamp(this.anchorHeight > 0.0f
                    ? this.anchorY : (this.height - this.panelHeight) * 0.5f, PANEL_MARGIN, maxPanelY());
            this.panelX = this.panelTargetX;
            this.panelY = this.panelTargetY;
            this.panelVelocityX = 0.0f;
            this.panelVelocityY = 0.0f;
        }

        GuiStyle.playSound("guiopen.wav", 0.25f);
    }

    // Отрисовка фона отключена: вместо ванильного затемнения — своя мягкая плёнка под панелью.
    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float partialTick) {
        // Снапшот кадра для «стекла» берём до отрисовки GUI — как в ClickGuiScreen.
        GuiBlur.beginFrame(GuiTheme.blurStrength());

        PoseStack stack = context.pose();
        float delta = nextFrameDelta();
        this.appear = Mathf.fast(this.appear, 1.0f, GuiStyle.anim(16.0f));
        this.scroll = Mathf.fast(this.scroll, this.scrollTarget, GuiStyle.anim(18.0f));
        layout();
        updatePanelMotion(delta);
        updateHover(mouseX, mouseY);

        int theme = GuiTheme.clientColor();
        float alpha = this.appear;

        GuiRender.roundedRect(stack, -1.0f, -1.0f, this.width + 2.0f, this.height + 2.0f, 0.0f,
                ColorUtils.multAlpha(ColorUtils.getColor(0, 0, 0, SCRIM_ALPHA), alpha));

        // [+] Перенос: сама панель за курсором не едет — виден только её контур,
        // а содержимое приглушается. После отпускания панель быстро и плавно доезжает сюда.
        if (this.draggingPanel && this.ghostValid) {
            GuiRender.roundedRect(stack, this.ghostX, this.ghostY, PANEL_WIDTH, this.panelHeight,
                    PANEL_RADIUS, ColorUtils.multAlpha(GHOST_FILL, alpha));
            GuiRender.roundedOutline(stack, this.ghostX, this.ghostY, PANEL_WIDTH, this.panelHeight,
                    GHOST_OUTLINE_WIDTH, ColorUtils.multAlpha(theme, 0.9f * alpha), PANEL_RADIUS);
        }

        float contentAlpha = alpha * (this.draggingPanel ? DIM_ALPHA : 1.0f);
        float spawnScale = SPAWN_SCALE + (1.0f - SPAWN_SCALE) * alpha;
        float centerX = this.panelX + PANEL_WIDTH * 0.5f;
        float centerY = this.panelY + this.panelHeight * 0.5f;

        stack.pushPose();
        stack.translate(centerX, centerY, 0.0f);
        stack.scale(spawnScale, spawnScale, 1.0f);
        stack.translate(-centerX, -centerY, 0.0f);

        GuiRender.drawGlass(stack, this.panelX, this.panelY, PANEL_WIDTH, this.panelHeight,
                PANEL_RADIUS, true, 0.0f);

        stack.pushPose();
        stack.translate(this.panelX, this.panelY, 0.0f);

        drawPill(stack, theme, contentAlpha);
        drawRows(stack, theme, contentAlpha);

        stack.popPose();
        stack.popPose();

        super.render(context, mouseX, mouseY, partialTick);
        GuiBlur.endFrame();
    }

    // [+] Заголовок и подвал убраны: панель — это «таблетка» переноса и список настроек.
    private void drawPill(PoseStack stack, int theme, float alpha) {
        float pillX = (PANEL_WIDTH - PILL_WIDTH) * 0.5f;
        GuiRender.roundedRect(stack, pillX, PILL_Y, PILL_WIDTH, PILL_HEIGHT, PILL_HEIGHT * 0.5f,
                ColorUtils.multAlpha(ColorUtils.overCol(WHITE_16, ColorUtils.multAlpha(theme, 0.85f),
                        this.pillHoverAnim), alpha));

        float dotY = PILL_Y + (PILL_HEIGHT - PILL_DOT) * 0.5f;
        float dotCenter = PANEL_WIDTH * 0.5f - 4.0f;
        for (int i = 0; i < 3; i++) {
            GuiRender.roundedRect(stack, dotCenter + i * 4.0f - PILL_DOT * 0.5f, dotY,
                    PILL_DOT, PILL_DOT, PILL_DOT * 0.5f,
                    ColorUtils.multAlpha(PILL_DOT_COLOR, alpha));
        }
    }

    private void drawRows(PoseStack stack, int theme, float alpha) {
        float viewportHeight = this.panelHeight - HEADER_HEIGHT - BOTTOM_PADDING;
        if (viewportHeight <= 1.0f) {
            return;
        }

        ScissorUtils.push();
        ScissorUtils.setFromComponentCoordinates(this.panelX, this.panelY + HEADER_HEIGHT,
                PANEL_WIDTH, viewportHeight);

        for (int i = 0; i < this.rows.length; i++) {
            Row row = this.rows[i];
            if (!row.active) {
                continue;
            }

            Setting setting = this.settings.get(row.index);
            float y = row.y - this.scroll;
            row.hoverAnim = Mathf.fast(row.hoverAnim, row.hovered ? 1.0f : 0.0f, GuiStyle.anim(14.0f));

            if (row.hoverAnim > 0.01f) {
                GuiRender.roundedRect(stack, row.x - 3.0f, y - 0.5f, row.width + 6.0f, row.height + 1.0f,
                        ROW_RADIUS, ColorUtils.multAlpha(theme, 0.08f * row.hoverAnim * alpha));
            }

            if (setting instanceof BooleanSetting booleanSetting) {
                drawBoolean(stack, row, booleanSetting, theme, y, alpha);
            } else if (setting instanceof FloatSetting floatSetting) {
                drawSlider(stack, row, floatSetting, theme, y, alpha);
            } else if (setting instanceof ModeSetting modeSetting) {
                drawMode(stack, row, modeSetting, theme, y, alpha);
            } else {
                drawPlain(stack, row, setting, theme, y, alpha);
            }
        }

        ScissorUtils.unset();
        ScissorUtils.pop();

        drawScrollbar(stack, theme, alpha, viewportHeight);
    }

    private void drawBoolean(PoseStack stack, Row row, BooleanSetting setting, int theme,
                             float y, float alpha) {
        row.knobAnim = Mathf.fast(row.knobAnim, setting.get() ? 1.0f : 0.0f, GuiStyle.anim(16.0f));
        drawLabel(stack, row, setting.name(), y, alpha);

        float toggleX = row.x + row.width - TOGGLE_WIDTH;
        float toggleY = y + (row.height - TOGGLE_HEIGHT) * 0.5f;
        int trackColor = ColorUtils.overCol(ColorUtils.getColor(40, 40, 40, 150),
                ColorUtils.multAlpha(theme, 0.35f), row.knobAnim);
        int outlineColor = ColorUtils.overCol(ColorUtils.getColor(110, 110, 110, 90),
                ColorUtils.multAlpha(theme, 0.45f), row.knobAnim);
        GuiRender.roundedWithOutline(stack, toggleX, toggleY, TOGGLE_WIDTH, TOGGLE_HEIGHT, 0.75f,
                ColorUtils.multAlpha(trackColor, alpha), ColorUtils.multAlpha(outlineColor, alpha),
                TOGGLE_HEIGHT * 0.5f);

        float knobX = toggleX + 1.25f + row.knobAnim * (TOGGLE_WIDTH - TOGGLE_KNOB - 2.5f);
        GuiRender.roundedRect(stack, knobX, toggleY + 1.25f, TOGGLE_KNOB, TOGGLE_KNOB,
                TOGGLE_KNOB * 0.5f, ColorUtils.multAlpha(
                        ColorUtils.overCol(ColorUtils.getColor(185, 190, 200, 255),
                                ColorUtils.getColor(255, 255, 255, 255), row.knobAnim), alpha));
    }

    private void drawSlider(PoseStack stack, Row row, FloatSetting setting, int theme,
                            float y, float alpha) {
        float progress = setting.getMax() > setting.getMin()
                ? (setting.get() - setting.getMin()) / (setting.getMax() - setting.getMin())
                : 0.0f;
        progress = Mth.clamp(progress, 0.0f, 1.0f);

        String value = formatValue(setting.get());
        float valueWidth = GuiFonts.SF_MEDIUM.getWidth(value, LABEL_SIZE);
        float boxWidth = valueWidth + 6.0f;
        float boxX = row.x + row.width - boxWidth;

        GuiFonts.SF_MEDIUM.draw(stack, fit(setting.name(), LABEL_SIZE, boxX - row.x - 4.0f),
                row.x, y + 1.0f,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), 0.92f * alpha), LABEL_SIZE);
        GuiRender.roundedRect(stack, boxX, y, boxWidth, 10.0f, 3.0f,
                ColorUtils.multAlpha(theme, 0.14f * alpha));
        GuiFonts.SF_MEDIUM.draw(stack, value, boxX + 3.0f, y + 1.5f,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), 0.95f * alpha), LABEL_SIZE);

        float trackX = row.x + 1.0f;
        float trackY = y + row.height - TRACK_HEIGHT - 2.0f;
        float trackWidth = row.width - 2.0f;
        GuiRender.roundedRect(stack, trackX, trackY, trackWidth, TRACK_HEIGHT, TRACK_HEIGHT * 0.5f,
                ColorUtils.multAlpha(theme, 0.16f * alpha));
        if (progress > 0.0f) {
            GuiRender.roundedRect(stack, trackX, trackY, Math.max(TRACK_HEIGHT, trackWidth * progress),
                    TRACK_HEIGHT, TRACK_HEIGHT * 0.5f, ColorUtils.multAlpha(theme, 0.9f * alpha));
        }

        float knobX = trackX + trackWidth * progress - KNOB_WIDTH * 0.5f;
        float knobY = trackY + TRACK_HEIGHT * 0.5f - KNOB_HEIGHT * 0.5f;
        GuiRender.roundedWithOutline(stack, knobX, knobY, KNOB_WIDTH, KNOB_HEIGHT, 0.5f,
                ColorUtils.multAlpha(theme, 0.95f * alpha),
                ColorUtils.multAlpha(GuiTheme.disabledColor(), 0.85f * alpha), KNOB_WIDTH * 0.5f);
    }

    private void drawMode(PoseStack stack, Row row, ModeSetting setting, int theme,
                          float y, float alpha) {
        // Метка ужимается до блока переключателя: у режимов контрол самый широкий.
        float labelLimit = row.width - (ARROW_SIZE * 2.0f + 4.0f + row.valueWidth) - 5.0f;
        GuiFonts.SF_MEDIUM.draw(stack, fit(setting.name(), LABEL_SIZE, labelLimit),
                row.x, y + (row.height - 7.0f) * 0.5f,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), 0.92f * alpha), LABEL_SIZE);

        float controlY = y + (row.height - ARROW_SIZE) * 0.5f;
        GuiRender.roundedRect(stack, row.leftArrowX, controlY, ARROW_SIZE, ARROW_SIZE, 3.0f,
                ColorUtils.multAlpha(theme, (row.hoveredLeftArrow ? 0.26f : 0.12f) * alpha));
        GuiRender.roundedRect(stack, row.rightArrowX, controlY, ARROW_SIZE, ARROW_SIZE, 3.0f,
                ColorUtils.multAlpha(theme, (row.hoveredRightArrow ? 0.26f : 0.12f) * alpha));
        GuiRender.roundedRect(stack, row.valueX, controlY, row.valueWidth, ARROW_SIZE, 3.0f,
                ColorUtils.multAlpha(theme, 0.10f * alpha));

        drawArrow(stack, '<', row.leftArrowX, controlY, theme, alpha);
        drawArrow(stack, '>', row.rightArrowX, controlY, theme, alpha);

        String current = setting.displayCurrent();
        float currentWidth = GuiFonts.SF_MEDIUM.getWidth(current, LABEL_SIZE);
        GuiFonts.SF_MEDIUM.draw(stack, current, row.valueX + (row.valueWidth - currentWidth) * 0.5f,
                controlY + (ARROW_SIZE - GuiFonts.SF_MEDIUM.getHeight(current, LABEL_SIZE)) * 0.5f,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), 0.95f * alpha), LABEL_SIZE);
    }

    private void drawPlain(PoseStack stack, Row row, Setting setting, int theme, float y, float alpha) {
        drawLabel(stack, row, setting.name(), y, alpha);

        String value = describe(setting);
        if (value.isEmpty()) {
            return;
        }

        float valueWidth = GuiFonts.SF_MEDIUM.getWidth(value, HINT_SIZE);
        float boxWidth = valueWidth + 6.0f;
        float boxX = row.x + row.width - boxWidth;
        float boxY = y + (row.height - 10.0f) * 0.5f;
        GuiRender.roundedRect(stack, boxX, boxY, boxWidth, 10.0f, 3.0f,
                ColorUtils.multAlpha(theme, 0.10f * alpha));
        GuiFonts.SF_MEDIUM.draw(stack, value, boxX + 3.0f, boxY + 1.5f,
                ColorUtils.multAlpha(GuiTheme.textColor(), 0.6f * alpha), HINT_SIZE);
    }

    private void drawLabel(PoseStack stack, Row row, String text, float y, float alpha) {
        GuiFonts.SF_MEDIUM.draw(stack, text, row.x, y + (row.height - 7.0f) * 0.5f,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), 0.92f * alpha), LABEL_SIZE);
    }

    private void drawArrow(PoseStack stack, char glyph, float x, float y, int theme, float alpha) {
        String text = String.valueOf(glyph);
        float width = GuiFonts.SF_BOLD.getWidth(text, LABEL_SIZE);
        GuiFonts.SF_BOLD.draw(stack, text, x + (ARROW_SIZE - width) * 0.5f,
                y + (ARROW_SIZE - GuiFonts.SF_BOLD.getHeight(text, LABEL_SIZE)) * 0.5f,
                ColorUtils.multAlpha(GuiTheme.disabledColor(), 0.9f * alpha), LABEL_SIZE);
    }

    private void drawScrollbar(PoseStack stack, int theme, float alpha, float viewportHeight) {
        if (this.maxScroll <= 0.0f) {
            return;
        }

        float trackY = HEADER_HEIGHT + 1.0f;
        float trackHeight = viewportHeight - 2.0f;
        float thumbHeight = Math.max(12.0f, trackHeight * (trackHeight / this.contentHeight));
        float progress = this.maxScroll > 0.0f ? this.scroll / this.maxScroll : 0.0f;
        float thumbY = trackY + (trackHeight - thumbHeight) * Mth.clamp(progress, 0.0f, 1.0f);

        GuiRender.roundedRect(stack, PANEL_WIDTH - SIDE_PADDING * 0.5f, trackY, 1.5f, trackHeight, 0.75f,
                ColorUtils.multAlpha(GuiTheme.textColor(), 0.08f * alpha));
        GuiRender.roundedRect(stack, PANEL_WIDTH - SIDE_PADDING * 0.5f, thumbY, 1.5f, thumbHeight, 0.75f,
                ColorUtils.multAlpha(theme, 0.5f * alpha));
    }

    private void layout() {
        float contentLimit = Math.max(24.0f, this.height - 40.0f - HEADER_HEIGHT - BOTTOM_PADDING);
        float cursor = 0.0f;

        for (int i = 0, size = this.settings.size(); i < size; i++) {
            Row row = this.rows[i];
            Setting setting = this.settings.get(i);
            if (!setting.visible()) {
                row.active = false;
                row.hovered = false;
                row.hoveredLeftArrow = false;
                row.hoveredRightArrow = false;
                continue;
            }

            row.active = true;
            row.index = i;
            row.height = rowHeight(setting);
            row.x = SIDE_PADDING;
            row.width = PANEL_WIDTH - SIDE_PADDING * 2.0f;
            row.y = HEADER_HEIGHT + cursor;
            layoutControls(row, setting);

            cursor += row.height + ROW_GAP;
        }

        this.contentHeight = cursor > 0.0f ? cursor - ROW_GAP : 0.0f;
        this.maxScroll = Math.max(0.0f, this.contentHeight - contentLimit);
        this.scrollTarget = Mth.clamp(this.scrollTarget, 0.0f, this.maxScroll);
        this.scroll = Mth.clamp(this.scroll, 0.0f, this.maxScroll);

        float visibleContent = Math.min(this.contentHeight, contentLimit);
        this.panelHeight = HEADER_HEIGHT + visibleContent + BOTTOM_PADDING;

        // [+] Панель живёт в своей точке экрана: цель зажимается в границы окна,
        // а сама панель догоняет цель пружиной (при переносе — контуром под курсором).
        this.panelTargetX = Mth.clamp(this.panelTargetX, PANEL_MARGIN, maxPanelX());
        this.panelTargetY = Mth.clamp(this.panelTargetY, PANEL_MARGIN, maxPanelY());
        this.panelX = Mth.clamp(this.panelX, -PANEL_WIDTH, this.width);
        this.panelY = Mth.clamp(this.panelY, -this.panelHeight, this.height);
    }

    private float maxPanelX() {
        return Math.max(PANEL_MARGIN, this.width - PANEL_WIDTH - PANEL_MARGIN);
    }

    private float maxPanelY() {
        return Math.max(PANEL_MARGIN, this.height - this.panelHeight - PANEL_MARGIN);
    }

    private float pillScreenX() {
        return this.panelX + (PANEL_WIDTH - PILL_WIDTH) * 0.5f;
    }

    private float pillScreenY() {
        return this.panelY + PILL_Y;
    }

    private boolean overPill(double mouseX, double mouseY) {
        return mouseX >= pillScreenX() - 3.0f && mouseX <= pillScreenX() + PILL_WIDTH + 3.0f
                && mouseY >= pillScreenY() - 3.0f && mouseY <= pillScreenY() + PILL_HEIGHT + 3.0f;
    }

    // [+] Пружина панели с подшагами: во время переноса цель заморожена, после отпускания
    // панель быстро и плавно доезжает до контура (сеттл ~0.15 с, без раскачки).
    private void updatePanelMotion(float delta) {
        if (delta <= 0.0f) {
            return;
        }

        int steps = Mth.clamp((int) Math.ceil(delta / MAX_SUB_STEP), 1, MAX_SUB_STEPS);
        float step = delta / steps;
        for (int i = 0; i < steps; i++) {
            this.panelVelocityX += ((this.panelTargetX - this.panelX) * PANEL_STIFFNESS
                    - this.panelVelocityX * PANEL_DAMPING) * step;
            this.panelVelocityY += ((this.panelTargetY - this.panelY) * PANEL_STIFFNESS
                    - this.panelVelocityY * PANEL_DAMPING) * step;
            this.panelVelocityX = Mth.clamp(this.panelVelocityX, -PANEL_MAX_SPEED, PANEL_MAX_SPEED);
            this.panelVelocityY = Mth.clamp(this.panelVelocityY, -PANEL_MAX_SPEED, PANEL_MAX_SPEED);
            this.panelX += this.panelVelocityX * step;
            this.panelY += this.panelVelocityY * step;
        }
    }

    private float nextFrameDelta() {
        long now = System.nanoTime();
        if (this.lastFrameNanos == 0L) {
            this.lastFrameNanos = now;
            return 1.0f / 60.0f;
        }

        float delta = Mth.clamp((now - this.lastFrameNanos) / 1_000_000_000.0f, 0.0f, MAX_FRAME_DELTA);
        this.lastFrameNanos = now;
        return delta;
    }

    private void layoutControls(Row row, Setting setting) {
        if (setting instanceof ModeSetting modeSetting) {
            String current = modeSetting.displayCurrent();
            float textWidth = GuiFonts.SF_MEDIUM.getWidth(current, LABEL_SIZE);
            row.valueWidth = Mth.clamp(textWidth + 6.0f, 18.0f, row.width * 0.55f);
            float total = ARROW_SIZE * 2.0f + 4.0f + row.valueWidth;
            float startX = row.x + row.width - total;
            row.leftArrowX = startX;
            row.valueX = startX + ARROW_SIZE + 2.0f;
            row.rightArrowX = row.valueX + row.valueWidth + 2.0f;
        }
    }

    private float rowHeight(Setting setting) {
        if (setting instanceof FloatSetting) {
            return SLIDER_HEIGHT;
        }
        if (setting instanceof ModeSetting) {
            return MODE_HEIGHT;
        }
        if (setting instanceof BooleanSetting) {
            return BOOLEAN_HEIGHT;
        }
        return PLAIN_HEIGHT;
    }

    private void updateHover(double mouseX, double mouseY) {
        boolean insidePanel = insidePanel(mouseX, mouseY);
        boolean insideViewport = insidePanel
                && mouseY >= this.panelY + HEADER_HEIGHT
                && mouseY <= this.panelY + this.panelHeight - BOTTOM_PADDING;

        // [+] Подсветка «таблетки» переноса — единственная интерактивная ручка в шапке.
        this.hoveredPill = overPill(mouseX, mouseY) || this.draggingPanel;
        this.pillHoverAnim = Mathf.fast(this.pillHoverAnim, this.hoveredPill ? 1.0f : 0.0f, GuiStyle.anim(16.0f));

        for (int i = 0; i < this.rows.length; i++) {
            Row row = this.rows[i];
            if (!row.active) {
                row.hovered = false;
                row.hoveredLeftArrow = false;
                row.hoveredRightArrow = false;
                continue;
            }

            float top = this.panelY + row.y - this.scroll;
            row.hovered = insideViewport
                    && mouseY >= top - 1.0f && mouseY <= top + row.height + 1.0f
                    && mouseX >= this.panelX + row.x - 3.0f
                    && mouseX <= this.panelX + row.x + row.width + 3.0f;

            Setting setting = this.settings.get(row.index);
            if (setting instanceof ModeSetting) {
                float controlY = top + (row.height - ARROW_SIZE) * 0.5f;
                row.hoveredLeftArrow = insideViewport && inRect(mouseX, mouseY,
                        this.panelX + row.leftArrowX, controlY, ARROW_SIZE, ARROW_SIZE);
                row.hoveredRightArrow = insideViewport && inRect(mouseX, mouseY,
                        this.panelX + row.rightArrowX, controlY, ARROW_SIZE, ARROW_SIZE);
            } else {
                row.hoveredLeftArrow = false;
                row.hoveredRightArrow = false;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            onClose();
            return true;
        }
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return false;
        }

        // [+] Зажатие «таблетки»: панель остаётся на месте, за курсором едет только контур.
        if (overPill(mouseX, mouseY)) {
            this.draggingPanel = true;
            this.grabOffsetX = (float) mouseX - this.panelX;
            this.grabOffsetY = (float) mouseY - this.panelY;
            this.ghostX = this.panelX;
            this.ghostY = this.panelY;
            this.ghostValid = true;
            GuiStyle.playSound("mode_pick.wav", 0.1f);
            return true;
        }

        if (!insidePanel(mouseX, mouseY)) {
            onClose();
            return true;
        }

        if (mouseY < this.panelY + HEADER_HEIGHT
                || mouseY > this.panelY + this.panelHeight - BOTTOM_PADDING) {
            return true;
        }

        for (int i = 0; i < this.rows.length; i++) {
            Row row = this.rows[i];
            if (!row.active) {
                continue;
            }

            float top = this.panelY + row.y - this.scroll;
            if (mouseY < top || mouseY > top + row.height) {
                continue;
            }

            Setting setting = this.settings.get(row.index);
            if (setting instanceof BooleanSetting booleanSetting) {
                if (inRect(mouseX, mouseY, this.panelX + row.x + row.width - TOGGLE_WIDTH - 1.0f, top,
                        TOGGLE_WIDTH + 2.0f, row.height)) {
                    booleanSetting.setState(!booleanSetting.get());
                    GuiStyle.playSound("mode_pick.wav", 0.12f);
                    return true;
                }
            } else if (setting instanceof FloatSetting) {
                this.draggingSlider = row.index;
                applySlider(row, mouseX);
                GuiStyle.playSound("mode_pick.wav", 0.08f);
                return true;
            } else if (setting instanceof ModeSetting modeSetting) {
                float controlY = top + (row.height - ARROW_SIZE) * 0.5f;
                if (inRect(mouseX, mouseY, this.panelX + row.leftArrowX, controlY, ARROW_SIZE, ARROW_SIZE)) {
                    cycleMode(modeSetting, -1);
                    return true;
                }
                if (inRect(mouseX, mouseY, this.panelX + row.rightArrowX, controlY, ARROW_SIZE, ARROW_SIZE)
                        || inRect(mouseX, mouseY, this.panelX + row.valueX, controlY,
                                row.valueWidth, ARROW_SIZE)) {
                    cycleMode(modeSetting, 1);
                    return true;
                }
            }
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        // [+] Контур идёт за курсором ровно, кадр в кадр — без сглаживания и пружин.
        if (this.draggingPanel) {
            this.ghostX = Mth.clamp((float) mouseX - this.grabOffsetX, PANEL_MARGIN, maxPanelX());
            this.ghostY = Mth.clamp((float) mouseY - this.grabOffsetY, PANEL_MARGIN, maxPanelY());
            return true;
        }

        if (this.draggingSlider < 0) {
            return false;
        }

        Row row = this.rows[this.draggingSlider];
        if (!row.active) {
            this.draggingSlider = -1;
            return false;
        }
        applySlider(row, mouseX);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.draggingPanel) {
            this.draggingPanel = false;
            if (this.ghostValid) {
                // [+] Отпустили «таблетку» — панель быстро и плавно доезжает до контура.
                this.panelTargetX = this.ghostX;
                this.panelTargetY = this.ghostY;
                this.ghostValid = false;
            }
            return true;
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && this.draggingSlider >= 0) {
            this.draggingSlider = -1;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (this.maxScroll <= 0.0f) {
            return false;
        }
        this.scrollTarget = Mth.clamp(this.scrollTarget - (float) verticalAmount * SCROLL_STEP,
                0.0f, this.maxScroll);
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        if (this.closing) {
            return;
        }
        this.closing = true;
        GuiStyle.playSound("guiclose.wav", 0.25f);
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void applySlider(Row row, double mouseX) {
        Setting setting = this.settings.get(row.index);
        if (!(setting instanceof FloatSetting floatSetting)) {
            return;
        }

        float trackWidth = Math.max(1.0f, row.width - 2.0f);
        float progress = Mth.clamp((float) (mouseX - this.panelX - row.x - 1.0f) / trackWidth, 0.0f, 1.0f);
        float raw = floatSetting.getMin() + (floatSetting.getMax() - floatSetting.getMin()) * progress;
        float step = floatSetting.getIncrement() <= 0.0f ? 0.01f : floatSetting.getIncrement();
        float snapped = (float) Mathf.step(raw, step);
        if (snapped != floatSetting.get()) {
            floatSetting.setValue(snapped);
        }
    }

    private void cycleMode(ModeSetting setting, int direction) {
        List<String> modes = setting.getMods();
        int size = modes.size();
        if (size <= 0) {
            return;
        }
        int index = (setting.getIndex() + direction % size + size) % size;
        setting.set(modes.get(index));
        GuiStyle.playSound("mode_pick.wav", 0.12f);
    }

    private String describe(Setting setting) {
        if (setting instanceof ListSetting listSetting) {
            List<BooleanSetting> values = listSetting.getSettings();
            int enabled = 0;
            for (int i = 0, size = values.size(); i < size; i++) {
                if (values.get(i).get()) {
                    enabled++;
                }
            }
            return enabled + "/" + values.size();
        }
        if (setting instanceof BindSetting bindSetting) {
            return bindSetting.getKey() == -1 ? "n/a" : KeyBoardUtils.getBindName(bindSetting.getKey());
        }
        if (setting instanceof TextSetting textSetting) {
            return textSetting.get();
        }
        return "";
    }

    private String formatValue(float value) {
        int tenths = Math.round(value * 10.0f);
        return tenths / 10 + "." + Math.abs(tenths % 10);
    }

    private String fit(String text, float size, float maxWidth) {
        if (text == null || text.isEmpty() || GuiFonts.SF_MEDIUM.getWidth(text, size) <= maxWidth) {
            return text == null ? "" : text;
        }

        int end = text.length();
        while (end > 0) {
            end = text.offsetByCodePoints(end, -1);
            String candidate = text.substring(0, end) + "...";
            if (GuiFonts.SF_MEDIUM.getWidth(candidate, size) <= maxWidth) {
                return candidate;
            }
        }
        return "...";
    }

    private boolean insidePanel(double mouseX, double mouseY) {
        return mouseX >= this.panelX && mouseX <= this.panelX + PANEL_WIDTH
                && mouseY >= this.panelY && mouseY <= this.panelY + this.panelHeight;
    }

    private boolean inRect(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    // Строка меню: индекс в списке настроек модуля + рассчитанные прямоугольники контролов.
    private static final class Row {

        private boolean active;
        private int index;
        private float x;
        private float y;
        private float width;
        private float height;
        private float hoverAnim;
        private float knobAnim;
        private boolean hovered;
        private boolean hoveredLeftArrow;
        private boolean hoveredRightArrow;
        private float leftArrowX;
        private float rightArrowX;
        private float valueX;
        private float valueWidth;
    }
}
