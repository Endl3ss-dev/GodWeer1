package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1657;

@CTransformer(class_1657.class)
public class S0576 {
   @CInject(method = "resetTicksSince", target = @CTarget("HEAD"), cancellable = true)
   public void a(InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.dC(S0190.dB())) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
