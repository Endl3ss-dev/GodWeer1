// deobf by pusia
package su.catlean.client.features.modules.attack;

import net.minecraft.world.item.Item;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalBreakChecker;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;

public final class AntiPhase extends PlaceModule {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AntiPhase.class, "trapCrawlers", "getTrapCrawlers()Z", 0), new PropertyReference1Impl(AntiPhase.class, "group", "getGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(AntiPhase.class, "ladders", "getLadders()Z", 0), new PropertyReference1Impl(AntiPhase.class, "vines", "getVines()Z", 0), new PropertyReference1Impl(AntiPhase.class, "frames", "getFrames()Z", 0), new PropertyReference1Impl(AntiPhase.class, "scaffoldings", "getScaffoldings()Z", 0) };
   @NotNull
   public static final AntiPhase INSTANCE = new AntiPhase();
   @NotNull
   private static final BooleanSetting trapCrawlers = SettingFactory.defaultImpl(INSTANCE, "trap-crawlers", false, null, null, 12, null);
   @NotNull
   private static final GroupSetting blocks = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "blocks", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting ladders = SettingFactory.defaultImpl(INSTANCE, "ladders", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting vines = SettingFactory.defaultImpl(INSTANCE, "vines", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting frames = SettingFactory.defaultImpl(INSTANCE, "frames", true, INSTANCE.getGroup(), null, 8, null);
   @NotNull
   private static final BooleanSetting scaffoldings = SettingFactory.defaultImpl(INSTANCE, "scaffoldings", true, INSTANCE.getGroup(), null, 8, null);

   private AntiPhase() {
      super("anti-phase", SubCategories.getSubCategory2(), 0, false, 12, null);
   }

   private final boolean getTrapCrawlers() {
      return (Boolean)trapCrawlers.computeObject(this, $$delegatedProperties[0]);
   }

   private final GroupValue getGroup() {
      return (GroupValue)blocks.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getLadders() {
      return (Boolean)ladders.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getVines() {
      return (Boolean)vines.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getFrames() {
      return (Boolean)frames.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getScaffoldings() {
      return (Boolean)scaffoldings.computeObject(this, $$delegatedProperties[5]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      PlaceModule.defaultImpl2(this, this.getItems(), false, false, null, false, 30, null);
   }

   private final List getItems() {
      Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();
      var arrayList = new ArrayList();

      for (Object object : players) {
         AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
         if (INSTANCE.checkContaining((Player)abstractClientPlayer)) {
            arrayList.add(object);
         }
      }

      players = arrayList;
      arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(players, 10));

      for (Object object2 : players) {
         AbstractClientPlayer abstractClientPlayer2 = (AbstractClientPlayer)object2;
         arrayList.add(BlockPos.containing((Position)INSTANCE.computeVec3((Entity)abstractClientPlayer2)));
      }

      return arrayList;
   }

   @NotNull
   @Override
   public List getItems4() {
      List<Item> arrayList = new ArrayList();
      if (this.getScaffoldings()) {
         arrayList.add(Blocks.SCAFFOLDING.asItem());
      }

      if (this.getLadders()) {
         arrayList.add(Blocks.LADDER.asItem());
      }

      if (this.getVines()) {
         arrayList.add(Blocks.VINE.asItem());
      }

      if (this.getFrames()) {
         arrayList.add(Items.ITEM_FRAME);
      }

      return arrayList;
   }

   private final boolean checkContaining(Player player) {
      if (player.isAlive()
         && !Intrinsics.areEqual(MinecraftHolder.getLocalPlayer(), player)
         && !player.isSwimming()
         && !PlayerNameUtil.checkGetGameProfile(player)) {
         if (MinecraftHolder.getClientLevel().noBlockCollision((Entity)player, player.getBoundingBox().move(0.0, 0.1, 0.0).inflate(1.0, 0.0, 1.0))
            || CrystalBreakChecker.INSTANCE.computeItems(player).size() > 1) {
            return false;
         } else if (this.checkEntitiesForRendering(new AABB(BlockPos.containing((Position)this.computeVec3((Entity)player).add(0.0, 0.3, 0.0))))) {
            return false;
         } else {
            return !this.getTrapCrawlers() && player.isVisuallyCrawling()
               ? false
               : MinecraftHolder.getLocalPlayer().distanceTo((Entity)player) <= this.getRange() + 3.0F;
         }
      } else {
         return false;
      }
   }

   private final boolean checkEntitiesForRendering(AABB aABB) {
      Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
      var arrayList = new ArrayList();

      for (Object object : entitiesForRendering) {
         if (object instanceof ItemFrame) {
            arrayList.add(object);
         }
      }

      entitiesForRendering = arrayList;
      boolean flag2;
      if (entitiesForRendering instanceof Collection && ((Collection)entitiesForRendering).isEmpty()) {
         flag2 = false;
      } else {
         Iterator<Entity> iterator = entitiesForRendering.iterator();

         while (true) {
            if (!iterator.hasNext()) {
               flag2 = false;
               break;
            }

            Entity object2 = iterator.next();
            ItemFrame itemFrame = (ItemFrame)object2;
            if (itemFrame.getBoundingBox().intersects(aABB)) {
               flag2 = true;
               break;
            }
         }
      }

      return flag2;
   }

   @NotNull
   @Override
   public TargetFilter getTargetFilter() {
      return TargetFilter.NONE;
   }

   static {
   }
}