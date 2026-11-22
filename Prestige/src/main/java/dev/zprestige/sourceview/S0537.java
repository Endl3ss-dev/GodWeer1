package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1306;
import net.minecraft.class_4587;
import net.minecraft.class_759;

@CTransformer(class_759.class)
public abstract class S0537 {
   @CInject(method = "swingArm", target = @CTarget("HEAD"), cancellable = true)
   public void a(float swingProgress, class_4587 matrixStack, int i, class_1306 arm, InjectionCallback ci) {
try {

      if (!S0211.w && S0190.cb(S0211.b, S0149.class)) {
         S0149 event = S0190.cc(matrixStack, arm, swingProgress, 1.0F);
         if (S0190.cd(event)) {
            matrixStack.method_22905(2.0F, 2.0F, 2.0F);
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
