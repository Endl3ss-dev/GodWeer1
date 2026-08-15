package antileak.base.api.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;

import antileak.base.Punch;
import antileak.base.api.commands.Command;
import antileak.base.api.utils.chat.ChatUtils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class FriendCommand extends Command {

    public FriendCommand() {
        super("friend");
    }

    
    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(literal("add")
                        .then(arg("player", word())
                                .suggests((context, builder1) -> {
                                    for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                                        String name = entry.getProfile().getName();
                                        if (name.toLowerCase().startsWith(builder1.getRemaining().toLowerCase())) builder1.suggest(name);
                                    }

                                    return builder1.buildFuture();
                                })
                                .executes(context -> {
                                    String player = context.getArgument("player", String.class);
                                    if (!Punch.INSTANCE.friendStorage.isFriend(player)) {
                                        Punch.INSTANCE.friendStorage.add(player);
                                        ChatUtils.sendMessage("Игрок " + player + " добавлен в друзья!");
                                    } else {
                                        ChatUtils.sendMessage("Игрок " + player + " уже в списке друзей!");
                                    }
                                    return SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("remove")
                        .then(arg("player", word())
                                .suggests((context, builder1) -> {
                                    Punch.INSTANCE.friendStorage.getFriends().stream()
                                            .sorted(String::compareTo)
                                            .filter(name -> name.startsWith(builder1.getRemaining()))
                                            .forEach(builder1::suggest);
                                    return builder1.buildFuture();
                                })
                                .executes(context -> {
                                    String player = context.getArgument("player", String.class);
                                    if (Punch.INSTANCE.friendStorage.isFriend(player)) {
                                        Punch.INSTANCE.friendStorage.remove(player);
                                        ChatUtils.sendMessage("Игрок " + player + " удалён из друзей!");
                                    } else {
                                        ChatUtils.sendMessage("Игрок " + player + " не найден в списке друзей!");
                                    }
                                    return SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("list")
                        .executes(context -> {
                            if (Punch.INSTANCE.friendStorage.getFriends().isEmpty()) {
                                ChatUtils.sendMessage("Список друзей пуст!");
                            } else {
                                StringBuilder builder1 = new StringBuilder();
                                for (int i = 0; i < Punch.INSTANCE.friendStorage.getFriends().size(); i++) {
                                    builder1.append(Punch.INSTANCE.friendStorage.getFriends().get(i));
                                    if (i < Punch.INSTANCE.friendStorage.getFriends().size() - 1) builder1.append(", ");
                                }
                                ChatUtils.sendMessage("Друзья: " + builder1);
                            }

                            return SINGLE_SUCCESS;
                        })
                )
                .then(literal("clear")
                        .executes(context -> {
                            if (!Punch.INSTANCE.friendStorage.isEmpty()) {
                                Punch.INSTANCE.friendStorage.clear();
                                ChatUtils.sendMessage("Список друзей очищен!");
                            } else {
                                ChatUtils.sendMessage("Список друзей пуст!");
                            }
                            return SINGLE_SUCCESS;
                        })
                );
    }
}