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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * [+] Хранилище граффити: какие рисунки на каких гранях блоков нанесены.
 *
 * <p>Файл: {@code config/swerve/graffiti.json}. Отдельный от {@code config.json},
 * потому что записей много и они меняются независимо от настроек модуля:
 * держать их в общем конфиге — значит переписывать весь файл настроек
 * при каждом баллончике.
 *
 * <h2>Формат</h2>
 * <pre>
 * {
 *   "format": 1,
 *   "worlds": {
 *     "singleplayer": {
 *       "minecraft:overworld": [
 *         {"x": 12, "y": 70, "z": -4, "face": "north", "design": "CAT", "spin": 90.0, "at": 1700000000000}
 *       ]
 *     }
 *   }
 * }
 * </pre>
 *
 * <p>Записи с неизвестным рисунком, гранью или нечисловыми координатами
 * молча пропускаются: файл мог остаться от другой версии клиента, и одна
 * битая строка не должна стирать остальные рисунки.
 *
 * <h2>Границы</h2>
 * Не больше {@link #MAX_PER_WORLD} рисунков на мир: список рисуется каждый кадр,
 * и без границы спам баллончиком превращает рендер в слайд-шоу. При переполнении
 * {@link #place} возвращает {@code null}, а модуль показывает предупреждение —
 * тихая потеря рисунка выглядела бы как баг.
 */
public final class GraffitiStore {

    /** Версия формата файла. Несовпадение — файл игнорируется целиком. */
    public static final int FORMAT = 1;

    /** Предел рисунков на один мир. */
    public static final int MAX_PER_WORLD = 512;

    /** Сколько записей проверять на «блок ещё на месте» за тик. */
    private static final int PRUNE_CHECKS_PER_TICK = 64;

    /** Через сколько мс после правки писать файл (правки идут пачкой при рисовании). */
    private static final long SAVE_DELAY_MS = 1_500L;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("swerve/graffiti.json");

    private record Section(String world, String dimension) {
    }

    private static final Map<Section, List<GraffitiSpot>> SPOTS = new LinkedHashMap<>();
    private static final List<GraffitiSpot> EMPTY = List.of();

    private static boolean loaded;
    private static boolean dirty;
    private static long dirtySince;

    /** Курсор подчистки: за тик проверяется порция, а не весь список. */
    private static int pruneCursor;

    private GraffitiStore() {
    }

    // ---- чтение

    /**
     * Рисунки текущего мира и измерения — только для чтения.
     *
     * <p>Возвращается живой список без обёртки: рендер зовёт метод каждый кадр,
     * и {@code Collections.unmodifiableList} здесь означал бы мусор в куче
     * на каждый кадр. Менять список можно только методами этого класса.
     */
    public static List<GraffitiSpot> current() {
        ensureLoaded();
        List<GraffitiSpot> list = SPOTS.get(new Section(GraffitiWorld.world(), GraffitiWorld.dimension()));
        return list == null ? EMPTY : list;
    }

    // ---- запись

    /**
     * Ставит рисунок на грань блока (или заменяет прежний на этой грани).
     *
     * @return запись или {@code null}, если в этом мире достигнут {@link #MAX_PER_WORLD}
     *         и грани ещё не было (замена на существующей грани проходит всегда)
     */
    public static GraffitiSpot place(BlockPos pos, Direction face, GraffitiDesign design, float spin) {
        if (design == null || face == null || pos == null) {
            return null;
        }
        ensureLoaded();
        // Свежий контекст: сразу после смены измерения кэш мира/измерения может
        // быть ещё от предыдущего, и рисунок ушёл бы не в тот раздел файла.
        GraffitiWorld.refresh();

        Section section = new Section(GraffitiWorld.world(), GraffitiWorld.dimension());
        List<GraffitiSpot> list = SPOTS.computeIfAbsent(section, key -> new ArrayList<>());
        GraffitiSpot spot = GraffitiSpot.of(section.world(), section.dimension(), pos, face, design, spin);

        for (int i = 0, size = list.size(); i < size; i++) {
            if (list.get(i).key().equals(spot.key())) {
                list.set(i, spot);
                markDirty();
                return spot;
            }
        }

        if (list.size() >= MAX_PER_WORLD) {
            return null;
        }

        list.add(spot);
        markDirty();
        return spot;
    }

    /**
     * Стирает рисунок с грани.
     *
     * @return удалённая запись или {@code null}, если на этой грани рисунка не было
     */
    public static GraffitiSpot remove(BlockPos pos, Direction face) {
        ensureLoaded();
        GraffitiWorld.refresh();
        List<GraffitiSpot> list = SPOTS.get(new Section(GraffitiWorld.world(), GraffitiWorld.dimension()));
        if (list == null) {
            return null;
        }
        long packed = pos.asLong();
        for (int i = 0, size = list.size(); i < size; i++) {
            GraffitiSpot spot = list.get(i);
            if (spot.position() == packed && spot.face() == face) {
                list.remove(i);
                markDirty();
                return spot;
            }
        }
        return null;
    }

    // ---- тик

    /**
     * Подчистка и отложенная запись. Зовётся из тика модуля.
     *
     * <p>Рисунок снимается, если блок исчез (сломали, взорвали): иначе он висел бы
     * в воздухе после каждого разрушения и копился в файле. За тик проверяется
     * порция {@value #PRUNE_CHECKS_PER_TICK} записей по кругу — на большом полотне
     * полный обход каждый тик не нужен, а курсор не даёт одним и тем же записям
     * проверяться постоянно.
     */
    public static void tick(BlockStateLookup lookup) {
        ensureLoaded();

        List<GraffitiSpot> list = SPOTS.get(new Section(GraffitiWorld.world(), GraffitiWorld.dimension()));
        if (list != null && !list.isEmpty() && lookup != null) {
            boolean pruned = false;
            int checks = Math.min(list.size(), PRUNE_CHECKS_PER_TICK);
            if (pruneCursor >= list.size()) {
                pruneCursor = 0;
            }

            for (int i = 0; i < checks && !list.isEmpty(); i++) {
                if (pruneCursor >= list.size()) {
                    pruneCursor = 0;
                }
                GraffitiSpot spot = list.get(pruneCursor);
                if (lookup.exists(spot.pos(), spot.face())) {
                    pruneCursor++;
                } else {
                    // Курсор не сдвигается: на его месте уже стоит следующий элемент.
                    list.remove(pruneCursor);
                    pruned = true;
                }
            }

            if (pruned) {
                markDirty();
            }
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
                        SPOTS.computeIfAbsent(new Section(spot.world(), spot.dimension()),
                                key -> new ArrayList<>()).add(spot);
                    }
                }
            }

            if (skipped > 0) {
                Swerve.LOGGER.warn("Graffiti file {}: skipped {} invalid entries", FILE, skipped);
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
            for (Map.Entry<Section, List<GraffitiSpot>> entry : SPOTS.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    continue;
                }
                Section section = entry.getKey();
                JsonObject dimensions = worlds.getAsJsonObject(section.world());
                if (dimensions == null) {
                    dimensions = new JsonObject();
                    worlds.add(section.world(), dimensions);
                }

                JsonArray array = new JsonArray();
                for (GraffitiSpot spot : entry.getValue()) {
                    BlockPos pos = spot.pos();
                    JsonObject item = new JsonObject();
                    item.addProperty("x", pos.getX());
                    item.addProperty("y", pos.getY());
                    item.addProperty("z", pos.getZ());
                    item.addProperty("face", spot.face().getName());
                    item.addProperty("design", spot.design().name());
                    item.addProperty("spin", spot.spin());
                    item.addProperty("at", spot.placedAt());
                    array.add(item);
                }
                dimensions.add(section.dimension(), array);
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
