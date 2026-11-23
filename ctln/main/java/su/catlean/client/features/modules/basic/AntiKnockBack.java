// deobf by pusia
package su.catlean.client.features.modules.basic;

import net.minecraft.network.PacketListener;
import java.util.Optional;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.PosRot;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ClientboundExplodePacketAccessor;
import su.catlean.mixins.accessors.ClientboundSetEntityMotionPacketAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class AntiKnockBack extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AntiKnockBack.class, "mode", "getMode()Lsu/catlean/yi;", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "oldBreak", "getOldBreak()Z", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "pauseInWater", "getPauseInWater()Z", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "explosions", "getExplosions()Z", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "cc", "getCc()Z", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "onlyWalls", "getOnlyWalls()Z", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "onlyGround", "getOnlyGround()Z", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "notMoving", "getNotMoving()Z", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "ignoreRotations", "getIgnoreRotations()Z", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "vertical", "getVertical()F", 0), new MutablePropertyReference1Impl(AntiKnockBack.class, "horizontal", "getHorizontal()F", 0) };
   @NotNull
   public static final AntiKnockBack INSTANCE = new AntiKnockBack();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", AntiKnockBack.Mode.GrimNew, null, null, 12, null);
   @NotNull
   private static final BooleanSetting oldBreak = SettingFactory.defaultImpl(
      INSTANCE, "old-break", false, null, () -> INSTANCE.getMode() == AntiKnockBack.Mode.GrimNew, 4, null
   );
   @NotNull
   private static final BooleanSetting pauseInLiquids = SettingFactory.defaultImpl(INSTANCE, "pause-in-liquids", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting explosions = SettingFactory.defaultImpl(INSTANCE, "explosions", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting pauseOnFlag = SettingFactory.defaultImpl(INSTANCE, "pause-on-flag", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyWalls = SettingFactory.defaultImpl(INSTANCE, "only-walls", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyGround = SettingFactory.defaultImpl(INSTANCE, "only-ground", false, null, () -> INSTANCE.getOnlyWalls(), 4, null);
   @NotNull
   private static final BooleanSetting notMoving = SettingFactory.defaultImpl(INSTANCE, "not-moving", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting ignoreRotations = SettingFactory.defaultImpl(
      INSTANCE, "ignore-rotations", false, null, () -> INSTANCE.getNotMoving(), 4, null
   );
   @NotNull
   private static final FloatSetting vertical = SettingFactory.defaultImpl(
      INSTANCE, "vertical", 0.0F, RangesKt.rangeTo(0.0F, 100.0F), null, () -> INSTANCE.getMode() == AntiKnockBack.Mode.Custom, 8, null
   );
   @NotNull
   private static final FloatSetting horizontal = SettingFactory.defaultImpl(
      INSTANCE, "horizontal", 0.0F, RangesKt.rangeTo(0.0F, 100.0F), null, () -> INSTANCE.getMode() == AntiKnockBack.Mode.Custom, 8, null
   );
   private static boolean flag;
   private static boolean flag2;
   private static int value;

   private AntiKnockBack() {
      super("anti-knock-back", SubCategories.getSubCategory4(), null, 4, null);
   }

   private final AntiKnockBack.Mode getMode() {
      return (AntiKnockBack.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getOldBreak() {
      return (Boolean)oldBreak.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getPauseInWater() {
      return (Boolean)pauseInLiquids.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getExplosions() {
      return (Boolean)explosions.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getOnlyWalls() {
      return (Boolean)onlyWalls.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getOnlyGround() {
      return (Boolean)onlyGround.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getNotMoving() {
      return (Boolean)notMoving.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getIgnoreRotations() {
      return (Boolean)ignoreRotations.computeObject(this, $$delegatedProperties[8]);
   }

   private final float getVertical() {
      return ((Number)vertical.computeObject(this, $$delegatedProperties[9])).floatValue();
   }

   private final float getHorizontal() {
      return ((Number)horizontal.computeObject(this, $$delegatedProperties[10])).floatValue();
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      Packet<? extends PacketListener> packet = receivePacket.getPacket();
      if (packet instanceof ClientboundSetEntityMotionPacket && ((ClientboundSetEntityMotionPacket)packet).id() == MinecraftHolder.getLocalPlayer().getId()) {
         if (!this.isFlag()) {
            return;
         }

         switch (AntiKnockBack.WhenMappings.values[this.getMode().ordinal()]) {
            case 1:
               ((ClientboundSetEntityMotionPacketAccessor)packet)
                  .setMovement(
                     new Vec3(
                        ((ClientboundSetEntityMotionPacket)packet).movement().x * this.getHorizontal() / 100.0,
                        ((ClientboundSetEntityMotionPacket)packet).movement().y * this.getVertical() / 100.0,
                        ((ClientboundSetEntityMotionPacket)packet).movement().z * this.getHorizontal() / 100.0
                     )
                  );
               break;
            case 2:
               receivePacket.cancel();
               break;
            case 3:
               receivePacket.cancel();
               value = 6;
               break;
            case 4:
               if (WorldUtil.INSTANCE.getTime4() > 200L) {
                  receivePacket.cancel();
                  flag = true;
               }
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }

      if (packet instanceof ClientboundExplodePacket && this.getExplosions() && !WindHop.INSTANCE.getEnabled()) {
         ClientboundExplodePacketAccessor packet2 = (ClientboundExplodePacketAccessor)receivePacket.getPacket();
         if (!this.isFlag()
            || !((ClientboundExplodePacket)packet).playerKnockback().isPresent()
            || ((Vec3)((ClientboundExplodePacket)packet).playerKnockback().get()).x == 0.0
               && ((Vec3)((ClientboundExplodePacket)packet).playerKnockback().get()).y == 0.0
               && ((Vec3)((ClientboundExplodePacket)packet).playerKnockback().get()).z == 0.0) {
            return;
         }

         switch (AntiKnockBack.WhenMappings.values[this.getMode().ordinal()]) {
            case 1:
               if (((ClientboundExplodePacket)packet).playerKnockback().isPresent()) {
                  Vec3 playerKnockbackItem = (Vec3)((ClientboundExplodePacket)packet).playerKnockback().get();
                  packet2.setPlayerKnockback(
                     Optional.of(
                        new Vec3(playerKnockbackItem.x * this.getHorizontal() / 100.0F, playerKnockbackItem.z * this.getVertical() / 100.0F, playerKnockbackItem.y * this.getHorizontal() / 100.0F)
                     )
                  );
               }
               break;
            case 2:
               packet2.setPlayerKnockback(Optional.of(Vec3.ZERO));
            case 3:
               break;
            case 4:
               if (WorldUtil.INSTANCE.getTime4() > 200L) {
                  packet2.setPlayerKnockback(Optional.of(Vec3.ZERO));
                  flag = true;
               }
               break;
            default:
               throw new NoWhenBranchMatchedException();
         }
      }

      if (this.getMode() == AntiKnockBack.Mode.OldGrim && packet instanceof ClientboundPingPacket && value > 0) {
         receivePacket.cancel();
         int value2 = value;
         value = value2 + -1;
      }

      if (packet instanceof ClientboundPlayerPositionPacket) {
         flag2 = false;
      }
   }

   private final boolean isFlag() {
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      if (MinecraftHolder.getClientLevel().getBlockState(containing).getBlock() instanceof AirBlock
         || this.getOnlyGround() && !MinecraftHolder.getLocalPlayer().onGround()) {
         if (this.getNotMoving() && !WorldUtil.INSTANCE.isFlag4()) {
            flag2 = true;
            return true;
         } else {
            return (
                     MinecraftHolder.getLocalPlayer().isInWater()
                        || MinecraftHolder.getLocalPlayer().isUnderWater()
                        || MinecraftHolder.getLocalPlayer().isInLava()
                  )
                  && this.getPauseInWater()
               ? false
               : !this.getNotMoving() && !this.getOnlyWalls();
         }
      } else {
         return true;
      }
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (!MinecraftHolder.getLocalPlayer().isInWater() && !MinecraftHolder.getLocalPlayer().isUnderWater() || !this.getPauseInWater()) {
         if (WorldUtil.INSTANCE.isFlag4()) {
            flag2 = false;
         }

         if (AntiKnockBack.WhenMappings.values[this.getMode().ordinal()] == 4 && flag) {
            BlockPos above = !MinecraftHolder.getLocalPlayer().isVisuallyCrawling() && !this.getOldBreak()
               ? MinecraftHolder.getLocalPlayer().blockPosition().above()
               : BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
            if (MinecraftHolder.getClientLevel().isEmptyBlock(above)) {
               Packet posRot = (Packet)(
                  new PosRot(
                     MinecraftHolder.getLocalPlayer().getX(),
                     MinecraftHolder.getLocalPlayer().getY(),
                     MinecraftHolder.getLocalPlayer().getZ(),
                     WorldUtil.INSTANCE.getAmount4(),
                     WorldUtil.INSTANCE.getAmount5(),
                     MinecraftHolder.getLocalPlayer().onGround(),
                     MinecraftHolder.getLocalPlayer().horizontalCollision
                  )
               );
               PacketUtil.applySend(posRot);
               PacketUtil.applyCurrentSequence(
                  arg -> (Packet)(
                     new ServerboundPlayerActionPacket(
                        Action.STOP_DESTROY_BLOCK,
                        !MinecraftHolder.getLocalPlayer().isVisuallyCrawling() && !INSTANCE.getOldBreak()
                           ? MinecraftHolder.getLocalPlayer().blockPosition().above()
                           : BlockPos.containing((Position)INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer())),
                        Direction.DOWN,
                        arg
                     )
                  )
               );
            }

            flag = false;
         }

         if (value > 0) {
            int value2 = value;
            value = value2 + -1;
         }
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (this.getNotMoving() && flag2 && sendPacket.getPacket() instanceof ServerboundMovePlayerPacket) {
         sendPacket.cancel();
      }
   }

   public final void update() {
      if (this.getIgnoreRotations()) {
         flag2 = false;
      }
   }

   @Override
   public void update2() {
      value = 0;
   }

   static {
   }

   public static enum Mode {
      Cancel,
      Custom,
      OldGrim,
      GrimNew;

      private static final AntiKnockBack.Mode[] modes = $values();
      private static final EnumEntries<AntiKnockBack.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<AntiKnockBack.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final AntiKnockBack.Mode[] $values() {
         return new AntiKnockBack.Mode[]{Cancel, Custom, OldGrim, GrimNew};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[AntiKnockBack.Mode.values().length];

         try {
            values2[AntiKnockBack.Mode.Custom.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[AntiKnockBack.Mode.Cancel.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[AntiKnockBack.Mode.OldGrim.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[AntiKnockBack.Mode.GrimNew.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         values = values2;
      }
   }
}