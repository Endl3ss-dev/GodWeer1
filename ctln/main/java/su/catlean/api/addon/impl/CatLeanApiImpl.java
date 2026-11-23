// deobf by pusia
package su.catlean.api.addon.impl;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import su.catlean.api.addon.CatLeanAddonLogger;
import su.catlean.api.addon.CatLeanApi;
import su.catlean.api.addon.registry.CatLeanEventBus;
import su.catlean.api.addon.registry.CatLeanRegistry;

public final class CatLeanApiImpl implements CatLeanApi {
   @NotNull
   public static final CatLeanApiImpl INSTANCE = new CatLeanApiImpl();
   @NotNull
   private static final CatLeanRegistry catLeanRegistry = AddonRegistryImpl.INSTANCE;
   @NotNull
   private static final CatLeanEventBus catLeanEventBus = AddonEventBusImpl.INSTANCE;

   private CatLeanApiImpl() {
   }

   @NotNull
   @Override
   public CatLeanRegistry getRegistry() {
      return catLeanRegistry;
   }

   @NotNull
   @Override
   public CatLeanEventBus getEvents() {
      return catLeanEventBus;
   }

   @NotNull
   @Override
   public CatLeanAddonLogger logger(String name) {
      Logger logger = LoggerFactory.getLogger("catlean-addon/" + name);
      return new AddonLoggerImpl(logger);
   }
}