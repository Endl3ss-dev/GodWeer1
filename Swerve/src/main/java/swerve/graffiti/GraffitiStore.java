package swerve.graffiti;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import swerve.Swerve;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * [+] Хранилище единственного граффити мира.
 *
 * <p>Модель намеренно «одно граффити на мир»: игрок носит рисунок с собой, и при
 * установке в новом месте прежний исчезает. Поэтому карта — {@code мир → рисунок},
 * без списков и без измерения в ключе: рисунок может «переехать» из Верхнего мира
 * в Нижний, и в файле останется одна запись.
 *
 * <p>Файл: {@code config/swerve/graffiti.json}. Отдельный от {@code config.json},
 * потому что меняется независимо от настроек модуля.
 *
 * <h2>Формат</h2>
 * <pre>
 * {
 *   "format": 1,
 *   "worlds": {
 *     "singleplayer": {
 *       "minecraft:overworld": [
 *         {"x": 12, "y": 70, "z": -4, "face": "north", "design": "SKETCH", "spin": 0.0, "at": 1700000000000}
 *       ]
 *     }
 *   }
 * }
 * </pre>
 *
 * <p>Структура файла оставлена прежней (мир → измерение → массив). Файл от версии
 * с несколькими рисунками читается и схлопывается до самого свежего: так переход
 * на новую модель не теряет последний рисунок игрока и не требует ручной правки.
 */
public final class GraffitiStore {

    /** Версия формата файла. Несовпадение — файл игнорируется целиком. */
    public static final int FORMAT = 1;

    /** Через сколько мс после правки писать файл. */
    private static final long SAVE_DELAY_MS = 800L;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/graffiti.json");

    /** Мир → единственный рисунок этого мира. */
    private static final Map<String, GraffitiSpot> SPOTS = new LinkedHashMap<>();

    private static boolean loaded;
    private static boolean dirty;
    private static long dirtySince;

    private GraffitiStore() {
    }

    // ---- чтение

    /**
     * Рисунок текущего мира (любое измерение) или {@code null}.
     *
     * <p>Измерение здесь не фильтруется: запись одна на мир, и рендер сам решает,
     * показывать ли её (см. {@link #visible()}).
     */
    public static GraffitiSpot placed() {
        ensureLoaded();
        return SPOTS.get(GraffitiWorld.world());
    }

    /** Рисунок, который нужно рисовать сейчас: только в том измерении, где он лежит. */
    public static GraffitiSpot visible() {
        GraffitiSpot spot = placed();
        if (spot == null) {
            return null;
        }
        return spot.dimension().equals(GraffitiWorld.dimension()) ? spot : null;
    }

    // ---- запись

    /**
     * Ставит рисунок на грань, снимая прежний в этом мире.
     *
     * @return новая запись (никогда не {@code null}, если аргументы корректны)
     */
    public static GraffitiSpot place(BlockPos pos, Direction face, GraffitiDesign design, float spin) {
        if (design == null || face == null || pos == null) {
            return null;
        }
        ensureLoaded();
        // Свежий контекст: сразу после смены измерения кэш мира/измерения может
        // быть ещё от предыдущего, и рисунок ушёл бы не в тот раздел файла.
        GraffitiWorld.refresh();

        GraffitiSpot spot = GraffitiSpot.of(GraffitiWorld.world(), GraffitiWorld.dimension(),
                pos.immutable(), face, design, spin);
        SPOTS.put(spot.world(), spot);
        markDirty();
        return spot;
    }

    /**
     * Убирает рисунок текущего мира.
     *
     * @return удалённая запись или {@code null}, если рисунка не было
     */
    public static GraffitiSpot remove() {
        ensureLoaded();
        GraffitiWorld.refresh();

        GraffitiSpot removed = SPOTS.remove(GraffitiWorld.world());
        if (removed != null) {
            markDirty();
        }
        return removed;
    }

    // ---- тик

    /**
     * Проверка «рисунок ещё на месте» и отложенная запись. Зовётся из тика модуля.
     *
     * <p>Запись снимается, если блок исчез или его грань закрыли: иначе рисунок
     * висел бы в воздухе после разрушения блока и оставался в файле навсегда.
     * Проверка одна — запись в мире тоже одна.
     */
    public static void tick(BlockStateLookup lookup) {
        ensureLoaded();

        GraffitiSpot spot = SPOTS.get(GraffitiWorld.world());
        // Проверять блок можно только в том измерении, где рисунок лежит: в другом
        // измерении по тем же координатам стоит совсем другой блок, и рисунок был бы
        // стёрт при первом же переходе через портал.
        if (spot != null && lookup != null
                && spot.dimension().equals(GraffitiWorld.dimension())
                && !lookup.exists(spot.pos(), spot.face())) {
            SPOTS.remove(spot.world());
            markDirty();
        }

        if (dirty && System.currentTimeMillis() - dirtySince >= SAVE_DELAY_MS) {
            save();
        }
    }

    /** Проверка «грань ещё существует» — уровень передаётся из модуля. */
    public interface BlockStateLookup {
        boolean exists(BlockPos pos, Direction face);
    }

    /** Есть ли у блока грань, к которой можно приклеить рисунок: блок есть, сосед открыт. */
    public static boolean faceExists(Level level, BlockPos pos, Direction face) {
        if (level == null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return false;
        }
        return level.getBlockState(pos.relative(face)).isAir();
    }

    // ---- файл

    /** Читает файл один раз за сессию. Вызывается лениво и из onEnable модуля. */
    public static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        SPOTS.clear();

        if (!Files.isRegularFile(FILE)) {
            return;
        }

        try {
            JsonElement root = JsonParser.parseString(Files.readString(FILE, StandardCharsets.UTF_8));
            if (!root.isJsonObject()) {
                Swerve.LOGGER.warn("Graffiti file {} is not a JSON object, ignoring", FILE);
                return;
            }

            JsonObject object = root.getAsJsonObject();
            JsonElement formatElement = object.get("format");
            int format = formatElement != null && formatElement.isJsonPrimitive()
                    ? formatElement.getAsInt() : FORMAT;
            if (format != FORMAT) {
                Swerve.LOGGER.warn("Graffiti format {} is not supported (expected {}), ignoring", format, FORMAT);
                return;
            }

            JsonElement worlds = object.get("worlds");
            if (worlds == null || !worlds.isJsonObject()) {
                return;
            }

            int skipped = 0;
            int collapsed = 0;
            for (Map.Entry<String, JsonElement> worldEntry : worlds.getAsJsonObject().entrySet()) {
                if (!worldEntry.getValue().isJsonObject()) {
                    continue;
                }
                for (Map.Entry<String, JsonElement> dimensionEntry : worldEntry.getValue().getAsJsonObject().entrySet()) {
                    if (!dimensionEntry.getValue().isJsonArray()) {
                        continue;
                    }
                    for (JsonElement element : dimensionEntry.getValue().getAsJsonArray()) {
                        GraffitiSpot spot = readSpot(worldEntry.getKey(), dimensionEntry.getKey(), element);
                        if (spot == null) {
                            skipped++;
                            continue;
                        }
                        GraffitiSpot existing = SPOTS.get(spot.world());
                        if (existing == null) {
                            SPOTS.put(spot.world(), spot);
                        } else {
                            // Файл от версии с несколькими рисунками: остаётся самый свежий.
                            collapsed++;
                            if (spot.placedAt() >= existing.placedAt()) {
                                SPOTS.put(spot.world(), spot);
                            }
                        }
                    }
                }
            }

            if (skipped > 0) {
                Swerve.LOGGER.warn("Graffiti file {}: skipped {} invalid entries", FILE, skipped);
            }
            if (collapsed > 0) {
                Swerve.LOGGER.info("Graffiti file {}: collapsed {} old entries into one per world", FILE, collapsed);
                markDirty();
            }
        } catch (Exception exception) {
            Swerve.LOGGER.warn("Failed to load graffiti {}", FILE, exception);
            SPOTS.clear();
        }
    }

    private static GraffitiSpot readSpot(String world, String dimension, JsonElement element) {
        if (element == null || !element.isJsonObject()) {
            return null;
        }
        JsonObject object = element.getAsJsonObject();
        try {
            int x = object.get("x").getAsInt();
            int y = object.get("y").getAsInt();
            int z = object.get("z").getAsInt();

            Direction face = Direction.byName(object.get("face").getAsString());
            GraffitiDesign design = GraffitiDesign.byName(object.get("design").getAsString());
            if (face == null || design == null) {
                return null;
            }

            float spin = object.has("spin") ? object.get("spin").getAsFloat() : 0.0F;
            long placedAt = object.has("at") ? object.get("at").getAsLong() : System.currentTimeMillis();
            return new GraffitiSpot(world, dimension, BlockPos.asLong(x, y, z), face, design,
                    GraffitiSpot.normalizeSpin(spin), placedAt);
        } catch (RuntimeException malformed) {
            return null;
        }
    }

    private static void ensureLoaded() {
        if (!loaded) {
            load();
        }
    }

    private static void markDirty() {
        if (!dirty) {
            dirtySince = System.currentTimeMillis();
        }
        dirty = true;
    }

    /** Немедленная запись (выключение модуля, выход из игры). */
    public static void save() {
        ensureLoaded();
        dirty = false;

        try {
            JsonObject root = new JsonObject();
            root.addProperty("format", FORMAT);

            JsonObject worlds = new JsonObject();
            for (GraffitiSpot spot : SPOTS.values()) {
                JsonObject dimensions = worlds.getAsJsonObject(spot.world());
                if (dimensions == null) {
                    dimensions = new JsonObject();
                    worlds.add(spot.world(), dimensions);
                }

                BlockPos pos = spot.pos();
                JsonObject item = new JsonObject();
                item.addProperty("x", pos.getX());
                item.addProperty("y", pos.getY());
                item.addProperty("z", pos.getZ());
                item.addProperty("face", spot.face().getName());
                item.addProperty("design", spot.design().name());
                item.addProperty("spin", spot.spin());
                item.addProperty("at", spot.placedAt());

                JsonArray array = new JsonArray();
                array.add(item);
                dimensions.add(spot.dimension(), array);
            }
            root.add("worlds", worlds);

            Files.createDirectories(FILE.getParent());
            Path temp = FILE.resolveSibling(FILE.getFileName() + ".tmp");
            Files.writeString(temp, GSON.toJson(root), StandardCharsets.UTF_8);
            try {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (Exception atomicUnsupported) {
                Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception exception) {
            // Флаг поднят обратно: следующая попытка в тике, правки не теряются.
            dirty = true;
            Swerve.LOGGER.warn("Failed to save graffiti {}", FILE, exception);
        }
    }
}
