package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.CTarget.Shift;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_310;

@CTransformer(class_310.class)
public class S0571 {
   @CInject(method = "doItemUse", target = @CTarget("HEAD"), cancellable = true)
   public void e(InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.dj(S0190.di())) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "tick", target = @CTarget("HEAD"), cancellable = true)
   public void b(InjectionCallback ci) {
try {

      if (!S0211.w && S0179.f) {
         if (class_310.method_1551().field_1687 != null && class_310.method_1551().field_1724 != null) {
            S0109 event = S0190.dc();
            if (S0190.dd(event)) {
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "tick", target = @CTarget("HEAD"))
   public void c(InjectionCallback ci) {
try {

      if (!S0211.w) {
         S0190.df(S0190.de());
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "doAttack", target = @CTarget("HEAD"), cancellable = true)
   public void f(InjectionCallback cir) {
try {

      if (!S0211.w) {
         if (S0190.dl(S0190.dk())) {
            cir.setReturnValue(false);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "onResolutionChanged", target = @CTarget("TAIL"))
   public void d(InjectionCallback ci) {
try {

      if (!S0211.w) {
         S0190.dh(S0190.dg(class_310.method_1551().method_22683()));
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(
      method = "tick",
      target = @CTarget(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;tick(Z)V", shift = Shift.AFTER),
      cancellable = true
   )
   public void a(InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (class_310.method_1551().field_1687 != null && class_310.method_1551().field_1724 != null) {
            S0179.f = false;
            S0109 event = S0190.da();
            if (S0190.db(event)) {
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "handleBlockBreaking", target = @CTarget("HEAD"), cancellable = true)
   public void g(boolean bl, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.dn(S0190.dm())) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
