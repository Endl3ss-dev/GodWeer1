package mods.voicechat;

import de.maxhenkel.configbuilder.ConfigBuilder;
import mods.voicechat.command.VoicechatCommands;
import mods.voicechat.config.ServerConfig;
import mods.voicechat.config.Translations;
import mods.voicechat.intercompatibility.CommonCompatibilityManager;
import mods.voicechat.logging.Log4JVoicechatLogger;
import mods.voicechat.logging.VoicechatLogger;
import mods.voicechat.plugins.PluginManager;
import mods.voicechat.voice.server.ServerVoiceEvents;

import net.minecraft.client.Minecraft;

import java.nio.file.Path;
import java.util.regex.Pattern;

public abstract class Voicechat {

    public static final String MODID = "voicechat";
    public static final VoicechatLogger LOGGER = new Log4JVoicechatLogger(MODID);
    public static ServerVoiceEvents SERVER;
    public static ServerConfig SERVER_CONFIG;
    public static Translations TRANSLATIONS;

    public static int COMPATIBILITY_VERSION = BuildConstants.COMPATIBILITY_VERSION;

    public static final Pattern GROUP_REGEX = Pattern.compile("^[^\\n\\r\\t\\s][^\\n\\r\\t]{0,23}$");

    public void initialize() {
        if (debugMode()) {
            LOGGER.warn("Running in debug mode - Don't leave this enabled in production!");
        }

        LOGGER.info("Compatibility version {}", COMPATIBILITY_VERSION);

        initializeConfigs();

        CommonCompatibilityManager.INSTANCE.getNetManager().init();
        SERVER = new ServerVoiceEvents();
        initPlugins();
        registerCommands();
    }

    protected void initPlugins() {
        PluginManager.instance().init();
    }

    protected void registerCommands() {
        CommonCompatibilityManager.INSTANCE.onRegisterServerCommands(VoicechatCommands::register);
    }

    public void initializeConfigs() {
        SERVER_CONFIG = ConfigBuilder.builder(ServerConfig::new).path(getVoicechatConfigFolderInternal().resolve("voicechat-server.properties")).build();
        TRANSLATIONS = ConfigBuilder.builder(Translations::new).path(getVoicechatConfigFolderInternal().resolve("translations.properties")).build();
    }

    public static boolean debugMode() {
        return false;
    }

    protected Path getVoicechatConfigFolderInternal() {
        return getVoicechatConfigFolder();
    }

    public static Path getVoicechatConfigFolder() {
        return Minecraft.getInstance().gameDir.toPath().resolve(MODID);
    }

    public static Path getConfigFolder() {
        return Minecraft.getInstance().gameDir.toPath().resolve(MODID).resolve("config");
    }

}
