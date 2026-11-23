// deobf by pusia
package su.catlean.client.features.modules.basic;

import java.util.ArrayList;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.KeyMappingAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;

public final class WindHop extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new MutablePropertyReference1Impl(WindHop.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0)};
   @NotNull
   public static final WindHop INSTANCE = new WindHop();
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);

   private WindHop() {
      super("wind-hop", SubCategories.getSubCategory4(), null, 4, null);
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[0]);
   }

   @Override
   public void update4() {
      if (!InputUtil.checkContains(((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyJump).getKey().getValue())) {
         MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
      }
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      MinecraftHolder.getMinecraft().options.keyJump.setDown(true);
      if (!MinecraftHolder.getLocalPlayer().getCooldowns().isOnCooldown(Items.WIND_CHARGE.getDefaultInstance())) {
         if (!MinecraftHolder.getLocalPlayer().onGround()
            && MinecraftHolder.getClientLevel()
               .getBlockCollisions((Entity)MinecraftHolder.getLocalPlayer(), MinecraftHolder.getLocalPlayer().getBoundingBox().move(0.0, -1.0, 0.0))
               .iterator()
               .hasNext()) {
            this.updateRotation(new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), 90.0F, false, null, 12, null));
         } else if (MinecraftHolder.getLocalPlayer().horizontalCollision) {
            BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
            Iterable enumEntries = (Iterable)WindHop.EntriesMappings.enumEntries;
            var arrayList = new ArrayList();

            for (Object object : enumEntries) {
               Direction direction = (Direction)object;
               if (direction != Direction.UP && direction != Direction.DOWN) {
                  arrayList.add(object);
               }
            }

            for (Direction direction2 : (Iterable<Direction>)arrayList) {
               BlockPos relative = containing.relative(direction2);
               if (!MinecraftHolder.getClientLevel().getBlockState(relative).canBeReplaced()) {
                  this.updateRotation(Rotation.vectorUtil.computeRotation(Vec3iUtil.computeVec3(relative).add(0.0, -0.5, 0.0)));
               }
            }
         }
      }
   }

   private final void updateRotation(Rotation rotation) {
      SearchInvResult searchInvResult = InventoryManager.INSTANCE
         .computeSearchInvResult(arg -> Intrinsics.areEqual(arg.getItem(), Items.WIND_CHARGE), this.getSwitchMode());
      if (searchInvResult.isFlag()) {
         RotationManager.INSTANCE.updateRotationAction(new RotationAction(rotation, 150, () -> {
            InventoryManager.INSTANCE.applyGetInventory(searchInvResult.getValue(), INSTANCE.getSwitchMode(), () -> {
               ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
               Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
               PacketUtil.applySend(serverboundSwingPacket);
               return Unit.INSTANCE;
            });
            return Unit.INSTANCE;
         }));
      }
   }

   static {
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());

      static {
      }
   }
}