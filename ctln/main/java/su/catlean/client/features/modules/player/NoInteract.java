// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.TargetBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.InteractBlockEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InteractionManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class NoInteract extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(NoInteract.class, "allBlocks", "getAllBlocks()Z", 0)};
   @NotNull
   public static final NoInteract INSTANCE = new NoInteract();
   @NotNull
   private static final BooleanSetting allBlocks = SettingFactory.defaultImpl(INSTANCE, "all-blocks", false, null, null, 12, null);

   private NoInteract() {
      super("no-interact", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final boolean getAllBlocks() {
      return (Boolean)allBlocks.computeObject(this, $$delegatedProperties[0]);
   }

   @Flow
   private final void onInteractBlock(InteractBlockEvent interactBlockEvent) {
      Block block = MinecraftHolder.getClientLevel().getBlockState(interactBlockEvent.getHitResult().getBlockPos()).getBlock();
      if (this.getAllBlocks()
         || InteractionManager.INSTANCE.testBlock(block)
         || block instanceof FenceBlock
         || block instanceof FenceGateBlock
         || block instanceof TrapDoorBlock
         || block instanceof DoorBlock
         || block instanceof TargetBlock
         || block instanceof RedStoneOreBlock) {
         interactBlockEvent.cancel();
      }

      if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.PLAYER_HEAD)
            && interactBlockEvent.getHand() == InteractionHand.OFF_HAND
         || Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.PLAYER_HEAD)
            && interactBlockEvent.getHand() == InteractionHand.MAIN_HAND) {
         interactBlockEvent.cancel();
      }
   }

   static {
   }
}