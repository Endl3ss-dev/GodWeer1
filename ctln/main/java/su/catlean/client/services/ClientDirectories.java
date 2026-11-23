// deobf by pusia
package su.catlean.client.services;

import java.io.File;
import org.jetbrains.annotations.NotNull;

public final class ClientDirectories {
   @NotNull
   private static final File file = new File(System.getProperty("user.home") + "/.ctl");
   @NotNull
   private static final File file2 = new File(file + "/inventories");
   @NotNull
   private static final File file3 = new File(file + "/stashes");
   @NotNull
   private static final File[] files;

   @NotNull
   public static final File getFile() {
      return file;
   }

   @NotNull
   public static final File getFile2() {
      return file2;
   }

   @NotNull
   public static final File getFile3() {
      return file3;
   }

   @NotNull
   public static final File[] getFiles() {
      return files;
   }

   static {
      File[] files2 = new File[]{file, file2, file3};
      files = files2;
   }
}