package releon.ru.mixin;

import net.minecraft.client.render.SkyRendering;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({SkyRendering.class})
public class SkyRendererMixin {
   public SkyRendererMixin() {
   }
}
