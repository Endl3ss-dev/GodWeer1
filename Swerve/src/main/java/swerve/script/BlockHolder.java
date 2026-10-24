package swerve.script;

/**
 * Контейнер стека блоков: либо сама функция (её корневой стек), либо «пасть»
 * C-блока (то, что вложено внутрь цикла или условия).
 *
 * <p>Благодаря общему интерфейсу вставка блока не различает корень скрипта и
 * вложенный стек — см. {@link ScriptBlocks#insert(BlockHolder, int, ScriptBlock)}.
 */
public interface BlockHolder {

    ScriptBlock getHead();

    void setHead(ScriptBlock block);
}
