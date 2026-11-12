package net.skycore.client.gui.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.skycore.client.SkycoreClient;
import net.skycore.client.gui.menu.MenuDraw;
import net.skycore.client.gui.menu.MenuPalette;
import net.skycore.client.gui.menu.MenuSound;
import net.skycore.client.gui.menu.MenuText.Weight;
import net.skycore.client.hud.Draggable;
import net.skycore.client.hud.HudRenderer;
import net.skycore.client.module.render.InterfaceModule;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.CurrentGuiGraphicsExtractorHelper;
import net.skycore.client.util.IconGlyphs;
import net.skycore.client.util.SelectAndSaveHelper;

/**
 * Редактор HUD.
 *
 * <p>Порт {@code Arcanic/gui/HudEditorScreen} (GPL-3.0) на модель HUD SkyCore.
 * В источнике элементы были модулями {@code HudModule} со своими {@code scale} и
 * {@code enabled}; в SkyCore элемент — это {@link Draggable} внутри
 * {@link HudRenderer}, а видимость задаётся мультивыбором
 * {@link InterfaceModule#elements()}. Отсюда отличия поведения:
 * <ul>
 *   <li>перетаскивание идёт через {@code DraggableManager} принимающего клиента
 *       (нормализованные позиции, пружина, ограничение краями экрана), а не через
 *       {@code HudDrag} источника;</li>
 *   <li>ПКМ по элементу включает и выключает его в {@code InterfaceModule.elements()},
 *       то есть ровно то же поле, что и в настройках модуля {@code Interface};</li>
 *   <li>колесо мыши не меняет размер: у элементов SkyCore нет собственного масштаба,
 *       а масштаб всего интерфейса живёт в {@code InterfaceModule} и меняется на
 *       странице настроек — подделывать его здесь означало бы менять чужую настройку
 *       без спроса.</li>
 * </ul>
 *
 * <p>Отрисовка — штатная для 26.2: {@code extractRenderState} → {@code enter2D} →
 * {@code beginFrame}/{@code flush}/{@code exit2D}. Текст и иконки — {@link MenuDraw}
 * и {@link IconGlyphs}, цвета — {@code MenuPalette} (через {@link MenuDraw}),
 * звуки — {@link MenuSound}. Никаких строковых ключей и аллокаций в цикле по элементам.
 */
public final class HudEditorScreen extends Screen {

    /** Шаг сетки в GUI-пикселях, как в источнике. */
    private static final int GRID_STEP = 40;

    /** Высота верхней панели с переключателем сетки. */
    private static final float TOOLBAR_HEIGHT = 30.0F;

    private final Screen parent;

    /** Индекс перетаскиваемого элемента в порядке регистрации или {@code -1}. */
    private int draggingIndex = -1;
    /** Показывать сетку (в источнике рисовалась всегда; здесь её можно отключить). */
    private boolean gridVisible = true;
    private float toolbarX;
    private float toolbarY;
    private float toolbarW;
    private float toolbarH;
    private float mouseX;
    private float mouseY;

    public HudEditorScreen(Screen parent) {
        super(Component.literal(SelectAndSaveHelper.text("Редактор HUD")));
        this.parent = parent;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;

        Minecraft instance = Minecraft.getInstance();
        HudRenderer renderer = SkycoreClient.interfaceRenderer();
        if (renderer == null) {
            return;
        }

        CurrentGuiGraphicsExtractorHelper.enter2D(extractor, instance.getDeltaTracker());
        BeginFrameHelper.beginFrame();
        try {
            // затемнение мира под редактором (в источнике — renderBackground с 0x70000000)
            MenuDraw.rect(0.0F, 0.0F, this.width, this.height, 0x70000000);
            if (this.gridVisible) {
                this.grid();
            }
            // HUD-проход игры на открытом экране не выполняется (см. HudRenderer.renderEditorPreview),
            // поэтому элементы рисуются здесь же — со своими размерами и выключенными тоже.
            renderer.renderEditorPreview(1.0F);
            this.toolbar();
            this.elements(renderer);
            this.hint();
        } finally {
            BeginFrameHelper.flush();
            CurrentGuiGraphicsExtractorHelper.exit2D();
        }
    }

    /** Сетка 40 px, как в источнике. */
    private void grid() {
        for (int x = 0; x < this.width; x += GRID_STEP) {
            MenuDraw.rect(x, 0.0F, 1.0F, this.height, 0x0CFFFFFF);
        }
        for (int y = 0; y < this.height; y += GRID_STEP) {
            MenuDraw.rect(0.0F, y, this.width, 1.0F, 0x0CFFFFFF);
        }
    }

    /** Верхняя панель: переключатель сетки и подсказка о сохранении позиций. */
    private void toolbar() {
        this.toolbarH = TOOLBAR_HEIGHT;
        this.toolbarW = 190.0F;
        this.toolbarX = (this.width - this.toolbarW) * 0.5F;
        this.toolbarY = 8.0F;

        MenuDraw.shadow(this.toolbarX, this.toolbarY + 3.0F, this.toolbarW, this.toolbarH,
                this.toolbarH * 0.5F, 12.0F, 0x70000000);
        MenuDraw.round(this.toolbarX, this.toolbarY, this.toolbarW, this.toolbarH,
                this.toolbarH * 0.5F, MenuPalette.card());
        MenuDraw.outline(this.toolbarX, this.toolbarY, this.toolbarW, this.toolbarH,
                this.toolbarH * 0.5F, MenuPalette.border());

        String label = SelectAndSaveHelper.text("Сетка");
        boolean hovered = this.mouseX >= this.toolbarX && this.mouseX < this.toolbarX + this.toolbarW
                && this.mouseY >= this.toolbarY && this.mouseY < this.toolbarY + this.toolbarH;
        MenuDraw.iconCs(IconGlyphs.RENDER, this.toolbarX + 15.0F, this.toolbarY + this.toolbarH * 0.5F, 12.0F,
                this.gridVisible ? MenuPalette.accent() : MenuPalette.muted());
        MenuDraw.textMid(label, this.toolbarX + 26.0F, this.toolbarY, this.toolbarH, Weight.MEDIUM, 12.0F,
                hovered ? MenuPalette.text() : MenuPalette.muted());
        String state = SelectAndSaveHelper.text(this.gridVisible ? "вкл" : "выкл");
        MenuDraw.textRight(state, this.toolbarX + this.toolbarW - 14.0F,
                this.toolbarY + (this.toolbarH - 12.0F) * 0.5F, Weight.SEMIBOLD, 11.0F,
                this.gridVisible ? MenuPalette.accent() : MenuPalette.dim());
    }

    /**
     * Рамки, подписи и «выключено» у каждого элемента.
     *
     * <p>Элементы уже нарисованы штатным HUD-проходом: экран открывается поверх игры,
     * и {@code HudRenderer.render} продолжает работать каждый кадр. Здесь только
     * обвязка редактора — так же, как это делал источник, вызывая {@code HudRenderer.draw}
     * для выключенных элементов.
     */
    private void elements(HudRenderer renderer) {
        InterfaceModule view = renderer.interfaceModule();
        if (view == null) {
            return;
        }
        int count = renderer.elementCount();
        for (int i = 0; i < count; i++) {
            Draggable draggable = renderer.elementAt(i);
            if (draggable == null || draggable.width() <= 0.0F || draggable.height() <= 0.0F) {
                continue;
            }
            float x = draggable.x();
            float y = draggable.y();
            float w = draggable.width();
            float h = draggable.height();

            boolean hovered = this.mouseX >= x && this.mouseX < x + w
                    && this.mouseY >= y && this.mouseY < y + h;
            boolean active = i == this.draggingIndex;
            int outline = active ? MenuDraw.a(MenuPalette.accent2(), 230)
                    : hovered ? MenuDraw.a(MenuPalette.accent(), 230) : 0x40FFFFFF;
            MenuDraw.outline(x - 3.0F, y - 3.0F, w + 6.0F, h + 6.0F, 12.0F, outline);

            if (!hovered && !active) {
                continue;
            }

            boolean enabled = renderer.isElementEnabled(draggable.id());
            String label = SelectAndSaveHelper.text(renderer.elementLabel(draggable.id()));
            if (!enabled) {
                label = label + " · " + SelectAndSaveHelper.text("выкл");
            }
            float fontSize = 12.0F;
            float labelWidth = MenuDraw.textWidth(label, Weight.SEMIBOLD, fontSize) + 20.0F;
            float labelHeight = fontSize + 12.0F;
            float labelX = x - 3.0F;
            float labelY = y - labelHeight - 8.0F < 0.0F ? y + h + 8.0F : y - labelHeight - 8.0F;
            MenuDraw.round(labelX, labelY, labelWidth, labelHeight, 8.0F, MenuPalette.card());
            MenuDraw.outline(labelX, labelY, labelWidth, labelHeight, 8.0F, MenuPalette.border());
            MenuDraw.text(label, labelX + 7.0F, labelY + 6.0F, Weight.SEMIBOLD, fontSize,
                    enabled ? MenuPalette.text() : MenuPalette.muted());
        }
    }

    /** Нижняя подсказка — та же формулировка, что в источнике, за вычетом колеса. */
    private void hint() {
        String hint = SelectAndSaveHelper.text(
                "ЛКМ — двигать   ·   ПКМ — вкл/выкл   ·   Esc — назад");
        float fontSize = 13.0F;
        float width = MenuDraw.textWidth(hint, Weight.MEDIUM, fontSize) + 40.0F;
        float height = fontSize + 22.0F;
        float x = (this.width - width) * 0.5F;
        float y = this.height - height - 30.0F;
        MenuDraw.shadow(x, y, width, height, 20.0F, 16.0F, 0x90000000);
        MenuDraw.round(x, y, width, height, height * 0.5F, MenuPalette.card());
        MenuDraw.outline(x, y, width, height, height * 0.5F, MenuPalette.border());
        MenuDraw.textCenter(hint, this.width * 0.5F, y + 11.0F, Weight.MEDIUM, fontSize,
                MenuPalette.muted());
    }

    // ---- взаимодействие

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        this.mouseX = (float) event.x();
        this.mouseY = (float) event.y();
        HudRenderer renderer = SkycoreClient.interfaceRenderer();
        if (renderer == null) {
            return super.mouseClicked(event, doubleClick);
        }
        if (event.button() == 0 && this.mouseX >= this.toolbarX
                && this.mouseX < this.toolbarX + this.toolbarW
                && this.mouseY >= this.toolbarY && this.mouseY < this.toolbarY + this.toolbarH) {
            this.gridVisible = !this.gridVisible;
            MenuSound.play(this.gridVisible ? MenuSound.TOGGLE_ON : MenuSound.TOGGLE_OFF);
            return true;
        }
        int index = renderer.elementAt(this.mouseX, this.mouseY);
        if (index < 0) {
            return super.mouseClicked(event, doubleClick);
        }

        if (event.button() == 1) {
            // ПКМ — видимость элемента в InterfaceModule.elements() (в источнике — toggle модуля).
            Draggable draggable = renderer.elementAt(index);
            if (draggable != null) {
                renderer.toggleElement(draggable.id());
                MenuSound.play(MenuSound.TOGGLE_ON);
            }
            return true;
        }
        if (event.button() != 0) {
            return true;
        }

        this.draggingIndex = index;
        // Смещение берём от текущей позиции элемента: beginDrag запоминает,
        // за какую точку его взяли, и дальше dragTo двигает без скачка.
        Draggable dragged = renderer.elementAt(index);
        if (dragged != null) {
            dragged.beginDrag(this.mouseX, this.mouseY);
            renderer.beginElementDrag(index, this.mouseX, this.mouseY);
        }
        MenuSound.play(MenuSound.GRAB);
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (this.draggingIndex < 0) {
            return super.mouseDragged(event, dragX, dragY);
        }
        this.mouseX = (float) event.x();
        this.mouseY = (float) event.y();
        HudRenderer renderer = SkycoreClient.interfaceRenderer();
        if (renderer != null) {
            renderer.dragElement(this.mouseX, this.mouseY, this.width, this.height);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.draggingIndex < 0) {
            return super.mouseReleased(event);
        }
        HudRenderer renderer = SkycoreClient.interfaceRenderer();
        if (renderer != null) {
            renderer.endElementDrag(this.width, this.height);
        }
        this.draggingIndex = -1;
        MenuSound.play(MenuSound.DROP);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        // Esc — назад к родительскому экрану (в источнике onClose возвращал parent).
        if (event.key() == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            this.minecraft.setScreenAndShow(this.parent);
        }
    }

    /** Иконка-маркер для кнопок входа в редактор (используется меню). */
    public static String icon() {
        return IconGlyphs.RENDER;
    }
}
