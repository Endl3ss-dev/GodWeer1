// deobf by pusia
package su.catlean.client.features.modules.equip;

import su.catlean.utils.KotlinCompat;
import java.util.Iterator;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.random.Random;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.network.PostTasksProcessEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.settings.ItemsSetting;
import su.catlean.api.setting.values.ItemsValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.Render2D;

public final class ChestStealer extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ChestStealer.class, "action", "getAction()Lsu/catlean/qu;", 0), new PropertyReference1Impl(ChestStealer.class, "sort", "getSort()Lsu/catlean/ik;", 0), new PropertyReference1Impl(ChestStealer.class, "items", "getItems()Lsu/catlean/s4;", 0), new PropertyReference1Impl(ChestStealer.class, "minDelayMs", "getMinDelayMs()I", 0), new PropertyReference1Impl(ChestStealer.class, "maxDelayMs", "getMaxDelayMs()I", 0), new PropertyReference1Impl(ChestStealer.class, "minDelayTicks", "getMinDelayTicks()I", 0), new PropertyReference1Impl(ChestStealer.class, "maxDelayTicks", "getMaxDelayTicks()I", 0), new PropertyReference1Impl(ChestStealer.class, "randomizeSlots", "getRandomizeSlots()Z", 0), new PropertyReference1Impl(ChestStealer.class, "close", "getClose()Z", 0), new PropertyReference1Impl(ChestStealer.class, "autoOpen", "getAutoOpen()Z", 0), new PropertyReference1Impl(ChestStealer.class, "rotate", "getRotate()Z", 0) };
   @NotNull
   public static final ChestStealer INSTANCE = new ChestStealer();
   @NotNull
   private static final EnumSetting action = SettingFactory.defaultImpl(INSTANCE, "action", ChestStealer.Action.FAST, null, null, 12, null);
   @NotNull
   private static final EnumSetting sort = SettingFactory.defaultImpl(INSTANCE, "sort", ChestStealer.Sort.NONE, null, null, 12, null);
   @NotNull
   private static final ItemsSetting items = SettingFactory.defaultImpl(
      INSTANCE, "items", new ItemsValue(null, 1, null), null, () -> INSTANCE.getSort() != ChestStealer.Sort.NONE, 4, null
   );
   @NotNull
   private static final IntSetting minDelayMs = SettingFactory.defaultImpl(
      INSTANCE, "min-delay-ms", 80, new IntRange(0, 500), null, () -> INSTANCE.getAction() == ChestStealer.Action.FAST, 8, null
   );
   @NotNull
   private static final IntSetting maxDelayMs = SettingFactory.defaultImpl(
      INSTANCE, "max-delay-ms", 80, new IntRange(0, 500), null, () -> INSTANCE.getAction() == ChestStealer.Action.FAST, 8, null
   );
   @NotNull
   private static final IntSetting minDelayTicks = SettingFactory.defaultImpl(
      INSTANCE, "min-delay-ticks", 0, new IntRange(0, 30), null, () -> INSTANCE.getAction() == ChestStealer.Action.ON_TICK, 8, null
   );
   @NotNull
   private static final IntSetting maxDelayTicks = SettingFactory.defaultImpl(
      INSTANCE, "max-delay-ticks", 1, new IntRange(0, 30), null, () -> INSTANCE.getAction() == ChestStealer.Action.ON_TICK, 8, null
   );
   @NotNull
   private static final BooleanSetting randomizeSlots = SettingFactory.defaultImpl(INSTANCE, "randomize-slots", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting closeIfEmpty = SettingFactory.defaultImpl(INSTANCE, "close-if-empty", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting autoOpen = SettingFactory.defaultImpl(INSTANCE, "auto-open", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rotate = SettingFactory.defaultImpl(INSTANCE, "rotate", false, null, () -> INSTANCE.getAutoOpen(), 4, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch3 = new StopWatch();
   private static int value;
   private static int value2;

   private ChestStealer() {
      super("chest-stealer", SubCategories.getSubCategory9(), null, 4, null);
   }

   private final ChestStealer.Action getAction() {
      return (ChestStealer.Action)action.computeObject(this, $$delegatedProperties[0]);
   }

   private final ChestStealer.Sort getSort() {
      return (ChestStealer.Sort)sort.computeObject(this, $$delegatedProperties[1]);
   }

   private final ItemsValue getItems() {
      return (ItemsValue)items.computeObject(this, $$delegatedProperties[2]);
   }

   private final int getMinDelayMs() {
      return ((Number)minDelayMs.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   private final int getMaxDelayMs() {
      return ((Number)maxDelayMs.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final int getMinDelayTicks() {
      return ((Number)minDelayTicks.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final int getMaxDelayTicks() {
      return ((Number)maxDelayTicks.computeObject(this, $$delegatedProperties[6])).intValue();
   }

   private final boolean getRandomizeSlots() {
      return (Boolean)randomizeSlots.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getClose() {
      return (Boolean)closeIfEmpty.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getAutoOpen() {
      return (Boolean)autoOpen.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getRotate() {
      return (Boolean)rotate.computeObject(this, $$delegatedProperties[10]);
   }

   @Flow
   private final void onPostTasksProcess(PostTasksProcessEvent postTasksProcessEvent) {
      if (this.getAction() == ChestStealer.Action.FAST) {
         this.applyCloseContainer(false);
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getAutoOpen() && MinecraftHolder.getMinecraft().gui.screen() == null && stopWatch.testValue(3000)) {
         for (BlockEntity item : (Iterable<BlockEntity>)ServerInfoService.INSTANCE.getItems()) {
            if (item instanceof EnderChestBlockEntity) {
               LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
               BlockPos blockPos = ((EnderChestBlockEntity)item).getBlockPos();
               if (!(localPlayer.distanceToSqr(Vec3iUtil.computeVec3(blockPos)) > 39.0)) {
                  double amount = Render2D.INSTANCE.computeAmount(-0.4, 0.4, Random.Default.nextFloat());
                  blockPos = ((EnderChestBlockEntity)item).getBlockPos();
                  Vec3 vec3 = Vec3iUtil.computeVec3(blockPos).add(amount, 0.375, -amount);
                  if (this.getRotate()) {
                     RotationManager.defaultImpl(RotationManager.INSTANCE, Rotation.vectorUtil.computeRotation(vec3), 0, 2, null);
                  }

                  MinecraftHolder.getMultiPlayerGameMode()
                     .useItemOn(
                        MinecraftHolder.getLocalPlayer(),
                        InteractionHand.MAIN_HAND,
                        new BlockHitResult(vec3, Direction.UP, ((EnderChestBlockEntity)item).getBlockPos(), false)
                     );
                  MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                  break;
               }
            }
         }
      }

      if (this.getAction() == ChestStealer.Action.ON_TICK) {
         this.applyCloseContainer(true);
      }
   }

   private final void applyCloseContainer(boolean flag) {
      GuiMove.INSTANCE.setFlag5(true);
      if (MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().level != null
         && MinecraftHolder.getLocalPlayer().containerMenu instanceof ChestMenu) {
         ChestMenu containerMenu = (ChestMenu)MinecraftHolder.getLocalPlayer().containerMenu;
         IntRange until = RangesKt.until(0, containerMenu.getContainer().getContainerSize());
         Iterator iterator = (this.getRandomizeSlots() ? CollectionsKt.shuffled((Iterable)until) : (Iterable)until).iterator();

         while (iterator.hasNext()) {
            int value3 = ((Number)iterator.next()).intValue();
            Slot slot = containerMenu.getSlot(value3);
            String string = MinecraftHolder.getMinecraft().gui.screen().getTitle().getString();
            if (slot.hasItem() && this.checkContains(slot.getItem())) {
               int value4 = flag ? value : value2;
               if (stopWatch2.testValue2(value4)
                  && !KotlinCompat.contains(string, "РђСѓРєС†РёРѕРЅ", false, 2, null)
                  && !KotlinCompat.contains(string, "РїРѕРєСѓРїРєРё", false, 2, null)
                  && !KotlinCompat.contains(string, "Р’Р«Р‘РћР ", false, 2, null)) {
                  Object object = null;
                  byte byteValue = 8;
                  boolean flag2 = false;
                  ContainerInput quicK_MOVE = ContainerInput.QUICK_MOVE;
                  byte byteValue2 = 0;
                  ContainerUtil.defaultImpl(value3, byteValue2, quicK_MOVE, flag2, byteValue, object);
                  stopWatch.update();
                  this.update();
               }
            }
         }

         if (this.checkGetContainer(containerMenu)
            && this.getClose()
            && stopWatch3.testValue(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) + 100)) {
            MinecraftHolder.getLocalPlayer().closeContainer();
         }
      } else {
         stopWatch3.update();
      }

      GuiMove.INSTANCE.setFlag5(false);
   }

   private final void update() {
      value2 = MathUtil.defaultImpl3((ClosedRange)(new IntRange(this.getMinDelayMs(), this.getMaxDelayMs())), false, 2, null);
      value = MathUtil.defaultImpl3((ClosedRange)(new IntRange(this.getMinDelayTicks(), this.getMaxDelayTicks())), false, 2, null);
   }

   private final boolean checkContains(ItemStack itemStack) {
      return switch (ChestStealer.WhenMappings.values[this.getSort().ordinal()]) {
         case 1 -> true;
         case 2 -> this.getItems().getItems().contains(itemStack.getItem());
         default -> !this.getItems().getItems().contains(itemStack.getItem());
      };
   }

   private final boolean checkGetContainer(ChestMenu chestMenu) {
      int value3 = 0;

      for (int i = chestMenu.getContainer().getContainerSize() == 90 ? 54 : 27; value3 < i; value3++) {
         if (chestMenu.getSlot(value3).hasItem()) {
            return false;
         }
      }

      return true;
   }

   static {
   }

   public static enum Action {
      ON_TICK,
      FAST;

      private static final ChestStealer.Action[] actions = $values();
      private static final EnumEntries<ChestStealer.Action> enumEntries = EnumEntriesKt.enumEntries(actions);

      private Action() {
      }

      @NotNull
      public static EnumEntries<ChestStealer.Action> getEnumEntries() {
         return enumEntries;
      }

      private static final ChestStealer.Action[] $values() {
         return new ChestStealer.Action[]{ON_TICK, FAST};
      }
   }

   public static enum Sort {
      NONE,
      WHITE_LIST,
      BLACK_LIST;

      private static final ChestStealer.Sort[] sorts = $values();
      private static final EnumEntries<ChestStealer.Sort> enumEntries = EnumEntriesKt.enumEntries(sorts);

      private Sort() {
      }

      @NotNull
      public static EnumEntries<ChestStealer.Sort> getEnumEntries() {
         return enumEntries;
      }

      private static final ChestStealer.Sort[] $values() {
         return new ChestStealer.Sort[]{NONE, WHITE_LIST, BLACK_LIST};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[ChestStealer.Sort.values().length];

         try {
            values2[ChestStealer.Sort.NONE.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[ChestStealer.Sort.WHITE_LIST.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}