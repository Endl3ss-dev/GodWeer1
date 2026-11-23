// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.PositionMoveRotation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ClientboundPlayerPositionPacket.class})
public interface ClientboundPlayerPositionPacketAccessor {

   @Mutable
   @Accessor("change")
   void setChange(PositionMoveRotation positionMoveRotation);
}