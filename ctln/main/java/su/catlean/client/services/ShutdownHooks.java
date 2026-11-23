// deobf by pusia
package su.catlean.client.services;

import su.catlean.api.IPasteable;

public final class ShutdownHooks {
   public static final void applyAddShutdownHook(IPasteable self) {
      Runtime.getRuntime().addShutdownHook(new ShutdownHook());
   }
}