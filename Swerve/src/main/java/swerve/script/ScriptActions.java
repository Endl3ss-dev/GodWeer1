package swerve.script;

import swerve.config.ConfigManager;
import swerve.managers.ModuleManager;
import swerve.modules.Module;
import swerve.modules.api.setting.BindSetting;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.modules.api.setting.Setting;
import swerve.modules.api.setting.TextSetting;
import swerve.utils.client.ClientSoundPlayer;
import swerve.utils.client.NotificationManager;
import swerve.utils.input.KeyBoardUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Действия блоков и вычисление условий.
 *
 * <p>Здесь же собраны мосты в клиент: переключение модулей, запись значения
 * настройки, уведомления, чат и команды. Всё, что может упасть из-за состояния
 * игры (нет мира, нет соединения), проверяется до вызова — интерпретатор не
 * должен ловить исключения на каждом блоке.
 */
public final class ScriptActions {

    private ScriptActions() {
    }

    // ---- действия

    public static void apply(ScriptBlock block) {
        if (block == null || block.getType() == null) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();

        switch (block.getType()) {
            case TOGGLE_MODULE -> toggleModule(text(block, 0));
            case ENABLE_MODULE -> setModule(text(block, 0), true);
            case DISABLE_MODULE -> setModule(text(block, 0), false);
            case SET_SETTING -> setSetting(text(block, 0), text(block, 1), text(block, 2));
            case NOTIFY -> NotificationManager.info("Скрипт", text(block, 0));
            case CHAT -> {
                if (mc.gui != null) {
                    mc.gui.getChat().addMessage(Component.literal("§d[Скрипт] §f" + text(block, 0)));
                }
            }
            case COMMAND -> runCommand(mc, text(block, 0));
            case SOUND -> ClientSoundPlayer.playSound(text(block, 0), 0.7, 1.0f);
            case SET_VAR -> setVariable(text(block, 0), text(block, 1));
            case CHANGE_VAR -> changeVariable(text(block, 0), number(block, 1));
            default -> {
                // Блоки управления выполняет ScriptEngine, сюда они не попадают.
            }
        }
    }

    private static void toggleModule(String name) {
        Module module = ModuleManager.getByName(name);
        if (module != null) {
            module.toggle();
        }
    }

    private static void setModule(String name, boolean enabled) {
        Module module = ModuleManager.getByName(name);
        if (module == null || module.isEnable() == enabled) {
            return;
        }
        module.setEnabled(enabled);
    }

    private static void setSetting(String moduleName, String settingName, String value) {
        Module module = ModuleManager.getByName(moduleName);
        if (module == null) {
            return;
        }

        List<Setting> settings = module.getSettings();
        for (int i = 0, size = settings.size(); i < size; i++) {
            Setting setting = settings.get(i);
            if (!matches(setting, settingName)) {
                continue;
            }

            if (setting instanceof BooleanSetting bool) {
                bool.setState(parseBoolean(value));
            } else if (setting instanceof FloatSetting number) {
                number.setValue((float) ScriptText.number(value));
            } else if (setting instanceof ModeSetting mode) {
                mode.set(value.trim());
            } else if (setting instanceof TextSetting text) {
                text.setText(value);
            } else if (setting instanceof BindSetting bind) {
                bind.setKey(parseKey(value));
            } else {
                continue;
            }

            ConfigManager.markDirty();
            return;
        }
    }

    private static boolean matches(Setting setting, String name) {
        String wanted = name == null ? "" : name.trim();
        return !wanted.isEmpty()
                && (setting.name().equalsIgnoreCase(wanted) || setting.displayName().equalsIgnoreCase(wanted));
    }

    private static void runCommand(Minecraft mc, String raw) {
        String command = raw == null ? "" : raw.trim();
        while (command.startsWith("/")) {
            command = command.substring(1);
        }
        if (command.isEmpty() || mc.player == null || mc.player.connection == null) {
            return;
        }
        mc.player.connection.sendCommand(command);
    }

    private static void setVariable(String name, String value) {
        String key = variableName(name);
        if (key.isEmpty()) {
            return;
        }
        ScriptStore.getVariables().put(key, value);
        ScriptStore.markDirty();
    }

    private static void changeVariable(String name, double delta) {
        String key = variableName(name);
        if (key.isEmpty()) {
            return;
        }
        double current = ScriptText.number(ScriptStore.getVariables().getOrDefault(key, "0"));
        ScriptStore.getVariables().put(key, ScriptText.number(current + delta));
        ScriptStore.markDirty();
    }

    private static String variableName(String raw) {
        return raw == null ? "" : raw.trim();
    }

    // ---- условия

    public static boolean condition(ScriptBlock block) {
        if (block == null || block.getType() == null) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();

        return switch (block.getType()) {
            case IF_MODULE -> isModuleEnabled(text(block, 0));
            case IF_MODULE_OFF -> !isModuleEnabled(text(block, 0));
            case IF_HEALTH -> mc.player != null && mc.player.getHealth() < number(block, 0);
            case IF_FOOD -> mc.player != null && mc.player.getFoodData().getFoodLevel() < number(block, 0);
            case IF_KEY -> KeyBoardUtils.isBindHeld(key(block, 0));
            case IF_VAR -> compare(text(block, 0), text(block, 1), text(block, 2));
            case IF_WORLD -> mc.level != null && mc.player != null;
            case IF_CHANCE -> Math.random() * 100.0 < number(block, 0);
            default -> false;
        };
    }

    private static boolean isModuleEnabled(String name) {
        Module module = ModuleManager.getByName(name);
        return module != null && module.isEnable();
    }

    private static boolean compare(String variable, String operator, String rawValue) {
        String key = variableName(variable);
        double left = ScriptText.number(ScriptStore.getVariables().getOrDefault(key, "0"));
        double right = ScriptText.number(rawValue);
        double epsilon = 1.0E-6;

        return switch (operator.trim()) {
            case "=" -> Math.abs(left - right) < epsilon;
            case "≠", "!=" -> Math.abs(left - right) >= epsilon;
            case "<" -> left < right;
            case "≤", "<=" -> left <= right;
            case ">" -> left > right;
            case "≥", ">=" -> left >= right;
            default -> false;
        };
    }

    // ---- чтение полей

    public static String text(ScriptBlock block, int slot) {
        return ScriptText.fill(block.get(slot));
    }

    public static double number(ScriptBlock block, int slot) {
        return ScriptText.number(block.get(slot));
    }

    public static int key(ScriptBlock block, int slot) {
        return parseKey(block.get(slot));
    }

    public static int parseKey(String raw) {
        String text = raw == null ? "" : raw.trim();
        if (text.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    public static boolean parseBoolean(String raw) {
        String text = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        return text.equals("да") || text.equals("true") || text.equals("1") || text.equals("вкл");
    }

    // ---- данные для палитры и выпадающих списков

    public static List<String> moduleNames() {
        List<Module> modules = ModuleManager.getModules();
        List<String> names = new ArrayList<>(modules.size());
        for (int i = 0, size = modules.size(); i < size; i++) {
            names.add(modules.get(i).getName());
        }
        return names;
    }

    public static List<String> settingNames(String moduleName) {
        Module module = ModuleManager.getByName(moduleName);
        if (module == null) {
            return List.of();
        }
        List<Setting> settings = module.getSettings();
        List<String> names = new ArrayList<>(settings.size());
        for (int i = 0, size = settings.size(); i < size; i++) {
            Setting setting = settings.get(i);
            if (setting instanceof BooleanSetting || setting instanceof FloatSetting
                    || setting instanceof ModeSetting || setting instanceof TextSetting
                    || setting instanceof BindSetting) {
                names.add(setting.name());
            }
        }
        return names;
    }

    public static List<String> functionNames() {
        List<ScriptFunction> functions = ScriptStore.getFunctions();
        List<String> names = new ArrayList<>(functions.size());
        for (int i = 0, size = functions.size(); i < size; i++) {
            names.add(functions.get(i).getName());
        }
        return names;
    }
}
