package swerve.modules.api.setting;

import java.util.List;
import java.util.function.Supplier;

public class ListSetting extends Setting {

    private final List<BooleanSetting> settings;

    public ListSetting(String name, BooleanSetting... settings) {
        super(name);
        this.settings = List.of(settings);
    }

    public static ListSetting of(String name, BooleanSetting... settings) {
        return new ListSetting(name, settings);
    }

    public List<BooleanSetting> getSettings() {
        return this.settings;
    }

    public boolean is(String name) {
        return requireSetting(name).isState();
    }

    public void set(String name, boolean value) {
        requireSetting(name).setState(value);
    }

    public ListSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }

    private BooleanSetting requireSetting(String name) {
        for (int i = 0, size = this.settings.size(); i < size; i++) {
            BooleanSetting option = this.settings.get(i);
            if (option.name().equalsIgnoreCase(name)) {
                return option;
            }
        }
        throw new NullPointerException("Unknown list setting entry: " + name);
    }
}
