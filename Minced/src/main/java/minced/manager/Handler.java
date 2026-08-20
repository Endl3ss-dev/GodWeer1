/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 */
package minced.manager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import minced.core.ClientTicker;
import minced.core.PotionUtil;
import minced.core.ArrowRenderer;
import minced.core.ContainerUtil;
import minced.core.TargetRenderer;
import minced.core.NotificationManager;
import minced.core.ServerChatParser;
import minced.core.PerspectiveHandler;
import minced.core.StaffList;
import minced.core.WaypointRenderer;
import minced.core.TpsUtil;
import minced.core.EventBus;
import minced.core.InventoryHelper;

public class Handler {
    List field12463 = new CopyOnWriteArrayList();
    InventoryHelper field12464 = new InventoryHelper();
    ServerChatParser field12465 = new ServerChatParser();

    public void method3539(Object param1) {
        this.field12463.add(param1);
        EventBus.method3013(param1);
    }

    public InventoryHelper method3540() {
        return this.field12464;
    }

    public ServerChatParser method3541() {
        return this.field12465;
    }

    public void method3542() {
        this.method3539(new NotificationManager());
        this.method3539(new TargetRenderer());
        this.method3539(new WaypointRenderer());
        this.method3539(new TpsUtil());
        this.method3539(new PotionUtil());
        this.method3539(new ClientTicker());
        this.method3539(new ArrowRenderer());
        this.method3539(new ContainerUtil());
        this.method3539(new PerspectiveHandler());
        this.method3539(new StaffList());
        this.method3539(this.field12464);
        this.method3539(this.field12465);
    }
}



