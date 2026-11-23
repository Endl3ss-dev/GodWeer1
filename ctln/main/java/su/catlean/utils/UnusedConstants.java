// deobf by pusia
package su.catlean.utils;

import org.jetbrains.annotations.NotNull;

public final class UnusedConstants {
   @NotNull
   public static final UnusedConstants INSTANCE = new UnusedConstants();
   // [deobf] never assigned or read in the original bytecode (ZKM residue); zero-initialized.
   public static final int value = 0;
   public static final int value2 = 0;
   public static final int value3 = 0;
   public static final int value4 = 0;
   public static final int value5 = 0;
   public static final int value6 = 0;

   private UnusedConstants() {
   }
}