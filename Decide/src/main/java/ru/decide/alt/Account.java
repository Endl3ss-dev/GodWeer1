/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.alt;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class Account {
    public String name;
    public Type type;
    public long created;
    public boolean favorite;

    public Account(String name, Type type, long created) {
        this.name = name;
        this.type = type;
        this.created = created;
    }

    public Account(String name) {
        this(name, Type.OFFLINE, System.currentTimeMillis());
    }

    public UUID offlineUuid() {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + this.name).getBytes(StandardCharsets.UTF_8));
    }

    public String headUrl() {
        return "https://minotar.net/helm/" + this.name + "/64.png";
    }

    @Environment(value=EnvType.CLIENT)
    public static enum Type {
        OFFLINE;

    }
}

