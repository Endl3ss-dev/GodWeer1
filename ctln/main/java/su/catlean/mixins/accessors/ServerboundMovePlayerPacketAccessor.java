// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ServerboundMovePlayerPacket.class})
public interface ServerboundMovePlayerPacketAccessor {

   @Mutable
   @Accessor("onGround")
   void setGround(boolean flag);
}