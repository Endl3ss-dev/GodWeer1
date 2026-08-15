package antileak.base.api.utils.rpc;

import com.adl.nativeprotect.User;
import lombok.Getter;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.network.ServerInfo;
import antileak.base.api.QClient;
import antileak.base.api.utils.rpc.utils.DiscordEventHandlers;
import antileak.base.api.utils.rpc.utils.DiscordRPC;
import antileak.base.api.utils.rpc.utils.DiscordRichPresence;
import antileak.base.api.utils.rpc.utils.DiscordUser;
import antileak.base.client.modules.impl.render.base.implement.WaterMarkV2;

@Getter
public class DiscordManager implements QClient {

    private DiscordDaemonThread discordDaemonThread;
    private long APPLICATION_ID;

    private boolean running;

    private String image;
    private String site;
    private String discord;
    String uid = User.getInstance().profile("uid");
    String username = User.getInstance().profile("username");
    String hwid = User.getInstance().profile("hwid");

    @Getter
    private static String discordUsername;
    @Getter
    private static String discordAvatarId;
    @Getter
    private static String discordUserId;

    private void cppInit() {
        discordDaemonThread = new DiscordDaemonThread();
        APPLICATION_ID = 1521560410019463278L;
        running = true;
        image = "https://i.ibb.co/Cp9bMtL";
        site = "https://punchdlc.fun";
        discord = "https://discord.gg/kEFkH9BFtD";
    }

    String state = "";

    public static DiscordRichPresence discordRichPresence = new DiscordRichPresence();
    public static DiscordRPC discordRPC = DiscordRPC.INSTANCE;

    public void init() {
        cppInit();
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .ready(user -> {
                    discordUsername = user.username;
                    discordAvatarId = user.avatar;
                    discordUserId = user.userId;
                    WaterMarkV2.setDiscordUser(user.userId, user.username, user.avatar);
                })
                .build();

        DiscordRPC.INSTANCE.Discord_Initialize(String.valueOf(APPLICATION_ID), handlers, true, "");
        discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        discordRPC.Discord_UpdatePresence(discordRichPresence);

        new Thread(() -> {
            while (running) {
                try {
                    String displayName = discordUsername != null ? discordUsername : username;
                    discordRichPresence.details = "Punch Client | v2.0";
                    discordRichPresence.state = "User | " + displayName;
                    discordRichPresence.largeImageKey = "avatar";
                    discordRichPresence.button_label_1 = "";
                    discordRichPresence.button_url_1 = "";
                    discordRichPresence.button_label_2 = "Дискорд";
                    discordRichPresence.button_url_2 = "https://discord.gg/SH9nkt3D5";
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
