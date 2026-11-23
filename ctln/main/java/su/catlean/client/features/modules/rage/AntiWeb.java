// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WebBlock;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.CobWebEvent;
import su.catlean.api.event.events.player.CollisionEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalBreakChecker;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class AntiWeb extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AntiWeb.class, "mode", "getMode()Lsu/catlean/mc;", 0), new MutablePropertyReference1Impl(AntiWeb.class, "timer", "getTimer()F", 0), new MutablePropertyReference1Impl(AntiWeb.class, "speed", "getSpeed()F", 0), new MutablePropertyReference1Impl(AntiWeb.class, "grim", "getGrim()Z", 0) };
   @NotNull
   public static final AntiWeb INSTANCE = new AntiWeb();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", AntiWeb.Mode.SOLID, null, null, 12, null);
   @NotNull
   private static final FloatSetting timer = SettingFactory.defaultImpl(
      INSTANCE, "timer", 20.0F, RangesKt.rangeTo(1.0F, 50.0F), null, () -> INSTANCE.getMode() == AntiWeb.Mode.TIMER, 8, null
   );
   @NotNull
   private static final FloatSetting speed = SettingFactory.defaultImpl(
      INSTANCE, "speed", 0.3F, RangesKt.rangeTo(0.0F, 10.0F), null, () -> INSTANCE.getMode() == AntiWeb.Mode.FLY, 8, null
   );
   @NotNull
   private static final BooleanSetting grim = SettingFactory.defaultImpl(
      INSTANCE, "grim", false, null, () -> INSTANCE.getMode() == AntiWeb.Mode.IGNORE, 4, null
   );

   private AntiWeb() {
      super("anti-web", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final AntiWeb.Mode getMode() {
      return (AntiWeb.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getTimer() {
      return ((Number)timer.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final float getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[2])).floatValue();
   }

   private final boolean getGrim() {
      return (Boolean)grim.computeObject(this, $$delegatedProperties[3]);
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (CrystalBreakChecker.INSTANCE.checkGetX((Player)MinecraftHolder.getLocalPlayer())) {
         if (this.getMode() == AntiWeb.Mode.TIMER && !MinecraftHolder.getMinecraft().player.onGround()) {
            TickService.INSTANCE.setAmount2(this.getTimer());
         }

         if (this.getMode() == AntiWeb.Mode.FLY) {
            double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, this.getSpeed(), false, 0.0F, 0.0F, 0.0F, 30, null);
            MinecraftHolder.getLocalPlayer().setDeltaMovement(defaultImpl[0], 0.0, defaultImpl[1]);
            if (MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
               MinecraftHolder.getLocalPlayer().setDeltaMovement(MinecraftHolder.getLocalPlayer().getDeltaMovement().add(0.0, this.getSpeed(), 0.0));
            }

            if (MinecraftHolder.getMinecraft().options.keyShift.isDown()) {
               MinecraftHolder.getLocalPlayer().setDeltaMovement(MinecraftHolder.getLocalPlayer().getDeltaMovement().add(0.0, -this.getSpeed(), 0.0));
            }
         }
      }
   }

   @Flow
   public final void onCollision(CollisionEvent e) {
      if (e.getState().getBlock() instanceof WebBlock && this.getMode() == AntiWeb.Mode.SOLID) {
         e.setState(Blocks.DIRT.defaultBlockState());
      }
   }

   @Flow
   public final void onCobWeb(CobWebEvent e) {
      if (this.getMode() == AntiWeb.Mode.IGNORE) {
         e.setCancelled(true);
         if (this.getGrim()) {
            PacketUtil.applyCurrentSequence(arg -> (Packet)(new ServerboundPlayerActionPacket(Action.STOP_DESTROY_BLOCK, e.getPos(), Direction.UP, arg)));
         }
      }
   }

   static {
   }

   public static enum Mode {
      TIMER,
      SOLID,
      IGNORE,
      FLY;

      private static final AntiWeb.Mode[] modes = $values();
      private static final EnumEntries<AntiWeb.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<AntiWeb.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final AntiWeb.Mode[] $values() {
         return new AntiWeb.Mode[]{TIMER, SOLID, IGNORE, FLY};
      }
   }
}