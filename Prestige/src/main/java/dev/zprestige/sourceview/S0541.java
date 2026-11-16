package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1799;

@CTransformer(class_1799.class)
public class S0541 {
   @CInject(method = "setBobbingAnimationTime", target = @CTarget("HEAD"), cancellable = true)
   public void b(int bobbingAnimationTime, InjectionCallback ci) {
try {

      if (!S0211.w && S0190.cu(S0211.b, S0055.class)) {
         if (S0190.cw(S0190.cv())) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "getBobbingAnimationTime", target = @CTarget("HEAD"), cancellable = true)
   public void a(InjectionCallback info) {
try {

      if (!S0211.w && S0190.cr(S0211.b, S0055.class)) {
         if (S0190.ct(S0190.cs())) {
            info.setReturnValue(0);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
