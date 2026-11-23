// deobf by pusia
package su.catlean.client.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.commands.Command;
import su.catlean.client.commands.args.FriendArgumentType;
import su.catlean.client.commands.args.PlayerArgumentType;
import su.catlean.client.services.FriendManager;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.proxy.ProxyManager;

public final class FriendCommand extends Command {
   @NotNull
   public static final FriendCommand INSTANCE = new FriendCommand();

   private FriendCommand() {
      super("friend");
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      builder.then(this.computeLiteralArgumentBuilder("clear").executes(arg -> {
         FriendManager.INSTANCE.applyClear();
         INSTANCE.updateText(LocalizationManager.INSTANCE.computeText("commands.friend.reset", new Object[0]));
         return 1;
      }));
      builder.then(
         this.computeLiteralArgumentBuilder("add")
            .then(this.computeRequiredArgumentBuilder("player", PlayerArgumentType.companion.getPlayerArgumentType()).executes(arg2 -> {
               String argument = (String)arg2.getArgument("player", String.class);
               FriendManager.INSTANCE.testText(argument);
               FriendCommand instance = INSTANCE;
               LocalizationManager instance2 = LocalizationManager.INSTANCE;
               Object[] objects = new Object[]{argument};
               instance.updateText(instance2.computeText("commands.friend.add", objects));
               return 1;
            }))
      );
      builder.then(
         this.computeLiteralArgumentBuilder("remove")
            .then(this.computeRequiredArgumentBuilder("player", FriendArgumentType.companion.getFriendArgumentType()).executes(arg3 -> {
               String argument2 = (String)arg3.getArgument("player", String.class);
               FriendManager.INSTANCE.checkRemove(argument2);
               FriendCommand instance3 = INSTANCE;
               LocalizationManager instance4 = LocalizationManager.INSTANCE;
               Object[] objects2 = new Object[]{argument2};
               instance3.updateText(instance4.computeText("commands.friend.remove", objects2));
               return 1;
            }))
      );
      builder.then(this.computeLiteralArgumentBuilder("list").executes(arg4 -> {
         if (ProxyManager.INSTANCE.getFriendsDataSource().getItems().isEmpty()) {
            INSTANCE.updateText(LocalizationManager.INSTANCE.computeText("commands.friend.empty", new Object[0]));
         } else {
            StringBuilder stringBuilder = new StringBuilder(LocalizationManager.INSTANCE.computeText("commands.friend.friends", new Object[0]));

            for (String item : (Iterable<String>)ProxyManager.INSTANCE.getFriendsDataSource().getItems()) {
               stringBuilder.append(item).append(", ");
            }

            INSTANCE.updateText(StringsKt.dropLast(stringBuilder.toString(), 2));
         }

         return 1;
      }));
      builder.executes(arg5 -> {
         INSTANCE.updateText(ChatFormatting.RED.toString() + "friend add <name> / friend remove <name> / friend list / friend clear");
         return 1;
      });
   }
}