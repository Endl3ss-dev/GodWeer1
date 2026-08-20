package org.privates.client.managers.component.impl.drag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.privates.client.Excellent;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.api.interfaces.IMouse;
import org.privates.client.api.interfaces.IWindow;
import org.privates.client.managers.component.Component;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.input.MouseReleaseEvent;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.render.Hud;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.screen.newgui.ColorPicker;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.math.ScaleMath;
import org.privates.client.utils.other.SoundUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorExp;
import org.privates.client.utils.render.font.Fonts;
import org.privates.common.impl.taskript.Script;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Getter
public class DragComponent extends Component implements IWindow, IMouse {
    private DragSetting selected;
    private final Vector2f offset = new Vector2f();
    private final List<Module> modules = new CopyOnWriteArrayList<>();
    public static List<Line> lines = new CopyOnWriteArrayList<>();
    private final Script script = new Script();

    private DragSetting settingsTarget;
    private Object settingsRenderer;
    private final Animation settingsPanelAnimation = new Animation();
    private float settingsPanelScrollY = 0, settingsPanelScrollYV = 0;
    private final Map<String, Animation> settingAnimations = new HashMap<>();
    private final Map<String, Float> settingAnimV = new HashMap<>();
    private final Map<String, Float> sliderAnimV = new HashMap<>();
    private final Map<String, Float> nameScrollX = new HashMap<>();
    private final Map<String, Long> lastScrollTime = new HashMap<>();
    private final Map<String, Boolean> scrollingForward = new HashMap<>();
    private final Map<String, Long> scrollPauseUntil = new HashMap<>();
    private final Map<String, Integer> modeOffsetX = new HashMap<>();
    private final Map<String, Long> modeTime = new HashMap<>();
    private final Map<String, List<Float>> multiBoolInterpolate = new HashMap<>();
    private final Map<String, Float> modeWidth = new HashMap<>();
    private final Map<String, Float> selectedV = new HashMap<>();
    private final Map<String, Float> offsetV = new HashMap<>();
    private final Map<String, Float> offsetX = new HashMap<>();
    private final Map<String, Long> time = new HashMap<>();
    private final Map<String, List<Float>> interpolateM = new HashMap<>();
    private final Map<String, List<Float>> offsetXM = new HashMap<>();
    private final Map<String, List<Long>> hoverTime = new HashMap<>();
    private final Map<String, ColorPicker> colorPickers = new HashMap<>();
    private final Map<String, Boolean> settingSelected = new HashMap<>();
    private final Map<String, String> inputValueString = new HashMap<>();
    private final Map<String, Boolean> isInputMode = new HashMap<>();
    private final Map<String, Float> itemScrollY = new HashMap<>();
    private final Map<String, Float> itemScrollYV = new HashMap<>();
    private final Map<String, List<Item>> filteredItems = new HashMap<>();
    private final Map<String, Map<Item, Float>> itemScaleAnimations = new HashMap<>();

    private final boolean settingsPanelDragging = false;
    private String colorPickerKey;

    private Vector4f currentPanelRect = null;
    private final List<HudSetting> hudSettings = new ArrayList<>();

    private List<Item> allItems = new ArrayList<>();

    public void post(MatrixStack matrix) {
        script.update();
        int width = mw.getScaledWidth(), height = mw.getScaledHeight();
        int color = ColorUtil.getColor(255, 64);
        boolean shouldRender = mc.currentScreen instanceof ChatScreen;

        if (!shouldRender) {
            selected = null;
            if (settingsTarget != null) {
                settingsTarget = null;
                settingsRenderer = null;
                colorPickerKey = null;
                settingsPanelAnimation.run(0.0, 0.3, Easings.CUBIC_OUT, true);
            }
        }

        initModules();
        handleAnimation();

        if (selected != null && selected.active) {
            float mouseX = (float) (mc.mouseHelper.getMouseX() / mw.getScaleFactor());
            float mouseY = (float) (mc.mouseHelper.getMouseY() / mw.getScaleFactor());
            selected.targetPosition.set(mouseX + offset.x, mouseY + offset.y);
            lines.clear();
            initSnaps(width, height);
            handleSnaps(matrix, color);
            handleDrags(width, height);
        }

        settingsPanelAnimation.update();
        settingAnimations.values().forEach(Animation::update);
        currentPanelRect = null;

        if (settingsTarget != null || settingsPanelAnimation.getValue() > 0.01) renderSettingsPanel(matrix);
    }

    private void renderHints(MatrixStack matrix) {
        // Icons removed
    }

    private void renderDescription(MatrixStack matrix) {
        // Descriptions removed
    }

    private Vector2f findBestPosition(DragSetting element, float boxW, float boxH, float scale) {
        // Removed - not used anymore
        return new Vector2f(0, 0);
    }

    private void loadSettingsForRenderer(Object renderer) {
        hudSettings.clear();
        if (renderer instanceof IConfigurableHud configurableHud) hudSettings.addAll(configurableHud.getHudSettings());

        if (allItems.isEmpty()) {
            allItems = Registry.ITEM.stream()
                    .filter(item -> item != Items.AIR)
                    .collect(Collectors.toList());
        }
    }

    private void renderSettingsPanel(MatrixStack matrix) {
        float sScale = getResolutionScale();
        float settingScale = sScale * 0.77f;
        settingsPanelAnimation.run(settingsTarget != null ? 1.0 : 0.0, 0.3, Easings.CUBIC_OUT, true);
        if (settingsPanelAnimation.getValue() <= 0.01 || settingsTarget == null) {
            currentPanelRect = null;
            return;
        }

        float panelWidth = 220f * sScale;
        float gap = 10f * sScale;
        float elementRight = settingsTarget.position.x + settingsTarget.size.x;
        float elementLeft = settingsTarget.position.x;
        float elementY = settingsTarget.position.y;
        float rightX = elementRight + gap;
        float leftX = elementLeft - panelWidth - gap;

        boolean fitsRight = (rightX + panelWidth) <= mw.getScaledWidth();
        boolean fitsLeft = leftX >= 0;
        boolean onRight = fitsRight || (!fitsLeft && (mw.getScaledWidth() - rightX) > elementLeft);
        float panelX = onRight ? rightX : leftX;

        float settingH = 0f;
        for (HudSetting setting : hudSettings) {
            String key = settingsTarget.getName() + "_" + setting.name;
            settingH += getSettingHeight(setting, key, settingScale) + 3f * settingScale;
        }

        float panelHeight = Math.min(settingH + 10f * sScale, mw.getScaledHeight() * 0.7f);
        float panelY = Math.min(elementY, mw.getScaledHeight() - panelHeight);

        currentPanelRect = new Vector4f(panelX, panelY, panelWidth, panelHeight);

        matrix.push();
        float animVal = (float) settingsPanelAnimation.getValue();
        float pivotX = onRight ? panelX : (panelX + panelWidth);
        float pivotY = panelY + panelHeight / 2f;
        matrix.translate(pivotX, pivotY, 0);
        matrix.scale(animVal, animVal, 1);
        matrix.translate(-pivotX, -pivotY, 0);

        RenderUtil.CustomStyled.draw(matrix, panelX, panelY, panelWidth, panelHeight, Round.of(8f * sScale), true, 12f * animVal);
        RenderUtil.Rounded.roundedRect(matrix, panelX, panelY, panelWidth, panelHeight,
                ColorUtil.multAlpha(ColorUtil.getColor(28, 26, 38, 170), animVal), Round.of(8f * sScale));
        RenderUtil.Rounded.roundedOutline(matrix, panelX, panelY, panelWidth, panelHeight, 1f * sScale,
                ColorUtil.multAlpha(ColorUtil.getColor(120, 120, 140, 70), animVal), Round.of(8f * sScale));

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(panelX + 5f * sScale, panelY + 5f * sScale, panelWidth - 10f * sScale, panelHeight - 10f * sScale);

        float currentY = panelY + 5f * sScale + settingsPanelScrollYV;
        float settingWidth = panelWidth - 10f * sScale;

        for (HudSetting setting : hudSettings) {
            String key = settingsTarget.getName() + "_" + setting.name;

            if (setting.type == SettingType.BOOLEAN)
                renderBooleanSetting(matrix, setting, panelX + 5f * sScale, currentY, settingWidth, 12f * settingScale, settingScale, animVal, key);
            else if (setting.type == SettingType.SLIDER)
                renderSliderSetting(matrix, setting, panelX + 5f * sScale, currentY, settingWidth, 20f * settingScale, settingScale, animVal, key);
            else if (setting.type == SettingType.MODE)
                renderModeSetting(matrix, setting, panelX + 5f * sScale, currentY, settingWidth, settingScale, animVal, key);
            else if (setting.type == SettingType.MULTI_BOOLEAN)
                renderMultiBooleanSetting(matrix, setting, panelX + 5f * sScale, currentY, settingWidth, settingScale, animVal, key);
            else if (setting.type == SettingType.COLOR)
                renderColorSetting(matrix, setting, panelX + 5f * sScale, currentY, settingWidth, settingScale, animVal, key);

            float h = getSettingHeight(setting, key, settingScale);
            currentY += h + 3f * settingScale;
        }

        ScissorExp.unset();
        ScissorExp.pop();
        matrix.pop();
    }

    private float getSettingHeight(HudSetting setting, String key, float scale) {
        if (setting.type == SettingType.BOOLEAN) return 12f * scale;
        if (setting.type == SettingType.SLIDER) return 20f * scale;
        if (setting.type == SettingType.MODE) return getModeLikeHeight(setting, scale);
        if (setting.type == SettingType.MULTI_BOOLEAN) return getModeLikeHeight(setting, scale);
        if (setting.type == SettingType.COLOR) {
            float h = 12f * scale;
            if (settingSelected.getOrDefault(key, false) && colorPickers.containsKey(key)) {
                ColorPicker cp = colorPickers.get(key);
                h += (cp.getHeight() + 2) * selectedV.getOrDefault(key, 0f);
            }
            return h;
        }
        return 12f * scale;
    }

    private float getModeLikeHeight(HudSetting setting, float scale) {
        if (!(setting.values instanceof List<?> valuesList) || valuesList.isEmpty()) {
            return 12f * scale;
        }
        float labelHeight = 10f * scale;
        float chipHeight = 17f * scale;
        float gap = 2f * scale;
        float available = 210f * scale;
        float rowWidth = 0f;
        int rows = 1;
        for (Object value : valuesList) {
            float chipW = Fonts.SF_SEMIBOLD.getWidth(String.valueOf(value), 9f * scale) + 8f * scale;
            if (rowWidth > 0f && rowWidth + chipW > available) {
                rows++;
                rowWidth = 0f;
            }
            rowWidth += chipW + gap;
        }
        return labelHeight + 3f * scale + rows * (chipHeight + gap);
    }

    private void renderBooleanSetting(MatrixStack matrix, HudSetting setting, float x, float y, float w, float h, float scale, float alpha, String key) {
        Theme theme = Theme.getInstance();
        boolean boolValue = setting.getter.get() > 0.5f;
        float toggle = Mathf.fast(settingAnimV.getOrDefault(key, 0f), boolValue ? 1f : 0f, 14f);
        settingAnimV.put(key, toggle);

        float nameSize = 9.5f * scale;
        float toggleW = 21f * scale;
        float toggleH = 12f * scale;
        float toggleX = x + w - toggleW;
        float toggleY = y + (h - toggleH) / 2f;
        float nameW = w - toggleW - 4f * scale;

        drawAutoScrollLabel(matrix, setting.name, x, y + h / 2f - nameSize / 2f, nameW, key,
                ColorUtil.multAlpha(theme.textColor(), alpha), nameSize, scale, y, h);

        int trackColor = ColorUtil.overCol(
                ColorUtil.multAlpha(ColorUtil.getColor(58, 24, 43), alpha),
                ColorUtil.multAlpha(theme.clientColor(), alpha), toggle);
        RenderUtil.Rounded.roundedRect(matrix, toggleX, toggleY, toggleW, toggleH, trackColor, Round.of(32f * scale));

        float dot = 8f * scale;
        float dotX = toggleX + 2f * scale + (toggleW - dot - 4f * scale) * toggle;
        RenderUtil.Rounded.roundedRect(matrix, dotX, toggleY + (toggleH - dot) / 2f, dot, dot,
                ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha), Round.of(dot / 2f));
    }

    private void renderSliderSetting(MatrixStack matrix, HudSetting setting, float x, float y, float w, float h, float scale, float alpha, String key) {
        Theme theme = Theme.getInstance();
        float value = setting.getter.get();
        String valueText = formatSliderValue(value);
        float nameSize = 9.5f * scale;
        float valueSize = 9f * scale;
        float valueW = Fonts.SF_SEMIBOLD.getWidth(valueText, valueSize);

        drawAutoScrollLabel(matrix, setting.name, x, y, w - valueW - 6f * scale, key,
                ColorUtil.multAlpha(theme.textColor(), alpha), nameSize, scale, y, 11f * scale);
        Fonts.SF_SEMIBOLD.draw(matrix, valueText, x + w - valueW, y,
                ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha), valueSize);

        float barY = y + h - 5f * scale;
        float barH = 3f * scale;
        float progress = setting.max == setting.min ? 0f : (value - setting.min) / (setting.max - setting.min);
        progress = MathHelper.clamp(progress, 0f, 1f);

        if (settingSelected.getOrDefault(key, false)) {
            float step = setting.increment <= 0 ? 0.1F : setting.increment;
            float mouseX = (float) (mc.mouseHelper.getMouseX() / mw.getScaleFactor());
            float np = MathHelper.clamp((mouseX - x) / Math.max(1f, w), 0f, 1f);
            setting.setter.accept((float) MathHelper.clamp(
                    Mathf.step(setting.min + (setting.max - setting.min) * np, step), setting.min, setting.max));
            progress = np;
        }

        float fillWTarget = w * progress;
        float fillW = Mathf.fast(selectedV.getOrDefault(key, fillWTarget), fillWTarget, 18f);
        selectedV.put(key, fillW);

        RenderUtil.Rounded.roundedRect(matrix, x, barY, w, barH,
                ColorUtil.multAlpha(ColorUtil.getColor(70, 70, 70), alpha), Round.of(0.5f * scale));
        RenderUtil.Rounded.roundedRect(matrix, x, barY, fillW, barH,
                ColorUtil.multAlpha(theme.clientColor(), alpha), Round.of(0.5f * scale));

        float knobR = 4.5f * scale;
        float knobCX = MathHelper.clamp(x + fillW, x + knobR, x + w - knobR);
        RenderUtil.Rounded.roundedRect(matrix, knobCX - knobR, barY + barH / 2f - knobR, knobR * 2f, knobR * 2f,
                ColorUtil.multAlpha(theme.clientColor(), alpha), Round.of(knobR));
    }

    private void renderModeSetting(MatrixStack matrix, HudSetting setting, float x, float y, float w, float scale, float alpha, String key) {
        if (!(setting.values instanceof List<?> valuesList) || valuesList.isEmpty()) return;
        Theme theme = Theme.getInstance();

        float labelSize = 9.5f * scale;
        Fonts.SF_SEMIBOLD.draw(matrix, setting.name, x, y,
                ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), alpha), labelSize);

        float chipH = 17f * scale;
        float chipGap = 2f * scale;
        float chipX = x;
        float chipY = y + 10f * scale + 3f * scale;
        String active = String.valueOf(setting.getter.get());

        for (Object value : valuesList) {
            String text = String.valueOf(value);
            float chipW = Fonts.SF_SEMIBOLD.getWidth(text, 9f * scale) + 8f * scale;
            if (chipX > x && chipX + chipW > x + w) {
                chipX = x;
                chipY += chipH + chipGap;
            }
            boolean on = text.equalsIgnoreCase(active);
            int bg = on ? ColorUtil.multAlpha(theme.clientColor(), alpha) : ColorUtil.multAlpha(ColorUtil.getColor(64, 64, 64, 166), alpha);
            int tc = on ? ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha) : ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), alpha);
            RenderUtil.Rounded.roundedRect(matrix, chipX, chipY, chipW, chipH, bg, Round.of(4f * scale));
            Fonts.SF_SEMIBOLD.draw(matrix, text, chipX + 4f * scale, chipY + (chipH - 9f * scale) / 2f - 0.5f * scale, tc, 9f * scale);
            chipX += chipW + chipGap;
        }
    }

    private void renderMultiBooleanSetting(MatrixStack matrix, HudSetting setting, float x, float y, float w, float scale, float alpha, String key) {
        if (!(setting.values instanceof List<?> valuesList) || valuesList.isEmpty()) return;
        Theme theme = Theme.getInstance();

        Fonts.SF_SEMIBOLD.draw(matrix, setting.name, x, y,
                ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), alpha), 9.5f * scale);

        float chipH = 17f * scale;
        float chipGap = 2f * scale;
        float chipX = x;
        float chipY = y + 10f * scale + 3f * scale;

        for (int i = 0; i < valuesList.size(); i++) {
            String text = String.valueOf(valuesList.get(i));
            float chipW = Fonts.SF_SEMIBOLD.getWidth(text, 9f * scale) + 8f * scale;
            if (chipX > x && chipX + chipW > x + w) {
                chipX = x;
                chipY += chipH + chipGap;
            }
            boolean active = i < setting.activeIndices.size() && setting.activeIndices.get(i);
            float interp = Mathf.fast(getMultiBoolInterp(key, i), active ? 1f : 0f, 14f);
            setMultiBoolInterp(key, i, interp);
            int bg = ColorUtil.overCol(
                    ColorUtil.multAlpha(ColorUtil.getColor(64, 64, 64, 166), alpha),
                    ColorUtil.multAlpha(theme.clientColor(), alpha), interp);
            int tc = ColorUtil.overCol(
                    ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), alpha),
                    ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255), alpha), interp);
            RenderUtil.Rounded.roundedRect(matrix, chipX, chipY, chipW, chipH, bg, Round.of(4f * scale));
            Fonts.SF_SEMIBOLD.draw(matrix, text, chipX + 4f * scale, chipY + (chipH - 9f * scale) / 2f - 0.5f * scale, tc, 9f * scale);
            chipX += chipW + chipGap;
        }
    }

    private void renderColorSetting(MatrixStack matrix, HudSetting setting, float x, float y, float w, float scale, float alpha, String key) {
        Theme theme = Theme.getInstance();
        int color = Math.round(setting.getter.get());
        int interpolate = ColorUtil.multAlpha(ColorUtil.getColor(177, 177, 177), alpha);

        float mouseX = (float) (mc.mouseHelper.getMouseX() / mw.getScaleFactor());
        float mouseY = (float) (mc.mouseHelper.getMouseY() / mw.getScaleFactor());
        float offsetXVal = (isHover(mouseX, mouseY, x, y, w - 14 * scale, 10f * scale) &&
                Fonts.SF_SEMIBOLD.getWidth(setting.name, 9.5f * scale) > w - 14 * scale) ? 10 * scale - ((w) - Fonts.SF_SEMIBOLD.getWidth(setting.name, 9.5f * scale)) : 0;
        float offV = Mathf.fast(offsetV.getOrDefault(key, 0f), offsetXVal, 16);
        offsetV.put(key, offV);

        float selV = Mathf.fast(selectedV.getOrDefault(key, 0f), settingSelected.getOrDefault(key, false) ? 1 : 0, 34);
        selectedV.put(key, selV);

        float h = 12f * scale;
        if (colorPickers.containsKey(key)) {
            ColorPicker cp = colorPickers.get(key);
            h = Mathf.fast(h, 8f * scale + (cp.getHeight() + 2) * selV, 34);
        }

        float centerY = y + h / 2f - Fonts.SF_SEMIBOLD.getHeight(setting.name, 9.5f * scale) / 1.5f;

        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(x, y, w - 14 * scale, h);
        Fonts.SF_SEMIBOLD.draw(matrix, setting.name, x - offV, centerY, interpolate, 9.5f * scale);
        ScissorExp.unset();
        ScissorExp.pop();

        float sw = 14f * scale;
        float sx = x + w - sw;
        float sy = y + (h - 10f * scale) / 2f;
        RenderUtil.Rounded.roundedRect(matrix, sx, sy, sw, 10f * scale, ColorUtil.multAlpha(color, alpha), Round.of(3f * scale));
        RenderUtil.Rounded.roundedOutline(matrix, sx, sy, sw, 10f * scale, 0.7f * scale,
                ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 255, 110), alpha), Round.of(3f * scale));

        if (colorPickers.containsKey(key) && h >= 9f * scale) {
            ColorPicker cp = colorPickers.get(key);
            ScissorExp.push();
            ScissorExp.setFromComponentCoordinates(x, y + 8 * scale, w, h - 8 * scale);
            cp.render(matrix, (int) mouseX, (int) mouseY);
            cp.setX((int) (x + w - 90 * scale));
            cp.setY((int) (y + 12 * scale));
            ScissorExp.unset();
            ScissorExp.pop();
        }
    }

    private void drawAutoScrollLabel(MatrixStack matrix, String text, float textX, float textY, float maxWidth, String key,
                                     int color, float size, float scale, float clipY, float clipH) {
        float textWidth = Fonts.SF_SEMIBOLD.getWidth(text, size);
        float offTarget = 0f;
        float mouseX = (float) (mc.mouseHelper.getMouseX() / mw.getScaleFactor());
        float mouseY = (float) (mc.mouseHelper.getMouseY() / mw.getScaleFactor());
        if (isHover(mouseX, mouseY, textX, clipY, maxWidth, clipH) && textWidth > maxWidth) {
            offTarget = textWidth - maxWidth + 4f * scale;
        }
        float off = Mathf.fast(offsetV.getOrDefault(key, 0f), offTarget, 16f);
        offsetV.put(key, off);
        ScissorExp.push();
        ScissorExp.setFromComponentCoordinates(textX, clipY, Math.max(1f, maxWidth), clipH);
        Fonts.SF_SEMIBOLD.draw(matrix, text, textX - off, textY, color, size);
        ScissorExp.unset();
        ScissorExp.pop();
    }

    private String formatSliderValue(float v) {
        String s = String.format(java.util.Locale.US, "%.2f", v);
        if (s.endsWith("00")) return s.substring(0, s.length() - 3);
        if (s.endsWith("0")) return s.substring(0, s.length() - 1);
        return s;
    }

    private float getMultiBoolInterp(String key, int index) {
        List<Float> list = multiBoolInterpolate.computeIfAbsent(key, k -> new ArrayList<>());
        while (list.size() <= index) list.add(0f);
        return list.get(index);
    }

    private void setMultiBoolInterp(String key, int index, float value) {
        List<Float> list = multiBoolInterpolate.computeIfAbsent(key, k -> new ArrayList<>());
        while (list.size() <= index) list.add(0f);
        list.set(index, value);
    }

    private float maxMode(HudSetting setting, String key, float scale) {
        if (!(setting.values instanceof List<?> vals)) return 20f * scale;
        String current = String.valueOf(setting.getter.get());
        float max = Fonts.SF_MEDIUM.getWidth(current, 8f * scale);
        if (settingSelected.getOrDefault(key, false)) {
            for (Object val : vals) {
                max = Math.max(max, Fonts.SF_MEDIUM.getWidth(String.valueOf(val), 8f * scale));
            }
        }
        return max + 4 * scale;
    }

    private float maxModeMulti(HudSetting setting, String key, float scale) {
        if (!(setting.values instanceof List<?> vals)) return 60f * scale;
        String values = "";
        for (int i = 0; i < vals.size(); i++) {
            boolean active = (i < setting.activeIndices.size() && setting.activeIndices.get(i));
            if (active && !values.isEmpty()) values += ", ";
            if (active) values += vals.get(i);
        }
        float max = Math.min(60 * scale, Fonts.SF_MEDIUM.getWidth("Выбрано: " + values, 8f * scale));
        if (settingSelected.getOrDefault(key, false)) {
            for (Object val : vals) {
                max = Math.max(max, Fonts.SF_MEDIUM.getWidth(String.valueOf(val), 8f * scale));
            }
        }
        return Math.min(90 * scale, max);
    }

    private float getResolutionScale() {
        int width = mw.getScaledWidth(), height = mw.getScaledHeight();
        if (width >= 3840 && height >= 2160) return 1.5f;
        else if (width >= 2560 && height >= 1440) return 1.0f;
        else if (width >= 1920 || height >= 1080) return 0.85f;
        else return 0.75f;
    }

    private Object getRendererByDragName(String dragName) {
        Hud hud = Hud.getInstance();
        return switch (dragName.toLowerCase()) {
            case "targethud" -> hud.targetHudRenderer();
            case "watermark" -> hud.watermarkRenderer();
            case "stafflist" -> hud.staffListRenderer();
            case "keybinds" -> hud.keybindsRenderer();
            case "potions" -> hud.potionsRenderer();
            case "cooldowns" -> hud.cooldownRenderer();
            case "keystrokes" -> hud.keystrokesRenderer();
            case "scoreboard" -> hud.scoreBoardRenderer();
            case "bossbar" -> hud.bossBarRenderer();
            case "information" -> hud.informationRenderer();
            case "armorhud" -> hud.armorHudRenderer();
            case "arraylist" -> hud.arrayListRenderer();
            case "inventoryhud" -> hud.inventoryHudRenderer();
            case "motiongraph" -> hud.motionGraphRenderer();
            case "nearbyplayers" -> hud.nearbyPlayersRenderer();
            case "armorsmart" -> hud.armorSmartRenderer();
            case "itembinds" -> hud.itemBindRenderer();
            case "itemcount" -> hud.itemCountRenderer();
            default -> null;
        };
    }

    private void handleDrags(int width, int height) {
        for (Module module : modules) {
            for (Setting<?> setting : module.getSettings()) {
                if (setting instanceof DragSetting dragSetting && dragSetting.active) {
                    dragSetting.position.x = (float) dragSetting.animationX.getValue();
                    dragSetting.position.y = (float) dragSetting.animationY.getValue();

                    dragSetting.position.x = MathHelper.clamp(dragSetting.position.x, 0, width - dragSetting.size.x);
                    dragSetting.position.y = MathHelper.clamp(dragSetting.position.y, 0, height - dragSetting.size.y);
                    dragSetting.targetPosition.x = MathHelper.clamp(dragSetting.targetPosition.x, 0, width - dragSetting.size.x);
                    dragSetting.targetPosition.y = MathHelper.clamp(dragSetting.targetPosition.y, 0, height - dragSetting.size.y);
                }
            }
        }
    }

    private void handleSnaps(MatrixStack matrix, int color) {
        for (Line snap : lines) {
            double closest = Double.MAX_VALUE;
            switch (snap.direction) {
                case VERTICAL:
                    for (float y = -selected.size.y; y <= 0; y += selected.size.y / 2F) {
                        if ((y == -selected.size.y / 2F && !snap.center) || (y == -selected.size.y && !snap.left) || (y == 0 && !snap.right))
                            continue;
                        double distance = Math.abs(selected.targetPosition.y - (snap.position + y));
                        if (distance < snap.distance && distance < closest) {
                            closest = distance;
                            selected.targetPosition.y = snap.position + y;
                            RectUtil.drawRect(matrix, 0, snap.position - 0.5F, scaled().x, 1F, color);
                        }
                    }
                    break;
                case HORIZONTAL:
                    for (float x = -selected.size.x; x <= 0; x += selected.size.x / 2F) {
                        if ((x == -selected.size.x / 2F && !snap.center) || (x == -selected.size.x && !snap.left) || (x == 0 && !snap.right))
                            continue;
                        float distance = Math.abs(selected.targetPosition.x - (snap.position + x));
                        if (distance < snap.distance && distance < closest) {
                            closest = distance;
                            selected.targetPosition.x = snap.position + x;
                            RectUtil.drawRect(matrix, snap.position - 0.5F, 0, 1F, scaled().y, color);
                        }
                    }
                    break;
            }
        }
    }

    private void initSnaps(int width, int height) {
        float edgeSnap = 5F, distance = 5F;
        lines.add(new Line(width / 4f, distance, Direction.HORIZONTAL, true, true, true));
        lines.add(new Line(height / 4f, distance, Direction.VERTICAL, true, true, true));
        lines.add(new Line(width - (width / 4f), distance, Direction.HORIZONTAL, true, true, true));
        lines.add(new Line(height - (height / 4f), distance, Direction.VERTICAL, true, true, true));
        lines.add(new Line(width / 2f, distance, Direction.HORIZONTAL, true, true, true));
        lines.add(new Line(height / 2f, distance, Direction.VERTICAL, true, true, true));
        lines.add(new Line(height - edgeSnap, distance, Direction.VERTICAL, false, false, true));
        lines.add(new Line(edgeSnap, distance, Direction.VERTICAL, false, true, false));
        lines.add(new Line(width - edgeSnap, distance, Direction.HORIZONTAL, false, false, true));
        lines.add(new Line(edgeSnap, distance, Direction.HORIZONTAL, false, true, false));

        for (Module module : modules) {
            module.getSettings().stream().filter(s -> s instanceof DragSetting).forEach(s -> {
                DragSetting dragSetting = (DragSetting) s;
                if (dragSetting != selected) {
                    lines.add(new Line(dragSetting.position.x + dragSetting.size.x + edgeSnap, distance, Direction.HORIZONTAL, false, true, false));
                    lines.add(new Line(dragSetting.position.x - edgeSnap, distance, Direction.HORIZONTAL, false, false, true));
                    lines.add(new Line(dragSetting.position.y - edgeSnap, distance, Direction.VERTICAL, false, false, true));
                    lines.add(new Line(dragSetting.position.y + dragSetting.size.y + edgeSnap, distance, Direction.VERTICAL, false, true, false));
                }
            });
        }
    }

    private void handleAnimation() {
        modules.forEach(m -> m.getSettings().forEach(s -> {
            if (s instanceof DragSetting ds) {
                ds.animationX.update();
                ds.animationY.update();
                if (script.isFinished()) {
                    ds.animationX.run(ds.targetPosition.x, 0.25, Easings.CUBIC_OUT, true);
                    ds.animationY.run(ds.targetPosition.y, 0.25, Easings.CUBIC_OUT, true);
                }
            }
        }));
    }

    private void initModules() {
        modules.clear();
        Excellent.inst().moduleManager().values().stream()
                .filter(m -> m.isEnabled() && m.getSettings().stream().anyMatch(s -> s instanceof DragSetting))
                .forEach(modules::add);
    }

    @EventHandler
    public void onEvent(MousePressEvent event) {
        if (!(event.getScreen() instanceof ChatScreen)) return;

        float sScale = getResolutionScale();
        float settingScale = sScale * 0.77f;
        Vector2f mouse = ScaleMath.getMouse(event.getMouseX(), event.getMouseY());
        float mouseX = mouse.x, mouseY = mouse.y;

        if (settingsTarget != null && event.getKey() == 0) {
            float panelWidth = 220f * sScale;
            float gap = 10f * sScale;
            float elementRight = settingsTarget.position.x + settingsTarget.size.x;
            float elementLeft = settingsTarget.position.x;
            float rightX = elementRight + gap;
            float leftX = elementLeft - panelWidth - gap;
            boolean fitsRight = (rightX + panelWidth) <= mw.getScaledWidth();
            boolean fitsLeft = leftX >= 0;
            float panelX = (fitsRight || (!fitsLeft && (mw.getScaledWidth() - rightX) > elementLeft)) ? rightX : leftX;

            float settingH = 0f;
            for (HudSetting setting : hudSettings) {
                String key = settingsTarget.getName() + "_" + setting.name;
                settingH += getSettingHeight(setting, key, settingScale) + 3f * settingScale;
            }
            float panelHeight = Math.min(settingH + 10f * sScale, mw.getScaledHeight() * 0.7f);
            float panelY = Math.min(settingsTarget.position.y, mw.getScaledHeight() - panelHeight);

            if (mouseX >= panelX + 5f * sScale && mouseX <= panelX + panelWidth - 5f * sScale &&
                    mouseY >= panelY + 5f * sScale && mouseY <= panelY + panelHeight - 5f * sScale) {

                float currentY = panelY + 5f * sScale + settingsPanelScrollYV;
                float settingWidth = panelWidth - 10f * sScale;

                for (HudSetting setting : hudSettings) {
                    String key = settingsTarget.getName() + "_" + setting.name;
                    float h = getSettingHeight(setting, key, settingScale);

                    if (mouseY >= currentY && mouseY <= currentY + h) {
                        handleSettingClick(setting, key, mouseX, mouseY, panelX + 5f * sScale, currentY, settingWidth, h, settingScale);
                        return;
                    }

                    currentY += h + 3f * settingScale;
                }
                return;
            }

            if (mouseX < panelX || mouseX > panelX + panelWidth || mouseY < panelY || mouseY > panelY + panelHeight) {
                settingsTarget = null;
                settingsRenderer = null;
                colorPickerKey = null;
                return;
            }
        }

        for (Module module : modules) {
            for (Setting<?> setting : module.getSettings()) {
                if (setting instanceof DragSetting dragSetting && dragSetting.active) {
                    Vector2f position = dragSetting.position;
                    Vector2f scale = dragSetting.size;

                    if (!dragSetting.structure && isHover(mouseX, mouseY, position.x, position.y, scale.x, scale.y)) {
                        if (event.getKey() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                            settingsTarget = dragSetting;
                            settingsRenderer = getRendererByDragName(dragSetting.getName());
                            if (settingsRenderer != null) loadSettingsForRenderer(settingsRenderer);
                            return;
                        } else if (event.getKey() == 0) {
                            selected = dragSetting;
                            offset.set(position.x - mouseX, position.y - mouseY);
                            return;
                        }
                    }
                }
            }
        }
    }


    private void handleSettingClick(HudSetting setting, String key, float mouseX, float mouseY, float x, float y, float w, float h, float scale) {
        if (setting.type == SettingType.BOOLEAN) {
            boolean currentValue = setting.getter.get() > 0.5f;
            setting.setter.accept(currentValue ? 0f : 1f);
            SoundUtil.playSound(!currentValue ? "cb_enabled.wav" : "cb_disabled.wav", 0.1f);
        } else if (setting.type == SettingType.SLIDER) {
            if (isHover(mouseX, mouseY, x, y, w, h)) {
                settingSelected.put(key, true);
                SoundUtil.playSound("slider.wav", 0.1f);
            }
        } else if (setting.type == SettingType.MODE) {
            if (setting.values instanceof List<?> vals) {
                float chipH = 17f * scale;
                float chipGap = 2f * scale;
                float chipX = x;
                float chipY = y + 10f * scale + 3f * scale;
                for (Object val : vals) {
                    String text = String.valueOf(val);
                    float chipW = Fonts.SF_SEMIBOLD.getWidth(text, 9f * scale) + 8f * scale;
                    if (chipX > x && chipX + chipW > x + w) {
                        chipX = x;
                        chipY += chipH + chipGap;
                    }
                    if (isHover(mouseX, mouseY, chipX, chipY, chipW, chipH)) {
                        int idx = vals.indexOf(val);
                        setting.setter.accept((float) idx);
                        SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                        return;
                    }
                    chipX += chipW + chipGap;
                }
            }
        } else if (setting.type == SettingType.MULTI_BOOLEAN) {
            if (setting.values instanceof List<?> vals) {
                float chipH = 17f * scale;
                float chipGap = 2f * scale;
                float chipX = x;
                float chipY = y + 10f * scale + 3f * scale;
                for (int i = 0; i < vals.size(); i++) {
                    String text = String.valueOf(vals.get(i));
                    float chipW = Fonts.SF_SEMIBOLD.getWidth(text, 9f * scale) + 8f * scale;
                    if (chipX > x && chipX + chipW > x + w) {
                        chipX = x;
                        chipY += chipH + chipGap;
                    }
                    if (isHover(mouseX, mouseY, chipX, chipY, chipW, chipH)) {
                        if (i < setting.activeIndices.size()) {
                            setting.activeIndices.set(i, !setting.activeIndices.get(i));
                            SoundUtil.playSound("mode_pick_on.wav", 0.1f);
                        }
                        return;
                    }
                    chipX += chipW + chipGap;
                }
            }
        } else if (setting.type == SettingType.COLOR) {
            float sw = 14f * scale;
            float sy = y + (h - 10f * scale) / 2f;
            if (isHover(mouseX, mouseY, x + w - sw, sy, sw, 10f * scale)) {
                boolean sel = settingSelected.getOrDefault(key, false);
                settingSelected.put(key, !sel);
                if (!sel) {
                    int color = Math.round(setting.getter.get());
                    ColorSetting cs = new ColorSetting(null, setting.name, color);
                    ColorPicker cp = new ColorPicker((int) (x + w + 4f * scale), (int) (y + 10f * scale), (int) (90f * scale), (int) (100f * scale), cs);
                    colorPickers.put(key, cp);
                } else {
                    colorPickers.remove(key);
                }
            } else if (settingSelected.getOrDefault(key, false) && colorPickers.containsKey(key)) {
                ColorPicker cp = colorPickers.get(key);
                if (isHover(mouseX, mouseY, cp.getX(), cp.getY(), cp.getWidth(), cp.getHeight())) {
                    cp.mouseClicked(mouseX, mouseY, 0);
                }
            }
        }
    }

    @EventHandler
    public void onEvent(MouseReleaseEvent event) {
        for (String key : new ArrayList<>(settingSelected.keySet())) {
            HudSetting setting = null;
            for (HudSetting s : hudSettings) {
                if (settingsTarget != null && (settingsTarget.getName() + "_" + s.name).equals(key)) {
                    setting = s;
                    break;
                }
            }
            if (setting != null && setting.type == SettingType.SLIDER) {
                settingSelected.put(key, false);
            }
        }

        if (selected != null && event.getKey() == 0) {
            script.cleanup().addTickStep(0, () -> {
                selected.targetPosition.set(selected.position);
                selected = null;
            }, () -> selected != null && selected.animationX.isFinished() && selected.animationY.isFinished());
        }
    }

    public void handleScroll(double delta) {
        float sScale = getResolutionScale();
        if (settingsTarget != null) {
            settingsPanelScrollY += (float) (delta * 10f * sScale);
            settingsPanelScrollYV = Mathf.fast(settingsPanelScrollYV, settingsPanelScrollY, 12);
        }
    }

    @AllArgsConstructor
    public static class Line {
        public float position, distance;
        public Direction direction;
        public boolean center, right, left;
    }

    public enum Direction {
        VERTICAL, HORIZONTAL
    }

    public static class HudSetting {
        public String name;
        public SettingType type;
        public java.util.function.Supplier<Float> getter;
        public java.util.function.Consumer<Float> setter;
        public float min, max, increment;
        public Object values;
        public List<Boolean> activeIndices;

        public HudSetting(String name, SettingType type, java.util.function.Supplier<Float> getter, java.util.function.Consumer<Float> setter) {
            this.name = name;
            this.type = type;
            this.getter = getter;
            this.setter = setter;
        }

        public HudSetting(String name, SettingType type, java.util.function.Supplier<Float> getter, java.util.function.Consumer<Float> setter, float min, float max, float increment) {
            this.name = name;
            this.type = type;
            this.getter = getter;
            this.setter = setter;
            this.min = min;
            this.max = max;
            this.increment = increment;
        }

        public HudSetting(String name, SettingType type, java.util.function.Supplier<Float> getter, java.util.function.Consumer<Float> setter, List<String> values) {
            this.name = name;
            this.type = type;
            this.getter = getter;
            this.setter = setter;
            this.values = values;
        }

        public HudSetting(String name, SettingType type, java.util.function.Supplier<Float> getter, java.util.function.Consumer<Float> setter, List<String> values, List<Boolean> activeIndices) {
            this.name = name;
            this.type = type;
            this.getter = getter;
            this.setter = setter;
            this.values = values;
            this.activeIndices = activeIndices;
        }
    }

    public enum SettingType {
        BOOLEAN, SLIDER, MODE, MULTI_BOOLEAN, COLOR
    }
}
