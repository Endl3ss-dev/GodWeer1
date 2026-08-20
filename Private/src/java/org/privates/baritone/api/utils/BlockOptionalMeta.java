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

package org.privates.baritone.api.utils;

import com.google.common.collect.ImmutableSet;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPredicateManager;
import net.minecraft.loot.LootTableManager;
import net.minecraft.resources.*;
import net.minecraft.util.Unit;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class BlockOptionalMeta {

    private final Block block;
    private final Set<BlockState> blockstates;
    private final ImmutableSet<Integer> stateHashes;
    private final ImmutableSet<Integer> stackHashes;
    private static final Pattern pattern = Pattern.compile("^(.+?)(?::(\\d+))?$");
    private static LootTableManager manager;
    private static final LootPredicateManager predicate = new LootPredicateManager();
    private static final Map<Block, List<Item>> drops = new HashMap<>();

    public BlockOptionalMeta(@Nonnull Block block) {
        this.block = block;
        this.blockstates = getStates(block);
        this.stateHashes = getStateHashes(blockstates);
        this.stackHashes = getStackHashes(blockstates);
    }

    public BlockOptionalMeta(@Nonnull String selector) {
        Matcher matcher = pattern.matcher(selector);

        if (!matcher.find()) {
            throw new IllegalArgumentException("invalid block selector");
        }

        MatchResult matchResult = matcher.toMatchResult();

        block = BlockUtils.stringToBlockRequired(matchResult.group(1));
        blockstates = getStates(block);
        stateHashes = getStateHashes(blockstates);
        stackHashes = getStackHashes(blockstates);
    }

    private static Set<BlockState> getStates(@Nonnull Block block) {
        return new HashSet<>(block.getStateContainer().getValidStates());
    }

    private static ImmutableSet<Integer> getStateHashes(Set<BlockState> blockstates) {
        return ImmutableSet.copyOf(
                blockstates.stream()
                        .map(BlockState::hashCode)
                        .toArray(Integer[]::new)
        );
    }

    private static ImmutableSet<Integer> getStackHashes(Set<BlockState> blockstates) {
        return ImmutableSet.copyOf(
                blockstates.stream()
                        .map(state -> {
                            Item item = state.getBlock().getItemDropped(state, new Random(), 0);
                            if (item == null) {
                                return ItemStack.EMPTY;
                            }
                            ItemStack stack = new ItemStack(item, 1);
                            stack.setDamage(state.getBlock().damageDropped(state));
                            return stack;
                        })
                        .filter(stack -> stack != null && !stack.isEmpty())
                        .map(BlockOptionalMeta::stackHash)
                        .toArray(Integer[]::new)
        );
    }

    public Block getBlock() {
        return block;
    }

    public boolean matches(@Nonnull Block block) {
        return block == this.block;
    }

    public boolean matches(@Nonnull BlockState blockstate) {
        Block block = blockstate.getBlock();
        return block == this.block && stateHashes.contains(blockstate.hashCode());
    }

    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stackHashes.contains(stackHash(stack));
    }

    private static int stackHash(ItemStack stack) {
        if (stack == null || stack.isEmpty() || stack.getItem() == null) {
            return 0;
        }
        ResourceLocation id = Registry.ITEM.getKey(stack.getItem());
        int itemHash = id != null ? id.toString().hashCode() : stack.getItem().hashCode();
        return 31 * itemHash + stack.getDamage();
    }

    @Override
    public String toString() {
        return String.format("BlockOptionalMeta{block=%s}", block);
    }

    public BlockState getAnyBlockState() {
        if (blockstates.size() > 0) {
            return blockstates.iterator().next();
        }

        return null;
    }

    public static LootTableManager getManager() {
        if (manager == null) {
            ResourcePackList rpl = new ResourcePackList(ResourcePackInfo::new, new ServerPackFinder());
            rpl.reloadPacksFromFinders();
            IResourcePack thePack = rpl.getAllPacks().iterator().next().getResourcePack();
            IReloadableResourceManager resourceManager = new SimpleReloadableResourceManager(ResourcePackType.SERVER_DATA);
            manager = new LootTableManager(predicate);
            resourceManager.addReloadListener(manager);
            try {
                resourceManager.reloadResourcesAndThen(new ThreadPerTaskExecutor(Thread::new), new ThreadPerTaskExecutor(Thread::new), Collections.singletonList(thePack), CompletableFuture.completedFuture(Unit.INSTANCE)).get();
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        return manager;
    }

    public static LootPredicateManager getPredicateManager() {
        return predicate;
    }

}
