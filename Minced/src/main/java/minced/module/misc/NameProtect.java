/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package minced.module.misc;
import minced.core.EventHandler;

import minced.core.BooleanSetting;
import minced.core.ColorListSetting;
import minced.core.EventNameProtect;
import minced.core.FriendEntry;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;

public class NameProtect
extends Module {
    static String field6592;
    static BooleanSetting field6593;
    ColorListSetting field6594 = new ColorListSetting(field6598);
    static String field6595;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field6596;
    static String field6597;
    static String field6598;

    public static void method1557() {
        field6593 = new BooleanSetting(field6592, false);
    }

    @EventHandler(2)
    public void method1558(EventNameProtect param1) {
        String v2 = param1.method2590();
        String v3 = this.field6594.method2237();
        if (v3 == null || v3.isEmpty()) {
            v3 = field6595;
        }
        v2 = v2.replace(I.getSession().getUsername(), v3);
        if (((Boolean)field6593.getValue()).booleanValue()) {
            for (FriendEntry v5 : (Iterable<FriendEntry>)(Minced.getInstance().getFriendManager().method1705())) {
                v2 = v2.replace(v5.method2633(), v3);
            }
        }
        param1.method2588(v2);
    }

    public NameProtect() {
        super(field6596, field6597, Combat.Miscellaneous);
        this.addSettings(field6593, this.field6594);
    }

    static {
        field6596 = "Name Protect";
        field6597 = "Скрывает ваш ник, обеспечивая конфиденциальность и защиту от преследования";
        field6598 = "Заменять на";
        field6595 = "Protected";
        field6592 = "Скрывать друзей";
        NameProtect.method1557();
    }
}



