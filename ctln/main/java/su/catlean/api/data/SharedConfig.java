// deobf by pusia
package su.catlean.api.data;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.render.Colors;

@Serializable
public final class SharedConfig {
   @NotNull
   public static final SharedConfig.Companion companion = new SharedConfig.Companion(null);
   @NotNull
   private String modulesConfig;
   @NotNull
   private String widgetsConfig;
   @NotNull
   private String inventoriesConfig;
   @NotNull
   private String themeConfig;
   private boolean flag;

   public SharedConfig(String modulesConfig, String widgetsConfig, String inventoriesConfig, String themeConfig, boolean flag) {
      this.modulesConfig = modulesConfig;
      this.widgetsConfig = widgetsConfig;
      this.inventoriesConfig = inventoriesConfig;
      this.themeConfig = themeConfig;
      this.flag = flag;
   }

   public SharedConfig(String text, String text2, String text3, String text4, boolean flag, int value, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value & 1) != 0) {
         text = "default";
      }

      if ((value & 2) != 0) {
         text2 = "default";
      }

      if ((value & 4) != 0) {
         text3 = "default";
      }

      if ((value & 8) != 0) {
         text4 = Colors.getThemeConfig().getId();
      }

      if ((value & 16) != 0) {
         flag = false;
      }

      this(text, text2, text3, text4, flag);
   }

   @NotNull
   public final String getModulesConfig() {
      return this.modulesConfig;
   }

   public final void updateText(String text) {
      this.modulesConfig = text;
   }

   @NotNull
   public final String getWidgetsConfig() {
      return this.widgetsConfig;
   }

   public final void updateText2(String text) {
      this.widgetsConfig = text;
   }

   @NotNull
   public final String getInventoriesConfig() {
      return this.inventoriesConfig;
   }

   @NotNull
   public final String getThemeConfig() {
      return this.themeConfig;
   }

   public final void updateText3(String text) {
      this.themeConfig = text;
   }

   public final boolean isFlag() {
      return this.flag;
   }

   public final void setFlag(boolean flag) {
      this.flag = flag;
   }

   @NotNull
   public final String getModulesConfig2() {
      return this.modulesConfig;
   }

   @NotNull
   public final String getWidgetsConfig2() {
      return this.widgetsConfig;
   }

   @NotNull
   public final String getInventoriesConfig2() {
      return this.inventoriesConfig;
   }

   @NotNull
   public final String getThemeConfig2() {
      return this.themeConfig;
   }

   public final boolean isFlag2() {
      return this.flag;
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag2 = this.flag;
      String themeConfig2 = this.themeConfig;
      String inventoriesConfig2 = this.inventoriesConfig;
      String widgetsConfig2 = this.widgetsConfig;
      String modulesConfig2 = this.modulesConfig;
      return "SharedConfig(modulesConfig="
         + modulesConfig2
         + ", widgetsConfig="
         + widgetsConfig2
         + ", inventoriesConfig="
         + inventoriesConfig2
         + ", themeConfig="
         + themeConfig2
         + ", rpcAsked="
         + flag2
         + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.modulesConfig.hashCode();
      hashCode = hashCode * 31 + this.widgetsConfig.hashCode();
      hashCode = hashCode * 31 + this.inventoriesConfig.hashCode();
      hashCode = hashCode * 31 + this.themeConfig.hashCode();
      return hashCode * 31 + Boolean.hashCode(this.flag);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof SharedConfig sharedConfig)) {
         return false;
      } else if (!Intrinsics.areEqual(this.modulesConfig, sharedConfig.modulesConfig)) {
         return false;
      } else if (!Intrinsics.areEqual(this.widgetsConfig, sharedConfig.widgetsConfig)) {
         return false;
      } else if (!Intrinsics.areEqual(this.inventoriesConfig, sharedConfig.inventoriesConfig)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.themeConfig, sharedConfig.themeConfig) ? false : this.flag == sharedConfig.flag;
      }
   }

   @JvmStatic
   public static final void applyShouldEncodeElementDefault(SharedConfig sharedConfig, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 0) ? true : !Intrinsics.areEqual(sharedConfig.modulesConfig, "default")) {
         compositeEncoder.encodeStringElement(serialDescriptor, 0, sharedConfig.modulesConfig);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 1) ? true : !Intrinsics.areEqual(sharedConfig.widgetsConfig, "default")) {
         compositeEncoder.encodeStringElement(serialDescriptor, 1, sharedConfig.widgetsConfig);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 2) ? true : !Intrinsics.areEqual(sharedConfig.inventoriesConfig, "default")) {
         compositeEncoder.encodeStringElement(serialDescriptor, 2, sharedConfig.inventoriesConfig);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 3)
         ? true
         : !Intrinsics.areEqual(sharedConfig.themeConfig, Colors.getThemeConfig().getId())) {
         compositeEncoder.encodeStringElement(serialDescriptor, 3, sharedConfig.themeConfig);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 4) ? true : sharedConfig.flag) {
         compositeEncoder.encodeBooleanElement(serialDescriptor, 4, sharedConfig.flag);
      }
   }

   public SharedConfig(
      int value, String text, String text2, String text3, String text4, boolean flag, SerializationConstructorMarker serializationConstructorMarker
   ) {
      if ((0) != 0) {
         PluginExceptionsKt.throwMissingFieldException(value, 0, SharedConfig$$serializer.INSTANCE.getDescriptor());
      }

      super();
      if ((value) == 0) {
         this.modulesConfig = "default";
      } else {
         this.modulesConfig = text;
      }

      if ((value) == 0) {
         this.widgetsConfig = "default";
      } else {
         this.widgetsConfig = text2;
      }

      if ((value) == 0) {
         this.inventoriesConfig = "default";
      } else {
         this.inventoriesConfig = text3;
      }

      if ((value) == 0) {
         this.themeConfig = Colors.getThemeConfig().getId();
      } else {
         this.themeConfig = text4;
      }

      if ((value) == 0) {
         this.flag = false;
      } else {
         this.flag = flag;
      }
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)SharedConfig$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}