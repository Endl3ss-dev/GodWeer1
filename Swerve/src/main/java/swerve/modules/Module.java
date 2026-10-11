package swerve.modules;

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
            onDisable();
        }

        // Единственная точка тоглов: сюда приходят и бинд, и клик в ClickGui.
        if (lastState != this.enable) {
            NotificationManager.toggle(this, this.enable);
        }
    }

    // Модуль, который сам показывает уведомления, может не сообщать о своём выключении.
    public boolean notifiesOnToggle() {
        return true;
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
