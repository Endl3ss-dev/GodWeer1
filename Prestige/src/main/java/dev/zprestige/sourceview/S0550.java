package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_4970.class_4971;

@CTransformer(class_4971.class)
public class S0550 {
   @CInject(
      method = "getAmbientOcclusionLightLevel(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;)F",
      target = @CTarget("RETURN"),
      cancellable = true
   )
   public void a(class_1922 blockView, class_2338 blockPos, InjectionCallback cir) {
try {

      if (!S0211.w) {
         if (S0190.a(S0211.b, S0116.class)) {
            S0116 event = S0190.b(null);
            if (S0190.c(event)) {
               cir.setReturnValue(1.0F);
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
