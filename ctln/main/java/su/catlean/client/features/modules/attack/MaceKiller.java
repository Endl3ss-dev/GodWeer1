// deobf by pusia
package su.catlean.client.features.modules.attack;

import net.minecraft.network.PacketListener;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.StatusOnly;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;

public final class MaceKiller extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(MaceKiller.class, "height", "getHeight()I", 0)};
   @NotNull
   public static final MaceKiller INSTANCE = new MaceKiller();
   @NotNull
   private static final IntSetting height = SettingFactory.defaultImpl(INSTANCE, "height", 20, new IntRange(0, 200), null, null, 24, null);
   private static int value = -1;

   private MaceKiller() {
      super("mace-killer", SubCategories.getSubCategory2(), null, 4, null);
   }

   private final int getHeight() {
      return ((Number)height.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      Packet<? extends PacketListener> packet = sendPacket.getPacket();
      if (packet instanceof ServerboundAttackPacket) {
         Entity entity = Criticals.INSTANCE.fetchEntity((ServerboundAttackPacket)packet);
         if (entity != null && !(entity instanceof EndCrystal)) {
            if (entity instanceof Player && ((Player)entity).isBlocking()) {
               return;
            }

            SearchInvResult inventory = InventoryManager.INSTANCE.fetchInventory(arg -> Intrinsics.areEqual(arg.getItem(), Items.MACE));
            if (inventory.isFlag()) {
               if (MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() != inventory.getValue()) {
                  inventory.update();
                  value = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
               }

               Vec3 vec3 = INSTANCE.getVec3();
               double distanceTo = INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(vec3);
               if (INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(vec3) > 10.0) {
                  int value2 = (int)Math.ceil(Math.abs(distanceTo / 10.0));

                  for (int i = 0; i < value2; i++) {
                     Packet statusOnly = (Packet)(new StatusOnly(false, MinecraftHolder.getLocalPlayer().horizontalCollision));
                     PacketUtil.applySend(statusOnly);
                  }
               }

               Packet pos = (Packet)(new Pos(vec3.x, vec3.y, vec3.z, false, MinecraftHolder.getLocalPlayer().horizontalCollision));
               PacketUtil.applySend(pos);
               pos = (Packet)(
                  new Pos(
                     MinecraftHolder.getLocalPlayer().getX(),
                     MinecraftHolder.getLocalPlayer().getY(),
                     MinecraftHolder.getLocalPlayer().getZ(),
                     false,
                     MinecraftHolder.getLocalPlayer().horizontalCollision
                  )
               );
               PacketUtil.applySend(pos);
            }
         }
      }
   }

   @Flow
   private final void onAfterSendPacket(AfterSendPacket afterSendPacket) {
      Packet<? extends PacketListener> packet = afterSendPacket.getPacket();
      if (packet instanceof ServerboundAttackPacket && value != -1) {
         InventoryManager.INSTANCE.applyGetInventory2(value);
         value = -1;
      }
   }

   private final Vec3 getVec3() {
      Vec3 vec3 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
      int value2 = 2;
      int height2 = this.getHeight() / 2;
      if (value2 <= height2) {
         while (true) {
            CrystalPredictor instance = CrystalPredictor.INSTANCE;
            Vec3 vec32 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
            Vec3 vec33 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(0.0, value2 * 1.8, 0.0);
            Object object = null;
            byte byteValue = 28;
            Object object2 = null;
            Object object3 = null;
            boolean flag = false;
            Vec3 vec34 = vec33;
            if (CrystalPredictor.defaultImpl(instance, vec32, vec34, flag, (List)object3, (List)object2, byteValue, object) != null) {
               break;
            }

            vec3 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(0.0, (value2 - 1) * 1.8, 0.0);
            if (value2 == height2) {
               break;
            }

            value2++;
         }
      }

      return vec3;
   }

   static {
   }
}