// deobf by pusia
package su.catlean.mixins.render;

import net.minecraft.util.StringDecomposer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import su.catlean.api.event.events.render.StringDecomposerEvent;

@Mixin({StringDecomposer.class})
public abstract class StringDecomposerMixin {

   public StringDecomposerMixin() {
   }

   @ModifyVariable(
      method = {"iterateFormatted(Ljava/lang/String;ILnet/minecraft/network/chat/Style;Lnet/minecraft/network/chat/Style;Lnet/minecraft/util/FormattedCharSink;)Z"},
      at = @At("HEAD"),
      ordinal = 0,
      argsOnly = true
   )
   private static String iterateFormattedHook(String text) {
      return StringDecomposerEvent.INSTANCE.call(text);
   }
}