/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.text2speech.Narrator
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.block.BlockState
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.component.DataComponentTypes
 *  net.minecraft.component.type.EquippableComponent
 *  net.minecraft.component.type.PotionContentsComponent
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.attribute.EntityAttributes
 *  net.minecraft.entity.effect.StatusEffect
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffectUtil
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.AxeItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
 *  net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
 *  net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket
 *  net.minecraft.registry.tag.FluidTags
 *  net.minecraft.screen.ScreenHandler
 *  net.minecraft.screen.slot.Slot
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.collection.DefaultedList
 */
package minced.core;

import com.google.common.collect.Lists;
import com.mojang.text2speech.Narrator;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import minced.mixin.accessor.IClientPlayerInteractionManagerMixin;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;

public class InventoryUtil {
    static Item[] field11699;
    static MinecraftClient I = MinecraftClient.getInstance();
    static float field11700;
    static float field11701;
    static float field11702;
    static float field11703;
    static float field11704;
    static float field11705;
    static float field11706;
    static String field11707;

    public static void method3309() {
        field11699 = new Item[]{Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.LEATHER_CHESTPLATE};
    }

    public static int method3310() {
        if (I == null || InventoryUtil.I.player == null) {
            return -1;
        }
        int v0 = -1;
        int v1 = 0;
        for (int v2 = 0; v2 < 45; ++v2) {
            ItemStack v3 = InventoryUtil.I.player.getInventory().getStack(v2);
            if (v3.isEmpty() || !v3.contains(DataComponentTypes.FOOD) || v3.getCount() <= v1) continue;
            v0 = v2;
            v1 = v3.getCount();
        }
        return v0;
    }

    public static void method3311(int param0, int param1) {
        if (param0 == param1) {
            return;
        }
        param0 = param0 < 9 ? param0 + 36 : param0;
        InventoryUtil.I.interactionManager.clickSlot(InventoryUtil.I.player.currentScreenHandler.syncId, param0, 0, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
        InventoryUtil.I.interactionManager.clickSlot(InventoryUtil.I.player.currentScreenHandler.syncId, param1, 0, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
        InventoryUtil.I.interactionManager.clickSlot(InventoryUtil.I.player.currentScreenHandler.syncId, param0, 0, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
    }

    public static void method3312(int param0, int param1) {
        if (InventoryUtil.I.player == null || InventoryUtil.I.interactionManager == null) {
            return;
        }
        InventoryUtil.I.interactionManager.clickSlot(0, param0, param1, SlotActionType.PICKUP, (PlayerEntity)InventoryUtil.I.player);
    }

    public static void method3313(int param0) {
        if (InventoryUtil.I.player == null || I.getNetworkHandler() == null || InventoryUtil.I.interactionManager == null) {
            return;
        }
        if (param0 < 0 || param0 > 8) {
            return;
        }
        if (InventoryUtil.I.player.getInventory().selectedSlot == param0) {
            return;
        }
        InventoryUtil.I.player.getInventory().selectedSlot = param0;
        ((IClientPlayerInteractionManagerMixin)InventoryUtil.I.interactionManager).invokeSyncSelectedSlot();
    }

    public static int method3314(BlockState param0, int param1, int param2) {
        if (InventoryUtil.I.player == null) {
            return -1;
        }
        int v3 = -1;
        float v4 = field11700;
        for (int v5 = param1; v5 <= param2; ++v5) {
            float v7;
            ItemStack v6 = InventoryUtil.I.player.getInventory().getStack(v5);
            if (v6.isEmpty() || !((v7 = InventoryUtil.method3319(param0, v6)) > v4)) continue;
            v4 = v7;
            v3 = v5;
        }
        return v3;
    }

    public static int method3315() {
        if (InventoryUtil.I.player == null) {
            return -1;
        }
        for (int v0 = 0; v0 < InventoryUtil.I.player.getInventory().main.size(); ++v0) {
            EquippableComponent v2;
            ItemStack v1 = (ItemStack)InventoryUtil.I.player.getInventory().main.get(v0);
            if (v1.isEmpty() || !(v1.getItem() instanceof ArmorItem) || !v1.contains(DataComponentTypes.EQUIPPABLE) || (v2 = (EquippableComponent)v1.get(DataComponentTypes.EQUIPPABLE)) == null || v2.slot() != EquipmentSlot.CHEST) continue;
            return v0;
        }
        return -1;
    }

    public static int method3316(ItemStack param0) {
        if (I == null || InventoryUtil.I.player == null || param0.isEmpty()) {
            return -1;
        }
        for (int v1 = 0; v1 < 45; ++v1) {
            ItemStack v2 = InventoryUtil.I.player.getInventory().getStack(v1);
            if (v2.isEmpty() || !ItemStack.areItemsAndComponentsEqual((ItemStack)v2, (ItemStack)param0)) continue;
            return v1;
        }
        return -1;
    }

    public static void method3317(int param0, int param1) {
        if (InventoryUtil.I.player == null || InventoryUtil.I.interactionManager == null) {
            return;
        }
        int v2 = param0 < 9 ? param0 + 36 : param0;
        InventoryUtil.I.interactionManager.clickSlot(0, v2, 0, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
        InventoryUtil.I.interactionManager.clickSlot(0, param1, 0, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
        InventoryUtil.I.interactionManager.clickSlot(0, v2, 0, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
    }

    public static int method3318() {
        if (InventoryUtil.I.player == null) {
            return -1;
        }
        for (int v0 = 0; v0 < 36; ++v0) {
            ItemStack v1 = InventoryUtil.I.player.getInventory().getStack(v0);
            if (!v1.isEmpty()) continue;
            return v0 < 9 ? v0 + 36 : v0;
        }
        return -1;
    }

    public static float method3319(BlockState param0, ItemStack param1) {
        float v2 = param1.getMiningSpeedMultiplier(param0);
        if (v2 > 1.0f) {
            v2 += (float)InventoryUtil.I.player.getAttributeValue(EntityAttributes.MINING_EFFICIENCY);
        }
        if (StatusEffectUtil.hasHaste((LivingEntity)InventoryUtil.I.player)) {
            v2 *= 1.0f + (float)(StatusEffectUtil.getHasteAmplifier((LivingEntity)InventoryUtil.I.player) + 1) * field11701;
        }
        if (InventoryUtil.I.player.hasStatusEffect(StatusEffects.MINING_FATIGUE)) {
            float v3;
            switch (InventoryUtil.I.player.getStatusEffect(StatusEffects.MINING_FATIGUE).getAmplifier()) {
                case 0: {
                    v3 = field11702;
                    break;
                }
                case 1: {
                    v3 = field11703;
                    break;
                }
                case 2: {
                    v3 = field11704;
                    break;
                }
                default: {
                    v3 = field11705;
                }
            }
            v2 *= v3;
        }
        v2 *= (float)InventoryUtil.I.player.getAttributeValue(EntityAttributes.BLOCK_BREAK_SPEED);
        if (InventoryUtil.I.player.isSubmergedIn(FluidTags.WATER)) {
            v2 *= (float)InventoryUtil.I.player.getAttributeInstance(EntityAttributes.SUBMERGED_MINING_SPEED).getValue();
        }
        if (!InventoryUtil.I.player.isOnGround()) {
            v2 /= field11706;
        }
        return v2;
    }

    public static int method3320(BlockState param0) {
        return InventoryUtil.method3314(param0, 0, 8);
    }

    private static void method3321(int param0, int param1) {
        if (InventoryUtil.I.player == null) {
            return;
        }
        if (InventoryUtil.I.player.isUsingItem() && !InventoryUtil.I.player.isBlocking()) {
            InventoryUtil.method3311(param0, 45);
            InventoryUtil.I.interactionManager.interactItem((PlayerEntity)InventoryUtil.I.player, Hand.OFF_HAND);
            InventoryUtil.method3311(param0, 45);
        } else {
            InventoryUtil.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(param0));
            InventoryUtil.I.interactionManager.interactItem((PlayerEntity)InventoryUtil.I.player, Hand.MAIN_HAND);
            InventoryUtil.I.player.networkHandler.sendPacket((Packet)new UpdateSelectedSlotC2SPacket(param1));
        }
    }

    public static int method3322(int param0) {
        if (param0 < 0 || param0 > 35) {
            return -1;
        }
        return param0 < 9 ? param0 + 36 : param0;
    }

    public static void method3323(Item param0) {
        if (InventoryUtil.I.player == null || InventoryUtil.I.interactionManager == null) {
            return;
        }
        int v1 = InventoryUtil.method3330(param0, 0, 8);
        int v2 = InventoryUtil.method3330(param0, 9, 35);
        int v3 = InventoryUtil.I.player.getInventory().selectedSlot;
        if (InventoryUtil.I.player.getMainHandStack().getItem() == param0) {
            InventoryUtil.I.interactionManager.interactItem((PlayerEntity)InventoryUtil.I.player, Hand.MAIN_HAND);
        } else if (InventoryUtil.I.player.getOffHandStack().getItem() == param0) {
            InventoryUtil.I.interactionManager.interactItem((PlayerEntity)InventoryUtil.I.player, Hand.OFF_HAND);
        } else if (v1 != -1) {
            InventoryUtil.I.player.getInventory().selectedSlot = v1;
            InventoryUtil.I.interactionManager.interactItem((PlayerEntity)InventoryUtil.I.player, Hand.MAIN_HAND);
            InventoryUtil.I.player.getInventory().selectedSlot = v3;
        } else if (v2 != -1) {
            InventoryUtil.method3325(param0);
        }
    }

    public static int method3324(boolean param0, boolean param1, boolean param2, boolean param3, StatusEffect ... param4) {
        if (InventoryUtil.I.player == null) {
            return -1;
        }
        if (param0) {
            for (int v5 = 0; v5 < 9; ++v5) {
                ItemStack v6 = InventoryUtil.I.player.getInventory().getStack(v5);
                if (!InventoryUtil.method3333(v6, param1, param2, param3, param4)) continue;
                return v5;
            }
        } else {
            ItemStack v6;
            int v5;
            for (v5 = 0; v5 < 9; ++v5) {
                v6 = InventoryUtil.I.player.getInventory().getStack(v5);
                if (!InventoryUtil.method3333(v6, param1, param2, param3, param4)) continue;
                return v5;
            }
            for (v5 = 9; v5 < 36; ++v5) {
                v6 = InventoryUtil.I.player.getInventory().getStack(v5);
                if (!InventoryUtil.method3333(v6, param1, param2, param3, param4)) continue;
                return v5;
            }
        }
        return -1;
    }

    public static void method3325(Item param0) {
        boolean v3;
        if (InventoryUtil.I.player == null || InventoryUtil.I.interactionManager == null) {
            return;
        }
        int v1 = InventoryUtil.method3336(param0);
        if (v1 == -1) {
            return;
        }
        int v2 = InventoryUtil.I.player.getInventory().selectedSlot;
        boolean bl = v3 = v1 < 9;
        if (v3 && v1 == v2) {
            InventoryUtil.method3334();
        } else if (v3) {
            InventoryUtil.method3321(v1, v2);
        } else {
            int v4 = v2 % 8 + 1;
            InventoryUtil.I.interactionManager.clickSlot(0, v1, v4, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
            InventoryUtil.method3321(v4, v2);
            InventoryUtil.I.interactionManager.clickSlot(0, v1, v4, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
        }
    }

    public static int method3326() {
        for (int v0 = 0; v0 < 45; ++v0) {
            ItemStack v1 = InventoryUtil.I.player.getInventory().getStack(v0);
            if (v1.isEmpty() || !(v1.getItem() instanceof AxeItem)) continue;
            return v0;
        }
        return -1;
    }

    public static void method3327(Item param0) {
        if (InventoryUtil.I.player == null || InventoryUtil.I.interactionManager == null) {
            return;
        }
        int v1 = InventoryUtil.method3336(param0);
        if (v1 == -1) {
            return;
        }
        int v2 = InventoryUtil.I.player.getInventory().selectedSlot;
        if (v1 < 9) {
            InventoryUtil.method3313(v1);
            return;
        }
        int v3 = InventoryUtil.method3322(v1);
        if (v3 < 0) {
            return;
        }
        InventoryUtil.I.interactionManager.clickSlot(InventoryUtil.I.player.currentScreenHandler.syncId, v3, v2, SlotActionType.SWAP, (PlayerEntity)InventoryUtil.I.player);
        InventoryUtil.I.player.networkHandler.sendPacket((Packet)new CloseHandledScreenC2SPacket(InventoryUtil.I.player.currentScreenHandler.syncId));
    }

    public static void method3328(int param0, int param1) {
        if (param0 == param1) {
            return;
        }
        param0 = param0 < 9 ? param0 + 36 : param0;
        InventoryUtil.method3312(param0, 0);
        InventoryUtil.method3312(param1, 0);
        InventoryUtil.method3312(param0, 0);
    }

    public static int method3329(Item param0) {
        if (InventoryUtil.I.player == null) {
            return 0;
        }
        int v1 = 0;
        for (ItemStack v3 : InventoryUtil.I.player.getInventory().main) {
            if (v3.isEmpty() || !v3.getItem().equals(param0)) continue;
            v1 += v3.getCount();
        }
        return v1;
    }

    public static int method3330(Item param0, int param1, int param2) {
        if (I == null || InventoryUtil.I.player == null) {
            return -1;
        }
        for (int v3 = param2; v3 >= param1; --v3) {
            ItemStack v4 = InventoryUtil.I.player.getInventory().getStack(v3);
            if (v4.isEmpty() || v4.getItem() != param0) continue;
            return v3;
        }
        return -1;
    }

    public static int method3331() {
        if (I == null || InventoryUtil.I.player == null) {
            return -1;
        }
        for (Item v3 : field11699) {
            int v4 = InventoryUtil.method3330(v3, 0, 35);
            if (v4 == -1) continue;
            return v4;
        }
        return -1;
    }

    public static void method3332(int param0, int param1, int param2, SlotActionType param3, PlayerEntity param4) {
        ScreenHandler v5 = param4.currentScreenHandler;
        if (param0 != v5.syncId) {
            Narrator.LOGGER.warn(field11707, (Object)param0, (Object)v5.syncId);
        } else {
            DefaultedList v6 = v5.slots;
            int v7 = v6.size();
            ArrayList v8 = Lists.newArrayListWithCapacity((int)v7);
            for (Slot v10 : (Iterable<Slot>)(v6)) {
                v8.add(v10.getStack().copy());
            }
            Int2ObjectOpenHashMap v9 = new Int2ObjectOpenHashMap();
            for (int v10 = 0; v10 < v7; ++v10) {
                ItemStack v12;
                ItemStack v11 = (ItemStack)v8.get(v10);
                if (ItemStack.areEqual((ItemStack)v11, (ItemStack)(v12 = ((Slot)v6.get(v10)).getStack()))) continue;
                v9.put(v10, v12.copy());
            }
            InventoryUtil.I.player.networkHandler.sendPacket((Packet)new ClickSlotC2SPacket(param0, v5.getRevision(), param1, param2, param3, v5.getCursorStack().copy(), (Int2ObjectMap)v9));
        }
    }

    public static boolean method3333(ItemStack param0, boolean param1, boolean param2, boolean param3, StatusEffect ... param4) {
        boolean v6;
        if (param0 == null || param0.isEmpty()) {
            return false;
        }
        Item v5 = param0.getItem();
        boolean bl = v6 = param1 && v5 == Items.POTION || param2 && v5 == Items.SPLASH_POTION || param3 && v5 == Items.LINGERING_POTION;
        if (!v6) {
            return false;
        }
        PotionContentsComponent v7 = (PotionContentsComponent)param0.getOrDefault(DataComponentTypes.POTION_CONTENTS, (Object)PotionContentsComponent.DEFAULT);
        for (StatusEffectInstance v9 : v7.getEffects()) {
            for (StatusEffect v13 : param4) {
                if (v9.getEffectType().value() != v13) continue;
                return true;
            }
        }
        return false;
    }

    private static void method3334() {
        if (InventoryUtil.I.player == null) {
            return;
        }
        if (InventoryUtil.I.player.isUsingItem() && !InventoryUtil.I.player.isBlocking()) {
            InventoryUtil.I.interactionManager.interactItem((PlayerEntity)InventoryUtil.I.player, Hand.OFF_HAND);
        } else {
            InventoryUtil.I.interactionManager.interactItem((PlayerEntity)InventoryUtil.I.player, Hand.MAIN_HAND);
        }
    }

    public static int method3335() {
        if (I == null || InventoryUtil.I.player == null) {
            return -1;
        }
        for (int v0 = 0; v0 < 45; ++v0) {
            ItemStack v1 = InventoryUtil.I.player.getInventory().getStack(v0);
            if (v1.isEmpty() || v1.getItem() != Items.TOTEM_OF_UNDYING || EnchantmentHelper.getEnchantments((ItemStack)v1).isEmpty()) continue;
            return v0;
        }
        return -1;
    }

    public static int method3336(Item param0) {
        if (I == null || InventoryUtil.I.player == null) {
            return -1;
        }
        for (int v1 = 0; v1 < 36; ++v1) {
            ItemStack v2 = InventoryUtil.I.player.getInventory().getStack(v1);
            if (v2.isEmpty() || v2.getItem() != param0) continue;
            return v1;
        }
        return -1;
    }

    static {
        field11707 = "Ignoring click in mismatching container. Click in {}, player has {}.";
        field11700 = -1.0f;
        field11701 = 0.2f;
        field11702 = 0.3f;
        field11703 = 0.09f;
        field11704 = 0.0027f;
        field11705 = 8.1E-4f;
        field11706 = 5.0f;
        InventoryUtil.method3309();
    }
}



