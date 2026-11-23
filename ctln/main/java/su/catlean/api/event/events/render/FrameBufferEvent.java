// deobf by pusia
package su.catlean.api.event.events.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.Event;

public final class FrameBufferEvent extends Event {
   @NotNull
   public static final FrameBufferEvent INSTANCE = new FrameBufferEvent();
   @Nullable
   private static RenderTarget frameBuffer;

   private FrameBufferEvent() {
   }

   @Nullable
   public final RenderTarget getFrameBuffer() {
      return frameBuffer;
   }

   public final void setFrameBuffer(RenderTarget renderTarget) {
      frameBuffer = renderTarget;
   }
}