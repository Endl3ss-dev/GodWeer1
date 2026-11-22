package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import tech.lightclient.LightClient;
import tech.lightclient.modules.api.Function;
import tech.lightclient.scripting.runtime.ScriptManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Client info and access to the client's own modules, mirroring the {@code client} object. */
public final class ClientApi {

    private static MinecraftClient mc() {
        return MinecraftClient.getInstance();
    }

    // ---- time ------------------------------------------------------------------------------

    public long tick() {
        return ScriptManager.clientTick();
    }

    public long millis() {
        return System.currentTimeMillis();
    }

    public long nanos() {
        return System.nanoTime();
    }

    public float fps() {
        return mc().getCurrentFps();
    }

    public float tickDelta() {
        return mc().getRenderTickCounter().getTickProgress(false);
    }

    public boolean onClientThread() {
        return mc().isOnThread();
    }

    public boolean inGame() {
        return mc().player != null && mc().world != null;
    }

    public boolean singleplayer() {
        return mc().isInSingleplayer();
    }

    public String version() {
        return mc().getGameVersion();
    }

    /** Script API version implemented by this client. */
    public int apiVersion() {
        return ScriptManager.API_VERSION;
    }

    // ---- client modules --------------------------------------------------------------------

    /** Read/write handle on one of the client's built-in modules. */
    public static final class Module {
        private final Function function;

        Module(Function function) {
            this.function = function;
        }

        public String name() {
            return function.getName();
        }

        public String displayName() {
            return function.getAlias() == null || function.getAlias().isEmpty()
                    ? function.getName() : function.getAlias();
        }

        public boolean enabled() {
            return function.isState();
        }

        public void setEnabled(boolean v) {
            mc().execute(() -> function.setState(v));
        }

        public void toggle() {
            mc().execute(function::toggle);
        }

        public int bind() {
            return function.getBind();
        }

        public List<String> settings() {
            List<String> out = new ArrayList<>();
            for (var s : function.getSettings()) out.add(s.getName());
            return out;
        }

        /**
         * Settings of client modules are deliberately read/write only through their concrete type,
         * and cannot be given onChange/visibleWhen hooks - those belong to a script's own settings.
         */
        public ScriptSettings.Check checkBox(String name) {
            return new ScriptSettings.Check(find(name, tech.lightclient.modules.api.settings.impl.BooleanSetting.class));
        }

        public ScriptSettings.Slide slider(String name) {
            return new ScriptSettings.Slide(find(name, tech.lightclient.modules.api.settings.impl.SliderSetting.class), false);
        }

        public ScriptSettings.Input input(String name) {
            return new ScriptSettings.Input(find(name, tech.lightclient.modules.api.settings.impl.StringSetting.class));
        }

        public ScriptSettings.Select selectable(String name) {
            return new ScriptSettings.Select(find(name, tech.lightclient.modules.api.settings.impl.ModeSetting.class));
        }

        public ScriptSettings.Combo combo(String name) {
            return new ScriptSettings.Combo(find(name, tech.lightclient.modules.api.settings.impl.ModeListSetting.class));
        }

        public ScriptSettings.Color colorPicker(String name) {
            return new ScriptSettings.Color(find(name, tech.lightclient.modules.api.settings.impl.ColorSetting.class));
        }

        private <T> T find(String name, Class<T> type) {
            String needle = normalise(name);
            for (var s : function.getSettings()) {
                if (normalise(s.getName()).equals(needle) || normalise(s.getName()).endsWith(needle)) {
                    if (!type.isInstance(s)) {
                        throw new IllegalArgumentException("setting '" + s.getName() + "' of module "
                                + function.getName() + " is a " + s.getClass().getSimpleName()
                                + ", not a " + type.getSimpleName());
                    }
                    return type.cast(s);
                }
            }
            throw new IllegalArgumentException("module " + function.getName() + " has no setting called '" + name + "'");
        }
    }

    /** Case, space, dot, dash and underscore insensitive matching, as the docs describe. */
    private static String normalise(String s) {
        return s == null ? "" : s.toLowerCase(Locale.US).replaceAll("[ ._-]", "");
    }

    public static final class Modules {
        public List<Module> all() {
            List<Module> out = new ArrayList<>();
            var fm = LightClient.getFunctionManager();
            if (fm == null) return out;
            for (Function f : fm.getFunctions()) out.add(new Module(f));
            return out;
        }

        public Module find(String name) {
            var fm = LightClient.getFunctionManager();
            if (fm == null) return null;
            String needle = normalise(name);
            for (Function f : fm.getFunctions()) {
                if (normalise(f.getName()).equals(needle) || normalise(f.getAlias()).equals(needle)) {
                    return new Module(f);
                }
            }
            return null;
        }

        public boolean exists(String name) {
            return find(name) != null;
        }

        public Module get(String name) {
            Module m = find(name);
            if (m == null) throw new IllegalArgumentException("there is no module called '" + name + "'");
            return m;
        }
    }

    private final Modules modules = new Modules();

    public Modules modules() {
        return modules;
    }
}
