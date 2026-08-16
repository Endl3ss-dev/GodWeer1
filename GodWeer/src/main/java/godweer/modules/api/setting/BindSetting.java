package godweer.modules.api.setting;

import java.util.function.Supplier;

public class BindSetting extends Setting {

    private int key;

    public BindSetting(String name, int keyDefault) {
        super(name);
        this.key = keyDefault;
    }

    public int getKey() {
        return this.key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public BindSetting visible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
