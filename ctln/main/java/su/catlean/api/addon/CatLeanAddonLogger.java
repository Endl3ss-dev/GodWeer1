// deobf by pusia
package su.catlean.api.addon;

public interface CatLeanAddonLogger {
   void info(String text);

   void warn(String text);

   void error(String text);

   void error(String text, Throwable throwable);
}