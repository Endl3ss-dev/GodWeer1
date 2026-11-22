package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_310;
import net.minecraft.class_9919;

@CTransformer(class_9919.class)
public class S0540 {
   @CInject(method = "update", target = @CTarget("HEAD"), cancellable = true)
   public void a(InjectionCallback cir) {
try {

      if (!S0211.w) {
         class_310 mc = class_310.method_1551();
         if (S0211.k != null && mc.field_1755 == S0211.k) {
            int maxFps = (Integer)(Object)(mc.field_1690.method_42524().method_41753());
            cir.setReturnValue(maxFps);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
