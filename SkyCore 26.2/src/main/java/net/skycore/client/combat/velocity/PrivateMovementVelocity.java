package net.skycore.client.combat.velocity;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.util.Util;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.movement.VelocityModule;

/**
 * Private (категория Movement) —
 * {@code Private/src/java/org/privates/client/managers/module/impl/movement/Velocity.java}
 * (легаси-маппинги, {@code SEntityVelocityPacket / SExplosionPacket}).
 *
 * <p>Второй модуль Velocity того же клиента: он лежит в Movement и содержит 17 режимов
 * {@code Grim / Grim Light / Grim Heavy / Grim Stable / Grim Advanced / Matrix / Vulcan / Intave /
 * Verus / NCP / AAC / Spartan / Vanilla / Custom / Zero / Reduce / Jump Reset}.
 * Настройки: «Горизонтальный %» и «Вертикальный %» (только {@code Custom}/{@code Reduce}),
 * «Шанс срабатывания %» (только {@code Grim Advanced}/{@code Matrix}), «Задержка (мс)» и
 * «Уменьшение силы» (только режимы {@code Grim*}»), «Только на земле» ({@code Grim Light}/{@code Grim Stable}),
 * «Блокировать взрывы» и «Отладка».
 *
 * <p>Перенос один в один: правки пакета выражены множителями {@link VelocityAction#scale},
 * абсолютные установки ({@code setMotion}) — через {@link VelocityAction#set} или прямым
 * {@code setDeltaMovement} на тике. Проверка {@code ViaUtil.allowedBypass()} в режиме {@code Grim}
 * требует версии 1.17+, а SkyCore 26.2 её всегда проходит, поэтому проверка снята вместе
 * с сообщением об ошибке.
 */
public final class PrivateMovementVelocity implements VelocityHandler {
   private final VelocityModule module;
   private final Random random = new Random();
   private boolean receivedVelocity;
   private int velocityTicks;
   private long velocityTimerAt;
   private double lastVelocityX;
   private double lastVelocityY;
   private double lastVelocityZ;

   public PrivateMovementVelocity(VelocityModule module) {
      this.module = module;
   }

   @Override
   public void onVelocityPacket(VelocityAction action) {
      LocalPlayer player = Minecraft.getInstance().player;
      if (player == null) {
         return;
      }

      Vec3 velocity = action.packetVelocity();
      this.lastVelocityX = velocity.x;
      this.lastVelocityY = velocity.y;
      this.lastVelocityZ = velocity.z;

      if (this.module.movementDebug().get()) {
         VelocityDebug.received(this.lastVelocityX, this.lastVelocityY, this.lastVelocityZ);
      }

      if (this.module.is(VelocityModule.PM_GRIM)) {
         this.handleGrimVelocity(action);
      } else if (this.module.is(VelocityModule.PM_GRIM_LIGHT)) {
         this.handleGrimLightVelocity(player, action);
      } else if (this.module.is(VelocityModule.PM_GRIM_HEAVY)) {
         this.handleGrimHeavyVelocity(player, action);
      } else if (this.module.is(VelocityModule.PM_GRIM_STABLE)) {
         this.handleGrimStableVelocity(player, action);
      } else if (this.module.is(VelocityModule.PM_GRIM_ADVANCED)) {
         this.handleGrimAdvancedVelocity(action);
      } else if (this.module.is(VelocityModule.PM_MATRIX)) {
         this.handleMatrixVelocity(action);
      } else if (this.module.is(VelocityModule.PM_VULCAN)) {
         action.scale(0.75, 0.9, 0.75);
      } else if (this.module.is(VelocityModule.PM_INTAVE)) {
         if (player.hurtTime > 0) {
            action.scale(0.5, 1.0, 0.5);
         }
      } else if (this.module.is(VelocityModule.PM_VERUS)) {
         action.scale(0.83, 0.95, 0.83);
      } else if (this.module.is(VelocityModule.PM_NCP)) {
         action.scale(0.9, 0.95, 0.9);
      } else if (this.module.is(VelocityModule.PM_AAC)) {
         if (player.onGround()) {
            action.scale(0.0, 1.0, 0.0);
         } else {
            action.scale(0.7, 1.0, 0.7);
         }
      } else if (this.module.is(VelocityModule.PM_SPARTAN)) {
         action.scale(0.8, 0.85, 0.8);
      } else if (this.module.is(VelocityModule.PM_VANILLA)) {
         action.cancel();
      } else if (this.module.is(VelocityModule.PM_CUSTOM) || this.module.is(VelocityModule.PM_REDUCE)) {
         double horizontal = this.module.movementHorizontal().get() / 100.0;
         double vertical = this.module.movementVertical().get() / 100.0;
         action.scale(horizontal, vertical, horizontal);
      } else if (this.module.is(VelocityModule.PM_ZERO)) {
         action.cancel();
      } else if (this.module.is(VelocityModule.PM_JUMP_RESET)) {
         this.receivedVelocity = true;
         this.velocityTicks = 0;
      }
   }

   @Override
   public boolean onInboundPacket(Packet<?> packet) {
      if (!(packet instanceof ClientboundExplodePacket) || !this.module.movementExplosions().get()) {
         return false;
      }
      return this.module.is(VelocityModule.PM_ZERO) || this.module.is(VelocityModule.PM_VANILLA);
   }

   @Override
   public void onTick(LocalPlayer player) {
      if (!this.receivedVelocity) {
         return;
      }

      this.velocityTicks++;

      if (this.module.is(VelocityModule.PM_GRIM)) {
         this.handleGrimUpdate(player);
      } else if (this.module.is(VelocityModule.PM_GRIM_LIGHT)) {
         this.handleGrimLightUpdate(player);
      } else if (this.module.is(VelocityModule.PM_GRIM_HEAVY)) {
         this.handleGrimHeavyUpdate(player);
      } else if (this.module.is(VelocityModule.PM_GRIM_STABLE)) {
         this.handleGrimStableUpdate(player);
      } else if (this.module.is(VelocityModule.PM_GRIM_ADVANCED)) {
         this.handleGrimAdvancedUpdate(player);
      } else if (this.module.is(VelocityModule.PM_MATRIX)) {
         this.handleMatrixUpdate(player);
      } else if (this.module.is(VelocityModule.PM_JUMP_RESET)) {
         this.handleJumpResetUpdate();
      }
   }

   @Override
   public void onInput(PlayerInputEvent event, LocalPlayer player) {
      if (!this.module.is(VelocityModule.PM_JUMP_RESET) || !this.receivedVelocity) {
         return;
      }
      if (event.keyPresses() == null || !event.keyPresses().jump()) {
         return;
      }

      // В источнике отменяется JumpEvent; в SkyCore ближайший эквивалент — снятие прыжка
      // с события ввода плюс обнуление вертикальной скорости, как в источнике.
      event.setJump(false);
      Vec3 motion = player.getDeltaMovement();
      player.setDeltaMovement(new Vec3(motion.x, 0.0, motion.z));
      this.receivedVelocity = false;
      this.velocityTicks = 0;

      if (this.module.movementDebug().get()) {
         VelocityDebug.send("Jump reset applied");
      }
   }

   private void handleGrimVelocity(VelocityAction action) {
      this.receivedVelocity = true;
      this.velocityTicks = 0;
      this.velocityTimerAt = Util.getMillis();
      double reduction = this.module.movementReduction().get();
      action.scale(reduction, reduction, reduction);
   }

   private void handleGrimLightVelocity(LocalPlayer player, VelocityAction action) {
      if (this.module.movementOnlyGround().get() && !player.onGround()) {
         return;
      }
      this.receivedVelocity = true;
      this.velocityTicks = 0;
      action.scale(0.9, 1.0, 0.9);
   }

   private void handleGrimHeavyVelocity(LocalPlayer player, VelocityAction action) {
      this.receivedVelocity = true;
      this.velocityTicks = 0;
      double reduction = this.module.movementReduction().get() * 0.5;
      action.scale(reduction, reduction, reduction);
      // В источнике микро-движения добавляются до применения пакета и перекрываются им же.
      this.addMicroMovements(player);
   }

   private void handleGrimStableVelocity(LocalPlayer player, VelocityAction action) {
      if (this.module.movementOnlyGround().get() && !player.onGround()) {
         return;
      }
      this.receivedVelocity = true;
      this.velocityTicks = 0;
      action.scale(0.85, 0.95, 0.85);
   }

   private void handleGrimAdvancedVelocity(VelocityAction action) {
      if (this.random.nextFloat() * 100.0F > this.module.movementChance().get()) {
         return;
      }
      this.receivedVelocity = true;
      this.velocityTicks = 0;
      double randomFactor = 0.7 + this.random.nextDouble() * 0.3;
      double reduction = this.module.movementReduction().get() * randomFactor;
      action.scale(reduction, reduction, reduction);
      this.velocityTimerAt = Util.getMillis();
   }

   private void handleMatrixVelocity(VelocityAction action) {
      if (this.random.nextFloat() * 100.0F > this.module.movementChance().get()) {
         return;
      }
      action.scale(0.6, 0.8, 0.6);
      this.receivedVelocity = true;
      this.velocityTicks = 0;
   }

   private void handleGrimUpdate(LocalPlayer player) {
      if (Util.getMillis() - this.velocityTimerAt >= this.module.movementGrimDelay().get()) {
         this.applyGrimMotionReduction(player);
      }
   }

   private void handleGrimLightUpdate(LocalPlayer player) {
      if (this.velocityTicks >= 1) {
         Vec3 motion = player.getDeltaMovement();
         player.setDeltaMovement(new Vec3(motion.x * 0.95, motion.y, motion.z * 0.95));
         this.receivedVelocity = false;
      }
   }

   private void handleGrimHeavyUpdate(LocalPlayer player) {
      if (this.velocityTicks >= 2) {
         Vec3 motion = player.getDeltaMovement();
         player.setDeltaMovement(new Vec3(motion.x * 0.6, motion.y * 0.8, motion.z * 0.6));
         this.receivedVelocity = false;
      }
   }

   private void handleGrimStableUpdate(LocalPlayer player) {
      if (this.velocityTicks >= 3) {
         Vec3 motion = player.getDeltaMovement();
         player.setDeltaMovement(new Vec3(motion.x * 0.85, motion.y, motion.z * 0.85));
         this.receivedVelocity = false;
      }
   }

   private void handleGrimAdvancedUpdate(LocalPlayer player) {
      if (this.velocityTicks >= 1 + this.random.nextInt(3)) {
         double reduction = 0.7 + this.random.nextDouble() * 0.2;
         Vec3 motion = player.getDeltaMovement();
         player.setDeltaMovement(new Vec3(motion.x * reduction, motion.y, motion.z * reduction));
         this.receivedVelocity = false;
      }
   }

   private void handleMatrixUpdate(LocalPlayer player) {
      if (this.velocityTicks >= 2) {
         this.addMicroMovements(player);
         Vec3 motion = player.getDeltaMovement();
         player.setDeltaMovement(new Vec3(motion.x * 0.75, motion.y, motion.z * 0.75));
         this.receivedVelocity = false;
      }
   }

   private void handleJumpResetUpdate() {
      if (this.velocityTicks >= 10) {
         this.receivedVelocity = false;
      }
   }

   private void applyGrimMotionReduction(LocalPlayer player) {
      if (!this.receivedVelocity) {
         return;
      }
      double reduction = this.module.movementReduction().get();
      Vec3 motion = player.getDeltaMovement();
      player.setDeltaMovement(new Vec3(motion.x * reduction, motion.y * reduction, motion.z * reduction));
      this.receivedVelocity = false;
      this.velocityTicks = 0;

      if (this.module.movementDebug().get()) {
         VelocityDebug.send("Applied Grim reduction");
      }
   }

   private void addMicroMovements(LocalPlayer player) {
      double microX = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.001;
      double microZ = (ThreadLocalRandom.current().nextDouble() - 0.5) * 0.001;
      Vec3 motion = player.getDeltaMovement();
      player.setDeltaMovement(new Vec3(motion.x + microX, motion.y, motion.z + microZ));
   }

   @Override
   public void reset() {
      this.receivedVelocity = false;
      this.velocityTicks = 0;
      this.velocityTimerAt = 0L;
      this.lastVelocityX = 0.0;
      this.lastVelocityY = 0.0;
      this.lastVelocityZ = 0.0;
   }
}
