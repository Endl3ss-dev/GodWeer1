// deobf by pusia
package su.catlean.client.features.ui.clickgui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.api.category.Category;
import su.catlean.api.category.SubCategory;
import su.catlean.api.data.Rectangle;
import su.catlean.api.data.Selector;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.PromoModule;
import su.catlean.client.features.ui.clickgui.settings.SettingComponent;
import su.catlean.client.features.ui.panel.ThemeEditor;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ChestOpenTracker;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class CategoryWindow {
   @NotNull
   private final Category category;
   private int value;
   private int value2;
   private int value3;
   private int value4;
   private int value5;
   private float amount;
   private float amount2;
   @NotNull
   private Selector selector;
   @NotNull
   private Selector selector2;
   private int value6;
   @NotNull
   private final ArrayList<Selector> items;
   @NotNull
   private final ArrayList<ModuleComponent> items2;
   @NotNull
   private final Animation animation;
   @NotNull
   private final Animation animation2;
   @NotNull
   private final Animation animation3;
   @Nullable
   private ModuleComponent moduleComponent;
   private boolean flag;

   public CategoryWindow(Category category, int value) {
      this.category = category;
      this.value = value;
      this.value2 = (int)(MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F + (this.value - 2) * 125.0F);
      this.value3 = 100;
      this.value4 = this.value2;
      this.value5 = this.value3;
      this.amount = 120.0F;
      this.amount2 = 350.0F;
      this.value6 = -1;
      this.items = new ArrayList();
      this.items2 = new ArrayList();
      this.animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
      this.animation2 = new Animation(NotificationManager.Notification.Type.OUT_EXPO, 400L);
      this.animation3 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 200L);
      this.flag = true;
      int value7 = 0;
      Iterable<Module> items22 = ModuleManager.INSTANCE.getItems2();

      for (Object sortedWithItem : CollectionsKt.sortedWith(items22, new CategoryWindow.Ordering2())) {
         Module module = (Module)sortedWithItem;
         if (this.category.getSubCategories().contains(module.getCategory())) {
            this.items2.add(new ModuleComponent(this, module, this.value2 + 3.0F, this.value3 + 45.0F + 18 * value7));
            value7++;
         }
      }

      if (this.category.getSubCategories().size() == 2) {
         this.items.add(new Selector(0.0F, 54.0F, (SubCategory)this.category.getSubCategories().get(0), null, 8, null));
         this.items.add(new Selector(56.0F, 54.0F, (SubCategory)this.category.getSubCategories().get(1), null, 8, null));
      }

      if (this.category.getSubCategories().size() == 3) {
         this.items.add(new Selector(0.0F, 35.5F, (SubCategory)this.category.getSubCategories().get(0), null, 8, null));
         this.items.add(new Selector(37.5F, 35.5F, (SubCategory)this.category.getSubCategories().get(1), null, 8, null));
         this.items.add(new Selector(74.5F, 35.5F, (SubCategory)this.category.getSubCategories().get(2), null, 8, null));
      }

      if (this.category.getSubCategories().size() == 4) {
         this.items.add(new Selector(0.0F, 27.0F, (SubCategory)this.category.getSubCategories().get(0), null, 8, null));
         this.items.add(new Selector(28.0F, 27.0F, (SubCategory)this.category.getSubCategories().get(1), null, 8, null));
         this.items.add(new Selector(56.0F, 27.0F, (SubCategory)this.category.getSubCategories().get(2), null, 8, null));
         this.items.add(new Selector(83.0F, 27.0F, (SubCategory)this.category.getSubCategories().get(3), null, 8, null));
      }

      this.selector = (Selector)this.items.get(0);
      this.selector2 = this.selector;
   }

   @NotNull
   public final Category getCategory() {
      return this.category;
   }

   public final int getValue() {
      return this.value;
   }

   public final void setValue(int value) {
      this.value = value;
   }

   public final int getValue2() {
      return this.value2;
   }

   public final void setValue2(int value) {
      this.value2 = value;
   }

   public final int getValue3() {
      return this.value3;
   }

   public final void setValue3(int value) {
      this.value3 = value;
   }

   public final int getValue4() {
      return this.value4;
   }

   public final void setValue4(int value) {
      this.value4 = value;
   }

   public final int getValue5() {
      return this.value5;
   }

   public final void setValue5(int value) {
      this.value5 = value;
   }

   public final float getAmount() {
      return this.amount;
   }

   public final void setAmount(float amount) {
      this.amount = amount;
   }

   public final float getAmount2() {
      return this.amount2;
   }

   public final void setAmount2(float amount) {
      this.amount2 = amount;
   }

   @NotNull
   public final Selector getSelector() {
      return this.selector;
   }

   @NotNull
   public final ArrayList<Selector> getItems() {
      return this.items;
   }

   @NotNull
   public final ArrayList<ModuleComponent> getItems2() {
      return this.items2;
   }

   @Nullable
   public final ModuleComponent getModuleComponent() {
      return this.moduleComponent;
   }

   public final void setModuleComponent(ModuleComponent moduleComponent) {
      this.moduleComponent = moduleComponent;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   public final void applyContains(Module module) {
      if (this.category.getSubCategories().contains(module.getCategory())) {
         var items22 = this.items2;
         boolean flag2;
         if (items22 instanceof Collection && items22.isEmpty()) {
            flag2 = false;
         } else {
            Iterator<ModuleComponent> iterator = items22.iterator();

            while (true) {
               if (!iterator.hasNext()) {
                  flag2 = false;
                  break;
               }

               ModuleComponent object = iterator.next();
               ModuleComponent moduleComponent2 = (ModuleComponent)object;
               if (Intrinsics.areEqual(moduleComponent2.getModule().getId(), module.getId())) {
                  flag2 = true;
                  break;
               }
            }
         }

         if (!flag2) {
            this.items2.add(new ModuleComponent(this, module, this.value2 + 3.0F, this.value3 + 45.0F));
            List<ModuleComponent> items23 = this.items2;
            if (items23.size() > 1) {
               CollectionsKt.sortWith(items23, new CategoryWindow.Ordering());
            }

            return;
         }
      }
   }

   public final void applyRemoveIf() {
      this.items2.removeIf(arg -> ((ModuleComponent)arg).getModule() instanceof PromoModule);
      if ((this.moduleComponent != null ? this.moduleComponent.getModule() : null) instanceof PromoModule) {
         this.moduleComponent = null;
      }
   }

   public final void render(GuiGraphicsExtractor context, int value, int value2) {
      Fonts.getFontRenderer3().setAmount2(50.0F);
      Fonts.getFontRenderer2().setAmount2(50.0F);
      if (this.flag) {
         this.value2 = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2 + (this.value - 2) * 125;
         this.value3 = 100;
         this.value4 = this.value2;
         this.value5 = this.value3;
         this.flag = false;
      }

      this.value2 = MathKt.roundToInt(this.computeAmount(this.value2, this.value4, 13.0F));
      this.value3 = MathKt.roundToInt(this.computeAmount(this.value3, this.value5, 13.0F));
      TexturedQuadPass.render(
         context.pose(),
         this.value2,
         this.value3,
         this.amount,
         25.0F,
         7.0F,
         2.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor4().brighter(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         7.0F
      );
      TexturedQuadPass.render(
         context.pose(),
         this.value2,
         this.value3 + 24.0F,
         this.amount,
         this.animation.getAmount() - 24.0F,
         0.0F,
         2.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor4(),
         ThemeManager.INSTANCE.getColor6(),
         7.0F,
         7.0F,
         0.0F
      );
      FontRenderer fontRenderer9 = Fonts.getFontRenderer9();
      GuiGraphicsExtractor context2 = context;
      String text = this.category.getId();
      String text2;
      if (text.length() > 0) {
         char charAt = text.charAt(0);
         FontRenderer fontRenderer = fontRenderer9;
         char upperCase = Character.toUpperCase(charAt);
         fontRenderer9 = fontRenderer;
         context2 = context;
         byte byteValue = 1;
         String text3 = text.substring(byteValue);
         text2 = upperCase + text3;
      } else {
         text2 = text;
      }

      fontRenderer9.render2(context2, text2, this.value2 + this.amount / 2.0F, this.value3 + 5.0F, 10);
      Render2D instance = Render2D.INSTANCE;
      Matrix3x2fStack pose = context.pose();
      float value22 = this.value2;
      float value32 = this.value3 + 41.0F;
      float amount3 = this.amount;
      Color[] colors = new Color[]{Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor6(), 0.3F)};
      float value7 = 1.0F;
      float value8 = amount3;
      float value9 = value32;
      float value10 = value22;
      Matrix3x2fStack matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value10, value9, value8, value7, colors);
      this.animation2.applyDoubleValue(1.0F);
      Render2D instance2 = Render2D.INSTANCE;
      Rectangle rectangle = new Rectangle(this.value2 + 1.0F, this.value3, this.value2 + this.amount, this.value3 + this.animation.getAmount());
      instance2.render(context, rectangle);
      if (this.moduleComponent == null) {
         this.render(value, value2, context, this.animation.isFlag());
      } else {
         this.render(value, value2, context);
      }

      Render2D.INSTANCE.render(context);
      Fonts.getFontRenderer3().update();
      Fonts.getFontRenderer2().update();
   }

   private final void render(int value, int value2, GuiGraphicsExtractor guiGraphicsExtractor) {
      this.animation3.applyDoubleValue(1.0F);
      FontRenderer.defaultImpl(
         Fonts.getFontRenderer3(),
         guiGraphicsExtractor.pose(),
         this.moduleComponent.getModule().getText3(),
         this.value2 + 8.0F + 40.0F * (1 - this.animation3.getAmount()),
         (float)this.value3 + 27,
         ThemeManager.INSTANCE.getColor7(),
         false,
         0,
         96,
         null
      );
      float value7 = 0.0F;
      if (this.moduleComponent != null) {
         List items3 = this.moduleComponent.getItems();
         if (items3 != null) {
            for (Object object : items3) {
               SettingComponent settingComponent = (SettingComponent)object;
               if (settingComponent.getSetting().getGroupValue() == null
                  || settingComponent.getSetting().getGroupValue().isFlag() && settingComponent.getSetting().getGroupValue().getType() != PlaceModule.Pause.BOOLS) {
                  settingComponent.setAmount2(this.value2 + 5.0F);
                  settingComponent.setAmount3(this.value3 + 47.0F + value7);
                  settingComponent.render(guiGraphicsExtractor, value, value2, this.animation2.getAmount());
                  value7 += settingComponent.getAmount6() + 2.0F;
               }
            }
         }
      }

      SvgTexture.defaultImpl(
         ThemeEditor.INSTANCE.getSvgTexture2(),
         (Matrix3x2f)guiGraphicsExtractor.pose(),
         this.value2 + this.amount - 15.0,
         this.value3 + 26.0,
         0.0F,
         0.0F,
         Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), this.animation3.getAmount()),
         null,
         0.0F,
         216,
         null
      );
      this.animation.applyDoubleValue(49.0F + value7);
   }

   private final void render(int value, int value2, GuiGraphicsExtractor guiGraphicsExtractor, boolean flag) {
      this.animation3.applyDoubleValue(0.0F);
      Iterable<Selector> items3 = this.items;

      for (Object object : items3) {
         Selector selector3 = (Selector)object;
         selector3.getAnimation().applyDoubleValue(Intrinsics.areEqual(selector3, this.selector) ? 1.0F : 0.0F);
         if (selector3.getAnimation().getAmount() > 0.001) {
            TexturedQuadPass.defaultImpl(
               guiGraphicsExtractor.pose(),
               this.value2 + selector3.getAmount() + 5.0F,
               this.value3 + 27.0F,
               selector3.getAmount2(),
               11.5F,
               2.0F,
               2.0F * selector3.getAnimation().getAmount(),
               0.05F * selector3.getAnimation().getAmount(),
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

         Color alpha = Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor8(), ThemeManager.INSTANCE.getColor7(), selector3.getAnimation().getAmount());
         FontRenderer fontRenderer3 = Fonts.getFontRenderer3();
         GuiGraphicsExtractor guiGraphicsExtractor2 = guiGraphicsExtractor;
         String text = selector3.getSubCategory().getId();
         String text2;
         if (text.length() > 0) {
            char charAt = text.charAt(0);
            FontRenderer fontRenderer = fontRenderer3;
            char upperCase = Character.toUpperCase(charAt);
            fontRenderer3 = fontRenderer;
            guiGraphicsExtractor2 = guiGraphicsExtractor;
            byte byteValue = 1;
            String text3 = text.substring(byteValue);
            text2 = upperCase + text3;
         } else {
            text2 = text;
         }

         fontRenderer3.render2(
            guiGraphicsExtractor2, text2, this.value2 + selector3.getAmount() + 5.0F + MathKt.roundToInt(selector3.getAmount2() / 0.2F) / 10.0F, this.value3 + 31.5F, alpha
         );
      }

      float value7 = 0.0F;
      float value8 = 0.0F;
      float value9 = 0.0F;
      Iterable<ModuleComponent> items22 = this.items2;
      boolean flag5 = false;
      var arrayList = new ArrayList();

      for (Object object2 : items22) {
         ModuleComponent moduleComponent2 = (ModuleComponent)object2;
         if (Intrinsics.areEqual(moduleComponent2.getModule().getCategory(), this.selector.getSubCategory())
            || Intrinsics.areEqual(moduleComponent2.getModule().getCategory(), this.selector2.getSubCategory()) && this.animation2.getAmount() < 1.0F) {
            arrayList.add(object2);
         }
      }

      items22 = arrayList;
      flag5 = false;

      for (Object object3 : items22) {
         ModuleComponent moduleComponent3 = (ModuleComponent)object3;
         float value22 = Intrinsics.areEqual(moduleComponent3.getModule().getCategory(), this.selector.getSubCategory())
            ? this.value2 - this.value6 * (this.amount * (1.0F - this.animation2.getAmount()))
            : this.value2 + this.value6 * (this.amount * this.animation2.getAmount());
         moduleComponent3.setAmount(value22 + 3.0F);
         if (Intrinsics.areEqual(moduleComponent3.getModule().getCategory(), this.selector.getSubCategory())) {
            moduleComponent3.setAmount2(this.value3 + 44.0F + value7);
            value7 += moduleComponent3.getAmount4() + 2.0F;
            value9 += (moduleComponent3.isFlag() ? 31.0F : 17.0F) + 2.0F;
         } else {
            moduleComponent3.setAmount2(this.value3 + 44.0F + value8);
            value8 += moduleComponent3.getAmount4() + 2.0F;
         }

         moduleComponent3.render(guiGraphicsExtractor, value, value2, this.animation2.getAmount(), flag);
      }

      this.animation.applyDoubleValue(45.0F + value9);
   }

   public final void updateSelector(Selector selector) {
      this.selector2 = this.selector;
      this.selector = selector;
      this.value6 = this.selector2.getAmount() > this.selector.getAmount() ? 1 : -1;
      if (this.value6 == 1) {
         SoundManager.INSTANCE.getSound7().applyExecuteIfPossible();
      } else {
         SoundManager.INSTANCE.getSound8().applyExecuteIfPossible();
      }

      this.animation2.setAmount(0.0F);
      this.animation2.updateAmount(this.animation2.getAmount());
   }

   public final void applyBooleanValue(double amount, double amount2, int value) {
      if (this.moduleComponent == null) {
         Iterable<Selector> items3 = this.items;
         boolean flag2 = false;

         for (Object object : items3) {
            Selector selector3 = (Selector)object;
            float amount3 = selector3.getAmount() + this.value2;
            if (amount <= selector3.getAmount() + selector3.getAmount2() + this.value2 ? amount3 <= amount : false) {
               int value32 = this.value3 + 27;
               int value33 = this.value3 + 38;
               int amount22 = (int)amount2;
               if ((value32 <= amount22 ? amount22 <= value33 : false) && !Intrinsics.areEqual(this.selector, selector3)) {
                  this.updateSelector(selector3);
               }
            }
         }

         items3 = (Iterable<Selector>)(Object)this.items2;
         flag2 = false;
         var arrayList = new ArrayList();

         for (Object object2 : items3) {
            ModuleComponent moduleComponent2 = (ModuleComponent)object2;
            if (Intrinsics.areEqual(moduleComponent2.getModule().getCategory(), this.selector.getSubCategory())) {
               arrayList.add(object2);
            }
         }

         items3 = arrayList;
         flag2 = false;

         for (Object object3 : items3) {
            ModuleComponent moduleComponent3 = (ModuleComponent)object3;
            moduleComponent3.applyIsEmpty(amount, amount2, value);
         }
      } else {
         int value22 = this.value2 + 3;
         int value23 = this.value2 + (int)this.amount - 3;
         int amount4 = (int)amount;
         if (value22 <= amount4 ? amount4 <= value23 : false) {
            value22 = this.value3 + 25;
            value23 = this.value3 + 40;
            amount4 = (int)amount2;
            if (value22 <= amount4 ? amount4 <= value23 : false) {
               this.moduleComponent = null;
               SoundManager.INSTANCE.getSound5().applyExecuteIfPossible();
               return;
            }
         }

         ModuleComponent moduleComponent4 = this.moduleComponent;
         if (moduleComponent4 != null) {
            List items4 = moduleComponent4.getItems();
            if (items4 != null) {
               Iterable iterable = items4;
               boolean flag7 = false;
               var arrayList2 = new ArrayList();

               for (Object object4 : iterable) {
                  SettingComponent settingComponent = (SettingComponent)object4;
                  if ((Boolean)settingComponent.getSetting().getVisible().invoke() && settingComponent.getSetting().getGroupValue() == null
                     || settingComponent.getSetting().getGroupValue().isFlag() && settingComponent.getSetting().getGroupValue().getType() != PlaceModule.Pause.BOOLS) {
                     arrayList2.add(object4);
                  }
               }

               iterable = arrayList2;
               flag7 = false;

               for (Object object5 : iterable) {
                  SettingComponent settingComponent2 = (SettingComponent)object5;
                  settingComponent2.updateAmount(amount, amount2, value);
               }
            }
         }
      }
   }

   public final void updateAmount(double amount, double amount2, int value) {
      if (this.moduleComponent != null && this.moduleComponent != null) {
         List items3 = this.moduleComponent.getItems();
         if (items3 != null) {
            for (Object object : items3) {
               SettingComponent settingComponent = (SettingComponent)object;
               settingComponent.updateAmount2(amount, amount2, value);
            }
         }
      }
   }

   public final void updateValue(int value) {
      for (Object items2Item : this.items2) {
         ModuleComponent moduleComponent2 = (ModuleComponent)items2Item;
         moduleComponent2.updateValue(value);
      }
   }

   public final void updateSymbol(char symbol) {
      for (Object items2Item : this.items2) {
         ModuleComponent moduleComponent2 = (ModuleComponent)items2Item;
         moduleComponent2.updateSymbol(symbol);
      }
   }

   public final boolean testAmount(double amount, double amount2, double amount3, double amount4) {
      boolean flag2 = false;

      for (Object items2Item : this.items2) {
         ModuleComponent moduleComponent2 = (ModuleComponent)items2Item;
         if (moduleComponent2.checkBooleanValue(amount, amount2, amount3)) {
            flag2 = true;
         }
      }

      return flag2;
   }

   public final void updateAmount(double amount, double amount2) {
      this.value5 += (int)(amount * 10.0F);
      this.value4 += (int)(amount2 * 10.0F);
   }

   private final float computeAmount(float amount, float amount2, float amount3) {
      float value7 = Math.clamp((ChestOpenTracker.INSTANCE.getValue() > 5 ? 1.0F / ChestOpenTracker.INSTANCE.getValue() : 0.016F) * amount3, 0.0F, 1.0F);
      return (1.0F - value7) * amount + value7 * amount2;
   }

   public final void update() {
      if (this.moduleComponent != null) {
         List items3 = this.moduleComponent.getItems();
         if (items3 != null) {
            for (Object object : items3) {
               SettingComponent settingComponent = (SettingComponent)object;
               settingComponent.update();
            }
         }
      }

      this.flag = true;
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         ModuleComponent object3 = (ModuleComponent)object;
         Comparable<String> lowerCase = object3.getModule().getId().toLowerCase(Locale.ROOT);
         object3 = (ModuleComponent)object2;
         Comparable<String> comparable = lowerCase;
         return ComparisonsKt.compareValues(comparable, object3.getModule().getId().toLowerCase(Locale.ROOT));
      }
   }

   public static final class Ordering2 implements Comparator {
      public Ordering2() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         Module object3 = (Module)object;
         Comparable<String> lowerCase = object3.getId().toLowerCase(Locale.ROOT);
         object3 = (Module)object2;
         Comparable<String> comparable = lowerCase;
         return ComparisonsKt.compareValues(comparable, object3.getId().toLowerCase(Locale.ROOT));
      }
   }
}