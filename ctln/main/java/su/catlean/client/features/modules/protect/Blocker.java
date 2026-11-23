// deobf by pusia
package su.catlean.client.features.modules.protect;

import java.util.ArrayList;
import java.util.List;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.AfterReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class Blocker extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Blocker.class, "triggerProgress", "getTriggerProgress()F", 0), new MutablePropertyReference1Impl(Blocker.class, "surround", "getSurround()Z", 0), new MutablePropertyReference1Impl(Blocker.class, "head", "getHead()Z", 0) };
   @NotNull
   public static final Blocker INSTANCE = new Blocker();
   @NotNull
   private static final FloatSetting triggerProgress = SettingFactory.defaultImpl(
      INSTANCE, "trigger-progress", 0.8F, RangesKt.rangeTo(0.0F, 0.9F), null, null, 24, null
   );
   @NotNull
   private static final BooleanSetting surround = SettingFactory.defaultImpl(INSTANCE, "surround", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting head = SettingFactory.defaultImpl(INSTANCE, "head", true, null, null, 12, null);
   @NotNull
   private static List<Blocker.BreakingPos> items = new ArrayList();

   private Blocker() {
      super("blocker", SubCategories.getSubCategory(), 200, false);
   }

   private final float getTriggerProgress() {
      return ((Number)triggerProgress.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   private final boolean getSurround() {
      return (Boolean)surround.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getHead() {
      return (Boolean)head.computeObject(this, $$delegatedProperties[2]);
   }

   @Flow
   private final void onAfterReceivePacket(AfterReceivePacket afterReceivePacket) {
      if (afterReceivePacket.getPacket() instanceof ClientboundBlockDestructionPacket) {
         ClientboundBlockDestructionPacket packet = (ClientboundBlockDestructionPacket)afterReceivePacket.getPacket();
         List<BlockPos> items2 = this.getItems();
         BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
         if (packet.getProgress() == 0) {
            if (this.getSurround() && items2.contains(packet.getPos())) {
               items.add(new Blocker.BreakingPos(packet.getPos(), 0, false, 6, null));
            }

            if (this.getHead() && (Intrinsics.areEqual(packet.getPos(), containing.above(2)) || Intrinsics.areEqual(packet.getPos(), containing.above(3)))) {
               items.add(new Blocker.BreakingPos(packet.getPos(), 0, true, 2, null));
            }
         }
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      for (Blocker.BreakingPos item : (Iterable<su.catlean.client.features.modules.protect.Blocker.BreakingPos>)items) {
         int value = item.getValue();
         item.setValue(value + 1);
         if (item.getValue() / 40.0F > this.getTriggerProgress()) {
            PlaceModule.defaultImpl(this, this.computeItems(item.getPos(), item.isFlag()), false, item.isFlag(), null, false, 26, null);
            item.setValue(Math.max(150, item.getValue()));
         }
      }

      items.removeIf(arg -> ((Blocker.BreakingPos)arg).getValue() > 200);
   }

   private final List<BlockPos> computeItems(BlockPos blockPos, boolean flag) {
      List<BlockPos> arrayList = new ArrayList();
      if (flag) {
         arrayList.add(blockPos.above());
      } else {
         for (Direction enumEntry : (Iterable<Direction>)Blocker.EntriesMappings.enumEntries) {
            arrayList.add(blockPos.relative(enumEntry));
         }
      }

      return arrayList;
   }

   private final List<BlockPos> getItems() {
      List<BlockPos> arrayList = new ArrayList();
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      Iterable enumEntries = (Iterable)Blocker.EntriesMappings.enumEntries;
      var arrayList2 = new ArrayList();

      for (Object object : enumEntries) {
         Direction direction = (Direction)object;
         if (direction != Direction.UP && direction != Direction.DOWN) {
            arrayList2.add(object);
         }
      }

      for (Direction direction2 : (Iterable<Direction>)arrayList2) {
         BlockPos relative = containing.relative(direction2);
         BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(relative);
         if (Intrinsics.areEqual(blockState.getBlock(), Blocks.OBSIDIAN) || Intrinsics.areEqual(blockState.getBlock(), Blocks.ENDER_CHEST)) {
            arrayList.add(relative);
         }
      }

      return arrayList;
   }

   static {
   }

   public static final class BreakingPos {
      @NotNull
      private final BlockPos pos;
      private int value;
      private boolean flag;

      public BreakingPos(BlockPos pos, int value, boolean flag) {
         this.pos = pos;
         this.value = value;
         this.flag = flag;
      }

      public BreakingPos(BlockPos blockPos, int value, boolean flag, int value2, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value2 & 2) != 0) {
            value = 0;
         }

         if ((value2 & 4) != 0) {
            flag = false;
         }

         this(blockPos, value, flag);
      }

      @NotNull
      public final BlockPos getPos() {
         return this.pos;
      }

      public final int getValue() {
         return this.value;
      }

      public final void setValue(int value) {
         this.value = value;
      }

      public final boolean isFlag() {
         return this.flag;
      }

      public final void setFlag(boolean flag) {
         this.flag = flag;
      }

      @NotNull
      public final BlockPos getPos2() {
         return this.pos;
      }

      public final int getValue2() {
         return this.value;
      }

      public final boolean isFlag2() {
         return this.flag;
      }

      @NotNull
      @Override
      public String toString() {
         boolean flag2 = this.flag;
         int value2 = this.value;
         BlockPos pos2 = this.pos;
         return "BreakingPos(pos=" + pos2 + ", progress=" + value2 + ", head=" + flag2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.pos.hashCode();
         hashCode = hashCode * 31 + Integer.hashCode(this.value);
         return hashCode * 31 + Boolean.hashCode(this.flag);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof Blocker.BreakingPos breakingPos)) {
            return false;
         } else if (!Intrinsics.areEqual(this.pos, breakingPos.pos)) {
            return false;
         } else {
            return this.value != breakingPos.value ? false : this.flag == breakingPos.flag;
         }
      }
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }
}