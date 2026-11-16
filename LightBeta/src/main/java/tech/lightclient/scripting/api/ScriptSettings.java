package tech.lightclient.scripting.api;

import tech.lightclient.modules.api.settings.api.Setting;
import tech.lightclient.modules.api.settings.impl.BindSetting;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ButtonSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeListSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.RangeSliderSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import tech.lightclient.modules.api.settings.impl.StringSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Script-facing wrappers around the client's own {@link Setting} types.
 *
 * <p>Backing the scripting API with the real setting classes rather than a parallel model means
 * script settings are drawn by the very same components as built-in modules, and are saved and
 * restored by the existing config code with no extra work.</p>
 */
public final class ScriptSettings {

    /** Fixed set of slider units, mirroring {@code Postfixes} in the docs. */
    public static final class Postfixes {
        public static final String PERCENT = "%";
        public static final String DEGREES = "\u00B0";
        public static final String SECONDS = "s";
        public static final String TICKS = "t";
        public static final String BLOCKS = "b";
        public static final String MS = "ms";
        public static final String MULTIPLIER = "x";
        public static final String HEALTH = "hp";

        private Postfixes() {}
    }

    /** Common behaviour: visibility, change notification, nesting. */
    public abstract static class Base<S extends Setting<?>> {
        protected final S setting;
        final List<Consumer<Base<S>>> listeners = new ArrayList<>();
        BooleanSetting parent;

        Base(S setting) {
            this.setting = setting;
        }

        public S raw() {
            return setting;
        }

        public String name() {
            return setting.getName();
        }

        /** Name of the checkbox this setting is nested under, or null when top level. */
        public String parentName() {
            return parent == null ? null : parent.getName();
        }

        /** Hides the setting unless the supplied condition holds. */
        @SuppressWarnings("unchecked")
        public Base<S> visibleWhen(Supplier<Boolean> condition) {
            Supplier<Boolean> existing = setting.visible;
            setting.setVisible(() -> {
                try {
                    return existing.get() && Boolean.TRUE.equals(condition.get());
                } catch (Throwable t) {
                    return true;
                }
            });
            return this;
        }

        /** Nests this setting under a checkbox, so it is drawn (and hidden) with it. */
        public void nestUnder(BooleanSetting parent) {
            if (parent == null) return;
            this.parent = parent;
            Supplier<Boolean> existing = setting.visible;
            setting.setVisible(() -> {
                try {
                    return parent.get() && existing.get();
                } catch (Throwable t) {
                    return true;
                }
            });
        }

        protected void fireChange() {
            for (Consumer<Base<S>> l : new ArrayList<>(listeners)) {
                try {
                    l.accept(this);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /** {@code checkBox("Name", true)} */
    public static final class Check extends Base<BooleanSetting> {
        Check(BooleanSetting s) { super(s); }

        public boolean value() { return setting.get(); }

        public void value(boolean v) {
            if (setting.get() == v) return;
            setting.set(v);
            fireChange();
        }

        public void toggle() { value(!value()); }

        public Check onChange(Consumer<Boolean> c) {
            listeners.add(b -> c.accept(value()));
            return this;
        }
    }

    /** {@code slider("Name", 5f, 1f, 20f, step)} */
    public static final class Slide extends Base<SliderSetting> {
        private final boolean integer;

        Slide(SliderSetting s, boolean integer) {
            super(s);
            this.integer = integer;
        }

        public float value() { return setting.get(); }

        public int intValue() { return Math.round(setting.get()); }

        public void value(float v) {
            float clamped = Math.max(setting.min, Math.min(setting.max, v));
            if (Math.abs(setting.get() - clamped) < 1e-6f) return;
            setting.set(clamped);
            fireChange();
        }

        public float min() { return setting.min; }

        public float max() { return setting.max; }

        public boolean isInteger() { return integer; }

        /** Appends a unit to the drawn number; returns the slider so it can be chained before {@code by}. */
        public Slide postfix(String unit) {
            setting.setPostfix(unit);
            return this;
        }

        public Slide onChange(Consumer<Float> c) {
            listeners.add(b -> c.accept(value()));
            return this;
        }
    }

    /** {@code rangeSlider("Name", 2f, 8f, 0f, 20f)} */
    public static final class Range extends Base<RangeSliderSetting> {
        Range(RangeSliderSetting s) { super(s); }

        public float from() { return setting.from(); }

        public float to() { return setting.to(); }

        public void value(float from, float to) {
            setting.setRange(from, to);
            fireChange();
        }

        public float min() { return setting.min; }

        public float max() { return setting.max; }

        public Range postfix(String unit) {
            // Range sliders draw bare numbers; kept for API symmetry with slider().
            return this;
        }

        public Range onChange(java.util.function.BiConsumer<Float, Float> c) {
            listeners.add(b -> c.accept(from(), to()));
            return this;
        }
    }

    /** {@code input("Name", "text")} */
    public static final class Input extends Base<StringSetting> {
        Input(StringSetting s) { super(s); }

        public String value() { return setting.get(); }

        public void value(String v) {
            String s = v == null ? "" : v;
            if (s.equals(setting.get())) return;
            setting.set(s);
            fireChange();
        }

        public Input onChange(Consumer<String> c) {
            listeners.add(b -> c.accept(value()));
            return this;
        }
    }

    /** {@code selectable("Name", "A", "B", selected = "A")} */
    public static final class Select extends Base<ModeSetting> {
        Select(ModeSetting s) { super(s); }

        public String value() { return setting.get(); }

        public void select(String option) {
            if (option == null) return;
            for (String o : setting.strings) {
                if (o.equalsIgnoreCase(option)) {
                    if (o.equals(setting.get())) return;
                    setting.set(o);
                    fireChange();
                    return;
                }
            }
        }

        public List<String> options() { return List.of(setting.strings); }

        public boolean is(String option) { return setting.is(option); }

        public Select onChange(Consumer<String> c) {
            listeners.add(b -> c.accept(value()));
            return this;
        }
    }

    /** {@code combo("Name", "A", "B", selected = listOf("A"))} */
    public static final class Combo extends Base<ModeListSetting> {
        Combo(ModeListSetting s) { super(s); }

        public List<String> value() {
            List<String> out = new ArrayList<>();
            for (BooleanSetting b : setting.get()) {
                if (b.get()) out.add(b.getName());
            }
            return out;
        }

        public List<String> options() {
            List<String> out = new ArrayList<>();
            for (BooleanSetting b : setting.get()) out.add(b.getName());
            return out;
        }

        public boolean has(String option) {
            BooleanSetting b = setting.getByName(option);
            return b != null && b.get();
        }

        public void set(String option, boolean on) {
            BooleanSetting b = setting.getByName(option);
            if (b == null || b.get() == on) return;
            b.set(on);
            fireChange();
        }

        public void value(List<String> selected) {
            List<String> want = selected == null ? List.of() : selected;
            boolean changed = false;
            for (BooleanSetting b : setting.get()) {
                boolean on = want.stream().anyMatch(s -> s.equalsIgnoreCase(b.getName()));
                if (b.get() != on) {
                    b.set(on);
                    changed = true;
                }
            }
            if (changed) fireChange();
        }

        public Combo onChange(Consumer<List<String>> c) {
            listeners.add(b -> c.accept(value()));
            return this;
        }
    }

    /** {@code colorPicker("Name", argb)} */
    public static final class Color extends Base<ColorSetting> {
        Color(ColorSetting s) { super(s); }

        public int value() { return setting.get(); }

        public void value(int argb) {
            if (setting.get() == argb) return;
            setting.set(argb);
            setting.updateHSB();
            fireChange();
        }

        public Color onChange(Consumer<Integer> c) {
            listeners.add(b -> c.accept(value()));
            return this;
        }
    }

    /** {@code hotkey("Name", Key.G) { ... }} */
    public static final class Hotkey extends Base<BindSetting> {
        final Runnable action;

        Hotkey(BindSetting s, Runnable action) {
            super(s);
            this.action = action;
        }

        public int value() { return setting.get(); }

        public void value(int key) {
            setting.set(key);
            fireChange();
        }

        void press() {
            if (action != null) action.run();
        }

        /** Fires the bound action; called by the manager when the key is pressed. */
        public void trigger() {
            press();
        }
    }

    /** {@code button("Name") { ... }} */
    public static final class Button extends Base<ButtonSetting> {
        Button(ButtonSetting s) { super(s); }

        public void press() { setting.press(); }
    }

    // ---- factories -------------------------------------------------------------------------

    public static Check checkBox(String name, boolean def) {
        return new Check(new BooleanSetting(name, def));
    }

    public static Slide slider(String name, float def, float min, float max, float step) {
        return new Slide(new SliderSetting(name, def, min, max, step), false);
    }

    public static Slide sliderInt(String name, int def, int min, int max) {
        return new Slide(new SliderSetting(name, def, min, max, 1f), true);
    }

    public static Range rangeSlider(String name, float from, float to, float min, float max, float step) {
        return new Range(new RangeSliderSetting(name, from, to, min, max, step));
    }

    public static Input input(String name, String def) {
        return new Input(new StringSetting(name, def == null ? "" : def));
    }

    public static Select selectable(String name, List<String> options, String selected) {
        String[] arr = options == null ? new String[0] : options.toArray(new String[0]);
        String def = selected != null ? selected : (arr.length > 0 ? arr[0] : "");
        return new Select(new ModeSetting(name, def, arr));
    }

    public static Combo combo(String name, List<String> options, List<String> selected) {
        List<BooleanSetting> opts = new ArrayList<>();
        if (options != null) {
            for (String o : options) {
                boolean on = selected != null && selected.stream().anyMatch(s -> s.equalsIgnoreCase(o));
                opts.add(new BooleanSetting(o, on));
            }
        }
        return new Combo(new ModeListSetting(name, opts.toArray(new BooleanSetting[0])));
    }

    public static Color colorPicker(String name, int argb) {
        return new Color(new ColorSetting(name, argb));
    }

    public static Hotkey hotkey(String name, int key, Runnable action) {
        return new Hotkey(new BindSetting(name, key), action);
    }

    public static Button button(String name, Runnable action) {
        return new Button(new ButtonSetting(name, action));
    }

    private ScriptSettings() {}
}
