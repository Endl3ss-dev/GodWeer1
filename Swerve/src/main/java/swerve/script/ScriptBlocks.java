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
        for (int i = 0; block != null && i < ScriptBlock.MAX_CHAIN; i++) {
            if (i == index) {
                return block;
            }
            block = block.getNext();
        }
        return null;
    }

    /** Глубина рекурсивного поиска для вложенных C-блоков. */
    private static final int MAX_DEPTH = 32;

    /**
     * Открепляет {@code target} от родителя, сохраняя цепочку блоков под ним.
     *
     * <p>Важно: {@code next} у снятого блока не обнуляется — вместе с блоком
     * уезжает и весь стек под ним, как в Scratch. Но связь <b>родителя</b> при этом
     * рвётся: цепочка под {@code target} не остаётся в исходном стеке.
     *
     * <p>[!] Раньше родитель получал {@code target.getNext()}, то есть блоки под
     * {@code target} оставались в обоих стеках одновременно. Возврат такой общей
     * цепочки на место (перетаскивание без движения, отмена перетаскивания)
     * замыкал список в кольцо: {@code tail.next} указывал на голову. Дальше
     * {@code ScriptStore.encodeChain} крутился бесконечно и добавлял блоки в
     * {@code JsonArray}, пока не кончалась память — падение клиента в тике:
     * {@code OutOfMemoryError: Java heap space в ScriptStore.encodeChain}.
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
        int guard = 0;

        ScriptBlock head = holder.getHead();
        if (head == target) {
            // Вместе с блоком уезжает весь стек под ним, поэтому контейнер пустеет.
            holder.setHead(null);
            return true;
        }

        for (ScriptBlock block = head; block != null && guard < ScriptBlock.MAX_CHAIN; block = block.getNext(), guard++) {
            if (block == target) {
                return false;
            }
            if (block.getNext() == target) {
                // Хвост уезжает с target: у контейнера не должно остаться ссылок
                // на блоки, которые теперь принадлежат перетаскиваемой цепочке.
                block.setNext(null);
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
        int guard = 0;
        for (ScriptBlock block = head; block != null && guard < ScriptBlock.MAX_CHAIN; block = block.getNext(), guard++) {
            if (block == target) {
                return true;
            }
            if (contains(block.getInner(), target, depth + 1)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Вставляет цепочку {@code first} в позицию {@code index} контейнера.
     *
     * @return {@code false}, если вставка запрещена (стек пытаются вложить в него
     *         самого себя) — вызывающий обязан вернуть блок в какой-то контейнер,
     *         иначе блок молча исчезнет из редактора
     */
    public static boolean insert(BlockHolder holder, int index, ScriptBlock first) {
        if (holder == null || first == null) {
            return false;
        }

        ScriptBlock head = holder.getHead();

        // Защита от петли: нельзя вложить стек в самого себя — ни в C-блок из этого
        // стека, ни в голову контейнера, в который он вставляется. Без второй
        // проверки «вставить цепочку перед её собственным хвостом» замыкало список.
        if (head != null && contains(first, head)) {
            return false;
        }
        if (holder instanceof ScriptBlock container && contains(first, container)) {
            return false;
        }

        ScriptBlock tail = first.tail();

        if (head == null) {
            tail.setNext(null);
            holder.setHead(first);
            return true;
        }

        if (index <= 0) {
            tail.setNext(head);
            holder.setHead(first);
            return true;
        }

        ScriptBlock before = at(head, index - 1);
        if (before == null) {
            // Индекс за концом стека — дописываем в хвост, а не теряем блоки.
            tail.setNext(null);
            head.tail().setNext(first);
            return true;
        }

        tail.setNext(before.getNext());
        before.setNext(first);
        return true;
    }

    /** Индекс блока в контейнере или -1. */
    public static int indexOf(BlockHolder holder, ScriptBlock target) {
        if (holder == null) {
            return -1;
        }
        ScriptBlock head = holder.getHead();
        int i = 0;
        for (ScriptBlock block = head; block != null && i < ScriptBlock.MAX_CHAIN; block = block.getNext(), i++) {
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
        int guard = 0;
        ScriptBlock head = holder.getHead();
        for (ScriptBlock block = head; block != null && guard < ScriptBlock.MAX_CHAIN; block = block.getNext(), guard++) {
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
