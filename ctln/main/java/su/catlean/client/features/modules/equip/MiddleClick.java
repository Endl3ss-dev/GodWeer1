// deobf by pusia
package su.catlean.client.features.modules.equip;

import java.util.Collection;
import kotlin.Unit;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PickUpBlockEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.cosmetic.Trajectories;
import su.catlean.client.services.FriendManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.LookRotationAction;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.world.WorldUtil;
import su.catlean.client.services.InventoryManager;

public final class MiddleClick extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(MiddleClick.class, "onBlock", "getOnBlock()Lsu/catlean/dn;", 0), new PropertyReference1Impl(MiddleClick.class, "onAir", "getOnAir()Lsu/catlean/dn;", 0), new PropertyReference1Impl(MiddleClick.class, "onEntity", "getOnEntity()Lsu/catlean/dn;", 0), new PropertyReference1Impl(MiddleClick.class, "onFlying", "getOnFlying()Lsu/catlean/dn;", 0), new PropertyReference1Impl(MiddleClick.class, "silent", "getSilent()Z", 0), new PropertyReference1Impl(MiddleClick.class, "inventory", "getInventory()Z", 0), new PropertyReference1Impl(MiddleClick.class, "swapDelay", "getSwapDelay()I", 0), new PropertyReference1Impl(MiddleClick.class, "antiWaste", "getAntiWaste()Z", 0), new PropertyReference1Impl(MiddleClick.class, "durability", "getDurability()I", 0), new PropertyReference1Impl(MiddleClick.class, "xpFactor", "getXpFactor()I", 0), new PropertyReference1Impl(MiddleClick.class, "pearlOnRelease", "getPearlOnRelease()Z", 0) };
   @NotNull
   public static final MiddleClick INSTANCE = new MiddleClick();
   @NotNull
   private static final EnumSetting onBlock = SettingFactory.defaultImpl(INSTANCE, "on-block", MiddleClick.OnBlock.XP, null, null, 12, null);
   @NotNull
   private static final EnumSetting onAir = SettingFactory.defaultImpl(INSTANCE, "on-air", MiddleClick.OnBlock.PEARL, null, null, 12, null);
   @NotNull
   private static final EnumSetting onEntity = SettingFactory.defaultImpl(INSTANCE, "on-entity", MiddleClick.OnBlock.FRIEND, null, null, 12, null);
   @NotNull
   private static final EnumSetting onElytra = SettingFactory.defaultImpl(INSTANCE, "on-elytra", MiddleClick.OnBlock.FIREWORK, null, null, 12, null);
   @NotNull
   private static final BooleanSetting silent = SettingFactory.defaultImpl(INSTANCE, "silent", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting inventory = SettingFactory.defaultImpl(INSTANCE, "inventory", true, null, null, 12, null);
   @NotNull
   private static final IntSetting swapDelay = SettingFactory.defaultImpl(
      INSTANCE, "swap-delay", 100, new IntRange(0, 1000), null, () -> !INSTANCE.getSilent(), 8, null
   );
   @NotNull
   private static final BooleanSetting antiWaste = SettingFactory.defaultImpl(INSTANCE, "anti-waste", true, null, null, 12, null);
   @NotNull
   private static final IntSetting stopOn = SettingFactory.defaultImpl(INSTANCE, "stop-on", 90, new IntRange(0, 100), null, null, 24, null);
   @NotNull
   private static final IntSetting xpFactor = SettingFactory.defaultImpl(INSTANCE, "xp-factor", 1, new IntRange(1, 16), null, null, 24, null);
   @NotNull
   private static final BooleanSetting pearlOnRelease = SettingFactory.defaultImpl(INSTANCE, "pearl-on-release", true, null, null, 12, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   private static boolean flag;

   private MiddleClick() {
      super("middle-click", SubCategories.getSubCategory9(), null, 4, null);
   }

   private final MiddleClick.OnBlock getOnBlock() {
      return (MiddleClick.OnBlock)onBlock.computeObject(this, $$delegatedProperties[0]);
   }

   private final MiddleClick.OnBlock getOnAir() {
      return (MiddleClick.OnBlock)onAir.computeObject(this, $$delegatedProperties[1]);
   }

   private final MiddleClick.OnBlock getOnEntity() {
      return (MiddleClick.OnBlock)onEntity.computeObject(this, $$delegatedProperties[2]);
   }

   private final MiddleClick.OnBlock getOnFlying() {
      return (MiddleClick.OnBlock)onElytra.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getSilent() {
      return (Boolean)silent.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getInventory() {
      return (Boolean)inventory.computeObject(this, $$delegatedProperties[5]);
   }

   private final int getSwapDelay() {
      return ((Number)swapDelay.computeObject(this, $$delegatedProperties[6])).intValue();
   }

   private final boolean getAntiWaste() {
      return (Boolean)antiWaste.computeObject(this, $$delegatedProperties[7]);
   }

   private final int getDurability() {
      return ((Number)stopOn.computeObject(this, $$delegatedProperties[8])).intValue();
   }

   private final int getXpFactor() {
      return ((Number)xpFactor.computeObject(this, $$delegatedProperties[9])).intValue();
   }

   private final boolean getPearlOnRelease() {
      return (Boolean)pearlOnRelease.computeObject(this, $$delegatedProperties[10]);
   }

   @Flow
   private final void onPickUpBlock(PickUpBlockEvent pickUpBlockEvent) {
      pickUpBlockEvent.cancel();
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getMinecraft().gui.screen() == null) {
         HitResult hitResult = MinecraftHolder.getMinecraft().hitResult;
         if (MinecraftHolder.getMinecraft().options.keyPickItem.isDown()) {
            flag = true;
            if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
               this.getOnFlying().getCallback().invoke();
            } else if (hitResult instanceof EntityHitResult && ((EntityHitResult)hitResult).getEntity() instanceof Player) {
               this.getOnEntity().getCallback().invoke();
            } else if (hitResult instanceof BlockHitResult && !MinecraftHolder.getClientLevel().isEmptyBlock(((BlockHitResult)hitResult).getBlockPos())) {
               this.getOnBlock().getCallback().invoke();
            } else {
               this.getOnAir().getCallback().invoke();
            }
         } else if (flag) {
            if (MinecraftHolder.getLocalPlayer().isFallFlying()) {
               this.getOnFlying().getCallback2().invoke();
            } else if (hitResult instanceof EntityHitResult && ((EntityHitResult)hitResult).getEntity() instanceof Player) {
               this.getOnEntity().getCallback2().invoke();
            } else if (hitResult instanceof BlockHitResult && !MinecraftHolder.getClientLevel().isEmptyBlock(((BlockHitResult)hitResult).getBlockPos())) {
               this.getOnBlock().getCallback2().invoke();
            } else {
               this.getOnAir().getCallback2().invoke();
            }

            flag = false;
         }
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (MinecraftHolder.getMinecraft().gui.screen() == null
         && MinecraftHolder.getMinecraft().options.keyPickItem.isDown()
         && this.getPearlOnRelease()
         && (
            this.getOnBlock() == MiddleClick.OnBlock.PEARL
               || this.getOnAir() == MiddleClick.OnBlock.PEARL
               || this.getOnEntity() == MiddleClick.OnBlock.PEARL
               || this.getOnFlying() == MiddleClick.OnBlock.PEARL
         )) {
         InventoryManager instance = su.catlean.client.services.InventoryManager.INSTANCE;
         Item[] items = new Item[]{Items.ENDER_PEARL};
         boolean isFlag = instance.computeSearchInvResult2(items).isFlag();
         if (isFlag) {
            Trajectories.INSTANCE.applyCos((Player)MinecraftHolder.getLocalPlayer(), Items.ENDER_PEARL, MinecraftHolder.getLocalPlayer().getYRot());
         }
      }
   }

   private final boolean isFlag() {
      InventoryManager instance = su.catlean.client.services.InventoryManager.INSTANCE;
      Item[] items = new Item[]{Items.EXPERIENCE_BOTTLE};
      if (!instance.computeSearchInvResult(items).isFlag()) {
         return false;
      } else {
         var inventory2 = InventoryUtil.fetchInventory(MinecraftHolder.getLocalPlayer());
         boolean flag2;
         if (inventory2 instanceof Collection && ((Collection)inventory2).isEmpty()) {
            flag2 = false;
         } else {
            for (Object object : inventory2) {
               ItemStack itemStack = (ItemStack)object;
               if (WorldUtil.INSTANCE.fetchMaxDamage(itemStack) < INSTANCE.getDurability()) {
                  return true ? true : !this.getAntiWaste();
               }
            }

            flag2 = false;
         }

         return flag2 ? true : !this.getAntiWaste();
      }
   }

   private final void applyGetInventory() {
      if (stopWatch.testValue2(250)) {
         InventoryManager instance = su.catlean.client.services.InventoryManager.INSTANCE;
         Item[] items = new Item[]{Items.FIREWORK_ROCKET};
         int value = instance.computeSearchInvResult(items).getValue();
         if (this.getSilent()) {
            if (this.getInventory() && (!this.getInventory() || value == -1)) {
               instance = su.catlean.client.services.InventoryManager.INSTANCE;
               InventoryManager instance2 = su.catlean.client.services.InventoryManager.INSTANCE;
               items = new Item[]{Items.FIREWORK_ROCKET};
               instance.applyGetInventory3(instance2.computeSearchInvResult2(items).getValue());
            } else {
               su.catlean.client.services.InventoryManager.INSTANCE.applyGetInventory(value, this.getXpFactor());
            }
         } else if (!this.getInventory() || this.getInventory() && value != -1) {
            if (value != -1) {
               new MiddleClick.Worker(value, MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), this.getSwapDelay(), false).start();
            }
         } else {
            instance = su.catlean.client.services.InventoryManager.INSTANCE;
            Item[] items2 = new Item[]{Items.FIREWORK_ROCKET};
            int value2 = instance.computeSearchInvResult2(items2).getValue();
            if (value2 != -1) {
               new MiddleClick.Worker(value2, MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), this.getSwapDelay(), true).start();
            }
         }
      }
   }

   private final void applyGetEntity() {
      if (stopWatch.testValue2(800) && MinecraftHolder.getMinecraft().hitResult instanceof EntityHitResult) {
         EntityHitResult hitResult = (EntityHitResult)MinecraftHolder.getMinecraft().hitResult;
         if (hitResult.getEntity() instanceof Player) {
            Player entity = (Player)hitResult.getEntity();
            if (PlayerNameUtil.checkGetGameProfile(entity)) {
               FriendManager.INSTANCE.checkGetGameProfile2((Player)hitResult.getEntity());
               ChatUtil.defaultImpl(
                  this,
                  ChatFormatting.RED.toString()
                     + hitResult.getEntity().getName().getString()
                     + ChatFormatting.GRAY
                     + " "
                     + ChatUtil.defaultImpl(this, "removed", new Object[0], false, 4, null),
                  false,
                  2,
                  null
               );
            } else {
               FriendManager.INSTANCE.checkGetGameProfile((Player)hitResult.getEntity());
               ChatUtil.defaultImpl(
                  this,
                  ChatFormatting.GREEN.toString()
                     + hitResult.getEntity().getName().getString()
                     + ChatFormatting.GRAY
                     + " "
                     + ChatUtil.defaultImpl(this, "added", new Object[0], false, 4, null),
                  false,
                  2,
                  null
               );
            }
         }
      }
   }

   private final void applyGetXRot() {
      if (stopWatch.testValue2(500)) {
         InventoryManager instance = su.catlean.client.services.InventoryManager.INSTANCE;
         Item[] items = new Item[]{Items.ENDER_PEARL};
         int value = instance.computeSearchInvResult(items).getValue();
         instance = su.catlean.client.services.InventoryManager.INSTANCE;
         Item[] items2 = new Item[]{Items.ENDER_PEARL};
         int value2 = instance.computeSearchInvResult2(items2).getValue();
         if (value != -1 || value2 != -1) {
            RotationManager instance2 = RotationManager.INSTANCE;
            Rotation rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
            Function0 function0 = () -> {
               if (INSTANCE.getSilent()) {
                  if (INSTANCE.getInventory() && (!INSTANCE.getInventory() || value == -1)) {
                     InventoryManager instance3 = su.catlean.client.services.InventoryManager.INSTANCE;
                     InventoryManager instance4 = su.catlean.client.services.InventoryManager.INSTANCE;
                     Item[] items3 = new Item[]{Items.ENDER_PEARL};
                     instance3.applyGetInventory3(instance4.computeSearchInvResult2(items3).getValue());
                  } else {
                     su.catlean.client.services.InventoryManager.INSTANCE.applyGetInventory(value, INSTANCE.getXpFactor());
                  }
               } else if (!INSTANCE.getInventory() || INSTANCE.getInventory() && value != -1) {
                  if (value != -1) {
                     new MiddleClick.Worker(value, MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), INSTANCE.getSwapDelay(), false).start();
                  }
               } else if (value2 != -1) {
                  new MiddleClick.Worker(value2, MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), INSTANCE.getSwapDelay(), true).start();
               }

               return Unit.INSTANCE;
            };
            short shortValue = 200;
            Rotation rotation2 = rotation;
            instance2.updateRotationAction(new LookRotationAction(rotation2, shortValue, function0));
         }
      }
   }

   private final void applyGetYRot() {
      if (this.isFlag()) {
         RotationManager instance = RotationManager.INSTANCE;
         Rotation rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), 90.0F, false, null, 12, null);
         Function0 function0 = () -> {
            InventoryManager instance2 = su.catlean.client.services.InventoryManager.INSTANCE;
            InventoryManager instance3 = su.catlean.client.services.InventoryManager.INSTANCE;
            Item[] items = new Item[]{Items.EXPERIENCE_BOTTLE};
            instance2.applyGetInventory(instance3.computeSearchInvResult(items).getValue(), INSTANCE.getXpFactor());
            return Unit.INSTANCE;
         };
         short shortValue = 200;
         Rotation rotation2 = rotation;
         instance.updateRotationAction(new RotationAction(rotation2, shortValue, function0));
      }
   }

   public static final void updateMiddleClick(MiddleClick middleClick) {
      middleClick.applyGetInventory();
   }

   public static final void updateMiddleClick2(MiddleClick middleClick) {
      middleClick.applyGetEntity();
   }

   public static final boolean testMiddleClick(MiddleClick middleClick) {
      return middleClick.getPearlOnRelease();
   }

   public static final void updateMiddleClick3(MiddleClick middleClick) {
      middleClick.applyGetXRot();
   }

   public static final void updateMiddleClick4(MiddleClick middleClick) {
      middleClick.applyGetYRot();
   }

   static {
   }

   public static enum OnBlock {
      FIREWORK(() -> {
         MiddleClick.updateMiddleClick(MiddleClick.INSTANCE);
         return Unit.INSTANCE;
      }, null, 2, null),
      FRIEND(() -> {
         MiddleClick.updateMiddleClick2(MiddleClick.INSTANCE);
         return Unit.INSTANCE;
      }, null, 2, null),
      PEARL(() -> {
         if (!MiddleClick.testMiddleClick(MiddleClick.INSTANCE)) {
            MiddleClick.updateMiddleClick3(MiddleClick.INSTANCE);
         }

         return Unit.INSTANCE;
      }, () -> {
         if (MiddleClick.testMiddleClick(MiddleClick.INSTANCE)) {
            MiddleClick.updateMiddleClick3(MiddleClick.INSTANCE);
         }

         return Unit.INSTANCE;
      }),
      XP(() -> {
         MiddleClick.updateMiddleClick4(MiddleClick.INSTANCE);
         return Unit.INSTANCE;
      }, null, 2, null),
      NONE(() -> Unit.INSTANCE, null, 2, null);

      @NotNull
      private final Function0 callback;
      @NotNull
      private final Function0 callback2;
      private static final MiddleClick.OnBlock[] onBlocks = $values();
      private static final EnumEntries<MiddleClick.OnBlock> enumEntries = EnumEntriesKt.enumEntries(onBlocks);

      private OnBlock(Function0 callback, Function0 callback2) {
         this.callback = callback;
         this.callback2 = callback2;
      }

      OnBlock(Function0 callback, Function0 callback2, int value2, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value2 & 2) != 0) {
            callback2 = () -> Unit.INSTANCE;
         }

         this(callback, callback2);
      }

      @NotNull
      public final Function0 getCallback() {
         return this.callback;
      }

      @NotNull
      public final Function0 getCallback2() {
         return this.callback2;
      }

      @NotNull
      public static EnumEntries<MiddleClick.OnBlock> getEnumEntries() {
         return enumEntries;
      }

      private static final MiddleClick.OnBlock[] $values() {
         return new MiddleClick.OnBlock[]{FIREWORK, FRIEND, PEARL, XP, NONE};
      }
   }

   public static final class Worker extends Thread {
      private int value;
      private int value2;
      private int value3;
      private boolean flag;

      public Worker(int value, int value2, int value3, boolean flag) {
         this.value = value;
         this.value2 = value2;
         this.value3 = value3;
         this.flag = flag;
      }

      @Override
      public void run() {
         try {
         GuiMove.INSTANCE.setFlag5(true);
         if (!this.flag) {
            su.catlean.client.services.InventoryManager.INSTANCE.applyGetInventory2(this.value);
         } else {
            Object object = null;
            byte byteValue = 8;
            boolean flag2 = false;
            ContainerInput swap = ContainerInput.SWAP;
            int value22 = this.value2;
            int value4 = this.value;
            ContainerUtil.defaultImpl(value4, value22, swap, flag2, byteValue, object);
         }

         GuiMove.INSTANCE.setFlag5(false);
         Thread.sleep(this.value3);
         RotationManager instance = RotationManager.INSTANCE;
         Rotation rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
         Function0 function0 = () -> {
            ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
            Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
            PacketUtil.applySend(serverboundSwingPacket);
            return Unit.INSTANCE;
         };
         short shortValue = 200;
         Rotation rotation2 = rotation;
         instance.updateRotationAction(new LookRotationAction(rotation2, shortValue, function0));
         Thread.sleep(this.value3);
         GuiMove.INSTANCE.setFlag5(true);
         if (!this.flag) {
            su.catlean.client.services.InventoryManager.INSTANCE.applyGetInventory2(this.value2);
         } else {
            function0 = null;
            byte byteValue2 = 8;
            boolean flag3 = false;
            ContainerInput swap2 = ContainerInput.SWAP;
            int value23 = this.value2;
            int value5 = this.value;
            ContainerUtil.defaultImpl(value5, value23, swap2, flag3, byteValue2, function0);
         }

         GuiMove.INSTANCE.setFlag5(false);
         super.run();
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            GuiMove.INSTANCE.setFlag5(false);
         }
      }
   }
}