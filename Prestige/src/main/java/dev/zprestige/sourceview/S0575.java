package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_702;
import net.minecraft.class_703;

@CTransformer(class_702.class)
public class S0575 {
   @CInject(method = "addParticle", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_703 particle, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.dy(S0211.b, S0139.class)) {
            S0139 event = S0190.dz();
            if (S0190.dA(event)) {
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
