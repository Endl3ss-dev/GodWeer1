// deobf by pusia
package su.catlean.client.features.modules.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.category.SubCategory;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.SettingHolder;
import su.catlean.api.setting.settings.BlocksSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.InteractionManager;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.world.WorldUtil;

public class Blocks extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Blocks.class, "selectedBlocks", "getSelectedBlocks()Lsu/catlean/yt;", 0), new PropertyReference1Impl(Blocks.class, "onlyStanding", "getOnlyStanding()Z", 0) };
   @NotNull
   private final BlocksSetting id;
   @NotNull
   private final BooleanSetting onlyStanding;

   public Blocks(String id, SubCategory category) {
      super(id, category, 131, true);
      SettingHolder thisValue = this;
      Block[] blocks = new Block[]{net.minecraft.world.level.block.Blocks.OBSIDIAN};
      this.id = SettingFactory.defaultImpl(thisValue, "blocks", new BlocksValue(CollectionsKt.mutableListOf(blocks)), null, null, 12, null);
      this.onlyStanding = SettingFactory.defaultImpl(this, "only-standing", true, null, null, 12, null);
   }

   private final BlocksValue getSelectedBlocks() {
      return (BlocksValue)this.id.computeObject(this, $$delegatedProperties[0]);
   }

   public final boolean getOnlyStanding() {
      return (Boolean)this.onlyStanding.computeObject(this, $$delegatedProperties[1]);
   }

   public final void applyGetBoundingBox(Player player) {
      if (!Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer()) || !MinecraftHolder.getLocalPlayer().isVisuallySwimming() || !this.getOnlyStanding()) {
         if (player != null) {
            double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, 0.5F, true, 0.0F, 0.0F, 0.0F, 28, null);
            double minX = player.getBoundingBox().minX;
            double minY = player.getBoundingBox().minY;
            double minZ = player.getBoundingBox().minZ;
            double maxX = player.getBoundingBox().maxX;
            double maxY = player.getBoundingBox().maxY;
            double maxZ = player.getBoundingBox().maxZ;
            if (!player.onGround()) {
               player.setBoundingBox(new AABB(minX, minY, minZ, maxX, maxY - 0.5F, maxZ));
            }

            List<BlockPos> items = CollectionsKt.toList(this.computeItems(BlockPos.containing((Position)this.computeVec3((Entity)player)), new LinkedHashSet()));
            List sortedWith;
            if (Intrinsics.areEqual(player, MinecraftHolder.getLocalPlayer())) {
               Iterable<BlockPos> iterable = items;
               sortedWith = CollectionsKt.sortedWith(iterable, new Blocks.Ordering2(defaultImpl));
            } else {
               Iterable<BlockPos> iterable2 = items;
               sortedWith = CollectionsKt.sortedWith(iterable2, new Blocks.Ordering());
            }

            items = sortedWith;
            PlaceModule.defaultImpl(this, items, false, false, null, false, 30, null);
            if (!player.onGround()) {
               player.setBoundingBox(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
            }
         }
      }
   }

   @Override
   public boolean testEntity(Entity crystal) {
      return CrystalPredictor.defaultImpl(
               CrystalPredictor.INSTANCE, this.computeVec3(crystal), (Player)MinecraftHolder.getLocalPlayer(), 0, false, null, null, 60, null
            )
            + 4.0F
         < MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount();
   }

   private final List<BlockPos> computeItems(BlockPos blockPos, Set items) {
      List<BlockPos> arrayList = new ArrayList();

      for (Direction enumEntry : (Iterable<Direction>)Blocks.EntriesMappings.enumEntries) {
         BlockPos relative = blockPos.relative(enumEntry);
         BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(relative);
         if (!items.contains(relative) && (blockState.canBeReplaced() || blockState.getCollisionShape((BlockGetter)MinecraftHolder.getClientLevel(), relative).isEmpty())) {
            items.add(relative);
            if (this.checkGetEntitiesOfClass(relative)) {
               arrayList.addAll(this.computeItems(relative, items));
            } else {
               BlockHitResult item = InteractionManager.INSTANCE
                  .fetchItem(relative, -1, this.getInteract(), TargetFilter.ONLY_CRYSTALS, this.getRange(), this.getWallRange(), arrayList);
               if (item != null) {
                  arrayList.add(relative);
               } else {
                  Iterable enumEntries = (Iterable)Blocks.EntriesMappings.enumEntries;
                  var arrayList2 = new ArrayList();

                  for (Object object : enumEntries) {
                     Direction direction = (Direction)object;
                     if (direction != Direction.UP) {
                        arrayList2.add(object);
                     }
                  }

                  for (Direction direction2 : (Iterable<Direction>)arrayList2) {
                     BlockPos relative2 = relative.relative(direction2);
                     if (!items.contains(relative2) && MinecraftHolder.getClientLevel().getBlockState(relative2).canBeReplaced()) {
                        items.add(relative2);
                        BlockHitResult item2 = InteractionManager.INSTANCE
                           .fetchItem(relative2, -1, this.getInteract(), TargetFilter.ONLY_CRYSTALS, this.getRange(), this.getWallRange(), arrayList);
                        if (item2 != null) {
                           arrayList.add(relative2);
                           arrayList.add(relative);
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      return arrayList;
   }

   @NotNull
   @Override
   public List getItems3() {
      return this.getSelectedBlocks().getBlocks();
   }

   static {
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         BlockPos object22 = (BlockPos)object2;
         Comparable distanceTo = MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(object22));
         object22 = (BlockPos)object;
         Comparable comparable = distanceTo;
         return ComparisonsKt.compareValues(comparable, MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(object22)));
      }
   }

   public static final class Ordering2 implements Comparator {
      public final double[] amounts;

      public Ordering2(double[] amounts) {
         this.amounts = amounts;
      }

      @Override
      public final int compare(Object object, Object object2) {
         BlockPos object3 = (BlockPos)object;
         Comparable distanceToSqr = Vec3iUtil.computeVec3(object3)
            .distanceToSqr(
               MinecraftHolder.getLocalPlayer().getX() + this.amounts[0],
               MinecraftHolder.getLocalPlayer().getY(),
               MinecraftHolder.getLocalPlayer().getZ() + this.amounts[1]
            );
         object3 = (BlockPos)object2;
         Comparable comparable = distanceToSqr;
         return ComparisonsKt.compareValues(
            comparable,
            Vec3iUtil.computeVec3(object3)
               .distanceToSqr(
                  MinecraftHolder.getLocalPlayer().getX() + this.amounts[0],
                  MinecraftHolder.getLocalPlayer().getY(),
                  MinecraftHolder.getLocalPlayer().getZ() + this.amounts[1]
               )
         );
      }
   }
}