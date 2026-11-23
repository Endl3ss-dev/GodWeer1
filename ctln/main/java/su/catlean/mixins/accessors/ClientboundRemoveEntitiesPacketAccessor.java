// deobf by pusia
package su.catlean.mixins.accessors;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({ClientboundRemoveEntitiesPacket.class})
public interface ClientboundRemoveEntitiesPacketAccessor {

   @Accessor("entityIds")
   IntList getIds();

   @Mutable
   @Accessor("entityIds")
   void setIds(IntList intList);
}