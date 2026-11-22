package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_636;

@CTransformer(class_636.class)
public class S0559 {
   @CInject(method = "attackEntity", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_1657 player, class_1297 target, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (class_310.method_1551().field_1724 != null) {
            if (player.equals(class_310.method_1551().field_1724)) {
               S0049 event = S0190.au(target);
               if (S0190.av(event)) {
                  ci.cancel();
               }
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
