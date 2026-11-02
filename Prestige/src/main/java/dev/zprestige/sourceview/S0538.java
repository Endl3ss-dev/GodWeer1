package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_329;
import net.minecraft.class_332;
import net.minecraft.class_9779;

@CTransformer(class_329.class)
public class S0538 {
   @CInject(method = "renderCrosshair", target = @CTarget("HEAD"), cancellable = true)
   public void b(class_332 context, class_9779 tickCounter, InjectionCallback ci) {
try {

      if (!S0211.w && S0190.cg(S0211.b, S0060.class)) {
         S0060 event = S0190.ch();
         if (S0190.ci(event)) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "renderScoreboardSidebar", target = @CTarget("HEAD"), cancellable = true)
   public void c(class_332 context, class_9779 counter, InjectionCallback ci) {
try {

      if (!S0211.w && S0190.cj(S0211.b, S0154.class)) {
         if (S0190.cl(S0190.ck())) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "renderOverlayMessage", target = @CTarget("HEAD"), cancellable = true)
   public void d(class_332 context, class_9779 tickCounter, InjectionCallback ci) {
try {

      if (!S0211.w && S0190.cm(S0211.b, S0148.class)) {
         if (S0190.co(S0190.cn())) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "renderStatusEffectOverlay", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_332 context, class_9779 tickCounter, InjectionCallback ci) {
try {

      if (!S0211.w) {
         S0105 event = S0190.ce();
         if (S0190.cf(event)) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
