package dev.zprestige.sourceview;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_10209;
import net.minecraft.class_1041;
import net.minecraft.class_11228;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1533;
import net.minecraft.class_1675;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_757;
import net.minecraft.class_7833;
import net.minecraft.class_9779;
import org.joml.Matrix4f;

@CTransformer(class_757.class)
public class S0535 {
   @CShadow("client")
   public class_310 client;
   @CShadow("guiRenderer")
   public class_11228 guiRenderer;

   @CInject(method = "updateCrosshairTarget", target = @CTarget("HEAD"), cancellable = true)
   public void e(float tickDelta, InjectionCallback ci) {
try {

      if (!S0211.w && S0211.b != null && S0190.bS(S0211.b, S0141.class)) {
         class_1297 entity2 = this.client.method_1560();
         if (entity2 != null) {
            if (this.client.field_1687 != null) {
               class_10209.method_64146().method_15396("pick");
               this.client.field_1692 = null;
               double d = S0190.bT();
               S0141 reachEvent = S0190.bU(0.0F);
               boolean call = S0190.bV(reachEvent);
               if (call) {
                  d += S0190.bW(reachEvent);
               }

               this.client.field_1765 = entity2.method_5745(d, tickDelta, false);
               class_243 vec3d = entity2.method_5836(tickDelta);
               boolean bl = false;
               int i = 3;
               double e = d;
               if (this.client.field_1724.method_68878()) {
                  e = 6.0;
                  d = 6.0;
               } else {
                  if (e > 3.0 && !call) {
                     bl = true;
                  }

                  d = e;
               }

               e *= e;
               if (this.client.field_1765 != null) {
                  e = this.client.field_1765.method_17784().method_1025(vec3d);
               }

               class_243 vec3d2 = entity2.method_5828(1.0F);
               class_243 vec3d3 = vec3d.method_1031(vec3d2.field_1352 * d, vec3d2.field_1351 * d, vec3d2.field_1350 * d);
               float f = 1.0F;
               class_238 box = entity2.method_5829().method_18804(vec3d2.method_1021(d)).method_1009(1.0, 1.0, 1.0);
               class_3966 entityHitResult = class_1675.method_18075(entity2, vec3d, vec3d3, box, S0190.bX(), e);
               if (entityHitResult != null) {
                  class_1297 entity22 = entityHitResult.method_17782();
                  class_243 vec3d4 = entityHitResult.method_17784();
                  double g = vec3d.method_1025(vec3d4);
                  if (bl && g > 9.0) {
                     this.client.field_1765 = class_3965.method_17778(
                        vec3d4, class_2350.method_10142(vec3d2.field_1352, vec3d2.field_1351, vec3d2.field_1350), class_2338.method_49638(vec3d4)
                     );
                  } else if (g < e || this.client.field_1765 == null) {
                     this.client.field_1765 = entityHitResult;
                     if (entity22 instanceof class_1309 || entity22 instanceof class_1533) {
                        this.client.field_1692 = entity22;
                     }
                  }
               }

               class_10209.method_64146().method_15407();
               ci.cancel();
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(target = @CTarget("HEAD"), method = "renderWorld")
   public void b(class_9779 tickCounter) {
try {

      if (!S0211.w && S0190.bH()) {
         class_4184 camera = this.client.field_1773.method_19418();
         class_4587 matrixStack = S0190.bI();
         RenderSystem.getModelViewStack().pushMatrix().mul(matrixStack.method_23760().method_23761());
         matrixStack.method_22907(class_7833.field_40714.rotationDegrees(camera.method_19329()));
         matrixStack.method_22907(class_7833.field_40716.rotationDegrees(camera.method_19330() + 180.0F));
         S0239.e.set(RenderSystem.getModelViewMatrix());
         S0239.f.set(matrixStack.method_23760().method_23761());
         RenderSystem.getModelViewStack().popMatrix();
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(target = @CTarget("HEAD"), method = "tiltViewWhenHurt", cancellable = true)
   public void c(class_4587 matrices, float tickDelta, InjectionCallback ci) {
try {

      if (!S0211.w && S0211.b != null && S0190.bJ(S0211.b, S0110.class)) {
         S0110 event = S0190.bK();
         if (S0190.bL(event)) {
            ci.cancel();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(target = @CTarget("HEAD"), method = "tick")
   public void d() {
try {

      if (!S0211.w && S0211.b != null && S0190.bM(S0211.b, S0030.class)) {
         S0030 event = S0190.bN(0);
         S0190.bO(event);

         for (int i = 0; i < S0190.bP(event); i++) {
            S0190.bR(S0190.bQ((class_757)(Object)(this))).method_70937();
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @CInject(method = "render", target = @CTarget("TAIL"))
   public void a(class_9779 tickCounter, boolean tick) {
try {

      List<Runnable> delayedActions = S0190.bp();
      boolean renderWork = !S0211.w && S0211.b != null && S0190.bq();
      if (!renderWork && delayedActions.isEmpty()) {
         S0190.br();
         S0190.bs();
      } else {
         S0530 cap = S0190.bt();
         boolean var11 = false /* VF: Semaphore variable */;

         label55: {
            try {
               var11 = true;
               S0190.bu(delayedActions);
               if (!renderWork) {
                  S0190.bv();
                  S0190.bw();
                  var11 = false;
                  break label55;
               }

               class_1041 window = this.client.method_22683();
               Matrix4f proj = S0190.bB(
                  S0190.bA(S0190.bz(S0190.by(this.guiRenderer))),
                  (float)window.method_4489() / window.method_4495(),
                  (float)window.method_4506() / window.method_4495()
               );
               S0455 context = S0190.bD(proj, S0190.bC().setTranslation(0.0F, 0.0F, -10000.0F), null);
               S0190.bE(context);
               var11 = false;
            } finally {
               if (var11) {
                  S0190.bG(cap);
               }
            }

            S0190.bF(cap);
            return;
         }

         S0190.bx(cap);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
