// deobf by pusia
package su.catlean.client.features.modules.misc;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.random.Random;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;

public final class TapeMouse extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(TapeMouse.class, "mode", "getMode()Lsu/catlean/z2;", 0), new PropertyReference1Impl(TapeMouse.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(TapeMouse.class, "randomize", "getRandomize()I", 0) };
   @NotNull
   public static final TapeMouse INSTANCE = new TapeMouse();
   @NotNull
   private static final EnumSetting button = SettingFactory.defaultImpl(INSTANCE, "button", TapeMouse.Button.LEFT, null, null, 12, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 600, new IntRange(0, 10000), null, null, 24, null);
   @NotNull
   private static final IntSetting randomize = SettingFactory.defaultImpl(INSTANCE, "randomize", 50, new IntRange(0, 10000), null, null, 24, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private TapeMouse() {
      super("tape-mouse", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final TapeMouse.Button getMode() {
      return (TapeMouse.Button)button.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final int getRandomize() {
      return ((Number)randomize.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   @Flow
   public final void onRender2D(Render2DEvent event) {
      if (stopWatch.testValue2(this.getDelay() + (int)(this.getRandomize() * Random.Default.nextDouble()))) {
         HitResult hitResult = MinecraftHolder.getMinecraft().hitResult;
         if (hitResult == null) {
            return;
         }

         HitResult hitResult2 = hitResult;
         if (this.getMode() == TapeMouse.Button.LEFT) {
            if (hitResult2 instanceof EntityHitResult) {
               MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), ((EntityHitResult)hitResult2).getEntity());
            } else if (hitResult2 instanceof BlockHitResult && !MinecraftHolder.getClientLevel().isEmptyBlock(((BlockHitResult)hitResult2).getBlockPos())) {
               MinecraftHolder.getMultiPlayerGameMode().startDestroyBlock(((BlockHitResult)hitResult2).getBlockPos(), ((BlockHitResult)hitResult2).getDirection());
            }

            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         } else if (hitResult2 instanceof EntityHitResult) {
            MinecraftHolder.getMultiPlayerGameMode()
               .interact((Player)MinecraftHolder.getLocalPlayer(), ((EntityHitResult)hitResult2).getEntity(), (EntityHitResult)hitResult2, InteractionHand.MAIN_HAND);
         } else if (hitResult2 instanceof BlockHitResult && !MinecraftHolder.getClientLevel().isEmptyBlock(((BlockHitResult)hitResult2).getBlockPos())) {
            MinecraftHolder.getMultiPlayerGameMode().useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, (BlockHitResult)hitResult2);
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         }
      }
   }

   static {
   }

   public static enum Button {
      RIGHT,
      LEFT;

      private static final TapeMouse.Button[] buttons = $values();
      private static final EnumEntries<TapeMouse.Button> enumEntries = EnumEntriesKt.enumEntries(buttons);

      private Button() {
      }

      @NotNull
      public static EnumEntries<TapeMouse.Button> getEnumEntries() {
         return enumEntries;
      }

      private static final TapeMouse.Button[] $values() {
         return new TapeMouse.Button[]{RIGHT, LEFT};
      }
   }
}