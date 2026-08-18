package sky.core.handlers;

import com.darkmagician6.eventapi.EventManager;
import lombok.Getter;
import other.party.PartyHandler;
import sky.core.handlers.impl.*;
import sky.core.utils.managers.impl.notificationmanager.NotificationManager;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class HandlerManager {

    private final List<Object> handlers = new CopyOnWriteArrayList<>();
    private AuraRotationHandler auraRotationHandler;

    public void init() {
        add(new TargetESPHandler());
        add(new InventoryHandler());
        add(new RotationHandler());
        auraRotationHandler = new AuraRotationHandler();
        add(auraRotationHandler);
        add(new NotificationManager());
        //add(new LookHandler());
        add(new TPSHandler());
        add(new StaffHandler());
        add(new MacroHandler());
        add(new AlertHandler());
        add(new PartyHandler());
        add(new ReallyWorldJoinHandler());
        add(new GpsRenderHandler());
        add(new WaypointRenderHandler());
        add(new AntiCrashMinecraftHandler());
        add(new PickItemFixHandler());
        add(new HolyWorldJoinHandler());
    }

    public void add(Object handler) {
        handlers.add(handler);
        EventManager.register(handler);
    }
}


