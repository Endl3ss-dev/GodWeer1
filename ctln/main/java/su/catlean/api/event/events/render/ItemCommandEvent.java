// deobf by pusia
package su.catlean.api.event.events.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;

public final class ItemCommandEvent extends Event {
   @NotNull
   public static final ItemCommandEvent INSTANCE = new ItemCommandEvent();
   @Nullable
   private static VertexConsumer provider;

   private ItemCommandEvent() {
   }

   @Nullable
   public final VertexConsumer getProvider() {
      return provider;
   }

   public final void setProvider(VertexConsumer vertexConsumer) {
      provider = vertexConsumer;
   }
}