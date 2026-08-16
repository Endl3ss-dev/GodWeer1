package never.lose.modules.api.setting;

import never.lose.managers.CfgManager;

import java.util.function.Supplier;

public abstract class Setting implements ISetting {

    private final String name;
    public Supplier<Boolean> visible = () -> true;

    public Setting(String name) {
        this.name = name;
    }

    protected void notifyChanged() {
        CfgManager.markDirty();
    }

    public String getName() {
        return name;
    }

    public abstract String getValueAsString();

    public abstract void setValueFromString(String value);
}
