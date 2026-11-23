// deobf by pusia
package su.catlean.client.features.ui.panel.panels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import su.catlean.api.data.ServiceConfig;
import su.catlean.api.data.WidgetConfig;
import su.catlean.client.features.ui.panel.Panel;
import su.catlean.client.features.ui.panel.components.ConfigEntry;
import su.catlean.client.features.ui.panel.components.ConfigListEntry;
import su.catlean.client.features.ui.panel.components.ConfigType;
import su.catlean.client.features.ui.panel.dialogs.Dialog;
import su.catlean.client.render.BoxPass;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ConfigFileHelper;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.utils.StopWatch;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.Render2D;

public final class ConfigPanel extends Panel {
   @NotNull
   public static final ConfigPanel INSTANCE = new ConfigPanel();
   @NotNull
   private static final List items = new ArrayList();
   private static float amount = 40.0F;
   @NotNull
   private static ConfigType configType = ConfigType.MODULES;
   @Nullable
   private static ConfigListEntry configListEntry;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @Nullable
   private static ConfigEntry configEntry;
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("panels/modules-config", 28, 28);
   @NotNull
   private static final SvgTexture svgTexture2 = new SvgTexture("panels/hud-config", 28, 28);
   @NotNull
   private static final Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);

   private ConfigPanel() {
      super("config-panel", 0.0F, 0.0F, 0.0F, 0.0F, 30, null);
   }

   @Nullable
   public final ConfigEntry getConfigEntry() {
      return configEntry;
   }

   public final void setConfigEntry(ConfigEntry configEntry) {
      ConfigPanel.configEntry = configEntry;
   }

   @Override
   public void applyClear() {
      List<ConfigListEntry> arrayList = new ArrayList();
      Collection items2 = ProxyManager.INSTANCE.getModuleDataSource().getItems();
      boolean flag = false;
      ServiceConfig[] array = (ServiceConfig[])items2.toArray(new ServiceConfig[0]);
      int value = 0;

      for (int i = array.length; value < i; value++) {
         ServiceConfig serviceConfig = array[value];
         arrayList.add(new ConfigListEntry(serviceConfig.getId(), 0.0F, 0.0F, ConfigType.MODULES, 6, null));
      }

      items2 = ProxyManager.INSTANCE.getWidgetDataSource().getItems();
      flag = false;
      WidgetConfig[] array2 = (WidgetConfig[])items2.toArray(new WidgetConfig[0]);
      int value2 = 0;

      for (int i2 = array2.length; value2 < i2; value2++) {
         WidgetConfig widgetConfig = array2[value2];
         arrayList.add(new ConfigListEntry(widgetConfig.getId(), 0.0F, 0.0F, ConfigType.WIDGETS, 6, null));
      }

      items.clear();
      items.addAll(arrayList);
   }

   @Override
   public void render(GuiGraphicsExtractor context, int value, int value2) {
      super.render(context, value, value2);
      animation.applyDoubleValue(this.getDialog() != null ? 0.5F : 0.0F);
      this.render(context);
      configListEntry = null;
      int amount3 = (int)((float)Math.floor(this.getAmount3() / (amount + 6)));
      float value3 = 0.0F;
      float value4 = 15.0F;
      float value5 = Math.max(3.0F, (this.getAmount3() - 6 - amount3 * amount) / amount3);

      for (ConfigListEntry item : (Iterable<ConfigListEntry>)items) {
         if (configType == item.getType()) {
            float amount2 = value3 + this.getAmount() + 4;
            float amount4 = value3 + this.getAmount() + 4 + amount;
            float value6 = value;
            if (amount2 <= value6 ? value6 <= amount4 : false) {
               amount2 = value4 + this.getAmount2() + 17;
               amount4 = value4 + this.getAmount2() + 17 + amount;
               value6 = value2;
               if (amount2 <= value6 ? value6 <= amount4 : false) {
                  configListEntry = item;
               }
            }

            TexturedQuadPass.defaultImpl(
               context.pose(),
               value3 + this.getAmount() + 4,
               value4 + this.getAmount2() + 17,
               amount,
               amount,
               4.0F,
               Intrinsics.areEqual(configListEntry, item) ? 2.0F : 0.0F,
               0.0F,
               1.0F,
               ThemeManager.INSTANCE.getColor5().darker(),
               ThemeManager.INSTANCE.getColor5().darker(),
               0.0F,
               0.0F,
               0.0F,
               14336,
               null
            );
            String text = item.getName().length() > 10 ? item.getName().substring(0, 7) + "..." : item.getName();
            Fonts.getFontRenderer()
               .render2(
                  context,
                  text,
                  value3 + this.getAmount() + 4 + amount / 2.0F,
                  value4 + this.getAmount2() + 17 + amount / 2.0F + 12.0F,
                  ThemeManager.INSTANCE.getColor5()
               );

            SvgTexture svgTexture3 = switch (ConfigPanel.WhenMappings.values[item.getType().ordinal()]) {
               case 1 -> svgTexture;
               case 2 -> svgTexture2;
               default -> throw new NoWhenBranchMatchedException();
            };
            boolean areEqual = false;
            if (item.getType() == ConfigType.MODULES) {
               areEqual = Intrinsics.areEqual(item.getName(), ProxyManager.INSTANCE.getModuleDataSource().getServiceConfig().getId());
            }

            if (item.getType() == ConfigType.WIDGETS) {
               areEqual = Intrinsics.areEqual(item.getName(), ProxyManager.INSTANCE.getWidgetDataSource().getWidgetConfig().getId());
            }

            if (areEqual) {
               SvgTexture.defaultImpl(
                  svgTexture3,
                  (Matrix3x2f)context.pose(),
                  value3 + this.getAmount() + 10.0,
                  value4 + this.getAmount2() + 18.0,
                  0.0F,
                  0.0F,
                  ThemeManager.INSTANCE.computeColor(0),
                  ThemeManager.INSTANCE.computeColor(180),
                  0.0F,
                  152,
                  null
               );
            } else {
               SvgTexture.defaultImpl(
                  svgTexture3, (Matrix3x2f)context.pose(), value3 + this.getAmount() + 10.0, value4 + this.getAmount2() + 18.0, 0.0F, 0.0F, null, null, 0.0F, 248, null
               );
            }

            value3 += amount + value5;
            if (value3 + amount + value5 > this.getAmount3()) {
               value3 = 0.0F;
               value4 += amount + 3.0F;
            }
         }
      }

      float amount5 = this.getAmount();
      float amount22 = this.getAmount2();
      float amount32 = this.getAmount3();
      float amount42 = this.getAmount4();
      Color color = Render2D.INSTANCE.computeColor(Color.BLACK, animation.getAmount());
      Color color2 = Render2D.INSTANCE.computeColor(Color.BLACK, animation.getAmount());
      Color color3 = Render2D.INSTANCE.computeColor(Color.BLACK, animation.getAmount());
      Color color4 = Render2D.INSTANCE.computeColor(Color.BLACK, animation.getAmount());
      Matrix3x2fStack pose = context.pose();
      Object object = null;
      short shortValue = 3072;
      Object object2 = null;
      float value7 = 0.0F;
      Matrix3x2fStack matrix3x2fStack = pose;
      Color color5 = color4;
      Color color6 = color3;
      Color color7 = color2;
      Color color8 = color;
      float value8 = 5.0F;
      BoxPass.defaultImpl(amount5, amount22, amount32, amount42, value8, color8, color7, color6, color5, matrix3x2fStack, value7, (Color)object2, shortValue, object);
      Dialog dialog = this.getDialog();
      if (dialog != null) {
         dialog.render(context, this.getAmount() + this.getAmount3() / 2.0F - 60.0F, this.getAmount2() + this.getAmount4() / 2 - 35.0F, value, value2);
      }

      if (configEntry != null) {
         configEntry.render(context, value, value2);
      }
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor) {
      float value = 16.0F;
      float value2 = 3.5F;
      TexturedQuadPass.defaultImpl(
         guiGraphicsExtractor.pose(),
         this.getAmount() + value2,
         this.getAmount2() + value,
         this.getAmount3() - 7.0F,
         13.0F,
         2.0F,
         0.0F,
         0.0F,
         1.0F,
         ThemeManager.INSTANCE.getColor5().darker(),
         ThemeManager.INSTANCE.getColor6(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      Render2D instance = Render2D.INSTANCE;
      Matrix3x2fStack pose = guiGraphicsExtractor.pose();
      float amount2 = this.getAmount() + (this.getAmount3() - 7.0F) * 0.5F + value2;
      float amount22 = this.getAmount2() + 2.0F + value;
      Color[] colors = new Color[]{new Color(1157627903, true)};
      float value3 = 9.0F;
      float value4 = 0.5F;
      float value5 = amount22;
      float value6 = amount2;
      Matrix3x2fStack matrix3x2fStack = pose;
      instance.render(matrix3x2fStack, value6, value5, value4, value3, colors);
      FontRenderer fontRenderer = Fonts.getFontRenderer();
      amount22 = this.getAmount() + (this.getAmount3() - 7.0F) * 0.25F + value2;
      float amount23 = this.getAmount2() + 5.0F + value;
      Color color = configType != ConfigType.MODULES ? Render2D.INSTANCE.computeColor(Color.WHITE, 0.5F) : Color.WHITE;
      fontRenderer.render2(guiGraphicsExtractor, "Modules", amount22, amount23, color);
      FontRenderer fontRenderer2 = Fonts.getFontRenderer();
      amount22 = this.getAmount() + (this.getAmount3() - 7.0F) * 0.75F + value2;
      amount23 = this.getAmount2() + 5.0F + value;
      Color color2 = configType != ConfigType.WIDGETS ? Render2D.INSTANCE.computeColor(Color.WHITE, 0.5F) : Color.WHITE;
      fontRenderer2.render2(guiGraphicsExtractor, "Hud", amount22, amount23, color2);
   }

   @Override
   public void updateAmount(float amount, float amount2, int value) {
      super.updateAmount(amount, amount2, value);
      Dialog dialog = this.getDialog();
      if (dialog != null ? !dialog.testAmount(amount, amount2, value) : true) {
         if (configEntry != null) {
            configEntry.updateAmount(amount, amount2, value);
         }

         ConfigEntry configEntry2;
         if (value == 1) {
            if (configListEntry != null) {
               configEntry2 = new ConfigEntry(ConfigEntry.Type.Config, amount, amount2, configListEntry.getName(), configListEntry.getType());
            } else {
               label99: {
                  float amount3 = this.getAmount();
                  if (amount <= this.getAmount() + this.getAmount3() ? amount3 <= amount : false) {
                     amount3 = this.getAmount2();
                     if (amount2 <= this.getAmount2() + this.getAmount4() ? amount3 <= amount2 : false) {
                        configEntry2 = new ConfigEntry(ConfigEntry.Type.Window, amount, amount2, null, configType, 8, null);
                        break label99;
                     }
                  }

                  configEntry2 = null;
               }
            }
         } else {
            configEntry2 = null;
         }

         configEntry = configEntry2;
         if (value == 0 && configListEntry != null) {
            if (!stopWatch.testValue(400)) {
               ConfigFileHelper.applyOrdinal(configListEntry.getName(), configListEntry.getType());
               SoundManager.INSTANCE.getSound18().applyExecuteIfPossible();
            } else {
               stopWatch.update();
            }
         } else {
            float amount22 = this.getAmount2() + 16;
            if (amount2 <= this.getAmount2() + 29 ? amount22 <= amount2 : false) {
               amount22 = this.getAmount() + 3.5F;
               if (amount <= this.getAmount() + 3.5F + (this.getAmount3() - 7.0F) * 0.5F ? amount22 <= amount : false) {
                  configType = ConfigType.MODULES;
               }

               amount22 = this.getAmount() + 3.5F + (this.getAmount3() - 7.0F) * 0.5F;
               if (amount <= this.getAmount() + 3.5F + (this.getAmount3() - 7.0F) ? amount22 <= amount : false) {
                  configType = ConfigType.WIDGETS;
               }
            }
         }
      }
   }

   static {
      INSTANCE.applyClear();
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[ConfigType.values().length];

         try {
            values2[ConfigType.MODULES.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[ConfigType.WIDGETS.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}