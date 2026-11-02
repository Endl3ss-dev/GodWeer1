package releon.ru.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import releon.ru.access.SeeInvisibleRenderStateAccessor;

@Mixin({LivingEntityRenderState.class})
public abstract class LivingEntityRenderStateMixin implements SeeInvisibleRenderStateAccessor {
   @Unique
   private float releon$seeInvisibleAlpha = 1.0F;
   @Unique
   private boolean releon$renderSeeInvisible;
   @Unique
   private int releon$chamsColor;
   @Unique
   private boolean releon$renderChams;

   public LivingEntityRenderStateMixin() {
   }

   @Override
   public void releon$setSeeInvisibleAlpha(float alpha) {
      this.releon$renderSeeInvisible = alpha < 0.999F;
      this.releon$seeInvisibleAlpha = alpha;
   }

   @Override
   public float releon$getSeeInvisibleAlpha() {
      return this.releon$seeInvisibleAlpha;
   }

   @Override
   public boolean releon$shouldRenderSeeInvisible() {
      return this.releon$renderSeeInvisible;
   }

   @Override
   public void releon$setChamsColor(int color) {
      this.releon$chamsColor = color;
      this.releon$renderChams = (color >>> 24 & 0xFF) > 0;
   }

   @Override
   public int releon$getChamsColor() {
      return this.releon$chamsColor;
   }

   @Override
   public boolean releon$shouldRenderChams() {
      return this.releon$renderChams;
   }
}
