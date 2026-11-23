// deobf by pusia
package su.catlean.client.features.modules.attack.aura;

import su.catlean.utils.KotlinCompat;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemAttributeModifiers.Entry;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.protect.Offhand;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.world.WorldUtil;

public final class AuraDamageHandler implements AuraHandler {
   @NotNull
   public static final AuraDamageHandler INSTANCE = new AuraDamageHandler();
   @NotNull
   private static Function0 callback = () -> Unit.INSTANCE;
   @NotNull
   private static Function0 callback2 = () -> Unit.INSTANCE;

   private AuraDamageHandler() {
   }

   @NotNull
   public final Function0 getCallback() {
      return callback;
   }

   @NotNull
   public final Function0 getCallback2() {
      return callback2;
   }

   @Override
   public void update() {
   }

   @Override
   public void applyMax(Rotation rotation) {
      callback2.invoke();
      callback2 = () -> Unit.INSTANCE;
      Offhand.INSTANCE.setValue3(1);
      WorldUtil.INSTANCE.applyGetKey();
   }

   @Override
   public void update2() {
   }

   public final void applyScreen() {
      if (Aura.INSTANCE.getOnHitSwap()) {
         int value = this.getValue();
         if (value != -1) {
            boolean flag = !(MinecraftHolder.getMinecraft().gui.screen() instanceof ShulkerBoxScreen)
               && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ContainerScreen);
            WorldUtil.INSTANCE.applySetDown();
            TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 2, (byte)0, false, () -> WorldUtil.INSTANCE.applyGetKey(), 6, null);
            int value2 = value < 9 ? value + 36 : value;
            Function0 callback3 = () -> {
               GuiMove.INSTANCE.setFlag5(true);
               if (!MinecraftHolder.getLocalPlayer().containerMenu.getCarried().isEmpty()) {
                  ContainerUtil.defaultImpl(-999, 0, null, false, 14, null);
               }

               ContainerInput swap = ContainerInput.SWAP;
               byte byteValue = 40;
               ContainerUtil.applyHandleContainerInput(value2, byteValue, swap, flag);
               GuiMove.INSTANCE.setFlag5(false);
               Offhand.INSTANCE.setValue3(15);
               return Unit.INSTANCE;
            };
            callback = callback3;
            callback2 = callback3;
         }
      }
   }

   public final int getValue() {
      int value = -1;
      double amount = 0.0;

      for (int i = 0; i < 37; i++) {
         if (i == 36) {
            for (TypedDataComponent<?> component : MinecraftHolder.getLocalPlayer().getOffhandItem().getComponents()) {
               if (Intrinsics.areEqual(component.type().toString(), "minecraft:attribute_modifiers") && component.value() instanceof ItemAttributeModifiers) {
                  for (Entry modifier : ((ItemAttributeModifiers)component.value()).modifiers()) {
                     if (KotlinCompat.contains(((Attribute)modifier.attribute().value()).getDescriptionId(), "attack_damage", false, 2, null)
                        && modifier.slot() == EquipmentSlotGroup.OFFHAND
                        && modifier.modifier().amount() > amount) {
                        amount = modifier.modifier().amount();
                        value = -1;
                     }
                  }
               }
            }
         } else {
            for (TypedDataComponent<?> component2 : MinecraftHolder.getLocalPlayer().getInventory().getItem(i).getComponents()) {
               if (Intrinsics.areEqual(component2.type().toString(), "minecraft:attribute_modifiers") && component2.value() instanceof ItemAttributeModifiers) {
                  for (Entry modifier2 : ((ItemAttributeModifiers)component2.value()).modifiers()) {
                     if (KotlinCompat.contains(((Attribute)modifier2.attribute().value()).getDescriptionId(), "attack_damage", false, 2, null)
                        && modifier2.slot() == EquipmentSlotGroup.OFFHAND
                        && modifier2.modifier().amount() > amount) {
                        amount = modifier2.modifier().amount();
                        value = i;
                     }
                  }
               }
            }
         }
      }

      if (value == -1) {
         for (int i2 = 0; i2 < 37; i2++) {
            Iterable components = (Iterable)MinecraftHolder.getLocalPlayer().getInventory().getItem(i2).getComponents();
            boolean flag2;
            if (components instanceof Collection && ((Collection)components).isEmpty()) {
               flag2 = false;
            } else {
               Iterator iterator = components.iterator();

               while (true) {
                  if (!iterator.hasNext()) {
                     flag2 = false;
                     break;
                  }

                  Object object = iterator.next();
                  TypedDataComponent typedDataComponent = (TypedDataComponent)object;
                  if (KotlinCompat.contains(typedDataComponent.toString(), "РЈСЂРѕРЅ V", false, 2, null)) {
                     flag2 = true;
                     break;
                  }
               }
            }

            if (flag2) {
               value = i2;
            }
         }
      }

      return value;
   }

   public final void update3() {
      callback.invoke();
      callback = () -> Unit.INSTANCE;
   }
}