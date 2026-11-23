// deobf by pusia
package su.catlean.utils.world;

import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.StopUsingItemEvent;
import su.catlean.client.features.modules.basic.Avoid;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.client.features.modules.rage.AntiWeb;
import su.catlean.client.features.modules.rage.Blink;
import su.catlean.client.features.modules.rage.Phase;
import su.catlean.client.services.InteractionManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.KeyMappingAccessor;
import su.catlean.mixins.accessors.LocalPlayerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;

public final class WorldUtil {
   @NotNull
   public static final WorldUtil INSTANCE = new WorldUtil();
   private static int value;
   private static float amount;
   private static float amount2;
   private static boolean flag;
   private static boolean flag2;
   private static long time = -1L;
   private static long time2 = -1L;
   private static long time3 = -1L;
   private static double amount3;
   private static boolean flag3;

   private WorldUtil() {
   }

   public final int getValue() {
      return value;
   }

   public final void setValue(int value) {
      WorldUtil.value = value;
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      WorldUtil.amount = amount;
   }

   public final float getAmount2() {
      return amount2;
   }

   public final void setAmount2(float amount) {
      amount2 = amount;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      WorldUtil.flag = flag;
   }

   public final boolean isFlag2() {
      return flag2;
   }

   public final void setFlag2(boolean flag) {
      flag2 = flag;
   }

   public final long getTime() {
      return time;
   }

   public final void setTime(long time) {
      WorldUtil.time = time;
   }

   public final long getTime2() {
      return time2;
   }

   public final void setTime2(long time) {
      time2 = time;
   }

   public final long getTime3() {
      return time3;
   }

   public final void setTime3(long time) {
      time3 = time;
   }

   public final double getAmount3() {
      return amount3;
   }

   public final void setAmount3(double amount) {
      amount3 = amount;
   }

   public final boolean isFlag3() {
      return flag3;
   }

   public final void setFlag3(boolean flag) {
      flag3 = flag;
   }

   public final float getAmount4() {
      return ((LocalPlayerAccessor)MinecraftHolder.getLocalPlayer()).getLastYaw();
   }

   public final float getAmount5() {
      return ((LocalPlayerAccessor)MinecraftHolder.getLocalPlayer()).getLastPitch();
   }

   @NotNull
   public final Vec3 getVec3() {
      return Blink.INSTANCE.getEnabled() && Blink.INSTANCE.getVec32() != null
         ? Blink.INSTANCE.getVec32()
         : EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundPlayerCommandPacket) {
         if (((ServerboundPlayerCommandPacket)sendPacket.getPacket()).getAction() == Action.START_SPRINTING) {
            flag = true;
         }

         if (((ServerboundPlayerCommandPacket)sendPacket.getPacket()).getAction() == Action.STOP_SPRINTING) {
            flag = false;
         }
      }

      if (sendPacket.getPacket() instanceof ServerboundContainerClickPacket) {
         flag2 = true;
      }

      if (sendPacket.getPacket() instanceof ServerboundContainerClosePacket) {
         flag2 = false;
      }

      if (sendPacket.getPacket() instanceof ServerboundUseItemPacket
         && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getUseItem().getItem(), Items.CHORUS_FRUIT)) {
         time3 = MinecraftHolder.getTime();
      }
   }

   @Flow
   private final void onStopUsingItem(StopUsingItemEvent stopUsingItemEvent) {
      if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getUseItem().getItem(), Items.CHORUS_FRUIT)) {
         time3 = MinecraftHolder.getTime();
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundPlayerPositionPacket
         && ((ClientboundPlayerPositionPacket)receivePacket.getPacket())
               .change()
               .position()
               .distanceTo(EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer()))
            > 3.0) {
         time2 = MinecraftHolder.getTime();
      }

      if (receivePacket.getPacket() instanceof ClientboundPlayerPositionPacket
         && MinecraftHolder.getTime() - time3 > 10000L
         && MinecraftHolder.getLocalPlayer().tickCount > 20) {
         time = MinecraftHolder.getTime();
      }

      if (receivePacket.getPacket() instanceof ClientboundSoundPacket
         && Intrinsics.areEqual(((ClientboundSoundPacket)receivePacket.getPacket()).getSound().value(), SoundEvents.ENDER_PEARL_THROW)) {
         time3 = MinecraftHolder.getTime();
      }
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      if (MinecraftHolder.getMinecraft().player != null) {
         GofraState.INSTANCE
            .setModifyCollisions(Avoid.INSTANCE.getEnabled() || AntiWeb.INSTANCE.getEnabled() || Phase.INSTANCE.getEnabled() || SpeedMine.INSTANCE.isFlag());
         double value2 = MinecraftHolder.getLocalPlayer().getX() - MinecraftHolder.getLocalPlayer().xo;
         double value3 = MinecraftHolder.getLocalPlayer().getY() - MinecraftHolder.getLocalPlayer().yo;
         double value4 = MinecraftHolder.getLocalPlayer().getZ() - MinecraftHolder.getLocalPlayer().zo;
         amount = (float)Math.hypot(value2, value4);
         amount2 = (float)Math.sqrt(value2 * value2 + value3 * value3 + value4 * value4);
         if (MinecraftHolder.getLocalPlayer().isFallFlying()
            && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getItemBySlot(EquipmentSlot.CHEST).getItem(), Items.ELYTRA)) {
            int value5 = value++;
         } else {
            value = 0;
         }

         if (MinecraftHolder.getMinecraft().gui.screen() == null) {
            flag2 = false;
         }
      }
   }

   @Flow
   private final void onMove(MoveEvent moveEvent) {
      if (!MinecraftHolder.getLocalPlayer().onGround() && !MinecraftHolder.getLocalPlayer().isInLiquid()) {
         if (moveEvent.getY() < 0.0) {
            amount3 = amount3 - moveEvent.getY();
         }
      } else {
         amount3 = 0.0;
      }

      flag3 = amount3 > 0.0;
   }

   public final long getTime4() {
      return System.currentTimeMillis() - time;
   }

   public final boolean isFlag4() {
      return MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().level != null
         && (MinecraftHolder.getLocalPlayer().input.getMoveVector().y != 0.0F || MinecraftHolder.getLocalPlayer().input.getMoveVector().x != 0.0F);
   }

   @NotNull
   public final double[] computeAmounts(float amount, boolean flag, float amount2, float amount3, float amount4) {
      float amount22 = amount2;
      float amount32 = amount3;
      if (flag) {
         amount22 = MinecraftHolder.getMinecraft().options.keyUp.isDown() ? 1.0F : (MinecraftHolder.getMinecraft().options.keyDown.isDown() ? -1.0F : 0.0F);
         amount32 = MinecraftHolder.getMinecraft().options.keyLeft.isDown() ? 1.0F : (MinecraftHolder.getMinecraft().options.keyRight.isDown() ? -1.0F : 0.0F);
      }

      float amount42 = amount4;
      if (amount22 != 0.0F) {
         if (amount32 != 0.0F) {
            amount42 = amount4 + (amount32 > 0.0F ? (amount22 > 0.0F ? -45.0F : 45.0F) : (amount22 > 0.0F ? 45.0F : -45.0F));
            amount32 = 0.0F;
         }

         amount22 = amount22 > 0.0F ? 1.0F : -1.0F;
      }

      double radians = Math.sin(Math.toRadians(amount42 + 90.0F));
      double radians2 = Math.cos(Math.toRadians(amount42 + 90.0F));
      double amount5 = amount22 * amount * radians2 + amount32 * amount * radians;
      double amount6 = amount22 * amount * radians - amount32 * amount * radians2;
      return new double[]{amount5, amount6};
   }

   public static double[] defaultImpl(WorldUtil worldUtil, float amount, boolean flag, float amount2, float amount3, float amount4, int value, Object object) {
      if ((value & 2) != 0) {
         flag = false;
      }

      if ((value & 4) != 0) {
         amount2 = MinecraftHolder.getLocalPlayer().input.getMoveVector().y;
      }

      if ((value & 8) != 0) {
         amount3 = MinecraftHolder.getLocalPlayer().input.getMoveVector().x;
      }

      if ((value & 16) != 0) {
         amount4 = MinecraftHolder.getLocalPlayer().getYRot();
      }

      return worldUtil.computeAmounts(amount, flag, amount2, amount3, amount4);
   }

   public final void onMove(MoveEvent event, double amount) {
      double value2 = MinecraftHolder.getMinecraft().player.input.getMoveVector().y;
      double value3 = MinecraftHolder.getMinecraft().player.input.getMoveVector().x;
      float yRot = MinecraftHolder.getMinecraft().player.getYRot();
      if (value2 == 0.0 && value3 == 0.0) {
         event.setX(0.0);
         event.setZ(0.0);
      } else {
         if (value2 != 0.0) {
            if (value3 > 0.0) {
               yRot += value2 > 0.0 ? -45 : 45;
            } else if (value3 < 0.0) {
               yRot += value2 > 0.0 ? 45 : -45;
            }

            value3 = 0.0;
            if (value2 > 0.0) {
               value2 = 1.0;
            } else if (value2 < 0.0) {
               value2 = -1.0;
            }
         }

         double radians = Math.sin(Math.toRadians(yRot + 90.0F));
         double radians2 = Math.cos(Math.toRadians(yRot + 90.0F));
         event.setX(value2 * amount * radians2 + value3 * amount * radians);
         event.setZ(value2 * amount * radians - value3 * amount * radians2);
      }
   }

   public final float getAmount6() {
      double value2 = MinecraftHolder.getMinecraft().options.keyUp.isDown() ? 1.0 : (MinecraftHolder.getMinecraft().options.keyDown.isDown() ? -1.0 : 0.0);
      double value3 = MinecraftHolder.getMinecraft().options.keyLeft.isDown() ? 1.0 : (MinecraftHolder.getMinecraft().options.keyRight.isDown() ? -1.0 : 0.0);
      if (value3 > 0.0) {
         value3 = 1.0;
      } else if (value3 < 0.0) {
         value3 = -1.0;
      }

      float yRot = MinecraftHolder.getMinecraft().player.getYRot();
      if (value2 == 0.0 && value3 == 0.0) {
         return yRot;
      } else {
         if (value2 != 0.0) {
            if (value3 > 0.0) {
               yRot += value2 > 0.0 ? -45.0F : -135.0F;
            } else if (value3 < 0.0) {
               yRot += value2 > 0.0 ? 45.0F : 135.0F;
            } else if (value2 < 0.0) {
               yRot += 180.0F;
            }
         }

         if (value2 == 0.0) {
            if (value3 > 0.0) {
               yRot -= 90.0F;
            } else {
               yRot += 90.0F;
            }
         }

         return yRot;
      }
   }

   @NotNull
   public final double[] computeAmounts(double amount) {
      float yRot = MinecraftHolder.getMinecraft().player.getYRot();
      double amount4 = amount * Math.cos(Math.toRadians(yRot + 90.0F));
      double amount5 = amount * Math.sin(Math.toRadians(yRot + 90.0F));
      return new double[]{amount4, amount5};
   }

   @Nullable
   public final Entity computeEntity(float amount, float amount2, float amount3, boolean flag, boolean flag2) {
      HitResult hitResult = flag ? null : this.computeHitResult(amount3, amount, amount2);
      Vec3 vec3 = EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer())
         .add(0.0, MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose()), 0.0);
      double amount32 = (float)Math.pow(amount3, 2);
      if (hitResult != null) {
         amount32 = hitResult.getLocation().distanceToSqr(vec3);
      }

      Vec3 vec32 = this.computeVec3(amount2, amount);
      Vec3 amount33 = vec3.add(vec32.x * amount3, vec32.y * amount3, vec32.z * amount3);
      AABB inflate = MinecraftHolder.getLocalPlayer().getBoundingBox().expandTowards(vec32.scale(amount3)).inflate(1.0, 1.0, 1.0);
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
         (Entity)MinecraftHolder.getLocalPlayer(),
         vec3,
         amount33,
         inflate,
         arg -> !arg.isSpectator() && arg.isPickable() && (arg instanceof LivingEntity || !flag2),
         amount32
      );
      return entityHitResult == null || !(vec3.distanceToSqr(entityHitResult.getLocation()) < amount32) && hitResult != null ? null : entityHitResult.getEntity();
   }

   public static Entity defaultImpl(WorldUtil worldUtil, float amount, float amount2, float amount3, boolean flag, boolean flag2, int value, Object object) {
      if ((value & 16) != 0) {
         flag2 = true;
      }

      return worldUtil.computeEntity(amount, amount2, amount3, flag, flag2);
   }

   @NotNull
   public final HitResult computeHitResult(float amount, float amount2, double amount3, double amount4, double amount5) {
      HitResult hitResult = this.computeHitResult(5.0F, amount, amount2, (float)amount3, (float)amount4, (float)amount5);
      Vec3 vec3 = new Vec3(amount3, amount4, amount5).add(0.0, MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose()), 0.0);
      double distanceToSqr = 25.0;
      if (hitResult.getType() != Type.MISS) {
         distanceToSqr = hitResult.getLocation().distanceToSqr(vec3);
      }

      Vec3 vec32 = this.computeVec3(amount2, amount);
      Vec3 vec33 = vec3.add(vec32.x * 5, vec32.y * 5, vec32.z * 5);
      AABB inflate = new AABB(amount3 - 0.3, amount4, amount5 - 0.3, amount3 + 0.3, amount4 + 1.8, amount5 + 0.3)
         .expandTowards(vec32.scale(5.0))
         .inflate(1.0, 1.0, 1.0);
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
         (Entity)MinecraftHolder.getLocalPlayer(), vec3, vec33, inflate, arg -> !arg.isSpectator() && arg.isPickable(), distanceToSqr
      );
      if (entityHitResult != null) {
         Entity entity = entityHitResult.getEntity();
         Vec3 location = entityHitResult.getLocation();
         double distanceToSqr2 = vec3.distanceToSqr(location);
         if ((distanceToSqr2 < distanceToSqr || hitResult.getType() == Type.MISS) && entity instanceof LivingEntity) {
            return (HitResult)entityHitResult;
         }
      }

      return hitResult;
   }

   @NotNull
   public final Vec3 computeVec3(float amount, float amount2) {
      return new Vec3(
         (float)Math.sin(-amount2 * (float) (Math.PI / 180.0)) * (float)Math.cos(amount * (float) (Math.PI / 180.0)),
         -((float)Math.sin(amount * (float) (Math.PI / 180.0))),
         (float)Math.cos(-amount2 * (float) (Math.PI / 180.0)) * (float)Math.cos(amount * (float) (Math.PI / 180.0))
      );
   }

   @NotNull
   public final HitResult computeHitResult(float amount, float amount2, float amount3) {
      Vec3 eyePosition = MinecraftHolder.getLocalPlayer().getEyePosition(1.0F);
      Vec3 vec3 = this.computeVec3(amount3, amount2);
      Vec3 amount4 = eyePosition.add(vec3.x * amount, vec3.y * amount, vec3.z * amount);
      return (HitResult)MinecraftHolder.getClientLevel()
         .clip(new ClipContext(eyePosition, amount4, Block.OUTLINE, Fluid.NONE, (Entity)MinecraftHolder.getLocalPlayer()));
   }

   private final HitResult computeHitResult(float amount, float amount2, float amount3, float amount4, float amount5, float amount6) {
      Vec3 vec3 = new Vec3(amount4, amount5, amount6);
      Vec3 vec32 = this.computeVec3(amount3, amount2);
      Vec3 amount7 = vec3.add(vec32.x * amount, vec32.y * amount, vec32.z * amount);
      return (HitResult)MinecraftHolder.getClientLevel()
         .clip(new ClipContext(vec3, amount7, Block.OUTLINE, Fluid.NONE, (Entity)MinecraftHolder.getLocalPlayer()));
   }

   public final double getAmount7() {
      int amplifier = 0;
      double value2 = 0.2873;
      if (MinecraftHolder.getMinecraft().player == null) {
         return 0.0;
      } else {
         if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.SPEED)) {
            amplifier = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.SPEED).getAmplifier();
            value2 *= 1.0 + 0.2 * (amplifier + 1);
         }

         if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.JUMP_BOOST)) {
            amplifier = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.JUMP_BOOST).getAmplifier();
            value2 /= 1.0 + 0.2 * (amplifier + 1);
         }

         if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.SLOWNESS)) {
            amplifier = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.SLOWNESS).getAmplifier();
            value2 /= 1.0 + 0.2 * (amplifier + 1);
         }

         return value2;
      }
   }

   public final double getAmount8() {
      double value2 = 0.39999995F;
      if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.JUMP_BOOST)) {
         double amplifier = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.JUMP_BOOST).getAmplifier();
         value2 += (amplifier + 1.0) * 0.1;
      }

      return value2;
   }

   public final float fetchMaxDamage(ItemStack stack) {
      return (float)(stack.getMaxDamage() - stack.getDamageValue()) / stack.getMaxDamage() * 100.0F;
   }

   public final float computeAmount(Vec3 targetPos) {
      return (float)Math.sqrt(this.computeAmount2(targetPos));
   }

   public final float computeAmount2(Vec3 targetPos) {
      if (MinecraftHolder.getMinecraft().player == null) {
         return 0.0F;
      } else {
         Vec3 eyePosition = InteractionManager.INSTANCE.fetchEyePosition((Entity)MinecraftHolder.getLocalPlayer());
         double value2 = targetPos.x - eyePosition.x;
         double value3 = targetPos.y - eyePosition.y;
         double value4 = targetPos.z - eyePosition.z;
         return (float)(value2 * value2 + value3 * value3 + value4 * value4);
      }
   }

   public final boolean checkMainCamera(Vec3 pos, int value) {
      double value2 = pos.x() - MinecraftHolder.getMinecraft().gameRenderer.mainCamera().position().x;
      double value3 = pos.z() - MinecraftHolder.getMinecraft().gameRenderer.mainCamera().position().z;
      float wrapDegrees = Mth.wrapDegrees(
         (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(value3, value2)) - 90.0)
            - Mth.wrapDegrees(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().yRot())
      );
      return Math.abs((double)wrapDegrees) <= value;
   }

   @Nullable
   public final Vec3 computeVec3(float amount, float amount2, float amount3) {
      Vec3 vec3 = EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getMinecraft().player)
         .add(0.0, MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose()), 0.0);
      float amount32 = (float)Math.pow(amount3, 2.0F);
      Vec3 vec32 = this.computeVec3(amount2, amount);
      Vec3 amount33 = vec3.add(vec32.x * amount3, vec32.y * amount3, vec32.z * amount3);
      AABB inflate = MinecraftHolder.getLocalPlayer().getBoundingBox().expandTowards(vec32.scale(amount3)).inflate(1.0, 1.0, 1.0);
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
         (Entity)MinecraftHolder.getLocalPlayer(), vec3, amount33, inflate, arg -> !arg.isSpectator(), amount32
      );
      return entityHitResult != null ? entityHitResult.getLocation() : null;
   }

   public final float fetchX(double amount, double amount2) {
      if (MinecraftHolder.getMinecraft().player == null) {
         return 0.0F;
      } else {
         double amount4 = MinecraftHolder.getLocalPlayer().getX() - amount;
         double amount22 = MinecraftHolder.getLocalPlayer().getZ() - amount2;
         return (float)(amount4 * amount4 + amount22 * amount22);
      }
   }

   public final float computeAmount(double amount, double amount2) {
      return (float)Math.sqrt(this.fetchX(amount, amount2));
   }

   @NotNull
   public final HitResult computeHitResult(double amount, double amount2, float amount3, float amount4) {
      double amount5 = Math.max(amount, amount2);
      double square = Mth.square(amount5);
      Vec3 eyePosition = MinecraftHolder.getLocalPlayer().getEyePosition();
      HitResult eyePosition2 = this.fetchEyePosition(amount, amount3, amount4);
      double distanceToSqr = eyePosition2.getLocation().distanceToSqr(eyePosition);
      if (eyePosition2.getType() != Type.MISS) {
         square = distanceToSqr;
         amount5 = Math.sqrt(distanceToSqr);
      }

      Vec3 vec3 = this.computeVec3(amount4, amount3);
      Vec3 vec32 = eyePosition.add(vec3.x * amount5, vec3.y * amount5, vec3.z * amount5);
      AABB inflate = MinecraftHolder.getLocalPlayer().getBoundingBox().expandTowards(vec3.scale(amount5)).inflate(1.0, 1.0, 1.0);
      EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(
         (Entity)MinecraftHolder.getLocalPlayer(), eyePosition, vec32, inflate, EntitySelector.CAN_BE_PICKED, square
      );
      return entityHitResult != null && entityHitResult.getLocation().distanceToSqr(eyePosition) < distanceToSqr
         ? this.fetchLocation((HitResult)entityHitResult, eyePosition, amount2)
         : this.fetchLocation(eyePosition2, eyePosition, amount);
   }

   private final HitResult fetchLocation(HitResult hitResult, Vec3 vec3, double amount) {
      Vec3 location = hitResult.getLocation();
      if (!location.closerThan((Position)vec3, amount)) {
         Vec3 location2 = hitResult.getLocation();
         Direction approximateNearest = Direction.getApproximateNearest(location2.x - vec3.x, location2.y - vec3.y, location2.z - vec3.z);
         return (HitResult)BlockHitResult.miss(location2, approximateNearest, BlockPos.containing((Position)location2));
      } else {
         return hitResult;
      }
   }

   @NotNull
   public final HitResult fetchEyePosition(double amount, float amount2, float amount3) {
      Vec3 scale = this.computeVec3(amount3, amount2).scale(amount);
      return (HitResult)MinecraftHolder.getClientLevel()
         .clip(
            new ClipContext(
               MinecraftHolder.getLocalPlayer().getEyePosition(),
               MinecraftHolder.getLocalPlayer().getEyePosition().add(scale),
               Block.OUTLINE,
               Fluid.NONE,
               (Entity)MinecraftHolder.getLocalPlayer()
            )
         );
   }

   public final void applySetDown() {
      MinecraftHolder.getMinecraft().options.keyUp.setDown(false);
      MinecraftHolder.getMinecraft().options.keyDown.setDown(false);
      MinecraftHolder.getMinecraft().options.keyLeft.setDown(false);
      MinecraftHolder.getMinecraft().options.keyRight.setDown(false);
      MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
      MinecraftHolder.getMinecraft().options.keyShift.setDown(false);
   }

   public final void applyGetKey() {
      MinecraftHolder.getMinecraft()
         .options
         .keyUp
         .setDown(InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyUp).getKey().getValue()));
      MinecraftHolder.getMinecraft()
         .options
         .keyDown
         .setDown(InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyDown).getKey().getValue()));
      MinecraftHolder.getMinecraft()
         .options
         .keyLeft
         .setDown(InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyLeft).getKey().getValue()));
      MinecraftHolder.getMinecraft()
         .options
         .keyRight
         .setDown(InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyRight).getKey().getValue()));
      MinecraftHolder.getMinecraft()
         .options
         .keyJump
         .setDown(InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyJump).getKey().getValue()));
      MinecraftHolder.getMinecraft()
         .options
         .keyShift
         .setDown(InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyShift).getKey().getValue()));
   }
}