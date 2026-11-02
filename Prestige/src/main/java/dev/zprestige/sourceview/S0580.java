package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_2960;
import net.minecraft.class_332;
import net.minecraft.class_437;

@CTransformer(class_437.class)
public class S0580 {
   @CInject(method = "renderBackgroundTexture", target = @CTarget("HEAD"), cancellable = true)
   public static void b(class_332 drawContext, class_2960 identifier, int i, int j, float f, float g, int k, int l, InjectionCallback ci) {
try {

      if (!S0211.w) {
         S0051 event = S0190.dU();
         if (S0190.dV(event)) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "renderBackground", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_332 drawContext, int i, int j, float f, InjectionCallback ci) {
try {

      if (!S0211.w) {
         S0051 event = S0190.dS();
         if (S0190.dT(event)) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
