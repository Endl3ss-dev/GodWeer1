// deobf by pusia
package su.catlean.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class UnusedHolder {
   // [deobf] never assigned in the original bytecode (ZKM residue).
   @NotNull
   private final String text = "";
   @Nullable
   private Object object;

   @NotNull
   public final String getText() {
      return this.text;
   }

   @Nullable
   public final Object getObject() {
      return this.object;
   }

   public final void setObject(Object object) {
      this.object = object;
   }
}