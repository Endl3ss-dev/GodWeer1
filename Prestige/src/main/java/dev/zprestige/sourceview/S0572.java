package dev.zprestige.sourceview;

import java.util.Optional;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.lenni0451.classtransform.annotations.injection.CRedirect;
import net.minecraft.class_11683;
import net.minecraft.class_1921;
import net.minecraft.class_3879;
import net.minecraft.class_408;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4618;
import net.minecraft.class_583;
import net.minecraft.class_11661.class_11670;
import net.minecraft.class_4597.class_4598;

@CTransformer(class_11683.class)
public class S0572 {
   @CInject(
      method = "render(Lnet/minecraft/client/render/command/OrderedRenderCommandQueueImpl$ModelCommand;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/OutlineVertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V",
      target = @CTarget("RETURN")
   )
   public void b$return$9b75() {
try {

      if (S0045.e) {
         S0311.u.remove();
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CRedirect(
      method = "render(Lnet/minecraft/client/render/command/OrderedRenderCommandQueueImpl$ModelCommand;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/OutlineVertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V",
      target = @CTarget(
         value = "INVOKE",
         target = "Lnet/minecraft/client/model/Model;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;III)V",
         ordinal = 0
      )
   )
   public void c(class_3879 model, class_4587 matrices, class_4588 originalConsumer, int light, int overlay, int color) {
try {

      class_4588 consumer = originalConsumer;
      class_1921 layer = (class_1921)(Object)(S0311.u.get());
      if (S0045.e
         && layer != null
         && model instanceof class_583<?> entityModel
         && (layer.method_24295() || layer.method_23289().isPresent())
         && S0190.do$java() != null
         && S0190.dp(entityModel)
         && (S0226.b.field_1755 == null || S0226.b.field_1755 instanceof class_408)) {
         Optional<Integer> textureId = S0190.dq(layer);
         Integer id = textureId.orElse(null);
         if (id != null) {
            consumer = S0190.dr(originalConsumer, id);
         }
      }

      model.method_62100(matrices, consumer, light, overlay, color);
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}

   @CInject(
      method = "render(Lnet/minecraft/client/render/command/OrderedRenderCommandQueueImpl$ModelCommand;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/client/render/OutlineVertexConsumerProvider;Lnet/minecraft/client/render/VertexConsumerProvider$Immediate;)V",
      target = @CTarget("HEAD")
   )
   public void a(
      class_11670<?> model, class_1921 renderLayer, class_4588 vertexConsumer, class_4618 outlineVertexConsumers, class_4598 crumblingOverlayVertexConsumers
   ) {
try {

      if (S0045.e) {
         S0311.u.set(renderLayer);
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
