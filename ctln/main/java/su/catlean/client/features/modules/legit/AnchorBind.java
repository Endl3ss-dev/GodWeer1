// deobf by pusia
package su.catlean.client.features.modules.legit;

import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.MinecraftAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.player.PlayerUtil;

public final class AnchorBind extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AnchorBind.class, "anchorDelay", "getAnchorDelay()I", 0), new PropertyReference1Impl(AnchorBind.class, "randomizeDelay", "getRandomizeDelay()Z", 0), new PropertyReference1Impl(AnchorBind.class, "anchorBind", "getAnchorBind()Lsu/catlean/dd;", 0) };
   @NotNull
   public static final AnchorBind INSTANCE = new AnchorBind();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 1, new IntRange(0, 5), null, null, 24, null);
   @NotNull
   private static final BooleanSetting randomizeDelay = SettingFactory.defaultImpl(INSTANCE, "randomize-delay", false, null, null, 12, null);
   @NotNull
   private static final BindSetting anchorBind = SettingFactory.defaultImpl(INSTANCE, "anchor-bind", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static PlayerUtil playerUtil = new PlayerUtil();

   private AnchorBind() {
      super("anchor-bind", SubCategories.getSubCategory3(), null, 4, null);
   }

   private final int getAnchorDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final boolean getRandomizeDelay() {
      return (Boolean)randomizeDelay.computeObject(this, $$delegatedProperties[1]);
   }

   private final Bind getAnchorBind() {
      return (Bind)anchorBind.computeObject(this, $$delegatedProperties[2]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (InputUtil.checkContains(this.getAnchorBind().getValue()) && playerUtil.testValue2(this.getAnchorDelay() > 0 ? this.getAnchorDelay() * 5 + 10 : 2)) {
         InventoryManager instance = InventoryManager.INSTANCE;
         Item[] items = new Item[]{Items.GLOWSTONE};
         int value = instance.computeSearchInvResult(items).getValue();
         boolean areEqual = Intrinsics.areEqual(
            MinecraftHolder.getLocalPlayer().getInventory().getItem(MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot()).getItem(),
            Items.GLOWSTONE
         );
         instance = InventoryManager.INSTANCE;
         Item[] items2 = new Item[]{Items.RESPAWN_ANCHOR};
         int value2 = instance.computeSearchInvResult(items2).getValue();
         int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
         boolean flag = MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult
            && Intrinsics.areEqual(
               MinecraftHolder.getClientLevel().getBlockState(((BlockHitResult)MinecraftHolder.getMinecraft().hitResult).getBlockPos()).getBlock(),
               Blocks.RESPAWN_ANCHOR
            );
         if (value == -1 || value2 == -1) {
            return;
         }

         if (flag) {
            int[] values = new int[]{value, selectedSlot};
            this.applyIntValue(values);
         } else {
            int[] values2 = new int[]{value2, value, areEqual ? value2 : selectedSlot};
            this.applyIntValue(values2);
         }
      }
   }

   private final void applyIntValue(int[] values) {
      Function0 function0 = null;
      function0 = () -> Unit.INSTANCE;

      for (Object reversedItem : ArraysKt.reversed(values)) {
         int value = ((Number)reversedItem).intValue();
         Function0 function02 = function0;
         function0 = () -> {
            InventoryManager.INSTANCE.applyGetInventory2(value);
            TaskScheduler.INSTANCE
               .applyAddLast(
                  INSTANCE.getAnchorDelay()
                     - 2
                     + (INSTANCE.getRandomizeDelay() ? MathUtil.defaultImpl3((ClosedRange)(new IntRange(-1, 1)), false, 2, null) : 0),
                  (byte)-10,
                  INSTANCE.getAnchorDelay() > 0,
                  () -> {
                     ((MinecraftAccessor)MinecraftHolder.getMinecraft()).idoItemUse();
                     TaskScheduler.INSTANCE
                        .applyAddLast(
                           INSTANCE.getAnchorDelay()
                              - 2
                              + (INSTANCE.getRandomizeDelay() ? MathUtil.defaultImpl3((ClosedRange)(new IntRange(-1, 1)), false, 2, null) : 0),
                           (byte)-10,
                           INSTANCE.getAnchorDelay() > 0,
                           () -> function02.invoke()
                        );
                  }
               );
            return Unit.INSTANCE;
         };
      }

      function0.invoke();
   }

   static {
   }
}