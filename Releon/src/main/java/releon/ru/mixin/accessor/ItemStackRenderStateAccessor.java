package releon.ru.mixin.accessor;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.ItemRenderState.LayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ItemRenderState.class})
public interface ItemStackRenderStateAccessor {
   @Accessor("layerCount")
   int releon$getActiveLayerCount();

   @Accessor("layers")
   LayerRenderState[] releon$getLayers();
}
