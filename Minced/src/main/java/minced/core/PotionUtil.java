/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.registry.Registries
 *  net.minecraft.registry.entry.RegistryEntry
 *  net.minecraft.text.MutableText
 *  net.minecraft.text.Text
 *  net.minecraft.util.Identifier
 *  sLM.4ZXYEo
 *  sg.ec.2н
 */
package minced.core;

import java.util.HashSet;
import java.util.Set;
import minced.core.EventChatMessage;
import minced.core.EventUpdate;
import minced.core.NotificationManager;
import minced.core.Notifications;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import sLM._4ZXYEo;
import sg.ec._2n;

public class PotionUtil
{
    static String н2б;
    static String ньь;
    static String ньф;

    Set<net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect>> field741 = new HashSet<>();
    int[] field742 = new int[4];
    Item[] field743 = new Item[4];
    static MinecraftClient I = MinecraftClient.getInstance();
    static String field744;
    static String field745;
    static String field746;
    static String field747;
    static String field748;
    static String field749;
    static String field750;
    static double field751;
    static String field752;

    @EventHandler(2)
    public void method189(EventChatMessage param1) {
        if (PotionUtil.I.player == null) {
            return;
        }
        if (!((Boolean)Notifications.field9917.getValue()).booleanValue() && !((Boolean)Notifications.field9919.getValue()).booleanValue()) {
            return;
        }
        String v2 = PotionUtil.I.player.getName().getString();
        String v3 = param1.method76();
        if (!v3.toLowerCase().contains(v2.toLowerCase())) {
            return;
        }
        if (this.method190(v3, v2)) {
            return;
        }
        if (((Boolean)Notifications.field9917.getValue()).booleanValue()) {
            String v4 = v3.length() > 80 ? _4ZXYEo.cfr_renamed_121("makeConcatWithConstants", new Object[]{}, (String)v3.substring(0, 80)) : v3;
            NotificationManager.method2456(field744, v4, -1);
        }
        if (((Boolean)Notifications.field9919.getValue()).booleanValue() && !I.isWindowFocused()) {
            _2n.衣((String)field745, (String)field746);
        }
    }

    private boolean method190(String param1, String param2) {
        char v9;
        int v8;
        String v5;
        String v3 = param1.replaceAll(field747, "").trim();
        String v4 = v3.toLowerCase();
        int v6 = v4.indexOf(v5 = param2.toLowerCase());
        if (v6 == -1) {
            return false;
        }
        String v7 = v3.substring(0, v6).replaceAll(field748, "").trim();
        return !(!v7.isEmpty() && !v7.matches(field749) || (v8 = v6 + param2.length()) >= v3.length() || (v9 = v3.charAt(v8)) != '>' && v9 != ':' && v9 != ']' && v9 != ')' && v9 != '»' && v9 != ' ');
    }

    @SuppressWarnings("unchecked")
    @EventHandler(2)
    public void method191(EventUpdate param1) {
        if (PotionUtil.I.player == null || PotionUtil.I.world == null) {
            return;
        }
        if (((Boolean)Notifications.field9915.getValue()).booleanValue()) {
            HashSet<net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect>> v2 = new HashSet<>();
            for (StatusEffectInstance entry_v42 : PotionUtil.I.player.getStatusEffects()) {
                v2.add(entry_v42.getEffectType());
            }
            java.util.Iterator<net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect>> v3_it = field741.iterator();
            while (v3_it.hasNext()) {
                net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect> v42_entry = v3_it.next();
                if (v2.contains(v42_entry)) continue;
                NotificationManager.method2456(field750, (String)((Object)_4ZXYEo.cfr_renamed_122("makeConcatWithConstants", new Object[]{}, (String)v42_entry.value().getName().getString())), -1);
                v3_it.remove();
            }
            this.field741.addAll(v2);
        }
        if (((Boolean)Notifications.field9913.getValue()).booleanValue()) {
            for (int v2 = 0; v2 < PotionUtil.I.player.getInventory().armor.size(); ++v2) {
                int v6;
                int v5;
                int v7;
                int v8;
                ItemStack v3_stack = (ItemStack)PotionUtil.I.player.getInventory().armor.get(v2);
                if (v3_stack.isEmpty() || v3_stack.getMaxDamage() <= 0) {
                    this.field743[v2] = null;
                    this.field742[v2] = 0;
                    continue;
                }
                Item v42_item = v3_stack.getItem();
                if (this.field743[v2] != v42_item) {
                    this.field743[v2] = v42_item;
                    this.field742[v2] = 0;
                }
                if ((v8 = (int)((double)(v7 = Math.max(0, (v5 = v3_stack.getMaxDamage()) - (v6 = v3_stack.getDamage()))) * field751 / (double)v5)) < 1 || v8 > 5 || this.field742[v2] == v8) continue;
                Identifier v9 = Registries.ITEM.getId(v42_item);
                Identifier v10 = Identifier.of((String)v9.getNamespace(), (String)((Object)_4ZXYEo.cfr_renamed_123("makeConcatWithConstants", new Object[]{}, (String)v9.getPath())));
                MutableText v11 = Text.empty().append(v3_stack.getName()).append(field752);
                NotificationManager.method2454(v10, (Text)v11);
                this.field742[v2] = v8;
            }
        }
    }

    static {
        н2б = "\u0001...";
        field744 = "S";
        field745 = "Minced";
        field746 = "Тебя упомянули на сервере, посмотри, вдруг там что-то важное";
        field747 = "§.";
        field748 = "[<>\\[\\](){}]";
        field749 = "\\W*";
        field750 = "M";
        ньь = "Эффект '\u0001' больше не активен!";
        field751 = 100.0;
        ньф = "textures/item/\u0001.png";
        field752 = " почти сломан!";
    }
}



