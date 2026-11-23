// deobf by pusia
package su.catlean.client.features.modules.attack;

import java.util.ArrayList;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.ChunkScanner;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.PlayerPredictionService;
import su.catlean.client.services.ServerInfoService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.player.PlayerNameUtil;

public final class HoleFiller extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(HoleFiller.class, "mode", "getMode()Lsu/catlean/cs;", 0), new PropertyReference1Impl(HoleFiller.class, "preferBlocks", "getPreferBlocks()Lsu/catlean/ow;", 0) };
   @NotNull
   public static final HoleFiller INSTANCE = new HoleFiller();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", HoleFiller.Mode.AROUND_PLAYERS, null, null, 12, null);
   @NotNull
   private static final EnumSetting prefer = SettingFactory.defaultImpl(INSTANCE, "prefer", HoleFiller.Prefer.OBBY, null, null, 12, null);

   private HoleFiller() {
      super("hole-filler", SubCategories.getSubCategory2(), 0, false, 12, null);
   }

   private final HoleFiller.Mode getMode() {
      return (HoleFiller.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final HoleFiller.Prefer getPreferBlocks() {
      return (HoleFiller.Prefer)prefer.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      PlaceModule.defaultImpl(this, this.getItems(), false, false, null, false, 30, null);
   }

   private final List<BlockPos> getItems() {
      List<BlockPos> items2 = this.getItems2();
      if (this.getMode() == HoleFiller.Mode.DEFAULT) {
         return items2;
      } else {
         ArrayList<BlockPos> arrayList = new ArrayList();

         for (BlockPos blockPos : (Iterable<BlockPos>)items2) {
            for (AbstractClientPlayer player : MinecraftHolder.getClientLevel().players()) {
               if (!Intrinsics.areEqual(player, MinecraftHolder.getMinecraft().player)
                  && !PlayerNameUtil.checkGetGameProfile((Player)player)
                  && !(player.distanceTo((Entity)MinecraftHolder.getMinecraft().player) > 12.0F)
                  && !(player.getY() < blockPos.above().getY())) {
                  int defaultImpl = (int)Math.ceil(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) * 1.5 / 50.0);
                  if (defaultImpl > 0) {
                     int value = 0;
                     if (value <= defaultImpl) {
                        while (true) {
                           Vec3 vec3 = PlayerPredictionService.INSTANCE.computeVec3((Player)player, value);
                           if (!(vec3.distanceTo(Vec3iUtil.computeVec3(blockPos)) > 2.0)) {
                              arrayList.add(blockPos);
                           }

                           if (value == defaultImpl) {
                              break;
                           }

                           value++;
                        }
                     }
                  } else if (!(this.computeVec3((Entity)player).distanceTo(Vec3iUtil.computeVec3(blockPos)) > 2.0)) {
                     arrayList.add(blockPos);
                  }
               }
            }
         }

         return arrayList;
      }
   }

   private final List<BlockPos> getItems2() {
      List<BlockPos> arrayList = new ArrayList();
      BlockPos containing = BlockPos.containing((Position)MinecraftHolder.getLocalPlayer().getEyePosition());
      int range = (int)((float)Math.ceil(this.getRange()));
      int value = containing.getX() - range;
      int value2 = containing.getX() + range;
      if (value <= value2) {
         while (true) {
            int value3 = containing.getY() - range;
            int value4 = containing.getY() + range;
            if (value3 <= value4) {
               while (true) {
                  int value5 = containing.getZ() - range;
                  int value6 = containing.getZ() + range;
                  if (value5 <= value6) {
                     while (true) {
                        BlockPos blockPos = new BlockPos(value, value3, value5);
                        if (ChunkScanner.INSTANCE.checkOffset(blockPos)) {
                           arrayList.add(blockPos);
                        }

                        if (value5 == value6) {
                           break;
                        }

                        value5++;
                     }
                  }

                  if (value3 == value4) {
                     break;
                  }

                  value3++;
               }
            }

            if (value == value2) {
               break;
            }

            value++;
         }
      }

      return arrayList;
   }

   @NotNull
   @Override
   public List<Block> getItems3() {
      if (this.getPreferBlocks() == HoleFiller.Prefer.WEB) {
         InventoryManager instance = InventoryManager.INSTANCE;
         Block[] blocks = new Block[]{Blocks.COBWEB};
         if (instance.computeSearchInvResult2(blocks).isFlag()) {
            return CollectionsKt.listOf(Blocks.COBWEB);
         }
      }

      return super.getItems3();
   }

   static {
   }

   public static enum Mode {
      DEFAULT,
      AROUND_PLAYERS;

      private static final HoleFiller.Mode[] modes = $values();
      private static final EnumEntries<HoleFiller.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<HoleFiller.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final HoleFiller.Mode[] $values() {
         return new HoleFiller.Mode[]{DEFAULT, AROUND_PLAYERS};
      }
   }

   public static enum Prefer {
      OBBY,
      WEB;

      private static final HoleFiller.Prefer[] prefers = $values();
      private static final EnumEntries<HoleFiller.Prefer> enumEntries = EnumEntriesKt.enumEntries(prefers);

      private Prefer() {
      }

      @NotNull
      public static EnumEntries<HoleFiller.Prefer> getEnumEntries() {
         return enumEntries;
      }

      private static final HoleFiller.Prefer[] $values() {
         return new HoleFiller.Prefer[]{OBBY, WEB};
      }
   }
}