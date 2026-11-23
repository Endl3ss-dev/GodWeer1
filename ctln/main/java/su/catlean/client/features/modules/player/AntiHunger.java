// deobf by pusia
package su.catlean.client.features.modules.player;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ServerboundMovePlayerPacketAccessor;

public final class AntiHunger extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AntiHunger.class, "ground", "getGround()Z", 0), new PropertyReference1Impl(AntiHunger.class, "sprint", "getSprint()Z", 0) };
   @NotNull
   public static final AntiHunger INSTANCE = new AntiHunger();
   @NotNull
   private static final BooleanSetting cancelGround = SettingFactory.defaultImpl(INSTANCE, "cancel-ground", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting cancelSprint = SettingFactory.defaultImpl(INSTANCE, "cancel-sprint", true, null, null, 12, null);

   private AntiHunger() {
      super("anti-hunger", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final boolean getGround() {
      return (Boolean)cancelGround.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getSprint() {
      return (Boolean)cancelSprint.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundMovePlayerPacket && this.getGround()) {
         ((ServerboundMovePlayerPacketAccessor)sendPacket.getPacket()).setGround(false);
      }

      if (sendPacket.getPacket() instanceof ServerboundPlayerCommandPacket
         && this.getSprint()
         && ((ServerboundPlayerCommandPacket)sendPacket.getPacket()).getAction() == Action.START_SPRINTING) {
         sendPacket.cancel();
      }
   }

   static {
   }
}