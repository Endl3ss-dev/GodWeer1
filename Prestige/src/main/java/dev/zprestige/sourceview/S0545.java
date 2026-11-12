package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;

@CTransformer(class_1309.class)
public abstract class S0545 {
   @CInject(
      method = "jump",
      target = @CTarget(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;addVelocityInternal(Lnet/minecraft/util/math/Vec3d;)V"),
      cancellable = true
   )
   public void e(InjectionCallback ci) {
try {

      if (!S0211.w) {
         class_1309 entity = (class_1309)(Object)(this);
         if (entity == class_310.method_1551().field_1724 && S0211.o != null && S0190.cV(S0211.o) != null) {
            float yaw = entity.method_36454();
            S0136 event = S0190.cW(yaw);
            if (S0190.cX(event)) {
               yaw = S0190.cY(event);
               float f = yaw * (float) (Math.PI / 180.0);
               entity.method_18799(entity.method_18798().method_1031(-class_3532.method_15374(f) * 0.2F, 0.0, class_3532.method_15362(f) * 0.2F));
               entity.field_64356 = true;
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "travel", target = @CTarget("RETURN"))
   public void b(class_243 movementInput) {
try {

      if (!S0211.w) {
         if (class_310.method_1551().field_1724 != null) {
            if (((class_1309)(Object)(this)).equals(class_310.method_1551().field_1724)) {
               if (S0211.o != null && S0190.cM(S0211.o) != null) {
                  S0111 event = S0190.cN(S0599.POST, ((class_1309)(Object)(this)).method_36454(), ((class_1309)(Object)(this)).method_36455());
                  if (S0190.cO(event)) {
                     ((class_1309)(Object)(this)).method_36456(S0111.c);
                     ((class_1309)(Object)(this)).method_36457(S0111.d);
                  }
               }
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "getHandSwingDuration", target = @CTarget("HEAD"), cancellable = true)
   public void c(InjectionCallback ci) {
try {

      if (!S0211.w && S0211.b != null && S0190.cP(S0211.b, S0107.class)) {
         S0107 event = S0190.cQ(0);
         if (S0190.cR(event)) {
            ci.setReturnValue(event.a);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "handleStatus", target = @CTarget("HEAD"))
   public void d(byte status) {
try {

      if (!S0211.w && S0211.b != null && S0190.cS(S0211.b, S0155.class)) {
         if (status == 30) {
            S0190.cU(S0190.cT((class_1309)(Object)(this)));
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "travel", target = @CTarget("HEAD"))
   public void a(class_243 movementInput, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (class_310.method_1551().field_1724 != null) {
            if (((class_1309)(Object)(this)).equals(class_310.method_1551().field_1724)) {
               if (S0211.o != null && S0190.cJ(S0211.o) != null) {
                  S0111 event = S0190.cK(S0599.PRE, ((class_1309)(Object)(this)).method_36454(), ((class_1309)(Object)(this)).method_36455());
                  if (S0190.cL(event)) {
                     S0111.c = ((class_1309)(Object)(this)).method_36454();
                     S0111.d = ((class_1309)(Object)(this)).method_36455();
                     ((class_1309)(Object)(this)).method_36456(event.a);
                     ((class_1309)(Object)(this)).method_36457(event.b);
                  }
               }
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
