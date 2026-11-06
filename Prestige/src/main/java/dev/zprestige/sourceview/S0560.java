package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1297;
import net.minecraft.class_638;
import net.minecraft.class_1297.class_5529;

@CTransformer(class_638.class)
public class S0560 {
   @CInject(method = "removeEntity", target = @CTarget("HEAD"))
   public void b(int entityId, class_5529 removalReason, InjectionCallback ci) {
try {

      if (!S0211.w) {
         class_638 world = (class_638)(Object)(this);
         if (world != null) {
            class_1297 entity = world.method_8469(entityId);
            if (entity != null) {
               S0061 event = S0190.ay(entity, S0599.POST);
               S0190.az(event);
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "addEntity", target = @CTarget("TAIL"))
   public void a(class_1297 entity, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (entity != null) {
            S0061 event = S0190.aw(entity, S0599.PRE);
            S0190.ax(event);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
