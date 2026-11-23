// deobf by pusia
package su.catlean.client.features.modules.esp;

import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchBlock;
import su.catlean.api.data.SearchChunk;
import su.catlean.api.event.events.network.AfterReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.world.BlockStateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BlocksSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EntitiesSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.api.setting.values.EntitiesValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.ChestOpenTracker;
import su.catlean.client.services.ServerInfoService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class Search extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Search.class, "selectedBlocks", "getSelectedBlocks()Lsu/catlean/yt;", 0), new MutablePropertyReference1Impl(Search.class, "selectedEntities", "getSelectedEntities()Lsu/catlean/z_;", 0), new PropertyReference1Impl(Search.class, "limit", "getLimit()Z", 0), new PropertyReference1Impl(Search.class, "limitCount", "getLimitCount()I", 0), new PropertyReference1Impl(Search.class, "mapColor", "getMapColor()Z", 0), new PropertyReference1Impl(Search.class, "color", "getColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(Search.class, "tracers", "getTracers()Z", 0), new PropertyReference1Impl(Search.class, "fill", "getFill()Z", 0), new PropertyReference1Impl(Search.class, "outline", "getOutline()Z", 0) };
   @NotNull
   public static final Search INSTANCE = new Search();
   @NotNull
   private static final BlocksSetting blocks = SettingFactory.defaultImpl(INSTANCE, "blocks", new BlocksValue(null, 1, null), null, null, 12, null);
   @NotNull
   private static final EntitiesSetting entities = SettingFactory.defaultImpl(INSTANCE, "entities", new EntitiesValue(null, 1, null), null, null, 12, null);
   @NotNull
   private static final BooleanSetting limit = SettingFactory.defaultImpl(INSTANCE, "limit", true, null, null, 12, null);
   @NotNull
   private static final IntSetting limitCount = SettingFactory.defaultImpl(
      INSTANCE, "limit-count", 50, new IntRange(1, 2048), null, () -> INSTANCE.getLimit(), 8, null
   );
   @NotNull
   private static final BooleanSetting mapColor = SettingFactory.defaultImpl(INSTANCE, "map-color", false, null, null, 12, null);
   @NotNull
   private static final ColorSetting color = SettingFactory.defaultImpl(
      INSTANCE, "color", new Color(170, 0, 0, 120), null, () -> !INSTANCE.getMapColor(), 4, null
   );
   @NotNull
   private static final BooleanSetting tracers = SettingFactory.defaultImpl(INSTANCE, "tracers", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting fill = SettingFactory.defaultImpl(INSTANCE, "fill", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting outline = SettingFactory.defaultImpl(INSTANCE, "outline", true, null, null, 12, null);
   @NotNull
   private static final ArrayList<SearchChunk> items = new ArrayList();
   private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   private static long time;
   private static boolean flag;

   private Search() {
      super("search", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final BlocksValue getSelectedBlocks() {
      return (BlocksValue)blocks.computeObject(this, $$delegatedProperties[0]);
   }

   private final EntitiesValue getSelectedEntities() {
      return (EntitiesValue)entities.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getLimit() {
      return (Boolean)limit.computeObject(this, $$delegatedProperties[2]);
   }

   private final int getLimitCount() {
      return ((Number)limitCount.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   private final boolean getMapColor() {
      return (Boolean)mapColor.computeObject(this, $$delegatedProperties[4]);
   }

   private final Color getColor() {
      return (Color)color.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getTracers() {
      return (Boolean)tracers.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getFill() {
      return (Boolean)fill.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getOutline() {
      return (Boolean)outline.computeObject(this, $$delegatedProperties[8]);
   }

   @Override
   public void update2() {
      items.clear();
      time = System.currentTimeMillis();
      flag = true;
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      if (flag) {
         executorService.submit(() -> INSTANCE.applySubmit());
      }
   }

   @Flow
   public final void onBlockState(BlockStateEvent e) {
      executorService.submit(() -> {
         ArrayList<SearchChunk> items2 = items;
         synchronized (items2) {
            int value = e.getPos().getX() >> 4;
            int value2 = e.getPos().getZ() >> 4;
            Iterable<SearchChunk> items3 = items;
            Iterator<SearchChunk> iterator = items3.iterator();

            Object object;
            while (true) {
               if (!iterator.hasNext()) {
                  object = null;
                  break;
               }

               SearchChunk object2 = iterator.next();
               SearchChunk searchChunk = (SearchChunk)object2;
               if (searchChunk.getValue2() == value2 && searchChunk.getValue() == value) {
                  object = (SearchChunk)object2;
                  break;
               }
            }

            object = object;
            if (object != null) {
               ((SearchChunk)object).setFlag(true);
            }

            Unit instance = Unit.INSTANCE;
         }
      });
   }

   @Flow
   public final void onAfterReceivePacket(AfterReceivePacket e) {
      if (e.getPacket() instanceof ClientboundLevelChunkWithLightPacket) {
         ClientboundLevelChunkWithLightPacket packet = (ClientboundLevelChunkWithLightPacket)e.getPacket();
         executorService.submit(() -> {
            ArrayList<SearchChunk> items2 = items;
            synchronized (items2) {
               Iterable<SearchChunk> items3 = items;
               Iterator<SearchChunk> iterator = items3.iterator();

               Object object;
               while (true) {
                  if (!iterator.hasNext()) {
                     object = null;
                     break;
                  }

                  SearchChunk object2 = iterator.next();
                  SearchChunk searchChunk = (SearchChunk)object2;
                  if (searchChunk.getValue2() == packet.getZ() && searchChunk.getValue() == packet.getZ()) {
                     object = (SearchChunk)object2;
                     break;
                  }
               }

               object = object;
               if (object != null) {
                  ((SearchChunk)object).setFlag(true);
               }

               Unit instance = Unit.INSTANCE;
            }
         });
      }
   }

   private final void applySubmit() {
      flag = false;
      executorService.submit(
         () -> {
            ArrayList<SearchChunk> items2 = items;
            synchronized (items2) {
               boolean testValue2 = stopWatch.testValue2(1500);

               for (ChunkPos items4Item : (Iterable<ChunkPos>)ServerInfoService.INSTANCE.getItems4()) {
                  Iterable<SearchChunk> items3 = items;
                  int value = 0;
                  Iterator<SearchChunk> iterator = items3.iterator();

                  SearchChunk object;
                  while (true) {
                     if (!iterator.hasNext()) {
                        object = null;
                        break;
                     }

                     SearchChunk object2 = iterator.next();
                     SearchChunk searchChunk = (SearchChunk)object2;
                     if (searchChunk.getValue2() == items4Item.z() && searchChunk.getValue() == items4Item.x()) {
                        object = object2;
                        break;
                     }
                  }

                  SearchChunk searchChunk2 = (SearchChunk)object;
                  if (searchChunk2 != null && testValue2 && searchChunk2.getList().isEmpty()) {
                     items.remove(searchChunk2);
                  } else if (searchChunk2 != null && !searchChunk2.isFlag()) {
                     continue;
                  }

                  List<SearchBlock> items4 = INSTANCE.computeItems(items4Item);
                  if (searchChunk2 != null && searchChunk2.isFlag()) {
                     items.remove(searchChunk2);
                  }

                  items.add(new SearchChunk(items4Item.x(), items4Item.z(), items4, false, 8, null));
                  value = (int)MinecraftHolder.getLocalPlayer().getX() >> 4;
                  final int valueF = value;
                  int value2 = (int)MinecraftHolder.getLocalPlayer().getZ() >> 4;
                  final int value2F = value2;
                  items.removeIf(
                     arg -> Math.hypot((double)valueF - ((SearchChunk)arg).getValue(), (double)value2F - ((SearchChunk)arg).getValue2())
                        > (((Number)MinecraftHolder.getMinecraft().options.renderDistance().get()).intValue() + 4) * 16
                  );
               }

               flag = true;
               Unit instance = Unit.INSTANCE;
            }
         }
      );
   }

   private final List<SearchBlock> computeItems(ChunkPos chunkPos) {
      List<SearchBlock> arrayList = new ArrayList();
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      int value = chunkPos.x() * 16;
      int value2 = chunkPos.z() * 16;

      for (int i = -64; i < 320; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               mutableBlockPos.set(value + j, i, value2 + k);
               Block block = MinecraftHolder.getClientLevel().getBlockState((BlockPos)mutableBlockPos).getBlock();
               if (this.checkContains(block)) {
                  arrayList.add(new SearchBlock(mutableBlockPos.immutable(), this.getMapColor() ? new Color(block.defaultMapColor().col) : this.getColor()));
               }
            }
         }
      }

      return arrayList;
   }

   @Flow
   public final void onRender3D(Render3DEvent e) {
      if (!items.isEmpty() || !this.getSelectedEntities().getEntities().isEmpty()) {
         int value = 0;
         if (ChestOpenTracker.INSTANCE.getValue() < 8 && MinecraftHolder.getLocalPlayer().tickCount > 100) {
            this.applyUnplug();
         } else {
            if (this.getFill() || this.getOutline()) {
               Iterable<SearchChunk> filterNotNull = CollectionsKt.filterNotNull(Lists.newArrayList(items));
               boolean flag2 = false;

               for (SearchChunk sortedWithItem : (Iterable<SearchChunk>)CollectionsKt.sortedWith(filterNotNull, new Search.Ordering2())) {
                  Iterable list = sortedWithItem.getList();

                  for (SearchBlock sortedWithItem2 : (Iterable<SearchBlock>)CollectionsKt.sortedWith(list, new Search.Ordering())) {
                     if ((value <= this.getLimitCount() || !this.getLimit())
                        && !(
                           Vec3iUtil.computeVec3(sortedWithItem2.getPos()).distanceTo(this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()))
                              > (((Number)MinecraftHolder.getMinecraft().options.renderDistance().get()).intValue() + 4) * 16
                        )) {
                        AABB aabb = new AABB(sortedWithItem2.getPos());
                        Color color2 = this.getMapColor() ? Render2D.INSTANCE.computeColor(sortedWithItem2.getColor(), 0.5F) : this.getColor();
                        if (this.getFill()) {
                           Render3D.defaultImpl2(Render3D.INSTANCE, aabb, color2, null, 4, null);
                        }

                        if (this.getOutline()) {
                           Render3D.defaultImpl3(Render3D.INSTANCE, aabb, Render2D.INSTANCE.computeColor(color2, color2.getAlpha() * 2.0F), null, 4, null);
                        }

                        if (this.getTracers()) {
                           Vec3 position = new Vec3(0.0, 0.0, 75.0)
                              .xRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().xRot())))
                              .yRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().yRot())))
                              .add(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().position());
                           Render3D.defaultImpl(Render3D.INSTANCE, position, Vec3iUtil.computeVec3(sortedWithItem2.getPos()), color2, null, true, 8, null);
                        }

                        value++;
                     }
                  }
               }

               filterNotNull = (Iterable<SearchChunk>)(Object)MinecraftHolder.getClientLevel().entitiesForRendering();
               flag2 = false;
               var arrayList = new ArrayList();

               for (Object object : filterNotNull) {
                  Entity entity = (Entity)object;
                  if (INSTANCE.getSelectedEntities().getEntities().contains(entity.getType())) {
                     arrayList.add(object);
                  }
               }

               for (Entity entity2 : (Iterable<Entity>)arrayList) {
                  if (!Intrinsics.areEqual(entity2, MinecraftHolder.getLocalPlayer())) {
                     AABB aabb2 = Render3D.INSTANCE.computeAABB(entity2);
                     if (this.getFill()) {
                        Render3D.defaultImpl2(Render3D.INSTANCE, aabb2, this.getColor(), null, 4, null);
                     }

                     if (this.getOutline()) {
                        Render3D.defaultImpl3(
                           Render3D.INSTANCE, aabb2, Render2D.INSTANCE.computeColor(this.getColor(), this.getColor().getAlpha() * 2.0F), null, 4, null
                        );
                     }

                     if (this.getTracers()) {
                        Vec3 position2 = new Vec3(0.0, 0.0, 75.0)
                           .xRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().xRot())))
                           .yRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().yRot())))
                           .add(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().position());
                        Render3D.defaultImpl(Render3D.INSTANCE, position2, aabb2.getCenter(), this.getColor(), null, true, 8, null);
                     }
                  }
               }
            }

            time = System.currentTimeMillis();
         }
      }
   }

   private final boolean checkContains(Block block) {
      return this.getSelectedBlocks().getBlocks().contains(block);
   }

   static {
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         SearchBlock object3 = (SearchBlock)object;
         Comparable distanceToSqr = MinecraftHolder.getLocalPlayer().distanceToSqr(Vec3iUtil.computeVec3(object3.getPos()));
         object3 = (SearchBlock)object2;
         Comparable comparable = distanceToSqr;
         return ComparisonsKt.compareValues(comparable, MinecraftHolder.getLocalPlayer().distanceToSqr(Vec3iUtil.computeVec3(object3.getPos())));
      }
   }

   public static final class Ordering2 implements Comparator {
      public Ordering2() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         SearchChunk object3 = (SearchChunk)object;
         Comparable distanceToSqr = MinecraftHolder.getLocalPlayer()
            .distanceToSqr((double)object3.getValue() * 16, MinecraftHolder.getLocalPlayer().getY(), (double)object3.getValue2() * 16);
         object3 = (SearchChunk)object2;
         Comparable comparable = distanceToSqr;
         return ComparisonsKt.compareValues(
            comparable,
            MinecraftHolder.getLocalPlayer()
               .distanceToSqr((double)object3.getValue() * 16, MinecraftHolder.getLocalPlayer().getY(), (double)object3.getValue2() * 16)
         );
      }
   }
}