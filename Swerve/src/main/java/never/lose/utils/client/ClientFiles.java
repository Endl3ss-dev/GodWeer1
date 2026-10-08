package never.lose.utils.client;

import net.minecraft.util.Util;

import java.io.File;
import java.util.Locale;

public final class ClientFiles {

    public static final String ROOT_NAME = "neverlose";

    private ClientFiles() {
    }

    public static File root() {
        return ensure(new File(ROOT_NAME));
    }

    public static File bindsDir() {
        return dir("binds");
    }

    public static File friendsDir() {
        return dir("friends");
    }

    public static File configsDir() {
        return dir("configs");
    }

    public static File bindsFile() {
        return new File(bindsDir(), "binds.json");
    }

    public static File friendsFile() {
        return new File(friendsDir(), "friends.json");
    }

    public static File clientFile() {
        return new File(root(), "client.json");
    }

    public static File configFile(String name) {
        return new File(configsDir(), sanitize(name) + ".json");
    }

    public static File dir(String name) {
        return ensure(new File(root(), name));
    }

    public static String sanitize(String name) {
        if (name == null) {
            return "unnamed";
        }
        String cleaned = name.trim().replaceAll("[\\\\/:*?\"<>|]", "_");
        return cleaned.isEmpty() ? "unnamed" : cleaned;
    }

    public static void openFolder(File folder) {
        if (folder == null) {
            return;
        }
        ensure(folder);
        try {
            Util.getOperatingSystem().open(folder);
            return;
        } catch (Throwable ignored) {
        }
        try {
            Util.getOperatingSystem().open(folder.toURI());
            return;
        } catch (Throwable ignored) {
        }
        try {
            String path = folder.getAbsolutePath();
            String os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
            if (os.contains("win")) {
                new ProcessBuilder("explorer", path).start();
            } else if (os.contains("mac")) {
                new ProcessBuilder("open", path).start();
            } else {
                new ProcessBuilder("xdg-open", path).start();
            }
        } catch (Exception ignored) {
        }
    }

    private static File ensure(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
