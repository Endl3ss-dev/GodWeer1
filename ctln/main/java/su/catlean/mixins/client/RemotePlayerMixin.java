// deobf by pusia
package su.catlean.mixins.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import su.catlean.interfaces.Resolvable;

@Mixin({RemotePlayer.class})
public class RemotePlayerMixin extends AbstractClientPlayer implements Resolvable {

   @Unique
   private Vec3 cachePosition = Vec3.ZERO;

   public RemotePlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
      super(clientLevel, gameProfile);
   }

   @Override
   public void savePosition(Vec3 vec3) {
      this.cachePosition = vec3;
   }

   @Override
   public Vec3 getSavedPosition() {
      return this.cachePosition;
   }
}