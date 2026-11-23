// deobf by pusia
package su.catlean.client.features.modules.rage;

import net.minecraft.sounds.SoundEvent;
import java.awt.Color;
import java.util.ArrayList;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity.AnimationStatus;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.BreakData;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.enums.SwitchMode;
import su.catlean.api.event.events.client.ScreenEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.AfterElytraEvent;
import su.catlean.api.event.events.player.JumpEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreElytraEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.equip.ElytraSwap;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.rotation.LookRotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StringHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class HighJump extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(HighJump.class, "mode", "getMode()Lsu/catlean/kj;", 0), new PropertyReference1Impl(HighJump.class, "vertical", "getVertical()F", 0), new PropertyReference1Impl(HighJump.class, "horizontal", "getHorizontal()F", 0), new PropertyReference1Impl(HighJump.class, "minimizeClicks", "getMinimizeClicks()Z", 0), new PropertyReference1Impl(HighJump.class, "offHandFirework", "getOffHandFirework()Z", 0), new PropertyReference1Impl(HighJump.class, "alternativeFireworkSwap", "getAlternativeFireworkSwap()Z", 0), new PropertyReference1Impl(HighJump.class, "ticks", "getTicks()I", 0) };
   @NotNull
   public static final HighJump INSTANCE = new HighJump();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", HighJump.Mode.VANILLA, null, null, 12, null);
   @NotNull
   private static final FloatSetting vertical = SettingFactory.defaultImpl(INSTANCE, "vertical", 0.42F, RangesKt.rangeTo(0.1F, 20.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting horizontal = StringHolder.computeFloatSetting(
      SettingFactory.defaultImpl(INSTANCE, "horizontal", 0.0F, RangesKt.rangeTo(0.0F, 5.0F), null, null, 24, null), 0.0F, "copy-from-player"
   );
   @NotNull
   private static final BooleanSetting minimizeClicks = SettingFactory.defaultImpl(
      INSTANCE, "minimize-clicks", false, null, () -> INSTANCE.getMode() == HighJump.Mode.ELYTRA, 4, null
   );
   @NotNull
   private static final BooleanSetting booleanSetting = SettingFactory.defaultImpl(
      INSTANCE, "offHand-firework", false, null, () -> INSTANCE.getMode() == HighJump.Mode.ELYTRA, 4, null
   );
   @NotNull
   private static final BooleanSetting altFireworkSwap = SettingFactory.defaultImpl(
      INSTANCE, "alt-firework-swap", false, null, () -> INSTANCE.getMode() == HighJump.Mode.ELYTRA, 4, null
   );
   @NotNull
   private static final IntSetting ticks = SettingFactory.defaultImpl(
      INSTANCE, "ticks", 2, new IntRange(1, 15), null, () -> INSTANCE.getMode() == HighJump.Mode.ELYTRA, 8, null
   );
   private static boolean flag;
   private static int value;
   @NotNull
   private static PlayerUtil playerUtil = new PlayerUtil();
   private static float amount;

   private HighJump() {
      super("high-jump", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final HighJump.Mode getMode() {
      return (HighJump.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   private final float getVertical() {
      return ((Number)vertical.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final float getHorizontal() {
      return ((Number)horizontal.computeObject(this, $$delegatedProperties[2])).floatValue();
   }

   private final boolean getMinimizeClicks() {
      return (Boolean)minimizeClicks.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getOffHandFirework() {
      return (Boolean)booleanSetting.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getAlternativeFireworkSwap() {
      return (Boolean)altFireworkSwap.computeObject(this, $$delegatedProperties[5]);
   }

   private final int getTicks() {
      return ((Number)ticks.computeObject(this, $$delegatedProperties[6])).intValue();
   }

   @Override
   public void update2() {
      value = 0;
   }

   @Flow
   private final void onScreen(ScreenEvent screenEvent) {
      if (flag) {
         screenEvent.cancel();
         flag = false;
      }
   }

   @Flow
   private final void onJump(JumpEvent jumpEvent) {
      if (this.getMode() == HighJump.Mode.VANILLA) {
         jumpEvent.cancel();
         jumpEvent.setY(this.getVertical());
         jumpEvent.setXz(this.getHorizontal());
      }
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (!MinecraftHolder.getLocalPlayer().getAbilities().flying && !MinecraftHolder.getLocalPlayer().isFallFlying()) {
         if (this.getMode() == HighJump.Mode.GRIM_SHULKER && MinecraftHolder.getLocalPlayer().tickCount % 5 == 0 && WorldUtil.INSTANCE.isFlag4()) {
            Iterable items = ServerInfoService.INSTANCE.getItems();
            boolean flag2 = false;
            var arrayList = new ArrayList();

            for (Object object : items) {
               if (object instanceof ShulkerBoxBlockEntity) {
                  arrayList.add(object);
               }
            }

            for (ShulkerBoxBlockEntity shulkerBoxBlockEntity : (Iterable<ShulkerBoxBlockEntity>)arrayList) {
               flag2 = shulkerBoxBlockEntity.getAnimationStatus() != AnimationStatus.OPENING && shulkerBoxBlockEntity.getAnimationStatus() != AnimationStatus.OPENED;
               AABB inflate = new AABB(shulkerBoxBlockEntity.getBlockPos()).inflate(0.5, 1.0, 0.5);
               AABB inflate2 = new AABB(shulkerBoxBlockEntity.getBlockPos()).inflate(1.5, 1.0, 1.5);
               Render3D.defaultImpl3(Render3D.INSTANCE, inflate, Color.WHITE, null, 4, null);
               if (MinecraftHolder.getLocalPlayer().getBoundingBox().intersects(inflate2)) {
                  if (flag2 && MinecraftHolder.getLocalPlayer().getBoundingBox().intersects(inflate2)) {
                     BreakData breakData = InteractionManager.INSTANCE.computeBreakData(shulkerBoxBlockEntity.getBlockPos(), PlaceModule.Interact.Strict);
                     if (breakData != null) {
                        RotationManager.INSTANCE
                           .updateRotationAction(
                              new LookRotationAction(
                                 Rotation.vectorUtil.computeRotation(breakData.getVector()),
                                 0,
                                 () -> {
                                    MinecraftHolder.getMultiPlayerGameMode()
                                       .useItemOn(
                                          MinecraftHolder.getLocalPlayer(),
                                          InteractionHand.MAIN_HAND,
                                          new BlockHitResult(breakData.getVector(), breakData.getDir(), shulkerBoxBlockEntity.getBlockPos(), false)
                                       );
                                    MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                                    flag = true;
                                    return Unit.INSTANCE;
                                 },
                                 2,
                                 null
                              )
                           );
                     }
                  } else if (MinecraftHolder.getLocalPlayer().getBoundingBox().intersects(inflate)) {
                     double[] defaultImpl;
                     if (this.getHorizontal() == 0.0F) {
                        double[] values = new double[]{
                           MinecraftHolder.getLocalPlayer().getDeltaMovement().x, MinecraftHolder.getLocalPlayer().getDeltaMovement().z
                        };
                        defaultImpl = values;
                     } else {
                        defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, this.getHorizontal(), false, 0.0F, 0.0F, 0.0F, 30, null);
                     }

                     double[] values2 = defaultImpl;
                     MinecraftHolder.getLocalPlayer().setDeltaMovement(values2[0], this.getVertical(), values2[1]);
                     Packet serverboundContainerClosePacket = (Packet)(new ServerboundContainerClosePacket(MinecraftHolder.getLocalPlayer().containerMenu.containerId));
                     PacketUtil.applySend(serverboundContainerClosePacket);
                  }
               }
            }
         }

         if (this.getMode() == HighJump.Mode.ELYTRA) {
            int value2 = InventoryManager.INSTANCE.getValue();
            InventoryManager instance = InventoryManager.INSTANCE;
            Item[] items2 = new Item[]{Items.FIREWORK_ROCKET};
            SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items2);
            if (value2 < 0 || !searchInvResult2.isFlag() || MinecraftHolder.getLocalPlayer().isInWater() || MinecraftHolder.getLocalPlayer().isInLava()) {
               this.applyUnplug();
               return;
            }

            RotationManager.INSTANCE.updateRotation(new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), -90.0F, false, SwitchMode.FORCE, 4, null), 0);
            if (value > this.getTicks() - 1) {
               int value3 = value++;
               if (value3 > this.getTicks() + 5) {
                  if (GuiMove.INSTANCE.getEnabled() && GuiMove.INSTANCE.getClickBypass() == GuiMove.Bypass.LEGIT) {
                     WorldUtil.INSTANCE.applyGetKey();
                  }

                  this.applyUnplug();
               }

               return;
            }

            if (MinecraftHolder.getLocalPlayer().onGround()) {
               if (!MinecraftHolder.getMinecraft().options.keyJump.isDown()) {
                  MinecraftHolder.getMinecraft().options.keyJump.setDown(true);
                  TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 3, (byte)0, false, () -> {
                     if (!InputUtil.checkContains(32)) {
                        MinecraftHolder.getMinecraft().options.keyJump.setDown(false);
                     }
                  }, 6, null);
               }

               return;
            }

            if (GuiMove.INSTANCE.getEnabled() && GuiMove.INSTANCE.getClickBypass() == GuiMove.Bypass.LEGIT && WorldUtil.INSTANCE.isFlag4()) {
               WorldUtil.INSTANCE.applySetDown();
               return;
            }

            if (playerUtil.testValue2(4)) {
               if (value2 >= 36) {
                  GuiMove.INSTANCE.setFlag5(true);
                  int value4 = value2 - 36;
                  ContainerUtil.applyHandleContainerInput(6, value4, ContainerInput.SWAP, true);
                  ElytraSwap.INSTANCE.setFlag(true);
                  MinecraftHolder.getLocalPlayer().startFallFlying();
                  Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
                  PacketUtil.applySend(serverboundPlayerCommandPacket);
                  if (value == 0) {
                     ElytraTarget.INSTANCE.applyGetItem(this.getOffHandFirework(), this.getAlternativeFireworkSwap());
                  }

                  boolean flag4 = true;
                  ContainerInput swap = ContainerInput.SWAP;
                  byte byteValue = 6;
                  ContainerUtil.applyHandleContainerInput(byteValue, value4, swap, flag4);
                  GuiMove.INSTANCE.setFlag5(false);
               } else {
                  GuiMove.INSTANCE.setFlag5(true);
                  ContainerUtil.defaultImpl(value2, 0, null, false, 14, null);
                  Packet serverboundPlayerCommandPacket2 = null;
                  byte byteValue2 = 14;
                  boolean flag5 = false;
                  Object object2 = null;
                  byte byteValue3 = 0;
                  byte byteValue4 = 6;
                  ContainerUtil.defaultImpl(byteValue4, byteValue3, (ContainerInput)object2, flag5, byteValue2, serverboundPlayerCommandPacket2);
                  if (!this.getMinimizeClicks()) {
                     ContainerUtil.defaultImpl(value2, 0, null, true, 6, null);
                  }

                  ElytraSwap.INSTANCE.setFlag(true);
                  MinecraftHolder.getLocalPlayer().startFallFlying();
                  serverboundPlayerCommandPacket2 = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
                  PacketUtil.applySend((Packet)serverboundPlayerCommandPacket2);
                  if (value == 0) {
                     ElytraTarget.INSTANCE.applyGetItem(this.getOffHandFirework(), this.getAlternativeFireworkSwap());
                  }

                  if (!this.getMinimizeClicks()) {
                     ContainerUtil.defaultImpl(value2, 0, null, false, 14, null);
                  }

                  ContainerUtil.defaultImpl(6, 0, null, false, 14, null);
                  serverboundPlayerCommandPacket2 = null;
                  byteValue2 = 6;
                  flag5 = true;
                  object2 = null;
                  byteValue3 = 0;
                  ContainerUtil.defaultImpl(value2, byteValue3, (ContainerInput)object2, flag5, byteValue2, serverboundPlayerCommandPacket2);
                  GuiMove.INSTANCE.setFlag5(false);
               }

               int value5 = value++;
            }
         }
      }
   }

   @Flow
   private final void onFireWorkVelocity(FireWorkVelocityEvent fireWorkVelocityEvent) {
      if (this.getMode() == HighJump.Mode.ELYTRA) {
         fireWorkVelocityEvent.cancel();
         Vec3 deltaMovement = MinecraftHolder.getLocalPlayer().getDeltaMovement();
         fireWorkVelocityEvent.setVelocity(MinecraftHolder.getLocalPlayer().getDeltaMovement().add(-deltaMovement.x * 0.5, 0.1 + (1.5 - deltaMovement.y) * 0.5, -deltaMovement.z * 0.5));
      }
   }

   @Flow
   private final void onPreElytra(PreElytraEvent preElytraEvent) {
      if (this.getMode() == HighJump.Mode.ELYTRA) {
         amount = MinecraftHolder.getLocalPlayer().getXRot();
         MinecraftHolder.getLocalPlayer().setXRot(-90.0F);
      }
   }

   @Flow
   private final void onAfterElytra(AfterElytraEvent afterElytraEvent) {
      if (this.getMode() == HighJump.Mode.ELYTRA) {
         MinecraftHolder.getLocalPlayer().setXRot(amount);
      }
   }

   @Flow
   private final void onSetPose(SetPoseEvent setPoseEvent) {
      if (this.getMode() == HighJump.Mode.ELYTRA) {
         setPoseEvent.cancel();
         setPoseEvent.setPose(Pose.STANDING);
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (this.getMode() == HighJump.Mode.ELYTRA) {
         if (receivePacket.getPacket() instanceof ClientboundSoundPacket) {
            Holder<SoundEvent> sound = ((ClientboundSoundPacket)receivePacket.getPacket()).getSound();
            if (Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_ELYTRA)
               || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_NETHERITE)
               || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_DIAMOND)
               || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_GOLD)
               || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_IRON)
               || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_GENERIC)
               || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_CHAIN)
               || Intrinsics.areEqual(sound, SoundEvents.ARMOR_EQUIP_LEATHER)) {
               receivePacket.cancel();
            }
         }
      }
   }

   static {
   }

   public static enum Mode {
      VANILLA,
      GRIM_SHULKER,
      ELYTRA;

      private static final HighJump.Mode[] modes = $values();
      private static final EnumEntries<HighJump.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<HighJump.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final HighJump.Mode[] $values() {
         return new HighJump.Mode[]{VANILLA, GRIM_SHULKER, ELYTRA};
      }
   }
}