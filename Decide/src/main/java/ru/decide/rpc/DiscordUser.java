/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.jna.Structure
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 */
package ru.decide.rpc;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(value=EnvType.CLIENT)
public class DiscordUser
extends Structure {
    public String userId;
    public String username;
    @Deprecated
    public String discriminator;
    public String avatar;

    protected List<String> getFieldOrder() {
        return Arrays.asList("userId", "username", "discriminator", "avatar");
    }
}

