package godweer.modules.api.setting;

import java.util.function.Supplier;

public class BooleanSetting extends Setting {

    private boolean state;

    public BooleanSetting(String name, boolean state) {
        super(name);
        this.state = state;
    }

    public static BooleanSetting of(String name, boolean state) {
        return new BooleanSetting(name, state);
    }

    public boolean isState() {
        return this.state;
    }

    public boolean get() {
        return this.state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public BooleanSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
