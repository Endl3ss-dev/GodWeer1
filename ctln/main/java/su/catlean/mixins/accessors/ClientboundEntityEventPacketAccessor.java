// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ClientboundEntityEventPacket.class})
public interface ClientboundEntityEventPacketAccessor {

   @Accessor("entityId")
   int getEntityId();
}