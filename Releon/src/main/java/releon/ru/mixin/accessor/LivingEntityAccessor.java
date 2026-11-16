package releon.ru.mixin.accessor;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({LivingEntity.class})
public interface LivingEntityAccessor {
   @Accessor("jumping")
   boolean releon$isJumping();

   @Accessor("jumpingCooldown")
   int releon$getJumpingCooldown();

   @Accessor("jumpingCooldown")
   void releon$setJumpingCooldown(int var1);
}
