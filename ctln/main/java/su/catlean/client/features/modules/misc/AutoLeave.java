// deobf by pusia
package su.catlean.client.features.modules.misc;

import su.catlean.utils.KotlinCompat;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerNameUtil;

public final class AutoLeave extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoLeave.class, "autoDisable", "getAutoDisable()Z", 0), new PropertyReference1Impl(AutoLeave.class, "fastLeave", "getFastLeave()Z", 0), new PropertyReference1Impl(AutoLeave.class, "lowHp", "getLowHp()Z", 0), new PropertyReference1Impl(AutoLeave.class, "totems", "getTotems()Z", 0), new PropertyReference1Impl(AutoLeave.class, "minTotems", "getMinTotems()I", 0), new PropertyReference1Impl(AutoLeave.class, "minHp", "getMinHp()F", 0), new PropertyReference1Impl(AutoLeave.class, "players", "getPlayers()Lsu/catlean/ql;", 0), new MutablePropertyReference1Impl(AutoLeave.class, "command", "getCommand()Ljava/lang/String;", 0), new PropertyReference1Impl(AutoLeave.class, "distance", "getDistance()I", 0) };
   @NotNull
   public static final AutoLeave INSTANCE = new AutoLeave();
   @NotNull
   private static final BooleanSetting disable = SettingFactory.defaultImpl(INSTANCE, "disable", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting booleanSetting = SettingFactory.defaultImpl(INSTANCE, "InstantLeave", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting hp = SettingFactory.defaultImpl(INSTANCE, "hp", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting totems = SettingFactory.defaultImpl(INSTANCE, "totems", false, null, null, 12, null);
   @NotNull
   private static final IntSetting minTotems = SettingFactory.defaultImpl(
      INSTANCE, "min-totems", 2, new IntRange(0, 10), null, () -> INSTANCE.getTotems(), 8, null
   );
   @NotNull
   private static final FloatSetting minHp = SettingFactory.defaultImpl(
      INSTANCE, "min-hp", 8.0F, RangesKt.rangeTo(1.0F, 20.0F), null, () -> INSTANCE.getLowHp(), 8, null
   );
   @NotNull
   private static final EnumSetting players = SettingFactory.defaultImpl(INSTANCE, "players", AutoLeave.Players.Leave, null, null, 12, null);
   @NotNull
   private static final StringSetting command = SettingFactory.defaultImpl(
      INSTANCE, "command", "hub", null, () -> INSTANCE.getPlayers() == AutoLeave.Players.Command, 4, null
   );
   @NotNull
   private static final IntSetting distance = SettingFactory.defaultImpl(
      INSTANCE, "distance", 256, new IntRange(4, 256), null, () -> INSTANCE.getPlayers() != AutoLeave.Players.None, 8, null
   );
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private AutoLeave() {
      super("auto-leave", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final boolean getAutoDisable() {
      return (Boolean)disable.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getFastLeave() {
      return (Boolean)booleanSetting.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getLowHp() {
      return (Boolean)hp.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getTotems() {
      return (Boolean)totems.computeObject(this, $$delegatedProperties[3]);
   }

   private final int getMinTotems() {
      return ((Number)minTotems.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final float getMinHp() {
      return ((Number)minHp.computeObject(this, $$delegatedProperties[5])).floatValue();
   }

   private final AutoLeave.Players getPlayers() {
      return (AutoLeave.Players)players.computeObject(this, $$delegatedProperties[6]);
   }

   private final String getCommand() {
      return (String)command.computeObject(this, $$delegatedProperties[7]);
   }

   private final int getDistance() {
      return ((Number)distance.computeObject(this, $$delegatedProperties[8])).intValue();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
         if (!Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer())
            && !PlayerNameUtil.checkGetGameProfile((Player)player)
            && this.getPlayers() != AutoLeave.Players.None
            && this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(this.computeVec3((Entity)player)) <= this.getDistance()) {
            switch (AutoLeave.WhenMappings.values[this.getPlayers().ordinal()]) {
               case 1:
                  if (stopWatch.testValue2(1000)) {
                     if (this.getAutoDisable()) {
                        this.applyUnplug();
                     }

                     ChatUtil.defaultImpl(this, ChatUtil.defaultImpl(this, "player", new Object[0], false, 4, null), false, 2, null);
                     MinecraftHolder.getLocalPlayer().connection.sendCommand(KotlinCompat.replace(this.getCommand(), "/", "", false, 4, null));
                  }

                  return;
               case 2:
                  this.applyDisconnect(ChatUtil.defaultImpl(this, "player", new Object[0], false, 4, null));
                  return;
               case 3:
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }
         }
      }

      if (this.getTotems()) {
         Object object = null;
         byte byteValue = 2;
         boolean flag = false;
         Item toteM_OF_UNDYING = Items.TOTEM_OF_UNDYING;
         if (InventoryManager.defaultImpl(InventoryManager.INSTANCE, toteM_OF_UNDYING, flag, byteValue, object) <= this.getMinTotems()) {
            this.applyDisconnect(ChatUtil.defaultImpl(this, "totems", new Object[0], false, 4, null));
         }
      }

      if (MinecraftHolder.getLocalPlayer().getHealth() < this.getMinHp() && this.getLowHp()) {
         this.applyDisconnect(ChatUtil.defaultImpl(this, "hp", new Object[0], false, 4, null));
      }
   }

   private final void applyDisconnect(String text) {
      if (this.getAutoDisable()) {
         this.updateText10(text);
      }

      if (this.getFastLeave()) {
         Packet serverboundSetCarriedItemPacket = (Packet)(new ServerboundSetCarriedItemPacket(-1337));
         PacketUtil.applySend(serverboundSetCarriedItemPacket);
      }

      MinecraftHolder.getClientPacketListener().getConnection().disconnect(Component.nullToEmpty("[CatLean] " + text));
   }

   static {
   }

   public static enum Players {
      None,
      Command,
      Leave;

      private static final AutoLeave.Players[] playerss = $values();
      private static final EnumEntries<AutoLeave.Players> enumEntries = EnumEntriesKt.enumEntries(playerss);

      private Players() {
      }

      @NotNull
      public static EnumEntries<AutoLeave.Players> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoLeave.Players[] $values() {
         return new AutoLeave.Players[]{None, Command, Leave};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AutoLeave.Players.values().length];

         try {
            values2[AutoLeave.Players.Command.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AutoLeave.Players.Leave.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[AutoLeave.Players.None.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}