/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.privates.baritone.utils.player;

import org.privates.baritone.api.utils.Helper;
import org.privates.baritone.api.utils.IPlayerController;
import org.privates.baritone.utils.accessor.IPlayerControllerMP;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

import java.lang.reflect.Field;


/**
 * Implementation of {@link IPlayerController} that chains to the primary player controller's methods
 *
 * @author Brady
 * @since 12/14/2018
 */
public enum PrimaryPlayerController implements IPlayerController, Helper {

    INSTANCE;

    @Override
    public void syncHeldItem() {
        if (mc.playerController instanceof IPlayerControllerMP) {
            ((IPlayerControllerMP) mc.playerController).callSyncCurrentPlayItem();
            return;
        }
        mc.playerController.syncCurrentPlayItem();
    }

    @Override
    public boolean hasBrokenBlock() {
        if (mc.playerController instanceof IPlayerControllerMP) {
            BlockPos current = ((IPlayerControllerMP) mc.playerController).getCurrentBlock();
            return current != null && current.getY() == -1;
        }
        BlockPos current = mc.playerController.currentBlock;
        return current != null && current.getY() == -1;
    }

    @Override
    public boolean onPlayerDamageBlock(BlockPos pos, Direction side) {
        return mc.playerController.onPlayerDamageBlock(pos, side);
    }

    @Override
    public void resetBlockRemoving() {
        mc.playerController.resetBlockRemoving();
    }

    @Override
    public ItemStack windowClick(int windowId, int slotId, int mouseButton, ClickType type, PlayerEntity player) {
        return mc.playerController.windowClick(windowId, slotId, mouseButton, type, player);
    }

    @Override
    public GameType getGameType() {
        return mc.playerController.getCurrentGameType();
    }

    @Override
    public ActionResultType processRightClickBlock(ClientPlayerEntity player, World world, Hand hand, BlockRayTraceResult result) {
        return mc.playerController.rightClickBlock(player, (ClientWorld) world, hand, result);
    }

    @Override
    public ActionResultType processRightClick(ClientPlayerEntity player, World world, Hand hand) {
        return mc.playerController.processRightClick(player, world, hand);
    }

    @Override
    public boolean clickBlock(BlockPos loc, Direction face) {
        return mc.playerController.clickBlock(loc, face);
    }

    @Override
    public void setHittingBlock(boolean hittingBlock) {
        if (mc.playerController instanceof IPlayerControllerMP) {
            ((IPlayerControllerMP) mc.playerController).setIsHittingBlock(hittingBlock);
            return;
        }
        try {
            Field field = findField(mc.playerController.getClass(), "isHittingBlock");
            if (field == null) {
                return;
            }
            field.setAccessible(true);
            field.setBoolean(mc.playerController, hittingBlock);
        } catch (ReflectiveOperationException ignored) {
            // Best-effort fallback for environments where accessor mixins are not applied (e.g. OptiFine controller wrappers)
        }
    }

    private static Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
