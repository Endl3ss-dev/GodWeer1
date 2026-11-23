// deobf by pusia
package su.catlean.client.features.modules.attack;

import net.minecraft.network.PacketListener;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class MoreKnockback extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(MoreKnockback.class, "inMove", "getInMove()Z", 0), new MutablePropertyReference1Impl(MoreKnockback.class, "chance", "getChance()I", 0) };
   @NotNull
   public static final MoreKnockback INSTANCE = new MoreKnockback();
   @NotNull
   private static final BooleanSetting allowWhileMove = SettingFactory.defaultImpl(INSTANCE, "allow-while-move", true, null, null, 12, null);
   @NotNull
   private static final IntSetting chance = SettingFactory.defaultImpl(INSTANCE, "chance", 100, new IntRange(0, 100), null, null, 24, null);

   private MoreKnockback() {
      super("more-knockback", SubCategories.getSubCategory2(), null, 4, null);
   }

   private final boolean getInMove() {
      return (Boolean)allowWhileMove.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getChance() {
      return ((Number)chance.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      Packet<? extends PacketListener> packet = sendPacket.getPacket();
      if (packet instanceof ServerboundAttackPacket) {
         Entity entity = Criticals.INSTANCE.fetchEntity((ServerboundAttackPacket)packet);
         if ((!WorldUtil.INSTANCE.isFlag4() || this.getInMove())
            && packet instanceof ServerboundAttackPacket
            && !(entity instanceof EndCrystal)
            && entity instanceof LivingEntity
            && MathUtil.defaultImpl(0, 100, false, 4, null) >= 100 - this.getChance()
            && !this.isFlag()) {
            if (MinecraftHolder.getLocalPlayer().isSprinting()) {
               Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.STOP_SPRINTING));
               PacketUtil.applySend(serverboundPlayerCommandPacket);
            }

            Packet serverboundPlayerCommandPacket2 = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_SPRINTING));
            PacketUtil.applySend(serverboundPlayerCommandPacket2);
            MinecraftHolder.getLocalPlayer().setSprinting(true);
            MinecraftHolder.getLocalPlayer().wasSprinting = true;
         }
      }
   }

   private final boolean isFlag() {
      boolean flag = MinecraftHolder.getLocalPlayer().getAbilities().flying
         || MinecraftHolder.getLocalPlayer().isFallFlying()
         || MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.BLINDNESS)
         || Intrinsics.areEqual(
            MinecraftHolder.getClientLevel()
               .getBlockState(BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())))
               .getBlock(),
            Blocks.COBWEB
         )
         || MinecraftHolder.getLocalPlayer().isInLava()
         || MinecraftHolder.getLocalPlayer().isUnderWater();
      if (MinecraftHolder.getLocalPlayer().getAttackStrengthScale(0.5F) < 0.9F) {
         return false;
      } else {
         return flag ? false : !MinecraftHolder.getLocalPlayer().onGround() && MinecraftHolder.getLocalPlayer().fallDistance > 0.0;
      }
   }

   static {
   }
}