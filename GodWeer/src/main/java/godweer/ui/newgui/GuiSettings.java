package godweer.ui.newgui;

public final class GuiSettings {

    private static final GuiSettings INSTANCE = new GuiSettings();

    private String settingsDisplay = "Под модулем";
    private String panelHeaderStyle = "Слитно";
    private String panelRenderMode = "Новый";
    private String textAlign = "Слева";

    private boolean moduleOutline = false;
    private float outlineWidth = 1.0f;
    private float outlineOpacity = 0.2f;

    private GuiSettings() {
    }

    public static GuiSettings getInstance() {
        return INSTANCE;
    }

    public SettingRef settingsDisplay() {
        return new SettingRef(() -> settingsDisplay, v -> settingsDisplay = v);
    }

    public SettingRef panelHeaderStyle() {
        return new SettingRef(() -> panelHeaderStyle, v -> panelHeaderStyle = v);
    }

    public SettingRef panelRenderMode() {
        return new SettingRef(() -> panelRenderMode, v -> panelRenderMode = v);
    }

    public SettingRef textAlign() {
        return new SettingRef(() -> textAlign, v -> textAlign = v);
    }

    public SettingRef moduleOutline() {
        return new SettingRef(() -> moduleOutline ? "true" : "false", v -> moduleOutline = "true".equals(v));
    }

    public SettingRef outlineWidth() {
        return new SettingRef(() -> String.valueOf(outlineWidth), v -> {
            try { outlineWidth = Float.parseFloat(v); } catch (NumberFormatException ignored) {}
        });
    }

    public SettingRef outlineOpacity() {
        return new SettingRef(() -> String.valueOf(outlineOpacity), v -> {
            try { outlineOpacity = Float.parseFloat(v); } catch (NumberFormatException ignored) {}
        });
    }

    public static final class SettingRef {

        private final java.util.function.Supplier<String> getter;
        private final java.util.function.Consumer<String> setter;

        SettingRef(java.util.function.Supplier<String> getter, java.util.function.Consumer<String> setter) {
            this.getter = getter;
            this.setter = setter;
        }

        public String getValue() {
            return getter.get();
        }

        public boolean is(String value) {
            return value.equals(getter.get());
        }

        public void set(String value) {
            setter.accept(value);
        }

        public boolean getValueBool() {
            return "true".equals(getter.get());
        }

        public float getValueFloat() {
            try {
                return Float.parseFloat(getter.get());
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        }
    }
}
