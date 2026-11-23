// deobf by pusia
package su.catlean.client.features.ui.hud.widgets;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import java.awt.Color;
import kotlin.ranges.RangesKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fc;
import su.catlean.client.features.ui.hud.Widget;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.ThemeManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.WorldUtil;

public final class CrosshairWidget extends Widget {
   @NotNull
   public static final CrosshairWidget INSTANCE = new CrosshairWidget();
   private static float amount;

   private CrosshairWidget() {
      super("crosshair");
   }

   public final float getAmount() {
      return amount;
   }

   public final void setAmount(float amount) {
      CrosshairWidget.amount = amount;
   }

   @Override
   public void render(GuiGraphicsExtractor context) {
      Color color = ThemeManager.INSTANCE.computeColor(0);
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F;
      float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F;
      float attackStrengthScale = 3.0F
         - MinecraftHolder.getMinecraft().player.getAttackStrengthScale(0.5F) * 3.0F
         + RangesKt.coerceIn(WorldUtil.INSTANCE.getAmount() * 10.0F, 0.0F, 3.0F);
      float amount2 = Render2D.INSTANCE.computeAmount(amount, attackStrengthScale, Render2D.INSTANCE.getAmount2()) + 7.0F;
      amount = attackStrengthScale;
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, 8, false, 4, null);
      Render2D instance = Render2D.INSTANCE;
      float value = guiScaledWidth - 1.0F;
      float value2 = guiScaledHeight - 6.0F + amount2;
      Color[] colors = new Color[]{Color.BLACK};
      instance.updateMeshBuilder(meshBuilder, value, value2, 2.0F, 4.0F, colors);
      instance = Render2D.INSTANCE;
      value = guiScaledWidth - 0.5F;
      value2 = guiScaledHeight - 5.5F + amount2;
      colors = new Color[]{color};
      instance.updateMeshBuilder(meshBuilder, value, value2, 1.0F, 3.0F, colors);
      instance = Render2D.INSTANCE;
      value = guiScaledWidth - 1.0F;
      value2 = guiScaledHeight + 2.0F - amount2;
      colors = new Color[]{Color.BLACK};
      instance.updateMeshBuilder(meshBuilder, value, value2, 2.0F, 4.0F, colors);
      instance = Render2D.INSTANCE;
      value = guiScaledWidth - 0.5F;
      value2 = guiScaledHeight + 2.5F - amount2;
      colors = new Color[]{color};
      instance.updateMeshBuilder(meshBuilder, value, value2, 1.0F, 3.0F, colors);
      instance = Render2D.INSTANCE;
      value = guiScaledWidth - 6.0F + amount2;
      value2 = guiScaledHeight - 1.0F;
      colors = new Color[]{Color.BLACK};
      instance.updateMeshBuilder(meshBuilder, value, value2, 4.0F, 2.0F, colors);
      instance = Render2D.INSTANCE;
      value = guiScaledWidth - 5.5F + amount2;
      value2 = guiScaledHeight - 0.5F;
      colors = new Color[]{color};
      instance.updateMeshBuilder(meshBuilder, value, value2, 3.0F, 1.0F, colors);
      instance = Render2D.INSTANCE;
      value = guiScaledWidth + 2.0F - amount2;
      value2 = guiScaledHeight - 1.0F;
      colors = new Color[]{Color.BLACK};
      instance.updateMeshBuilder(meshBuilder, value, value2, 4.0F, 2.0F, colors);
      instance = Render2D.INSTANCE;
      value = guiScaledWidth + 2.5F - amount2;
      value2 = guiScaledHeight - 0.5F;
      colors = new Color[]{color};
      instance.updateMeshBuilder(meshBuilder, value, value2, 3.0F, 1.0F, colors);
      MeshBuilder.defaultImpl(
         meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, null, new Matrix3x2f((Matrix3x2fc)context.pose()), null, null, 110, null
      );
   }
}