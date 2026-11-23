// deobf by pusia
package su.catlean.client.features.modules.protect;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;

public final class Burrow extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(Burrow.class, "once", "getOnce()Z", 0)};
   @NotNull
   public static final Burrow INSTANCE = new Burrow();
   @NotNull
   private static final BooleanSetting once = SettingFactory.defaultImpl(INSTANCE, "once", true, null, null, 12, null);
   @Nullable
   private static BlockPos blockPos;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();

   private Burrow() {
      super("burrow", SubCategories.getSubCategory(), null, 4, null);
   }

   private final boolean getOnce() {
      return (Boolean)once.computeObject(this, $$delegatedProperties[0]);
   }

   @Override
   public void update2() {
      blockPos = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      if (!this.getOnce() && !Intrinsics.areEqual(containing, blockPos)) {
         this.applyUnplug();
      } else {
         InventoryManager instance = InventoryManager.INSTANCE;
         Block[] blocks = new Block[]{Blocks.OBSIDIAN};
         SearchInvResult searchInvResult = instance.computeSearchInvResult(blocks);
         instance = InventoryManager.INSTANCE;
         Block[] blocks2 = new Block[]{Blocks.ENDER_CHEST};
         SearchInvResult searchInvResult2 = instance.computeSearchInvResult(blocks2);
         int value = !searchInvResult.isFlag() ? searchInvResult2.getValue() : searchInvResult.getValue();
         if (value != -1 && !MinecraftHolder.getMinecraft().isLocalServer()) {
            if (stopWatch.testValue(1000)) {
               float xRot = MinecraftHolder.getLocalPlayer().getXRot();
               float value2 = 90 - xRot;
               this.applyGetX(0.42, xRot + value2 * 0.25F);
               this.applyGetX(0.75, xRot + value2 * 0.5F);
               this.applyGetX(1.01, xRot + value2 * 0.75F);
               this.applyGetX(1.16, xRot + value2);
               InventoryManager.INSTANCE.updateValue(value);
               PacketUtil.applyCurrentSequence(
                  arg -> (Packet)(
                     new ServerboundUseItemOnPacket(
                        InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3iUtil.computeVec3(containing).add(0.0, -0.5, 0.0), Direction.UP, containing.below(), false),
                        arg
                     )
                  )
               );
               Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
               PacketUtil.applySend(serverboundSwingPacket);
               InventoryManager.INSTANCE.update();
               serverboundSwingPacket = (Packet)(
                  new PosRot(
                     MinecraftHolder.getLocalPlayer().getX(),
                     MinecraftHolder.getLocalPlayer().getY() + 1.0F,
                     MinecraftHolder.getLocalPlayer().getZ(),
                     MinecraftHolder.getLocalPlayer().getYRot(),
                     90.0F,
                     true,
                     MinecraftHolder.getLocalPlayer().horizontalCollision
                  )
               );
               PacketUtil.applySend(serverboundSwingPacket);
               stopWatch.update();
               if (this.getOnce()) {
                  this.applyUnplug();
               }
            }
         } else {
            this.applyUnplug();
         }
      }
   }

   private final void applyGetX(double amount, float amount2) {
      Packet posRot = (Packet)(
         new PosRot(
            MinecraftHolder.getLocalPlayer().getX(),
            MinecraftHolder.getLocalPlayer().getY() + amount,
            MinecraftHolder.getLocalPlayer().getZ(),
            MinecraftHolder.getLocalPlayer().getYRot(),
            amount2,
            false,
            MinecraftHolder.getLocalPlayer().horizontalCollision
         )
      );
      PacketUtil.applySend(posRot);
   }

   static {
   }
}