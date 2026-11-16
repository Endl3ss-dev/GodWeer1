package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_2248;
import net.minecraft.class_2350;
import net.minecraft.class_2680;

@CTransformer(class_2248.class)
public class S0553 {
   @CInject(method = "shouldDrawSide", target = @CTarget("HEAD"), cancellable = true)
   public static void a(class_2680 state, class_2680 otherState, class_2350 side, InjectionCallback cir) {
try {

      if (!S0211.w) {
         if (S0190.z(S0211.b, S0116.class)) {
            S0116 event = S0190.A(state.method_26204());
            if (S0190.B(event)) {
               cir.setReturnValue(S0190.C(event));
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
