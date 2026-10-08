package never.lose.cosmetics;

import java.nio.file.Path;
import java.util.Locale;

/**
 * Один аватар Figura на диске. Иммутабелен, создаётся только сканером.
 * searchKey считается заранее, чтобы фильтрация в GUI не аллоцировала строки.
 */
public final class CosmeticEntry {

    private final String name;
    private final String folder;
    private final Path path;
    private final CosmeticSection section;
    private final long sizeBytes;
    private final boolean uploadable;
    private final String searchKey;

    public CosmeticEntry(String name, String folder, Path path, CosmeticSection section, long sizeBytes) {
        this.name = name;
        this.folder = folder;
        this.path = path;
        this.section = section;
        this.sizeBytes = sizeBytes;
        this.uploadable = sizeBytes > 0L && sizeBytes <= 100L * 1024L;
        this.searchKey = (name + " " + folder).toLowerCase(Locale.ROOT);
    }

    public String getName() {
        return name;
    }

    public String getFolder() {
        return folder;
    }

    public Path getPath() {
        return path;
    }

    public CosmeticSection getSection() {
        return section;
    }

    public long getSizeBytes() {
        return sizeBytes;
    }

    /** Лимит бэкенда Figura - 100 KB: крупнее не выгрузится и другие игроки аватар не увидят. */
    public boolean isUploadable() {
        return uploadable;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public String getSizeLabel() {
        if (sizeBytes <= 0L) {
            return "?";
        }

        if (sizeBytes < 1024L) {
            return sizeBytes + " B";
        }

        if (sizeBytes < 1024L * 1024L) {
            return (sizeBytes / 1024L) + " KB";
        }

        return String.format(Locale.ROOT, "%.1f MB", sizeBytes / 1048576.0);
    }

    public boolean matches(String query) {
        return query.isEmpty() || searchKey.contains(query);
    }
}
