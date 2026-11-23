// deobf by pusia
package su.catlean.client.features.modules.misc;

import kotlin.collections.ArrayDeque;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.LongRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundCommandSuggestionsPacket;
import net.minecraft.network.protocol.game.ServerboundCommandSuggestionPacket;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.PostTasksProcessEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.chat.MessageType;
import su.catlean.utils.network.PacketUtil;

public final class FastLatency extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(FastLatency.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(FastLatency.class, "spikeDetector", "getSpikeDetector()Lsu/catlean/tr;", 0), new PropertyReference1Impl(FastLatency.class, "messageDelay", "getMessageDelay()I", 0) };
   @NotNull
   public static final FastLatency INSTANCE = new FastLatency();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 80, new IntRange(0, 1000), null, null, 24, null);
   @NotNull
   private static final EnumSetting spikeDetector = SettingFactory.defaultImpl(INSTANCE, "spike-detector", FastLatency.SpikeDetector.CHAT, null, null, 12, null);
   @NotNull
   private static final IntSetting messageDelay = SettingFactory.defaultImpl(INSTANCE, "message-delay", 500, new IntRange(0, 10000), null, null, 24, null);
   @NotNull
   private static final ArrayDeque<Integer> arrayDeque = new ArrayDeque(20);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch3 = new StopWatch();
   private static long time;
   private static int value;

   private FastLatency() {
      super("fast-latency", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final FastLatency.SpikeDetector getSpikeDetector() {
      return (FastLatency.SpikeDetector)spikeDetector.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getMessageDelay() {
      return ((Number)messageDelay.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   public final int getValue() {
      return value;
   }

   public final void setValue(int value) {
      FastLatency.value = value;
   }

   @Flow
   private final void onPostTasksProcess(PostTasksProcessEvent postTasksProcessEvent) {
      if (MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().level != null
         && stopWatch.testValue(5000)
         && stopWatch2.testValue2(this.getDelay())) {
         Packet serverboundCommandSuggestionPack = (Packet)(new ServerboundCommandSuggestionPacket(1337, "w "));
         PacketUtil.applySend(serverboundCommandSuggestionPack);
         time = System.currentTimeMillis();
         stopWatch.update();
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundCommandSuggestionsPacket
         && ((ClientboundCommandSuggestionsPacket)receivePacket.getPacket()).id() == 1337) {
         value = (int)RangesKt.coerceIn(System.currentTimeMillis() - time, (ClosedRange)(new LongRange(0L, 1000L)));
         if (arrayDeque.size() >= 20) {
            arrayDeque.removeFirst();
         }

         arrayDeque.add(value);
         if (value > (float)CollectionsKt.averageOfInt((Iterable)arrayDeque) * 1.15F && arrayDeque.size() >= 20) {
            StopWatch stopWatch32 = stopWatch3;
            int messageDelay2 = this.getMessageDelay();
            if (stopWatch32.testValue2(messageDelay2)) {
               Module thisValue = this;
               Object[] objects = new Object[]{(int)CollectionsKt.averageOfInt((Iterable)arrayDeque), value};
               String defaultImpl = ChatUtil.defaultImpl(thisValue, "spike", objects, false, 4, null);
               if (this.getSpikeDetector() == FastLatency.SpikeDetector.BOTH || this.getSpikeDetector() == FastLatency.SpikeDetector.NOTIFICATION) {
                  ChatUtil.defaultImpl(this, defaultImpl, MessageType.WARNING, 0, null, 12, null);
               }

               if (this.getSpikeDetector() == FastLatency.SpikeDetector.BOTH || this.getSpikeDetector() == FastLatency.SpikeDetector.CHAT) {
                  ChatUtil.defaultImpl(this, defaultImpl, false, 2, null);
               }
            }
         }

         stopWatch.applyCurrentTimeMillis(5000);
      }
   }

   static {
   }

   public static enum SpikeDetector {
      OFF,
      NOTIFICATION,
      CHAT,
      BOTH;

      private static final FastLatency.SpikeDetector[] spikeDetectors = $values();
      private static final EnumEntries<FastLatency.SpikeDetector> enumEntries = EnumEntriesKt.enumEntries(spikeDetectors);

      private SpikeDetector() {
      }

      @NotNull
      public static EnumEntries<FastLatency.SpikeDetector> getEnumEntries() {
         return enumEntries;
      }

      private static final FastLatency.SpikeDetector[] $values() {
         return new FastLatency.SpikeDetector[]{OFF, NOTIFICATION, CHAT, BOTH};
      }
   }
}