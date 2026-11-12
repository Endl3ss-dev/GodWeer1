package releon.ru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import releon.ru.api.events.impl.CloseScreenEvent;
import releon.ru.api.events.impl.PlayerTravelEvent;
import releon.ru.api.events.impl.PushEvent;
import releon.ru.api.events.impl.UsingItemEvent;
import releon.ru.api.module.impl.combat.AuraModule;
import releon.ru.api.module.impl.combat.aura.AngleConnection;
import releon.ru.api.module.impl.player.AntiDrop;
import releon.ru.manager.Manager;
import releon.ru.utils.move.MoveUtil;

@Mixin({ClientPlayerEntity.class})
public abstract class ClientPlayerEntityMixin {
   @Shadow
   private float lastYawClient;
   @Shadow
   private float lastPitchClient;
   @Shadow
   @Final
   public ClientPlayNetworkHandler networkHandler;
   @Shadow
   @Final
   protected MinecraftClient client;
   @Unique
   private double releon$prevX;
   @Unique
   private double releon$prevZ;
   @Unique
   private float releon$prevBodyYaw;

   public ClientPlayerEntityMixin() {
   }

   @Shadow
   public abstract boolean isUsingItem();

   @Inject(
      method = {"pushOutOfBlocks"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void releon$pushOutOfBlocks(double x, double z, CallbackInfo ci) {
      PushEvent event = Manager.postEvent(new PushEvent(PushEvent.Type.BLOCK));
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @Inject(
      method = {"dropSelectedItem"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void releon$preventProtectedDrop(boolean dropEntireStack, CallbackInfoReturnable<Boolean> cir) {
      ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
      if (AntiDrop.shouldBlock(player.getMainHandStack())) {
         cir.setReturnValue(false);
      }
   }

   @Inject(
      method = {"tickMovement"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/input/Input;tick()V",
         shift = Shift.AFTER
      )},
      require = 0
   )
   private void releon$onInputTick(CallbackInfo ci) {
      if (this.client.player != null) {
         Manager.postEvent(new PlayerTravelEvent(Vec3d.ZERO, false));
      }
   }

   @Redirect(
      method = {"applyMovementSpeedFactors"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/util/math/Vec2f;multiply(F)Lnet/minecraft/util/math/Vec2f;",
         ordinal = 1
      ),
      require = 0
   )
   private Vec2f releon$cancelItemSlowdown(Vec2f vec, float multiplier) {
      UsingItemEvent event = Manager.postEvent(new UsingItemEvent((byte)0));
      ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
      return event.isCancelled() && this.isUsingItem() && !player.hasVehicle() ? vec.multiply(1.0F) : vec.multiply(multiplier);
   }

   @Inject(
      method = {"closeHandledScreen"},
      at = {@At("HEAD")},
      cancellable = true,
      require = 0
   )
   private void releon$closeHandledScreenHook(CallbackInfo ci) {
      Screen screen = this.client.currentScreen;
      CloseScreenEvent event = Manager.postEvent(new CloseScreenEvent(screen));
      if (event.isCancelled()) {
         ci.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"sendMovementPackets"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F"
      )},
      require = 0
   )
   private float releon$packetYaw(float original) {
      ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
      AngleConnection controller = AngleConnection.INSTANCE;
      if (!controller.shouldApplyPacketRotation()) {
         this.releon$syncBodyYawCache(player, original);
         return original;
      } else {
         float yaw = controller.getPacketYaw();
         float bodyYaw = MoveUtil.calculateBodyYaw(
            yaw, this.releon$prevBodyYaw, this.releon$prevX, this.releon$prevZ, player.getX(), player.getZ(), player.getHandSwingProgress(1.0F)
         );
         this.releon$prevBodyYaw = bodyYaw;
         this.releon$prevX = player.getX();
         this.releon$prevZ = player.getZ();
         player.setBodyYaw(bodyYaw);
         return yaw;
      }
   }

   @Unique
   private void releon$syncBodyYawCache(ClientPlayerEntity player, float yaw) {
      this.releon$prevBodyYaw = yaw;
      this.releon$prevX = player.getX();
      this.releon$prevZ = player.getZ();
   }

   @ModifyExpressionValue(
      method = {"sendMovementPackets"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F"
      )},
      require = 0
   )
   private float releon$packetPitch(float original) {
      AngleConnection controller = AngleConnection.INSTANCE;
      return controller.shouldApplyPacketRotation() ? controller.getPacketPitch() : original;
   }

   @Inject(
      method = {"sendMovementPackets"},
      at = {@At("TAIL")}
   )
   private void releon$ensureSilentRotationPacket(CallbackInfo ci) {
      AngleConnection controller = AngleConnection.INSTANCE;
      AuraModule aura = AuraModule.getInstance();
      boolean queuedAuraAttack = aura != null && aura.hasQueuedAttack();
      if (!controller.shouldApplyPacketRotation()) {
         if (queuedAuraAttack) {
            aura.flushQueuedAttack();
         }
      } else {
         ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
         float yaw = controller.getPacketYaw();
         float pitch = controller.getPacketPitch();
         boolean rotationChanged = Math.abs(yaw - this.lastYawClient) > 0.001F || Math.abs(pitch - this.lastPitchClient) > 0.001F;
         if (rotationChanged) {
            this.networkHandler.sendPacket(new LookAndOnGround(yaw, pitch, player.isOnGround(), player.horizontalCollision));
            this.lastYawClient = yaw;
            this.lastPitchClient = pitch;
         }

         if (queuedAuraAttack) {
            aura.flushQueuedAttack();
         }
      }
   }
}

