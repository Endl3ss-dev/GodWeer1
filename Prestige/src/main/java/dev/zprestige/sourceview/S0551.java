package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_742;
import net.minecraft.class_8685;

@CTransformer(class_742.class)
public class S0551 {
   @CInject(method = "getSkin", target = @CTarget("RETURN"), cancellable = true)
   public void a(InjectionCallback ci) {
try {

      if (!S0211.w && S0190.d(S0211.b, S0103.class)) {
         S0103 event = S0190.e((class_8685)(Object)(ci.getReturnValue()), ((class_742)(Object)(this)).method_5477().getString());
         if (S0190.f(event)) {
            ci.setReturnValue(S0190.g(event));
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
