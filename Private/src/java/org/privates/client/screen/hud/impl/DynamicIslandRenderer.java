package org.privates.client.screen.hud.impl;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.AbstractHud;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DynamicIslandRenderer extends AbstractHud implements IRenderer, IConfigurableHud {
    private final DragSetting drag;

    @Setter
    @Getter
    private float scale = 1.0f;

    // Animation states
    private float currentWidth = 80f;
    private float targetWidth = 80f;
    private long widthAnimStart = 0;
    private final float widthAnimDuration = 300f; // 0.3 seconds

    // Display modes
    private enum DisplayMode {
        TIME,
        MUSIC,
        MODULE
    }

    private DisplayMode currentMode = DisplayMode.TIME;
    private DisplayMode targetMode = DisplayMode.TIME;

    // Module toggle tracking
    private String lastToggledModule = "";
    private boolean lastToggleState = false;
    private long moduleToggleTime = 0;
    private static final long MODULE_DISPLAY_DURATION = 3000; // 3 seconds

    // Music tracking
    private String currentMusicName = "";
    private long musicStartTime = 0;
    private float musicProgress = 0f;

    // Sizes
    private static final float COMPACT_WIDTH = 80f;
    private static final float EXPANDED_WIDTH = 140f;
    private static final float HEIGHT = 24f;

    public DynamicIslandRenderer(DragSetting drag) {
        this.drag = drag;
    }

    @Override
    public List<DragComponent.HudSetting> getHudSettings() {
        return List.of(
                new DragComponent.HudSetting("Scale", DragComponent.SettingType.SLIDER,
                        () -> this.scale, v -> this.scale = v, 0.5f, 2.0f, 0.1f)
        );
    }

    public void onModuleToggle(Module module, boolean enabled) {
        if (module == null) return;
        lastToggledModule = module.getName();
        lastToggleState = enabled;
        moduleToggleTime = System.currentTimeMillis();
        setMode(DisplayMode.MODULE);
    }

    private void setMode(DisplayMode mode) {
        if (targetMode != mode) {
            targetMode = mode;
            updateTargetWidth();
        }
    }

    private void updateTargetWidth() {
        float newWidth = COMPACT_WIDTH;
        if (targetMode == DisplayMode.MUSIC || targetMode == DisplayMode.MODULE) {
            newWidth = EXPANDED_WIDTH;
        }

        if (targetWidth != newWidth) {
            targetWidth = newWidth;
            widthAnimStart = System.currentTimeMillis();
        }
    }

    private void updateMode() {
        if (mc.player == null) {
            setMode(DisplayMode.TIME);
            return;
        }

        // Check if module display should expire
        if (currentMode == DisplayMode.MODULE) {
            if (System.currentTimeMillis() - moduleToggleTime > MODULE_DISPLAY_DURATION) {
                setMode(DisplayMode.TIME);
            }
        }

        // Check for music disc
        ItemStack mainHand = mc.player.getHeldItemMainhand();
        ItemStack offHand = mc.player.getHeldItemOffhand();

        if (isMusicDisc(mainHand) || isMusicDisc(offHand)) {
            ItemStack disc = isMusicDisc(mainHand) ? mainHand : offHand;
            String musicName = disc.getDisplayName().getString();

            if (!musicName.equals(currentMusicName)) {
                currentMusicName = musicName;
                musicStartTime = System.currentTimeMillis();
            }

            // Only switch to music mode if not showing module toggle
            if (currentMode != DisplayMode.MODULE) {
                setMode(DisplayMode.MUSIC);
            }
        } else {
            currentMusicName = "";
            if (currentMode == DisplayMode.MUSIC) {
                setMode(DisplayMode.TIME);
            }
        }
    }

    @Override
    public void render(Render2DEvent event) {
        if (mc.player == null) return;

        updateMode();

        MatrixStack matrix = event.getMatrix();
        float s = scale;

        // Animate width
        long elapsed = System.currentTimeMillis() - widthAnimStart;
        float progress = Math.min(1f, elapsed / widthAnimDuration);
        float easedProgress = (float) Easings.EXPO_OUT.ease(progress);
        currentWidth = (float) Mathf.lerp(currentWidth, targetWidth, easedProgress);

        // Update current mode when animation completes
        if (progress >= 1f && currentMode != targetMode) {
            currentMode = targetMode;
        }

        float width = currentWidth * s;
        float height = HEIGHT * s;

        // Position
        float x = drag.position.x;
        float y = drag.position.y;

        if (x == 0 && y == 0) {
            x = (mc.getMainWindow().getScaledWidth() / 2f) - (width / 2f);
            y = 10f;
            drag.position.set(x, y);
        }

        drag.size.set(width, height);

        // Render background
        float radius = height / 2f;
        RenderUtil.CustomStyled.draw(matrix, x, y, width, height, Round.of(radius), true, 10f);

        int bgColor = ColorUtil.replAlpha(ColorUtil.getColor(0, 0, 0), (int) (0.3f * 255));
        RenderUtil.Rounded.smooth(matrix, x, y, width, height, bgColor, Round.of(radius));

        // Render content based on mode
        renderContent(matrix, x, y, width, height, s);
    }

    private void renderContent(MatrixStack matrix, float x, float y, float width, float height, float s) {
        float centerY = y + height / 2f;
        float textSize = 7f * s;

        switch (currentMode) {
            case TIME:
                renderTimeMode(matrix, x, width, centerY, textSize);
                break;
            case MUSIC:
                renderMusicMode(matrix, x, y, width, height, centerY, textSize, s);
                break;
            case MODULE:
                renderModuleMode(matrix, x, width, centerY, textSize);
                break;
        }
    }

    private void renderTimeMode(MatrixStack matrix, float x, float width, float centerY, float textSize) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        String time = timeFormat.format(new Date());

        float textWidth = Fonts.SF_MEDIUM.getWidth(time, textSize);
        float textX = x + (width - textWidth) / 2f;
        float textY = centerY - Fonts.SF_MEDIUM.getHeight(time, textSize) / 2f;

        Fonts.SF_MEDIUM.draw(matrix, time, textX, textY, -1, textSize);
    }

    private void renderMusicMode(MatrixStack matrix, float x, float y, float width, float height, float centerY, float textSize, float s) {
        // Music icon
        float iconSize = 8f * s;
        float iconX = x + 8f * s;
        float iconY = centerY - iconSize / 2f;

        int themeColor = Theme.getInstance().clientColor();
        Fonts.icon.draw(matrix, "♪", iconX, iconY, themeColor, iconSize);

        // Music name
        String displayName = currentMusicName.length() > 12 ? currentMusicName.substring(0, 12) + "..." : currentMusicName;
        float textX = iconX + iconSize + 4f * s;
        float textY = centerY - Fonts.SF_MEDIUM.getHeight(displayName, textSize) / 2f;

        Fonts.SF_MEDIUM.draw(matrix, displayName, textX, textY, -1, textSize);

        // Progress bar at bottom
        long elapsed = System.currentTimeMillis() - musicStartTime;
        musicProgress = (elapsed % 10000) / 10000f; // 10 second loop for demo

        float barHeight = 2f * s;
        float barY = y + height - barHeight - 2f * s;
        float barX = x + 4f * s;
        float barWidth = width - 8f * s;

        // Background
        int bgColor = ColorUtil.replAlpha(ColorUtil.getColor(255, 255, 255), 30);
        RenderUtil.Rounded.smooth(matrix, barX, barY, barWidth, barHeight, bgColor, Round.of(barHeight / 2f));

        // Progress
        float progressWidth = barWidth * musicProgress;
        if (progressWidth > 0) {
            RenderUtil.Rounded.smooth(matrix, barX, barY, progressWidth, barHeight, themeColor, Round.of(barHeight / 2f));
        }
    }

    private void renderModuleMode(MatrixStack matrix, float x, float width, float centerY, float textSize) {
        // Module name
        String moduleName = lastToggledModule;
        String status = lastToggleState ? "ON" : "OFF";
        String displayText = moduleName + " " + status;

        float textWidth = Fonts.SF_MEDIUM.getWidth(displayText, textSize);
        float textX = x + (width - textWidth) / 2f;
        float textY = centerY - Fonts.SF_MEDIUM.getHeight(displayText, textSize) / 2f;

        int themeColor = Theme.getInstance().clientColor();
        int statusColor = lastToggleState ? ColorUtil.getColor(0, 255, 0) : ColorUtil.getColor(255, 0, 0);

        // Draw module name
        float nameWidth = Fonts.SF_MEDIUM.getWidth(moduleName + " ", textSize);
        Fonts.SF_MEDIUM.draw(matrix, moduleName + " ", textX, textY, -1, textSize);

        // Draw status
        Fonts.SF_MEDIUM.draw(matrix, status, textX + nameWidth, textY, statusColor, textSize);
    }

    private boolean isMusicDisc(ItemStack stack) {
        if (stack.isEmpty()) return false;
        return stack.getItem() == Items.MUSIC_DISC_13 ||
                stack.getItem() == Items.MUSIC_DISC_CAT ||
                stack.getItem() == Items.MUSIC_DISC_BLOCKS ||
                stack.getItem() == Items.MUSIC_DISC_CHIRP ||
                stack.getItem() == Items.MUSIC_DISC_FAR ||
                stack.getItem() == Items.MUSIC_DISC_MALL ||
                stack.getItem() == Items.MUSIC_DISC_MELLOHI ||
                stack.getItem() == Items.MUSIC_DISC_STAL ||
                stack.getItem() == Items.MUSIC_DISC_STRAD ||
                stack.getItem() == Items.MUSIC_DISC_WARD ||
                stack.getItem() == Items.MUSIC_DISC_11 ||
                stack.getItem() == Items.MUSIC_DISC_WAIT ||
                stack.getItem() == Items.MUSIC_DISC_PIGSTEP;
    }
}
