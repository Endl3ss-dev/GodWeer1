// deobf by pusia
package su.catlean.client.features.modules.misc;

import java.util.Map;
import net.minecraft.network.PacketListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientCommonPacketListener;
import net.minecraft.network.protocol.common.ClientboundKeepAlivePacket;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;

public final class PingSpoof extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(PingSpoof.class, "delay", "getDelay()I", 0)};
   @NotNull
   public static final PingSpoof INSTANCE = new PingSpoof();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 100, new IntRange(1, 29500), null, null, 24, null);
   @NotNull
   private static final ConcurrentHashMap<Packet<? extends PacketListener>, Long> map = new ConcurrentHashMap();

   private PingSpoof() {
      super("ping-spoof", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundPingPacket || receivePacket.getPacket() instanceof ClientboundKeepAlivePacket) {
         map.put(receivePacket.getPacket(), MinecraftHolder.getTime());
         receivePacket.cancel();
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      Iterable<Map.Entry<Packet<? extends PacketListener>, Long>> entrySet = map.entrySet();
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(entrySet, 10));

      for (Object object : entrySet) {
         Entry entry = (Entry)object;
         arrayList.add(new Pair(entry.getKey(), entry.getValue()));
      }

      List list = arrayList;

      for (Object object2 : list) {
         Pair pair = (Pair)object2;
         Packet component1 = (Packet)pair.component1();
         Long component2 = (Long)pair.component2();
         if (MinecraftHolder.getTime() - component2 >= INSTANCE.getDelay()) {
            try {
               if (component1 instanceof ClientboundPingPacket) {
                  ((ClientboundPingPacket)component1).handle((ClientCommonPacketListener)MinecraftHolder.getClientPacketListener());
               } else if (component1 instanceof ClientboundKeepAlivePacket) {
                  ((ClientboundKeepAlivePacket)component1).handle((ClientCommonPacketListener)MinecraftHolder.getClientPacketListener());
               }

               map.remove(component1);
            } catch (Exception exception) {
               ChatUtil.defaultImpl(INSTANCE, "Error processing packet: " + exception.getMessage(), false, 2, null);
            }
         }
      }
   }

   static {
   }
}