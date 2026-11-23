// deobf by pusia
package su.catlean.client.commands.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.commands.Command;
import su.catlean.client.services.CommandManager;

public final class GpsCommand extends Command {
   @NotNull
   public static final GpsCommand INSTANCE = new GpsCommand();

   private GpsCommand() {
      super("gps");
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      builder.then(this.computeLiteralArgumentBuilder("off").executes(arg -> {
         CommandManager.INSTANCE.setBlockPos(null);
         return 1;
      }));
      builder.then(
         this.computeRequiredArgumentBuilder("x", (ArgumentType)IntegerArgumentType.integer())
            .then(this.computeRequiredArgumentBuilder("z", (ArgumentType)IntegerArgumentType.integer()).executes(arg2 -> {
               Integer argument = (Integer)arg2.getArgument("x", int.class);
               Integer argument2 = (Integer)arg2.getArgument("z", int.class);
               CommandManager.INSTANCE.setBlockPos(new BlockPos(argument, 0, argument2));
               return 1;
            }))
      );
      builder.executes(arg3 -> {
         INSTANCE.updateText("gps <off / x z>");
         return 1;
      });
   }
}