// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.TickService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class AntiVoid extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AntiVoid.class, "mode", "getMode()Lsu/catlean/d_;", 0), new MutablePropertyReference1Impl(AntiVoid.class, "rubberBandPacket", "getRubberBandPacket()Z", 0) };
   @NotNull
   public static final AntiVoid INSTANCE = new AntiVoid();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", AntiVoid.Mode.TIMER, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rubberBand = SettingFactory.defaultImpl(
      INSTANCE, "rubber-band", true, null, () -> INSTANCE.getMode() == AntiVoid.Mode.NCP, 4, null
   );

   private AntiVoid() {
      super("anti-void", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final AntiVoid.Mode getMode() {
      return (AntiVoid.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getRubberBandPacket() {
      return (Boolean)rubberBand.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow(
      priority = -20
   )
   public final void onMove(MoveEvent e) {
      if (this.isFlag()) {
         if (this.getMode() == AntiVoid.Mode.NCP) {
            e.setCancelled(true);
            e.setY(0.0);
            if (this.getRubberBandPacket()) {
               Packet pos = (Packet)(
                  new Pos(
                     MinecraftHolder.getLocalPlayer().getX(),
                     MinecraftHolder.getLocalPlayer().getY(),
                     MinecraftHolder.getLocalPlayer().getZ(),
                     true,
                     MinecraftHolder.getLocalPlayer().horizontalCollision
                  )
               );
               PacketUtil.applySend(pos);
            }
         } else {
            TickService.INSTANCE.setAmount2(0.2F);
         }
      }
   }

   private final boolean isFlag() {
      if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
         return false;
      } else {
         for (int i = (int)MinecraftHolder.getLocalPlayer().getY(); -65 < i; i--) {
            if (!MinecraftHolder.getClientLevel()
               .isEmptyBlock(BlockPos.containing(MinecraftHolder.getLocalPlayer().getX(), i, MinecraftHolder.getLocalPlayer().getZ()))) {
               return false;
            }
         }

         return WorldUtil.INSTANCE.getAmount3() > 0.0;
      }
   }

   static {
   }

   public static enum Mode {
      NCP,
      TIMER;

      private static final AntiVoid.Mode[] modes = $values();
      private static final EnumEntries<AntiVoid.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<AntiVoid.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final AntiVoid.Mode[] $values() {
         return new AntiVoid.Mode[]{NCP, TIMER};
      }
   }
}