package swerve.script;

/**
 * Операции над стеками блоков: поиск, открепление и вставка.
 *
 * <p>Все три операции работают одинаково для корня функции и для «пасти»
 * C-блока, потому что те реализуют {@link BlockHolder}.
 */
public final class ScriptBlocks {

    private ScriptBlocks() {
    }

    /** Блок по индексу в стеке; {@code null}, если индекса нет. */
    public static ScriptBlock at(ScriptBlock head, int index) {
        ScriptBlock block = head;
        int i = 0;
        while (block != null) {
            if (i == index) {
                return block;
            }
            block = block.getNext();
            i++;
        }
        return null;
    }

    /** Глубина рекурсивного поиска для вложенных C-блоков. */
    private static final int MAX_DEPTH = 32;

    /**
     * Открепляет {@code target} от родителя, сохраняя цепочку блоков под ним.
     *
     * <p>Важно: {@code next} у снятого блока не обнуляется — вместе с блоком
     * уезжает и весь стек под ним, как в Scratch.
     *
     * @return {@code true}, если блок был найден и откреплён
     */
    public static boolean detach(BlockHolder holder, ScriptBlock target) {
        return detach(holder, target, 0);
    }

    private static boolean detach(BlockHolder holder, ScriptBlock target, int depth) {
        if (holder == null || target == null || depth > MAX_DEPTH) {
            return false;
        }

        ScriptBlock head = holder.getHead();
        if (head == target) {
            holder.setHead(target.getNext());
            return true;
        }

        for (ScriptBlock block = head; block != null; block = block.getNext()) {
            if (block == target) {
                return false;
            }
            if (block.getNext() == target) {
                block.setNext(target.getNext());
                return true;
            }
            if (block.getInner() != null && detach(block, target, depth + 1)) {
                return true;
            }
        }
        return false;
    }

    /** Есть ли {@code target} внутри стека (включая вложенные C-блоки). */
    public static boolean contains(ScriptBlock head, ScriptBlock target) {
        return contains(head, target, 0);
    }

    private static boolean contains(ScriptBlock head, ScriptBlock target, int depth) {
        if (head == null || target == null || depth > MAX_DEPTH) {
            return false;
        }
        for (ScriptBlock block = head; block != null; block = block.getNext()) {
            if (block == target) {
                return true;
            }
            if (contains(block.getInner(), target, depth + 1)) {
                return true;
            }
        }
        return false;
    }

    /** Вставляет цепочку {@code first} в позицию {@code index} контейнера. */
    public static void insert(BlockHolder holder, int index, ScriptBlock first) {
        if (holder == null || first == null) {
            return;
        }

        // Защита от петли: вложить стек в сам себя нельзя, иначе раскладка
        // и интерпретатор ушли бы в бесконечную рекурсию.
        if (holder instanceof ScriptBlock container && contains(first, container)) {
            return;
        }

        ScriptBlock tail = first.tail();
        ScriptBlock head = holder.getHead();

        if (head == null) {
            tail.setNext(null);
            holder.setHead(first);
            return;
        }

        if (index <= 0) {
            tail.setNext(head);
            holder.setHead(first);
            return;
        }

        ScriptBlock before = at(head, index - 1);
        if (before == null) {
            // Индекс за концом стека — дописываем в хвост, а не теряем блоки.
            tail.setNext(null);
            head.tail().setNext(first);
            return;
        }

        tail.setNext(before.getNext());
        before.setNext(first);
    }

    /** Индекс блока в контейнере или -1. */
    public static int indexOf(BlockHolder holder, ScriptBlock target) {
        if (holder == null) {
            return -1;
        }
        ScriptBlock head = holder.getHead();
        int i = 0;
        for (ScriptBlock block = head; block != null; block = block.getNext(), i++) {
            if (block == target) {
                return i;
            }
        }
        return -1;
    }

    /** Контейнер, в котором лежит блок (корень функции либо C-блок), или null. */
    public static BlockHolder holderOf(BlockHolder root, ScriptBlock target) {
        return holderOf(root, target, 0);
    }

    private static BlockHolder holderOf(BlockHolder holder, ScriptBlock target, int depth) {
        if (holder == null || target == null || depth > MAX_DEPTH) {
            return null;
        }
        ScriptBlock head = holder.getHead();
        for (ScriptBlock block = head; block != null; block = block.getNext()) {
            if (block == target) {
                return holder;
            }
            if (block.getInner() != null) {
                BlockHolder found = holderOf(block, target, depth + 1);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
