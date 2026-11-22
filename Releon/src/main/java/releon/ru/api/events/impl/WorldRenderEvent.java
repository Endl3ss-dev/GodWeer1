package releon.ru.api.events.impl;

import net.minecraft.client.util.math.MatrixStack;
import releon.ru.api.events.Event;

public record WorldRenderEvent(MatrixStack poseStack, float tickDelta) implements Event {
   public MatrixStack getStack() {
      return this.poseStack;
   }

   public float getTickDelta() {
      return this.tickDelta;
   }

   public float getPartialTicks() {
      return this.tickDelta;
   }
}
