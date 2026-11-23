// deobf by pusia
package su.catlean.client.features.modules.rage;

import java.util.ArrayList;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.game.ClientboundMoveEntityPacket;
import net.minecraft.network.protocol.game.ClientboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Rot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.EventPlayerTravel;
import su.catlean.api.event.events.player.ShouldDismountEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class BoatFly extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(BoatFly.class, "mode", "getMode()Lsu/catlean/z4;", 0), new PropertyReference1Impl(BoatFly.class, "phase", "getPhase()Z", 0), new PropertyReference1Impl(BoatFly.class, "gravity", "getGravity()Z", 0), new PropertyReference1Impl(BoatFly.class, "automount", "getAutomount()Z", 0), new PropertyReference1Impl(BoatFly.class, "allowShift", "getAllowShift()Z", 0), new PropertyReference1Impl(BoatFly.class, "speed", "getSpeed()F", 0), new PropertyReference1Impl(BoatFly.class, "yspeed", "getYspeed()F", 0), new PropertyReference1Impl(BoatFly.class, "advanced", "getAdvanced()Lsu/catlean/nl;", 0), new PropertyReference1Impl(BoatFly.class, "glidespeed", "getGlidespeed()F", 0), new PropertyReference1Impl(BoatFly.class, "slotClick", "getSlotClick()Z", 0), new PropertyReference1Impl(BoatFly.class, "limit", "getLimit()Z", 0), new PropertyReference1Impl(BoatFly.class, "ongroundpacket", "getOngroundpacket()Z", 0), new PropertyReference1Impl(BoatFly.class, "spoofpackets", "getSpoofpackets()Z", 0), new PropertyReference1Impl(BoatFly.class, "jitter", "getJitter()F", 0), new PropertyReference1Impl(BoatFly.class, "cancelrotations", "getCancelrotations()Z", 0), new PropertyReference1Impl(BoatFly.class, "cancel", "getCancel()Z", 0), new PropertyReference1Impl(BoatFly.class, "pause", "getPause()Z", 0), new PropertyReference1Impl(BoatFly.class, "enableticks", "getEnableticks()I", 0), new PropertyReference1Impl(BoatFly.class, "waitTicks", "getWaitTicks()I", 0), new PropertyReference1Impl(BoatFly.class, "timer", "getTimer()F", 0) };
   @NotNull
   public static final BoatFly INSTANCE = new BoatFly();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", BoatFly.Mode.Packet, null, null, 12, null);
   @NotNull
   private static final BooleanSetting phase = SettingFactory.defaultImpl(INSTANCE, "phase", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting gravity = SettingFactory.defaultImpl(INSTANCE, "gravity", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting autoMount = SettingFactory.defaultImpl(INSTANCE, "auto-mount", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting allowShift = SettingFactory.defaultImpl(INSTANCE, "allow-shift", true, null, null, 12, null);
   @NotNull
   private static final FloatSetting speed = SettingFactory.defaultImpl(INSTANCE, "speed", 2.0F, RangesKt.rangeTo(0.0F, 25.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting ySpeed = SettingFactory.defaultImpl(INSTANCE, "y-speed", 1.0F, RangesKt.rangeTo(0.0F, 10.0F), null, null, 24, null);
   @NotNull
   private static final GroupSetting advanced = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "advanced", null, null, 6, null);
   @NotNull
   private static final FloatSetting glideSpeed = SettingFactory.defaultImpl(
      INSTANCE, "glide-speed", 0.0F, RangesKt.rangeTo(0.0F, 10.0F), INSTANCE.getAdvanced(), null, 16, null
   );
   @NotNull
   private static final BooleanSetting clickSlot = SettingFactory.defaultImpl(INSTANCE, "click-slot", false, INSTANCE.getAdvanced(), null, 8, null);
   @NotNull
   private static final BooleanSetting limit = SettingFactory.defaultImpl(INSTANCE, "limit", true, INSTANCE.getAdvanced(), null, 8, null);
   @NotNull
   private static final BooleanSetting onGroundPacket = SettingFactory.defaultImpl(INSTANCE, "on-ground-packet", false, INSTANCE.getAdvanced(), null, 8, null);
   @NotNull
   private static final BooleanSetting spoofPackets = SettingFactory.defaultImpl(INSTANCE, "spoof-packets", false, INSTANCE.getAdvanced(), null, 8, null);
   @NotNull
   private static final FloatSetting jitter = SettingFactory.computeFloatSetting(
      INSTANCE, "jitter", 0.1F, RangesKt.rangeTo(0.0F, 10.0F), INSTANCE.getAdvanced(), () -> INSTANCE.getSpoofpackets()
   );
   @NotNull
   private static final BooleanSetting cancelRotations = SettingFactory.defaultImpl(INSTANCE, "cancel-rotations", true, INSTANCE.getAdvanced(), null, 8, null);
   @NotNull
   private static final BooleanSetting cancel = SettingFactory.defaultImpl(INSTANCE, "cancel", true, INSTANCE.getAdvanced(), null, 8, null);
   @NotNull
   private static final BooleanSetting pause = SettingFactory.defaultImpl(INSTANCE, "pause", false, INSTANCE.getAdvanced(), null, 8, null);
   @NotNull
   private static final IntSetting enableTicks = SettingFactory.computeIntSetting(
      INSTANCE, "enable-ticks", 10, new IntRange(1, 100), INSTANCE.getAdvanced(), () -> INSTANCE.getPause()
   );
   @NotNull
   private static final IntSetting waitTicks = SettingFactory.computeIntSetting(
      INSTANCE, "wait-ticks", 10, new IntRange(1, 100), INSTANCE.getAdvanced(), () -> INSTANCE.getPause()
   );
   @NotNull
   private static final FloatSetting timer = SettingFactory.defaultImpl(
      INSTANCE, "timer", 1.0F, RangesKt.rangeTo(0.1F, 5.0F), INSTANCE.getAdvanced(), null, 16, null
   );
   @NotNull
   private static final ArrayList<ServerboundMoveVehiclePacket> items = new ArrayList();
   private static int value;
   private static int value2;
   private static boolean flag;
   private static boolean flag2;
   private static boolean flag3;

   private BoatFly() {
      super("boat-fly", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final BoatFly.Mode getMode() {
      return (BoatFly.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getPhase() {
      return (Boolean)phase.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getGravity() {
      return (Boolean)gravity.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getAutomount() {
      return (Boolean)autoMount.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getAllowShift() {
      return (Boolean)allowShift.computeObject(this, $$delegatedProperties[4]);
   }

   private final float getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[5])).floatValue();
   }

   private final float getYspeed() {
      return ((Number)ySpeed.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   private final GroupValue getAdvanced() {
      return (GroupValue)advanced.computeObject(this, $$delegatedProperties[7]);
   }

   private final float getGlidespeed() {
      return ((Number)glideSpeed.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   private final boolean getSlotClick() {
      return (Boolean)clickSlot.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getLimit() {
      return (Boolean)limit.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getOngroundpacket() {
      return (Boolean)onGroundPacket.computeObject(this, $$delegatedProperties[11]);
   }

   private final boolean getSpoofpackets() {
      return (Boolean)spoofPackets.computeObject(this, $$delegatedProperties[12]);
   }

   private final float getJitter() {
      return ((Number)jitter.computeObject(this, $$delegatedProperties[13])).floatValue();
   }

   private final boolean getCancelrotations() {
      return (Boolean)cancelRotations.computeObject(this, $$delegatedProperties[14]);
   }

   private final boolean getCancel() {
      return (Boolean)cancel.computeObject(this, $$delegatedProperties[15]);
   }

   private final boolean getPause() {
      return (Boolean)pause.computeObject(this, $$delegatedProperties[16]);
   }

   private final int getEnableticks() {
      return ((Number)enableTicks.computeObject(this, $$delegatedProperties[17])).intValue();
   }

   private final int getWaitTicks() {
      return ((Number)waitTicks.computeObject(this, $$delegatedProperties[18])).intValue();
   }

   private final float getTimer() {
      return ((Number)timer.computeObject(this, $$delegatedProperties[19])).floatValue();
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         if (this.getAutomount()) {
            this.applyDistanceToSqr();
         }
      } else {
         this.applyUnplug();
      }
   }

   @Override
   public void update4() {
      items.clear();
      flag = false;
      if (MinecraftHolder.getMinecraft().player != null) {
         if (this.getPhase() && this.getMode() == BoatFly.Mode.Motion) {
            Entity controlledVehicle = MinecraftHolder.getLocalPlayer().getControlledVehicle();
            if (controlledVehicle != null) {
               Entity entity = controlledVehicle;
               entity.noPhysics = false;
            }

            MinecraftHolder.getLocalPlayer().noPhysics = false;
         }

         Entity controlledVehicle2 = MinecraftHolder.getLocalPlayer().getControlledVehicle();
         if (controlledVehicle2 != null) {
            controlledVehicle2.setNoGravity(false);
         }

         MinecraftHolder.getLocalPlayer().setNoGravity(false);
      }
   }

   private final float getAmount() {
      flag3 = !flag3;
      return flag3 ? this.getJitter() : -this.getJitter();
   }

   private final void updateServerboundMoveVehiclePacket(ServerboundMoveVehiclePacket serverboundMoveVehiclePacket) {
      items.add(serverboundMoveVehiclePacket);
      PacketUtil.applySend((Packet)serverboundMoveVehiclePacket);
   }

   private final void applyGetBlockState(Entity entity) {
      BlockPos containing = null;
      containing = BlockPos.containing((Position)this.computeVec3(entity));
      short shortValue = 255;

      for (int i = 0; i < shortValue; i++) {
         if (!MinecraftHolder.getClientLevel().getBlockState(containing).canBeReplaced()
            || MinecraftHolder.getClientLevel().getBlockState(containing).getBlock() == Blocks.WATER) {
            entity.setPos(entity.getX(), containing.getY() + 1, entity.getZ());
            INSTANCE.updateServerboundMoveVehiclePacket(ServerboundMoveVehiclePacket.fromEntity(entity));
            entity.setPos(entity.getX(), entity.getY(), entity.getZ());
            return;
         }

         containing = containing.below();
      }
   }

   private final void applyDistanceToSqr() {
      for (Entity entitiesForRenderingItem : MinecraftHolder.getClientLevel().entitiesForRendering()) {
         if (entitiesForRenderingItem instanceof Boat && !(MinecraftHolder.getLocalPlayer().distanceToSqr(entitiesForRenderingItem) > 25.0)) {
            EntityHitResult entityHitResult = new EntityHitResult(entitiesForRenderingItem, ((Boat)entitiesForRenderingItem).getBoundingBox().getCenter());
            MinecraftHolder.getMinecraft().gameMode.interact((Player)MinecraftHolder.getLocalPlayer(), entitiesForRenderingItem, entityHitResult, InteractionHand.MAIN_HAND);
            break;
         }
      }
   }

   @Flow
   private final void onShouldDismount(ShouldDismountEvent shouldDismountEvent) {
      if (this.getAllowShift()) {
         shouldDismountEvent.cancel();
      }
   }

   @Flow
   private final void onPlayerTravel(EventPlayerTravel eventPlayerTravel) {
      if (this.isFlag6()) {
         if (MinecraftHolder.getLocalPlayer().getControlledVehicle() == null) {
            if (this.getAutomount()) {
               this.applyDistanceToSqr();
            }
         } else {
            if (this.getPhase() && this.getMode() == BoatFly.Mode.Motion) {
               Entity controlledVehicle = MinecraftHolder.getLocalPlayer().getControlledVehicle();
               if (controlledVehicle != null) {
                  Entity entity = controlledVehicle;
                  entity.noPhysics = true;
                  entity.setNoGravity(true);
               }

               MinecraftHolder.getLocalPlayer().noPhysics = true;
            }

            if (!flag2) {
               Entity controlledVehicle2 = MinecraftHolder.getLocalPlayer().getControlledVehicle();
               if (controlledVehicle2 != null) {
                  controlledVehicle2.setNoGravity(!this.getGravity());
               }

               MinecraftHolder.getLocalPlayer().setNoGravity(!this.getGravity());
            }

            if (this.getPause()) {
               if (value > this.getEnableticks() && !flag) {
                  value = 0;
                  flag = true;
                  value2 = this.getWaitTicks();
               }

               if (value2 > 0 && flag) {
                  value2 += -1;
                  return;
               }

               if (value2 <= 0) {
                  flag = false;
               }
            }

            Entity controlledVehicle3 = MinecraftHolder.getLocalPlayer().getControlledVehicle();
            if (controlledVehicle3 != null) {
               Entity entity2 = controlledVehicle3;
               if (this.getTimer() != 1.0F) {
                  TickService.INSTANCE.setAmount2(this.getTimer());
               }

               entity2.setYRot(MinecraftHolder.getLocalPlayer().getYRot());
               double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, this.getSpeed(), false, 0.0F, 0.0F, 0.0F, 30, null);
               double value3 = entity2.getX() + defaultImpl[0];
               double value4 = entity2.getZ() + defaultImpl[1];
               double value5 = entity2.getY();
               flag2 = false;
               entity2.setDeltaMovement(entity2.getDeltaMovement().x(), -this.getGlidespeed() / 100.0F, entity2.getDeltaMovement().z());
               if (this.getMode() == BoatFly.Mode.Motion) {
                  entity2.setDeltaMovement(defaultImpl[0], entity2.getDeltaMovement().y(), defaultImpl[1]);
               }

               if (MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
                  if (this.getMode() == BoatFly.Mode.Motion) {
                     entity2.setDeltaMovement(entity2.getDeltaMovement().x(), entity2.getDeltaMovement().y() + this.getYspeed(), entity2.getDeltaMovement().z());
                  } else {
                     value5 += this.getYspeed();
                  }
               } else if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
                  if (this.getMode() == BoatFly.Mode.Motion) {
                     entity2.setDeltaMovement(entity2.getDeltaMovement().x(), entity2.getDeltaMovement().y() - this.getYspeed(), entity2.getDeltaMovement().z());
                  } else {
                     value5 -= this.getYspeed();
                  }
               }

               if (!WorldUtil.INSTANCE.isFlag4()) {
                  entity2.setDeltaMovement(0.0, entity2.getDeltaMovement().y(), 0.0);
               }

               if (this.getOngroundpacket()) {
                  this.applyGetBlockState(entity2);
               }

               if (this.getMode() == BoatFly.Mode.Packet) {
                  entity2.setPos(value3, value5, value4);
                  this.updateServerboundMoveVehiclePacket(ServerboundMoveVehiclePacket.fromEntity(entity2));
               }

               if (this.getSlotClick()) {
                  MinecraftHolder.getMultiPlayerGameMode()
                     .handleContainerInput(
                        MinecraftHolder.getLocalPlayer().containerMenu.containerId, 0, 0, ContainerInput.CLONE, (Player)MinecraftHolder.getLocalPlayer()
                     );
               }

               if (this.getSpoofpackets()) {
                  Vec3 pos = this.computeVec3(entity2).add(0.0, this.getAmount(), 0.0);
                  float yRot = entity2.getYRot();
                  float xRot = entity2.getXRot();
                  Vec3 vec3 = this.computeVec3(entity2);
                  entity2.setYRot(entity2.getYRot());
                  entity2.setXRot(entity2.getXRot());
                  entity2.setPos(pos);
                  this.updateServerboundMoveVehiclePacket(ServerboundMoveVehiclePacket.fromEntity(entity2));
                  entity2.setYRot(yRot);
                  entity2.setXRot(xRot);
                  entity2.setPos(vec3);
               }

               MinecraftHolder.getLocalPlayer().move(MoverType.SELF, MinecraftHolder.getLocalPlayer().getDeltaMovement());
               eventPlayerTravel.cancel();
               value++;
            }
         }
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         if (receivePacket.getPacket() instanceof ClientboundDisconnectPacket) {
            this.applyUnplug();
         }

         if (MinecraftHolder.getLocalPlayer().isHandsBusy() && !flag2 && !flag) {
            if (this.getCancel()) {
               if (receivePacket.getPacket() instanceof ClientboundMoveVehiclePacket) {
                  receivePacket.cancel();
               }

               if (receivePacket.getPacket() instanceof ClientboundPlayerPositionPacket) {
                  receivePacket.cancel();
               }

               if (receivePacket.getPacket() instanceof ClientboundMoveEntityPacket) {
                  receivePacket.cancel();
               }

               if (receivePacket.getPacket() instanceof ClientboundSetEntityLinkPacket) {
                  receivePacket.cancel();
               }
            }
         }
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         if ((sendPacket.getPacket() instanceof Rot && this.getCancelrotations() || sendPacket.getPacket() instanceof ServerboundPlayerInputPacket)
            && MinecraftHolder.getLocalPlayer().isHandsBusy()) {
            sendPacket.cancel();
         }

         if (flag2 && sendPacket.getPacket() instanceof ServerboundMoveVehiclePacket) {
            sendPacket.cancel();
         }

         if (sendPacket.getPacket() instanceof ServerboundPlayerInputPacket && this.getAllowShift()) {
            sendPacket.cancel();
         }

         if (MinecraftHolder.getLocalPlayer().getControlledVehicle() != null && !flag2 && !flag) {
            if (sendPacket.getPacket() instanceof ServerboundMoveVehiclePacket && this.getLimit() && this.getMode() == BoatFly.Mode.Packet) {
               if (items.contains(sendPacket.getPacket())) {
                  items.remove(sendPacket.getPacket());
               } else {
                  sendPacket.cancel();
               }
            }
         }
      }
   }

   static {
   }

   public static enum Mode {
      Packet,
      Motion;

      private static final BoatFly.Mode[] modes = $values();
      private static final EnumEntries<BoatFly.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<BoatFly.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final BoatFly.Mode[] $values() {
         return new BoatFly.Mode[]{Packet, Motion};
      }
   }
}