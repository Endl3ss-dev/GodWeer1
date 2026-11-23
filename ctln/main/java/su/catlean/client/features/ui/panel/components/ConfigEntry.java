// deobf by pusia
package su.catlean.client.features.ui.panel.components;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.ui.panel.dialogs.ConfigNameDialog;
import su.catlean.client.features.ui.panel.panels.ConfigPanel;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.FontRenderer;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ConfigFileHelper;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.proxy.ProxyManager;

public final class ConfigEntry {
   @NotNull
   private ConfigEntry.Type type;
   private float amount;
   private float amount2;
   @NotNull
   private String elementId;
   @NotNull
   private ConfigType configType;
   @NotNull
   private final List<ConfigAction> items;
   private float amount3;

   public ConfigEntry(ConfigEntry.Type type, float amount, float amount2, String elementId, ConfigType configType) {
      this.type = type;
      this.amount = amount;
      this.amount2 = amount2;
      this.elementId = elementId;
      this.configType = configType;
      this.items = new ArrayList();
      this.amount3 = 62.0F;
      if (this.type == ConfigEntry.Type.Config) {
         List<ConfigAction> items2 = this.items;
         ConfigAction[] configActions = new ConfigAction[]{new ConfigAction(LocalizationManager.INSTANCE.computeText("windows.config.load", new Object[0]), arg -> {
            if (!StringsKt.isBlank(this.elementId)) {
               ConfigFileHelper.applyOrdinal(this.elementId, this.configType);
            }

            SoundManager.INSTANCE.getSound18().applyExecuteIfPossible();
            return Unit.INSTANCE;
         }), new ConfigAction(LocalizationManager.INSTANCE.computeText("windows.config.open-in-explorer", new Object[0]), arg2 -> {
            if (!StringsKt.isBlank(this.elementId)) {
               if (this.configType == ConfigType.MODULES) {
                  OS platform = Util.getPlatform();
                  File file = ProxyManager.INSTANCE.getModuleDataSource().getFile();
                  OS value = platform;
                  if (!file.exists()) {
                     try {
                        file.createNewFile();
                     } catch (java.io.IOException exception) {
                        throw new RuntimeException(exception);
                     }
                  }

                  value.openFile(file);
               } else {
                  OS platform2 = Util.getPlatform();
                  File file2 = ProxyManager.INSTANCE.getWidgetDataSource().getFile();
                  OS value2 = platform2;
                  if (!file2.exists()) {
                     try {
                        file2.createNewFile();
                     } catch (java.io.IOException exception) {
                        throw new RuntimeException(exception);
                     }
                  }

                  value2.openFile(file2);
               }
            }

            return Unit.INSTANCE;
         }), new ConfigAction(LocalizationManager.INSTANCE.computeText("windows.config.delete", new Object[0]), arg3 -> {
            if (!StringsKt.isBlank(this.elementId)) {
               ConfigFileHelper.applyDelete((String)arg3, this.configType);
               ConfigPanel.INSTANCE.setDialog(null);
               ConfigPanel.INSTANCE.applyClear();
            }

            return Unit.INSTANCE;
         })};
         items2.addAll(CollectionsKt.listOf(configActions));
      } else {
         this.items.add(new ConfigAction(LocalizationManager.INSTANCE.computeText("windows.config.save", new Object[0]), arg4 -> {
            ConfigPanel.INSTANCE.setConfigEntry(null);
            ConfigPanel.INSTANCE.setDialog(new ConfigNameDialog(this.configType));
            return Unit.INSTANCE;
         }));
      }
   }

   public ConfigEntry(
      ConfigEntry.Type type, float amount, float amount2, String text, ConfigType configType, int value, DefaultConstructorMarker defaultConstructorMarker
   ) {
      if ((value & 2) != 0) {
         amount = 0.0F;
      }

      if ((value & 4) != 0) {
         amount2 = 0.0F;
      }

      if ((value & 8) != 0) {
         text = "";
      }

      if ((value & 16) != 0) {
         configType = ConfigType.MODULES;
      }

      this(type, amount, amount2, text, configType);
   }

   @NotNull
   public final ConfigEntry.Type getType() {
      return this.type;
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
   public final ConfigType getConfigType() {
      return this.configType;
   }

   public final float getAmount3() {
      return this.amount3;
   }

   public final void setAmount3(float amount) {
      this.amount3 = amount;
   }

   public final void render(GuiGraphicsExtractor context, int value, int value2) {
      float size = this.items.size() * 14.0F + 2.0F;
      TexturedQuadPass.defaultImpl(
         context.pose(),
         this.amount,
         this.amount2,
         this.amount3,
         size,
         4.0F,
         1.0F,
         0.07F,
         1.0F,
         ThemeManager.INSTANCE.getColor5().darker(),
         ThemeManager.INSTANCE.getColor6().darker(),
         0.0F,
         0.0F,
         0.0F,
         14336,
         null
      );
      int value3 = 0;

      for (Object item : this.items) {
         ConfigAction configAction = (ConfigAction)item;
         float amount4 = this.amount;
         float amount5 = this.amount + this.amount3;
         float value4 = value;
         if (amount4 <= value4 ? value4 <= amount5 : false) {
            amount4 = this.amount2 + 2 + value3 * 14;
            amount5 = this.amount2 + 14 + value3 * 14;
            value4 = value2;
            if (amount4 <= value4 ? value4 <= amount5 : false) {
               TexturedQuadPass.defaultImpl(
                  context.pose(),
                  this.amount + 2,
                  this.amount2 + 2 + value3 * 14,
                  this.amount3 - 4,
                  12.0F,
                  2.0F,
                  2.0F,
                  0.1F,
                  1.0F,
                  ThemeManager.INSTANCE.getColor5(),
                  ThemeManager.INSTANCE.getColor6(),
                  0.0F,
                  0.0F,
                  0.0F,
                  14336,
                  null
               );
            }
         }

         FontRenderer fontRenderer = Fonts.getFontRenderer();
         String text = configAction.getId();
         float amount6 = this.amount + 5;
         float amount22 = this.amount2 + 2 + value3 * 14;
         Color color = value3 == this.items.size() - 1 && this.items.size() > 1 ? new Color(255, 0, 0, 170) : Color.WHITE;
         float value5 = amount22;
         float value6 = amount6;
         String text2 = text;
         fontRenderer.render(context, text2, value6, value5, color);
         value3++;
      }
   }

   public final void updateAmount(float amount, float amount2, int value) {
      int value2 = 0;

      for (Object item : this.items) {
         ConfigAction configAction = (ConfigAction)item;
         float amount4 = this.amount;
         if (amount <= this.amount + this.amount3 ? amount4 <= amount : false) {
            amount4 = this.amount2 + 2 + value2 * 14;
            if (amount2 <= this.amount2 + 14 + value2 * 14 ? amount4 <= amount2 : false) {
               configAction.getAction().invoke(this.elementId);
            }
         }

         value2++;
      }
   }

   public static enum Type {
      Config,
      Window;

      private static final ConfigEntry.Type[] types = $values();
      private static final EnumEntries<ConfigEntry.Type> enumEntries = EnumEntriesKt.enumEntries(types);

      private Type() {
      }

      @NotNull
      public static EnumEntries<ConfigEntry.Type> getEnumEntries() {
         return enumEntries;
      }

      private static final ConfigEntry.Type[] $values() {
         return new ConfigEntry.Type[]{Config, Window};
      }
   }
}