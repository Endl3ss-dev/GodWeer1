// deobf by pusia
package su.catlean.api.addon.impl;

import org.slf4j.Logger;
import su.catlean.api.addon.CatLeanAddonLogger;

public final class AddonLoggerImpl implements CatLeanAddonLogger {
   public final Logger logger;

   public AddonLoggerImpl(Logger logger) {
      this.logger = logger;
   }

   @Override
   public void info(String message) {
      this.logger.info(message);
   }

   @Override
   public void warn(String message) {
      this.logger.warn(message);
   }

   @Override
   public void error(String message) {
      this.logger.error(message);
   }

   @Override
   public void error(String message, Throwable throwable) {
      this.logger.error(message, throwable);
   }
}