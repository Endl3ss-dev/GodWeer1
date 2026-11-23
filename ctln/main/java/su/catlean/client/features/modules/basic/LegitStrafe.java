// deobf by pusia
package su.catlean.client.features.modules.basic;

import kotlin.math.MathKt;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.enums.SwitchMode;
import su.catlean.api.event.events.player.KeyboardInputEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.RotationManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ClientInputAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class LegitStrafe extends Module {
   @NotNull
   public static final LegitStrafe INSTANCE = new LegitStrafe();
   private static boolean flag;

   private LegitStrafe() {
      super("legit-strafe", SubCategories.getSubCategory4(), null, 4, null);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      boolean flag2 = WorldUtil.INSTANCE.isFlag4()
         && (!MinecraftHolder.getMinecraft().options.keyUp.isDown() || MinecraftHolder.getMinecraft().options.keyDown.isDown());
      if (flag2) {
         RotationManager.INSTANCE
            .updateRotation(new Rotation(WorldUtil.INSTANCE.getAmount6(), MinecraftHolder.getLocalPlayer().getXRot(), false, SwitchMode.FORCE, 4, null), 0);
         flag = true;
      }
   }

   @Flow(
      priority = 10
   )
   private final void onKeyboardInput(KeyboardInputEvent keyboardInputEvent) {
      if (flag) {
         flag = false;
         float value = MinecraftHolder.getLocalPlayer().input.getMoveVector().y;
         float value2 = MinecraftHolder.getLocalPlayer().input.getMoveVector().x;
         float yRot = (MinecraftHolder.getLocalPlayer().getYRot() - WorldUtil.INSTANCE.getAmount6()) * (float) (Math.PI / 180.0);
         float value3 = (float)Math.cos(yRot);
         float value4 = (float)Math.sin(yRot);
         ((ClientInputAccessor)MinecraftHolder.getLocalPlayer().input)
            .setMoveVector(new Vec2(MathKt.roundToInt(value2 * value3 - value * value4), MathKt.roundToInt(value * value3 + value2 * value4)));
      }
   }
}