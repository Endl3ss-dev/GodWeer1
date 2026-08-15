package antileak.base.api.storages;


import antileak.base.Punch;
import antileak.base.api.QClient;
import antileak.base.api.events.EventInvoker;
import antileak.base.api.storages.implement.*;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.api.utils.tps.TPSCalc;
import antileak.base.mods.maseffects.MaseffectsParticleTypes;
import antileak.base.mods.particular.ParticularParticleTypes;
import antileak.base.client.modules.impl.render.TotemAngel;

public class InitializeStorage implements QClient {

    public void onInitialize() {
        EventInvoker.register(this);
        this.initStorages();
    }

    
    public void initStorages() {
        MaseffectsParticleTypes.register();
        ParticularParticleTypes.register();
        Punch.INSTANCE.moduleStorage = new ModuleStorage();
        Punch.INSTANCE.themeStorage = new ThemeStorage();
        Punch.INSTANCE.tpsCalc = new TPSCalc();
        EventInvoker.register(Punch.INSTANCE.tpsCalc);
        Punch.INSTANCE.localizationStorage = new LocalizationStorage();
        Punch.INSTANCE.freeLookStorage = new FreeLookStorage();
        Punch.INSTANCE.rotationStorage = new RotationStorage();
        Punch.INSTANCE.serverStorage = new ServerStorage();
        Punch.INSTANCE.serverStorage.ServerManager();
        Punch.INSTANCE.friendStorage = new FriendStorage();
        Punch.INSTANCE.macroStorage = new MacroStorage();
        Punch.INSTANCE.staffStorage = new StaffStorage();
        Punch.INSTANCE.waypointStorage = new WaypointStorage();
        Punch.INSTANCE.commandStorage = new CommandStorage();
        Punch.INSTANCE.configStorage = new ConfigStorage();
    }
}
