// deobf by pusia
package su.catlean.client.features.modules.misc;

import su.catlean.utils.KotlinCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.FilesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.random.Random;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import net.minecraft.util.Util;
import net.minecraft.util.Util.OS;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.ActionSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.ClientDirectories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;

public final class Spammer extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Spammer.class, "openFile", "getOpenFile()Lsu/catlean/cr;", 0), new MutablePropertyReference1Impl(Spammer.class, "mode", "getMode()Lsu/catlean/c4;", 0), new MutablePropertyReference1Impl(Spammer.class, "whisperPrefix", "getWhisperPrefix()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Spammer.class, "antiSpam", "getAntiSpam()Z", 0), new MutablePropertyReference1Impl(Spammer.class, "delay", "getDelay()F", 0) };
   @NotNull
   public static final Spammer INSTANCE = new Spammer();
   @NotNull
   private static final ActionSetting openFile = SettingFactory.defaultImpl(INSTANCE, "open-file", () -> {
      OS platform = Util.getPlatform();
      File file2 = Spammer.file;
      OS value = platform;
      if (!file2.exists()) {
         try {
            file2.createNewFile();
         } catch (java.io.IOException exception) {
            throw new RuntimeException(exception);
         }
      }

      value.openFile(file2);
      return Unit.INSTANCE;
   }, null, null, 12, null);
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", Spammer.Mode.GLOBAL, null, null, 12, null);
   @NotNull
   private static final StringSetting prefix = SettingFactory.defaultImpl(
      INSTANCE, "prefix", "msg", null, () -> INSTANCE.getMode() == Spammer.Mode.WHISPERS, 4, null
   );
   @NotNull
   private static final BooleanSetting antiSpam = SettingFactory.defaultImpl(INSTANCE, "anti-spam", false, null, null, 12, null);
   @NotNull
   private static final FloatSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 5.0F, RangesKt.rangeTo(0.0F, 30.0F), null, null, 24, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static List<Object> items = CollectionsKt.emptyList();
   private static long time;
   @NotNull
   private static final File file = new File(ClientDirectories.getFile(), "spammer.txt");

   private Spammer() {
      super("spammer", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final Spammer.Mode getMode() {
      return (Spammer.Mode)mode.computeObject(this, $$delegatedProperties[1]);
   }

   private final String getWhisperPrefix() {
      return (String)prefix.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getAntiSpam() {
      return (Boolean)antiSpam.computeObject(this, $$delegatedProperties[3]);
   }

   private final float getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[4])).floatValue();
   }

   @Override
   public void update2() {
      items = this.getItems();
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent event) {
      if (MinecraftHolder.getLocalPlayer().tickCount % 60 == 0 && file.exists() && file.lastModified() > time) {
         items = this.getItems();
      }

      if (stopWatch.testValue((int)(this.getDelay() * 1000)) && !items.isEmpty()) {
         String random = CollectionsKt.random(items, (Random)Random.Default) + (this.getAntiSpam() ? " " + this.getText2() : "");
         if (KotlinCompat.startsWith(random, "/", false, 2, null)) {
            byte byteValue = 0;
            byte byteValue2 = 1;
            random = StringsKt.removeRange(random, byteValue, byteValue2).toString();
            MinecraftHolder.getClientPacketListener().sendCommand(random);
         } else {
            switch (Spammer.WhenMappings.values[this.getMode().ordinal()]) {
               case 1:
                  MinecraftHolder.getClientPacketListener().sendChat("!" + random);
                  break;
               case 2:
                  MinecraftHolder.getClientPacketListener().sendChat(random);
                  break;
               case 3:
                  MinecraftHolder.getClientPacketListener().sendCommand(this.getWhisperPrefix() + " " + this.getText() + " " + random);
                  break;
               default:
                  throw new NoWhenBranchMatchedException();
            }
         }

         stopWatch.update();
      } else if (items.isEmpty()) {
         this.updateText10(ChatUtil.defaultImpl(this, "empty", new Object[0], false, 4, null));
      }
   }

   private final String getText() {
      return (String)CollectionsKt.randomOrNull(
         SequencesKt.toList(
            SequencesKt.filter(
               SequencesKt.map(CollectionsKt.asSequence(MinecraftHolder.getClientPacketListener().getOnlinePlayers()), arg -> arg.getProfile().name()),
               arg2 -> Pattern.matches("^\\w{3,16}$", arg2)
            )
         ),
         (Random)Random.Default
      );
   }

   private final String getText2() {
      return "[" + (char)(Random.Default.nextInt(26) + 97) + Random.Default.nextInt(10) + (char)(Random.Default.nextInt(26) + 97) + "]";
   }

   private final List<Object> getItems() {
      try {
      if (file.createNewFile()) {
         return CollectionsKt.emptyList();
      } else {
         time = file.lastModified();
         File file2 = file;
         if (!file2.exists()) {
            file2.createNewFile();
         }

         Iterable<String> linesdefault = KotlinCompat.readLines(file2, null, 1, null);
         boolean flag2 = false;
         var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(linesdefault, 10));

         for (Object object : linesdefault) {
            String text = (String)object;
            arrayList.add(StringsKt.trim(text).toString());
         }

         var value = arrayList;
         flag2 = false;
         arrayList = new ArrayList();

         for (Object object2 : value) {
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
      WHISPERS;

      private static final Spammer.Mode[] modes = $values();
      private static final EnumEntries<Spammer.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<Spammer.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final Spammer.Mode[] $values() {
         return new Spammer.Mode[]{GLOBAL, LOCAL, WHISPERS};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         Module[] modules = new Module[2];
         int[] values2 = new int[Spammer.Mode.values().length];

         try {
            values2[Spammer.Mode.GLOBAL.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Spammer.Mode.LOCAL.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Spammer.Mode.WHISPERS.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}