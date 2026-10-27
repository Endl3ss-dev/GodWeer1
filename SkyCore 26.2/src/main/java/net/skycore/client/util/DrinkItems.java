package net.skycore.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

/**
 * Распознавание «напитков»-голов (абсент, медовуха, грог) по текстуре.
 *
 * <p>Портировано из {@code Swerve/utils/item/DrinkUtil.java}. Логика источника
 * сохранена: голова опознаётся по хешу текстуры внутри профиля предмета, а не по
 * имени, потому что серверы выдают эти головы с произвольными названиями.
 * Переписано под модель предметов SkyCore 26.2:
 * <ul>
 *   <li>{@link ResolvableProfile} читается через {@code partialProfile()}, как в
 *       {@code FindTextureInTagHelper} и {@code TextureHashHelper} — прямого
 *       {@code profile.properties()} у компонента в 26.2 нет;</li>
 *   <li>{@code GameProfile.id()} уже не {@code Optional}, поэтому проверка на null
 *       простая;</li>
 *   <li>кеш по значению текстуры и по UUID профиля не даёт декодировать Base64
 *       каждый кадр/тик.</li>
 * </ul>
 */
public final class DrinkItems {

    /** Напитки серверов с головами. */
    public enum Drink {
        ABSINTHE("Абсент"),
        MEAD("Медовуха"),
        GROG("Грог");

        private final String displayName;

        Drink(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }

    private record HashEntry(String hash, Drink drink) {
    }

    private static final String TEXTURE_MARKER = "textures.minecraft.net/texture/";
    private static final int MARKER_LENGTH = TEXTURE_MARKER.length();
    private static final int CACHE_LIMIT = 256;

    private static final List<HashEntry> ENTRIES = new ArrayList<>();
    private static final Map<java.util.UUID, Drink> BY_PROFILE = new HashMap<>();
    private static final Map<String, Drink> CACHE = new HashMap<>();

    static {
        ENTRIES.add(new HashEntry("7f806752de76439c0cfde926b4b7692fb9715beada6932f5d42cfd4c349a6", Drink.ABSINTHE));
        ENTRIES.add(new HashEntry("41b8b442af156d654f45255f3fd36e8ad6c61ae55ef41e80412e7fa6cd283e2", Drink.GROG));
        ENTRIES.add(new HashEntry("810afaa1f0efd7fa505c9139571a01b9b1a8cfd6b1e006d1e792b0ceffdf93e7", Drink.MEAD));
    }

    private DrinkItems() {
    }

    /** Добавляет известный хеш текстуры (например, с нового сервера). */
    public static void addHash(Drink drink, String hash) {
        if (drink == null || hash == null || hash.isEmpty()) {
            return;
        }
        for (int i = 0; i < ENTRIES.size(); i++) {
            if (ENTRIES.get(i).hash().equals(hash)) {
                return;
            }
        }
        ENTRIES.add(new HashEntry(hash, drink));
        CACHE.clear();
    }

    /** Добавляет соответствие по UUID профиля — для голов без текстуры в компоненте. */
    public static void addProfileId(Drink drink, java.util.UUID id) {
        if (drink != null && id != null) {
            BY_PROFILE.put(id, drink);
            CACHE.clear();
        }
    }

    public static boolean isHead(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(Items.PLAYER_HEAD);
    }

    /** Профиль головы или {@code null}, если это не голова. */
    public static GameProfile profile(ItemStack stack) {
        if (!isHead(stack)) {
            return null;
        }
        ResolvableProfile component = stack.get(DataComponents.PROFILE);
        return component == null ? null : component.partialProfile();
    }

    /** Хеш текстуры (часть URL после {@code /texture/}) или {@code null}. */
    public static String textureHash(ItemStack stack) {
        String value = textureValue(stack);
        return value == null ? null : decodeHash(value);
    }

    /** Напиток, которому принадлежит голова, или {@code null}. */
    public static Drink drink(ItemStack stack) {
        GameProfile profile = profile(stack);
        if (profile == null) {
            return null;
        }
        String value = textureValue(stack);
        if (value == null) {
            return profile.id() == null ? null : BY_PROFILE.get(profile.id());
        }

        Drink cached = CACHE.get(value);
        if (cached != null || CACHE.containsKey(value)) {
            return cached;
        }

        String hash = decodeHash(value);
        Drink drink = hash == null ? null : matchHash(hash);
        if (drink == null && profile.id() != null) {
            drink = BY_PROFILE.get(profile.id());
        }
        if (CACHE.size() >= CACHE_LIMIT) {
            CACHE.clear();
        }
        CACHE.put(value, drink);
        return drink;
    }

    /** Совпадает ли голова с выбранным напитком. */
    public static boolean matches(ItemStack stack, Drink drink) {
        return drink != null && drink(stack) == drink;
    }

    /** Значение свойства {@code textures} или {@code null}. */
    public static String textureValue(ItemStack stack) {
        GameProfile profile = profile(stack);
        if (profile == null) {
            return null;
        }
        PropertyMap properties = profile.properties();
        if (properties == null) {
            return null;
        }
        for (Property property : properties.values()) {
            String value = property == null ? null : property.value();
            if (value != null && !value.isEmpty()) {
                return value;
            }
        }
        return null;
    }

    private static String decodeHash(String base64) {
        String json;
        try {
            json = new String(Base64.getDecoder().decode(base64), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException failure) {
            return null;
        }

        int start = json.indexOf(TEXTURE_MARKER);
        if (start < 0) {
            return null;
        }
        start += MARKER_LENGTH;

        int end = start;
        int length = json.length();
        while (end < length) {
            char character = json.charAt(end);
            if (character == '"' || character == '\\' || character == '?') {
                break;
            }
            end++;
        }
        return end > start ? json.substring(start, end) : null;
    }

    private static Drink matchHash(String hash) {
        for (int i = 0; i < ENTRIES.size(); i++) {
            HashEntry entry = ENTRIES.get(i);
            String known = entry.hash();
            if (hash.equals(known) || hash.startsWith(known) || known.startsWith(hash)) {
                return entry.drink();
            }
        }
        return null;
    }
}
