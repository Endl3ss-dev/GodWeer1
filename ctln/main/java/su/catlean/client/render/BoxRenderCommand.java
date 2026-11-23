// deobf by pusia
package su.catlean.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class BoxRenderCommand {
   @NotNull
   public static final BoxRenderCommand INSTANCE = new BoxRenderCommand();
   @NotNull
   private static final Map<BoxRenderCommand.BlockRenderData, Long> map = new ConcurrentHashMap();

   private BoxRenderCommand() {
   }

   @NotNull
   public final Map<BoxRenderCommand.BlockRenderData, Long> getMap() {
      return map;
   }

   public final void render(PoseStack matrixStack) {
      Map<BoxRenderCommand.BlockRenderData, Long> map2 = map;

      for (Map.Entry<BoxRenderCommand.BlockRenderData, Long> entrySetItem : map2.entrySet()) {
         BoxRenderCommand.BlockRenderData key = (BoxRenderCommand.BlockRenderData)entrySetItem.getKey();
         long value = ((Number)entrySetItem.getValue()).longValue();
         if ((float)(MinecraftHolder.getTime() - value) > 300.0F) {
            map.remove(key);
         } else {
            key.render(MinecraftHolder.getTime() - value, matrixStack);
         }
      }
   }

   public final void applyPut(BlockPos pos, Color lineColor, Color fillColor, PlaceModule.Animation animationMode, PlaceModule.Render renderMode) {
      if (renderMode != PlaceModule.Render.NONE) {
         map.put(new BoxRenderCommand.BlockRenderData(pos, lineColor, fillColor, animationMode, renderMode), MinecraftHolder.getTime());
      }
   }

   private static final boolean testBlockPos(BlockPos blockPos, BoxRenderCommand.BlockRenderData blockRenderData) {
      return Intrinsics.areEqual(blockRenderData.getPos(), blockPos);
   }

   private static final boolean checkBooleanValue(Function1 callback, Object object) {
      return (Boolean)callback.invoke(object);
   }

   public static final class BlockRenderData {
      @NotNull
      private final BlockPos pos;
      @NotNull
      private final Color lineColor;
      @NotNull
      private final Color fillColor;
      @NotNull
      private final PlaceModule.Animation animationMode;
      @NotNull
      private final PlaceModule.Render renderMode;

      public BlockRenderData(BlockPos pos, Color lineColor, Color fillColor, PlaceModule.Animation animationMode, PlaceModule.Render renderMode) {
         this.pos = pos;
         this.lineColor = lineColor;
         this.fillColor = fillColor;
         this.animationMode = animationMode;
         this.renderMode = renderMode;
      }

      @NotNull
      public final BlockPos getPos() {
         return this.pos;
      }

      @NotNull
      public final Color getLineColor() {
         return this.lineColor;
      }

      @NotNull
      public final Color getFillColor() {
         return this.fillColor;
      }

      @NotNull
      public final PlaceModule.Animation getAnimationMode() {
         return this.animationMode;
      }

      @NotNull
      public final PlaceModule.Render getRenderMode() {
         return this.renderMode;
      }

      public final void render(long time, PoseStack stack) {
         AABB aabb = new AABB(this.pos);
         float time2 = 1.0F;
         switch (BoxRenderCommand.BlockRenderData.WhenMappings.values[this.animationMode.ordinal()]) {
            case 1:
               break;
            case 2:
               time2 = 1 - (float)time / 300.0F;
               aabb = new AABB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX(), this.pos.getY(), this.pos.getZ())
                  .contract(time2, time2, time2)
                  .move(0.5 + time2 * 0.5, 0.5 + time2 * 0.5, 0.5 + time2 * 0.5);
               break;
            case 3:
               time2 = 1.0F - (float)time / 300.0F;
               aabb = new AABB(this.pos);
               break;
            case 4:
               time2 = (float)time / 300.0F;
               aabb = new AABB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX(), this.pos.getY(), this.pos.getZ())
                  .contract(time2, time2, time2)
                  .move(0.5 + time2 * 0.5, 0.5 + time2 * 0.5, 0.5 + time2 * 0.5);
               break;
            case 5:
               time2 = time > 100L ? 1 - (float)(time - 100) / 400 : (float)time / 100;
               aabb = new AABB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX(), this.pos.getY(), this.pos.getZ())
                  .contract(time2, time2, time2)
                  .move(0.5 + time2 * 0.5, 0.5 + time2 * 0.5, 0.5 + time2 * 0.5);
               break;
            case 6:
               time2 = (float)time / 300.0F;
               aabb = new AABB(this.pos.getX(), this.pos.getY() + time2, this.pos.getZ(), this.pos.getX() + 1, this.pos.getY(), this.pos.getZ() + 1);
               break;
            case 7:
               time2 = time < 200L ? 1.0F : 1 + ((float)time - 200.0F) / 400.0F;
               aabb = new AABB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX(), this.pos.getY(), this.pos.getZ())
                  .contract(time2, time2, time2)
                  .move(0.5 + time2 * 0.5, 0.5 + time2 * 0.5, 0.5 + time2 * 0.5);
               break;
            case 8:
               time2 = time < 200L ? 1.5F - (float)time / 200.0F * 0.5F : 1.0F;
               aabb = new AABB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX(), this.pos.getY(), this.pos.getZ())
                  .contract(time2, time2, time2)
                  .move(0.5 + time2 * 0.5F, 0.5 + time2 * 0.5, 0.5 + time2 * 0.5);
               break;
            case 9:
               time2 = 1.0F + (float)time / 1500.0F;
               aabb = new AABB(this.pos.getX(), this.pos.getY(), this.pos.getZ(), this.pos.getX(), this.pos.getY(), this.pos.getZ())
                  .contract(time2, time2, time2)
                  .move(0.5 + time2 * 0.5F, 0.5F + time2 * 0.5, 0.5 + time2 * 0.5);
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         this.updateAmount(time2, aabb, this.renderMode, this.lineColor, this.fillColor);
      }

      private final void updateAmount(float amount, AABB aABB, PlaceModule.Render render, Color color, Color color2) {
         if (render == PlaceModule.Render.BOTH || render == PlaceModule.Render.LINE) {
            Render3D.defaultImpl3(Render3D.INSTANCE, aABB, Render2D.INSTANCE.computeColor(color, Render2D.INSTANCE.fetchAlpha(color) * amount), null, 4, null);
         }

         if (render == PlaceModule.Render.BOTH || render == PlaceModule.Render.FILL) {
            Render3D.defaultImpl2(Render3D.INSTANCE, aABB, Render2D.INSTANCE.computeColor(color2, Render2D.INSTANCE.fetchAlpha(color2) * amount), null, 4, null);
         }

         if (render == PlaceModule.Render.SHADER) {
            Render3D.INSTANCE
               .testAABB(
                  aABB,
                  Render2D.INSTANCE.computeColor(color2, Render2D.INSTANCE.fetchAlpha(color2) * amount),
                  Render2D.INSTANCE.computeColor(color, Render2D.INSTANCE.fetchAlpha(color) * amount)
               );
         }
      }

      @NotNull
      public final BlockPos getPos2() {
         return this.pos;
      }

      @NotNull
      public final Color getLineColor2() {
         return this.lineColor;
      }

      @NotNull
      public final Color getFillColor2() {
         return this.fillColor;
      }

      @NotNull
      public final PlaceModule.Animation getAnimationMode2() {
         return this.animationMode;
      }

      @NotNull
      public final PlaceModule.Render getRenderMode2() {
         return this.renderMode;
      }

      @NotNull
      @Override
      public String toString() {
         PlaceModule.Render renderMode2 = this.renderMode;
         PlaceModule.Animation animationMode2 = this.animationMode;
         Color fillColor2 = this.fillColor;
         Color lineColor2 = this.lineColor;
         BlockPos pos2 = this.pos;
         return "BlockRenderData(pos=" + pos2 + ", lineColor=" + lineColor2 + ", fillColor=" + fillColor2 + ", animationMode=" + animationMode2 + ", renderMode=" + renderMode2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.pos.hashCode();
         hashCode = hashCode * 31 + this.lineColor.hashCode();
         hashCode = hashCode * 31 + this.fillColor.hashCode();
         hashCode = hashCode * 31 + this.animationMode.hashCode();
         return hashCode * 31 + this.renderMode.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof BoxRenderCommand.BlockRenderData blockRenderData)) {
            return false;
         } else if (!Intrinsics.areEqual(this.pos, blockRenderData.pos)) {
            return false;
         } else if (!Intrinsics.areEqual(this.lineColor, blockRenderData.lineColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.fillColor, blockRenderData.fillColor)) {
            return false;
         } else {
            return this.animationMode != blockRenderData.animationMode ? false : this.renderMode == blockRenderData.renderMode;
         }
      }

      public static final class WhenMappings {
         public static final int[] values;

         static {
            int[] values2 = new int[PlaceModule.Animation.values().length];

            try {
               values2[PlaceModule.Animation.Static.ordinal()] = 1;
            } catch (NoSuchFieldError exception) {
            }

            try {
               values2[PlaceModule.Animation.Decrease.ordinal()] = 2;
            } catch (NoSuchFieldError exception2) {
            }

            try {
               values2[PlaceModule.Animation.Fade.ordinal()] = 3;
            } catch (NoSuchFieldError exception3) {
            }

            try {
               values2[PlaceModule.Animation.FILL.ordinal()] = 4;
            } catch (NoSuchFieldError exception4) {
            }

            try {
               values2[PlaceModule.Animation.Flash.ordinal()] = 5;
            } catch (NoSuchFieldError exception5) {
            }

            try {
               values2[PlaceModule.Animation.Grow.ordinal()] = 6;
            } catch (NoSuchFieldError exception6) {
            }

            try {
               values2[PlaceModule.Animation.TNT.ordinal()] = 7;
            } catch (NoSuchFieldError exception7) {
            }

            try {
               values2[PlaceModule.Animation.Pull.ordinal()] = 8;
            } catch (NoSuchFieldError exception8) {
            }

            try {
               values2[PlaceModule.Animation.Hover.ordinal()] = 9;
            } catch (NoSuchFieldError exception9) {
            }

            values = values2;
         }
      }
   }
}