package org.privates.client.managers.other.config;

import lombok.extern.log4j.Log4j2;
import org.privates.client.api.client.Constants;
import org.privates.client.api.interfaces.IExecutor;
import org.privates.client.utils.file.FileManager;
import org.privates.client.utils.file.FileType;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Log4j2
public class ConfigManager implements IExecutor {

    public static final File CONFIG_DIRECTORY = new File(FileManager.DIRECTORY, FileType.CONFIG.getName());
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final ConcurrentHashMap<String, ConfigFile> configs = new ConcurrentHashMap<>();

    private volatile float savedPanelWidth = -1;
    private volatile float savedPanelHeight = -1;
    private volatile float savedPanelSpacing = -1;
    private volatile boolean welcomeCompleted = false;
    private volatile String selectedBackground = "background2.png";

    public ConfigManager() {
        init();
    }

    private void init() {
        if (!CONFIG_DIRECTORY.exists() && !CONFIG_DIRECTORY.mkdir()) {
            log.error("Не удалось создать папку {}", FileType.CONFIG.getName());
            throw new RuntimeException("Failed to create config directory");
        }
        update();
    }

    public ConfigFile get(final String config) {
        return configs.computeIfAbsent(config,
                key -> new ConfigFile(new File(CONFIG_DIRECTORY, key + Constants.FILE_FORMAT)));
    }

    public ConfigFile get() {
        return get("default");
    }

    public void set(final String config) {
        executorService.execute(() -> {
            try {
                ConfigFile configFile = get(config);
                if (configFile != null) {
                    configFile.write();
                }
            } catch (Exception e) {
                log.error("Error writing config: {}", config, e);
            }
        });
    }


    public static void save(String configName) {
        org.privates.client.Excellent.inst().configManager().set(configName);
    }

    public void set() {
        set("default");
    }

    public boolean update() {
        configs.clear();
        File[] files = CONFIG_DIRECTORY.listFiles((dir, name) -> name.endsWith(Constants.FILE_FORMAT));
        if (files == null) {
            return false;
        }
        for (File file : files) {
            String configName = file.getName().replace(Constants.FILE_FORMAT, "");
            configs.put(configName, new ConfigFile(file));
        }
        return true;
    }

    public boolean delete(final String config) {
        ConfigFile configFile = configs.remove(config);
        if (configFile == null) {
            return false;
        }
        return configFile.getFile().delete();
    }

    private volatile Map<String, Object> mainMenuSettings = new ConcurrentHashMap<>();

    public void saveMainMenuSettings(Map<String, Object> settings) {
        if (settings != null) {
            this.mainMenuSettings = new ConcurrentHashMap<>(settings);
        }
    }

    public Map<String, Object> getMainMenuSettings() {
        return new HashMap<>(mainMenuSettings);
    }

    public void savePanelDimensions(float width, float height, float spacing) {
        if (width <= 0 || height <= 0 || spacing < 0) {
            throw new IllegalArgumentException("Invalid panel dimensions");
        }
        this.savedPanelWidth = width;
        this.savedPanelHeight = height;
        this.savedPanelSpacing = spacing;
    }

    public float getSavedPanelWidth() {
        return savedPanelWidth;
    }

    public float getSavedPanelHeight() {
        return savedPanelHeight;
    }

    public float getSavedPanelSpacing() {
        return savedPanelSpacing;
    }

    public boolean hasSavedDimensions() {
        return savedPanelWidth > 0 && savedPanelHeight > 0 && savedPanelSpacing >= 0;
    }

    public void markWelcomeCompleted() {
        this.welcomeCompleted = true;
    }

    public boolean hasCompletedWelcome() {
        return welcomeCompleted;
    }

    public void saveSelectedBackground(String background) {
        if (background != null && !background.isEmpty()) {
            this.selectedBackground = background;
        }
    }

    public String getSelectedBackground() {
        return selectedBackground;
    }

    public boolean isEmpty() {
        return configs.isEmpty();
    }

    public int size() {
        return configs.size();
    }

    public Collection<ConfigFile> getConfigs() {
        return configs.values();
    }

    public void forEach(Consumer<ConfigFile> action) {
        if (action != null) {
            configs.values().forEach(action);
        }
    }

    public boolean contains(String configName) {
        return configs.containsKey(configName);
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}