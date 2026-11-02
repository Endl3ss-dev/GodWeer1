package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_2535;
import net.minecraft.class_2547;
import net.minecraft.class_2596;

@CTransformer(class_2535.class)
public class S0556 {
   @CInject(method = "send(Lnet/minecraft/network/packet/Packet;)V", target = @CTarget("HEAD"), cancellable = true)
   public void b(class_2596<?> packet, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0211.b != null && S0190.X(S0211.b, S0138.class)) {
            S0138 event = S0190.Y(packet);
            if (S0190.Z(event)) {
               ci.cancel();
               return;
            }
         }

         if (S0211.u != null) {
            S0190.aa(S0211.u, packet);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "handlePacket", target = @CTarget("HEAD"), cancellable = true)
   public static <T extends class_2547> void a(class_2596<T> packet, class_2547 listener, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.U(S0211.b, S0137.class)) {
            S0137 event = S0190.V(packet);
            if (S0190.W(event)) {
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
