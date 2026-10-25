package swerve.modules.impl.misc;

import swerve.acd.AcdCategory;
import swerve.acd.AcdCollector;
import swerve.acd.AcdExporter;
import swerve.acd.AcdFormatter;
import swerve.acd.AcdReport;
import swerve.acd.AcdSeverity;
import swerve.acd.AcdStore;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.ActionSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ListSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.api.setting.TextSetting;

import net.minecraft.client.Minecraft;

/**
 * Диагностический накопитель событий, связанных с работой античита: собирает то, что
 * сервер реально показал клиенту (системные сообщения, причина отключения, коррекция
 * позиции, выданная скорость, неизвестные каналы полезной нагрузки, ошибки обработки
 * пакетов), классифицирует это и умеет отдать весь журнал целиком — в буфер обмена,
 * в файл и в локальный чат.
 *
 * <h2>Чего модуль не делает — и не будет делать</h2>
 * Он не определяет античит по косвенным признакам и не меняет поведение клиента.
 * Внутренние флаги защиты клиенту не отправляются: единственный честный источник —
 * текст и пакеты, которые сервер прислал сам. Поэтому у каждой записи стоит
 * {@link swerve.acd.AcdConfidence}, и «в тексте назван Grim» — предположение о том,
 * что сообщение прислала защита, а не доказательство флага. Здесь нет ни подбора
 * значений под пороги, ни перестановки пакетов, ни подавления проверок: см.
 * {@code Swerve/docs/AntiCheatDetector.md} §5.
 *
 * <h2>Жизненный цикл</h2>
 * <ul>
 *   <li>обработчики живут в миксинах и отписаться не могут, поэтому единственный
 *       выключатель — {@link AcdStore#setCollecting(boolean)}; первой строкой в каждом
 *       источнике проверяется он, и в выключенном состоянии работа заканчивается
 *       чтением одного {@code volatile boolean};</li>
 *   <li>выключение НЕ чистит историю: {@link #onDisable()} пишет служебную запись и
 *       гасит флаг, а {@code AcdStore} продолжает держать буфер и сведения о сессии;</li>
 *   <li>кнопки экспорта — {@link ActionSetting}, они кликабельны и при выключенном
 *       модуле (окно настроек модуля открывается независимо от его состояния),
 *       поэтому «поиграть → выключить → забрать логи» работает без исключений;</li>
 *   <li>настройки передаются в {@link AcdStore} из {@link #onTick()} и перед каждым
 *       действием, чтобы ползунки влияли на поведение и при выключенном модуле.</li>
 * </ul>
 */
public final class AntiCheatDetector extends Module {

    /** Как часто обновлять подпись в окне модуля — не каждый тик, а раз в секунду. */
    private static final long DESCRIPTION_PERIOD_MS = 1_000L;

    private final ModeSetting format = new ModeSetting("Формат отчёта", AcdExporter.FORMAT_TEXT,
            AcdExporter.FORMAT_TEXT, AcdExporter.FORMAT_JSON);

    private final FloatSetting capacity = new FloatSetting("Размер истории", 1_000F,
            AcdStore.MIN_CAPACITY, AcdStore.MAX_CAPACITY, 50F);
    private final FloatSetting mergeWindow = new FloatSetting("Окно склейки повторов, мс", 1_200F,
            0F, 10_000F, 100F);
    private final FloatSetting chatLines = new FloatSetting("Строк в чат", AcdExporter.DEFAULT_CHAT_LINES,
            AcdExporter.MIN_CHAT_LINES, AcdExporter.MAX_CHAT_LINES, 1F);
    /**
     * Временное окно выгрузки, а не сбора: история всегда остаётся полной, а окно
     * отвечает на вопрос «что случилось за последние 10 минут». 0 — без окна.
     */
    private final FloatSetting exportWindow = new FloatSetting("Только за последние, мин", 0F, 0F, 1440F, 15F);

    /** Последняя запись: список в окне модуля не рисуем, но свежие события видеть надо. */
    private final TextSetting lastEntry = new TextSetting("Последняя запись", "—", 120);

    private final ModeSetting severityFloor = new ModeSetting("Минимальная важность",
            AcdSeverity.INFO.label(), labels());

    private final BooleanSetting[] categoryFlags = flags();
    private final ListSetting categories = new ListSetting("Категории", this.categoryFlags);

    private final BooleanSetting showRaw = new BooleanSetting("Печатать исходный текст", true);
    private final BooleanSetting showSource = new BooleanSetting("Печатать источник", true);
    private final BooleanSetting showSession = new BooleanSetting("Печатать номер сессии", true);
    private final ListSetting verbosity = new ListSetting("Подробность", this.showRaw, this.showSource, this.showSession);

    private final BooleanSetting scanChat = new BooleanSetting("Искать совпадения в чате игроков", true);
    private final BooleanSetting announce = new BooleanSetting("Показывать важное в чате сразу", false);

    private final ActionSetting copyAll = new ActionSetting("Скопировать все логи", "Copy All Logs")
            .action(this::copyAll);
    private final ActionSetting saveReport = new ActionSetting("Сохранить отчёт в файл", "Save to file")
            .action(this::saveReport);
    private final ActionSetting showInChat = new ActionSetting("Показать последние в чате", "Show in chat")
            .action(this::showInChat);
    private final ActionSetting clear = new ActionSetting("Очистить историю", "Clear history")
            .action(AntiCheatDetector::clearHistory);

    private long lastDescriptionMs;
    private String lastDescription = "";

    public AntiCheatDetector() {
        super("AntiCheatDetector",
                "Собирает диагностические события античита, хранит историю и отдаёт её целиком",
                ModuleCategory.MISC);
        addSettings(this.format, this.capacity, this.mergeWindow, this.severityFloor, this.categories,
                this.verbosity, this.scanChat, this.announce, this.chatLines, this.exportWindow,
                this.lastEntry, this.copyAll, this.saveReport, this.showInChat, this.clear);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        syncPolicy();
        // Сессию начинаем сразу: включение в середине игры не должно оставаться без
        // привязки к соединению — логин-пакет мы уже могли пропустить.
        AcdStore.beginSession(AcdCollector.sessionLabel());
        AcdStore.setCollecting(true);
        AcdStore.setStopHook(this::disableFromClientThread);
        AcdCollector.noteCollectionStarted();
        refreshDescription(true);
    }

    /**
     * Останов после внутренней ошибки: флагу сборки уже выставлен {@code false}, а модуль
     * гасится в игровом потоке — иначе переключатель в окне продолжал бы показывать «вкл».
     */
    private void disableFromClientThread() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.isSameThread()) {
            setEnabled(false);
            return;
        }
        client.execute(() -> setEnabled(false));
    }

    @Override
    public void onDisable() {
        // Служебная запись — ДО сброса флага: иначе в отчёте не будет видно, когда
        // сбор остановили, и «42 записи за 5 минут» придётся читать вслепую.
        AcdCollector.noteCollectionStopped(AcdStore.size());
        AcdStore.setCollecting(false);
        refreshDescription(true);
    }

    @Override
    public void onTick() {
        syncPolicy();
        // Клиент вышел в меню, а событие отключения нас не догнало (одиночная игра,
        // падение мира): сессию закрываем здесь, иначе отчёт вечно показывает «открыта».
        if (AcdStore.isSessionOpen() && Minecraft.getInstance().getConnection() == null) {
            AcdStore.endSession();
        }
        refreshDescription(false);
    }

    /** Значения настроек → хранилище. Дёшево и без аллокаций: вызывается каждый тик. */
    private void syncPolicy() {
        int want = (int) this.capacity.get();
        if (want != AcdStore.capacity()) {
            AcdStore.setCapacity(want);
        }
        AcdStore.setMergeWindowMs((long) this.mergeWindow.get());
        AcdStore.setSeverityFloor(AcdSeverity.values()[this.severityFloor.getIndex()]);
        AcdStore.setScansChat(this.scanChat.get());
        AcdStore.setAnnounces(this.announce.get());
        AcdCategory[] categories = AcdCategory.values();
        for (int i = 0; i < this.categoryFlags.length; i++) {
            AcdStore.setEnabled(categories[i], this.categoryFlags[i].get());
        }
    }

    private void copyAll() {
        syncPolicy();
        AcdExporter.copyToClipboard(this.format.getCurrent(), options(), windowMs());
    }

    private void saveReport() {
        syncPolicy();
        AcdExporter.exportToFile(this.format.getCurrent(), options(), windowMs());
    }

    private void showInChat() {
        syncPolicy();
        AcdExporter.printToChat((int) this.chatLines.get(), windowMs());
    }

    /** Минуты из настройки в миллисекунды; 0 означает «без окна», и это проверяется в экспортере. */
    private long windowMs() {
        float minutes = this.exportWindow.get();
        return minutes <= 0F ? 0L : (long) (minutes * 60_000F);
    }

    private static void clearHistory() {
        AcdExporter.clearHistory();
    }

    private AcdFormatter.Options options() {
        return new AcdFormatter.Options(this.showRaw.get(), this.showSource.get(), this.showSession.get());
    }

    /**
     * Строка состояния в шапке окна модуля: счётчик записей видно и после выключения,
     * потому что история живёт в {@link AcdStore}, а не в этом инстансе.
     */
    private void refreshDescription(boolean force) {
        long now = System.currentTimeMillis();
        if (!force && now - this.lastDescriptionMs < DESCRIPTION_PERIOD_MS) {
            return;
        }
        this.lastDescriptionMs = now;
        String status = "история: " + AcdStore.statusLine();
        if (force || !status.equals(this.lastDescription)) {
            this.lastDescription = status;
            setDescription(getName() + " — " + status);
        }
        // Одна запись вместо целого снимка: аллокация размера буфера раз в секунду
        // в окне настроек не нужна.
        AcdReport recent = AcdStore.snapshot(1);
        String latest = recent.size() == 0 ? "—"
                : recent.events()[0].severity().label() + " · "
                        + (recent.events()[0].message().isEmpty()
                                ? recent.events()[0].source() : recent.events()[0].message());
        if (force || !latest.equals(this.lastEntry.get())) {
            this.lastEntry.setText(latest);
        }
    }

    private static String[] labels() {
        AcdSeverity[] severities = AcdSeverity.values();
        String[] labels = new String[severities.length];
        for (int i = 0; i < severities.length; i++) {
            labels[i] = severities[i].label();
        }
        return labels;
    }

    private static BooleanSetting[] flags() {
        AcdCategory[] categories = AcdCategory.values();
        BooleanSetting[] settings = new BooleanSetting[categories.length];
        for (int i = 0; i < categories.length; i++) {
            settings[i] = new BooleanSetting(categories[i].label(), true);
        }
        return settings;
    }
}
