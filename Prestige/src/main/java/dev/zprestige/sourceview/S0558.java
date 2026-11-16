package dev.zprestige.sourceview;

import com.mojang.authlib.GameProfile;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1268;
import net.minecraft.class_638;
import net.minecraft.class_742;
import net.minecraft.class_746;

@CTransformer(class_746.class)
public abstract class S0558 extends class_742 {
   public S0558(class_638 world, GameProfile profile)  throws Throwable {
      super(world, profile);
try {

   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "sendMovementPackets", target = @CTarget("HEAD"))
   public void b$return$9b75() {
try {

      if (!S0211.w) {
         S0134.c = this.method_36454();
         S0134.d = this.method_36455();
         S0134 event = S0190.al(S0599.PRE, S0134.c, S0134.d);
         boolean changed = S0190.am(event);
         S0240 movementOverride = S0211.o == null ? null : S0190.an(S0211.o);
         if (movementOverride != null) {
            this.method_36456(S0190.ao(movementOverride));
            this.method_36457(S0190.ap(movementOverride));
         } else if (changed) {
            this.method_36456(S0190.aq(event));
            this.method_36457(S0190.ar(event));
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "sendMovementPackets", target = @CTarget("RETURN"))
   public void c$return$9b75() {
try {

      if (!S0211.w) {
         S0134 event = S0190.as(S0599.POST, S0134.c, S0134.d);
         S0190.at(event);
         this.method_36456(S0134.c);
         this.method_36457(S0134.d);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "swingHand", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_1268 hand, InjectionCallback ci) {
try {

      if (!S0211.w && S0211.b != null && S0190.af(S0211.b, S0106.class)) {
         S0106 event = S0190.ag(S0599.PRE);
         if (S0190.ah(event)) {
            ci.cancel();
         } else if (!S0190.ai(event) && S0190.ak(S0190.aj(S0599.POST))) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
