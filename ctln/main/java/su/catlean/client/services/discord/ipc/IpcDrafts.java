// deobf by pusia
package su.catlean.client.services.discord.ipc;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.esp.ChamsEngine;

public final class IpcDrafts {
   @NotNull
   public static final IpcDrafts INSTANCE = new IpcDrafts();
   @NotNull
   private static final List items = new ArrayList();
   private static boolean flag;
   private static int value;

   private IpcDrafts() {
   }

   @NotNull
   public final List getItems() {
      return items;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      IpcDrafts.flag = flag;
   }

   public final int getValue() {
      return value;
   }

   public final void setValue(int value) {
      IpcDrafts.value = value;
   }

   public final void update() {
      if (flag) {
         DraftNotFoundException.applyPrintln(DiscordPresence.INSTANCE, ((ChamsEngine)items.get(value)).getObject());
         value = (value + 1) % items.size();
      }
   }

   static {
   }
}