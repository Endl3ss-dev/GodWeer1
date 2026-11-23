// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({AbstractSignEditScreen.class})
public interface SignEditScreenAccessor {

   @Accessor("sign")
   SignBlockEntity getSign();

   @Accessor("isFrontText")
   boolean isIsFrontText();
}