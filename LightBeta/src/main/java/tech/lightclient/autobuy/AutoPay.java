package tech.lightclient.autobuy;

import tech.lightclient.autobuy.QClient;
import net.minecraft.text.Text;

/**
 * Автоперевод средств на твинк (порт ветки {@code a.l} из DimalaBuy).
 * Раз в {@link AutoBuyConfig#autoPayIntervalSeconds} секунд запрашивает баланс ({@code bal})
 * и, если он выше удерживаемого ({@link AutoBuyConfig#autoPayKeepBalance}), переводит излишек
 * указанному нику: {@code pay <ник> <баланс - удерживаемое>}.
 */
public class AutoPay implements QClient {
    private int timer = 0;
    private boolean waiting = false;
    private int waitTicks = 0;
    private boolean started = false;

    public void tick() {
        if (mc.player == null) {
            return;
        }
        AutoBuyConfig cfg = AutoBuyConfig.cfg();
        if (!cfg.autoPay) {
            started = false;
            waiting = false;
            return;
        }
        if (!started) {
            started = true;
            timer = intervalTicks(cfg);
        }
        if (waiting) {
            waitTicks--;
            if (waitTicks <= 0) {
                waiting = false;
                timer = intervalTicks(cfg);
            }
            return;
        }
        if (timer > 0) {
            timer--;
            return;
        }
        mc.player.networkHandler.sendChatCommand("money");
        waiting = true;
        waitTicks = 100;
    }

    /** Вызывается из движка при распознавании баланса в чате. */
    public void onBalance(long balance) {
        if (!waiting) {
            return;
        }
        waiting = false;
        AutoBuyConfig cfg = AutoBuyConfig.cfg();
        timer = intervalTicks(cfg);
        if (cfg.autoPayNick == null || cfg.autoPayNick.isBlank()) {
            return;
        }
        if (balance > cfg.autoPayKeepBalance) {
            long amount = balance - cfg.autoPayKeepBalance;
            mc.player.networkHandler.sendChatCommand("pay " + cfg.autoPayNick.trim() + " " + amount);
            if (mc.player != null) {
                mc.player.sendMessage(Text.literal("§a[AutoPay] Переведено §f" + amount
                        + "§a игроку §f" + cfg.autoPayNick.trim()), false);
            }
        }
    }

    private int intervalTicks(AutoBuyConfig cfg) {
        return Math.max(60, cfg.autoPayIntervalSeconds * 20);
    }
}
