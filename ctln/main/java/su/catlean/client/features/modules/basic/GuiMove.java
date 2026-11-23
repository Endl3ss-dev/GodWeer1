// deobf by pusia
package su.catlean.client.features.modules.basic;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerClosePacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket.Pos;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.ClickSlotEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ClientInputAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.world.WorldUtil;

public final class GuiMove extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{new PropertyReference1Impl(GuiMove.class, "clickBypass", "getClickBypass()Lsu/catlean/q2;", 0)};
   @NotNull
   public static final GuiMove INSTANCE = new GuiMove();
   @NotNull
   private static final EnumSetting bypass = SettingFactory.defaultImpl(INSTANCE, "bypass", GuiMove.Bypass.NONE, null, null, 12, null);
   @NotNull
   private static List<Object> items = CollectionsKt.emptyList();
   @NotNull
   private static final LinkedList<GuiMove.ClickData> linkedList = new LinkedList();
   @NotNull
   private static final AtomicBoolean atomicBoolean = new AtomicBoolean();
   private static boolean flag;
   private static boolean flag2;
   private static int value;
   private static boolean flag3;
   private static boolean flag4;
   @NotNull
   private static final Map map = new LinkedHashMap();
   @NotNull
   private static final Map map2 = new LinkedHashMap();
   private static boolean flag5;

   private GuiMove() {
      super("gui-move", SubCategories.getSubCategory4(), null, 4, null);
   }

   @NotNull
   public final GuiMove.Bypass getClickBypass() {
      return (GuiMove.Bypass)bypass.computeObject(this, $$delegatedProperties[0]);
   }

   @NotNull
   public final Map getMap() {
      return map;
   }

   @NotNull
   public final Map getMap2() {
      return map2;
   }

   public final boolean isFlag5() {
      return flag5;
   }

   public final void setFlag5(boolean flag) {
      flag5 = flag;
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (items.isEmpty()) {
         KeyMapping[] keyMappings = new KeyMapping[]{
            MinecraftHolder.getMinecraft().options.keyUp,
            MinecraftHolder.getMinecraft().options.keyDown,
            MinecraftHolder.getMinecraft().options.keyLeft,
            MinecraftHolder.getMinecraft().options.keyRight,
            MinecraftHolder.getMinecraft().options.keyJump,
            MinecraftHolder.getMinecraft().options.keySprint
         };
         items = CollectionsKt.listOf(keyMappings);
      }

      if ((!flag4 || this.getClickBypass() != GuiMove.Bypass.LEGIT)
         && MinecraftHolder.getMinecraft().gui.screen() != null
         && !(MinecraftHolder.getMinecraft().gui.screen() instanceof ChatScreen)) {
         Iterable<Object> items2 = items;

         for (Object object : items2) {
            KeyMapping keyMapping = (KeyMapping)object;
            keyMapping.setDown(InputUtil.checkContains(InputConstants.getKey(keyMapping.saveString()).getValue()));
         }
      }

      if (this.getClickBypass() == GuiMove.Bypass.LEGIT && !linkedList.isEmpty() && flag || value != 0) {
         switch (value) {
            case 0:
               Sprint.INSTANCE.setFlag(true);
               flag3 = MinecraftHolder.getLocalPlayer().isSprinting();
               MinecraftHolder.getLocalPlayer().setSprinting(false);
               WorldUtil.INSTANCE.applySetDown();
               value = 1;
               break;
            case 1:
               Sprint.INSTANCE.setFlag(true);
               atomicBoolean.set(true);
               this.applyCopy(map2);
               this.applyGetStateId(map);

               while (!linkedList.isEmpty()) {
                  GuiMove.ClickData poll = (GuiMove.ClickData)linkedList.poll();
                  MultiPlayerGameMode gameMode = MinecraftHolder.getMinecraft().gameMode;
                  if (gameMode != null) {
                     gameMode.handleContainerInput(
                        poll.getValue(), poll.getValue2(), poll.getValue3(), poll.getActionType(), (Player)MinecraftHolder.getLocalPlayer()
                     );
                  }
               }

               Packet serverboundContainerClosePacket = (Packet)(new ServerboundContainerClosePacket(MinecraftHolder.getLocalPlayer().containerMenu.containerId));
               PacketUtil.applySend(serverboundContainerClosePacket);
               atomicBoolean.set(false);
               flag = false;
               Sprint.INSTANCE.setFlag(true);
               MinecraftHolder.getLocalPlayer().setSprinting(flag3);
               WorldUtil.INSTANCE.applyGetKey();
               value = 0;
               this.applyGetStateId(map2);
         }
      }
   }

   @Flow
   private final void onClickSlot(ClickSlotEvent clickSlotEvent) {
      if (!flag5 && !atomicBoolean.get()) {
         if ((WorldUtil.INSTANCE.isFlag4() || MinecraftHolder.getMinecraft().options.keyJump.isDown()) && !MinecraftHolder.getLocalPlayer().isFallFlying()) {
            switch (GuiMove.WhenMappings.values[this.getClickBypass().ordinal()]) {
               case 1:
               case 2:
                  this.applyClicked(
                     clickSlotEvent.getSyncId(),
                     clickSlotEvent.getId(),
                     clickSlotEvent.getButton(),
                     clickSlotEvent.getAction(),
                     (Player)MinecraftHolder.getLocalPlayer()
                  );
                  double value2 = this.getClickBypass() == GuiMove.Bypass.STRICT_NCP ? 0.0656 : 2.71875E-7;
                  if (MinecraftHolder.getLocalPlayer().onGround()
                     && !MinecraftHolder.getClientLevel()
                        .getBlockCollisions((Entity)MinecraftHolder.getLocalPlayer(), MinecraftHolder.getLocalPlayer().getBoundingBox().move(0.0, value2, 0.0))
                        .iterator()
                        .hasNext()) {
                     if (MinecraftHolder.getLocalPlayer().isSprinting()) {
                        Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.STOP_SPRINTING));
                        PacketUtil.applySend(serverboundPlayerCommandPacket);
                        flag2 = true;
                     }

                     Packet pos = (Packet)(
                        new Pos(
                           MinecraftHolder.getLocalPlayer().getX(),
                           MinecraftHolder.getLocalPlayer().getY() + value2,
                           MinecraftHolder.getLocalPlayer().getZ(),
                           false,
                           MinecraftHolder.getLocalPlayer().horizontalCollision
                        )
                     );
                     PacketUtil.applySend(pos);
                  }
                  break;
               case 3:
                  this.applyClicked(
                     clickSlotEvent.getSyncId(),
                     clickSlotEvent.getId(),
                     clickSlotEvent.getButton(),
                     clickSlotEvent.getAction(),
                     (Player)MinecraftHolder.getLocalPlayer()
                  );
                  Packet serverboundPlayerCommandPacket2 = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.STOP_SPRINTING));
                  PacketUtil.applySend(serverboundPlayerCommandPacket2);
                  MinecraftHolder.getMinecraft().options.keyUp.setDown(false);
                  ((ClientInputAccessor)MinecraftHolder.getLocalPlayer().input)
                     .setMoveVector(new Vec2(MinecraftHolder.getLocalPlayer().input.getMoveVector().x, 0.0F));
                  break;
               case 4:
                  if (MinecraftHolder.getLocalPlayer().containerMenu.slots.size() != 46) {
                     return;
                  }

                  if (linkedList.isEmpty()) {
                     this.applyCopy(map);
                  }

                  this.applyClicked(
                     clickSlotEvent.getSyncId(),
                     clickSlotEvent.getId(),
                     clickSlotEvent.getButton(),
                     clickSlotEvent.getAction(),
                     (Player)MinecraftHolder.getLocalPlayer()
                  );
                  linkedList.add(
                     new GuiMove.ClickData(clickSlotEvent.getSyncId(), clickSlotEvent.getId(), clickSlotEvent.getButton(), clickSlotEvent.getAction())
                  );
                  clickSlotEvent.cancel();
                  break;
               case 5:
                  clickSlotEvent.cancel();
            }
         } else {
            flag4 = true;
         }
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (!flag5) {
         if (receivePacket.getPacket() instanceof ClientboundContainerClosePacket) {
            flag4 = false;
            if (this.getClickBypass() == GuiMove.Bypass.DISABLE_CLICKS) {
               receivePacket.cancel();
            }

            if (this.getClickBypass() == GuiMove.Bypass.LEGIT && !atomicBoolean.get() && !linkedList.isEmpty()) {
               receivePacket.cancel();
               flag = true;
            }
         }

         if (receivePacket.getPacket() instanceof ClientboundOpenScreenPacket) {
            flag4 = true;
         }
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (!flag5) {
         if (sendPacket.getPacket() instanceof ServerboundContainerClosePacket) {
            flag4 = false;
            if (this.getClickBypass() == GuiMove.Bypass.DISABLE_CLICKS) {
               sendPacket.cancel();
            }

            if (this.getClickBypass() == GuiMove.Bypass.LEGIT && !atomicBoolean.get()) {
               if (!linkedList.isEmpty()) {
                  flag = true;
               }

               sendPacket.cancel();
            }
         }
      }
   }

   @Flow
   private final void onAfterSendPacket(AfterSendPacket afterSendPacket) {
      if (!flag5) {
         if (afterSendPacket.getPacket() instanceof ServerboundContainerClickPacket && flag2 && this.getClickBypass() == GuiMove.Bypass.STRICT_NCP) {
            Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_SPRINTING));
            PacketUtil.applySend(serverboundPlayerCommandPacket);
            flag2 = false;
         }
      }
   }

   private final void applyClicked(int value, int value2, int value3, ContainerInput containerInput, Player player) {
      if (value == player.containerMenu.containerId) {
         player.containerMenu.clicked(value2, value3, containerInput, player);
      }
   }

   private final void applyCopy(Map map) {
      for (int i = 0; i < 46; i++) {
         map.put(i, MinecraftHolder.getLocalPlayer().containerMenu.getSlot(i).getItem().copy());
      }
   }

   private final void applyGetStateId(Map map) {
      for (int i = 0; i < 46; i++) {
         MinecraftHolder.getLocalPlayer().containerMenu.setItem(i, MinecraftHolder.getLocalPlayer().containerMenu.getStateId(), (ItemStack)map.get(i));
      }
   }

   static {
   }

   public static enum Bypass {
      NONE,
      DISABLE_CLICKS,
      STRICT_NCP,
      STRICT_NCP_2,
      MATRIX_NCP,
      LEGIT;

      private static final GuiMove.Bypass[] bypasss = $values();
      private static final EnumEntries<GuiMove.Bypass> enumEntries = EnumEntriesKt.enumEntries(bypasss);

      private Bypass() {
      }

      @NotNull
      public static EnumEntries<GuiMove.Bypass> getEnumEntries() {
         return enumEntries;
      }

      private static final GuiMove.Bypass[] $values() {
         return new GuiMove.Bypass[]{NONE, DISABLE_CLICKS, STRICT_NCP, STRICT_NCP_2, MATRIX_NCP, LEGIT};
      }
   }

   public static final class ClickData {
      private final int value;
      private final int value2;
      private final int value3;
      @NotNull
      private final ContainerInput actionType;

      public ClickData(int value, int value2, int value3, ContainerInput actionType) {
         this.value = value;
         this.value2 = value2;
         this.value3 = value3;
         this.actionType = actionType;
      }

      public final int getValue() {
         return this.value;
      }

      public final int getValue2() {
         return this.value2;
      }

      public final int getValue3() {
         return this.value3;
      }

      @NotNull
      public final ContainerInput getActionType() {
         return this.actionType;
      }

      public final int getValue4() {
         return this.value;
      }

      public final int getValue5() {
         return this.value2;
      }

      public final int getValue6() {
         return this.value3;
      }

      @NotNull
      public final ContainerInput getActionType2() {
         return this.actionType;
      }

      @NotNull
      @Override
      public String toString() {
         ContainerInput actionType2 = this.actionType;
         int value32 = this.value3;
         int value22 = this.value2;
         int value4 = this.value;
         return "ClickData(syncId=" + value4 + ", slotId=" + value22 + ", button=" + value32 + ", actionType=" + actionType2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = Integer.hashCode(this.value);
         hashCode = hashCode * 31 + Integer.hashCode(this.value2);
         hashCode = hashCode * 31 + Integer.hashCode(this.value3);
         return hashCode * 31 + this.actionType.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof GuiMove.ClickData clickData)) {
            return false;
         } else if (this.value != clickData.value) {
            return false;
         } else if (this.value2 != clickData.value2) {
            return false;
         } else {
            return this.value3 != clickData.value3 ? false : this.actionType == clickData.actionType;
         }
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[GuiMove.Bypass.values().length];

         try {
            values2[GuiMove.Bypass.STRICT_NCP.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[GuiMove.Bypass.STRICT_NCP_2.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[GuiMove.Bypass.MATRIX_NCP.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         try {
            values2[GuiMove.Bypass.LEGIT.ordinal()] = 4;
         } catch (NoSuchFieldError exception4) {
         }

         try {
            values2[GuiMove.Bypass.DISABLE_CLICKS.ordinal()] = 5;
         } catch (NoSuchFieldError exception5) {
         }

         values = values2;
      }
   }
}