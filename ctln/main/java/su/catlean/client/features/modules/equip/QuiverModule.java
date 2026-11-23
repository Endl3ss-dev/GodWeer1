// deobf by pusia
package su.catlean.client.features.modules.equip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;

public final class QuiverModule extends Module {
   @NotNull
   public static final QuiverModule INSTANCE = new QuiverModule();

   private QuiverModule() {
      super("Quiver", SubCategories.getSubCategory(), null, 4, null);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.BOW)
         && MinecraftHolder.getLocalPlayer().getTicksUsingItem() >= 4
         && this.isFlag()) {
         RotationManager instance = RotationManager.INSTANCE;
         Rotation rotation = new Rotation(MinecraftHolder.getLocalPlayer().getXRot(), -90.0F, false, null, 12, null);
         Function0 function0 = () -> {
            Packet serverboundPlayerActionPacket = (Packet)(new ServerboundPlayerActionPacket(Action.RELEASE_USE_ITEM, BlockPos.ZERO, MinecraftHolder.getLocalPlayer().getDirection()));
            PacketUtil.applySend(serverboundPlayerActionPacket);
            MinecraftHolder.getLocalPlayer().releaseUsingItem();
            return Unit.INSTANCE;
         };
         byte byteValue = 0;
         Rotation rotation2 = rotation;
         instance.updateRotationAction(new RotationAction(rotation2, byteValue, function0));
      }
   }

   private final boolean isFlag() {
      Iterable intRange = (Iterable)(new IntRange(0, 35));
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(intRange, 10));
      Iterator iterator = intRange.iterator();

      while (iterator.hasNext()) {
         int nextInt = ((IntIterator)iterator).nextInt();
         ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(nextInt);
         arrayList.add(
            item.getItem() instanceof ArrowItem
               ? new QuiverModule.ArrowData(
                  nextInt,
                  (MobEffectInstance)CollectionsKt.firstOrNull(
                     ((PotionContents)item.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).getAllEffects()
                  )
               )
               : null
         );
      }

      List filterNotNull = CollectionsKt.filterNotNull(arrayList);
      if (filterNotNull.isEmpty()) {
         return false;
      } else {
         Iterable iterable = filterNotNull;
         Iterator iterator2 = iterable.iterator();
         if (!iterator2.hasNext()) {
            throw new NoSuchElementException();
         } else {
            Object object = iterator2.next();
            Object object2;
            if (!iterator2.hasNext()) {
               object2 = object;
            } else {
               QuiverModule.ArrowData arrowData = (QuiverModule.ArrowData)object;
               int value = arrowData.getValue();

               do {
                  Object object3 = iterator2.next();
                  QuiverModule.ArrowData arrowData2 = (QuiverModule.ArrowData)object3;
                  int value2 = arrowData2.getValue();
                  if (value > value2) {
                     object = object3;
                     value = value2;
                  }
               } while (iterator2.hasNext());

               object2 = object;
            }

            int value3 = ((QuiverModule.ArrowData)object2).getValue();

            for (QuiverModule.ArrowData arrowData3 : (Iterable<su.catlean.client.features.modules.equip.QuiverModule.ArrowData>)filterNotNull) {
               if (this.checkGetEffect(arrowData3)) {
                  if (arrowData3.getValue() != value3) {
                     ContainerUtil.defaultImpl(arrowData3.getValue(), 0, null, false, 14, null);
                     ContainerUtil.defaultImpl(value3, 0, null, false, 14, null);
                     ContainerUtil.defaultImpl(arrowData3.getValue(), 0, null, true, 6, null);
                  }

                  return true;
               }
            }

            return false;
         }
      }
   }

   private final boolean checkGetEffect(QuiverModule.ArrowData arrowData) {
      MobEffectInstance mobEffectInstance = arrowData.getMobEffectInstance();
      if (Intrinsics.areEqual(mobEffectInstance != null ? mobEffectInstance.getEffect() : null, MobEffects.STRENGTH)) {
         LocalPlayer player = MinecraftHolder.getMinecraft().player;
         if (player != null ? !player.hasEffect(MobEffects.STRENGTH) : false) {
            return true;
         }
      }

      mobEffectInstance = arrowData.getMobEffectInstance();
      if (Intrinsics.areEqual(mobEffectInstance != null ? mobEffectInstance.getEffect() : null, MobEffects.SPEED)) {
         LocalPlayer player2 = MinecraftHolder.getMinecraft().player;
         if (player2 != null ? !player2.hasEffect(MobEffects.SPEED) : false) {
            return true;
         }
      }

      return false;
   }

   public static final class ArrowData {
      private final int value;
      @Nullable
      private final MobEffectInstance mobEffectInstance;

      public ArrowData(int value, MobEffectInstance mobEffectInstance) {
         this.value = value;
         this.mobEffectInstance = mobEffectInstance;
      }

      public final int getValue() {
         return this.value;
      }

      @Nullable
      public final MobEffectInstance getMobEffectInstance() {
         return this.mobEffectInstance;
      }

      public final int getValue2() {
         return this.value;
      }

      @Nullable
      public final MobEffectInstance getMobEffectInstance2() {
         return this.mobEffectInstance;
      }

      @NotNull
      public final QuiverModule.ArrowData computeArrowData(int value, MobEffectInstance mobEffectInstance) {
         return new QuiverModule.ArrowData(value, mobEffectInstance);
      }

      @NotNull
      @Override
      public String toString() {
         return "ArrowData(slot=" + this.value + ", effect=" + this.mobEffectInstance + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Integer.hashCode(this.value);
         return hashCode * 31 + (this.mobEffectInstance == null ? 0 : this.mobEffectInstance.hashCode());
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof QuiverModule.ArrowData arrowData)) {
            return false;
         } else {
            return this.value != arrowData.value ? false : Intrinsics.areEqual(this.mobEffectInstance, arrowData.mobEffectInstance);
         }
      }
   }
}