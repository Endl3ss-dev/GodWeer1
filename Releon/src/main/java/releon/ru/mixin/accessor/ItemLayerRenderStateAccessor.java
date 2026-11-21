package releon.ru.mixin.accessor;

import net.minecraft.client.render.item.ItemRenderState.Glint;
import net.minecraft.client.render.item.ItemRenderState.LayerRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.json.Transformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LayerRenderState.class})
public interface ItemLayerRenderStateAccessor {
   @Accessor("glint")
   Glint releon$getFoilType();

   @Accessor("transform")
   Transformation releon$getItemTransform();

   @Accessor("useLight")
   boolean releon$getUsesBlockLight();

   @Accessor("specialModelType")
   SpecialModelRenderer<Object> releon$getSpecialRenderer();

   @Accessor("tints")
   int[] releon$getTintLayers();
}
