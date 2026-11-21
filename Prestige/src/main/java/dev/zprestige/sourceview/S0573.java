package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_11910;
import net.minecraft.class_312;

@CTransformer(class_312.class)
public class S0573 {
   @CShadow("cursorDeltaX")
   public double cursorDeltaX;
   @CShadow("cursorDeltaY")
   public double cursorDeltaY;

   @CInject(method = "onMouseButton", target = @CTarget("HEAD"))
   public void b(long window, class_11910 input, int action, InjectionCallback ci) {
try {

      int button = input.comp_4801();
      if (!S0211.w) {
         if (button != -1) {
            S0190.dw(S0190.dv(button, action));
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "updateMouse", target = @CTarget("HEAD"), cancellable = true)
   public void a(double timeDelta, InjectionCallback ci) {
try {

      if (!S0211.w && S0190.ds(S0211.b, S0113.class)) {
         S0113 event = S0190.dt(this.cursorDeltaX, this.cursorDeltaY);
         if (S0190.du(event)) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
