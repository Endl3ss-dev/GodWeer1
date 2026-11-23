// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ServerboundMovePlayerPacketAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class NoFall extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(NoFall.class, "mode", "getMode()Lsu/catlean/ni;", 0), new PropertyReference1Impl(NoFall.class, "fallDistance", "getFallDistance()Lsu/catlean/qi;", 0), new PropertyReference1Impl(NoFall.class, "fallDistanceValue", "getFallDistanceValue()I", 0), new PropertyReference1Impl(NoFall.class, "waterBucket", "getWaterBucket()Z", 0), new PropertyReference1Impl(NoFall.class, "retrieve", "getRetrieve()Z", 0), new PropertyReference1Impl(NoFall.class, "enderPearl", "getEnderPearl()Z", 0), new PropertyReference1Impl(NoFall.class, "cobweb", "getCobweb()Z", 0), new PropertyReference1Impl(NoFall.class, "twistingVines", "getTwistingVines()Z", 0), new PropertyReference1Impl(NoFall.class, "grim", "getGrim()Z", 0) };
   @NotNull
   public static final NoFall INSTANCE = new NoFall();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", NoFall.Mode.Rubberband, null, null, 12, null);
   @NotNull
   private static final EnumSetting fallDistance = SettingFactory.defaultImpl(INSTANCE, "fall-distance", NoFall.FallDistance.Calc, null, null, 12, null);
   @NotNull
   private static final IntSetting fallDistanceVal = SettingFactory.defaultImpl(
      INSTANCE, "fall-distance-val", 10, new IntRange(2, 100), null, () -> INSTANCE.getFallDistance() == NoFall.FallDistance.Custom, 8, null
   );
   @NotNull
   private static final BooleanSetting waterBucket = SettingFactory.defaultImpl(
      INSTANCE, "water-bucket", true, null, () -> INSTANCE.getMode() == NoFall.Mode.Items, 4, null
   );
   @NotNull
   private static final BooleanSetting retrieve = SettingFactory.defaultImpl(
      INSTANCE, "retrieve", true, null, () -> INSTANCE.getMode() == NoFall.Mode.Items && INSTANCE.getWaterBucket(), 4, null
   );
   @NotNull
   private static final BooleanSetting enderPearl = SettingFactory.defaultImpl(
      INSTANCE, "ender-pearl", true, null, () -> INSTANCE.getMode() == NoFall.Mode.Items, 4, null
   );
   @NotNull
   private static final BooleanSetting cobweb = SettingFactory.defaultImpl(
      INSTANCE, "cobweb", true, null, () -> INSTANCE.getMode() == NoFall.Mode.Items, 4, null
   );
   @NotNull
   private static final BooleanSetting twistingVines = SettingFactory.defaultImpl(
      INSTANCE, "twisting-vines", true, null, () -> INSTANCE.getMode() == NoFall.Mode.Items, 4, null
   );
   @NotNull
   private static final BooleanSetting grim = SettingFactory.defaultImpl(INSTANCE, "grim", true, null, () -> INSTANCE.getMode() == NoFall.Mode.Items, 4, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   private static boolean flag;
   private static int value;
   private static int value2;
   private static boolean flag2;

   private NoFall() {
      super("no-fall", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final NoFall.Mode getMode() {
      return (NoFall.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final NoFall.FallDistance getFallDistance() {
      return (NoFall.FallDistance)fallDistance.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getFallDistanceValue() {
      return ((Number)fallDistanceVal.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final boolean getWaterBucket() {
      return (Boolean)waterBucket.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getRetrieve() {
      return (Boolean)retrieve.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getEnderPearl() {
      return (Boolean)enderPearl.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getCobweb() {
      return (Boolean)cobweb.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getTwistingVines() {
      return (Boolean)twistingVines.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getGrim() {
      return (Boolean)grim.computeObject(this, $$delegatedProperties[8]);
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      if (MinecraftHolder.getLocalPlayer().tickCount < 5) {
         value2 = 0;
      }

      if (value == 1) {
         value = 2;
         this.applySetDown();
      } else if (value == 2) {
         MinecraftHolder.getLocalPlayer().jumpFromGround();
         value = 3;
         this.applySetDown();
      } else if (value == 3) {
         flag2 = false;
         value = 0;
         this.applySetDown();
      } else {
         if (this.isFlag()) {
            switch (NoFall.WhenMappings.values[this.getMode().ordinal()]) {
               case 1:
                  Packet statusOnly = (Packet)(new StatusOnly(true, MinecraftHolder.getLocalPlayer().horizontalCollision));
                  PacketUtil.applySend(statusOnly);
                  break;
               case 2:
                  BlockPos containing = null;
                  containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
                  byte byteValue = 5;

                  for (int i = 0; i < byteValue; i++) {
                     BlockPos below = containing.below(i);
                     if (!MinecraftHolder.getClientLevel().getBlockState(below).canBeReplaced()) {
                        containing = below.above();
                     }
                  }

                  InventoryManager instance = InventoryManager.INSTANCE;
                  Item[] items = new Item[]{Items.ENDER_PEARL};
                  SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
                  instance = InventoryManager.INSTANCE;
                  Item[] items2 = new Item[]{Items.COBWEB};
                  SearchInvResult searchInvResult2 = instance.computeSearchInvResult(items2);
                  instance = InventoryManager.INSTANCE;
                  Item[] items3 = new Item[]{Items.TWISTING_VINES};
                  SearchInvResult searchInvResult3 = instance.computeSearchInvResult(items3);
                  instance = InventoryManager.INSTANCE;
                  Item[] items4 = new Item[]{Items.WATER_BUCKET};
                  SearchInvResult searchInvResult4 = instance.computeSearchInvResult(items4);
                  if (searchInvResult4.isFlag() && this.getWaterBucket() && stopWatch.testValue(500)) {
                     this.applyGetYRot(searchInvResult4);
                     flag = this.getRetrieve();
                  } else if (searchInvResult.isFlag() && this.getEnderPearl() && stopWatch.testValue(500)) {
                     this.applyGetYRot(searchInvResult);
                     stopWatch.update();
                  } else if (searchInvResult2.isFlag() && this.getCobweb()) {
                     this.applyBlockInteractionRange(searchInvResult2, containing);
                  } else if (searchInvResult3.isFlag() && this.getTwistingVines()) {
                     this.applyBlockInteractionRange(searchInvResult3, containing);
                  }
                  break;
               case 3:
               case 4:
                  flag2 = true;
                  break;
               case 5:
                  flag2 = true;
                  this.applySetDown();
                  break;
               case 6:
                  LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
                  boolean flag4 = false;
                  Packet posRot = (Packet)(
                     new PosRot(localPlayer.getX(), localPlayer.getY() + 1.0E-9, localPlayer.getZ(), localPlayer.getYRot(), localPlayer.getXRot(), false, localPlayer.horizontalCollision)
                  );
                  PacketUtil.applySend(posRot);
                  localPlayer.resetFallDistance();
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }
         } else if (flag) {
            InventoryManager instance2 = InventoryManager.INSTANCE;
            Item[] items5 = new Item[]{Items.BUCKET};
            SearchInvResult searchInvResult5 = instance2.computeSearchInvResult(items5);
            if (searchInvResult5.isFlag()) {
               this.applyGetYRot(searchInvResult5);
            }

            flag = false;
         }
      }
   }

   private final void applyGetYRot(SearchInvResult searchInvResult) {
      RotationManager.INSTANCE
         .updateRotationAction(new RotationAction(new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), 90.0F, false, null, 12, null), 0, () -> {
            InventoryManager.INSTANCE.applyGetInventory();
            searchInvResult.update();
            InteractionResult interactionResult = ContainerUtil.computeInteractionResult(InteractionHand.MAIN_HAND, MinecraftHolder.getLocalPlayer().getYRot(), 90.0F);
            if (!Intrinsics.areEqual(interactionResult, InteractionResult.PASS)) {
               stopWatch.update();
            }

            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            InventoryManager.INSTANCE.update();
            return Unit.INSTANCE;
         }, 2, null));
   }

   private final void applyBlockInteractionRange(SearchInvResult searchInvResult, BlockPos blockPos) {
      RotationAction defaultImpl = InteractionManager.defaultImpl(
         InteractionManager.INSTANCE,
         blockPos,
         searchInvResult.getValue(),
         this.getGrim() ? PlaceModule.Interact.GRIM : PlaceModule.Interact.Strict,
         PlaceModule.PlaceMode.Normal,
         TargetFilter.ALL,
         (float)MinecraftHolder.getLocalPlayer().blockInteractionRange(),
         0.0F,
         200,
         PlaceModule.Switch.Silent,
         null,
         false,
         1536,
         null
      );
      if (defaultImpl != null) {
         RotationManager.INSTANCE.updateRotationAction(defaultImpl);
         stopWatch.update();
      }
   }

   private final boolean isFlag() {
      if (MinecraftHolder.getMinecraft().player != null
         && !MinecraftHolder.getLocalPlayer().isFallFlying()
         && !MinecraftHolder.getLocalPlayer().getAbilities().flying) {
         if (this.getMode() == NoFall.Mode.GrimOld) {
            return WorldUtil.INSTANCE.getAmount3() > 3.0;
         } else {
            return switch (NoFall.WhenMappings.values2[this.getFallDistance().ordinal()]) {
               case 1 -> WorldUtil.INSTANCE.getAmount3() > this.getFallDistanceValue();
               case 2 -> (WorldUtil.INSTANCE.getAmount3() - 3) / 2.0F + 3.5F > MinecraftHolder.getLocalPlayer().getHealth() / 3.0F;
               default -> throw new NoWhenBranchMatchedException();
            };
         }
      } else {
         return false;
      }
   }

   @Flow
   public final void onSendPacket(SendPacket e) {
      if (e.getPacket() instanceof ServerboundMovePlayerPacket) {
         boolean flag3 = this.getMode() == NoFall.Mode.GRIM_NEW && value2 < 11;
         if (flag2) {
            if (flag3) {
               ((ServerboundMovePlayerPacketAccessor)e.getPacket()).setGround(!MinecraftHolder.getLocalPlayer().onGround());
               int value3 = value2++;
            } else {
               ((ServerboundMovePlayerPacketAccessor)e.getPacket()).setGround(false);
            }

            if (MinecraftHolder.getLocalPlayer().onGround() && value == 0 && this.getMode() == NoFall.Mode.GRIM_NEW && !flag3) {
               value = 1;
            }
         }
      }
   }

   private final void applySetDown() {
      MinecraftHolder.getLocalPlayer().setSprinting(false);
      MinecraftHolder.getMinecraft().options.keyUp.setDown(false);
      MinecraftHolder.getMinecraft().options.keyDown.setDown(false);
      MinecraftHolder.getMinecraft().options.keyLeft.setDown(false);
      MinecraftHolder.getMinecraft().options.keyRight.setDown(false);
      MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
   }

   @Override
   public void update2() {
      flag2 = false;
   }

   static {
   }

   public static enum FallDistance {
      Calc,
      Custom;

      private static final NoFall.FallDistance[] fallDistances = $values();
      private static final EnumEntries<NoFall.FallDistance> enumEntries = EnumEntriesKt.enumEntries(fallDistances);

      private FallDistance() {
      }

      @NotNull
      public static EnumEntries<NoFall.FallDistance> getEnumEntries() {
         return enumEntries;
      }

      private static final NoFall.FallDistance[] $values() {
         return new NoFall.FallDistance[]{Calc, Custom};
      }
   }

   public static enum Mode {
      Rubberband,
      Items,
      MatrixOffGround,
      Vanilla,
      GrimOld,
      GRIM_NEW;

      private static final NoFall.Mode[] modes = $values();
      private static final EnumEntries<NoFall.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<NoFall.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final NoFall.Mode[] $values() {
         return new NoFall.Mode[]{Rubberband, Items, MatrixOffGround, Vanilla, GrimOld, GRIM_NEW};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;

      static {
         int[] values3 = new int[NoFall.Mode.values().length];

         try {
            values3[NoFall.Mode.Rubberband.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values3[NoFall.Mode.Items.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values3[NoFall.Mode.MatrixOffGround.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values3[NoFall.Mode.Vanilla.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values3[NoFall.Mode.GRIM_NEW.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values3[NoFall.Mode.GrimOld.ordinal()] = 6;
         } catch (NoSuchFieldError exception6) {
         }

         values = values3;
         values3 = new int[NoFall.FallDistance.values().length];

         try {
            values3[NoFall.FallDistance.Custom.ordinal()] = 1;
         } catch (NoSuchFieldError exception7) {
         }

         try {
            values3[NoFall.FallDistance.Calc.ordinal()] = 2;
         } catch (NoSuchFieldError exception8) {
         }

         values2 = values3;
      }
   }
}