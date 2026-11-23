// deobf by pusia
package su.catlean.client.features.modules.legit;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;

public final class BowSpam extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(BowSpam.class, "delay", "getDelay()I", 0)};
   @NotNull
   public static final BowSpam INSTANCE = new BowSpam();
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 3, new IntRange(0, 20), null, null, 24, null);

   private BowSpam() {
      super("bow-spam", SubCategories.getSubCategory3(), null, 4, null);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
      if ((Intrinsics.areEqual(localPlayer.getOffhandItem().getItem(), Items.BOW) || Intrinsics.areEqual(localPlayer.getMainHandItem().getItem(), Items.BOW))
         && localPlayer.isUsingItem()
         && localPlayer.getTicksUsingItem() >= INSTANCE.getDelay()) {
         MinecraftHolder.getMultiPlayerGameMode().releaseUsingItem((Player)MinecraftHolder.getLocalPlayer());
         ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
         localPlayer.releaseUsingItem();
      }
   }

   static {
   }
}