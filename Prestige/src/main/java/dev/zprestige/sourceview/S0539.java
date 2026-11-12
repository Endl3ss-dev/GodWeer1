package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4603;

@CTransformer(class_4603.class)
public class S0539 {
   @CInject(
      method = "renderFireOverlay",
      target = @CTarget(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;translate(FFF)V"),
      cancellable = true
   )
   public static void a(class_4587 matrices, class_4597 vertexConsumers, InjectionCallback ci) {
try {

      if (!S0211.w) {
         S0029 event = S0190.cp();
         if (S0190.cq(event)) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
