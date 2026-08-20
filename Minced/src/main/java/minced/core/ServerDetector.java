/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.Object2IntMap
 *  it.unimi.dsi.fastutil.objects.ObjectIterator
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.PlayerListEntry
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.network.packet.s2c.play.BossBarS2CPacket
 *  net.minecraft.network.packet.s2c.play.BossBarS2CPacket$Consumer
 *  net.minecraft.scoreboard.ScoreHolder
 *  net.minecraft.scoreboard.ScoreboardObjective
 *  sg.ec.2т$Ч
 */
package minced.core;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.network.packet.s2c.play.BossBarS2CPacket;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

public final class ServerDetector
{
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field9270;
    static String field9271;
    static String field9272;
    static boolean field9273;
    static UUID field9274;
    static String field9275;
    static String field9276;
    static String field9277;
    static String field9278;
    static String field9279;
    static String field9280;

    public static int method2681(LivingEntity param0) {
        if (I.getCurrentServerEntry() != null) {
            if (ServerDetector.method2684(field9270)) {
                Object2IntMap v1 = ServerDetector.I.world.getScoreboard().getScoreHolderObjectives((ScoreHolder)param0);
                for (ScoreboardObjective v3 : (Iterable<ScoreboardObjective>)(v1.keySet())) {
                    String v4 = v3.getDisplayName().getString();
                    if (!v4.contains(field9271) && !v4.contains(field9272)) continue;
                    return v1.getOrDefault((Object)v3, 20);
                }
            } else {
                Object2IntMap v1 = ServerDetector.I.world.getScoreboard().getScoreHolderObjectives((ScoreHolder)param0);
                ObjectIterator v2 = v1.keySet().iterator();
                if (v2.hasNext()) {
                    ScoreboardObjective v3 = (ScoreboardObjective)v2.next();
                    return v1.getOrDefault((Object)v3, 20);
                }
            }
        }
        return (int)((float)((int)param0.getHealth()) + param0.getAbsorptionAmount());
    }

    public static boolean method2682() {
        return field9273;
    }

    public static void method2683() {
        field9273 = false;
        field9274 = null;
    }

    public static boolean method2684(String param0) {
        return I.getCurrentServerEntry() != null && ServerDetector.I.getCurrentServerEntry().address != null && ServerDetector.I.getCurrentServerEntry().address.contains(param0);
    }

    public static String method2685() {
        if (ServerDetector.I.world == null || ServerDetector.I.player == null) {
            return field9275;
        }
        String v0 = I.isInSingleplayer() ? field9276 : (I.getCurrentServerEntry() == null ? field9277 : ServerDetector.I.getCurrentServerEntry().address);
        return v0;
    }

    public static void method2686(BossBarS2CPacket param0) {
        if (param0 == null) {
            return;
        }
        try {
            param0.accept(new BossBarS2CPacket.Consumer() {
                @Override
                public void add(UUID uuid, Text name, float percent, BossBar.Color color, BossBar.Style style, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {
                    field9273 = true;
                    field9274 = uuid;
                }

                @Override
                public void remove(UUID uuid) {
                    if (uuid != null && uuid.equals(field9274)) {
                        ServerDetector.method2683();
                    }
                }

                @Override
                public void updateProgress(UUID uuid, float percent) {}

                @Override
                public void updateName(UUID uuid, Text name) {}

                @Override
                public void updateStyle(UUID uuid, BossBar.Color color, BossBar.Style style) {}

                @Override
                public void updateProperties(UUID uuid, boolean darkenSky, boolean dragonMusic, boolean thickenFog) {}
            });
        } catch (Throwable ignored) {
        }
    }

    public static String method2687(String param0) {
        if (I == null || I.getNetworkHandler() == null) {
            return field9278;
        }
        for (PlayerListEntry v2 : I.getNetworkHandler().getListedPlayerListEntries()) {
            String v3 = v2.getProfile().getName();
            if (!param0.contains(v3)) continue;
            return v3;
        }
        return field9279;
    }

    private ServerDetector() {
        throw new UnsupportedOperationException(field9280);
    }

    static {
        field9273 = false;
        field9270 = "funtime";
        field9271 = "Здоровья";
        field9272 = "❤️";
        field9275 = "LocalHost";
        field9276 = "LocalHost";
        field9277 = "LocalHost";
        field9278 = "unk";
        field9279 = "unk";
        field9280 = "This is a utility class and cannot be instantiated";
        ServerDetector.method2683();
    }
}



