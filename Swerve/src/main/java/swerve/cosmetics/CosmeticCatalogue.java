package swerve.cosmetics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.resources.ResourceLocation;

import swerve.Swerve;

/**
 * Каталог гардероба: список моделей из {@code assets/swerve/cosmetics/index.json}.
 *
 * <p>Модели и текстуры приехали из Arcanic (Bedrock-геометрия), новое имя — {@code swerve}.
 */
public final class CosmeticCatalogue {

    public static final String WINGS = "wings";
    public static final String BODYWEAR = "bodywear";
    public static final String PET = "pet";
    public static final String HAT = "hat";

    /** Слоты в порядке вкладок гардероба. */
    public static final String[] SLOTS = {WINGS, BODYWEAR, PET, HAT};

    private static final List<CosmeticItem> ALL = load();
    private static final List<List<CosmeticItem>> BY_SLOT = split();

    private CosmeticCatalogue() {
    }

    public static List<CosmeticItem> all() {
        return ALL;
    }

    /** Модели слота (wings/bodywear/pet/hat) — в порядке каталога. */
    public static List<CosmeticItem> of(String slot) {
        int i = index(slot);
        return i < 0 ? Collections.emptyList() : BY_SLOT.get(i);
    }

    public static CosmeticItem byNumber(int n) {
        for (int i = 0; i < ALL.size(); i++) {
            if (ALL.get(i).n() == n) {
                return ALL.get(i);
            }
        }
        return null;
    }

    /** Текстура модели. */
    public static ResourceLocation texture(int n) {
        return ResourceLocation.fromNamespaceAndPath("swerve", "textures/cosmetics/cosmetic_" + n + ".png");
    }

    /** Иконка модели для гардероба. */
    public static ResourceLocation icon(int n) {
        return ResourceLocation.fromNamespaceAndPath("swerve", "textures/cosmetics/icon_" + n + ".png");
    }

    public static int index(String slot) {
        for (int i = 0; i < SLOTS.length; i++) {
            if (SLOTS[i].equals(slot)) {
                return i;
            }
        }
        return -1;
    }

    private static List<List<CosmeticItem>> split() {
        List<List<CosmeticItem>> out = new ArrayList<>();
        for (String slot : SLOTS) {
            List<CosmeticItem> list = new ArrayList<>();
            for (CosmeticItem it : ALL) {
                if (it.type().equals(slot)) {
                    list.add(it);
                }
            }
            out.add(list);
        }
        return out;
    }

    private static List<CosmeticItem> load() {
        List<CosmeticItem> out = new ArrayList<>();
        try (var in = CosmeticCatalogue.class.getResourceAsStream("/assets/swerve/cosmetics/index.json")) {
            if (in == null) {
                return out;
            }
            JsonArray arr = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonArray();
            for (JsonElement e : arr) {
                JsonObject o = e.getAsJsonObject();
                out.add(new CosmeticItem(
                        o.get("n").getAsInt(),
                        o.get("name").getAsString(),
                        o.get("type").getAsString(),
                        (int) num(o, "pos", 1),
                        (int) num(o, "cat", 1),
                        num(o, "scale", 1),
                        num(o, "x", 0),
                        num(o, "y", 0),
                        num(o, "z", 0),
                        num(o, "yaw", 0),
                        num(o, "pitch", 0),
                        num(o, "roll", 0)));
            }
        } catch (Exception e) {
            Swerve.LOGGER.warn("cosmetic catalogue: {}", e.toString());
        }
        return out;
    }

    private static float num(JsonObject o, String k, float def) {
        return o.has(k) && o.get(k).isJsonPrimitive() ? o.get(k).getAsFloat() : def;
    }
}
