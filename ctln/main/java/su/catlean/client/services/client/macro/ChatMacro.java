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
public final class ChatMacro implements Macro {
   @NotNull
   public static final ChatMacro.Companion companion = new ChatMacro.Companion(null);
   @NotNull
   private String id;
   @NotNull
   private Bind bind;
   private boolean flag;
   @NotNull
   private String message;

   public ChatMacro(String id, Bind bind, boolean flag, String message) {
      this.id = id;
      this.bind = bind;
      this.flag = flag;
      this.message = message;
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
   public final String getMessage() {
      return this.message;
   }

   public final void updateText2(String text) {
      this.message = text;
   }

   @NotNull
   public final String getId2() {
      return this.id;
   }

   @NotNull
   public final Bind getBind2() {
      return this.bind;
   }

   public final boolean isFlag2() {
      return this.flag;
   }

   @NotNull
   public final String getMessage2() {
      return this.message;
   }

   @NotNull
   @Override
   public String toString() {
      String message2 = this.message;
      boolean flag2 = this.flag;
      Bind bind2 = this.bind;
      String text = this.id;
      return "ChatMacro(id=" + text + ", bind=" + bind2 + ", enabled=" + flag2 + ", message=" + message2 + ")";
   }

   @Override
   public int hashCode() {
      int hashCode = this.id.hashCode();
      hashCode = hashCode * 31 + this.bind.hashCode();
      hashCode = hashCode * 31 + Boolean.hashCode(this.flag);
      return hashCode * 31 + this.message.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (!(object instanceof ChatMacro chatMacro)) {
         return false;
      } else if (!Intrinsics.areEqual(this.id, chatMacro.id)) {
         return false;
      } else if (!Intrinsics.areEqual(this.bind, chatMacro.bind)) {
         return false;
      } else {
         return this.flag != chatMacro.flag ? false : Intrinsics.areEqual(this.message, chatMacro.message);
      }
   }

   @JvmStatic
   public static final void applyEncodeStringElement(ChatMacro chatMacro, CompositeEncoder compositeEncoder, SerialDescriptor serialDescriptor) {
      compositeEncoder.encodeStringElement(serialDescriptor, 0, chatMacro.getId());
      compositeEncoder.encodeSerializableElement(serialDescriptor, 1, (SerializationStrategy)Bind$$serializer.INSTANCE, chatMacro.getBind());
      compositeEncoder.encodeBooleanElement(serialDescriptor, 2, chatMacro.isFlag());
      compositeEncoder.encodeStringElement(serialDescriptor, 3, chatMacro.message);
   }

   public ChatMacro(int value, String text, Bind bind, boolean flag, String text2, SerializationConstructorMarker serializationConstructorMarker) {
      if (15 != (15)) {
         PluginExceptionsKt.throwMissingFieldException(value, 15, ChatMacro$$serializer.INSTANCE.getDescriptor());
      }

      super();
      this.id = text;
      this.bind = bind;
      this.flag = flag;
      this.message = text2;
   }

   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final KSerializer getKSerializer() {
         return (KSerializer)ChatMacro$$serializer.INSTANCE;
      }

      public Companion(DefaultConstructorMarker defaultConstructorMarker) {
         this();
      }
   }
}