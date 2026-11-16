/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.fabricmc.loader.api.FabricLoader
 *  net.minecraft.class_310
 *  org.lwjgl.glfw.GLFW
 */
package ru.decide.cosmetics.melee;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;

@Environment(value=EnvType.CLIENT)
public final class MeleeSettings {
    private static final String FILE_NAME = "decide_melee.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("decide_melee.json");
    private static MeleeSettings instance;
    private boolean firstPerson = true;
    private boolean thirdPerson = true;
    private int inspectKey = 72;
    private transient boolean inspectWasDown;

    private MeleeSettings() {
    }

    public static MeleeSettings get() {
        if (instance == null) {
            instance = MeleeSettings.load();
        }
        return instance;
    }

    private static MeleeSettings load() {
        if (Files.exists(PATH, new LinkOption[0])) {
            try {
                MeleeSettings loaded = (MeleeSettings)GSON.fromJson(Files.readString(PATH), MeleeSettings.class);
                if (loaded != null) {
                    return loaded;
                }
            }
            catch (Exception loaded) {
                // empty catch block
            }
        }
        MeleeSettings settings = new MeleeSettings();
        settings.save();
        return settings;
    }

    public boolean isFirstPerson() {
        return this.firstPerson;
    }

    public boolean isThirdPerson() {
        return this.thirdPerson;
    }

    public int getInspectKey() {
        return this.inspectKey;
    }

    public void toggleFirstPerson() {
        this.firstPerson = !this.firstPerson;
        this.save();
    }

    public void toggleThirdPerson() {
        this.thirdPerson = !this.thirdPerson;
        this.save();
    }

    public void setInspectKey(int key) {
        this.inspectKey = key;
        this.save();
    }

    public boolean pollInspectPressed() {
        if (this.inspectKey <= 0) {
            return false;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.method_22683() == null) {
            return false;
        }
        boolean down = GLFW.glfwGetKey((long)client.method_22683().method_4490(), (int)this.inspectKey) == 1;
        boolean pressed = down && !this.inspectWasDown;
        this.inspectWasDown = down;
        return pressed;
    }

    public void save() {
        try {
            Files.createDirectories(PATH.getParent(), new FileAttribute[0]);
            Files.writeString(PATH, (CharSequence)GSON.toJson((Object)this), new OpenOption[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

