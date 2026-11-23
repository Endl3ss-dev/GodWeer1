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
public final class ItemMacro implements Macro {
   @NotNull
   public static final ItemMacro.Companion companion = new ItemMacro.Companion(null);
   @NotNull
   private String id;
   @NotNull
   private Bind bind;
   private boolean flag;
   @NotNull
   private String item;
   private boolean flag2;

   public ItemMacro(String id, Bind bind, boolean flag, String item, boolean flag2) {
      this.id = id;
      this.bind = bind;
      this.flag = flag;
      this.item = item;
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
   public final String getItem() {
      return this.item;
   }

   public final void updateText2(String text) {
      this.item = text;
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
   public final String getItem2() {
      return this.item;
   }

   public final boolean isFlag4() {
      return this.flag2;
   }

   @NotNull
   @Override
   public String toString() {
      boolean flag22 = this.flag2;
      String item2 = this.item;
      boolean flag3 = this.flag;
      Bind bind2 = this.bind;
      String text = this.id;
      return "ItemMacro(id=" + text + ", bind=" + bind2 + ", enabled=" + flag3 + ", item=" + item2 + ", legit=" + flag22 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      hashCode = hashCode * 31 + this.bind.hashCode();
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      hashCode = hashCode * 31 + this.item.hashCode();
      return hashCode * 31 + Boolean.hashCode(this.flag2);
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ItemMacro itemMacro)) {
         return false;
      } else if (!Intrinsics.areEqual(this.id, itemMacro.id)) {
         return false;
      } else if (!Intrinsics.areEqual(this.bind, itemMacro.bind)) {
         return false;
      } else if (this.flag != itemMacro.flag) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.item, itemMacro.item) ? false : this.flag2 == itemMacro.flag2;
      }
   }

   @JvmStatic
   public static final void applyEncodeBooleanElement(ItemMacro itemMacro, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeStringElement(serialDescriptor, 0, itemMacro.getId());
      compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)Bind$$serializer.INSTANCE, itemMacro.getBind());
      compositeEncoder.encodeBooleanElement(serialDescriptor, 2, itemMacro.isFlag());
      compositeEncoder.encodeStringElement(serialDescriptor, 3, itemMacro.item);
      compositeEncoder.encodeBooleanElement(serialDescriptor, 4, itemMacro.flag2);
   }

   public ItemMacro(int value, String text, Bind bind, boolean flag, String text2, boolean flag2, SerializationConstructorMarker serializationConstructorMarker) {
      if (31 != (31)) {
         PluginExceptionsKt.throwMissingFieldException(value, 31, ItemMacro$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.bind = bind;
      this.flag = flag;
      this.item = text2;
      this.flag2 = flag2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ItemMacro$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}