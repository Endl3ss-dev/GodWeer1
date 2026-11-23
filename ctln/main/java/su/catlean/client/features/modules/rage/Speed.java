// deobf by pusia
package su.catlean.client.features.modules.rage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.equip.ElytraSwap;
import su.catlean.client.features.modules.esp.PlayerSkeletonTracker;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class Speed extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Speed.class, "mode", "getMode()Lsu/catlean/th;", 0), new MutablePropertyReference1Impl(Speed.class, "useTimer", "getUseTimer()Z", 0), new MutablePropertyReference1Impl(Speed.class, "pauseInLiquids", "getPauseInLiquids()Z", 0), new MutablePropertyReference1Impl(Speed.class, "pauseWhileSneaking", "getPauseWhileSneaking()Z", 0), new PropertyReference1Impl(Speed.class, "fireWorkSlot", "getFireWorkSlot()I", 0), new PropertyReference1Impl(Speed.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(Speed.class, "armorStands", "getArmorStands()Z", 0), new MutablePropertyReference1Impl(Speed.class, "distance", "getDistance()F", 0), new MutablePropertyReference1Impl(Speed.class, "speed", "getSpeed()F", 0), new MutablePropertyReference1Impl(Speed.class, "groundSpeed", "getGroundSpeed()F", 0), new MutablePropertyReference1Impl(Speed.class, "boostAngle", "getBoostAngle()Lsu/catlean/gu;", 0), new MutablePropertyReference1Impl(Speed.class, "predictTicks", "getPredictTicks()F", 0), new MutablePropertyReference1Impl(Speed.class, "rollBackTicks", "getRollBackTicks()I", 0) };
   @NotNull
   public static final Speed INSTANCE = new Speed();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", Speed.Mode.NCP, null, null, 12, null);
   @NotNull
   private static final BooleanSetting useTimer = SettingFactory.defaultImpl(
      INSTANCE,
      "use-timer",
      false,
      null,
      () -> INSTANCE.getMode() == Speed.Mode.NCP || INSTANCE.getMode() == Speed.Mode.STRICT_STRAFE || INSTANCE.getMode() == Speed.Mode.MATRIX,
      4,
      null
   );
   @NotNull
   private static final BooleanSetting pauseInLiquids = SettingFactory.defaultImpl(INSTANCE, "pause-in-liquids", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting pauseWhileSneaking = SettingFactory.defaultImpl(INSTANCE, "pause-while-sneaking", false, null, null, 12, null);
   @NotNull
   private static final IntSetting fireSlot = SettingFactory.defaultImpl(
      INSTANCE, "fire-slot", 1, new IntRange(1, 9), null, () -> INSTANCE.getMode() == Speed.Mode.FIREWORK, 8, null
   );
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(
      INSTANCE, "delay", 8, new IntRange(1, 20), null, () -> INSTANCE.getMode() == Speed.Mode.FIREWORK, 8, null
   );
   @NotNull
   private static final BooleanSetting armorStands = SettingFactory.defaultImpl(
      INSTANCE, "armor-stands", false, null, () -> INSTANCE.getMode() == Speed.Mode.GRIM_ENTITY, 4, null
   );
   @NotNull
   private static final FloatSetting distance = SettingFactory.defaultImpl(
      INSTANCE, "distance", 1.0F, RangesKt.rangeTo(0.1F, 2.0F), null, () -> INSTANCE.getMode() == Speed.Mode.GRIM_ENTITY, 8, null
   );
   @NotNull
   private static final FloatSetting speed = SettingFactory.defaultImpl(
      INSTANCE, "speed", 8.0F, RangesKt.rangeTo(0.1F, 10.0F), null, () -> INSTANCE.getMode() == Speed.Mode.GRIM_ENTITY, 8, null
   );
   @NotNull
   private static final FloatSetting groundSpeed = SettingFactory.defaultImpl(
      INSTANCE, "ground-speed", 8.0F, RangesKt.rangeTo(0.1F, 10.0F), null, () -> INSTANCE.getMode() == Speed.Mode.GRIM_ENTITY, 8, null
   );
   @NotNull
   private static final EnumSetting boostAngle = SettingFactory.defaultImpl(
      INSTANCE, "boost-angle", Speed.BoostAngle.SERVER, null, () -> INSTANCE.getMode() == Speed.Mode.GRIM_ENTITY, 4, null
   );
   @NotNull
   private static final FloatSetting predictTicks = SettingFactory.defaultImpl(
      INSTANCE,
      "predict-ticks",
      3.0F,
      RangesKt.rangeTo(0.0F, 10.0F),
      null,
      () -> INSTANCE.getMode() == Speed.Mode.GRIM_ENTITY && INSTANCE.getBoostAngle() == Speed.BoostAngle.PREDICT,
      8,
      null
   );
   @NotNull
   private static final IntSetting rollbackTicks = SettingFactory.defaultImpl(
      INSTANCE, "rollback-ticks", 5, new IntRange(0, 30), null, () -> INSTANCE.getMode() == Speed.Mode.GRIM_ENTITY, 8, null
   );
   private static double amount;
   private static int value;
   private static int value2;
   private static float amount2;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private Speed() {
      super("speed", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final Speed.Mode getMode() {
      return (Speed.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getUseTimer() {
      return (Boolean)useTimer.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getPauseInLiquids() {
      return (Boolean)pauseInLiquids.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getPauseWhileSneaking() {
      return (Boolean)pauseWhileSneaking.computeObject(this, $$delegatedProperties[3]);
   }

   private final int getFireWorkSlot() {
      return ((Number)fireSlot.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final boolean getArmorStands() {
      return (Boolean)armorStands.computeObject(this, $$delegatedProperties[6]);
   }

   private final float getDistance() {
      return ((Number)distance.computeObject(this, $$delegatedProperties[7])).floatValue();
   }

   private final float getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   private final float getGroundSpeed() {
      return ((Number)groundSpeed.computeObject(this, $$delegatedProperties[9])).floatValue();
   }

   private final Speed.BoostAngle getBoostAngle() {
      return (Speed.BoostAngle)boostAngle.computeObject(this, $$delegatedProperties[10]);
   }

   private final float getPredictTicks() {
      return ((Number)predictTicks.computeObject(this, $$delegatedProperties[11])).floatValue();
   }

   private final int getRollBackTicks() {
      return ((Number)rollbackTicks.computeObject(this, $$delegatedProperties[12])).intValue();
   }

   @Override
   public void update2() {
      value = 1;
      value2 = 0;
      amount = 0.2873;
      stopWatch.update();
   }

   @Flow
   private final void onPreSync(PreSyncEvent preSyncEvent) {
      if ((!MinecraftHolder.getLocalPlayer().isInLiquid() || !this.getPauseInLiquids())
         && (!MinecraftHolder.getLocalPlayer().isShiftKeyDown() || !this.getPauseWhileSneaking())) {
         if (this.getMode() == Speed.Mode.MATRIX) {
            boolean flag = false;

            for (VoxelShape blockCollision : MinecraftHolder.getClientLevel()
               .getBlockCollisions(
                  (Entity)MinecraftHolder.getLocalPlayer(), MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(0.5, 0.0, 0.5).move(0.0, -1.0, 0.0)
               )) {
               if (!Intrinsics.areEqual(blockCollision, Shapes.empty())) {
                  flag = true;
                  break;
               }
            }

            if (WorldUtil.INSTANCE.isFlag4() && flag && WorldUtil.INSTANCE.getAmount3() <= 0.0) {
               MinecraftHolder.getLocalPlayer().setOnGround(true);
               MinecraftHolder.getLocalPlayer().jumpFromGround();
            } else if (WorldUtil.INSTANCE.getAmount3() > 0.0 && this.getUseTimer()) {
               TickService.INSTANCE.setAmount2(1.088F);
               MinecraftHolder.getLocalPlayer().push(0.0, -0.003, 0.0);
            }
         }
      }
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if ((!MinecraftHolder.getLocalPlayer().isInLiquid() || !this.getPauseInLiquids())
         && (!MinecraftHolder.getLocalPlayer().isShiftKeyDown() || !this.getPauseWhileSneaking())) {
         if (this.getMode() == Speed.Mode.GRIM_ENTITY) {
            PlayerSkeletonTracker.INSTANCE.applyPlayers(this.getRollBackTicks());
            if (WorldUtil.INSTANCE.isFlag4()) {
               int value3 = 0;
               Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
               boolean intersects = false;
               var arrayList = new ArrayList();

               for (Object object : entitiesForRendering) {
                  Entity entity = (Entity)object;
                  if (!Intrinsics.areEqual(entity, MinecraftHolder.getLocalPlayer())
                     && (!(entity instanceof ArmorStand) || INSTANCE.getArmorStands())
                     && (entity instanceof LivingEntity || entity instanceof Boat)) {
                     arrayList.add(object);
                  }
               }

               for (Entity entity2 : (Iterable<Entity>)arrayList) {
                  intersects = MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(this.getDistance()).intersects(entity2.getBoundingBox());
                  Map map = PlayerSkeletonTracker.INSTANCE.getMap();
                  boolean flag;
                  if (map.isEmpty()) {
                     flag = false;
                  } else {
                     Iterator iterator = map.entrySet().iterator();

                     while (true) {
                        if (!iterator.hasNext()) {
                           flag = false;
                           break;
                        }

                        label161: {
                           Entry entry = (Entry)iterator.next();
                           if (((Number)entry.getKey()).intValue() == entity2.getId()) {
                              Iterable value4 = (Iterable)entry.getValue();
                              if (value4 instanceof Collection && ((Collection)value4).isEmpty()) {
                                 flag = false;
                              } else {
                                 Iterator iterator2 = value4.iterator();

                                 while (true) {
                                    if (!iterator2.hasNext()) {
                                       flag = false;
                                       break;
                                    }

                                    Object object2 = iterator2.next();
                                    AABB aabb = (AABB)object2;
                                    if (MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(INSTANCE.getDistance()).intersects(aabb)) {
                                       flag = true;
                                       break;
                                    }
                                 }
                              }

                              if (flag) {
                                 flag = true;
                                 break label161;
                              }
                           }

                           flag = false;
                        }

                        if (flag) {
                           flag = true;
                           break;
                        }
                     }
                  }

                  boolean flag2 = flag;
                  if (flag2 || intersects) {
                     value3++;
                  }
               }

               float groundSpeed2 = MinecraftHolder.getLocalPlayer().onGround() ? this.getGroundSpeed() : this.getSpeed();

               float value5 = switch (Speed.WhenMappings.values[this.getBoostAngle().ordinal()]) {
                  case 1 -> RotationManager.INSTANCE.fetchXRot(true).getAmount();
                  case 2 -> MinecraftHolder.getLocalPlayer().getYRot();
                  case 3 -> {
                     Entity entity3 = Aura.INSTANCE.getEntity();
                     yield Aura.INSTANCE.getEnabled() && entity3 != null
                        ? Rotation.vectorUtil
                           .computeRotation(
                              this.computeVec3(entity3)
                                 .add(
                                    new Vec3(entity3.getX() - entity3.xo, entity3.getY() - entity3.yo, entity3.getZ() - entity3.zo)
                                       .multiply(this.getPredictTicks(), this.getPredictTicks(), this.getPredictTicks())
                                 )
                           )
                           .getAmount()
                        : RotationManager.INSTANCE.fetchXRot(true).getAmount();
                  }
                  default -> throw new NoWhenBranchMatchedException();
               };
               double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, groundSpeed2 / 100.0F * value3, false, 0.0F, 0.0F, value5, 14, null);
               MinecraftHolder.getLocalPlayer().push(defaultImpl[0], 0.0, defaultImpl[1]);
            }
         }

         if (this.getMode() == Speed.Mode.FIREWORK) {
            int value22 = value2;
            value2 = value22 + -1;
            value22 = InventoryManager.INSTANCE.getValue();
            InventoryManager instance = InventoryManager.INSTANCE;
            Item[] items = new Item[]{Items.FIREWORK_ROCKET};
            SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
            boolean areEqual = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FIREWORK_ROCKET);
            if (!searchInvResult.isFlag()) {
               instance = InventoryManager.INSTANCE;
               Item[] items2 = new Item[]{Items.FIREWORK_ROCKET};
               SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items2);
               if (searchInvResult2.isFlag()) {
                  ContainerUtil.defaultImpl(searchInvResult2.getValue(), this.getFireWorkSlot() - 1, ContainerInput.SWAP, false, 8, null);
               }
            }

            if (value22 != -1
               && (searchInvResult.isFlag() || areEqual)
               && !MinecraftHolder.getLocalPlayer().onGround()
               && WorldUtil.INSTANCE.getAmount3() > 0.0
               && value2 <= 0) {
               if (value22 != -2) {
                  ContainerUtil.defaultImpl(value22, 0, null, false, 14, null);
                  ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
                  ContainerUtil.defaultImpl(value22, 0, null, true, 6, null);
               }

               ElytraSwap.INSTANCE.setFlag(true);
               Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
               PacketUtil.applySend(serverboundPlayerCommandPacket);
               MinecraftHolder.getLocalPlayer().startFallFlying();
               int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
               if (selectedSlot != searchInvResult.getValue() && !areEqual) {
                  InventoryManager.INSTANCE.applyGetInventory2(searchInvResult.getValue());
               }

               ContainerUtil.defaultImpl(areEqual ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
               if (selectedSlot != searchInvResult.getValue() && !areEqual) {
                  InventoryManager.INSTANCE.applyGetInventory2(selectedSlot);
               }

               if (value22 != -2) {
                  ContainerUtil.defaultImpl(value22, 0, null, false, 14, null);
                  ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
                  ContainerUtil.defaultImpl(value22, 0, null, true, 6, null);
               }

               value2 = this.getDelay();
            }
         }
      }
   }

   @Flow
   private final void onMove(MoveEvent moveEvent) {
      if (!MinecraftHolder.getLocalPlayer().getAbilities().flying) {
         if (!MinecraftHolder.getLocalPlayer().isFallFlying()) {
            if (MinecraftHolder.getLocalPlayer().getFoodData().getFoodLevel() > 6) {
               if (!moveEvent.getCancelled()) {
                  if ((!MinecraftHolder.getLocalPlayer().isInLiquid() || !this.getPauseInLiquids())
                     && (!MinecraftHolder.getLocalPlayer().isShiftKeyDown() || !this.getPauseWhileSneaking())) {
                     if (this.getMode() == Speed.Mode.NCP || this.getMode() == Speed.Mode.STRICT_STRAFE) {
                        if (WorldUtil.INSTANCE.isFlag4()) {
                           if (this.getUseTimer()) {
                              TickService.INSTANCE.setAmount2(1.088F);
                           }

                           float amount3 = this.getMode() == Speed.Mode.NCP && MinecraftHolder.getLocalPlayer().input.getMoveVector().y <= 0.0F && amount2 > 0.0F
                              ? WorldUtil.INSTANCE.getAmount() * 0.66F
                              : WorldUtil.INSTANCE.getAmount();
                           boolean flag = !MinecraftHolder.getLocalPlayer().horizontalCollision || !Step.INSTANCE.getEnabled();
                           if (value == 1 && MinecraftHolder.getLocalPlayer().onGround() && flag) {
                              MinecraftHolder.getLocalPlayer()
                                 .setDeltaMovement(
                                    MinecraftHolder.getLocalPlayer().getDeltaMovement().x,
                                    WorldUtil.INSTANCE.getAmount8(),
                                    MinecraftHolder.getLocalPlayer().getDeltaMovement().z
                                 );
                              moveEvent.setY(WorldUtil.INSTANCE.getAmount8());
                              amount *= 2.149;
                              value = 2;
                           } else if (value == 2) {
                              amount = amount3 - 0.66 * (amount3 - WorldUtil.INSTANCE.getAmount7());
                              value = 3;
                           } else {
                              if (MinecraftHolder.getClientLevel()
                                    .getBlockCollisions(
                                       (Entity)MinecraftHolder.getLocalPlayer(),
                                       MinecraftHolder.getLocalPlayer()
                                          .getBoundingBox()
                                          .move(0.0, MinecraftHolder.getLocalPlayer().getDeltaMovement().y(), 0.0)
                                    )
                                    .iterator()
                                    .hasNext()
                                 || MinecraftHolder.getLocalPlayer().verticalCollision) {
                                 value = 1;
                              }

                              amount = amount3 - amount3 / 159.0;
                           }

                           amount = Math.max(amount, WorldUtil.INSTANCE.getAmount7());
                           double value3 = this.getMode() != Speed.Mode.STRICT_STRAFE && !(MinecraftHolder.getLocalPlayer().input.getMoveVector().y < 1.0F)
                              ? 0.576
                              : 0.465;
                           double value4 = this.getMode() != Speed.Mode.STRICT_STRAFE && !(MinecraftHolder.getLocalPlayer().input.getMoveVector().y < 1.0F)
                              ? 0.57
                              : 0.44;
                           if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.SPEED)) {
                              double amplifier = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.SPEED).getAmplifier();
                              value3 *= 1 + 0.2 * (amplifier + 1);
                              value4 *= 1 + 0.2 * (amplifier + 1);
                           }

                           if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.SLOWNESS)) {
                              double amplifier2 = MinecraftHolder.getLocalPlayer().getEffect(MobEffects.SLOWNESS).getAmplifier();
                              value3 /= 1 + 0.2 * (amplifier2 + 1);
                              value4 /= 1 + 0.2 * (amplifier2 + 1);
                           }

                           amount = Math.min(amount, value2 > 25 ? value3 : value4);
                           int value5 = value2++;
                           if (value5 > 50) {
                              value2 = 0;
                           }

                           WorldUtil.INSTANCE.onMove(moveEvent, amount);
                           amount2 = MinecraftHolder.getLocalPlayer().input.getMoveVector().y;
                        } else {
                           moveEvent.setX(0.0);
                           moveEvent.setZ(0.0);
                        }

                        moveEvent.cancel();
                     }

                     if (this.getMode() == Speed.Mode.OLD_MATRIX
                        && WorldUtil.INSTANCE.getAmount3() > 0.5
                        && !MinecraftHolder.getLocalPlayer().onGround()
                        && MinecraftHolder.getClientLevel()
                           .getBlockCollisions(
                              (Entity)MinecraftHolder.getLocalPlayer(),
                              MinecraftHolder.getLocalPlayer().getBoundingBox().inflate(-0.299, 0.0, -0.299).move(0.0, moveEvent.getY(), 0.0)
                           )
                           .iterator()
                           .hasNext()) {
                        moveEvent.setX(moveEvent.getX() * 2);
                        moveEvent.setZ(moveEvent.getZ() * 2);
                        MinecraftHolder.getLocalPlayer().setDeltaMovement(MinecraftHolder.getLocalPlayer().getDeltaMovement().multiply(2.0, 1.0, 2.0));
                        moveEvent.cancel();
                     }
                  }
               }
            }
         }
      }
   }

   static {
   }

   public static enum BoostAngle {
      CLIENT,
      SERVER,
      PREDICT;

      private static final Speed.BoostAngle[] boostAngles = $values();
      private static final EnumEntries<Speed.BoostAngle> enumEntries = EnumEntriesKt.enumEntries(boostAngles);

      private BoostAngle() {
      }

      @NotNull
      public static EnumEntries<Speed.BoostAngle> getEnumEntries() {
         return enumEntries;
      }

      private static final Speed.BoostAngle[] $values() {
         return new Speed.BoostAngle[]{CLIENT, SERVER, PREDICT};
      }
   }

   public static enum Mode {
      STRICT_STRAFE,
      MATRIX,
      NCP,
      GRIM_ENTITY,
      FIREWORK,
      OLD_MATRIX;

      private static final Speed.Mode[] modes = $values();
      private static final EnumEntries<Speed.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<Speed.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final Speed.Mode[] $values() {
         return new Speed.Mode[]{STRICT_STRAFE, MATRIX, NCP, GRIM_ENTITY, FIREWORK, OLD_MATRIX};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Speed.BoostAngle.values().length];

         try {
            values2[Speed.BoostAngle.SERVER.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Speed.BoostAngle.CLIENT.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Speed.BoostAngle.PREDICT.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}