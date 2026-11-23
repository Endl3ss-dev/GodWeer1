// deobf by pusia
package su.catlean.client.features.modules.rage;

import net.minecraft.network.PacketListener;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.optionals.OptionalsKt;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.player.ClientInput;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.events.client.TickEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerTargetUtil;
import su.catlean.utils.render.Render3D;

public final class Blink extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(Blink.class, "limit", "getLimit()I", 0), new MutablePropertyReference1Impl(Blink.class, "attackAction", "getAttackAction()Lsu/catlean/y;", 0), new MutablePropertyReference1Impl( Blink.class, "limitAction", "getLimitAction()Lsu/catlean/client/features/modules/movement/rage/blink/Blink$LimitAction;", 0 ), new PropertyReference1Impl(Blink.class, "cancel", "getCancel()Lsu/catlean/dd;", 0), new PropertyReference1Impl(Blink.class, "showPosition", "getShowPosition()Z", 0), new PropertyReference1Impl(Blink.class, "delayOnlyMove", "getDelayOnlyMove()Z", 0), new PropertyReference1Impl(Blink.class, "ignoreInventoryActions", "getIgnoreInventoryActions()Z", 0), new PropertyReference1Impl(Blink.class, "onlyInRange", "getOnlyInRange()Z", 0), new PropertyReference1Impl(Blink.class, "range", "getRange()F", 0), new PropertyReference1Impl(Blink.class, "resetOn", "getResetOn()Lsu/catlean/nl;", 0), new PropertyReference1Impl(Blink.class, "velocityReset", "getVelocityReset()Z", 0), new PropertyReference1Impl(Blink.class, "lagBackReset", "getLagBackReset()Z", 0), new PropertyReference1Impl(Blink.class, "openScreenReset", "getOpenScreenReset()Z", 0) };
   @NotNull
   public static final Blink INSTANCE = new Blink();
   @NotNull
   private static final IntSetting limit = SettingFactory.defaultImpl(INSTANCE, "limit", 15, new IntRange(1, 600), null, null, 24, null);
   @NotNull
   private static final EnumSetting attackAction = SettingFactory.defaultImpl(INSTANCE, "attack-action", Blink.AttackAction.NONE, null, null, 12, null);
   @NotNull
   private static final EnumSetting limitAction = SettingFactory.defaultImpl(INSTANCE, "limit-action", Blink.AttackAction.NONE, null, null, 12, null);
   @NotNull
   private static final BindSetting cancelButton = SettingFactory.defaultImpl(
      INSTANCE, "cancel-button", new Bind(340, false, false, 6, null), null, null, 12, null
   );
   @NotNull
   private static final BooleanSetting showPosition = SettingFactory.defaultImpl(INSTANCE, "show-position", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting delayOnlyMove = SettingFactory.defaultImpl(INSTANCE, "delay-only-move", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting ignoreInventoryActions = SettingFactory.defaultImpl(
      INSTANCE, "ignore-inventory-actions", true, null, () -> !INSTANCE.getDelayOnlyMove(), 4, null
   );
   @NotNull
   private static final BooleanSetting onlyInRange = SettingFactory.defaultImpl(INSTANCE, "only-in-range", false, null, null, 12, null);
   @NotNull
   private static final FloatSetting range = SettingFactory.defaultImpl(
      INSTANCE, "range", 5.0F, RangesKt.rangeTo(2.0F, 10.0F), null, () -> INSTANCE.getOnlyInRange(), 8, null
   );
   @NotNull
   private static final GroupSetting resetOn = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "reset-on", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting velocity = SettingFactory.defaultImpl(INSTANCE, "velocity", true, INSTANCE.getResetOn(), null, 8, null);
   @NotNull
   private static final BooleanSetting lagBack = SettingFactory.defaultImpl(INSTANCE, "lag-back", true, INSTANCE.getResetOn(), null, 8, null);
   @NotNull
   private static final BooleanSetting openScreen = SettingFactory.defaultImpl(INSTANCE, "open-screen", true, INSTANCE.getResetOn(), null, 8, null);
   @NotNull
   private static Vec3 vec3 = Vec3.ZERO;
   private static float amount;
   private static boolean flag;
   @NotNull
   private static final Queue<Packet<? extends PacketListener>> queue = new LinkedList();
   @NotNull
   private static final Queue<Packet<? extends PacketListener>> queue2 = new LinkedList();
   @NotNull
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean();
   @Nullable
   private static ClientInput clientInput;
   @Nullable
   private static Vec3 vec32;
   private static boolean flag2;

   private Blink() {
      super("blink", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final int getLimit() {
      return ((Number)limit.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final void updateValue(int value) {
      limit.updateObject(this, $$delegatedProperties[0], value);
   }

   @NotNull
   public final Blink.AttackAction getAttackAction() {
      return (Blink.AttackAction)attackAction.computeObject(this, $$delegatedProperties[1]);
   }

   private final Blink.AttackAction getLimitAction() {
      return (Blink.AttackAction)limitAction.computeObject(this, $$delegatedProperties[2]);
   }

   private final void updateAttackAction(Blink.AttackAction attackAction) {
      limitAction.updateObject(this, $$delegatedProperties[2], attackAction);
   }

   private final Bind getCancel() {
      return (Bind)cancelButton.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getShowPosition() {
      return (Boolean)showPosition.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getDelayOnlyMove() {
      return (Boolean)delayOnlyMove.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getIgnoreInventoryActions() {
      return (Boolean)ignoreInventoryActions.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getOnlyInRange() {
      return (Boolean)onlyInRange.computeObject(this, $$delegatedProperties[7]);
   }

   private final float getRange() {
      return ((Number)range.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   private final GroupValue getResetOn() {
      return (GroupValue)resetOn.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getVelocityReset() {
      return (Boolean)velocity.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getLagBackReset() {
      return (Boolean)lagBack.computeObject(this, $$delegatedProperties[11]);
   }

   private final boolean getOpenScreenReset() {
      return (Boolean)openScreen.computeObject(this, $$delegatedProperties[12]);
   }

   @Nullable
   public final Vec3 getVec32() {
      return vec32;
   }

   public final void setVec32(Vec3 vec3) {
      vec32 = vec3;
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().level != null
         && !MinecraftHolder.getMinecraft().hasSingleplayerServer()) {
         queue2.clear();
         if (!this.getOnlyInRange()) {
            vec32 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
         }

         vec3 = MinecraftHolder.getLocalPlayer().getDeltaMovement();
         amount = MinecraftHolder.getLocalPlayer().getYRot();
         flag = MinecraftHolder.getLocalPlayer().isSprinting();
         atomicBoolean.set(false);
         queue.clear();
         clientInput = MinecraftHolder.getLocalPlayer().input;
      } else {
         this.applyUnplug();
      }
   }

   @Override
   public void update4() {
      if (this.isFlag6()) {
         while (!queue.isEmpty()) {
            Packet poll = (Packet)queue.poll();
            PacketUtil.applySend(poll);
         }
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundPlayerPositionPacket && this.getLagBackReset()) {
         this.applyUnplug();
      }

      if (receivePacket.getPacket() instanceof ClientboundSetEntityMotionPacket
         && ((ClientboundSetEntityMotionPacket)receivePacket.getPacket()).id() == MinecraftHolder.getLocalPlayer().getId()
         && this.getVelocityReset()) {
         this.applyUnplug();
      }

      if (receivePacket.getPacket() instanceof ClientboundExplodePacket && this.getVelocityReset()) {
         Vec3 orNull = (Vec3)OptionalsKt.getOrNull(((ClientboundExplodePacket)receivePacket.getPacket()).playerKnockback());
         if (orNull != null) {
            Vec3 vec33 = orNull;
            if (vec33.x != 0.0 || vec33.y != 0.0 || vec33.z != 0.0) {
               INSTANCE.applyUnplug();
            }
         }
      }

      if (receivePacket.getPacket() instanceof ClientboundOpenScreenPacket && this.getOpenScreenReset()) {
         this.applyUnplug();
      }
   }

   public final void update() {
      this.updateValue(60);
      this.updateAttackAction(Blink.AttackAction.OFF);
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (MinecraftHolder.getMinecraft().player != null && !atomicBoolean.get()) {
         if (this.getOnlyInRange()) {
            Player defaultImpl = PlayerTargetUtil.defaultImpl(PlayerTargetUtil.INSTANCE, this.getRange(), AutoCrystal.TargetSort.DISTANCE, false, true, 4, null);
            if (defaultImpl == null || !Aura.INSTANCE.getEnabled()) {
               this.applyGetX();
               vec32 = null;
               return;
            }

            if (vec32 == null) {
               vec32 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
            }

            double distanceTo = this.computeVec3((Entity)defaultImpl).distanceTo(vec32);
            double distanceTo2 = this.computeVec3((Entity)defaultImpl).distanceTo(this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
            if (distanceTo <= distanceTo2 || distanceTo < 1.0) {
               this.applyGetX();
               vec32 = this.computeVec3((Entity)MinecraftHolder.getLocalPlayer());
               return;
            }
         }

         if (sendPacket.getPacket() instanceof ServerboundMovePlayerPacket) {
            if (flag2) {
               flag2 = false;
               this.applyGetX();
               return;
            }
         } else if (this.getDelayOnlyMove()) {
            return;
         }

         if (!this.getIgnoreInventoryActions()
            || !(sendPacket.getPacket() instanceof ServerboundContainerClickPacket)
               && !(sendPacket.getPacket() instanceof ServerboundUseItemPacket)
               && (
                  !(sendPacket.getPacket() instanceof ServerboundPlayerActionPacket)
                     || ((ServerboundPlayerActionPacket)sendPacket.getPacket()).getAction() != Action.SWAP_ITEM_WITH_OFFHAND
               )
               && !(sendPacket.getPacket() instanceof ServerboundSetCarriedItemPacket)) {
            if ((sendPacket.getPacket() instanceof ServerboundAttackPacket || sendPacket.getPacket() instanceof ServerboundSwingPacket)
               && !ElytraTarget.INSTANCE.getEnabled()) {
               switch (Blink.WhenMappings.values[this.getAttackAction().ordinal()]) {
                  case 1:
                     this.applyUnplug();
                     break;
                  case 2:
                     flag2 = true;
               }
            }

            if (sendPacket.getPacket() instanceof ServerboundPongPacket) {
               queue2.add(sendPacket.getPacket());
            }

            sendPacket.cancel();
            queue.add(sendPacket.getPacket());
         }
      }
   }

   @Flow
   private final void onTick(TickEvent tickEvent) {
      if (!this.isFlag6() || MinecraftHolder.getLocalPlayer().isDeadOrDying()) {
         this.applyUnplug();
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (InputUtil.checkContains(this.getCancel().getValue()) && vec32 != null) {
         this.applySet();
         this.applyUnplug();
      } else {
         if (queue.size() > this.getLimit()) {
            switch (Blink.WhenMappings.values[this.getLimitAction().ordinal()]) {
               case 1:
                  this.applyUnplug();
                  break;
               case 2:
                  this.applyGetX();
            }
         }
      }
   }

   private final void applyGetX() {
      if (this.isFlag6()) {
         atomicBoolean.set(true);

         while (!queue.isEmpty()) {
            Packet poll = (Packet)queue.poll();
            PacketUtil.applySend(poll);
            if (poll instanceof ServerboundMovePlayerPacket) {
               vec32 = new Vec3(
                  ((ServerboundMovePlayerPacket)poll).getX(MinecraftHolder.getLocalPlayer().getX()),
                  ((ServerboundMovePlayerPacket)poll).getY(MinecraftHolder.getLocalPlayer().getY()),
                  ((ServerboundMovePlayerPacket)poll).getZ(MinecraftHolder.getLocalPlayer().getZ())
               );
            }
         }

         atomicBoolean.set(false);
         queue.clear();
      }
   }

   private final void applySet() {
      queue.clear();
      MinecraftHolder.getLocalPlayer().setPosRaw(vec32.x(), vec32.y(), vec32.z());
      MinecraftHolder.getLocalPlayer().setDeltaMovement(vec3);
      MinecraftHolder.getLocalPlayer().setYRot(amount);
      MinecraftHolder.getLocalPlayer().setSprinting(flag);
      MinecraftHolder.getLocalPlayer().setShiftKeyDown(false);
      MinecraftHolder.getMinecraft().options.keyShift.setDown(false);
      if (clientInput != null) {
         MinecraftHolder.getLocalPlayer().input = clientInput;
      }

      atomicBoolean.set(true);

      while (!queue2.isEmpty()) {
         Packet poll = (Packet)queue2.poll();
         PacketUtil.applySend(poll);
      }

      atomicBoolean.set(false);
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.isFlag6() && this.getShowPosition()) {
         if (vec32 != null) {
            Vec3 vec322 = vec32;
            if (!Intrinsics.areEqual(vec32, INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()))) {
               Render3D.defaultImpl(Render3D.INSTANCE, render3DEvent.getStack(), vec322, (Player)MinecraftHolder.getLocalPlayer(), 0.45F, false, 16, null);
            }
         }
      }
   }

   static {
   }

   public static enum AttackAction {
      REPEAT,
      OFF,
      NONE;

      private static final Blink.AttackAction[] attackActions = $values();
      private static final EnumEntries<Blink.AttackAction> enumEntries = EnumEntriesKt.enumEntries(attackActions);

      private AttackAction() {
      }

      @NotNull
      public static EnumEntries<Blink.AttackAction> getEnumEntries() {
         return enumEntries;
      }

      private static final Blink.AttackAction[] $values() {
         return new Blink.AttackAction[]{REPEAT, OFF, NONE};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[Blink.AttackAction.values().length];

         try {
            values2[Blink.AttackAction.OFF.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[Blink.AttackAction.REPEAT.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         values = values2;
      }
   }
}