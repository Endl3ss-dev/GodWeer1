package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_241;
import net.minecraft.class_310;
import net.minecraft.class_743;
import net.minecraft.class_744;

@CTransformer(class_743.class)
public class S0543 extends class_744 {
   @CInject(method = "tick", target = @CTarget("RETURN"))
   public void b(InjectionCallback ci) {
try {

      if (!S0211.w) {
         S0131 keyboardTickEvent = S0190.cB(S0599.POST);
         S0190.cC(keyboardTickEvent);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "tick", target = @CTarget("RETURN"))
   public void c(InjectionCallback ci) {
try {

      if (!S0211.w && S0211.u != null) {
         if (S0190.cD(S0211.u)) {
            this.field_54155 = S0190.cE(false, false, false, false, false, false, false);
            this.field_55868 = class_241.field_1340;
         } else if (S0190.cF(S0211.u) && this.method_20622()) {
            class_310 client = class_310.method_1551();
            if (client.field_1724 != null && !client.field_1724.method_5624()) {
               client.field_1724.method_5728(true);
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "tick", target = @CTarget("HEAD"))
   public void a(InjectionCallback ci) {
try {

      S0131 event = S0190.cz(S0599.PRE);
      S0190.cA(event);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
