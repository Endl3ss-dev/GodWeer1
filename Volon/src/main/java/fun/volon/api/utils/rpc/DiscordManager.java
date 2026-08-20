package fun.volon.api.utils.rpc;

import lombok.Getter;
import net.minecraft.client.network.ServerInfo;
import fun.volon.api.QClient;
import fun.volon.api.utils.rpc.utils.DiscordEventHandlers;
import fun.volon.api.utils.rpc.utils.DiscordRPC;
import fun.volon.api.utils.rpc.utils.DiscordRichPresence;

@Getter
public class DiscordManager implements QClient {

    private DiscordDaemonThread discordDaemonThread;
    private boolean running;

    public static DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    public static DiscordRPC discordRPC = DiscordRPC.INSTANCE;

    private void cppInit() {
        discordDaemonThread = new DiscordDaemonThread();
        running = true;
    }

    public void init() {
        cppInit();
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().build();

        DiscordRPC.INSTANCE.Discord_Initialize(DiscordApplication.applicationId(), handlers, true, "");
        discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        applyPresence();
        discordRPC.Discord_UpdatePresence(discordRichPresence);

        new Thread(() -> {
            while (running) {
                try {
                    applyPresence();
                    DiscordRPC.INSTANCE.Discord_UpdatePresence(discordRichPresence);
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }, "Discord-RPC-Updater").start();

        discordDaemonThread.start();
    }

    private void applyPresence() {
        String serverIP = "Singleplayer";
        if (mc != null && mc.getCurrentServerEntry() != null) {
            serverIP = mc.getCurrentServerEntry().address;
        }
        
        discordRichPresence.details = "Играет на: " + serverIP;
        discordRichPresence.state = "User | EvelPremium";
        discordRichPresence.largeImageKey = DiscordApplication.LARGE_IMAGE_KEY;
        discordRichPresence.largeImageText = DiscordApplication.NAME;
        discordRichPresence.button_label_2 = DiscordApplication.TELEGRAM_BUTTON_LABEL;
        discordRichPresence.button_url_2 = DiscordApplication.TELEGRAM_BUTTON_URL;
    }

    public DiscordManager start() {
        init();
        return this;
    }

    public void stopRPC() {
        running = false;
        DiscordRPC.INSTANCE.Discord_Shutdown();
        if (discordDaemonThread != null) {
            discordDaemonThread.interrupt();
        }
    }

    private class DiscordDaemonThread extends Thread {
        @Override
        public void run() {
            this.setName("Discord-RPC");

            try {
                while (running) {
                    DiscordRPC.INSTANCE.Discord_RunCallbacks();
                    Thread.sleep(15 * 1000);
                }
            } catch (Exception exception) {
                stopRPC();
            }

            super.run();
        }
    }
}
