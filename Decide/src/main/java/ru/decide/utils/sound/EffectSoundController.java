/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_1113
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 */
package ru.decide.utils.sound;

import java.util.HashMap;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1113;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import ru.decide.utils.sound.EffectAmbienceSound;

@Environment(value=EnvType.CLIENT)
public final class EffectSoundController {
    private static final String[] VALID_SOUNDS = new String[]{"weather.rain", "block.water.ambient", "ambient.underwater.loop", "block.fire.ambient", "block.lava.ambient", "block.portal.ambient", "block.end_portal.ambient", "block.beacon.ambient", "block.bubble_column.upwards_ambient", "entity.bee.loop", "entity.bat.loop", "entity.phantom.ambient", "entity.ghast.ambient", "entity.warden.ambient", "entity.elder_guardian.ambient", "entity.allay.ambient_without_item", "weather.rain.above", "ambient.basalt_deltas.loop", "ambient.crimson_forest.loop", "ambient.warped_forest.loop", "ambient.soul_sand_valley.loop", "ambient.nether_wastes.loop", "block.amethyst_block.chime", "entity.fox.ambient", "entity.wind_charge.loop", "block.trial_spawner.ambient", "block.trial_spawner.ambient_ominous"};
    private static final Map<String, Entry> ENTRIES = new HashMap<String, Entry>();
    private static final Map<String, Boolean> SOUND_CACHE = new HashMap<String, Boolean>();

    private EffectSoundController() {
    }

    private static boolean isSoundValid(String soundPath) {
        return SOUND_CACHE.computeIfAbsent(soundPath, path -> {
            try {
                class_310 client = class_310.method_1551();
                if (client == null || client.method_1483() == null) {
                    return false;
                }
                class_3414 event = class_3414.method_47908((class_2960)class_2960.method_60655((String)"minecraft", (String)soundPath));
                return true;
            }
            catch (Exception e) {
                return false;
            }
        });
    }

    public static void tick(String owner, boolean enabled, int token, float volume) {
        class_310 client = class_310.method_1551();
        if (client == null || client.method_1483() == null) {
            return;
        }
        if (!enabled || volume <= 0.002f) {
            EffectSoundController.stop(owner);
            return;
        }
        Entry entry = ENTRIES.get(owner);
        if (entry != null && entry.token == token && !entry.sound.method_4793()) {
            float targetVolume = Math.max(0.001f, volume);
            float currentVolume = entry.sound.getBaseVolume();
            float newVolume = currentVolume + (targetVolume - currentVolume) * 0.15f;
            entry.sound.setBaseVolume(newVolume);
            return;
        }
        EffectSoundController.stop(owner);
        int soundIndex = Math.floorMod(token * 7 + 1, VALID_SOUNDS.length);
        String soundPath = VALID_SOUNDS[soundIndex];
        for (int attempts = 0; !EffectSoundController.isSoundValid(soundPath) && attempts < VALID_SOUNDS.length; ++attempts) {
            soundIndex = (soundIndex + 1) % VALID_SOUNDS.length;
            soundPath = VALID_SOUNDS[soundIndex];
        }
        if (!EffectSoundController.isSoundValid(soundPath)) {
            return;
        }
        float pitch = token == 2 ? 0.54f : 0.55f + (float)Math.floorMod(token * 5 + 1, 17) * 0.04f;
        EffectAmbienceSound sound = new EffectAmbienceSound(owner, soundPath, token, 0.001f, pitch);
        client.method_1483().method_4873((class_1113)sound);
        ENTRIES.put(owner, new Entry(token, sound, soundPath));
    }

    public static boolean isActive(String owner, int token) {
        Entry entry = ENTRIES.get(owner);
        return entry != null && entry.token == token && !entry.sound.method_4793();
    }

    public static void stop(String owner) {
        Entry entry = ENTRIES.remove(owner);
        if (entry != null) {
            entry.sound.stopSound();
        }
    }

    public static void stopAll() {
        for (Entry entry : ENTRIES.values()) {
            entry.sound.stopSound();
        }
        ENTRIES.clear();
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Entry {
        private final int token;
        private final EffectAmbienceSound sound;
        private final String soundPath;

        private Entry(int token, EffectAmbienceSound sound, String soundPath) {
            this.token = token;
            this.sound = sound;
            this.soundPath = soundPath;
        }
    }
}

