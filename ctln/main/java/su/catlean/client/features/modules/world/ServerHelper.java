// deobf by pusia
package su.catlean.client.features.modules.world;

import su.catlean.utils.KotlinCompat;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;
import org.joml.Vector4d;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.render.SlotRenderEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.render.TexturedQuadPass;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.ClientboundRemoveEntitiesPacketAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.Render2D;

public final class ServerHelper extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ServerHelper.class, "aucHelper", "getAucHelper()Z", 0), new PropertyReference1Impl(ServerHelper.class, "structureCooldowns", "getStructureCooldowns()Z", 0) };
   @NotNull
   public static final ServerHelper INSTANCE = new ServerHelper();
   @NotNull
   private static final BooleanSetting aucHelper = SettingFactory.defaultImpl(INSTANCE, "auc-helper", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting structureCooldowns = SettingFactory.defaultImpl(INSTANCE, "structure-cooldowns", true, null, null, 12, null);
   // [deobf] never assigned in the original bytecode (JVM default false); explicit for compilability.
   private static final boolean flag = false;
   @NotNull
   private static final List<ServerHelper.Consumable> items = new ArrayList();
   @NotNull
   private static final List items2 = new ArrayList();
   @NotNull
   private static final IntArrayList intArrayList = new IntArrayList();

   private ServerHelper() {
      super("server-helper", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final boolean getAucHelper() {
      return (Boolean)aucHelper.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getStructureCooldowns() {
      return (Boolean)structureCooldowns.computeObject(this, $$delegatedProperties[1]);
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (flag && receivePacket.getPacket() instanceof ClientboundRemoveEntitiesPacket) {
         ClientboundRemoveEntitiesPacketAccessor packet = (ClientboundRemoveEntitiesPacketAccessor)receivePacket.getPacket();
         IntArrayList intArrayList2 = new IntArrayList();

         for (Object ids : (Iterable)packet.getIds()) {
            Integer integer = (Integer)ids;
            var players = MinecraftHolder.getClientLevel().players();
            boolean flag2;
            if (players instanceof Collection && ((Collection)players).isEmpty()) {
               flag2 = true;
            } else {
               Iterator<AbstractClientPlayer> iterator = players.iterator();

               while (true) {
                  if (!iterator.hasNext()) {
                     flag2 = true;
                     break;
                  }

                  AbstractClientPlayer object = iterator.next();
                  AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
                  if (integer != null && abstractClientPlayer.getId() == integer) {
                     flag2 = false;
                     break;
                  }
               }
            }

            if (flag2) {
               intArrayList2.add(integer);
            } else {
               intArrayList.add(integer);
            }
         }

         ((ClientboundRemoveEntitiesPacketAccessor)receivePacket.getPacket()).setIds((IntList)intArrayList2);
      }

      if (this.getStructureCooldowns() && receivePacket.getPacket() instanceof ClientboundSoundPacket) {
         if (Intrinsics.areEqual(((ClientboundSoundPacket)receivePacket.getPacket()).getSound().value(), SoundEvents.PISTON_CONTRACT)
            || Intrinsics.areEqual(((ClientboundSoundPacket)receivePacket.getPacket()).getSound().value(), SoundEvents.PISTON_EXTEND)) {
            TaskScheduler.defaultImpl(
               TaskScheduler.INSTANCE,
               0,
               (byte)0,
               false,
               () -> items.add(
                  new ServerHelper.Consumable(
                     new Vec3(
                        ((ClientboundSoundPacket)receivePacket.getPacket()).getX(),
                        ((ClientboundSoundPacket)receivePacket.getPacket()).getY(),
                        ((ClientboundSoundPacket)receivePacket.getPacket()).getZ()
                     ),
                     MinecraftHolder.getTime() + 15000L,
                     Items.NETHERITE_SCRAP
                  )
               ),
               7,
               null
            );
         } else if (Intrinsics.areEqual(((ClientboundSoundPacket)receivePacket.getPacket()).getSound().value(), SoundEvents.ANVIL_PLACE)) {
            BlockPos containing = BlockPos.containing(
               ((ClientboundSoundPacket)receivePacket.getPacket()).getX(),
               ((ClientboundSoundPacket)receivePacket.getPacket()).getY(),
               ((ClientboundSoundPacket)receivePacket.getPacket()).getZ()
            );
            TaskScheduler.defaultImpl(
               TaskScheduler.INSTANCE,
               5,
               (byte)0,
               false,
               () -> {
                  Iterable<BlockPos> iterable = INSTANCE.fetchX(containing, 4, 4);
                  int value = 0;
                  var arrayList = new ArrayList();

                  for (Object object2 : iterable) {
                     BlockPos blockPos = (BlockPos)object2;
                     if (Vec3iUtil.computeVec3(blockPos).distanceTo(Vec3iUtil.computeVec3(containing)) > 2.0
                        && Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(blockPos).getBlock(), Blocks.COBBLESTONE)) {
                        arrayList.add(object2);
                     }
                  }

                  iterable = arrayList;
                  value = ((boolean)false ? 1 : 0);
                  Iterator<BlockPos> iterator2 = iterable.iterator();
                  Object object3;
                  if (!iterator2.hasNext()) {
                     object3 = null;
                  } else {
                     BlockPos object4 = iterator2.next();
                     if (!iterator2.hasNext()) {
                        object3 = object4;
                     } else {
                        BlockPos blockPos2 = (BlockPos)object4;
                        double distanceTo = Vec3iUtil.computeVec3(blockPos2).distanceTo(Vec3iUtil.computeVec3(containing));

                        do {
                           BlockPos object5 = iterator2.next();
                           BlockPos blockPos3 = (BlockPos)object5;
                           double distanceTo2 = Vec3iUtil.computeVec3(blockPos3).distanceTo(Vec3iUtil.computeVec3(containing));
                           if (Double.compare(distanceTo, distanceTo2) > 0) {
                              object4 = object5;
                              distanceTo = distanceTo2;
                           }
                        } while (iterator2.hasNext());

                        object3 = object4;
                     }
                  }

                  BlockPos blockPos4 = (BlockPos)object3;
                  if (blockPos4 != null) {
                     var value2 = INSTANCE.fetchX(blockPos4, 1, 0);
                     int value3;
                     if (value2 instanceof Collection && ((Collection)value2).isEmpty()) {
                        value3 = 0;
                     } else {
                        int value4 = 0;

                        for (Object object6 : value2) {
                           BlockPos blockPos5 = (BlockPos)object6;
                           if (Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(blockPos5).getBlock(), Blocks.ANDESITE)) {
                              if (++value4 < 0) {
                                 CollectionsKt.throwCountOverflow();
                              }
                           }
                        }

                        value3 = value4;
                     }

                     int value5 = value3;
                     value = value5 == 8 ? '\uea60' : 20000;
                     items.add(
                        new ServerHelper.Consumable(
                           Vec3iUtil.computeVec3(blockPos4).add(0.0, value5 == 8 ? -0.5 : 0.0, 0.0), MinecraftHolder.getTime() + value - 250, Items.DRIED_KELP
                        )
                     );
                  }
               },
               6,
               null
            );
         }
      }
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (this.getStructureCooldowns()) {
         items.removeIf(arg -> MinecraftHolder.getTime() - ((ServerHelper.Consumable)arg).getTime() >= 0L);

         for (Object item : items) {
            ServerHelper.Consumable consumable = (ServerHelper.Consumable)item;
            Vector4d vector4d = Render2D.INSTANCE.computeVector4d(consumable.getPos());
            int time = (int)((double)(consumable.getTime() - MinecraftHolder.getTime()) / 50.0F);
            int value = time / 1200;
            String text = "%02d";
            Object[] objects = new Object[]{time % 1200 / 20};
            String text2 = value + ":" + String.format(text, Arrays.copyOf(objects, objects.length));
            float amount = Fonts.getFontRenderer().computeAmount(text2) + 22.0F;
            if (vector4d != null) {
               float value2 = (float)(vector4d.x + (float)(vector4d.z - vector4d.x) / 2.0F - amount / 2);
               float value3 = (float)vector4d.y;
               TexturedQuadPass.defaultImpl(
                  render2DEvent.getContext().pose(),
                  value2,
                  value3,
                  amount,
                  16.0F,
                  3.0F,
                  1.5F,
                  0.0F,
                  1.0F,
                  ThemeManager.INSTANCE.getColor5(),
                  ThemeManager.INSTANCE.getColor6(),
                  0.0F,
                  0.0F,
                  0.0F,
                  14336,
                  null
               );
               render2DEvent.getContext().pose().pushMatrix();
               render2DEvent.getContext().pose().translate(value2 + 2.0F, value3 + 2.0F);
               render2DEvent.getContext().pose().scale(0.7F);
               render2DEvent.getContext().item(consumable.getItem().getDefaultInstance(), 0, 0);
               render2DEvent.getContext().pose().popMatrix();
               Render2D instance = Render2D.INSTANCE;
               Matrix3x2fStack pose = render2DEvent.getContext().pose();
               float value4 = value2 + 14;
               float value5 = value3 + 4.0F;
               Color[] colors = new Color[]{new Color(1157627903, true)};
               float value6 = 9.0F;
               float value7 = 0.5F;
               float value8 = value5;
               float value9 = value4;
               Matrix3x2fStack matrix3x2fStack = pose;
               instance.render(matrix3x2fStack, value9, value8, value7, value6, colors);
               Fonts.getFontRenderer().render(render2DEvent.getContext(), text2, value2 + 18.0F, value3 + 3.0F, ThemeManager.INSTANCE.getColor7());
            }
         }
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getLocalPlayer().containerMenu instanceof ChestMenu && this.getAucHelper()) {
         ChestMenu containerMenu = (ChestMenu)MinecraftHolder.getLocalPlayer().containerMenu;
         String string = MinecraftHolder.getMinecraft().gui.screen().getTitle().getString();
         if (KotlinCompat.contains(string, "РђСѓРєС†РёРѕРЅ", false, 2, null) || KotlinCompat.contains(string, "РџРѕРёСЃРє", false, 2, null)) {
            items2.clear();
            Map linkedHashMap = new LinkedHashMap();
            int value = 0;

            for (ItemStack item : containerMenu.getItems()) {
               if (value <= 44) {
                  int string2 = this.fetchString(item);
                  if (item.getCount() > 1) {
                     string2 /= item.getCount();
                  }

                  linkedHashMap.put(this.fetchDescriptionId(item), Math.min(((Number)linkedHashMap.getOrDefault(this.fetchDescriptionId(item), 999999999)).intValue(), string2));
                  value++;
               }
            }

            value = 0;

            for (ItemStack item2 : containerMenu.getItems()) {
               if (linkedHashMap.get(this.fetchDescriptionId(item2)) != null) {
                  int string3 = this.fetchString(item2);
                  if (item2.getCount() > 1) {
                     string3 /= item2.getCount();
                  }

                  List<ServerHelper.AucItem> items22 = items2;
                  Item item3 = item2.getItem();
                  Integer descriptionId = (Integer)linkedHashMap.get(this.fetchDescriptionId(item2));
                  items22.add(new ServerHelper.AucItem(item3, string3, descriptionId != null ? descriptionId : 99999999, value));
               }

               value++;
            }
         }
      }

      if (flag) {
         intArrayList.removeIf(
            arg -> {
               Entity entity = MinecraftHolder.getClientLevel().getEntity(arg);
               if (entity != null
                  && entity instanceof Player
                  && ((Player)entity).isAlive()
                  && MinecraftHolder.getClientLevel().isLoaded(((Player)entity).blockPosition())) {
                  return false;
               } else {
                  MinecraftHolder.getClientLevel().removeEntity(arg, RemovalReason.KILLED);
                  return true;
               }
            }
         );
      }
   }

   private final String fetchDescriptionId(ItemStack itemStack) {
      return itemStack.getItem().getDescriptionId();
   }

   @Flow
   private final void onSlotRender(SlotRenderEvent slotRenderEvent) {
      Slot slot = slotRenderEvent.getSlot();
      GuiGraphicsExtractor context = slotRenderEvent.getContext();
      if (MinecraftHolder.getLocalPlayer().containerMenu instanceof ChestMenu) {
         String string = MinecraftHolder.getMinecraft().gui.screen().getTitle().getString();
         byte byteValue = 4;
         if (KotlinCompat.contains(string, "РђСѓРєС†РёРѕРЅ", false, 2, null) || KotlinCompat.contains(string, "РџРѕРёСЃРє", false, 2, null)) {
            for (ServerHelper.AucItem items2Item : (Iterable<su.catlean.client.features.modules.world.ServerHelper.AucItem>)items2) {
               if (items2Item.getValue3() == slot.index && slot.index <= 44 && !slot.getItem().isEmpty()) {
                  float value2 = (float)Math.pow(items2Item.getValue2(), byteValue) / (float)Math.pow(items2Item.getValue(), byteValue);
                  context.fill(slot.x, slot.y, slot.x + 16, slot.y + 16, Render2D.INSTANCE.computeColor(Color.GREEN, value2).getRGB());
                  return;
               }
            }
         }
      }
   }

   private final int fetchString(ItemStack itemStack) {
      String var21 = null;
      ItemLore lore = (ItemLore)itemStack.getComponents().get(DataComponents.LORE);
      if (lore == null) {
         return 999999999;
      } else {
         ItemLore itemLore = lore;

         try {
            label64: {
               ServerData serverData = MinecraftHolder.getClientPacketListener().getServerData();
               if (serverData != null) {
                  var21 = serverData.ip;
                  if (serverData.ip != null) {
                     break label64;
                  }
               }

               var21 = "singleplayer";
            }

            if (KotlinCompat.contains(var21, "holyworld", false, 2, null)) {
               for (Component line : itemLore.lines()) {
                  if (KotlinCompat.contains(line.getString(), "Р¦РµРЅР° Р·Р° 1 РµРґ.", false, 2, null)) {
                     for (Component sibling : line.getSiblings()) {
                        String string = sibling.getString();
                        CharSequence charSequence = string;
                        int value = 0;

                        for (int k = 0; k < charSequence.length(); k++) {
                           char charAt = charSequence.charAt(k);
                           if (Character.isDigit(charAt)) {
                              value++;
                           }
                        }

                        if (value > 1) {
                           return Integer.parseInt(
                              KotlinCompat.replace(KotlinCompat.replace(string, " ", "", false, 4, null), "В¤", "", false, 4, null)
                           );
                        }
                     }
                  }
               }
            } else {
               for (Component line2 : itemLore.lines()) {
                  if (KotlinCompat.contains(line2.getString(), "Р¦РµРЅa", false, 2, null)) {
                     return Integer.parseInt(
                        KotlinCompat.replace(KotlinCompat.replace(line2.getString(), "$ Р¦РµРЅa $", "", false, 4, null), ",", "", false, 4, null)
                     );
                  }
               }
            }
         } catch (Exception exception) {
         }

         return 9999999;
      }
   }

   private final List<BlockPos> fetchX(BlockPos blockPos, int value, int value2) {
      List<BlockPos> arrayList = new ArrayList();
      int value3 = blockPos.getX() - value;
      int value4 = blockPos.getX() + value;
      if (value3 <= value4) {
         while (true) {
            int value5 = blockPos.getZ() - value;
            int value6 = blockPos.getZ() + value;
            if (value5 <= value6) {
               while (true) {
                  int value22 = blockPos.getY() - value2;
                  int value23 = blockPos.getY() + value2;
                  if (value22 <= value23) {
                     while (true) {
                        arrayList.add(new BlockPos(value3, value22, value5));
                        if (value22 == value23) {
                           break;
                        }

                        value22++;
                     }
                  }

                  if (value5 == value6) {
                     break;
                  }

                  value5++;
               }
            }

            if (value3 == value4) {
               break;
            }

            value3++;
         }
      }

      return arrayList;
   }

   static {
   }

   public static final class AucItem {
      @NotNull
      private final Item item;
      private final int value;
      private final int value2;
      private final int value3;

      public AucItem(Item item, int value, int value2, int value3) {
         this.item = item;
         this.value = value;
         this.value2 = value2;
         this.value3 = value3;
      }

      @NotNull
      public final Item getItem() {
         return this.item;
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
      public final Item getItem2() {
         return this.item;
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
      @Override
      public String toString() {
         int value32 = this.value3;
         int value22 = this.value2;
         int value4 = this.value;
         Item item2 = this.item;
         return "AucItem(item=" + item2 + ", price=" + value4 + ", lowestPrice=" + value22 + ", id=" + value32 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.item.hashCode();
         hashCode = hashCode * 31 + Integer.hashCode(this.value);
         hashCode = hashCode * 31 + Integer.hashCode(this.value2);
         return hashCode * 31 + Integer.hashCode(this.value3);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof ServerHelper.AucItem aucItem)) {
            return false;
         } else if (!Intrinsics.areEqual(this.item, aucItem.item)) {
            return false;
         } else if (this.value != aucItem.value) {
            return false;
         } else {
            return this.value2 != aucItem.value2 ? false : this.value3 == aucItem.value3;
         }
      }
   }

   public static final class Consumable {
      @NotNull
      private final Vec3 pos;
      private final long time;
      @NotNull
      private final Item item;

      public Consumable(Vec3 pos, long time, Item item) {
         this.pos = pos;
         this.time = time;
         this.item = item;
      }

      @NotNull
      public final Vec3 getPos() {
         return this.pos;
      }

      public final long getTime() {
         return this.time;
      }

      @NotNull
      public final Item getItem() {
         return this.item;
      }

      @NotNull
      public final Vec3 getPos2() {
         return this.pos;
      }

      public final long getTime2() {
         return this.time;
      }

      @NotNull
      public final Item getItem2() {
         return this.item;
      }

      @NotNull
      @Override
      public String toString() {
         Item item2 = this.item;
         long time2 = this.time;
         Vec3 pos2 = this.pos;
         return "Consumable(pos=" + pos2 + ", expiresOn=" + time2 + ", item=" + item2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.pos.hashCode();
         hashCode = hashCode * 31 + Long.hashCode(this.time);
         return hashCode * 31 + this.item.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof ServerHelper.Consumable consumable)) {
            return false;
         } else if (!Intrinsics.areEqual(this.pos, consumable.pos)) {
            return false;
         } else {
            return this.time != consumable.time ? false : Intrinsics.areEqual(this.item, consumable.item);
         }
      }
   }
}