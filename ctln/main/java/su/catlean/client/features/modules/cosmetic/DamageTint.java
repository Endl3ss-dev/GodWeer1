// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import net.minecraft.network.PacketListener;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import su.catlean.api.data.Rotation;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.Pipelines;
import su.catlean.client.services.NotificationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;

public final class DamageTint extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(DamageTint.class, "vector", "getVector()Z", 0), new PropertyReference1Impl(DamageTint.class, "vectorColor", "getVectorColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(DamageTint.class, "vectorSize", "getVectorSize()I", 0), new PropertyReference1Impl(DamageTint.class, "tint", "getTint()Z", 0), new PropertyReference1Impl(DamageTint.class, "tintColor", "getTintColor()Ljava/awt/Color;", 0) };
   @NotNull
   public static final DamageTint INSTANCE = new DamageTint();
   @NotNull
   private static final BooleanSetting vector = SettingFactory.defaultImpl(INSTANCE, "vector", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting vectorColor = SettingFactory.defaultImpl(INSTANCE, "vector-color", Color.RED, null, () -> INSTANCE.getVector(), 4, null);
   @NotNull
   private static final IntSetting vectorSize = SettingFactory.defaultImpl(
      INSTANCE, "vector-size", 100, new IntRange(10, 500), null, () -> INSTANCE.getVector(), 8, null
   );
   @NotNull
   private static final BooleanSetting tint = SettingFactory.defaultImpl(INSTANCE, "tint", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting tintColor = SettingFactory.defaultImpl(INSTANCE, "tint-color", Color.RED, null, () -> INSTANCE.getTint(), 4, null);
   @NotNull
   private static final Identifier identifier = Identifier.withDefaultNamespace("textures/misc/vignette.png");
   @Nullable
   private static Vec3 vec3;
   @Nullable
   private static Vec3 vec32;
   @NotNull
   private static final List<DamageTint.Hit> items = new ArrayList();

   private DamageTint() {
      super("damage-tint", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final boolean getVector() {
      return (Boolean)vector.computeObject(this, $$delegatedProperties[0]);
   }

   private final Color getVectorColor() {
      return (Color)vectorColor.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getVectorSize() {
      return ((Number)vectorSize.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final boolean getTint() {
      return (Boolean)tint.computeObject(this, $$delegatedProperties[3]);
   }

   private final Color getTintColor() {
      return (Color)tintColor.computeObject(this, $$delegatedProperties[4]);
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (MinecraftHolder.getLocalPlayer().hurtTime > 0) {
         if (vec3 != null) {
            Object object = null;
            byte byteValue = 2;
            Object object2 = null;
            Vec3 vec33 = vec3;
            items.add(new DamageTint.Hit(vec33, (Animation)object2, byteValue, (DefaultConstructorMarker)object));
            vec3 = null;
         } else if (vec32 != null) {
            Object object3 = null;
            byte byteValue2 = 2;
            Object object4 = null;
            Vec3 vec322 = vec32;
            items.add(new DamageTint.Hit(vec322, (Animation)object4, byteValue2, (DefaultConstructorMarker)object3));
            vec32 = null;
         }
      }

      if (this.getTint()) {
         this.applyAbs(10.0F, 2.0F);
      }

      if (this.getVector()) {
         for (DamageTint.Hit item : (Iterable<su.catlean.client.features.modules.cosmetic.DamageTint.Hit>)items) {
            item.getAnimation().applyDoubleValue(1.0F);
            float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F;
            float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F;
            PoseStack poseStack = new PoseStack();
            float amount = Rotation.vectorUtil.computeRotation(item.getVec()).getAmount() - MinecraftHolder.getLocalPlayer().getYRot();
            poseStack.translate(guiScaledWidth, guiScaledHeight, 0.0F);
            poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(amount));
            poseStack.translate(-guiScaledWidth, -guiScaledHeight, 0.0F);
            this.render(
               poseStack, guiScaledWidth, guiScaledHeight, this.getVectorSize(), Render2D.INSTANCE.computeColor(this.getVectorColor(), 1.0F - item.getAnimation().getAmount())
            );
         }
      }

      items.removeIf(arg -> ((DamageTint.Hit)arg).getAnimation().isFlag());
   }

   public final void render(PoseStack matrices, float amount, float amount2, float amount3, Color color) {
      Matrix4f pose = matrices.last().pose();
      Render2D.INSTANCE
         .applyGetTexture(
            pose,
            AtlasTexture.getCatlean(),
            amount,
            amount2,
            amount3,
            amount3,
            color,
            color,
            color,
            color,
            true,
            ParticleType.HALO.getAmount(),
            ParticleType.HALO.getAmount2(),
            ParticleType.HALO.getAmount3(),
            ParticleType.HALO.getAmount4()
         );
   }

   public final void applyAbs(float amount, float amount2) {
      float health = MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount();
      boolean flag = health <= amount;
      float amount3 = Math.abs((flag ? health / amount : 1.0F) - 1.0F) * amount2;
      if (health <= 4.0F) {
         amount3 += Math.abs(0.2F * (float)Math.sin((float)Math.toRadians(MinecraftHolder.getLocalPlayer().tickCount * 16.0 + Render2D.INSTANCE.getAmount2())));
      }

      amount3 = RangesKt.coerceIn(amount3, 0.0F, 1.0F);
      Color color = new Color(
         (int)((1.0F - Render2D.INSTANCE.fetchRed(this.getTintColor())) * amount3 * 255),
         (int)((1.0F - Render2D.INSTANCE.fetchGreen(this.getTintColor())) * amount3 * 255),
         (int)((1.0F - Render2D.INSTANCE.fetchBlue(this.getTintColor())) * amount3 * 255),
         255
      );
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth();
      float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight();
      GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(identifier).getTextureView();
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 0, false, 6, null);
      MeshBuilder.defaultImpl(meshBuilder, 0.0F, guiScaledHeight, 0.0F, 4, null).computeMeshBuilder(0.0F, 1.0F).fetchAlpha(color);
      MeshBuilder.defaultImpl(meshBuilder, guiScaledWidth, guiScaledHeight, 0.0F, 4, null).computeMeshBuilder(1.0F, 1.0F).fetchAlpha(color);
      MeshBuilder.defaultImpl(meshBuilder, guiScaledWidth, 0.0F, 0.0F, 4, null).computeMeshBuilder(1.0F, 0.0F).fetchAlpha(color);
      MeshBuilder.defaultImpl(meshBuilder, 0.0F, 0.0F, 0.0F, 4, null).computeMeshBuilder(0.0F, 0.0F).fetchAlpha(color);
      MeshBuilder.defaultImpl(
         meshBuilder, Pipelines.INSTANCE.getRenderPipeline20(), null, null, null, null, MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)), null, 94, null
      );
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundSetEntityMotionPacket) {
         Packet<? extends PacketListener> packet = receivePacket.getPacket();
         if (((ClientboundSetEntityMotionPacket)packet).id() == MinecraftHolder.getLocalPlayer().getId()
            && ((ClientboundSetEntityMotionPacket)packet).movement().x != 0.0
            && ((ClientboundSetEntityMotionPacket)packet).movement().z != 0.0) {
            vec32 = new Vec3(
                  -((ClientboundSetEntityMotionPacket)packet).movement().x,
                  ((ClientboundSetEntityMotionPacket)packet).movement().y,
                  -((ClientboundSetEntityMotionPacket)packet).movement().z
               )
               .add(this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
         }
      }

      if (receivePacket.getPacket() instanceof ClientboundExplodePacket) {
         Packet<? extends PacketListener> packet2 = receivePacket.getPacket();
         vec3 = new Vec3(
            ((ClientboundExplodePacket)packet2).center().x, ((ClientboundExplodePacket)packet2).center().y, ((ClientboundExplodePacket)packet2).center().z
         );
      }
   }

   static {
   }

   public static final class Hit {
      @NotNull
      private final Vec3 vec;
      @NotNull
      private final Animation animation;

      public Hit(Vec3 vec, Animation animation) {
         this.vec = vec;
         this.animation = animation;
      }

      public Hit(Vec3 vec3, Animation animation, int value, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value & 2) != 0) {
            animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 1000L);
         }

         this(vec3, animation);
      }

      @NotNull
      public final Vec3 getVec() {
         return this.vec;
      }

      @NotNull
      public final Animation getAnimation() {
         return this.animation;
      }

      @NotNull
      public final Vec3 getVec2() {
         return this.vec;
      }

      @NotNull
      public final Animation getAnimation2() {
         return this.animation;
      }

      @NotNull
      @Override
      public String toString() {
         return "Hit(vec=" + this.vec + ", animation=" + this.animation + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.vec.hashCode();
         return hashCode * 31 + this.animation.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof DamageTint.Hit hit)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.vec, hit.vec) ? false : Intrinsics.areEqual(this.animation, hit.animation);
         }
      }
   }
}