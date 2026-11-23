// deobf by pusia
package su.catlean.client.commands.impl;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.commands.Command;
import su.catlean.client.features.modules.equip.AutoArmor;
import su.catlean.client.services.ThreadManager;
import su.catlean.utils.MinecraftHolder;

public final class FixAllCommand extends Command {
   @NotNull
   public static final FixAllCommand INSTANCE = new FixAllCommand();

   private FixAllCommand() {
      super("fixall");
   }

   @Override
   public void applyExecutes(LiteralArgumentBuilder builder) {
      builder.then(this.computeRequiredArgumentBuilder("delay", (ArgumentType)IntegerArgumentType.integer()).executes(arg -> {
         long value = ((Number)arg.getArgument("delay", int.class)).intValue();
         INSTANCE.applyExecute(value);
         return 1;
      }));
      builder.executes(arg2 -> {
         INSTANCE.applyExecute(100L);
         return 1;
      });
   }

   public final void applyExecute(long time) {
      MinecraftHolder.getClientPacketListener().sendCommand("fix hand");
      MinecraftHolder.getMultiPlayerGameMode()
         .handleContainerInput(
            MinecraftHolder.getLocalPlayer().containerMenu.containerId,
            1,
            MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(),
            ContainerInput.SWAP,
            (Player)MinecraftHolder.getLocalPlayer()
         );
      ThreadManager instance = ThreadManager.INSTANCE;
      byte byteValue = 0;
      instance.getExecutorService().execute(new FixAllCommand.FixTask(byteValue, time));
   }

   public static final class FixTask implements Runnable {
      public final int value;
      public final long time;

      public FixTask(int value, long time) {
         this.value = value;
         this.time = time;
      }

      @Override
      public final void run() {
         try {
         Thread.sleep(this.value);

         for (int i = 5; i < 45; i++) {
            AutoArmor.INSTANCE.getPlayerUtil().updateValue(-5);
            ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(i >= 36 ? i - 36 : i);
            if (!item.isEmpty() && item.isBarVisible() || i <= 8) {
               MinecraftHolder.getMultiPlayerGameMode()
                  .handleContainerInput(
                     MinecraftHolder.getLocalPlayer().containerMenu.containerId,
                     i,
                     MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(),
                     ContainerInput.SWAP,
                     (Player)MinecraftHolder.getLocalPlayer()
                  );
               Thread.sleep(this.time);
               MinecraftHolder.getClientPacketListener().sendCommand("fix hand");
               Thread.sleep(this.time);
               MinecraftHolder.getMultiPlayerGameMode()
                  .handleContainerInput(
                     MinecraftHolder.getLocalPlayer().containerMenu.containerId,
                     i,
                     MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(),
                     ContainerInput.SWAP,
                     (Player)MinecraftHolder.getLocalPlayer()
                  );
               Thread.sleep(this.time);
            }
         }

         MinecraftHolder.getMultiPlayerGameMode()
            .handleContainerInput(
               MinecraftHolder.getLocalPlayer().containerMenu.containerId,
               1,
               MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(),
               ContainerInput.SWAP,
               (Player)MinecraftHolder.getLocalPlayer()
            );
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
         }
      }
   }
}