package fun.volon.client.modules.impl.movement;


import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.api.utils.chat.ChatUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.TextSetting;

import java.util.concurrent.ThreadLocalRandom;

/**
 * TpBack — автотелепорт домой после смерти (под
 * ToolRise).
 *
 * Логика (тайминги 1-в-1 как в standalone AutoCart — «идеальные»):
 *  1. Игрок умирает → /sethome <homeName> + авто-респавн
 *  2. Через 100 мс → /home <homeName>
 *  3. Если включён чекбокс «Команда после /home» → через 200 мс
 *     отправляется команда из поля (например rtp far). Введённое
 *     пользователем значение нормализуется: лидирующий «/» и пробелы
 *     по краям убираются, чтобы «rtp», «/rtp», « rtp » работали одинаково
 *     и слалось через {@code sendChatCommand} (сервер получит «/rtp»).
 *
 * Задержки хардкожены (100/200 мс) — как в оригинале, не настраиваются.
 */
public class TpBack extends Module {

    public static TpBack INSTANCE = new TpBack();

    // «Идеальные» задержки из оригинального AutoCart (handleTeleportBack)
    private static final long HOME_DELAY_MS = 100L;
    private static final long COMMAND_DELAY_MS = 200L;

    private boolean isDead = false;
    private long deathTimestamp = 0L;

    public BooleanSetting randomName = new BooleanSetting("Рандом название", false);
    public TextSetting homeName = new TextSetting("Название дома", "home", 32)
            .visible(() -> !randomName.isState());

    public BooleanSetting sendAfterCommand = new BooleanSetting("Команда после /home", false);
    public TextSetting afterCommand = new TextSetting("Что писать после /home", "rtp far", 64)
            .visible(() -> sendAfterCommand.isState());

    public TpBack() {
        super("TpBack", "Автотелепорт домой после смерти (под ToolRise)", ModuleCategory.MOVEMENT);
        addSettings(randomName, homeName, sendAfterCommand, afterCommand);
    }

    @Override
    public void onDisable() {
        isDead = false;
        deathTimestamp = 0L;
        super.onDisable();
    }

    @EventLink
    public void onEvent(final EventUpdate event) {
        if (mc.player == null || mc.world == null || mc.player.networkHandler == null) return;

        boolean playerDead = mc.player.getHealth() <= 0 && mc.player.deathTime > 0;

        // Смерть → /sethome + респавн, запускаем цепочку отложенных команд
        if (playerDead && !isDead) {
            isDead = true;
            deathTimestamp = System.currentTimeMillis();
            String home = getHomeName();
            sendCommand("sethome " + home);
            mc.player.requestRespawn();
            ChatUtils.sendMessage("TpBack: сохранён дом \"" + home + "\"");

            // Через 100 мс → /home, затем (если надо) ещё через 200 мс → команда
            scheduleHomeChain(home);
        }

        // Сброс признака смерти, когда игрок снова жив
        if (!playerDead && System.currentTimeMillis() - deathTimestamp > 1000L) {
            isDead = false;
        }
    }

    /**
     * Цепочка отложенных команд в стиле оригинального AutoCart:
     * 100 мс → /home, затем +200 мс → кастомная команда (если включена).
     * Запускается на daemon-потоке, сами пакеты отправляются через mc.execute
     * (в основном потоке клиента), чтобы не было гонок с network handler.
     */
    private void scheduleHomeChain(String home) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(HOME_DELAY_MS);
            } catch (InterruptedException ignored) {
                return;
            }
            mc.execute(() -> {
                if (mc.player == null) return;
                sendCommand("home " + home);
                ChatUtils.sendMessage("TpBack: выполнен /home " + home);

                if (!sendAfterCommand.isState()) return;

                Thread cmdThread = new Thread(() -> {
                    try {
                        Thread.sleep(COMMAND_DELAY_MS);
                    } catch (InterruptedException ignored) {
                        return;
                    }
                    mc.execute(() -> {
                        if (mc.player == null) return;
                        String normalized = normalizeCommand(afterCommand.get());
                        if (!normalized.isEmpty()) {
                            sendCommand(normalized);
                            ChatUtils.sendMessage("TpBack: отправлена команда \"" + normalized + "\"");
                        }
                    });
                });
                cmdThread.setDaemon(true);
                cmdThread.start();
            });
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Возвращает имя дома: рандомное (если включён чекбокс) или введённое вручную.
     * Рандом генерируется каждый раз новый — от 2 до 6 строчных латинских букв.
     */
    private String getHomeName() {
        if (randomName.isState()) {
            return generateRandomName();
        }
        String name = homeName.get().trim();
        return name.isEmpty() ? "home" : name;
    }

    /** Генерирует случайное имя из 2–6 строчных латинских букв */
    private String generateRandomName() {
        ThreadLocalRandom rng = ThreadLocalRandom.current();
        int length = rng.nextInt(2, 7); // [2, 6]
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = (char)('a' + rng.nextInt(26));
        }
        return new String(chars);
    }

    /**
     * Нормализует пользовательский ввод команды.
     * «rtp», «/rtp», « rtp far », «/rtp far» → «rtp» / «rtp far»
     * (без лидирующего слэша — его добавит {@code sendChatCommand}).
     */
    private String normalizeCommand(String input) {
        if (input == null) return "";
        String trimmed = input.trim();
        while (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1).trim();
        }
        return trimmed;
    }

    private void sendCommand(String command) {
        if (mc.player != null && mc.player.networkHandler != null) {
            mc.player.networkHandler.sendChatCommand(command);
        }
    }
}