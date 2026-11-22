/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.theme;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.theme.Theme;
import ru.decide.utils.colors.ColorUtil;

@Environment(value=EnvType.CLIENT)
public class ThemeManager {
    private static final Path THEME_DIR = Path.of("C:/decide/client1_21_11/theme", new String[0]);
    private static final Path ACTIVE_FILE = THEME_DIR.resolve(".active");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ThemeManager instance;
    public static final int MAX_THEMES = 9;
    private final List<Theme> themes = new ArrayList<Theme>();
    private Theme active;

    public static ThemeManager get() {
        if (instance == null) {
            instance = new ThemeManager();
            instance.load();
        }
        return instance;
    }

    public List<Theme> getThemes() {
        return this.themes;
    }

    public Theme getActive() {
        if (this.active == null) {
            Theme theme = this.active = this.themes.isEmpty() ? new Theme("Default") : this.themes.get(0);
            if (this.themes.isEmpty()) {
                this.themes.add(this.active);
            }
        }
        return this.active;
    }

    public void setActive(Theme theme) {
        if (theme == null) {
            return;
        }
        this.active = theme;
        this.saveActiveName();
    }

    public boolean canCreate() {
        return this.themes.size() < 9;
    }

    public Theme createTheme(String name) {
        if (!this.canCreate()) {
            return this.getActive();
        }
        name = this.uniqueName(name, null);
        Theme base = this.getActive();
        Theme created = base.copy(name);
        this.themes.add(created);
        this.setActive(created);
        this.saveTheme(created);
        return created;
    }

    public void renameTheme(Theme theme, String newName) {
        if (theme == null || theme.builtin) {
            return;
        }
        String string = newName = newName == null ? "" : newName.trim();
        if (newName.isEmpty() || newName.equalsIgnoreCase(theme.name)) {
            return;
        }
        try {
            Files.deleteIfExists(THEME_DIR.resolve(theme.name + ".json"));
        }
        catch (IOException iOException) {
            // empty catch block
        }
        theme.name = this.uniqueName(newName, theme);
        this.saveTheme(theme);
        if (this.active == theme) {
            this.saveActiveName();
        }
    }

    public void deleteTheme(Theme theme) {
        if (theme == null || theme.builtin || this.themes.size() <= 1) {
            return;
        }
        this.themes.remove(theme);
        try {
            Files.deleteIfExists(THEME_DIR.resolve(theme.name + ".json"));
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (this.active == theme) {
            this.active = this.themes.get(0);
            this.saveActiveName();
        }
    }

    private static Theme builtin(String name, int visual, int hud) {
        Theme t = new Theme(name);
        t.builtin = true;
        t.colors[0] = visual;
        t.colors[5] = hud;
        t.colors[3] = Theme.rgb(255, 255, 255);
        t.colors[4] = Theme.rgb(177, 177, 177);
        t.colors[1] = Theme.rgb(15, 15, 15);
        t.colors[2] = ColorUtil.getColor(255, 255, 255, 15);
        t.colors[6] = ColorUtil.getColor(255, 255, 255, 15);
        t.colors[7] = ColorUtil.getColor(255, 255, 255, 10);
        t.setSlider(0, 0.9f);
        t.setSlider(1, 4.0f);
        t.setBool(0, true);
        t.setBool(1, false);
        return t;
    }

    public void load() {
        this.themes.clear();
        this.themes.add(ThemeManager.builtin("Blue", Theme.rgb(124, 143, 246), Theme.rgb(143, 161, 250)));
        this.themes.add(ThemeManager.builtin("Red", Theme.rgb(246, 124, 126), Theme.rgb(246, 124, 126)));
        try {
            Files.createDirectories(THEME_DIR, new FileAttribute[0]);
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(THEME_DIR, "*.json");){
                for (Path path : stream) {
                    Theme t2 = this.readTheme(path);
                    if (t2 == null || this.nameExists(t2.name, null)) continue;
                    this.themes.add(t2);
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String activeName = this.readActiveName();
        this.active = this.themes.stream().filter(t -> t.name.equals(activeName)).findFirst().orElse(this.themes.get(0));
    }

    public void saveTheme(Theme theme) {
        if (theme == null || theme.builtin) {
            return;
        }
        try {
            int i;
            Files.createDirectories(THEME_DIR, new FileAttribute[0]);
            JsonObject root = new JsonObject();
            root.addProperty("name", theme.name);
            for (i = 0; i < 8; ++i) {
                root.addProperty(ThemeManager.keyOf(i), (Number)theme.colors[i]);
            }
            for (i = 0; i < theme.sliders.length; ++i) {
                root.addProperty(ThemeManager.sliderKeyOf(i), (Number)Float.valueOf(theme.sliders[i]));
            }
            for (i = 0; i < theme.booleans.length; ++i) {
                root.addProperty(ThemeManager.boolKeyOf(i), Boolean.valueOf(theme.booleans[i]));
            }
            Path file = THEME_DIR.resolve(theme.name + ".json");
            try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream(file.toFile()), StandardCharsets.UTF_8);){
                GSON.toJson((JsonElement)root, (Appendable)writer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveActive() {
        this.saveTheme(this.getActive());
    }

    private Theme readTheme(Path path) {
        Theme theme;
        InputStreamReader reader = new InputStreamReader((InputStream)new FileInputStream(path.toFile()), StandardCharsets.UTF_8);
        try {
            String k;
            int i;
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            String name = root.has("name") ? root.get("name").getAsString() : ThemeManager.stripExt(path.getFileName().toString());
            Theme t = new Theme(name);
            for (i = 0; i < 8; ++i) {
                k = ThemeManager.keyOf(i);
                if (!root.has(k)) continue;
                t.colors[i] = root.get(k).getAsInt();
            }
            for (i = 0; i < t.sliders.length; ++i) {
                k = ThemeManager.sliderKeyOf(i);
                if (!root.has(k)) continue;
                t.setSlider(i, root.get(k).getAsFloat());
            }
            for (i = 0; i < t.booleans.length; ++i) {
                k = ThemeManager.boolKeyOf(i);
                if (!root.has(k)) continue;
                t.setBool(i, root.get(k).getAsBoolean());
            }
            theme = t;
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((Reader)reader).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        ((Reader)reader).close();
        return theme;
    }

    private void saveActiveName() {
        try {
            Files.createDirectories(THEME_DIR, new FileAttribute[0]);
            Files.write(ACTIVE_FILE, this.getActive().name.getBytes(StandardCharsets.UTF_8), new OpenOption[0]);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    private String readActiveName() {
        try {
            if (Files.exists(ACTIVE_FILE, new LinkOption[0])) {
                return new String(Files.readAllBytes(ACTIVE_FILE), StandardCharsets.UTF_8).trim();
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return null;
    }

    private String uniqueName(String base, Theme exclude) {
        if (base == null || base.isBlank()) {
            base = "Theme";
        }
        if ((base = base.replaceAll("[\\\\/:*?\"<>|]", "_").trim()).isEmpty()) {
            base = "Theme";
        }
        Object name = base;
        int n = 1;
        while (this.nameExists((String)name, exclude)) {
            name = base + " " + ++n;
        }
        return name;
    }

    private boolean nameExists(String name, Theme exclude) {
        return this.themes.stream().anyMatch(t -> t != exclude && t.name.equalsIgnoreCase(name));
    }

    private static String keyOf(int i) {
        return switch (i) {
            case 0 -> "accent";
            case 1 -> "background";
            case 2 -> "outline";
            case 3 -> "text";
            case 4 -> "text2";
            case 5 -> "element";
            case 6 -> "separator";
            case 7 -> "lightbg";
            default -> "c" + i;
        };
    }

    private static String sliderKeyOf(int i) {
        return switch (i) {
            case 0 -> "opacity";
            case 1 -> "blur";
            default -> "s" + i;
        };
    }

    private static String boolKeyOf(int i) {
        return switch (i) {
            case 0 -> "shadow";
            case 1 -> "distortion";
            default -> "b" + i;
        };
    }

    private static String stripExt(String fileName) {
        int dot = fileName.lastIndexOf(46);
        return dot > 0 ? fileName.substring(0, dot) : fileName;
    }
}

