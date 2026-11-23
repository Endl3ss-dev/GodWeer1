// deobf by pusia
package su.catlean.gofra;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public final class Branch {
   @NotNull
   private final Class<?> klass;
   private final byte priority;
   @NotNull
   private Consumer<Object> consumer;
   private Method privateLookupInMethod;

   public Branch(Class<?> klass, Object obj, Method method) {
      try {
         this.klass = method.getParameters()[0].getType();
         this.priority = method.getAnnotation(Flow.class).priority();
         Class[] classs = new Class[]{Class.class, Lookup.class};
         this.privateLookupInMethod = MethodHandles.class.getDeclaredMethod("privateLookupIn", classs);
         Method privateLookupInMethod2 = this.privateLookupInMethod;
         Object[] objects = new Object[]{klass, MethodHandles.lookup()};
         Lookup invoke = (Lookup)privateLookupInMethod2.invoke(null, objects);
         MethodType methodType = MethodType.methodType(void.class, method.getParameters()[0].getType());
         MethodHandle virtual = invoke.findVirtual(klass, method.getName(), methodType);
         MethodType methodType2 = MethodType.methodType(Consumer.class, klass);
         this.consumer = (Consumer)LambdaMetafactory.metafactory(invoke, "accept", methodType2, MethodType.methodType(void.class, Object.class), virtual, methodType)
            .getTarget()
            .invoke((Object)obj);
      } catch (Throwable exception) {
         throw new RuntimeException(exception);
      }
   }

   @NotNull
   public final Class<?> getEventTarget() {
      return this.klass;
   }

   public final byte getPriority() {
      return this.priority;
   }

   public final void call(Object event) {
      this.consumer.accept(event);
   }
}