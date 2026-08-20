package fun.volon.client.modules.settings.implement;

import lombok.Getter;
import lombok.Setter;
import fun.volon.Volon;
import fun.volon.api.storages.implement.ThemeStorage;
import fun.volon.client.modules.settings.Setting;

import java.util.List;
import java.util.function.Supplier;

@Getter
@Setter
public class ThemeSetting extends Setting {

    public ThemeSetting(String name) {
        super(name);
    }

    public List<ThemeStorage.Themes> getThemes() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) {
            return List.of();
        }
        return Volon.INSTANCE.themeStorage.getThemeList();
    }

    public ThemeStorage.Themes getValue() {
        if (Volon.INSTANCE == null || Volon.INSTANCE.themeStorage == null) {
            return null;
        }
        return Volon.INSTANCE.themeStorage.getThemes();
    }

    public void setValue(ThemeStorage.Themes theme) {
        if (Volon.INSTANCE != null && Volon.INSTANCE.themeStorage != null) {
            Volon.INSTANCE.themeStorage.setThemes(theme);
            // Немедленно сохраняем чтобы тема применялась до перезапуска
            try {
                if (Volon.INSTANCE.configStorage != null) {
                    Volon.INSTANCE.configStorage.saveConfig(Volon.INSTANCE.configStorage.currentConfig);
                    Volon.INSTANCE.configStorage.saveGlobals();
                }
            } catch (Exception ignored) {}
        }
    }

    public ThemeSetting visible(Supplier<Boolean> state) {
        this.visible = state;
        return this;
    }
}
