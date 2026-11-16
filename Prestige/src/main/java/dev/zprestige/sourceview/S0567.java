package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1937;

@CTransformer(class_1937.class)
public class S0567 {
   @CInject(method = "getTimeOfDay", target = @CTarget("RETURN"), cancellable = true)
   public void b(InjectionCallback cir) {
try {

      if (!S0211.w && S0190.et(S0211.b, S0115.class)) {
         S0115 event = S0190.eu((Long)(Object)(cir.getReturnValue()));
         if (S0190.ev(event)) {
            cir.setReturnValue(S0190.ew(event));
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "getRainGradient(F)F", target = @CTarget("RETURN"), cancellable = true)
   public void a(float delta, InjectionCallback cir) {
try {

      if (!S0211.w && S0190.eq(S0211.b, S0140.class)) {
         if (S0190.es(S0190.er())) {
            cir.setReturnValue(0.0F);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
