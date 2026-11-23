// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({SoundEngine.class})
public interface SoundEngineAccessor {

   @Accessor("loaded")
   boolean isLoaded();
}