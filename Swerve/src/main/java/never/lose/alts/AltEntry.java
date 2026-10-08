package never.lose.alts;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class AltEntry {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final String name;
    private final long addedAt;
    private final UUID uuid;

    private boolean favorite;
    private String dateCache;

    public float hoverAnim = 0f;

    public AltEntry(String name, long addedAt) {
        this(name, addedAt, false);
    }

    public AltEntry(String name, long addedAt, boolean favorite) {
        this.name = name;
        this.addedAt = addedAt;
        this.favorite = favorite;
        this.uuid = UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
    }

    public String getName() {
        return name;
    }

    public long getAddedAt() {
        return addedAt;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void toggleFavorite() {
        this.favorite = !favorite;
    }

    public String getDate() {
        if (dateCache == null) {
            dateCache = LocalDate.ofInstant(Instant.ofEpochMilli(addedAt), ZoneId.systemDefault()).format(DATE_FMT);
        }
        return dateCache;
    }
}