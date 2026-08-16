package godweer.utils.item;

import com.mojang.authlib.properties.Property;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DrinkUtil {

    public enum Drink {
        ABSINTHE("Абсент"),
        MEAD("Медовуха"),
        GROG("Грог");

        private final String displayName;

        Drink(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    private record HashEntry(String hash, Drink drink) {
    }

    private static final String TEXTURE_MARKER = "textures.minecraft.net/texture/";
    private static final int MARKER_LENGTH = TEXTURE_MARKER.length();
    private static final int CACHE_LIMIT = 256;

    private static final List<HashEntry> ENTRIES = new ArrayList<>();
    private static final Map<UUID, Drink> DRINK_BY_PROFILE_ID = new HashMap<>();
    private static final Map<String, Drink> DRINK_CACHE = new HashMap<>();

    static {
        ENTRIES.add(new HashEntry("7f806752de76439c0cfde926b4b7692fb9715beada6932f5d42cfd4c349a6", Drink.ABSINTHE));
        ENTRIES.add(new HashEntry("41b8b442af156d654f45255f3fd36e8ad6c61ae55ef41e80412e7fa6cd283e2", Drink.GROG));
        ENTRIES.add(new HashEntry("810afaa1f0efd7fa505c9139571a01b9b1a8cfd6b1e006d1e792b0ceffdf93e7", Drink.MEAD));
    }

    private DrinkUtil() {
    }

    public static void addHash(Drink drink, String hash) {
        if (drink == null || hash == null || hash.isEmpty()) {
            return;
        }
        for (int i = 0, size = ENTRIES.size(); i < size; i++) {
            if (ENTRIES.get(i).hash().equals(hash)) {
                return;
            }
        }
        ENTRIES.add(new HashEntry(hash, drink));
        DRINK_CACHE.clear();
    }

    public static void addProfileId(Drink drink, UUID id) {
        if (drink != null && id != null) {
            DRINK_BY_PROFILE_ID.put(id, drink);
            DRINK_CACHE.clear();
        }
    }

    public static boolean isHead(ItemStack stack) {
        return stack != null && !stack.isEmpty() && stack.is(Items.PLAYER_HEAD);
    }

    public static ResolvableProfile getProfile(ItemStack stack) {
        return isHead(stack) ? stack.get(DataComponents.PROFILE) : null;
    }

    public static UUID getProfileId(ItemStack stack) {
        ResolvableProfile profile = getProfile(stack);
        return profile == null ? null : profile.id().orElse(null);
    }

    public static String getTextureHash(ItemStack stack) {
        ResolvableProfile profile = getProfile(stack);
        if (profile == null) {
            return null;
        }
        String value = firstTextureValue(profile);
        return value == null ? null : decodeHash(value);
    }

    public static Drink getDrink(ItemStack stack) {
        ResolvableProfile profile = getProfile(stack);
        if (profile == null) {
            return null;
        }

        String value = firstTextureValue(profile);
        if (value == null) {
            UUID id = profile.id().orElse(null);
            return id == null ? null : DRINK_BY_PROFILE_ID.get(id);
        }

        Drink cached = DRINK_CACHE.get(value);
        if (cached != null || DRINK_CACHE.containsKey(value)) {
            return cached;
        }

        String hash = decodeHash(value);
        Drink drink = hash == null ? null : matchHash(hash);
        if (drink == null) {
            UUID id = profile.id().orElse(null);
            if (id != null) {
                drink = DRINK_BY_PROFILE_ID.get(id);
            }
        }

        if (DRINK_CACHE.size() >= CACHE_LIMIT) {
            DRINK_CACHE.clear();
        }
        DRINK_CACHE.put(value, drink);
        return drink;
    }

    public static boolean isDrink(ItemStack stack) {
        return getDrink(stack) != null;
    }

    private static String firstTextureValue(ResolvableProfile profile) {
        Collection<Property> textures = profile.properties().get("textures");
        if (textures.isEmpty()) {
            return null;
        }
        for (Property property : textures) {
            String value = property.value();
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
        } catch (IllegalArgumentException exception) {
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
        for (int i = 0, size = ENTRIES.size(); i < size; i++) {
            HashEntry entry = ENTRIES.get(i);
            String knownHash = entry.hash();
            if (hash.equals(knownHash) || hash.startsWith(knownHash) || knownHash.startsWith(hash)) {
                return entry.drink();
            }
        }
        return null;
    }
}
