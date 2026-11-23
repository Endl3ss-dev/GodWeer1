// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ClearClickMapEvent;
import su.catlean.api.event.events.network.SlowDownEvent;
import su.catlean.api.event.events.player.KeyboardInputEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.ShouldStopSprintingEvent;
import su.catlean.api.event.events.world.SlipperinessEvent;
import su.catlean.api.event.events.world.StepOnSlimeEvent;
import su.catlean.api.event.events.world.SweetBerryBushCollisionEvent;
import su.catlean.api.event.events.world.VelocityMultiplierEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.Sprint;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ClientInputAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;

public final class NoSlow extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(NoSlow.class, "mode", "getMode()Lsu/catlean/mf;", 0), new PropertyReference1Impl(NoSlow.class, "limitPackets", "getLimitPackets()Z", 0), new PropertyReference1Impl(NoSlow.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(NoSlow.class, "mainHand", "getMainHand()Z", 0), new PropertyReference1Impl(NoSlow.class, "selection", "getSelection()Lsu/catlean/nl;", 0), new PropertyReference1Impl(NoSlow.class, "food", "getFood()Z", 0), new PropertyReference1Impl(NoSlow.class, "projectiles", "getProjectiles()Z", 0), new PropertyReference1Impl(NoSlow.class, "shield", "getShield()Z", 0), new PropertyReference1Impl(NoSlow.class, "soulSand", "getSoulSand()Z", 0), new PropertyReference1Impl(NoSlow.class, "honey", "getHoney()Z", 0), new PropertyReference1Impl(NoSlow.class, "slime", "getSlime()Z", 0), new PropertyReference1Impl(NoSlow.class, "ice", "getIce()Z", 0), new PropertyReference1Impl(NoSlow.class, "sweetBerryBush", "getSweetBerryBush()Z", 0), new PropertyReference1Impl(NoSlow.class, "sneak", "getSneak()Z", 0), new PropertyReference1Impl(NoSlow.class, "crawl", "getCrawl()Z", 0) };
   @NotNull
   public static final NoSlow INSTANCE = new NoSlow();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", NoSlow.Mode.VANILLA, null, null, 12, null);
   @NotNull
   private static final BooleanSetting limitPackets = SettingFactory.defaultImpl(
      INSTANCE, "limit-packets", false, null, () -> INSTANCE.getMode() == NoSlow.Mode.GRIM, 4, null
   );
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(
      INSTANCE, "delay", 3, new IntRange(1, 10), null, () -> INSTANCE.getMode() == NoSlow.Mode.GRIM_2, 8, null
   );
   @NotNull
   private static final BooleanSetting mainHand = SettingFactory.defaultImpl(INSTANCE, "main-hand", true, null, null, 12, null);
   @NotNull
   private static final GroupSetting selection = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "selection", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting food = SettingFactory.defaultImpl(INSTANCE, "food", true, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting projectiles = SettingFactory.defaultImpl(INSTANCE, "projectiles", true, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting shield = SettingFactory.defaultImpl(INSTANCE, "shield", true, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting soulSand = SettingFactory.defaultImpl(INSTANCE, "soul-sand", true, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting honey = SettingFactory.defaultImpl(INSTANCE, "honey", true, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting slime = SettingFactory.defaultImpl(INSTANCE, "slime", true, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting ice = SettingFactory.defaultImpl(INSTANCE, "ice", true, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting sweetBerryBush = SettingFactory.defaultImpl(INSTANCE, "sweet-berry-bush", true, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting sneak = SettingFactory.defaultImpl(INSTANCE, "sneak", false, INSTANCE.getSelection(), null, 8, null);
   @NotNull
   private static final BooleanSetting crawl = SettingFactory.defaultImpl(INSTANCE, "crawl", false, INSTANCE.getSelection(), null, 8, null);
   private static boolean flag;
   private static boolean flag2;
   private static boolean flag3;
   private static boolean flag4;
   private static boolean flag5;
   private static boolean flag6;

   private NoSlow() {
      super("no-slow", SubCategories.getSubCategory5(), null, 4, null);
   }

   @NotNull
   public final NoSlow.Mode getMode() {
      return (NoSlow.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getLimitPackets() {
      return (Boolean)limitPackets.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final boolean getMainHand() {
      return (Boolean)mainHand.computeObject(this, $$delegatedProperties[3]);
   }

   private final GroupValue getSelection() {
      return (GroupValue)selection.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getFood() {
      return (Boolean)food.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getProjectiles() {
      return (Boolean)projectiles.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getShield() {
      return (Boolean)shield.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getSoulSand() {
      return (Boolean)soulSand.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getHoney() {
      return (Boolean)honey.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getSlime() {
      return (Boolean)slime.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getIce() {
      return (Boolean)ice.computeObject(this, $$delegatedProperties[11]);
   }

   private final boolean getSweetBerryBush() {
      return (Boolean)sweetBerryBush.computeObject(this, $$delegatedProperties[12]);
   }

   private final boolean getSneak() {
      return (Boolean)sneak.computeObject(this, $$delegatedProperties[13]);
   }

   private final boolean getCrawl() {
      return (Boolean)crawl.computeObject(this, $$delegatedProperties[14]);
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      flag3 = flag2;
      flag2 = !flag2;
      if (flag4) {
         flag2 = true;
         flag4 = false;
      }

      if (flag) {
         MinecraftHolder.getMinecraft().options.keyShift.setDown(false);
         MinecraftHolder.getLocalPlayer().setSprinting(false);
         flag = false;
      }

      if (MinecraftHolder.getLocalPlayer().isUsingItem() && !MinecraftHolder.getLocalPlayer().isHandsBusy() && !MinecraftHolder.getLocalPlayer().isFallFlying()
         )
       {
         switch (NoSlow.WhenMappings.values[this.getMode().ordinal()]) {
            case 1:
               Packet serverboundSetCarriedItemPacket = (Packet)(new ServerboundSetCarriedItemPacket(MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot()));
               PacketUtil.applySend(serverboundSetCarriedItemPacket);
               break;
            case 2:
               if (MinecraftHolder.getLocalPlayer().getUsedItemHand() == InteractionHand.OFF_HAND) {
                  Packet serverboundSetCarriedItemPacket2 = (Packet)(new ServerboundSetCarriedItemPacket(MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() % 8 + 1));
                  PacketUtil.applySend(serverboundSetCarriedItemPacket2);
                  serverboundSetCarriedItemPacket2 = (Packet)(new ServerboundSetCarriedItemPacket(MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot()));
                  PacketUtil.applySend(serverboundSetCarriedItemPacket2);
               } else if (this.getMainHand()
                  && (
                     MinecraftHolder.getLocalPlayer().getTicksUsingItem() <= 3
                        || MinecraftHolder.getLocalPlayer().tickCount % 2 == 0
                        || !this.getLimitPackets()
                  )) {
                  ContainerUtil.defaultImpl(InteractionHand.OFF_HAND, 0.0F, 0.0F, 6, null);
               }
         }
      }
   }

   @Flow
   private final void onSlipperiness(SlipperinessEvent slipperinessEvent) {
      if (this.getSlime() && Intrinsics.areEqual(slipperinessEvent.getBlock(), Blocks.SLIME_BLOCK)) {
         slipperinessEvent.cancel();
      }

      if (this.getIce()
         && (
            Intrinsics.areEqual(slipperinessEvent.getBlock(), Blocks.ICE)
               || Intrinsics.areEqual(slipperinessEvent.getBlock(), Blocks.PACKED_ICE)
               || Intrinsics.areEqual(slipperinessEvent.getBlock(), Blocks.BLUE_ICE)
               || Intrinsics.areEqual(slipperinessEvent.getBlock(), Blocks.FROSTED_ICE)
         )
         && !MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
         slipperinessEvent.cancel();
      }
   }

   @Flow
   private final void onVelocityMultiplier(VelocityMultiplierEvent velocityMultiplierEvent) {
      if (this.getSoulSand() && Intrinsics.areEqual(velocityMultiplierEvent.getBlock(), Blocks.SOUL_SAND)
         || this.getHoney() && Intrinsics.areEqual(velocityMultiplierEvent.getBlock(), Blocks.HONEY_BLOCK)) {
         velocityMultiplierEvent.cancel();
      }
   }

   @Flow
   private final void onStepOnSlime(StepOnSlimeEvent stepOnSlimeEvent) {
      if (this.getSlime()) {
         stepOnSlimeEvent.cancel();
      }
   }

   @Flow
   private final void onClearClickMap(ClearClickMapEvent clearClickMapEvent) {
      if (flag6) {
         clearClickMapEvent.cancel();
      }
   }

   @Flow
   private final void onKeyboardInput(KeyboardInputEvent keyboardInputEvent) {
      if (this.getMode() == NoSlow.Mode.MATRIX && MinecraftHolder.getLocalPlayer().isUsingItem() && !MinecraftHolder.getLocalPlayer().isFallFlying()) {
         ((ClientInputAccessor)MinecraftHolder.getLocalPlayer().input)
            .setMoveVector(new Vec2(MinecraftHolder.getLocalPlayer().input.getMoveVector().x * 5, MinecraftHolder.getLocalPlayer().input.getMoveVector().y * 5));
         float value = MinecraftHolder.getLocalPlayer().onGround()
            ? (
               MinecraftHolder.getLocalPlayer().input.getMoveVector().y != 0.0F && MinecraftHolder.getLocalPlayer().input.getMoveVector().x != 0.0F
                  ? 0.35F
                  : 0.5F
            )
            : (
               MinecraftHolder.getLocalPlayer().input.getMoveVector().y != 0.0F && MinecraftHolder.getLocalPlayer().input.getMoveVector().x != 0.0F
                  ? 0.47F
                  : 0.67F
            );
         ((ClientInputAccessor)MinecraftHolder.getLocalPlayer().input)
            .setMoveVector(
               new Vec2(MinecraftHolder.getLocalPlayer().input.getMoveVector().x * value, MinecraftHolder.getLocalPlayer().input.getMoveVector().y * value)
            );
      } else if (this.checkGetItem(true) && MinecraftHolder.getLocalPlayer().isUsingItem() && !MinecraftHolder.getLocalPlayer().isFallFlying()) {
         ((ClientInputAccessor)MinecraftHolder.getLocalPlayer().input)
            .setMoveVector(new Vec2(MinecraftHolder.getLocalPlayer().input.getMoveVector().x * 5, MinecraftHolder.getLocalPlayer().input.getMoveVector().y * 5));
      }
   }

   @Flow
   private final void onSlowDown(SlowDownEvent slowDownEvent) {
      if (MinecraftHolder.getLocalPlayer().isVisuallyCrawling() && this.getCrawl() || this.getSneak()) {
         slowDownEvent.cancel();
      }
   }

   @Flow
   private final void onShouldStopSprinting(ShouldStopSprintingEvent shouldStopSprintingEvent) {
      if (this.checkGetItem(false) && MinecraftHolder.getLocalPlayer().isUsingItem()) {
         shouldStopSprintingEvent.cancel();
      }
   }

   @Flow
   private final void onSweetBerryBushCollision(SweetBerryBushCollisionEvent sweetBerryBushCollisionEvent) {
      if (this.getSweetBerryBush()) {
         sweetBerryBushCollisionEvent.cancel();
      }
   }

   private final boolean checkGetItem(boolean flag) {
      if (this.getMode() == NoSlow.Mode.MATRIX) {
         return false;
      } else if (!this.getFood() && MinecraftHolder.getLocalPlayer().getUseItem().getComponents().has(DataComponents.FOOD)) {
         return false;
      } else if (!this.getShield() && Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getUseItem().getItem(), Items.SHIELD)) {
         return false;
      } else if (this.getProjectiles()
         || !Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getUseItem().getItem(), Items.CROSSBOW)
            && !Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getUseItem().getItem(), Items.BOW)
            && !Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getUseItem().getItem(), Items.TRIDENT)) {
         if (!this.getMainHand() && MinecraftHolder.getLocalPlayer().getUsedItemHand() == InteractionHand.MAIN_HAND) {
            return false;
         } else if (this.getMode() == NoSlow.Mode.GRIM_2) {
            if (MinecraftHolder.getLocalPlayer().isUsingItem()) {
               if (MinecraftHolder.getLocalPlayer().getTicksUsingItem() >= this.getDelay()) {
                  return true;
               }

               if (MinecraftHolder.getLocalPlayer().getTicksUsingItem() <= 2) {
                  flag6 = true;
                  ContainerUtil.defaultImpl(1, 0, null, true, 6, null);
                  flag6 = false;
               }
            }

            return false;
         } else if (this.getMode() == NoSlow.Mode.GRIM_3) {
            if (MinecraftHolder.getLocalPlayer().isUsingItem()) {
               switch (MinecraftHolder.getLocalPlayer().getTicksUsingItem()) {
                  case 1:
                     flag5 = MinecraftHolder.getLocalPlayer().isSprinting();
                     MinecraftHolder.getLocalPlayer().setSprinting(false);
                     Sprint.INSTANCE.setFlag(true);
                     return false;
                  case 2:
                     flag6 = true;
                     ContainerUtil.defaultImpl(1, 0, null, true, 6, null);
                     flag6 = false;
                     Sprint.INSTANCE.setFlag(true);
                     return false;
                  case 3:
                     MinecraftHolder.getLocalPlayer().setSprinting(flag5);
                     Sprint.INSTANCE.setFlag(true);
                     return false;
                  default:
                     return true;
               }
            } else {
               return false;
            }
         } else if (this.getMode() == NoSlow.Mode.GRIM_V3) {
            if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
               return false;
            } else if (MinecraftHolder.getLocalPlayer().getTicksUsingItem() < 3) {
               return false;
            } else if (!MinecraftHolder.getLocalPlayer().onGround()) {
               return !flag2;
            } else if (!MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
               return MinecraftHolder.getLocalPlayer().tickCount % 2 == 0;
            } else if (flag3) {
               return true;
            } else {
               flag4 = true;
               return false;
            }
         } else {
            return !MinecraftHolder.getLocalPlayer().getOffhandItem().getComponents().has(DataComponents.FOOD)
                  && !Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.SHIELD)
               || this.getMode() != NoSlow.Mode.GRIM
               || MinecraftHolder.getLocalPlayer().getUsedItemHand() != InteractionHand.MAIN_HAND;
         }
      } else {
         return false;
      }
   }

   static {
   }

   public static enum Mode {
      VANILLA,
      NCP,
      GRIM,
      MATRIX,
      GRIM_V3,
      GRIM_2,
      GRIM_3;

      private static final NoSlow.Mode[] modes = $values();
      private static final EnumEntries<NoSlow.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<NoSlow.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final NoSlow.Mode[] $values() {
         return new NoSlow.Mode[]{VANILLA, NCP, GRIM, MATRIX, GRIM_V3, GRIM_2, GRIM_3};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[NoSlow.Mode.values().length];

         try {
            values2[NoSlow.Mode.NCP.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[NoSlow.Mode.GRIM.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}