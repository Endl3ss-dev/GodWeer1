// deobf by pusia
package su.catlean.client.features.modules.rage;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.world.WorldUtil;

public final class Spider extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Spider.class, "mode", "getMode()Lsu/catlean/sd;", 0), new PropertyReference1Impl(Spider.class, "delay", "getDelay()I", 0) };
   @NotNull
   public static final Spider INSTANCE = new Spider();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", Spider.Mode.DEFAULT, null, null, 12, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 2, new IntRange(1, 15), null, null, 24, null);

   private Spider() {
      super("spider", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final Spider.Mode getMode() {
      return (Spider.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getLocalPlayer().horizontalCollision) {
         switch (Spider.WhenMappings.values[this.getMode().ordinal()]) {
            case 1:
               MinecraftHolder.getLocalPlayer().setOnGround(MinecraftHolder.getLocalPlayer().tickCount % this.getDelay() == 0);
               if (MinecraftHolder.getLocalPlayer().onGround()) {
                  MinecraftHolder.getLocalPlayer()
                     .setDeltaMovement(MinecraftHolder.getLocalPlayer().getDeltaMovement().x(), 0.42, MinecraftHolder.getLocalPlayer().getDeltaMovement().z());
               }
               break;
            case 2:
               MinecraftHolder.getLocalPlayer()
                  .setDeltaMovement(MinecraftHolder.getLocalPlayer().getDeltaMovement().x(), 0.21, MinecraftHolder.getLocalPlayer().getDeltaMovement().z());
               break;
            case 3:
               if (MinecraftHolder.getLocalPlayer().tickCount % 2 == 0
                  && MinecraftHolder.getMinecraft().options.keyJump.isDown()
                  && this.getMode() == Spider.Mode.WATER_BUCKET) {
                  float xRot = MinecraftHolder.getLocalPlayer().getXRot();
                  MinecraftHolder.getLocalPlayer().setXRot(82.0F);
                  InventoryManager instance = InventoryManager.INSTANCE;
                  Item[] items = new Item[]{Items.WATER_BUCKET};
                  SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
                  if (searchInvResult.isFlag()) {
                     InventoryManager.INSTANCE.updateValue(searchInvResult.getValue());
                     ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
                     MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                     InventoryManager.INSTANCE.update();
                  }

                  MinecraftHolder.getLocalPlayer().setXRot(xRot);
               }
         }
      }
   }

   @Flow
   private final void onPreSync(PreSyncEvent preSyncEvent) {
      switch (Spider.WhenMappings.values[this.getMode().ordinal()]) {
         case 4:
            if (MinecraftHolder.getLocalPlayer().horizontalCollision
               && MinecraftHolder.getMinecraft().options.keyJump.isDown()
               && MinecraftHolder.getLocalPlayer().getDeltaMovement().y() <= -0.3739040364667221
               && this.getMode() == Spider.Mode.MATRIX_2) {
               MinecraftHolder.getLocalPlayer().setOnGround(true);
               MinecraftHolder.getLocalPlayer()
                  .setDeltaMovement(
                     MinecraftHolder.getLocalPlayer().getDeltaMovement().x(), 0.48114514191918, MinecraftHolder.getLocalPlayer().getDeltaMovement().z()
                  );
            }
            break;
         case 5:
            if (MinecraftHolder.getLocalPlayer().horizontalCollision && WorldUtil.INSTANCE.isFlag4()) {
               MinecraftHolder.getLocalPlayer().setOnGround(true);
               MinecraftHolder.getLocalPlayer().jumpFromGround();
            }
      }
   }

   static {
   }

   public static enum Mode {
      DEFAULT,
      MATRIX,
      MATRIX_2,
      WATER_BUCKET,
      FT_FENCE;

      private static final Spider.Mode[] modes = $values();
      private static final EnumEntries<Spider.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<Spider.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final Spider.Mode[] $values() {
         return new Spider.Mode[]{DEFAULT, MATRIX, MATRIX_2, WATER_BUCKET, FT_FENCE};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Spider.Mode.values().length];

         try {
            values2[Spider.Mode.MATRIX.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Spider.Mode.DEFAULT.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[Spider.Mode.WATER_BUCKET.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[Spider.Mode.MATRIX_2.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[Spider.Mode.FT_FENCE.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         values = values2;
      }
   }
}