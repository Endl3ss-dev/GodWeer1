package org.privates.client.managers.events.other;

import net.minecraft.block.Block;
import net.minecraft.forge.api.Event;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class PlaceObsidianEvent extends Event {
    private Block block;
    private BlockPos pos;

    public Block getBlock() {
        return this.block;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof PlaceObsidianEvent other)) {
            return false;
        } else if (!other.canEqual(this)) {
            return false;
        } else {
            Object this$block = this.getBlock();
            Object other$block = other.getBlock();
            if (Objects.equals(this$block, other$block)) {
                Object this$pos = this.getPos();
                Object other$pos = other.getPos();
                return Objects.equals(this$pos, other$pos);
            } else {
                return false;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof PlaceObsidianEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Object $block = this.getBlock();
        result = result * 59 + ($block == null ? 43 : $block.hashCode());
        Object $pos = this.getPos();
        return result * 59 + ($pos == null ? 43 : $pos.hashCode());
    }

    @Override
    public String toString() {
        return "PlaceObsidianEvent(block=" + this.getBlock() + ", pos=" + this.getPos() + ")";
    }

    public PlaceObsidianEvent(Block block, BlockPos pos) {
        this.block = block;
        this.pos = pos;
    }
}
