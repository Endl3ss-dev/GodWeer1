// deobf by pusia
package su.catlean.api.event.events.network;

import io.netty.channel.ChannelPipeline;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.properties.Delegates;
import kotlin.properties.ReadWriteProperty;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.PacketFlow;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.Event;
import su.catlean.gofra.Gofra;

public final class AddHandlersEvent extends Event {
   @NotNull
   public static final AddHandlersEvent INSTANCE = new AddHandlersEvent();
   public static final KProperty<Object>[] $$delegatedProperties;
   private static AtomicReference<ChannelPipeline> pipeline;
   private static PacketFlow side;
   @NotNull
   private static final ReadWriteProperty<Object, Object> local$delegate = Delegates.INSTANCE.notNull();

   private AddHandlersEvent() {
   }

   @NotNull
   public final AtomicReference<ChannelPipeline> getPipeline() {
      if (pipeline != null) {
         return pipeline;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("pipeline");
         return null;
      }
   }

   @NotNull
   public final PacketFlow getSide() {
      if (side != null) {
         return side;
      } else {
         Intrinsics.throwUninitializedPropertyAccessException("side");
         return null;
      }
   }

   public final boolean getLocal() {
      return (Boolean)local$delegate.getValue(this, $$delegatedProperties[0]);
   }

   private final void setLocal(boolean flag) {
      local$delegate.setValue(this, $$delegatedProperties[0], flag);
   }

   public final boolean call(AtomicReference<ChannelPipeline> pipeline, PacketFlow side, boolean flag) {
      this.setCancelled(false);
      AddHandlersEvent.pipeline = pipeline;
      AddHandlersEvent.side = side;
      this.setLocal(flag);
      Gofra.INSTANCE.drain(this);
      return this.getCancelled();
   }

   static {
      KProperty[] delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(AddHandlersEvent.class, "local", "getLocal()Z", 0)};
      $$delegatedProperties = delegatedProperties;
   }
}