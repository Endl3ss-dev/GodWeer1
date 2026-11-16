package dev.zprestige.sourceview;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.CTarget.Shift;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_10209;
import net.minecraft.class_765;
import net.minecraft.class_9848;

@CTransformer(class_765.class)
public class S0544 {
   @CShadow("glTexture")
   public GpuTexture glTexture;

   @CInject(
      method = "update",
      target = @CTarget(value = "INVOKE", target = "Lnet/minecraft/util/profiler/Profiler;push(Ljava/lang/String;)V", shift = Shift.AFTER),
      cancellable = true
   )
   public void a(float tickProgress, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.cG(S0211.b, S0033.class)) {
            S0033 event = S0190.cH();
            if (S0190.cI(event)) {
               RenderSystem.getDevice().createCommandEncoder().clearColorTexture(this.glTexture, class_9848.method_61324(255, 255, 255, 255));
               class_10209.method_64146().method_15407();
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
