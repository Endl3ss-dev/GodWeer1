package fun.volon.api.commands.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import fun.volon.api.commands.Command;
import fun.volon.api.utils.ai.NeuroRecorder;
import fun.volon.api.utils.chat.ChatUtils;

import java.util.List;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

/**
 * Команды для системы записи ручного боя:
 *
 *   .neuro start          — начать запись ручного боя
 *   .neuro stop           — остановить запись
 *   .neuro save <name>    — сохранить в файл
 *   .neuro play <name>    — загрузить и включить воспроизведение
 *   .neuro list           — список профилей
 *   .neuro status         — текущий статус
 *   .neuro off            — выключить воспроизведение
 */
public class NeuroCommand extends Command {

    public NeuroCommand() {
        super("neuro");
    }

    @Override
    public void execute(LiteralArgumentBuilder<CommandSource> builder) {
        builder
            .executes(ctx -> { sendHelp(); return SINGLE_SUCCESS; })

            // .neuro start
            .then(literal("start").executes(ctx -> {
                NeuroRecorder.INSTANCE.startRecording();
                return SINGLE_SUCCESS;
            }))

            // .neuro stop
            .then(literal("stop").executes(ctx -> {
                if (!NeuroRecorder.INSTANCE.isRecording()) {
                    ChatUtils.sendMessage("§c[NeuroRec] Запись не запущена");
                    return SINGLE_SUCCESS;
                }
                NeuroRecorder.INSTANCE.stopRecording();
                return SINGLE_SUCCESS;
            }))

            // .neuro save <name>
            .then(literal("save")
                .then(arg("name", word()).executes(ctx -> {
                    String name = ctx.getArgument("name", String.class);
                    NeuroRecorder.INSTANCE.save(name);
                    return SINGLE_SUCCESS;
                }))
                // .neuro save без имени — авто-имя по времени
                .executes(ctx -> {
                    NeuroRecorder.INSTANCE.save("profile_" + System.currentTimeMillis());
                    return SINGLE_SUCCESS;
                })
            )

            // .neuro play <name>
            .then(literal("play")
                .then(arg("name", word())
                    .suggests((ctx, suggestions) -> {
                        NeuroRecorder.INSTANCE.listProfiles().stream()
                            .filter(n -> n.toLowerCase().startsWith(suggestions.getRemaining().toLowerCase()))
                            .forEach(suggestions::suggest);
                        return suggestions.buildFuture();
                    })
                    .executes(ctx -> {
                        String name = ctx.getArgument("name", String.class);
                        if (NeuroRecorder.INSTANCE.load(name)) {
                            NeuroRecorder.INSTANCE.startReplay();
                        }
                        return SINGLE_SUCCESS;
                    })
                )
            )

            // .neuro off
            .then(literal("off").executes(ctx -> {
                NeuroRecorder.INSTANCE.stopReplay();
                return SINGLE_SUCCESS;
            }))

            // .neuro list
            .then(literal("list").executes(ctx -> {
                List<String> profiles = NeuroRecorder.INSTANCE.listProfiles();
                if (profiles.isEmpty()) {
                    ChatUtils.sendMessage("§7[NeuroRec] Нет сохранённых профилей");
                } else {
                    ChatUtils.sendMessage("§a[NeuroRec] Профили (" + profiles.size() + "):");
                    for (String p : profiles) {
                        ChatUtils.sendMessage("  §f" + p);
                    }
                }
                return SINGLE_SUCCESS;
            }))

            // .neuro status
            .then(literal("status").executes(ctx -> {
                ChatUtils.sendMessage("§a[NeuroRec] " + NeuroRecorder.INSTANCE.getStatus());
                return SINGLE_SUCCESS;
            }));
    }

    private void sendHelp() {
        ChatUtils.sendMessage("§a[NeuroRec] Команды:");
        ChatUtils.sendMessage("  §f.neuro start §7— начать запись ручного боя");
        ChatUtils.sendMessage("  §f.neuro stop §7— остановить запись");
        ChatUtils.sendMessage("  §f.neuro save <имя> §7— сохранить профиль");
        ChatUtils.sendMessage("  §f.neuro play <имя> §7— воспроизвести профиль");
        ChatUtils.sendMessage("  §f.neuro off §7— выключить воспроизведение");
        ChatUtils.sendMessage("  §f.neuro list §7— список профилей");
        ChatUtils.sendMessage("  §f.neuro status §7— текущий статус");
    }
}
