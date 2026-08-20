/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.objects.ObjectCollection
 *  it.unimi.dsi.fastutil.objects.ObjectIterable
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 *  net.minecraft.client.network.ClientPlayerEntity
 *  net.minecraft.client.world.ClientWorld
 *  net.minecraft.entity.Entity
 *  net.minecraft.network.listener.PacketListener
 *  net.minecraft.network.packet.Packet
 *  sLM.4ZXYEo
 */
package minced.core;

import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import minced.core.RecordClass229;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import sLM._4ZXYEo;

public final class LogUtil {
    static String 诶М;
    static String 诶с;
    static String 诶О;
    static String 诶С;
    static String 诶П;
    static String 诶弟;
    static String 诶必;

    static String field8393;
    static String field8394;
    static String field8395;
    static String field8396;
    static boolean field8397;
    static String field8398;
    static String field8399;
    static String field8400;
    static String field8401;
    static Map<String, Long> field8402;
    static String field8403 = "field8403";
    static String field8404;
    static String field8405;
    static String field8406;
    static String field8407;

    public static String method2266(Entity param0) {
        if (param0 == null) {
            return field8393;
        }
        String v1 = param0.getWorld() != null ? param0.getWorld().getRegistryKey().getValue().toString() : field8395;
        return _4ZXYEo.и7("makeConcatWithConstants", new Object[]{}, (String)param0.getClass().getSimpleName(), (int)param0.getId(), (String)LogUtil.method2267(param0.getX()), (String)LogUtil.method2267(param0.getY()), (String)LogUtil.method2267(param0.getZ()), (String)LogUtil.method2267(param0.getYaw()), (String)LogUtil.method2267(param0.getPitch()), (String)v1);
    }

    private static String method2267(double param0) {
        return String.format(Locale.ROOT, field8396, param0);
    }

    public static boolean method2268() {
        return field8397;
    }

    public static String method2269(MinecraftClient param0) {
        if (param0 == null) {
            return field8398;
        }
        return _4ZXYEo.иБ("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2278(param0.currentScreen), (String)LogUtil.method2279(param0.world), (String)LogUtil.method2266((Entity)param0.player), (String)LogUtil.method2266(param0.getCameraEntity()), (String)LogUtil.method2266(param0.targetedEntity));
    }

    public static String method2270(RecordClass229 param0) {
        if (param0 == null) {
            return field8399;
        }
        return _4ZXYEo.иГ("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2279(param0.world()), (String)LogUtil.method2266((Entity)param0.player()), (String)LogUtil.method2266(param0.cameraEntity()), (String)LogUtil.method2266(param0.targetedEntity()));
    }

    public static String method2271(PacketListener param0) {
        if (param0 == null) {
            return field8400;
        }
        return _4ZXYEo.иl("makeConcatWithConstants", new Object[]{}, (String)param0.getClass().getSimpleName(), (String)String.valueOf(param0.getPhase()));
    }

    public static String method2272(ClientPlayerEntity param0) {
        if (param0 == null) {
            return field8401;
        }
        return _4ZXYEo.иЭ("makeConcatWithConstants", new Object[]{}, (String)LogUtil.method2266((Entity)param0), (boolean)param0.isOnGround(), (boolean)param0.isSprinting(), (boolean)param0.isSneaking(), (boolean)param0.horizontalCollision);
    }

    public static void method2273(boolean param0) {
        field8397 = param0;
    }

    private LogUtil() {
    }

    private static String method2274(String param0) {
        return param0.replace('\n', ' ').replace('\r', ' ').trim();
    }

    public static void method2275(String param0, long param1, String param3, String param4) {
        if (!field8397) {
            return;
        }
        long v5 = System.currentTimeMillis();
        Long v7 = field8402.putIfAbsent(param0, v5);
        if (v7 != null && v5 - v7 < param1) {
            return;
        }
        field8402.put(param0, v5);
        LogUtil.cfr_underscore(param3, param4);
    }

    public static void method2276() {
        field8402 = new ConcurrentHashMap();
        field8397 = Boolean.parseBoolean(System.getProperty(field8403, field8404));
    }

    public static String method2277(Packet param0) {
        return param0 == null ? field8405 : param0.getClass().getSimpleName();
    }

    public static void cfr_underscore(String param0, String param1) {
        if (!field8397) {
            return;
        }
        System.out.println((String)((Object)_4ZXYEo.и6("makeConcatWithConstants", new Object[]{}, (String)param0, (String)param1)));
    }

    public static String method2278(Screen param0) {
        String v1;
        if (param0 == null) {
            return field8406;
        }
        String string = v1 = param0.getTitle() != null ? param0.getTitle().getString() : "";
        if (v1 == null || v1.isBlank()) {
            return param0.getClass().getSimpleName();
        }
        return _4ZXYEo.иб("makeConcatWithConstants", new Object[]{}, (String)param0.getClass().getSimpleName(), (String)LogUtil.method2274(v1));
    }

    public static String method2279(ClientWorld param0) {
        if (param0 == null) {
            return field8407;
        }
        return param0.getRegistryKey().getValue().toString();
    }

    static {
        field8397 = false;
        诶М = "[BotTrace][\u0001] \u0001";
        field8398 = "client=null";
        诶с = "screen=\u0001, world=\u0001, player=\u0001, camera=\u0001, targeted=\u0001";
        field8399 = "snapshot=null";
        诶О = "world=\u0001, player=\u0001, camera=\u0001, targeted=\u0001";
        field8405 = "packet=null";
        field8400 = "listener=null";
        诶С = "\u0001(phase=\u0001)";
        field8406 = "null";
        诶П = "\u0001(title=\u0001)";
        field8407 = "null";
        field8393 = "null";
        field8394 = "null";
        field8395 = "null";
        诶弟 = "\u0001#\u0001@(\u0001,\u0001,\u0001)/yaw=\u0001/pitch=\u0001/world=\u0001";
        field8401 = "player=null";
        诶必 = "\u0001, onGround=\u0001, sprinting=\u0001, sneaking=\u0001, horizontalCollision=\u0001";
        field8396 = "%.3f";
        field8404 = "false";
        LogUtil.method2276();
    }
}



