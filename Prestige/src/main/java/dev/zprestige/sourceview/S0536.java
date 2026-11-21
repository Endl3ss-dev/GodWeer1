package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_2794;
import net.minecraft.class_5539;
import net.minecraft.class_5868;

@CTransformer(class_5868.class)
public class S0536 {
   @CInject(method = "<init>", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_2794 generator, class_5539 world, InjectionCallback ci) {
try {

      if (generator == null) {
         S0011 accessor = S0190.bY(this);
         S0190.bZ(accessor, -9999999);
         S0190.ca(accessor, 100000000);
         ci.cancel();
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
