package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_10042;
import net.minecraft.class_583;
import net.minecraft.class_922;

@CTransformer(class_922.class)
public class S0570 {
   @CShadow("model")
   public class_583<?> model;

   @CInject(method = "isVisible", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_10042 state, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0045.e && S0045.f) {
            if (this.model != null) {
               if (S0190.cZ(this.model)) {
                  ci.setReturnValue(true);
               }
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
