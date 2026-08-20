package org.privates.client.managers.other.config;

import com.google.gson.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.privates.client.Excellent;
import org.privates.client.api.client.Constants;
import org.privates.client.managers.component.impl.drag.DragComponent;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.impl.render.Hud;
import org.privates.client.managers.module.settings.Setting;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.screen.hud.IConfigurableHud;
import org.privates.client.utils.file.AbstractFile;
import org.privates.client.utils.file.FileType;
import org.privates.client.utils.render.draw.RenderUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFile extends AbstractFile {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .create();

    public ConfigFile(final File file) {
        super(file, FileType.CONFIG);
    }

    @Override
    public boolean read() {
        if (!getFile().exists()) {
            return false;
        }

        try {
            String content = Files.readString(getFile().toPath(), StandardCharsets.UTF_8);
            if (content == null || content.trim().isEmpty()) {
                return false;
            }

            JsonObject root = new JsonParser().parse(content).getAsJsonObject();

            if (root.has("welcome")) {
                JsonObject welcome = root.getAsJsonObject("welcome");
                if (welcome.has("completed")) {
                    boolean completed = welcome.get("completed").getAsBoolean();
                    if (completed) {
                        Excellent.inst().configManager().markWelcomeCompleted();
                    }
                }
                if (welcome.has("selectedBackground")) {
                    String bg = welcome.get("selectedBackground").getAsString();
                    Excellent.inst().configManager().saveSelectedBackground(bg);
                    RenderUtil.setBackground(bg);
                }
            }

            if (root.has("module")) {
                loadModules(root.getAsJsonObject("module"));
            }

            if (root.has("gui")) {
                loadGui(root.getAsJsonObject("gui"));
            }

            if (root.has("hudSettings")) {
                loadHudSettings(root.getAsJsonObject("hudSettings"));
            }

            if (root.has("mainMenu")) {
                loadMainMenu(root.getAsJsonObject("mainMenu"));
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean write() {
        try {
            File parent = getFile().getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            if (!getFile().exists()) {
                getFile().createNewFile();
            }

            JsonObject root = new JsonObject();

            JsonObject info = new JsonObject();
            info.addProperty("build", Constants.VERSION);
            root.add(Constants.NAME, info);

            JsonObject modules = new JsonObject();
            saveModules(modules);
            root.add("module", modules);

            JsonObject gui = new JsonObject();
            saveGui(gui);
            root.add("gui", gui);

            JsonObject hud = new JsonObject();
            saveHudSettings(hud);
            root.add("hudSettings", hud);

            JsonObject menu = new JsonObject();
            saveMainMenu(menu);
            root.add("mainMenu", menu);

            JsonObject welcome = new JsonObject();
            welcome.addProperty("completed", Excellent.inst().configManager().hasCompletedWelcome());
            welcome.addProperty("selectedBackground", Excellent.inst().configManager().getSelectedBackground());
            root.add("welcome", welcome);

            try (FileWriter fw = new FileWriter(getFile(), StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(GSON.toJson(root));
                bw.flush();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadModules(JsonObject obj) {
        for (Module module : Excellent.inst().moduleManager().values()) {
            try {
                String key = sanitize(module.getName());
                if (!obj.has(key)) continue;

                JsonObject moduleObj = obj.getAsJsonObject(key);

                if (moduleObj.has("key")) {
                    module.setKey(moduleObj.get("key").getAsInt());
                }

                if (moduleObj.has("state")) {
                    module.setEnabled(moduleObj.get("state").getAsBoolean(), false);
                }

                for (Setting<?> setting : module.getSettings()) {
                    loadSetting(moduleObj, setting);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveModules(JsonObject obj) {
        for (Module module : Excellent.inst().moduleManager().values()) {
            try {
                JsonObject moduleObj = new JsonObject();
                moduleObj.addProperty("key", module.getKey());
                moduleObj.addProperty("state", module.isEnabled());

                for (Setting<?> setting : module.getSettings()) {
                    saveSetting(moduleObj, setting);
                }

                obj.add(sanitize(module.getName()), moduleObj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadSetting(JsonObject obj, Setting<?> setting) {
        try {
            String key = sanitize(setting.getName());
            if (!obj.has(key)) return;

            JsonElement elem = obj.get(key);

            if (setting instanceof BooleanSetting s) {
                s.set(elem.getAsBoolean());
            } else if (setting instanceof ColorSetting s) {
                s.set(elem.getAsInt());
            } else if (setting instanceof DragSetting s) {
                JsonObject drag = elem.getAsJsonObject();
                if (drag.has("x") && drag.has("y") && drag.has("width") && drag.has("height")) {
                    s.position.set(drag.get("x").getAsFloat(), drag.get("y").getAsFloat());
                    s.targetPosition.set(drag.get("x").getAsFloat(), drag.get("y").getAsFloat());
                    s.size.set(drag.get("width").getAsFloat(), drag.get("height").getAsFloat());
                }
            } else if (setting instanceof ListSetting<?> s) {
                String val = elem.getAsString();
                for (Object mode : s.values) {
                    if (mode.toString().equals(val)) {
                        s.setAsObject(mode);
                        break;
                    }
                }
            } else if (setting instanceof ModeSetting s) {
                s.set(elem.getAsString());
            } else if (setting instanceof MultiBooleanSetting s) {
                JsonObject multi = elem.getAsJsonObject();
                for (BooleanSetting bool : s.getValues()) {
                    String boolKey = sanitize(bool.getName());
                    if (multi.has(boolKey)) {
                        bool.set(multi.get(boolKey).getAsBoolean());
                    }
                }
            } else if (setting instanceof SliderSetting s) {
                s.set(elem.getAsFloat());
            } else if (setting instanceof SliderSettingRand s) {
                JsonObject range = elem.getAsJsonObject();
                if (range.has("min") && range.has("max")) {
                    float minVal = range.get("min").getAsFloat();
                    float maxVal = range.get("max").getAsFloat();
                    s.set(new SliderSettingRand.RangeValue(minVal, maxVal));
                }
            } else if (setting instanceof BindSetting s) {
                s.setConfigValue(elem.getAsInt());
            } else if (setting instanceof StringSetting s) {
                s.set(elem.getAsString());
            } else if (setting instanceof ItemSetting s) {
                String itemName = elem.getAsString();
                if (itemName != null && !itemName.isEmpty()) {
                    ResourceLocation loc = new ResourceLocation(itemName);
                    Item item = Registry.ITEM.getOrDefault(loc);
                    if (!item.equals(Items.AIR)) {
                        s.set(item);
                    }
                }
            } else if (setting instanceof ThemeSetting s) {
                String preset = elem.getAsString();
                for (ThemeSetting.ThemePreset p : s.getPresets()) {
                    if (p.getName().equals(preset)) {
                        s.set(p);
                        break;
                    }
                }
            } else if (setting instanceof MultiItemSetting s) {
                if (elem.isJsonArray()) {
                    JsonArray arr = elem.getAsJsonArray();
                    List<Item> items = new ArrayList<>();
                    for (JsonElement e : arr) {
                        String itemName = e.getAsString();
                        if (itemName != null && !itemName.isEmpty()) {
                            ResourceLocation loc = new ResourceLocation(itemName);
                            Item item = Registry.ITEM.getOrDefault(loc);
                            if (!item.equals(Items.AIR)) {
                                items.add(item);
                            }
                        }
                    }
                    s.set(items);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSetting(JsonObject obj, Setting<?> setting) {
        try {
            String key = sanitize(setting.getName());

            if (setting instanceof BooleanSetting s) {
                obj.addProperty(key, s.getValue());
            } else if (setting instanceof ColorSetting s) {
                obj.addProperty(key, s.getValue());
            } else if (setting instanceof DragSetting s) {
                JsonObject drag = new JsonObject();
                drag.addProperty("x", s.position.x);
                drag.addProperty("y", s.position.y);
                drag.addProperty("width", s.size.x);
                drag.addProperty("height", s.size.y);
                obj.add(key, drag);
            } else if (setting instanceof ListSetting<?> s) {
                obj.addProperty(key, s.getValue().toString());
            } else if (setting instanceof ModeSetting s) {
                obj.addProperty(key, s.getValue());
            } else if (setting instanceof MultiBooleanSetting s) {
                JsonObject multi = new JsonObject();
                for (BooleanSetting bool : s.getValues()) {
                    multi.addProperty(sanitize(bool.getName()), bool.getValue());
                }
                obj.add(key, multi);
            } else if (setting instanceof SliderSetting s) {
                obj.addProperty(key, s.getValue());
            } else if (setting instanceof SliderSettingRand s) {
                JsonObject range = new JsonObject();
                SliderSettingRand.RangeValue value = s.getValue();
                range.addProperty("min", value.getMinValue());
                range.addProperty("max", value.getMaxValue());
                obj.add(key, range);
            } else if (setting instanceof BindSetting s) {
                obj.addProperty(key, s.getValue());
            } else if (setting instanceof StringSetting s) {
                obj.addProperty(key, s.getValue());
            } else if (setting instanceof ItemSetting s) {
                Item item = s.getValue();
                if (item != null && !item.equals(Items.AIR)) {
                    ResourceLocation loc = Registry.ITEM.getKey(item);
                    obj.addProperty(key, loc != null ? loc.toString() : "");
                } else {
                    obj.addProperty(key, "");
                }
            } else if (setting instanceof ThemeSetting s) {
                obj.addProperty(key, s.getValue().getName());
            } else if (setting instanceof MultiItemSetting s) {
                JsonArray arr = new JsonArray();
                for (Item item : s.getValue()) {
                    if (item != null && !item.equals(Items.AIR)) {
                        ResourceLocation loc = Registry.ITEM.getKey(item);
                        if (loc != null) {
                            arr.add(loc.toString());
                        }
                    }
                }
                obj.add(key, arr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadGui(JsonObject obj) {
        try {
            if (obj.has("panelWidth") && obj.has("panelHeight") && obj.has("panelSpacing")) {
                float w = obj.get("panelWidth").getAsFloat();
                float h = obj.get("panelHeight").getAsFloat();
                float s = obj.get("panelSpacing").getAsFloat();
                Excellent.inst().configManager().savePanelDimensions(w, h, s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveGui(JsonObject obj) {
        try {
            if (Excellent.inst().configManager().hasSavedDimensions()) {
                obj.addProperty("panelWidth", Excellent.inst().configManager().getSavedPanelWidth());
                obj.addProperty("panelHeight", Excellent.inst().configManager().getSavedPanelHeight());
                obj.addProperty("panelSpacing", Excellent.inst().configManager().getSavedPanelSpacing());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHudSettings(JsonObject obj) {
        try {
            Hud hud = Hud.getInstance();
            if (hud == null) return;

            String[] elements = {
                    "targethud", "watermark", "stafflist", "keybinds", "potions",
                    "cooldowns", "scoreboard", "bossbar", "information", "armorhud",
                    "inventoryhud", "motiongraph", "armorsmart", "itembinds", "itemcount"
            };

            for (String name : elements) {
                if (!obj.has(name)) continue;

                JsonObject settings = obj.getAsJsonObject(name);
                Object renderer = getRenderer(name);

                if (renderer instanceof IConfigurableHud config) {
                    for (DragComponent.HudSetting setting : config.getHudSettings()) {
                        String key = sanitize(setting.name);
                        if (settings.has(key)) {
                            float value = settings.get(key).getAsFloat();
                            setting.setter.accept(value);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveHudSettings(JsonObject obj) {
        try {
            Hud hud = Hud.getInstance();
            if (hud == null) return;

            String[] elements = {
                    "targethud", "watermark", "stafflist", "keybinds", "potions",
                    "cooldowns", "scoreboard", "bossbar", "information", "armorhud",
                    "inventoryhud", "motiongraph", "armorsmart", "itembinds", "itemcount"
            };

            for (String name : elements) {
                Object renderer = getRenderer(name);

                if (renderer instanceof IConfigurableHud config) {
                    List<DragComponent.HudSetting> settings = config.getHudSettings();

                    if (!settings.isEmpty()) {
                        JsonObject settingsObj = new JsonObject();

                        for (DragComponent.HudSetting setting : settings) {
                            float value = setting.getter.get();
                            settingsObj.addProperty(sanitize(setting.name), value);
                        }

                        obj.add(name, settingsObj);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMainMenu(JsonObject obj) {
        try {
            Map<String, Object> settings = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                JsonElement value = entry.getValue();
                if (value.isJsonPrimitive()) {
                    JsonPrimitive prim = value.getAsJsonPrimitive();
                    if (prim.isBoolean()) {
                        settings.put(entry.getKey(), prim.getAsBoolean());
                    } else if (prim.isNumber()) {
                        settings.put(entry.getKey(), prim.getAsFloat());
                    } else if (prim.isString()) {
                        settings.put(entry.getKey(), prim.getAsString());
                    }
                }
            }
            Excellent.inst().configManager().saveMainMenuSettings(settings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveMainMenu(JsonObject obj) {
        try {
            Map<String, Object> settings = Excellent.inst().configManager().getMainMenuSettings();
            if (settings == null || settings.isEmpty()) return;

            for (Map.Entry<String, Object> entry : settings.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Boolean) {
                    obj.addProperty(entry.getKey(), (Boolean) value);
                } else if (value instanceof Number) {
                    obj.addProperty(entry.getKey(), (Number) value);
                } else if (value instanceof String) {
                    obj.addProperty(entry.getKey(), (String) value);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object getRenderer(String name) {
        Hud hud = Hud.getInstance();
        if (hud == null) return null;

        return switch (name.toLowerCase()) {
            case "targethud" -> hud.targetHudRenderer();
            case "watermark" -> hud.watermarkRenderer();
            case "stafflist" -> hud.staffListRenderer();
            case "keybinds" -> hud.keybindsRenderer();
            case "potions" -> hud.potionsRenderer();
            case "cooldowns" -> hud.cooldownRenderer();
            case "scoreboard" -> hud.scoreBoardRenderer();
            case "bossbar" -> hud.bossBarRenderer();
            case "information" -> hud.informationRenderer();
            case "armorhud" -> hud.armorHudRenderer();
            case "inventoryhud" -> hud.inventoryHudRenderer();
            case "motiongraph" -> hud.motionGraphRenderer();
            case "armorsmart" -> hud.armorSmartRenderer();
            case "itembinds" -> hud.itemBindRenderer();
            case "arraylist" -> hud.arrayListRenderer();
            case "itemcount" -> hud.itemCountRenderer();
            default -> null;
        };
    }

    private String sanitize(String key) {
        if (key == null) return "";
        return key.replaceAll("[^a-zA-Z0-9а-яА-ЯёЁ_-]", "_");
    }
}
