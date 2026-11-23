// deobf by pusia
package su.catlean.client.features.modules.attack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.client.features.modules.world.SpeedMine$Engine;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.player.PlayerTargetUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class AutoMine extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoMine.class, "cevPriority", "getCevPriority()Z", 0), new PropertyReference1Impl(AutoMine.class, "antiShulker", "getAntiShulker()Z", 0), new PropertyReference1Impl(AutoMine.class, "eChestMine", "getEChestMine()Z", 0), new PropertyReference1Impl(AutoMine.class, "debug", "getDebug()Z", 0), new PropertyReference1Impl(AutoMine.class, "antiCrawl", "getAntiCrawl()Z", 0), new PropertyReference1Impl(AutoMine.class, "levelDown", "getLevelDown()Lsu/catlean/api/setting/values/Bind;", 0), new PropertyReference1Impl(AutoMine.class, "cevCrawlers", "getCevCrawlers()Z", 0), new PropertyReference1Impl(AutoMine.class, "holdPosition", "getHoldPosition()Z", 0), new PropertyReference1Impl(AutoMine.class, "holdGroup", "getHoldGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AutoMine.class, "holdCiv", "getHoldCiv()Z", 0), new PropertyReference1Impl(AutoMine.class, "holdCev", "getHoldCev()Z", 0), new PropertyReference1Impl(AutoMine.class, "holdSurr", "getHoldSurr()Z", 0) };
   @NotNull
   public static final AutoMine INSTANCE = new AutoMine();
   @NotNull
   private static final BooleanSetting cevPriority = SettingFactory.defaultImpl(INSTANCE, "cev-priority", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting antiShulker = SettingFactory.defaultImpl(INSTANCE, "anti-shulker", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting eChestsMine = SettingFactory.defaultImpl(INSTANCE, "e-chests-mine", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting debug = SettingFactory.defaultImpl(INSTANCE, "debug", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting antiCrawl = SettingFactory.defaultImpl(INSTANCE, "anti-crawl", true, null, null, 12, null);
   @NotNull
   private static final BindSetting levelDown = SettingFactory.defaultImpl(INSTANCE, "level-down", new Bind(0, false, false, 7, null), null, null, 12, null);
   @NotNull
   private static final BooleanSetting cevCrawlers = SettingFactory.defaultImpl(INSTANCE, "cev-crawlers", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting syncWithRebreak = SettingFactory.defaultImpl(INSTANCE, "sync-with-rebreak", true, null, null, 12, null);
   @NotNull
   private static final GroupSetting holdSelection = SettingFactory.computeGroupSetting(
      INSTANCE, "hold-selection", PlaceModule.Pause.BOOLS, () -> INSTANCE.getHoldPosition()
   );
   @NotNull
   private static final BooleanSetting holdCiv = SettingFactory.computeBooleanSetting(
      INSTANCE, "hold-civ", true, INSTANCE.getHoldGroup(), () -> INSTANCE.getHoldPosition()
   );
   @NotNull
   private static final BooleanSetting holdCev = SettingFactory.computeBooleanSetting(
      INSTANCE, "hold-cev", true, INSTANCE.getHoldGroup(), () -> INSTANCE.getHoldPosition()
   );
   @NotNull
   private static final BooleanSetting holdSurr = SettingFactory.computeBooleanSetting(
      INSTANCE, "hold-surr", true, INSTANCE.getHoldGroup(), () -> INSTANCE.getHoldPosition()
   );
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();
   @NotNull
   private static List items = new ArrayList();

   private AutoMine() {
      super("auto-mine", SubCategories.getSubCategory2(), null, 4, null);
   }

   private final boolean getCevPriority() {
      return (Boolean)cevPriority.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getAntiShulker() {
      return (Boolean)antiShulker.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getEChestMine() {
      return (Boolean)eChestsMine.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getDebug() {
      return (Boolean)debug.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getAntiCrawl() {
      return (Boolean)antiCrawl.computeObject(this, $$delegatedProperties[4]);
   }

   private final Bind getLevelDown() {
      return (Bind)levelDown.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getCevCrawlers() {
      return (Boolean)cevCrawlers.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getHoldPosition() {
      return (Boolean)syncWithRebreak.computeObject(this, $$delegatedProperties[7]);
   }

   private final GroupValue getHoldGroup() {
      return (GroupValue)holdSelection.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getHoldCiv() {
      return (Boolean)holdCiv.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getHoldCev() {
      return (Boolean)holdCev.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getHoldSurr() {
      return (Boolean)holdSurr.computeObject(this, $$delegatedProperties[11]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      Object var211 = null;
      AutoMine.BreakData var220 = null;
      items.clear();
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      if (!this.getAntiCrawl() || !this.isFlag()) {
         if (!this.getEChestMine() || !this.isFlag2()) {
            if (InputUtil.checkContains(this.getLevelDown().getValue())) {
               this.applyContinueDestroyBlock(containing.below(), true);
            } else {
               Player defaultImpl2 = AutoCrystal.INSTANCE.getPlayer() == null
                  ? PlayerTargetUtil.defaultImpl(
                     PlayerTargetUtil.INSTANCE,
                     (float)MinecraftHolder.getLocalPlayer().blockInteractionRange(),
                     AutoCrystal.TargetSort.FOV,
                     false,
                     false,
                     12,
                     null
                  )
                  : AutoCrystal.INSTANCE.getPlayer();
               if (defaultImpl2 != null && playerUtil.testValue(60)) {
                  if (!this.getAntiShulker() || !this.checkGetBlockPos(defaultImpl2)) {
                     BlockPos containing2 = BlockPos.containing((Position)this.computeVec3((Entity)defaultImpl2));
                     List<BlockPos> arrayList = new ArrayList();
                     List<BlockPos> arrayList2 = new ArrayList();
                     AABB boundingBox = defaultImpl2.getBoundingBox();
                     arrayList2.add(BlockPos.containing(boundingBox.minX + 0.001, boundingBox.minY, boundingBox.minZ + 0.001));
                     arrayList2.add(BlockPos.containing(boundingBox.minX + 0.001, boundingBox.minY, boundingBox.maxZ - 0.001));
                     arrayList2.add(BlockPos.containing(boundingBox.maxX - 0.001, boundingBox.minY, boundingBox.maxZ - 0.001));
                     arrayList2.add(BlockPos.containing(boundingBox.maxX - 0.001, boundingBox.minY, boundingBox.minZ + 0.001));

                     for (BlockPos blockPos : (Iterable<BlockPos>)arrayList2) {
                        if (!(
                              MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(blockPos))
                                 > MinecraftHolder.getLocalPlayer().blockInteractionRange()
                           )
                           && !Intrinsics.areEqual(blockPos, containing)) {
                           BlockState blockState = this.fetchBlockState(blockPos);
                           if (Intrinsics.areEqual(blockState.getBlock(), Blocks.OBSIDIAN) || Intrinsics.areEqual(blockState.getBlock(), Blocks.ENDER_CHEST)) {
                              arrayList.add(blockPos);
                           }
                        }
                     }

                     BlockState blockState2 = this.fetchBlockState(containing2);
                     ArrayList<AutoMine.BreakData> arrayList3 = new ArrayList();
                     int value = 0;
                     if (!Intrinsics.areEqual(blockState2.getBlock(), Blocks.BEDROCK)) {
                        if (this.checkAbove(defaultImpl2, arrayList2)) {
                           return;
                        }

                        defaultImpl(this, defaultImpl2, arrayList2, arrayList3, false, 8, null);
                        Iterable enumEntries = (Iterable)AutoMine.EntriesMappings.enumEntries;
                        var arrayList4 = new ArrayList();

                        for (Object object : enumEntries) {
                           Direction direction = (Direction)object;
                           if (direction != Direction.UP && direction != Direction.DOWN) {
                              arrayList4.add(object);
                           }
                        }

                        for (Direction direction2 : (Iterable<Direction>)arrayList4) {
                           float value2 = 0.0F;
                           BlockPos relative = containing2.relative(direction2);
                           BlockState blockState3 = this.fetchBlockState(relative);
                           if (Intrinsics.areEqual(blockState3.getBlock(), Blocks.AIR)) {
                              value++;
                           }

                           if ((Intrinsics.areEqual(blockState3.getBlock(), Blocks.OBSIDIAN) || Intrinsics.areEqual(blockState3.getBlock(), Blocks.ENDER_CHEST))
                              && !(
                                 Vec3iUtil.computeVec3(relative).distanceTo(MinecraftHolder.getLocalPlayer().getEyePosition())
                                    > MinecraftHolder.getLocalPlayer().blockInteractionRange()
                              )
                              && !Intrinsics.areEqual(relative, containing)) {
                              BlockState blockState4 = this.fetchBlockState(relative.below());
                              if (Intrinsics.areEqual(blockState4.getBlock(), Blocks.OBSIDIAN) || Intrinsics.areEqual(blockState4.getBlock(), Blocks.BEDROCK)) {
                                 value2 += 6.0F;
                              }

                              for (int j = 1; j < 3; j++) {
                                 BlockState blockState5 = this.fetchBlockState(relative.below().relative(direction2, j));
                                 BlockState blockState6 = this.fetchBlockState(relative.relative(direction2, j));
                                 if ((Intrinsics.areEqual(blockState5.getBlock(), Blocks.OBSIDIAN) || Intrinsics.areEqual(blockState5.getBlock(), Blocks.BEDROCK))
                                    && blockState6.isAir()) {
                                    value2 += 7.5F;
                                 }
                              }

                              Iterable enumEntries2 = (Iterable)AutoMine.EntriesMappings.enumEntries;
                              boolean flag4 = false;
                              var arrayList5 = new ArrayList();
                              boolean flag5 = false;

                              for (Object object2 : enumEntries2) {
                                 Direction direction3 = (Direction)object2;
                                 if (direction3 != Direction.DOWN && direction3 != Direction.UP && direction3 != direction2) {
                                    arrayList5.add(object2);
                                 }
                              }

                              for (Direction direction4 : (Iterable<Direction>)arrayList5) {
                                 BlockPos relative2 = relative.relative(direction2).relative(direction4);
                                 BlockState blockState7 = this.fetchBlockState(relative2);
                                 BlockState blockState8 = this.fetchBlockState(relative2.below());
                                 BlockState blockState9 = this.fetchBlockState(relative.relative(direction4));
                                 if (!Intrinsics.areEqual(blockState9.getBlock(), Blocks.OBSIDIAN)
                                    && !Intrinsics.areEqual(blockState9.getBlock(), Blocks.BEDROCK)
                                    && (Intrinsics.areEqual(blockState8.getBlock(), Blocks.OBSIDIAN) || Intrinsics.areEqual(blockState8.getBlock(), Blocks.BEDROCK))
                                    && blockState7.isAir()) {
                                    value2 += 9.0F;
                                 }
                              }

                              enumEntries2 = (Iterable)AutoMine.EntriesMappings.enumEntries;
                              flag4 = false;
                              arrayList5 = new ArrayList();
                              flag5 = false;

                              for (Object object3 : enumEntries2) {
                                 Direction direction5 = (Direction)object3;
                                 if (direction5 != Direction.DOWN) {
                                    arrayList5.add(object3);
                                 }
                              }

                              for (Direction direction6 : (Iterable<Direction>)arrayList5) {
                                 BlockPos relative3 = relative.relative(direction6);
                                 if (!Intrinsics.areEqual(relative3, containing2)
                                    && !Intrinsics.areEqual(relative3, containing)
                                    && !(
                                       Vec3iUtil.computeVec3(relative3).distanceTo(MinecraftHolder.getLocalPlayer().getEyePosition())
                                          > MinecraftHolder.getLocalPlayer().blockInteractionRange()
                                    )) {
                                    BlockState blockState10 = this.fetchBlockState(relative3);
                                    if (blockState10.isAir()) {
                                       BlockState blockState11 = this.fetchBlockState(relative3.below());
                                       BlockState blockState12 = this.fetchBlockState(relative3.below(2));
                                       if (Intrinsics.areEqual(blockState11.getBlock(), Blocks.OBSIDIAN) || Intrinsics.areEqual(blockState11.getBlock(), Blocks.BEDROCK)) {
                                          value2 += !Intrinsics.areEqual(blockState12.getBlock(), Blocks.OBSIDIAN)
                                                && !Intrinsics.areEqual(blockState12.getBlock(), Blocks.BEDROCK)
                                             ? 9.0F
                                             : 10.0F;
                                       }
                                    } else if (Intrinsics.areEqual(blockState10.getBlock(), Blocks.OBSIDIAN)
                                       || Intrinsics.areEqual(blockState10.getBlock(), Blocks.ENDER_CHEST)) {
                                       boolean flag8 = true;
                                       Iterable enumEntries3 = (Iterable)AutoMine.EntriesMappings.enumEntries;
                                       var arrayList6 = new ArrayList();

                                       for (Object object4 : enumEntries3) {
                                          Direction direction7 = (Direction)object4;
                                          if (direction7 != Direction.DOWN) {
                                             arrayList6.add(object4);
                                          }
                                       }

                                       for (Direction direction8 : (Iterable<Direction>)arrayList6) {
                                          BlockPos relative4 = relative3.relative(direction8);
                                          if (!Intrinsics.areEqual(relative4, containing2)
                                             && !Intrinsics.areEqual(relative4, relative3)
                                             && !Intrinsics.areEqual(relative4, relative)
                                             && !Intrinsics.areEqual(relative4, containing)
                                             && !(
                                                Vec3iUtil.computeVec3(relative4).distanceTo(MinecraftHolder.getLocalPlayer().getEyePosition())
                                                   > MinecraftHolder.getLocalPlayer().blockInteractionRange()
                                             )
                                             && this.fetchBlockState(relative4).isAir()) {
                                             BlockState blockState13 = this.fetchBlockState(relative4.below());
                                             if (Intrinsics.areEqual(blockState13.getBlock(), Blocks.OBSIDIAN)
                                                || Intrinsics.areEqual(blockState13.getBlock(), Blocks.BEDROCK)) {
                                                value2++;
                                                if (flag8) {
                                                   Direction[] directions = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};
                                                   List<Direction> mutableListOf = CollectionsKt.mutableListOf(directions);
                                                   mutableListOf.remove(direction6.getOpposite());
                                                   ArrayList<AutoMine.BreakData> arrayList7 = arrayList3;
                                                   BlockPos blockPos2 = relative3;
                                                   float value3;
                                                   if (direction6 == Direction.UP) {
                                                      value3 = 11.0F;
                                                   } else {
                                                      Iterable<Direction> iterable = mutableListOf;
                                                      Iterator<Direction> iterator = iterable.iterator();

                                                      while (true) {
                                                         if (!iterator.hasNext()) {
                                                            var211 = null;
                                                            break;
                                                         }

                                                         Direction object5 = iterator.next();
                                                         Direction direction9 = (Direction)object5;
                                                         BlockPos relative5 = relative3.below().relative(direction9);
                                                         BlockState blockState14 = INSTANCE.fetchBlockState(relative5);
                                                         BlockPos relative6 = relative3.relative(direction9);
                                                         BlockState blockState15 = INSTANCE.fetchBlockState(relative6);
                                                         if ((
                                                               Intrinsics.areEqual(blockState14.getBlock(), Blocks.OBSIDIAN)
                                                                  || Intrinsics.areEqual(blockState14.getBlock(), Blocks.BEDROCK)
                                                            )
                                                            && blockState15.isAir()) {
                                                            var211 = object5;
                                                            break;
                                                         }
                                                      }

                                                      Object object6 = var211;
                                                      arrayList7 = arrayList3;
                                                      blockPos2 = relative3;
                                                      value3 = object6 != null ? 16.0F : 11.0F;
                                                   }

                                                   float value4 = value3;
                                                   BlockPos blockPos3 = blockPos2;
                                                   arrayList7.add(new AutoMine.BreakData(blockPos3, value4));
                                                   flag8 = false;
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }

                              arrayList3.add(new AutoMine.BreakData(relative, value2));
                           }
                        }
                     }

                     if (value > 1) {
                        arrayList3.clear();
                     }

                     for (BlockPos blockPos4 : (Iterable<BlockPos>)arrayList) {
                        arrayList3.add(new AutoMine.BreakData(blockPos4, 300.0F));
                     }

                     items = arrayList3;
                     if (SpeedMine.INSTANCE.getEnabled() && SpeedMine.INSTANCE.getDoubleMine()) {
                        var value5 = arrayList3;
                        boolean flag14;
                        if (value5 instanceof Collection && value5.isEmpty()) {
                           flag14 = false;
                        } else {
                           label596: {
                              for (Object object7 : value5) {
                                 AutoMine.BreakData breakData = (AutoMine.BreakData)object7;
                                 if (breakData.getAmount() == 100.0F) {
                                    flag14 = true;
                                    break label596;
                                 }
                              }

                              flag14 = false;
                           }
                        }

                        boolean flag16 = flag14;
                        AutoMine.BreakData breakData2;
                        if (flag16) {
                           Iterable<AutoMine.BreakData> iterable2 = arrayList3;
                           boolean flag17 = false;
                           var arrayList8 = new ArrayList();

                           for (Object object8 : iterable2) {
                              AutoMine.BreakData breakData3 = (AutoMine.BreakData)object8;
                              if (breakData3.getAmount() != 100.0F) {
                                 arrayList8.add(object8);
                              }
                           }

                           var value6 = arrayList8;
                           flag17 = false;
                           Iterator iterator2 = value6.iterator();
                           Object object9;
                           if (!iterator2.hasNext()) {
                              object9 = null;
                           } else {
                              Object object10 = iterator2.next();
                              if (!iterator2.hasNext()) {
                                 object9 = object10;
                              } else {
                                 AutoMine.BreakData breakData4 = (AutoMine.BreakData)object10;
                                 float amount = breakData4.getAmount();

                                 do {
                                    Object object11 = iterator2.next();
                                    AutoMine.BreakData breakData5 = (AutoMine.BreakData)object11;
                                    float amount2 = breakData5.getAmount();
                                    if (Float.compare(amount, amount2) < 0) {
                                       object10 = object11;
                                       amount = amount2;
                                    }
                                 } while (iterator2.hasNext());

                                 object9 = object10;
                              }
                           }

                           breakData2 = (AutoMine.BreakData)object9;
                        } else {
                           Iterable<AutoMine.BreakData> iterable3 = arrayList3;
                           Iterator<AutoMine.BreakData> iterator3 = iterable3.iterator();
                           AutoMine.BreakData object12;
                           if (!iterator3.hasNext()) {
                              object12 = null;
                           } else {
                              AutoMine.BreakData object13 = iterator3.next();
                              if (!iterator3.hasNext()) {
                                 object12 = object13;
                              } else {
                                 AutoMine.BreakData breakData6 = (AutoMine.BreakData)object13;
                                 float amount3 = breakData6.getAmount();

                                 do {
                                    AutoMine.BreakData object14 = iterator3.next();
                                    AutoMine.BreakData breakData7 = (AutoMine.BreakData)object14;
                                    float amount4 = breakData7.getAmount();
                                    if (Float.compare(amount3, amount4) < 0) {
                                       object13 = object14;
                                       amount3 = amount4;
                                    }
                                 } while (iterator3.hasNext());

                                 object12 = object13;
                              }
                           }

                           breakData2 = (AutoMine.BreakData)object12;
                        }

                        AutoMine.BreakData breakData8 = breakData2;
                        Iterable<AutoMine.BreakData> iterable4 = arrayList3;
                        boolean flag25 = false;
                        var arrayList9 = new ArrayList();

                        for (Object object15 : iterable4) {
                           AutoMine.BreakData breakData9 = (AutoMine.BreakData)object15;
                           if (breakData8 != null && !Intrinsics.areEqual(breakData9, breakData8)) {
                              arrayList9.add(object15);
                           }
                        }

                        iterable4 = arrayList9;
                        flag25 = false;
                        Iterator<AutoMine.BreakData> iterator4 = iterable4.iterator();
                        AutoMine.BreakData object16;
                        if (!iterator4.hasNext()) {
                           object16 = null;
                        } else {
                           AutoMine.BreakData object17 = iterator4.next();
                           if (!iterator4.hasNext()) {
                              object16 = object17;
                           } else {
                              AutoMine.BreakData breakData10 = (AutoMine.BreakData)object17;
                              float amount5 = breakData10.getAmount();

                              do {
                                 AutoMine.BreakData object18 = iterator4.next();
                                 AutoMine.BreakData breakData11 = (AutoMine.BreakData)object18;
                                 float amount6 = breakData11.getAmount();
                                 if (Float.compare(amount5, amount6) < 0) {
                                    object17 = object18;
                                    amount5 = amount6;
                                 }
                              } while (iterator4.hasNext());

                              object16 = object17;
                           }
                        }

                        AutoMine.BreakData breakData12 = (AutoMine.BreakData)object16;
                        if (breakData8 == null) {
                           return;
                        }

                        label272: {
                           if (SpeedMine.INSTANCE.checkIsEmpty(breakData8.getBlockPos())) {
                              var220 = breakData12;
                              if (breakData12 != null) {
                                 break label272;
                              }
                           }

                           var220 = breakData8;
                        }

                        AutoMine.BreakData breakData13 = var220;
                        this.applyContinueDestroyBlock(breakData13.getBlockPos(), false);
                     } else {
                        Iterable<AutoMine.BreakData> iterable5 = arrayList3;
                        Iterator<AutoMine.BreakData> iterator5 = iterable5.iterator();
                        AutoMine.BreakData object19;
                        if (!iterator5.hasNext()) {
                           object19 = null;
                        } else {
                           AutoMine.BreakData object20 = iterator5.next();
                           if (!iterator5.hasNext()) {
                              object19 = object20;
                           } else {
                              AutoMine.BreakData breakData14 = (AutoMine.BreakData)object20;
                              float amount7 = breakData14.getAmount();

                              do {
                                 AutoMine.BreakData object21 = iterator5.next();
                                 AutoMine.BreakData breakData15 = (AutoMine.BreakData)object21;
                                 float amount8 = breakData15.getAmount();
                                 if (Float.compare(amount7, amount8) < 0) {
                                    object20 = object21;
                                    amount7 = amount8;
                                 }
                              } while (iterator5.hasNext());

                              object19 = object20;
                           }
                        }

                        object19 = (AutoMine.BreakData)object19;
                        if ((AutoMine.BreakData)object19 != null) {
                           AutoMine.BreakData breakData16 = (AutoMine.BreakData)object19;
                           INSTANCE.applyContinueDestroyBlock(breakData16.getBlockPos(), false);
                        } else {
                           object19 = null;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private final void applyAbove(Player player, List items, List items2, boolean flag) {
      if (!player.isVisuallyCrawling() || this.getCevCrawlers()) {
         Iterable items3 = items;

         for (BlockPos sortedWithItem : (Iterable<BlockPos>)CollectionsKt.sortedWith(items3, new AutoMine.Ordering(player))) {
            BlockPos above = sortedWithItem.above(2);

            for (int j = 0; j < 4; j++) {
               BlockPos above2 = above.above(j);
               if (MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(above2))
                     <= MinecraftHolder.getLocalPlayer().blockInteractionRange()
                  && (flag || Intrinsics.areEqual(this.fetchBlockState(above2).getBlock(), Blocks.OBSIDIAN))) {
                  boolean isAir = this.fetchBlockState(above2.above()).isAir();
                  boolean isAir2 = this.fetchBlockState(above2.below()).isAir();
                  if (!isAir && j == 3) {
                     break;
                  }

                  boolean flag3 = SpeedMine.INSTANCE.getEnabled() && SpeedMine.INSTANCE.getDoubleMine();
                  float value = flag3 ? 100.0F : 99.0F;
                  float value2 = flag3 ? 99.0F : 100.0F;
                  float value3 = this.getCevPriority() ? (isAir2 ? value : value2) : 0.5F;
                  items2.add(new AutoMine.BreakData(above2, value3));
               }
            }
         }
      }
   }

   public static void defaultImpl(AutoMine autoMine, Player player, List items, List items2, boolean flag, int value, Object object) {
      if ((value & 8) != 0) {
         flag = false;
      }

      autoMine.applyAbove(player, items, items2, flag);
   }

   private final BlockState fetchBlockState(BlockPos blockPos) {
      return MinecraftHolder.getClientLevel().getBlockState(blockPos);
   }

   private final boolean checkAbove(Player player, List items) {
      if (this.getHoldPosition() && SpeedMine.INSTANCE.getEnabled() && SpeedMine.INSTANCE.getRebreak() != SpeedMine.Rebreak.OFF) {
         Iterable<SpeedMine$Engine> linkedList = SpeedMine.INSTANCE.getLinkedList();
         boolean flag = false;
         Iterator<SpeedMine$Engine> iterator = linkedList.iterator();

         Object object;
         while (true) {
            if (!iterator.hasNext()) {
               object = null;
               break;
            }

            SpeedMine$Engine object2 = iterator.next();
            SpeedMine$Engine engine = (SpeedMine$Engine)object2;
            if (MinecraftHolder.getClientLevel().isEmptyBlock(engine.getPos()) || engine.getValue() > 0) {
               object = (SpeedMine$Engine)object2;
               break;
            }
         }

         object = object;
         if (object != null) {
            BlockPos pos = ((SpeedMine$Engine)object).getPos();
            if (pos != null) {
               BlockPos blockPos = pos;
               if (this.getHoldCev()) {
                  List arrayList = new ArrayList();
                  this.applyAbove(player, items, arrayList, true);
                  linkedList = arrayList;
                  flag = false;
                  boolean flag3;
                  if (linkedList instanceof Collection && ((Collection)linkedList).isEmpty()) {
                     flag3 = false;
                  } else {
                     iterator = linkedList.iterator();

                     while (true) {
                        if (!iterator.hasNext()) {
                           flag3 = false;
                           break;
                        }

                        SpeedMine$Engine object3 = iterator.next();
                        AutoMine.BreakData breakData = (AutoMine.BreakData)(Object)object3;
                        if (INSTANCE.fetchBlockState(breakData.getBlockPos().below()).isAir() && Intrinsics.areEqual(breakData.getBlockPos(), blockPos)) {
                           flag3 = true;
                           break;
                        }
                     }
                  }

                  if (flag3) {
                     return true;
                  }
               }

               for (BlockPos item : (Iterable<BlockPos>)items) {
                  Iterable enumEntries = (Iterable)AutoMine.EntriesMappings.enumEntries;
                  var arrayList2 = new ArrayList();

                  for (Object object4 : enumEntries) {
                     Direction direction = (Direction)object4;
                     if (direction != Direction.UP && direction != Direction.DOWN) {
                        arrayList2.add(object4);
                     }
                  }

                  for (Direction direction2 : (Iterable<Direction>)arrayList2) {
                     BlockPos relative = item.relative(direction2);
                     if (!items.contains(relative)
                        && (this.getHoldSurr() && Intrinsics.areEqual(blockPos, relative) || this.getHoldCiv() && Intrinsics.areEqual(blockPos, relative.above()))) {
                        return true;
                     }
                  }
               }

               return false;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.getDebug()) {
         for (AutoMine.BreakData item : (Iterable<su.catlean.client.features.modules.attack.AutoMine.BreakData>)items) {
            int time = (int)(MinecraftHolder.getTime() / 2.0 % 360);
            time = time >= 180 ? 360 - time : time;
            Color alpha = Render2D.INSTANCE
               .fetchAlpha(
                  Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 0.5F),
                  Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 0.0F),
                  time / 360.0F
               );
            Render3D.defaultImpl3(Render3D.INSTANCE, new AABB(item.getBlockPos()), alpha, null, 4, null);
            Render3D.defaultImpl(
               Render3D.INSTANCE, String.valueOf(item.getAmount()), Vec3iUtil.computeVec3(item.getBlockPos()), alpha, 0.0F, 0.0F, false, null, 120, null
            );
         }
      }
   }

   private final boolean isFlag() {
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      if (MinecraftHolder.getLocalPlayer().getPose() == Pose.SWIMMING
         && !MinecraftHolder.getLocalPlayer().isInWater()
         && !MinecraftHolder.getLocalPlayer().isUnderWater()) {
         BlockPos above = containing.above();
         BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(above);
         BlockPos below = containing.below();
         BlockState blockState2 = MinecraftHolder.getClientLevel().getBlockState(below);
         if (!Intrinsics.areEqual(blockState.getBlock(), Blocks.BEDROCK)
            && blockState.getDestroySpeed((BlockGetter)MinecraftHolder.getClientLevel(), above) > 0.0F
            && !Intrinsics.areEqual(blockState.getBlock(), Blocks.AIR)
            && !Intrinsics.areEqual(blockState.getBlock(), Blocks.CAVE_AIR)
            && !blockState.canBeReplaced()) {
            this.applyContinueDestroyBlock(above, true);
            return true;
         }

         if (!Intrinsics.areEqual(blockState2.getBlock(), Blocks.BEDROCK)
            && blockState2.getDestroySpeed((BlockGetter)MinecraftHolder.getClientLevel(), below) > 0.0F
            && !Intrinsics.areEqual(blockState2.getBlock(), Blocks.AIR)
            && !Intrinsics.areEqual(blockState2.getBlock(), Blocks.CAVE_AIR)
            && !blockState2.canBeReplaced()) {
            this.applyContinueDestroyBlock(below, true);
            return true;
         }
      }

      return false;
   }

   private final boolean isFlag2() {
      for (BlockEntity item : (Iterable<BlockEntity>)ServerInfoService.INSTANCE.getItems()) {
         Vec3 eyePosition = MinecraftHolder.getLocalPlayer().getEyePosition();
         BlockPos blockPos = item.getBlockPos();
         if (!(eyePosition.distanceTo(Vec3iUtil.computeVec3(blockPos)) > 2.2) && item instanceof EnderChestBlockEntity) {
            this.applyContinueDestroyBlock(((EnderChestBlockEntity)item).getBlockPos(), false);
            break;
         }
      }

      return false;
   }

   private final boolean checkGetBlockPos(Player player) {
      for (BlockEntity item : (Iterable<BlockEntity>)ServerInfoService.INSTANCE.getItems()) {
         Vec3 eyePosition = player.getEyePosition();
         BlockPos blockPos = item.getBlockPos();
         if (!(eyePosition.distanceTo(Vec3iUtil.computeVec3(blockPos)) > 2.2) && item instanceof ShulkerBoxBlockEntity) {
            this.applyContinueDestroyBlock(((ShulkerBoxBlockEntity)item).getBlockPos(), true);
            break;
         }
      }

      return false;
   }

   private final void applyContinueDestroyBlock(BlockPos blockPos, boolean flag) {
      if (blockPos != null) {
         if (!SpeedMine.INSTANCE.isFlag2() || flag) {
            if (SpeedMine.INSTANCE.getEnabled()) {
               if (!SpeedMine.INSTANCE.getDoubleMine() || SpeedMine.INSTANCE.getLinkedList().size() == 1) {
                  this.getPlayerUtil();
               }

               SpeedMine.INSTANCE.applyGetAbilities(blockPos, Direction.UP);
            } else {
               MinecraftHolder.getMultiPlayerGameMode().continueDestroyBlock(blockPos, Direction.UP);
               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            }
         }
      }
   }

   public final void getPlayerUtil() {
      playerUtil.update();
   }

   static {
   }

   public static final class BreakData {
      @NotNull
      private final BlockPos blockPos;
      private final float amount;

      public BreakData(BlockPos blockPos, float amount) {
         this.blockPos = blockPos;
         this.amount = amount;
      }

      @NotNull
      public final BlockPos getBlockPos() {
         return this.blockPos;
      }

      public final float getAmount() {
         return this.amount;
      }

      @NotNull
      public final BlockPos getBlockPos2() {
         return this.blockPos;
      }

      public final float getAmount2() {
         return this.amount;
      }

      @NotNull
      @Override
      public String toString() {
         return "BreakData(blockPos=" + this.blockPos + ", damage=" + this.amount + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.blockPos.hashCode();
         return hashCode * 31 + Float.hashCode(this.amount);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof AutoMine.BreakData breakData)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.blockPos, breakData.blockPos) ? false : Float.compare(this.amount, breakData.amount) == 0;
         }
      }
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static final class Ordering implements Comparator {
      public final Player player;

      public Ordering(Player player) {
         this.player = player;
      }

      @Override
      public final int compare(Object object, Object object2) {
         BlockPos object3 = (BlockPos)object;
         Comparable distanceTo = this.player.getEyePosition().distanceTo(Vec3iUtil.computeVec3(object3));
         object3 = (BlockPos)object2;
         Comparable comparable = distanceTo;
         return ComparisonsKt.compareValues(comparable, this.player.getEyePosition().distanceTo(Vec3iUtil.computeVec3(object3)));
      }
   }
}