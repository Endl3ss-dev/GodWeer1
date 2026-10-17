package swerve.cosmetics.melee;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

/** Plays the sounds of melee clips, by their name in the mod's namespace. */
public final class MeleeSoundIds {

    private MeleeSoundIds() {
    }

    public static SoundInstance play(String key) {
        if (key == null || key.isEmpty()) {
            return null;
        }
        var mc = Minecraft.getInstance();
        if (mc.player == null) {
            return null;
        }
        var event = SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath("swerve", key));
        var sound = SimpleSoundInstance.forUI(event, 1, 0.65f);
        mc.getSoundManager().play(sound);
        return sound;
    }

    public static void stop(SoundInstance sound) {
        if (sound != null) {
            Minecraft.getInstance().getSoundManager().stop(sound);
        }
    }

    public static boolean isActive(SoundInstance sound) {
        return sound != null && Minecraft.getInstance().getSoundManager().isActive(sound);
    }
}
