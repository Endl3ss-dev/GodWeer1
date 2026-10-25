package swerve.ui.script;

import swerve.script.BlockHolder;
import swerve.script.BlockType;
import swerve.script.ScriptActions;
import swerve.script.ScriptBlock;
import swerve.ui.menu.MenuText.Weight;
import swerve.ui.menu.Px;
import swerve.ui.newgui.GuiKeys;

import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * Раскладка и отрисовка блоков в стиле Scratch.
 *
 * <p>Блоки рисуются в реальных пикселях окна (как и всё меню Swerve), поэтому
 * масштаб передаётся коэффициентом {@code u}. За кадр канвас считает геометрию
 * один раз и складывает её в три списка: попадания по блокам, попадания по
 * полям и точки вставки. Экран ввода пользуется ими же — благодаря этому
 * кликабельно ровно то, что нарисовано.
 *
 * <p>В модели нет координат, стек всегда идёт сверху вниз. Перетаскивание —
 * это перестановка ссылок в {@link swerve.script.ScriptBlocks}, а не перенос
 * пикселей, поэтому блоки никогда не наезжают друг на друга.
 */
public final class ScriptCanvas {

    /** Попадание по блоку: {@code slot < 0} — шапка, иначе — конкретное поле. */
    public static final class Hit {

        public final ScriptBlock block;
        public final int slot;
        public final int x;
        public final int y;
        public final int width;
        public final int height;

        Hit(ScriptBlock block, int slot, int x, int y, int width, int height) {
            this.block = block;
            this.slot = slot;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public boolean inside(double px, double py) {
            return px >= this.x && px < this.x + this.width && py >= this.y && py < this.y + this.height;
        }
    }

    /** Точка вставки: индекс в контейнере и полоса, подсвечиваемая при перетаскивании. */
    public static final class DropZone {

        public final BlockHolder holder;
        public final int index;
        public final int x;
        public final int y;
        public final int width;

        DropZone(BlockHolder holder, int index, int x, int y, int width) {
            this.holder = holder;
            this.index = index;
            this.x = x;
            this.y = y;
            this.width = width;
        }

        public boolean near(double px, double py, int radius) {
            return px >= this.x - radius && px <= this.x + this.width + radius
                    && py >= this.y - radius && py <= this.y + radius;
        }
    }

    private static final int OUTLINE_WHITE = 0xFFFFFFFF;

    private final List<Hit> blocks = new ArrayList<>();
    private final List<Hit> slots = new ArrayList<>();
    private final List<DropZone> zones = new ArrayList<>();
    private final Map<ScriptBlock, int[]> metrics = new IdentityHashMap<>();

    private boolean interactive;
    private float unit = 1.0f;

    // Поле, которое сейчас набирают: значение берётся из буфера экрана,
    // чтобы буквы появлялись прямо в блоке, как в Scratch.
    private ScriptBlock editBlock;
    private int editSlotIndex = -1;
    private String editText = "";

    public List<Hit> blocks() {
        return this.blocks;
    }

    public List<Hit> slots() {
        return this.slots;
    }

    public List<DropZone> zones() {
        return this.zones;
    }

    /** Подготовка кадра: сбрасывает кэш метрик и списки попаданий. */
    public void begin(float unit) {
        this.metrics.clear();
        this.blocks.clear();
        this.slots.clear();
        this.zones.clear();
        this.unit = unit <= 0.0f ? 1.0f : unit;
    }

    /** Включает сбор попаданий и точек вставки (для палитры выключено). */
    public void interactive(boolean interactive) {
        this.interactive = interactive;
    }

    /** Подсветка редактируемого поля: в блоке рисуется буфер, а не сохранённое значение. */
    public void editing(ScriptBlock block, int slot, String text) {
        this.editBlock = block;
        this.editSlotIndex = slot;
        this.editText = text == null ? "" : text;
    }

    // ---- метрики

    public static int font(float u) {
        return Math.max(9, Math.round(15.0f * u));
    }

    private static int padLeft(float u) {
        return Math.max(7, Math.round(12.0f * u));
    }

    private static int padRight(float u) {
        return Math.max(7, Math.round(12.0f * u));
    }

    private int gap() {
        return Math.max(3, Math.round(6.0f * this.unit));
    }

    public int headerHeight() {
        return this.headerHeight(this.unit);
    }

    public int headerHeight(float u) {
        return Math.max(22, Math.round(34.0f * u));
    }

    private int slotHeight(float u) {
        return Math.max(15, Math.round(22.0f * u));
    }

    private int corner(float u) {
        return Math.max(3, Math.round(7.0f * u));
    }

    private int indent(float u) {
        return Math.max(10, Math.round(24.0f * u));
    }

    private int mouthPad(float u) {
        return Math.max(4, Math.round(8.0f * u));
    }

    private int mouthMin(float u) {
        return Math.max(14, Math.round(24.0f * u));
    }

    private int bottomBar(float u) {
        return Math.max(5, Math.round(9.0f * u));
    }

    // ---- измерение

    public int width(ScriptBlock block, float u) {
        return metrics(block, u)[0];
    }

    public int height(ScriptBlock block, float u) {
        return metrics(block, u)[1];
    }

    private int[] metrics(ScriptBlock block, float u) {
        int[] cached = this.metrics.get(block);
        if (cached != null) {
            return cached;
        }

        BlockType type = block.getType();
        int[] value = new int[2];
        if (type == null) {
            this.metrics.put(block, value);
            return value;
        }

        int fs = font(u);
        int ownWidth = padLeft(u);

        for (int i = 0; i < type.slotCount() + 1; i++) {
            String word = type.word(i);
            if (!word.isEmpty()) {
                if (ownWidth > padLeft(u)) {
                    ownWidth += gap();
                }
                ownWidth += Math.round(Px.textWidth(word, Weight.BOLD, fs));
            }
            if (i < type.slotCount()) {
                if (ownWidth > padLeft(u)) {
                    ownWidth += gap();
                }
                ownWidth += slotWidth(block, i, u);
            }
        }
        ownWidth += padRight(u);

        int ownHeight = headerHeight(u);
        if (type.container()) {
            int innerHeight = chainHeight(block.getInner(), u);
            ownHeight += mouthPad(u) + Math.max(mouthMin(u), innerHeight) + mouthPad(u) + bottomBar(u);
            ownWidth = Math.max(ownWidth, indent(u) + chainWidth(block.getInner(), u) + padRight(u));
        }

        value[0] = ownWidth;
        value[1] = ownHeight;
        this.metrics.put(block, value);
        return value;
    }

    public int chainWidth(ScriptBlock head, float u) {
        int width = 0;
        for (ScriptBlock block = head; block != null; block = block.getNext()) {
            width = Math.max(width, width(block, u));
        }
        return width;
    }

    public int chainHeight(ScriptBlock head, float u) {
        int height = 0;
        for (ScriptBlock block = head; block != null; block = block.getNext()) {
            height += height(block, u);
        }
        return height;
    }

    private int slotWidth(ScriptBlock block, int index, float u) {
        BlockType.Slot slot = block.getType().slot(index);
        if (slot == null) {
            return Math.round(40.0f * u);
        }

        int fs = font(u);
        int width = Math.round(Px.textWidth(display(block, index), Weight.BOLD, fs)) + Math.round(16.0f * u);

        int min = switch (slot.type()) {
            case NUMBER -> Math.round(38.0f * u);
            case KEY -> Math.round(46.0f * u);
            case BOOL, OP -> Math.round(46.0f * u);
            default -> Math.round(66.0f * u);
        };
        if (slot.type().dropdown()) {
            width += Math.round(10.0f * u);
        }
        return Math.max(min, width);
    }

    /** Как значение поля выглядит в блоке. */
    public String display(ScriptBlock block, int index) {
        if (block == this.editBlock && index == this.editSlotIndex) {
            return this.editText.length() > 22 ? this.editText.substring(0, 21) + "…" : this.editText;
        }

        BlockType.Slot slot = block.getType().slot(index);
        String value = block.get(index);
        if (slot == null) {
            return value;
        }

        if (slot.type() == swerve.script.SlotType.KEY) {
            int key = ScriptActions.parseKey(value);
            return key == -1 ? "клавиша" : GuiKeys.keyName(key);
        }
        if (value.isBlank()) {
            return switch (slot.type()) {
                case MODULE -> "модуль";
                case SETTING -> "настройка";
                case FUNCTION -> "функция";
                case OPTION -> "звук";
                case BOOL -> "да";
                case OP -> "=";
                case KEY -> "клавиша";
                case NUMBER -> "0";
                case TEXT -> "текст";
            };
        }
        if (slot.type() == swerve.script.SlotType.OPTION) {
            return value.endsWith(".wav") ? value.substring(0, value.length() - 4) : value;
        }
        if (value.length() > 22) {
            return value.substring(0, 21) + "…";
        }
        return value;
    }

    // ---- отрисовка

    /**
     * Рисует стек начиная с {@code head} и возвращает нижнюю границу.
     *
     * @param holder контейнер (функция или C-блок), нужен для точек вставки
     */
    public int drawChain(GuiGraphics g, BlockHolder holder, ScriptBlock head, int x, int y, float alpha) {
        int top = y;
        int width = chainWidth(head, this.unit);
        int index = 0;

        for (ScriptBlock block = head; block != null; block = block.getNext()) {
            if (this.interactive && holder != null) {
                this.zones.add(new DropZone(holder, index, x, top, Math.max(width, 40)));
            }
            drawBlock(g, block, x, top, alpha);
            top += height(block, this.unit);
            index++;
        }

        if (this.interactive && holder != null) {
            this.zones.add(new DropZone(holder, index, x, top, Math.max(width, 40)));
        }
        return top;
    }

    /** Одиночный блок без вложенных попаданий — палитра и «призрак» перетаскивания. */
    public void drawBlock(GuiGraphics g, ScriptBlock block, int x, int y, float alpha) {
        BlockType type = block.getType();
        if (type == null) {
            return;
        }

        int u = Math.round(this.unit);
        int header = headerHeight(this.unit);
        int total = height(block, this.unit);
        int width = width(block, this.unit);
        int color = alpha(type.category().color(), alpha);
        int radius = corner(this.unit);

        // Тень чуть ниже и правее: блок «лежит» на полотне, как в Scratch.
        Px.round(g, x + Math.max(1, u / 2), y + Math.max(1, u / 2), width, total, radius, 0x14000000);
        Px.round(g, x, y, width, total, radius, color);

        if (type.hat()) {
            // Шляпа: скругляем только верх, низ остаётся срезом под следующий блок.
            int r = Math.round(16.0f * this.unit);
            Px.round(g, x, y, width, header, r, color);
            Px.rect(g, x, y + header / 2, width, header - header / 2, color);
        }

        drawWords(g, block, x, y, header, alpha);

        if (type.container() && this.interactive) {
            // Тело C-блока тоже перетаскивается; попадания детей добавятся позже
            // и потому перекроют его при поиске снизу вверх.
            this.blocks.add(new Hit(block, -1, x, y, width, total));
        }

        if (type.container()) {
            int innerHeight = chainHeight(block.getInner(), this.unit);
            int inner = Math.max(mouthMin(this.unit), innerHeight);
            int innerY = y + header + mouthPad(this.unit) + Math.max(0, (inner - innerHeight) / 2);
            drawChain(g, block, block.getInner(), x + indent(this.unit), innerY, alpha);

            int bar = bottomBar(this.unit);
            Px.round(g, x, y + total - bar, width, bar, Math.max(2, radius - 2),
                    alpha(swerve.script.BlockCategory.darken(type.category().color(), 0.88f), alpha));
        }
    }

    private void drawWords(GuiGraphics g, ScriptBlock block, int x, int y, int header, float alpha) {
        BlockType type = block.getType();
        int fs = font(this.unit);
        int cx = x + padLeft(this.unit);
        int textY = y + (header - fs) / 2;
        int white = alpha(0xFFFFFF, alpha);

        for (int i = 0; i < type.slotCount() + 1; i++) {
            String word = type.word(i);
            if (!word.isEmpty()) {
                Px.text(g, word, cx, textY, Weight.BOLD, fs, white);
                cx += Math.round(Px.textWidth(word, Weight.BOLD, fs)) + gap();
            }
            if (i >= type.slotCount()) {
                continue;
            }

            int width = slotWidth(block, i, this.unit);
            int height = slotHeight(this.unit);
            int sy = y + (header - height) / 2;

            Px.round(g, cx, sy, width, height, height / 2, alpha(0xFFFFFF, alpha * 0.95f));
            Px.outline(g, cx, sy, width, height, height / 2, alpha(0x33000000, alpha));

            String caption = display(block, i);
            int textWidth = Math.round(Px.textWidth(caption, Weight.BOLD, fs));
            int tx = cx + Math.round(7.0f * this.unit);
            if (type.slot(i) != null && type.slot(i).type().dropdown()) {
                tx = cx + Math.max(Math.round(7.0f * this.unit), (width - textWidth) / 2 - Math.round(3.0f * this.unit));
            }
            Px.text(g, caption, tx, sy + (height - fs) / 2, Weight.BOLD, fs, alpha(0xFF575E75, alpha));

            if (block == this.editBlock && i == this.editSlotIndex
                    && (System.currentTimeMillis() / 500) % 2 == 0) {
                int caretX = tx + Math.round(Px.textWidth(caption, Weight.BOLD, fs)) + Math.max(1, Math.round(1.5f * this.unit));
                g.fill(caretX, sy + Math.round(3.0f * this.unit), caretX + Math.max(1, Math.round(this.unit)),
                        sy + height - Math.round(3.0f * this.unit), 0xFF575E75);
            }

            if (type.slot(i) != null && type.slot(i).type().dropdown()) {
                chevron(g, cx + width - Math.round(10.0f * this.unit), sy + height / 2, alpha(0xFF7C829A, alpha));
            }

            if (this.interactive) {
                this.slots.add(new Hit(block, i, cx, sy, width, height));
            }
            cx += width + gap();
        }

        if (this.interactive) {
            this.blocks.add(new Hit(block, -1, x, y, width(block, this.unit), headerHeight(this.unit)));
        }
    }

    /** Маленькая стрелка «есть список»: четыре строки по одному пикселю. */
    private void chevron(GuiGraphics g, int cx, int cy, int color) {
        int u = Math.max(1, Math.round(this.unit));
        for (int i = 0; i < 4; i++) {
            int width = (4 - i) * u;
            g.fill(cx - width / 2, cy - 2 * u + i * u, cx + (width + 1) / 2, cy - 2 * u + i * u + u, color);
        }
    }

    private static int alpha(int color, float alpha) {
        int a = (int) (((color >>> 24) & 0xFF) * Math.max(0.0f, Math.min(1.0f, alpha)));
        return (a << 24) | (color & 0xFFFFFF);
    }

    /** Поле, на котором стоит курсор, или {@code null}. */
    public Hit slotAt(double px, double py) {
        for (int i = this.slots.size() - 1; i >= 0; i--) {
            Hit hit = this.slots.get(i);
            if (hit.inside(px, py)) {
                return hit;
            }
        }
        return null;
    }

    /** Блок под курсором; вложенные блоки важнее родителя. */
    public Hit blockAt(double px, double py) {
        for (int i = this.blocks.size() - 1; i >= 0; i--) {
            Hit hit = this.blocks.get(i);
            if (hit.inside(px, py)) {
                return hit;
            }
        }
        return null;
    }

    /** Ближайшая точка вставки в радиусе или {@code null}. */
    public DropZone dropZoneAt(double px, double py, int radius) {
        DropZone best = null;
        double bestDistance = Double.MAX_VALUE;

        for (int i = 0, size = this.zones.size(); i < size; i++) {
            DropZone zone = this.zones.get(i);
            if (!zone.near(px, py, radius)) {
                continue;
            }
            double dx = px - zone.x;
            double dy = py - zone.y;
            double distance = dx * dx + dy * dy;
            if (distance < bestDistance) {
                bestDistance = distance;
                best = zone;
            }
        }
        return best;
    }

    public static int white() {
        return OUTLINE_WHITE;
    }

    /** Список блоков категории — для палитры. */
    public static List<BlockType> typesOf(swerve.script.BlockCategory category) {
        List<BlockType> types = new ArrayList<>();
        for (BlockType type : BlockType.values()) {
            if (type.category() == category) {
                types.add(type);
            }
        }
        return types;
    }
}
