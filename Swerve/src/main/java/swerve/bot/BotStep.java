package swerve.bot;

/**
 * Один шаг поведения: тип, значения полей и флаг «включён».
 *
 * <p>Выключенный шаг остаётся в списке, но {@link BotRunner} его пропускает —
 * так удобно отлаживать бота, не удаляя заготовки.
 */
public final class BotStep {

    private final BotStepType type;
    private final String[] values;
    private boolean enabled = true;

    public BotStep(BotStepType type) {
        this.type = type;
        int count = type == null ? 0 : type.paramCount();
        this.values = new String[count];
        for (int i = 0; i < count; i++) {
            BotStepType.Param param = type.param(i);
            this.values[i] = param == null ? "" : param.defaultValue();
        }
    }

    public static BotStep create(BotStepType type) {
        return new BotStep(type);
    }

    public BotStepType getType() {
        return this.type;
    }

    public int valueCount() {
        return this.values.length;
    }

    public String get(int index) {
        return index >= 0 && index < this.values.length ? this.values[index] : "";
    }

    public void set(int index, String value) {
        if (index >= 0 && index < this.values.length) {
            this.values[index] = value == null ? "" : value;
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void toggle() {
        this.enabled = !this.enabled;
    }

    public double number(int index) {
        String text = get(index).trim().replace(',', '.');
        if (text.isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    public int integer(int index) {
        return (int) Math.round(number(index));
    }

    public String describe() {
        return this.type == null ? "?" : this.type.describe(this.values);
    }

    @Override
    public String toString() {
        return (this.type == null ? "?" : this.type.name()) + " " + describe();
    }
}
