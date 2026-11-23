// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({BrewingStandBlockEntity.class})
public interface BrewingStandBlockEntityAccessor {

   @Accessor("brewTime")
   int getBrewTime();
}