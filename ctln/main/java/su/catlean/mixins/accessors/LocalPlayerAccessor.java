// deobf by pusia
package su.catlean.mixins.accessors;

import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({LocalPlayer.class})
public interface LocalPlayerAccessor {

   @Invoker("sendPosition")
   void iSendMovementPackets();

   @Accessor("yRotLast")
   float getLastYaw();

   @Accessor("xRotLast")
   float getLastPitch();

   @Accessor("yRotLast")
   void setLastYaw(float value);

   @Accessor("xRotLast")
   void setLastPitch(float value);

   @Accessor("xLast")
   double getLastX();

   @Accessor("yLast")
   double getLastBaseY();

   @Accessor("zLast")
   double getLastZ();

   @Accessor("jumpRidingScale")
   void setJumpRidingScale(float value);
}