package godweer.modules.api.setting;

import java.awt.Color;
import java.util.function.Supplier;

public abstract class Setting {

    private final String name;

    protected Supplier<Boolean> visible = () -> Boolean.TRUE;
    protected Color color = Color.WHITE;

    public Setting(String name) {
        this.name = name;
    }

    public String name() {
        return this.name;
    }

    public String displayName() {
        return this.name;
    }

    public boolean visible() {
        Boolean state = this.visible.get();
        return state == null || state;
    }

    public Supplier<Boolean> visibleSupplier() {
        return this.visible;
    }

    public Color color() {
        return this.color;
    }

    public Setting color(Color color) {
        this.color = color;
        return this;
    }
}
