// deobf by pusia
package su.catlean.client.features.ui.clickgui;

import su.catlean.utils.KotlinCompat;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IndexedValue;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.features.ui.panel.PanelScreen;
import su.catlean.client.features.ui.panel.ThemeEditor;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ModuleManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.render.Render2D;

public final class SearchBar {
   @NotNull
   public static final SearchBar INSTANCE = new SearchBar();
   @NotNull
   private static SearchBar.HubSelector hubSelector;
   @NotNull
   private static SearchBar.HubSelector hubSelector2;
   private static boolean flag;
   private static boolean flag2;
   @NotNull
   private static final List items;
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_EXPO, 600L);
   @NotNull
   private static String text = "";
   @Nullable
   private static Function0 callback;

   private SearchBar() {
   }

   public final boolean isFlag2() {
      return flag2;
   }

   public final void setFlag2(boolean flag) {
      flag2 = flag;
   }

   public final void render(GuiGraphicsExtractor context, int value, int value2) {
      if (!Intrinsics.areEqual(MinecraftHolder.getMinecraft().gui.screen(), ClickGuiScreen.INSTANCE)) {
         flag2 = false;
      }

      animation.applyDoubleValue(flag2 ? 1.0F : 0.0F);
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F;
      float value3 = 5.0F;
      float value4 = 220.0F;
      float value5 = 16.0F;
      TexturedQuadPass.defaultImpl(
         context.pose(),
         guiScaledWidth - value4 / 2.0F,
         value3,
         value4,
         value5,
         5.0F,
         2.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor4(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Iterable items2 = items;
      int value6 = 0;

      for (Object object : items2) {
         int value7 = value6++;
         if (value7 < 0) {
            CollectionsKt.throwIndexOverflow();
         }

         SearchBar.HubSelector hubSelector3 = (SearchBar.HubSelector)object;

         boolean flag3 = switch (value7) {
            case 0 -> Intrinsics.areEqual(MinecraftHolder.getMinecraft().gui.screen(), ClickGuiScreen.INSTANCE);
            case 1 -> Intrinsics.areEqual(MinecraftHolder.getMinecraft().gui.screen(), PanelScreen.INSTANCE);
            case 2 -> Intrinsics.areEqual(MinecraftHolder.getMinecraft().gui.screen(), WidgetEditorScreen.INSTANCE);
            case 3 -> flag;
            case 4 -> flag2;
            default -> false;
         };
         hubSelector3.getAnimation().applyDoubleValue(flag3 ? 1.0F : 0.0F);
         float amount = guiScaledWidth - value4 / 2.0F + hubSelector3.getAmount() + 2.0F;
         float amount2 = value7 == 4 ? 1.0F : 1.0F - animation.getAmount();
         TexturedQuadPass.defaultImpl(
            context.pose(),
            amount,
            value3 + 2,
            40.0F,
            value5 - 4.0F,
            4.0F,
            2.0F * hubSelector3.getAnimation().getAmount(),
            hubSelector3.getAnimation().getAmount() / 5.0F,
            amount2,
            ThemeManager.INSTANCE.getColor4(),
            ThemeManager.INSTANCE.getColor6(),
            0.0F,
            0.0F,
            0.0F,
            14336,
            null
         );
         Fonts.getFontRenderer3()
            .render2(
               context,
               value7 == 4 && flag2 ? "Back" : hubSelector3.getName(),
               amount + 20.0F,
               value3 + 7.0F,
               Render2D.INSTANCE
                  .computeColor(
                     Color.WHITE,
                     Intrinsics.areEqual(hubSelector3.getName(), "Search")
                           && !Intrinsics.areEqual(MinecraftHolder.getMinecraft().gui.screen(), ClickGuiScreen.INSTANCE)
                        ? 0.3F
                        : amount2
                  )
            );
      }

      this.render(context, value, value2, guiScaledWidth, value3, value4, value5);
      if (flag) {
         ThemeEditor.INSTANCE.render(context, value, value2);
      }
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, int value, int value2, float amount, float amount2, float amount3, float amount4) {
      callback = null;
      if (flag2) {
         Fonts.getFontRenderer3()
            .render(
               guiGraphicsExtractor,
               text + (System.currentTimeMillis() % 1000.0 > 500.0 ? "" : "_"),
               amount - amount3 / 2.0F + 6.0F,
               amount2 + 2.0F,
               Render2D.INSTANCE.computeColor(Color.WHITE, animation.getAmount())
            );
         Map map = this.getMap();
         if (!map.isEmpty()) {
            TexturedQuadPass.defaultImpl(
               guiGraphicsExtractor.pose(),
               amount - amount3 / 2.0F,
               amount2 + amount4 + 4.0F,
               amount3,
               map.size() * 14.0F + 4.0F,
               5.0F,
               2.0F,
               0.0F,
               1.0F,
               ThemeManager.INSTANCE.getColor4(),
               ThemeManager.INSTANCE.getColor6(),
               0.0F,
               0.0F,
               0.0F,
               14336,
               null
            );

            for (Object withIndexItem : CollectionsKt.withIndex(map.entrySet())) {
               IndexedValue indexedValue = (IndexedValue)withIndexItem;
               int component1 = indexedValue.component1();
               Entry component2 = (Entry)indexedValue.component2();
               String key = (String)component2.getKey();
               Function0 value3 = (Function0)component2.getValue();
               float amount5 = amount - amount3 / 2.0F + 6.0F;
               float amount22 = amount2 + amount4 + 6.0F + 14.0F * component1;
               float value4 = amount5 - 2.0F;
               float amount32 = amount5 + amount3 - 4.0F;
               float value5 = value;
               if (value4 <= value5 ? value5 <= amount32 : false) {
                  value4 = amount22 - 2.0F;
                  amount32 = amount22 + 11.0F;
                  value5 = value2;
                  if (value4 <= value5 ? value5 <= amount32 : false) {
                     TexturedQuadPass.defaultImpl(
                        guiGraphicsExtractor.pose(),
                        amount5 - 4.0F,
                        amount22,
                        amount3 - 4.0F,
                        14.0F,
                        4.0F,
                        2.0F,
                        0.0F,
                        1.0F,
                        ThemeManager.INSTANCE.getColor4(),
                        ThemeManager.INSTANCE.getColor6(),
                        0.0F,
                        0.0F,
                        0.0F,
                        14336,
                        null
                     );
                     callback = value3;
                  }
               }

               Fonts.getFontRenderer3()
                  .render(guiGraphicsExtractor, key, amount5, amount22 + 1.0F, Render2D.INSTANCE.computeColor(Color.WHITE, animation.getAmount()));
            }
         }
      }
   }

   private final Map getMap() {
      Map linkedHashMap = new LinkedHashMap();
      if (text.length() == 0) {
         return linkedHashMap;
      } else {
         String lowerCase = null;
         lowerCase = KotlinCompat.replace(text, " ", "-", false, 4, null).toLowerCase(Locale.ROOT);
         String text2 = (String)lowerCase;
         int value = 0;

         for (int i = lowerCase.length(); value < i; value++) {
            char charAt = text2.charAt(value);
            if (KotlinCompat.contains("йцукенгшщзхъфывапролджэячсмитьбю", charAt, false, 2, null)) {
               lowerCase = KotlinCompat.replace(
                  (String)lowerCase,
                  charAt,
                  "qwertyuiop[]asdfghjkl;'zxcvbnm,.".charAt(KotlinCompat.indexOf("йцукенгшщзхъфывапролджэячсмитьбю", charAt, 0, false, 6, null)),
                  false,
                  4,
                  null
               );
            }
         }

         for (Module items2Item : (Iterable<Module>)ModuleManager.INSTANCE.getItems2()) {
            if (linkedHashMap.size() >= 10) {
               break;
            }

            Iterable items5 = items2Item.getItems5();
            boolean flag3 = false;
            var arrayList = new ArrayList();
            boolean flag4 = false;

            for (Object object : items5) {
               Setting setting = (Setting)object;
               if (KotlinCompat.contains(setting.getId(), (CharSequence)lowerCase, false, 2, null)
                  || KotlinCompat.contains(KotlinCompat.replace(setting.getId(), "-", "", false, 4, null), (CharSequence)lowerCase, false, 2, null)) {
                  arrayList.add(object);
               }
            }

            items5 = arrayList;
            flag3 = false;
            arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items5, 10));
            flag4 = false;

            for (Object object2 : items5) {
               Setting setting2 = (Setting)object2;
               arrayList.add(TuplesKt.to(items2Item.getText3() + " ➜ " + setting2.getText(), (Function0)() -> {
                  ClickGuiScreen.INSTANCE.updateModule(items2Item);
                  return Unit.INSTANCE;
               }));
            }

            MapsKt.putAll(linkedHashMap, arrayList);
            Iterable aliases = items2Item.getAliases();
            Iterator iterator = aliases.iterator();

            Object object3;
            while (true) {
               if (iterator.hasNext()) {
                  Object object4 = iterator.next();
                  String text3 = (String)object4;
                  if (!KotlinCompat.contains(text3, (CharSequence)lowerCase, false, 2, null)) {
                     continue;
                  }

                  object3 = object4;
                  break;
               }

               object3 = null;
               break;
            }

            String text4 = (String)object3;
            if (KotlinCompat.contains(items2Item.getId(), (CharSequence)lowerCase, false, 2, null)
               || text4 != null
               || KotlinCompat.contains(KotlinCompat.replace(items2Item.getId(), "-", "", false, 4, null), (CharSequence)lowerCase, false, 2, null)) {
               linkedHashMap.put(text4 == null ? items2Item.getText3() : items2Item.getText3() + " (" + text4 + ")", (kotlin.jvm.functions.Function0)() -> {
                  ClickGuiScreen.INSTANCE.applyExecute(items2Item);
                  return Unit.INSTANCE;
               });
            }
         }

         return linkedHashMap;
      }
   }

   public final boolean checkGetGuiScaledWidth(double amount, double amount2, int value) {
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - 108.0F;

      for (Object item : items) {
         SearchBar.HubSelector hubSelector3 = (SearchBar.HubSelector)item;
         if (!flag2 || Intrinsics.areEqual(hubSelector3.getName(), "Search")) {
            float amount3 = guiScaledWidth + hubSelector3.getAmount();
            if ((amount3 <= amount ? amount <= amount3 + 40.0F : false) && (7.0 <= amount2 ? amount2 <= 19.0 : false)) {
               hubSelector3.getAction().invoke();
               return true;
            }
         }
      }

      if (callback == null) {
         flag2 = false;
         if (flag) {
            ThemeEditor.INSTANCE.applyClear(amount, amount2, value);
         }

         return false;
      } else {
         if (callback != null) {
            callback.invoke();
         }

         return true;
      }
   }

   public final void updateAmount(double amount, double amount2, int value) {
      if (flag) {
         ThemeEditor.INSTANCE.updateAmount(amount, amount2, value);
      }
   }

   public final void updateValue(int value, int value2, int value3) {
      if (value == 70 && InputUtil.checkContains(341)) {
         flag2 = !flag2;
      }

      if (flag2 && value == 256) {
         flag2 = false;
      }

      if (flag2 && value == 259) {
         text = StringsKt.dropLast(text, 1);
      } else if (flag) {
         ThemeEditor.INSTANCE.updateValue(value, value2, value3);
      }
   }

   public final void applyLength(char symbol) {
      if (flag2 && text.length() < 20) {
         text = text + symbol;
      } else if (flag) {
         ThemeEditor.INSTANCE.updateSymbol(symbol);
      }
   }

   public final boolean testAmount(double amount, double amount2, double amount3) {
      return flag ? ThemeEditor.INSTANCE.checkClamp(amount, amount2, amount3) : false;
   }

   static {
      SearchBar.HubSelector[] hubSelectors = new SearchBar.HubSelector[]{new SearchBar.HubSelector(0.0F, "UI", null, () -> {
         MinecraftHolder.getMinecraft().gui.setScreen(ClickGuiScreen.INSTANCE);
         return Unit.INSTANCE;
      }, 4, null), new SearchBar.HubSelector(44.0F, "Windows", null, () -> {
         MinecraftHolder.getMinecraft().gui.setScreen(PanelScreen.INSTANCE);
         return Unit.INSTANCE;
      }, 4, null), new SearchBar.HubSelector(88.0F, "Hud", null, () -> {
         MinecraftHolder.getMinecraft().gui.setScreen(WidgetEditorScreen.INSTANCE);
         return Unit.INSTANCE;
      }, 4, null), new SearchBar.HubSelector(132.0F, "Theme", null, () -> {
         flag = !flag;
         return Unit.INSTANCE;
      }, 4, null), new SearchBar.HubSelector(176.0F, "Search", null, () -> {
         flag2 = !flag2;
         text = "";
         return Unit.INSTANCE;
      }, 4, null)};
      items = CollectionsKt.mutableListOf(hubSelectors);
      hubSelector = (SearchBar.HubSelector)items.get(0);
      hubSelector2 = hubSelector;
   }

   public static final class HubSelector {
      private final float amount;
      @NotNull
      private final String name;
      @NotNull
      private final Animation animation;
      @NotNull
      private final Function0 action;

      public HubSelector(float amount, String name, Animation animation, Function0 action) {
         this.amount = amount;
         this.name = name;
         this.animation = animation;
         this.action = action;
      }

      public HubSelector(float amount, String text, Animation animation, Function0 callback, int value, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value & 4) != 0) {
            animation = new Animation(NotificationManager.Notification.Type.OUT_EXPO, 600L);
         }

         this(amount, text, animation, callback);
      }

      public final float getAmount() {
         return this.amount;
      }

      @NotNull
      public final String getName() {
         return this.name;
      }

      @NotNull
      public final Animation getAnimation() {
         return this.animation;
      }

      @NotNull
      public final Function0 getAction() {
         return this.action;
      }

      public final float getAmount2() {
         return this.amount;
      }

      @NotNull
      public final String getName2() {
         return this.name;
      }

      @NotNull
      public final Animation getAnimation2() {
         return this.animation;
      }

      @NotNull
      public final Function0 getAction2() {
         return this.action;
      }

      @NotNull
      @Override
      public String toString() {
         Function0 action2 = this.action;
         Animation animation2 = this.animation;
         String name2 = this.name;
         float amount2 = this.amount;
         return "HubSelector(x=" + amount2 + ", name=" + name2 + ", animation=" + animation2 + ", action=" + action2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Float.hashCode(this.amount);
         hashCode = hashCode * 31 + this.name.hashCode();
         hashCode = hashCode * 31 + this.animation.hashCode();
         return hashCode * 31 + this.action.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof SearchBar.HubSelector hubSelector)) {
            return false;
         } else if (Float.compare(this.amount, hubSelector.amount) != 0) {
            return false;
         } else if (!Intrinsics.areEqual(this.name, hubSelector.name)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.animation, hubSelector.animation) ? false : Intrinsics.areEqual(this.action, hubSelector.action);
         }
      }
   }
}