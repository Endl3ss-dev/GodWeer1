package net.skycore.client.mixin;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.skycore.client.module.movement.VelocityModule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Пакет скорости своего игрока для {@link VelocityModule}.
 *
 * <p>Пара точек входа вместо отмены пакета: перед ванильным обработчиком запоминается текущая
 * скорость игрока, после — модуль решает, вернуть прежнюю скорость (полная отмена), умножить
 * применённую или подставить своё значение. Так обходы из клиентов, которые правили поля пакета
 * ({@code motionX/motionY/motionZ}), работают без доступа к закрытым полям
 * {@code ClientboundSetEntityMotionPacket} в 26.2, а результат для локального игрока тот же.
 *
 * <p>{@code handleSetEntityMotion} выполняется на клиентском потоке, поэтому здесь можно свободно
 * менять скорость игрока.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerVelocityMixin {
   @Inject(method = "handleSetEntityMotion", at = @At("HEAD"))
   private void skycore$velocityBefore(ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket, CallbackInfo callbackInfo) {
      VelocityModule.INSTANCE.beforeVelocityPacket(clientboundSetEntityMotionPacket);
   }

   @Inject(method = "handleSetEntityMotion", at = @At("TAIL"))
   private void skycore$velocityAfter(ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket, CallbackInfo callbackInfo) {
      VelocityModule.INSTANCE.afterVelocityPacket(clientboundSetEntityMotionPacket);
   }
}
