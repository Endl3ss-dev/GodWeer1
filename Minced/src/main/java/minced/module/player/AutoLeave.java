/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 */
package minced.module.player;
import minced.core.EventHandler;
import minced.core.ServerDetector;

import java.util.List;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.StaffList;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class AutoLeave
extends Module {
    MultiBooleanSetting field5952 = new MultiBooleanSetting(field5969, new BooleanSetting(field5970, true), new BooleanSetting(field5971, true), new BooleanSetting(field5972, true));
    static String field5953;
    static String field5954;
    static String field5955;
    BooleanSetting field5956;
    ModeSetting field5957 = new ModeSetting(field5973, field5974, () -> this.field5952.method1898(field5953) != false || this.field5952.method1898(field5954) != false || this.field5952.method1898(field5955) != false, field5975, field5976, field5977);
    static String field5958;
    static String field5959;
    static String field5960;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field5961;
    static String field5962;
    static String field5963;
    static String field5964;
    static String field5965;
    static String field5966;
    static String field5967;
    static String field5968;
    static String field5969;
    static String field5970;
    static String field5971;
    static String field5972;
    static String field5973;
    static String field5974;
    static String field5975;
    static String field5976;
    static String field5977;
    static String field5978;
    static String field5979;
    static float field5980;
    static float field5981;
    SliderSetting field5982;
    static String field5983;
    static float field5984;
    static float field5985;
    SliderSetting field5986;
    static String field5987;
    static String field5988;
    static String field5989;
    static String field5990;
    static String field5991;

    private void method1416() {
        if (((Boolean)this.field5956.getValue()).booleanValue() && !ServerDetector.method2682()) {
            return;
        }
        String v1 = (String)this.field5957.getValue();
        int v2 = -1;
        switch (v1.hashCode()) {
            case 1009609085: {
                if (!v1.equals(field5958)) break;
                v2 = 0;
                break;
            }
            case 997585421: {
                if (!v1.equals(field5959)) break;
                v2 = 1;
                break;
            }
            case 1053926: {
                if (!v1.equals(field5960)) break;
                v2 = 2;
            }
        }
        switch (v2) {
            case 0: {
                AutoLeave.I.player.networkHandler.sendCommand(field5961);
                break;
            }
            case 1: {
                AutoLeave.I.player.networkHandler.sendCommand(field5962);
                break;
            }
            case 2: {
                AutoLeave.I.player.networkHandler.sendCommand(field5963);
            }
        }
        this.toggle();
    }

    public AutoLeave() {
        super(field5967, field5968, Combat.Player);
        this.field5956 = new BooleanSetting(field5978, false, () -> this.field5952.method1898(field5964) != false || this.field5952.method1898(field5965) != false || this.field5952.method1898(field5966) != false);
        this.field5982 = new SliderSetting(field5979, field5980, 1.0f, field5981, 1.0f, () -> this.field5952.method1898(field5991));
        this.field5986 = new SliderSetting(field5983, field5984, 1.0f, field5985, 1.0f, () -> this.field5952.method1898(field5987));
        this.addSettings(this.field5952, this.field5957, this.field5956, this.field5982, this.field5986);
    }

    @EventHandler(2)
    public void method1419(EventUpdate param1) {
        List v2;
        if (AutoLeave.I.player == null || AutoLeave.I.world == null) {
            return;
        }
        if (this.field5952.method1898(field5988).booleanValue() && AutoLeave.I.player.getHealth() <= ((Float)this.field5982.getValue()).floatValue()) {
            this.method1416();
            return;
        }
        if (this.field5952.method1898(field5989).booleanValue()) {
            v2 = AutoLeave.I.world.getPlayers();
            for (PlayerEntity v4 : (Iterable<PlayerEntity>)(v2)) {
                if (v4 == AutoLeave.I.player || Minced.getInstance().getFriendManager().cfr_renamed_4(v4.getGameProfile().getName()) || !(AutoLeave.I.player.distanceTo((Entity)v4) <= ((Float)this.field5986.getValue()).floatValue())) continue;
                this.method1416();
                return;
            }
        }
        if (this.field5952.method1898(field5990).booleanValue() && !(v2 = StaffList.method2575()).isEmpty()) {
            this.method1416();
        }
    }

    static {
        field5967 = "Auto Leave";
        field5968 = "Избегает нежелательной встречи с игроками, выбранным способом, обеспечивая безопасность ";
        field5969 = "Ливать если";
        field5970 = "Мало здоровья";
        field5971 = "Рядом игрок";
        field5972 = "Администратор в ванише";
        field5973 = "Тип ухода";
        field5974 = "Спавн";
        field5975 = "Спавн";
        field5976 = "Домой";
        field5977 = "Хаб";
        field5978 = "Ливать только в конце пвп";
        field5979 = "Здоровье";
        field5980 = 15.0f;
        field5981 = 20.0f;
        field5983 = "Радиус";
        field5984 = 10.0f;
        field5985 = 50.0f;
        field5988 = "Мало здоровья";
        field5989 = "Рядом игрок";
        field5990 = "Администратор в ванише";
        field5958 = "Спавн";
        field5959 = "Домой";
        field5960 = "Хаб";
        field5961 = "spawn";
        field5962 = "home home";
        field5963 = "hub";
        field5987 = "Рядом игрок";
        field5991 = "Мало здоровья";
        field5964 = "Мало здоровья";
        field5965 = "Рядом игрок";
        field5966 = "Администратор в ванише";
        field5953 = "Мало здоровья";
        field5954 = "Рядом игрок";
        field5955 = "Администратор в ванише";
    }
}



