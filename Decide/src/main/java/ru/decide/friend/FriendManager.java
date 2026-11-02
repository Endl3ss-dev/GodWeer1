/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.friend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import ru.decide.Client;
import ru.decide.manager.event_impl.WorldLoadEvent;
import ru.decide.manager.events.orbit.EventHandler;

@Environment(value=EnvType.CLIENT)
public class FriendManager {
    private static final Path FILE = Path.of("C:/decide/client1_21_11/friends.json", new String[0]);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Set<String> friends = new HashSet<String>();

    public void init() {
        this.load();
        Client.eventHandler().subscribe(this);
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        this.load();
    }

    public boolean add(String name) {
        boolean added = this.friends.add(name.toLowerCase());
        if (added) {
            this.save();
        }
        return added;
    }

    public boolean remove(String name) {
        boolean removed = this.friends.remove(name.toLowerCase());
        if (removed) {
            this.save();
        }
        return removed;
    }

    public boolean isFriend(String name) {
        return this.friends.contains(name.toLowerCase());
    }

    public Set<String> getFriends() {
        return Collections.unmodifiableSet(this.friends);
    }

    private void save() {
        JsonArray arr = new JsonArray();
        this.friends.forEach(arg_0 -> ((JsonArray)arr).add(arg_0));
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            try (OutputStreamWriter w = new OutputStreamWriter((OutputStream)new FileOutputStream(FILE.toFile()), StandardCharsets.UTF_8);){
                GSON.toJson((JsonElement)arr, (Appendable)w);
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
            this.friends.clear();
            arr.forEach(el -> this.friends.add(el.getAsString().toLowerCase()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

