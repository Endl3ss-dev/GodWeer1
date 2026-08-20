/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 */
package minced.core;

import java.util.LinkedList;
import minced.core.CancellableEvent;
import minced.core.NotificationWidget;
import minced.core.EventRender2D;
import minced.core.Minced;
import minced.module.visual.Interface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class NotificationManager
implements CancellableEvent {
    static MinecraftClient I = MinecraftClient.getInstance();
    static LinkedList<NotificationWidget> field8942;
    static String field8943;
    static float field8944;
    static float field8945;

    @EventHandler(2)
    public void method2451(EventRender2D param1) {
        if (I == null || I.getWindow() == null) {
            return;
        }
        field8942.removeIf(v -> !v.method2474());
        if (!Minced.getInstance().getModuleManager().method187(Interface.class).isEnabled() || !Interface.field2067.method1898(field8943).booleanValue()) {
            return;
        }
        int v2 = I.getWindow().getScaledWidth();
        int v3 = I.getWindow().getScaledHeight();
        float v4 = (float)v3 / 2.0f + field8944;
        for (NotificationWidget v6 : field8942) {
            v6.method2477(((float)v2 - v6.method2484()) / 2.0f, v4, param1.method2997());
            v4 += field8945;
        }
    }

    public static void method2452() {
        field8942 = new LinkedList<NotificationWidget>();
    }

    public static void method2453(Identifier param0, Text param1, int param2) {
        NotificationManager.method2457(new NotificationWidget(param0, param1, (Integer)param2));
    }

    public static void method2454(Identifier param0, Text param1) {
        NotificationManager.method2457(new NotificationWidget(param0, param1, null));
    }

    public static boolean method2455() {
        return !field8942.isEmpty();
    }

    public static void method2456(String param0, String param1, int param2) {
        NotificationManager.method2457(new NotificationWidget(param0, param1, param2));
    }

    private static void method2457(NotificationWidget param0) {
        field8942.addFirst(param0);
        if (field8942.size() > 10) {
            field8942.subList(10, field8942.size()).forEach(NotificationWidget::method2478);
        }
    }

    static {
        field8943 = "Уведомления";
        field8944 = 21.0f;
        field8945 = 18.0f;
        NotificationManager.method2452();
    }
}



