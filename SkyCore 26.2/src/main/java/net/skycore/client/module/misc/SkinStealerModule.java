package net.skycore.client.module.misc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.skycore.client.account.SkinService;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.StringSetting;
import net.skycore.client.command.ChatMessages;

/**
 * SkinStealer — получить скин по нику и подставить его себе.
 *
 * <p>Поведение по просьбе: модуль <b>не рисует ничего на экране</b>. Единственный
 * видимый результат — строка в чате с ником, чтобы им можно было воспользоваться
 * командой сервера {@code /skin <ник>}, и необязательные уведомления об успехе или
 * ошибке.
 *
 * <p>Как работает:
 * <ol>
 *   <li>ник из настройки отправляется в {@link SkinService} — он качает скин в фоновом
 *       потоке и регистрирует текстуру (скачивание с сети в кадре недопустимо);</li>
 *   <li>в тике модуль ждёт готовую текстуру, после чего собирает {@link PlayerSkin} из
 *       загруженного {@link Identifier} — так же, как это делает
 *       {@code TextureHashHelper.skinForId};</li>
 *   <li>если включено «Применить скин», собранный {@link PlayerSkin} отдаётся миксину
 *       {@code AbstractClientPlayerSkinOverrideMixin}, и локальный игрок рисуется с
 *       чужим скином. Скин видит только клиент — сервер о подмене не знает;</li>
 *   <li>если включена «История ников», ник дописывается в {@code skycore/skin-history.txt}
 *       и остаётся там между запусками.</li>
 * </ol>
 *
 * <p>Ограничение, о котором честно стоит помнить: загрузка идёт через
 * {@link SkinService} (Mojang API + crafatar), поэтому нужен интернет, а модель скина
 * всегда широкая (Slim-скины определяются только по подписи профиля, которой у
 * скачанного PNG нет).
 */
public final class SkinStealerModule extends ClientModule {

    private static final long TIMEOUT_MS = 12000L;
    private static final int HISTORY_LIMIT = 50;
    private static final Path HISTORY_FILE = Path.of("skycore", "skin-history.txt");

    private static final List<String> HISTORY = new ArrayList<>();
    private static boolean historyLoaded;

    /** Подмена для миксина: {@code null} — рисуем штатный скин. */
    private static volatile PlayerSkin override;
    private static volatile String overrideNick = "";

    public static final SkinStealerModule INSTANCE = new SkinStealerModule();
    private final StringSetting target = this.add(new StringSetting("Ник цели", "", 16));
    private final BooleanSetting apply = this.add(new BooleanSetting("Применить скин", true));
    private final BooleanSetting history = this.add(new BooleanSetting("История ников", true));
    private final BooleanSetting notifySuccess = this.add(new BooleanSetting("Уведомление об успехе", true));
    private final BooleanSetting notifyError = this.add(new BooleanSetting("Уведомление об ошибке", true));

    private String requestedNick = "";
    private long requestedAt;
    private boolean settled;

    private SkinStealerModule() {
        super("Skin Stealer", "Загружает скин по нику и подставляет его вам", ModuleCategory.MISC);
    }

    // ---- жизненный цикл

    @Override
    protected void onEnable() {
        request(this.target.get(), true);
    }

    @Override
    protected void onDisable() {
        this.requestedNick = "";
        this.settled = false;
        clearOverride();
    }

    /** Ник, который сейчас подставлен (или пустая строка). */
    public static String activeNick() {
        return overrideNick;
    }

    /** Подмена для миксина: только для локального игрока и только когда она включена. */
    public static PlayerSkin overrideFor(AbstractClientPlayer player) {
        PlayerSkin skin = override;
        if (skin == null || player == null) {
            return null;
        }
        return player == Minecraft.getInstance().player ? skin : null;
    }

    // ---- работа

    /**
     * Запускает загрузку ника.
     *
     * @param nick    ник цели
     * @param announce писать ли ник в чат (при включении модуля — да, при смене
     *                 настройки — тоже да, чтобы ник было видно сразу)
     */
    public void request(String nick, boolean announce) {
        String trimmed = nick == null ? "" : nick.trim();
        if (trimmed.isEmpty()) {
            if (this.notifyError.get()) {
                ChatMessages.displayError("SkinStealer: ник не задан");
            }
            return;
        }
        if (!trimmed.matches("[A-Za-z0-9_]{3,16}")) {
            if (this.notifyError.get()) {
                ChatMessages.displayError("SkinStealer: ник не найден");
            }
            return;
        }
        if (trimmed.equalsIgnoreCase(this.requestedNick) && !this.settled) {
            return;
        }

        this.requestedNick = trimmed;
        this.requestedAt = System.currentTimeMillis();
        this.settled = false;
        clearOverride();
        SkinService.prefetch(trimmed);

        if (announce) {
            // основной результат модуля — ник в чате для команды /skin
            ChatMessages.displayInfo("SkinStealer: /skin " + trimmed);
        }
        remember(trimmed);
    }

    @Override
    protected void onTick() {
        // настройки не имеют слушателей: смену ника и выключение подмены замечаем здесь
        String current = this.target.get() == null ? "" : this.target.get().trim();
        if (!current.isEmpty() && !current.equalsIgnoreCase(this.requestedNick)) {
            request(current, true);
            return;
        }
        if (!this.apply.get() && !overrideNick.isEmpty()) {
            clearOverride();
        }
        if (this.requestedNick.isEmpty() || this.settled) {
            return;
        }
        Identifier texture = SkinService.skinIfReady(this.requestedNick);
        if (texture != null) {
            this.settled = true;
            applyTexture(texture, this.requestedNick);
            return;
        }
        if (System.currentTimeMillis() - this.requestedAt > TIMEOUT_MS) {
            this.settled = true;
            if (this.notifyError.get()) {
                ChatMessages.displayError("SkinStealer: ник не найден — " + this.requestedNick);
            }
        }
    }

    private void applyTexture(Identifier texture, String nick) {
        if (!this.apply.get()) {
            if (this.notifySuccess.get()) {
                ChatMessages.displayInfo("SkinStealer: скин найден — " + nick);
            }
            return;
        }
        try {
            // та же сборка скина, что в TextureHashHelper.skinForId: текстура из
            // загруженного файла, без плаща и элитр, широкая модель
            ClientAsset.DownloadedTexture body = new ClientAsset.DownloadedTexture(texture,
                    "skycore_skinstealer/" + nick.toLowerCase(java.util.Locale.ROOT));
            override = PlayerSkin.insecure(body, null, null, PlayerModelType.WIDE);
            overrideNick = nick;
            if (this.notifySuccess.get()) {
                ChatMessages.displayInfo("SkinStealer: скин загружен и применён — " + nick);
            }
        } catch (RuntimeException failure) {
            override = null;
            overrideNick = "";
            if (this.notifyError.get()) {
                ChatMessages.displayError("SkinStealer: не удалось применить скин");
            }
        }
    }

    private static void clearOverride() {
        override = null;
        overrideNick = "";
    }

    // ---- история

    private void remember(String nick) {
        if (!this.history.get()) {
            return;
        }
        loadHistory();
        for (String known : HISTORY) {
            if (known.equalsIgnoreCase(nick)) {
                return;
            }
        }
        HISTORY.add(nick);
        while (HISTORY.size() > HISTORY_LIMIT) {
            HISTORY.remove(0);
        }
        saveHistory();
    }

    /** Все запомненные ники (последние — в конце). */
    public static List<String> history() {
        loadHistory();
        return Collections.unmodifiableList(HISTORY);
    }

    private static void loadHistory() {
        if (historyLoaded) {
            return;
        }
        historyLoaded = true;
        try {
            if (!Files.exists(HISTORY_FILE)) {
                return;
            }
            for (String line : Files.readAllLines(HISTORY_FILE, StandardCharsets.UTF_8)) {
                String nick = line.trim();
                if (!nick.isEmpty() && HISTORY.stream().noneMatch(known -> known.equalsIgnoreCase(nick))) {
                    HISTORY.add(nick);
                }
            }
        } catch (IOException ignored) {
            // файл истории не критичен: без него модуль работает как обычно
        }
    }

    private static void saveHistory() {
        try {
            Path parent = HISTORY_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(HISTORY_FILE, HISTORY, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // см. loadHistory
        }
    }
}
