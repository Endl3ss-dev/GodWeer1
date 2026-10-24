package swerve.bot;

import swerve.Swerve;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Headless-клиент Minecraft 1.21.4 (protocol 769).
 *
 * <p>Подключается к серверу тем же путём, что и обычный клиент: handshake →
 * login → configuration → play. Умеет ровно то, что нужно боту-автоматизации и
 * не требует игрового мира: держать соединение (keep-alive), читать чат,
 * отправлять команды и сообщения, поворачиваться и двигаться, работать с окном
 * инвентаря, приседать/бежать, возрождаться.
 *
 * <h2>Что поддерживается</h2>
 * <ul>
 *   <li>offline-режим (UUID выводится из ника, как в ванильном клиенте);</li>
 *   <li>сжатие пакетов (login {@code compress});</li>
 *   <li>ответы на cookie/плагин-запросы и на «known packs» в configuration —
 *       без них современные прокси (Velocity, Paper) держат подключение вечно;</li>
 *   <li>подтверждение телепорта и повторная отправка позиции.</li>
 * </ul>
 *
 * <h2>Чего нет и почему</h2>
 * <ul>
 *   <li>online-mode (шифрование) — требует сессии Mojang, это отдельный вход
 *       через аккаунт; сервер с {@code online-mode=true} отключит бота с
 *       понятным сообщением;</li>
 *   <li>мира и сущностей — бот не грузит чанки, поэтому шагов «атаковать
 *       сущность» в каталоге нет. Обманывать пользователя шагами, которые не
 *       работают, хуже, чем не иметь их.</li>
 * </ul>
 *
 * <p>Обмен идёт в отдельном потоке-демоне; игровой поток не блокируется.
 * Все публичные методы можно звать из любого потока.
 */
public final class BotConnection {

    public enum State {
        OFFLINE("офлайн"),
        CONNECTING("подключение"),
        LOGIN("вход"),
        CONFIGURATION("конфигурация"),
        PLAY("в игре"),
        ERROR("ошибка");

        private final String displayName;

        State(String displayName) {
            this.displayName = displayName;
        }

        public String displayName() {
            return this.displayName;
        }
    }

    private static final int CONNECT_TIMEOUT_MS = 8_000;
    private static final int READ_TIMEOUT_MS = 45_000;
    private static final int MAX_PACKET = 2 * 1024 * 1024;
    private static final int MAX_COMPRESSED = 8 * 1024 * 1024;

    private final BotProfile profile;
    private final BotLog log;

    private volatile State state = State.OFFLINE;
    private volatile boolean running;
    private volatile String lastError = "";
    private volatile long connectedAt;
    private volatile long lastPacketAt;

    private volatile double x;
    private volatile double y = 64.0;
    private volatile double z;
    private volatile float yaw;
    private volatile float pitch;
    private volatile float health = 20.0f;
    private volatile int food = 20;
    private volatile int entityId;
    private volatile int teleportId;
    private volatile int windowId;
    private volatile int windowStateId;
    private volatile int heldSlot;
    private volatile boolean sneaking;
    private volatile boolean sprinting;
    private volatile String lastChat = "";
    private volatile long lastChatAt;
    /** Когда бот отправлял последнее сообщение — чтобы пометить ответ сервера. */
    private volatile long lastSentAt;

    private Socket socket;
    private DataInputStream input;
    private OutputStream output;
    private Thread thread;
    private int compression = -1;
    private final Object writeLock = new Object();

    public BotConnection(BotProfile profile, BotLog log) {
        this.profile = profile;
        this.log = log;
    }

    // ---- состояние

    public State state() {
        return this.state;
    }

    public boolean isPlaying() {
        return this.state == State.PLAY;
    }

    public boolean isOnline() {
        return this.state == State.PLAY || this.state == State.CONFIGURATION;
    }

    public String lastError() {
        return this.lastError;
    }

    public long connectedAt() {
        return this.connectedAt;
    }

    public long lastPacketAt() {
        return this.lastPacketAt;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    public float yaw() {
        return this.yaw;
    }

    public float pitch() {
        return this.pitch;
    }

    public float health() {
        return this.health;
    }

    public int food() {
        return this.food;
    }

    public int entityId() {
        return this.entityId;
    }

    public String lastChat() {
        return this.lastChat;
    }

    public long lastChatAt() {
        return this.lastChatAt;
    }

    // ---- управление

    public synchronized void start() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.lastError = "";
        this.thread = new Thread(this::loop, "Swerve-Bot-" + this.profile.getNickname());
        this.thread.setDaemon(true);
        this.thread.start();
    }

    public synchronized void stop() {
        if (!this.running) {
            closeSocket();
            return;
        }
        this.running = false;
        closeSocket();
        this.state = State.OFFLINE;
    }

    /** Переподключение: рвём сокет, цикл сам поднимет новое соединение. */
    public void reconnect() {
        this.log.info("переподключение");
        closeSocket();
    }

    private void closeSocket() {
        Socket current = this.socket;
        this.socket = null;
        if (current != null) {
            try {
                current.close();
            } catch (IOException ignored) {
                // закрываем по своей воле — ошибка закрытия не важна
            }
        }
    }

    private void loop() {
        while (this.running) {
            try {
                session();
            } catch (Exception exception) {
                String message = exception.getMessage() == null ? exception.getClass().getSimpleName()
                        : exception.getMessage();
                this.lastError = message;
                if (this.running) {
                    this.log.error(message);
                }
                Swerve.LOGGER.debug("Bot {} session ended: {}", this.profile.getNickname(), message);
            } finally {
                this.state = this.running ? this.state : State.OFFLINE;
                closeSocket();
                this.compression = -1;
            }

            if (!this.running) {
                break;
            }
            if (!this.profile.isAutoReconnect()) {
                this.state = State.ERROR;
                break;
            }

            long delay = (long) (this.profile.getReconnectDelay() * 1000.0f);
            this.log.warn("соединение потеряно, переподключение через "
                    + String.format("%.1f", this.profile.getReconnectDelay()) + " с");
            this.state = State.CONNECTING;
            long until = System.currentTimeMillis() + delay;
            while (this.running && System.currentTimeMillis() < until) {
                sleep(100);
            }
        }
        this.state = State.OFFLINE;
    }

    private void session() throws IOException {
        this.state = State.CONNECTING;
        Socket client = new Socket();
        client.setTcpNoDelay(true);
        client.connect(new InetSocketAddress(this.profile.getHost(), this.profile.getPort()), CONNECT_TIMEOUT_MS);
        client.setSoTimeout(READ_TIMEOUT_MS);
        this.socket = client;
        this.input = new DataInputStream(client.getInputStream());
        this.output = client.getOutputStream();

        this.log.ok("соединение с " + this.profile.address());

        writePacket(0x00, new PacketWriter()
                .varInt(this.profile.getProtocol())
                .string(this.profile.getHost())
                .shortValue(this.profile.getPort())
                .varInt(2)); // 2 = login

        this.state = State.LOGIN;
        UUID uuid = offlineUuid(this.profile.getNickname());
        writePacket(0x00, new PacketWriter()
                .string(this.profile.getNickname())
                .uuid(uuid));

        while (this.running && !client.isClosed()) {
            readPacket();
        }
    }

    /** UUID offline-режима: так же выводит его ванильный клиент. */
    public static UUID offlineUuid(String nickname) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + nickname).getBytes(StandardCharsets.UTF_8));
    }

    // ---- чтение

    private void readPacket() throws IOException {
        int length = readVarInt(this.input);
        if (length <= 0 || length > MAX_COMPRESSED) {
            throw new IOException("некорректная длина пакета: " + length);
        }
        byte[] frame = new byte[length];
        this.input.readFully(frame);

        byte[] body;
        if (this.compression >= 0) {
            PacketReader reader = new PacketReader(frame);
            int uncompressed = reader.readVarInt();
            byte[] rest = reader.readRest();
            if (uncompressed == 0) {
                body = rest;
            } else {
                if (uncompressed > MAX_PACKET) {
                    throw new IOException("пакет слишком большой: " + uncompressed);
                }
                body = inflate(rest, uncompressed);
            }
        } else {
            body = frame;
        }

        PacketReader reader = new PacketReader(body);
        int id = reader.readVarInt();
        this.lastPacketAt = System.currentTimeMillis();

        switch (this.state) {
            case LOGIN -> handleLogin(id, reader);
            case CONFIGURATION -> handleConfiguration(id, reader);
            case PLAY -> handlePlay(id, reader);
            default -> {
                // в остальных состояниях пакеты не ожидаются
            }
        }
    }

    private static int readVarInt(InputStream in) throws IOException {
        int value = 0;
        int position = 0;
        while (true) {
            int current = in.read();
            if (current < 0) {
                throw new EOFException("сервер закрыл соединение");
            }
            value |= (current & 0x7F) << position;
            if ((current & 0x80) == 0) {
                return value;
            }
            position += 7;
            if (position > 35) {
                throw new IOException("VarInt слишком длинный");
            }
        }
    }

    private static byte[] inflate(byte[] data, int expected) throws IOException {
        Inflater inflater = new Inflater();
        try {
            inflater.setInput(data);
            byte[] out = new byte[expected];
            int offset = 0;
            while (offset < expected && !inflater.finished()) {
                int written = inflater.inflate(out, offset, expected - offset);
                if (written == 0) {
                    if (inflater.needsInput() || inflater.needsDictionary()) {
                        break;
                    }
                }
                offset += written;
            }
            if (offset < expected) {
                byte[] shorter = new byte[offset];
                System.arraycopy(out, 0, shorter, 0, offset);
                return shorter;
            }
            return out;
        } catch (DataFormatException exception) {
            throw new IOException("сжатый пакет повреждён", exception);
        } finally {
            inflater.end();
        }
    }

    // ---- фазы

    private void handleLogin(int id, PacketReader reader) throws IOException {
        switch (id) {
            case 0x00 -> throw new IOException("кик на входе: " + ChatNbt.read(reader));
            case 0x01 -> throw new IOException("сервер требует online-mode (шифрование), бот его не умеет");
            case 0x02 -> {
                this.log.ok("вход выполнен");
                writePacket(0x03, new PacketWriter()); // login_acknowledged
                this.state = State.CONFIGURATION;
            }
            case 0x03 -> {
                int threshold = reader.readVarInt();
                this.compression = Math.max(0, threshold);
                this.log.info("сжатие включено (порог " + threshold + ")");
            }
            case 0x04 -> {
                int messageId = reader.readVarInt();
                writePacket(0x02, new PacketWriter().varInt(messageId).bool(false));
            }
            case 0x05 -> {
                String key = reader.readString(64);
                writePacket(0x04, new PacketWriter().string(key).bool(false));
            }
            default -> {
                // незнакомые пакеты логина игнорируем
            }
        }
    }

    private void handleConfiguration(int id, PacketReader reader) throws IOException {
        switch (id) {
            case 0x00 -> {
                String key = reader.readString(64);
                writePacket(0x01, new PacketWriter().string(key).bool(false));
            }
            case 0x02 -> throw new IOException("кик в конфигурации: " + ChatNbt.read(reader));
            case 0x03 -> {
                writePacket(0x03, new PacketWriter()); // finish_configuration
                this.state = State.PLAY;
                this.connectedAt = System.currentTimeMillis();
                this.log.ok("конфигурация завершена, вход в игру");
            }
            case 0x04 -> {
                long value = reader.readLong();
                writePacket(0x04, new PacketWriter().longValue(value));
            }
            case 0x05 -> {
                int value = reader.readInt();
                writePacket(0x05, new PacketWriter().intValue(value));
            }
            case 0x09 -> {
                // add_resource_pack: uuid, url, hash, forced, hasPrompt(+prompt)
                UUID pack = reader.readUuid();
                reader.readString(512);
                reader.readString(64);
                reader.readBoolean();
                if (reader.readBoolean()) {
                    ChatNbt.read(reader);
                }
                writePacket(0x06, new PacketWriter().uuid(pack).varInt(1)); // 1 = declined
            }
            case 0x0E -> writePacket(0x07, new PacketWriter().varInt(0));
            case 0x08, 0x01, 0x06, 0x07, 0x0A, 0x0B, 0x0C, 0x0D, 0x0F, 0x10 -> {
                // реестры, теги, cookies, transfer, ссылки сервера — боту не нужны
            }
            default -> {
                // прочее игнорируем
            }
        }
    }

    private void handlePlay(int id, PacketReader reader) throws IOException {
        switch (id) {
            case 0x27 -> { // keep_alive
                long value = reader.readLong();
                writePacket(0x1A, new PacketWriter().longValue(value));
            }
            case 0x1D -> throw new IOException("кик: " + ChatNbt.read(reader));
            case 0x73 -> { // system_chat
                String text = ChatNbt.read(reader);
                appendChat(text);
            }
            case 0x3B -> { // player_chat
                reader.readUuid();
                reader.readVarInt();
                reader.readBoolean();
                String plain = reader.readString(256);
                appendChat(plain);
            }
            case 0x1E -> { // profileless_chat
                String text = ChatNbt.read(reader);
                appendChat(text);
            }
            case 0x2C -> { // login (join game)
                this.entityId = reader.readInt();
                this.log.ok("вошёл в мир, entityId=" + this.entityId);
                sendClientSettings();
            }
            case 0x42 -> { // position
                this.teleportId = reader.readVarInt();
                this.x = reader.readDouble();
                this.y = reader.readDouble();
                this.z = reader.readDouble();
                reader.readDouble();
                reader.readDouble();
                reader.readDouble();
                this.yaw = reader.readFloat();
                this.pitch = reader.readFloat();
                writePacket(0x00, new PacketWriter().varInt(this.teleportId));
                sendPosition(true);
            }
            case 0x62 -> { // update_health
                this.health = reader.readFloat();
                this.food = reader.readVarInt();
                reader.readFloat();
            }
            case 0x35 -> { // open_window
                this.windowId = reader.readVarInt();
                this.windowStateId = 0;
            }
            case 0x12 -> this.windowId = 0; // close_window
            case 0x13 -> { // window_items
                this.windowId = reader.readVarInt();
                this.windowStateId = reader.readVarInt();
            }
            case 0x70 -> { // start_configuration
                writePacket(0x0E, new PacketWriter()); // configuration_acknowledged
                this.state = State.CONFIGURATION;
                this.log.info("сервер отправил в конфигурацию");
            }
            default -> {
                // остальные 120+ пакетов боту не нужны
            }
        }
    }

    private void appendChat(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        this.lastChat = text;
        this.lastChatAt = System.currentTimeMillis();
        // Ответ, пришедший сразу после нашей отправки, помечаем: так в журнале
        // видно, что сообщение — реакция на команду бота, а не чужое.
        boolean afterSend = System.currentTimeMillis() - this.lastSentAt < 2_500L;
        this.log.chat((afterSend ? "↩ " : "") + text);
    }

    private void sendClientSettings() throws IOException {
        PacketWriter writer = new PacketWriter();
        writer.string("ru_ru")
                .byteValue(8)               // render distance
                .varInt(0)                  // chat mode: full
                .bool(true)                 // chat colors
                .byteValue(0x7F)            // skin parts: все
                .varInt(1)                  // main hand: right
                .bool(false)                // text filtering
                .bool(true)                 // allow server listings
                .varInt(0);                 // particle status: all
        writePacket(this.state == State.CONFIGURATION ? 0x00 : 0x0C, writer);
    }

    // ---- отправка

    private void writePacket(int id, PacketWriter payload) throws IOException {
        PacketWriter body = new PacketWriter();
        body.varInt(id);
        body.bytes(payload.toByteArray());
        byte[] raw = body.toByteArray();

        ByteArrayOutputStream frame = new ByteArrayOutputStream(raw.length + 8);
        if (this.compression >= 0) {
            if (raw.length >= this.compression) {
                byte[] compressed = deflate(raw);
                PacketWriter inner = new PacketWriter().varInt(raw.length).bytes(compressed);
                byte[] data = inner.toByteArray();
                writeVarInt(frame, data.length);
                frame.write(data, 0, data.length);
            } else {
                PacketWriter inner = new PacketWriter().varInt(0).bytes(raw);
                byte[] data = inner.toByteArray();
                writeVarInt(frame, data.length);
                frame.write(data, 0, data.length);
            }
        } else {
            writeVarInt(frame, raw.length);
            frame.write(raw, 0, raw.length);
        }

        synchronized (this.writeLock) {
            OutputStream out = this.output;
            if (out == null) {
                throw new IOException("соединение закрыто");
            }
            frame.writeTo(out);
            out.flush();
        }
    }

    private static byte[] deflate(byte[] data) {
        Deflater deflater = new Deflater();
        try {
            deflater.setInput(data);
            deflater.finish();
            ByteArrayOutputStream out = new ByteArrayOutputStream(data.length);
            byte[] buffer = new byte[8192];
            while (!deflater.finished()) {
                int written = deflater.deflate(buffer);
                out.write(buffer, 0, written);
            }
            return out.toByteArray();
        } finally {
            deflater.end();
        }
    }

    private static void writeVarInt(ByteArrayOutputStream out, int value) {
        while ((value & 0xFFFFFF80) != 0) {
            out.write((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.write(value);
    }

    // ---- действия бота

    /**
     * Отправить сообщение от имени бота.
     *
     * <p>Пакет уходит только в собственное соединение бота — клиент игрока в
     * отправке не участвует. Если бот ещё не в игре (или уже отключился),
     * сообщение не пишется в сокет: иначе в него улетели бы байты, которые
     * сервер не ждёт, а причина молчания осталась бы непонятной.
     */
    public void chat(String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        if (!isPlaying()) {
            this.log.error("не в игре (" + this.state.displayName() + "): «" + text + "» не отправлено");
            return;
        }
        String message = text.length() > 256 ? text.substring(0, 256) : text;
        try {
            if (message.startsWith("/")) {
                writePacket(0x05, new PacketWriter().string(message.substring(1)));
            } else {
                writePacket(0x07, new PacketWriter()
                        .string(message)
                        .longValue(System.currentTimeMillis())
                        .longValue(0L)
                        .bool(false)
                        .varInt(0)
                        .varInt(0));
            }
            this.lastSentAt = System.currentTimeMillis();
            this.log.command("→ " + message);
        } catch (IOException exception) {
            this.log.error("не удалось отправить сообщение: " + exception.getMessage());
        }
    }

    public void command(String value) {
        String command = value == null ? "" : value.trim();
        while (command.startsWith("/")) {
            command = command.substring(1);
        }
        if (!command.isEmpty()) {
            chat("/" + command);
        }
    }

    public void look(float newYaw, float newPitch) {
        this.yaw = newYaw;
        this.pitch = Math.max(-90.0f, Math.min(90.0f, newPitch));
        try {
            writePacket(0x1E, new PacketWriter().floatValue(this.yaw).floatValue(this.pitch).byteValue(0));
        } catch (IOException exception) {
            this.log.error("поворот не отправлен: " + exception.getMessage());
        }
    }

    /** Повернуться в сторону точки: yaw 0 — юг (+Z), 90 — запад (-X). */
    public void lookAt(double targetX, double targetY, double targetZ) {
        double dx = targetX - this.x;
        double dy = targetY - this.y;
        double dz = targetZ - this.z;
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float newYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float newPitch = (float) -Math.toDegrees(Math.atan2(dy, Math.max(1.0E-4, horizontal)));
        look(newYaw, newPitch);
    }

    /** Взять/убрать предмет в левую руку (entity_action 6). */
    public void swapOffhand() {
        try {
            writePacket(0x28, new PacketWriter().varInt(this.entityId).varInt(6).varInt(0));
        } catch (IOException exception) {
            this.log.error("левая рука не сработала: " + exception.getMessage());
        }
    }

    /** Сбросить ввод и подтвердить текущую позицию — бот останавливается. */
    public void stopMovement() {
        try {
            writePacket(0x29, new PacketWriter().byteValue(0)); // player_input: без флагов
        } catch (IOException exception) {
            this.log.error("остановка не отправлена: " + exception.getMessage());
        }
        sendPosition(false);
    }

    public void moveTo(double newX, double newY, double newZ) {
        this.x = newX;
        this.y = newY;
        this.z = newZ;
        sendPosition(false);
    }

    private void sendPosition(boolean withRotation) {
        try {
            PacketWriter writer = new PacketWriter()
                    .doubleValue(this.x).doubleValue(this.y).doubleValue(this.z);
            if (withRotation) {
                writer.floatValue(this.yaw).floatValue(this.pitch).byteValue(0);
                writePacket(0x1D, writer);
            } else {
                writer.byteValue(0);
                writePacket(0x1C, writer);
            }
        } catch (IOException exception) {
            this.log.error("позиция не отправлена: " + exception.getMessage());
        }
    }

    public void setSneaking(boolean value) {
        this.sneaking = value;
        playerAction(value ? 0 : 1);
    }

    public void setSprinting(boolean value) {
        this.sprinting = value;
        playerAction(value ? 3 : 4);
    }

    private void playerAction(int action) {
        try {
            writePacket(0x28, new PacketWriter().varInt(this.entityId).varInt(action).varInt(0));
        } catch (IOException exception) {
            this.log.error("действие не отправлено: " + exception.getMessage());
        }
    }

    public void jump() {
        try {
            writePacket(0x29, new PacketWriter().byteValue(0)); // player_input: jump
        } catch (IOException exception) {
            this.log.error("прыжок не отправлен: " + exception.getMessage());
        }
    }

    public void swing() {
        try {
            writePacket(0x3A, new PacketWriter().varInt(0)); // arm_animation
        } catch (IOException exception) {
            this.log.error("взмах не отправлен: " + exception.getMessage());
        }
    }

    public void useItem() {
        try {
            writePacket(0x3D, new PacketWriter().varInt(0).varInt(0)
                    .floatValue(this.yaw).floatValue(this.pitch));
        } catch (IOException exception) {
            this.log.error("использование предмета не отправлено: " + exception.getMessage());
        }
    }

    public void respawn() {
        try {
            writePacket(0x0A, new PacketWriter().varInt(0)); // client_command: perform respawn
        } catch (IOException exception) {
            this.log.error("возрождение не отправлено: " + exception.getMessage());
        }
    }

    public void heldSlot(int slot) {
        this.heldSlot = Math.max(0, Math.min(8, slot));
        try {
            writePacket(0x33, new PacketWriter().shortValue(this.heldSlot));
        } catch (IOException exception) {
            this.log.error("слот не выбран: " + exception.getMessage());
        }
    }

    /**
     * Свой инвентарь открывается на клиенте и пакета не требует: окно игрока
     * всегда имеет id 0. Метод лишь фиксирует это состояние, чтобы шаги работы
     * со слотами шли в правильное окно.
     */
    public void openInventory() {
        this.windowId = 0;
        this.log.info("инвентарь игрока доступен (окно 0)");
    }

    public void closeInventory() {
        int open = this.windowId;
        this.windowId = 0;
        if (open == 0) {
            return;
        }
        try {
            writePacket(0x11, new PacketWriter().byteValue(open));
        } catch (IOException exception) {
            this.log.error("инвентарь не закрыт: " + exception.getMessage());
        }
    }

    /**
     * Клик по слоту. Слоты окна игрока: 5–8 броня, 9–35 рюкзак, 36–44 хотбар,
     * 45 левая рука.
     */
    public void clickSlot(int slot, int button, int mode) {
        try {
            PacketWriter writer = new PacketWriter()
                    .byteValue(this.windowId)
                    .varInt(this.windowStateId)
                    .shortValue(slot)
                    .byteValue(button)
                    .varInt(mode)
                    .varInt(0)          // changedSlots: пусто
                    .varInt(0);         // cursorItem: пустой предмет
            writePacket(0x10, writer);
        } catch (IOException exception) {
            this.log.error("клик по слоту не отправлен: " + exception.getMessage());
        }
    }

    public void dropSlot(int slot) {
        clickSlot(slot, 0, 4); // mode 4 = выбросить
    }

    /** Shift-клик: перенос стака между окнами. */
    public void quickMove(int slot) {
        clickSlot(slot, 0, 1);
    }

    /** Поменять слот инвентаря с ячейкой хотбара (mode 2 = swap). */
    public void swapWithHotbar(int slot, int hotbar) {
        clickSlot(slot, Math.max(0, Math.min(8, hotbar)), 2);
    }

    /** Выбросить весь стак (mode 4, кнопка 1). */
    public void throwStack(int slot) {
        clickSlot(slot, 1, 4);
    }

    /** Переложить предмет: shift-клик (mode 1) переносит стак между окнами. */
    public void moveSlot(int from, int to) {
        clickSlot(from, 0, 0);
        clickSlot(to, 0, 0);
    }

    public void equipArmor(int fromSlot, String piece) {
        int target = switch (piece == null ? "" : piece.trim().toLowerCase()) {
            case "шлем" -> 5;
            case "нагрудник" -> 6;
            case "штаны" -> 7;
            case "ботинки" -> 8;
            default -> 5;
        };
        moveSlot(fromSlot, target);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(Math.max(1L, millis));
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
