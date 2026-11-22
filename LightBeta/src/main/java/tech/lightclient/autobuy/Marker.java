package tech.lightclient.autobuy;

/**
 * NBT-маркер для распознавания кастомного предмета сервера.
 * Порт класса {@code a.i} из DimalaBuy.
 *
 * <ul>
 *     <li>{@link Type#PBV} — значение из {@code PublicBukkitValues} (ключ=значение)</li>
 *     <li>{@link Type#CUSTOM_ENCH} — присутствие кастомного (не minecraft:) зачарования</li>
 *     <li>{@link Type#CUSTOM_DATA} — произвольный NBT ключ {@code parent.child}=значение</li>
 * </ul>
 */
public class Marker {
    public enum Type {
        PBV,
        CUSTOM_ENCH,
        CUSTOM_DATA
    }

    public Type type;
    public String key;
    public String value;

    public Marker() {
    }

    public Marker(Type type, String key, String value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    public static Marker pbv(String key, String value) {
        return new Marker(Type.PBV, key, value);
    }

    public static Marker customEnch(String key) {
        return new Marker(Type.CUSTOM_ENCH, key, null);
    }

    public static Marker customData(String key, String value) {
        return new Marker(Type.CUSTOM_DATA, key, value);
    }

    @Override
    public String toString() {
        return type + "{" + key + (value == null ? "" : "=" + value) + "}";
    }
}
