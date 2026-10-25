package swerve.acd;

import swerve.Swerve;
import swerve.managers.ModuleManager;
import swerve.modules.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.DisconnectionDetails;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.ClientboundDisconnectPacket;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

/**
 * Сбор событий: единственная точка, куда миксины приносят то, что прислал сервер.
 *
 * <h2>Что здесь источник, а что вывод</h2>
 * <ul>
 *   <li>{@link AcdConfidence#CONFIRMED} — факт, который прямо следует из пакета: текст
 *       системного сообщения, причина отключения, бренд сервера, неизвестный канал
 *       полезной нагрузки, ошибка обработки пакета, коррекция позиции;</li>
 *   <li>{@link AcdConfidence#OBSERVED} — то, что измерил клиент: расстояние отката,
 *       выданная сервером скорость, список включённых модулей в момент события;</li>
 *   <li>{@link AcdConfidence#HEURISTIC} — категория, выбранная текстовым правилом
 *       ({@link AcdRules}). Такое помечается как предположение всегда, включая случай,
 *       когда сервер написал название античита словами: клиент не видит внутренних
 *       флагов защиты и не может знать, что за ними стояло.</li>
 * </ul>
 *
 * <h2>Чего здесь нет намеренно</h2>
 * Ни одного изменения пакета, тайминга, порядка отправки или состояния игры. Модуль
 * читает то же, что читает vanilla-обработчик, и пишет в свой буфер. Никакого
 * подписочного механизма: миксин не «отписывается», поэтому единственный выключатель —
 * {@link AcdStore#setCollecting(boolean)}, и он стоит первой строкой в каждом методе.
 */
public final class AcdCollector {

    /** Источник-подпись: попадает в поле Source каждой записи. */
    private static final String SRC_SYSTEM_CHAT = "Server system message";
    private static final String SRC_DISGUISED_CHAT = "Server chat (unsigned type)";
    private static final String SRC_PLAYER_CHAT = "Signed player chat";
    private static final String SRC_PAYLOAD = "Custom payload";
    private static final String SRC_POSITION = "Packet ClientboundPlayerPositionPacket";
    private static final String SRC_MOTION = "Packet ClientboundSetEntityMotionPacket";
    private static final String SRC_LOGIN = "Client login";
    private static final String SRC_DISCONNECT_PACKET = "Packet ClientboundDisconnectPacket";
    private static final String SRC_DISCONNECT = "Client connection";
    private static final String SRC_PACKET_ERROR = "Packet error handler";

    /** Приписка для сообщений, пришедших в строку заголовка (action bar), а не в чат. */
    private static final String OVERLAY_HINT = "оверлей-строка; ";

    /** Ниже этого смещения серверной коррекции не считаем событием: шум поворота камеры. */
    private static final double MIN_CORRECTION_BLOCKS = 0.25D;

    /** Сколько включённых модулей перечислять в контексте (дальше — только счётчик). */
    private static final int MAX_CONTEXT_MODULES = 8;

    /** Источник служебных отметок самого детектора (включение/выключение сборки). */
    private static final String SRC_SELF = "AntiCheatDetector";

    /**
     * Почему отметка сборки лежит в «Неизвестном», а не в «Сети»: это не событие
     * соединения, а наше собственное действие. Первый прогон на живом сервере это и
     * показал — счётчик NETWORK_DIAGNOSTIC (6 записей) состоял только из таких
     * включений и выключений, то есть врал о сети при нуле сетевых событий. Ни одна из девяти категорий не описывает
     * «модуль включился», а придумывать десятую — значило бы переписать классификатор;
     * нейтральная корзина с подписанным источником даёт меньше ложных выводов.
     */
    private static final String LIFECYCLE_NOTE =
            "служебная отметка самого детектора, к соединению отношения не имеет";

    /** Причина, уже записанная пакетом причины: по ней {@link #onDisconnect} не дублирует тот же текст. */
    private static volatile String lastKickReason = "";

    private AcdCollector() {
    }

    // ---- точки входа, вызываемые из миксинов

    /** Начало игровой сессии; сбрасывает «причину уже записали», чтобы следующее отключение не слилось с прошлым. */
    public static void onLogin(ClientboundLoginPacket packet) {
        // Сессию начинаем независимо от того, включён ли сбор: иначе история, накопленная
        // до паузы, потеряла бы привязку к соединению.
        lastKickReason = "";
        AcdStore.beginSession(serverLabel());
        emit(AcdCategory.NETWORK_DIAGNOSTIC, AcdCategory.NETWORK_DIAGNOSTIC.defaultSeverity(),
                AcdConfidence.CONFIRMED, SRC_LOGIN, "", "вход в мир завершён, сессия #" + AcdStore.sessionId(), "", false);
    }

    /** Системное сообщение сервера: его пишет сам сервер или плагин, а не игрок. */
    public static void onSystemChat(ClientboundSystemChatPacket packet) {
        if (packet == null || !AcdStore.isCollecting()) {
            return;
        }
        String raw = text(packet.content());
        String snippet = AcdClassifier.snippet(raw);
        String key = AcdClassifier.searchKey(snippet);
        AcdRule rule = AcdClassifier.classify(key);
        AcdCategory category = rule == null ? AcdCategory.SERVER_MESSAGE : rule.target();
        AcdConfidence confidence = rule == null ? AcdConfidence.CONFIRMED : AcdConfidence.HEURISTIC;
        String detail = rule == null
                ? (packet.overlay() ? OVERLAY_HINT + "правил не затронуто" : "правил не затронуто")
                : ruleDetail(rule, key);
        String message = (packet.overlay() ? "сообщение поверх HUD: " : "системное сообщение: ") + quote(snippet);
        emit(category, rule == null ? category.defaultSeverity() : rule.severity(), confidence,
                SRC_SYSTEM_CHAT, raw, message, detail, false);
    }

    /** Сообщение того же канала, что и чат, но без подписи игрока: им пользуются плагины, в том числе защиты. */
    public static void onDisguisedChat(ClientboundDisguisedChatPacket packet) {
        if (packet == null || !AcdStore.isCollecting()) {
            return;
        }
        ingestMatched(SRC_DISGUISED_CHAT, text(packet.message()), true);
    }

    /** Подписанный чат игрока: записывается ТОЛЬКО если текст совпал с правилом. */
    public static void onPlayerChat(ClientboundPlayerChatPacket packet) {
        if (packet == null || !AcdStore.isCollecting() || !AcdStore.scansChat()) {
            return;
        }
        Component unsigned = packet.unsignedContent();
        String raw = unsigned != null ? unsigned.getString()
                : (packet.body() == null ? "" : packet.body().content());
        ingestMatched(SRC_PLAYER_CHAT, raw, true);
    }

    /** Полезная нагрузка вне ванильных протоколов: бренд сервера и незнакомые каналы. */
    public static void onCustomPayload(ClientboundCustomPayloadPacket packet) {
        if (packet == null || !AcdStore.isCollecting()) {
            return;
        }
        CustomPacketPayload payload = packet.payload();
        if (payload instanceof BrandPayload brand) {
            String name = brand.brand() == null ? "" : brand.brand();
            emit(AcdCategory.SERVER_MESSAGE, AcdCategory.SERVER_MESSAGE.defaultSeverity(),
                AcdConfidence.CONFIRMED, SRC_PAYLOAD, name,
                    "бренд сервера (minecraft:brand): " + quote(name),
                    "бренд объявляет сам сервер; по нему судят о прокси и сборке сервера, но не о наличии античита",
                    false);
            return;
        }
        if (payload instanceof DiscardedPayload discarded) {
            // Содержимое не читается: канал не зарегистрирован, декодера нет. Идентификатор канала —
            // это всё, что клиент имеет права утверждать.
            String id = discarded.id() == null ? "?" : discarded.id().toString();
            emit(AcdCategory.UNKNOWN_EVENT, AcdCategory.UNKNOWN_EVENT.defaultSeverity(),
                    AcdConfidence.CONFIRMED, SRC_PAYLOAD, id,
                    "сервер прислал полезную нагрузку в неизвестном канале " + id,
                    "клиент не знает кодировщика этого канала; какие-либо выводы о его назначении невозможны", false);
        }
    }

    /** Сервер вернул нашего игрока на свою позицию — это и есть «setback», если он был. */
    public static void onPositionCorrection(ClientboundPlayerPositionPacket packet) {
        if (packet == null || !AcdStore.isCollecting()) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null) {
            return;
        }
        PositionMoveRotation change = packet.change();
        Vec3 target = change == null ? null : change.position();
        if (target == null) {
            return;
        }
        Set<?> relatives = packet.relatives();
        boolean relative = relatives != null && !relatives.isEmpty();
        double dx = target.x - player.getX();
        double dy = target.y - player.getY();
        double dz = target.z - player.getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (!relative && distance < MIN_CORRECTION_BLOCKS) {
            return;
        }
        String message = relative
                ? "сервер скорректировал позицию относительно чего-то (смещение не измеряем)"
                : "сервер вернул игрока на свою позицию: сдвиг " + num(distance) + " блока";
        // Оговорка обязательна: ClientboundPlayerPositionPacket — это не только setback.
        // Им же выполняют /tp, возврат на спавн и респаун, и по одному пакету их не
        // развести. Без этой фразы читатель додумывает «античит вернул меня за 730 блоков».
        String teleportCaveat = relative ? ""
                : "; тот же пакет присылают обычные телепорты (спавн, /tp, респаун) — "
                  + "отличить их от коррекции античита по одному пакету невозможно";
        String detail = "собственная скорость на момент коррекции " + num(speed(client))
                + " блока/тик; причина коррекции клиенту неизвестна"
                + (relatives == null ? "" : ", флагов относительности: " + relatives.size())
                + teleportCaveat;
        emit(AcdCategory.MOVEMENT_DIAGNOSTIC, AcdCategory.MOVEMENT_DIAGNOSTIC.defaultSeverity(),
                AcdConfidence.CONFIRMED, SRC_POSITION, "", message, detail, true);
    }

    /** Скорость, выданную сервером нашему игроку, клиент видит — а вот за что она, нет. */
    public static void onMotion(ClientboundSetEntityMotionPacket packet) {
        if (packet == null || !AcdStore.isCollecting()) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if (player == null || packet.getId() != player.getId()) {
            return;
        }
        double xa = packet.getXa();
        double ya = packet.getYa();
        double za = packet.getZa();
        double horizontal = Math.sqrt(xa * xa + za * za);
        if (horizontal < 0.01D && Math.abs(ya) < 0.01D) {
            return;
        }
        emit(AcdCategory.COMBAT_DIAGNOSTIC, AcdCategory.COMBAT_DIAGNOSTIC.defaultSeverity(),
                AcdConfidence.OBSERVED, SRC_MOTION, "",
                "сервер выдал нашему игроку скорость: горизонталь " + num(horizontal)
                        + ", вертикаль " + num(ya),
                "это может быть отталкивание от удара, толчок от блока или реакция сервера на «velocity»; "
                        + "античит по этому пакету не опознаётся",
                true);
    }

    /** Пакет причины отключения: текст, который сервер показал нам перед разрывом. */
    public static void onDisconnectReason(ClientboundDisconnectPacket packet) {
        if (packet == null || !AcdStore.isCollecting()) {
            return;
        }
        String raw = text(packet.reason());
        String snippet = AcdClassifier.snippet(raw);
        lastKickReason = snippet;
        emit(AcdCategory.KICK_DISCONNECT, AcdCategory.KICK_DISCONNECT.defaultSeverity(),
                AcdConfidence.CONFIRMED, SRC_DISCONNECT_PACKET, raw,
                "сервер прислал причину отключения: " + quote(snippet), describeReason(snippet), false);
    }

    /**
     * Разрыв соединения. Вызывается и при кике, и при обычной ошибке, и при выходе в меню,
     * иногда из потока netty — поэтому ничего, кроме своего буфера, не трогает.
     */
    public static void onDisconnect(DisconnectionDetails details) {
        String raw = details == null ? "" : text(details.reason());
        String snippet = AcdClassifier.snippet(raw);
        AcdStore.endSession();
        if (!AcdStore.isCollecting() || snippet.isEmpty() || snippet.equals(lastKickReason)) {
            // Причина уже записана пакетом — второго события про тот же текст не будет.
            lastKickReason = "";
            return;
        }
        lastKickReason = "";
        emit(AcdCategory.KICK_DISCONNECT, AcdCategory.KICK_DISCONNECT.defaultSeverity(),
                AcdConfidence.CONFIRMED, SRC_DISCONNECT, raw,
                "соединение закрыто: " + quote(snippet), describeReason(snippet), false);
    }

    /** Ошибка обработки входящего пакета: сломанный/вредоносный пакет античит тоже может вызвать. */
    public static void onPacketError(Packet<?> packet, Exception exception) {
        if (!AcdStore.isCollecting() || exception == null) {
            return;
        }
        String kind = packet == null ? "?" : packet.getClass().getSimpleName();
        String raw = exception.getClass().getName() + ": " + exception.getMessage();
        emit(AcdCategory.CLIENT_ERROR, AcdCategory.CLIENT_ERROR.defaultSeverity(),
                AcdConfidence.CONFIRMED, SRC_PACKET_ERROR, raw, "ошибка обработки пакета " + kind,
                "стек трейс не сохраняется намеренно: он содержит кадры чужих библиотек и ничего не говорит о причине",
                false);
    }

    /**
     * Аварийный останов сбора: из обработчика миксина, чтобы одна поломка не уронила
     * игру и не продолжила плодить ошибки на каждом пакете.
     *
     * <p>Модуль при этом выключается только если мы уже в игровом потоке:
     * {@code setEnabled} двигает очередь уведомлений (обычный {@code ArrayList}) и
     * трогать её из netty нельзя. Из чужого потока переключение уезжает в очередь
     * клиента — сбор остановлен немедленно, а выключенная кнопка в GUI появится в
     * ближайшем тике.
     */
    public static void disableCollection(String where, Exception failure) {
        AcdStore.setCollecting(false);
        Swerve.LOGGER.error("AntiCheatDetector остановлен после ошибки в {}", where, failure);
        Runnable hook = AcdStore.stopHook();
        if (hook == null) {
            return;
        }
        try {
            hook.run();
        } catch (RuntimeException toggleFailure) {
            Swerve.LOGGER.warn("AntiCheatDetector: не удалось выключить модуль после ошибки", toggleFailure);
        }
    }

    /** Внутренняя ошибка самого детектора — её тоже честно показывать в его же отчёте. */
    public static void onInternalError(String where, Throwable failure) {
        String raw = failure == null ? "?" : failure.getClass().getName() + ": " + failure.getMessage();
        emit(AcdCategory.CLIENT_ERROR, AcdSeverity.WARNING, AcdConfidence.CONFIRMED,
                SRC_SELF, raw, "сбой в самом детекторе (" + where + ")",
                "сбор остановлен, накопленная история сохранена", false);
    }

    // ---- вспомогательное

    private static void ingestMatched(String source, String raw, boolean onlyMatches) {
        String snippet = AcdClassifier.snippet(raw);
        if (snippet.isEmpty()) {
            return;
        }
        String key = AcdClassifier.searchKey(snippet);
        AcdRule rule = AcdClassifier.classify(key);
        if (rule == null) {
            if (onlyMatches) {
                return;
            }
            emit(AcdCategory.SERVER_MESSAGE, AcdCategory.SERVER_MESSAGE.defaultSeverity(),
                    AcdConfidence.CONFIRMED, source, raw, "сообщение: " + quote(snippet),
                    "правил не затронуто", false);
            return;
        }
        emit(rule.target(), rule.severity(), AcdConfidence.HEURISTIC, source, raw,
                "сообщение: " + quote(snippet), ruleDetail(rule, key), false);
    }

    /** Важность и примечание к причине отключения, если текст что-то говорит правилам. */
    private static String describeReason(String snippet) {
        String key = AcdClassifier.searchKey(snippet);
        AcdRule rule = AcdClassifier.classify(key);
        if (rule == null) {
            return "категорию определил источник: это пакет отключения; прислала ли его защита, по тексту не видно";
        }
        return "в тексте причины найдено совпадение, но категорию задаёт пакет (он подтверждён), а не правило: "
                + ruleDetail(rule, key);
    }

    private static void emit(AcdCategory category, AcdSeverity severity, AcdConfidence confidence,
                             String source, String raw, String message, String detail, boolean withContext) {
        if (!AcdStore.isCollecting()) {
            return;
        }
        if (!AcdStore.isEnabled(category) || !severity.atLeast(AcdStore.severityFloor())) {
            return;
        }
        boolean added = false;
        try {
            added = AcdStore.record(new AcdEvent(System.currentTimeMillis(), System.nanoTime(), category,
                    severity, confidence, source, raw, message, detail, withContext ? enabledModules() : "",
                    AcdStore.sessionId()));
        } catch (RuntimeException exception) {
            // Хранилище не должно ронять игровой поток: сбор выключается, история остаётся.
            disableCollection("record " + category, exception);
            return;
        }
        // Короткая отметка в чат — только для нового важного события: повторы молчат,
        // иначе спам-канал превратит чат в эхо журнала.
        if (added && AcdStore.announces()
                && (severity == AcdSeverity.WARNING || severity == AcdSeverity.CRITICAL)) {
            announce(category, severity, message);
        }
    }

    /**
     * Печатает важное в локальный чат. Только из игрового потока: часть источников
     * приходит из netty, а {@code ChatComponent} не потокобезопасен. В классификацию эта
     * строка не попадает — она идёт через отрисовку, а не через пакет.
     */
    private static void announce(AcdCategory category, AcdSeverity severity, String message) {
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }
        Runnable print = () -> {
            if (client.gui != null) {
                client.gui.getChat().addMessage(Component.literal("§d[ACD] §"
                        + (severity == AcdSeverity.CRITICAL ? "c" : "e") + category.label()
                        + " §7" + AcdClassifier.snippet(message)));
            }
        };
        if (client.isSameThread()) {
            print.run();
        } else {
            client.execute(print);
        }
    }

    /** Имена включённых модулей на момент события — контекст, который ищут в разборе флагов. */
    private static String enabledModules() {
        List<Module> modules = ModuleManager.getModules();
        StringBuilder builder = null;
        int enabled = 0;
        for (int i = 0, size = modules.size(); i < size; i++) {
            Module module = modules.get(i);
            if (!module.isEnable()) {
                continue;
            }
            enabled++;
            if (builder == null) {
                builder = new StringBuilder(64);
            }
            if (enabled <= MAX_CONTEXT_MODULES) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(module.getName());
            }
        }
        if (builder == null) {
            return "";
        }
        if (enabled > MAX_CONTEXT_MODULES) {
            builder.append(" …и ещё ").append(enabled - MAX_CONTEXT_MODULES);
        }
        return builder.toString();
    }

    /**
     * Контекст сессии: адрес и название сервера. Никаких токенов, сессий Microsoft,
     * списков игроков и содержимого чужого чата — только то, что нужно, чтобы понять,
     * к какой именно попытке относится отчёт.
     */
    /** Метка соединения для шапки сессии — тот же текст, что попадает в отчёт. */
    public static String sessionLabel() {
        return serverLabel();
    }

    /**
     * Служебная запись «сбор включён/выключен» — чтобы в отчёте было видно, какой
     * промежуток времени вообще наблюдался, а не только набор событий.
     */
    public static void noteCollectionStarted() {
        emit(AcdCategory.UNKNOWN_EVENT, AcdSeverity.INFO, AcdConfidence.CONFIRMED,
                SRC_SELF, "", "включён сбор диагностики", LIFECYCLE_NOTE, false);
    }

    public static void noteCollectionStopped(int recorded) {
        emit(AcdCategory.UNKNOWN_EVENT, AcdSeverity.INFO, AcdConfidence.CONFIRMED,
                SRC_SELF, "", "сбор выключен, в истории записей: " + recorded, LIFECYCLE_NOTE, false);
    }

    private static String serverLabel() {
        try {
            Minecraft client = Minecraft.getInstance();
            if (client.isSingleplayer()) {
                return "одиночная игра";
            }
            ClientPacketListener listener = client.getConnection();
            if (listener == null) {
                return "";
            }
            ServerData data = listener.getServerData();
            if (data == null) {
                return "";
            }
            String name = data.name == null ? "" : AcdClassifier.snippet(data.name);
            String ip = data.ip == null ? "" : data.ip;
            return name.isEmpty() ? ip : name + " (" + ip + ")";
        } catch (RuntimeException exception) {
            return "";
        }
    }

    private static double speed(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) {
            return 0.0D;
        }
        Vec3 motion = player.getDeltaMovement();
        return Math.sqrt(motion.x * motion.x + motion.z * motion.z);
    }

    /** Как именно правило объясняет запись: id, совпавший маркер и его формулировка. */
    private static String ruleDetail(AcdRule rule, String key) {
        String marker = rule.matchedMarker(key);
        return "правило " + rule.id() + ": совпало «" + (marker == null ? "?" : marker) + "» — " + rule.reason();
    }

    private static String text(Component component) {
        return component == null ? "" : component.getString();
    }

    private static String quote(String value) {
        return value.isEmpty() ? "«пусто»" : "«" + value + "»";
    }

    /** Две цифры после точки без String.format: форматирование нужно только при событии, но аллокации лишние не нужны. */
    private static String num(double value) {
        return String.valueOf(Math.round(value * 100.0D) / 100.0D);
    }
}
