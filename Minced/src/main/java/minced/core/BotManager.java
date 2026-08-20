/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.client.network.ServerAddress
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.hit.HitResult
 *  sLM.4ZXYEo
 *  sg.ec.2西$2
 *  sg.ec.2西$Т
 *  sg.ec.7
 *  sg.ec.sp
 *  sg.ec.tp
 *  sg.ec.fh
 *  sg.ec.fyu
 *  sg.ec.chch
 *  sg.ec.chh
 */
package minced.core;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import minced.core.BotSupplier;
import minced.core.BotAction;
import minced.core.BotTimings;
import minced.core.ServiceRegistry;
import minced.core.BotView;
import minced.core.LogUtil;
import minced.core.EventUpdate;
import minced.core.AltManagerCardState;
import minced.core.BotContext;
import minced.core.CommandSourceUtil;
import minced.core.BotSession;
import minced.core.BlockPosUtil;
import minced.core.RecordClass232;
import minced.core.RecordClass30;
import minced.core.RecordClass61;
import minced.enums.HEADLESS;
import minced.module.misc.Bots;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import sLM._4ZXYEo;
import sg.ec._2Xi;
import sg.ec._7;
import sg.ec.sp;
import sg.ec.tp;
import sg.ec.fh;
import sg.ec.fyu;
import sg.ec.chch;
import sg.ec.chh;

public final class BotManager
implements BotSupplier {
    static String 衣Ъ;
    static String 衣Ы;
    static String Вн;
    static String ВЭ;
    static String В2;
    static String Вм;
    static String ВЩ;
    static String ВР;
    static String ВУ;
    static String ВФ;
    static String ВМ;
    static String ВО;
    static String ВД;
    static String ВС;
    static String ВП;
    static String ВO;
    static String ВЙ;
    static String В弟;
    static String Ву;
    static String ВБ;
    static String Вх;
    static String ВХ;
    static String Вг;
    static String Вю;

    boolean field8134;
    String field8135;
    static String field8136;
    List<BotTimings> field8137;
    List<BotSession> field8138 = new CopyOnWriteArrayList<>();
    static String field8139;
    static String field8140;
    static String field8141;
    Map<String, BotSession> field8142 = new ConcurrentHashMap<>();
    Map<String, Object> field8143 = new ConcurrentHashMap<>();
    Map<String, Object> field8144 = new ConcurrentHashMap<>();
    Map<String, String> field8145 = new ConcurrentHashMap<>();
    Map<String, HEADLESS> field8146 = new ConcurrentHashMap<>();
    Map<String, BlockPosUtil> field8147 = new ConcurrentHashMap<>();
    Map<String, Object> O = new ConcurrentHashMap<>();
    Map<String, Object> field8148 = new ConcurrentHashMap<>();
    Map<String, Object> field8149 = new ConcurrentHashMap<>();
    Map<String, _2Xi.t2> field8150 = new ConcurrentHashMap<>();
    Map<String, Object> field8151 = new ConcurrentHashMap<>();
    Set<String> field8152 = ConcurrentHashMap.newKeySet();
    Set<String> field8153 = ConcurrentHashMap.newKeySet();
    boolean field8154;
    static float field8155;
    static float field8156;
    static String field8157;
    static String field8158;
    static String field8159;
    static String field8160;
    static String field8161;
    static String field8162;
    static String field8163;
    static String field8164;
    AtomicInteger field8165;
    static String field8166 = "/spawn";
    String field8167;
    static long field8168 = 3L;
    long field8169;
    static long field8170 = 3L;
    long field8171;
    static long field8172 = 500L;
    long field8173;
    boolean field8174 = true;
    static String field8175 = "Rick123";
    String field8176;
    static String field8177;
    static String field8178;
    boolean field8179;
    static String field8180;
    static String field8181;
    static String field8182;
    static long field8183;
    static String field8184;
    static String field8185;
    static String field8186;
    static String field8187;
    static String field8188;
    static String field8189;
    static String field8190;
    static String field8191;
    static String field8192;
    static String field8193;
    static String field8194;
    boolean field8195;
    static String field8196;
    static String field8197;
    long field8198;
    long field8199;
    static String field8200;
    static String field8201;
    static String field8202;
    static String field8203;
    static String field8204;
    static String field8205;
    static String field8206;
    static String field8207;
    static String field8208;
    static String field8209;
    static String field8210;
    static String field8211;
    static long field8212;
    static String field8213;
    static String field8214;
    static String field8215;
    static String field8216;
    static String field8217;
    static String field8218;
    static String field8219;
    static String field8220;
    static String field8221;
    static String field8222;
    static String field8223;
    static String field8224;
    static long field8225;
    static long field8226;
    static String field8227;

    private static /* synthetic */ AltManagerCardState method2120(String param0) {
        return new AltManagerCardState();
    }

    @Override
    public Object get() {
        return this;
    }

    public boolean method2121() {
        return this.field8134;
    }

    public void O(boolean param1) {
        this.field8134 = param1;
    }

    public void method2122(String param1) {
        if (param1 == null || param1.isBlank()) {
            return;
        }
        this.field8135 = param1.trim();
        LogUtil.cfr_underscore(field8136, (String)((Object)_4ZXYEo.ЧД("makeConcatWithConstants", new Object[]{}, (String)this.field8135)));
    }

    public void method2123() {
        this.field8137.clear();
    }

    private void method2124() {
        for (BotSession v2 : List.copyOf(this.field8138)) {
            if (!v2.method58()) {
                LogUtil.cfr_underscore(field8139, (String)((Object)_4ZXYEo.ЧМ("makeConcatWithConstants", new Object[]{}, (String)v2.method62(), (String)v2.method67())));
                this.method2140(v2);
                continue;
            }
            v2.method63();
            if (!v2.method58()) {
                LogUtil.cfr_underscore(field8140, (String)((Object)_4ZXYEo.chO("makeConcatWithConstants", new Object[]{}, (String)v2.method62(), (String)v2.method67())));
                this.method2140(v2);
                continue;
            }
            if (!v2.method65()) continue;
            this.cfr_renamed_4(v2);
        }
    }

    @Override
    public void method1860() {
        BotSupplier.super.method1860();
        this.method2144();
        this.method2129(field8141);
        this.field8138.clear();
        this.field8142.clear();
        this.field8143.clear();
        this.field8144.clear();
        this.field8145.clear();
        this.field8146.clear();
        this.field8147.clear();
        this.O.clear();
        this.field8148.clear();
        this.field8149.clear();
        this.field8150.clear();
        this.field8151.clear();
        this.field8152.clear();
        this.field8153.clear();
        this.field8137.clear();
        this.field8154 = false;
        this.field8135 = null;
    }

    public BotSession method2126(String param1) {
        if (param1 == null || param1.isBlank()) {
            return null;
        }
        return (BotSession)this.field8142.get(BotManager.method2163(param1));
    }

    static String method2127(String param0) {
        String string;
        if (param0 == null || param0.isBlank()) {
            return field8157;
        }
        String v1 = param0.toLowerCase(Locale.ROOT);
        int v2 = -1;
        switch (v1.hashCode()) {
            case 103669: {
                if (!v1.equals(field8158)) break;
                v2 = 0;
                break;
            }
            case 1503846: {
                if (!v1.equals(field8159)) break;
                v2 = 1;
                break;
            }
            case 109638523: {
                if (!v1.equals(field8160)) break;
                v2 = 2;
                break;
            }
            case 1455208620: {
                if (!v1.equals(field8161)) break;
                v2 = 3;
            }
        }
        switch (v2) {
            case 0: 
            case 1: {
                string = field8162;
                break;
            }
            case 2: 
            case 3: {
                string = field8163;
                break;
            }
            default: {
                string = field8164;
            }
        }
        return string;
    }

    public void method2128(String param1) {
        if (param1 == null || param1.isBlank()) {
            return;
        }
        BlockPosUtil v2 = (BlockPosUtil)this.field8147.remove(BotManager.method2163(param1));
        if (v2 != null) {
            v2.method427();
        }
    }

    public BotManager() {
        this.field8137 = new CopyOnWriteArrayList();
        this.field8165 = new AtomicInteger(1);
        this.field8167 = field8166;
        this.field8169 = TimeUnit.MINUTES.toMillis(field8168);
        this.field8171 = TimeUnit.MINUTES.toMillis(field8170);
        this.field8173 = field8172;
        this.field8176 = field8175;
    }

    public void method2129(String param1) {
        for (BotSession v3 : List.copyOf(this.field8138)) {
            if (v3 != null && v3.method62() != null && !v3.method62().isBlank()) {
                this.field8153.add(BotManager.method2163(v3.method62()));
            }
            v3.method64(param1);
            this.method2140(v3);
        }
    }

    public long method2130() {
        return this.field8169;
    }

    public int cfr_renamed_4(String param1) {
        if (param1 == null || param1.isBlank()) {
            return 100;
        }
        RecordClass30 v2 = (RecordClass30)this.O.get(BotManager.method2163(param1));
        return v2 != null ? v2.delayTicks() : 100;
    }

    public String method2131() {
        return this.field8176;
    }

    public String method2132(String param1) {
        if (param1 == null || param1.isBlank()) {
            return field8177;
        }
        BotSession v2 = this.method2126(param1);
        if (v2 == null) {
            return _4ZXYEo.Чя("makeConcatWithConstants", new Object[]{}, (String)param1);
        }
        if (v2 instanceof _7) {
            return null;
        }
        if (v2 instanceof chch) {
            chch v3 = (chch)v2;
            return this.method2181(v2.method62(), v3.必(), HEADLESS.RENDERABLE);
        }
        return field8178;
    }

    private void method2133() {
        if (!this.field8179 || this.field8167 == null || this.field8167.isBlank()) {
            this.method2188();
            return;
        }
        long v1 = System.currentTimeMillis();
        for (BotSession v4 : List.copyOf(this.field8138)) {
            String v5;
            Long v6;
            if (!v4.method58()) {
                LogUtil.cfr_underscore(field8180, (String)((Object)_4ZXYEo.Чь("makeConcatWithConstants", new Object[]{}, (String)v4.method62(), (String)v4.method67())));
                this.method2140(v4);
                continue;
            }
            if (!v4.method65() || (v6 = (Long)this.field8144.get(v5 = BotManager.method2163(v4.method62()))) != null && v1 - v6 < this.field8169) continue;
            v4.method59(this.field8167);
            this.field8144.put(v5, v1);
        }
        this.method2188();
    }

    public String method2134() {
        return this.field8167;
    }

    public boolean method2135(String param1, boolean param2) {
        if (param1 == null || param1.isBlank()) {
            return false;
        }
        String v3 = BotManager.method2163(param1);
        if (this.method2126(param1) == null && !this.field8143.containsKey(v3)) {
            return false;
        }
        if (param2) {
            this.field8152.add(v3);
        } else {
            this.field8152.remove(v3);
        }
        return true;
    }

    public void method2136(boolean param1) {
        this.field8179 = param1;
        if (param1) {
            this.field8144.clear();
        }
    }

    public RecordClass232 method2137(String param1) {
        if (param1 == null || param1.isBlank()) {
            return new RecordClass232(false, field8181);
        }
        RecordClass232 v2 = (RecordClass232)this.field8148.get(BotManager.method2163(param1));
        return v2 != null ? v2 : new RecordClass232(false, field8182);
    }

    public boolean method2138(String param1) {
        if (param1 == null || param1.isBlank()) {
            return false;
        }
        RecordClass30 v2 = (RecordClass30)this.O.get(BotManager.method2163(param1));
        return v2 != null && v2.enabled();
    }

    public boolean method2139() {
        return this.field8179;
    }

    public void method2140(BotSession param1) {
        if (param1 == null) {
            return;
        }
        this.field8138.remove(param1);
        if (param1.method62() != null && !param1.method62().isBlank()) {
            String v2 = BotManager.method2163(param1.method62());
            this.field8142.remove(v2, param1);
            this.field8144.remove(v2);
            this.field8151.remove(v2);
            boolean v3 = this.field8153.remove(v2);
            if (this.method2178(param1, v2, v3)) {
                String v4 = this.field8145.getOrDefault(v2, param1.method66());
                HEADLESS v5 = this.field8146.getOrDefault(v2, param1 instanceof chch ? HEADLESS.HEADLESS : HEADLESS.RENDERABLE);
                RecordClass30 v6 = (RecordClass30)this.O.get(v2);
                long v7 = System.currentTimeMillis() + (long)v6.delayTicks() * field8183;
                this.field8150.put(v2, new _2Xi.t2(param1.method62(), v4, v5, v7));
                LogUtil.cfr_underscore(field8184, (String)((Object)_4ZXYEo.chh("makeConcatWithConstants", new Object[]{}, (String)param1.method62(), (int)v6.delayTicks(), (String)v4, (String)v5.method173())));
            } else {
                this.field8150.remove(v2);
            }
        }
        this.method2146(param1);
    }

    public String method2141() {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        return v1 != null ? v1.method2214() : null;
    }

    private void method2142(BotSession param1) {
        if (param1 == null || param1.method62() == null || param1.method62().isBlank()) {
            return;
        }
        if (!this.field8152.contains(BotManager.method2163(param1.method62()))) {
            return;
        }
        fh.з((BotSession)param1, param0 -> {
            if (param0.player().isUsingItem()) {
                return;
            }
            if (param0.player().getAttackCooldownProgress(field8155) <= field8156) {
                return;
            }
            HitResult v1 = param0.client().crosshairTarget;
            if (v1 == null) {
                return;
            }
            switch (v1.getType()) {
                case ENTITY: {
                    if (param0.client().targetedEntity == null) break;
                    param0.interactionManager().attackEntity((PlayerEntity)param0.player(), param0.client().targetedEntity);
                    param0.player().swingHand(Hand.MAIN_HAND);
                    break;
                }
                case BLOCK: {
                    BlockHitResult v2 = (BlockHitResult)v1;
                    param0.interactionManager().attackBlock(v2.getBlockPos(), v2.getSide());
                    param0.interactionManager().updateBlockBreakingProgress(v2.getBlockPos(), v2.getSide());
                    param0.player().swingHand(Hand.MAIN_HAND);
                    break;
                }
                default:
                    break;
            }
        });
    }

    public boolean method2143(String param1, String param2) {
        BotSession v3 = this.method2126(param1);
        if (v3 == null) {
            return false;
        }
        this.field8153.add(BotManager.method2163(v3.method62()));
        v3.method64(param2);
        this.method2140(v3);
        return true;
    }

    public boolean method2144() {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v1 == null) {
            return false;
        }
        this.field8135 = null;
        LogUtil.cfr_underscore(field8185, (String)((Object)_4ZXYEo.ЧШ("makeConcatWithConstants", new Object[]{}, (String)v1.method2214())));
        return v1.method2204();
    }

    public List method2145() {
        return List.copyOf(this.field8138);
    }

    private void method2146(BotSession param1) {
        String v3;
        if (param1 == null || param1.method62() == null || param1.method62().isBlank()) {
            return;
        }
        BotView v2 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v2 != null) {
            v2.method2215(param1.method62());
        }
        if ((v3 = this.method2141()) != null && BotManager.method2163(v3).equals(BotManager.method2163(param1.method62()))) {
            this.method2144();
        }
        if (this.field8135 != null && BotManager.method2163(this.field8135).equals(BotManager.method2163(param1.method62()))) {
            this.field8135 = null;
        }
    }

    public void method2147(String param1) {
        if (param1 != null && !param1.isBlank()) {
            this.field8176 = param1;
        }
    }

    private static boolean method2148(BotSession param0, HEADLESS param1) {
        boolean bl;
        if (param0 == null || param1 == null) {
            return false;
        }
        switch (param1) {
            default: {
                throw new MatchException(null, null);
            }
            case RENDERABLE: {
                bl = param0 instanceof _7;
                break;
            }
            case HEADLESS: {
                bl = param0 instanceof chch;
            }
        }
        return bl;
    }

    public boolean method2149(int param12) {
        return this.field8137.removeIf(param1 -> param1.method195() == param12);
    }

    public BotAction method2150(String param1) {
        if (param1 == null || param1.isBlank()) {
            return null;
        }
        return (BotAction)this.field8143.get(BotManager.method2163(param1));
    }

    public String method2151(String param1) {
        BotContext v4;
        if (param1 == null || param1.isBlank()) {
            return field8186;
        }
        BotSession v2 = this.method2126(param1);
        if (v2 == null) {
            return _4ZXYEo.ЧЩ("makeConcatWithConstants", new Object[]{}, (String)param1);
        }
        if (v2 instanceof sp) {
            sp v3 = (sp)v2;
            v4 = v3.ь();
            if (!v2.method65() || v4 == null || !v4.isReadyForView()) {
                this.field8135 = v2.method62();
                LogUtil.cfr_underscore(field8189, (String)((Object)_4ZXYEo.ЧХ("makeConcatWithConstants", new Object[]{}, (String)v2.method62(), (boolean)v2.method65(), (v4 != null && v4.isReadyForView() ? 1 : 0) != 0)));
                return field8190;
            }
        } else {
            if (v2 instanceof chch) {
                return field8187;
            }
            return field8188;
        }
        BotView v5 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v5 == null) {
            return field8191;
        }
        if (!v5.method2194(v2.method62(), v4)) {
            LogUtil.cfr_underscore(field8192, (String)((Object)_4ZXYEo.chf("makeConcatWithConstants", new Object[]{}, (String)v2.method62())));
            return field8193;
        }
        this.field8135 = null;
        LogUtil.cfr_underscore(field8194, (String)((Object)_4ZXYEo.ЧН("makeConcatWithConstants", new Object[]{}, (String)v2.method62())));
        return null;
    }

    public void method2152(String param1, boolean param2, String param3) {
        if (param1 == null || param1.isBlank()) {
            return;
        }
        String v4 = BotManager.method2163(param1);
        this.field8149.remove(v4);
        if (!param2) {
            this.field8148.remove(v4);
            return;
        }
        this.field8148.put(v4, new RecordClass232(true, param3));
    }

    @EventHandler(2)
    public void method2153(EventUpdate param1) {
        this.method2168();
        this.method2157();
        this.method2133();
        this.method2184();
    }

    public boolean method2154() {
        return this.field8195;
    }

    public void method2155(String param1, String param2) {
        if (param1 == null || param1.isBlank() || param2 == null || param2.isBlank()) {
            return;
        }
        String v3 = BotManager.method2163(param1);
        String v4 = (String)this.field8151.get(v3);
        if (v4 == null) {
            return;
        }
        Long v5 = BotManager.method2173(param2);
        if (v5 == null) {
            return;
        }
        this.field8151.remove(v3);
        if (v5 <= 0L || v4.equalsIgnoreCase(param1)) {
            return;
        }
        BotSession v6 = this.method2126(param1);
        if (v6 == null || !v6.method65()) {
            return;
        }
        v6.method59((String)((Object)_4ZXYEo.ЧЯ("makeConcatWithConstants", new Object[]{}, (String)v4, (Long)v5)));
    }

    public boolean method2156(String param1, BotAction param2) {
        if (param1 == null || param1.isBlank() || param2 == null) {
            return false;
        }
        BotSession v3 = this.method2126(param1);
        if (v3 != null) {
            v3.method60(param2);
            this.field8143.remove(BotManager.method2163(param1));
            return true;
        }
        this.field8143.put(BotManager.method2163(param1), param2);
        return false;
    }

    private void method2157() {
        for (BotSession v2 : List.copyOf(this.field8138)) {
            if (v2.method58() && v2.method65()) {
                this.cfr_renamed_4(v2);
                boolean v3 = this.method2166(v2);
                if (!v3) {
                    this.method2142(v2);
                    BotAction v4 = v2.method56();
                    if (v4 != null) {
                        v4.method192(v2);
                    }
                }
            }
            if (v2.method58()) continue;
            LogUtil.cfr_underscore(field8196, (String)((Object)_4ZXYEo.ЧА("makeConcatWithConstants", new Object[]{}, (String)v2.method62(), (String)v2.method67())));
            this.method2140(v2);
        }
    }

    public BlockPosUtil method2158(String param1) {
        if (param1 == null || param1.isBlank()) {
            return null;
        }
        return this.field8147.computeIfAbsent(BotManager.method2163(param1), param0 -> new BlockPosUtil());
    }

    public void method2159(String param1, boolean param2, int param3) {
        if (param1 == null || param1.isBlank()) {
            return;
        }
        String v4 = BotManager.method2163(param1);
        if (!param2) {
            this.O.remove(v4);
            this.field8150.remove(v4);
            return;
        }
        this.O.put(v4, new RecordClass30(true, param3));
    }

    public int method2160(String param1) {
        if (param1 == null || param1.isBlank()) {
            return 0;
        }
        int v2 = 0;
        for (BotSession v4 : List.copyOf(this.field8138)) {
            if (!v4.method65()) continue;
            this.field8151.put(BotManager.method2163(v4.method62()), param1.trim());
            v4.method59(field8197);
            ++v2;
        }
        return v2;
    }

    public void method2161(boolean param1) {
        this.field8195 = param1;
        this.field8154 = false;
        this.field8198 = System.currentTimeMillis();
        this.field8199 = 0L;
        this.field8174 = true;
        this.method2189();
    }

    public BlockPosUtil method2162(String param1) {
        if (param1 == null || param1.isBlank()) {
            return null;
        }
        return (BlockPosUtil)this.field8147.get(BotManager.method2163(param1));
    }

    private static String method2163(String param0) {
        return param0.trim().toLowerCase(Locale.ROOT);
    }

    public String method2164(String param1, String param2) {
        return this.method2181(param1, param2, HEADLESS.RENDERABLE);
    }

    private static /* synthetic */ void method2165(AltManagerCardState param0, BotSession param1, RecordClass61 param2) {
        for (AbstractClientPlayerEntity v4 : param2.world().getPlayers()) {
            if (v4 == null || v4 == param2.player() || !fh.cfr_renamed_168((PlayerEntity)v4)) continue;
            param0.ШЕ = true;
            param0.Шш = 1;
            param0.ША = 0;
            param1.method60((BotAction)chh.н);
            param1.method59(field8200);
            break;
        }
    }

    private boolean method2166(BotSession param1) {
        if (param1 == null || param1.method62() == null || param1.method62().isBlank()) {
            return false;
        }
        String v2 = BotManager.method2163(param1.method62());
        RecordClass232 v3 = (RecordClass232)this.field8148.get(v2);
        if (v3 == null || !v3.enabled()) {
            this.field8149.remove(v2);
            return false;
        }
        AltManagerCardState v4 = (AltManagerCardState)this.field8149.computeIfAbsent(v2, param0 -> new AltManagerCardState());
        if (!v4.ШЕ) {
            fh.з((BotSession)param1, param2 -> BotManager.method2165(v4, param1, param2));
            return v4.ШЕ;
        }
        ++v4.ША;
        if (v4.Шш == 1 && v4.ША >= 40) {
            v4.Шш = 2;
            v4.ША = 0;
            String v5 = v3.action();
            if (v5.equals(field8201)) {
                param1.method59(field8203);
            } else if (v5.equals(field8202)) {
                param1.method59(field8204);
            } else {
                this.field8153.add(v2);
                param1.method64(field8205);
                this.method2140(param1);
                return true;
            }
            return true;
        }
        if (v4.Шш >= 2 && v4.ША >= 40) {
            this.field8149.remove(v2);
            return false;
        }
        return true;
    }

    public long method2167() {
        return this.field8171;
    }

    private void method2168() {
        long v1 = System.currentTimeMillis();
        for (Map.Entry v4 : List.copyOf(this.field8150.entrySet())) {
            _2Xi.t2 v5 = (_2Xi.t2)v4.getValue();
            if (v5 == null || v5.reconnectAtMs() > v1) continue;
            this.field8150.remove(v4.getKey(), v5);
            String v6 = this.method2181(v5.botName(), v5.address(), v5.mode());
            if (v6 == null) continue;
            LogUtil.cfr_underscore(field8206, (String)((Object)_4ZXYEo.ЧЕ("makeConcatWithConstants", new Object[]{}, (String)((String)v4.getKey()), (String)v6)));
        }
    }

    public void method2169(String param1) {
        if (param1 != null && !param1.isBlank()) {
            this.field8167 = param1;
        }
    }

    public boolean method2170(String param1, String param2) {
        BotSession v3 = this.method2126(param1);
        if (v3 == null) {
            return false;
        }
        v3.method57(param2);
        return true;
    }

    public int method2171(String param1) {
        int v2 = 0;
        for (BotSession v4 : List.copyOf(this.field8138)) {
            if (!v4.method65()) continue;
            v4.method57(param1);
            ++v2;
        }
        return v2;
    }

    public boolean method2172() {
        String v1 = this.method2141();
        if (v1 == null || v1.isBlank()) {
            return false;
        }
        BotSession v2 = this.method2126(v1);
        if (v2 == null) {
            return false;
        }
        BotAction v3 = v2.method56();
        return v3 != null && v3 != chh.н;
    }

    private static Long method2173(String param0) {
        if (param0 == null || param0.isBlank()) {
            return null;
        }
        String v1 = param0.toLowerCase(Locale.ROOT);
        if (!v1.contains(field8207) && !v1.contains(field8208)) {
            return null;
        }
        String v2 = param0.replaceAll(field8209, "");
        if (v2.isEmpty()) {
            return null;
        }
        return Long.parseLong(v2);
    }

    public void method2174(boolean param1) {
        this.field8135 = null;
        for (BotSession v3 : List.copyOf(this.field8138)) {
            if (v3 == null || v3.method62() == null || v3.method62().isBlank()) continue;
            this.field8153.add(BotManager.method2163(v3.method62()));
        }
        BotView v2 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v2 != null) {
            v2.method2203();
        }
        this.method2129(param1 ? field8210 : field8211);
    }

    /*
     * Enabled aggressive block sorting
     */
    private void cfr_renamed_4(BotSession param1) {
        String v2 = this.field8135;
        if (v2 == null || param1 == null || param1.method62() == null) {
            return;
        }
        if (!BotManager.method2163(v2).equals(BotManager.method2163(param1.method62()))) {
            return;
        }
        if (!(param1 instanceof sp)) {
            this.field8135 = null;
            return;
        }
        sp v3 = (sp)param1;
        BotContext v4 = v3.ь();
        if (!param1.method65() || v4 == null || !v4.isReadyForView()) {
            LogUtil.method2275((String)((Object)_4ZXYEo.chg("makeConcatWithConstants", new Object[]{}, (String)BotManager.method2163(param1.method62()))), field8212, field8213, (String)((Object)_4ZXYEo.chI("makeConcatWithConstants", new Object[]{}, (String)param1.method62(), (boolean)param1.method65(), (v4 != null && v4.isReadyForView() ? 1 : 0) != 0)));
            return;
        }
        BotView v5 = (BotView)ServiceRegistry.method2118(BotView.class);
        if (v5 != null && v5.method2194(param1.method62(), v4)) {
            this.field8135 = null;
            LogUtil.cfr_underscore(field8214, (String)((Object)_4ZXYEo.Чр("makeConcatWithConstants", new Object[]{}, (String)param1.method62())));
        }
    }

    public void method2177(BotSession param1) {
        if (param1 == null || param1.method62() == null || param1.method62().isBlank()) {
            return;
        }
        String v2 = BotManager.method2163(param1.method62());
        BotSession v3 = this.field8142.put(v2, param1);
        if (v3 != null && v3 != param1) {
            this.field8138.remove(v3);
        }
        if (!this.field8138.contains(param1)) {
            this.field8138.add(param1);
        }
        this.field8150.remove(v2);
        this.field8149.remove(v2);
        BotAction v4 = (BotAction)this.field8143.remove(v2);
        if (v4 != null) {
            param1.method60(v4);
        } else if (param1.method56() == null) {
            param1.method60((BotAction)chh.н);
        }
    }

    private boolean method2178(BotSession param1, String param2, boolean param3) {
        if (param1 == null || param2 == null || param3) {
            return false;
        }
        RecordClass30 v4 = (RecordClass30)this.O.get(param2);
        if (v4 == null || !v4.enabled()) {
            return false;
        }
        String v5 = this.field8145.getOrDefault(param2, param1.method66());
        if (v5 == null || v5.isBlank()) {
            return false;
        }
        String v6 = param1.method67();
        if (v6 == null) {
            return true;
        }
        String v7 = v6.toLowerCase(Locale.ROOT);
        return !v7.contains(field8215) && !v7.contains(field8216) && !v7.contains(field8217) && !v7.contains(field8218);
    }

    public void method2179() {
        this.method2124();
    }

    public List method2180() {
        return List.copyOf(this.field8137);
    }

    public String method2181(String param1, String param2, HEADLESS param3) {
        _7 v0;
        if (param1 == null || param1.isBlank()) {
            return field8219;
        }
        if (param2 == null || param2.isBlank()) {
            return field8220;
        }
        String v4 = param1.trim();
        String v5 = param2.trim();
        if (!ServerAddress.isValid((String)v5)) {
            return field8221;
        }
        HEADLESS v6 = param3 != null ? param3 : HEADLESS.RENDERABLE;
        String v7 = BotManager.method2163(v4);
        this.field8145.put(v7, v5);
        this.field8146.put(v7, v6);
        this.field8150.remove(v7);
        this.field8153.remove(v7);
        BotSession v8 = this.method2126(v4);
        if (v8 != null) {
            if (v8.method58() && BotManager.method2148(v8, v6)) {
                return field8222;
            }
            if (v8.method58()) {
                this.field8153.add(v7);
                v8.method64((String)((Object)_4ZXYEo.Ч1("makeConcatWithConstants", new Object[]{}, (String)v6.method173())));
            }
            this.method2140(v8);
        }
        Bots.method1169(v4, v5);
        if (v6 == HEADLESS.RENDERABLE) {
            this.method2122(v4);
        } else if (this.field8135 != null && BotManager.method2163(this.field8135).equals(v7)) {
            this.field8135 = null;
            LogUtil.cfr_underscore(field8223, (String)((Object)_4ZXYEo.Ч西("makeConcatWithConstants", new Object[]{}, (String)v4)));
        }
        LogUtil.cfr_underscore(field8224, (String)((Object)_4ZXYEo.Чщ("makeConcatWithConstants", new Object[]{}, (String)v4, (String)v5, (String)v6.method173(), (String)this.field8135)));
        switch (v6) {
            default: {
                throw new MatchException(null, null);
            }
            case RENDERABLE: {
                v0 = new _7(v4, ServerAddress.parse((String)v5), this::method2131);
                break;
            }
            case HEADLESS: {
                v0 = new chch(v4, ServerAddress.parse((String)v5), this::method2131);
            }
        }
        _7 v9 = v0;
        this.method2177((BotSession)v9);
        if (v9 instanceof _7) {
            _7 v10 = v9;
            v10.Щ必();
        } else if (v9 instanceof chch) {
            chch v11 = (chch)v9;
            v11.Н1();
        }
        return null;
    }

    public int method2182(String param1, long param2) {
        if (param1 == null || param1.isBlank() || param2 < field8225) {
            return -1;
        }
        BotTimings v4 = new BotTimings(this.field8165.getAndIncrement(), param1.trim(), param2);
        this.field8137.add(v4);
        return v4.method195();
    }

    public void method2183(long param1) {
        if (param1 >= field8226) {
            this.field8169 = param1;
        }
    }

    private void method2184() {
        if (!this.field8134 || this.field8137.isEmpty()) {
            return;
        }
        CommandSourceUtil v1 = ServiceRegistry.method2113();
        if (v1 == null) {
            return;
        }
        long v2 = System.currentTimeMillis();
        for (BotTimings v5 : this.field8137) {
            if (!v5.method197(v2)) continue;
            v5.method196(v2);
            v1.method421(v5.method198());
        }
    }

    public RecordClass30 method2185(String param1) {
        if (param1 == null || param1.isBlank()) {
            return new RecordClass30(false, 100);
        }
        RecordClass30 v2 = (RecordClass30)this.O.get(BotManager.method2163(param1));
        return v2 != null ? v2 : new RecordClass30(false, 100);
    }

    public boolean method2186(String param1) {
        return param1 != null && !param1.isBlank() && this.field8152.contains(BotManager.method2163(param1));
    }

    public boolean cfr_renamed_4(String param1, String param2) {
        if (param1 == null || param1.isBlank() || param2 == null || param2.isBlank()) {
            return false;
        }
        BotSession v3 = this.method2126(param1);
        if (v3 == null || !v3.method65()) {
            return false;
        }
        this.field8151.put(BotManager.method2163(param1), param2.trim());
        v3.method59(field8227);
        return true;
    }

    public boolean method2187() {
        BotView v1 = (BotView)ServiceRegistry.method2118(BotView.class);
        return v1 != null && v1.method2198();
    }

    private void method2188() {
        if (!this.field8195) {
            if (this.field8154) {
                this.field8154 = false;
                this.method2189();
            }
            return;
        }
        long v1 = System.currentTimeMillis();
        if (this.field8154) {
            if (v1 < this.field8199) {
                return;
            }
            this.field8154 = false;
            this.field8174 = !this.field8174;
            this.method2189();
            return;
        }
        if (v1 - this.field8198 < this.field8171) {
            return;
        }
        this.field8198 = v1;
        this.field8199 = v1 + this.field8173;
        this.field8154 = true;
        boolean v3 = this.field8174;
        for (BotSession v5 : List.copyOf(this.field8138)) {
            tp v7;
            if (!(v5 instanceof fyu)) continue;
            fyu v6 = (fyu)v5;
            if (!v5.method65() || (v7 = v6.ь()) == null) continue;
            v7.бУ(v3);
            v7.бю(!v3);
        }
    }

    private void method2189() {
        for (BotSession v2 : List.copyOf(this.field8138)) {
            fyu v3;
            tp v4;
            if (!(v2 instanceof fyu) || (v4 = (v3 = (fyu)v2).ь()) == null) continue;
            v4.бУ(false);
            v4.бю(false);
        }
    }

    static {
        field8141 = "Client shutdown";
        field8183 = 50L;
        field8184 = "BOT-SVC";
        衣Ъ = "scheduled reconnect for bot=\u0001, inTicks=\u0001, address=\u0001, mode=\u0001";
        field8181 = "disconnect";
        field8182 = "disconnect";
        field8227 = "/balance";
        field8197 = "/balance";
        衣Ы = "/pay \u0001 \u0001";
        field8226 = 1000L;
        field8225 = 1000L;
        field8219 = "Имя бота не может быть пустым";
        field8220 = "Адрес сервера не может быть пустым";
        field8221 = "Некорректный адрес сервера";
        field8222 = "Бот с таким именем уже активен";
        Вн = "Reconnecting in \u0001 mode";
        field8223 = "BOT-SVC";
        ВЭ = "cleared pending view for headless connect bot=\u0001";
        field8224 = "BOT-SVC";
        В2 = "connectBot name=\u0001, address=\u0001, mode=\u0001, pendingView=\u0001";
        field8186 = "Имя бота не может быть пустым";
        Вм = "Бот не найден: \u0001";
        field8187 = "Этот бот подключен в headless-режиме. Для .bot view переподключи его с режимом renderable";
        field8188 = "Этот бот пока не поддерживает полноценное переключение камеры";
        field8189 = "BOT-SVC";
        ВЩ = "activateView deferred for bot=\u0001, ready=\u0001, contextReady=\u0001";
        field8190 = "Bot-context еще не готов для свободного переключения, включу view автоматически как только он дозреет";
        field8191 = "BotViewService не зарегистрирован";
        field8192 = "BOT-SVC";
        ВР = "activateView failed for bot=\u0001";
        field8193 = "Не удалось активировать bot-context";
        field8194 = "BOT-SVC";
        ВУ = "activateView success for bot=\u0001";
        field8177 = "Имя бота не может быть пустым";
        ВФ = "Бот не найден: \u0001";
        field8178 = "Этот бот нельзя автоматически переподключить в renderable-режим";
        field8185 = "BOT-SVC";
        ВМ = "deactivateView requested, active=\u0001";
        field8210 = "Main client transfer";
        field8211 = "Main client disconnected";
        field8136 = "BOT-SVC";
        ВО = "requestViewActivation bot=\u0001";
        ВД = "[BotService] Behavior tick failed for \u0001: \u0001";
        field8196 = "BOT-SVC";
        ВС = "Removing disconnected bot \u0001 (status=\u0001)";
        field8139 = "BOT-SVC";
        ВП = "Removing disconnected bot \u0001 (status=\u0001)";
        ВO = "[BotService] Session tick failed for \u0001: \u0001";
        field8140 = "BOT-SVC";
        ВЙ = "Removing disconnected bot \u0001 (status=\u0001)";
        field8180 = "BOT-SVC";
        В弟 = "Removing disconnected bot \u0001 during anti-afk (status=\u0001)";
        Ву = "[BotService] Anti-AFK failed for \u0001: \u0001";
        field8206 = "BOT-SVC";
        ВБ = "reconnect failed for bot=\u0001: \u0001";
        Вх = "[BotService] Anti-AFK walk failed for \u0001: \u0001";
        field8201 = "hub";
        field8202 = "spawn";
        field8203 = "/hub";
        field8204 = "/spawn";
        field8205 = "Auto leave";
        field8215 = "user";
        field8216 = "shutdown";
        field8217 = "reconnecting in";
        field8218 = "main client";
        field8207 = "баланс";
        field8208 = "balance";
        field8209 = "[^0-9]";
        field8157 = "disconnect";
        field8158 = "hub";
        field8159 = "/hub";
        field8160 = "spawn";
        field8161 = "/spawn";
        field8162 = "hub";
        field8163 = "spawn";
        field8164 = "disconnect";
        ВХ = "bot-pending-view-wait-\u0001";
        field8212 = 1000L;
        field8213 = "BOT-SVC";
        Вг = "pending view still waiting for bot=\u0001, ready=\u0001, contextReady=\u0001";
        field8214 = "BOT-SVC";
        Вю = "pending view activated for bot=\u0001";
        field8155 = 0.5f;
        field8156 = 0.9f;
        field8200 = "/sellwood";
    }
}



