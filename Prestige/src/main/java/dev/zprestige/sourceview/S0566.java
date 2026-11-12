package dev.zprestige.sourceview;

import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CModifyArgs;
import net.lenni0451.classtransform.utils.CArgs;
import net.minecraft.class_5223;

@CTransformer(class_5223.class)
public class S0566 {
   @CModifyArgs(
      method = "visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
      target = @CTarget(
         value = "INVOKE",
         target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
         ordinal = 0
      )
   )
   public static void a(CArgs cArgs) {
try {

      String text = (String)(Object)(cArgs.get(0));
      if (text != null) {
         if (!S0211.w) {
            if (!S0190.el(S0211.b, S0133.class)) {
               return;
            }

            S0133 event = S0190.em("", text);
            if (S0190.en(event)) {
               cArgs.set(0, text.replace(S0190.eo(event), S0190.ep(event)));
            }
         } else {
            cArgs.set(0, text);
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
