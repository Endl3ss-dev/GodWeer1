package swerve.modules;

import swerve.Swerve;
import swerve.config.ConfigManager;
import swerve.modules.api.setting.Setting;
import swerve.utils.client.NotificationManager;

import java.util.ArrayList;
import java.util.List;

public abstract class Module {

    private final List<Setting> settings = new ArrayList<>();

    private String name;
    private String description;
    private ModuleCategory category;
    private int key = -1;
    private boolean open;
    private boolean enable;

    public Module(String name, String description, ModuleCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public Module(String name, ModuleCategory category) {
        this(name, "NULLABLE", category);
    }

    public void onEnable() {
        this.enable = true;
    }

    public void onDisable() {
        this.enable = false;
    }

    public void onTick() {
    }

    public void toggle() {
        setEnabled(!this.enable);
    }

    public void setEnabled(boolean state) {
        applyEnabled(state, true);
    }

    /**
     * [+] Восстановление состояния из конфига: без уведомления в чат.
     *
     * <p>Иначе каждый запуск клиента начинался бы с пачки сообщений о включении
     * модулей, которые пользователь включил в прошлой сессии.
     */
    public void setEnabledQuiet(boolean state) {
        applyEnabled(state, false);
    }

    private void applyEnabled(boolean state, boolean notify) {
        boolean lastState = this.enable;
        this.enable = state;

        try {
            if (state) {
                onEnable();
            } else if (lastState) {
                onDisable();
            }
        } catch (Exception e) {
            this.enable = false;
            try {
                onDisable();
            } catch (Exception disableFailure) {
                // [!] Состояние уже сброшено: падение onDisable не должно уводить исключение наружу,
                // иначе один сломанный модуль отменит восстановление остальных из конфига.
                Swerve.LOGGER.warn("Module '{}' failed to disable", this.name, disableFailure);
            }
        }

        if (lastState == this.enable) {
            return;
        }

        // Единственная точка тоглов: сюда приходят и бинд, и клик в ClickGui.
        if (notify) {
            NotificationManager.toggle(this, this.enable);
            // [+] Состояние поменял пользователь — конфиг нужно переписать.
            ConfigManager.markDirty();
        }
    }

    // Модуль, который сам показывает уведомления, может не сообщать о своём выключении.
    public boolean notifiesOnToggle() {
        return true;
    }

    /**
     * [+] Настройки модуля живут в его собственном файле, а не в общем конфиге.
     *
     * <p>Так устроен ViewModel: раскладка рук и её настройки пишутся асинхронно в
     * {@code swerve/viewmodel.json} и читаются в {@code onEnable}. Дублировать их в
     * {@code config.json} нельзя — при включении модуль всё равно прочитает свой файл
     * и перезапишет значения, получится два источника правды. Общий конфиг в этом
     * случае хранит только включённость и бинд.
     */
    public boolean usesOwnConfig() {
        return false;
    }

    public void addSettings(Setting... settings) {
        if (settings == null) {
            return;
        }
        for (Setting setting : settings) {
            if (setting != null) {
                this.settings.add(setting);
            }
        }
    }

    public List<Setting> getSettings() {
        return this.settings;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return this.name;
    }

    public String getDisplayDescription() {
        return this.description;
    }

    public ModuleCategory getCategory() {
        return this.category;
    }

    public void setCategory(ModuleCategory category) {
        this.category = category;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
