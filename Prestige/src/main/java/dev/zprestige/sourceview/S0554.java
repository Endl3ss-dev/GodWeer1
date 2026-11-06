package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_4184;

@CTransformer(class_4184.class)
public class S0554 {
   @CInject(method = "setRotation", target = @CTarget("HEAD"), cancellable = true)
   public void b(float yaw, float pitch, InjectionCallback ci) {
try {

      if (!S0032.a && !S0211.w) {
         S0032 event = S0190.L(yaw, pitch);
         if (S0190.M(event)) {
            S0032.a = true;
            S0190.Q(S0190.N((class_4184)(Object)(this)), S0190.O(event), S0190.P(event));
            S0032.a = false;
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "setPos", target = @CTarget("HEAD"), cancellable = true)
   public void a(double x, double y, double z, InjectionCallback ci) {
try {

      if (!S0058.a && !S0211.w && S0190.D(S0211.b, S0058.class)) {
         S0058 event = S0190.E(x, y, z);
         if (S0190.F(event)) {
            S0058.a = true;
            S0190.K(S0190.G((class_4184)(Object)(this)), S0190.H(event), S0190.I(event), S0190.J(event));
            S0058.a = false;
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
