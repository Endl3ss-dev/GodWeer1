/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.lang;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.lang.Translations;

@Environment(value=EnvType.CLIENT)
public final class Lang {
    private static final Path LANG_FILE = Path.of("C:/decide/client1_21_11/language.txt", new String[0]);
    private static final Map<String, String> DICT = Translations.build();
    private static Language current = Lang.readSavedLanguage();

    private Lang() {
    }

    public static void init() {
        System.out.println("[Lang] \u043f\u0435\u0440\u0435\u0432\u043e\u0434\u043e\u0432 \u0432 \u0441\u043b\u043e\u0432\u0430\u0440\u0435: " + DICT.size() + ", \u044f\u0437\u044b\u043a: " + Lang.tag());
    }

    public static Language getLanguage() {
        return current;
    }

    public static boolean isEnglish() {
        return current == Language.ENGLISH;
    }

    public static String tag() {
        return current == Language.ENGLISH ? "EN" : "RU";
    }

    public static void setLanguage(Language language) {
        if (language == null || language == current) {
            return;
        }
        current = language;
        Lang.saveLanguage();
    }

    public static void toggle() {
        Lang.setLanguage(current == Language.ENGLISH ? Language.RUSSIAN : Language.ENGLISH);
    }

    public static void reloadDict() {
    }

    public static String tr(String ru) {
        if (ru == null || current != Language.ENGLISH) {
            return ru;
        }
        String en = DICT.get(ru);
        return en != null ? en : ru;
    }

    public static String pick(String ru, String en) {
        return current == Language.ENGLISH ? en : ru;
    }

    private static Language readSavedLanguage() {
        try {
            String s;
            if (Files.exists(LANG_FILE, new LinkOption[0]) && ((s = Files.readString(LANG_FILE, StandardCharsets.UTF_8).trim()).equalsIgnoreCase("EN") || s.equalsIgnoreCase("ENGLISH"))) {
                return Language.ENGLISH;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return Language.RUSSIAN;
    }

    private static void saveLanguage() {
        try {
            Files.createDirectories(LANG_FILE.getParent(), new FileAttribute[0]);
            Files.writeString(LANG_FILE, (CharSequence)(current == Language.ENGLISH ? "EN" : "RU"), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Language {
        RUSSIAN,
        ENGLISH;

    }
}

