package tech.lightclient.client.utils.discord;

import com.adl.nativeprotect.User;

import tech.lightclient.LightClient;
import tech.lightclient.client.utils.discord.utils.DiscordEventHandlers;
import tech.lightclient.client.utils.discord.utils.DiscordRPC;
import tech.lightclient.client.utils.discord.utils.DiscordRichPresence;
import tech.lightclient.client.utils.discord.utils.RPCButton;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Setter
@Getter
public class DiscordManager {
    private static final Logger LOGGER = LogManager.getLogger("Light-DiscordRPC");
    private static final String APPLICATION_ID = "1513271190234595358";

    private final DiscordDaemonThread discordDaemonThread = new DiscordDaemonThread();
    private boolean running = true;
    private DiscordInfo info = new DiscordInfo("Unknown", "", "");
    private Identifier avatarId;

    public void init() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("linux")) {
            return;
        }

        running = true;

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .ready((user) -> {
                    LightClient.getDiscordManager().setInfo(
                            new DiscordInfo(user.username,
                                    "https://www.upload.ee/image/19437178/lv_0_20260607222914.gif",
                                    user.userId));

                    String uid = User.getInstance().profile("uid");
                    String role = LightClient.getRole();
                    String username = User.getInstance().profile("username");

                    DiscordRichPresence presence = new DiscordRichPresence.Builder()
                            .setStartTimestamp(System.currentTimeMillis() / 1000)
                            .setDetails("Логин: " + username)
                            .setState("Айди пользователя: " + uid)
                            .setButtons(RPCButton.create("Телеграм", "https://t.me/lightclientmc"),
                                    RPCButton.create("Сайт", "https://lightclient.tech"))
                            .build();
                    DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
                })
                .build();

        try {
            DiscordRPC.INSTANCE.Discord_Initialize(APPLICATION_ID, handlers, true, "");
            if (!discordDaemonThread.isAlive()) {
                discordDaemonThread.setDaemon(true);
                discordDaemonThread.start();
            }
        } catch (Throwable t) {
            running = false;
            LOGGER.error("Не удалось инициализировать Discord RPC", t);
        }
    }

    public void stopRPC() {
        this.running = false;
        try {
            DiscordRPC.INSTANCE.Discord_Shutdown();
        } catch (Throwable throwable) {
            LOGGER.debug("Discord RPC shutdown skipped", throwable);
        }
    }

    public void load() throws IOException {
        if (avatarId == null && !info.avatarUrl.isEmpty()) {
            avatarId = Buffer.registerDynamicTexture("avatar-", Buffer.getHeadFromURL(info.avatarUrl));
        }
    }

    public Identifier getAvatarId() {
        return avatarId;
    }

    private class DiscordDaemonThread extends Thread {
        @Override
        public void run() {
            this.setName("Discord-RPC");
            try {
                MinecraftClient mc = MinecraftClient.getInstance();
                while (mc == null || mc.getTextureManager() == null) {
                    Thread.sleep(500);
                    mc = MinecraftClient.getInstance();
                }

                while (LightClient.getDiscordManager().isRunning()) {
                    DiscordRPC.INSTANCE.Discord_RunCallbacks();
                    DiscordRPC.INSTANCE.Discord_UpdateConnection();
                    load();
                    Thread.sleep(1500);
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                running = false;
            } catch (Throwable throwable) {
                running = false;
                LOGGER.warn("Discord RPC недоступен, интеграция отключена: {}", throwable.getMessage());
            }
        }
    }

    public record DiscordInfo(String userName, String avatarUrl, String userId) {
    }
}

