// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class Flight extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Flight.class, "mode", "getMode()Lsu/catlean/uy;", 0), new PropertyReference1Impl(Flight.class, "hSpeed", "getHSpeed()F", 0), new PropertyReference1Impl(Flight.class, "vSpeed", "getVSpeed()F", 0), new PropertyReference1Impl(Flight.class, "antiKick", "getAntiKick()Z", 0) };
   @NotNull
   public static final Flight INSTANCE = new Flight();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", Flight.Mode.Vanilla, null, null, 12, null);
   @NotNull
   private static final FloatSetting horizontal = SettingFactory.defaultImpl(INSTANCE, "horizontal", 1.0F, RangesKt.rangeTo(0.0F, 10.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting vertical = SettingFactory.defaultImpl(INSTANCE, "vertical", 0.78F, RangesKt.rangeTo(0.0F, 5.0F), null, null, 24, null);
   @NotNull
   private static final BooleanSetting antiKick = SettingFactory.defaultImpl(
      INSTANCE, "anti-kick", false, null, () -> INSTANCE.getMode() == Flight.Mode.Creative || INSTANCE.getMode() == Flight.Mode.Vanilla, 4, null
   );
   @Nullable
   private static Vec3 vec3 = Vec3.ZERO;

   private Flight() {
      super("flight", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final Flight.Mode getMode() {
      return (Flight.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getHSpeed() {
      return ((Number)horizontal.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final float getVSpeed() {
      return ((Number)vertical.computeObject(this, $$delegatedProperties[2])).floatValue();
   }

   private final boolean getAntiKick() {
      return (Boolean)antiKick.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   private final void onPreSync(PreSyncEvent preSyncEvent) {
      switch (Flight.WhenMappings.values[this.getMode().ordinal()]) {
         case 1:
            if (WorldUtil.INSTANCE.isFlag4()) {
               double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, this.getHSpeed(), false, 0.0F, 0.0F, 0.0F, 30, null);
               MinecraftHolder.getLocalPlayer().setDeltaMovement(defaultImpl[0], 0.0, defaultImpl[1]);
            } else {
               MinecraftHolder.getLocalPlayer().setDeltaMovement(0.0, 0.0, 0.0);
            }

            if (MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
               MinecraftHolder.getLocalPlayer().setDeltaMovement(MinecraftHolder.getLocalPlayer().getDeltaMovement().add(0.0, this.getVSpeed(), 0.0));
            }

            if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
               MinecraftHolder.getLocalPlayer().setDeltaMovement(MinecraftHolder.getLocalPlayer().getDeltaMovement().add(0.0, -this.getVSpeed(), 0.0));
            }
            break;
         case 2:
            MinecraftHolder.getLocalPlayer().setSprinting(true);
            if (MinecraftHolder.getLocalPlayer().tickCount % 2 == 0) {
               preSyncEvent.cancel();
               Packet statusOnly = (Packet)(new StatusOnly(true, false));
               PacketUtil.applySend(statusOnly);
            }
      }

      if (this.getAntiKick() && MinecraftHolder.getLocalPlayer().tickCount % 15 == 0) {
         MinecraftHolder.getLocalPlayer()
            .setDeltaMovement(
               new Vec3(
                  MinecraftHolder.getLocalPlayer().getDeltaMovement().x,
                  MinecraftHolder.getLocalPlayer().tickCount % 30 == 0 ? -0.08 : 0.08,
                  MinecraftHolder.getLocalPlayer().getDeltaMovement().z
               )
            );
      }
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent event) {
      if (this.getMode() == Flight.Mode.Creative) {
         MinecraftHolder.getLocalPlayer().getAbilities().flying = true;
         MinecraftHolder.getLocalPlayer().getAbilities().setFlyingSpeed(this.getHSpeed() / 10.0F);
      }

      if (this.getMode() == Flight.Mode.MATRIX_JUMP && WorldUtil.INSTANCE.getAmount3() > 0.0) {
         double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, this.getHSpeed(), false, 0.0F, 0.0F, 0.0F, 30, null);
         MinecraftHolder.getLocalPlayer()
            .setDeltaMovement(
               defaultImpl[0],
               MinecraftHolder.getMinecraft().options.keyJump.isDown()
                  ? INSTANCE.getVSpeed()
                  : (MinecraftHolder.getMinecraft().options.keyShift.isDown() ? -INSTANCE.getVSpeed() : 0.0),
               defaultImpl[1]
            );
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (this.getMode() == Flight.Mode.MATRIX_JUMP && receivePacket.getPacket() instanceof ClientboundPlayerPositionPacket) {
         vec3 = MinecraftHolder.getLocalPlayer().getDeltaMovement();
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (this.getMode() == Flight.Mode.MATRIX_JUMP && sendPacket.getPacket() instanceof PosRot && vec3 != null) {
         MinecraftHolder.getLocalPlayer().setDeltaMovement(vec3.x, vec3.y, vec3.z);
         this.applyUnplug();
         vec3 = null;
      }
   }

   @Flow
   public final void onMove(MoveEvent event) {
      if (this.getMode() == Flight.Mode.GRIM_GLIDE) {
         double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, 0.3F, false, 1.0F, 0.0F, 0.0F, 16, null);
         event.cancel();
         event.setY(-0.03);
         event.setX(defaultImpl[0]);
         event.setZ(defaultImpl[1]);
         MinecraftHolder.getLocalPlayer().setDeltaMovement(event.getX(), -0.03, event.getZ());
      }
   }

   @Override
   public void update4() {
      MinecraftHolder.getLocalPlayer().getAbilities().flying = false;
      MinecraftHolder.getLocalPlayer().getAbilities().setFlyingSpeed(0.05F);
   }

   static {
   }

   public static enum Mode {
      Vanilla,
      Creative,
      MATRIX_JUMP,
      GRIM_GLIDE;

      private static final Flight.Mode[] modes = $values();
      private static final EnumEntries<Flight.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<Flight.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final Flight.Mode[] $values() {
         return new Flight.Mode[]{Vanilla, Creative, MATRIX_JUMP, GRIM_GLIDE};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Flight.Mode.values().length];

         try {
            values2[Flight.Mode.Vanilla.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Flight.Mode.GRIM_GLIDE.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}