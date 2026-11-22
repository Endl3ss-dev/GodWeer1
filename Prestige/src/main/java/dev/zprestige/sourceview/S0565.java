package dev.zprestige.sourceview;

import net.lenni0451.classtransform.annotations.CTarget;
import net.lenni0451.classtransform.annotations.CTransformer;
import net.lenni0451.classtransform.annotations.injection.CModifyArgs;
import net.lenni0451.classtransform.utils.CArgs;
import net.minecraft.class_1657;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_8138.class_8141;

@CTransformer(class_8141.class)
public class S0565 {
   @CModifyArgs(
      method = "getLines",
      target = @CTarget(
         value = "INVOKE",
         target = "Lnet/minecraft/client/font/TextRenderer;wrapLines(Lnet/minecraft/text/StringVisitable;I)Ljava/util/List;",
         ordinal = 0
      )
   )
   public void a(CArgs cArgs) {
try {

      if (!S0211.w && S0190.eh(S0211.b, S0132.class)) {
         class_2561 text = (class_2561)(Object)(cArgs.get(0));
         String stringText = text.getString();
         class_638 world = class_310.method_1551().field_1687;
         if (!stringText.isBlank() && world != null) {
            for (class_1657 player : world.method_18456()) {
               int index = stringText.indexOf(player.method_5820());
               boolean surrounded = index == -1
                  || index > 0 && Character.isLetterOrDigit(stringText.charAt(index - 1))
                  || index + player.method_5820().length() < stringText.length()
                     && Character.isLetterOrDigit(stringText.charAt(index + player.method_5820().length()));
               if (!surrounded) {
                  S0132 labelTextEvent = S0190.ei(player, text);
                  if (S0190.ej(labelTextEvent)) {
                     cArgs.set(0, S0190.ek(labelTextEvent));
                  }
               }
            }
         }
      }
   
} catch (Throwable __sourceFailure) { throw dev.zprestige.sourceview.Sneaky.raise(__sourceFailure); }
}
}
