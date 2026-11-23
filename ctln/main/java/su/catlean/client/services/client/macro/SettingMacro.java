// deobf by pusia
package su.catlean.client.services.client.macro;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.setting.values.Bind;
import su.catlean.api.setting.values.Bind$$serializer;

@Serializable
public final class SettingMacro implements Macro {
   @NotNull
   public static final SettingMacro.Companion companion = new SettingMacro.Companion(null);
   @NotNull
   private String id;
   @NotNull
   private Bind bind;
   private boolean flag;
   @NotNull
   private String module;
   @NotNull
   private String setting;
   @NotNull
   private String pressValue;
   @NotNull
   private String releaseValue;
   private boolean flag2;

   public SettingMacro(String id, Bind bind, boolean flag, String module, String setting, String pressValue, String releaseValue, boolean flag2) {
      this.id = id;
      this.bind = bind;
      this.flag = flag;
      this.module = module;
      this.setting = setting;
      this.pressValue = pressValue;
      this.releaseValue = releaseValue;
      this.flag2 = flag2;
   }

   @NotNull
   @Override
   public String getId() {
      return this.id;
   }

   @Override
   public void updateText(String text) {
      this.id = text;
   }

   @NotNull
   @Override
   public Bind getBind() {
      return this.bind;
   }

   @Override
   public void updateBind(Bind bind) {
      this.bind = bind;
   }

   @Override
   public boolean isFlag() {
      return this.flag;
   }

   @Override
   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   @NotNull
   public final String getModule() {
      return this.module;
   }

   public final void updateText2(String text) {
      this.module = text;
   }

   @NotNull
   public final String getSetting() {
      return this.setting;
   }

   public final void updateText3(String text) {
      this.setting = text;
   }

   @NotNull
   public final String getPressValue() {
      return this.pressValue;
   }

   public final void updateText4(String text) {
      this.pressValue = text;
   }

   @NotNull
   public final String getReleaseValue() {
      return this.releaseValue;
   }

   public final void updateText5(String text) {
      this.releaseValue = text;
   }

   public final boolean isFlag2() {
      return this.flag2;
   }

   public final void setFlag2(boolean flag) {
      this.flag2 = flag;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final Bind getBind2() {
      return this.bind;
   }

   public final boolean isFlag3() {
      return this.flag;
   }

   @NotNull
   public final String getModule2() {
      return this.module;
   }

   @NotNull
   public final String getSetting2() {
      return this.setting;
   }

   @NotNull
   public final String getPressValue2() {
      return this.pressValue;
   }

   @NotNull
   public final String getReleaseValue2() {
      return this.releaseValue;
   }

   public final boolean isFlag4() {
      return this.flag2;
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag22 = this.flag2;
      String releaseValue2 = this.releaseValue;
      String pressValue2 = this.pressValue;
      String setting2 = this.setting;
      String module2 = this.module;
      boolean flag3 = this.flag;
      Bind bind2 = this.bind;
      String text = this.id;
      return "SettingMacro(id="
         + text
         + ", bind="
         + bind2
         + ", enabled="
         + flag3
         + ", module="
         + module2
         + ", setting="
         + setting2
         + ", pressValue="
         + pressValue2
         + ", releaseValue="
         + releaseValue2
         + ", hold="
         + flag22
         + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      hashCode = hashCode * 31 + this.bind.hashCode();
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      hashCode = hashCode * 31 + this.module.hashCode();
      hashCode = hashCode * 31 + this.setting.hashCode();
      hashCode = hashCode * 31 + this.pressValue.hashCode();
      hashCode = hashCode * 31 + this.releaseValue.hashCode();
      return hashCode * 31 + Boolean.hashCode(this.flag2);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof SettingMacro settingMacro)) {
         return false;
      } else if (!Intrinsics.areEqual(this.id, settingMacro.id)) {
         return false;
      } else if (!Intrinsics.areEqual(this.bind, settingMacro.bind)) {
         return false;
      } else if (this.flag != settingMacro.flag) {
         return false;
      } else if (!Intrinsics.areEqual(this.module, settingMacro.module)) {
         return false;
      } else if (!Intrinsics.areEqual(this.setting, settingMacro.setting)) {
         return false;
      } else if (!Intrinsics.areEqual(this.pressValue, settingMacro.pressValue)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.releaseValue, settingMacro.releaseValue) ? false : this.flag2 == settingMacro.flag2;
      }
   }

   @JvmStatic
   public static final void applyEncodeStringElement(SettingMacro settingMacro, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeStringElement(serialDescriptor, 0, settingMacro.getId());
      compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)Bind$$serializer.INSTANCE, settingMacro.getBind());
      compositeEncoder.encodeBooleanElement(serialDescriptor, 2, settingMacro.isFlag());
      compositeEncoder.encodeStringElement(serialDescriptor, 3, settingMacro.module);
      compositeEncoder.encodeStringElement(serialDescriptor, 4, settingMacro.setting);
      compositeEncoder.encodeStringElement(serialDescriptor, 5, settingMacro.pressValue);
      compositeEncoder.encodeStringElement(serialDescriptor, 6, settingMacro.releaseValue);
      compositeEncoder.encodeBooleanElement(serialDescriptor, 7, settingMacro.flag2);
   }

   public SettingMacro(
      int value,
      String text,
      Bind bind,
      boolean flag,
      String text2,
      String text3,
      String text4,
      String text5,
      boolean flag2,
      SerializationConstructorMarker serializationConstructorMarker
   ) {
      if (255 != (0xFF)) {
         PluginExceptionsKt.throwMissingFieldException(value, 255, SettingMacro$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.bind = bind;
      this.flag = flag;
      this.module = text2;
      this.setting = text3;
      this.pressValue = text4;
      this.releaseValue = text5;
      this.flag2 = flag2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)SettingMacro$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}