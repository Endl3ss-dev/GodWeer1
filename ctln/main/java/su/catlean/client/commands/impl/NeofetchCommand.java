// deobf by pusia
package su.catlean.client.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.commands.Command;

public final class NeofetchCommand extends Command {
   @NotNull
   public static final NeofetchCommand INSTANCE = new NeofetchCommand();

   private NeofetchCommand() {
      super("neofetch");
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      builder.executes(arg -> 1);
   }
}