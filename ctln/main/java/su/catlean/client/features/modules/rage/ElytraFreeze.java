// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class ElytraFreeze extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(ElytraFreeze.class, "packet", "getPacket()Z", 0)};
   @NotNull
   public static final ElytraFreeze INSTANCE = new ElytraFreeze();
   @NotNull
   private static final BooleanSetting cancelPacket = SettingFactory.defaultImpl(INSTANCE, "cancel-packet", false, null, null, 12, null);

   private ElytraFreeze() {
      super("elytra-freeze", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final boolean getPacket() {
      return (Boolean)cancelPacket.computeObject(this, $$delegatedProperties[0]);
   }

   @Flow
   private final void onMove(MoveEvent moveEvent) {
      if (!this.isFlag6()) {
         this.applyUnplug();
      } else {
         if (this.isFlag()) {
            moveEvent.setX(0.0);
            moveEvent.setY(0.0);
            moveEvent.setZ(0.0);
            moveEvent.cancel();
         }
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (this.getPacket()
         && this.isFlag6()
         && this.isFlag()
         && (sendPacket.getPacket() instanceof ServerboundMovePlayerPacket || sendPacket.getPacket() instanceof ServerboundPlayerInputPacket)) {
         sendPacket.cancel();
      }
   }

   private final boolean isFlag() {
      return MinecraftHolder.getLocalPlayer().isFallFlying() && Aura.INSTANCE.getEntity() != null
         ? Aura.INSTANCE.getEntity().distanceTo((Entity)MinecraftHolder.getLocalPlayer()) < Aura.INSTANCE.getAttackRange()
         : false;
   }

   static {
   }
}