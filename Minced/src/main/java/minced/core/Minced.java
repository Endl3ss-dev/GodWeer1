/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ModInitializer
 */
package minced.core;

import minced.core.UserInfo;
import minced.core.ServiceRegistry;
import minced.core.EventBus;
import minced.core.Fonts;
import minced.manager.Account;
import minced.manager.AutoConfig;
import minced.manager.BlockESP;
import minced.manager.ClickGui;
import minced.manager.Command;
import minced.manager.ComponentHandler;
import minced.manager.Config;
import minced.manager.Dragging;
import minced.manager.Friend;
import minced.manager.Handler;
import minced.manager.Inventory;
import minced.manager.Macro;
import minced.manager.Module;
import minced.manager.Staff;
import minced.manager.Theme;
import minced.manager.Waypoint;
import net.fabricmc.api.ModInitializer;

public class Minced
implements ModInitializer {
    ClickGui field12510;
    Dragging field12511;
    Account field12512;
    Theme field12513;
    Config field12514;
    Module field12515;
    Friend field12516;
    Staff field12517;
    Macro field12518;
    Waypoint field12519;
    BlockESP field12520;
    Inventory field12521;
    ComponentHandler field12522;
    Handler field12523;
    public Command field12524;
    AutoConfig field12525;
    static Minced field12526;

    public ClickGui getDropDown() {
        return this.field12510;
    }

    private void initManager() {
        this.field12511 = new Dragging();
        this.field12511.load();
        this.field12512 = new Account();
        this.field12512.load();
        this.field12513 = new Theme();
        this.field12513.load();
        this.field12514 = new Config();
        this.field12514.load();
        this.field12515 = new Module();
        this.field12515.method183();
        this.field12516 = new Friend();
        this.field12516.load();
        this.field12517 = new Staff();
        this.field12517.load();
        this.field12518 = new Macro();
        this.field12518.load();
        this.field12519 = new Waypoint();
        this.field12519.load();
        this.field12520 = new BlockESP();
        this.field12520.load();
        this.field12521 = new Inventory();
        this.field12521.load();
        this.field12522 = new ComponentHandler();
        this.field12522.method2015();
        this.field12523 = new Handler();
        this.field12523.method3542();
        this.field12510 = new ClickGui();
        this.field12524 = new Command();
        ServiceRegistry.method2114();
        this.field12525 = new AutoConfig();
        this.field12525.load();
        this.field12525.method1645();
    }

    public Dragging getDraggingManager() {
        return this.field12511;
    }

    public Theme getThemeManager() {
        return this.field12513;
    }

    public Waypoint getWaypointManager() {
        return this.field12519;
    }

    public Macro getMacroManager() {
        return this.field12518;
    }

    public Account getAccountManager() {
        return this.field12512;
    }

    public AutoConfig getAutoConfigManager() {
        return this.field12525;
    }

    public BlockESP getBlockESPManager() {
        return this.field12520;
    }

    public Command getCommandManager() {
        return this.field12524;
    }

    public ComponentHandler getComponentHandler() {
        return this.field12522;
    }

    public Module getModuleManager() {
        return this.field12515;
    }

    public Friend getFriendManager() {
        return this.field12516;
    }

    private void init() {
        Fonts.method3556();
        UserInfo.method1839();
        this.initManager();
        EventBus.method3013(this);
        DiscordRPC.start();
    }

    public Handler getHandlerManager() {
        return this.field12523;
    }

    public Config getConfigManager() {
        return this.field12514;
    }

    public void onInitialize() {
        field12526 = this;
        this.init();
    }

    public Staff getStaffManager() {
        return this.field12517;
    }

    public static Minced getInstance() {
        return field12526;
    }

    public Inventory getInventoryManager() {
        return this.field12521;
    }
}


