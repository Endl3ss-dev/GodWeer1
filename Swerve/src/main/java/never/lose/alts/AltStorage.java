package never.lose.alts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;
import never.lose.NeverLose;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class AltStorage {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type LIST_TYPE = new TypeToken<List<StoredAlt>>() {}.getType();
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("endless")
            .resolve("alts.json");

    private AltStorage() {}

    public static List<AltEntry> load() {
        List<AltEntry> result = new ArrayList<>();
        if (!Files.exists(FILE)) return result;

        try (Reader reader = Files.newBufferedReader(FILE)) {
            List<StoredAlt> stored = GSON.fromJson(reader, LIST_TYPE);
            if (stored != null) {
                for (StoredAlt alt : stored) {
                    if (alt != null && alt.name() != null && !alt.name().isBlank()) {
                        result.add(new AltEntry(alt.name(), alt.addedAt(), alt.favorite()));
                    }
                }
            }
        } catch (Exception e) {
            NeverLose.LOG.warn("[Endless] Failed to load alts: {}", e.getMessage());
        }
        return result;
    }

    public static void save(List<AltEntry> entries) {
        try {
            Files.createDirectories(FILE.getParent());

            List<StoredAlt> stored = new ArrayList<>(entries.size());
            for (AltEntry entry : entries) {
                stored.add(new StoredAlt(entry.getName(), entry.getAddedAt(), entry.isFavorite()));
            }

            try (Writer writer = Files.newBufferedWriter(FILE)) {
                GSON.toJson(stored, LIST_TYPE, writer);
            }
        } catch (IOException e) {
            NeverLose.LOG.warn("[Endless] Failed to save alts: {}", e.getMessage());
        }
    }

    // без favorite в JSON Gson подставит false — старые alts.json читаются без миграции
    private record StoredAlt(String name, long addedAt, boolean favorite) {}
}