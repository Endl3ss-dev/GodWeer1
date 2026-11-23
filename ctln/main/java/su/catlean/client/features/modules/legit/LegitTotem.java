// deobf by pusia
package su.catlean.client.features.modules.legit;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.random.Random;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.WidgetManager;
import su.catlean.gofra.Flow;
import su.catlean.interfaces.IMouse;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.world.WorldUtil;

public final class LegitTotem extends Module {
   @NotNull
   public static final LegitTotem INSTANCE = new LegitTotem();
   private static int value;
   private static int value2;

   private LegitTotem() {
      super("legit-totem", SubCategories.getSubCategory3(), null, 4, null);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      boolean areEqual = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.TOTEM_OF_UNDYING);
      SearchInvResult searchInvResult = this.getSearchInvResult();
      if (searchInvResult.isFlag()) {
         if (value > 0) {
            int value3 = value++;
         }

         if (!areEqual && value == 0) {
            value2 = MathUtil.defaultImpl(2, 4, false, 4, null);
            int value4 = value++;
         }

         if (MinecraftHolder.getMinecraft().gui.screen() != null && !areEqual) {
            float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - 80.0F;
            float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F;
            int value5 = searchInvResult.getValue() % 9;
            int value6 = searchInvResult.getValue();
            byte byteValue = 9;
            int value7 = value6 / byteValue;
            if ((value6 ^ byteValue) < 0 && value7 * byteValue != value6) {
               value7--;
            }

            int value8 = value7 - 1;
            float value9 = guiScaledWidth + value5 * 20;
            float value10 = guiScaledHeight + value8 * 20;
            double amount = WidgetManager.INSTANCE.getAmount() + (value9 - WidgetManager.INSTANCE.getAmount()) * 0.6;
            double amount2 = WidgetManager.INSTANCE.getAmount2() + (value10 - WidgetManager.INSTANCE.getAmount2()) * 0.6;
            int guiScale = MinecraftHolder.getMinecraft().getWindow().getGuiScale();
            InputConstants.grabOrReleaseMouse(MinecraftHolder.getMinecraft().getWindow(), 212993, amount * guiScale, amount2 * guiScale);
            ((IMouse)MinecraftHolder.getMinecraft().mouseHandler).setPosition((int)(amount * guiScale), (int)(amount2 * guiScale));
         }

         if (value >= value2) {
            if (MinecraftHolder.getMinecraft().gui.screen() == null) {
               MinecraftHolder.getMinecraft().getTutorial().onOpenInventory();
               MinecraftHolder.getMinecraft().gui.setScreen((Screen)(new InventoryScreen((Player)MinecraftHolder.getLocalPlayer())));
               value2 = value2 + MathUtil.defaultImpl(2, 4, false, 4, null);
               WorldUtil.INSTANCE.applySetDown();
            } else {
               ContainerUtil.defaultImpl(searchInvResult.getValue(), 40, ContainerInput.SWAP, false, 8, null);
               TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, MathUtil.defaultImpl(2, 4, false, 4, null), (byte)0, false, () -> {
                  MinecraftHolder.getLocalPlayer().clientSideCloseContainer();
                  MinecraftHolder.getLocalPlayer().closeContainer();
                  MinecraftHolder.getMinecraft().gui.setScreen(null);
                  WorldUtil.INSTANCE.applyGetKey();
               }, 6, null);
               value = 0;
            }
         }
      }
   }

   private final SearchInvResult getSearchInvResult() {
      List<SearchInvResult> arrayList = new ArrayList();

      for (int i = 36; -1 < i; i--) {
         ItemStack item;
         label40: {
            LocalPlayer player = MinecraftHolder.getMinecraft().player;
            if (player != null) {
               Inventory inventory = player.getInventory();
               if (inventory != null) {
                  item = inventory.getItem(i);
                  break label40;
               }
            }

            item = null;
         }

         ItemStack itemStack = item;
         if (Intrinsics.areEqual(itemStack != null ? itemStack.getItem() : null, Items.TOTEM_OF_UNDYING)) {
            arrayList.add(new SearchInvResult(i < 9 ? i + 36 : i, true, itemStack));
         }
      }

      return !arrayList.isEmpty()
         ? (SearchInvResult)CollectionsKt.random(arrayList, (Random)Random.Default)
         : SearchInvResult.searchInvResultCompanion.getSearchInvResult();
   }
}