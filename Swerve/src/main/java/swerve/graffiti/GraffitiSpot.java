package swerve.graffiti;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

/**
 * [+] Один рисунок на грани блока.
 *
 * <p>Неизменяемая запись: при переносе рисунка создаётся новая (меняются позиция,
 * грань и время установки), а старая просто выбрасывается из {@link GraffitiStore}.
 * Так у рендера и у хранилища всегда согласованные данные — менять поля «на месте»
 * не нужно и нечем.
 *
 * @param world     ключ мира ({@code singleplayer} или адрес сервера)
 * @param dimension идентификатор измерения, например {@code minecraft:overworld}
 * @param position  позиция блока в упакованном виде, см. {@link BlockPos#asLong}
 * @param face      грань, на которой нарисовано
 * @param design    рисунок
 * @param spin      поворот рисунка вокруг нормали грани, в градусах [0, 360)
 * @param placedAt  время установки в мс ({@link System#currentTimeMillis}) — для проявления
 */
public record GraffitiSpot(String world, String dimension, long position, Direction face,
                           GraffitiDesign design, float spin, long placedAt) {

    public static GraffitiSpot of(String world, String dimension, BlockPos pos, Direction face,
                                  GraffitiDesign design, float spin) {
        return new GraffitiSpot(world, dimension, pos.asLong(), face, design, normalizeSpin(spin),
                System.currentTimeMillis());
    }

    public BlockPos pos() {
        return BlockPos.of(this.position);
    }

    /** Нормализует угол в [0, 360): в файле и в рендере угол всегда канонический. */
    public static float normalizeSpin(float degrees) {
        if (!Float.isFinite(degrees)) {
            return 0.0F;
        }
        float value = degrees % 360.0F;
        return value < 0.0F ? value + 360.0F : value;
    }
}
