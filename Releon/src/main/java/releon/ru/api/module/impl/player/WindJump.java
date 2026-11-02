package releon.ru.api.module.impl.player;

import java.util.Locale;
import java.util.Map;
import lombok.Generated;
import net.minecraft.client.util.InputUtil.Type;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import releon.ru.api.drag.impl.Notifications;
import releon.ru.api.events.annotation.SubscribeEvent;
import releon.ru.api.events.impl.HotBarScrollEvent;
import releon.ru.api.events.impl.KeyEvent;
import releon.ru.api.events.impl.RotationUpdateEvent;
import releon.ru.api.events.impl.TickEvent;
import releon.ru.api.module.Module;
import releon.ru.api.module.ModuleCategory;
import releon.ru.api.module.impl.combat.aura.Angle;
import releon.ru.api.module.impl.combat.aura.AngleConfig;
import releon.ru.api.module.impl.combat.aura.AngleConnection;
import releon.ru.api.module.impl.combat.aura.impl.LinearConstructor;
import releon.ru.api.module.impl.combat.aura.util.TaskPriority;
import releon.ru.api.module.impl.movement.InventoryMove;
import releon.ru.api.settings.bind.InputType;
import releon.ru.api.settings.bind.KeyBind;
import releon.ru.api.settings.impl.BindSetting;
import releon.ru.mixin.accessor.ItemCooldownInstanceAccessor;
import releon.ru.mixin.accessor.ItemCooldownsAccessor;
import releon.ru.utils.inventory.InventoryFlowManager;
import releon.ru.utils.inventory.InventoryTask;
import releon.ru.utils.inventory.bind.InventoryBindHelper;
import releon.ru.utils.inventory.lookup.InventoryUtils;
import releon.ru.utils.sounds.SoundManager;

public class WindJump extends Module {
   private final BindSetting keySetting = this.register(new BindSetting("Кнопка", "Кнопка для заряда ветра", KeyBind.keyboard(-1)));
   static final float THROW_PITCH = 90.0F;
   static final int ROTATION_WAIT_TICKS = 2;
   static final float ROTATION_READY_PITCH = 89.0F;
   static final int ROTATION_STABLE_TICKS = 1;
   static final long USE_TIMEOUT_MS = 550L;
   private WindJump.Phase phase = WindJump.Phase.IDLE;
   private int rotationTicks = 0;
   private int rotationStableTicks = 0;
   private long lastThrowTime = 0L;
   private long useStartedAt = 0L;

   public WindJump() {
      super("Wind Jump", "Прыжковый модуль под Wind-серверы", ModuleCategory.PLAYER);
   }

   @SubscribeEvent
   public void onKey(KeyEvent event) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null && this.phase == WindJump.Phase.IDLE) {
         if (event.action() == 1 && this.matchesBind(event)) {
            this.tryUseWindCharge();
         }
      }
   }

   @SubscribeEvent
   public void onScroll(HotBarScrollEvent event) {
      if (mc.player != null && mc.world != null && mc.currentScreen == null && this.phase == WindJump.Phase.IDLE) {
         if (InventoryBindHelper.matchesScroll(event, this.keySetting)) {
            event.setCancelled(true);
            this.tryUseWindCharge();
         }
      }
   }

   @SubscribeEvent
   public void onRotationUpdate(RotationUpdateEvent event) {
      if (mc.player != null && mc.world != null && this.phase != WindJump.Phase.IDLE && event.isPre()) {
         Angle throwAngle = new Angle(mc.player.getYaw(), 90.0F);
         AngleConfig config = new AngleConfig(new LinearConstructor(), true, true);
         AngleConnection.INSTANCE.rotateTo(throwAngle, 3, config, TaskPriority.HIGH_IMPORTANCE_1, this);
      }
   }

   @SubscribeEvent
   public void onTick(TickEvent.Pre event) {
      if (mc.player == null || mc.world == null) {
         this.resetState();
      } else if (this.phase == WindJump.Phase.ROTATING) {
         this.rotationTicks++;
         Angle currentRotation = AngleConnection.INSTANCE.getRotation();
         boolean rotationReady = currentRotation != null && currentRotation.getPitch() >= 89.0F;
         boolean waitedEnough = this.rotationTicks >= 2;
         if (rotationReady && waitedEnough) {
            this.phase = WindJump.Phase.ARMING;
            this.rotationStableTicks = 0;
         } else {
            if (this.rotationTicks > 10) {
               this.finishAndReset();
            }
         }
      } else if (this.phase == WindJump.Phase.ARMING) {
         Angle currentRotation = AngleConnection.INSTANCE.getRotation();
         if (currentRotation != null && !(currentRotation.getPitch() < 89.0F)) {
            this.rotationStableTicks++;
            if (this.rotationStableTicks >= 1) {
               if (!this.scheduleUse()) {
                  this.finishAndReset();
                  return;
               }

               this.phase = WindJump.Phase.USING;
               this.useStartedAt = System.currentTimeMillis();
               this.notifyItemUsed("Заряд ветра", false);
            }
         } else {
            this.phase = WindJump.Phase.ROTATING;
            this.rotationStableTicks = 0;
         }
      } else {
         if (this.phase == WindJump.Phase.USING) {
            if (InventoryTask.isSwapAndUseIdle() && InventoryFlowManager.isIdle() && System.currentTimeMillis() - this.useStartedAt >= 50L) {
               this.finishAndReset();
               return;
            }

            if (System.currentTimeMillis() - this.useStartedAt > 550L) {
               this.finishAndReset();
            }
         }
      }
   }

   private void tryUseWindCharge() {
      if (System.currentTimeMillis() - this.lastThrowTime >= 100L) {
         boolean hasHotbarCharge = InventoryUtils.findItemInHotbar(Items.WIND_CHARGE) != -1;
         boolean hasInventoryCharge = InventoryUtils.findItemInInventory(Items.WIND_CHARGE) != -1;
         if (!hasHotbarCharge && !hasInventoryCharge) {
            this.notifyItemMissing("Заряд ветра ");
         } else {
            this.lastThrowTime = System.currentTimeMillis();
            this.phase = WindJump.Phase.ROTATING;
            this.rotationTicks = 0;
            this.rotationStableTicks = 0;
            this.useStartedAt = 0L;
            AngleConnection.INSTANCE.forcePacketRotation(1);
         }
      }
   }

   private boolean scheduleUse() {
      if (mc.player == null) {
         return false;
      } else {
         int hotbarSlot = InventoryUtils.findItemInHotbar(Items.WIND_CHARGE);
         if (hotbarSlot != -1) {
            ItemStack charge = mc.player.getInventory().getStack(hotbarSlot);
            if (mc.player.getItemCooldownManager().isCoolingDown(charge)) {
               this.notifyItemCooldown("Заряд ветра", charge);
               return false;
            } else {
               InventoryTask.useHotbarItem(hotbarSlot, mc.player.getInventory().getSelectedSlot(), 1, 2);
               return true;
            }
         } else {
            int inventorySlot = InventoryUtils.findItemInInventory(Items.WIND_CHARGE);
            if (inventorySlot == -1) {
               return false;
            } else {
               ItemStack charge = mc.player.getInventory().getStack(inventorySlot);
               if (mc.player.getItemCooldownManager().isCoolingDown(charge)) {
                  this.notifyItemCooldown("Заряд ветра", charge);
                  return false;
               } else {
                  InventoryTask.swapAndUse(Items.WIND_CHARGE, this.shouldQueueInventoryAction());
                  return true;
               }
            }
         }
      }
   }

   private boolean shouldQueueInventoryAction() {
      InventoryMove inventoryMove = InventoryMove.getInstance();
      return inventoryMove == null ? true : !"ReallyWorld".equals(inventoryMove.getSelectedMode());
   }

   private boolean matchesBind(KeyEvent event) {
      KeyBind bind = this.keySetting.getValue();
      return bind != null && bind.isBound()
         ? bind.getType() == InputType.MOUSE && event.type() == Type.MOUSE && event.key() == bind.getCode()
            || bind.getType() == InputType.KEYBOARD && event.type() == Type.KEYSYM && event.key() == bind.getCode()
         : false;
   }

   private void finishAndReset() {
      AngleConnection.INSTANCE.startReturning();
      this.resetState();
   }

   private void resetState() {
      this.phase = WindJump.Phase.IDLE;
      this.rotationTicks = 0;
      this.rotationStableTicks = 0;
      this.useStartedAt = 0L;
   }

   private void notifyElytraHelper(String text, boolean sound) {
      Notifications.push("WindJump", text, 4000L, sound ? SoundManager.NOTIFICATION : null);
   }

   private void notifyItemUsed(String itemName, boolean sound) {
      this.notifyElytraHelper(itemName + " использован", sound);
   }

   private void notifyItemMissing(String itemName) {
      this.notifyElytraHelper(itemName + " Не найдено", true);
   }

   private void notifyItemCooldown(String itemName, ItemStack stack) {
      this.notifyElytraHelper(itemName + " будет доступен через " + this.formatCooldownDurationTicks(this.cooldownRemainingTicks(stack)), true);
   }

   private String formatCooldownDurationTicks(int ticks) {
      if (ticks < 0) {
         return "?";
      } else {
         int totalSeconds = Math.max(1, (int)Math.ceil((double)ticks / 20.0));
         int minutes = totalSeconds / 60;
         int seconds = totalSeconds % 60;
         return minutes > 0 ? minutes + ":" + String.format(Locale.US, "%02d", seconds) : totalSeconds + "s";
      }
   }

   private int cooldownRemainingTicks(ItemStack stack) {
      if (mc.player != null && stack != null && !stack.isEmpty()) {
         ItemCooldownManager cooldowns = mc.player.getItemCooldownManager();

         try {
            Identifier group = cooldowns.getGroup(stack);
            Map<Identifier, ?> activeCooldowns = ((ItemCooldownsAccessor)cooldowns).releon$getCooldowns();
            if (activeCooldowns.get(group) instanceof ItemCooldownInstanceAccessor accessor) {
               return Math.max(0, accessor.releon$getEndTime() - ((ItemCooldownsAccessor)cooldowns).releon$getTickCount());
            }
         } catch (RuntimeException var7) {
         }

         float progress = cooldowns.getCooldownProgress(stack, 0.0F);
         return progress <= 0.0F ? -1 : Math.max(1, Math.round(progress * 20.0F));
      } else {
         return -1;
      }
   }

   public boolean isRunning() {
      return this.phase != WindJump.Phase.IDLE;
   }

   @Override
   protected void onDisable() {
      AngleConnection.INSTANCE.startReturning();
      this.resetState();
   }

   @Generated
   public BindSetting getKeySetting() {
      return this.keySetting;
   }

   @Generated
   public WindJump.Phase getPhase() {
      return this.phase;
   }

   @Generated
   public int getRotationTicks() {
      return this.rotationTicks;
   }

   @Generated
   public int getRotationStableTicks() {
      return this.rotationStableTicks;
   }

   @Generated
   public long getLastThrowTime() {
      return this.lastThrowTime;
   }

   @Generated
   public long getUseStartedAt() {
      return this.useStartedAt;
   }

   static enum Phase {
      IDLE,
      ROTATING,
      ARMING,
      USING;

      private Phase() {
      }
   }
}
