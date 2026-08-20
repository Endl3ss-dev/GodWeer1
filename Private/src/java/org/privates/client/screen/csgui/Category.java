package org.privates.client.screen.csgui;

import lombok.Getter;
import lombok.Setter;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.Excellent;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.font.Fonts;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static net.minecraft.client.gui.AbstractGui.fill;

@Getter
public class Category implements Element {

    private final float width, height;
    private final org.privates.client.managers.module.Category category;
    private final WexsideGui gui;
    private final ArrayList<Module> modules = new ArrayList<>();

    @Setter
    private float x, y;
    @Setter
    private float offsetX, offsetVX;
    @Setter
    private float delta, deltaV;

    public Category(WexsideGui gui, org.privates.client.managers.module.Category category, float x, float y) {
        this.gui = gui;
        this.category = category;
        this.x = x;
        this.y = y;
        this.width = 210f; // Увеличена ширина для нового дизайна
        this.height = 35f; // Увеличена высота
        loadModules("");
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        if (deltaV != delta && gui.getSelectedCategory() == this) {
            deltaV = (float) Mathf.lerp(deltaV, delta, 8);
        }

        if (offsetVX != offsetX)
            offsetVX = (float) Mathf.lerp(offsetVX, offsetX, 12);

        boolean isSelected = gui.getSelectedCategory() == this;

        // Простой фон категории без закруглений
        if (isSelected) {
            fill(stack, (int) x, (int) y, (int) (x + width), (int) (y + height),
                    ColorUtil.getColor(40, 40, 50, 200));
        } else {
            fill(stack, (int) x, (int) y, (int) (x + width), (int) (y + height),
                    ColorUtil.getColor(25, 25, 35, 100));
        }

        // Иконка категории - упрощенная
        String icon = getCategoryIcon();
        if (IMinecraft.mc.fontRenderer != null) {
            IMinecraft.mc.fontRenderer.drawString(stack, icon, x + 15, y + height / 2f - 4,
                    isSelected ? ColorUtil.getColor(255, 107, 107) : ColorUtil.getColor(180, 180, 180));
        }

        // Название категории
        String categoryName = category.getName().substring(0, 1).toUpperCase() +
                category.getName().substring(1).toLowerCase();
        if (IMinecraft.mc.fontRenderer != null) {
            IMinecraft.mc.fontRenderer.drawString(stack, categoryName, x + 45, y + height / 2f - 4,
                    isSelected ? Color.WHITE.getRGB() : ColorUtil.getColor(160, 160, 160));
        }

        // Упрощенный пересчет
        if (isSelected) {
            recalculate();
        }
    }

    private String getCategoryIcon() {
        switch (category.getName().toLowerCase()) {
            case "combat":
                return "⚔";
            case "movement":
                return "⚡";
            case "render":
                return "🖥";
            case "player":
                return "👤";
            case "misc":
                return "📦";
            default:
                return "•";
        }
    }

    @Override
    public void mouse(float mouseX, float mouseY, int button, int action) {
        if (Mathf.isHover(mouseX, mouseY, x, y, width, height) && action == GLFW.GLFW_PRESS) {
            gui.setSelectedCategory(this);
            gui.setSelectedY(y);

            // Сброс offset для всех категорий
            for (Category c : gui.getCategories())
                c.setOffsetX(0);
            offsetX = 5;
        }

        if (gui.getSelectedCategory() == this) {
            for (Module module : modules)
                module.mouse(mouseX, mouseY, button, action);
        }
    }

    @Override
    public void keyPressed(int key) {
    }

    @Override
    public void charTyped(char typed) {
    }

    public float getMaxHeight() {
        float high = 0;
        for (Module module : modules) {
            if (high < module.getY() + module.getHeight() + delta)
                high = module.getY() + module.getHeight() + delta;
        }
        return high;
    }

    public void recalculate() {
        float startX = gui.getX() + 290; // Позиция контентной области
        float startY = gui.getY() + 110;
        float moduleWidth = (gui.getWidth() - 310 - 30) / 2; // Ширина одного модуля в двух колонках

        for (int i = 0; i < modules.size(); i++) {
            Module module = modules.get(i);

            if (i % 2 == 0) {
                // Первая колонка
                module.setX(startX + 10);
            } else {
                // Вторая колонка
                module.setX(startX + moduleWidth + 20);
            }

            // Позиция по Y
            int row = i / 2;
            module.setY(startY + (row * (module.getHeight() + 10)));
            module.setWidth(moduleWidth);

            // Пересчитываем настройки
            for (int j = 0; j < module.getSettings().size(); j++) {
                Setting setting = module.getSettings().get(j);
                setting.setY(
                        (j == 0) ? module.getY() + module.getHeight() :
                                module.getSettings().get(j - 1).getY() +
                                        module.getSettings().get(j - 1).getHeight() + 3
                );
                module.setHeight(module.getHeight() + setting.getHeight() + 3);
            }
        }
    }

    public void loadModules(String key) {
        modules.clear();

        List<org.privates.client.managers.module.Module> filteredModules =
                Excellent.inst().moduleManager().get(category).stream()
                        .filter(f -> (f.getCategory() == category && key.isEmpty()) ||
                                (!key.isEmpty() && f.getName().toLowerCase().contains(key.toLowerCase())))
                        .toList();

        for (int i = 0; i < filteredModules.size(); i++) {
            Module module = new Module(this, filteredModules.get(i), 0, 0); // Позиции будут пересчитаны
            modules.add(module);
        }

        recalculate();
    }

    private boolean isInRange(float y, float height) {
        return y + height >= gui.getY() + 100 && y <= gui.getY() + gui.getHeight() - 20;
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
}