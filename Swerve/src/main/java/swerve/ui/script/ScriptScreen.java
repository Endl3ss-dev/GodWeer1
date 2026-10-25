package swerve.ui.script;

import swerve.managers.ModuleManager;
import swerve.modules.Module;
import swerve.modules.impl.misc.Scripts;
import swerve.script.BlockCategory;
import swerve.script.BlockHolder;
import swerve.script.BlockType;
import swerve.script.ScriptBlock;
import swerve.script.ScriptBlocks;
import swerve.script.ScriptFunction;
import swerve.script.ScriptStore;
import swerve.script.ScriptTrigger;
import swerve.script.SlotType;
import swerve.ui.menu.MenuSound;
import swerve.ui.menu.MenuText.Weight;
import swerve.ui.menu.Px;
import swerve.ui.newgui.GuiKeys;
import swerve.utils.render.ScissorUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Редактор скриптов в духе Scratch.
 *
 * <p>Слева — список функций и палитра блоков, справа — полотно со стеком блоков
 * выбранной функции. Блоки перетаскиваются мышью: можно менять порядок, класть
 * блоки внутрь циклов и условий, а перетаскивание на палитру удаляет блок.
 * Верхняя палитра цветов повторяет Scratch: жёлтые события, оранжевое
 * управление, синие действия клиента, оранжевые переменные и розовые «мои блоки».
 *
 * <p>Вся геометрия в реальных пикселях окна: {@link Px#begin(GuiGraphics)}
 * масштабирует позу, поэтому редактор одинаково выглядит при любом guiScale.
 */
public final class ScriptScreen extends Screen {

    // ---- палитра Scratch 3
    private static final int C_TOP = 0xFF855CD6;
    private static final int C_TOP_LIGHT = 0xFF9C7BE0;
    private static final int C_PANEL = 0xFFF9F9F9;
    private static final int C_PANEL_ALT = 0xFFF1F1F1;
    private static final int C_BORDER = 0xFFD9D9D9;
    private static final int C_CANVAS = 0xFFFFFFFF;
    private static final int C_DOT = 0xFFE8E8E8;
    private static final int C_TEXT = 0xFF575E75;
    private static final int C_TEXT_DARK = 0xFF2E3142;
    private static final int C_MUTED = 0xFF9AA0B0;
    private static final int C_ACCENT = 0xFF855CD6;
    private static final int C_HOVER = 0xFFEEE8FB;
    private static final int C_WHITE = 0xFFFFFFFF;
    private static final int C_RUN = 0xFF4CBF56;
    private static final int C_STOP = 0xFFE05A5A;

    private static final int FUNC_ROWS = 4;
    private static final int MAX_PALETTE = 32;
    private static final int MAX_POPUP = 64;
    private static final int POPUP_ROWS = 8;

    private final ScriptCanvas canvas = new ScriptCanvas();
    private final ScriptCanvas paletteCanvas = new ScriptCanvas();
    private final ScriptCanvas ghostCanvas = new ScriptCanvas();
    private final Map<BlockType, ScriptBlock> paletteModels = new EnumMap<>(BlockType.class);

    private final List<BlockType> paletteTypes = new ArrayList<>();
    private final List<String> popupOptions = new ArrayList<>();

    private final StringBuilder editBuffer = new StringBuilder();
    private final StringBuilder nameBuffer = new StringBuilder();
    private boolean editFresh;

    // ---- выбранная функция и категория
    private ScriptFunction selected;
    private BlockCategory category = BlockCategory.EVENT;

    // ---- геометрия окна (реальные пиксели)
    private float u = 1.0f;
    private int px;
    private int py;
    private int pw;
    private int ph;
    private int titleH;
    private int sideW;
    private int bodyY;
    private int bodyH;
    private int scriptX;
    private int scriptY;
    private int scriptW;
    private int scriptH;
    private int toolbarH;
    private int funcListY;
    private int funcListH;
    private int funcRowH;
    private int catY;
    private int catRowH;
    private int paletteY;
    private int paletteH;
    private int hintY;
    private int hintH;

    private int closeX;
    private int closeY;
    private int closeR;
    private int nameX;
    private int nameY;
    private int nameW;
    private int nameH;
    private int keyX;
    private int keyY;
    private int keyW;
    private int keyH;
    private int runX;
    private int runY;
    private int runW;
    private int runH;
    private int delX;
    private int delY;
    private int delW;
    private int delH;
    private int newX;
    private int newY;
    private int newW;
    private int newH;

    // ---- прокрутка
    private float canvasScroll;
    private float paletteScroll;
    private float funcScroll;
    private float popupScroll;
    private int canvasContentH;
    private int paletteContentH;
    private int funcContentH;

    // ---- редактирование значений
    private ScriptBlock editingBlock;
    private int editingSlot = -1;
    private boolean nameEditing;
    private boolean bindingFunctionKey;
    private ScriptBlock bindingBlock;
    private int bindingSlot = -1;

    // ---- перетаскивание
    private ScriptBlock dragBlock;
    private boolean dragFromPalette;
    private BlockHolder dragOriginHolder;
    private int dragOriginIndex = -1;
    private double dragX;
    private double dragY;
    private boolean dragMoved;

    // ---- выпадающий список
    private ScriptBlock popupBlock;
    private int popupSlot = -1;
    private int popupAnchorX;
    private int popupAnchorY;
    private int popupX;
    private int popupY;
    private int popupW;
    private int popupH;
    private int popupRowH;
    private int popupCount;

    // ---- наведение и попадания (пересчитываются каждый кадр)
    private ScriptBlock hoverBlock;
    private int hoverSlot = -1;
    private final int[] funcRect = new int[FUNC_ROWS * 4];
    private final int[] funcIndex = new int[FUNC_ROWS];
    private int funcCount;
    private final int[] catRect = new int[8 * 4];
    private final int[] palRect = new int[MAX_PALETTE * 4];
    private final BlockType[] palType = new BlockType[MAX_PALETTE];
    private int palCount;
    private final int[] popupRect = new int[MAX_POPUP * 4];

    public ScriptScreen() {
        super(Component.literal("Скрипты"));
        List<ScriptFunction> functions = ScriptStore.getFunctions();
        this.selected = functions.isEmpty() ? ScriptStore.create() : functions.get(0);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int S(double value) {
        return (int) Math.round(value * this.u);
    }

    // ---- отрисовка

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        g.fill(0, 0, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight(), 0xB3000000);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        draw(g, mouseX, mouseY);
    }

    private void draw(GuiGraphics g, int mouseX, int mouseY) {
        layout();

        double mx = Px.px(mouseX);
        double my = Px.px(mouseY);

        updateHover(mx, my);

        Px.begin(g);
        try {
            frame(g);
            titleBar(g, mx, my);
            sidebar(g, mx, my);
            rightPane(g, mx, my);
            if (this.dragBlock != null) {
                drawDrag(g, mx, my);
            }
            drawPopup(g, mx, my);
        } finally {
            Px.end(g);
        }
    }

    private void layout() {
        int sw = Px.width();
        int sh = Px.height();

        this.u = Math.max(0.72f, Math.min(1.6f, Math.min(sw / 1500.0f, sh / 900.0f)));
        this.pw = Math.min(sw - S(48), S(1500));
        this.ph = Math.min(sh - S(48), S(880));
        this.px = (sw - this.pw) / 2;
        this.py = (sh - this.ph) / 2;

        this.titleH = S(48);
        this.sideW = Math.min(S(340), this.pw / 3);
        this.bodyY = this.py + this.titleH;
        this.bodyH = this.ph - this.titleH;

        this.toolbarH = S(56);
        this.scriptX = this.px + this.sideW;
        this.scriptY = this.bodyY + this.toolbarH;
        this.scriptW = this.pw - this.sideW;
        this.hintH = S(24);
        this.scriptH = this.bodyH - this.toolbarH - this.hintH;
        this.hintY = this.scriptY + this.scriptH;

        this.closeR = S(13);
        this.closeX = this.px + this.pw - S(26);
        this.closeY = this.py + this.titleH / 2;

        int pad = S(14);
        this.nameX = this.scriptX + pad;
        this.nameY = this.bodyY + S(14);
        this.nameW = Math.min(S(300), this.scriptW / 3);
        this.nameH = S(28);

        this.keyW = S(132);
        this.keyH = S(28);
        this.keyX = this.nameX + this.nameW + S(8);
        this.keyY = this.nameY;

        this.runW = S(122);
        this.runH = S(28);
        this.runX = this.scriptX + this.scriptW - pad - this.runW;
        this.runY = this.nameY;

        this.newH = S(24);
        this.delH = S(24);

        int sidePad = S(14);
        int sideInner = this.sideW - sidePad * 2;
        this.funcListY = this.bodyY + S(34);
        this.funcRowH = S(28);
        this.funcListH = this.funcRowH * FUNC_ROWS + S(4);

        this.newY = this.funcListY + this.funcListH + S(4);
        this.newX = this.px + sidePad;

        this.catY = this.newY + this.newH + S(14);
        this.catRowH = S(24);

        this.paletteY = this.catY + this.catRowH * BlockCategory.values().length + S(6);
        this.paletteH = Math.max(S(80), this.bodyY + this.bodyH - S(10) - this.paletteY);

        this.delY = this.newY;

        // Кнопки «+ функция» и «удалить» делят ширину панели поровну.
        this.newW = sideInner / 2;
        this.delX = this.newX + this.newW + S(6);
        this.delW = sideInner - this.newW - S(6);
    }

    private void frame(GuiGraphics g) {
        // Окно квадратное, как в Scratch: панели доходят до края, и скругление
        // окна дало бы светлые «уши» в углах, где начинаются панели.
        Px.rect(g, this.px, this.py, this.pw, this.ph, C_PANEL);
        Px.outline(g, this.px, this.py, this.pw, this.ph, 0, 0x33000000);
        Px.rect(g, this.px, this.bodyY, this.sideW, this.bodyH, C_PANEL_ALT);
        Px.rect(g, this.px + this.sideW - 1, this.bodyY, 1, this.bodyH, C_BORDER);
        Px.rect(g, this.scriptX, this.bodyY, this.scriptW, this.toolbarH, C_PANEL);
        Px.rect(g, this.scriptX, this.scriptY - 1, this.scriptW, 1, C_BORDER);
        Px.rect(g, this.scriptX, this.hintY, this.scriptW, this.hintH, C_PANEL_ALT);
        Px.rect(g, this.scriptX, this.hintY, this.scriptW, 1, C_BORDER);
    }

    private void titleBar(GuiGraphics g, double mx, double my) {
        Px.rect(g, this.px, this.py, this.pw, this.titleH, C_TOP);
        Px.rect(g, this.px, this.py + this.titleH - S(2), this.pw, S(2), 0x22000000);

        int fs = Math.max(10, S(16));
        Px.text(g, "SWERVE", this.px + S(20), this.py + (this.titleH - fs) / 2 - S(1), Weight.BOLD, fs, C_WHITE);
        int brandW = Math.round(Px.textWidth("SWERVE", Weight.BOLD, fs));
        Px.text(g, "· скрипты как в Scratch", this.px + S(20) + brandW + S(8),
                this.py + (this.titleH - fs) / 2, Weight.MEDIUM, Math.max(9, S(12)), 0xCCFFFFFF);

        boolean hot = distance(mx, my, this.closeX, this.closeY) <= this.closeR + S(3);
        Px.circle(g, this.closeX, this.closeY, this.closeR, hot ? 0x55FFFFFF : 0x33FFFFFF);
        int arm = Math.max(3, S(5));
        int thickness = Math.max(1, S(2));
        Px.seg(g, this.closeX - arm, this.closeY - arm, this.closeX + arm, this.closeY + arm, thickness, C_WHITE);
        Px.seg(g, this.closeX + arm, this.closeY - arm, this.closeX - arm, this.closeY + arm, thickness, C_WHITE);

        Px.text(g, "функции запускаются прямо в клиенте", this.px + S(20),
                this.py + this.titleH - S(16), Weight.MEDIUM, Math.max(8, S(10)), 0x99FFFFFF);
    }

    private void sidebar(GuiGraphics g, double mx, double my) {
        int pad = S(14);
        int x = this.px + pad;
        int w = this.sideW - pad * 2;

        int fs = Math.max(8, S(10));
        Px.text(g, "ФУНКЦИИ", x, this.bodyY + S(12), Weight.BOLD, fs, C_MUTED);

        this.funcCount = 0;
        List<ScriptFunction> functions = ScriptStore.getFunctions();
        int maxScroll = Math.max(0, functions.size() * this.funcRowH - this.funcListH);
        this.funcContentH = functions.size() * this.funcRowH;
        this.funcScroll = clamp(this.funcScroll, 0, maxScroll);

        ScissorUtils.push();
        ScissorUtils.set(this.px, Px.height() - (this.funcListY + this.funcListH), this.sideW, this.funcListH);

        int rowFs = Math.max(9, S(11));
        for (int i = 0, size = functions.size(); i < size; i++) {
            ScriptFunction function = functions.get(i);
            int y = this.funcListY + i * this.funcRowH - Math.round(this.funcScroll);
            if (y + this.funcRowH < this.funcListY || y > this.funcListY + this.funcListH) {
                continue;
            }
            boolean active = function == this.selected;
            boolean hover = mx >= x && mx < x + w && my >= y && my < y + this.funcRowH - S(2);

            if (active) {
                Px.round(g, x, y, w, this.funcRowH - S(2), S(6), C_WHITE);
                Px.outline(g, x, y, w, this.funcRowH - S(2), S(6), 0x14000000);
                Px.rect(g, x, y + S(5), S(3), this.funcRowH - S(12), C_ACCENT);
            } else if (hover) {
                Px.round(g, x, y, w, this.funcRowH - S(2), S(6), C_HOVER);
            }

            int dotColor = function.getTrigger().ordinal() == 0 ? 0xFFB9BECB : 0xFFFFBF00;
            Px.circle(g, x + S(12), y + (this.funcRowH - S(2)) / 2, Math.max(2, S(3)), dotColor);

            String name = Px.ellipsize(function.getName(), Weight.SEMIBOLD, rowFs, w - S(74));
            Px.text(g, name, x + S(22), y + (this.funcRowH - S(3) - rowFs) / 2,
                    Weight.SEMIBOLD, rowFs, active ? C_TEXT_DARK : C_TEXT);

            if (ScriptEngineRunning.isRunning(function)) {
                Px.circle(g, x + w - S(14), y + (this.funcRowH - S(2)) / 2, Math.max(2, S(3)), C_RUN);
            } else if (function.getKey() != -1) {
                String key = GuiKeys.keyName(function.getKey());
                int keyFs = Math.max(8, S(9));
                int kw = Px.textWidth(key, Weight.BOLD, keyFs) + S(8);
                int kx = x + w - kw - S(6);
                int ky = y + (this.funcRowH - S(2) - S(16)) / 2;
                Px.round(g, kx, ky, kw, S(16), S(5), 0xFFE7E7EC);
                Px.text(g, key, kx + S(4), ky + (S(16) - keyFs) / 2, Weight.BOLD, keyFs, C_TEXT);
            }

            if (this.funcCount < FUNC_ROWS) {
                this.funcRect[this.funcCount * 4] = x;
                this.funcRect[this.funcCount * 4 + 1] = y;
                this.funcRect[this.funcCount * 4 + 2] = w;
                this.funcRect[this.funcCount * 4 + 3] = this.funcRowH - S(2);
                this.funcIndex[this.funcCount] = i;
                this.funcCount++;
            }
        }
        ScissorUtils.pop();

        // кнопки «новая» и «удалить»
        boolean newHover = inside(mx, my, this.newX, this.newY, this.newW, this.newH);
        Px.round(g, this.newX, this.newY, this.newW, this.newH, S(6),
                newHover ? C_HOVER : 0xFFE7E7EC);
        Px.textCenter(g, "+ функция", this.newX + this.newW / 2,
                this.newY + (this.newH - Math.max(9, S(11))) / 2, Weight.SEMIBOLD,
                Math.max(9, S(11)), C_TEXT);

        boolean delHover = inside(mx, my, this.delX, this.delY, this.delW, this.delH);
        boolean canDelete = this.selected != null;
        Px.round(g, this.delX, this.delY, this.delW, this.delH, S(6),
                delHover && canDelete ? 0xFFF8D7D7 : 0xFFE7E7EC);
        Px.textCenter(g, "удалить", this.delX + this.delW / 2,
                this.delY + (this.delH - Math.max(9, S(11))) / 2, Weight.SEMIBOLD,
                Math.max(9, S(11)), canDelete ? 0xFFC24B4B : C_MUTED);

        // категории
        Px.text(g, "БЛОКИ", x, this.catY - S(13), Weight.BOLD, fs, C_MUTED);
        BlockCategory[] categories = BlockCategory.values();
        for (int i = 0; i < categories.length; i++) {
            BlockCategory item = categories[i];
            int y = this.catY + i * this.catRowH;
            boolean active = item == this.category;
            boolean hover = mx >= x && mx < x + w && my >= y && my < y + this.catRowH - S(2);

            if (active) {
                Px.round(g, x, y, w, this.catRowH - S(2), S(5), C_WHITE);
                Px.outline(g, x, y, w, this.catRowH - S(2), S(5), 0x14000000);
            } else if (hover) {
                Px.round(g, x, y, w, this.catRowH - S(2), S(5), C_HOVER);
            }

            int dotR = Math.max(3, S(5));
            Px.circle(g, x + S(11), y + (this.catRowH - S(2)) / 2, dotR, item.color());
            Px.text(g, item.displayName(), x + S(22), y + (this.catRowH - S(2) - Math.max(9, S(11))) / 2,
                    Weight.SEMIBOLD, Math.max(9, S(11)), active ? C_TEXT_DARK : C_TEXT);

            this.catRect[i * 4] = x;
            this.catRect[i * 4 + 1] = y;
            this.catRect[i * 4 + 2] = w;
            this.catRect[i * 4 + 3] = this.catRowH - S(2);
        }

        drawPalette(g, mx, my);
    }

    private void drawPalette(GuiGraphics g, double mx, double my) {
        this.palCount = 0;
        this.paletteTypes.clear();
        for (BlockType type : BlockType.values()) {
            if (type.category() == this.category) {
                this.paletteTypes.add(type);
            }
        }

        float pu = this.u * 0.8f;
        int itemX = this.px + S(10);
        int itemW = this.sideW - S(20);

        this.paletteCanvas.begin(pu);
        this.paletteCanvas.interactive(false);

        // Первый проход: высота содержимого.
        int content = S(6);
        for (int i = 0; i < this.paletteTypes.size(); i++) {
            ScriptBlock model = paletteModel(this.paletteTypes.get(i));
            content += this.paletteCanvas.height(model, pu) + S(6);
        }
        this.paletteContentH = content;
        this.paletteScroll = clamp(this.paletteScroll, 0, Math.max(0, content - this.paletteH));

        ScissorUtils.push();
        ScissorUtils.set(this.px, Px.height() - (this.paletteY + this.paletteH), this.sideW, this.paletteH);

        int y = this.paletteY + S(6) - Math.round(this.paletteScroll);
        for (int i = 0; i < this.paletteTypes.size(); i++) {
            BlockType type = this.paletteTypes.get(i);
            ScriptBlock model = paletteModel(type);
            int height = this.paletteCanvas.height(model, pu);

            if (y + height >= this.paletteY && y <= this.paletteY + this.paletteH) {
                boolean hover = mx >= itemX && mx < itemX + itemW && my >= y && my < y + height;
                if (hover) {
                    Px.round(g, itemX - S(4), y - S(3), itemW + S(8), height + S(6), S(6), 0xD9EEE8FB);
                }
                this.paletteCanvas.drawBlock(g, model, itemX, y, 1.0f);
            }

            if (this.palCount < MAX_PALETTE && y + height >= this.paletteY
                    && y <= this.paletteY + this.paletteH) {
                int index = this.palCount;
                this.palRect[index * 4] = itemX - S(4);
                this.palRect[index * 4 + 1] = y;
                this.palRect[index * 4 + 2] = itemW + S(8);
                this.palRect[index * 4 + 3] = height;
                this.palType[index] = type;
                this.palCount++;
            }
            y += height + S(6);
        }
        ScissorUtils.pop();

        // Подсказка при перетаскивании блока в палитру — там он удаляется.
        if (this.dragBlock != null && !this.dragFromPalette && inPalette(mx, my)) {
            Px.round(g, this.px + S(6), this.paletteY - S(2), this.sideW - S(12), this.paletteH, S(8), 0x33E05A5A);
            int fs = Math.max(9, S(11));
            Px.textCenter(g, "отпусти, чтобы удалить", this.px + this.sideW / 2,
                    this.paletteY + this.paletteH / 2, Weight.BOLD, fs, 0xFFB93B3B);
        }
    }

    private ScriptBlock paletteModel(BlockType type) {
        ScriptBlock model = this.paletteModels.get(type);
        if (model == null) {
            model = new ScriptBlock(type);
            this.paletteModels.put(type, model);
        }
        return model;
    }

    private void rightPane(GuiGraphics g, double mx, double my) {
        // полотно
        Px.rect(g, this.scriptX, this.scriptY, this.scriptW, this.scriptH, C_CANVAS);

        int step = Math.max(14, S(24));
        for (int gx = this.scriptX + step; gx < this.scriptX + this.scriptW; gx += step) {
            for (int gy = this.scriptY + step; gy < this.scriptY + this.scriptH; gy += step) {
                g.fill(gx, gy, gx + Math.max(1, S(2)), gy + Math.max(1, S(2)), C_DOT);
            }
        }

        toolbar(g, mx, my);

        this.canvas.begin(this.u);
        this.canvas.interactive(true);
        this.canvas.editing(this.editingBlock, this.editingSlot, this.editBuffer.toString());
        this.hoverBlock = null;
        this.hoverSlot = -1;

        ScissorUtils.push();
        ScissorUtils.set(this.scriptX, Px.height() - (this.scriptY + this.scriptH), this.scriptW, this.scriptH);

        int contentX = this.scriptX + S(36);
        int contentY = this.scriptY + S(28) - Math.round(this.canvasScroll);

        if (this.selected == null || this.selected.getHead() == null) {
            emptyHint(g);
            this.canvasContentH = 0;
        } else {
            this.canvas.drawChain(g, this.selected, this.selected.getHead(), contentX, contentY, 1.0f);
            int bottom = contentY + this.canvas.chainHeight(this.selected.getHead(), this.u);
            this.canvasContentH = bottom - (this.scriptY + S(28));
        }

        this.canvasScroll = clamp(this.canvasScroll, 0,
                Math.max(0, this.canvasContentH + S(40) - this.scriptH));

        // куда встанет перетаскиваемый блок
        if (this.dragBlock != null) {
            ScriptCanvas.DropZone zone = this.canvas.dropZoneAt(mx, my, S(30));
            if (zone != null) {
                Px.round(g, zone.x, zone.y - S(3), Math.max(zone.width, S(46)), S(6), S(3), C_ACCENT);
            }
        }

        ScissorUtils.pop();

        // наведение
        if (this.dragBlock == null) {
            ScriptCanvas.Hit slot = this.canvas.slotAt(mx, my);
            ScriptCanvas.Hit block = this.canvas.blockAt(mx, my);
            if (slot != null) {
                this.hoverBlock = slot.block;
                this.hoverSlot = slot.slot;
                Px.outline(g, slot.x - S(1), slot.y - S(1), slot.width + S(2), slot.height + S(2),
                        slot.height / 2, C_ACCENT);
            } else if (block != null) {
                this.hoverBlock = block.block;
                Px.outline(g, block.x, block.y, block.width, block.height, S(8), 0x66855CD6);
            }
        }

        hintBar(g);
    }

    private void emptyHint(GuiGraphics g) {
        int fs = Math.max(10, S(13));
        String text = "Выбери блок слева и нажми на него — он добавится в скрипт";
        int width = Math.round(Px.textWidth(text, Weight.SEMIBOLD, fs));
        int x = this.scriptX + (this.scriptW - width) / 2;
        int y = this.scriptY + this.scriptH / 2 - S(10);
        Px.text(g, text, x, y, Weight.SEMIBOLD, fs, C_MUTED);
    }

    private void toolbar(GuiGraphics g, double mx, double my) {
        boolean editingName = this.nameEditing;
        Px.round(g, this.nameX, this.nameY, this.nameW, this.nameH, S(6), C_WHITE);
        Px.outline(g, this.nameX, this.nameY, this.nameW, this.nameH, S(6),
                editingName ? C_ACCENT : 0x22000000);

        int fs = Math.max(9, S(12));
        String name = editingName ? this.nameBuffer.toString() : (this.selected == null ? "" : this.selected.getName());
        String shown = Px.ellipsize(name, Weight.SEMIBOLD, fs, this.nameW - S(20));
        Px.text(g, shown, this.nameX + S(9), this.nameY + (this.nameH - fs) / 2,
                Weight.SEMIBOLD, fs, this.selected == null ? C_MUTED : C_TEXT_DARK);

        if (editingName && (System.currentTimeMillis() / 500) % 2 == 0) {
            int caretX = this.nameX + S(9) + Math.round(Px.textWidth(shown, Weight.SEMIBOLD, fs));
            g.fill(caretX + 1, this.nameY + S(7), caretX + Math.max(1, S(1)) + 1, this.nameY + this.nameH - S(7), C_TEXT_DARK);
        }

        if (this.selected == null) {
            return;
        }

        // клавиша функции
        boolean bind = this.bindingFunctionKey;
        Px.round(g, this.keyX, this.keyY, this.keyW, this.keyH, S(6), bind ? 0xFFF3ECFF : C_WHITE);
        Px.outline(g, this.keyX, this.keyY, this.keyW, this.keyH, S(6), bind ? C_ACCENT : 0x22000000);
        String keyText = bind ? "нажми клавишу…" : (this.selected.getKey() == -1
                ? "клавиша: —" : "клавиша: " + GuiKeys.keyName(this.selected.getKey()));
        Px.text(g, Px.ellipsize(keyText, Weight.SEMIBOLD, fs, this.keyW - S(16)),
                this.keyX + S(8), this.keyY + (this.keyH - fs) / 2, Weight.SEMIBOLD, fs,
                bind ? C_ACCENT : C_TEXT);

        // запуск / остановка
        boolean running = ScriptEngineRunning.isRunning(this.selected);
        boolean runHover = inside(mx, my, this.runX, this.runY, this.runW, this.runH);
        Px.round(g, this.runX, this.runY, this.runW, this.runH, S(6), running ? C_STOP : (runHover ? 0xFF44B44E : C_RUN));
        Px.textCenter(g, running ? "■  Стоп" : "▶  Запустить", this.runX + this.runW / 2,
                this.runY + (this.runH - fs) / 2, Weight.BOLD, fs, C_WHITE);

        // триггер и подсказка
        ScriptTrigger trigger = this.selected.getTrigger();
        String triggerText = "Триггер: " + trigger.displayName();
        String hint = this.selected.hasHat() ? triggerText : triggerText + "  ·  поставь жёлтый блок сверху, чтобы менять";
        Px.text(g, Px.ellipsize(hint, Weight.MEDIUM, Math.max(8, S(10)), this.scriptW - S(28)),
                this.nameX, this.nameY + this.nameH + S(4), Weight.MEDIUM, Math.max(8, S(10)), C_MUTED);
    }

    private void hintBar(GuiGraphics g) {
        int fs = Math.max(8, S(10));
        String hint = "ЛКМ — перетаскивать блоки  ·  ПКМ — удалить  ·  колесо — прокрутка"
                + "  ·  имя функции — клик по полю  ·  Esc — закрыть";
        Px.text(g, Px.ellipsize(hint, Weight.MEDIUM, fs, this.scriptW - S(24)),
                this.scriptX + S(14), this.hintY + (this.hintH - fs) / 2, Weight.MEDIUM, fs, C_MUTED);
    }

    private void drawDrag(GuiGraphics g, double mx, double my) {
        ScriptBlock block = this.dragBlock;
        if (block == null || block.getType() == null) {
            return;
        }

        this.ghostCanvas.begin(this.u);
        this.ghostCanvas.interactive(false);

        int x = (int) Math.round(mx) - S(30);
        int y = (int) Math.round(my) - this.ghostCanvas.headerHeight(this.u) / 2;
        this.ghostCanvas.drawChain(g, null, block, x, y, 0.92f);
    }

    // ---- выпадающий список

    private void drawPopup(GuiGraphics g, double mx, double my) {
        this.popupCount = 0;
        if (this.popupBlock == null || this.popupSlot < 0) {
            return;
        }

        int rowH = Math.max(16, S(22));
        int visibleRows = Math.min(POPUP_ROWS, Math.max(1, this.popupOptions.size()));
        int width = S(196);
        int height = visibleRows * rowH + S(8);

        int x = clamp(this.popupAnchorX, this.px + S(8), this.px + this.pw - width - S(8));
        int y = clamp(this.popupAnchorY, this.py + S(8), this.py + this.ph - height - S(8));

        int maxScroll = Math.max(0, this.popupOptions.size() * rowH - visibleRows * rowH);
        this.popupScroll = clamp(this.popupScroll, 0, maxScroll);

        this.popupX = x;
        this.popupY = y;
        this.popupW = width;
        this.popupH = height;
        this.popupRowH = rowH;

        Px.round(g, x + S(2), y + S(3), width, height, S(8), 0x33000000);
        Px.round(g, x, y, width, height, S(8), C_WHITE);
        Px.outline(g, x, y, width, height, S(8), 0x33000000);

        String current = this.popupBlock.get(this.popupSlot);

        ScissorUtils.push();
        ScissorUtils.set(x, Px.height() - (y + height), width, height);

        int rowFs = Math.max(9, S(11));
        int offset = Math.round(this.popupScroll);
        int first = offset / rowH;
        int last = Math.min(this.popupOptions.size(), first + visibleRows + 1);

        for (int i = first; i < last; i++) {
            String option = this.popupOptions.get(i);
            int ry = y + S(4) + i * rowH - offset;
            boolean hover = mx >= x && mx < x + width && my >= ry && my < ry + rowH;
            boolean active = option.equals(current);

            if (hover) {
                Px.round(g, x + S(4), ry, width - S(8), rowH, S(5), C_HOVER);
            }
            if (active) {
                Px.round(g, x + S(4), ry, width - S(8), rowH, S(5), 0xFFF0EAFD);
            }

            Px.text(g, Px.ellipsize(option, Weight.SEMIBOLD, rowFs, width - S(28)),
                    x + S(12), ry + (rowH - rowFs) / 2, Weight.SEMIBOLD, rowFs,
                    active ? C_ACCENT : C_TEXT);

            if (this.popupCount < MAX_POPUP) {
                this.popupRect[this.popupCount * 4] = x;
                this.popupRect[this.popupCount * 4 + 1] = ry;
                this.popupRect[this.popupCount * 4 + 2] = width;
                this.popupRect[this.popupCount * 4 + 3] = rowH;
                this.popupIndex[this.popupCount] = i;
                this.popupCount++;
            }
        }

        ScissorUtils.pop();
    }

    private final int[] popupIndex = new int[MAX_POPUP];

    // ---- ввод

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double x = Px.px(mouseX);
        double y = Px.px(mouseY);
        layout();

        // привязка клавиши ловит в том числе кнопки мыши
        if (this.bindingFunctionKey && this.selected != null) {
            this.selected.setKey(1000 + Math.max(0, button));
            ScriptStore.markDirty();
            this.bindingFunctionKey = false;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }
        if (this.bindingBlock != null) {
            this.bindingBlock.set(this.bindingSlot, String.valueOf(1000 + Math.max(0, button)));
            ScriptStore.markDirty();
            this.bindingBlock = null;
            this.bindingSlot = -1;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }

        if (this.popupBlock != null) {
            for (int i = 0; i < this.popupCount; i++) {
                if (inside(x, y, this.popupRect[i * 4], this.popupRect[i * 4 + 1],
                        this.popupRect[i * 4 + 2], this.popupRect[i * 4 + 3])) {
                    selectOption(this.popupOptions.get(this.popupIndex[i]));
                    return true;
                }
            }
            closePopup();
            return true;
        }

        if (distance(x, y, this.closeX, this.closeY) <= this.closeR + S(3)) {
            onClose();
            return true;
        }

        if (button == 1) {
            // ПКМ: отмена перетаскивания либо удаление блока
            if (this.dragBlock != null) {
                cancelDrag();
                return true;
            }
            if (overScript(x, y)) {
                ScriptCanvas.Hit hit = this.canvas.blockAt(x, y);
                if (hit != null) {
                    deleteChain(hit.block);
                }
            }
            return true;
        }

        if (button != 0) {
            return true;
        }

        if (this.nameEditing && !inside(x, y, this.nameX, this.nameY, this.nameW, this.nameH)) {
            commitName();
        }
        commitEdit();

        if (inside(x, y, this.nameX, this.nameY, this.nameW, this.nameH)) {
            // Повторный клик по тому же полю не должен стирать уже набранное имя.
            if (!this.nameEditing) {
                this.nameEditing = true;
                this.nameBuffer.setLength(0);
                if (this.selected != null) {
                    this.nameBuffer.append(this.selected.getName());
                }
            }
            return true;
        }

        if (inside(x, y, this.keyX, this.keyY, this.keyW, this.keyH) && this.selected != null) {
            this.bindingFunctionKey = true;
            this.nameEditing = false;
            MenuSound.play(MenuSound.CLICK);
            return true;
        }

        if (inside(x, y, this.runX, this.runY, this.runW, this.runH) && this.selected != null) {
            if (ScriptEngineRunning.isRunning(this.selected)) {
                ScriptEngineRunning.stop(this.selected);
            } else {
                ScriptEngineRunning.run(this.selected);
            }
            MenuSound.play(MenuSound.CLICK);
            return true;
        }

        if (inside(x, y, this.newX, this.newY, this.newW, this.newH)) {
            this.selected = ScriptStore.create();
            this.canvasScroll = 0;
            MenuSound.play(MenuSound.OPEN);
            return true;
        }

        if (inside(x, y, this.delX, this.delY, this.delW, this.delH) && this.selected != null) {
            ScriptFunction removed = this.selected;
            ScriptStore.remove(removed);
            this.selected = ScriptStore.getFunctions().isEmpty() ? null : ScriptStore.getFunctions().get(0);
            MenuSound.play(MenuSound.RESET);
            return true;
        }

        for (int i = 0; i < this.funcCount; i++) {
            if (inside(x, y, this.funcRect[i * 4], this.funcRect[i * 4 + 1],
                    this.funcRect[i * 4 + 2], this.funcRect[i * 4 + 3])) {
                int index = this.funcIndex[i];
                List<ScriptFunction> all = ScriptStore.getFunctions();
                this.selected = index >= 0 && index < all.size() ? all.get(index) : this.selected;
                this.canvasScroll = 0;
                MenuSound.play(MenuSound.CLICK, 1.05f, 0.5f);
                return true;
            }
        }

        BlockCategory[] categories = BlockCategory.values();
        for (int i = 0; i < categories.length; i++) {
            if (inside(x, y, this.catRect[i * 4], this.catRect[i * 4 + 1],
                    this.catRect[i * 4 + 2], this.catRect[i * 4 + 3])) {
                this.category = categories[i];
                this.paletteScroll = 0;
                MenuSound.play(MenuSound.CLICK, 1.05f, 0.5f);
                return true;
            }
        }

        for (int i = 0; i < this.palCount; i++) {
            if (inside(x, y, this.palRect[i * 4], this.palRect[i * 4 + 1],
                    this.palRect[i * 4 + 2], this.palRect[i * 4 + 3])) {
                BlockType type = this.palType[i];
                if (type != null) {
                    if (this.selected == null) {
                        this.selected = ScriptStore.create();
                    }
                    this.dragBlock = new ScriptBlock(type);
                    this.dragFromPalette = true;
                    this.dragOriginHolder = null;
                    this.dragOriginIndex = -1;
                    this.dragMoved = false;
                    this.dragX = x;
                    this.dragY = y;
                    MenuSound.play(MenuSound.GRAB, 1.0f, 0.4f);
                }
                return true;
            }
        }

        if (overScript(x, y)) {
            ScriptCanvas.Hit slot = this.canvas.slotAt(x, y);
            if (slot != null) {
                startEdit(slot.block, slot.slot, x, y);
                return true;
            }

            ScriptCanvas.Hit hit = this.canvas.blockAt(x, y);
            if (hit != null) {
                beginDrag(hit.block, x, y);
                return true;
            }
            this.nameEditing = false;
            return true;
        }

        this.nameEditing = false;
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.dragBlock != null) {
            finishDrag(Px.px(mouseX), Px.px(mouseY));
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.dragBlock != null) {
            this.dragX = Px.px(mouseX);
            this.dragY = Px.px(mouseY);
            this.dragMoved = true;
            return true;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        double x = Px.px(mouseX);
        double y = Px.px(mouseY);
        layout();

        if (this.popupBlock != null) {
            this.popupScroll -= (float) scrollY * Math.max(16, S(22)) * 2.0f;
            return true;
        }

        if (x < this.px + this.sideW) {
            if (y >= this.paletteY) {
                this.paletteScroll -= (float) scrollY * S(40);
            } else if (y >= this.funcListY && y <= this.funcListY + this.funcListH) {
                this.funcScroll -= (float) scrollY * S(30);
            }
        } else if (overScript(x, y)) {
            this.canvasScroll -= (float) scrollY * S(40);
        }
        return true;
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        // Привязка клавиши: следующая нажатая клавиша становится биндом.
        if (this.bindingFunctionKey && this.selected != null) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.bindingFunctionKey = false;
                return true;
            }
            this.selected.setKey(key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE ? -1 : key);
            ScriptStore.markDirty();
            this.bindingFunctionKey = false;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }

        if (this.bindingBlock != null) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.bindingBlock = null;
                this.bindingSlot = -1;
                return true;
            }
            String value = key == GLFW.GLFW_KEY_BACKSPACE || key == GLFW.GLFW_KEY_DELETE
                    ? "-1" : String.valueOf(key);
            this.bindingBlock.set(this.bindingSlot, value);
            ScriptStore.markDirty();
            this.bindingBlock = null;
            this.bindingSlot = -1;
            MenuSound.play(MenuSound.SELECT);
            return true;
        }

        if (this.editingBlock != null) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.editingBlock = null;
                this.editingSlot = -1;
                this.editBuffer.setLength(0);
                return true;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                commitEdit();
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (this.editFresh) {
                    this.editBuffer.setLength(0);
                    this.editFresh = false;
                } else if (this.editBuffer.length() > 0) {
                    this.editBuffer.setLength(this.editBuffer.length() - 1);
                }
                MenuSound.play(MenuSound.TYPE, 1.0f, 0.25f);
                return true;
            }
            return true;
        }

        if (this.nameEditing) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                this.nameEditing = false;
                return true;
            }
            if (key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER) {
                commitName();
                return true;
            }
            if (key == GLFW.GLFW_KEY_BACKSPACE && this.nameBuffer.length() > 0) {
                this.nameBuffer.setLength(this.nameBuffer.length() - 1);
                MenuSound.play(MenuSound.TYPE, 1.0f, 0.25f);
                return true;
            }
            return true;
        }

        if (key == GLFW.GLFW_KEY_ESCAPE) {
            if (this.popupBlock != null) {
                closePopup();
            } else if (this.dragBlock != null) {
                cancelDrag();
            } else {
                onClose();
            }
            return true;
        }

        if (key == GLFW.GLFW_KEY_DELETE && this.hoverBlock != null && this.dragBlock == null) {
            deleteChain(this.hoverBlock);
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (this.editingBlock != null) {
            BlockType.Slot slot = this.editingBlock.getType().slot(this.editingSlot);
            SlotType type = slot == null ? SlotType.TEXT : slot.type();
            if (allowed(type, codePoint)) {
                if (this.editFresh) {
                    this.editBuffer.setLength(0);
                    this.editFresh = false;
                }
                if (this.editBuffer.length() < 48) {
                    this.editBuffer.append(codePoint);
                    MenuSound.play(MenuSound.TYPE, 1.2f, 0.3f);
                }
            }
            return true;
        }

        if (this.nameEditing) {
            if (this.nameBuffer.length() < 24 && codePoint >= 32 && codePoint != 127) {
                this.nameBuffer.append(codePoint);
                MenuSound.play(MenuSound.TYPE, 1.2f, 0.3f);
            }
            return true;
        }

        return super.charTyped(codePoint, modifiers);
    }

    private boolean allowed(SlotType type, char chr) {
        if (chr < 32 || chr == 127) {
            return false;
        }
        if (type == SlotType.NUMBER) {
            return Character.isDigit(chr) || chr == '.' || chr == ',' || chr == '-';
        }
        return true;
    }

    // ---- операции со скриптом

    private void startEdit(ScriptBlock block, int slot, double mouseX, double mouseY) {
        commitEdit();
        this.nameEditing = false;
        BlockType.Slot spec = block.getType().slot(slot);
        if (spec == null) {
            return;
        }

        if (spec.type() == SlotType.KEY) {
            this.bindingBlock = block;
            this.bindingSlot = slot;
            MenuSound.play(MenuSound.CHARGE, 1.0f, 0.5f);
            return;
        }

        if (spec.type().dropdown()) {
            openPopup(block, slot, mouseX, mouseY);
            return;
        }

        this.editingBlock = block;
        this.editingSlot = slot;
        this.editBuffer.setLength(0);
        this.editBuffer.append(block.get(slot));
        // Первое нажатие заменяет значение целиком, как в Scratch; Enter сохраняет его.
        this.editFresh = true;
        MenuSound.play(MenuSound.CLICK, 1.1f, 0.4f);
    }

    private void commitEdit() {
        if (this.editingBlock == null || this.editingSlot < 0) {
            return;
        }
        String value = this.editBuffer.toString();
        BlockType.Slot spec = this.editingBlock.getType().slot(this.editingSlot);
        if (spec != null && spec.type() == SlotType.NUMBER && value.isBlank()) {
            value = "0";
        }
        this.editingBlock.set(this.editingSlot, value);
        ScriptStore.markDirty();
        this.editingBlock = null;
        this.editingSlot = -1;
        this.editBuffer.setLength(0);
        this.editFresh = false;
    }

    private void commitName() {
        if (this.selected != null) {
            String name = this.nameBuffer.toString().trim();
            if (!name.isEmpty()) {
                ScriptFunction other = ScriptStore.find(name);
                if (other != null && other != this.selected) {
                    name = ScriptStore.uniqueName(name);
                }
                this.selected.setName(name);
                ScriptStore.markDirty();
            }
        }
        this.nameEditing = false;
    }

    private void openPopup(ScriptBlock block, int slot, double mouseX, double mouseY) {
        BlockType.Slot spec = block.getType().slot(slot);
        if (spec == null) {
            return;
        }

        this.popupOptions.clear();
        switch (spec.type()) {
            case MODULE -> this.popupOptions.addAll(swerve.script.ScriptActions.moduleNames());
            case SETTING -> this.popupOptions.addAll(swerve.script.ScriptActions.settingNames(block.get(0)));
            case FUNCTION -> this.popupOptions.addAll(swerve.script.ScriptActions.functionNames());
            default -> {
                for (String option : spec.options()) {
                    this.popupOptions.add(option);
                }
            }
        }

        if (this.popupOptions.isEmpty()) {
            this.popupOptions.add("—");
        }

        ScriptCanvas.Hit hit = this.canvas.slotAt(mouseX, mouseY);
        this.popupBlock = block;
        this.popupSlot = slot;
        this.popupScroll = 0;

        int index = this.popupOptions.indexOf(block.get(slot));
        if (index > POPUP_ROWS - 2) {
            this.popupScroll = index * Math.max(16, S(22));
        }

        this.popupAnchorX = hit == null ? (int) mouseX : hit.x;
        this.popupAnchorY = hit == null ? (int) mouseY + S(24) : hit.y + hit.height + S(4);
        MenuSound.play(MenuSound.POP, 1.0f, 0.5f);
    }

    private void closePopup() {
        this.popupBlock = null;
        this.popupSlot = -1;
    }

    private void selectOption(String option) {
        if (this.popupBlock == null || this.popupSlot < 0) {
            return;
        }

        this.popupBlock.set(this.popupSlot, option == null ? "" : option);

        // Смена модуля делает прежнюю настройку недействительной — сбрасываем её.
        BlockType type = this.popupBlock.getType();
        if (type == BlockType.SET_SETTING && this.popupSlot == 0) {
            List<String> settings = swerve.script.ScriptActions.settingNames(option);
            if (!settings.contains(this.popupBlock.get(1))) {
                this.popupBlock.set(1, settings.isEmpty() ? "" : settings.get(0));
            }
        }

        ScriptStore.markDirty();
        closePopup();
        MenuSound.play(MenuSound.SELECT);
    }

    private void beginDrag(ScriptBlock block, double mouseX, double mouseY) {
        if (this.selected == null) {
            return;
        }
        commitEdit();

        BlockHolder holder = ScriptBlocks.holderOf(this.selected, block);
        if (holder == null) {
            return;
        }
        int index = ScriptBlocks.indexOf(holder, block);
        if (!ScriptBlocks.detach(holder, block)) {
            return;
        }

        this.dragBlock = block;
        this.dragFromPalette = false;
        this.dragOriginHolder = holder;
        this.dragOriginIndex = index;
        this.dragMoved = false;
        this.dragX = mouseX;
        this.dragY = mouseY;
        MenuSound.play(MenuSound.GRAB, 1.0f, 0.4f);
    }

    private void cancelDrag() {
        if (this.dragBlock == null) {
            return;
        }
        if (!this.dragFromPalette) {
            attach(this.dragOriginHolder, this.dragOriginIndex, this.dragBlock);
        }
        this.dragBlock = null;
        this.dragOriginHolder = null;
        this.dragOriginIndex = -1;
        MenuSound.play(MenuSound.POP, 0.9f, 0.5f);
    }

    private void finishDrag(double x, double y) {
        ScriptBlock block = this.dragBlock;
        if (block == null) {
            return;
        }
        boolean fromPalette = this.dragFromPalette;
        boolean moved = this.dragMoved;

        this.dragBlock = null;
        this.dragFromPalette = false;
        this.dragMoved = false;

        if (this.selected == null) {
            return;
        }

        // Клик по блоку без движения — это не перетаскивание: возвращаем на место,
        // иначе блок молча менял бы порядок от простого нажатия.
        if (!fromPalette && !moved) {
            attach(this.dragOriginHolder, this.dragOriginIndex, block);
            return;
        }

        if (fromPalette && inPalette(x, y)) {
            appendToScript(block);
            return;
        }

        if (!fromPalette && inPalette(x, y)) {
            // Блок уже откреплён — просто уходит в корзину палитры.
            block.setNext(null);
            ScriptStore.markDirty();
            MenuSound.play(MenuSound.DROP, 0.9f, 0.5f);
            return;
        }

        if (!inside(x, y, this.scriptX, this.scriptY, this.scriptW, this.scriptH)) {
            if (fromPalette) {
                appendToScript(block);
            } else {
                attach(this.dragOriginHolder, this.dragOriginIndex, block);
            }
            return;
        }

        place(block, x, y);
    }

    private void place(ScriptBlock block, double x, double y) {
        if (this.selected == null) {
            return;
        }

        // Шляпа всегда остаётся первым блоком: иначе функция потеряет триггер.
        if (block.getType() != null && block.getType().hat()) {
            attach(this.selected, 0, block);
            ScriptStore.markDirty();
            return;
        }

        ScriptCanvas.DropZone zone = this.canvas.dropZoneAt(x, y, S(30));
        if (zone != null) {
            attach(zone.holder, zone.index, block);
        } else {
            attach(this.selected, Integer.MAX_VALUE, block);
        }
        ScriptStore.markDirty();
        MenuSound.play(MenuSound.DROP, 1.0f, 0.45f);
    }

    private void appendToScript(ScriptBlock block) {
        if (this.selected == null) {
            return;
        }
        if (block.getType() != null && block.getType().hat()) {
            attach(this.selected, 0, block);
        } else {
            attach(this.selected, Integer.MAX_VALUE, block);
        }
        ScriptStore.markDirty();
        MenuSound.play(MenuSound.DROP, 1.0f, 0.45f);
    }

    /**
     * [+] Вставка с гарантией, что блок не потеряется.
     *
     * <p>{@link ScriptBlocks#insert} отказывает, если цепочку пытаются вложить в неё
     * саму (иначе список замкнулся бы в кольцо). Игнорировать отказ нельзя: блок к
     * этому моменту уже откреплён от прежнего контейнера, и без возврата он молча
     * исчез бы из редактора. Поэтому при отказе цепочка уезжает в конец корневого
     * стека — блок остаётся видимым, а кольца не возникает.
     */
    private void attach(BlockHolder holder, int index, ScriptBlock block) {
        if (block == null) {
            return;
        }
        if (ScriptBlocks.insert(holder, index, block)) {
            return;
        }
        if (this.selected != null && ScriptBlocks.insert(this.selected, Integer.MAX_VALUE, block)) {
            swerve.Swerve.LOGGER.debug("Script block moved to the end: nesting a stack into itself is rejected");
            return;
        }
        swerve.Swerve.LOGGER.warn("Script block could not be attached to any container");
    }

    private void deleteChain(ScriptBlock block) {
        if (this.selected == null || block == null) {
            return;
        }
        BlockHolder holder = ScriptBlocks.holderOf(this.selected, block);
        if (holder == null || !ScriptBlocks.detach(holder, block)) {
            return;
        }
        block.setNext(null);
        ScriptStore.markDirty();
        MenuSound.play(MenuSound.DROP, 0.85f, 0.5f);
    }

    // ---- вспомогательное

    private void updateHover(double mx, double my) {
        this.hoverBlock = null;
        this.hoverSlot = -1;
        if (this.dragBlock != null || this.popupBlock != null || !overScript(mx, my)) {
            return;
        }
        ScriptCanvas.Hit slot = this.canvas.slotAt(mx, my);
        if (slot != null) {
            this.hoverBlock = slot.block;
            this.hoverSlot = slot.slot;
            return;
        }
        ScriptCanvas.Hit block = this.canvas.blockAt(mx, my);
        if (block != null) {
            this.hoverBlock = block.block;
        }
    }

    private boolean overScript(double x, double y) {
        return inside(x, y, this.scriptX, this.scriptY, this.scriptW, this.scriptH);
    }

    private boolean inPalette(double x, double y) {
        return x >= this.px && x < this.px + this.sideW
                && y >= this.paletteY && y < this.bodyY + this.bodyH;
    }

    private static boolean inside(double x, double y, int rx, int ry, int rw, int rh) {
        return x >= rx && x < rx + rw && y >= ry && y < ry + rh;
    }

    private static double distance(double x, double y, int cx, int cy) {
        double dx = x - cx;
        double dy = y - cy;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private static int clamp(int value, int min, int max) {
        if (max < min) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    private static float clamp(float value, float min, float max) {
        if (max < min) {
            return min;
        }
        return Math.max(min, Math.min(max, value));
    }

    @Override
    public void onClose() {
        // Блок, поднятый мышью, нужно вернуть в скрипт: иначе он остался бы
        // откреплённым в памяти и пропал бы из файла.
        if (this.dragBlock != null) {
            cancelDrag();
        }
        commitEdit();
        commitName();
        ScriptStore.saveIfDirty();
        Module module = ModuleManager.get(Scripts.class);
        if (module != null) {
            module.setEnabledQuiet(false);
        }
        super.onClose();
    }

    /**
     * Тонкая обёртка над движком: редактор не зависит от его внутренностей,
     * а проверка «работает ли функция» читается в коде отрисовки коротко.
     */
    private static final class ScriptEngineRunning {

        private static boolean isRunning(ScriptFunction function) {
            return swerve.script.ScriptEngine.isRunning(function);
        }

        private static void run(ScriptFunction function) {
            swerve.script.ScriptEngine.run(function);
        }

        private static void stop(ScriptFunction function) {
            swerve.script.ScriptEngine.stop(function);
        }
    }
}
