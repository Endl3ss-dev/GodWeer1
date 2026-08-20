/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.CommandDispatcher
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.ClientCommandSource
 *  net.minecraft.command.CommandSource
 */
package minced.manager;

import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayList;
import java.util.List;
import minced.core.CommandReconnect;
import minced.core.CommandHelp;
import minced.core.CommandFakePlayer;
import minced.core.CommandBlockEsp;
import minced.core.CommandConfig;
import minced.core.CommandWaypoint;
import minced.core.CommandParse;
import minced.core.CommandCounter;
import minced.core.CommandMacro;
import minced.core.CommandBind;
import minced.core.CommandStaff;
import minced.core.CommandInventory;
import minced.core.CommandFriend;
import minced.core.CommandNeuro;
import minced.core.CommandVClip;
import minced.core.CommandGps;
import minced.core.CommandSay;
import minced.core.CommandBot;
import minced.core.ClientCommand;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

public class Command {
    CommandSource field69;
    static String field70 = ".";
    String field71 = field70;
    CommandDispatcher field72 = new CommandDispatcher();
    List field73;

    public CommandSource method70() {
        return this.field69;
    }

    public Command() {
        this.field69 = new ClientCommandSource(null, MinecraftClient.getInstance());
        this.field73 = new ArrayList();
        this.method75(new CommandConfig());
        this.method75(new CommandFriend());
        this.method75(new CommandStaff());
        this.method75(new CommandBind());
        this.method75(new CommandHelp());
        this.method75(new CommandGps());
        this.method75(new CommandWaypoint());
        this.method75(new CommandReconnect());
        this.method75(new CommandVClip());
        this.method75(new CommandMacro());
        this.method75(new CommandBlockEsp());
        this.method75(new CommandInventory());
        this.method75(new CommandCounter());
        this.method75(new CommandParse());
        this.method75(new CommandFakePlayer());
        this.method75(new CommandNeuro());
        this.method75(new CommandSay());
        this.method75(new CommandBot());
    }

    public void method71(String param1) {
        this.field71 = param1;
    }

    public CommandDispatcher method72() {
        return this.field72;
    }

    public List method73() {
        return this.field73;
    }

    public String method74() {
        return this.field71;
    }

    private void method75(ClientCommand param1) {
        param1.method418(this.field72);
        this.field73.add(param1);
    }
}



