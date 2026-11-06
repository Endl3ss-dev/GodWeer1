package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2818;

@CTransformer(class_2818.class)
public class S0568 {
   @CShadow("world")
   public class_1937 world;

   @CInject(method = "setBlockState", target = @CTarget("TAIL"))
   public void a(class_2338 pos, class_2680 state, int flags, InjectionCallback cir) {
try {

      if (!S0211.w) {
         if (S0190.ex(S0211.b, S0054.class)) {
            if (this.world.method_8608()) {
               S0054 event = S0190.ey(pos, state);
               S0190.ez(event);
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
