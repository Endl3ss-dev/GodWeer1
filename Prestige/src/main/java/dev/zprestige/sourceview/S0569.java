package dev.zprestige.sourceview;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CShadow;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.CTarget.Shift;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import net.minecraft.class_761;
import net.minecraft.class_9779;
import net.minecraft.class_9922;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Vector4f;

@CTransformer(class_761.class)
public class S0569 {
   @CShadow("capturedFrustum")
   public class_4604 frustum;

   @CInject(method = "setupFrustum", target = @CTarget("RETURN"))
   public void b(Matrix4f matrix4f, Matrix4f matrix4f2, class_243 vec3d, InjectionCallback ci) {
try {

      if (!S0211.w) {
         if (S0211.b != null && S0190.eM(S0211.b, S0112.class)) {
            S0112 event = S0190.eN((class_4604)(Object)(ci.getReturnValue()));
            S0190.eO(event);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(method = "reload", target = @CTarget("HEAD"))
   public void c(InjectionCallback ci) {
try {

      if (!S0211.w && S0211.b != null && S0190.eP(S0211.b, S0153.class)) {
         S0190.eR(S0190.eQ());
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @CInject(
      method = "render",
      target = @CTarget(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/FrameGraphBuilder;run(Lnet/minecraft/client/util/ObjectAllocator;Lnet/minecraft/client/render/FrameGraphBuilder$Profiler;)V",
         shift = Shift.AFTER
      )
   )
   public void a(
      class_9922 allocator,
      class_9779 tickCounter,
      boolean renderBlockOutline,
      class_4184 camera,
      Matrix4f theCameraRotation,
      Matrix4f actualRealProjMatrix,
      Matrix4f projectionMatrix,
      GpuBufferSlice fogBuffer,
      Vector4f fogColor,
      boolean renderSky
   ) {
try {

      if (!S0211.w && S0211.b != null) {
         List<Runnable> delayedActions = S0190.eA();
         boolean renderWork = S0190.eB();
         boolean hudWork = S0190.eC();
         if (hudWork) {
            S0239.d.set(actualRealProjMatrix);
            if (!renderWork) {
               S0190.eD();
            }
         }

         if (renderWork || !delayedActions.isEmpty()) {
            S0530 cap = S0190.eE();
            boolean var19 = false /* VF: Semaphore variable */;

            label55: {
               try {
                  var19 = true;
                  S0190.eF(delayedActions);
                  if (!renderWork) {
                     var19 = false;
                     break label55;
                  }

                  Matrix4fStack actualRealModView = RenderSystem.getModelViewStack();
                  S0455 context = S0190.eI(actualRealProjMatrix, S0190.eH(actualRealModView), camera);
                  S0190.eJ(context);
                  var19 = false;
               } finally {
                  if (var19) {
                     S0190.eL(cap);
                  }
               }

               S0190.eK(cap);
               return;
            }

            S0190.eG(cap);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
