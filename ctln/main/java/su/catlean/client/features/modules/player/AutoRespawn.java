// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.ThreadManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.network.PacketUtil;

public final class AutoRespawn extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoRespawn.class, "deathCoords", "getDeathCoords()Z", 0), new PropertyReference1Impl(AutoRespawn.class, "deathCommand", "getDeathCommand()Lsu/catlean/d6;", 0), new PropertyReference1Impl(AutoRespawn.class, "kit", "getKit()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoRespawn.class, "command", "getCommand()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoRespawn.class, "commandTiming", "getCommandTiming()Lsu/catlean/u1;", 0) };
   @NotNull
   public static final AutoRespawn INSTANCE = new AutoRespawn();
   @NotNull
   private static final BooleanSetting deathCoords = SettingFactory.defaultImpl(INSTANCE, "death-coords", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting deathCommand = SettingFactory.defaultImpl(INSTANCE, "death-command", AutoRespawn.DeathCommand.KIT, null, null, 12, null);
   @NotNull
   private static final StringSetting kitName = SettingFactory.defaultImpl(
      INSTANCE, "kit-name", "kitname", null, () -> INSTANCE.getDeathCommand() == AutoRespawn.DeathCommand.KIT, 4, null
   );
   @NotNull
   private static final StringSetting command = SettingFactory.defaultImpl(
      INSTANCE, "command", "/ebal", null, () -> INSTANCE.getDeathCommand() == AutoRespawn.DeathCommand.CUSTOM, 4, null
   );
   @NotNull
   private static final EnumSetting commandTiming = SettingFactory.defaultImpl(
      INSTANCE, "command-timing", AutoRespawn.CommandTiming.DELAYED, null, null, 12, null
   );
   private static boolean flag;
   private static int value;

   private AutoRespawn() {
      super("auto-respawn", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final boolean getDeathCoords() {
      return (Boolean)deathCoords.computeObject(this, $$delegatedProperties[0]);
   }

   private final AutoRespawn.DeathCommand getDeathCommand() {
      return (AutoRespawn.DeathCommand)deathCommand.computeObject(this, $$delegatedProperties[1]);
   }

   private final String getKit() {
      return (String)kitName.computeObject(this, $$delegatedProperties[2]);
   }

   private final String getCommand() {
      return (String)command.computeObject(this, $$delegatedProperties[3]);
   }

   private final AutoRespawn.CommandTiming getCommandTiming() {
      return (AutoRespawn.CommandTiming)commandTiming.computeObject(this, $$delegatedProperties[4]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getMinecraft().gui.screen() instanceof DeathScreen) {
         if (flag) {
            value++;
            if (this.getDeathCoords()) {
               ChatUtil.defaultImpl(
                  this,
                  ChatUtil.defaultImpl(this, "respawn-message", new Object[0], false, 4, null)
                     + " X: "
                     + (int)MinecraftHolder.getLocalPlayer().getX()
                     + " Z: "
                     + (int)MinecraftHolder.getLocalPlayer().getZ(),
                  false,
                  2,
                  null
               );
            }

            MinecraftHolder.getLocalPlayer().respawn();
            MinecraftHolder.getMinecraft().gui.setScreen(null);
            if (this.getCommandTiming() == AutoRespawn.CommandTiming.DELAYED) {
               ThreadManager instance = ThreadManager.INSTANCE;
               short shortValue = 1000;
               instance.getExecutorService().execute(new AutoRespawn.Task(shortValue));
            }

            flag = false;
         }
      } else {
         flag = true;
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundEntityEventPacket
         && MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().level != null) {
         ClientboundEntityEventPacket packet = (ClientboundEntityEventPacket)receivePacket.getPacket();
         if ((packet.getEventId() == 3 || packet.getEventId() == 60)
            && Intrinsics.areEqual(packet.getEntity((Level)MinecraftHolder.getClientLevel()), MinecraftHolder.getLocalPlayer())
            && this.getCommandTiming() == AutoRespawn.CommandTiming.INSTANT) {
            this.applyOrdinal();
         }
      }
   }

   public final void applyOrdinal() {
      if (MinecraftHolder.getMinecraft().player != null) {
         switch (AutoRespawn.WhenMappings.values[this.getDeathCommand().ordinal()]) {
            case 1:
               PacketUtil.applySendCommand("kit " + this.getKit());
               break;
            case 2:
               PacketUtil.applySendCommand("back");
               break;
            case 3:
               PacketUtil.applySendCommand("home");
               break;
            case 4:
               PacketUtil.applySendCommand(this.getCommand());
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }
   }

   static {
   }

   public static enum CommandTiming {
      INSTANT,
      DELAYED;

      private static final AutoRespawn.CommandTiming[] commandTimings = $values();
      private static final EnumEntries<AutoRespawn.CommandTiming> enumEntries = EnumEntriesKt.enumEntries(commandTimings);

      private CommandTiming() {
      }

      @NotNull
      public static EnumEntries<AutoRespawn.CommandTiming> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoRespawn.CommandTiming[] $values() {
         return new AutoRespawn.CommandTiming[]{INSTANT, DELAYED};
      }
   }

   public static enum DeathCommand {
      KIT,
      HOME,
      BACK,
      CUSTOM;

      private static final AutoRespawn.DeathCommand[] deathCommands = $values();
      private static final EnumEntries<AutoRespawn.DeathCommand> enumEntries = EnumEntriesKt.enumEntries(deathCommands);

      private DeathCommand() {
      }

      @NotNull
      public static EnumEntries<AutoRespawn.DeathCommand> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoRespawn.DeathCommand[] $values() {
         return new AutoRespawn.DeathCommand[]{KIT, HOME, BACK, CUSTOM};
      }
   }

   public static final class Task implements Runnable {
      public final int value;

      public Task(int value) {
         this.value = value;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
         AutoRespawn.INSTANCE.applyOrdinal();
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AutoRespawn.DeathCommand.values().length];

         try {
            values2[AutoRespawn.DeathCommand.KIT.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AutoRespawn.DeathCommand.BACK.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[AutoRespawn.DeathCommand.HOME.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[AutoRespawn.DeathCommand.CUSTOM.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         values = values2;
      }
   }
}