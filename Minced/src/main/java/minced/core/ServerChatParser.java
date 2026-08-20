/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.gui.screen.ingame.GenericContainerScreen
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.s2c.play.GameMessageS2CPacket
 *  net.minecraft.scoreboard.AbstractTeam
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.ScoreboardDisplaySlot
 *  net.minecraft.scoreboard.ScoreboardEntry
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.screen.GenericContainerScreenHandler
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.text.Text
 *  org.apache.commons.lang3.StringUtils
 */
package minced.core;

import minced.core.ColorUtil;
import minced.core.CancellableEvent;
import minced.core.EventUpdate;
import minced.core.NotificationManager;
import minced.core.EventPacket;
import minced.core.ServerDetector;
import minced.core.ClientState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

public class ServerChatParser
implements CancellableEvent {
    int cfr_renamed_4;
    boolean field8946;
    static String field8947;
    static String field8948;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field8949;
    static String field8950;
    static String field8951;
    ClientState field8952 = new ClientState();
    static double field8953;
    static String field8954;
    static String field8955;
    static String field8956;
    static String field8957;
    static String field8958;
    static String field8959;
    static String field8960;
    static String field8961;

    public void method2458(int param1) {
        if (param1 > 0 && param1 < 64) {
            this.cfr_renamed_4 = param1;
            this.field8946 = true;
        } else {
            NotificationManager.method2456(field8947, field8948, ColorUtil.method1852(255, 85, 85, 255));
        }
    }

    public int method2459() {
        return this.method2462();
    }

    public void method2460(int param1, int param2, SlotActionType param3, boolean param4) {
        this.method2463(ServerChatParser.I.player.currentScreenHandler.syncId, param1, param2, param3, param4);
    }

    @EventHandler(2)
    public void method2461(EventUpdate param1) {
        GenericContainerScreen v3;
        if (this.cfr_renamed_4 == 0) {
            return;
        }
        if (!ServerDetector.method2684(field8949)) {
            this.cfr_renamed_4 = 0;
            return;
        }
        int v2 = this.method2462();
        if (this.field8946) {
            if (v2 == -1) {
                this.field8946 = false;
            } else {
                ServerChatParser.I.player.networkHandler.sendChatCommand(field8950);
            }
            return;
        }
        if (v2 == this.cfr_renamed_4) {
            this.cfr_renamed_4 = 0;
            return;
        }
        Screen v4 = ServerChatParser.I.currentScreen;
        if (v4 instanceof GenericContainerScreen && (v3 = (GenericContainerScreen)v4).getTitle().getString().equals(field8951)) {
            int[] nArray;
            boolean bl = ((GenericContainerScreenHandler)v3.getScreenHandler()).getInventory().size() < 10;
            if (this.cfr_renamed_4 < 15) {
                int[] nArray2 = new int[2];
                nArray2[0] = 0;
                nArray = nArray2;
                nArray2[1] = 0;
            } else if (this.cfr_renamed_4 < 33) {
                int[] nArray3 = new int[2];
                nArray3[0] = 1;
                nArray = nArray3;
                nArray3[1] = 14;
            } else if (this.cfr_renamed_4 < 48) {
                int[] nArray4 = new int[2];
                nArray4[0] = 2;
                nArray = nArray4;
                nArray4[1] = 32;
            } else {
                int[] nArray5 = new int[2];
                nArray5[0] = 3;
                nArray = nArray5;
                nArray5[1] = 47;
            }
            int[] v5 = nArray;
            if (bl) {
                this.method2460(v5[0], 0, SlotActionType.PICKUP, false);
            } else {
                this.method2460(17 + this.cfr_renamed_4 - v5[1], 0, SlotActionType.PICKUP, false);
            }
            return;
        }
        if (this.field8952.method3110(field8953)) {
            ServerChatParser.I.player.networkHandler.sendChatCommand(field8954);
        }
    }

    private int method2462() {
        if (ServerChatParser.I.world == null) {
            return -1;
        }
        Scoreboard v1 = ServerChatParser.I.world.getScoreboard();
        ScoreboardObjective v2 = v1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (v2 != null) {
            for (ScoreboardEntry v4 : v1.getScoreboardEntries(v2)) {
                String v6;
                String v5 = Team.decorateName((AbstractTeam)v1.getScoreHolderTeam(v4.owner()), (Text)v4.name()).getString();
                if (v5.isEmpty() || (v6 = StringUtils.substringBetween((String)v5, (String)field8955, (String)field8956)) == null || v6.isEmpty()) continue;
                return Integer.parseInt(v6.replace(field8957, ""));
            }
        }
        return -1;
    }

    public void method2463(int param1, int param2, int param3, SlotActionType param4, boolean param5) {
        ServerChatParser.I.interactionManager.clickSlot(param1, param2, param3, param4, (PlayerEntity)ServerChatParser.I.player);
        if (param5) {
            ServerChatParser.I.player.currentScreenHandler.onSlotClick(param2, param3, param4, (PlayerEntity)ServerChatParser.I.player);
        }
    }

    @EventHandler(2)
    public void method2464(EventPacket param1) {
        GameMessageS2CPacket v2;
        Object v3;
        if (this.cfr_renamed_4 != 0 && (v3 = param1.method2467()) instanceof GameMessageS2CPacket && !((String)(v3 = (v2 = (GameMessageS2CPacket)v3).content().getString().toLowerCase())).contains(field8958) && ((String)v3).contains(field8959)) {
            NotificationManager.method2456(field8960, field8961, ColorUtil.method1852(255, 85, 85, 255));
            this.cfr_renamed_4 = 0;
        }
    }

    static {
        field8958 = "хаб";
        field8959 = "не удалось";
        field8960 = "RCT";
        field8961 = "На данную анархию нельзя зайти";
        field8949 = "holyworld";
        field8950 = "hub";
        field8951 = "Выбор Лайт анархии:";
        field8953 = 500.0;
        field8954 = "lite";
        field8947 = "RCT";
        field8948 = "Неверный лайт";
        field8955 = "#";
        field8956 = " -◆-";
        field8957 = " (1.20)";
    }
}



