// deobf by pusia
package su.catlean.client.features.modules.misc;

import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.BrewingStandMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.math.Vec3iUtil;
import net.minecraft.world.item.alchemy.Potion;

public final class AutoPotionBrew extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoPotionBrew.class, "mode", "getMode()Lsu/catlean/gz;", 0), new PropertyReference1Impl(AutoPotionBrew.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(AutoPotionBrew.class, "addGunpowder", "getAddGunpowder()Z", 0), new PropertyReference1Impl(AutoPotionBrew.class, "afterComplete", "getAfterComplete()Lsu/catlean/i;", 0), new PropertyReference1Impl(AutoPotionBrew.class, "open", "getOpen()Z", 0) };
   @NotNull
   public static final AutoPotionBrew INSTANCE = new AutoPotionBrew();
   @NotNull
   private static final EnumSetting potion = SettingFactory.defaultImpl(INSTANCE, "potion", AutoPotionBrew.PotionMode.STRENGTH, null, null, 12, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 100, new IntRange(0, 1000), null, null, 24, null);
   @NotNull
   private static final BooleanSetting throwable = SettingFactory.defaultImpl(INSTANCE, "throwable", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting afterComplete = SettingFactory.defaultImpl(
      INSTANCE, "after-complete", AutoPotionBrew.AfterComplete.TAKE, null, null, 12, null
   );
   @NotNull
   private static final BooleanSetting open = SettingFactory.defaultImpl(INSTANCE, "open", true, null, null, 12, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final Map<BlockPos, Integer> map = new LinkedHashMap();
   @Nullable
   private static BlockPos blockPos;

   private AutoPotionBrew() {
      super("auto-potion-brew", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final AutoPotionBrew.PotionMode getMode() {
      return (AutoPotionBrew.PotionMode)potion.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final boolean getAddGunpowder() {
      return (Boolean)throwable.computeObject(this, $$delegatedProperties[2]);
   }

   private final AutoPotionBrew.AfterComplete getAfterComplete() {
      return (AutoPotionBrew.AfterComplete)afterComplete.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getOpen() {
      return (Boolean)open.computeObject(this, $$delegatedProperties[4]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (!(MinecraftHolder.getLocalPlayer().containerMenu instanceof BrewingStandMenu)) {
         if (this.getOpen()) {
            for (BlockEntity item : (Iterable<BlockEntity>)ServerInfoService.INSTANCE.getItems()) {
               if (item instanceof BrewingStandBlockEntity) {
                  LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
                  BlockPos blockPos2 = ((BrewingStandBlockEntity)item).getBlockPos();
                  if (localPlayer.distanceToSqr(Vec3iUtil.computeVec3(blockPos2))
                        <= MinecraftHolder.getLocalPlayer().blockInteractionRange() * MinecraftHolder.getLocalPlayer().blockInteractionRange()
                     && (
                        map.get(((BrewingStandBlockEntity)item).getBlockPos()) == null
                           || MinecraftHolder.getLocalPlayer().tickCount - ((Number)map.get(((BrewingStandBlockEntity)item).getBlockPos())).intValue() > 0
                     )
                     && MinecraftHolder.getLocalPlayer().tickCount % 10 == 0) {
                     BlockHitResult blockHitResult = new BlockHitResult(
                        new Vec3(
                           (double)((BrewingStandBlockEntity)item).getBlockPos().getX() + 0.5F,
                           ((BrewingStandBlockEntity)item).getBlockPos().getY() + 0.125,
                           (double)((BrewingStandBlockEntity)item).getBlockPos().getZ() + 0.5F
                        ),
                        Direction.UP,
                        ((BrewingStandBlockEntity)item).getBlockPos(),
                        false
                     );
                     RotationManager.INSTANCE.updateRotationAction(new RotationAction(Rotation.vectorUtil.computeRotation(blockHitResult.getLocation()), 0, () -> {
                        MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, blockHitResult);
                        blockPos = ((BrewingStandBlockEntity)item).getBlockPos();
                        return Unit.INSTANCE;
                     }, 2, null));
                  }
               }
            }
         }
      } else {
         BrewingStandMenu containerMenu = (BrewingStandMenu)MinecraftHolder.getLocalPlayer().containerMenu;
         if (this.isFlag2()) {
            if (this.fetchItem(Items.BLAZE_POWDER) != -1) {
               this.updateItem(Items.BLAZE_POWDER, 4);
            }

            return;
         }

         for (int i = 0; i < 3; i++) {
            if (Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(i)).getItem().getItem(), Items.AIR)) {
               int value = this.computeValue((Potion)Potions.WATER.value());
               if (stopWatch.testValue(this.getDelay()) && value != -1) {
                  ContainerUtil.defaultImpl(value, 0, null, false, 14, null);
                  ContainerUtil.defaultImpl(i, 0, null, false, 14, null);
                  Object object = null;
                  byte byteValue = 14;
                  boolean flag = false;
                  Object object2 = null;
                  byte byteValue2 = 0;
                  ContainerUtil.defaultImpl(value, byteValue2, (ContainerInput)object2, flag, byteValue, object);
                  stopWatch.update();
                  return;
               }
            }
         }

         if (this.checkPotion((Potion)Potions.WATER.value())
            && this.fetchItem(Items.NETHER_WART) != -1
            && !Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(3)).getItem().getItem(), Items.NETHER_WART)) {
            this.updateItem(Items.NETHER_WART, 3);
            return;
         }

         if (this.checkPotion((Potion)Potions.AWKWARD.value())
            && this.fetchItem(this.getMode().getItem()) != -1
            && !Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(3)).getItem().getItem(), this.getMode().getItem())) {
            this.updateItem(this.getMode().getItem(), 3);
            return;
         }

         if (this.checkPotion((Potion)Potions.NIGHT_VISION.value())
            && !Intrinsics.areEqual(this.getMode().getItem2(), Items.AIR)
            && this.fetchItem(this.getMode().getItem2()) != -1
            && !Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(3)).getItem().getItem(), this.getMode().getItem2())) {
            this.updateItem(this.getMode().getItem2(), 3);
            return;
         }

         boolean flag2 = this.checkPotion((Potion)Potions.STRENGTH.value())
            || this.checkPotion((Potion)Potions.SWIFTNESS.value())
            || this.checkPotion((Potion)Potions.SWIFTNESS.value())
            || this.checkPotion((Potion)Potions.INVISIBILITY.value());
         boolean flag3 = true;
         if (flag2 && !Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(3)).getItem().getItem(), this.getMode().getItem3())) {
            if (this.fetchItem(this.getMode().getItem3()) != -1) {
               this.updateItem(this.getMode().getItem3(), 3);
               return;
            }

            flag3 = false;
         }

         boolean flag4 = this.checkPotion((Potion)Potions.STRONG_STRENGTH.value())
            || this.checkPotion((Potion)Potions.STRONG_SWIFTNESS.value())
            || this.checkPotion((Potion)Potions.LONG_FIRE_RESISTANCE.value())
            || this.checkPotion((Potion)Potions.LONG_INVISIBILITY.value());
         if (flag2 && !flag3) {
            flag4 = true;
         }

         if (flag4) {
            if (!this.getAddGunpowder() || this.fetchItem(Items.GUNPOWDER) == -1 || this.isFlag()) {
               this.update();
               return;
            }

            if (!Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(3)).getItem().getItem(), Items.GUNPOWDER)) {
               this.updateItem(Items.GUNPOWDER, 3);
               return;
            }
         }

         if (containerMenu.getBrewingTicks() > 0 && blockPos != null) {
            MinecraftHolder.getLocalPlayer().closeContainer();
            map.put(blockPos, MinecraftHolder.getLocalPlayer().tickCount + containerMenu.getBrewingTicks());
         }
      }
   }

   private final void update() {
      for (int i = 0; i < 3; i++) {
         if (stopWatch.testValue(this.getDelay())) {
            if (this.getAfterComplete() == AutoPotionBrew.AfterComplete.TAKE) {
               ContainerUtil.defaultImpl(i, 0, ContainerInput.QUICK_MOVE, false, 8, null);
            } else if (this.getAfterComplete() == AutoPotionBrew.AfterComplete.THROW) {
               ContainerUtil.defaultImpl(i, 0, ContainerInput.THROW, false, 8, null);
            }
         }
      }
   }

   private final boolean isFlag() {
      for (int i = 0; i < 3; i++) {
         if (!Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(i)).getItem().getItem(), Items.SPLASH_POTION)) {
            return false;
         }
      }

      return true;
   }

   private final boolean checkPotion(Potion potion) {
      for (int i = 0; i < 3; i++) {
         ItemStack item = ((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(i)).getItem();
         PotionContents potioN_CONTENTS = (PotionContents)item.get(DataComponents.POTION_CONTENTS);
         if (potioN_CONTENTS == null || !potioN_CONTENTS.potion().isPresent()) {
            return false;
         }

         Potion potionItemItem = (Potion)((Holder)potioN_CONTENTS.potion().get()).value();
         if (!Intrinsics.areEqual(potionItemItem, potion)) {
            return false;
         }
      }

      return true;
   }

   private final int computeValue(Potion potion) {
      for (int i = 5; i < 41; i++) {
         ItemStack item = ((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(i)).getItem();
         PotionContents potioN_CONTENTS = (PotionContents)item.get(DataComponents.POTION_CONTENTS);
         if (potioN_CONTENTS != null && potioN_CONTENTS.potion().isPresent()) {
            Potion potionItemItem = (Potion)((Holder)potioN_CONTENTS.potion().get()).value();
            if (Intrinsics.areEqual(potionItemItem, potion)) {
               return i;
            }
         }
      }

      return -1;
   }

   private final void updateItem(Item item, int value) {
      if (stopWatch.testValue(this.getDelay())) {
         int item2 = this.fetchItem(item);
         if (item2 != -1) {
            ContainerUtil.defaultImpl(item2, 0, null, false, 14, null);
            ContainerUtil.defaultImpl(value, 1, null, false, 12, null);
            Object object = null;
            byte byteValue = 14;
            boolean flag = false;
            Object object2 = null;
            byte byteValue2 = 0;
            ContainerUtil.defaultImpl(item2, byteValue2, (ContainerInput)object2, flag, byteValue, object);
            stopWatch.update();
         }
      }
   }

   private final boolean isFlag2() {
      return ((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(4)).getItem().getItem() instanceof AirItem;
   }

   private final int fetchItem(Item item) {
      for (int i = 5; i < 41; i++) {
         if (Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(i)).getItem().getItem(), item)) {
            return i;
         }
      }

      return -1;
   }

   static {
   }

   public static enum AfterComplete {
      THROW,
      TAKE,
      NONE;

      private static final AutoPotionBrew.AfterComplete[] afterCompletes = $values();
      private static final EnumEntries<AutoPotionBrew.AfterComplete> enumEntries = EnumEntriesKt.enumEntries(afterCompletes);

      private AfterComplete() {
      }

      @NotNull
      public static EnumEntries<AutoPotionBrew.AfterComplete> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoPotionBrew.AfterComplete[] $values() {
         return new AutoPotionBrew.AfterComplete[]{THROW, TAKE, NONE};
      }
   }

   public static enum PotionMode {
      SPEED(Items.SUGAR, Items.AIR, Items.GLOWSTONE_DUST),
      STRENGTH(Items.BLAZE_POWDER, Items.AIR, Items.GLOWSTONE_DUST),
      FIRE_RESISTANCE(Items.MAGMA_CREAM, Items.AIR, Items.REDSTONE),
      INVISIBILITY(Items.GOLDEN_CARROT, Items.FERMENTED_SPIDER_EYE, Items.REDSTONE);

      @NotNull
      private final Item item;
      @NotNull
      private final Item item2;
      @NotNull
      private final Item item3;
      private static final AutoPotionBrew.PotionMode[] potions = $values();
      private static final EnumEntries<AutoPotionBrew.PotionMode> enumEntries = EnumEntriesKt.enumEntries(potions);

      private PotionMode(Item item, Item item2, Item item3) {
         this.item = item;
         this.item2 = item2;
         this.item3 = item3;
      }

      @NotNull
      public final Item getItem() {
         return this.item;
      }

      @NotNull
      public final Item getItem2() {
         return this.item2;
      }

      @NotNull
      public final Item getItem3() {
         return this.item3;
      }

      @NotNull
      public static EnumEntries<AutoPotionBrew.PotionMode> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoPotionBrew.PotionMode[] $values() {
         return new AutoPotionBrew.PotionMode[]{SPEED, STRENGTH, FIRE_RESISTANCE, INVISIBILITY};
      }
   }
}