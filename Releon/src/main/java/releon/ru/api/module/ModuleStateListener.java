package releon.ru.api.module;

@FunctionalInterface
public interface ModuleStateListener {
   void onChanged(Module var1);
}
