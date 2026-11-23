// deobf by pusia
package su.catlean.client.features.modules.misc;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;

public final class ClientInterface extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ClientInterface.class, "moduleMode", "getModuleMode()Lsu/catlean/j;", 0), new PropertyReference1Impl(ClientInterface.class, "moduleText", "getModuleText()Lsu/catlean/ub;", 0), new PropertyReference1Impl(ClientInterface.class, "linesMode", "getLinesMode()Lsu/catlean/client/features/modules/utility/misc/ui/UI$Lines;", 0), new PropertyReference1Impl(ClientInterface.class, "hellHitPromo", "getHellHitPromo()Z", 0) };
   @NotNull
   public static final ClientInterface INSTANCE = new ClientInterface();
   @NotNull
   private static final EnumSetting moduleMode = SettingFactory.defaultImpl(INSTANCE, "module-mode", ClientInterface.ModuleMode.DEFAULT, null, null, 12, null);
   @NotNull
   private static final EnumSetting moduleText = SettingFactory.defaultImpl(INSTANCE, "module-text", ClientInterface.ModuleText.CENTER, null, null, 12, null);
   @NotNull
   private static final EnumSetting linesMode = SettingFactory.defaultImpl(INSTANCE, "lines-mode", ClientInterface.LinesMode.NEW, null, null, 12, null);
   @NotNull
   private static final BooleanSetting hellhitPromo = SettingFactory.defaultImpl(INSTANCE, "hellhit-promo", true, null, null, 12, null);

   private ClientInterface() {
      super("UI", SubCategories.getSubCategory11(), null, 4, null);
   }

   @NotNull
   public final ClientInterface.ModuleMode getModuleMode() {
      return (ClientInterface.ModuleMode)moduleMode.computeObject(this, $$delegatedProperties[0]);
   }

   @NotNull
   public final ClientInterface.ModuleText getModuleText() {
      return (ClientInterface.ModuleText)moduleText.computeObject(this, $$delegatedProperties[1]);
   }

   @NotNull
   public final ClientInterface.LinesMode getLinesMode() {
      return (ClientInterface.LinesMode)linesMode.computeObject(this, $$delegatedProperties[2]);
   }

   public final boolean getHellHitPromo() {
      return (Boolean)hellhitPromo.computeObject(this, $$delegatedProperties[3]);
   }

   static {
      if (INSTANCE.getBind().getValue() == -1) {
         INSTANCE.getBind().setValue(80);
      }
   }

   public static enum LinesMode {
      OLD,
      NEW;

      private static final ClientInterface.LinesMode[] linesModes = $values();
      private static final EnumEntries<ClientInterface.LinesMode> enumEntries = EnumEntriesKt.enumEntries(linesModes);

      private LinesMode() {
      }

      @NotNull
      public static EnumEntries<ClientInterface.LinesMode> getEnumEntries() {
         return enumEntries;
      }

      private static final ClientInterface.LinesMode[] $values() {
         return new ClientInterface.LinesMode[]{OLD, NEW};
      }
   }

   public static enum ModuleMode {
      DEFAULT,
      RECT,
      FILL;

      private static final ClientInterface.ModuleMode[] moduleModes = $values();
      private static final EnumEntries<ClientInterface.ModuleMode> enumEntries = EnumEntriesKt.enumEntries(moduleModes);

      private ModuleMode() {
      }

      @NotNull
      public static EnumEntries<ClientInterface.ModuleMode> getEnumEntries() {
         return enumEntries;
      }

      private static final ClientInterface.ModuleMode[] $values() {
         return new ClientInterface.ModuleMode[]{DEFAULT, RECT, FILL};
      }
   }

   public static enum ModuleText {
      CENTER,
      LEFT;

      private static final ClientInterface.ModuleText[] moduleTexts = $values();
      private static final EnumEntries<ClientInterface.ModuleText> enumEntries = EnumEntriesKt.enumEntries(moduleTexts);

      private ModuleText() {
      }

      @NotNull
      public static EnumEntries<ClientInterface.ModuleText> getEnumEntries() {
         return enumEntries;
      }

      private static final ClientInterface.ModuleText[] $values() {
         return new ClientInterface.ModuleText[]{CENTER, LEFT};
      }
   }
}