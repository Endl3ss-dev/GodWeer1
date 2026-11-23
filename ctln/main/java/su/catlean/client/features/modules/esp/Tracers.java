// deobf by pusia
package su.catlean.client.features.modules.esp;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Quaternionfc;
import su.catlean.api.enums.ParticleType;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.ChestOpenTracker;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.ThreadManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.animation.Animation;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.render.AtlasTexture;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class Tracers extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Tracers.class, "mode", "getMode()Lsu/catlean/_n;", 0), new PropertyReference1Impl(Tracers.class, "flat", "getFlat()Z", 0), new PropertyReference1Impl(Tracers.class, "onlyOffscreen", "getOnlyOffscreen()Z", 0), new PropertyReference1Impl(Tracers.class, "friendColor", "getFriendColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(Tracers.class, "teamColor", "getTeamColor()Z", 0) };
   @NotNull
   public static final Tracers INSTANCE = new Tracers();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", Tracers.Mode.ARROWS_1, null, null, 12, null);
   @NotNull
   private static final BooleanSetting flat = SettingFactory.defaultImpl(INSTANCE, "flat", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyOffScreen = SettingFactory.defaultImpl(INSTANCE, "only-off-screen", false, null, null, 12, null);
   @NotNull
   private static final ColorSetting friend = SettingFactory.defaultImpl(INSTANCE, "friend", Color.GREEN, null, null, 12, null);
   @NotNull
   private static final BooleanSetting teamColors = SettingFactory.defaultImpl(INSTANCE, "team-colors", false, null, null, 12, null);
   private static float amount;
   @NotNull
   private static Animation animation = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);
   @NotNull
   private static Animation animation2 = new Animation(NotificationManager.Notification.Type.OUT_QUINT, 300L);

   private Tracers() {
      super("tracers", SubCategories.getSubCategory7(), null, 4, null);
   }

   @NotNull
   public final Tracers.Mode getMode() {
      return (Tracers.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getFlat() {
      return (Boolean)flat.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getOnlyOffscreen() {
      return (Boolean)onlyOffScreen.computeObject(this, $$delegatedProperties[2]);
   }

   private final Color getFriendColor() {
      return (Color)friend.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getTeamColor() {
      return (Boolean)teamColors.computeObject(this, $$delegatedProperties[4]);
   }

   @Flow
   public final void onRender2D(Render2DEvent e) {
      if (this.getMode() != Tracers.Mode.LINES) {
         boolean flag = MinecraftHolder.getMinecraft().gui.screen() != null && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen);
         animation.applyDoubleValue(!this.getFlat() && !flag ? 42.0F : 90.0F);
         animation2.applyDoubleValue(flag ? 150.0F : 40.0F);
         float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F;
         float guiScaledHeight = MinecraftHolder.getMinecraft().getWindow().getGuiScaledHeight() / 2.0F;
         amount = this.computeAmount(amount, MinecraftHolder.getLocalPlayer().getYRot(), 13.0F);
         Iterable<Object> items = ThreadManager.INSTANCE.getItems();
         int value = 0;

         for (Object object : items) {
            int value2 = value++;
            if (value2 < 0) {
               CollectionsKt.throwIndexOverflow();
            }

            AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
            if (!Intrinsics.areEqual(abstractClientPlayer, MinecraftHolder.getMinecraft().player)
               && (!INSTANCE.getOnlyOffscreen() || !Render3D.INSTANCE.getFrustum().isVisible(abstractClientPlayer.getBoundingBox()))) {
               PoseStack poseStack = new PoseStack();
               poseStack.pushPose();
               poseStack.translate(guiScaledWidth, guiScaledHeight, 0.0F);
               float value3 = Math.min(90.0F, Math.max(MinecraftHolder.getLocalPlayer().getXRot(), animation.getAmount()));
               poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Math.abs(value3) - 102));
               poseStack.translate(-guiScaledWidth, -guiScaledHeight, 0.0F);
               double amount2 = Render2D.INSTANCE.computeAmount(abstractClientPlayer.xo, abstractClientPlayer.getX(), (double)Render3D.INSTANCE.getAmount4());
               double amount3 = Render2D.INSTANCE.computeAmount(abstractClientPlayer.zo, abstractClientPlayer.getZ(), (double)Render3D.INSTANCE.getAmount4());
               double amount4 = Render2D.INSTANCE
                  .computeAmount(MinecraftHolder.getLocalPlayer().xo, MinecraftHolder.getLocalPlayer().getX(), (double)Render3D.INSTANCE.getAmount4());
               double amount5 = Render2D.INSTANCE
                  .computeAmount(MinecraftHolder.getLocalPlayer().zo, MinecraftHolder.getLocalPlayer().getZ(), (double)Render3D.INSTANCE.getAmount4());
               float wrapDegrees = (float)Mth.wrapDegrees(Math.toDegrees(Math.atan2(amount3 - amount5, amount2 - amount4)) - 90.0) - amount;
               poseStack.translate(guiScaledWidth, guiScaledHeight, 0.0F);
               poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(wrapDegrees));
               poseStack.translate(-guiScaledWidth, -guiScaledHeight, 0.0F);
               Color friendColor = PlayerNameUtil.checkGetGameProfile((Player)abstractClientPlayer) ? INSTANCE.getFriendColor() : ThemeManager.INSTANCE.computeColor(value2 * 30);
               if (abstractClientPlayer.getTeam() != null && INSTANCE.getTeamColor()) {
                  friendColor = new Color(abstractClientPlayer.getTeamColor());
               }

               INSTANCE.render(poseStack, guiScaledWidth, guiScaledHeight - animation2.getAmount(), 18.0F, friendColor);
               poseStack.popPose();
            }
         }
      }
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
            this.getMode().getParticleType().getAmount(),
            this.getMode().getParticleType().getAmount2(),
            this.getMode().getParticleType().getAmount3(),
            this.getMode().getParticleType().getAmount4()
         );
   }

   @Flow
   public final void onRender3D(Render3DEvent e) {
      if (this.getMode() == Tracers.Mode.LINES) {
         for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
            if (!Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer())) {
               AABB aabb = Render3D.INSTANCE.computeAABB((Entity)player);
               Vec3 position = new Vec3(0.0, 0.0, 75.0)
                  .xRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().xRot())))
                  .yRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().yRot())))
                  .add(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().position());
               Color friendColor = PlayerNameUtil.checkGetGameProfile((Player)player) ? this.getFriendColor() : ThemeManager.INSTANCE.computeColor(30);
               if (player.getTeam() != null && this.getTeamColor()) {
                  friendColor = new Color(player.getTeamColor());
               }

               Render3D.defaultImpl(Render3D.INSTANCE, position, aabb.getCenter(), friendColor, null, true, 8, null);
            }
         }
      }
   }

   private final float computeAmount(float amount, float amount2, float amount3) {
      float value = Math.clamp((ChestOpenTracker.INSTANCE.getValue() > 5 ? 1.0F / ChestOpenTracker.INSTANCE.getValue() : 0.016F) * amount3, 0.0F, 1.0F);
      return (1.0F - value) * amount + value * amount2;
   }

   static {
   }

   public static enum Mode {
      ARROWS_1(ParticleType.TRIANGLE1),
      ARROWS_2(ParticleType.TRIANGLE2),
      ARROWS_3(ParticleType.TRIANGLE3),
      ARROWS_4(ParticleType.TRIANGLE4),
      ARROWS_5(ParticleType.TRIANGLE5),
      LINES(ParticleType.TRIANGLE1);

      @NotNull
      private final ParticleType particleType;
      private static final Tracers.Mode[] modes = $values();
      private static final EnumEntries<Tracers.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode(ParticleType particleType) {
         this.particleType = particleType;
      }

      @NotNull
      public final ParticleType getParticleType() {
         return this.particleType;
      }

      @NotNull
      public static EnumEntries<Tracers.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final Tracers.Mode[] $values() {
         return new Tracers.Mode[]{ARROWS_1, ARROWS_2, ARROWS_3, ARROWS_4, ARROWS_5, LINES};
      }
   }
}