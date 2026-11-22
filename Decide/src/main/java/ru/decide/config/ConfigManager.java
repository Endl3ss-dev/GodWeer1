/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  org.joml.Vector2fc
 */
package ru.decide.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector2fc;
import ru.decide.Client;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.manager.event_impl.EventTick;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Module;
import ru.decide.module.api.settings.Setting;
import ru.decide.module.api.settings.impl.BindSetting;
import ru.decide.module.api.settings.impl.BlockListSetting;
import ru.decide.module.api.settings.impl.BooleanSetting;
import ru.decide.module.api.settings.impl.BooleanSettingHud;
import ru.decide.module.api.settings.impl.ColorSetting;
import ru.decide.module.api.settings.impl.DragSetting;
import ru.decide.module.api.settings.impl.ModeSetting;
import ru.decide.module.api.settings.impl.ModeSettingHud;
import ru.decide.module.api.settings.impl.MultiBooleanSetting;
import ru.decide.module.api.settings.impl.PixelGridSetting;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.module.api.settings.impl.StringSetting;

@Environment(value=EnvType.CLIENT)
public class ConfigManager {
    public static final String AUTO_CONFIG = "auto";
    private static final Path CONFIG_DIR = Path.of("C:/decide/client1_21_11/config", new String[0]);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private int tickTimer = 0;
    private JsonObject pendingCosmetics;
    private static volatile boolean loading;

    public void setup() {
        try {
            Files.createDirectories(CONFIG_DIR, new FileAttribute[0]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Client.eventHandler().subscribe(this);
    }

    public void init() {
        this.load(AUTO_CONFIG);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
    }

    @EventHandler
    public void onTick(EventTick event) {
        if (++this.tickTimer >= 1200) {
            this.tickTimer = 0;
            this.save(AUTO_CONFIG);
        }
    }

    public void autoSave() {
        this.save(AUTO_CONFIG);
    }

    public void save(String name) {
        JsonObject root = new JsonObject();
        JsonObject modules = new JsonObject();
        for (Module module : Client.get().moduleManager().values()) {
            JsonObject moduleObj = new JsonObject();
            moduleObj.addProperty("enabled", Boolean.valueOf(module.isEnabled()));
            moduleObj.addProperty("key", (Number)module.getKey());
            JsonObject settings = new JsonObject();
            for (Setting<?> setting : module.getSettings()) {
                this.serializeSetting(settings, setting);
            }
            moduleObj.add("settings", (JsonElement)settings);
            modules.add(module.getName(), (JsonElement)moduleObj);
        }
        root.add("modules", (JsonElement)modules);
        CosmeticManager cosmetics = CosmeticManager.getInstance();
        if (cosmetics.isInitialized()) {
            root.add("cosmetics", (JsonElement)cosmetics.toConfigJson());
        }
        Path file = CONFIG_DIR.resolve(name + ".json");
        try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream(file.toFile()), StandardCharsets.UTF_8);){
            GSON.toJson((JsonElement)root, (Appendable)writer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLoadingConfig() {
        return loading;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean load(String name) {
        boolean bl;
        Path file = CONFIG_DIR.resolve(name + ".json");
        if (!Files.exists(file, new LinkOption[0])) {
            return false;
        }
        InputStreamReader reader = new InputStreamReader((InputStream)new FileInputStream(file.toFile()), StandardCharsets.UTF_8);
        try {
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            JsonObject modules = root.getAsJsonObject("modules");
            JsonObject cosmetics = root.has("cosmetics") && root.get("cosmetics").isJsonObject() ? root.getAsJsonObject("cosmetics") : null;
            loading = true;
            try {
                for (Module module : Client.get().moduleManager().values()) {
                    if (!modules.has(module.getName())) continue;
                    JsonObject moduleObj = modules.getAsJsonObject(module.getName());
                    if (moduleObj.has("enabled")) {
                        module.setEnabled(moduleObj.get("enabled").getAsBoolean(), false);
                    }
                    if (moduleObj.has("key")) {
                        module.setKey(moduleObj.get("key").getAsInt());
                    }
                    if (!moduleObj.has("settings")) continue;
                    JsonObject settings = moduleObj.getAsJsonObject("settings");
                    for (Setting<?> setting : module.getSettings()) {
                        this.deserializeSetting(settings, setting);
                    }
                }
            }
            finally {
                loading = false;
            }
            if (cosmetics != null) {
                if (CosmeticManager.getInstance().isInitialized()) {
                    CosmeticManager.getInstance().applyConfigJson(cosmetics);
                } else {
                    this.pendingCosmetics = cosmetics.deepCopy();
                }
            }
            bl = true;
        }
        catch (Throwable throwable) {
            try {
                try {
                    ((Reader)reader).close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        ((Reader)reader).close();
        return bl;
    }

    public void applyPendingCosmetics() {
        JsonObject pending = this.pendingCosmetics;
        this.pendingCosmetics = null;
        if (pending != null && CosmeticManager.getInstance().isInitialized()) {
            CosmeticManager.getInstance().applyConfigJson(pending);
        }
    }

    public boolean delete(String name) {
        if (name.equals(AUTO_CONFIG)) {
            return false;
        }
        Path file = CONFIG_DIR.resolve(name + ".json");
        try {
            if (Files.exists(file, new LinkOption[0])) {
                Files.delete(file);
                return true;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Path getConfigDir() {
        return CONFIG_DIR;
    }

    public List<String> listConfigs() {
        ArrayList<String> configs = new ArrayList<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(CONFIG_DIR, "*.json");){
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                configs.add(fileName.substring(0, fileName.length() - 5));
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return configs;
    }

    private void serializeSetting(JsonObject obj, Setting<?> setting) {
        if (setting instanceof DragSetting) {
            DragSetting drag = (DragSetting)setting;
            JsonObject vec = new JsonObject();
            vec.addProperty("x", (Number)Float.valueOf(drag.position.x));
            vec.addProperty("y", (Number)Float.valueOf(drag.position.y));
            obj.add(setting.getConfigKey(), (JsonElement)vec);
            return;
        }
        if (setting instanceof MultiBooleanSetting) {
            MultiBooleanSetting ms = (MultiBooleanSetting)setting;
            JsonObject msObj = new JsonObject();
            for (BooleanSetting val : ms.getValues()) {
                msObj.addProperty(val.getName().toLowerCase(), val.getValue());
            }
            obj.add(setting.getConfigKey(), (JsonElement)msObj);
            return;
        }
        if (setting instanceof BlockListSetting) {
            BlockListSetting bl = (BlockListSetting)setting;
            JsonArray arr = new JsonArray();
            for (String id : bl.getIds()) {
                arr.add(id);
            }
            obj.add(setting.getConfigKey(), (JsonElement)arr);
            return;
        }
        if (setting instanceof PixelGridSetting) {
            PixelGridSetting grid = (PixelGridSetting)setting;
            JsonArray arr = new JsonArray();
            for (String row : grid.toRows()) {
                arr.add(row);
            }
            obj.add(setting.getConfigKey(), (JsonElement)arr);
            return;
        }
        Object value = setting.getValue();
        if (value instanceof Boolean) {
            Boolean b = (Boolean)value;
            obj.addProperty(setting.getConfigKey(), b);
        } else if (value instanceof Float) {
            Float f = (Float)value;
            obj.addProperty(setting.getConfigKey(), (Number)f);
        } else if (value instanceof String) {
            String s = (String)value;
            obj.addProperty(setting.getConfigKey(), s);
        } else if (value instanceof Integer) {
            Integer i = (Integer)value;
            obj.addProperty(setting.getConfigKey(), (Number)i);
        }
    }

    private void deserializeSetting(JsonObject obj, Setting<?> setting) {
        if (!obj.has(setting.getConfigKey())) {
            return;
        }
        JsonElement el = obj.get(setting.getConfigKey());
        try {
            this.deserializeValue(obj, setting, el);
        }
        catch (Exception e) {
            System.err.println("[ConfigManager] \u041d\u0435 \u0443\u0434\u0430\u043b\u043e\u0441\u044c \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u044c \u043d\u0430\u0441\u0442\u0440\u043e\u0439\u043a\u0443 '" + setting.getName() + "': " + String.valueOf(e));
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void deserializeValue(JsonObject obj, Setting<?> setting, JsonElement el) {
        if (setting instanceof DragSetting) {
            DragSetting drag = (DragSetting)setting;
            if (el.isJsonObject()) {
                JsonObject vec = el.getAsJsonObject();
                drag.position.set(vec.get("x").getAsFloat(), vec.get("y").getAsFloat());
                drag.targetPosition.set((Vector2fc)drag.position);
                return;
            }
        }
        if (setting instanceof MultiBooleanSetting) {
            MultiBooleanSetting ms = (MultiBooleanSetting)setting;
            if (el.isJsonObject()) {
                JsonObject msObj = el.getAsJsonObject();
                Iterator<BooleanSetting> iterator = ms.getValues().iterator();
                while (iterator.hasNext()) {
                    BooleanSetting val = iterator.next();
                    String key = val.getName().toLowerCase();
                    if (!msObj.has(key)) continue;
                    val.set(msObj.get(key).getAsBoolean());
                }
                return;
            }
        }
        if (setting instanceof BlockListSetting) {
            BlockListSetting bl = (BlockListSetting)setting;
            if (el.isJsonArray()) {
                bl.getIds().clear();
                Iterator msObj = el.getAsJsonArray().iterator();
                while (msObj.hasNext()) {
                    JsonElement e = (JsonElement)msObj.next();
                    String id = e.getAsString();
                    if (bl.getIds().contains(id)) continue;
                    bl.getIds().add(id);
                }
                return;
            }
        }
        if (setting instanceof PixelGridSetting) {
            PixelGridSetting grid = (PixelGridSetting)setting;
            if (el.isJsonArray()) {
                ArrayList<String> rows = new ArrayList<String>();
                Iterator iterator = el.getAsJsonArray().iterator();
                while (true) {
                    if (!iterator.hasNext()) {
                        grid.fromRows(rows);
                        return;
                    }
                    JsonElement e = (JsonElement)iterator.next();
                    rows.add(e.getAsString());
                }
            }
        }
        if (setting instanceof BooleanSetting) {
            BooleanSetting bs = (BooleanSetting)setting;
            bs.set(el.getAsBoolean());
            return;
        }
        if (setting instanceof BooleanSettingHud) {
            BooleanSettingHud bs = (BooleanSettingHud)setting;
            bs.set(el.getAsBoolean());
            return;
        }
        if (setting instanceof SliderSetting) {
            SliderSetting ss = (SliderSetting)setting;
            ss.set(Float.valueOf(el.getAsFloat()));
            return;
        }
        if (setting instanceof ModeSetting) {
            ModeSetting ms = (ModeSetting)setting;
            ms.set(el.getAsString());
            return;
        }
        if (setting instanceof ModeSettingHud) {
            ModeSettingHud ms = (ModeSettingHud)setting;
            ms.set(el.getAsString());
            return;
        }
        if (setting instanceof BindSetting) {
            BindSetting bind = (BindSetting)setting;
            bind.set(el.getAsInt());
            return;
        }
        if (setting instanceof ColorSetting) {
            ColorSetting cs = (ColorSetting)setting;
            cs.set(el.getAsInt());
            return;
        }
        if (!(setting instanceof StringSetting)) return;
        StringSetting str = (StringSetting)setting;
        str.set(el.getAsString());
    }
}

