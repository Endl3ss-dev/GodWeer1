// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.InventorySnapshot;
import su.catlean.client.features.ui.clickgui.popup.PopupWindow;
import su.catlean.client.features.ui.clickgui.settings.ItemPicker;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.client.services.sources.InventoryDataSource;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;

public final class KitSelector extends PopupWindow {
   @NotNull
   public static final KitSelector.Companion companion = new KitSelector.Companion(null);
   @NotNull
   private final Map<Object, Item> map = new LinkedHashMap();
   @NotNull
   private final Button button = new Button(0.0F, 0.0F, 40.0F, 15.0F, "", () -> {
      Iterable items = ProxyManager.INSTANCE.getInventoryDataSource().getItems();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

      for (Object object : items) {
         InventorySnapshot inventorySnapshot = (InventorySnapshot)object;
         arrayList.add(inventorySnapshot.getId());
      }

      List list = arrayList;
      Function1 function1 = arg -> {
         this.applyRemoveIf((String)arg);
         return Unit.INSTANCE;
      };
      Function1 function12 = arg2 -> {
         this.updateText((String)arg2);
         popupWindow.setFlag(true);
         return Unit.INSTANCE;
      };
      List list2 = list;
      popupWindow = new ListSelector(list2, function12, function1);
      return Unit.INSTANCE;
   }, 0, ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor13(), 0.0F, 0.0F, 1600, null);
   @Nullable
   private static PopupWindow popupWindow;

   public KitSelector() {
      this.applyClear();
   }

   private final void updateText(String text) {
      Iterable items = ProxyManager.INSTANCE.getInventoryDataSource().getItems();
      InventoryDataSource inventoryDataSource = ProxyManager.INSTANCE.getInventoryDataSource();
      Iterator iterator = items.iterator();

      Object object;
      while (true) {
         if (iterator.hasNext()) {
            Object object2 = iterator.next();
            InventorySnapshot inventorySnapshot = (InventorySnapshot)object2;
            if (!Intrinsics.areEqual(inventorySnapshot.getId(), text)) {
               continue;
            }

            object = object2;
            break;
         }

         object = null;
         break;
      }

      InventorySnapshot inventorySnapshot2 = (InventorySnapshot)object;
      if ((InventorySnapshot)object != null) {
         inventoryDataSource.updateInventorySnapshot(inventorySnapshot2);
         this.applyClear();
      }
   }

   private final void applyRemoveIf(String text) {
      ProxyManager.INSTANCE.getInventoryDataSource().getItems().removeIf(arg -> Intrinsics.areEqual(((InventorySnapshot)arg).getId(), text));
      this.applyClear();
   }

   public final void applyClear() {
      this.setAmount(188.0F);
      this.setAmount2(119.0F);
      this.map.clear();
      int value = 0;

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 9; j++) {
            int value2 = value;
            Item air = ProxyManager.INSTANCE.getInventoryDataSource().getInventorySnapshot().getItems().get(value) == null
               ? Items.AIR
               : (Item)ProxyManager.INSTANCE.getInventoryDataSource().getInventorySnapshot().getItems().get(value);
            this.map.put(new Button(j * 20.0F, i * 20.0F, 18.0F, 18.0F, "", () -> {
               Item[] items = new Item[]{air};
               popupWindow = new ItemPicker(CollectionsKt.mutableListOf(items), arg -> {
                  this.applyGetKey(value2, (Item)arg);
                  popupWindow.setFlag(true);
                  return Unit.INSTANCE;
               });
               return Unit.INSTANCE;
            }, value, ThemeManager.INSTANCE.getColor10(), ThemeManager.INSTANCE.getColor13(), 0.0F, 0.0F, 1536, null), air);
            value++;
         }
      }
   }

   private final void applyGetKey(int value, Item item) {
      Map<Object, Item> hashMap = Maps.newHashMap(this.map);

      for (Map.Entry<Object, Item> entrySetItem : hashMap.entrySet()) {
         if (((Button)entrySetItem.getKey()).getValue() == value) {
            this.map.put(entrySetItem.getKey(), item);
         }
      }

      ProxyManager.INSTANCE.getInventoryDataSource().getInventorySnapshot().getItems().put(value, item);
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2) {
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - this.getAmount() / 2.0F;
      float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - this.getAmount2() / 2.0F - 50.0F;
      Render2D instance = Render2D.INSTANCE;
      Matrix3x2fStack pose = context.pose();
      float value3 = guiScaledWidth + 6;
      float value4 = guiScaledHeight + 91.5F;
      float amount = this.getAmount() - 12;
      Color[] colors = new Color[]{new Color(1157627903, true)};
      float value5 = 0.5F;
      float value6 = amount;
      float value7 = value4;
      float value8 = value3;
      Matrix3x2fStack matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value8, value7, value6, value5, colors);
      this.button.render(guiScaledWidth + 6, guiScaledHeight + 5.0F, value, value2, context);
      Fonts.getFontRenderer2().render2(context, "select", guiScaledWidth + 25.0F, guiScaledHeight + 11.0F, ThemeManager.INSTANCE.getColor8());
      Fonts.getFontRenderer2()
         .render2(
            context,
            "Current kit: " + ProxyManager.INSTANCE.getInventoryDataSource().getInventorySnapshot().getId(),
            guiScaledWidth + this.getAmount() / 2.0F,
            guiScaledHeight + 11.0F,
            ThemeManager.INSTANCE.getColor8()
         );
      int value9 = 0;
      Map<Object, Item> map2 = this.map;

      for (Map.Entry<Object, Item> entrySetItem : map2.entrySet()) {
         int value10 = (0 <= value9 ? value9 < 9 : false) ? 0 : ((9 <= value9 ? value9 < 18 : false) ? 20 : ((18 <= value9 ? value9 < 27 : false) ? 40 : 65));
         ((Button)entrySetItem.getKey()).render(value9 % 9 * 20.0F + guiScaledWidth + 5.0F, value10 + guiScaledHeight + 30.0F, value, value2, context);
         if (popupWindow == null) {
            context.pose().pushMatrix();
            context.pose().translate(value9 % 9 * 20.0F + guiScaledWidth + 6.0F, value10 + guiScaledHeight + 31.0F);
            context.item(((Item)entrySetItem.getValue()).asItem().getDefaultInstance(), 0, 0);
            context.pose().popMatrix();
         }

         value9++;
      }

      if (popupWindow != null) {
         popupWindow.render2(context, value, value2);
      }
   }

   @Override
   public void applyEntrySet(int value, int value2, int value3) {
      super.applyEntrySet(value, value2, value3);
      if (popupWindow != null) {
         popupWindow.applyEntrySet(value, value2, value3);
      } else {
         Map<Object, Item> map2 = this.map;

         for (Map.Entry<Object, Item> entrySetItem : map2.entrySet()) {
            ((Button)entrySetItem.getKey()).updateAmount(value, value2);
         }

         this.button.updateAmount(value, value2);
      }
   }

   @Override
   public void updateValue(int value) {
      super.updateValue(value);
      if (popupWindow != null) {
         popupWindow.updateValue(value);
      }
   }

   @Override
   public void updateSymbol(char symbol) {
      super.updateSymbol(symbol);
      if (popupWindow != null) {
         popupWindow.updateSymbol(symbol);
      }
   }

   @Override
   public void updateAmount(double amount, double amount2, double amount3) {
      super.updateAmount(amount, amount2, amount3);
      if (popupWindow != null) {
         popupWindow.updateAmount(amount, amount2, amount3);
      }
   }

   public static final PopupWindow getPopupWindow() {
      return popupWindow;
   }

   public static final void setPopupWindow(PopupWindow popupWindow) {
      KitSelector.popupWindow = popupWindow;
   }

   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final PopupWindow getPopupWindow() {
         return KitSelector.getPopupWindow();
      }

      public final void updatePopupWindow(PopupWindow popupWindow) {
         KitSelector.setPopupWindow(popupWindow);
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}