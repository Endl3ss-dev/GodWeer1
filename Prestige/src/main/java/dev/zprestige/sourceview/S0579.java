package dev.zprestige.sourceview;

import java.util.Optional;
import net.lenni0451.classtransform.InjectionCallback;
import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CInject;
import net.minecraft.class_2960;
import net.minecraft.class_3304;

@CTransformer(class_3304.class)
public class S0579 {
   @CInject(method = "getResource", target = @CTarget("HEAD"), cancellable = true)
   public void a(class_2960 identifier, InjectionCallback callbackInfoReturnable) {
try {

      if (identifier.method_12836().equals("prestige")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dI(identifier)));
      }

      if (identifier.method_12832().equals("shaders/post/glow.json")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dJ("assets/prestige/shaders/post/glow.json")));
      }

      if (identifier.method_12832().equals("shaders/program/glow.json")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dK("assets/prestige/shaders/program/glow.json")));
      }

      if (identifier.method_12832().equals("shaders/program/glow.fsh")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dL("assets/prestige/shaders/program/glow.fsh")));
      }

      if (identifier.method_12832().equals("shaders/core/blur.json")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dM("assets/minecraft/shaders/core/blur.json")));
      }

      if (identifier.method_12832().equals("shaders/core/blur.fsh")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dN("assets/minecraft/shaders/core/blur.fsh")));
      }

      if (identifier.method_12832().equals("shaders/core/rounded_rect.json")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dO("assets/minecraft/shaders/core/rounded_rect.json")));
      }

      if (identifier.method_12832().equals("shaders/core/rounded_rect.fsh")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dP("assets/minecraft/shaders/core/rounded_rect.fsh")));
      }

      if (identifier.method_12832().equals("shaders/core/rounded_rect_glow.json")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dQ("assets/minecraft/shaders/core/rounded_rect_glow.json")));
      }

      if (identifier.method_12832().equals("shaders/core/rounded_rect_glow.fsh")) {
         callbackInfoReturnable.setReturnValue(Optional.of(S0190.dR("assets/minecraft/shaders/core/rounded_rect_glow.fsh")));
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
