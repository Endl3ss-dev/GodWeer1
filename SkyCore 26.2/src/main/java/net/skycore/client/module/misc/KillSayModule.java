package net.skycore.client.module.misc;

import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.StringSetting;

/**
 * KillSay — сообщение в чат после убийства игрока.
 *
 * <p>Отличие от {@code AutoMessage}: там текст зашит в код и всегда один и тот же,
 * здесь он задаётся строкой в настройках. Текст можно делать шаблоном:
 * <ul>
 *   <li>{@code {name}} — ник убитого;</li>
 *   <li>{@code {me}} — свой ник.</li>
 * </ul>
 *
 * <p>Убийство определяется по серверному сообщению в чате (те же пакеты, что слушает
 * {@code AutoMessage}): клиент не знает о смерти сущности на сервере, а сообщение
 * «вы убили …» приходит на всех серверах, где такие сообщения включены. Список
 * маркеров — ниже; проверка идёт по тексту без форматирования и без учёта регистра.
 *
 * <p>Сообщение отправляется один раз на строку чата: сервер может продублировать
 * уведомление, поэтому одинаковый ник в пределах {@link #DUPLICATE_WINDOW_MS} не
 * повторяется.
 */
public final class KillSayModule extends ClientModule {

    public static final KillSayModule INSTANCE = new KillSayModule();

    /** Маркеры серверных сообщений об убийстве (русские и английские серверы). */
    private static final String[] KILL_MARKERS = {
            "вы убили",
            "ты убил",
            "вы одолели",
            "you killed",
            "you have killed",
            "you slayed",
    };

    private static final long DUPLICATE_WINDOW_MS = 1500L;

    private final StringSetting message = this.add(
            new StringSetting("Сообщение", "{name} ez", 100));

    private String lastVictim = "";
    private long lastSentAt;

    private KillSayModule() {
        super("Kill Say", "Пишет в чат заданный текст после убийства игрока", ModuleCategory.MISC);
        SkycoreClient.events().subscribe(PacketReceiveEvent.class, this::onPacket);
    }

    private void onPacket(PacketReceiveEvent event) {
        if (!this.isEnabled()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.level == null || player.connection == null) {
            return;
        }

        String text = chatText(event.getPacket());
        if (text == null) {
            return;
        }

        String victim = victimFromMessage(text);
        if (victim == null || victim.isEmpty()) {
            return;
        }
        if (victim.equalsIgnoreCase(player.getGameProfile().name())) {
            return;
        }

        long now = System.currentTimeMillis();
        if (victim.equalsIgnoreCase(this.lastVictim) && now - this.lastSentAt < DUPLICATE_WINDOW_MS) {
            return;
        }

        String outgoing = this.compose(victim, player.getGameProfile().name());
        if (outgoing.isBlank()) {
            return;
        }

        player.connection.sendChat(outgoing);
        this.lastVictim = victim;
        this.lastSentAt = now;
    }

    /** Подставляет ник в шаблон; пустой шаблон означает «ничего не отправлять». */
    String compose(String victim, String self) {
        String template = this.message.get();
        if (template == null || template.isBlank()) {
            return "";
        }
        String text = template.replace("{name}", victim);
        if (self != null) {
            text = text.replace("{me}", self);
        }
        return text.trim();
    }

    private static String chatText(Packet<?> packet) {
        if (packet instanceof ClientboundSystemChatPacket system) {
            return strip(system.content());
        }
        if (packet instanceof ClientboundDisguisedChatPacket disguised) {
            return strip(disguised.message());
        }
        return null;
    }

    private static String strip(Component component) {
        return component == null ? null : ChatFormatting.stripFormatting(component.getString());
    }

    /**
     * Ник из сообщения об убийстве.
     *
     * <p>Берётся первое слово после маркера, из него снимается пунктуация — так же,
     * как это делает {@code AutoMessage}, потому что серверы оформляют уведомление
     * по-разному («Вы убили Steve!», «Вы убили Steve.», «You killed Steve»).
     */
    static String victimFromMessage(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        String lower = text.toLowerCase(Locale.ROOT);
        int markerAt = -1;
        int markerLength = 0;
        for (String marker : KILL_MARKERS) {
            int index = lower.indexOf(marker);
            if (index >= 0 && (markerAt < 0 || index < markerAt)) {
                markerAt = index;
                markerLength = marker.length();
            }
        }
        if (markerAt < 0) {
            return null;
        }
        String rest = text.substring(markerAt + markerLength).trim();
        if (rest.isEmpty()) {
            return null;
        }
        rest = rest.replaceAll("^[!.,:;\\s]+", "");
        String[] parts = rest.split("\\s+");
        if (parts.length == 0) {
            return null;
        }
        String name = parts[0].replaceAll("[!.,:;]+$", "");
        return name.isEmpty() ? null : name;
    }
}
