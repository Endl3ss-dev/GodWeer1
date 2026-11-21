package net.skycore.client.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.InteractionHand;

/** Local BlockItem use outcome, not an authoritative server placement acknowledgement. */
public record BlockUseObservationEvent(BlockPos target, BlockHitResult hit,
                                       InteractionHand hand, String item,
                                       boolean consumed, boolean predictedChange) { }
