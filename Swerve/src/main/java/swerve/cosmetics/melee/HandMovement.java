package swerve.cosmetics.melee;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;

/**
 * Расстояние, пройденное игроком: один раз за тик, потом интерполируется каждый кадр.
 *
 * <p>Нужен для клипа бега ножа — фаза анимации идёт от пройденного пути.
 */
public final class HandMovement {

    private static LocalPlayer owner;
    private static float previous;
    private static float current;

    private HandMovement() {
    }

    public static void tick(Minecraft mc) {
        if (mc.player != owner) {
            owner = mc.player;
            previous = current = owner == null ? 0 : owner.moveDist;
        } else if (owner != null) {
            previous = current;
            current = owner.moveDist;
            if (current < previous || current - previous > 4) {
                previous = current;
            }
        }
    }

    public static float distance() {
        Minecraft mc = Minecraft.getInstance();
        if (owner != mc.player) {
            return mc.player == null ? 0 : mc.player.moveDist;
        }
        return interpolate(previous, current, mc.getDeltaTracker().getGameTimeDeltaPartialTick(false));
    }

    public static float interpolate(float before, float after, float partial) {
        return Mth.lerp(Mth.clamp(partial, 0.0F, 1.0F), before, after);
    }
}
