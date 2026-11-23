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
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalBreakChecker;
import su.catlean.client.services.PlayerPredictionService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.player.PlayerTargetUtil;

public final class AutoWeb extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoWeb.class, "targetMode", "getTargetMode()Lsu/catlean/_b;", 0), new MutablePropertyReference1Impl(AutoWeb.class, "face", "getFace()Lsu/catlean/dv;", 0) };
   @NotNull
   public static final AutoWeb INSTANCE = new AutoWeb();
   @NotNull
   private static final EnumSetting targetMode = SettingFactory.defaultImpl(INSTANCE, "target-mode", AutoWeb.TargetMode.AUTO, null, null, 12, null);
   @NotNull
   private static final EnumSetting face = SettingFactory.defaultImpl(INSTANCE, "face", AutoWeb.Face.ON, null, null, 12, null);

   private AutoWeb() {
      super("auto-web", SubCategories.getSubCategory2(), 132, false, 8, null);
   }

   private final AutoWeb.TargetMode getTargetMode() {
      return (AutoWeb.TargetMode)targetMode.computeObject(this, $$delegatedProperties[0]);
   }

   private final AutoWeb.Face getFace() {
      return (AutoWeb.Face)face.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      Player defaultImpl = PlayerTargetUtil.defaultImpl(PlayerTargetUtil.INSTANCE, this.getRange(), AutoCrystal.TargetSort.FOV, false, false, 12, null);
      if (this.getTargetMode() == AutoWeb.TargetMode.AUTO) {
         if (Aura.INSTANCE.getEnabled() && Aura.INSTANCE.getEntity() != null && Aura.INSTANCE.getEntity() instanceof Player) {
            defaultImpl = (Player)Aura.INSTANCE.getEntity();
         }

         if (AutoCrystal.INSTANCE.getEnabled() && AutoCrystal.INSTANCE.getPlayer() != null) {
            defaultImpl = AutoCrystal.INSTANCE.getPlayer();
         }

         if (defaultImpl == null || CrystalBreakChecker.INSTANCE.checkContaining(defaultImpl, this.getFace() != AutoWeb.Face.OFF)) {
            return;
         }

         this.applyAbove(defaultImpl);
      } else {
         for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
            if (!Intrinsics.areEqual(player, MinecraftHolder.getMinecraft().player)
               && !PlayerNameUtil.checkGetGameProfile((Player)player)
               && !(player.distanceTo((Entity)MinecraftHolder.getMinecraft().player) > this.getRange())
               && !CrystalBreakChecker.INSTANCE.checkContaining((Player)player, this.getFace() != AutoWeb.Face.OFF)) {
               this.applyAbove((Player)player);
            }
         }
      }
   }

   private final void applyAbove(Player player) {
      ArrayList<BlockPos> arrayList = new ArrayList();
      BlockPos containing = BlockPos.containing((Position)PlayerPredictionService.INSTANCE.computeVec3(player, CrystalBreakChecker.INSTANCE.computeValue(player)));
      if (this.getFace() != AutoWeb.Face.ONLY
         && !(MinecraftHolder.getClientLevel().getBlockState(containing).getBlock() instanceof LiquidBlock)
         && !Intrinsics.areEqual(containing, BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getMinecraft().player)))) {
         arrayList.add(containing);
      }

      if (this.getFace() != AutoWeb.Face.OFF
         && !(MinecraftHolder.getClientLevel().getBlockState(containing.above()).getBlock() instanceof LiquidBlock)
         && !Intrinsics.areEqual(containing.above(), BlockPos.containing((Position)MinecraftHolder.getMinecraft().player.getEyePosition()))) {
         arrayList.add(containing.above());
      }

      PlaceModule.defaultImpl(this, arrayList, false, false, null, false, 30, null);
   }

   @NotNull
   @Override
   public List<Block> getItems3() {
      return CollectionsKt.listOf(Blocks.COBWEB);
   }

   @NotNull
   @Override
   public TargetFilter getTargetFilter() {
      return TargetFilter.NONE;
   }

   static {
   }

   public static enum Face {
      OFF,
      ON,
      ONLY;

      private static final AutoWeb.Face[] faces = $values();
      private static final EnumEntries<AutoWeb.Face> enumEntries = EnumEntriesKt.enumEntries(faces);

      private Face() {
      }

      @NotNull
      public static EnumEntries<AutoWeb.Face> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoWeb.Face[] $values() {
         return new AutoWeb.Face[]{OFF, ON, ONLY};
      }
   }

   public static enum TargetMode {
      AUTO,
      ALL;

      private static final AutoWeb.TargetMode[] targetModes = $values();
      private static final EnumEntries<AutoWeb.TargetMode> enumEntries = EnumEntriesKt.enumEntries(targetModes);

      private TargetMode() {
      }

      @NotNull
      public static EnumEntries<AutoWeb.TargetMode> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoWeb.TargetMode[] $values() {
         return new AutoWeb.TargetMode[]{AUTO, ALL};
      }
   }
}