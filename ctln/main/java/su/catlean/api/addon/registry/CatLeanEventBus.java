// deobf by pusia
package su.catlean.api.addon.registry;

public interface CatLeanEventBus {
   void subscribe(Object object);

   void unsubscribe(Object object);

   void post(Object object);
}