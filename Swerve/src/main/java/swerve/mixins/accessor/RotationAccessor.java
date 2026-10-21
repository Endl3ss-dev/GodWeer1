package swerve.mixins.accessor;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Аксессор к полям поворота игрока (yRot / xRot).
 * Используется модулем Aura для принудительной установки углов взгляда.
 */
@Mixin(LocalPlayer.class)
public interface RotationAccessor {

    @Accessor("yRot")
    float swerve$getYaw();

    @Accessor("xRot")
    float swerve$getPitch();

    @Accessor("yRot")
    void swerve$setYaw(float yaw);

    @Accessor("xRot")
    void swerve$setPitch(float pitch);
}
