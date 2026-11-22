package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.skycore.client.SkycoreClient;
import net.skycore.client.event.PlayerTickEndEvent;
import net.skycore.client.event.Subscription;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.ModeSetting;

/**
 * Eagle-only Scaffold module for Minecraft 26.2 / SkyCore.
 *
 * <p>Only the Eagle path is retained. It holds the sneak key while the block below
 * the player is air, preventing the
 * player from walking off an edge without changing the selected item or rotation.
 */
public final class ScaffoldModule extends ClientModule {
   private enum Mode {
      Eagle
   }

   private final ModeSetting mode = this.add(new ModeSetting("Mode", "Eagle", "Eagle"));
   private boolean wasSneaking;
   private Subscription tickSubscription;

   public ScaffoldModule() {
      super("Scaffold", "Eagle edge protection", ModuleCategory.MOVEMENT);
   }

   @Override
   protected void onEnable() {
      this.wasSneaking = false;
      if (this.tickSubscription != null) {
         this.tickSubscription.close();
      }
      this.tickSubscription = SkycoreClient.events().subscribe(PlayerTickEndEvent.class, event -> this.onPlayerTick());
   }

   @Override
   protected void onDisable() {
      Minecraft minecraft = Minecraft.getInstance();
      if (this.wasSneaking && minecraft != null) {
         minecraft.options.keyShift.setDown(false);
         this.wasSneaking = false;
      }
      if (this.tickSubscription != null) {
         this.tickSubscription.close();
         this.tickSubscription = null;
      }
   }

   private void onPlayerTick() {
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.player == null || minecraft.level == null || minecraft.gameMode == null) {
         return;
      }

      BlockPos checkPos = BlockPos.containing(
         minecraft.player.getX(), minecraft.player.getY() - 1.0, minecraft.player.getZ());
      boolean shouldSneak = minecraft.level.getBlockState(checkPos).isAir();
      minecraft.options.keyShift.setDown(shouldSneak);
      this.wasSneaking = shouldSneak;
   }

   public String suffix() {
      return this.mode.get();
   }
}
