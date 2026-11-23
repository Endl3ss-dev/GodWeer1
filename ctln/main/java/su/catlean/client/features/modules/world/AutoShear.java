// deobf by pusia
package su.catlean.client.features.modules.world;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.LookRotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerUtil;

public final class AutoShear extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(AutoShear.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0), new MutablePropertyReference1Impl(AutoShear.class, "delay", "getDelay()I", 0) };
   @NotNull
   public static final AutoShear INSTANCE = new AutoShear();
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 0, new IntRange(0, 10), null, null, 24, null);
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();

   private AutoShear() {
      super("auto-shear", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (playerUtil.testValue2(this.getDelay())) {
         Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
         boolean flag = false;
         var arrayList = new ArrayList();

         for (Object object : entitiesForRendering) {
            Entity entity = (Entity)object;
            if (entity instanceof Sheep
               && !((Sheep)entity).isSheared()
               && !((Sheep)entity).isBaby()
               && MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(((Sheep)entity).getEyePosition())
                  < MinecraftHolder.getLocalPlayer().entityInteractionRange()) {
               arrayList.add(object);
            }
         }

         entitiesForRendering = arrayList;
         flag = false;
         List sortedWith = CollectionsKt.sortedWith(entitiesForRendering, new AutoShear.Ordering());
         SearchInvResult searchInvResult = InventoryManager.INSTANCE.computeSearchInvResult(arg -> arg.getItem() instanceof ShearsItem, this.getSwitchMode());
         if (sortedWith.isEmpty() || !searchInvResult.isFlag()) {
            return;
         }

         Entity first = (Entity)CollectionsKt.first(sortedWith);
         RotationManager.INSTANCE.updateRotationAction(new LookRotationAction(Rotation.vectorUtil.computeRotation(first.getEyePosition()), 0, () -> {
            InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getSwitchMode(), () -> {
               EntityHitResult entityHitResult = new EntityHitResult(first, first.getBoundingBox().getCenter());
               MinecraftHolder.getMultiPlayerGameMode().interact((Player)MinecraftHolder.getLocalPlayer(), first, entityHitResult, InteractionHand.MAIN_HAND);
               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
               return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
         }, 2, null));
      }
   }

   static {
   }

   public static final class Ordering implements Comparator {
      public Ordering() {
      }

      @Override
      public final int compare(Object object, Object object2) {
         Entity object3 = (Entity)object;
         Comparable distanceToSqr = MinecraftHolder.getLocalPlayer().getEyePosition().distanceToSqr(AutoShear.INSTANCE.computeVec3(object3));
         object3 = (Entity)object2;
         Comparable comparable = distanceToSqr;
         return ComparisonsKt.compareValues(comparable, MinecraftHolder.getLocalPlayer().getEyePosition().distanceToSqr(AutoShear.INSTANCE.computeVec3(object3)));
      }
   }
}