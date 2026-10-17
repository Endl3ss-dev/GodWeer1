package swerve.cosmetics.cape;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import swerve.cosmetics.CosmeticStore;
import swerve.cosmetics.Wearables;

/**
 * Состояние плащей: выбранный рисунок и то, что надето на других игроках.
 *
 * <p>Свой плащ показывается другим клиентам Swerve через строку чата {@code SWCOS}
 * (шестое число — номер рисунка, 0 — плаща нет).
 */
public final class CapeState {

    /** Плащи, надетые на других игроков: UUID -> номер рисунка. */
    private static final Map<UUID, Integer> REMOTE = new HashMap<>();

    private static String design;

    private CapeState() {
    }

    /** Выбранный рисунок (null — плаща нет). */
    public static String design() {
        return design;
    }

    public static void design(String name) {
        String next = name == null || name.isEmpty() ? null : name;
        if (design == null ? next == null : design.equals(next)) {
            return;
        }
        design = next;
        Wearables.touch();
        CosmeticStore.save();
    }

    public static void unequip() {
        design(null);
    }

    /** Номер рисунка для синхронизации (0 — плаща нет). */
    public static int index() {
        return Capes.indexOf(design);
    }

    public static void applyRemote(UUID id, int index) {
        if (index <= 0) {
            REMOTE.remove(id);
        } else {
            REMOTE.put(id, index);
        }
    }

    public static int remoteIndex(UUID id) {
        Integer value = REMOTE.get(id);
        return value == null ? 0 : value;
    }

    public static void forget(UUID id) {
        REMOTE.remove(id);
    }

    /** Рисунок другого игрока (null — неизвестно). */
    public static String remoteDesign(UUID id) {
        return Capes.nameOf(remoteIndex(id));
    }
}
