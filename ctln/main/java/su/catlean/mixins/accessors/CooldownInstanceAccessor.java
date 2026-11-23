// deobf by pusia
package su.catlean.mixins.accessors;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.gen.Accessor;

@Pseudo
@Mixin(
   targets = {"net.minecraft.world.item.ItemCooldowns$CooldownInstance"}
)
public interface CooldownInstanceAccessor {

   @Accessor("endTime")
   int getEndTime();
}