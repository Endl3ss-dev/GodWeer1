package swerve.script;

/**
 * Один блок в скрипте.
 *
 * <p>Модель максимально простая и повторяет Scratch: у блока есть поля
 * ({@link #get(int)}), следующий блок в стеке ({@link #getNext()}) и — у циклов
 * и условий — вложенный стек ({@link #getInner()}). Никаких координат в модели
 * нет: редактор всегда раскладывает стек сверху вниз и пересчитывает геометрию
 * каждый кадр, поэтому перетаскивание — это просто перестановка ссылок.
 *
 * <p>Блок — это ещё и контейнер ({@link BlockHolder}): «пасть» C-блока и есть
 * его вложенный стек. Поэтому {@link #getHead()} и {@link #setHead(ScriptBlock)}
 * работают с тем же полем, что {@link #getInner()} и {@link #setInner(ScriptBlock)},
 * и операции над стеками ({@link ScriptBlocks}) не различают корень функции и
 * вложенный стек цикла.
 */
public final class ScriptBlock implements BlockHolder {

    private final BlockType type;
    private final String[] values;

    private ScriptBlock next;
    private ScriptBlock inner;

    public ScriptBlock(BlockType type) {
        this.type = type;
        int count = type == null ? 0 : type.slotCount();
        this.values = new String[count];
        for (int i = 0; i < count; i++) {
            BlockType.Slot slot = type.slot(i);
            this.values[i] = slot == null ? "" : slot.defaultValue();
        }
    }

    /** Копия блока без связей (для дублирования и перетаскивания из палитры). */
    public static ScriptBlock create(BlockType type) {
        return new ScriptBlock(type);
    }

    public BlockType getType() {
        return this.type;
    }

    public int slotCount() {
        return this.values.length;
    }

    public String get(int index) {
        return index >= 0 && index < this.values.length ? this.values[index] : "";
    }

    public void set(int index, String value) {
        if (index >= 0 && index < this.values.length) {
            this.values[index] = value == null ? "" : value;
        }
    }

    public ScriptBlock getNext() {
        return this.next;
    }

    public void setNext(ScriptBlock next) {
        this.next = next;
    }

    public ScriptBlock getInner() {
        return this.inner;
    }

    public void setInner(ScriptBlock inner) {
        this.inner = inner;
    }

    // ---- BlockHolder: контейнером блока служит его же вложенный стек
    //
    // Эти два метода намеренно повторяют getInner/setInner, а не хранят отдельное
    // поле: иначе у C-блока было бы два независимых «нутра» и вложенные блоки
    // терялись бы при перетаскивании (см. ScriptBlocks.insert/detach).

    @Override
    public ScriptBlock getHead() {
        return this.inner;
    }

    @Override
    public void setHead(ScriptBlock block) {
        this.inner = block;
    }

    /** Длина стека начиная с этого блока (без вложенных стеков). */
    public int chainLength() {
        int length = 0;
        for (ScriptBlock block = this; block != null; block = block.getNext()) {
            length++;
        }
        return length;
    }

    public ScriptBlock tail() {
        ScriptBlock block = this;
        while (block.getNext() != null) {
            block = block.getNext();
        }
        return block;
    }

    /** Все блоки стека в порядке выполнения — удобно для поиска и подсчёта. */
    public java.util.List<ScriptBlock> flatten() {
        java.util.List<ScriptBlock> list = new java.util.ArrayList<>();
        for (ScriptBlock block = this; block != null; block = block.getNext()) {
            list.add(block);
        }
        return list;
    }

    @Override
    public String toString() {
        return "ScriptBlock[" + (this.type == null ? "?" : this.type.id()) + "]";
    }
}
