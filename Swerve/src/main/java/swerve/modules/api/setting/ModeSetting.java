package swerve.modules.api.setting;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ModeSetting extends Setting {

    private final List<String> mods;

    private String current;
    private int index;

    public ModeSetting(String name, String current, String... modes) {
        super(name);
        this.mods = Arrays.asList(modes);
        this.index = Math.max(0, this.mods.indexOf(current));
        this.current = this.mods.get(this.index);
    }

    public List<String> getMods() {
        return this.mods;
    }

    public String getCurrent() {
        return this.current;
    }

    public int getIndex() {
        return this.index;
    }

    public void set(String selected) {
        int newIndex = this.mods.indexOf(selected);
        if (newIndex < 0) {
            return;
        }
        this.current = selected;
        this.index = newIndex;
    }

    public boolean is(String mode) {
        return this.current.equals(mode);
    }

    public String displayMode(String mode) {
        return mode;
    }

    public String displayCurrent() {
        return this.current;
    }

    public ModeSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
