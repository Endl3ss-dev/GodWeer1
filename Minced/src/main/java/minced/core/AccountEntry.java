/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.core;

import java.util.UUID;
import java.util.function.Predicate;
import minced.enums.CRACKED;

public class AccountEntry {
    String name;
    boolean favorite;
    CRACKED accountType;
    long createdAt;
    String lastServerAddress;
    byte[] microsoftTokenData;
    UUID uuid;

    public void setName(String param1) {
        this.name = param1;
    }

    public void setFavorite(boolean param1) {
        this.favorite = param1;
    }

    public CRACKED getAccountType() {
        return this.accountType;
    }

    public AccountEntry(String param1, CRACKED param2, UUID param3) {
        this(param1, param2, System.currentTimeMillis(), false, "", param3, null);
    }

    public long getCreatedAt() {
        return this.createdAt;
    }

    public boolean isFavorite() {
        return this.favorite;
    }

    public void setLastServerAddress(String param1) {
        this.lastServerAddress = param1;
    }

    public void setMicrosoftTokenData(byte[] param1) {
        this.microsoftTokenData = param1;
    }

    public AccountEntry(String param1, CRACKED param2, long param3, boolean param5, String param6, UUID param7) {
        this(param1, param2, param3, param5, param6, param7, null);
    }

    public AccountEntry(String param1, CRACKED param2, long param3, boolean param5, String param6, UUID param7, byte[] param8) {
        this.name = param1;
        this.accountType = param2;
        this.createdAt = param3;
        this.favorite = param5;
        this.lastServerAddress = param6;
        this.uuid = param7;
        this.microsoftTokenData = param8;
    }

    public void setAccountType(CRACKED param1) {
        this.accountType = param1;
    }

    public AccountEntry(String param1, CRACKED param2, long param3, boolean param5, String param6) {
        this(param1, param2, param3, param5, param6, null, null);
    }

    public void setUuid(UUID param1) {
        this.uuid = param1;
    }

    public String getLastServerAddress() {
        return this.lastServerAddress;
    }

    public byte[] getMicrosoftTokenData() {
        return this.microsoftTokenData;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public AccountEntry(String param1, CRACKED param2) {
        this(param1, param2, System.currentTimeMillis(), false, "", null, null);
    }

    public String getName() {
        return this.name;
    }

    public void setCreatedAt(long param1) {
        this.createdAt = param1;
    }
}



