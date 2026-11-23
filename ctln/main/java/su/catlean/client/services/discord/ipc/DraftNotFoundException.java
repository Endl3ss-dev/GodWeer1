// deobf by pusia
package su.catlean.client.services.discord.ipc;

import java.util.Iterator;
import kotlin.jvm.internal.Intrinsics;
import su.catlean.client.features.modules.esp.ChamsEngine;

public final class DraftNotFoundException {
   public static final void applyPrintln(DiscordPresence self, Object draftId) {
      Iterable items = IpcDrafts.INSTANCE.getItems();
      Iterator iterator = items.iterator();

      Object object;
      while (true) {
         if (iterator.hasNext()) {
            Object object2 = iterator.next();
            ChamsEngine chamsEngine = (ChamsEngine)object2;
            if (!Intrinsics.areEqual(chamsEngine.getObject(), draftId)) {
               continue;
            }

            object = object2;
            break;
         }

         object = null;
         break;
      }

      object = (ChamsEngine)object;
      if ((ChamsEngine)object != null) {
         ChamsEngine chamsEngine2 = (ChamsEngine)object;
         DiscordPresence instance = DiscordPresence.INSTANCE;
         instance.updateText(chamsEngine2.getText());
         instance.updateText2(chamsEngine2.getText2());
         instance.updatePair(chamsEngine2.getPair());
         instance.updatePair2(chamsEngine2.getPair2());
         instance.updatePair3(chamsEngine2.getPair3());
         instance.updatePair4(chamsEngine2.getPair4());
         if (instance != null) {
            return;
         }
      }

      System.out.println("Discord IPC - Draft with id " + draftId + " not found");
   }
}