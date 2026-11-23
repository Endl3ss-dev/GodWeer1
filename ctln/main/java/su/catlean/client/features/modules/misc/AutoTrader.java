// deobf by pusia
package su.catlean.client.features.modules.misc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.settings.ItemsSetting;
import su.catlean.api.setting.values.ItemsValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.ui.ClickGuiScreen;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;

public final class AutoTrader extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(AutoTrader.class, "buy", "getBuy()Z", 0), new MutablePropertyReference1Impl(AutoTrader.class, "buyItem", "getBuyItem()Lsu/catlean/s4;", 0), new PropertyReference1Impl(AutoTrader.class, "sell", "getSell()Z", 0), new MutablePropertyReference1Impl(AutoTrader.class, "sellItem", "getSellItem()Lsu/catlean/s4;", 0), new MutablePropertyReference1Impl(AutoTrader.class, "interactDelay", "getInteractDelay()I", 0), new MutablePropertyReference1Impl(AutoTrader.class, "tradeDelay", "getTradeDelay()I", 0) };
   @NotNull
   public static final AutoTrader INSTANCE = new AutoTrader();
   @NotNull
   private static final BooleanSetting buy = SettingFactory.defaultImpl(INSTANCE, "buy", true, null, null, 12, null);
   @NotNull
   private static final ItemsSetting buyItem = SettingFactory.defaultImpl(INSTANCE, "buy-item", new ItemsValue(null, 1, null), null, null, 12, null);
   @NotNull
   private static final BooleanSetting sell = SettingFactory.defaultImpl(INSTANCE, "sell", false, null, null, 12, null);
   @NotNull
   private static final ItemsSetting sellItem = SettingFactory.defaultImpl(INSTANCE, "sell-item", new ItemsValue(null, 1, null), null, null, 12, null);
   @NotNull
   private static final IntSetting openDelay = SettingFactory.defaultImpl(INSTANCE, "open-delay", 12, new IntRange(0, 40), null, null, 24, null);
   @NotNull
   private static final IntSetting tradeDelay = SettingFactory.defaultImpl(INSTANCE, "trade-delay", 3, new IntRange(0, 40), null, null, 24, null);
   private static int value;
   private static int value2;
   private static int value3;
   @NotNull
   private static final Map<Integer, Integer> map = new LinkedHashMap();

   private AutoTrader() {
      super("auto-trader", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final boolean getBuy() {
      return (Boolean)buy.computeObject(this, $$delegatedProperties[0]);
   }

   private final ItemsValue getBuyItem() {
      return (ItemsValue)buyItem.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getSell() {
      return (Boolean)sell.computeObject(this, $$delegatedProperties[2]);
   }

   private final ItemsValue getSellItem() {
      return (ItemsValue)sellItem.computeObject(this, $$delegatedProperties[3]);
   }

   private final int getInteractDelay() {
      return ((Number)openDelay.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final int getTradeDelay() {
      return ((Number)tradeDelay.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (value > 0) {
         int value4 = value;
         value = value4 + -1;
      }

      if (value2 > 0) {
         int value22 = value2;
         value2 = value22 + -1;
      } else {
         map.entrySet().removeIf(arg -> {
            int value5 = ((Number)arg.getValue()).intValue();
            return MinecraftHolder.getLocalPlayer().tickCount - value5 > 160;
         });
         if (MinecraftHolder.getMinecraft().gui.screen() instanceof MerchantScreen) {
            MerchantScreen screen = (MerchantScreen)MinecraftHolder.getMinecraft().gui.screen();
            MerchantMenu menu = (MerchantMenu)screen.getMenu();
            MerchantOffers offers = menu.getOffers();
            int selectionHint = 0;

            for (int i = ((Collection)offers).size(); selectionHint < i; selectionHint++) {
               MerchantOffer merchantOffer = (MerchantOffer)offers.get(selectionHint);
               if (this.checkGetCostA(merchantOffer)) {
                  menu.tryMoveItems(selectionHint);
                  menu.setSelectionHint(selectionHint);
                  Packet serverboundSelectTradePacket = (Packet)(new ServerboundSelectTradePacket(selectionHint));
                  PacketUtil.applySend(serverboundSelectTradePacket);
                  GuiMove.INSTANCE.setFlag5(true);
                  ContainerUtil.defaultImpl(2, 0, ContainerInput.QUICK_MOVE, false, 8, null);
                  GuiMove.INSTANCE.setFlag5(false);
                  value2 = this.getTradeDelay();
                  return;
               }

               if (!menu.getSlot(0).getItem().isEmpty()) {
                  GuiMove.INSTANCE.setFlag5(true);
                  ContainerUtil.defaultImpl(0, 0, ContainerInput.QUICK_MOVE, false, 8, null);
                  GuiMove.INSTANCE.setFlag5(false);
                  value2 = this.getTradeDelay();
                  return;
               }

               if (!menu.getSlot(1).getItem().isEmpty()) {
                  GuiMove.INSTANCE.setFlag5(true);
                  ContainerUtil.defaultImpl(1, 0, ContainerInput.QUICK_MOVE, false, 8, null);
                  GuiMove.INSTANCE.setFlag5(false);
                  value2 = this.getTradeDelay();
                  return;
               }

               map.put(value3, MinecraftHolder.getLocalPlayer().tickCount);
            }

            MinecraftHolder.getLocalPlayer().closeContainer();
         } else if (value <= 0 && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ClickGuiScreen)) {
            Iterable<Entity> entitiesForRendering = MinecraftHolder.getClientLevel().entitiesForRendering();
            boolean flag = false;
            var arrayList = new ArrayList();

            for (Object object : entitiesForRendering) {
               Entity entity = (Entity)object;
               if (entity instanceof Villager && MinecraftHolder.getLocalPlayer().distanceToSqr(entity) < 16.0 && !map.containsKey(((Villager)entity).getId())) {
                  arrayList.add(object);
               }
            }

            entitiesForRendering = arrayList;
            flag = false;
            Iterator<Entity> iterator = entitiesForRendering.iterator();
            Entity object2;
            if (!iterator.hasNext()) {
               object2 = null;
            } else {
               Entity object3 = iterator.next();
               if (!iterator.hasNext()) {
                  object2 = object3;
               } else {
                  Entity entity2 = (Entity)object3;
                  float distanceTo = entity2.distanceTo((Entity)MinecraftHolder.getLocalPlayer());

                  do {
                     Entity object4 = iterator.next();
                     Entity entity3 = (Entity)object4;
                     float distanceTo2 = entity3.distanceTo((Entity)MinecraftHolder.getLocalPlayer());
                     if (Float.compare(distanceTo, distanceTo2) > 0) {
                        object3 = object4;
                        distanceTo = distanceTo2;
                     }
                  } while (iterator.hasNext());

                  object2 = object3;
               }
            }

            Entity entity4 = (Entity)object2;
            if (entity4 != null) {
               RotationManager.INSTANCE
                  .updateRotationAction(
                     new RotationAction(
                        Rotation.vectorUtil.computeRotation(entity4.getEyePosition().add(Math.random() * 0.2, 0.0, Math.random() * 0.2)), 0, () -> {
                           EntityHitResult entityHitResult = new EntityHitResult(entity4, entity4.getBoundingBox().getCenter());
                           MinecraftHolder.getMultiPlayerGameMode().interact((Player)MinecraftHolder.getLocalPlayer(), entity4, entityHitResult, InteractionHand.MAIN_HAND);
                           value3 = entity4.getId();
                           value = INSTANCE.getInteractDelay();
                           return Unit.INSTANCE;
                        }, 2, null
                     )
                  );
            }
         }
      }
   }

   private final boolean checkGetCostA(MerchantOffer merchantOffer) {
      for (Item item : (Iterable<Item>)this.getBuyItem().getItems()) {
         boolean areEqual = Intrinsics.areEqual(merchantOffer.getResult().getItem(), item);
         int count = merchantOffer.getCostA().getCount();
         InventoryManager instance = InventoryManager.INSTANCE;
         Item item2 = merchantOffer.getCostA().getItem();
         Object object = null;
         byte byteValue = 2;
         boolean flag = false;
         Item item3 = item2;
         boolean flag2 = count <= InventoryManager.defaultImpl(instance, item3, flag, byteValue, object);
         boolean flag3 = areEqual && !merchantOffer.isOutOfStock() && this.getBuy();
         if ((!flag3 || flag2) && flag3) {
            return true;
         }
      }

      for (Item item4 : (Iterable<Item>)this.getSellItem().getItems()) {
         boolean areEqual2 = Intrinsics.areEqual(merchantOffer.getCostA().getItem(), item4);
         int count2 = merchantOffer.getCostA().getCount();
         InventoryManager instance2 = InventoryManager.INSTANCE;
         Item item5 = merchantOffer.getCostA().getItem();
         Object object2 = null;
         byte byteValue2 = 2;
         boolean flag4 = false;
         Item item6 = item5;
         boolean flag5 = count2 <= InventoryManager.defaultImpl(instance2, item6, flag4, byteValue2, object2);
         boolean flag6 = areEqual2 && !merchantOffer.isOutOfStock() && this.getSell();
         if ((!flag6 || flag5) && flag6) {
            return true;
         }
      }

      return false;
   }

   static {
   }
}