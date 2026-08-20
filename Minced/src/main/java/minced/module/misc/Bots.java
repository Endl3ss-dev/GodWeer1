/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  sLM.4ZXYEo
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.lang.invoke.CallSite;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.BotAction;
import minced.core.GroupSetting;
import minced.core.ServiceRegistry;
import minced.core.BotManager;
import minced.core.EventUpdate;
import minced.core.BotSession;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import sLM._4ZXYEo;

public final class Bots
extends Module
{
    static long cfr_renamed_180;
    static String cfr_renamed_375;

    static String field4628;
    MultiBooleanSetting field4629 = new MultiBooleanSetting(field4637, new BooleanSetting(field4638, true), new BooleanSetting(field4639, true));
    static String field4630;
    int field4631 = -1;
    static Object field4632;
    static String field4633;
    static DateTimeFormatter field4634;
    static String field4635;
    static String field4636;
    static String field4637;
    static String field4638;
    static String field4639;
    static String field4640;
    BooleanSetting field4641 = new BooleanSetting(field4640, true);
    static String field4642;
    GroupSetting field4643 = new GroupSetting(field4642);
    static String field4644;
    static float field4645;
    static float field4646;
    static float field4647;
    SliderSetting field4648 = new SliderSetting(field4644, field4645, field4646, field4647, 1.0f);
    static String field4649;
    BooleanSetting field4650 = new BooleanSetting(field4649, true);
    static String field4651;
    BooleanSetting field4652 = new BooleanSetting(field4651, true);
    static String field4653;
    BooleanSetting field4654 = new BooleanSetting(field4653, true);
    static String field4655;
    static String field4656;
    static String field4657;
    static String field4658;
    static String field4659;
    static String field4660;
    static String field4661;
    static long field4662;
    static float field4663;

    @Override
    public void onDisable() {
        BotManager v1 = ServiceRegistry.method2116();
        if (v1 != null && !v1.method2145().isEmpty()) {
            v1.method2129(field4628);
        }
        super.onDisable();
    }

    public static boolean method1162() {
        Bots v0 = Bots.method1171();
        Boolean v1 = v0 == null ? null : v0.field4629.method1898(field4630);
        return v0 == null || Boolean.TRUE.equals(v1);
    }

    @EventHandler(2)
    public void method1163(EventUpdate param1) {
        if (this.field4631 > 0) {
            --this.field4631;
            return;
        }
        if (this.field4631 == 0) {
            this.field4631 = -1;
            BotManager v2 = ServiceRegistry.method2116();
            if (v2 == null) {
                return;
            }
            for (BotSession v4 : (Iterable<BotSession>)(v2.method2145())) {
                BotAction v5;
                if (v4 == null || (v5 = v4.method56()) == null) continue;
                v4.method60(v5);
            }
        }
    }

    public static void method1164() {
        field4632 = new Object();
        field4634 = DateTimeFormatter.ofPattern(field4633);
    }

    public Bots() {
        super(field4635, field4636, Combat.Miscellaneous);
        this.field4643.method1989(field4655);
        this.addSettings(this.field4629, this.field4641, this.field4650, this.field4643, this.field4648, this.field4652, this.field4654);
    }

    public static boolean method1165(String param0) {
        return param0 != null && (param0.contains(field4656) || param0.contains(field4657));
    }

    public static boolean method1166() {
        Bots v0 = Bots.method1171();
        Boolean v1 = v0 == null ? null : v0.field4629.method1898(field4658);
        return v0 == null || Boolean.TRUE.equals(v1);
    }

    public static boolean method1167() {
        Bots v0 = Bots.method1171();
        return v0 == null || (Boolean)v0.field4654.getValue() != false;
    }

    public static boolean method1168() {
        Bots v0 = Bots.method1171();
        return v0 == null || (Boolean)v0.field4652.getValue() != false;
    }

    public static void method1169(String param0, String param1) {
        if (param0 == null || param0.isBlank() || param1 == null || param1.isBlank()) {
            return;
        }
        String v2 = LocalDateTime.now().format(field4634);
        String v3 = _4ZXYEo.ЦФ("makeConcatWithConstants", new Object[]{}, (String)v2, (String)param0, (String)param1, (String)System.lineSeparator());
        Path v4 = Path.of(field4659, new String[0]);
        Path v5 = v4.resolve(field4660);
        Object v6 = field4632;
        synchronized (field4632) {
            try {
                if (!Files.exists(v4, new LinkOption[0])) {
                    Files.createDirectories(v4, new FileAttribute[0]);
                }
                Files.write(v5, ((String)((Object)v3)).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
            catch (java.io.IOException v7) {
                // Diagnostic log only; a failed append must not break bot connection handling.
            }
            // ** MonitorExit[v6] (shouldn't be in output)
            return;
        }
    }

    public static String method1170() {
        String v1;
        Bots v0 = Bots.method1171();
        String string = v1 = v0 == null ? "" : (String)v0.field4643.getValue();
        if (v0 == null || v1 == null || v1.isBlank()) {
            return field4661;
        }
        return v1.trim();
    }

    private static Bots method1171() {
        Bots v1;
        if (Minced.getInstance() == null || Minced.getInstance().getModuleManager() == null) {
            return null;
        }
        Module v0 = Minced.getInstance().getModuleManager().method187(Bots.class);
        return v0 instanceof Bots ? (v1 = (Bots)v0) : null;
    }

    public static long method1172() {
        Bots v0 = Bots.method1171();
        if (v0 == null) {
            return field4662;
        }
        return Math.max(0L, (long)Math.round(((Float)v0.field4648.getValue()).floatValue() * field4663));
    }

    public static boolean method1173() {
        Bots v0 = Bots.method1171();
        return v0 == null || (Boolean)v0.field4650.getValue() != false;
    }

    public static void method1174() {
        Bots v0 = Bots.method1171();
        if (v0 != null) {
            v0.field4631 = 20;
        }
    }

    public static boolean method1175() {
        Bots v0 = Bots.method1171();
        return v0 == null || (Boolean)v0.field4641.getValue() != false;
    }

    static {
        cfr_renamed_180 = 0L;
        field4635 = "Bots";
        field4636 = "Боты и их глобальные настройки";
        field4637 = "Отображать";
        field4638 = "Чат";
        field4639 = "БоссБар";
        field4640 = "Позволяет ботам двигаться";
        field4642 = "Ник бота";
        field4644 = "Задержка сек";
        field4645 = 8.0f;
        field4646 = 3.0f;
        field4647 = 60.0f;
        field4649 = "Спуфер ресурспака";
        field4651 = "Закрывать меню RW";
        field4653 = "Переключать экран";
        field4655 = "Rickstone";
        field4628 = "Bots module disabled";
        field4630 = "Чат";
        field4658 = "БоссБар";
        field4661 = "Rickstone";
        field4662 = 8000L;
        field4663 = 1000.0f;
        field4656 = "ꈁꀀꈂꌁꈂꀁ§0ꈃꄀ";
        field4657 = "ꈁꀀꈂꌁꈂꀁꈃꄀ";
        cfr_renamed_375 = "[\u0001] \u0001 -> \u0001\u0001";
        field4659 = "MincedBots";
        field4660 = "bot_connections.txt";
        field4633 = "yyyy-MM-dd HH:mm:ss";
        Bots.method1164();
    }
}



