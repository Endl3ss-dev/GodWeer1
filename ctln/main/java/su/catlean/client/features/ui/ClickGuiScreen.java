// deobf by pusia
package su.catlean.client.features.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.category.Category;
import su.catlean.api.category.SubCategory;
import su.catlean.api.data.Selector;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PromoModule;
import su.catlean.client.features.modules.misc.ClientInterface;
import su.catlean.client.features.ui.clickgui.CategoryWindow;
import su.catlean.client.features.ui.clickgui.ModuleComponent;
import su.catlean.client.features.ui.clickgui.SearchBar;
import su.catlean.client.features.ui.clickgui.popup.PopupWindow;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.OnlineUsersService;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.ThreadManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.render.Render2D;

public final class ClickGuiScreen extends Screen {
   @NotNull
   public static final ClickGuiScreen INSTANCE = new ClickGuiScreen();
   private static boolean flag = true;
   @NotNull
   private static final List items;
   @Nullable
   private static PopupWindow popupWindow;
   @NotNull
   private static Animation animation;
   @NotNull
   private static Animation animation2;
   private static boolean flag2;
   private static boolean flag3;
   private static int value;
   private static int value2;

   private ClickGuiScreen() {
      super(Component.nullToEmpty("catlean-interface"));
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      ClickGuiScreen.flag = flag;
   }

   @Nullable
   public final PopupWindow getPopupWindow() {
      return popupWindow;
   }

   public final void setPopupWindow(PopupWindow popupWindow) {
      ClickGuiScreen.popupWindow = popupWindow;
   }

   public void extractRenderState(GuiGraphicsExtractor context, int value, int value2, float amount) {
      ClickGuiScreen.value = value;
      ClickGuiScreen.value2 = value2;
   }

   public final void render(GuiGraphicsExtractor context) {
      if (ClientInterface.INSTANCE.getHellHitPromo()) {
         for (Object items3Item : OnlineUsersService.INSTANCE.getItems3()) {
            PromoModule promoModule = (PromoModule)items3Item;

            for (Object item : items) {
               CategoryWindow categoryWindow = (CategoryWindow)item;
               categoryWindow.applyContains(promoModule);
            }
         }
      } else {
         for (Object item2 : items) {
            CategoryWindow categoryWindow2 = (CategoryWindow)item2;
            categoryWindow2.applyRemoveIf();
         }
      }

      if (!flag3 && !flag2) {
         animation.updateAmount(0.0F);
         flag3 = true;
      }

      animation.applyDoubleValue(0.1F);
      context.pose().pushMatrix();
      context.pose().translate(this.width / 2.0F, this.height / 2.0F);
      if (flag3) {
         context.pose().scale(1.1F - animation.getAmount());
      }

      if (flag2) {
         animation2.applyDoubleValue(1.0F);
         context.pose().scale(1.0F + animation2.getAmount());
         Render2D.INSTANCE.setAmount(1.0F - animation2.getAmount());
         if (animation2.getAmount() >= 0.999F) {
            flag2 = false;
            super.onClose();
         }
      } else {
         animation2.updateAmount(0.0F);
      }

      context.pose().translate(-this.width / 2.0F, -this.height / 2.0F);
      flag = true;

      for (Object item3 : items) {
         CategoryWindow categoryWindow3 = (CategoryWindow)item3;
         categoryWindow3.render(context, value, value2);
      }

      context.pose().popMatrix();
      SearchBar.INSTANCE.render(context, value, value2);
      if (popupWindow != null) {
         popupWindow.render2(context, value, value2);
      }

      Fonts.getFontRenderer()
         .render(
            context,
            "Drag and drop the configuration here to add\nCTRL + SHIFT + LMB - Reset hovered module\nLMB - enable module\nRMB - open settings\nMMB - bind module\nALT - show binds",
            5.0F,
            5.0F,
            Color.WHITE
         );
      Fonts.getFontRenderer()
         .render(
            context,
            "CatLean public beta (free)",
            MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() - 85.0F,
            MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() - 10.0F,
            Color.WHITE
         );
      Render2D.INSTANCE.setAmount(1.0F);
   }

   public void tick() {
      super.tick();
      if (popupWindow == null) {
         if (InputUtil.checkContains(263)) {
            for (Object item : items) {
               CategoryWindow categoryWindow = (CategoryWindow)item;
               categoryWindow.setValue4(categoryWindow.getValue4() - 10);
            }
         }

         if (InputUtil.checkContains(262)) {
            for (Object item2 : items) {
               CategoryWindow categoryWindow2 = (CategoryWindow)item2;
               categoryWindow2.setValue4(categoryWindow2.getValue4() + 10);
            }
         }

         if (InputUtil.checkContains(265)) {
            for (Object item3 : items) {
               CategoryWindow categoryWindow3 = (CategoryWindow)item3;
               categoryWindow3.setValue5(categoryWindow3.getValue5() - 10);
            }
         }

         if (InputUtil.checkContains(264)) {
            for (Object item4 : items) {
               CategoryWindow categoryWindow4 = (CategoryWindow)item4;
               categoryWindow4.setValue5(categoryWindow4.getValue5() + 10);
            }
         }
      }
   }

   private final void applyContains(SubCategory subCategory) {
      for (Object item : items) {
         CategoryWindow categoryWindow = (CategoryWindow)item;
         if (categoryWindow.getCategory().getSubCategories().contains(subCategory)) {
            for (Object item2 : categoryWindow.getItems()) {
               Selector selector = (Selector)item2;
               if (Intrinsics.areEqual(selector.getSubCategory(), subCategory)) {
                  categoryWindow.setModuleComponent(null);
                  SoundManager.INSTANCE.getSound5().applyExecuteIfPossible();
                  if (!Intrinsics.areEqual(categoryWindow.getSelector().getSubCategory(), subCategory)) {
                     categoryWindow.updateSelector(selector);
                  }
               }
            }
         }
      }
   }

   public final void updateModule(Module module) {
      for (Object item : items) {
         CategoryWindow categoryWindow = (CategoryWindow)item;

         for (Object items2Item : categoryWindow.getItems2()) {
            ModuleComponent moduleComponent = (ModuleComponent)items2Item;
            if (Intrinsics.areEqual(moduleComponent.getModule(), module)) {
               categoryWindow.setModuleComponent(moduleComponent);
            }
         }
      }
   }

   public final void applyExecute(Module module) {
      this.applyContains(module.getCategory());
      ThreadManager instance = ThreadManager.INSTANCE;
      short shortValue = 300;
      instance.getExecutorService().execute(new ClickGuiScreen.OpenTask(shortValue, module));
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean mouseClicked(MouseButtonEvent click, boolean flag) {
      double value3 = click.x();
      double value4 = click.y();
      int button = click.button();
      if (popupWindow != null) {
         popupWindow.applyEntrySet((int)value3, (int)value4, button);
         return false;
      } else {
         if (!SearchBar.INSTANCE.checkGetGuiScaledWidth(value3, value4, button)) {
            for (Object item : items) {
               CategoryWindow categoryWindow = (CategoryWindow)item;
               categoryWindow.applyBooleanValue(value3, value4, button);
            }
         }

         return super.mouseClicked(click, flag);
      }
   }

   public boolean mouseReleased(MouseButtonEvent click) {
      double value3 = click.x();
      double value4 = click.y();
      int button = click.button();

      for (Object item : items) {
         CategoryWindow categoryWindow = (CategoryWindow)item;
         categoryWindow.updateAmount(value3, value4, button);
      }

      SearchBar.INSTANCE.updateAmount(value3, value4, button);
      return super.mouseReleased(click);
   }

   public boolean keyPressed(KeyEvent input) {
      int key = input.key();
      int scancode = input.scancode();
      int modifiers = input.modifiers();
      if (popupWindow != null) {
         popupWindow.updateValue(key);
         return false;
      } else {
         for (Object item : items) {
            CategoryWindow categoryWindow = (CategoryWindow)item;
            categoryWindow.updateValue(key);
         }

         SearchBar.INSTANCE.updateValue(key, scancode, modifiers);
         return super.keyPressed(input);
      }
   }

   public boolean charTyped(CharacterEvent input) {
      Character firstOrNull = ArraysKt.firstOrNull(input.codepointAsString().toCharArray());
      if (firstOrNull == null) {
         return super.charTyped(input);
      } else {
         char ch = firstOrNull;
         if (popupWindow != null) {
            popupWindow.updateSymbol(ch);
            return super.charTyped(input);
         } else {
            for (Object item : items) {
               CategoryWindow categoryWindow = (CategoryWindow)item;
               categoryWindow.updateSymbol(ch);
            }

            SearchBar.INSTANCE.applyLength(ch);
            return super.charTyped(input);
         }
      }
   }

   public boolean mouseScrolled(double amount, double amount2, double amount3, double amount4) {
      if (popupWindow != null) {
         popupWindow.updateAmount(amount, amount2, amount4);
         return super.mouseScrolled(amount, amount2, amount3, amount4);
      } else if (SearchBar.INSTANCE.testAmount(amount, amount2, amount4)) {
         return super.mouseScrolled(amount, amount2, amount3, amount4);
      } else {
         boolean flag4 = false;

         for (Object item : items) {
            CategoryWindow categoryWindow = (CategoryWindow)item;
            if (categoryWindow.testAmount(amount, amount2, amount4, amount3)) {
               flag4 = true;
            }
         }

         if (!flag4) {
            for (Object item2 : items) {
               CategoryWindow categoryWindow2 = (CategoryWindow)item2;
               categoryWindow2.updateAmount(amount4, amount3);
            }
         }

         return super.mouseScrolled(amount, amount2, amount3, amount4);
      }
   }

   public void onClose() {
      for (Object item : items) {
         CategoryWindow categoryWindow = (CategoryWindow)item;
         categoryWindow.update();
      }

      flag = false;
      flag2 = true;
      flag3 = false;
   }

   public void extractBackground(GuiGraphicsExtractor context, int value, int value2, float amount) {
   }

   public static final List getItems() {
      return items;
   }

   static {
      Iterable items2 = ModuleManager.INSTANCE.getItems();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items2, 10));
      int value3 = 0;

      for (Object object : items2) {
         int value4 = value3++;
         if (value4 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         Category category = (Category)object;
         arrayList.add(new CategoryWindow(category, value4));
      }

      items = arrayList;
      animation = new Animation(NotificationManager.Notification.Type.OUT_BACK, 150L);
      animation2 = new Animation(NotificationManager.Notification.Type.IN_QUAD, 100L);
   }

   public static final class OpenTask implements Runnable {
      public final int value;
      public final Module module;

      public OpenTask(int value, Module module) {
         this.value = value;
         this.module = module;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }

         for (Object item : ClickGuiScreen.getItems()) {
            CategoryWindow categoryWindow = (CategoryWindow)item;

            for (Object items2Item : categoryWindow.getItems2()) {
               ModuleComponent moduleComponent = (ModuleComponent)items2Item;
               if (Intrinsics.areEqual(moduleComponent.getModule(), this.module)) {
                  moduleComponent.applyCurrentTimeMillis();
               }
            }
         }
      }
   }
}