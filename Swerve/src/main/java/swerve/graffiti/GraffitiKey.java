package swerve.graffiti;

import net.minecraft.core.Direction;

/**
 * [+] Ключ одного граффити: мир + измерение + позиция блока + грань.
 *
 * <p>Позиция упакована в long ({@code BlockPos.asLong}) — ключ не аллоцирует объект
 * на каждое обращение и годится для HashMap без эквализации по координатам.
 * Мир входит в ключ, чтобы одинаковые координаты на двух серверах не конфликтовали.
 */
public record GraffitiKey(String world, String dimension, long position, Direction face) {
}
