package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import net.minecraft.class_310;

@CTransformer(class_309.class)
public class S0542 {
   @CShadow("client")
   public class_310 client;

   @CInject(method = "onKey", target = @CTarget("HEAD"), cancellable = true)
   public void a(long window, int action, class_11908 input, InjectionCallback ci) {
try {

      int key = input.method_74228();
      if (!S0211.w) {
         if (key != -1) {
            S0065 event = S0190.cx(key, action);
            if (S0190.cy(event)) {
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
