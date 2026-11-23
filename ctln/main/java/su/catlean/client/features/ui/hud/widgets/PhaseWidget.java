// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import java.awt.Color;
import kotlin.collections.CollectionsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.features.ui.hud.WidgetEditorScreen;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.EntityPositionUtil;

public final class PhaseWidget extends Widget {
   @NotNull
   public static final PhaseWidget INSTANCE = new PhaseWidget();
   @Nullable
   private static BlockPos blockPos;
   private static boolean flag;

   private PhaseWidget() {
      super("phase-widget");
   }

   @Nullable
   public final BlockPos getBlockPos() {
      return blockPos;
   }

   public final void setBlockPos(BlockPos blockPos) {
      PhaseWidget.blockPos = blockPos;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      PhaseWidget.flag = flag;
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      float value = 50.0F;
      if (blockPos != null) {
         double amount = Render2D.INSTANCE
            .computeAmount(MinecraftHolder.getLocalPlayer().xo, MinecraftHolder.getLocalPlayer().getX(), Render2D.INSTANCE.getAmount2());
         double amount2 = Render2D.INSTANCE
            .computeAmount(MinecraftHolder.getLocalPlayer().zo, MinecraftHolder.getLocalPlayer().getZ(), Render2D.INSTANCE.getAmount2());
         float value2 = blockPos.getX() + 0.5F;
         float value3 = blockPos.getZ() + 0.5F;
         float value4 = ((float)amount - value2 + 0.2F) * value;
         float value5 = ((float)amount2 - value3 + 0.2F) * value;
         context.pose().pushMatrix();
         context.pose()
            .rotateAbout(
               (float)Math.toRadians(-MinecraftHolder.getLocalPlayer().getYRot() + 180.0), this.getAmount3() + value * 0.5F, this.getAmount4() + value * 0.5F
            );
         float amount3 = this.getAmount3() + value4;
         float amount4 = this.getAmount4() + value5;
         Color color7 = !flag ? ThemeManager.INSTANCE.getColor7() : Color.RED;
         Render2D.INSTANCE.render(context.pose(), this.getAmount3(), this.getAmount4(), value, value, ThemeManager.INSTANCE.getColor7());
         Render2D.INSTANCE.render(context.pose(), amount3, amount4, value * 0.6F, value * 0.6F, color7);
         context.pose().popMatrix();
         this.updateAmount(value, value);
      }
   }

   @Override
   public void update() {
      AABB boundingBox = MinecraftHolder.getLocalPlayer().getBoundingBox();
      float value = 1.0E-5F;
      flag = false;
      blockPos = !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)
            && !(MinecraftHolder.getMinecraft().gui.screen() instanceof WidgetEditorScreen)
         ? null
         : BlockPos.containing((Position)EntityPositionUtil.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      BlockPos[] blockPoss = new BlockPos[]{
         BlockPos.containing(boundingBox.minX, boundingBox.minY + 0.3, boundingBox.minZ),
         BlockPos.containing(boundingBox.minX, boundingBox.minY + 0.3, boundingBox.maxZ - value),
         BlockPos.containing(boundingBox.maxX - value, boundingBox.minY + 0.3, boundingBox.minZ),
         BlockPos.containing(boundingBox.maxX - value, boundingBox.minY + 0.3, boundingBox.maxZ - value)
      };

      for (BlockPos listOfItem : CollectionsKt.listOf(blockPoss)) {
         if (!MinecraftHolder.getClientLevel().getBlockState(listOfItem).canBeReplaced()) {
            blockPos = listOfItem;
         } else {
            flag = true;
         }
      }
   }
}