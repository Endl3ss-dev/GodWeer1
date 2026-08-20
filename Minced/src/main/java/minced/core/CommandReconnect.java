/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.brigadier.arguments.ArgumentType
 *  com.mojang.brigadier.arguments.IntegerArgumentType
 *  com.mojang.brigadier.builder.LiteralArgumentBuilder
 *  com.mojang.brigadier.context.CommandContext
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.HandledScreen
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
 *  net.minecraft.scoreboard.AbstractTeam
 *  net.minecraft.scoreboard.Scoreboard
 *  net.minecraft.scoreboard.ScoreboardDisplaySlot
 *  net.minecraft.scoreboard.ScoreboardEntry
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  net.minecraft.scoreboard.Team
 *  net.minecraft.screen.GenericContainerScreenHandler
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.text.Text
 *  net.minecraft.util.Hand
 *  net.minecraft.world.Difficulty
 *  sLM.4ZXYEo
 *  sg.ec.2O
 *  sg.ec.Фе$П
 */
package minced.core;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import minced.core.ColorUtil;
import minced.core.EventUpdate;
import minced.core.NotificationManager;
import minced.core.EventPacket;
import minced.core.ReconnectState;
import minced.core.ServerDetector;
import minced.core.EventBus;
import minced.core.ClientState;
import minced.core.InventoryUtil;
import minced.core.ClientCommand;
import minced.enums.NONE;
import minced.enums.NONE1;
import minced.enums.SOLO;
import minced.mixin.accessor.PlayerListHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import sLM._4ZXYEo;
import sg.ec._2O;
import sg.ec.fe;

public class CommandReconnect
extends ClientCommand
{
    static String cfr_renamed_2;
    static String cfr_renamed_3;

    ReconnectState field75 = new ReconnectState();
    static String field76;
    static String field77;
    static String field78;
    static String field79;
    static String field80;
    static String field81;
    static String field82;
    static String field83;
    static String field84;
    static String field85;
    static String field86;
    static String field87;
    static String field88;
    static String field89;
    static String field90;
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field91;
    static String field92;
    static String field93;
    static String field94;
    static String field95;
    static String field96;
    static String field97;
    static String field98;
    static String field99;
    static String field100;
    static String field101;
    static String field102;
    static String field103;
    static String field104;
    static String field105;
    static String field106;
    static String field107;
    static String field108;
    ClientState field109 = new ClientState();
    static double field110;
    static String field111;
    static String field112;
    static String field113;
    static String field114;
    static String field115;
    static String field116;
    static String field117;
    static String field118;
    static String field119;
    static String field120;
    static String field121;
    static String field122;
    static String field123;
    static String field124;
    static String field125;
    static String field126;
    static String field127;
    static String field128;
    static String field129;
    static String field130;
    static String field131;
    static String field132;
    static String field133;
    static String field134;
    static String field135;
    static String field136;
    static String field137;
    static String field138;
    static String field139;
    static String field140;
    static String field141 = "server";
    static String field142 = "reconnect";
    static String field143 = "rc";
    static String field144;
    static String field145;
    static String field146;
    static String field147;
    static double field148;
    static String field149;
    static String field150;
    static String field151;
    static String field152;
    static String field153;
    static String field154;
    static String field155;

    private void method80(GenericContainerScreenHandler param1, String param2) {
        int v3;
        if (this.field75.field849 == NONE.CLASSIC_MODE && param2.contains(field76)) {
            _2O.n4((GenericContainerScreenHandler)param1, (int)15);
            this.field75.field849 = NONE.CLASSIC_SERVER;
            return;
        }
        if (this.field75.field849 == NONE.CLASSIC_SERVER && param2.contains(field77) && this.method81(param1, v3 = _2O.д((int)this.field75.field848))) {
            _2O.n4((GenericContainerScreenHandler)param1, (int)v3);
            this.method86();
        }
    }

    private boolean cfr_renamed_0(int param1) {
        boolean bl;
        switch (this.field75.field847) {
            default: {
                throw new MatchException(null, null);
            }
            case NONE: {
                NotificationManager.method2456(field78, field79, ColorUtil.method1852(255, 85, 85, 255));
                bl = false;
                break;
            }
            case LITE: {
                SOLO v2 = this.method85(param1);
                if (v2 == SOLO.UNKNOWN) {
                    NotificationManager.method2456(field80, field81, ColorUtil.method1852(255, 85, 85, 255));
                    bl = false;
                    break;
                }
                this.field75.O = this.method98(v2);
                int v3 = _2O.cfr_renamed_1((String)this.field75.O, (int)param1);
                if (v3 == -1) {
                    NotificationManager.method2456(field82, field83, ColorUtil.method1852(255, 85, 85, 255));
                    bl = false;
                    break;
                }
                this.field75.field848 = param1;
                bl = true;
                break;
            }
            case LITE120: {
                if (param1 < 1 || param1 > 3) {
                    NotificationManager.method2456(field84, field85, ColorUtil.method1852(255, 85, 85, 255));
                    bl = false;
                    break;
                }
                this.field75.field848 = param1;
                bl = true;
                break;
            }
            case CLASSIC: {
                if (((Integer)_2O.д((int)param1)).intValue() == -1) {
                    NotificationManager.method2456(field86, field87, ColorUtil.method1852(255, 85, 85, 255));
                    bl = false;
                    break;
                }
                this.field75.field848 = param1;
                bl = true;
            }
        }
        return bl;
    }

    private boolean method81(GenericContainerScreenHandler param1, int param2) {
        return param2 >= 0 && param2 < param1.slots.size() && param1.getSlot(param2).hasStack();
    }

    private static /* synthetic */ void method82(StringBuilder param0, PlayerListEntry param1) {
        if (param1.getProfile() != null && param1.getProfile().getName() != null) {
            param0.append(param1.getProfile().getName()).append(field88);
        }
    }

    private void method83() {
        CharSequence v2;
        this.field75.field847 = NONE1.NONE;
        this.field75.O = "";
        this.field75.field848 = -1;
        if (!ServerDetector.method2684(field89) && !ServerDetector.method2684(field90) || CommandReconnect.I.player == null || CommandReconnect.I.inGameHud == null) {
            return;
        }
        Text v1 = ((PlayerListHudAccessor)CommandReconnect.I.inGameHud.getPlayerListHud()).getHeader();
        if ((v1 == null || v1.getString().isBlank()) && CommandReconnect.I.player.networkHandler != null && CommandReconnect.I.player.networkHandler.getPlayerList() != null) {
            final StringBuilder sb_v2 = new StringBuilder();
            CommandReconnect.I.player.networkHandler.getPlayerList().forEach(arg_0 -> CommandReconnect.method82(sb_v2, arg_0));
            if (!sb_v2.isEmpty()) {
                v1 = Text.of(sb_v2.toString());
            }
        }
        if (v1 == null) {
            return;
        }
        v2 = v1.getString();
        if (((String)v2).isBlank()) {
            return;
        }
        if (((String)v2).contains(field91)) {
            this.field75.field847 = NONE1.CLASSIC;
            this.method88((String)v2, field92);
        } else if (!(!((String)v2).contains(field93) || ((String)v2).contains(field94) || ((String)v2).contains(field95) || ((String)v2).contains(field96) || ((String)v2).contains(field97))) {
            this.field75.field847 = NONE1.LITE120;
            this.method88((String)v2, field98);
        } else if (((String)v2).contains(field99)) {
            this.field75.field847 = NONE1.LITE;
            this.method88((String)v2, field100);
        }
    }

    private int method84(CommandContext param1, Integer param2) {
        if (CommandReconnect.I.player == null || CommandReconnect.I.world == null) {
            return 1;
        }
        if ((ServerDetector.method2684(field101) || ServerDetector.method2684(field102)) && ServerDetector.method2682()) {
            NotificationManager.method2456(field103, field104, ColorUtil.method1852(255, 85, 85, 255));
            return 1;
        }
        this.field75.method267();
        this.method83();
        if (param2 != null) {
            if (ServerDetector.method2684(field105)) {
                this.field75.field848 = param2;
            } else if (ServerDetector.method2684(field106) || ServerDetector.method2684(field107)) {
                if (this.field75.field847 == NONE1.NONE) {
                    this.field75.field847 = NONE1.LITE;
                }
                if (!this.cfr_renamed_0(param2)) {
                    this.field75.method267();
                    return 1;
                }
            } else if (ServerDetector.method2684(field108)) {
                if (CommandReconnect.I.world.getDifficulty() == Difficulty.EASY && this.field109.method3110(field110)) {
                    int v3 = this.method90();
                    CommandReconnect.I.player.networkHandler.sendChatCommand((String)((Object)_4ZXYEo.弟к("makeConcatWithConstants", new Object[]{}, (int)v3)));
                    this.field109.method3114();
                    this.field75.field851 = false;
                }
            } else {
                this.field75.field848 = param2;
            }
        }
        this.field109.method3114();
        CommandReconnect.I.player.networkHandler.sendChatCommand(field111);
        this.field75.field851 = true;
        return 1;
    }

    private SOLO method85(int param1) {
        if (param1 >= 1 && param1 <= 16) {
            return SOLO.SOLO;
        }
        if (param1 >= 17 && param1 <= 37) {
            return SOLO.DUO;
        }
        if (param1 >= 38 && param1 <= 53) {
            return SOLO.TRIO;
        }
        if (param1 >= 54) {
            return SOLO.CLAN;
        }
        return SOLO.UNKNOWN;
    }

    private void method86() {
        this.field75.method267();
        this.field109.method3114();
    }

    private void method87(GenericContainerScreenHandler param1, String param2) {
        if (param2.contains(field112)) {
            _2O.n4((GenericContainerScreenHandler)param1, (int)12);
            this.method95();
            return;
        }
        if (this.field75.field849 == NONE.LITE_CATEGORY && param2.contains(field113)) {
            int v3 = _2O.Д((String)this.field75.O);
            if (v3 == -1) {
                this.field75.field849 = NONE.NONE;
                return;
            }
            if (!this.field75.field853) {
                _2O.n4((GenericContainerScreenHandler)param1, (int)v3);
                this.field75.field853 = true;
                this.field75.field849 = this.field75.field850 == -1 ? NONE.NONE : NONE.LITE_SERVER;
                return;
            }
        }
        if (this.field75.field849 == NONE.LITE_SERVER && param2.contains(field114)) {
            if (this.field75.field850 == -1) {
                this.field75.field849 = NONE.NONE;
                return;
            }
            if (this.method81(param1, this.field75.field850)) {
                _2O.n4((GenericContainerScreenHandler)param1, (int)this.field75.field850);
                this.method86();
            }
        }
    }

    private void method88(String param1, String param2) {
        String[] v3 = param1.split(field115);
        if (v3.length < 2) {
            return;
        }
        String v4 = v3[1].replace(field116, "").trim();
        String[] v5 = v4.split(field117);
        if (v5.length < 2) {
            return;
        }
        this.field75.O = v4.replace(param2, "").replaceAll(field118, "").trim();
        String v6 = v5[1].replaceAll(field119, "").trim();
        if (v6.isEmpty()) {
            return;
        }
        this.field75.field848 = Integer.parseInt(v6);
    }

    private int method89(CommandContext param1) {
        return this.method84(param1, null);
    }

    private int method90() {
        if (CommandReconnect.I.world == null) {
            return -1;
        }
        Scoreboard v1 = CommandReconnect.I.world.getScoreboard();
        ScoreboardObjective v2 = v1.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
        if (v2 != null) {
            for (ScoreboardEntry v4 : v1.getScoreboardEntries(v2)) {
                String v7;
                String[] v6;
                String v5 = Team.decorateName((AbstractTeam)v1.getScoreHolderTeam(v4.owner()), (Text)v4.name()).getString();
                if (v5.isEmpty() || !v5.contains(field120) || (v6 = v5.split(field121)).length <= 1 || (v7 = v6[1].replaceAll(field122, "")).isEmpty()) continue;
                return Integer.parseInt(v7);
            }
        }
        return -1;
    }

    private int method91() {
        if (CommandReconnect.I.world == null) {
            return -1;
        }
        for (ScoreboardObjective v2 : CommandReconnect.I.world.getScoreboard().getObjectives()) {
            String v3 = v2.getDisplayName().getString();
            if (!v3.contains(field123)) continue;
            return Integer.parseInt(v3.split(field124)[1].replace(field125, ""));
        }
        return -1;
    }

    @EventHandler(2)
    public void method92(EventPacket param1) {
        Object v3;
        int v22;
        if (!this.field75.field851 || CommandReconnect.I.player == null || CommandReconnect.I.world == null) {
            return;
        }
        if ((ServerDetector.method2684(field126) || ServerDetector.method2684(field127)) && !(param1.method2467() instanceof OpenScreenS2CPacket) && (v22 = InventoryUtil.method3336(Items.COMPASS)) != -1) {
            if (this.field75.field847 == NONE1.LITE) {
                if (!this.field75.field855) {
                    this.field75.field855 = true;
                    this.method95();
                    CommandReconnect.I.player.networkHandler.sendChatCommand(field128);
                }
            } else if (!this.field75.field854) {
                this.field75.field854 = true;
                CommandReconnect.I.player.networkHandler.sendChatCommand(field129);
            }
        }
        if ((ServerDetector.method2684(field130) || ServerDetector.method2684(field131)) && (v3 = param1.method2467()) instanceof OpenScreenS2CPacket) {
            OpenScreenS2CPacket v22a = (OpenScreenS2CPacket)v3;
            v3 = v22a.getName().getString();
            if (this.field75.field847 == NONE1.LITE && ((String)v3).contains(field132)) {
                this.field75.field849 = NONE.LITE_MODE;
            } else if (this.field75.field847 == NONE1.LITE && ((String)v3).contains(field133)) {
                this.method95();
            } else if (this.field75.field847 == NONE1.LITE120 && ((String)v3).contains(field134)) {
                this.field75.field849 = NONE.LITE120_MODE;
            } else if (this.field75.field847 == NONE1.LITE120 && ((String)v3).contains(field135)) {
                this.field75.field849 = NONE.LITE120_SERVER;
            } else if (this.field75.field847 == NONE1.CLASSIC && ((String)v3).contains(field136)) {
                this.field75.field849 = NONE.CLASSIC_MODE;
            } else if (this.field75.field847 == NONE1.CLASSIC && ((String)v3).contains(field137)) {
                this.field75.field849 = NONE.CLASSIC_SERVER;
            }
        }
    }

    private void method93(GenericContainerScreenHandler param1, String param2) {
        int v3;
        if (this.field75.field849 == NONE.LITE120_MODE && param2.contains(field138)) {
            _2O.n4((GenericContainerScreenHandler)param1, (int)10);
            this.field75.field849 = NONE.LITE120_SERVER;
            return;
        }
        if (this.field75.field849 == NONE.LITE120_SERVER && param2.contains(field139) && this.method81(param1, v3 = _2O.p11((int)this.field75.field848))) {
            _2O.n4((GenericContainerScreenHandler)param1, (int)v3);
            this.method86();
        }
    }

    public int method94(CommandContext param1) {
        int v2 = (Integer)param1.getArgument(field140, Integer.class);
        return this.method84(param1, v2);
    }

    private void method95() {
        this.field75.field849 = NONE.LITE_CATEGORY;
        this.field75.field853 = false;
        this.field75.field850 = _2O.cfr_renamed_1((String)this.field75.O, (int)this.field75.field848);
    }

    @Override
    public void run(LiteralArgumentBuilder param1) {
        param1.executes(this::method89);
        param1.then(CommandReconnect.Ж((String)field141, (ArgumentType)IntegerArgumentType.integer((int)1)).executes(this::method94));
    }

    public static RequiredArgumentBuilder Ж(String name, ArgumentType<?> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    private void method96() {
        HandledScreen v1;
        Object v3;
        block8: {
            block7: {
                v3 = CommandReconnect.I.currentScreen;
                if (!(v3 instanceof HandledScreen)) break block7;
                v1 = (HandledScreen)v3;
                v3 = CommandReconnect.I.player.currentScreenHandler;
                if (v3 instanceof GenericContainerScreenHandler) break block8;
            }
            return;
        }
        GenericContainerScreenHandler v2 = (GenericContainerScreenHandler)v3;
        v3 = v1.getTitle().getString();
        switch (this.field75.field847) {
            case LITE: {
                this.method87(v2, (String)v3);
                break;
            }
            case LITE120: {
                this.method93(v2, (String)v3);
                break;
            }
            case CLASSIC: {
                this.method80(v2, (String)v3);
            }
        }
    }

    public CommandReconnect() {
        super(field142, field143);
        EventBus.method3013(this);
    }

    @EventHandler(2)
    public void method97(EventUpdate param1) {
        if (!this.field75.field851 || CommandReconnect.I.world == null || CommandReconnect.I.player == null) {
            return;
        }
        if (ServerDetector.method2684(field144) || ServerDetector.method2684(field145)) {
            this.method96();
        } else if ((ServerDetector.method2684(field146) || ServerDetector.method2684(field147)) && CommandReconnect.I.world.getDifficulty() == Difficulty.EASY && this.field109.method3110(field148)) {
            int v22 = this.method90();
            CommandReconnect.I.player.networkHandler.sendChatCommand((String)((Object)_4ZXYEo.弟Р("makeConcatWithConstants", new Object[]{}, (int)v22)));
            this.field109.method3114();
            this.field75.field851 = false;
        } else if (ServerDetector.method2684(field149)) {
            int v22;
            if (!this.field75.field852 && (v22 = InventoryUtil.method3336(Items.COMPASS)) != -1) {
                CommandReconnect.I.player.getInventory().selectedSlot = v22;
                CommandReconnect.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(v22));
                CommandReconnect.I.interactionManager.interactItem((PlayerEntity)CommandReconnect.I.player, Hand.MAIN_HAND);
                this.field75.field852 = true;
            }
            if (CommandReconnect.I.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                String v22title = CommandReconnect.I.currentScreen.getTitle().getString();
                if (v22title.contains(field150)) {
                    CommandReconnect.I.interactionManager.clickSlot(CommandReconnect.I.player.currentScreenHandler.syncId, 21, 0, SlotActionType.PICKUP, (PlayerEntity)CommandReconnect.I.player);
                    CommandReconnect.I.player.currentScreenHandler.onSlotClick(21, 0, SlotActionType.PICKUP, (PlayerEntity)CommandReconnect.I.player);
                }
                if (v22title.contains(field151)) {
                    int v3;
                    int n = v3 = this.field75.field848 > 0 ? this.field75.field848 : this.method91();
                    if (v3 <= 0) {
                        return;
                    }
                    int v4 = v3 - 1;
                    CommandReconnect.I.interactionManager.clickSlot(CommandReconnect.I.player.currentScreenHandler.syncId, v4, 0, SlotActionType.PICKUP, (PlayerEntity)CommandReconnect.I.player);
                    CommandReconnect.I.player.currentScreenHandler.onSlotClick(v4, 0, SlotActionType.PICKUP, (PlayerEntity)CommandReconnect.I.player);
                    this.field75.field851 = false;
                }
            }
        }
    }

    private String method98(SOLO param1) {
        String string;
        switch (fe.p11.Щ[param1.ordinal()]) {
            case 1: {
                string = field152;
                break;
            }
            case 2: {
                string = field153;
                break;
            }
            case 3: {
                string = field154;
                break;
            }
            case 4: {
                string = field155;
                break;
            }
            default: {
                string = "";
            }
        }
        return string;
    }

    static {
        field140 = "server";
        field101 = "holyworld";
        field102 = "hollyworld";
        field103 = "RCT";
        field104 = "Вы находитесь в режиме пvp";
        field105 = "reallyworld";
        field106 = "holyworld";
        field107 = "hollyworld";
        field108 = "funtime";
        field110 = 1000.0;
        cfr_renamed_2 = "an\u0001";
        field111 = "hub";
        field144 = "holyworld";
        field145 = "hollyworld";
        field146 = "spooky";
        field147 = "funtime";
        field148 = 1000.0;
        cfr_renamed_3 = "an\u0001";
        field149 = "reallyworld";
        field150 = "Выбор сервера";
        field151 = "Выбор мира грифа";
        field126 = "holyworld";
        field127 = "hollyworld";
        field128 = "lite";
        field129 = "menu";
        field130 = "holyworld";
        field131 = "hollyworld";
        field132 = "Выберите режим";
        field133 = "Выбор Лайт анархии";
        field134 = "Выберите режим";
        field135 = "Выберите сервер Лайт";
        field136 = "Выберите режим";
        field137 = "Выберите Классич";
        field89 = "holyworld";
        field90 = "hollyworld";
        field91 = "Классик";
        field92 = "Классик";
        field93 = "Лайт";
        field94 = "СолоЛайт";
        field95 = "ДуоЛайт";
        field96 = "ТриоЛайт";
        field97 = "КланЛайт";
        field98 = "Лайт";
        field99 = "Лайт";
        field100 = "Лайт";
        field115 = "▶";
        field116 = "Анархия";
        field117 = "#";
        field118 = "#\\d+";
        field119 = "[^0-9]";
        field112 = "Выберите режим";
        field113 = "Выбор Лайт анархии";
        field114 = "Выбор Лайт анархии";
        field78 = "RCT";
        field79 = "Неизвестный режим";
        field80 = "RCT";
        field81 = "Неверный номер сервера";
        field82 = "RCT";
        field83 = "Неверный номер сервера";
        field84 = "RCT";
        field85 = "Неверный номер сервера (1-3)";
        field86 = "RCT";
        field87 = "Неверный номер сервера (1-9)";
        field138 = "Выберите режим";
        field139 = "Выберите сервер Лайт";
        field76 = "Выберите режим";
        field77 = "Выберите Классич";
        field152 = "Соло";
        field153 = "Дуо";
        field154 = "Трио";
        field155 = "Клан";
        field123 = "ГРИФ #";
        field124 = "#";
        field125 = " ";
        field120 = "Анархия #";
        field121 = "#";
        field122 = "[^0-9]";
        field88 = " ";
    }
}



