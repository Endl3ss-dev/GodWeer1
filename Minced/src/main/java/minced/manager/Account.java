/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.client.MinecraftClient
 *  sLM.4ZXYEo
 *  sg.ec.ЧЭ$Ф
 */
package minced.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import minced.core.AccountEntry;
import minced.core.AccountStorage;
import minced.core.ConfigFile;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import com.mojang.authlib.yggdrasil.ProfileResult;
import minced.enums.CRACKED;
import minced.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.session.Session;
import net.minecraft.client.MinecraftClient;
import sLM._4ZXYEo;
import sg.ec.cheh;

public class Account
extends ConfigFile
{
    static String PARSE_ERROR_MESSAGE;
    static String SESSION_ERROR_MESSAGE;
    static String MISSING_TOKEN_MESSAGE;
    static String TOKEN_SAVE_ERROR_MESSAGE;

    Object data;
    static String ACCOUNTS_READ_KEY;
    static String NAME_READ_KEY;
    static String SELECTED_ACCOUNT_READ_KEY;
    static String TYPE_READ_KEY;
    static String TIMESTAMP_READ_KEY;
    static String FAVORITE_READ_KEY;
    static String LAST_SERVER_READ_KEY;
    static String UUID_READ_KEY;
    static String TOKEN_DATA_READ_KEY;
    static String ACCOUNT_FILE_PATH = "temp\\accounts.file";
    boolean sessionInitializationPending = true;
    static String NAME_WRITE_KEY;
    static String TYPE_WRITE_KEY;
    static String TIMESTAMP_WRITE_KEY;
    static String FAVORITE_WRITE_KEY;
    static String LAST_SERVER_WRITE_KEY;
    static String UUID_WRITE_KEY;
    static String TOKEN_DATA_WRITE_KEY;
    static String ACCOUNTS_WRITE_KEY;
    static String SELECTED_ACCOUNT_WRITE_KEY;
    static String MICROSOFT_REAUTH_LABEL;

    public void addAccount(String param1, CRACKED param2, UUID param3) {
        if (param1 == null || param1.trim().isEmpty()) {
            return;
        }
        String v4 = param1.replace(" ", "_").trim();
        if (this.findAccount(v4) != null) {
            return;
        }
        ((AccountStorage)this.data).getAccounts().add(new AccountEntry(v4, param2, param3));
        this.save();
    }

    public List getFavoriteAccounts() {
        return ((AccountStorage)this.data).getAccounts().stream().filter(x -> x.isFavorite()).collect(Collectors.toList());
    }

    public int getAccountCount() {
        return ((AccountStorage)this.data).getAccounts().size();
    }

    public void setFavorite(String param1, boolean param2) {
        AccountEntry v3 = this.findAccount(param1);
        if (v3 != null) {
            v3.setFavorite(param2);
            this.save();
        }
    }

    public void addCrackedAccount(String param1) {
        this.addAccount(param1, CRACKED.CRACKED);
    }

    private void readIfPresent(JsonObject param1, String param2, Consumer<JsonElement> param3) {
        JsonElement v4 = param1.get(param2);
        if (v4 != null && !v4.isJsonNull()) {
            param3.accept(v4);
        }
    }

    public void setSelectedAccount(String param1) {
        AccountEntry v2 = this.findAccount(param1);
        if (v2 != null) {
            ((AccountStorage)this.data).setSelectedAccountName(param1);
            this.save();
        }
    }

    public AccountEntry findAccount(String param12) {
        if (param12 == null) {
            return null;
        }
        String clean = param12.replace(" ", "_").trim();
        return ((AccountStorage)this.data).getAccounts().stream().filter((AccountEntry param1) -> param1.getName().equals(param12) || param1.getName().equals(clean)).findFirst().orElse(null);
    }

    @Override
    protected void readJson(JsonObject param12) {
        ((AccountStorage)this.data).getAccounts().clear();
        ((AccountStorage)this.data).setSelectedAccountName(null);
        JsonArray v2 = param12.getAsJsonArray(ACCOUNTS_READ_KEY);
        if (v2 != null) {
            for (JsonElement v4 : v2) {
                if (!v4.isJsonObject()) continue;
                JsonObject v5 = v4.getAsJsonObject();
                this.readIfPresent(v5, NAME_READ_KEY, param2 -> {
                    JsonElement v12;
                    JsonElement v11;
                    JsonElement v10;
                    String v3 = param2.getAsString().replace(" ", "_").trim();
                    CRACKED v4_enum = CRACKED.CRACKED;
                    long v5_time = System.currentTimeMillis();
                    boolean v7 = false;
                    String v8 = "";
                    JsonElement v9 = v5.get(TYPE_READ_KEY);
                    if (v9 != null && !v9.isJsonNull()) {
                        v4_enum = CRACKED.valueOf(v9.getAsString());
                    }
                    if ((v10 = v5.get(TIMESTAMP_READ_KEY)) != null && !v10.isJsonNull()) {
                        v5_time = v10.getAsLong();
                    }
                    if ((v11 = v5.get(FAVORITE_READ_KEY)) != null && !v11.isJsonNull()) {
                        v7 = v11.getAsBoolean();
                    }
                    if ((v12 = v5.get(LAST_SERVER_READ_KEY)) != null && !v12.isJsonNull()) {
                        v8 = v12.getAsString();
                    }
                    UUID v13 = null;
                    JsonElement v14 = v5.get(UUID_READ_KEY);
                    if (v14 != null && !v14.isJsonNull()) {
                        v13 = UUID.fromString(v14.getAsString());
                    }
                    byte[] v15 = null;
                    JsonElement v16 = v5.get(TOKEN_DATA_READ_KEY);
                    if (v16 != null && !v16.isJsonNull()) {
                        v15 = Base64.getDecoder().decode(v16.getAsString());
                    }
                    ((AccountStorage)this.data).getAccounts().add(new AccountEntry(v3, v4_enum, v5_time, v7, v8, v13, v15));
                });
            }
        }
        this.readIfPresent(param12, SELECTED_ACCOUNT_READ_KEY, param1 -> ((AccountStorage)this.data).setSelectedAccountName(param1.getAsString().replace(" ", "_").trim()));
    }

    public void importMicrosoftAccount(AccountEntry param1, Object param2) {
        // IAS has been removed; importing Microsoft account data is disabled.
    }

    public void addAccount(String param1, CRACKED param2) {
        this.addAccount(param1, param2, null);
    }

    public Account() {
        super(ACCOUNT_FILE_PATH);
    }

    public String getSelectedAccountName() {
        return ((AccountStorage)this.data).getSelectedAccountName();
    }

    public List getAccountNames() {
        return ((AccountStorage)this.data).getAccounts().stream().map(x -> x.getName()).collect(Collectors.toList());
    }

    public List getRegularAccounts() {
        return ((AccountStorage)this.data).getAccounts().stream().filter(param0 -> !param0.isFavorite()).collect(Collectors.toList());
    }

    @Override
    protected JsonObject writeJson() {
        JsonObject v1 = new JsonObject();
        JsonArray v2 = new JsonArray();
        for (AccountEntry v4 : ((AccountStorage)this.data).getAccounts()) {
            JsonObject v5 = new JsonObject();
            v5.addProperty(NAME_WRITE_KEY, v4.getName());
            v5.addProperty(TYPE_WRITE_KEY, v4.getAccountType().name());
            v5.addProperty(TIMESTAMP_WRITE_KEY, (Number)v4.getCreatedAt());
            v5.addProperty(FAVORITE_WRITE_KEY, Boolean.valueOf(v4.isFavorite()));
            v5.addProperty(LAST_SERVER_WRITE_KEY, v4.getLastServerAddress());
            if (v4.getUuid() != null) {
                v5.addProperty(UUID_WRITE_KEY, v4.getUuid().toString());
            }
            if (v4.getMicrosoftTokenData() != null) {
                v5.addProperty(TOKEN_DATA_WRITE_KEY, Base64.getEncoder().encodeToString(v4.getMicrosoftTokenData()));
            }
            v2.add((JsonElement)v5);
        }
        v1.add(ACCOUNTS_WRITE_KEY, (JsonElement)v2);
        if (((AccountStorage)this.data).getSelectedAccountName() != null) {
            v1.addProperty(SELECTED_ACCOUNT_WRITE_KEY, ((AccountStorage)this.data).getSelectedAccountName());
        }
        return v1;
    }

    public void login(String param1) {
        if (param1 == null) return;
        String clean = param1.replace(" ", "_").trim();
        AccountEntry v2 = this.findAccount(clean);
        if (v2 != null) {
            ((AccountStorage)this.data).setSelectedAccountName(v2.getName());
            this.save();
            this.applyAccountSession(v2.getName());
        }
    }

    private void applyAccountSession(String param1) {
        AccountEntry account = this.findAccount(param1);
        if (account == null || account.getAccountType() != CRACKED.CRACKED) {
            return;
        }
        this.applyOfflineSession(account);
    }

    /**
     * Подменяет сессию клиента на оффлайн-аккаунт.
     */
    private void applyOfflineSession(AccountEntry account) {
        String v2 = account.getName().replace(" ", "_").trim();
        account.setName(v2);
        UUID v3 = account.getUuid();
        if (v3 == null) {
            v3 = UUID.nameUUIDFromBytes(
                    ("OfflinePlayer:" + v2).getBytes(java.nio.charset.StandardCharsets.UTF_8));
            account.setUuid(v3);
        }
        this.save();
        Session v4 = new Session(v2, v3, "", Optional.empty(), Optional.empty(), Session.AccountType.LEGACY);
        MinecraftClient v5 = MinecraftClient.getInstance();
        ((MinecraftAccessor)((Object)v5)).ias$session(v4);
        ((MinecraftAccessor)((Object)v5)).ias$gameProfileFuture(
                CompletableFuture.completedFuture(new ProfileResult(new com.mojang.authlib.GameProfile(v3, v2))));
    }

    public void initializeSession() {
        if (!this.sessionInitializationPending) {
            return;
        }
        this.sessionInitializationPending = false;
        String v1 = this.getSelectedAccountName();
        if (v1 != null && !v1.isEmpty()) {
            this.applyAccountSession(v1);
        }
    }

    private void authenticateMicrosoftAccount(AccountEntry param1) {
        byte[] v2 = param1.getMicrosoftTokenData();
        if (v2 == null) {
            System.err.println((String)((Object)_4ZXYEo.ф6("makeConcatWithConstants", new Object[]{}, (String)param1.getName())));
            return;
        }
        // IAS has been removed; Microsoft account login is disabled.
    }

    @Override
    protected void resetData() {
        this.data = new AccountStorage();
    }

    @Override
    public void load() {
        this.ensureParentDirectory();
        this.resetData();
        this.loadFromDisk();
    }

    public List getAccountsSnapshot() {
        return new ArrayList(((AccountStorage)this.data).getAccounts());
    }

    public void toggleFavorite(String param1) {
        AccountEntry v2 = this.findAccount(param1);
        if (v2 != null) {
            v2.setFavorite(!v2.isFavorite());
            this.save();
        }
    }

    public void updateLastServerAddress(String param1, String param2) {
        AccountEntry v3 = this.findAccount(param1);
        if (v3 != null) {
            v3.setLastServerAddress(param2 != null ? param2 : "");
            this.save();
        }
    }

    public void removeAccount(String param12) {
        if (param12 == null) {
            return;
        }
        boolean v2 = ((AccountStorage)this.data).getAccounts().removeIf(param1 -> param1.getName().equals(param12));
        if (v2) {
            if (param12.equals(((AccountStorage)this.data).getSelectedAccountName())) {
                ((AccountStorage)this.data).setSelectedAccountName(null);
            }
            this.save();
        }
    }

    public boolean isFavorite(String param1) {
        AccountEntry v2 = this.findAccount(param1);
        return v2 != null && v2.isFavorite();
    }

    static {
        NAME_WRITE_KEY = "name";
        TYPE_WRITE_KEY = "type";
        TIMESTAMP_WRITE_KEY = "timestamp";
        FAVORITE_WRITE_KEY = "isFavorite";
        LAST_SERVER_WRITE_KEY = "lastServerIp";
        UUID_WRITE_KEY = "uuid";
        TOKEN_DATA_WRITE_KEY = "msTokenData";
        ACCOUNTS_WRITE_KEY = "accounts";
        SELECTED_ACCOUNT_WRITE_KEY = "selectedAccount";
        ACCOUNTS_READ_KEY = "accounts";
        NAME_READ_KEY = "name";
        PARSE_ERROR_MESSAGE = "Failed to parse account entry: \u0001";
        SELECTED_ACCOUNT_READ_KEY = "selectedAccount";
        SESSION_ERROR_MESSAGE = "[Minced] Failed to apply session for account: \u0001 - \u0001";
        MISSING_TOKEN_MESSAGE = "[Minced] Microsoft account '\u0001' has no saved tokens.";
        MICROSOFT_REAUTH_LABEL = "Minced-MSReAuth";
        TOKEN_SAVE_ERROR_MESSAGE = "[Minced] Failed to save Microsoft tokens: \u0001";
        TYPE_READ_KEY = "type";
        TIMESTAMP_READ_KEY = "timestamp";
        FAVORITE_READ_KEY = "isFavorite";
        LAST_SERVER_READ_KEY = "lastServerIp";
        UUID_READ_KEY = "uuid";
        TOKEN_DATA_READ_KEY = "msTokenData";
    }
}


