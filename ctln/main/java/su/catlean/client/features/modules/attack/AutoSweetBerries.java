// deobf by pusia
package su.catlean.client.features.modules.attack;

import net.minecraft.world.level.block.Block;
import java.util.ArrayList;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrassBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalBreakChecker;
import su.catlean.client.services.PlayerPredictionService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.player.PlayerTargetUtil;

public final class AutoSweetBerries extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoSweetBerries.class, "targetMode", "getTargetMode()Lsu/catlean/lb;", 0), new MutablePropertyReference1Impl(AutoSweetBerries.class, "directions", "getDirections()Z", 0) };
   @NotNull
   public static final AutoSweetBerries INSTANCE = new AutoSweetBerries();
   @NotNull
   private static final EnumSetting targetMode = SettingFactory.defaultImpl(INSTANCE, "target-mode", AutoSweetBerries.TargetMode.AUTO, null, null, 12, null);
   @NotNull
   private static final BooleanSetting directions = SettingFactory.defaultImpl(INSTANCE, "directions", false, null, null, 12, null);

   private AutoSweetBerries() {
      super("auto-sweet-berries", SubCategories.getSubCategory2(), 0, false, 12, null);
   }

   private final AutoSweetBerries.TargetMode getTargetMode() {
      return (AutoSweetBerries.TargetMode)targetMode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getDirections() {
      return (Boolean)directions.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      Player defaultImpl = PlayerTargetUtil.defaultImpl(PlayerTargetUtil.INSTANCE, this.getRange(), AutoCrystal.TargetSort.FOV, false, false, 12, null);
      if (this.getTargetMode() == AutoSweetBerries.TargetMode.AUTO) {
         if (Aura.INSTANCE.getEnabled() && Aura.INSTANCE.getEntity() != null && Aura.INSTANCE.getEntity() instanceof Player) {
            defaultImpl = (Player)Aura.INSTANCE.getEntity();
         }

         if (AutoCrystal.INSTANCE.getEnabled() && AutoCrystal.INSTANCE.getPlayer() != null) {
            defaultImpl = AutoCrystal.INSTANCE.getPlayer();
         }

         if (defaultImpl == null || CrystalBreakChecker.INSTANCE.checkContaining(defaultImpl)) {
            return;
         }

         this.applyGetBlock(defaultImpl);
      } else {
         for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
            if (!Intrinsics.areEqual(player, MinecraftHolder.getMinecraft().player)
               && !PlayerNameUtil.checkGetGameProfile((Player)player)
               && !(player.distanceTo((Entity)MinecraftHolder.getMinecraft().player) > this.getRange())
               && !CrystalBreakChecker.INSTANCE.checkContaining((Player)player)) {
               this.applyGetBlock((Player)player);
            }
         }
      }
   }

   private final void applyGetBlock(Player player) {
      ArrayList<BlockPos> arrayList = new ArrayList();
      BlockPos containing = BlockPos.containing((Position)PlayerPredictionService.INSTANCE.computeVec3(player, CrystalBreakChecker.INSTANCE.computeValue(player)));
      BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(containing);
      BlockState blockState2 = MinecraftHolder.getClientLevel().getBlockState(containing.below());
      if ((blockState2.getBlock() instanceof GrassBlock || Intrinsics.areEqual(blockState2.getBlock(), Blocks.DIRT))
         && !(blockState.getBlock() instanceof LiquidBlock)
         && !Intrinsics.areEqual(containing, BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getMinecraft().player)))) {
         arrayList.add(containing);
      }

      if (this.getDirections()) {
         Iterable enumEntries = (Iterable)AutoSweetBerries.EntriesMappings.enumEntries;
         var arrayList2 = new ArrayList();

         for (Object object : enumEntries) {
            Direction direction = (Direction)object;
            if (direction != Direction.UP && direction != Direction.DOWN) {
               arrayList2.add(object);
            }
         }

         for (Direction direction2 : (Iterable<Direction>)arrayList2) {
            BlockPos relative = containing.relative(direction2);
            BlockState blockState3 = MinecraftHolder.getClientLevel().getBlockState(relative);
            BlockState blockState4 = MinecraftHolder.getClientLevel().getBlockState(relative.below());
            if ((blockState4.getBlock() instanceof GrassBlock || Intrinsics.areEqual(blockState4.getBlock(), Blocks.DIRT))
               && !(blockState3.getBlock() instanceof LiquidBlock)
               && !Intrinsics.areEqual(relative, BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getMinecraft().player)))) {
               arrayList.add(relative);
            }
         }
      }

      PlaceModule.defaultImpl(this, arrayList, false, false, null, false, 30, null);
   }

   @NotNull
   @Override
   public List<Block> getItems3() {
      return CollectionsKt.listOf(Blocks.SWEET_BERRY_BUSH);
   }

   @NotNull
   @Override
   public TargetFilter getTargetFilter() {
      return TargetFilter.NONE;
   }

   static {
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static enum TargetMode {
      AUTO,
      ALL;

      private static final AutoSweetBerries.TargetMode[] targetModes = $values();
      private static final EnumEntries<AutoSweetBerries.TargetMode> enumEntries = EnumEntriesKt.enumEntries(targetModes);

      private TargetMode() {
      }

      @NotNull
      public static EnumEntries<AutoSweetBerries.TargetMode> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoSweetBerries.TargetMode[] $values() {
         return new AutoSweetBerries.TargetMode[]{AUTO, ALL};
      }
   }
}