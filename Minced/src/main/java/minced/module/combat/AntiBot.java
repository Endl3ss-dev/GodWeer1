/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.AbstractClientPlayerEntity
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  sLM.4ZXYEo
 */
package minced.module.combat;
import minced.core.EventHandler;

import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.stream.StreamSupport;
import minced.core.ModeSetting;
import minced.core.EventSwapWorld;
import minced.core.EventAddEntity;
import minced.core.ServerDetector;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import sLM._4ZXYEo;

public class AntiBot
extends Module {
    static String cfr_renamed_361;

    static String field3727;
    static String field3728;
    static Set field3729;
    static MinecraftClient I = MinecraftClient.getInstance();
    ModeSetting field3730 = new ModeSetting(field3736, field3737, field3738, field3739, field3740);
    static String field3731;
    static String field3732;
    static String field3733;
    static String field3734;
    static String field3735;
    static String field3736;
    static String field3737;
    static String field3738;
    static String field3739;
    static String field3740;

    private boolean method1032(PlayerEntity param1) {
        if (!ServerDetector.method2684(field3727) || !ServerDetector.method2684(field3728)) {
            return false;
        }
        boolean v2 = ((ItemStack)param1.getInventory().armor.get(0)).getItem() == Items.AIR;
        boolean v3 = ((ItemStack)param1.getInventory().armor.get(1)).getItem() == Items.AIR;
        boolean v4 = ((ItemStack)param1.getInventory().armor.get(2)).getItem() == Items.AIR;
        boolean v5 = ((ItemStack)param1.getInventory().armor.get(3)).getItem() == Items.AIR;
        boolean v6 = ((ItemStack)param1.getInventory().armor.get(0)).getItem() != Items.AIR;
        boolean v7 = ((ItemStack)param1.getInventory().armor.get(1)).getItem() != Items.AIR;
        boolean v8 = ((ItemStack)param1.getInventory().armor.get(2)).getItem() != Items.AIR;
        boolean v9 = ((ItemStack)param1.getInventory().armor.get(3)).getItem() != Items.AIR;
        ItemStack v10 = (ItemStack)param1.getInventory().armor.get(0);
        ItemStack v11 = (ItemStack)param1.getInventory().armor.get(1);
        ItemStack v12 = (ItemStack)param1.getInventory().armor.get(2);
        ItemStack v13 = (ItemStack)param1.getInventory().armor.get(3);
        boolean v14 = !v10.isEmpty() && v10.contains(DataComponentTypes.ENCHANTMENTS);
        boolean v15 = !v11.isEmpty() && v11.contains(DataComponentTypes.ENCHANTMENTS);
        boolean v16 = !v12.isEmpty() && v12.contains(DataComponentTypes.ENCHANTMENTS);
        boolean v17 = !v13.isEmpty() && v13.contains(DataComponentTypes.ENCHANTMENTS);
        boolean v18 = v2 || v6 && !v14;
        boolean v19 = v3 || v7 && !v15;
        boolean v20 = v4 || v8 && !v16;
        boolean v21 = v5 || v9 && !v17;
        boolean v22 = param1.getArmor() == 0;
        boolean v23 = Minced.getInstance().getFriendManager().cfr_renamed_4(param1.getGameProfile().getName());
        boolean v24 = !v10.isEmpty() && !v11.isEmpty() && !v12.isEmpty() && !v13.isEmpty() && v10.getDamage() > 0 && v11.getDamage() > 0 && v12.getDamage() > 0 && v13.getDamage() > 0;
        String v25 = param1.getName().getString();
        boolean v26 = v25.length() == 6;
        boolean v27 = v18 && v19 && v20 && v21;
        return v26 && !v23 && !v22 && !v24 && v27;
    }

    private void l(PlayerEntity param1, boolean param2) {
        if (param2) {
            field3729.add(param1.getUuid());
        } else {
            field3729.remove(param1.getUuid());
        }
    }

    public static boolean method1033(PlayerEntity param0) {
        return field3729.contains(param0.getUuid());
    }

    @Override
    public void onDisable() {
        field3729.clear();
        super.onDisable();
    }

    @EventHandler(2)
    public void method1035(EventAddEntity param1) {
        Entity v3 = param1.method2497();
        if (v3 instanceof PlayerEntity) {
            PlayerEntity v2 = (PlayerEntity)v3;
            if (v2 == AntiBot.I.player) {
                return;
            }
            this.l(v2, this.method1036((Entity)v2));
        }
    }

    private boolean method1036(Entity param1) {
        AbstractClientPlayerEntity v2;
        if (param1 instanceof AbstractClientPlayerEntity) {
            v2 = (AbstractClientPlayerEntity)param1;
            if (AntiBot.I.player == null || v2 == AntiBot.I.player) {
                return false;
            }
        } else {
            return false;
        }
        if (this.field3730.isMode(field3731)) {
            return !v2.getUuid().equals(UUID.nameUUIDFromBytes(((String)((Object)_4ZXYEo.弟ш("makeConcatWithConstants", new Object[]{}, (String)v2.getName().getString()))).getBytes(StandardCharsets.UTF_8)));
        }
        if (this.field3730.isMode(field3732)) {
            return this.method1032((PlayerEntity)v2);
        }
        GameProfile v3 = v2.getGameProfile();
        String v4 = v3.getName();
        if (v4.contains(field3733)) {
            return true;
        }
        if (v2.isSpectator()) {
            return true;
        }
        boolean v5 = StreamSupport.stream(v2.getArmorItems().spliterator(), false).anyMatch(param0 -> !param0.isEmpty());
        boolean v6 = v3.getProperties().isEmpty();
        if (v6 && v5) {
            return true;
        }
        return v6;
    }

    public static void method1037() {
        field3729 = new HashSet();
    }

    public AntiBot() {
        super(field3734, field3735, Combat.Combat);
        this.addSettings(this.field3730);
    }

    @EventHandler(2)
    public void method1038(EventSwapWorld param1) {
        field3729.clear();
    }

    @Override
    public void onEnable() {
        if (AntiBot.I.world != null) {
            for (PlayerEntity v2 : AntiBot.I.world.getPlayers()) {
                if (v2 == AntiBot.I.player) continue;
                this.l(v2, this.method1036((Entity)v2));
            }
        }
        super.onEnable();
    }

    static {
        field3734 = "Anti Bot";
        field3735 = "Определяет и метит ботов, вызванных системой античита";
        field3736 = "Мод";
        field3737 = "Matrix";
        field3738 = "Matrix";
        field3739 = "UniAC";
        field3740 = "Reallyworld";
        field3731 = "ReallyWorld";
        cfr_renamed_361 = "OfflinePlayer:\u0001";
        field3732 = "UniAC";
        field3733 = "-";
        field3727 = "lonygrief";
        field3728 = "slimeworld";
        AntiBot.method1037();
    }
}



