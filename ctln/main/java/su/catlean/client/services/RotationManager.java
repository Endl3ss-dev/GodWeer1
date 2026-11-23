// deobf by pusia
package su.catlean.client.services;

import java.util.ArrayList;
import java.util.Iterator;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Rotation;
import su.catlean.api.enums.SwitchMode;
import su.catlean.api.event.events.player.EventPlayerTravel;
import su.catlean.api.event.events.player.EventPostPlayerTravel;
import su.catlean.api.event.events.player.FixVelocityEvent;
import su.catlean.api.event.events.player.JumpEvent;
import su.catlean.api.event.events.player.KeyboardInputEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PostInteractItemEvent;
import su.catlean.api.event.events.player.PostSyncEvent;
import su.catlean.api.event.events.player.PreInteractItemEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.event.events.render.LookRenderEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.world.FireWorkVectorEvent;
import su.catlean.api.service.Service;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.aura.AuraSprintHandler;
import su.catlean.client.features.modules.basic.AntiKnockBack;
import su.catlean.client.features.modules.basic.ElytraRecast;
import su.catlean.client.features.modules.basic.MoveFix;
import su.catlean.client.features.modules.cosmetic.FreeLook;
import su.catlean.client.features.modules.rage.ElytraTarget;
import su.catlean.client.features.modules.rage.MaceElytraTarget;
import su.catlean.client.features.modules.world.FreeCam;
import su.catlean.client.services.rotation.LookRotationAction;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.client.services.rotation.SilentRotationAction;
import su.catlean.client.services.rotation.modes.AdvancedRotation;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ClientInputAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class RotationManager implements Service {
   @NotNull
   public static final RotationManager INSTANCE = new RotationManager();
   @NotNull
   private static ArrayList<RotationAction> items = new ArrayList();
   @NotNull
   private static ArrayList<RotationAction> items2 = new ArrayList();
   @NotNull
   private static Rotation rotation = new Rotation(0.0F, 0.0F, false, null, 12, null);
   @Nullable
   private static Rotation rotation2;
   @Nullable
   private static Rotation rotation3;
   @Nullable
   private static Rotation rotation4;
   @Nullable
   private static Rotation rotation5;
   @Nullable
   private static RotationAction rotationAction;
   @Nullable
   private static RotationAction rotationAction2;
   private static float amount;
   private static float amount2;
   private static float amount3;

   private RotationManager() {
   }

   @Nullable
   public final Rotation getRotation3() {
      return rotation3;
   }

   public final void setRotation3(Rotation rotation) {
      rotation3 = rotation;
   }

   @Nullable
   public final Rotation getRotation4() {
      return rotation4;
   }

   public final void setRotation4(Rotation rotation) {
      rotation4 = rotation;
   }

   public final void updateRotationAction(RotationAction sequence) {
      AntiKnockBack.INSTANCE.update();
      items.add(sequence);
   }

   public final void updateRotationAction2(RotationAction sequence) {
      AntiKnockBack.INSTANCE.update();
      items2.add(sequence);
   }

   public final void updateRotation(Rotation rotation, int value) {
      AntiKnockBack.INSTANCE.update();
      items.add(new RotationAction(rotation, value, () -> Unit.INSTANCE));
   }

   public static void defaultImpl(RotationManager rotationManager, Rotation rotation, int value, int value2, Object object) {
      if ((value2 & 2) != 0) {
         value = 150;
      }

      rotationManager.updateRotation(rotation, value);
   }

   @NotNull
   public final Rotation fetchXRot(boolean flag) {
      Iterable<RotationAction> items3 = items;
      Iterator<RotationAction> iterator = items3.iterator();
      Object object;
      if (!iterator.hasNext()) {
         object = null;
      } else {
         RotationAction object2 = iterator.next();
         if (!iterator.hasNext()) {
            object = (RotationAction)object2;
         } else {
            RotationAction rotationAction3 = (RotationAction)object2;
            int value = rotationAction3.getValue();

            do {
               RotationAction object3 = iterator.next();
               RotationAction rotationAction4 = (RotationAction)object3;
               int value2 = rotationAction4.getValue();
               if (value < value2) {
                  object2 = object3;
                  value = value2;
               }
            } while (iterator.hasNext());

            object = (RotationAction)object2;
         }
      }

      if (object != null) {
         Rotation rotation6 = ((RotationAction)object).getRotation();
         if (rotation6 != null) {
            Rotation rotation7 = rotation6.getRotation();
            if (rotation7 != null) {
               return rotation7;
            }
         }
      }

      return flag
         ? new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null)
         : new Rotation(WorldUtil.INSTANCE.getAmount4(), WorldUtil.INSTANCE.getAmount5(), false, null, 12, null);
   }

   public static Rotation defaultImpl(RotationManager rotationManager, boolean flag, int value, Object object) {
      if ((value & 1) != 0) {
         flag = false;
      }

      return rotationManager.fetchXRot(flag);
   }

   @Flow(
      priority = 20
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (rotationAction2 != null) {
         rotationAction2.getAction().invoke();
         rotationAction2 = null;
      }
   }

   @Flow(
      priority = -20
   )
   private final void onPlayerUpdate2(PlayerUpdateEvent playerUpdateEvent) {
      items2.removeIf(arg -> {
         INSTANCE.applyGetX(((RotationAction)arg).getRotation().getAmount(), ((RotationAction)arg).getRotation().getAmount2());
         ((RotationAction)arg).getAction().invoke();
         if (((RotationAction)arg).getRotation().isFlag()) {
            INSTANCE.applyGetX(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot());
         }

         return true;
      });
      Iterable<RotationAction> items3 = items;
      Iterator<RotationAction> iterator = items3.iterator();
      Object object;
      if (!iterator.hasNext()) {
         object = null;
      } else {
         RotationAction object2 = iterator.next();
         if (!iterator.hasNext()) {
            object = object2;
         } else {
            RotationAction rotationAction3 = (RotationAction)object2;
            int value = rotationAction3.getValue();

            do {
               RotationAction object3 = iterator.next();
               RotationAction rotationAction4 = (RotationAction)object3;
               int value2 = rotationAction4.getValue();
               if (value < value2) {
                  object2 = object3;
                  value = value2;
               }
            } while (iterator.hasNext());

            object = object2;
         }
      }

      rotationAction = (RotationAction)object;
      if (rotationAction != null) {
         rotation2 = rotationAction.getRotation();
         if (rotation2.getAmount() == MinecraftHolder.getLocalPlayer().getYRot() && rotation2.getAmount2() == MinecraftHolder.getLocalPlayer().getXRot()) {
            Iterable<RotationAction> items4 = items;
            var arrayList = new ArrayList();
            int value3 = 0;

            for (Object object4 : items4) {
               RotationAction rotationAction5 = (RotationAction)object4;
               if (!Intrinsics.areEqual(rotationAction5, rotationAction)) {
                  arrayList.add(object4);
               }
            }

            var value4 = arrayList;
            Iterator iterator2 = value4.iterator();
            if (!iterator2.hasNext()) {
               object = null;
            } else {
               Object object5 = iterator2.next();
               if (!iterator2.hasNext()) {
                  object = object5;
               } else {
                  RotationAction rotationAction6 = (RotationAction)object5;
                  value3 = rotationAction6.getValue();

                  do {
                     Object object6 = iterator2.next();
                     RotationAction rotationAction7 = (RotationAction)object6;
                     int value5 = rotationAction7.getValue();
                     if (value3 < value5) {
                        object5 = object6;
                        value3 = value5;
                     }
                  } while (iterator2.hasNext());

                  object = object5;
               }
            }

            object = (RotationAction)object;
            if ((RotationAction)object != null) {
               RotationAction rotationAction8 = (RotationAction)object;
               rotation2 = rotationAction8.getRotation();
            }
         }

         rotation2 = rotation2.getRotation();
         if (rotationAction instanceof LookRotationAction) {
            rotationAction.getAction().invoke();
            rotationAction = null;
         }

         if (rotationAction instanceof SilentRotationAction) {
            rotationAction2 = new RotationAction(rotationAction.getRotation(), rotationAction.getValue(), rotationAction.getAction());
            rotationAction = null;
         }

         items.clear();
      }
   }

   @Flow(
      priority = 20
   )
   private final void onPreSync(PreSyncEvent preSyncEvent) {
      rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
      rotation4 = rotation3;
      rotation3 = null;
      if (rotation2 != null) {
         MinecraftHolder.getLocalPlayer().setYRot(rotation2.getAmount());
         MinecraftHolder.getLocalPlayer().setXRot(rotation2.getAmount2());
         rotation3 = rotation2;
         rotation2 = null;
      }
   }

   @Flow(
      priority = -20
   )
   private final void onPostSync(PostSyncEvent postSyncEvent) {
      if (rotationAction != null) {
         rotationAction.getAction().invoke();
         rotationAction = null;
      }

      MinecraftHolder.getLocalPlayer().setYRot(rotation.getAmount());
      MinecraftHolder.getLocalPlayer().setXRot(rotation.getAmount2());
   }

   @Flow
   private final void onLookRender(LookRenderEvent lookRenderEvent) {
      if (rotation3 != null && rotation4 != null) {
         amount = lookRenderEvent.getBodyYaw();
         lookRenderEvent.setBodyYaw(
            Mth.wrapDegrees(Render2D.INSTANCE.computeAmount(rotation4.getAmount(), rotation3.getAmount(), Render3D.INSTANCE.getAmount4()))
         );
         lookRenderEvent.setYaw(0.0F);
         lookRenderEvent.setPitch(
            Mth.wrapDegrees(Render2D.INSTANCE.computeAmount(rotation4.getAmount2(), rotation3.getAmount2(), Render3D.INSTANCE.getAmount4()))
         );
      }
   }

   @Flow
   private final void onJump(JumpEvent jumpEvent) {
      if (this.isFlag2() || this.isFlag()) {
         jumpEvent.setYaw(this.getAmount());
         jumpEvent.cancel();
      }
   }

   @Flow
   private final void onFixVelocity(FixVelocityEvent fixVelocityEvent) {
      if (this.isFlag()) {
         fixVelocityEvent.setVelocity(this.computeVec3(this.getAmount(), fixVelocityEvent.getMovementInput(), fixVelocityEvent.getSpeed()));
         fixVelocityEvent.cancel();
      }
   }

   @Flow
   private final void onKeyboardInput(KeyboardInputEvent keyboardInputEvent) {
      if (this.isFlag()) {
         float value = MinecraftHolder.getLocalPlayer().input.getMoveVector().y;
         float value2 = MinecraftHolder.getLocalPlayer().input.getMoveVector().x;
         float yRot = MinecraftHolder.getLocalPlayer().getYRot();
         float amount4 = (yRot - rotation2.getAmount()) * (float) (Math.PI / 180.0);
         float value3 = (float)Math.cos(amount4);
         float value4 = (float)Math.sin(amount4);
         float roundToInt = MathKt.roundToInt(value * value3 + value2 * value4);
         float roundToInt2 = MathKt.roundToInt(value2 * value3 - value * value4);
         ((ClientInputAccessor)MinecraftHolder.getLocalPlayer().input).setMoveVector(new Vec2(roundToInt2, roundToInt));
         MinecraftHolder.getLocalPlayer().input.keyPresses = new Input(
            roundToInt > 0.0F,
            roundToInt < 0.0F,
            roundToInt2 > 0.0F,
            roundToInt2 < 0.0F,
            MinecraftHolder.getLocalPlayer().input.keyPresses.jump(),
            MinecraftHolder.getLocalPlayer().input.keyPresses.shift(),
            AuraSprintHandler.INSTANCE.isFlag2() ? false : MinecraftHolder.getLocalPlayer().input.keyPresses.sprint()
         );
      }
   }

   @Flow
   private final void onPreInteractItem(PreInteractItemEvent preInteractItemEvent) {
      amount2 = MinecraftHolder.getLocalPlayer().getYRot();
      amount3 = MinecraftHolder.getLocalPlayer().getXRot();
      Rotation xRot = this.fetchXRot(true);
      MinecraftHolder.getLocalPlayer().setYRot(xRot.getAmount());
      MinecraftHolder.getLocalPlayer().setXRot(xRot.getAmount2());
   }

   @Flow
   private final void onPostInteractItem(PostInteractItemEvent postInteractItemEvent) {
      MinecraftHolder.getLocalPlayer().setYRot(amount2);
      MinecraftHolder.getLocalPlayer().setXRot(amount3);
   }

   private final boolean isFlag() {
      return rotation2 != null
         && MoveFix.INSTANCE.getEnabled()
         && rotation2.getMoveFix() != SwitchMode.FORCE
         && MoveFix.INSTANCE.getMode() != MoveFix.Mode.FOCUS
         && !MinecraftHolder.getLocalPlayer().isHandsBusy()
         && !MinecraftHolder.getLocalPlayer().isSwimming()
         && !FreeCam.INSTANCE.getEnabled()
         && !FreeLook.INSTANCE.getEnabled()
         && !ElytraRecast.INSTANCE.getEnabled();
   }

   private final boolean isFlag2() {
      return rotation2 != null
         && (
            MoveFix.INSTANCE.getEnabled()
               || MoveFix.INSTANCE.getEnabled() && MoveFix.INSTANCE.getMode() == MoveFix.Mode.FOCUS
               || ElytraTarget.INSTANCE.getEnabled()
               || MaceElytraTarget.INSTANCE.getEnabled()
               || rotation2.getMoveFix() == SwitchMode.FORCE
               || MinecraftHolder.getLocalPlayer().isHandsBusy()
               || MinecraftHolder.getLocalPlayer().isFallFlying()
               || MinecraftHolder.getLocalPlayer().isSwimming()
         )
         && !FreeCam.INSTANCE.getEnabled()
         && !FreeLook.INSTANCE.getEnabled()
         && !ElytraRecast.INSTANCE.getEnabled();
   }

   @Flow
   private final void onPlayerTravel(EventPlayerTravel eventPlayerTravel) {
      if (this.isFlag2()) {
         rotation5 = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
         MinecraftHolder.getLocalPlayer().setYRot(rotation2 != null ? rotation2.getAmount() : 0.0F);
         MinecraftHolder.getLocalPlayer().setXRot(rotation2 != null ? rotation2.getAmount2() : 0.0F);
      }
   }

   @Flow
   private final void onPostPlayerTravel(EventPostPlayerTravel eventPostPlayerTravel) {
      if (this.isFlag2() && rotation5 != null) {
         MinecraftHolder.getLocalPlayer().setYRot(rotation5.getAmount());
         MinecraftHolder.getLocalPlayer().setXRot(rotation5.getAmount2());
      }
   }

   @Flow
   private final void onFireWorkVector(FireWorkVectorEvent fireWorkVectorEvent) {
      if ((MoveFix.INSTANCE.getEnabled() || ElytraTarget.INSTANCE.getEnabled() || MaceElytraTarget.INSTANCE.getEnabled())
         && (MinecraftHolder.getLocalPlayer().isHandsBusy() || MinecraftHolder.getLocalPlayer().isFallFlying() || MinecraftHolder.getLocalPlayer().isSwimming())
         && !FreeCam.INSTANCE.getEnabled()) {
         fireWorkVectorEvent.cancel();
         fireWorkVectorEvent.setVector(WorldUtil.INSTANCE.computeVec3(WorldUtil.INSTANCE.getAmount5(), WorldUtil.INSTANCE.getAmount4()));
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      Rotation rotation32 = rotation3;
      Rotation rotation42 = rotation4;
      if (rotation32 != null && rotation42 != null && Aura.INSTANCE.getEnabled() && AdvancedRotation.INSTANCE.getClientLook()) {
         MinecraftHolder.getLocalPlayer().setYRot(Render2D.INSTANCE.computeAmount(rotation42.getAmount(), rotation32.getAmount(), Render3D.INSTANCE.getAmount4()));
         MinecraftHolder.getLocalPlayer().setXRot(Render2D.INSTANCE.computeAmount(rotation42.getAmount2(), rotation32.getAmount2(), Render3D.INSTANCE.getAmount4()));
      }
   }

   public final float getAmount() {
      return rotation2 != null ? rotation2.getAmount() : 0.0F;
   }

   public final void applyGetX(float amount, float amount2) {
      LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
      localPlayer.connection.send((Packet)(new PosRot(localPlayer.getX(), localPlayer.getY(), localPlayer.getZ(), amount, amount2, localPlayer.onGround(), localPlayer.horizontalCollision)));
   }

   @NotNull
   public final Rotation computeRotation(Vec3 to) {
      double wrapDegrees = Mth.wrapDegrees(
         Math.toDegrees(Math.atan2(to.z - MinecraftHolder.getLocalPlayer().getZ(), to.x - MinecraftHolder.getLocalPlayer().getX()))
            - 90
            - WorldUtil.INSTANCE.getAmount4()
      );
      double amount5 = -Math.toDegrees(
            Math.atan2(
               to.y
                  - (
                     AntiKnockBack.INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y
                        + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose())
                  ),
               Math.hypot(to.x - MinecraftHolder.getLocalPlayer().getX(), to.z - MinecraftHolder.getLocalPlayer().getZ())
            )
         )
         - WorldUtil.INSTANCE.getAmount5();
      double amount4 = WorldUtil.INSTANCE.getAmount4() + wrapDegrees;
      double amount52 = WorldUtil.INSTANCE.getAmount5() + amount5;
      return new Rotation((float)amount4, (float)amount52, false, null, 12, null);
   }

   private final Vec3 computeVec3(float amount, Vec3 vec3, float amount2) {
      double lengthSqr = vec3.lengthSqr();
      if (lengthSqr < 1.0E-7) {
         return Vec3.ZERO;
      } else {
         Vec3 scale = (lengthSqr > 1.0 ? vec3.normalize() : vec3).scale(amount2);
         float amount4 = (float)Math.sin(amount * (float) (Math.PI / 180.0));
         float amount5 = (float)Math.cos(amount * (float) (Math.PI / 180.0));
         return new Vec3(scale.x * amount5 - scale.z * amount4, scale.y, scale.z * amount5 + scale.x * amount4);
      }
   }
}