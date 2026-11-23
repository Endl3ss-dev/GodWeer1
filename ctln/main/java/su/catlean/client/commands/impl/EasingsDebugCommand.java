// deobf by pusia
package su.catlean.client.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.commands.Command;
import su.catlean.client.features.ui.debug.EasingsDebugScreen;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;

public final class EasingsDebugCommand extends Command {
   @NotNull
   public static final EasingsDebugCommand INSTANCE = new EasingsDebugCommand();

   private EasingsDebugCommand() {
      super("easingsdebug");
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      builder.executes(
         arg -> {
            TaskScheduler.defaultImpl(
               TaskScheduler.INSTANCE, 10, (byte)0, false, () -> MinecraftHolder.getMinecraft().gui.setScreen(new EasingsDebugScreen()), 6, null
            );
            return 1;
         }
      );
   }
}