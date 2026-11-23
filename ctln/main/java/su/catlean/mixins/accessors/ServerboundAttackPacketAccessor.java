// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ServerboundAttackPacket.class})
public interface ServerboundAttackPacketAccessor {

   @Mutable
   @Accessor("entityId")
   void setEntityId(int value);
}