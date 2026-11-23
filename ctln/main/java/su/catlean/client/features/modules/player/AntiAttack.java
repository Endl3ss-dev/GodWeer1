// deobf by pusia
package su.catlean.client.features.modules.player;

import net.minecraft.network.PacketListener;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Criticals;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;

public final class AntiAttack extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AntiAttack.class, "friends", "getFriends()Z", 0), new PropertyReference1Impl(AntiAttack.class, "villagers", "getVillagers()Z", 0), new PropertyReference1Impl(AntiAttack.class, "zoglins", "getZoglins()Z", 0) };
   @NotNull
   public static final AntiAttack INSTANCE = new AntiAttack();
   @NotNull
   private static final BooleanSetting friends = SettingFactory.defaultImpl(INSTANCE, "friends", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting villagers = SettingFactory.defaultImpl(INSTANCE, "villagers", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting zoglins = SettingFactory.defaultImpl(INSTANCE, "zoglins", true, null, null, 12, null);

   private AntiAttack() {
      super("anti-attack", SubCategories.getSubCategory10(), null, 4, null);
   }

   private final boolean getFriends() {
      return (Boolean)friends.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getVillagers() {
      return (Boolean)villagers.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getZoglins() {
      return (Boolean)zoglins.computeObject(this, $$delegatedProperties[2]);
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      Packet<? extends PacketListener> packet = sendPacket.getPacket();
      if (packet instanceof ServerboundAttackPacket) {
         Entity entity = Criticals.INSTANCE.fetchEntity((ServerboundAttackPacket)packet);

         try {
            if (INSTANCE.getFriends() && entity instanceof Player && PlayerNameUtil.checkGetGameProfile((Player)entity)
               || INSTANCE.getVillagers() && entity instanceof Villager
               || INSTANCE.getZoglins()
                  && entity instanceof ZombifiedPiglin
                  && !Intrinsics.areEqual(((ZombifiedPiglin)entity).getTarget(), MinecraftHolder.getLocalPlayer())) {
               sendPacket.cancel();
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   static {
   }
}