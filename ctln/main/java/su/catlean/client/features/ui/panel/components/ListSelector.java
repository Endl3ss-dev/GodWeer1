// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.InventorySnapshot;
import su.catlean.api.data.Rectangle;
import su.catlean.client.features.modules.equip.InventoryManager;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.features.ui.clickgui.popup.PopupWindow;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;

public final class ListSelector extends PopupWindow {
   @NotNull
   private final List list;
   @NotNull
   private final Function1 clickAction;
   @NotNull
   private final Function1 clickRemoveAction;
   @Nullable
   private String text;
   private boolean flag;
   @NotNull
   private String text2;
   private boolean flag2;
   private boolean flag3;

   public ListSelector(List list, Function1 clickAction, Function1 clickRemoveAction) {
      this.list = list;
      this.clickAction = clickAction;
      this.clickRemoveAction = clickRemoveAction;
      this.text2 = "";
   }

   @NotNull
   public final List getList() {
      return this.list;
   }

   @NotNull
   public final Function1 getClickAction() {
      return this.clickAction;
   }

   @NotNull
   public final Function1 getClickRemoveAction() {
      return this.clickRemoveAction;
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2) {
      boolean var75 = false;
      boolean var73 = false;
      float guiScaledWidth;
      float guiScaledHeight;
      boolean flag22;
      label145: {
         guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - this.getAmount() / 2.0F;
         guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - this.getAmount2() / 2.0F - 50.0F;
         float amount = guiScaledWidth + this.getAmount() - 25.0F;
         float amount2 = guiScaledWidth + this.getAmount() - 5;
         float value3 = value;
         if (amount <= value3 ? value3 <= amount2 : false) {
            amount = guiScaledHeight + 28.0F;
            amount2 = guiScaledHeight + 48.0F;
            value3 = value2;
            if (amount <= value3 ? value3 <= amount2 : false) {
               flag22 = true;
               break label145;
            }
         }

         flag22 = false;
      }

      this.flag2 = flag22;
      Fonts.getFontRenderer9().render2(context, "Select", guiScaledWidth + this.getAmount() / 2.0F, guiScaledHeight + 3, 10);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         guiScaledWidth + 5.0F,
         guiScaledHeight + 28.0F,
         this.getAmount() - 33.0F,
         20.0F,
         4.0F,
         0.0F,
         0.1F,
         1.0F,
         ThemeManager.INSTANCE.getColor4().brighter(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      TexturedQuadPass.defaultImpl(
         context.pose(),
         guiScaledWidth + this.getAmount() - 24.5F,
         guiScaledHeight + 28.0F,
         20.5F,
         20.0F,
         4.0F,
         0.0F,
         0.1F,
         1.0F,
         ThemeManager.INSTANCE.getColor4().brighter(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Fonts.getFontRenderer9()
         .render2(
            context, "+", guiScaledWidth + this.getAmount() - 15.0F, guiScaledHeight + 35.5F, this.flag2 ? ThemeManager.INSTANCE.getColor7() : ThemeManager.INSTANCE.getColor8()
         );
      FontRenderer fontRenderer9 = Fonts.getFontRenderer9();
      String text22 = this.flag ? this.text2 + (MinecraftHolder.getTime() % 700 > 350L ? "_" : "") : "Enter name";
      float value4 = guiScaledWidth + 7.0F;
      float value5 = guiScaledHeight + 29;
      Color color7 = this.flag ? ThemeManager.INSTANCE.getColor7() : ThemeManager.INSTANCE.getColor8();
      float value6 = value5;
      float value7 = value4;
      String text3 = text22;
      fontRenderer9.render(context, text3, value7, value6, color7);
      TexturedQuadPass.defaultImpl(
         context.pose(),
         guiScaledWidth + 4.0F,
         guiScaledHeight + 53.0F,
         this.getAmount() - 8.0F,
         this.getAmount2() - 57.0F,
         4.0F,
         2.0F,
         0.1F,
         1.0F,
         ThemeManager.INSTANCE.getColor4().brighter(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      this.text = null;
      this.flag3 = false;
      Render2D instance = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(guiScaledWidth, guiScaledHeight + 55.0F, guiScaledWidth + this.getAmount(), guiScaledHeight + this.getAmount2() - 6.0F);
      instance.render(context, rectangle);
      Iterable list2 = this.list;
      int value8 = 0;
      Iterator iterator = CollectionsKt.sortedWith(list2, new ListSelector.Ordering()).iterator();
      int value9 = 0;

      while (iterator.hasNext()) {
         value8 = value9++;
         String text4 = (String)iterator.next();
         float amount3 = guiScaledHeight + 58.0F + value8 * 18.0F + this.getAmount3();
         if (!(amount3 < guiScaledHeight + 40) && !(amount3 > guiScaledHeight + this.getAmount2())) {
            label133: {
               float amount4 = guiScaledWidth + this.getAmount();
               float value10 = value;
               if ((guiScaledWidth <= value10 ? value10 <= amount4 : false) && value2 > guiScaledHeight + 58.0F) {
                  amount4 = amount3 + 17.0F;
                  value10 = value2;
                  if (amount3 <= value10 ? value10 <= amount4 : false) {
                     var73 = true;
                     break label133;
                  }
               }

               var73 = false;
            }

            boolean flag4 = var73;
            if (flag4) {
               this.text = text4;
               TexturedQuadPass.defaultImpl(
                  context.pose(),
                  guiScaledWidth + 6.0F,
                  amount3 - 2.0F,
                  this.getAmount() - 12.0F,
                  20.0F,
                  4.0F,
                  2.0F,
                  0.1F,
                  1.0F,
                  ThemeManager.INSTANCE.getColor4(),
                  ThemeManager.INSTANCE.getColor6(),
                  0.0F,
                  0.0F,
                  0.0F,
                  14336,
                  null
               );
            }

            label123: {
               fontRenderer9 = Fonts.getFontRenderer2();
               text22 = text4.length() > 28 ? text4.substring(0, 28) + "..." : text4;
               value4 = guiScaledWidth + 10.0F;
               value5 = amount3 + 1;
               color7 = Intrinsics.areEqual(text4, ProxyManager.INSTANCE.getInventoryDataSource().getInventorySnapshot().getId())
                  ? ThemeManager.INSTANCE.getColor2()
                  : ThemeManager.INSTANCE.getColor8();
               value6 = value5;
               value7 = value4;
               text3 = text22;
               fontRenderer9.render(context, text3, value7, value6, color7);
               float amount5 = guiScaledWidth + this.getAmount() - 20.0F;
               float amount6 = guiScaledWidth + this.getAmount() - 10.0F;
               float value11 = value;
               if (amount5 <= value11 ? value11 <= amount6 : false) {
                  amount5 = amount3 + 3.0F;
                  amount6 = amount3 + 13.0F;
                  value11 = value2;
                  if (amount5 <= value11 ? value11 <= amount6 : false) {
                     var75 = true;
                     break label123;
                  }
               }

               var75 = false;
            }

            boolean flag5 = var75;
            Color color = flag5 ? Render2D.INSTANCE.computeColor(Color.RED, 0.7F) : Render2D.INSTANCE.computeColor(Color.RED, 0.5F);
            float amount7 = guiScaledWidth + this.getAmount() - 20.0F;
            float value12 = amount3 + 3.0F;
            Matrix3x2fStack pose = context.pose();
            float value13 = flag5 ? 2.0F : 1.0F;
            Color red = flag5 ? Color.RED : Render2D.INSTANCE.computeColor(Color.RED, 0.5F);
            value6 = value13;
            BoxPass.render(amount7, value12, 10.0F, 10.0F, 3.0F, color, color, color, color, pose, value6, red);
            if (!this.flag3 && flag5) {
               this.flag3 = true;
            }
         }
      }

      Render2D.INSTANCE.render(context);
   }

   @Override
   public void applyEntrySet(int value, int value2, int value3) {
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - this.getAmount() / 2.0F;
      float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F - this.getAmount2() / 2.0F - 50.0F;
      if (this.flag3 && this.text != null) {
         this.clickRemoveAction.invoke(this.text);
      } else {
         if (this.flag2) {
            this.applyContains();
         }

         float value4 = guiScaledHeight + 28.0F;
         float amount = guiScaledHeight + 48.0F;
         float value22 = value2;
         if (value4 <= value22 ? value22 <= amount : false) {
            value4 = guiScaledWidth + 4.0F;
            amount = guiScaledWidth + this.getAmount() - 33.0F;
            value22 = value;
            if (value4 <= value22 ? value22 <= amount : false) {
               this.flag = !this.flag;
            }
         }

         if (this.text != null) {
            String text3 = this.text;
            this.clickAction.invoke(this.text);
         }

         super.applyEntrySet(value, value2, value3);
      }
   }

   @Override
   public void updateValue(int value) {
      switch (value) {
         case 256:
            this.flag = false;
            break;
         case 257:
            this.applyContains();
         case 258:
         default:
            break;
         case 259:
            this.text2 = StringsKt.dropLast(this.text2, 1);
            if (this.text2.length() == 0) {
               this.flag = false;
            }
      }

      super.updateValue(value);
   }

   private final void applyContains() {
      if (!StringsKt.isBlank(this.text2)) {
         Iterable items = ProxyManager.INSTANCE.getInventoryDataSource().getItems();
         var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items, 10));

         for (Object object : items) {
            InventorySnapshot inventorySnapshot = (InventorySnapshot)object;
            arrayList.add(inventorySnapshot.getId());
         }

         if (!arrayList.contains(this.text2)) {
            InventorySnapshot item = InventoryManager.INSTANCE.fetchItem(this.text2);
            ProxyManager.INSTANCE.getInventoryDataSource().getItems().add(item);
            ProxyManager.INSTANCE.getInventoryDataSource().updateInventorySnapshot(item);
            this.text2 = "";
            this.flag = false;
            this.setFlag(true);
            ClickGuiScreen.INSTANCE.setPopupWindow(new KitSelector());
         }
      }
   }

   @Override
   public void updateSymbol(char symbol) {
      if (this.flag && this.text2.length() < 20) {
         this.text2 = this.text2 + symbol;
         this.setAmount3(0.0F);
      }
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         String object3 = (String)object;
         Comparable<String> comparable = object3;
         object3 = (String)object2;
         Comparable<String> comparable2 = comparable;
         return ComparisonsKt.compareValues(comparable2, object3);
      }
   }
}