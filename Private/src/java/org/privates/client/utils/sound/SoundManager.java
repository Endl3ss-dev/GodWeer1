package org.privates.client.utils.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class SoundManager {

    private static final String SOUND_DOMAIN = "minecraft"; // Minecraft domain for default assets
    private static final String SOUND_PATH_PREFIX = "private.sound."; // Prefix for sound names in sounds.json

    public static void playSound(String name, float volume, float pitch) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().getSoundHandler() == null) {
            return; // Не воспроизводить звуки, если не в игре или обработчик звуков не готов
        }
        try {
            // Use the full path for ResourceLocation
            ResourceLocation soundLocation = new ResourceLocation(SOUND_DOMAIN, SOUND_PATH_PREFIX + name.replace('.', '/')); // Convert dot notation to path
            SoundEvent soundEvent = new SoundEvent(soundLocation);
            Minecraft.getInstance().getSoundHandler().play(
                    net.minecraft.client.audio.SimpleSound.master(soundEvent, pitch, volume)
            );
        } catch (Exception e) {
            // Ошибки воспроизведения звука можно игнорировать или выводить в консоль
            System.err.println("Failed to play sound: " + name + " - " + e.getMessage());
        }
    }

    public static void playSound(String name) {
        playSound(name, 1.0f, 1.0f);
    }

    public static void playSound(String name, float pitch) {
        playSound(name, 1.0f, pitch);
    }
}