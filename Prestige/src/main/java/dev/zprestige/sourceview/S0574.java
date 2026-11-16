package dev.zprestige.sourceview;

import java.util.Optional;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_2960;
import net.minecraft.class_3294;

@CTransformer(class_3294.class)
public class S0574 {
   @CInject(method = "getResource", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_2960 identifier, InjectionCallback callbackInfoReturnable) {
try {

      if (identifier.method_12836().equals("prestige")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dx(identifier)));
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
