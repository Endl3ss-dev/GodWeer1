package net.minecraft.item;

import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import nuclear.control.Manager;
import nuclear.module.impl.player.ItemsCooldown;

public class EnderPearlItem extends Item {
    public EnderPearlItem(Properties builder) {
        super(builder);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        ItemsCooldown cooldown = Manager.FUNCTION_MANAGER.itemsCooldown;

        // Check if the item can be used (no active cooldown)
        if (cooldown.state && !cooldown.canUseItem(this)) {
            return ActionResult.resultFail(itemstack);
        }

        // Play sound
        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(),
                SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F,
                0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            EnderPearlEntity enderpearlentity = new EnderPearlEntity(worldIn, playerIn);
            enderpearlentity.setItem(itemstack);
            enderpearlentity.func_234612_a_(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.addEntity(enderpearlentity);
        }

        // Update cooldown only on successful use
        if (cooldown.state) {
            cooldown.onItemUsed(this);
        } else {
            playerIn.getCooldownTracker().setCooldown(this, 20);
        }

        playerIn.addStat(Stats.ITEM_USED.get(this));

        if (!playerIn.abilities.isCreativeMode) {
            itemstack.shrink(1);
        }

        return ActionResult.resultSuccess(itemstack);
    }
}