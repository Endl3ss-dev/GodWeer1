package dev.zprestige.sourceview;

import com.mojang.blaze3d.buffers.Std140Builder;
import java.awt.Color;
import java.nio.ByteBuffer;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_758;
import net.minecraft.class_9779;
import org.joml.Vector4f;

@CTransformer(class_758.class)
public class S0552 {
   @CInject(
      method = "applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;",
      target = @CTarget("TAIL"),
      cancellable = true
   )
   public void b(class_4184 camera, int viewDistance, class_9779 tickCounter, float tickProgress, class_638 world, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.u(S0211.b, S0031.class)) {
            S0031 event = S0190.v(0.0F, 0.0F, Color.WHITE);
            if (S0190.w(event)) {
               Color c = S0190.x(event);
               ci.setReturnValue(S0190.y(c.getRed() / 255.0F, c.getGreen() / 255.0F, c.getBlue() / 255.0F, c.getAlpha() / 255.0F));
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V", target = @CTarget("HEAD"), cancellable = true)
   public void a(
      ByteBuffer buffer,
      int bufPos,
      Vector4f fogColor,
      float environmentalStart,
      float environmentalEnd,
      float renderDistanceStart,
      float renderDistanceEnd,
      float skyEnd,
      float cloudEnd,
      InjectionCallback ci
   ) {
try {

      if (!S0211.w) {
         if (S0190.h(S0211.b, S0031.class)) {
            S0031 event = S0190.i(0.0F, 0.0F, Color.WHITE);
            if (S0190.j(event)) {
               Vector4f color = S0190.o(
                  S0190.k(event).getRed() / 255.0F, S0190.l(event).getGreen() / 255.0F, S0190.m(event).getBlue() / 255.0F, S0190.n(event).getAlpha() / 255.0F
               );
               Float skyOv = S0190.p(event);
               float effectiveSkyEnd = skyOv == null ? skyEnd : skyOv;
               buffer.position(bufPos);
               Std140Builder.intoBuffer(buffer)
                  .putVec4(color)
                  .putFloat(S0190.q(event))
                  .putFloat(S0190.r(event))
                  .putFloat(S0190.s(event))
                  .putFloat(S0190.t(event))
                  .putFloat(effectiveSkyEnd)
                  .putFloat(cloudEnd);
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
