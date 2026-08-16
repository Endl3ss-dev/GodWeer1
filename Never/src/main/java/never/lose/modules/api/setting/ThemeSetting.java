package never.lose.modules.api.setting;

import never.lose.managers.Theme;

import java.util.Arrays;
import java.util.function.Supplier;

public class ThemeSetting extends Setting {

    private final Theme[] themes;
    private Theme current;

    public ThemeSetting(Theme defaultValue, Theme... themes) {
        super("Theme");
        setValue(defaultValue);
        this.themes = themes;
    }

    public void setValue(Theme theme) {
        if (current == null) {
            current = theme;
            return;
        }
        if (current != theme) {
            theme.startAnimation(current.color1, current.color2);
            current = theme;
            notifyChanged();
        }
    }

    public Theme getValue() {
        return current;
    }

    public Theme[] getThemes() {
        return themes;
    }

    @Override
    public String getValueAsString() {
        return getValue().name;
    }

    @Override
    public void setValueFromString(String value) {
        Arrays.stream(themes).filter(theme -> theme.name.equals(value)).forEach(this::setValue);
    }

    @Override
    public ThemeSetting setVisible(Supplier<Boolean> visible) {
        this.visible = visible;
        return this;
    }
}
