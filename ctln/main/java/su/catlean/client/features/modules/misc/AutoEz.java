// deobf by pusia
package su.catlean.client.features.modules.misc;

import su.catlean.utils.KotlinCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.random.Random;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ActionSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.NotationSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.api.setting.values.NotationValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.services.ClientDirectories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.player.PlayerTargetUtil;

public final class AutoEz extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoEz.class, "notation", "getNotation()Lsu/catlean/g6;", 0), new MutablePropertyReference1Impl(AutoEz.class, "openFile", "getOpenFile()Lsu/catlean/cr;", 0), new MutablePropertyReference1Impl(AutoEz.class, "openFileTotem", "getOpenFileTotem()Lsu/catlean/cr;", 0), new MutablePropertyReference1Impl(AutoEz.class, "mode", "getMode()Lsu/catlean/ol;", 0), new MutablePropertyReference1Impl(AutoEz.class, "whisperPrefix", "getWhisperPrefix()Ljava/lang/String;", 0) };
   @NotNull
   public static final AutoEz INSTANCE = new AutoEz();
   @NotNull
   private static final File file = new File(ClientDirectories.getFile(), "autoez.txt");
   @NotNull
   private static final File file2 = new File(ClientDirectories.getFile(), "autoez_totem.txt");
   @NotNull
   private static final NotationSetting notation = SettingFactory.defaultImpl(
      INSTANCE, "notation", new NotationValue("%name% will be replaced with player name"), null, null, 12, null
   );
   @NotNull
   private static final ActionSetting openDeath = SettingFactory.defaultImpl(INSTANCE, "open-death", () -> {
      OS platform = Util.getPlatform();
      File file3 = file;
      OS value = platform;
      if (!file3.exists()) {
         try {
            file3.createNewFile();
         } catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
         }
      }

      value.openFile(file3);
      return Unit.INSTANCE;
   }, null, null, 12, null);
   @NotNull
   private static final ActionSetting openTotemPop = SettingFactory.defaultImpl(INSTANCE, "open-totem-pop", () -> {
      OS platform = Util.getPlatform();
      File file22 = file2;
      OS value = platform;
      if (!file22.exists()) {
         try {
            file22.createNewFile();
         } catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
         }
      }

      value.openFile(file22);
      return Unit.INSTANCE;
   }, null, null, 12, null);
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", AutoEz.Mode.GLOBAL, null, null, 12, null);
   @NotNull
   private static final StringSetting prefix = SettingFactory.defaultImpl(
      INSTANCE, "prefix", "msg", null, () -> INSTANCE.getMode() == AutoEz.Mode.WHISPERS, 4, null
   );
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   private static long time;
   private static long time2;
   @NotNull
   private static List<Object> items = CollectionsKt.emptyList();
   @NotNull
   private static List<Object> items2 = CollectionsKt.emptyList();

   private AutoEz() {
      super("auto-ez", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final AutoEz.Mode getMode() {
      return (AutoEz.Mode)mode.computeObject(this, $$delegatedProperties[3]);
   }

   private final String getWhisperPrefix() {
      return (String)prefix.computeObject(this, $$delegatedProperties[4]);
   }

   @Override
   public void update2() {
      items2 = this.computeItems(file);
      items = this.computeItems(file2);
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      if (this.isFlag6()) {
         if (MinecraftHolder.getLocalPlayer().tickCount % 60 == 0 && file.exists() && file.lastModified() > time) {
            items2 = this.computeItems(file);
            time = file.lastModified();
         }

         if (MinecraftHolder.getLocalPlayer().tickCount % 60 == 0 && file2.exists() && file2.lastModified() > time2) {
            items = this.computeItems(file2);
            time2 = file2.lastModified();
         }

         for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
            if ((Intrinsics.areEqual(player, AutoCrystal.INSTANCE.getPlayer()) || Intrinsics.areEqual(player, Aura.INSTANCE.getEntity()))
               && (player.isDeadOrDying() || player.getHealth() <= 0.0F)) {
               this.applyIsEmpty(player.getGameProfile().name(), items2);
            }
         }
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundEntityEventPacket && this.isFlag6()) {
         if (((ClientboundEntityEventPacket)receivePacket.getPacket()).getEventId() == 3
            || ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEventId() == 60) {
            Entity entity = ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEntity((Level)MinecraftHolder.getClientLevel());
            if (entity != null) {
               Entity entity2 = entity;
               if ((Intrinsics.areEqual(entity2, AutoCrystal.INSTANCE.getPlayer()) || Intrinsics.areEqual(entity2, Aura.INSTANCE.getEntity()))
                  && entity2 instanceof Player) {
                  INSTANCE.applyIsEmpty(String.valueOf(((Player)entity2).getName().tryCollapseToString()), items2);
               }
            }
         }

         if (((ClientboundEntityEventPacket)receivePacket.getPacket()).getEventId() == 35) {
            Entity entity3 = ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEntity((Level)MinecraftHolder.getClientLevel());
            if (entity3 != null) {
               Entity entity4 = entity3;
               if (Intrinsics.areEqual(entity4, AutoCrystal.INSTANCE.getPlayer()) || Intrinsics.areEqual(entity4, Aura.INSTANCE.getEntity())) {
                  INSTANCE.applyIsEmpty(String.valueOf(entity4.getName().tryCollapseToString()), items);
               }
            }
         }
      }
   }

   private final void applyIsEmpty(String text, List items) {
      TeamCommunication.INSTANCE.getAtomicBoolean().set(true);
      if (stopWatch.testValue(450) && !items.isEmpty()) {
         String replacedefault = KotlinCompat.replace(
            KotlinCompat.replace((String)CollectionsKt.random(items, (Random)Random.Default), "%name%", text, false, 4, null),
            "%count%",
            String.valueOf(PlayerTargetUtil.INSTANCE.computeValue(text)),
            false,
            4,
            null
         );
         if (KotlinCompat.startsWith(replacedefault, "/", false, 2, null)) {
            byte byteValue = 0;
            byte byteValue2 = 1;
            replacedefault = StringsKt.removeRange(replacedefault, byteValue, byteValue2).toString();
            MinecraftHolder.getClientPacketListener().sendCommand(replacedefault);
         } else {
            switch (AutoEz.WhenMappings.values[this.getMode().ordinal()]) {
               case 1:
                  MinecraftHolder.getClientPacketListener().sendChat("!" + replacedefault);
                  break;
               case 2:
                  MinecraftHolder.getClientPacketListener().sendChat(replacedefault);
                  break;
               case 3:
                  MinecraftHolder.getClientPacketListener().sendCommand(this.getWhisperPrefix() + " " + text + " " + replacedefault);
                  break;
               case 4:
                  ChatUtil.defaultImpl(this, replacedefault, false, 2, null);
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }
         }

         stopWatch.update();
      } else if (items.isEmpty()) {
         ChatUtil.defaultImpl(this, ChatUtil.defaultImpl(this, "empty", new Object[0], false, 4, null), false, 2, null);
      }

      TeamCommunication.INSTANCE.getAtomicBoolean().set(false);
   }

   private final List<Object> computeItems(File file) {
      try {
      if (file.createNewFile()) {
         return CollectionsKt.emptyList();
      } else {
         if (!file.exists()) {
            file.createNewFile();
         }

         Iterable<String> linesdefault = KotlinCompat.readLines(file, null, 1, null);
         boolean flag2 = false;
         var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(linesdefault, 10));

         for (Object object : linesdefault) {
            String text = (String)object;
            arrayList.add(StringsKt.trim(text).toString());
         }

         linesdefault = arrayList;
         flag2 = false;
         arrayList = new ArrayList();

         for (Object object2 : linesdefault) {
            String text2 = (String)object2;
            if (!StringsKt.isBlank(text2)) {
               arrayList.add(object2);
            }
         }

         return arrayList;
      }
      } catch (java.io.IOException exception) {
         throw new RuntimeException(exception);
      }
   }

   static {
   }

   public static enum Mode {
      GLOBAL,
      LOCAL,
      WHISPERS,
      SILENT;

      private static final AutoEz.Mode[] modes = $values();
      private static final EnumEntries<AutoEz.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<AutoEz.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoEz.Mode[] $values() {
         return new AutoEz.Mode[]{GLOBAL, LOCAL, WHISPERS, SILENT};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AutoEz.Mode.values().length];

         try {
            values2[AutoEz.Mode.GLOBAL.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AutoEz.Mode.LOCAL.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[AutoEz.Mode.WHISPERS.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[AutoEz.Mode.SILENT.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         values = values2;
      }
   }
}