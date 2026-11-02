package net.skycore.client.module.misc;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.skycore.client.command.ChatMessages;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.ActionSetting;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.ModeSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.StringSetting;
import net.skycore.client.util.DinnerboneFlip;

/**
 * Dinnerbone — переворот модели игрока «вверх ногами», как от ника {@code Dinnerbone}.
 *
 * <h2>Как это работает в Minecraft (и почему это важно)</h2>
 * Переворот — не эффект и не анимация, а <b>клиентский рендер</b>:
 * {@code LivingEntityRenderer} смотрит на имя сущности и, если оно равно
 * {@code Dinnerbone} или {@code Grumm}, переворачивает модель. Никакого пакета
 * «переверни меня» в протоколе нет.
 *
 * <p>Значит, «видно другим игрокам» возможно только одним способом: имя, которое
 * сервер рассылает всем клиентам, должно стать {@code Dinnerbone}. Имя игрока —
 * серверные данные, клиент их не меняет; поэтому модуль пытается добиться этого
 * <b>командой ника</b> на самом сервере. Если сервер такого не разрешает — никакой
 * клиентский мод этого не обойдёт, это предел протокола, а не лень автора.
 *
 * <h2>Варианты, которые пробует модуль</h2>
 * <ol>
 *   <li><b>Ник</b> — по очереди отправляет команды из настройки «Команды ника»
 *       ({@code /nick}, {@code /nickname}, {@code /setnick} и т. д., имена
 *       {@code Dinnerbone}/{@code Grumm}). После каждой попытки проверяет, изменилось
 *       ли имя игрока в списке игроков; как только изменилось — останавливается.</li>
 *   <li><b>Локально</b> — принудительный переворот только в вашем клиенте
 *       ({@link DinnerboneFlip} + миксин {@code LivingEntityRendererDinnerboneMixin}).
 *       Другие игроки этого не видят, зато работает на любом сервере и годится для
 *       скриншотов/стрима.</li>
 *   <li><b>Ник + локально</b> — оба варианта сразу: пока сервер думает, вы уже
 *       перевёрнуты у себя.</li>
 * </ol>
 *
 * <p>Про «а вдруг сервер не мой»: список команд можно править прямо в меню, добавляя
 * то, что поддерживает конкретный сервер (например, {@code /nick set Dinnerbone} или
 * ник с префиксом доната). Если ни одна не сработала — модуль честно скажет об этом в
 * чат, а не будет молча делать вид, что перевернул вас для всех.
 */
public final class DinnerboneModule extends ClientModule {

    private static final String[] STRATEGIES = {"Ник + локально", "Только ник", "Только локально"};
    private static final String[] LOCAL_TARGETS = {"Себя", "Себя и других"};
    private static final String DEFAULT_COMMANDS =
            "/nick Dinnerbone;/nickname Dinnerbone;/setnick Dinnerbone;/nick set Dinnerbone;/nick Grumm";

    public static final DinnerboneModule INSTANCE = new DinnerboneModule();

    private final ModeSetting strategy = this.add(
            new ModeSetting("Способ", "Ник + локально", STRATEGIES));
    private final ModeSetting localTargets = this.add(
            new ModeSetting("Переворачивать локально", "Себя", LOCAL_TARGETS));
    private final StringSetting commands = this.add(
            new StringSetting("Команды ника (через ;)", DEFAULT_COMMANDS, 300));
    private final NumberSetting delay = this.add(
            new NumberSetting("Пауза между командами (мс)", 4000.0, 1000.0, 20000.0, 500.0));
    private final BooleanSetting stopOnSuccess = this.add(
            new BooleanSetting("Останавливаться после успеха", true));
    private final BooleanSetting notify = this.add(
            new BooleanSetting("Писать в чат", true));
    private final ActionSetting checkName = this.add(
            new ActionSetting("Проверить ник").actionLabel("Проверить").action(this::reportName));
    private final ActionSetting sendNow = this.add(
            new ActionSetting("Отправить команду сейчас").actionLabel("Отправить").action(this::sendNext));

    private int timer;
    private int attempt;
    /** Ник применился или сервер отказал во всех командах — больше не дёргаем. */
    private boolean finished;

    private DinnerboneModule() {
        super("Dinner Bone", "Переворот игрока как от ника Dinnerbone", ModuleCategory.MISC);
        this.commands.visibleWhen(this::nickEnabled);
        this.delay.visibleWhen(this::nickEnabled);
        this.stopOnSuccess.visibleWhen(this::nickEnabled);
        this.sendNow.visibleWhen(this::nickEnabled);
        this.localTargets.visibleWhen(this::localEnabled);
    }

    // ── включение / выключение ────────────────────────────────────────────────

    @Override
    protected void onEnable() {
        this.timer = 0;
        this.attempt = 0;
        this.finished = false;
        this.applyLocal();
        if (!this.nickEnabled()) {
            return;
        }
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().getConnection() == null) {
            this.applyLocal();
            return;
        }
        this.info("Dinner Bone: пробую ник — если сервер не разрешает /nick, "
                + "перевернуть вас для других игроков невозможно (это рендер клиентов, а не пакет).");
        this.timer = Math.max(1, this.intervalTicks());
    }

    @Override
    protected void onDisable() {
        DinnerboneFlip.configure(false, false);
        this.timer = 0;
        this.attempt = 0;
        this.finished = false;
    }

    @Override
    protected void onTick() {
        this.applyLocal();
        if (!this.nickEnabled() || this.finished) {
            return;
        }
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().getConnection() == null) {
            return;
        }
        if (this.isNickApplied()) {
            this.finished = true;
            this.info("Dinner Bone: ник применился (" + this.currentName()
                    + ") — другие игроки видят вас перевёрнутым.");
            return;
        }
        if (++this.timer < this.intervalTicks()) {
            return;
        }
        List<String> queue = this.commandQueue();
        if (queue.isEmpty()) {
            return;
        }
        if (this.attempt >= queue.size()) {
            if (this.stopOnSuccess.get()) {
                this.finished = true;
                this.info("Dinner Bone: сервер не дал ник ни одной из " + queue.size()
                        + " команд — остаётся локальный переворот (виден только вам).");
                return;
            }
            this.attempt = 0;
        }
        this.sendNext();
    }

    // ── ник ───────────────────────────────────────────────────────────────────

    private void sendNext() {
        List<String> queue = this.commandQueue();
        if (queue.isEmpty()) {
            return;
        }
        String command = queue.get(this.attempt % queue.size());
        this.attempt++;
        this.timer = 0;
        this.sendCommand(command);
        if (this.notify.get()) {
            this.info("Dinner Bone: " + command);
        }
    }

    private void sendCommand(String command) {
        var connection = Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.connection;
        if (connection == null) {
            return;
        }
        String trimmed = command.startsWith("/") ? command.substring(1) : command;
        if (!trimmed.isBlank()) {
            connection.sendCommand(trimmed.trim());
        }
    }

    /** Команды из настройки: делим по {@code ;}, убираем пустые. */
    private List<String> commandQueue() {
        List<String> queue = new ArrayList<>();
        String raw = this.commands.get();
        if (raw == null) {
            return queue;
        }
        for (String part : raw.split(";")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                queue.add(trimmed);
            }
        }
        return queue;
    }

    /** Сервер уже переименовал игрока в Dinnerbone/Grumm (с префиксами ранга или без)? */
    private boolean isNickApplied() {
        return matchesFlipName(this.tabName()) || matchesFlipName(this.profileName());
    }

    /** Сравнение с учётом цветов и префиксов: имя может быть «[VIP] Dinnerbone». */
    private static boolean matchesFlipName(String name) {
        if (name == null) {
            return false;
        }
        String clean = ChatFormatting.stripFormatting(name).trim();
        return clean.equalsIgnoreCase("Dinnerbone") || clean.equalsIgnoreCase("Grumm")
                || clean.endsWith("Dinnerbone") || clean.endsWith("Grumm");
    }

    /** Ник из списка игроков (с префиксами и цветами). */
    private String tabName() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return null;
        }
        PlayerInfo info = minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());
        if (info == null) {
            return null;
        }
        Component display = info.getTabListDisplayName();
        return display == null ? null : display.getString();
    }

    /** Имя из профиля игрока (то, что сервер прислал как настоящее имя). */
    private String profileName() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.getConnection() == null) {
            return null;
        }
        PlayerInfo info = minecraft.getConnection().getPlayerInfo(minecraft.player.getUUID());
        if (info != null && info.getProfile() != null && info.getProfile().name() != null) {
            return info.getProfile().name();
        }
        return minecraft.player.getName().getString();
    }

    /** Имя игрока так, как его видит клиент: ник из списка игроков, иначе из профиля. */
    private String currentName() {
        String tab = this.tabName();
        if (tab != null && !ChatFormatting.stripFormatting(tab).isBlank()) {
            return ChatFormatting.stripFormatting(tab).trim();
        }
        return this.profileName();
    }

    private void reportName() {
        String name = this.currentName();
        this.info("Dinner Bone: ваше имя сейчас — " + (name == null ? "неизвестно" : name)
                + "; локальный переворот "
                + (DinnerboneFlip.knownUnsupported() ? "недоступен в этой сборке" : "включён (виден только вам)")
                + ".");
    }

    // ── локальный переворот ───────────────────────────────────────────────────

    private void applyLocal() {
        DinnerboneFlip.configure(this.localEnabled(), this.localTargets.is("Себя и других"));
        if (this.localEnabled() && DinnerboneFlip.knownUnsupported() && DinnerboneFlip.takeUnsupportedWarning()) {
            this.info("Dinner Bone: в этой сборке 26.2 нет поля флага переворота — "
                    + "локальный вариант не сработает, остаётся ник.");
        }
    }

    private boolean localEnabled() {
        return !this.strategy.is("Только ник");
    }

    private boolean nickEnabled() {
        return !this.strategy.is("Только локально");
    }

    private int intervalTicks() {
        return Math.max(1, (int) Math.round(this.delay.get() / 50.0));
    }

    private void info(String text) {
        if (this.notify.get()) {
            ChatMessages.displayInfo(text);
        }
    }

    /** Вызывается миксином рендера: переворачивать ли эту сущность. */
    public static boolean shouldFlip(Entity entity) {
        return DinnerboneFlip.shouldFlip(entity);
    }
}
