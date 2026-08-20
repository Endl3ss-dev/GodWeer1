package org.privates.client.screen.csgui;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.render.font.Fonts;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;

@Getter
public class WexsideGui extends Screen implements IMinecraft {

    private final ArrayList<Category> categories = new ArrayList<>();

    private float x, y, width, height;
    private float startX, startY;
    private boolean drag = false;
    @Setter
    private Category selectedCategory;
    @Setter
    private float selectedY, selectedVY;
    private int id;
    private int color;
    private float interpolate;
    private float scale, scaleV;
    private boolean input = false;
    private String find = "";
    private float findY, findYV;

    public WexsideGui() {
        super(new StringTextComponent(""));
    }

    @Override
    protected void init() {
        System.out.println("DeadlyGUI: Инициализация начата");

        if (x == 0 || y == 0 || width == 0 || height == 0) {
            width = calc(1000); // Увеличена ширина для двух колонок
            height = calc(600);  // Увеличена высота
            x = mc.getMainWindow().getScaledWidth() / 2f - width / 2f;
            y = mc.getMainWindow().getScaledHeight() / 2f - height / 2f;
            System.out.println("DeadlyGUI: Размеры установлены - " + width + "x" + height + " на " + x + "," + y);
        }

        if (categories.isEmpty()) {
            System.out.println("DeadlyGUI: Загружаем категории...");
            float offsetY = y + 120; // Сдвинули категории ниже для заголовка
            for (org.privates.client.managers.module.Category c : org.privates.client.managers.module.Category.values()) {
                Category category = new Category(this, c, x + 30, offsetY);
                categories.add(category);
                offsetY += category.getHeight() + 8;
                System.out.println("DeadlyGUI: Добавлена категория " + c.getName());
            }
        }

        if (selectedCategory == null && !categories.isEmpty()) {
            selectedCategory = categories.get(0);
            System.out.println("DeadlyGUI: Выбрана категория по умолчанию: " + selectedCategory.getCategory().getName());
        }

        selectedY = selectedCategory != null ? selectedCategory.getY() : y + 120;
        selectedVY = selectedY;
        scale = 1;
        scaleV = 1; // Убираем анимацию масштаба для отладки

        Theme theme = Theme.getInstance();
        if (color == 0) {
            color = theme != null ? theme.clientColor() : ColorUtil.getColor(255, 107, 107);
            interpolate = 1;
        }
        id = 0;

        System.out.println("DeadlyGUI: Инициализация завершена. Категорий: " + categories.size());
        super.init();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Убеждаемся что все переменные инициализированы
        if (selectedCategory != null && selectedY != selectedVY)
            selectedVY = (float) Mathf.lerp(selectedVY, selectedY, 12f);

        if (interpolate != 1f)
            interpolate = (float) Mathf.lerp(interpolate, 1f, 0.2f);

        if (scale != scaleV)
            scaleV = (float) Mathf.lerp(scaleV, scale, 16f);

        if (findY != findYV)
            findYV = (float) Mathf.lerp(findYV, findY, 8f);

        if (scale == 0 && scaleV <= 0.05f) {
            super.closeScreen();
            return;
        }

        Theme theme = Theme.getInstance();

        // Простой фон без блюра (для отладки)
        fill(matrixStack, 0, 0, mc.getMainWindow().getScaledWidth(),
                mc.getMainWindow().getScaledHeight(), ColorUtil.getColor(0, 0, 0, 150));

        // Убираем масштабирование для отладки
        // GlStateManager.pushMatrix();
        // GlStateManager.translatef((x + width / 2f), (y + height / 2f), 1);
        // GlStateManager.scalef(scaleV, scaleV, scaleV);
        // GlStateManager.translatef(-(x + width / 2f), -(y + height / 2f), 1);

        // Основное окно
        RenderUtil.Rounded.roundedRect(matrixStack, x, y, width, height,
                ColorUtil.getColor(12, 12, 17, 255), Round.of(8));

        // Простая отладочная рамка
        fill(matrixStack, (int) x, (int) y, (int) (x + width), (int) (y + 5),
                ColorUtil.getColor(255, 107, 107, 255));

        // Заголовок "Deadly" - упрощенный
        if (Fonts.SF_BOLD != null) {
            Fonts.SF_BOLD.draw(matrixStack, "Deadly", x + 40, y + 30,
                    ColorUtil.getColor(255, 107, 107), 32);
        } else {
            // Fallback если шрифт не найден
            drawStringWithShadow(matrixStack, mc.fontRenderer, "Deadly", (int) (x + 40), (int) (y + 30),
                    ColorUtil.getColor(255, 107, 107));
        }

        // Верхняя панель с поиском и профилем
        renderTopPanel(matrixStack, mouseX, mouseY);

        // Боковая панель с категориями
        renderSidebar(matrixStack, mouseX, mouseY);

        // Основной контент с модулями
        renderMainContent(matrixStack, mouseX, mouseY);

        // GlStateManager.popMatrix();

        if (drag) {
            x += mouseX - startX;
            y += mouseY - startY;
            selectedY += mouseY - startY;
            selectedVY += mouseY - startY;

            for (Category category : categories) {
                category.setX(category.getX() + mouseX - startX);
                category.setY(category.getY() + mouseY - startY);
                for (Module module : category.getModules()) {
                    module.setX(module.getX() + mouseX - startX);
                    module.setY(module.getY() + mouseY - startY);
                    for (Setting setting : module.getSettings()) {
                        setting.setX(setting.getX() + mouseX - startX);
                        setting.setY(setting.getY() + mouseY - startY);
                    }
                }
            }

            startX = mouseX;
            startY = mouseY;
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    private void renderTopPanel(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Простая отладочная панель
        fill(matrixStack, (int) (x + 200), (int) (y + 10), (int) (x + width - 200), (int) (y + 80),
                ColorUtil.getColor(20, 20, 25, 200));

        // Название категории в центре
        if (selectedCategory != null) {
            String categoryName = selectedCategory.getCategory().getName().substring(0, 1).toUpperCase() +
                    selectedCategory.getCategory().getName().substring(1).toLowerCase();
            float centerX = x + width / 2f - mc.fontRenderer.getStringWidth(categoryName) / 2f;

            drawStringWithShadow(matrixStack, mc.fontRenderer, categoryName, (int) centerX, (int) (y + 30),
                    ColorUtil.getColor(180, 180, 180));
        }

        // Простая область поиска
        float searchX = x + width / 2f - 80;
        float searchY = y + 55;
        fill(matrixStack, (int) searchX, (int) searchY, (int) (searchX + 160), (int) (searchY + 25),
                ColorUtil.getColor(20, 20, 25, 150));

        String searchText = find.isEmpty() && !input ? "Поиск..." : find;
        drawStringWithShadow(matrixStack, mc.fontRenderer, searchText, (int) (searchX + 25), (int) (searchY + 8),
                find.isEmpty() && !input ? ColorUtil.getColor(120, 120, 120) : Color.WHITE.getRGB());

        // Профиль справа
        float profileX = x + width - 180;
        drawStringWithShadow(matrixStack, mc.fontRenderer, ".akk1o", (int) profileX, (int) (y + 25), Color.WHITE.getRGB());
        drawStringWithShadow(matrixStack, mc.fontRenderer, "till: lifetime", (int) profileX, (int) (y + 38),
                ColorUtil.getColor(120, 120, 120));

        // Простой аватар
        fill(matrixStack, (int) (x + width - 50), (int) (y + 20), (int) (x + width - 20), (int) (y + 50),
                ColorUtil.getColor(255, 107, 107, 255));
    }

    private void renderSidebar(MatrixStack matrixStack, int mouseX, int mouseY) {
        float sidebarWidth = 250;

        // Фон сайдбара
        fill(matrixStack, (int) (x + 20), (int) (y + 100), (int) (x + 20 + sidebarWidth), (int) (y + height - 20),
                ColorUtil.getColor(18, 18, 25, 200));

        // Рендерим категории
        for (Category category : categories) {
            if (category != null) {
                category.render(matrixStack, mouseX, mouseY);
            }
        }
    }

    private void renderMainContent(MatrixStack matrixStack, int mouseX, int mouseY) {
        if (selectedCategory == null) return;

        float contentX = x + 290;
        float contentY = y + 100;
        float contentWidth = width - 310;
        float contentHeight = height - 120;

        // Фон контента
        fill(matrixStack, (int) contentX, (int) contentY, (int) (contentX + contentWidth), (int) (contentY + contentHeight),
                ColorUtil.getColor(18, 18, 25, 100));

        // Простая отладочная информация
        String debugText = "Modules: " + selectedCategory.getModules().size();
        drawStringWithShadow(matrixStack, mc.fontRenderer, debugText, (int) (contentX + 10), (int) (contentY + 10),
                Color.WHITE.getRGB());

        // Рендерим модули (упрощенно)
        float moduleWidth = (contentWidth - 30) / 2;
        float moduleX1 = contentX + 10;
        float moduleX2 = contentX + moduleWidth + 20;
        float moduleY = contentY + 30;

        int moduleIndex = 0;
        for (Module module : selectedCategory.getModules()) {
            if (module != null) {
                if (moduleIndex % 2 == 0) {
                    module.setX(moduleX1);
                    module.setY(moduleY + selectedCategory.getDelta());
                    module.setWidth(moduleWidth);
                } else {
                    module.setX(moduleX2);
                    module.setY(moduleY + selectedCategory.getDelta());
                    module.setWidth(moduleWidth);
                    moduleY += 100; // Фиксированная высота для отладки
                }

                // Простая отрисовка модуля
                fill(matrixStack, (int) module.getX(), (int) module.getY(),
                        (int) (module.getX() + module.getWidth()), (int) (module.getY() + 80),
                        ColorUtil.getColor(30, 30, 40, 200));

                drawStringWithShadow(matrixStack, mc.fontRenderer, module.getFunction().getName(),
                        (int) (module.getX() + 10), (int) (module.getY() + 10), Color.WHITE.getRGB());

                moduleIndex++;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        startX = (float) mouseX;
        startY = (float) mouseY;

        // Перетаскивание за заголовок
        if (Mathf.isHover((float) mouseX, (float) mouseY, x, y, width, 80) && button == 0) {
            drag = true;
        }

        // Клик по поиску
        float searchX = x + width / 2f - 80;
        float searchY = y + 55;
        if (Mathf.isHover((float) mouseX, (float) mouseY, searchX, searchY, 160, 25)) {
            input = !input;
        }

        for (Category category : categories)
            category.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_PRESS);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        drag = false;

        for (Category category : categories)
            category.mouse((float) mouseX, (float) mouseY, button, GLFW.GLFW_RELEASE);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float contentX = x + 290;
        float contentY = y + 100;
        float contentWidth = width - 310;
        float contentHeight = height - 120;

        if (Mathf.isHover((float) mouseX, (float) mouseY, contentX, contentY, contentWidth, contentHeight) &&
                selectedCategory != null) {
            selectedCategory.setDelta((float) (selectedCategory.getDelta() + delta * 15));
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (selectedCategory != null) {
            for (Module module : selectedCategory.getModules()) {
                module.keyPressed(keyCode);
                for (Setting setting : module.getSettings()) {
                    setting.keyPressed(keyCode);
                }
            }
        }

        if (input) {
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                find = find.isEmpty() ? "" : find.substring(0, find.length() - 1);
            } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_ESCAPE) {
                input = false;
                find = "";
            }
            if (selectedCategory != null)
                selectedCategory.loadModules(find);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (selectedCategory != null) {
            for (Module module : selectedCategory.getModules()) {
                for (Setting setting : module.getSettings()) {
                    setting.charTyped(codePoint);
                }
            }
        }

        if (input) {
            find += codePoint;
            if (selectedCategory != null)
                selectedCategory.loadModules(find);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void closeScreen() {
        scale = 0;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private float calc(float value) {
        return value;
    }

    private boolean isInRange(float y, float height, float parentY, float parentHeight) {
        return y + height >= parentY && y <= parentY + parentHeight;
    }

    public float getScaleV() {
        return scaleV;
    }
}