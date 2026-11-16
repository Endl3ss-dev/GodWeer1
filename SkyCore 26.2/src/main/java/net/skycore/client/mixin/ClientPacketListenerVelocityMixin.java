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
 * <p>Пакет отменяется целиком прямо в обработчике — точно так же, как это делали клиенты-источники
 * на своём событии пакета. Поэтому ванильный {@code Entity#lerpMotion} не вызывается вообще и
 * скорость игрока остаётся такой, какой её сделал клиент, — ни «применить и вернуть», ни
 * промежуточных состояний нет. Обходы, которым нужно изменить скорость (правка
 * {@code motionX/motionY/motionZ} у пакета в источниках), выставляют её сами уже после отмены.
 *
 * <p>Инъекция стоит в начале обработчика, но действие выполняет только на клиентском потоке:
 * {@code handleSetEntityMotion} первым делом вызывает {@code PacketUtils.ensureRunningOnSameThread},
 * и если пакет пришёл из сети, он сначала перебрасывается в очередь клиента, после чего обработчик
 * (и эта инъекция) выполняется заново уже в тике.
 */
@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerVelocityMixin {
   @Inject(method = "handleSetEntityMotion", at = @At("HEAD"), cancellable = true)
   private void skycore$velocity(ClientboundSetEntityMotionPacket clientboundSetEntityMotionPacket, CallbackInfo callbackInfo) {
      if (VelocityModule.INSTANCE.handleVelocityPacket(clientboundSetEntityMotionPacket)) {
         callbackInfo.cancel();
      }
   }
}
