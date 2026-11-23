// deobf by pusia
package su.catlean.client.features.modules.protect;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.StopUsingItemEvent;
import su.catlean.api.event.events.player.UsingItemEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;

public final class AutoGapple extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(AutoGapple.class, "health", "getHealth()F", 0)};
   @NotNull
   public static final AutoGapple INSTANCE = new AutoGapple();
   @NotNull
   private static final FloatSetting health = SettingFactory.defaultImpl(INSTANCE, "health", 15.0F, RangesKt.rangeTo(1.0F, 36.0F), null, null, 24, null);
   private static boolean flag;

   private AutoGapple() {
      super("auto-gapple", SubCategories.getSubCategory(), null, 4, null);
   }

   private final float getHealth() {
      return ((Number)health.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      flag = false;
      boolean flag2 = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.GOLDEN_APPLE)
         || Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.ENCHANTED_GOLDEN_APPLE);
      boolean flag3 = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.GOLDEN_APPLE)
         || Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.ENCHANTED_GOLDEN_APPLE);
      if ((flag2 || flag3) && MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount() <= this.getHealth()) {
         if (!MinecraftHolder.getLocalPlayer().isUsingItem()) {
            ContainerUtil.defaultImpl(flag2 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
         }

         flag = true;
      }
   }

   @Flow
   private final void onUsingItem(UsingItemEvent usingItemEvent) {
      if (flag) {
         usingItemEvent.cancel();
      }
   }

   @Flow
   private final void onStopUsingItem(StopUsingItemEvent stopUsingItemEvent) {
      if (flag) {
         stopUsingItemEvent.cancel();
      }
   }

   static {
   }
}