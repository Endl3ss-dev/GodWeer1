package antileak.base.api.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

import antileak.base.Punch;
import antileak.base.api.commands.Command;
import antileak.base.api.utils.chat.ChatUtils;

import java.io.File;
import java.util.Arrays;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config");
    }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> builder) {
        builder
                .then(literal("save")
                        .then(arg("config", word())
                                .suggests((context, builder1) -> {
                                    if (Punch.INSTANCE.configsDir.exists() && Punch.INSTANCE.configsDir.isDirectory()) {
                                        File[] files = Punch.INSTANCE.configsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".punch"));
                                        if (files != null) {
                                            Arrays.stream(files)
                                                    .map(File::getName)
                                                    .map(name -> name.replace(".punch", ""))
                                                    .forEach(builder1::suggest);
                                        }
                                    }
                                    return builder1.buildFuture();
                                })
                                .executes(context -> {
                                    String config = context.getArgument("config", String.class);
                                    try {
                                        Punch.INSTANCE.configStorage.saveConfig(config);
                                        ChatUtils.sendMessage("Конфиг " + config + " успешно сохранён!");
                                    } catch (Exception e) {
                                        ChatUtils.sendMessage("Ошибка при сохранении конфига " + config + "!");
                                        e.printStackTrace();
                                    }
                                    return SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("load")
                        .then(arg("config", word())
                                .suggests((context, builder1) -> {
                                    if (Punch.INSTANCE.configsDir.exists() && Punch.INSTANCE.configsDir.isDirectory()) {
                                        File[] files = Punch.INSTANCE.configsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".punch"));
                                        if (files != null) {
                                            Arrays.stream(files)
                                                    .map(File::getName)
                                                    .map(name -> name.replace(".punch", ""))
                                                    .forEach(builder1::suggest);
                                        }
                                    }
                                    return builder1.buildFuture();
                                })
                                .executes(context -> {
                                    String config = context.getArgument("config", String.class);
                                    try {
                                        Punch.INSTANCE.configStorage.loadConfig(config);
                                        ChatUtils.sendMessage("Конфиг " + config + " успешно загружен!");
                                    } catch (Exception e) {
                                        ChatUtils.sendMessage("Ошибка при загрузке конфига " + config + "!");
                                        e.printStackTrace();
                                    }
                                    return SINGLE_SUCCESS;
                                })
                        )
                )
                .then(literal("list")
                        .executes(context -> {
                            File[] files = Punch.INSTANCE.configsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".punch"));
                            if (files == null || files.length == 0) {
                                ChatUtils.sendMessage("Список конфигов пуст!");
                            } else {
                                StringBuilder builder1 = new StringBuilder();
                                for (int i = 0; i < files.length; i++) {
                                    String fileName = files[i].getName().replace(".punch", "");
                                    builder1.append(fileName);
                                    if (i < files.length - 1) builder1.append(", ");
                                }
                                ChatUtils.sendMessage("Конфиги: " + builder1);
                            }
                            return SINGLE_SUCCESS;
                        })
                )
                .then(literal("dir")
                        .executes(context -> {
                            try {
                                File configsDir = new File(Punch.INSTANCE.globalsDir, "configs");
                                if (!configsDir.exists()) {
                                    configsDir.mkdirs();
                                }
                                new ProcessBuilder("explorer.exe", configsDir.getAbsolutePath()).start();
                                ChatUtils.sendMessage("Папка с конфигами открыта!");
                            } catch (Exception e) {
                                ChatUtils.sendMessage("Ошибка при открытии папки с конфигами!");
                                e.printStackTrace();
                            }
                            return SINGLE_SUCCESS;
                        })
                );
    }
}