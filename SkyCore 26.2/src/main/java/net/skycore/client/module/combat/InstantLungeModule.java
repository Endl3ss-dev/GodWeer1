// Adapted from Prestige InstantLunge (dev.zprestige.prestige.e0 / S0295)
package net.skycore.client.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.SpearHelper;
import net.skycore.client.event.WorldChangeEvent;
import net.skycore.client.mixin.MinecraftGetRightClickDelayAccessor;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.util.UseItemHelper;

public final class InstantLungeModule extends ClientModule {
   public static final InstantLungeModule INSTANCE = new InstantLungeModule();

   private static final int MIN_FOOD_LEVEL = 6;

   private final BooleanSetting swapBack = this.add(
      new BooleanSetting("Swap Back", "Returns to the previous slot after the lunge.", true)
   );
   private final NumberSetting swapBackDelayMin = this.add(
      new NumberSetting("Swap Back Delay Min", "Minimum random time before swapping back (ms).", 20.0, 0.0, 300.0, 1.0, "ms")
   );
   private final NumberSetting swapBackDelayMax = this.add(
      new NumberSetting("Swap Back Delay Max", "Maximum random time before swapping back (ms).", 70.0, 0.0, 300.0, 1.0, "ms")
   );

   private int previousSlot = -1;
   private long swapBackAtMs;
   private boolean awaitingSwapBack;

   public InstantLungeModule() {
      super("Instant Lunge", "Instantly lunges with the spear without needing to swap or click", ModuleCategory.COMBAT);
      this.swapBackDelayMin.visibleWhen(this::isSwapBackEnabled);
      this.swapBackDelayMax.visibleWhen(this::isSwapBackEnabled);
      SkycoreClient.events().subscribe(WorldChangeEvent.class, this::onWorldChange);
   }

   private boolean isSwapBackEnabled() {
      return this.swapBack.get();
   }

   @Override
   protected void onEnable() {
      this.previousSlot = -1;
      this.swapBackAtMs = 0L;
      this.awaitingSwapBack = false;
      LocalPlayer player = Minecraft.getInstance().player;
      if (player != null) {
         SpearHelper.warmHotbarEnchantCache(player);
      }
   }

   @Override
   protected void onDisable() {
      int slot = this.previousSlot;
      this.previousSlot = -1;
      this.swapBackAtMs = 0L;
      this.awaitingSwapBack = false;
      if (slot >= 0 && slot < SpearHelper.HOTBAR_SIZE && Minecraft.getInstance().player != null) {
         UseItemHelper.switchToHotbarSlot(slot);
      }
   }

   private void onWorldChange(WorldChangeEvent event) {
      this.previousSlot = -1;
      this.swapBackAtMs = 0L;
      this.awaitingSwapBack = false;
      if (this.isEnabled()) {
         this.setEnabled(false);
      }
   }

   @Override
   protected void onTick() {
      Minecraft mc = Minecraft.getInstance();
      LocalPlayer player = mc.player;
      if (player == null || mc.level == null || mc.gameMode == null) {
         this.previousSlot = -1;
         this.awaitingSwapBack = false;
         this.setEnabled(false);
         return;
      }

      if (this.awaitingSwapBack) {
         if (System.currentTimeMillis() >= this.swapBackAtMs) {
            int slot = this.previousSlot;
            this.previousSlot = -1;
            this.awaitingSwapBack = false;
            if (slot >= 0 && slot < SpearHelper.HOTBAR_SIZE) {
               UseItemHelper.switchToHotbarSlot(slot);
            }
            this.setEnabled(false);
         }
         return;
      }

      if (mc.gui.screen() != null
         || !player.isAlive()
         || player.isSpectator()
         || player.isUsingItem()
         || player.getFoodData().getFoodLevel() <= MIN_FOOD_LEVEL) {
         return;
      }

      int spearSlot = findLungeSpearSlot(player);
      if (spearSlot == -1) {
         return;
      }

      int currentSlot = player.getInventory().getSelectedSlot();
      boolean swapped = currentSlot != spearSlot;
      if (swapped) {
         UseItemHelper.switchToHotbarSlot(spearSlot);
      }

      MinecraftGetRightClickDelayAccessor accessor = (MinecraftGetRightClickDelayAccessor)mc;
      accessor.skycore$setMissTime(0);
      boolean attacked = accessor.skycore$startAttack();
      if (!attacked) {
         if (swapped) {
            UseItemHelper.switchToHotbarSlot(currentSlot);
         }
         return;
      }

      if (swapped && this.swapBack.get()) {
         double d1 = this.swapBackDelayMin.get();
         double d2 = this.swapBackDelayMax.get();
         double minMs = Math.min(d1, d2);
         double maxMs = Math.max(d1, d2);
         long delayMs = maxMs <= minMs
            ? Math.round(minMs)
            : Math.round(minMs + ThreadLocalRandom.current().nextDouble(maxMs - minMs));
         if (delayMs <= 0L) {
            UseItemHelper.switchToHotbarSlot(currentSlot);
            this.setEnabled(false);
         } else {
            this.previousSlot = currentSlot;
            this.swapBackAtMs = System.currentTimeMillis() + delayMs;
            this.awaitingSwapBack = true;
         }
      } else {
         this.setEnabled(false);
      }
   }

   private static int findLungeSpearSlot(LocalPlayer player) {
      int bestSlot = -1;
      int bestLevel = 0;
      for (int slot = 0; slot < SpearHelper.HOTBAR_SIZE; slot++) {
         ItemStack stack = player.getInventory().getItem(slot);
         if (SpearHelper.isSpear(stack)) {
            int level = SpearHelper.lungeLevel(stack);
            if (level > bestLevel) {
               bestLevel = level;
               bestSlot = slot;
            }
         }
      }
      return bestSlot;
   }
}
