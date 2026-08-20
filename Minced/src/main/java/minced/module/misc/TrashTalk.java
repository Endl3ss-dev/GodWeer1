/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket
 *  net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket
 *  net.minecraft.world.World
 *  sg.ec.Ш$Ч
 */
package minced.module.misc;
import minced.core.EventHandler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.EventUpdate;
import minced.core.EventPacket;
import minced.core.EventAttack;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.world.World;
import sg.ec.sh;

public class TrashTalk
extends Module
{
    public static net.minecraft.client.MinecraftClient I;
    static String field1810;
    static String field1811;
    static String field1812;
    static String field1813;
    static String field1814;
    static String field1815;
    static String field1816;
    static String field1817;
    static String field1818;
    static String field1819;
    static String field1820;
    static String field1821;
    static String field1822;
    static String field1823;
    static String field1824;
    static String field1825;
    static String field1826;
    static String field1827;
    static String field1828;
    static String field1829;
    static String field1830;
    static String[] field1831;
    static MinecraftClient var_net_minecraft_client_MinecraftClient_I = MinecraftClient.getInstance();
    Map field1832;
    Set var_java_util_Set_I;
    SliderSetting field1833 = new SliderSetting(field1838, field1839, 0.0f, field1840, field1841);
    static float field1834;
    long field1835 = 0L;
    static String field1836;
    static String field1837;
    static String field1838;
    static float field1839;
    static float field1840;
    static float field1841;
    static String field1842;
    BooleanSetting field1843 = new BooleanSetting(field1842, true);
    static float field1844;
    static double field1845;
    static long field1846;

    public static void method562() {
        field1831 = new String[]{field1810, field1811, field1812, field1813, field1814, field1815, field1816, field1817, field1818, field1819, field1820, field1821, field1822, field1823, field1824, field1825, field1826, field1827, field1828, field1829, field1830};
    }

    @EventHandler(2)
    private void method563(EventPacket param1) {
        PlayerEntity v5;
        int v6;
        sh.ch v7;
        DeathMessageS2CPacket v2;
        Entity v4;
        if (TrashTalk.var_net_minecraft_client_MinecraftClient_I.player == null || TrashTalk.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return;
        }
        if (!param1.method2466()) {
            return;
        }
        Packet v3 = param1.method2467();
        if (v3 instanceof DeathMessageS2CPacket && (v4 = TrashTalk.var_net_minecraft_client_MinecraftClient_I.world.getEntityById((v2 = (DeathMessageS2CPacket)v3).playerId())) instanceof PlayerEntity && (v7 = (sh.ch)this.field1832.get(v6 = (v5 = (PlayerEntity)v4).getId())) != null && (Boolean)v7.cfr_renamed_402()) {
            this.method565(v7.м);
            this.field1832.remove(v6);
        }
        Packet v3s = param1.method2467();
        if (v3s instanceof EntityStatusS2CPacket) {
            Entity v4s;
            sh.ch v7s;
            int v6s;
            EntityStatusS2CPacket v2s = (EntityStatusS2CPacket)v3s;
            if (v2s.getStatus() == 3 && (v4s = v2s.getEntity((World)TrashTalk.var_net_minecraft_client_MinecraftClient_I.world)) instanceof PlayerEntity && (v7s = (sh.ch)this.field1832.get(v6s = ((PlayerEntity)v4s).getId())) != null && (Boolean)v7s.cfr_renamed_402()) {
                this.method565(v7s.м);
                this.field1832.remove(v6s);
            }
        }
    }

    @EventHandler(2)
    private void method564(EventAttack param1) {
        PlayerEntity v3;
        if (TrashTalk.var_net_minecraft_client_MinecraftClient_I.player == null || TrashTalk.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return;
        }
        Entity v2 = param1.method2471();
        if (v2 instanceof PlayerEntity) {
            v3 = (PlayerEntity)v2;
            if (v3 == TrashTalk.var_net_minecraft_client_MinecraftClient_I.player) {
                return;
            }
        } else {
            return;
        }
        int v4 = v3.getId();
        String v5 = v3.getGameProfile().getName();
        float v6 = v3.getHealth();
        double v7 = TrashTalk.var_net_minecraft_client_MinecraftClient_I.player.distanceTo((Entity)v3);
        if (this.field1832.containsKey(v4)) {
            ((sh.ch)this.field1832.get(v4)).н(v6, v7);
        } else {
            this.field1832.put(v4, new sh.ch(v5, v6, v7));
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.field1832.clear();
        this.var_java_util_Set_I.clear();
    }

    private void method565(String param1) {
        long v4;
        if (TrashTalk.var_net_minecraft_client_MinecraftClient_I.player == null || TrashTalk.var_net_minecraft_client_MinecraftClient_I.player.networkHandler == null) {
            return;
        }
        long v2 = System.currentTimeMillis();
        if (v2 - this.field1835 < (v4 = (long)(((Float)this.field1833.getValue()).floatValue() * field1834))) {
            return;
        }
        if (this.var_java_util_Set_I.contains(param1)) {
            return;
        }
        String v6 = field1831[ThreadLocalRandom.current().nextInt(field1831.length)];
        v6 = String.format(v6, param1);
        TrashTalk.var_net_minecraft_client_MinecraftClient_I.player.networkHandler.sendChatMessage(v6);
        this.field1835 = v2;
        this.var_java_util_Set_I.add(param1);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        this.field1832.clear();
        this.var_java_util_Set_I.clear();
    }

    public TrashTalk() {
        super(field1836, field1837, Combat.Miscellaneous);
        this.field1832 = new ConcurrentHashMap();
        this.var_java_util_Set_I = new HashSet();
        this.addSettings(this.field1833, this.field1843);
    }

    @EventHandler(2)
    private void method566(EventUpdate param1) {
        if (TrashTalk.var_net_minecraft_client_MinecraftClient_I.player == null || TrashTalk.var_net_minecraft_client_MinecraftClient_I.world == null) {
            return;
        }
        Iterator v2 = this.field1832.entrySet().iterator();
        while (v2.hasNext()) {
            Map.Entry v3 = (Map.Entry)(v2.next());
            int v4 = (Integer)v3.getKey();
            sh.ch v5 = (sh.ch)v3.getValue();
            Entity v6 = TrashTalk.var_net_minecraft_client_MinecraftClient_I.world.getEntityById(v4);
            if (v6 instanceof PlayerEntity) {
                PlayerEntity v7 = (PlayerEntity)v6;
                float v8 = v7.getHealth();
                double v9 = TrashTalk.var_net_minecraft_client_MinecraftClient_I.player.distanceTo((Entity)v7);
                if (v8 <= 0.0f && (Boolean)v5.cfr_renamed_402()) {
                    this.method565(v5.м);
                    v2.remove();
                    continue;
                }
                if (v8 < v5.f13) {
                    v5.f13 = v8;
                    if (v8 <= field1844) {
                        v5.О = true;
                    }
                }
                v5.I = v9;
            } else {
                if ((Boolean)v5.cfr_renamed_402() && !((Boolean)this.field1843.getValue()).booleanValue() && (Boolean)v5.cfr_renamed_403() && v5.I < field1845) {
                    this.method565(v5.м);
                }
                v2.remove();
            }
            if (v5.cfr_renamed_402()) continue;
            v2.remove();
        }
        if (System.currentTimeMillis() - this.field1835 > field1846) {
            this.var_java_util_Set_I.clear();
        }
    }

    static {
        field1836 = "Trash Talk";
        field1837 = "Пишет сообщения при убийстве игроков";
        field1838 = "Задержка (сек)";
        field1839 = 3.0f;
        field1840 = 10.0f;
        field1841 = 0.5f;
        field1842 = "Только убийства";
        field1844 = 10.0f;
        field1845 = 30.0;
        field1846 = 60000L;
        field1834 = 1000.0f;
        field1810 = "! %s куколд на минималках";
        field1811 = "! %s твоя тёлка сосет у всего сервера, пока ты смотришь";
        field1812 = "! %s типичный фанат рогов, сиди и наблюдай за альфой";
        field1813 = "! %s твоя мать шлюха, а ты её главный куколд-выкидыш";
        field1814 = "! %s твоя жизнь это смотреть, как других ебут, терпила";
        field1815 = "! %s соси хуй, ничтожество, твоя тёлка уже выбрала меня";
        field1816 = "! %s сиди и плачь, пока твоя тёлка стонет моё имя";
        field1817 = "! %s ты официальный куколд этого проекта, ez kill";
        field1818 = "! %s твоя тёлка  общая, а ты просто платишь за её интернет";
        field1819 = "! %s убейся, куколд, ты даже в игре не можешь за себя постоять";
        field1820 = "! %s ты жалкий наблюдатель, твоя судьба смотреть и сосать";
        field1821 = "! %s твоя жизнь  это ошибка, которую я сейчас исправил";
        field1822 = "! %s ты дегенерат куколд, вали в дурку к своей мамаше";
        field1823 = "! %s сиди в углу и смотри, как я доминирую над тобой кукичем и твоей тёлкой";
        field1824 = "! %s твоя тёлка уже в моем дискорде кидает голые фотки)";
        field1825 = "! %s твоя смерть это лучшее, что случилось с твоей семьей)";
        field1826 = "! %s твоя тёлка кончает от одного моего ника, пока ты сосешь)";
        field1827 = "! %s сиди и смотри как я ебу твою тёлку в прямом эфире, кукич)";
        field1828 = "! %s твоя жизнь говно, твоя тёлка общая дырка)";
        field1829 = "! %s я кончил твоей тёлке в рот, пока ты бежал от меня)";
        field1830 = "! %s сиди в говне, терпила, твоя тёлка выбрала победителя, тоесть меня)";
        TrashTalk.method562();
    }
}



