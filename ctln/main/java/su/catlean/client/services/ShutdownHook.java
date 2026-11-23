// deobf by pusia
package su.catlean.client.services;

import su.catlean.client.services.discord.ipc.DiscordPresence;
import su.catlean.client.services.proxy.ProxyManager;

public final class ShutdownHook extends Thread {
   public ShutdownHook() {
   }

   @Override
   public void run() {
      ProxyManager.INSTANCE.applyGetDeclaredFields2();
      DiscordPresence.INSTANCE.getUnit();
      super.run();
   }
}