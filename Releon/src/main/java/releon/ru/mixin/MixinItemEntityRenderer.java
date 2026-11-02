package releon.ru.mixin;

import java.util.HashMap;
import java.util.WeakHashMap;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.render.entity.state.ItemEntityRenderState;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import releon.ru.api.module.impl.visual.ItemPhysics;

@Mixin({ItemEntityRenderer.class})
public abstract class MixinItemEntityRenderer {
   @Unique
   private static final WeakHashMap<ItemEntityRenderState, Boolean> releon$groundStateMap = new WeakHashMap<>();
   @Unique
   private static final HashMap<Integer, Integer> releon$groundHoldMap = new HashMap<>();
   @Unique
   private ItemEntityRenderState releon$currentState;

   public MixinItemEntityRenderer() {
   }

   @Inject(
      method = {"updateRenderState(Lnet/minecraft/entity/ItemEntity;Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;F)V"},
      at = {@At("HEAD")},
      require = 0
   )
   private void releon$captureGroundState(ItemEntity entity, ItemEntityRenderState state, float tickDelta, CallbackInfo ci) {
      releon$groundStateMap.put(state, releon$resolveGroundState(entity, entity.isOnGround()));
   }

   @Redirect(
      method = {"render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V",
         ordinal = 0
      ),
      require = 0
   )
   private void releon$translate(
      MatrixStack matrices,
      float x,
      float y,
      float z,
      ItemEntityRenderState state,
      MatrixStack matricesArg,
      OrderedRenderCommandQueue submitNodeCollector,
      CameraRenderState cameraRenderState
   ) {
      this.releon$currentState = state;
      boolean enabled = releon$isItemPhysicsEnabled();
      boolean stableGround = releon$groundStateMap.getOrDefault(state, Boolean.valueOf(false));
      if (!enabled && !stableGround) {
         matrices.translate(x, y, z);
      } else {
         Box box = state.itemRenderState.getModelBoundingBox();
         matrices.translate(x, -((float)box.minY) + 0.0625F, z);
      }
   }

   @Redirect(
      method = {"render(Lnet/minecraft/client/render/entity/state/ItemEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/entity/ItemEntityRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/ItemStackEntityRenderState;Lnet/minecraft/util/math/random/Random;Lnet/minecraft/util/math/Box;)V"
      ),
      require = 0
   )
   private void releon$renderPhysics(
      MatrixStack matrices, OrderedRenderCommandQueue submitNodeCollector, int light, ItemStackEntityRenderState stackState, Random random, Box box
   ) {
      if (releon$isItemPhysicsEnabled() && this.releon$currentState != null) {
         float age = this.releon$currentState.age;
         float offset = this.releon$currentState.uniqueOffset;
         boolean onGround = releon$groundStateMap.getOrDefault(this.releon$currentState, Boolean.valueOf(false));
         matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-ItemEntity.getRotation(age, offset)));
         if (onGround) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90.0F));
            matrices.translate(0.0F, -((float)box.getLengthY() / 2.0F) + 0.0625F, 0.0F);
         } else {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((age * 15.0F + offset * 360.0F) % 360.0F));
         }
      }

      ItemEntityRenderer.render(matrices, submitNodeCollector, light, stackState, random, box);
   }

   @Unique
   private static boolean releon$isItemPhysicsEnabled() {
      ItemPhysics itemPhysics = ItemPhysics.getInstance();
      return itemPhysics != null && itemPhysics.isEnabled();
   }

   @Unique
   private static boolean releon$resolveGroundState(ItemEntity entity, boolean onGround) {
      int entityId = entity.getId();
      if (onGround) {
         releon$groundHoldMap.put(entityId, 8);
         releon$trimGroundMap();
         return true;
      } else {
         Integer holdTicks = releon$groundHoldMap.get(entityId);
         if (holdTicks != null && holdTicks > 0) {
            Vec3d velocity = entity.getVelocity();
            boolean lowMotion = Math.abs(velocity.x) <= 0.08 && Math.abs(velocity.y) <= 0.08 && Math.abs(velocity.z) <= 0.08;
            if (!lowMotion) {
               releon$groundHoldMap.remove(entityId);
               return false;
            } else {
               if (holdTicks == 1) {
                  releon$groundHoldMap.remove(entityId);
               } else {
                  releon$groundHoldMap.put(entityId, holdTicks - 1);
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   @Unique
   private static void releon$trimGroundMap() {
      if (releon$groundHoldMap.size() > 4096) {
         releon$groundHoldMap.clear();
      }
   }
}
