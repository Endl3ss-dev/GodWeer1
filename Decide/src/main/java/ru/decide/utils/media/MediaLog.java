/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.utils.media;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public final class MediaLog {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/LyricsText");
    private static final Path FILE = MediaLog.resolveFile();
    private static final boolean ENABLED = Boolean.getBoolean("white.lyrics.log") || System.getProperty("white.lyrics.trace") != null;

    private MediaLog() {
    }

    private static Path resolveFile() {
        String path = System.getProperty("white.lyrics.log");
        if (path == null || path.isBlank()) {
            return null;
        }
        return Path.of(path, new String[0]);
    }

    public static boolean enabled() {
        return ENABLED;
    }

    public static void note(String tag, String message) {
        if (!ENABLED) {
            return;
        }
        String line = "[" + tag + "] " + message;
        LOGGER.info(line);
        if (FILE == null) {
            return;
        }
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(FILE, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND));){
                writer.println(line);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

