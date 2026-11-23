// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import net.minecraft.network.PacketListener;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.concurrent.CopyOnWriteArrayList;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.esp.LogoutSpots;
import su.catlean.client.features.modules.esp.PlayerEsp;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.PlayerEspRenderer;
import su.catlean.client.render.vertex.PolygonVertexConsumer;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class PopChams extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(PopChams.class, "mode", "getMode()Lsu/catlean/p_;", 0), new PropertyReference1Impl(PopChams.class, "alpha", "getAlpha()I", 0), new PropertyReference1Impl(PopChams.class, "ySpeed", "getYSpeed()F", 0), new PropertyReference1Impl(PopChams.class, "aSpeed", "getASpeed()F", 0) };
   @NotNull
   public static final PopChams INSTANCE = new PopChams();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", PopChams.Mode.DEFAULT, null, null, 12, null);
   @NotNull
   private static final IntSetting startAlpha = SettingFactory.defaultImpl(INSTANCE, "start-alpha", 255, new IntRange(0, 255), null, null, 24, null);
   @NotNull
   private static final FloatSetting ySpeed = SettingFactory.defaultImpl(INSTANCE, "y-speed", 0.1F, RangesKt.rangeTo(-1.0F, 1.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting alphaSpeed = SettingFactory.defaultImpl(INSTANCE, "alpha-speed", 5.0F, RangesKt.rangeTo(1.0F, 20.0F), null, null, 24, null);
   @NotNull
   private static final CopyOnWriteArrayList<Dismemberment.BodyPart> items = new CopyOnWriteArrayList();

   private PopChams() {
      super("pop-chams", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final PopChams.Mode getMode() {
      return (PopChams.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getAlpha() {
      return ((Number)startAlpha.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final float getYSpeed() {
      return ((Number)ySpeed.computeObject(this, $$delegatedProperties[2])).floatValue();
   }

   private final float getASpeed() {
      return ((Number)alphaSpeed.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      items.removeIf(arg -> ((Dismemberment.BodyPart)arg).testAmount(INSTANCE.getASpeed(), INSTANCE.getYSpeed()));
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.getMode() == PopChams.Mode.DEFAULT) {
         if (!items.isEmpty()) {
            MeshBuilder meshBuilder = null;
            meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_COLOR, items.size() * 72, false, 4, null);
            Iterable<Dismemberment.BodyPart> items2 = items;
            boolean flag = false;

            for (Object object : items2) {
               Dismemberment.BodyPart bodyPart = (Dismemberment.BodyPart)object;
               PlayerEspRenderer.INSTANCE
                  .render(
                     render3DEvent.getStack(),
                     bodyPart.getModel(),
                     bodyPart.getState(),
                     bodyPart.getVec3(),
                     Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), bodyPart.getAmount2()),
                     PlayerEsp.RenderMode.FILL,
                     (MeshBuilder)meshBuilder
                  );
            }

            MeshBuilder.defaultImpl(
               (MeshBuilder)meshBuilder, Pipelines.INSTANCE.getRenderPipeline22(), null, null, render3DEvent.getStack().last().pose(), null, null, null, 118, null
            );
            VertexFormat positioN_COLOR = DefaultVertexFormat.POSITION_COLOR;
            int size = items.size() * 144;
            boolean flag2 = true;
            int value = size;
            VertexFormat vertexFormat = positioN_COLOR;
            meshBuilder = new MeshBuilder(vertexFormat, value, flag2);
            items2 = items;
            flag = false;

            for (Object object2 : items2) {
               Dismemberment.BodyPart bodyPart2 = (Dismemberment.BodyPart)object2;
               PlayerEspRenderer.INSTANCE
                  .render(
                     render3DEvent.getStack(),
                     bodyPart2.getModel(),
                     bodyPart2.getState(),
                     bodyPart2.getVec3(),
                     Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), bodyPart2.getAmount2() * 2.0F),
                     PlayerEsp.RenderMode.OUTLINE,
                     (MeshBuilder)meshBuilder
                  );
            }

            MeshBuilder.defaultImpl(
               (MeshBuilder)meshBuilder, Pipelines.INSTANCE.getRenderPipeline25(), null, null, render3DEvent.getStack().last().pose(), null, null, null, 118, null
            );
         }
      } else {
         Iterable<Dismemberment.BodyPart> items3 = items;

         for (Object object3 : items3) {
            Dismemberment.BodyPart bodyPart3 = (Dismemberment.BodyPart)object3;
            INSTANCE.render(render3DEvent.getStack(), bodyPart3);
         }
      }
   }

   @Flow
   public final void onReceivePacket(ReceivePacket event) {
      if (MinecraftHolder.getMinecraft().getCameraEntity() != null
         && MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().gameRenderer.mainCamera() != null
         && MinecraftHolder.getMinecraft().level != null
         && MinecraftHolder.getLocalPlayer().tickCount >= 10) {
         Packet<? extends PacketListener> packet = event.getPacket();
         if (packet instanceof ClientboundEntityEventPacket
            && ((ClientboundEntityEventPacket)packet).getEventId() == 35
            && MinecraftHolder.getMinecraft().getCameraEntity() != null
            && MinecraftHolder.getMinecraft().gameRenderer.mainCamera() != null) {
            Entity entity = ((ClientboundEntityEventPacket)packet).getEntity((Level)MinecraftHolder.getClientLevel());
            Player player = entity instanceof Player ? (Player)entity : null;
            if ((entity instanceof Player ? (Player)entity : null) == null) {
               return;
            }

            Player player2 = player;
            if (Intrinsics.areEqual(player2, MinecraftHolder.getMinecraft().player)) {
               return;
            }

            PlayerModel model = (PlayerModel)((AvatarRenderer)MinecraftHolder.getMinecraft().getEntityRenderDispatcher().getRenderer((Entity)player2)).getModel();
            EntityRenderState renderState = MinecraftHolder.getMinecraft()
               .getEntityRenderDispatcher()
               .getRenderer((Entity)player2)
               .createRenderState((Entity)player2, Render3D.INSTANCE.getAmount4());
            String text = "null cannot be cast to non-null type net.minecraft.client.renderer.entity.state.AvatarRenderState";
            AvatarRenderState avatarRenderState = (AvatarRenderState)renderState;
            AvatarRenderState avatarRenderState2 = new AvatarRenderState();
            avatarRenderState2.capeFlap = avatarRenderState.capeFlap;
            avatarRenderState2.capeLean = avatarRenderState.capeLean;
            avatarRenderState2.capeLean2 = avatarRenderState.capeLean2;
            avatarRenderState2.x = avatarRenderState.x;
            avatarRenderState2.y = avatarRenderState.y;
            avatarRenderState2.z = avatarRenderState.z;
            avatarRenderState2.bodyRot = avatarRenderState.bodyRot;
            avatarRenderState2.yRot = avatarRenderState.yRot;
            avatarRenderState2.xRot = avatarRenderState.xRot;
            avatarRenderState2.attackTime = avatarRenderState.attackTime;
            avatarRenderState2.flyingYRot = avatarRenderState.flyingYRot;
            avatarRenderState2.ageInTicks = avatarRenderState.ageInTicks;
            avatarRenderState2.walkAnimationSpeed = avatarRenderState.walkAnimationSpeed;
            avatarRenderState2.speedValue = avatarRenderState.speedValue;
            avatarRenderState2.walkAnimationPos = avatarRenderState.walkAnimationPos;
            model.setupAnim(avatarRenderState);
            Vec3 vec3 = this.computeVec3((Entity)player2);
            Identifier texturePath = ((AbstractClientPlayer)player2).getSkin().body().texturePath();
            items.add(new Dismemberment.BodyPart(model, avatarRenderState2, vec3, texturePath, this.getAlpha()));
         }
      }
   }

   private final void render(PoseStack poseStack, Dismemberment.BodyPart bodyPart) {
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(bodyPart.getTexture()).getTextureView();
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 256, false, 4, null);
      PolygonVertexConsumer polygonVertexConsumer = new PolygonVertexConsumer(meshBuilder, Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), bodyPart.getAmount2()));
      poseStack.pushPose();
      Vec3 vec3 = bodyPart.getVec3();
      Vec3 position = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position();
      double value = vec3.x - position.x();
      double value2 = vec3.y - position.y();
      double value3 = vec3.z - position.z();
      poseStack.translate((float)value, (float)value2, (float)value3);
      poseStack.scale(bodyPart.getState().scale, bodyPart.getState().scale, bodyPart.getState().scale);
      LogoutSpots.INSTANCE.render(bodyPart.getState(), poseStack, bodyPart.getState().bodyRot);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      poseStack.scale(0.9375F, 0.9375F, 0.9375F);
      poseStack.translate(0.0F, -1.501F, 0.0F);
      bodyPart.getModel().setupAnim(bodyPart.getState());
      bodyPart.getModel().renderToBuffer(poseStack, polygonVertexConsumer, 15728880, 0, ThemeManager.INSTANCE.getColor2().getRGB());
      MeshBuilder.defaultImpl(
         meshBuilder,
         Pipelines.INSTANCE.getRenderPipeline16(),
         null,
         null,
         new PoseStack().last().pose(),
         null,
         MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
         null,
         86,
         null
      );
      poseStack.popPose();
   }

   static {
   }

   public static enum Mode {
      DEFAULT,
      GHOST;

      private static final PopChams.Mode[] modes = $values();
      private static final EnumEntries<PopChams.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<PopChams.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final PopChams.Mode[] $values() {
         return new PopChams.Mode[]{DEFAULT, GHOST};
      }
   }
}