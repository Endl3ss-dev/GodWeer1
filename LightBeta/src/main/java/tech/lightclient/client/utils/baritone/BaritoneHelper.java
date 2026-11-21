package tech.lightclient.client.utils.baritone;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

/**
 * Безопасная обёртка над Baritone API.
 * Все вызовы к Baritone проходят через этот класс.
 * Если Baritone не установлен — методы возвращают false / ничего не делают.
 */
public final class BaritoneHelper {

    private static Boolean available = null;
    private static Object baritoneInstance = null;

    private BaritoneHelper() {
    }
    public static boolean isAvailable() {
        if (available == null) {
            available = checkBaritonePresence();
        }
        return available;
    }

    private static boolean checkBaritonePresence() {
        try {
            Class.forName("baritone.api.BaritoneAPI");
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            return false;
        }
    }

    public static void resetCache() {
        available = null;
        baritoneInstance = null;
    }

    public static boolean mine(Block... blocks) {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.mine(blocks);
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    /**
     * Идёт к указанной позиции (в радиусе range блоков).
     */
    public static boolean goTo(BlockPos pos, int range) {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.goTo(pos, range);
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    /**
     * Идёт к точной позиции.
     */
    public static boolean goToExact(BlockPos pos) {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.goToExact(pos);
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    /**
     * Останавливает все процессы Baritone.
     */
    public static boolean stop() {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.stop();
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    /**
     * Проверяет, идёт ли сейчас патхфайндинг.
     */
    public static boolean isPathing() {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.isPathing();
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    /**
     * Проверяет, активен ли процесс Mine.
     */
    public static boolean isMining() {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.isMining();
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    /**
     * Отменяет процесс Mine.
     */
    public static boolean cancelMine() {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.cancelMine();
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    public static boolean breakBlock(BlockPos pos, Direction side) {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.breakBlock(pos, side);
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    public static boolean lookAt(float yaw, float pitch) {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.lookAt(yaw, pitch);
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    /**
     * Применяет настройки Baritone для AutoMine.
     */
    public static boolean applySettings(boolean enable, double blockBreakDelay) {
        return applySettings(enable, blockBreakDelay, false, true);
    }

    /**
     * Применяет настройки Baritone с управлением нативным рендером пути/цели.
     */
    public static boolean applySettings(boolean enable, double blockBreakDelay, boolean renderPath) {
        return applySettings(enable, blockBreakDelay, renderPath, true);
    }

    /**
     * Applies Baritone settings with optional camera ownership. AutoEnd must
     * leave freeLook disabled while walking, otherwise Baritone presses
     * forward using the current camera yaw and can drive straight into walls
     * instead of following its calculated path.
     */
    public static boolean applySettings(boolean enable, double blockBreakDelay,
                                        boolean renderPath, boolean freeLook) {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.applySettings(enable, blockBreakDelay, renderPath, freeLook);
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }

    /**
     * Пауза Baritone (без потери цели). true = пауза, false = снять.
     */
    public static boolean setPaused(boolean paused) {
        if (!isAvailable()) return false;
        try {
            return BaritoneAccessor.setPaused(paused);
        } catch (NoClassDefFoundError | Exception e) {
            available = false;
            return false;
        }
    }
}
