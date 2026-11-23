// deobf by pusia
package su.catlean.client.features.modules.player;

import java.awt.Color;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class AirPlace extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AirPlace.class, "bypass", "getBypass()Z", 0), new PropertyReference1Impl(AirPlace.class, "range", "getRange()F", 0), new PropertyReference1Impl(AirPlace.class, "color", "getColor()Ljava/awt/Color;", 0) };
   @NotNull
   public static final AirPlace INSTANCE = new AirPlace();
   @NotNull
   private static final BooleanSetting b2t2 = SettingFactory.defaultImpl(INSTANCE, "2b2t", true, null, null, 12, null);
   @NotNull
   private static final FloatSetting range = SettingFactory.defaultImpl(INSTANCE, "range", 5.0F, RangesKt.rangeTo(0.0F, 6.0F), null, null, 24, null);
   @NotNull
   private static final ColorSetting colorSetting = SettingFactory.defaultImpl(INSTANCE, "fill--color", new Color(100, 50, 255, 50), null, null, 12, null);
   @Nullable
   private static BlockHitResult blockHitResult;
   private static int value;

   private AirPlace() {
      super("air-place", SubCategories.getSubCategory10(), null, 4, null);
   }

   public final boolean getBypass() {
      return (Boolean)b2t2.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getRange() {
      return ((Number)range.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final Color getColor() {
      return (Color)colorSetting.computeObject(this, $$delegatedProperties[2]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (value > 0) {
         int value2 = value;
         value = value2 + -1;
      }

      HitResult pick = MinecraftHolder.getMinecraft().getCameraEntity().pick(this.getRange(), 0.0F, false);
      if (pick instanceof BlockHitResult) {
         blockHitResult = (BlockHitResult)pick;
         if (MinecraftHolder.getClientLevel().getBlockState(blockHitResult.getBlockPos()).getBlock().equals(Blocks.AIR)) {
            boolean flag = MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof BlockItem;
            boolean flag2 = MinecraftHolder.getLocalPlayer().getOffhandItem().getItem() instanceof BlockItem;
            if (MinecraftHolder.getMinecraft().options.keyUse.isDown() && (flag || flag2) && value <= 0) {
               if (this.getBypass()) {
                  ContainerUtil.defaultImpl(flag ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, 0.0F, 0.0F, 6, null);
                  Packet serverboundPlayerActionPacket = (Packet)(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                  PacketUtil.applySend(serverboundPlayerActionPacket);
                  MinecraftHolder.getMultiPlayerGameMode()
                     .useItemOn(MinecraftHolder.getLocalPlayer(), flag ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, blockHitResult);
                  MinecraftHolder.getLocalPlayer().swing(flag ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
                  serverboundPlayerActionPacket = (Packet)(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                  PacketUtil.applySend(serverboundPlayerActionPacket);
                  MinecraftHolder.getLocalPlayer().swing(flag ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
               } else {
                  MinecraftHolder.getMultiPlayerGameMode()
                     .useItemOn(MinecraftHolder.getLocalPlayer(), flag ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, blockHitResult);
                  MinecraftHolder.getLocalPlayer().swing(flag ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
               }

               value = !NoDelay.INSTANCE.getEnabled() || !NoDelay.INSTANCE.getBlocks() && !NoDelay.INSTANCE.getAll() ? 4 : 0;
            }
         }
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (blockHitResult != null) {
         BlockHitResult blockHitResult2 = blockHitResult;
         boolean flag = MinecraftHolder.getLocalPlayer().getMainHandItem().getItem() instanceof BlockItem;
         boolean flag2 = MinecraftHolder.getLocalPlayer().getOffhandItem().getItem() instanceof BlockItem;
         AABB aabb = new AABB(blockHitResult2.getBlockPos());
         if (!MinecraftHolder.getClientLevel().getBlockState(blockHitResult2.getBlockPos()).getBlock().equals(Blocks.AIR) || !flag && !flag2) {
            return;
         }

         Render3D.defaultImpl2(Render3D.INSTANCE, aabb, INSTANCE.getColor(), null, 4, null);
         Render3D.defaultImpl3(Render3D.INSTANCE, aabb, Render2D.INSTANCE.computeColor(INSTANCE.getColor(), 1.0F), null, 4, null);
      }
   }

   static {
   }
}