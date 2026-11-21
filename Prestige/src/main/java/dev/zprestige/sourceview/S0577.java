package dev.zprestige.sourceview;

import com.mojang.authlib.GameProfile;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_640;
import net.minecraft.class_8685;

@CTransformer(class_640.class)
public class S0577 {
   @CShadow("profile")
   public GameProfile profile;

   @CInject(method = "getSkinTextures", target = @CTarget("RETURN"), cancellable = true)
   public void a(InjectionCallback ci) {
try {

      if (this.profile != null && !S0211.w && S0190.dD(S0211.b, S0103.class)) {
         S0103 event = S0190.dF((class_8685)(Object)(ci.getReturnValue()), S0190.dE(this.profile));
         if (S0190.dG(event)) {
            ci.setReturnValue(S0190.dH(event));
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
