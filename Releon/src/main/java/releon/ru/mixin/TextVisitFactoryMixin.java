package releon.ru.mixin;

import net.minecraft.text.TextVisitFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import releon.ru.api.drag.impl.ClientLanguage;
import releon.ru.api.events.impl.TextFactoryEvent;
import releon.ru.manager.Manager;

@Mixin({TextVisitFactory.class})
public class TextVisitFactoryMixin {
   public TextVisitFactoryMixin() {
   }

   @ModifyArg(
      method = {"visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/text/TextVisitFactory;visitFormatted(Ljava/lang/String;ILnet/minecraft/text/Style;Lnet/minecraft/text/Style;Lnet/minecraft/text/CharacterVisitor;)Z",
         ordinal = 0
      ),
      index = 0,
      require = 0
   )
   private static String releon$adjustText(String text) {
      TextFactoryEvent event = Manager.postEvent(new TextFactoryEvent(ClientLanguage.tr(text)));
      return event.getText();
   }
}
