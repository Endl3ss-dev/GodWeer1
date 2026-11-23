// deobf by pusia
package su.catlean.client.features.modules.legit;

import java.util.Collection;
import java.util.Iterator;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SplashPotionItem;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.misc.AutoSoup;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.utils.world.WorldUtil;

public final class AutoBuff extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoBuff.class, "strength", "getStrength()Z", 0), new PropertyReference1Impl(AutoBuff.class, "speedPotion", "getSpeedPotion()Z", 0), new PropertyReference1Impl(AutoBuff.class, "fire", "getFire()Z", 0), new PropertyReference1Impl(AutoBuff.class, "heal", "getHeal()Z", 0), new PropertyReference1Impl(AutoBuff.class, "healthH", "getHealthH()I", 0), new PropertyReference1Impl(AutoBuff.class, "regen", "getRegen()Z", 0), new PropertyReference1Impl(AutoBuff.class, "triggerOn", "getTriggerOn()Lsu/catlean/fd;", 0), new PropertyReference1Impl(AutoBuff.class, "healthR", "getHealthR()I", 0), new PropertyReference1Impl(AutoBuff.class, "potionsPerTick", "getPotionsPerTick()I", 0), new MutablePropertyReference1Impl(AutoBuff.class, "switchMode", "getSwitchMode()Lsu/catlean/uv;", 0) };
   @NotNull
   public static final AutoBuff INSTANCE = new AutoBuff();
   @NotNull
   private static final BooleanSetting strength = SettingFactory.defaultImpl(INSTANCE, "strength", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting speed = SettingFactory.defaultImpl(INSTANCE, "speed", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting fireResistance = SettingFactory.defaultImpl(INSTANCE, "fire-resistance", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting instantHealing = SettingFactory.defaultImpl(INSTANCE, "instant-healing", true, null, null, 12, null);
   @NotNull
   private static final IntSetting health = SettingFactory.defaultImpl(INSTANCE, "health", 8, new IntRange(0, 20), null, null, 24, null);
   @NotNull
   private static final BooleanSetting regeneration = SettingFactory.defaultImpl(INSTANCE, "regeneration", true, null, null, 12, null);
   @NotNull
   private static final EnumSetting trigger = SettingFactory.defaultImpl(INSTANCE, "trigger", AutoBuff.Trigger.LackOfRegen, null, null, 12, null);
   @NotNull
   private static final IntSetting hp = SettingFactory.defaultImpl(
      INSTANCE, "hp", 8, new IntRange(0, 20), null, () -> INSTANCE.getTriggerOn() == AutoBuff.Trigger.Health, 8, null
   );
   @NotNull
   private static final IntSetting potionsPerTick = SettingFactory.defaultImpl(INSTANCE, "potions-per-tick", 3, new IntRange(1, 3), null, null, 24, null);
   @NotNull
   private static final EnumSetting switchValue = SettingFactory.defaultImpl(INSTANCE, "switch", AutoSoup.Switch.SILENT_FULL, null, null, 12, null);
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();
   private static int value;

   private AutoBuff() {
      super("auto-buff", SubCategories.getSubCategory3(), null, 4, null);
   }

   private final boolean getStrength() {
      return (Boolean)strength.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getSpeedPotion() {
      return (Boolean)speed.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getFire() {
      return (Boolean)fireResistance.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getHeal() {
      return (Boolean)instantHealing.computeObject(this, $$delegatedProperties[3]);
   }

   private final int getHealthH() {
      return ((Number)health.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final boolean getRegen() {
      return (Boolean)regeneration.computeObject(this, $$delegatedProperties[5]);
   }

   private final AutoBuff.Trigger getTriggerOn() {
      return (AutoBuff.Trigger)trigger.computeObject(this, $$delegatedProperties[6]);
   }

   private final int getHealthR() {
      return ((Number)hp.computeObject(this, $$delegatedProperties[7])).intValue();
   }

   private final int getPotionsPerTick() {
      return ((Number)potionsPerTick.computeObject(this, $$delegatedProperties[8])).intValue();
   }

   private final AutoSoup.Switch getSwitchMode() {
      return (AutoSoup.Switch)switchValue.computeObject(this, $$delegatedProperties[9]);
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      value = 0;
      if (this.isFlag()) {
         RotationManager instance = RotationManager.INSTANCE;
         Rotation rotation = new Rotation(WorldUtil.INSTANCE.getAmount4(), 90.0F, false, null, 12, null);
         Function0 function0 = () -> {
            INSTANCE.applyHasEffect();
            return Unit.INSTANCE;
         };
         short shortValue = 200;
         Rotation rotation2 = rotation;
         instance.updateRotationAction(new RotationAction(rotation2, shortValue, function0));
      }
   }

   private final void applyHasEffect() {
      LocalPlayer localPlayer = MinecraftHolder.getLocalPlayer();
      if (!localPlayer.hasEffect(MobEffects.SPEED) && INSTANCE.getSpeedPotion() && value < INSTANCE.getPotionsPerTick()) {
         INSTANCE.updateHolder(MobEffects.SPEED);
      }

      if (!localPlayer.hasEffect(MobEffects.STRENGTH) && INSTANCE.getStrength() && value < INSTANCE.getPotionsPerTick()) {
         INSTANCE.updateHolder(MobEffects.STRENGTH);
      }

      if (!localPlayer.hasEffect(MobEffects.FIRE_RESISTANCE) && INSTANCE.getFire() && value < INSTANCE.getPotionsPerTick()) {
         INSTANCE.updateHolder(MobEffects.FIRE_RESISTANCE);
      }

      if (localPlayer.getHealth() + localPlayer.getAbsorptionAmount() < INSTANCE.getHealthH() && INSTANCE.getHeal() && value < INSTANCE.getPotionsPerTick()) {
         INSTANCE.updateHolder(MobEffects.INSTANT_HEALTH);
      }

      if ((
            !localPlayer.hasEffect(MobEffects.REGENERATION) && INSTANCE.getTriggerOn() == AutoBuff.Trigger.LackOfRegen
               || localPlayer.getHealth() + localPlayer.getAbsorptionAmount() < INSTANCE.getHealthR() && INSTANCE.getTriggerOn() == AutoBuff.Trigger.Health
         )
         && INSTANCE.getRegen()
         && value < INSTANCE.getPotionsPerTick()) {
         INSTANCE.updateHolder(MobEffects.REGENERATION);
      }

      playerUtil.update();
   }

   private final void updateHolder(Holder holder) {
      int value2 = this.computeValue(holder);
      if (value2 != -1) {
         InventoryManager.INSTANCE.applyGetInventory(value2, this.getSwitchMode(), () -> {
            ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
            Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
            PacketUtil.applySend(serverboundSwingPacket);
            int value3 = value++;
            return Unit.INSTANCE;
         });
      }
   }

   private final int computeValue(Holder holder) {
      return InventoryManager.INSTANCE.computeSearchInvResult(arg -> INSTANCE.checkGetAllEffects(arg, holder), this.getSwitchMode()).getValue();
   }

   private final boolean checkHasEffect(Holder holder) {
      return !MinecraftHolder.getLocalPlayer().hasEffect(holder) && this.computeValue(holder) != -1;
   }

   private final boolean checkGetAllEffects(ItemStack itemStack, Holder holder) {
      if ((itemStack != null ? itemStack.getItem() : null) instanceof SplashPotionItem) {
         Iterable<MobEffectInstance> allEffects = ((PotionContents)itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)).getAllEffects();
         boolean flag;
         if (allEffects instanceof Collection && ((Collection)allEffects).isEmpty()) {
            flag = false;
         } else {
            Iterator<MobEffectInstance> iterator = allEffects.iterator();

            while (true) {
               if (!iterator.hasNext()) {
                  flag = false;
                  break;
               }

               MobEffectInstance object = iterator.next();
               MobEffectInstance mobEffectInstance = (MobEffectInstance)object;
               if (Intrinsics.areEqual(mobEffectInstance.getEffect(), holder)) {
                  flag = true;
                  break;
               }
            }
         }

         if (flag) {
            return true;
         }
      }

      return false;
   }

   private final boolean isFlag() {
      if (playerUtil.testValue(6 + (int)Math.floor(ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null) / 50.0))
         && MinecraftHolder.getLocalPlayer().tickCount > 100) {
         if (Aura.INSTANCE.getEnabled() && Aura.INSTANCE.getEntity() != null && MinecraftHolder.getLocalPlayer().getAttackStrengthScale(1.0F) > 0.5F) {
            return false;
         } else {
            return MinecraftHolder.getLocalPlayer().isFallFlying()
               ? false
               : this.checkHasEffect(MobEffects.SPEED) && this.getSpeedPotion()
                  || this.checkHasEffect(MobEffects.STRENGTH) && this.getStrength()
                  || this.checkHasEffect(MobEffects.FIRE_RESISTANCE) && this.getFire()
                  || MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount() < this.getHealthH()
                     && this.checkHasEffect(MobEffects.INSTANT_HEALTH)
                     && this.getHeal()
                  || this.getTriggerOn() == AutoBuff.Trigger.LackOfRegen && this.checkHasEffect(MobEffects.REGENERATION) && this.getRegen()
                  || MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount() < this.getHealthR()
                     && this.getTriggerOn() == AutoBuff.Trigger.Health
                     && this.checkHasEffect(MobEffects.REGENERATION)
                     && this.getRegen();
         }
      } else {
         return false;
      }
   }

   static {
   }

   public static enum Trigger {
      LackOfRegen,
      Health;

      private static final AutoBuff.Trigger[] triggers = $values();
      private static final EnumEntries<AutoBuff.Trigger> enumEntries = EnumEntriesKt.enumEntries(triggers);

      private Trigger() {
      }

      @NotNull
      public static EnumEntries<AutoBuff.Trigger> getEnumEntries() {
         return enumEntries;
      }

      private static final AutoBuff.Trigger[] $values() {
         return new AutoBuff.Trigger[]{LackOfRegen, Health};
      }
   }
}