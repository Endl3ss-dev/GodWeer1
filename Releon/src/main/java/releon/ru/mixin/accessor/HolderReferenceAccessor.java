package releon.ru.mixin.accessor;

import net.minecraft.registry.entry.RegistryEntry.Reference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({Reference.class})
public interface HolderReferenceAccessor<T> {
   @Invoker("setValue")
   void releon$bindValue(T var1);
}
