// deobf by pusia
package su.catlean.gofra;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import kotlin.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Gofra {
   @NotNull
   public static final Gofra INSTANCE = new Gofra();
   @NotNull
   private static final ConcurrentHashMap<Object, List<Branch>> cachedBranches = new ConcurrentHashMap<>();
   @NotNull
   private static final ConcurrentHashMap<Object, List<Branch>> branches = new ConcurrentHashMap<>();

   private Gofra() {
   }

   @Nullable
   public final Unit drain(Object event) {
      List<Branch> classValue = branches.get(event.getClass());
      Unit instance;
      if (classValue != null) {
         List<Branch> list = classValue;

         for (Object object : list) {
            Branch branch = (Branch)object;
            branch.call(event);
         }

         instance = Unit.INSTANCE;
      } else {
         instance = null;
      }

      return instance;
   }

   public final void plugAll(Object... listeners) {
      Object[] listeners2 = listeners;
      int value = 0;

      for (int i = listeners.length; value < i; value++) {
         Object object = listeners2[value];
         INSTANCE.plug(object);
      }
   }

   public final void plug(Object obj) {
      for (Object branche : this.getBranches(obj)) {
         Branch branch = (Branch)branche;
         INSTANCE.insert(branches.computeIfAbsent(branch.getEventTarget(), Gofra::plug$lambda$0$0), branch);
      }
   }

   public final void unplug(Object obj) {
      for (Object branche : this.getBranches(obj)) {
         Branch branch = (Branch)branche;
         List<Branch> eventTarget = branches.get(branch.getEventTarget());
         if (eventTarget != null) {
            eventTarget.remove(branch);
         }
      }
   }

   private final void insert(List<Branch> items, Branch branch) {
      int value = 0;
      Iterator<Branch> iterator = items.iterator();

      int value2;
      while (true) {
         if (!iterator.hasNext()) {
            value2 = -1;
            break;
         }

         Branch object = iterator.next();
         Branch branch2 = (Branch)object;
         if (branch.getPriority() > branch2.getPriority()) {
            value2 = value;
            break;
         }

         value++;
      }

      int size = value2;
      size = size == -1 ? items.size() : size;
      items.add(size, branch);
   }

   private final List<Branch> getBranches(Object object) {
      return cachedBranches.computeIfAbsent(object, it -> Gofra.getBranches$lambda$0(object, it));
   }

   private final void getBranchesRecursive(List<Branch> items, Class<?> classValue, Object object) {
      Method[] declaredMethods = classValue.getDeclaredMethods();
      int value = 0;

      for (int i = declaredMethods.length; value < i; value++) {
         Method method = declaredMethods[value];
         if (method.isAnnotationPresent(Flow.class)) {
            items.add(new Branch(classValue, object, method));
         }
      }

      if (classValue.getSuperclass() != null) {
         this.getBranchesRecursive(items, classValue.getSuperclass(), object);
      }
   }

   private static final List<Branch> plug$lambda$0$0(Object it) {
      return new CopyOnWriteArrayList<>();
   }

   private static final List getBranches$lambda$0(Object object, Object it) {
      CopyOnWriteArrayList copyOnWriteArrayList = new CopyOnWriteArrayList();
      INSTANCE.getBranchesRecursive(copyOnWriteArrayList, object.getClass(), object);
      return copyOnWriteArrayList;
   }
}