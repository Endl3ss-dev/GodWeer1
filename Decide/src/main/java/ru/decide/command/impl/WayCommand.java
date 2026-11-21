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
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 */
package ru.decide.command.impl;

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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import ru.decide.Client;
import ru.decide.command.Command;
import ru.decide.manager.event_impl.EventDisplay;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.theme.ThemeColor;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.colors.ColorFormatting;
import ru.decide.utils.colors.ColorUtil;
import ru.decide.utils.math.ChatUtils;
import ru.decide.utils.other.Projection;
import ru.decide.utils.render.RenderUtil;
import ru.decide.utils.render.font.Font;
import ru.decide.utils.render.font.Fonts;

@Environment(value=EnvType.CLIENT)
public class WayCommand
extends Command
implements IMinecraft {
    private static WayCommand INSTANCE;
    private static final Path FILE;
    private static final Gson GSON;
    private final Map<String, Waypoint> waypoints = new ConcurrentHashMap<String, Waypoint>();

    public static WayCommand get() {
        return INSTANCE;
    }

    public Collection<Waypoint> getWaypoints() {
        return this.waypoints.values();
    }

    public void addWaypoint(String name, double x, double y, double z) {
        this.waypoints.put(name.toLowerCase(), new Waypoint(name, new class_243(x, y, z)));
        this.save();
    }

    public boolean removeWaypoint(String name) {
        boolean removed;
        boolean bl = removed = this.waypoints.remove(name.toLowerCase()) != null;
        if (removed) {
            this.save();
        }
        return removed;
    }

    public WayCommand() {
        super("way", ".way <add|delete|clear|list>", "\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442\u044b \u2014 \u043c\u0435\u0442\u043a\u0438 \u043d\u0430 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u0430\u0445");
        INSTANCE = this;
        this.load();
        Client.eventHandler().subscribe(this);
    }

    @Override
    public void execute(String[] args) {
        if (args.length == 0) {
            this.showHelp();
            return;
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                class_243 pos;
                if (args.length < 2) {
                    ChatUtils.addChatMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: \u00a7f.way add <\u0438\u043c\u044f> [x y z]");
                    return;
                }
                String name = args[1];
                if (args.length >= 5) {
                    try {
                        pos = new class_243(Double.parseDouble(args[2]), Double.parseDouble(args[3]), Double.parseDouble(args[4]));
                    }
                    catch (NumberFormatException ex) {
                        ChatUtils.addChatMessage("\u00a7c\u041e\u0448\u0438\u0431\u043a\u0430: \u043d\u0435\u0432\u0435\u0440\u043d\u044b\u0435 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b");
                        return;
                    }
                } else {
                    if (WayCommand.mc.field_1724 == null) {
                        ChatUtils.addChatMessage("\u00a7c\u0412\u044b \u043d\u0435 \u0432 \u043c\u0438\u0440\u0435, \u0443\u043a\u0430\u0436\u0438\u0442\u0435 \u043a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b: \u00a7f.way add <\u0438\u043c\u044f> <x> <y> <z>");
                        return;
                    }
                    pos = WayCommand.mc.field_1724.method_73189();
                }
                boolean replaced = this.waypoints.put(name.toLowerCase(), new Waypoint(name, pos)) != null;
                this.save();
                ChatUtils.addChatMessage((replaced ? "\u00a7a\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442 \u043e\u0431\u043d\u043e\u0432\u043b\u0451\u043d: \u00a7f" : "\u00a7a\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442 \u0434\u043e\u0431\u0430\u0432\u043b\u0435\u043d: \u00a7f") + name + " \u00a77(" + (int)pos.field_1352 + ", " + (int)pos.field_1351 + ", " + (int)pos.field_1350 + ")");
                break;
            }
            case "delete": 
            case "del": 
            case "remove": {
                if (args.length < 2) {
                    ChatUtils.addChatMessage("\u00a77\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435: \u00a7f.way delete <\u0438\u043c\u044f>");
                    return;
                }
                if (this.waypoints.remove(args[1].toLowerCase()) != null) {
                    this.save();
                    ChatUtils.addChatMessage("\u00a7a\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442 \u0443\u0434\u0430\u043b\u0451\u043d: \u00a7f" + args[1]);
                    break;
                }
                ChatUtils.addChatMessage("\u00a7c\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442 \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d: \u00a7f" + args[1]);
                break;
            }
            case "clear": {
                int count = this.waypoints.size();
                this.waypoints.clear();
                this.save();
                ChatUtils.addChatMessage("\u00a7a\u0423\u0434\u0430\u043b\u0435\u043d\u043e \u0432\u0435\u0439\u043f\u043e\u0438\u043d\u0442\u043e\u0432: \u00a7f" + count);
                break;
            }
            case "list": {
                if (this.waypoints.isEmpty()) {
                    ChatUtils.addChatMessage("\u00a77\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442\u043e\u0432 \u043d\u0435\u0442");
                    return;
                }
                ChatUtils.addChatMessage("\u00a77\u0412\u0435\u0439\u043f\u043e\u0438\u043d\u0442\u044b (\u00a7f" + this.waypoints.size() + "\u00a77):");
                for (Waypoint w : this.waypoints.values()) {
                    ChatUtils.addChatMessage("\u00a78\u2022 \u00a7f" + w.name() + " \u00a77\u2014 \u00a7f" + (int)w.pos().field_1352 + ", " + (int)w.pos().field_1351 + ", " + (int)w.pos().field_1350);
                }
                break;
            }
            default: {
                this.showHelp();
            }
        }
    }

    @Override
    public List<String> getSuggestions(String subPrefix) {
        String lower = subPrefix.toLowerCase();
        if (lower.startsWith("delete ") || lower.startsWith("del ") || lower.startsWith("remove ")) {
            String sub = lower.substring(0, lower.indexOf(32));
            String namePart = lower.substring(sub.length() + 1);
            return this.waypoints.values().stream().filter(w -> w.name().toLowerCase().startsWith(namePart)).map(w -> sub + " " + w.name()).toList();
        }
        return Stream.of("add", "delete", "clear", "list").filter(s -> s.startsWith(lower)).toList();
    }

    @EventHandler
    public void onDisplay(EventDisplay e) {
        if (WayCommand.mc.field_1724 == null || WayCommand.mc.field_1687 == null || this.waypoints.isEmpty()) {
            return;
        }
        if (WayCommand.mc.field_1690.field_1842) {
            return;
        }
        Font font = Fonts.sf_medium;
        float fontSize = 8.0f;
        class_243 camPos = WayCommand.mc.field_1773.method_19418().method_71156();
        float maxRenderDist = Math.max(64, (Integer)WayCommand.mc.field_1690.method_42503().method_41753() * 16 - 16);
        for (Waypoint w : this.waypoints.values()) {
            boolean clamped;
            float dist = (float)camPos.method_1022(w.pos());
            class_243 renderPos = w.pos();
            boolean bl = clamped = dist > maxRenderDist;
            if (clamped) {
                renderPos = camPos.method_1019(w.pos().method_1020(camPos).method_1029().method_1021((double)maxRenderDist));
            }
            class_243 screen = Projection.worldSpaceToScreenSpace(renderPos);
            if (screen.field_1350 <= 0.0 || screen.field_1350 >= 1.0) continue;
            String text = w.name() + " " + ColorFormatting.getColor(ThemeColor.getDarkTextColor(1.0f)) + (int)dist + "\u043c";
            float wr = font.getWidth(text, fontSize) + 10.0f;
            float h = 13.0f;
            float x = (float)screen.field_1352 - wr / 2.0f;
            float y = (float)screen.field_1351 - h - 6.0f;
            RenderUtil.Blur.blur(x, y, wr, h, 1.0f, 4.0f, ColorUtil.getRectColor(1.0f));
            RenderUtil.Render2D.outline(x, y, wr, h, 0.5f, ThemeColor.getOutlineColor(1.0f), 4.0f);
            font.drawCentered(text, x + wr / 2.0f, y + 1.8f, fontSize, ThemeColor.getTextColor(1.0f));
            if (clamped) continue;
            float iconSize = 8.0f;
            RenderUtil.Images.texture(class_2960.method_60655((String)"decide", (String)"textures/hud/marker.png"), (float)screen.field_1352 - iconSize / 2.0f, (float)screen.field_1351 - iconSize / 2.0f, iconSize, iconSize, ColorUtil.getClientColor(1));
        }
    }

    private void save() {
        JsonArray arr = new JsonArray();
        for (Waypoint w : this.waypoints.values()) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", w.name());
            obj.addProperty("x", (Number)w.pos().field_1352);
            obj.addProperty("y", (Number)w.pos().field_1351);
            obj.addProperty("z", (Number)w.pos().field_1350);
            arr.add((JsonElement)obj);
        }
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            try (OutputStreamWriter wtr = new OutputStreamWriter((OutputStream)new FileOutputStream(FILE.toFile()), StandardCharsets.UTF_8);){
                GSON.toJson((JsonElement)arr, (Appendable)wtr);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        if (!Files.exists(FILE, new LinkOption[0])) {
            return;
        }
        try (InputStreamReader r = new InputStreamReader((InputStream)new FileInputStream(FILE.toFile()), StandardCharsets.UTF_8);){
            JsonArray arr = JsonParser.parseReader((Reader)r).getAsJsonArray();
            this.waypoints.clear();
            for (JsonElement el : arr) {
                JsonObject obj = el.getAsJsonObject();
                String name = obj.get("name").getAsString();
                class_243 pos = new class_243(obj.get("x").getAsDouble(), obj.get("y").getAsDouble(), obj.get("z").getAsDouble());
                this.waypoints.put(name.toLowerCase(), new Waypoint(name, pos));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showHelp() {
        ChatUtils.addChatMessage("\u00a77.way \u00a7fadd \u00a77<\u0438\u043c\u044f> [x y z] \u00a78| \u00a77.way \u00a7fdelete \u00a77<\u0438\u043c\u044f> \u00a78| \u00a77.way \u00a7fclear \u00a78| \u00a77.way \u00a7flist");
    }

    static {
        FILE = Path.of("C:/decide/client1_21_11/waypoints.json", new String[0]);
        GSON = new GsonBuilder().setPrettyPrinting().create();
    }

    @Environment(value=EnvType.CLIENT)
    public record Waypoint(String name, class_243 pos) {
    }
}

