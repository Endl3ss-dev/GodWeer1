package swerve.script;

/**
 * Контейнер стека блоков: либо сама функция (её корневой стек), либо «пасть»
 * C-блока (то, что вложено внутрь цикла или условия).
 *
 * <p>Реализации две: {@link ScriptFunction} (её корневой стек) и
 * {@link ScriptBlock} (вложенный стек C-блока — его «пасть»). Благодаря общему
 * интерфейсу вставка и открепление не различают корень и вложенный стек —
 * см. {@link ScriptBlocks#insert(BlockHolder, int, ScriptBlock)}.
 */
public interface BlockHolder {

    ScriptBlock getHead();

    void setHead(ScriptBlock block);
}
