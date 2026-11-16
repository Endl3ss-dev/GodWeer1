/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2561
 *  net.minecraft.class_2596
 *  net.minecraft.class_266
 *  net.minecraft.class_268
 *  net.minecraft.class_269
 *  net.minecraft.class_270
 *  net.minecraft.class_2761
 *  net.minecraft.class_3532
 *  net.minecraft.class_8646
 *  net.minecraft.class_9011
 *  org.apache.commons.lang3.StringUtils
 */
package ru.decide.utils.math;

import java.lang.runtime.SwitchBootstraps;
import java.util.Objects;
import lombok.Generated;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_266;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_270;
import net.minecraft.class_2761;
import net.minecraft.class_3532;
import net.minecraft.class_8646;
import net.minecraft.class_9011;
import org.apache.commons.lang3.StringUtils;
import ru.decide.manager.event_impl.EventPacket;
import ru.decide.utils.annotation.IMinecraft;
import ru.decide.utils.taskript.StopWatch;

@Environment(value=EnvType.CLIENT)
public final class ServerUtil
implements IMinecraft {
    private static final StopWatch pvpWatch = new StopWatch();
    public static String server = "Local   ";
    public static float TPS = 20.0f;
    public static long timestamp;
    public static int anarchy;
    public static boolean pvpEnd;

    public static void tick() {
        anarchy = ServerUtil.getAnarchyMode();
        server = ServerUtil.getServer();
        pvpEnd = ServerUtil.inPvpEnd();
        if (ServerUtil.inPvp()) {
            pvpWatch.reset();
        }
    }

    public static void packet(EventPacket e) {
        class_2596<?> class_25962 = e.getPacket();
        Objects.requireNonNull(class_25962);
        class_2596<?> class_25963 = class_25962;
        int n = 0;
        switch (SwitchBootstraps.typeSwitch("typeSwitch", new Object[]{class_2761.class}, class_25963, n)) {
            case 0: {
                class_2761 time = (class_2761)class_25963;
                long nanoTime = System.nanoTime();
                float maxTPS = 20.0f;
                float rawTPS = maxTPS * (1.0E9f / (float)(nanoTime - timestamp));
                TPS = class_3532.method_15363((float)rawTPS, (float)0.0f, (float)maxTPS);
                timestamp = nanoTime;
                break;
            }
        }
    }

    public static boolean nullCheck() {
        return ServerUtil.mc.field_1724 == null || ServerUtil.mc.field_1687 == null;
    }

    private static String getServer() {
        if (ServerUtil.nullCheck() || mc.method_1562() == null || mc.method_1562().method_45734() == null || mc.method_1562().method_52790() == null) {
            return "Vanilla";
        }
        String serverIp = ServerUtil.mc.method_1562().method_45734().field_3761.toLowerCase();
        String brand = mc.method_1562().method_52790().toLowerCase();
        if (brand.contains("botfilter")) {
            return "FunTime";
        }
        if (brand.contains("\u00a76spooky\u00a7ccore")) {
            return "SpookyTime";
        }
        if (brand.contains("holyworld") || brand.contains("vk.com/idwok")) {
            return "HolyWorld";
        }
        if (serverIp.contains("reallyworld")) {
            return "ReallyWorld";
        }
        return serverIp;
    }

    private static int getAnarchyMode() {
        class_269 scoreboard = ServerUtil.mc.field_1687.method_8428();
        class_266 objective = scoreboard.method_1189(class_8646.field_45157);
        switch (server) {
            case "FunTime": {
                String[] string;
                if (objective == null || (string = objective.method_1114().getString().split("-")).length <= 1) break;
                return Integer.parseInt(string[1]);
            }
            case "HolyWorld": {
                for (class_9011 scoreboardEntry : scoreboard.method_1184(objective)) {
                    String string;
                    String text = class_268.method_1142((class_270)scoreboard.method_1164(scoreboardEntry.comp_2127()), (class_2561)scoreboardEntry.method_55387()).getString();
                    if (text.isEmpty() || (string = StringUtils.substringBetween((String)text, (String)"#", (String)" -\u25c6-")) == null || string.isEmpty()) continue;
                    return Integer.parseInt(string.replace(" 1.20", ""));
                }
                break;
            }
        }
        return -1;
    }

    public static boolean isPvp() {
        return !pvpWatch.finished(500.0);
    }

    private static boolean inPvp() {
        return ServerUtil.mc.field_1705.method_1740().field_2060.values().stream().map(c -> c.method_5414().getString().toLowerCase()).anyMatch(s -> s.contains("pvp") || s.contains("\u043f\u0432\u043f"));
    }

    private static boolean inPvpEnd() {
        return ServerUtil.mc.field_1705.method_1740().field_2060.values().stream().map(c -> c.method_5414().getString().toLowerCase()).anyMatch(s -> !(!s.contains("pvp") && !s.contains("\u043f\u0432\u043f") || !s.contains("0") && !s.contains("1")));
    }

    public static String getWorldType() {
        return ServerUtil.mc.field_1687.method_27983().method_29177().method_12832();
    }

    public static boolean isCopyTime() {
        return server.equals("SpookyTime");
    }

    public static boolean isFunTime() {
        return server.equals("FunTime");
    }

    public static boolean isReallyWorld() {
        return server.equals("ReallyWorld");
    }

    public static boolean isHolyWorld() {
        return server.equals("HolyWorld");
    }

    public static boolean isVanilla() {
        return server.equals("Vanilla");
    }

    @Generated
    private ServerUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    @Generated
    public static int getAnarchy() {
        return anarchy;
    }

    @Generated
    public static boolean isPvpEnd() {
        return pvpEnd;
    }
}

