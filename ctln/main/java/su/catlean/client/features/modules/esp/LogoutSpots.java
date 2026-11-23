// deobf by pusia
package su.catlean.client.features.modules.esp;

import su.catlean.utils.KotlinCompat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.NoWhenBranchMatchedException;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Entry;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import org.joml.Quaternionfc;
import org.joml.Vector4d;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.Pipelines;
import su.catlean.client.render.PlayerEspRenderer;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.render.vertex.PolygonVertexConsumer;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.render.MeshBuilder;
import su.catlean.utils.render.Render2D;

public final class LogoutSpots extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(LogoutSpots.class, "renderMode", "getRenderMode()Lsu/catlean/o3;", 0), new PropertyReference1Impl(LogoutSpots.class, "color", "getColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(LogoutSpots.class, "friends", "getFriends()Z", 0), new MutablePropertyReference1Impl(LogoutSpots.class, "hDistance", "getHDistance()I", 0), new MutablePropertyReference1Impl(LogoutSpots.class, "vDistance", "getVDistance()I", 0), new PropertyReference1Impl(LogoutSpots.class, "timeOut", "getTimeOut()Z", 0), new PropertyReference1Impl(LogoutSpots.class, "timeOutValue", "getTimeOutValue()F", 0) };
   @NotNull
   public static final LogoutSpots INSTANCE = new LogoutSpots();
   @NotNull
   private static final EnumSetting renderMode = SettingFactory.defaultImpl(INSTANCE, "render-mode", LogoutSpots.RenderMode.GHOST, null, null, 12, null);
   @NotNull
   private static final ColorSetting color = SettingFactory.defaultImpl(INSTANCE, "color", Color.WHITE, null, null, 12, null);
   @NotNull
   private static final BooleanSetting friends = SettingFactory.defaultImpl(INSTANCE, "friends", false, null, null, 12, null);
   @NotNull
   private static final IntSetting maxHorizontalDistance = SettingFactory.defaultImpl(
      INSTANCE, "max-horizontal-distance", 64, new IntRange(1, 256), null, null, 24, null
   );
   @NotNull
   private static final IntSetting maxVerticalDistance = SettingFactory.defaultImpl(
      INSTANCE, "max-vertical-distance", 32, new IntRange(1, 256), null, null, 24, null
   );
   @NotNull
   private static final BooleanSetting timeOut = SettingFactory.defaultImpl(INSTANCE, "time-out", false, null, null, 12, null);
   @NotNull
   private static final FloatSetting timeOutValue = SettingFactory.defaultImpl(
      INSTANCE, "time-out-value", 1.0F, RangesKt.rangeTo(0.0F, 3600.0F), null, null, 24, null
   );
   @NotNull
   private static final Map<Object, Object> map = Maps.newConcurrentMap();
   @NotNull
   private static final ArrayList<LogoutSpots.LogoutSpot> items = new ArrayList();
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("no-connection", 10, 10);

   private LogoutSpots() {
      super("logout-spots", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final LogoutSpots.RenderMode getRenderMode() {
      return (LogoutSpots.RenderMode)renderMode.computeObject(this, $$delegatedProperties[0]);
   }

   private final Color getColor() {
      return (Color)color.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getFriends() {
      return (Boolean)friends.computeObject(this, $$delegatedProperties[2]);
   }

   private final int getHDistance() {
      return ((Number)maxHorizontalDistance.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   private final int getVDistance() {
      return ((Number)maxVerticalDistance.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final boolean getTimeOut() {
      return (Boolean)timeOut.computeObject(this, $$delegatedProperties[5]);
   }

   private final float getTimeOutValue() {
      return ((Number)timeOutValue.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundPlayerInfoUpdatePacket) {
         ClientboundPlayerInfoUpdatePacket packet = (ClientboundPlayerInfoUpdatePacket)receivePacket.getPacket();
         if (packet.actions().contains(Action.ADD_PLAYER)) {
            for (Entry entry : packet.newEntries()) {
               items.removeIf(
                  arg -> {
                     UUID uuid = ((LogoutSpots.LogoutSpot)arg).getUuid();
                     GameProfile profile = entry.profile();
                     boolean flag;
                     if (Intrinsics.areEqual(uuid, profile != null ? profile.id() : null)) {
                        Module instance = INSTANCE;
                        Module instance2 = INSTANCE;
                        Object[] objects = new Object[]{
                           ((LogoutSpots.LogoutSpot)arg).getPlayer().getName().getString(),
                           (int)((LogoutSpots.LogoutSpot)arg).getPlayer().getX(),
                           (int)((LogoutSpots.LogoutSpot)arg).getPlayer().getY(),
                           (int)((LogoutSpots.LogoutSpot)arg).getPlayer().getZ()
                        };
                        ChatUtil.defaultImpl(instance, ChatUtil.defaultImpl(instance2, "login", objects, false, 4, null), false, 2, null);
                        flag = true;
                     } else {
                        flag = false;
                     }

                     return flag;
                  }
               );
            }
         }

         map.clear();
      }

      if (receivePacket.getPacket() instanceof ClientboundPlayerInfoRemovePacket) {
         for (UUID profileId : ((ClientboundPlayerInfoRemovePacket)receivePacket.getPacket()).profileIds()) {
            for (UUID keySetItem : (java.util.Set<UUID>)(java.util.Set<?>)map.keySet()) {
               if (Intrinsics.areEqual(keySetItem, profileId)) {
                  Player map2 = (Player)map.get(keySetItem);
                  if (map2 != null) {
                     Player player = map2;
                     if (INSTANCE.getFriends() || !PlayerNameUtil.checkGetGameProfile(player)) {
                        Module instance3 = INSTANCE;
                        Module instance4 = INSTANCE;
                        Object[] objects2 = new Object[]{player.getName().getString(), (int)player.getX(), (int)player.getY(), (int)player.getZ()};
                        ChatUtil.defaultImpl(instance3, ChatUtil.defaultImpl(instance4, "logout", objects2, false, 4, null), false, 2, null);
                        items.add(new LogoutSpots.LogoutSpot(keySetItem, player, System.currentTimeMillis()));
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void update2() {
      map.clear();
      items.clear();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
         if (!Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer()) && !this.checkGetGameProfile((Player)player)) {
            map.put(player.getGameProfile().id(), player);
         }
      }

      if (this.getTimeOut()) {
         Iterable<LogoutSpots.LogoutSpot> arrayList = Lists.newArrayList(items);

         for (Object object : arrayList) {
            LogoutSpots.LogoutSpot logoutSpot = (LogoutSpots.LogoutSpot)object;
            if ((float)(MinecraftHolder.getTime() - logoutSpot.getTime()) > INSTANCE.getTimeOutValue() * 1000) {
               items.remove(logoutSpot);
            }
         }
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      switch (LogoutSpots.WhenMappings.values[this.getRenderMode().ordinal()]) {
         case 1:
            this.render(render3DEvent.getStack());
            break;
         case 2:
            PlayerEspRenderer instance = PlayerEspRenderer.INSTANCE;
            PoseStack stack = render3DEvent.getStack();
            PlayerEsp.RenderMode both = PlayerEsp.RenderMode.BOTH;
            Color color2 = this.getColor();
            Color color3 = Render2D.INSTANCE.computeColor(this.getColor(), 1.0F);
            Iterable<LogoutSpots.LogoutSpot> items2 = items;
            Color color4 = color3;
            Color color5 = color2;
            PlayerEsp.RenderMode renderMode2 = both;
            PoseStack poseStack = stack;
            PlayerEspRenderer playerEspRenderer = instance;
            var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items2, 10));

            for (Object object : items2) {
               LogoutSpots.LogoutSpot logoutSpot = (LogoutSpots.LogoutSpot)object;
               arrayList.add(logoutSpot.getPlayer());
            }

            List list = arrayList;
            playerEspRenderer.render(poseStack, renderMode2, color5, color4, Lists.newArrayList(list), this.getVDistance(), this.getHDistance());
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      for (Object array : Lists.newArrayList(items)) {
         LogoutSpots.LogoutSpot logoutSpot = (LogoutSpots.LogoutSpot)array;
         float time = INSTANCE.getTimeOut() ? 1.0F - (float)(MinecraftHolder.getTime() - logoutSpot.getTime()) / (INSTANCE.getTimeOutValue() * 1000.0F) : 1.0F;
         Vec3 window = Render2D.INSTANCE.fetchWindow(INSTANCE.computeVec3((Entity)logoutSpot.getPlayer()).add(0.0, 2.0, 0.0));
         Vector4d vector4d = null;
         if (window.z > 0.0 && window.z < 1.0) {
            vector4d = new Vector4d(window.x, window.y, window.z, 0.0);
            vector4d.x = Math.min(window.x, vector4d.x);
            vector4d.y = Math.min(window.y, vector4d.y);
            vector4d.z = Math.max(window.x, vector4d.z);
         }

         float currentTimeMillis = (float)(System.currentTimeMillis() - logoutSpot.getTime()) / 500.0F;
         int currentTimeMillis2 = (int)((System.currentTimeMillis() - logoutSpot.getTime()) / 1000.0);
         String string = logoutSpot.getPlayer().getName().getString()
            + " "
            + (int)(logoutSpot.getPlayer().getHealth() + logoutSpot.getPlayer().getAbsorptionAmount())
            + " "
            + currentTimeMillis2 / 60
            + ":"
            + currentTimeMillis2 % 60;
         string = StringsKt.dropLast(string, Math.max(0, (int)(string.length() * (1.0F - currentTimeMillis))));
         if (vector4d != null) {
            float value = (float)(vector4d.z - vector4d.x) / 2.0F;
            float amount = Fonts.getFontRenderer6().computeAmount(string) + 18.0F;
            float value2 = (float)(vector4d.x + value - amount / 2.0F);
            TexturedQuadPass.defaultImpl(
               render2DEvent.getContext().pose(),
               value2 - 2,
               (float)vector4d.y - 13.0F,
               amount,
               12.0F,
               2.0F,
               1.5F,
               0.0F,
               1.0F,
               Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor5(), time * Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor5())),
               Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor6(), time * Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor5())),
               0.0F,
               0.0F,
               0.0F,
               14336,
               null
            );
            SvgTexture.defaultImpl(
               svgTexture,
               (Matrix3x2f)render2DEvent.getContext().pose(),
               value2,
               (float)vector4d.y - 13.5,
               0.0F,
               0.0F,
               Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), time * Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor5())),
               null,
               0.0F,
               216,
               null
            );
            Fonts.getFontRenderer6()
               .render(
                  render2DEvent.getContext(),
                  string,
                  value2 + 12.0F,
                  (float)vector4d.y - 13.0F,
                  Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor7(), time * Render2D.INSTANCE.fetchAlpha(ThemeManager.INSTANCE.getColor5()))
               );
         }
      }
   }

   private final boolean checkGetGameProfile(Player player) {
      return KotlinCompat.contains(player.getGameProfile().name(), "-", false, 2, null)
         || KotlinCompat.contains(player.getGameProfile().name(), "_", false, 2, null);
   }

   private final void render(PoseStack poseStack) {
      for (Object array : Lists.newArrayList(items)) {
         LogoutSpots.LogoutSpot logoutSpot = (LogoutSpots.LogoutSpot)array;
         INSTANCE.render(
            poseStack,
            logoutSpot,
            INSTANCE.getTimeOut()
               ? 1.0F - (float)(MinecraftHolder.getTime() - logoutSpot.getTime()) / (INSTANCE.getTimeOutValue() * 1000.0F)
               : Math.max(0.4F, 1.0F - (float)(MinecraftHolder.getTime() - logoutSpot.getTime()) / 800.0F)
         );
      }
   }

   private final void render(PoseStack poseStack, LogoutSpots.LogoutSpot logoutSpot, float amount) {
      Player player = logoutSpot.getPlayer();
      PlayerModel model = (PlayerModel)((AvatarRenderer)MinecraftHolder.getMinecraft().getEntityRenderDispatcher().getRenderer((Entity)player)).getModel();
      EntityRenderState renderState = MinecraftHolder.getMinecraft().getEntityRenderDispatcher().getRenderer((Entity)player).createRenderState((Entity)player, 1.0F);
      String text = "null cannot be cast to non-null type net.minecraft.client.renderer.entity.state.AvatarRenderState";
      AvatarRenderState avatarRenderState = (AvatarRenderState)renderState;
      TextureManager textureManager = MinecraftHolder.getMinecraft().getTextureManager();
      String text2 = "null cannot be cast to non-null type net.minecraft.client.player.RemotePlayer";
      GpuTextureView textureView = textureManager.getTexture(((RemotePlayer)player).getSkin().body().texturePath()).getTextureView();
      MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, 256, false, 4, null);
      PolygonVertexConsumer polygonVertexConsumer = new PolygonVertexConsumer(meshBuilder, Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), amount));
      poseStack.pushPose();
      Vec3 vec3 = this.computeVec3((Entity)player);
      double value = vec3.x - MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position().x();
      double value2 = vec3.y - MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position().y();
      double value3 = vec3.z - MinecraftHolder.getMinecraft().getEntityRenderDispatcher().camera.position().z();
      poseStack.translate((float)value, (float)value2, (float)value3);
      poseStack.scale(avatarRenderState.scale, avatarRenderState.scale, avatarRenderState.scale);
      this.render(avatarRenderState, poseStack, avatarRenderState.bodyRot);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      poseStack.scale(0.9375F, 0.9375F, 0.9375F);
      poseStack.translate(0.0F, -1.501F, 0.0F);
      model.setupAnim(avatarRenderState);
      model.renderToBuffer(poseStack, polygonVertexConsumer, 15728880, 0, ThemeManager.INSTANCE.getColor2().getRGB());
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

   public final void render(AvatarRenderState state, PoseStack stack, float amount) {
      if (state.isFallFlying) {
         stack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - amount));
         if (!state.isAutoSpinAttack) {
            stack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(state.fallFlyingScale() * (-90.0F - state.xRot)));
         }

         if (state.shouldApplyFlyingYRot) {
            stack.mulPose((Quaternionfc)Axis.YP.rotation(state.flyingYRot));
         }
      } else if (state.swimAmount > 0.0F) {
         stack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - amount));
         stack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Mth.lerp(state.swimAmount, 0.0F, state.isInWater ? -90.0F - state.xRot : -90.0F)));
         if (state.isVisuallySwimming) {
            stack.translate(0.0F, -1.0F, 0.3F);
         }
      } else {
         stack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F - amount));
      }
   }

   static {
   }

   public static final class LogoutSpot {
      @NotNull
      private final UUID uuid;
      @NotNull
      private final Player player;
      private long time;

      public LogoutSpot(UUID uuid, Player player, long time) {
         this.uuid = uuid;
         this.player = player;
         this.time = time;
      }

      @NotNull
      public final UUID getUuid() {
         return this.uuid;
      }

      @NotNull
      public final Player getPlayer() {
         return this.player;
      }

      public final long getTime() {
         return this.time;
      }

      public final void setTime(long time) {
         this.time = time;
      }

      @NotNull
      public final UUID getUuid2() {
         return this.uuid;
      }

      @NotNull
      public final Player getPlayer2() {
         return this.player;
      }

      public final long getTime2() {
         return this.time;
      }

      @NotNull
      @Override
      public String toString() {
         long time2 = this.time;
         Player player2 = this.player;
         UUID uuid2 = this.uuid;
         return "LogoutSpot(uuid=" + uuid2 + ", player=" + player2 + ", logTime=" + time2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.uuid.hashCode();
         hashCode = hashCode * 31 + this.player.hashCode();
         return hashCode * 31 + Long.hashCode(this.time);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof LogoutSpots.LogoutSpot logoutSpot)) {
            return false;
         } else if (!Intrinsics.areEqual(this.uuid, logoutSpot.uuid)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.player, logoutSpot.player) ? false : this.time == logoutSpot.time;
         }
      }
   }

   public static enum RenderMode {
      DEFAULT,
      GHOST;

      private static final LogoutSpots.RenderMode[] renderModes = $values();
      private static final EnumEntries<LogoutSpots.RenderMode> enumEntries = EnumEntriesKt.enumEntries(renderModes);

      private RenderMode() {
      }

      @NotNull
      public static EnumEntries<LogoutSpots.RenderMode> getEnumEntries() {
         return enumEntries;
      }

      private static final LogoutSpots.RenderMode[] $values() {
         return new LogoutSpots.RenderMode[]{DEFAULT, GHOST};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[LogoutSpots.RenderMode.values().length];

         try {
            values2[LogoutSpots.RenderMode.GHOST.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[LogoutSpots.RenderMode.DEFAULT.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}