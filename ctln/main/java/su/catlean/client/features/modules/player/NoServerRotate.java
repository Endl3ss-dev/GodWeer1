// deobf by pusia
package su.catlean.client.features.modules.player;

import net.minecraft.network.PacketListener;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.PositionMoveRotation;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.FlagEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ClientboundPlayerPositionPacketAccessor;
import su.catlean.utils.MinecraftHolder;

public final class NoServerRotate extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(NoServerRotate.class, "legit", "getLegit()Z", 0)};
   @NotNull
   public static final NoServerRotate INSTANCE = new NoServerRotate();
   @NotNull
   private static final BooleanSetting legit = SettingFactory.defaultImpl(INSTANCE, "legit", true, null, null, 12, null);
   private static float amount;
   private static float amount2;

   private NoServerRotate() {
      super("no-server-rotate", SubCategories.getSubCategory10(), null, 4, null);
   }

   public final boolean getLegit() {
      return (Boolean)legit.computeObject(this, $$delegatedProperties[0]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      amount = MinecraftHolder.getLocalPlayer().getYRot();
      amount2 = MinecraftHolder.getLocalPlayer().getXRot();
   }

   @Flow
   private final void onFlag(FlagEvent flagEvent) {
      if (this.getLegit()) {
         MinecraftHolder.getLocalPlayer().setYRot(amount + 1.0E-6F);
         MinecraftHolder.getLocalPlayer().setXRot(amount2 + 1.0E-6F);
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundPlayerPositionPacket && !this.getLegit()) {
         Packet<? extends PacketListener> packet = receivePacket.getPacket();
         PositionMoveRotation change = ((ClientboundPlayerPositionPacket)packet).change();
         ((ClientboundPlayerPositionPacketAccessor)packet)
            .setChange(
               new PositionMoveRotation(
                  change.position(), change.deltaMovement(), MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot()
               )
            );
      }
   }

   static {
   }
}