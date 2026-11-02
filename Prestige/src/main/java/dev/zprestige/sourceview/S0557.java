package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_2672;
import net.minecraft.class_2678;
import net.minecraft.class_2818;
import net.minecraft.class_634;
import net.minecraft.class_638;

@CTransformer(class_634.class)
public class S0557 {
   @CShadow("world")
   public class_638 world;

   @CInject(method = "onChunkData", target = @CTarget("TAIL"))
   public void b(class_2672 packet, InjectionCallback info) {
try {

      if (!S0211.w) {
         class_2818 chunk = this.world.method_8497(packet.method_11523(), packet.method_11524());
         S0190.ae(S0190.ad(chunk));
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "onGameJoin", target = @CTarget("TAIL"))
   public void a(class_2678 gameJoinS2CPacket, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (this.world != null) {
            S0034 event = S0190.ab();
            S0190.ac(event);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
