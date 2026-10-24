package swerve.bot;

import java.util.ArrayList;
import java.util.List;

/**
 * Профиль бота: куда подключаться, под каким ником и что делать.
 *
 * <p>Профиль — это данные, а не живой клиент: студия редактирует профили,
 * {@link BotManager} запускает по ним {@link BotRunner}. Поэтому профиль можно
 * сохранить, скопировать и отредактировать, пока бот работает (изменения
 * применятся при следующем запуске).
 */
public final class BotProfile {

    /** Протокол 1.21.4 — единственная версия, которую умеет headless-клиент Swerve. */
    public static final int PROTOCOL_1_21_4 = 769;

    private String name = "бот";
    private String host = "localhost";
    private int port = 25565;
    private String nickname = "SwerveBot";
    private int protocol = PROTOCOL_1_21_4;

    private boolean autoReconnect = true;
    private float reconnectDelay = 5.0f;
    private float stepDelay = 0.4f;
    private boolean loopForever = true;

    private final List<BotStep> steps = new ArrayList<>();

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.length() > 24 ? name.substring(0, 24) : name;
        }
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        if (host != null) {
            this.host = host.trim();
        }
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = Math.max(1, Math.min(65535, port));
    }

    public String getNickname() {
        return this.nickname;
    }

    public void setNickname(String nickname) {
        if (nickname == null) {
            return;
        }
        String clean = nickname.trim();
        this.nickname = clean.length() > 16 ? clean.substring(0, 16) : clean;
    }

    public int getProtocol() {
        return this.protocol;
    }

    public void setProtocol(int protocol) {
        this.protocol = protocol;
    }

    public boolean isAutoReconnect() {
        return this.autoReconnect;
    }

    public void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public float getReconnectDelay() {
        return this.reconnectDelay;
    }

    public void setReconnectDelay(float reconnectDelay) {
        this.reconnectDelay = Math.max(0.5f, Math.min(120.0f, reconnectDelay));
    }

    public float getStepDelay() {
        return this.stepDelay;
    }

    public void setStepDelay(float stepDelay) {
        this.stepDelay = Math.max(0.0f, Math.min(30.0f, stepDelay));
    }

    /** Повторять список шагов по кругу, когда он закончился. */
    public boolean isLoopForever() {
        return this.loopForever;
    }

    public void setLoopForever(boolean loopForever) {
        this.loopForever = loopForever;
    }

    public List<BotStep> getSteps() {
        return this.steps;
    }

    public void addStep(BotStep step) {
        if (step != null) {
            this.steps.add(step);
        }
    }

    public void insertStep(int index, BotStep step) {
        if (step == null) {
            return;
        }
        int at = Math.max(0, Math.min(this.steps.size(), index));
        this.steps.add(at, step);
    }

    public void removeStep(BotStep step) {
        this.steps.remove(step);
    }

    public int indexOf(BotStep step) {
        return this.steps.indexOf(step);
    }

    public void move(BotStep step, int direction) {
        int index = this.steps.indexOf(step);
        if (index < 0) {
            return;
        }
        int target = index + direction;
        if (target < 0 || target >= this.steps.size()) {
            return;
        }
        this.steps.remove(index);
        this.steps.add(target, step);
    }

    /** Копия профиля (шаги тоже копируются, но без ссылок на живой клиент). */
    public BotProfile copy(String newName) {
        BotProfile copy = new BotProfile();
        copy.name = newName;
        copy.host = this.host;
        copy.port = this.port;
        copy.nickname = this.nickname;
        copy.protocol = this.protocol;
        copy.autoReconnect = this.autoReconnect;
        copy.reconnectDelay = this.reconnectDelay;
        copy.stepDelay = this.stepDelay;
        copy.loopForever = this.loopForever;
        for (int i = 0, size = this.steps.size(); i < size; i++) {
            BotStep step = this.steps.get(i);
            BotStep clone = new BotStep(step.getType());
            for (int v = 0; v < step.valueCount(); v++) {
                clone.set(v, step.get(v));
            }
            clone.setEnabled(step.isEnabled());
            copy.steps.add(clone);
        }
        return copy;
    }

    /** Краткая строка для студии: «ник @ хост:порт». */
    public String address() {
        return this.host + ":" + this.port;
    }

    /** Строка подключения для лога: «ник @ хост:порт». */
    public String getAddressLine() {
        return this.nickname + " @ " + address();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
