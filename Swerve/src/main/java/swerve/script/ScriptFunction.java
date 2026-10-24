package swerve.script;

/**
 * Пользовательская функция — то, что в Scratch называется «Мой блок».
 *
 * <p>Тело функции — один стек блоков. Верхний блок задаёт триггер: жёлтая
 * шляпа «каждый тик» запускает функцию сама, «когда заходишь в мир» — при
 * входе в мир, «когда функция вызвана» — только вручную (кнопка, бинд или
 * блок «вызвать»). Если шляпы нет, функция тоже считается ручной.
 */
public final class ScriptFunction implements BlockHolder {

    private String name;
    private int key = -1;
    private ScriptBlock stack;

    public ScriptFunction(String name) {
        this.name = name == null || name.isBlank() ? "функция" : name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.length() > 24 ? name.substring(0, 24) : name;
        }
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    @Override
    public ScriptBlock getHead() {
        return this.stack;
    }

    @Override
    public void setHead(ScriptBlock block) {
        this.stack = block;
    }

    public ScriptBlock getStack() {
        return this.stack;
    }

    public void setStack(ScriptBlock stack) {
        this.stack = stack;
    }

    /** Триггер по верхнему блоку; по умолчанию — вызов вручную. */
    public ScriptTrigger getTrigger() {
        if (this.stack == null || this.stack.getType() == null) {
            return ScriptTrigger.MANUAL;
        }
        ScriptTrigger trigger = this.stack.getType().trigger();
        return trigger == null ? ScriptTrigger.MANUAL : trigger;
    }

    /** Шляпа обязана быть первым блоком: если её перетащили ниже, триггер теряется. */
    public boolean hasHat() {
        return this.stack != null && this.stack.getType() != null && this.stack.getType().hat();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
