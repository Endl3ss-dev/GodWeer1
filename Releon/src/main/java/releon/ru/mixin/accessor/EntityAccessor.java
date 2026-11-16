package releon.ru.mixin.accessor;

import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.tag.TagKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Entity.class})
public interface EntityAccessor {
   @Accessor("horizontalCollision")
   boolean releon$isHorizontalCollision();

   @Accessor("verticalCollision")
   boolean releon$isVerticalCollision();

   @Accessor("fluidHeight")
   Object2DoubleMap<TagKey<Fluid>> releon$getFluidHeight();

   @Accessor("submergedFluidTag")
   Set<TagKey<Fluid>> releon$getSubmergedFluidTag();
}
