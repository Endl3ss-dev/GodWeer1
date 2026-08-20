package org.privates.client.utils.other;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import static org.privates.client.api.interfaces.IMinecraft.mc;


public class PlayerSS {

    public static Block getBlock() {
        return getBlock(0.0, 0.0, 0.0);
    }

    public static Block getBlock(double x, double y, double z) {
        BlockPos pos = mc.player.getPosition().add(x, y, z);
        BlockState state = mc.world.getBlockState(pos);
        return state.getBlock();
    }


    public static boolean heldItem(PlayerEntity player, Item item) {
        return player.getHeldItemMainhand().getItem() == item ||
                player.getHeldItemOffhand().getItem() == item;
    }


    public static boolean collide(LivingEntity entity) {
        return collide(entity, 0.0f);
    }

    public static boolean collide(LivingEntity entity, float margin) {
        ClientPlayerEntity player = mc.player;
        AxisAlignedBB playerBB = player.getBoundingBox();
        AxisAlignedBB entityBB = entity.getBoundingBox().grow(margin, margin, margin);

        return playerBB.maxX > entityBB.minX &&
                playerBB.maxY > entityBB.minY &&
                playerBB.maxZ > entityBB.minZ &&
                playerBB.minX < entityBB.maxX &&
                playerBB.minY < entityBB.maxY &&
                playerBB.minZ < entityBB.maxZ;
    }


    public static int getItem(Class<? extends Item> itemClass, boolean hotbarOnly) {
        // Search hotbar first
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != null && itemClass.isInstance(stack.getItem())) {
                return i;
            }
        }

        if (hotbarOnly) {
            return -1;
        }

        for (int i = 9; i < 36; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack != null && itemClass.isInstance(stack.getItem())) {
                return i;
            }
        }

        return -1;
    }

    public static int getChestplate() {
        for (int i = 0; i < 45; i++) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() instanceof ArmorItem armor) {
                if (armor.getEquipmentSlot() == EquipmentSlotType.CHEST) {
                    if (i == 40) {
                        return 45;
                    } else if (i < 9) {
                        return i + 36;
                    } else {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public static Item getChest() {
        ClientPlayerEntity player = mc.player;
        ItemStack chestStack = player.getItemStackFromSlot(EquipmentSlotType.CHEST);
        return chestStack.getItem();
    }

    public static boolean equipElytra() {
        return getChest() == Items.ELYTRA;
    }
}