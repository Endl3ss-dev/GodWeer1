package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_2338;
import net.minecraft.class_852;

@CTransformer(class_852.class)
public class S0555 {
   @CInject(method = "markClosed", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_2338 pos, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.R(S0211.b, S0116.class)) {
            if (S0190.T(S0190.S(null))) {
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
