package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.ICommand;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.commands.manager.CommandRepository;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.commands.exception.CommandNotFoundException;
import tech.lightclient.commands.helpers.TabCompleteHelper;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static tech.lightclient.commands.IChatControl.FORCE_COMMAND_PREFIX;

public class HelpCommand extends Command {
    protected HelpCommand() {
        super("help");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(1);

        CommandRepository commandRepository = LightClient.getCommandRepository();

        if (!args.hasAny() || args.is(Integer.class)) {
            logDirect("Доступные команды РІ чите:", Formatting.AQUA);

            commandRepository.getRegistry().descendingStream()
                    .filter(command -> !command.hiddenFromHelp())
                    .forEach(command -> {
                        String names = String.join("/", command.getNames());
                        String name = command.getNames().get(0);

                        MutableText namesComponent = Text.literal(names)
                                .styled(style -> style.withColor(Formatting.WHITE));

                        MutableText shortDescComponent = Text.literal(" - " + command.getShortDesc())
                                .styled(style -> style.withColor(Formatting.DARK_GRAY));

                        MutableText hoverComponent = Text.literal("")
                                .append(namesComponent)
                                .append(Text.literal("\n" + command.getShortDesc()))
                                .styled(style -> style.withColor(Formatting.GRAY));

                        MutableText clickableName = Text.literal(name)
                                .styled(style -> style
                                        .withColor(Formatting.GRAY)
                                        .withClickEvent(new ClickEvent.RunCommand(FORCE_COMMAND_PREFIX + "help " + name))
                                        .withHoverEvent(new HoverEvent.ShowText(Text.literal("Нажмите, чтобы посмотреть подробную справку"))));

                        MutableText line = clickableName.copy().append(shortDescComponent);

                        line.styled(style -> style.withHoverEvent(new HoverEvent.ShowText(hoverComponent)));

                        logDirect(line);
                    });

        } else {
            String commandName = args.getString().toLowerCase();
            ICommand command = commandRepository.getCommand(commandName);
            if (command == null) {
                throw new CommandNotFoundException(commandName);
            }

            logDirect("");
            command.getLongDesc().forEach(this::logDirect);
            logDirect("");

            MutableText returnComponent = Text.literal("Нажмите, чтобы вернуться Рє списку команд")
                    .styled(style -> style
                            .withColor(Formatting.AQUA)
                            .withClickEvent(new ClickEvent.RunCommand(FORCE_COMMAND_PREFIX + "help"))
                            .withHoverEvent(new HoverEvent.ShowText(Text.literal("Вернуться Рє общему списку"))));

            logDirect(returnComponent);
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            return new TabCompleteHelper()
                    .addCommands(LightClient.getCommandRepository())
                    .filterPrefix(args.getString())
                    .stream();
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Просмотр всех доступных команд";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "РЎ помощью этой команды можно просмотреть подробную справочную информацию Рѕ том, как использовать определенные команды",
                "",
                "РСЃРїРѕР»СЊР·РѕРІР°РЅРёРµ:",
                "> help - Перечисляет все команды Рё их краткие описания.",
                "> help <command> - Отображение справочной информации по конкретной команде."
        );
    }
}

