/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.ui.theme;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.ui.theme.Theme;
import ru.decide.ui.theme.ThemeManager;

@Environment(value=EnvType.CLIENT)
public final class ThemePersistence {
    private static final File FILE = new File("C:/decide/client1_21_11/config/theme", "lv_theme.json");

    private ThemePersistence() {
    }

    public static void init() {
        ThemeManager.setOnChange(ThemePersistence::save);
        File parent = FILE.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!FILE.exists()) {
            return;
        }
        try (FileReader reader = new FileReader(FILE);){
            Properties props = new Properties();
            props.load(reader);
            String name = props.getProperty("theme");
            try {
                ThemeManager.set(Theme.valueOf(name));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        catch (IOException e) {
            System.err.println("[ThemePersistence] \u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u0442\u0435\u043c\u0443: " + e.getMessage());
        }
    }

    private static void save() {
        try {
            File parent = FILE.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (FileWriter writer = new FileWriter(FILE);){
                Properties props = new Properties();
                props.setProperty("theme", ThemeManager.current().name());
                props.store(writer, "DecideVisuals GUI Theme");
            }
        }
        catch (IOException e) {
            System.err.println("[ThemePersistence] \u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0441\u043e\u0445\u0440\u0430\u043d\u0438\u0442\u044c \u0442\u0435\u043c\u0443: " + e.getMessage());
        }
    }
}

