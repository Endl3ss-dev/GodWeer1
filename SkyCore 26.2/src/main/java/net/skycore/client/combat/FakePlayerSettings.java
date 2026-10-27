package net.skycore.client.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Настройки поведения фейк-игрока, которые пишет {@code FakePlayerModule}.
 *
 * <p>Почему отдельный класс, а не поля модуля: сам фейк-игрок живёт в
 * {@code command.FakeplayerCommand} (там же вся физика, урон и тотем), и тянуть
 * зависимость команды на GUI-модуль было бы встречной связью. Здесь только
 * примитивы, которые читает {@code FakeplayerCommand.FakePlayer}.
 *
 * <p>Режимы повторяют шкалу из запроса, и означают они «насколько рано бот тратит
 * тотем / поднимает щит», то есть насколько трудно его убить:
 * <ul>
 *   <li>{@code Legit} — как игрок-человек: тотем только на последних хитах,
 *       щит после первого попадания;</li>
 *   <li>{@code Normal} — базовое поведение;</li>
 *   <li>{@code Hard} — тратит тотем раньше, щит держит при приближении;</li>
 *   <li>{@code Extra} — тотем почти на полном здоровье, щит поднят постоянно.</li>
 * </ul>
 */
public final class FakePlayerSettings {

    /** Порог здоровья, при котором бот тратит тотем, по режимам. */
    private static final float[] TOTEM_THRESHOLD = {3.0F, 6.0F, 10.0F, 16.0F};

    private static volatile boolean copyInventory;
    private static volatile boolean useTotem = true;
    private static volatile int totemMode = 1;
    private static volatile int totemCount = 1;
    private static volatile boolean infiniteTotems;
    private static volatile boolean useShield;
    private static volatile int shieldMode = 1;

    private FakePlayerSettings() {
    }

    public static void configure(boolean copyInventory, boolean useTotem, int totemMode, int totemCount,
                                 boolean infiniteTotems, boolean useShield, int shieldMode) {
        FakePlayerSettings.copyInventory = copyInventory;
        FakePlayerSettings.useTotem = useTotem;
        FakePlayerSettings.totemMode = clampMode(totemMode);
        FakePlayerSettings.totemCount = Math.max(0, Math.min(300, totemCount));
        FakePlayerSettings.infiniteTotems = infiniteTotems;
        FakePlayerSettings.useShield = useShield;
        FakePlayerSettings.shieldMode = clampMode(shieldMode);
    }

    /** Сбрасывает всё в «выключено» — вызывается при выключении модуля. */
    public static void reset() {
        configure(false, false, 1, 0, false, false, 1);
    }

    public static boolean copyInventory() {
        return copyInventory;
    }

    public static boolean useTotem() {
        return useTotem;
    }

    public static boolean infiniteTotems() {
        return infiniteTotems;
    }

    public static int totemCount() {
        return totemCount;
    }

    public static int totemMode() {
        return totemMode;
    }

    public static boolean useShield() {
        return useShield;
    }

    public static int shieldMode() {
        return shieldMode;
    }

    /** Здоровье, на котором бот тратит тотем в текущем режиме. */
    public static float totemThreshold() {
        return TOTEM_THRESHOLD[totemMode];
    }

    /** Нужно ли поднимать щит прямо сейчас. */
    public static boolean shieldWanted(Player fake) {
        if (!useShield || fake == null) {
            return false;
        }
        Player self = Minecraft.getInstance().player;
        double distance = self == null ? Double.MAX_VALUE : fake.distanceTo(self);
        return switch (shieldMode) {
            case 0 -> distance <= 4.0 && fake.hurtTime > 0;
            case 2 -> distance <= 12.0;
            case 3 -> true;
            default -> distance <= 6.0;
        };
    }

    private static int clampMode(int mode) {
        return Math.max(0, Math.min(TOTEM_THRESHOLD.length - 1, mode));
    }
}
