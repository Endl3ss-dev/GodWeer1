package org.privates.client.screen.hudconstructor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.render.Hud;
import org.privates.client.managers.module.settings.impl.DragSetting;
import org.privates.client.screen.hud.IRenderer;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.draw.ScissorUtil;
import org.privates.client.utils.render.font.Fonts;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HudConstructor extends Screen {
    private final Hud hudModule;
    private final List<HudElement> hudElements = new ArrayList<>();
    private HudElement selectedElement;
    private HudElement draggingElement;
    private float dragOffsetX, dragOffsetY;
    private boolean showSettings = false;
    private float settingsPanelX, settingsPanelY;
    private final float settingsPanelWidth = 250;
    private final float settingsPanelHeight = 300;
    private float scrollOffset = 0;
    private float targetScrollOffset = 0;
    private final Map<String, HudConfig> elementConfigs = new HashMap<>();
    private final Path configPath = Paths.get("private", "hudconfig.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public HudConstructor() {
        super(new StringTextComponent("HUD Constructor"));
        this.hudModule = Hud.getInstance();
        initializeElements();
        loadConfig();
    }

    private void initializeElements() {
        if (hudModule.checks().getValue("Watermark")) {
            hudElements.add(new HudElement("Watermark", hudModule.watermarkRenderer(), hudModule.watermarkDrag()));
        }
        if (hudModule.checks().getValue("TargetHud")) {
            hudElements.add(new HudElement("TargetHud", hudModule.targetHudRenderer(), hudModule.targetHudRendererDrag()));
        }
        if (hudModule.checks().getValue("Keybinds")) {
            hudElements.add(new HudElement("Keybinds", hudModule.keybindsRenderer(), hudModule.keybindsRendererDrag()));
        }
        if (hudModule.checks().getValue("Potions")) {
            hudElements.add(new HudElement("Potions", hudModule.potionsRenderer(), hudModule.potionsRendererDrag()));
        }
        if (hudModule.checks().getValue("Information")) {
            hudElements.add(new HudElement("Information", hudModule.informationRenderer(), hudModule.infoDrag()));
        }
        if (hudModule.checks().getValue("ArmorHud")) {
            hudElements.add(new HudElement("ArmorHud", hudModule.armorHudRenderer(), hudModule.armorHudDrag()));
        }
        if (hudModule.checks().getValue("StaffList")) {
            hudElements.add(new HudElement("StaffList", hudModule.staffListRenderer(), hudModule.staffListDrag()));
        }
        if (hudModule.checks().getValue("ArrayList")) {
            hudElements.add(new HudElement("ArrayList", hudModule.arrayListRenderer(), hudModule.arrayListDrag()));
        }
        if (hudModule.checks().getValue("InventoryHud")) {
            hudElements.add(new HudElement("InventoryHud", hudModule.inventoryHudRenderer(), hudModule.inventoryHudDrag()));
        }
        if (hudModule.checks().getValue("MotionGraph")) {
            hudElements.add(new HudElement("MotionGraph", hudModule.motionGraphRenderer(), hudModule.motionGraphDrag()));
        }


        if (hudModule.checks().getValue("Cooldowns")) {
            hudElements.add(new HudElement("Cooldowns", hudModule.cooldownRenderer(), hudModule.cooldownsDrag()));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);

        for (HudElement element : hudElements) {
            element.render(matrixStack, mouseX, mouseY, partialTicks);

            if (element == selectedElement) {
                float x = element.dragSetting().position.x;
                float y = element.dragSetting().position.y;
                float width = element.dragSetting().size.x;
                float height = element.dragSetting().size.y;

                RenderUtil.Rounded.roundedOutline(matrixStack, x - 2, y - 2, width + 4, height + 4,
                        1f, Theme.getInstance().clientColor(), Round.of(4));
            }
        }

        if (showSettings && selectedElement != null) {
            renderSettingsPanel(matrixStack, mouseX, mouseY);
        }

        String title = "HUD Constructor";
        float titleX = width / 2f - Fonts.SF_BOLD.getWidth(title, 16) / 2f;
        Fonts.SF_BOLD.draw(matrixStack, title, titleX, 10, ColorUtil.getColor(255, 255, 255), 16);

        String hint = "Click on element to select, drag to move, right click for settings";
        float hintX = width / 2f - Fonts.SF_REGULAR.getWidth(hint, 10) / 2f;
        Fonts.SF_REGULAR.draw(matrixStack, hint, hintX, 30, ColorUtil.getColor(180, 180, 180), 10);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderSettingsPanel(MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderUtil.Rounded.roundedRect(matrixStack, settingsPanelX, settingsPanelY,
                settingsPanelWidth, settingsPanelHeight, ColorUtil.getColor(20, 20, 25, 240), Round.of(8));

        RenderUtil.Rounded.roundedOutline(matrixStack, settingsPanelX, settingsPanelY,
                settingsPanelWidth, settingsPanelHeight, 1f, ColorUtil.getColor(50, 50, 60), Round.of(8));

        String title = selectedElement.name() + " Settings";
        float titleX = settingsPanelX + settingsPanelWidth / 2f - Fonts.SF_BOLD.getWidth(title, 12) / 2f;
        Fonts.SF_BOLD.draw(matrixStack, title, titleX, settingsPanelY + 10, ColorUtil.getColor(255, 255, 255), 12);

        ScissorUtil.enableScissor(settingsPanelX, settingsPanelY + 30, settingsPanelWidth, settingsPanelHeight - 40);

        float y = settingsPanelY + 40 + scrollOffset;
        HudConfig config = elementConfigs.computeIfAbsent(selectedElement.name(), k -> new HudConfig());

        renderSlider(matrixStack, "Font Size", config.fontSize, 6, 20, settingsPanelX + 10, y, settingsPanelWidth - 20, mouseX, mouseY);
        y += 40;

        renderSlider(matrixStack, "Icon Size", config.iconSize, 8, 32, settingsPanelX + 10, y, settingsPanelWidth - 20, mouseX, mouseY);
        y += 40;

        renderSlider(matrixStack, "Spacing", config.spacing, 0, 20, settingsPanelX + 10, y, settingsPanelWidth - 20, mouseX, mouseY);
        y += 40;

        renderSlider(matrixStack, "Scale", config.scale, 0.5f, 2.0f, settingsPanelX + 10, y, settingsPanelWidth - 20, mouseX, mouseY);
        y += 40;

        renderSlider(matrixStack, "Opacity", config.opacity, 0.1f, 1.0f, settingsPanelX + 10, y, settingsPanelWidth - 20, mouseX, mouseY);

        ScissorUtil.disableScissor();

        float closeX = settingsPanelX + settingsPanelWidth - 25;
        float closeY = settingsPanelY + 5;
        RenderUtil.Rounded.roundedRect(matrixStack, closeX, closeY, 20, 20, ColorUtil.getColor(255, 50, 50, 180), Round.of(4));
        Fonts.SF_BOLD.draw(matrixStack, "X", closeX + 7, closeY + 6, ColorUtil.getColor(255, 255, 255), 10);
    }

    private void renderSlider(MatrixStack matrixStack, String name, SliderValue value, float min, float max,
                              float x, float y, float width, int mouseX, int mouseY) {
        Fonts.SF_REGULAR.draw(matrixStack, name, x, y, ColorUtil.getColor(255, 255, 255), 10);

        float sliderY = y + 15;
        float sliderHeight = 4;

        RenderUtil.Rounded.roundedRect(matrixStack, x, sliderY, width, sliderHeight,
                ColorUtil.getColor(40, 40, 50), Round.of(2));

        float progress = (value.getValue() - min) / (max - min);
        float filledWidth = width * progress;

        RenderUtil.Rounded.roundedRect(matrixStack, x, sliderY, filledWidth, sliderHeight,
                Theme.getInstance().clientColor(), Round.of(2));

        float handleX = x + filledWidth - 4;
        RenderUtil.Rounded.roundedRect(matrixStack, handleX, sliderY - 3, 8, 10,
                Theme.getInstance().clientColor(), Round.of(4));

        String valueText = String.format("%.1f", value.getValue());
        float valueX = x + width - Fonts.SF_REGULAR.getWidth(valueText, 9);
        Fonts.SF_REGULAR.draw(matrixStack, valueText, valueX, y, ColorUtil.getColor(180, 180, 180), 9);

        if (value.isDragging() && Mathf.isHover(mouseX, mouseY, x, sliderY - 5, width, sliderHeight + 10)) {
            float newProgress = Math.max(0, Math.min(1, (mouseX - x) / width));
            value.setValue(min + (max - min) * newProgress);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (showSettings && selectedElement != null) {
            float closeX = settingsPanelX + settingsPanelWidth - 25;
            float closeY = settingsPanelY + 5;
            if (Mathf.isHover((float) mouseX, (float) mouseY, closeX, closeY, 20, 20)) {
                showSettings = false;
                saveConfig();
                return true;
            }

            if (Mathf.isHover((float) mouseX, (float) mouseY, settingsPanelX, settingsPanelY, settingsPanelWidth, settingsPanelHeight)) {
                HudConfig config = elementConfigs.get(selectedElement.name());
                if (config != null) {
                    float y = settingsPanelY + 40 + scrollOffset;
                    checkSliderClick(config.fontSize, settingsPanelX + 10, y + 15, settingsPanelWidth - 20, (float) mouseX, (float) mouseY);
                    y += 40;
                    checkSliderClick(config.iconSize, settingsPanelX + 10, y + 15, settingsPanelWidth - 20, (float) mouseX, (float) mouseY);
                    y += 40;
                    checkSliderClick(config.spacing, settingsPanelX + 10, y + 15, settingsPanelWidth - 20, (float) mouseX, (float) mouseY);
                    y += 40;
                    checkSliderClick(config.scale, settingsPanelX + 10, y + 15, settingsPanelWidth - 20, (float) mouseX, (float) mouseY);
                    y += 40;
                    checkSliderClick(config.opacity, settingsPanelX + 10, y + 15, settingsPanelWidth - 20, (float) mouseX, (float) mouseY);
                }
                return true;
            }
        }

        for (HudElement element : hudElements) {
            float x = element.dragSetting().position.x;
            float y = element.dragSetting().position.y;
            float width = element.dragSetting().size.x;
            float height = element.dragSetting().size.y;

            if (Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, height)) {
                if (button == 0) {
                    selectedElement = element;
                    draggingElement = element;
                    dragOffsetX = (float) mouseX - x;
                    dragOffsetY = (float) mouseY - y;
                } else if (button == 1) {
                    selectedElement = element;
                    showSettings = true;
                    settingsPanelX = (float) mouseX;
                    settingsPanelY = (float) mouseY;
                    if (settingsPanelX + settingsPanelWidth > width) {
                        settingsPanelX = width - settingsPanelWidth - 10;
                    }
                    if (settingsPanelY + settingsPanelHeight > height) {
                        settingsPanelY = height - settingsPanelHeight - 10;
                    }
                }
                return true;
            }
        }

        selectedElement = null;
        showSettings = false;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void checkSliderClick(SliderValue value, float x, float y, float width, float mouseX, float mouseY) {
        if (Mathf.isHover(mouseX, mouseY, x, y - 5, width, 14)) {
            value.setDragging(true);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        draggingElement = null;

        if (elementConfigs.containsKey(selectedElement != null ? selectedElement.name() : "")) {
            HudConfig config = elementConfigs.get(selectedElement.name());
            config.fontSize.setDragging(false);
            config.iconSize.setDragging(false);
            config.spacing.setDragging(false);
            config.scale.setDragging(false);
            config.opacity.setDragging(false);
        }

        saveConfig();
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (draggingElement != null) {
            draggingElement.dragSetting().position.x = (float) mouseX - dragOffsetX;
            draggingElement.dragSetting().position.y = (float) mouseY - dragOffsetY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (showSettings && Mathf.isHover((float) mouseX, (float) mouseY, settingsPanelX, settingsPanelY, settingsPanelWidth, settingsPanelHeight)) {
            targetScrollOffset += (float) delta * 10;
            targetScrollOffset = Math.max(Math.min(targetScrollOffset, 0), -100);
            scrollOffset = (float) Mathf.lerp(scrollOffset, targetScrollOffset, 0.2f);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
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
        saveConfig();
        minecraft.displayScreen(null);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void saveConfig() {
        try {
            Files.createDirectories(configPath.getParent());
            Map<String, HudConfigData> configData = new HashMap<>();

            for (Map.Entry<String, HudConfig> entry : elementConfigs.entrySet()) {
                HudConfig config = entry.getValue();
                HudConfigData data = new HudConfigData();
                data.fontSize = config.fontSize.getValue();
                data.iconSize = config.iconSize.getValue();
                data.spacing = config.spacing.getValue();
                data.scale = config.scale.getValue();
                data.opacity = config.opacity.getValue();
                configData.put(entry.getKey(), data);
            }

            for (HudElement element : hudElements) {
                String name = element.name();
                HudConfigData data = configData.computeIfAbsent(name, k -> new HudConfigData());
                data.x = element.dragSetting().position.x;
                data.y = element.dragSetting().position.y;
            }

            try (Writer writer = Files.newBufferedWriter(configPath)) {
                gson.toJson(configData, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                Map<String, HudConfigData> configData = gson.fromJson(reader,
                        new com.google.gson.reflect.TypeToken<Map<String, HudConfigData>>() {
                        }.getType());

                if (configData != null) {
                    for (Map.Entry<String, HudConfigData> entry : configData.entrySet()) {
                        HudConfigData data = entry.getValue();
                        HudConfig config = elementConfigs.computeIfAbsent(entry.getKey(), k -> new HudConfig());
                        config.fontSize.setValue(data.fontSize);
                        config.iconSize.setValue(data.iconSize);
                        config.spacing.setValue(data.spacing);
                        config.scale.setValue(data.scale);
                        config.opacity.setValue(data.opacity);

                        for (HudElement element : hudElements) {
                            if (element.name().equals(entry.getKey())) {
                                element.dragSetting().position.x = data.x;
                                element.dragSetting().position.y = data.y;
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private record HudElement(String name, IRenderer renderer, DragSetting dragSetting) {

        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                renderer.render(new Render2DEvent(
                        matrixStack,
                        Minecraft.getInstance().gameRenderer.getActiveRenderInfo(),
                        Minecraft.getInstance().getMainWindow(),
                        partialTicks
                ));
            }
        }

    private static class HudConfig {
        public final SliderValue fontSize = new SliderValue(10);
        public final SliderValue iconSize = new SliderValue(16);
        public final SliderValue spacing = new SliderValue(5);
        public final SliderValue scale = new SliderValue(1.0f);
        public final SliderValue opacity = new SliderValue(1.0f);
    }

    private static class HudConfigData {
        public float fontSize = 10;
        public float iconSize = 16;
        public float spacing = 5;
        public float scale = 1.0f;
        public float opacity = 1.0f;
        public float x = 0;
        public float y = 0;
    }

    private static class SliderValue {
        private float value;
        private boolean dragging;

        public SliderValue(float defaultValue) {
            this.value = defaultValue;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public boolean isDragging() {
            return dragging;
        }

        public void setDragging(boolean dragging) {
            this.dragging = dragging;
        }
    }
}