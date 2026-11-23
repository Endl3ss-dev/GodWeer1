// deobf by pusia
package su.catlean.mixins.accessors;

import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ModelPart.class})
public interface ModelPartAccessor {

   @Accessor("cubes")
   List<Cube> getCuboidsList();
}