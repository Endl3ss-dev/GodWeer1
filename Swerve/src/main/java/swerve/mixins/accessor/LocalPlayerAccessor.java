package swerve.mixins.accessor;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor {

    @Accessor("wasSprinting")
    boolean swerve$wasSprinting();

    /**
     * Ванильный шлюз старта спринта (приватный {@code LocalPlayer.canStartSprinting}): импульс
     * ввода, еда, слепота, использование предмета, транспорт, элитры, крауч. Speed читает его,
     * а не выставляет спринт наугад: спринт — это модификатор атрибута MOVEMENT_SPEED (+30%),
     * и разрешение его поднимать должно совпадать с решением самой игры.
     */
    @Invoker("canStartSprinting")
    boolean swerve$canStartSprinting();
}
