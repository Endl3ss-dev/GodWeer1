package dev.zprestige.sourceview;

import java.awt.Color;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_12076;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_9975;

@CTransformer(class_9975.class)
public class S0564 {
   @CInject(method = "updateRenderState", target = @CTarget("TAIL"))
   public void a(class_638 world, float tickDelta, class_4184 camera, class_12076 state, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0190.dY(S0211.b, S0104.class)) {
            int skyPacked = state.field_63097;
            Color initialSky = S0190.dZ(skyPacked >> 16 & 0xFF, skyPacked >> 8 & 0xFF, skyPacked & 0xFF);
            int hzPacked = state.field_63095;
            Color initialHorizon = S0190.ea(hzPacked >> 16 & 0xFF, hzPacked >> 8 & 0xFF, hzPacked & 0xFF);
            S0104 event = S0190.eb(initialSky, initialHorizon);
            if (S0190.ec(event)) {
               if (S0190.ed(event) != null) {
                  Color c = S0190.ee(event);
                  state.field_63097 = 0xFF000000 | (c.getRed() & 0xFF) << 16 | (c.getGreen() & 0xFF) << 8 | c.getBlue() & 0xFF;
               }

               if (S0190.ef(event) != null) {
                  Color h = S0190.eg(event);
                  int origAlpha = hzPacked >> 24 & 0xFF;
                  state.field_63095 = origAlpha << 24 | (h.getRed() & 0xFF) << 16 | (h.getGreen() & 0xFF) << 8 | h.getBlue() & 0xFF;
               }
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
