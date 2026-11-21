package net.skycore.client.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;

/** Explains the deliberately removed remote chat feature. */
public final class IrcCommand extends ClientCommand {
   public IrcCommand() {
      super("irc");
   }

   @Override
   public void execute(LiteralArgumentBuilder<ClientSuggestionProvider> command) {
      command.executes(context -> unavailable());
      command.then(arg("message", StringArgumentType.greedyString()).executes(context -> unavailable()));
   }

   private static int unavailable() {
      ChatMessages.displayInfo("Общий IRC-чат отключён в офлайн-сборке. Для локальной группы: .party");
      return 1;
   }
}
