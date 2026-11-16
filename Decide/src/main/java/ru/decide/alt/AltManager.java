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
 *  net.minecraft.class_310
 *  net.minecraft.class_320
 */
package ru.decide.alt;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import net.minecraft.class_320;
import ru.decide.alt.Account;
import ru.decide.mixin.MinecraftClientAccessor;

@Environment(value=EnvType.CLIENT)
public class AltManager {
    private static final Path FILE = Path.of("C:/decide/client1_21_11/alts.json", new String[0]);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static AltManager instance;
    private final List<Account> accounts = new ArrayList<Account>();
    private String activeName = null;
    private boolean booted = false;

    public static AltManager get() {
        if (instance == null) {
            instance = new AltManager();
            instance.load();
        }
        return instance;
    }

    public List<Account> getAccounts() {
        return this.accounts;
    }

    public boolean exists(String name) {
        return this.accounts.stream().anyMatch(a -> a.name.equalsIgnoreCase(name));
    }

    public Account add(String name) {
        String string = name = name == null ? "" : name.trim();
        if (name.isEmpty() || this.exists(name)) {
            return null;
        }
        Account account = new Account(name);
        this.accounts.add(account);
        this.save();
        return account;
    }

    public String randomName() {
        return this.randomName("");
    }

    public String randomName(String base) {
        base = base == null ? "" : base.trim();
        StringBuilder cleaned = new StringBuilder();
        for (char c : base.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '_') continue;
            cleaned.append(c);
        }
        if (cleaned.length() > 12) {
            cleaned.setLength(12);
        }
        base = cleaned.toString();
        String pool = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random r = new Random();
        for (int attempt = 0; attempt < 60; ++attempt) {
            int maxLen = Math.max(1, Math.min(12, 16 - base.length()));
            int minLen = Math.min(4, maxLen);
            int n = minLen + r.nextInt(maxLen - minLen + 1);
            StringBuilder sb = new StringBuilder(base);
            for (int i = 0; i < n; ++i) {
                sb.append("abcdefghijklmnopqrstuvwxyz0123456789".charAt(r.nextInt("abcdefghijklmnopqrstuvwxyz0123456789".length())));
            }
            String name = sb.toString();
            if (name.isEmpty() || name.length() > 16 || this.exists(name)) continue;
            return name;
        }
        return (base.isEmpty() ? "Player" : base) + r.nextInt(10000);
    }

    public void remove(Account account) {
        if (this.accounts.remove(account)) {
            this.save();
        }
    }

    public void toggleFavorite(Account account) {
        if (account == null) {
            return;
        }
        account.favorite = !account.favorite;
        this.save();
    }

    public List<Account> getSorted() {
        ArrayList<Account> sorted = new ArrayList<Account>();
        for (Account a : this.accounts) {
            if (!a.favorite) continue;
            sorted.add(a);
        }
        for (Account a : this.accounts) {
            if (a.favorite) continue;
            sorted.add(a);
        }
        return sorted;
    }

    public void login(Account account) {
        if (account == null) {
            return;
        }
        this.applySession(account);
        this.activeName = account.name;
        this.save();
    }

    private void applySession(Account account) {
        class_310 mc = class_310.method_1551();
        if (mc == null) {
            return;
        }
        class_320 session = new class_320(account.name, account.offlineUuid(), "0", Optional.empty(), Optional.empty());
        ((MinecraftClientAccessor)mc).setSession(session);
    }

    public void bootstrap() {
        if (this.booted) {
            return;
        }
        class_310 mc = class_310.method_1551();
        if (mc == null || mc.method_1548() == null) {
            return;
        }
        this.booted = true;
        if (this.activeName == null) {
            return;
        }
        for (Account a : this.accounts) {
            if (!a.name.equalsIgnoreCase(this.activeName)) continue;
            this.applySession(a);
            return;
        }
    }

    public String currentName() {
        return class_310.method_1551().method_1548().method_1676();
    }

    public void save() {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();
        for (Account a : this.accounts) {
            JsonObject o = new JsonObject();
            o.addProperty("name", a.name);
            o.addProperty("type", a.type.name());
            o.addProperty("created", (Number)a.created);
            o.addProperty("favorite", Boolean.valueOf(a.favorite));
            arr.add((JsonElement)o);
        }
        root.add("accounts", (JsonElement)arr);
        if (this.activeName != null) {
            root.addProperty("active", this.activeName);
        }
        try {
            Files.createDirectories(FILE.getParent(), new FileAttribute[0]);
            try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream(FILE.toFile()), StandardCharsets.UTF_8);){
                GSON.toJson((JsonElement)root, (Appendable)writer);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        this.accounts.clear();
        if (!Files.exists(FILE, new LinkOption[0])) {
            return;
        }
        try (InputStreamReader reader = new InputStreamReader((InputStream)new FileInputStream(FILE.toFile()), StandardCharsets.UTF_8);){
            JsonObject root = JsonParser.parseReader((Reader)reader).getAsJsonObject();
            if (root.has("active")) {
                this.activeName = root.get("active").getAsString();
            }
            if (!root.has("accounts")) {
                return;
            }
            for (JsonElement el : root.getAsJsonArray("accounts")) {
                JsonObject o = el.getAsJsonObject();
                String name = o.has("name") ? o.get("name").getAsString() : null;
                if (name == null || name.isBlank()) continue;
                Account.Type type = Account.Type.OFFLINE;
                if (o.has("type")) {
                    try {
                        type = Account.Type.valueOf(o.get("type").getAsString());
                    }
                    catch (IllegalArgumentException illegalArgumentException) {
                        // empty catch block
                    }
                }
                long created = o.has("created") ? o.get("created").getAsLong() : System.currentTimeMillis();
                Account acc = new Account(name, type, created);
                if (o.has("favorite")) {
                    acc.favorite = o.get("favorite").getAsBoolean();
                }
                this.accounts.add(acc);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

