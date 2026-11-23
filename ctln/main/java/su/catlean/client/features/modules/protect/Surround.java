// deobf by pusia
package su.catlean.client.features.modules.protect;

import net.minecraft.network.PacketListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.ClosedFloatingPointRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.network.AfterReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.world.WorldUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.SettingHolder;
import su.catlean.api.setting.settings.BlocksSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class Surround extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Surround.class, "yDisable", "getYDisable()Z", 0), new PropertyReference1Impl(Surround.class, "async", "getAsync()Z", 0), new PropertyReference1Impl(Surround.class, "asyncDelay", "getAsyncDelay()I", 0), new MutablePropertyReference1Impl(Surround.class, "selectedBlocks", "getSelectedBlocks()Lsu/catlean/yt;", 0), new MutablePropertyReference1Impl(Surround.class, "center", "getCenter()Lsu/catlean/we;", 0), new MutablePropertyReference1Impl(Surround.class, "forceCenter", "getForceCenter()Z", 0) };
   @NotNull
   public static final Surround INSTANCE = new Surround();
   @NotNull
   private static final BooleanSetting booleanSetting = SettingFactory.defaultImpl(INSTANCE, "y disable", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting async = SettingFactory.defaultImpl(INSTANCE, "async", true, null, null, 12, null);
   @NotNull
   private static final IntSetting asyncDelay = SettingFactory.defaultImpl(
      INSTANCE, "async-delay", 0, new IntRange(0, 250), null, () -> INSTANCE.getAsync(), 8, null
   );
   @NotNull
   private static final BlocksSetting blocks;
   @NotNull
   private static final EnumSetting center = SettingFactory.defaultImpl(INSTANCE, "center", Surround.Center.OFF, null, null, 12, null);
   @NotNull
   private static final BooleanSetting forceCenter = SettingFactory.defaultImpl(
      INSTANCE, "force-center", false, null, () -> INSTANCE.getCenter() != Surround.Center.OFF, 4, null
   );
   private static boolean flag;
   @NotNull
   private static StopWatch stopWatch = new StopWatch();
   @NotNull
   private static StopWatch stopWatch2 = new StopWatch();

   private Surround() {
      super("surround", SubCategories.getSubCategory(), 200, false);
   }

   private final boolean getYDisable() {
      return (Boolean)booleanSetting.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getAsync() {
      return (Boolean)async.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getAsyncDelay() {
      return ((Number)asyncDelay.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final BlocksValue getSelectedBlocks() {
      return (BlocksValue)blocks.computeObject(this, $$delegatedProperties[3]);
   }

   private final Surround.Center getCenter() {
      return (Surround.Center)center.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getForceCenter() {
      return (Boolean)forceCenter.computeObject(this, $$delegatedProperties[5]);
   }

   @Override
   public void update2() {
      if (!this.isFlag6()) {
         flag = false;
      } else {
         if (this.getCenter() != Surround.Center.OFF) {
            BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
            ClosedFloatingPointRange<Double> rangeTo = RangesKt.rangeTo(containing.getX() + 0.301, containing.getX() + 0.699);
            ClosedFloatingPointRange<Double> rangeTo2 = RangesKt.rangeTo(containing.getZ() + 0.301, containing.getZ() + 0.699);
            double value = this.getCenter() == Surround.Center.DEFAULT
               ? Vec3iUtil.computeVec3(containing).x
               : ((Number)RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getX(), rangeTo)).doubleValue();
            double value2 = this.getCenter() == Surround.Center.DEFAULT
               ? Vec3iUtil.computeVec3(containing).z
               : ((Number)RangesKt.coerceIn(MinecraftHolder.getLocalPlayer().getZ(), rangeTo2)).doubleValue();
            MinecraftHolder.getLocalPlayer().setPos(new Vec3(value, this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).y, value2));
            if (this.getForceCenter()) {
               LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
               localPlayer.connection
                  .send((Packet)(new PosRot(value, localPlayer.getY(), value2, localPlayer.getYRot(), localPlayer.getXRot(), localPlayer.onGround(), localPlayer.horizontalCollision)));
            }
         }

         flag = false;
      }
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      if (MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getLocalPlayer().getY() > Math.floor(MinecraftHolder.getLocalPlayer().yo) + 0.9F
         && this.getYDisable()) {
         this.applyUnplug();
      }
   }

   @Flow
   private final void onWorldUpdate(WorldUpdateEvent worldUpdateEvent) {
      if (this.getAsync()
         && stopWatch.testValue2(this.getAsyncDelay())
         && stopWatch2.testValue((int)Math.ceil(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) * 1.5))) {
         PlaceModule.defaultImpl(
            this,
            this.computeItems(
               BlockPos.containing(
                  MinecraftHolder.getLocalPlayer().getX(),
                  MinecraftHolder.getLocalPlayer().getY() - Math.floor(MinecraftHolder.getLocalPlayer().getY()) > 0.8
                     ? Math.floor(MinecraftHolder.getLocalPlayer().getY()) + 1.0
                     : Math.floor(MinecraftHolder.getLocalPlayer().getY()),
                  MinecraftHolder.getLocalPlayer().getZ()
               ),
               new ArrayList()
            ),
            true,
            false,
            null,
            false,
            28,
            null
         );
         flag = true;
      }
   }

   @Flow(
      priority = 10
   )
   private final void onAfterReceivePacket(AfterReceivePacket afterReceivePacket) {
      if (this.getAsync() && afterReceivePacket.getPacket() instanceof ClientboundAddEntityPacket) {
         Packet<? extends PacketListener> packet = afterReceivePacket.getPacket();
         Vec3 vec3 = new Vec3(
            ((ClientboundAddEntityPacket)packet).getX(), ((ClientboundAddEntityPacket)packet).getY(), ((ClientboundAddEntityPacket)packet).getZ()
         );
         if (Intrinsics.areEqual(((ClientboundAddEntityPacket)packet).getType(), EntityTypes.END_CRYSTAL)
            && vec3.distanceToSqr(MinecraftHolder.getLocalPlayer().getEyePosition()) < 9.0) {
            List items = this.computeItems(
               BlockPos.containing(
                  MinecraftHolder.getLocalPlayer().getX(),
                  MinecraftHolder.getLocalPlayer().getY() - Math.floor(MinecraftHolder.getLocalPlayer().getY()) > 0.8
                     ? Math.floor(MinecraftHolder.getLocalPlayer().getY()) + 1.0
                     : Math.floor(MinecraftHolder.getLocalPlayer().getY()),
                  MinecraftHolder.getLocalPlayer().getZ()
               ),
               new ArrayList()
            );
            if (!items.isEmpty()) {
               ServerboundAttackPacket serverboundAttackPacket = new ServerboundAttackPacket(((ClientboundAddEntityPacket)packet).getId());
               PacketUtil.applySend((Packet)serverboundAttackPacket);
               Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
               PacketUtil.applySend(serverboundSwingPacket);
               PlaceModule thisValue = this;
               Iterable iterable = items;
               PlaceModule.defaultImpl(thisValue, CollectionsKt.sortedWith(iterable, new Surround.Ordering(vec3)), true, false, null, false, 28, null);
               flag = true;
               afterReceivePacket.cancel();
            }
         }
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (flag) {
         flag = false;
      } else {
         if (PlaceModule.defaultImpl(
            this,
            this.computeItems(
               BlockPos.containing(
                  MinecraftHolder.getLocalPlayer().getX(),
                  MinecraftHolder.getLocalPlayer().getY() - Math.floor(MinecraftHolder.getLocalPlayer().getY()) > 0.8
                     ? Math.floor(MinecraftHolder.getLocalPlayer().getY()) + 1.0
                     : Math.floor(MinecraftHolder.getLocalPlayer().getY()),
                  MinecraftHolder.getLocalPlayer().getZ()
               ),
               new ArrayList()
            ),
            false,
            false,
            null,
            false,
            30,
            null
         )) {
            stopWatch2.update();
         }
      }
   }

   private final List computeItems(BlockPos blockPos, List items) {
      List<BlockPos> arrayList = new ArrayList();
      Iterable enumEntries = (Iterable)Surround.EntriesMappings.enumEntries;
      boolean flag2 = false;
      var arrayList2 = new ArrayList();
      boolean flag3 = false;

      for (Object object : enumEntries) {
         Direction direction = (Direction)object;
         if (direction != Direction.UP) {
            arrayList2.add(object);
         }
      }

      for (Direction direction2 : (Iterable<Direction>)arrayList2) {
         BlockPos relative = blockPos.relative(direction2);
         if (!items.contains(relative) && MinecraftHolder.getClientLevel().getBlockState(relative).canBeReplaced()) {
            if (this.checkGetEntitiesOfClass(relative)) {
               items.add(blockPos);
               arrayList.addAll(this.computeItems(relative, items));
            } else if (InteractionManager.defaultImpl(
                  InteractionManager.INSTANCE,
                  relative,
                  0,
                  this.getInteract(),
                  this.getPlaceMode(),
                  TargetFilter.ONLY_CRYSTALS,
                  6.0F,
                  6.0F,
                  this.getValue(),
                  this.getSwitch(),
                  null,
                  false,
                  1536,
                  null
               )
               != null) {
               arrayList.add(relative);
            } else {
               Iterable enumEntries2 = (Iterable)Surround.EntriesMappings.enumEntries;
               flag3 = false;
               var arrayList3 = new ArrayList();

               for (Object object2 : enumEntries2) {
                  Direction direction3 = (Direction)object2;
                  if (direction3 != Direction.UP) {
                     arrayList3.add(object2);
                  }
               }

               for (Direction direction4 : (Iterable<Direction>)arrayList3) {
                  if (!items.contains(relative) && MinecraftHolder.getClientLevel().getBlockState(relative).canBeReplaced()) {
                     BlockPos relative2 = relative.relative(direction4);
                     if (InteractionManager.defaultImpl(
                           InteractionManager.INSTANCE,
                           relative2,
                           0,
                           this.getInteract(),
                           this.getPlaceMode(),
                           TargetFilter.ONLY_CRYSTALS,
                           6.0F,
                           6.0F,
                           this.getValue(),
                           this.getSwitch(),
                           null,
                           false,
                           1536,
                           null
                        )
                        != null) {
                        arrayList.add(relative2);
                        arrayList.add(relative);
                     }
                  }
               }
            }
         }
      }

      double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, 0.5F, true, 0.0F, 0.0F, 0.0F, 28, null);
      enumEntries = arrayList;
      flag2 = false;
      return CollectionsKt.sortedWith(enumEntries, new Surround.Ordering2(defaultImpl));
   }

   @NotNull
   @Override
   public List getItems3() {
      return this.getSelectedBlocks().getBlocks();
   }

   static {
      SettingHolder instance = INSTANCE;
      Block[] blocks2 = new Block[]{Blocks.OBSIDIAN, Blocks.ENDER_CHEST, Blocks.NETHERITE_BLOCK};
      blocks = SettingFactory.defaultImpl(instance, "blocks", new BlocksValue(CollectionsKt.mutableListOf(blocks2)), null, null, 12, null);
   }

   public static enum Center {
      OFF,
      DEFAULT,
      SMART;

      private static final Surround.Center[] centers = $values();
      private static final EnumEntries<Surround.Center> enumEntries = EnumEntriesKt.enumEntries(centers);

      private Center() {
      }

      @NotNull
      public static EnumEntries<Surround.Center> getEnumEntries() {
         return enumEntries;
      }

      private static final Surround.Center[] $values() {
         return new Surround.Center[]{OFF, DEFAULT, SMART};
      }
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static final class Ordering implements Comparator {
      public final Vec3 vec3;

      public Ordering(Vec3 vec3) {
         this.vec3 = vec3;
      }

      @Override
      public final int compare(Object object, Object object2) {
         BlockPos object3 = (BlockPos)object;
         Comparable distanceTo = Vec3iUtil.computeVec3(object3).distanceTo(this.vec3);
         object3 = (BlockPos)object2;
         Comparable comparable = distanceTo;
         return ComparisonsKt.compareValues(comparable, Vec3iUtil.computeVec3(object3).distanceTo(this.vec3));
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