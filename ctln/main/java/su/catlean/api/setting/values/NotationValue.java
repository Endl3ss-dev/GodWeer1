// deobf by pusia
package su.catlean.api.setting.values;

import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

public final class NotationValue {
   @NotNull
   private final String text;

   public NotationValue(String text) {
      this.text = text;
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   @NotNull
   public final String getText2() {
      return this.text;
   }

   @NotNull
   @Override
   public String toString() {
      return "NotationValue(text=" + this.text + ")";
   }

   @Override
   public int hashCode() {
      return this.text.hashCode();
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else {
         return !(object instanceof NotationValue notationValue) ? false : Intrinsics.areEqual(this.text, notationValue.text);
      }
   }
}