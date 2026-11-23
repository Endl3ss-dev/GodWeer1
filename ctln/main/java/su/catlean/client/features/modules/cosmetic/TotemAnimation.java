// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.awt.Color;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;
import org.joml.Matrix3x2fStack;
import org.joml.Quaternionfc;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.ItemCommandEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.ShowFloatingItemEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.vertex.ColoringVertexConsumer;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;

public final class TotemAnimation extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(TotemAnimation.class, "mode", "getMode()Lsu/catlean/gx;", 0), new PropertyReference1Impl(TotemAnimation.class, "speed", "getSpeed()I", 0) };
   @NotNull
   public static final TotemAnimation INSTANCE = new TotemAnimation();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", TotemAnimation.Mode.FadeOut, null, null, 12, null);
   @NotNull
   private static final IntSetting speed = SettingFactory.defaultImpl(INSTANCE, "speed", 40, new IntRange(1, 100), null, null, 24, null);
   @Nullable
   private static ItemStack itemStack;
   private static int value;
   @Nullable
   private static VertexConsumer vertexConsumer;

   private TotemAnimation() {
      super("totem-animation", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final TotemAnimation.Mode getMode() {
      return (TotemAnimation.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   @Flow
   private final void onShowFloatingItem(ShowFloatingItemEvent showFloatingItemEvent) {
      itemStack = showFloatingItemEvent.getItem();
      value = this.getValue();
      showFloatingItemEvent.cancel();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (value > 0) {
         value += -1;
         if (value == 0) {
            itemStack = null;
         }
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (itemStack != null && value > 0 && this.getMode() != TotemAnimation.Mode.Off) {
         int guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth();
         int guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight();
         int value2 = this.getValue() - value;
         float amount2 = (value2 + Render2D.INSTANCE.getAmount2()) / this.getValue();
         float value3 = amount2 * amount2;
         float value4 = amount2 * value3;
         float value5 = 10.25F * value4 * value3 - 24.95F * value3 * value3 + 25.5F * value4 - 13.8F * value3 + 4.0F * amount2;
         float value6 = value5 * (float) Math.PI;
         PoseStack poseStack = new PoseStack();
         poseStack.pushPose();
         float amount22 = value2 + Render2D.INSTANCE.getAmount2();
         float value7 = 50.0F + 175.0F * (float)Math.sin(value6);
         switch (TotemAnimation.WhenMappings.values[this.getMode().ordinal()]) {
            case 1:
               float value8 = (float)(Math.sin(amount22 * 112 / 180.0F) * 100);
               float value9 = (float)(Math.cos(amount22 * 112 / 180.0F) * 50);
               poseStack.translate(guiScaledWidth / 2 + value8, guiScaledHeight / 2 + value9, -50.0F);
               poseStack.scale(value7, -value7, value7);
               break;
            case 2:
               poseStack.translate(guiScaledWidth / 2, guiScaledHeight / 2, -50.0F);
               poseStack.scale(value7, -value7, value7);
               break;
            case 3:
               poseStack.translate(guiScaledWidth / 2, guiScaledHeight / 2, -50.0F);
               poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(amount22 * 2));
               poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(amount22 * 2));
               poseStack.scale(200 - amount22 * 1.5F, -200 + amount22 * 1.5F, 200 - amount22 * 1.5F);
               break;
            case 4:
               poseStack.translate(guiScaledWidth / 2, guiScaledHeight / 2, -50.0F);
               poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(amount22 * 3));
               poseStack.scale(200 - amount22 * 1.5F, -200 + amount22 * 1.5F, 200 - amount22 * 1.5F);
               break;
            case 5:
               float value10 = (float)(Math.pow(amount22, 3.0) * 0.2F);
               poseStack.translate(guiScaledWidth / 2, guiScaledHeight / 2 + value10, -50.0F);
               poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(amount22 * 5));
               poseStack.scale(200 - amount22 * 1.5F, -200 + amount22 * 1.5F, 200 - amount22 * 1.5F);
               break;
            case 6:
               float value11 = (float)(Math.pow(amount22, 3.0) * 0.2F) - 20;
               poseStack.translate(guiScaledWidth / 2, guiScaledHeight / 2 - value11, -50.0F);
               poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(amount22 * value * 2));
               poseStack.scale(200 - amount22 * 1.5F, -200 + amount22 * 1.5F, 200 - amount22 * 1.5F);
               break;
            case 7:
               float value12 = (float)(Math.pow(amount22, 2.0) * 4.5F);
               poseStack.translate(guiScaledWidth / 2 + value12, guiScaledHeight / 2, -50.0F);
               poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(amount22 * 40));
               poseStack.scale(200 - amount22 * 1.5F, -200 + amount22 * 1.5F, 200 - amount22 * 1.5F);
            case 8:
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }

         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 1024, false, 4, null);
         ColoringVertexConsumer coloringVertexConsumer = new ColoringVertexConsumer(meshBuilder, Render2D.INSTANCE.computeColor(Color.WHITE, 1.0F - amount2).darker());
         vertexConsumer = coloringVertexConsumer;
         ItemStackRenderState itemStackRenderState = new ItemStackRenderState();
         MinecraftHolder.getMinecraft()
            .getItemModelResolver()
            .updateForTopItem(itemStackRenderState, itemStack.getItem().getDefaultInstance(), ItemDisplayContext.FIXED, (Level)MinecraftHolder.getClientLevel(), null, 0);
         SubmitNodeStorage submitNodeStorage = new SubmitNodeStorage();
         itemStackRenderState.submit(poseStack, (SubmitNodeCollector)submitNodeStorage, 15728880, OverlayTexture.NO_OVERLAY, 0);
         MinecraftHolder.getMinecraft().gameRenderer.featureRenderDispatcher().renderAllFeatures(submitNodeStorage);
         GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(TextureAtlas.LOCATION_ITEMS).getTextureView();
         MeshBuilder.defaultImpl(
            meshBuilder,
            Pipelines.INSTANCE.getRenderPipeline14(),
            null,
            null,
            new PoseStack().last().pose(),
            (Matrix3x2f)(new Matrix3x2fStack()),
            MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
            null,
            70,
            null
         );
         vertexConsumer = null;
         poseStack.popPose();
      }
   }

   @Flow
   private final void onItemCommand(ItemCommandEvent itemCommandEvent) {
      if (vertexConsumer != null) {
         itemCommandEvent.setProvider(vertexConsumer);
         itemCommandEvent.cancel();
      }
   }

   private final int getValue() {
      int speed2 = 101 - this.getSpeed();
      if (this.getMode() == TotemAnimation.Mode.FadeOut) {
         return speed2 / 4;
      } else {
         return this.getMode() == TotemAnimation.Mode.Insert ? speed2 / 2 : speed2;
      }
   }

   static {
   }

   public static enum Mode {
      FadeOut,
      Size,
      Otkisuli,
      Insert,
      Fall,
      Rocket,
      Roll,
      Off;

      private static final TotemAnimation.Mode[] modes = $values();
      private static final EnumEntries<TotemAnimation.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<TotemAnimation.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final TotemAnimation.Mode[] $values() {
         return new TotemAnimation.Mode[]{FadeOut, Size, Otkisuli, Insert, Fall, Rocket, Roll, Off};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[TotemAnimation.Mode.values().length];

         try {
            values2[TotemAnimation.Mode.FadeOut.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[TotemAnimation.Mode.Size.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[TotemAnimation.Mode.Otkisuli.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[TotemAnimation.Mode.Insert.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[TotemAnimation.Mode.Fall.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         try {
            values2[TotemAnimation.Mode.Rocket.ordinal()] = 6;
         } catch (NoSuchFieldError exception6) {
         }

         try {
            values2[TotemAnimation.Mode.Roll.ordinal()] = 7;
         } catch (NoSuchFieldError exception7) {
         }

         try {
            values2[TotemAnimation.Mode.Off.ordinal()] = 8;
         } catch (NoSuchFieldError exception8) {
         }

         values = values2;
      }
   }
}