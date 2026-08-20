package fun.volon.api.storages;


import fun.volon.Volon;
import fun.volon.api.QClient;
import fun.volon.api.events.EventInvoker;
import fun.volon.api.storages.implement.*;
import fun.volon.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import fun.volon.api.utils.tps.TPSCalc;
import fun.volon.client.modules.impl.render.TotemAngel;

public class InitializeStorage implements QClient {

    public void onInitialize() {
        EventInvoker.register(this);
        this.initStorages();
    }

    
    public void initStorages() {
        Volon.INSTANCE.moduleStorage = new ModuleStorage();
        Volon.INSTANCE.themeStorage = new ThemeStorage();
        Volon.INSTANCE.tpsCalc = new TPSCalc();
        EventInvoker.register(Volon.INSTANCE.tpsCalc);
        Volon.INSTANCE.localizationStorage = new LocalizationStorage();
        Volon.INSTANCE.freeLookStorage = new FreeLookStorage();
        Volon.INSTANCE.rotationStorage = new RotationStorage();
        // Volon.INSTANCE.serverStorage = new ServerStorage();
        Volon.INSTANCE.friendStorage = new FriendStorage();
        Volon.INSTANCE.macroStorage = new MacroStorage();
        Volon.INSTANCE.staffStorage = new StaffStorage();
        Volon.INSTANCE.waypointStorage = new WaypointStorage();
        Volon.INSTANCE.commandStorage = new CommandStorage();
        Volon.INSTANCE.configStorage = new ConfigStorage();
    }
}
