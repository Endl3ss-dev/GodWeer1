// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MouseHandler.class})
public interface MouseHandlerAccessor {

   @Invoker("onButton")
   void iMouseClick(long value, MouseButtonInfo mouseButtonInfo, int value2);
}