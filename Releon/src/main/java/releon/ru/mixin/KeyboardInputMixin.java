package releon.ru.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import releon.ru.api.events.impl.InputEvent;
import releon.ru.api.module.impl.combat.AutoSwap;
import releon.ru.api.module.impl.combat.aura.Angle;
import releon.ru.api.module.impl.combat.aura.AngleConnection;
import releon.ru.api.module.impl.combat.aura.AngleConstructor;
import releon.ru.api.module.impl.movement.InventoryMove;
import releon.ru.manager.Manager;
import releon.ru.utils.inventory.InventoryFlowManager;
import releon.ru.utils.modules.warden.rotation.FreeLookController;
import releon.ru.utils.modules.warden.rotation.RotationController;

@Mixin({KeyboardInput.class})
public abstract class KeyboardInputMixin {
   public KeyboardInputMixin() {
   }

   @ModifyExpressionValue(
      method = {"tick"},
      at = {@At(
         value = "NEW",
         target = "(ZZZZZZZ)Lnet/minecraft/util/PlayerInput;"
      )}
   )
   private PlayerInput releon$tickHook(PlayerInput original) {
      InputEvent event = Manager.postEvent(new InputEvent(original));
      InventoryFlowManager.input(event);
      AutoSwap autoSwap = AutoSwap.getInstance();
      if (autoSwap != null && autoSwap.shouldBlockAutoDamageInput()) {
         event.inputNone();
      } else if (autoSwap != null && autoSwap.shouldBlockAuraInput()) {
         event.setDirectionalLow(false, false, false, false);
         event.setJumping(false);
         event.setSprinting(false);
      }

      return this.releon$transformInput(event.getInput());
   }

   @Unique
   private PlayerInput releon$transformInput(PlayerInput input) {
      MinecraftClient client = MinecraftClient.getInstance();
      AngleConnection rotationController = AngleConnection.INSTANCE;
      Angle angle = rotationController.getCurrentAngle();
      AngleConstructor plan = rotationController.getCurrentRotationPlan();
      if (client.player != null && RotationController.isRotating() && FreeLookController.isActive() && !this.releon$shouldPreserveInventoryMovement()) {
         return this.releon$rotateInput(input, FreeLookController.getFreeYaw() - client.player.getYaw());
      } else {
         return client.player != null
               && !this.releon$shouldPreserveInventoryMovement()
               && angle != null
               && plan != null
               && plan.isMoveCorrection()
               && plan.isFreeCorrection()
            ? this.releon$rotateInput(input, client.player.getYaw() - angle.getYaw())
            : input;
      }
   }

   @Unique
   private PlayerInput releon$rotateInput(PlayerInput input, float deltaYaw) {
      float z = this.releon$getMovementMultiplier(input.forward(), input.backward());
      float x = this.releon$getMovementMultiplier(input.left(), input.right());
      float radians = deltaYaw * (float) (Math.PI / 180.0);
      float cos = MathHelper.cos((double)radians);
      float sin = MathHelper.sin((double)radians);
      float newX = x * cos - z * sin;
      float newZ = z * cos + x * sin;
      int movementSideways = Math.round(newX);
      int movementForward = Math.round(newZ);
      return new PlayerInput(movementForward > 0, movementForward < 0, movementSideways > 0, movementSideways < 0, input.jump(), input.sneak(), input.sprint());
   }

   @Unique
   private boolean releon$shouldPreserveInventoryMovement() {
      InventoryMove inventoryMove = InventoryMove.getInstance();
      return inventoryMove != null && inventoryMove.shouldPreserveInventoryMovementInput();
   }

   @Unique
   private float releon$getMovementMultiplier(boolean positive, boolean negative) {
      if (positive == negative) {
         return 0.0F;
      } else {
         return positive ? 1.0F : -1.0F;
      }
   }
}
