package dev.zprestige.sourceview;

import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_2394;
import net.minecraft.class_3218;
import net.minecraft.class_3414;
import net.minecraft.class_5362;
import net.minecraft.class_6880;
import net.minecraft.class_1937.class_7867;

@CTransformer(class_3218.class)
public class S0581 {
   @CInject(method = "createExplosion", target = @CTarget("HEAD"))
   public void a(
      class_1297 entity,
      class_1282 damageSource,
      class_5362 behavior,
      double x,
      double y,
      double z,
      float power,
      boolean createFire,
      class_7867 explosionSourceType,
      class_2394 smallParticle,
      class_2394 largeParticle,
      class_6880<class_3414> soundEvent,
      InjectionCallback ci
   ) {
try {

      if (!S0211.w) {
         S0063 event = S0190.dW(power, x, y, z);
         S0190.dX(event);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
