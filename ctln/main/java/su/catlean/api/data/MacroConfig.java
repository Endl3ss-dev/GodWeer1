// deobf by pusia
package su.catlean.api.data;

import java.util.ArrayList;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.Serializable;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.services.client.macro.ChatMacro$$serializer;
import su.catlean.client.services.client.macro.ItemMacro$$serializer;
import su.catlean.client.services.client.macro.SettingMacro$$serializer;

@Serializable
public final class MacroConfig {
   @NotNull
   public static final MacroConfig.Companion companion = new MacroConfig.Companion(null);
   @NotNull
   private final List settingMacros;
   @NotNull
   private final List chatMacros;
   @NotNull
   private final List itemMacros;
   @JvmField
   @NotNull
   private static final Lazy[] lazys;

   public MacroConfig(List settingMacros, List chatMacros, List itemMacros) {
      this.settingMacros = settingMacros;
      this.chatMacros = chatMacros;
      this.itemMacros = itemMacros;
   }

   @NotNull
   public final List getSettingMacros() {
      return this.settingMacros;
   }

   @NotNull
   public final List getChatMacros() {
      return this.chatMacros;
   }

   @NotNull
   public final List getItemMacros() {
      return this.itemMacros;
   }

   @NotNull
   public final List getSettingMacros2() {
      return this.settingMacros;
   }

   @NotNull
   public final List getChatMacros2() {
      return this.chatMacros;
   }

   @NotNull
   public final List getItemMacros2() {
      return this.itemMacros;
   }

   @NotNull
   @Override
   public String toString() {
      List itemMacros2 = this.itemMacros;
      List chatMacros2 = this.chatMacros;
      List settingMacros2 = this.settingMacros;
      return "MacroConfig(settingMacros=" + settingMacros2 + ", chatMacros=" + chatMacros2 + ", itemMacros=" + itemMacros2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.settingMacros.hashCode();
      hashCode = hashCode * 31 + this.chatMacros.hashCode();
      return hashCode * 31 + this.itemMacros.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof MacroConfig macroConfig)) {
         return false;
      } else if (!Intrinsics.areEqual(this.settingMacros, macroConfig.settingMacros)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.chatMacros, macroConfig.chatMacros) ? false : Intrinsics.areEqual(this.itemMacros, macroConfig.itemMacros);
      }
   }

   @JvmStatic
   public static final void applyEncodeSerializableElement(MacroConfig macroConfig, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      Lazy[] lazys2 = lazys;
      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 0) ? true : !Intrinsics.areEqual(macroConfig.settingMacros, new ArrayList())) {
         compositeEncoder.encodeSerializableElement(serialDescriptor, 0, (SerializationStrategy)lazys2[0].getValue(), macroConfig.settingMacros);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 1) ? true : !Intrinsics.areEqual(macroConfig.chatMacros, new ArrayList())) {
         compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)lazys2[1].getValue(), macroConfig.chatMacros);
      }

      if (compositeEncoder.shouldEncodeElementDefault(serialDescriptor, 2) ? true : !Intrinsics.areEqual(macroConfig.itemMacros, new ArrayList())) {
         compositeEncoder.encodeSerializableElement(serialDescriptor, 2, (SerializationStrategy)lazys2[2].getValue(), macroConfig.itemMacros);
      }
   }

   public MacroConfig(int value, List items, List items2, List items3, SerializationConstructorMarker serializationConstructorMarker) {
      if ((0) != 0) {
         PluginExceptionsKt.throwMissingFieldException(value, 0, MacroConfig$$serializer.INSTANCE.getDescriptor());
      }

      super();
      if ((value) == 0) {
         this.settingMacros = new ArrayList();
      } else {
         this.settingMacros = items;
      }

      if ((value) == 0) {
         this.chatMacros = new ArrayList();
      } else {
         this.chatMacros = items2;
      }

      if ((value) == 0) {
         this.itemMacros = new ArrayList();
      } else {
         this.itemMacros = items3;
      }
   }

   public static final Lazy[] getLazys() {
      return lazys;
   }

   static {
      Lazy[] lazys2 = new Lazy[]{
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)SettingMacro$$serializer.INSTANCE))),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)ChatMacro$$serializer.INSTANCE))),
         LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, () -> (KSerializer)(new ArrayListSerializer((KSerializer)ItemMacro$$serializer.INSTANCE)))
      };
      lazys = lazys2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)MacroConfig$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}