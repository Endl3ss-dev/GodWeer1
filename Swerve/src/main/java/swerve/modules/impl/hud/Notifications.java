package swerve.modules.impl.hud;

import com.mojang.blaze3d.vertex.PoseStack;

import swerve.managers.ThemeManager;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.ui.hud.HudElement;
import swerve.ui.newgui.GuiFonts;
import swerve.ui.newgui.GuiRender;
import swerve.ui.newgui.GuiStyle;
import swerve.utils.client.Notification;
import swerve.utils.client.NotificationManager;
import swerve.utils.color.ColorUtils;

import net.minecraft.client.Minecraft;

import java.util.List;

// Отрисовка очереди уведомлений в стиле исходного клиента (Private, Notification.render):
// плашка по центру ниже середины экрана, иконка, разделитель, текст с подсветкой ключевого
// слова, выезд с easeOutBack и затухание с easeOutQuint. Позиция задаётся настройкой,
// поэтому элемент не перетаскивается и координаты не сохраняет.
public final class Notifications extends HudElement {

    private static final float HEIGHT = 18.0f;
    private static final float RADIUS = 6.0f;
    private static final float PADDING = 6.0f;
    private static final float ICON_SIZE = 10.0f;
    private static final float ICON_GAP = 6.0f;
    private static final float SEPARATOR_WIDTH = 0.25f;
    private static final float SEPARATOR_HEIGHT = 7.5f;
    private static final float TEXT_SIZE = 7.0f;
    private static final float SLIDE_DISTANCE = 15.0f;
    private static final float CENTER_OFFSET_Y = 30.0f;
    private static final float MARGIN = 3.0f;
    private static final float WIDTH_SPEED = 18.0f;
    private static final float APPEAR_SPEED = 16.0f;
    private static final float SCALE_FLOOR = 0.85f;
    private static final int BAR_HEIGHT = 1;

    private static final int PLATE_COLOR = ColorUtils.rgba(0, 0, 0, 51);
    private static final int TEXT_COLOR = ColorUtils.rgba(255, 255, 255, 235);
    private static final int SEPARATOR_COLOR = ColorUtils.rgba(255, 255, 255, 13);
    private static final int BAR_TRACK_COLOR = ColorUtils.rgba(255, 255, 255, 28);

    private final ModeSetting position = new ModeSetting("Позиция", "Центр", "Центр", "Правый верх", "Правый низ");
    private final ModeSetting style = new ModeSetting("Стиль", "Плашка", "Плашка", "Минимал");
    private final FloatSetting spacing = new FloatSetting("Отступ", 1.5f, 0.5f, 6.0f, 0.5f);
    private final FloatSetting animationSpeed = new FloatSetting("Скорость анимации", 15.0f, 5.0f, 30.0f, 1.0f);
    private final BooleanSetting icons = new BooleanSetting("Иконки", true);
    private final BooleanSetting separators = new BooleanSetting("Разделитель", true);
    private final BooleanSetting progressBar = new BooleanSetting("Полоса времени", false);
    private final BooleanSetting sounds = new BooleanSetting("Звуки", true);

    private final float[] frameY = new float[NotificationManager.MAX_VISIBLE];

    private float frameWidth;
    private float frameHeight;

    private int lastSoundRevision;

    public Notifications() {
        super("Notifications", "Всплывающие уведомления клиента");
        addSettings(position, style, spacing, animationSpeed, icons, separators, progressBar, sounds);
    }

    @Override
    public boolean notifiesOnToggle() {
        return false;
    }

    @Override
    public void onEnable() {
        this.lastSoundRevision = NotificationManager.getRevision();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        NotificationManager.clear();
        super.onDisable();
    }

    @Override
    protected void measure() {
        NotificationManager.update();

        Minecraft minecraft = Minecraft.getInstance();
        float screenWidth = minecraft.getWindow().getGuiScaledWidth();
        float screenHeight = minecraft.getWindow().getGuiScaledHeight();

        boolean right = this.position.is("Правый верх") || this.position.is("Правый низ");
        boolean bottom = this.position.is("Правый низ");
        setAnchor(right, bottom);

        if (this.position.is("Центр")) {
            setTargetPosition(screenWidth * 0.5f, screenHeight * 0.5f + CENTER_OFFSET_Y);
        } else {
            setTargetPosition(right ? screenWidth - MARGIN : MARGIN, bottom ? screenHeight - MARGIN : MARGIN);
        }

        prepareFrame(getFrameDelta());
        setSize(Math.max(1.0f, this.frameWidth), this.frameHeight);
    }

    @Override
    protected void renderContent(PoseStack stack, float alpha, float delta) {
        List<Notification> visible = NotificationManager.getVisible();
        if (visible.isEmpty()) {
            return;
        }

        playSounds();

        boolean minimal = this.style.is("Минимал");
        int theme = ThemeManager.getColor();
        int row = 0;

        for (int i = 0, size = visible.size(); i < size; i++) {
            Notification notification = visible.get(i);
            if (notification.getAppear() <= 0.01f) {
                continue;
            }

            float width = notification.getAnimatedWidth();
            if (width <= 0.5f) {
                continue;
            }

            float fade = easeOutQuint(notification.getFade());
            float slide = easeOutBack(notification.getSlide());
            float rowAlpha = alpha * fade;
            float x = pillX(width);
            float y = this.frameY[row++] + (1.0f - slide) * SLIDE_DISTANCE;

            if (!minimal) {
                GuiRender.roundedRect(stack, x, y, width, HEIGHT, RADIUS,
                        ColorUtils.multAlpha(PLATE_COLOR, rowAlpha));
            }

            float centerY = y + HEIGHT * 0.5f;
            float scale = SCALE_FLOOR + (1.0f - SCALE_FLOOR) * easeOutQuint(notification.getAppear());

            stack.pushPose();
            stack.translate(0.0f, centerY, 0.0f);
            stack.scale(scale, scale, 1.0f);
            stack.translate(0.0f, -centerY, 0.0f);

            float currentX = x + PADDING;
            if (this.icons.get()) {
                String icon = String.valueOf(notification.getType().getIcon());
                GuiFonts.HUD_ICONS.draw(stack, icon, currentX, y + (HEIGHT - ICON_SIZE) * 0.5f,
                        ColorUtils.multAlpha(theme, rowAlpha), ICON_SIZE);
                currentX += GuiFonts.HUD_ICONS.getWidth(icon, ICON_SIZE) + ICON_GAP;

                if (this.separators.get()) {
                    GuiRender.roundedRect(stack, currentX, y + (HEIGHT - SEPARATOR_HEIGHT) * 0.5f,
                            SEPARATOR_WIDTH, SEPARATOR_HEIGHT, SEPARATOR_WIDTH * 0.5f,
                            ColorUtils.multAlpha(SEPARATOR_COLOR, rowAlpha));
                    currentX += SEPARATOR_WIDTH + ICON_GAP;
                }
            }

            drawText(stack, notification, currentX, y, rowAlpha, theme);

            if (this.progressBar.get()) {
                drawProgressBar(stack, notification, x, y, width, rowAlpha);
            }

            stack.popPose();
        }
    }

    // Анимации очереди продвигаются один раз за кадр — до расчёта вёрстки.
    private void prepareFrame(float delta) {
        List<Notification> visible = NotificationManager.getVisible();
        float gap = this.spacing.get();
        float slideSpeed = this.animationSpeed.get() * 1.5f;

        float totalHeight = 0.0f;
        float maxWidth = 0.0f;
        int drawn = 0;

        for (int i = 0, size = visible.size(); i < size; i++) {
            Notification notification = visible.get(i);

            if (notification.getTargetWidth() <= 0.0f) {
                notification.setHeight(HEIGHT);
                notification.setTargetWidth(measureWidth(notification));
            }

            float target = notification.getTargetAppear();
            notification.setAppear(approach(notification.getAppear(), target, delta, APPEAR_SPEED));
            notification.setSlide(approach(notification.getSlide(), target, delta, slideSpeed));
            notification.setFade(approach(notification.getFade(), target, delta, slideSpeed));
            notification.setAnimatedWidth(approach(notification.getAnimatedWidth(),
                    notification.getTargetWidth() * notification.getSlide(), delta, WIDTH_SPEED));

            if (notification.getAppear() <= 0.01f || drawn >= this.frameY.length) {
                continue;
            }

            float rowHeight = HEIGHT * (SCALE_FLOOR + (1.0f - SCALE_FLOOR)
                    * easeOutQuint(notification.getAppear()));

            this.frameY[drawn] = totalHeight;
            if (drawn > 0) {
                totalHeight += gap;
            }
            totalHeight += rowHeight;
            maxWidth = Math.max(maxWidth, notification.getAnimatedWidth());
            drawn++;
        }

        this.frameHeight = totalHeight;
        this.frameWidth = maxWidth;
    }

    private float pillX(float width) {
        return this.position.is("Центр") ? -width * 0.5f : this.frameWidth - width;
    }

    private void drawText(PoseStack stack, Notification notification, float x, float y,
                          float rowAlpha, int theme) {
        String text = notification.getText();
        String highlight = notification.getHighlight();
        float textY = y + (HEIGHT - GuiFonts.SF_MEDIUM.getHeight(text, TEXT_SIZE)) * 0.5f;
        float cursor = x;

        if (!text.isEmpty()) {
            GuiFonts.SF_MEDIUM.draw(stack, text, cursor, textY,
                    ColorUtils.multAlpha(TEXT_COLOR, rowAlpha), TEXT_SIZE);
            cursor += GuiFonts.SF_MEDIUM.getWidth(text, TEXT_SIZE);
        }

        if (!highlight.isEmpty()) {
            GuiFonts.SF_MEDIUM.draw(stack, highlight, cursor, textY,
                    ColorUtils.multAlpha(theme, rowAlpha), TEXT_SIZE);
        }
    }

    private void drawProgressBar(PoseStack stack, Notification notification, float x, float y,
                                 float width, float rowAlpha) {
        long duration = notification.getDurationMs();
        if (duration <= 0L) {
            return;
        }

        float remaining = 1.0f - (System.currentTimeMillis() - notification.getCreatedMs()) / (float) duration;
        remaining = Math.max(0.0f, Math.min(1.0f, remaining));

        float barY = y + HEIGHT - BAR_HEIGHT - 1.0f;
        float barWidth = width - PADDING * 2.0f;

        GuiRender.roundedRect(stack, x + PADDING, barY, barWidth, BAR_HEIGHT, BAR_HEIGHT * 0.5f,
                ColorUtils.multAlpha(BAR_TRACK_COLOR, rowAlpha));
        GuiRender.roundedRect(stack, x + PADDING, barY, barWidth * remaining, BAR_HEIGHT, BAR_HEIGHT * 0.5f,
                ColorUtils.multAlpha(notification.getType().getColor(), rowAlpha));
    }

    private float measureWidth(Notification notification) {
        float width = PADDING * 2.0f;

        if (this.icons.get()) {
            width += GuiFonts.HUD_ICONS.getWidth(String.valueOf(notification.getType().getIcon()), ICON_SIZE)
                    + ICON_GAP;
            if (this.separators.get()) {
                width += SEPARATOR_WIDTH + ICON_GAP;
            }
        }

        width += GuiFonts.SF_MEDIUM.getWidth(notification.getText(), TEXT_SIZE);
        width += GuiFonts.SF_MEDIUM.getWidth(notification.getHighlight(), TEXT_SIZE);
        return width;
    }

    private void playSounds() {
        if (!this.sounds.get()) {
            return;
        }

        int revision = NotificationManager.getRevision();
        if (revision == this.lastSoundRevision) {
            return;
        }
        this.lastSoundRevision = revision;

        List<Notification> visible = NotificationManager.getVisible();
        if (visible.isEmpty()) {
            return;
        }

        Notification newest = visible.get(visible.size() - 1);
        GuiStyle.playSound(typeSound(newest.getType(), newest.isPositive()), 0.12f);
    }

    private static String typeSound(Notification.Type type, boolean positive) {
        return switch (type) {
            case SUCCESS -> "bind_success.wav";
            case WARNING -> "bind_wait.wav";
            case ERROR -> "bind_remove.wav";
            case MODULE -> positive ? "cb_enabled.wav" : "cb_disabled.wav";
            default -> "mode_pick.wav";
        };
    }

    private static float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1.0f;
        return 1.0f + c3 * (float) Math.pow(t - 1.0f, 3) + c1 * (float) Math.pow(t - 1.0f, 2);
    }

    private static float easeOutQuint(float t) {
        return 1.0f - (float) Math.pow(1.0f - t, 5);
    }

    private static float approach(float current, float target, float delta, float speed) {
        return current + (target - current) * (1.0f - (float) Math.exp(-delta * speed));
    }
}
