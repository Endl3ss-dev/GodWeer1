// deobf by pusia
package su.catlean.client.features.modules.equip;

import java.util.List;
import java.util.Optional;
import kotlin.collections.CollectionsKt;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import net.minecraft.core.component.DataComponentType;

public final class PotionsFormatter {
   @NotNull
   public static final PotionsFormatter INSTANCE = new PotionsFormatter();

   private PotionsFormatter() {
   }

   @NotNull
   public final ItemStack computeItemStack(int value) {
      List<MobEffectInstance> listOf = null;
      Component literal = null;
      switch (value) {
         case -16711936:
            MobEffectInstance[] mobEffectInstances = new MobEffectInstance[]{
               new MobEffectInstance(MobEffects.HEALTH_BOOST, 3600, 1),
               new MobEffectInstance(MobEffects.INVISIBILITY, 18000, 0),
               new MobEffectInstance(MobEffects.REGENERATION, 1200, 1),
               new MobEffectInstance(MobEffects.RESISTANCE, 1200, 0)
            };
            listOf = CollectionsKt.listOf(mobEffectInstances);
            literal = (Component)Component.literal("§2§l[★]§r§a§l Зелье Победителя§r");
            break;
         case -16711681:
            MobEffectInstance[] mobEffectInstances2 = new MobEffectInstance[]{
               new MobEffectInstance(MobEffects.JUMP_BOOST, 3600, 0), new MobEffectInstance(MobEffects.SPEED, 3600, 2)
            };
            listOf = CollectionsKt.listOf(mobEffectInstances2);
            literal = (Component)Component.literal("§3§l[★]§r§b§l Моча Флеша§r");
            break;
         case -6684877:
            MobEffectInstance[] mobEffectInstances3 = new MobEffectInstance[]{
               new MobEffectInstance(MobEffects.POISON, 1000, 1),
               new MobEffectInstance(MobEffects.SLOWNESS, 1800, 3),
               new MobEffectInstance(MobEffects.WEAKNESS, 1800, 2),
               new MobEffectInstance(MobEffects.WITHER, 600, 4)
            };
            listOf = CollectionsKt.listOf(mobEffectInstances3);
            literal = (Component)Component.literal("§2§l[★]§r§a§l Серная кислота§r");
            break;
         case -3407872:
            MobEffectInstance[] mobEffectInstances4 = new MobEffectInstance[]{
               new MobEffectInstance(MobEffects.RESISTANCE, 3600, 0), new MobEffectInstance(MobEffects.STRENGTH, 1800, 3)
            };
            listOf = CollectionsKt.listOf(mobEffectInstances4);
            literal = (Component)Component.literal("§4§l[★]§r§c§l Зелье Киллера§r");
            break;
         case -65281:
            MobEffectInstance[] mobEffectInstances5 = new MobEffectInstance[]{
               new MobEffectInstance(MobEffects.HEALTH_BOOST, 900, 2), new MobEffectInstance(MobEffects.REGENERATION, 900, 2)
            };
            listOf = CollectionsKt.listOf(mobEffectInstances5);
            literal = (Component)Component.literal("§5§l[★]§r§d§l Зелье Медика§r");
            break;
         case -39424:
            MobEffectInstance[] mobEffectInstances6 = new MobEffectInstance[]{
               new MobEffectInstance(MobEffects.BLINDNESS, 200, 0),
               new MobEffectInstance(MobEffects.GLOWING, 3600, 0),
               new MobEffectInstance(MobEffects.HUNGER, 1800, 9),
               new MobEffectInstance(MobEffects.SLOWNESS, 3600, 2),
               new MobEffectInstance(MobEffects.WITHER, 600, 4)
            };
            listOf = CollectionsKt.listOf(mobEffectInstances6);
            literal = (Component)Component.literal("§c§l[★]§r§6§l Зелье Отрыжки§r");
            break;
         case -256:
            MobEffectInstance[] mobEffectInstances7 = new MobEffectInstance[]{
               new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 18000, 0),
               new MobEffectInstance(MobEffects.HASTE, 3600, 0),
               new MobEffectInstance(MobEffects.INVISIBILITY, 18000, 0),
               new MobEffectInstance(MobEffects.SPEED, 18000, 2),
               new MobEffectInstance(MobEffects.STRENGTH, 6000, 2)
            };
            listOf = CollectionsKt.listOf(mobEffectInstances7);
            literal = (Component)Component.literal("§6§l[★]§r§e§l Зелье агента§r");
            break;
         case -1:
            MobEffectInstance[] mobEffectInstances8 = new MobEffectInstance[]{
               new MobEffectInstance(MobEffects.BLINDNESS, 400, 0), new MobEffectInstance(MobEffects.GLOWING, 4800, 0)
            };
            listOf = CollectionsKt.listOf(mobEffectInstances8);
            literal = (Component)Component.literal("§6§l[★]§r§e§l Вспышка§r");
            break;
         default:
            ItemStack itemStack = new ItemStack((ItemLike)Items.SPLASH_POTION);
            itemStack.set(DataComponents.CUSTOM_NAME, Component.literal("§7Неизвестное зелье"));
            return itemStack;
      }

      ItemStack itemStack2 = new ItemStack((ItemLike)Items.SPLASH_POTION);
      itemStack2.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.empty(), (List)listOf, Optional.empty()));
      itemStack2.set(DataComponents.CUSTOM_NAME, literal);
      return itemStack2;
   }
}