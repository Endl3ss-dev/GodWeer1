package org.privates.client.screen.csgui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.keyboard.Keyboard;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Module implements Element {

    private final Category category;
    private final org.privates.client.managers.module.Module function;
    private final ArrayList<Setting> settings = new ArrayList<>();

    @Setter
    private float x, y, height, width;
    @Setter
    private float delta;
    private float interpolate;
    private boolean input;

    public Module(Category category, org.privates.client.managers.module.Module function, float x, float y) {
        this.category = category;
        this.function = function;
        this.x = x;
        this.y = y;
        this.width = 280; // Ширина будет установлена в Category.recalculate()
        this.input = false;
        this.height = calculateModuleHeight();
        this.interpolate = function.isEnabled() ? 1 : 0;

        loadSettings();
    }

    private void loadSettings() {
        settings.clear();
        float offsetY = y + 60; // Начинаем настройки после заголовка модуля

        for (org.privates.client.managers.module.settings.Setting<?> s : function.getSettings()) {
            Setting setting = new Setting(this, s, x + 10, offsetY);
            settings.add(setting);
            offsetY += setting.getHeight() + 5;
        }

        height = offsetY - y + 10; // Финальная высота модуля
    }

    private float calculateModuleHeight() {
        float baseHeight = 60; // Базовая высота для заголовка
        float settingsHeight = 0;

        for (org.privates.client.managers.module.settings.Setting<?> s : function.getSettings()) {
            settingsHeight += getSettingHeight(s) + 5;
        }

        return baseHeight + settingsHeight + 10;
    }

    private float getSettingHeight(org.privates.client.managers.module.settings.Setting<?> setting) {
        if (setting instanceof org.privates.client.managers.module.settings.impl.BooleanSetting) {
            return 20;
        } else if (setting instanceof org.privates.client.managers.module.settings.impl.SliderSetting) {
            return 35;
        } else if (setting instanceof org.privates.client.managers.module.settings.impl.ModeSetting) {
            return 25;
        } else if (setting instanceof org.privates.client.managers.module.settings.impl.StringSetting) {
            return 30;
        }
        return 20;
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        if (interpolate != (function.isEnabled() ? 1 : 0))
            interpolate = (float) Mathf.lerp(interpolate, function.isEnabled() ? 1 : 0, 20);

        Theme theme = Theme.getInstance();

        // Фон модуля
        RenderUtil.Rounded.roundedRect(stack, x, y + delta, width, height,
                ColorUtil.getColor(22, 22, 30, 200), Round.of(8));
        RenderUtil.Rounded.roundedOutline(stack, x, y + delta, width, height, 8f,
                ColorUtil.getColor(35, 35, 45, 150), Round.of(8));

        // Заголовок модуля
        String moduleText = function.getName() +
                (input ? "[...]" : (function.getKey() > 0 ? " [" + Keyboard.keyName(function.getKey()) + "]" : ""));

        Fonts.SF_MEDIUM.draw(stack, moduleText, x + 15, y + 15 + delta,
                Color.WHITE.getRGB(), 14);

        // Кнопка включения в правом верхнем углу
        float toggleX = x + width - 40;
        float toggleY = y + 15 + delta;

        // Фон переключателя
        Color enabledColor = new Color(theme.clientColor());
        Color disabledColor = new Color(60, 60, 70);
        Color switchColor = interpolateColor(enabledColor, disabledColor, interpolate);

        RenderUtil.Rounded.roundedRect(stack, toggleX, toggleY, 25, 12,
                switchColor.getRGB(), Round.of(6));

        // Индикатор переключателя
        float indicatorX = toggleX + 2 + (interpolate * 11);
        Color activeColor = new Color(255, 255, 255);
        Color inactiveColor = new Color(200, 200, 200);
        Color indicatorColor = interpolateColor(activeColor, inactiveColor, interpolate);

        RenderUtil.Rounded.roundedRect(stack, indicatorX, toggleY + 2, 8, 8,
                indicatorColor.getRGB(), Round.of(4));

        // Текст "Enabled"
        Fonts.SF_MEDIUM.draw(stack, "Enabled", x + 15, y + 35 + delta,
                ColorUtil.getColor(150, 150, 150), 10);

        // Рендерим настройки
        for (Setting setting : settings) {
            if (isInRange(setting.getY() + setting.getDelta(), setting.getHeight()))
                setting.render(stack, mouseX, mouseY);
        }
    }

    @Override
    public void mouse(float mouseX, float mouseY, int button, int action) {
        // Клик по переключателю модуля
        float toggleX = x + width - 40;
        float toggleY = y + 15 + delta;

        if (Mathf.isHover(mouseX, mouseY, toggleX, toggleY, 25, 12)
                && button == 0 && action == GLFW.GLFW_PRESS) {
            function.toggle();
            return;
        }

        // Клик по заголовку для биндинга (ПКМ)
        if (Mathf.isHover(mouseX, mouseY, x + 15, y + delta, width - 70, 30)
                && button == 2 && action == GLFW.GLFW_PRESS) {
            input = !input;
            return;
        }

        // Передаем клики настройкам
        for (Setting setting : settings) {
            setting.mouse(mouseX, mouseY, button, action);
        }
    }

    @Override
    public void keyPressed(int key) {
        if (input) {
            function.setKey(key != GLFW.GLFW_KEY_ESCAPE && key != GLFW.GLFW_KEY_DELETE ? key : -1);
            input = false;
        }

        for (Setting setting : settings) {
            setting.keyPressed(key);
        }
    }

    @Override
    public void charTyped(char typed) {
        for (Setting setting : settings) {
            setting.charTyped(typed);
        }
    }

    private boolean isInRange(float y, float height) {
        return y + height >= category.getGui().getY() + 100 &&
                y <= category.getGui().getY() + category.getGui().getHeight() - 20;
    }

    private List<String> wrapText(String text, float maxWidth, float fontSize) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine + (currentLine.length() > 0 ? " " : "") + word;
            if (Fonts.SF_MEDIUM.getWidth(testLine, fontSize) <= maxWidth) {
                currentLine.append(currentLine.length() > 0 ? " " : "").append(word);
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    lines.add(word);
                }
            }
        }
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        return lines;
    }

    private Color interpolateColor(Color color1, Color color2, float factor) {
        int red = (int) (color1.getRed() * factor + color2.getRed() * (1 - factor));
        int green = (int) (color1.getGreen() * factor + color2.getGreen() * (1 - factor));
        int blue = (int) (color1.getBlue() * factor + color2.getBlue() * (1 - factor));
        return new Color(red, green, blue);
    }

    // Обновляем позиции настроек при изменении позиции модуля
    public void updateSettingsPositions() {
        float offsetY = y + 55;
        for (Setting setting : settings) {
            setting.setX(x + 10);
            setting.setY(offsetY);
            offsetY += setting.getHeight() + 5;
        }
    }

    // Переопределяем сеттеры для обновления позиций настроек
    public void setX(float x) {
        this.x = x;
        updateSettingsPositions();
    }

    public void setY(float y) {
        this.y = y;
        updateSettingsPositions();
    }
}