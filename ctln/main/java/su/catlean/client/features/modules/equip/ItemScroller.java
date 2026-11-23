// deobf by pusia
package su.catlean.client.features.modules.equip;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.random.Random;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.player.ClickSlotEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.MouseHandlerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.ContainerUtil;

public final class ItemScroller extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(ItemScroller.class, "delay", "getDelay()I", 0)};
   @NotNull
   public static final ItemScroller INSTANCE = new ItemScroller();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 75, new IntRange(0, 200), null, null, 24, null);
   private static boolean flag;
   private static boolean flag2;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private ItemScroller() {
      super("item-scroller", SubCategories.getSubCategory9(), null, 4, null);
   }

   public final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   @Flow
   private final void onClickSlot(ClickSlotEvent clickSlotEvent) {
      if ((InputUtil.checkContains(340) || InputUtil.checkContains(344))
         && (InputUtil.checkContains(341) || InputUtil.checkContains(345))
         && clickSlotEvent.getAction() == ContainerInput.THROW
         && !flag2) {
         Item item = ((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(clickSlotEvent.getId())).getItem().getItem();
         flag2 = true;
         int value = 0;

         for (int i = MinecraftHolder.getLocalPlayer().containerMenu.slots.size(); value < i; value++) {
            if (Intrinsics.areEqual(((Slot)MinecraftHolder.getLocalPlayer().containerMenu.slots.get(value)).getItem().getItem(), item)) {
               boolean flag3 = false;
               ContainerInput throwValue = ContainerInput.THROW;
               byte byteValue = 1;
               ContainerUtil.applyHandleContainerInput(value, byteValue, throwValue, flag3);
            }
         }

         flag2 = false;
      }

      if ((InputUtil.checkContains(342) || InputUtil.checkContains(346))
         && (InputUtil.checkContains(341) || InputUtil.checkContains(345))
         && clickSlotEvent.getAction() == ContainerInput.THROW
         && !flag2) {
         flag2 = true;
         int value2 = 0;

         for (int i2 = MinecraftHolder.getLocalPlayer().containerMenu.slots.size(); value2 < i2; value2++) {
            ContainerUtil.applyHandleContainerInput(value2, 1, ContainerInput.THROW, false);
         }

         flag2 = false;
      }
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if (inputEvent.getDevice() == InputEvent.Device.Mouse && inputEvent.getKey() == 0) {
         flag = inputEvent.getAction() == InputEvent.Action.Press;
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (MinecraftHolder.getMinecraft().gui.screen() != null
         && flag
         && InputUtil.checkContains(340)
         && stopWatch.testValue(this.getDelay() + (int)(Random.Default.nextDouble() * 10))) {
         ((MouseHandlerAccessor)MinecraftHolder.getMinecraft().mouseHandler)
            .iMouseClick(MinecraftHolder.getMinecraft().getWindow().handle(), new MouseButtonInfo(0, 0), 0);
         ((MouseHandlerAccessor)MinecraftHolder.getMinecraft().mouseHandler)
            .iMouseClick(MinecraftHolder.getMinecraft().getWindow().handle(), new MouseButtonInfo(0, 1), 1);
         stopWatch.update();
      }
   }

   static {
   }
}