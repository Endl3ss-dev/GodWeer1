/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.hud.ChatHud$ChatState
 *  net.minecraft.client.gui.hud.ClientBossBar
 *  net.minecraft.client.gui.hud.PlayerListHud
 *  net.minecraft.text.Text
 */
package minced.core;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import minced.mixin.accessor.BossBarHudAccessor;
import minced.mixin.accessor.InGameHudAccessor;
import minced.mixin.accessor.PlayerListHudAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.text.Text;

public final class HudAccess {
    private ChatHud.ChatState н;
    private final Map<UUID, ClientBossBar> с;
    private Text eh;
    private Text I;
    private boolean cfr_renamed_135;
    private Text cfr_renamed_136;
    private int cfr_renamed_137;
    private boolean cfr_renamed_138;
    private Text ь;
    private Text м;
    private int cfr_renamed_139;
    private int cfr_renamed_140;
    private int cfr_renamed_141;
    private int cfr_renamed_142;
    public void method2033(Text param1, Text param2) {
        this.eh = param1;
        this.I = param2;
    }

    public Map method2034() {
        return this.с;
    }

    public static HudAccess method2035(MinecraftClient param0) {
        HudAccess v1 = new HudAccess();
        v1.method2045(param0);
        return v1;
    }

    public void method2036(Text param1) {
        this.ь = param1;
    }

    public void method2037(MinecraftClient param1) {
        this.method2047(param1, true, true);
    }

    public void method2038(MinecraftClient param1, boolean param2, boolean param3) {
        if (param1 == null || param1.inGameHud == null) {
            return;
        }
        if (param2) {
            this.н = param1.inGameHud.getChatHud().toChatState();
        }
        PlayerListHud v4 = param1.inGameHud.getPlayerListHud();
        PlayerListHudAccessor v5 = (PlayerListHudAccessor)v4;
        this.eh = v5.getHeader();
        this.I = v5.getFooter();
        this.cfr_renamed_135 = v5.isVisible();
        InGameHudAccessor v6 = (InGameHudAccessor)param1.inGameHud;
        this.cfr_renamed_136 = v6.getOverlayMessage();
        this.cfr_renamed_137 = v6.getOverlayRemaining();
        this.cfr_renamed_138 = v6.isOverlayTinted();
        this.ь = v6.getTitleValue();
        this.м = v6.getSubtitleValue();
        this.cfr_renamed_139 = v6.getTitleRemainTicks();
        this.cfr_renamed_140 = v6.getTitleFadeInTicks();
        this.cfr_renamed_141 = v6.getTitleStayTicks();
        this.cfr_renamed_142 = v6.getTitleFadeOutTicks();
        if (param3) {
            this.с.clear();
            this.с.putAll(HudAccess.method2040(((BossBarHudAccessor)param1.inGameHud.getBossBarHud()).getBossBars()));
        }
    }

    public void method2039(boolean param1) {
        this.cfr_renamed_136 = null;
        this.cfr_renamed_137 = 0;
        this.cfr_renamed_138 = false;
        this.ь = null;
        this.м = null;
        this.cfr_renamed_139 = 0;
        if (param1) {
            this.cfr_renamed_140 = 10;
            this.cfr_renamed_141 = 70;
            this.cfr_renamed_142 = 20;
        }
    }

    private static Map<UUID, ClientBossBar> method2040(Map<UUID, ClientBossBar> param0) {
        LinkedHashMap<UUID, ClientBossBar> v1 = new LinkedHashMap<>();
        if (param0 == null) {
            return v1;
        }
        for (Map.Entry<UUID, ClientBossBar> v3 : param0.entrySet()) {
            ClientBossBar v4 = v3.getValue();
            if (v4 == null) continue;
            v1.put(v3.getKey(), new ClientBossBar(v3.getKey(), v4.getName(), v4.getPercent(), v4.getColor(), v4.getStyle(), v4.shouldDarkenSky(), v4.hasDragonMusic(), v4.shouldThickenFog()));
        }
        return v1;
    }

    public ChatHud.ChatState method2041() {
        return this.н;
    }

    public HudAccess() {
        this.с = new LinkedHashMap<>();
    }

    public void method2042(MinecraftClient param1, boolean param2, boolean param3, boolean param4, boolean param5) {
        if (param1 == null || param1.inGameHud == null) {
            return;
        }
        if (param4) {
            if (!param2) {
                param1.inGameHud.getChatHud().clear(false);
            } else if (this.н != null) {
                param1.inGameHud.getChatHud().restoreChatState(this.н);
            } else {
                param1.inGameHud.getChatHud().clear(false);
            }
        }
        PlayerListHud v6 = param1.inGameHud.getPlayerListHud();
        PlayerListHudAccessor v7 = (PlayerListHudAccessor)v6;
        v7.setHeaderValue(this.eh);
        v7.setFooterValue(this.I);
        v7.setVisibleValue(this.cfr_renamed_135);
        InGameHudAccessor v8 = (InGameHudAccessor)param1.inGameHud;
        v8.setOverlayMessageValue(this.cfr_renamed_136);
        v8.setOverlayRemaining(this.cfr_renamed_137);
        v8.setOverlayTinted(this.cfr_renamed_138);
        v8.setTitleValue(this.ь);
        v8.setSubtitleValue(this.м);
        v8.setTitleRemainTicks(this.cfr_renamed_139);
        v8.setTitleFadeInTicks(this.cfr_renamed_140);
        v8.setTitleStayTicks(this.cfr_renamed_141);
        v8.setTitleFadeOutTicks(this.cfr_renamed_142);
        if (param5) {
            Map<UUID, ClientBossBar> v9 = ((BossBarHudAccessor)param1.inGameHud.getBossBarHud()).getBossBars();
            v9.clear();
            if (param3) {
                v9.putAll(HudAccess.method2040(this.с));
            }
        }
    }

    public void method2043(Text param1) {
        this.м = param1;
    }

    public void method2044() {
        this.н = null;
        this.с.clear();
        this.eh = null;
        this.I = null;
        this.cfr_renamed_135 = false;
        this.method2039(true);
    }

    public void method2045(MinecraftClient param1) {
        this.method2038(param1, true, true);
    }

    public void method2046(Text param1, boolean param2) {
        this.cfr_renamed_136 = param1;
        this.cfr_renamed_138 = param2;
        this.cfr_renamed_137 = param1 != null ? 60 : 0;
    }

    public void method2047(MinecraftClient param1, boolean param2, boolean param3) {
        this.method2042(param1, param2, param3, true, true);
    }

    public void method2048(int param1, int param2, int param3) {
        this.cfr_renamed_140 = param1;
        this.cfr_renamed_141 = param2;
        this.cfr_renamed_142 = param3;
        if (this.ь != null || this.м != null) {
            this.cfr_renamed_139 = param1 + param2 + param3;
        }
    }
}



