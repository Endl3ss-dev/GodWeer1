package org.privates.client.screen.customvisual.components;

import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;

import java.util.function.Consumer;
import java.util.function.Supplier;

// Базовый класс для всех компонентов свойств
public abstract class PropertyComponent {
    protected final String name;

    public PropertyComponent(String name) {
        this.name = name;
    }

    public abstract void render(MatrixStack matrix, float x, float y, float width, float height, int mouseX, int mouseY);

    public abstract boolean mouseClicked(double mouseX, double mouseY, float x, float y, float width, float height);

    protected boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}

// Категория свойств (заголовок секции)
class CategoryComponent extends PropertyComponent {
    public CategoryComponent(String name) {
        super(name);
    }

    @Override
    public void render(MatrixStack matrix, float x, float y, float width, float height, int mouseX, int mouseY) {
        // Фон категории
        RenderUtil.Rounded.roundedRect(matrix, x, y + 5, width, height - 10,
                ColorUtil.getColor(40, 40, 50, 150), Round.of(3));

        // Название категории
        Fonts.SF_MEDIUM.draw(matrix, name, x + 8, y + height / 2 - 4,
                Theme.getInstance().clientColor(), 8);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, float x, float y, float width, float height) {
        return false;
    }
}

// Булевское свойство (чекбокс)
class BooleanPropertyComponent extends PropertyComponent {
    private final Supplier<Boolean> getter;
    private final Consumer<Boolean> setter;

    public BooleanPropertyComponent(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(name);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void render(MatrixStack matrix, float x, float y, float width, float height, int mouseX, int mouseY) {
        boolean value = getter.get();
        boolean hovered = isHovered(mouseX, mouseY, x, y, width, height);

        // Фон
        if (hovered) {
            RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                    ColorUtil.getColor(255, 255, 255, 20), Round.of(3));
        }

        // Название свойства
        Fonts.SF_MEDIUM.draw(matrix, name, x + 5, y + height / 2 - 4,
                ColorUtil.getColor(255, 255, 255), 8);

        // Чекбокс
        float checkboxSize = 12f;
        float checkboxX = x + width - checkboxSize - 5;
        float checkboxY = y + (height - checkboxSize) / 2;

        int checkboxColor = value ? Theme.getInstance().clientColor() : ColorUtil.getColor(60, 60, 60);
        RenderUtil.Rounded.roundedRect(matrix, checkboxX, checkboxY, checkboxSize, checkboxSize,
                checkboxColor, Round.of(3));

        if (value) {
            // Галочка
            Fonts.SF_MEDIUM.draw(matrix, "✓", checkboxX + 2, checkboxY + 1,
                    ColorUtil.getColor(255, 255, 255), 8);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, float x, float y, float width, float height) {
        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            setter.accept(!getter.get());
            return true;
        }
        return false;
    }
}

// Числовое свойство (слайдер)
class NumberPropertyComponent extends PropertyComponent {
    private final Supplier<Float> getter;
    private final Consumer<Float> setter;
    private final float min, max;
    private boolean dragging = false;

    public NumberPropertyComponent(String name, Supplier<Float> getter, Consumer<Float> setter, float min, float max) {
        super(name);
        this.getter = getter;
        this.setter = setter;
        this.min = min;
        this.max = max;
    }

    @Override
    public void render(MatrixStack matrix, float x, float y, float width, float height, int mouseX, int mouseY) {
        float value = getter.get();
        boolean hovered = isHovered(mouseX, mouseY, x, y, width, height);

        // Фон
        if (hovered || dragging) {
            RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                    ColorUtil.getColor(255, 255, 255, 20), Round.of(3));
        }

        // Название свойства
        Fonts.SF_MEDIUM.draw(matrix, name, x + 5, y + 3,
                ColorUtil.getColor(255, 255, 255), 8);

        // Значение
        String valueText = String.format("%.1f", value);
        Fonts.SF_MEDIUM.draw(matrix, valueText, x + width - 40, y + 3,
                ColorUtil.getColor(200, 200, 200), 8);

        // Слайдер
        float sliderWidth = width - 10;
        float sliderHeight = 3f;
        float sliderX = x + 5;
        float sliderY = y + height - 8;

        // Фон слайдера
        RenderUtil.Rounded.roundedRect(matrix, sliderX, sliderY, sliderWidth, sliderHeight,
                ColorUtil.getColor(60, 60, 60), Round.of(1.5f));

        // Заполненная часть
        float progress = (value - min) / (max - min);
        RenderUtil.Rounded.roundedRect(matrix, sliderX, sliderY, sliderWidth * progress, sliderHeight,
                Theme.getInstance().clientColor(), Round.of(1.5f));

        // Ползунок
        float thumbSize = 6f;
        float thumbX = sliderX + (sliderWidth * progress) - thumbSize / 2;
        RenderUtil.Rounded.roundedRect(matrix, thumbX, sliderY - 1.5f, thumbSize, thumbSize,
                ColorUtil.getColor(255, 255, 255), Round.of(3));

        // Обработка перетаскивания
        if (dragging) {
            float newProgress = Math.max(0, Math.min(1, (mouseX - sliderX) / sliderWidth));
            float newValue = min + newProgress * (max - min);
            setter.accept(newValue);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, float x, float y, float width, float height) {
        float sliderY = y + height - 8;
        if (mouseY >= sliderY - 3 && mouseY <= sliderY + 6) {
            dragging = true;
            return true;
        }
        return false;
    }

    public void mouseReleased() {
        dragging = false;
    }
}

// Текстовое свойство (поле ввода)
class TextPropertyComponent extends PropertyComponent {
    private final Supplier<String> getter;
    private final Consumer<String> setter;
    private boolean editing = false;
    private String editText = "";

    public TextPropertyComponent(String name, Supplier<String> getter, Consumer<String> setter) {
        super(name);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void render(MatrixStack matrix, float x, float y, float width, float height, int mouseX, int mouseY) {
        String value = editing ? editText : getter.get();
        boolean hovered = isHovered(mouseX, mouseY, x, y, width, height);

        // Фон
        int bgColor = editing ? ColorUtil.getColor(40, 40, 50, 200) :
                hovered ? ColorUtil.getColor(255, 255, 255, 20) : ColorUtil.getColor(0, 0, 0, 0);
        if (bgColor != ColorUtil.getColor(0, 0, 0, 0)) {
            RenderUtil.Rounded.roundedRect(matrix, x, y, width, height, bgColor, Round.of(3));
        }

        // Граница при редактировании
        if (editing) {
            RenderUtil.Rounded.roundedOutline(matrix, x, y, width, height, 1f,
                    Theme.getInstance().clientColor(), Round.of(3));
        }

        // Название свойства
        Fonts.SF_MEDIUM.draw(matrix, name, x + 5, y + 3,
                ColorUtil.getColor(255, 255, 255), 8);

        // Значение
        String displayText = value;
        if (displayText.length() > 15) {
            displayText = displayText.substring(0, 12) + "...";
        }

        int textColor = editing ? ColorUtil.getColor(255, 255, 255) : ColorUtil.getColor(200, 200, 200);
        Fonts.SF_MEDIUM.draw(matrix, displayText, x + 5, y + height - 12, textColor, 8);

        // Курсор при редактировании
        if (editing && System.currentTimeMillis() % 1000 < 500) {
            float cursorX = x + 5 + Fonts.SF_MEDIUM.getWidth(editText, 8);
            RenderUtil.Rounded.roundedRect(matrix, cursorX, y + height - 12, 1, 8,
                    ColorUtil.getColor(255, 255, 255), Round.of(0.5f));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, float x, float y, float width, float height) {
        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            if (!editing) {
                editing = true;
                editText = getter.get();
            }
            return true;
        } else if (editing) {
            editing = false;
            setter.accept(editText);
        }
        return false;
    }

    public boolean keyPressed(int keyCode, char typedChar) {
        if (!editing) return false;

        if (keyCode == 257) { // Enter
            editing = false;
            setter.accept(editText);
            return true;
        } else if (keyCode == 256) { // Escape
            editing = false;
            editText = getter.get();
            return true;
        } else if (keyCode == 259) { // Backspace
            if (!editText.isEmpty()) {
                editText = editText.substring(0, editText.length() - 1);
            }
            return true;
        } else if (Character.isLetterOrDigit(typedChar) || Character.isSpaceChar(typedChar) ||
                "!@#$%^&*()_+-=[]{}|;':\",./<>?".indexOf(typedChar) != -1) {
            editText += typedChar;
            return true;
        }

        return false;
    }
}

// Цветовое свойство (цветовая палитра)
class ColorPropertyComponent extends PropertyComponent {
    private final Supplier<Integer> getter;
    private final Consumer<Integer> setter;
    private boolean showPicker = false;

    public ColorPropertyComponent(String name, Supplier<Integer> getter, Consumer<Integer> setter) {
        super(name);
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void render(MatrixStack matrix, float x, float y, float width, float height, int mouseX, int mouseY) {
        int color = getter.get();
        boolean hovered = isHovered(mouseX, mouseY, x, y, width, height);

        // Фон
        if (hovered) {
            RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                    ColorUtil.getColor(255, 255, 255, 20), Round.of(3));
        }

        // Название свойства
        Fonts.SF_MEDIUM.draw(matrix, name, x + 5, y + height / 2 - 4,
                ColorUtil.getColor(255, 255, 255), 8);

        // Цветовой образец
        float colorSize = 16f;
        float colorX = x + width - colorSize - 5;
        float colorY = y + (height - colorSize) / 2;

        // Фон для прозрачности (шахматная доска)
        RenderUtil.Rounded.roundedRect(matrix, colorX, colorY, colorSize, colorSize,
                ColorUtil.getColor(200, 200, 200), Round.of(3));
        RenderUtil.Rounded.roundedRect(matrix, colorX, colorY, colorSize / 2, colorSize / 2,
                ColorUtil.getColor(100, 100, 100), Round.of(3, 0, 0, 0));
        RenderUtil.Rounded.roundedRect(matrix, colorX + colorSize / 2, colorY + colorSize / 2, colorSize / 2, colorSize / 2,
                ColorUtil.getColor(100, 100, 100), Round.of(0, 0, 3, 0));

        // Цвет
        RenderUtil.Rounded.roundedRect(matrix, colorX, colorY, colorSize, colorSize, color, Round.of(3));

        // Рамка
        RenderUtil.Rounded.roundedOutline(matrix, colorX, colorY, colorSize, colorSize, 1f,
                ColorUtil.getColor(255, 255, 255, 100), Round.of(3));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, float x, float y, float width, float height) {
        if (isHovered(mouseX, mouseY, x, y, width, height)) {
            showPicker = !showPicker;
            // Здесь можно открыть цветовую палитру
            return true;
        }
        return false;
    }
}

// Перечислимое свойство (выпадающий список)
class EnumPropertyComponent extends PropertyComponent {
    private final Supplier<String> getter;
    private final Consumer<String> setter;
    private final String[] options;
    private boolean showDropdown = false;

    public EnumPropertyComponent(String name, Supplier<String> getter, Consumer<String> setter, String[] options) {
        super(name);
        this.getter = getter;
        this.setter = setter;
        this.options = options;
    }

    @Override
    public void render(MatrixStack matrix, float x, float y, float width, float height, int mouseX, int mouseY) {
        String value = getter.get();
        boolean hovered = isHovered(mouseX, mouseY, x, y, width, height);

        // Фон
        if (hovered || showDropdown) {
            RenderUtil.Rounded.roundedRect(matrix, x, y, width, height,
                    ColorUtil.getColor(255, 255, 255, 20), Round.of(3));
        }

        // Название свойства
        Fonts.SF_MEDIUM.draw(matrix, name, x + 5, y + 3,
                ColorUtil.getColor(255, 255, 255), 8);

        // Текущее значение
        Fonts.SF_MEDIUM.draw(matrix, value, x + 5, y + height - 12,
                ColorUtil.getColor(200, 200, 200), 8);

        // Стрелка
        String arrow = showDropdown ? "▲" : "▼";
        Fonts.SF_MEDIUM.draw(matrix, arrow, x + width - 15, y + height / 2 - 4,
                ColorUtil.getColor(150, 150, 150), 8);

        // Выпадающий список
        if (showDropdown) {
            float dropdownY = y + height + 2;
            float optionHeight = 20f;
            float dropdownHeight = options.length * optionHeight;

            // Фон выпадающего списка
            RenderUtil.Rounded.roundedRect(matrix, x, dropdownY, width, dropdownHeight,
                    ColorUtil.getColor(30, 30, 40, 220), Round.of(4));
            RenderUtil.Rounded.roundedOutline(matrix, x, dropdownY, width, dropdownHeight, 1f,
                    ColorUtil.getColor(60, 60, 80), Round.of(4));

            // Опции
            for (int i = 0; i < options.length; i++) {
                float optionY = dropdownY + i * optionHeight;
                boolean optionHovered = mouseY >= optionY && mouseY <= optionY + optionHeight &&
                        mouseX >= x && mouseX <= x + width;
                boolean isSelected = options[i].equals(value);

                if (optionHovered) {
                    RenderUtil.Rounded.roundedRect(matrix, x + 2, optionY + 1, width - 4, optionHeight - 2,
                            ColorUtil.getColor(255, 255, 255, 30), Round.of(3));
                } else if (isSelected) {
                    RenderUtil.Rounded.roundedRect(matrix, x + 2, optionY + 1, width - 4, optionHeight - 2,
                            ColorUtil.multAlpha(Theme.getInstance().clientColor(), 0.3f), Round.of(3));
                }

                int textColor = isSelected ? Theme.getInstance().clientColor() : ColorUtil.getColor(255, 255, 255);
                Fonts.SF_MEDIUM.draw(matrix, options[i], x + 8, optionY + optionHeight / 2 - 4, textColor, 8);
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, float x, float y, float width, float height) {
        if (showDropdown) {
            // Проверяем клики по опциям
            float dropdownY = y + height + 2;
            float optionHeight = 20f;

            for (int i = 0; i < options.length; i++) {
                float optionY = dropdownY + i * optionHeight;
                if (mouseY >= optionY && mouseY <= optionY + optionHeight &&
                        mouseX >= x && mouseX <= x + width) {
                    setter.accept(options[i]);
                    showDropdown = false;
                    return true;
                }
            }
            showDropdown = false;
        } else if (isHovered(mouseX, mouseY, x, y, width, height)) {
            showDropdown = true;
            return true;
        }
        return false;
    }
}