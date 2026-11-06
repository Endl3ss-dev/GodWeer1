package tech.lightclient.commands.commands.defaults;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.client.ILogger;
import tech.lightclient.client.utils.player.PlayerUtils;
import tech.lightclient.commands.Command;
import tech.lightclient.commands.argument.IArgConsumer;
import tech.lightclient.commands.exception.CommandException;
import tech.lightclient.modules.impl.combat.AuraModule;
import tech.lightclient.modules.impl.combat.aura.rotations.LegitNeuroModel;
import tech.lightclient.modules.impl.combat.aura.rotations.LegitNeuroRecorder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.Formatting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class LegitNeuroCommand extends Command implements ILogger {

    protected LegitNeuroCommand() {
        super("lneuro");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            printUsage();
            return;
        }

        String sub = args.getString().toLowerCase();

        switch (sub) {
            case "recode" -> handleRecode(args);
            case "stop" -> handleStop();
            case "train" -> handleTrain(args);
            case "play" -> handlePlay(args);
            default -> printUsage();
        }
    }

    private void handleRecode(IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            logDirect(Formatting.RED + "Укажите название: .lneuro recode <название>", Formatting.GRAY);
            return;
        }
        String name = args.getString();
        if (LegitNeuroRecorder.start(name)) {
            long total = LegitNeuroRecorder.getTotalMs(name)
                    + (System.currentTimeMillis() - 0L) * 0; // текущая сессия добавится при stop
            logDirect("Запись датасета '" + Formatting.YELLOW + name + Formatting.GRAY + "' начата.", Formatting.GRAY);
            logDirect("Ранее записано: " + Formatting.YELLOW + formatMs(LegitNeuroRecorder.getTotalMs(name))
                    + Formatting.GRAY + " (новое время добавится к этому)", Formatting.GRAY);
            logDirect("Сохраняются только подтверждённые попадания (миссы игнорируются).", Formatting.GRAY);
        } else {
            logDirect(Formatting.RED + "Не удалось начать запись (проверьте название).", Formatting.GRAY);
        }
    }

    private void handleStop() {
        if (!LegitNeuroRecorder.isRecording()) {
            logDirect(Formatting.RED + "Запись сейчас не идёт.", Formatting.GRAY);
            return;
        }
        String name = LegitNeuroRecorder.getName();
        LegitNeuroRecorder.stop();
        logDirect("Запись '" + Formatting.YELLOW + name + Formatting.GRAY + "' остановлена. Всего: "
                + Formatting.YELLOW + formatMs(LegitNeuroRecorder.getTotalMs(name)), Formatting.GRAY);
        logDirect("Продолжить: " + Formatting.YELLOW + ".lneuro recode " + name, Formatting.GRAY);
    }

    private void handleTrain(IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            logDirect(Formatting.RED + "Укажите название: .lneuro train <название>", Formatting.GRAY);
            return;
        }
        String name = args.getString();

        long rows = LegitNeuroRecorder.getRowCount(name);
        if (rows == 0) {
            logDirect(Formatting.RED + "В датасете '" + name + "' нет записей!", Formatting.GRAY);
            logDirect("Сначала запишите удары: " + Formatting.YELLOW + ".lneuro recode " + name
                    + Formatting.GRAY + " -> бейте цель (попадания сохраняются) -> " + Formatting.YELLOW + ".lneuro stop",
                    Formatting.GRAY);
            return;
        }
        logDirect("Примеров в датасете: " + Formatting.YELLOW + rows, Formatting.GRAY);

        Path script = findTrainingScript();
        if (script == null) {
            logDirect(Formatting.RED + "Скрипт light_lneuro_train.py не найден рядом с игрой.", Formatting.GRAY);
            return;
        }

        final Path scriptPath = script;
        logDirect("Обучение '" + Formatting.YELLOW + name + Formatting.GRAY + "' запущено (python)...", Formatting.GRAY);

        Thread worker = new Thread(() -> {
            try {
                String[] candidates = {"python", "python3"};
                Process proc = null;
                IOException lastError = null;
                for (String exe : candidates) {
                    try {
                        proc = new ProcessBuilder(exe, scriptPath.toAbsolutePath().toString(), name)
                                .redirectErrorStream(true)
                                .start();
                        break;
                    } catch (IOException e) {
                        lastError = e;
                    }
                }
                if (proc == null) {
                    final IOException err = lastError;
                    logChat(Formatting.RED + "Python не найден: " + (err != null ? err.getMessage() : "?"));
                    return;
                }

                StringBuilder out = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(proc.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        out.append(line).append('\n');
                    }
                }
                int code = proc.waitFor();

                if (code == 0 && LegitNeuroModel.load(name)) {
                    logChat(Formatting.GREEN + "Модель '" + name + "' обучена и загружена!");
                } else {
                    logChat(Formatting.RED + "Обучение завершилось с кодом " + code);
                }
                String tail = out.toString();
                if (tail.length() > 1500) tail = tail.substring(tail.length() - 1500);
                final String output = tail;
                logChat(Formatting.GRAY + output.replace("\n", " | "));
            } catch (Exception e) {
                logChat(Formatting.RED + "Ошибка обучения: " + e.getMessage());
            }
        }, "lneuro-train");
        worker.setDaemon(true);
        worker.start();
    }

    private void handlePlay(IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            logDirect(Formatting.RED + "Укажите название: .lneuro play <название>", Formatting.GRAY);
            return;
        }
        String name = args.getString();
        if (!LegitNeuroModel.load(name)) {
            logDirect(Formatting.RED + "Модель '" + name + "' не найдена. Сначала обучите: .lneuro train " + name,
                    Formatting.GRAY);
            return;
        }
        AuraModule aura = AuraModule.getInstance();
        if (aura != null) {
            aura.selectLegitNeuroEngine();
        }
        logDirect("Под-ротация Neuro активирована с моделью '"
                + Formatting.YELLOW + name + Formatting.GRAY + "'.", Formatting.GRAY);
        logDirect("Включите режим ротации " + Formatting.YELLOW + "Legit" + Formatting.GRAY
                + " в Aura (подротация уже выбрана).", Formatting.GRAY);
    }

    private void printUsage() {
        logDirect("Legit Neuro:", Formatting.GRAY);
        logDirect(Formatting.YELLOW + ".lneuro recode <название>" + Formatting.GRAY + " - начать/продолжить запись", Formatting.GRAY);
        logDirect(Formatting.YELLOW + ".lneuro stop" + Formatting.GRAY + " - остановить запись (время суммируется)", Formatting.GRAY);
        logDirect(Formatting.YELLOW + ".lneuro train <название>" + Formatting.GRAY + " - обучить модель (python)", Formatting.GRAY);
        logDirect(Formatting.YELLOW + ".lneuro play <название>" + Formatting.GRAY + " - играть обученной ротацией", Formatting.GRAY);
    }

    private static void logChat(String message) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client.player == null) return;
        // Из фонового потока обучения - выполняем на главном треде игры
        client.execute(() -> client.inGameHud.getChatHud().addMessage(
                net.minecraft.text.Text.literal(message)));
    }

    private static String formatMs(long ms) {
        long seconds = ms / 1000L;
        return String.format("%d:%02d", seconds / 60, seconds % 60);
    }

    private static Path findTrainingScript() {
        Path gameDir = net.fabricmc.loader.api.FabricLoader.getInstance().getGameDir();
        for (Path candidate : new Path[]{
                gameDir.resolveSibling("light_lneuro_train.py"),
                gameDir.resolve("light_lneuro_train.py"),
                Path.of("light_lneuro_train.py"),
                Path.of("../light_lneuro_train.py")}) {
            if (Files.exists(candidate)) {
                return candidate;
            }
        }
        return null;
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (!args.hasAny()) {
            return Stream.of("recode", "stop", "train", "play").sorted();
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Запись/обучение/проигрывание нейро-ротации Legit";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Нейро-подротация для Legit режима Aura.",
                "",
                "> lneuro recode <название> - начать или продолжить запись датасета",
                "  (сохраняются только попадания; время записи суммируется между сессиями)",
                "> lneuro stop - поставить запись на паузу",
                "> lneuro train <название> - обучить модель через python-скрипт",
                "> lneuro play <название> - включить обученную под-ротацию",
                "",
                "После play выберите в Aura режим ротации Legit и подротацию Neuro."
        );
    }
}
