package mods.voicechat;

import com.sun.jna.Platform;
import de.maxhenkel.configbuilder.ConfigBuilder;
import de.maxhenkel.configbuilder.MigratableConfig;
import mods.voicechat.config.CategoryVolumeConfig;
import mods.voicechat.config.ClientConfig;
import mods.voicechat.config.PlayerVolumeConfig;
import mods.voicechat.intercompatibility.CommonCompatibilityManager;
import mods.voicechat.macos.VersionCheck;
import mods.voicechat.plugins.impl.opus.OpusManager;
import mods.voicechat.profile.UsernameCache;
import mods.voicechat.voice.client.ClientManager;

public abstract class VoicechatClient {

    public static ClientConfig CLIENT_CONFIG;
    public static PlayerVolumeConfig PLAYER_VOLUME_CONFIG;
    public static CategoryVolumeConfig CATEGORY_VOLUME_CONFIG;
    public static UsernameCache USERNAME_CACHE;

    public VoicechatClient() {
    }

    public void initializeConfigs() {
        CLIENT_CONFIG = ConfigBuilder.builder(ClientConfig::new).path(Voicechat.getVoicechatConfigFolder().resolve("voicechat-client.properties")).build();

        if (CLIENT_CONFIG.configVersion.get() == null || CLIENT_CONFIG.configVersion.get() < 1) {
            migrateConfigVersion();
        }
        PLAYER_VOLUME_CONFIG = new PlayerVolumeConfig(Voicechat.getVoicechatConfigFolder().resolve("player-volumes.properties"));
        CATEGORY_VOLUME_CONFIG = new CategoryVolumeConfig(Voicechat.getVoicechatConfigFolder().resolve("category-volumes.properties"));
        USERNAME_CACHE = new UsernameCache(Voicechat.getVoicechatConfigFolder().resolve("username-cache.json").toFile());
    }

    private void migrateConfigVersion() {
        MigratableConfig migratableConfig = new MigratableConfig() {
            @Override
            public String get(String key) {
                try {
                    java.lang.reflect.Field[] fields = ClientConfig.class.getDeclaredFields();
                    for (java.lang.reflect.Field field : fields) {
                        if (de.maxhenkel.configbuilder.entry.ConfigEntry.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            de.maxhenkel.configbuilder.entry.ConfigEntry<?> entry = (de.maxhenkel.configbuilder.entry.ConfigEntry<?>) field.get(CLIENT_CONFIG);
                            if (entry != null) {
                                try {
                                    java.lang.reflect.Method getKeyMethod = entry.getClass().getMethod("getKey");
                                    String entryKey = (String) getKeyMethod.invoke(entry);
                                    if (key.equals(entryKey)) {
                                        Object value = entry.get();
                                        return value != null ? value.toString() : null;
                                    }
                                } catch (Exception e) {
                                    // Try alternative approach
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // Ignore
                }
                return null;
            }

            @Override
            public void set(String key, String value) {
                try {
                    java.lang.reflect.Field[] fields = ClientConfig.class.getDeclaredFields();
                    for (java.lang.reflect.Field field : fields) {
                        if (de.maxhenkel.configbuilder.entry.ConfigEntry.class.isAssignableFrom(field.getType())) {
                            field.setAccessible(true);
                            de.maxhenkel.configbuilder.entry.ConfigEntry<?> entry = (de.maxhenkel.configbuilder.entry.ConfigEntry<?>) field.get(CLIENT_CONFIG);
                            if (entry != null) {
                                try {
                                    java.lang.reflect.Method getKeyMethod = entry.getClass().getMethod("getKey");
                                    String entryKey = (String) getKeyMethod.invoke(entry);
                                    if (key.equals(entryKey)) {
                                        if (entry instanceof de.maxhenkel.configbuilder.entry.IntegerConfigEntry) {
                                            ((de.maxhenkel.configbuilder.entry.IntegerConfigEntry) entry).set(Integer.parseInt(value)).save();
                                        } else if (entry instanceof de.maxhenkel.configbuilder.entry.BooleanConfigEntry) {
                                            ((de.maxhenkel.configbuilder.entry.BooleanConfigEntry) entry).set(Boolean.parseBoolean(value)).save();
                                        } else if (entry instanceof de.maxhenkel.configbuilder.entry.DoubleConfigEntry) {
                                            ((de.maxhenkel.configbuilder.entry.DoubleConfigEntry) entry).set(Double.parseDouble(value)).save();
                                        } else if (entry instanceof de.maxhenkel.configbuilder.entry.StringConfigEntry) {
                                            ((de.maxhenkel.configbuilder.entry.StringConfigEntry) entry).set(value).save();
                                        }
                                        return;
                                    }
                                } catch (Exception e) {
                                    // Try alternative approach
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Voicechat.LOGGER.warn("Failed to migrate config entry: {}", key, e);
                }
            }
        };
        ClientConfig.migrate(migratableConfig);
    }

    public void initializeClient() {
        initializeConfigs();

        ClientManager.instance();

        OpusManager.opusNativeCheck();

        if (Platform.isMac()) {
            if (!VersionCheck.isMacOSNativeCompatible()) {
                Voicechat.LOGGER.warn("Your MacOS version is incompatible with {}", CommonCompatibilityManager.INSTANCE.getModName());
            }
            if (!CLIENT_CONFIG.javaMicrophoneImplementation.get()) {
                CLIENT_CONFIG.javaMicrophoneImplementation.set(true).save();
            }
        }
    }
}
