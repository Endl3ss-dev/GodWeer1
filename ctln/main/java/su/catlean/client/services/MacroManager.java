// deobf by pusia
package su.catlean.client.services;

import su.catlean.utils.KotlinCompat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.KeyBind;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.service.Service;
import su.catlean.api.setting.Setting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.basic.Sprint;
import su.catlean.client.features.ui.hud.widgets.KeybindsWidget;
import su.catlean.client.services.client.macro.ChatMacro;
import su.catlean.client.services.client.macro.ItemMacro;
import su.catlean.client.services.client.macro.Macro;
import su.catlean.client.services.client.macro.SettingMacro;
import su.catlean.client.services.proxy.ProxyManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.MinecraftAccessor;
import su.catlean.utils.KeyNames;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.world.WorldUtil;

public final class MacroManager implements Service {
   @NotNull
   public static final MacroManager INSTANCE = new MacroManager();
   @Nullable
   private static Function0 callback;
   private static int value;
   private static boolean flag;
   @NotNull
   private static final Map<String, Item> map = new LinkedHashMap();

   private MacroManager() {
   }

   @NotNull
   public final List getItems() {
      return ProxyManager.INSTANCE.getMacroDataSource().getItems();
   }

   public final boolean testMacro(Macro macro) {
      return this.getItems().add(macro);
   }

   public final boolean checkRemove(Macro macro) {
      return this.getItems().remove(macro);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (callback != null) {
         switch (value) {
            case 0:
               Sprint.INSTANCE.setFlag(true);
               flag = MinecraftHolder.getLocalPlayer().isSprinting();
               MinecraftHolder.getLocalPlayer().setSprinting(false);
               WorldUtil.INSTANCE.applySetDown();
               value = 1;
               break;
            case 1:
               Sprint.INSTANCE.setFlag(true);
               GuiMove.INSTANCE.setFlag5(true);
               if (callback != null) {
                  callback.invoke();
               }

               GuiMove.INSTANCE.setFlag5(false);
               Sprint.INSTANCE.setFlag(true);
               MinecraftHolder.getLocalPlayer().setSprinting(flag);
               WorldUtil.INSTANCE.applyGetKey();
               value = 0;
               callback = null;
         }
      }
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if (inputEvent.getKey() != -1
         && MinecraftHolder.getMinecraft().level != null
         && MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().gui.screen() == null
         && inputEvent.getAction() != InputEvent.Action.Hold) {
         Iterable items = this.getItems();
         var arrayList = new ArrayList();

         for (Object object : items) {
            Macro macro = (Macro)object;
            if (macro.getBind().getValue() == inputEvent.getKey() && macro.isFlag()) {
               arrayList.add(object);
            }
         }

         for (Object object2 : arrayList) {
            Macro macro2 = (Macro)object2;
            MacroManager instance = INSTANCE;
            instance.updateMacro(macro2, inputEvent.getAction() == InputEvent.Action.Press);
         }
      }
   }

   private final void updateMacro(Macro macro, boolean flag) {
      GuiMove.INSTANCE.setFlag5(true);
      if (macro instanceof ChatMacro) {
         if (flag) {
            this.applySendChat((ChatMacro)macro);
         }
      } else if (macro instanceof ItemMacro) {
         if (flag) {
            TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 0, (byte)-10, false, () -> INSTANCE.applyGetBlockPos((ItemMacro)macro), 5, null);
         }
      } else {
         if (!(macro instanceof SettingMacro)) {
            throw new NoWhenBranchMatchedException();
         }

         this.applyParseBoolean((SettingMacro)macro, flag);
      }

      GuiMove.INSTANCE.setFlag5(false);
   }

   private final void applySendChat(ChatMacro chatMacro) {
      if (KotlinCompat.startsWith(chatMacro.getMessage(), "/", false, 2, null)) {
         MinecraftHolder.getLocalPlayer().connection.sendCommand(KotlinCompat.replaceFirst(chatMacro.getMessage(), "/", "", false, 4, null));
      } else {
         MinecraftHolder.getLocalPlayer().connection.sendChat(chatMacro.getMessage());
      }
   }

   private final void applyGetBlockPos(ItemMacro itemMacro) {
      Item item = this.computeItem(itemMacro.getItem());
      if (item != null) {
         InventoryManager instance = InventoryManager.INSTANCE;
         Item[] items = new Item[]{item};
         SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
         instance = InventoryManager.INSTANCE;
         Item[] items2 = new Item[]{item};
         SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items2);
         if (searchInvResult.isFlag()) {
            if (itemMacro.isFlag2()) {
               searchInvResult.update();
               if (!(item instanceof ShieldItem)) {
                  TaskScheduler.defaultImpl(
                     TaskScheduler.INSTANCE, MathUtil.defaultImpl3((ClosedRange)(new IntRange(1, 2)), false, 2, null), (byte)0, false, () -> {
                        ((MinecraftAccessor)MinecraftHolder.getMinecraft()).idoItemUse();
                        MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                     }, 6, null
                  );
               }
            } else {
               InventoryManager.INSTANCE.updateValue(searchInvResult.getValue());
               if (!(item instanceof BlockItem)) {
                  ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
               } else if (MinecraftHolder.getMinecraft().hitResult != null
                  && MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult
                  && !MinecraftHolder.getClientLevel().isEmptyBlock(((BlockHitResult)MinecraftHolder.getMinecraft().hitResult).getBlockPos())) {
                  MinecraftHolder.getMultiPlayerGameMode()
                     .useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, (BlockHitResult)MinecraftHolder.getMinecraft().hitResult);
               } else {
                  ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
               }

               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
               InventoryManager.INSTANCE.update();
            }

            return;
         }

         if (searchInvResult2.isFlag()) {
            callback = () -> {
               if (itemMacro.isFlag2()) {
                  ContainerUtil.defaultImpl(
                     searchInvResult2.getValue(), MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), ContainerInput.SWAP, false, 8, null
                  );
               } else {
                  int value2 = searchInvResult2.getValue();
                  int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
                  Object object = null;
                  byte byteValue = 8;
                  boolean flag2 = false;
                  ContainerInput swap = ContainerInput.SWAP;
                  int value3 = selectedSlot;
                  int value4 = value2;
                  ContainerUtil.defaultImpl(value4, value3, swap, flag2, byteValue, object);
                  if (MinecraftHolder.getMinecraft().hitResult != null
                     && MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult
                     && !MinecraftHolder.getClientLevel().isEmptyBlock(((BlockHitResult)MinecraftHolder.getMinecraft().hitResult).getBlockPos())) {
                     MinecraftHolder.getMultiPlayerGameMode()
                        .useItemOn(MinecraftHolder.getLocalPlayer(), InteractionHand.MAIN_HAND, (BlockHitResult)MinecraftHolder.getMinecraft().hitResult);
                  } else {
                     ContainerUtil.defaultImpl(InteractionHand.MAIN_HAND, 0.0F, 0.0F, 6, null);
                  }

                  MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
                  ContainerUtil.defaultImpl(
                     searchInvResult2.getValue(), MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot(), ContainerInput.SWAP, false, 8, null
                  );
               }

               return Unit.INSTANCE;
            };
            if (!GuiMove.INSTANCE.getEnabled() || !WorldUtil.INSTANCE.isFlag4() || GuiMove.INSTANCE.getClickBypass() != GuiMove.Bypass.LEGIT) {
               if (callback != null) {
                  callback.invoke();
               }

               callback = null;
            }
         }
      }
   }

   private final void applyParseBoolean(SettingMacro settingMacro, boolean flag) {
      Iterable<Module> items2 = ModuleManager.INSTANCE.getItems2();
      Iterator<Module> iterator = items2.iterator();

      Object object;
      while (true) {
         if (iterator.hasNext()) {
            Module object2 = iterator.next();
            Module module = (Module)object2;
            if (!Intrinsics.areEqual(module.getId(), settingMacro.getModule())) {
               continue;
            }

            object = object2;
            break;
         }

         object = null;
         break;
      }

      object = (Module)object;
      if ((Module)object != null) {
         Module module2 = (Module)object;
         Iterable items5 = module2.getItems5();
         Iterator iterator2 = items5.iterator();

         while (true) {
            if (iterator2.hasNext()) {
               Object object3 = iterator2.next();
               Setting setting = (Setting)object3;
               if (!Intrinsics.areEqual(setting.getId(), settingMacro.getSetting())) {
                  continue;
               }

               object = object3;
               break;
            }

            object = null;
            break;
         }

         object = (Setting)object;
         if ((Setting)object != null) {
            Setting setting2 = (Setting)object;
            Object object22 = setting2.getObject2();
            if (object22 instanceof Integer) {
               Setting setting3 = setting2 instanceof Setting ? setting2 : null;
               if (setting3 != null) {
                  if (settingMacro.isFlag2()) {
                     if (flag) {
                        setting3.setObject2(Integer.parseInt(settingMacro.getPressValue()));
                        KeybindsWidget.INSTANCE
                           .getItems()
                           .add(
                              new KeyBind(
                                 settingMacro.getId(),
                                 KeyNames.INSTANCE.computeText(settingMacro.getBind().getValue()),
                                 StringsKt.trim(module2.getText3()).toString() + " -> " + setting2.getText() + " -> " + settingMacro.getPressValue(),
                                 null,
                                 false,
                                 24,
                                 null
                              )
                           );
                     } else {
                        setting3.setObject2(Integer.parseInt(settingMacro.getReleaseValue()));
                        Iterable<KeyBind> items = KeybindsWidget.INSTANCE.getItems();
                        var arrayList = new ArrayList();

                        for (Object object4 : items) {
                           KeyBind keyBind = (KeyBind)object4;
                           if (Intrinsics.areEqual(keyBind.getName(), settingMacro.getId())) {
                              arrayList.add(object4);
                           }
                        }

                        var value2 = arrayList;

                        for (Object object5 : value2) {
                           KeyBind keyBind2 = (KeyBind)object5;
                           keyBind2.setFlag(true);
                        }
                     }
                  } else if (flag) {
                     if (((Number)setting3.getObject2()).intValue() == Integer.parseInt(settingMacro.getPressValue())) {
                        setting3.setObject2(Integer.parseInt(settingMacro.getReleaseValue()));
                        Iterable<KeyBind> items3 = KeybindsWidget.INSTANCE.getItems();
                        var arrayList2 = new ArrayList();

                        for (Object object6 : items3) {
                           KeyBind keyBind3 = (KeyBind)object6;
                           if (Intrinsics.areEqual(keyBind3.getName(), settingMacro.getId())) {
                              arrayList2.add(object6);
                           }
                        }

                        var value3 = arrayList2;

                        for (Object object7 : value3) {
                           KeyBind keyBind4 = (KeyBind)object7;
                           keyBind4.setFlag(true);
                        }
                     } else {
                        setting3.setObject2(Integer.parseInt(settingMacro.getPressValue()));
                        KeybindsWidget.INSTANCE
                           .getItems()
                           .add(
                              new KeyBind(
                                 settingMacro.getId(),
                                 KeyNames.INSTANCE.computeText(settingMacro.getBind().getValue()),
                                 StringsKt.trim(module2.getText3()).toString() + " -> " + setting2.getText() + " -> " + settingMacro.getPressValue(),
                                 null,
                                 false,
                                 24,
                                 null
                              )
                           );
                     }
                  }
               }
            } else if (object22 instanceof Float) {
               Setting setting4 = setting2 instanceof Setting ? setting2 : null;
               if (setting4 != null) {
                  if (settingMacro.isFlag2()) {
                     if (flag) {
                        setting4.setObject2(Float.parseFloat(settingMacro.getPressValue()));
                        KeybindsWidget.INSTANCE
                           .getItems()
                           .add(
                              new KeyBind(
                                 settingMacro.getId(),
                                 KeyNames.INSTANCE.computeText(settingMacro.getBind().getValue()),
                                 StringsKt.trim(module2.getText3()).toString() + " -> " + setting2.getText() + " -> " + settingMacro.getPressValue(),
                                 null,
                                 false,
                                 24,
                                 null
                              )
                           );
                     } else {
                        setting4.setObject2(Float.parseFloat(settingMacro.getReleaseValue()));
                        Iterable<KeyBind> items4 = KeybindsWidget.INSTANCE.getItems();
                        var arrayList3 = new ArrayList();

                        for (Object object8 : items4) {
                           KeyBind keyBind5 = (KeyBind)object8;
                           if (Intrinsics.areEqual(keyBind5.getName(), settingMacro.getId())) {
                              arrayList3.add(object8);
                           }
                        }

                        var value4 = arrayList3;

                        for (Object object9 : value4) {
                           KeyBind keyBind6 = (KeyBind)object9;
                           keyBind6.setFlag(true);
                        }
                     }
                  } else if (flag) {
                     if (((Number)setting4.getObject2()).floatValue() == Float.parseFloat(settingMacro.getPressValue())) {
                        setting4.setObject2(Float.parseFloat(settingMacro.getReleaseValue()));
                        Iterable<KeyBind> items6 = KeybindsWidget.INSTANCE.getItems();
                        var arrayList4 = new ArrayList();

                        for (Object object10 : items6) {
                           KeyBind keyBind7 = (KeyBind)object10;
                           if (Intrinsics.areEqual(keyBind7.getName(), settingMacro.getId())) {
                              arrayList4.add(object10);
                           }
                        }

                        var value5 = arrayList4;

                        for (Object object11 : value5) {
                           KeyBind keyBind8 = (KeyBind)object11;
                           keyBind8.setFlag(true);
                        }
                     } else {
                        setting4.setObject2(Float.parseFloat(settingMacro.getPressValue()));
                        KeybindsWidget.INSTANCE
                           .getItems()
                           .add(
                              new KeyBind(
                                 settingMacro.getId(),
                                 KeyNames.INSTANCE.computeText(settingMacro.getBind().getValue()),
                                 StringsKt.trim(module2.getText3()).toString() + " -> " + setting2.getText() + " -> " + settingMacro.getPressValue(),
                                 null,
                                 false,
                                 24,
                                 null
                              )
                           );
                     }
                  }
               }
            } else if (object22 instanceof Boolean) {
               Setting setting5 = setting2 instanceof Setting ? setting2 : null;
               if (setting5 != null) {
                  if (settingMacro.isFlag2()) {
                     if (flag) {
                        setting5.setObject2(Boolean.parseBoolean(settingMacro.getPressValue()));
                        KeybindsWidget.INSTANCE
                           .getItems()
                           .add(
                              new KeyBind(
                                 settingMacro.getId(),
                                 KeyNames.INSTANCE.computeText(settingMacro.getBind().getValue()),
                                 StringsKt.trim(module2.getText3()).toString() + " -> " + setting2.getText() + " -> " + settingMacro.getPressValue(),
                                 null,
                                 false,
                                 24,
                                 null
                              )
                           );
                     } else {
                        setting5.setObject2(Boolean.parseBoolean(settingMacro.getReleaseValue()));
                        Iterable<KeyBind> items7 = KeybindsWidget.INSTANCE.getItems();
                        var arrayList5 = new ArrayList();

                        for (Object object12 : items7) {
                           KeyBind keyBind9 = (KeyBind)object12;
                           if (Intrinsics.areEqual(keyBind9.getName(), settingMacro.getId())) {
                              arrayList5.add(object12);
                           }
                        }

                        var value6 = arrayList5;

                        for (Object object13 : value6) {
                           KeyBind keyBind10 = (KeyBind)object13;
                           keyBind10.setFlag(true);
                        }
                     }
                  } else if (flag) {
                     if ((Boolean)setting5.getObject2() == Boolean.parseBoolean(settingMacro.getPressValue())) {
                        setting5.setObject2(Boolean.parseBoolean(settingMacro.getReleaseValue()));
                        Iterable<KeyBind> items8 = KeybindsWidget.INSTANCE.getItems();
                        var arrayList6 = new ArrayList();

                        for (Object object14 : items8) {
                           KeyBind keyBind11 = (KeyBind)object14;
                           if (Intrinsics.areEqual(keyBind11.getName(), settingMacro.getId())) {
                              arrayList6.add(object14);
                           }
                        }

                        var value7 = arrayList6;

                        for (Object object15 : value7) {
                           KeyBind keyBind12 = (KeyBind)object15;
                           keyBind12.setFlag(true);
                        }
                     } else {
                        setting5.setObject2(Boolean.parseBoolean(settingMacro.getPressValue()));
                        KeybindsWidget.INSTANCE
                           .getItems()
                           .add(
                              new KeyBind(
                                 settingMacro.getId(),
                                 KeyNames.INSTANCE.computeText(settingMacro.getBind().getValue()),
                                 StringsKt.trim(module2.getText3()).toString() + " -> " + setting2.getText() + " -> " + settingMacro.getPressValue(),
                                 null,
                                 false,
                                 24,
                                 null
                              )
                           );
                     }
                  }
               }
            } else {
               if (!(object22 instanceof Enum)) {
                  return;
               }

               Setting setting6 = setting2 instanceof Setting ? setting2 : null;
               if (setting6 != null) {
                  Object[] enumConstants = setting6.getObject2().getClass().getEnumConstants();
                  boolean flag36 = false;
                  int value8 = 0;
                  int length = enumConstants.length;

                  while (true) {
                     if (value8 >= length) {
                        object = null;
                        break;
                     }

                     Object object16 = enumConstants[value8];
                     Enum enumValue = (Enum)object16;
                     if (StringsKt.equals(enumValue.name(), KotlinCompat.replace(settingMacro.getPressValue(), " ", "_", false, 4, null), true)) {
                        object = object16;
                        break;
                     }

                     value8++;
                  }

                  object = (Enum)object;
                  if ((Enum)object == null) {
                     return;
                  }

                  Enum object23 = (Enum)object;
                  Object[] enumConstants2 = setting6.getObject2().getClass().getEnumConstants();
                  length = 0;
                  int length2 = enumConstants2.length;

                  while (true) {
                     if (length >= length2) {
                        object = null;
                        break;
                     }

                     Object object17 = enumConstants2[length];
                     Enum enumValue2 = (Enum)object17;
                     if (StringsKt.equals(enumValue2.name(), KotlinCompat.replace(settingMacro.getReleaseValue(), " ", "_", false, 4, null), true)) {
                        object = object17;
                        break;
                     }

                     length++;
                  }

                  object = (Enum)object;
                  if ((Enum)object == null) {
                     return;
                  }

                  Enum object24 = (Enum)object;
                  if (settingMacro.isFlag2()) {
                     if (flag) {
                        setting6.setObject2(object23);
                        KeybindsWidget.INSTANCE
                           .getItems()
                           .add(
                              new KeyBind(
                                 settingMacro.getId(),
                                 KeyNames.INSTANCE.computeText(settingMacro.getBind().getValue()),
                                 StringsKt.trim(module2.getText3()).toString() + " -> " + setting2.getText() + " -> " + settingMacro.getPressValue(),
                                 null,
                                 false,
                                 24,
                                 null
                              )
                           );
                     } else {
                        setting6.setObject2(object24);
                        Iterable<KeyBind> items9 = KeybindsWidget.INSTANCE.getItems();
                        flag36 = false;
                        var arrayList7 = new ArrayList();

                        for (Object object18 : items9) {
                           KeyBind keyBind13 = (KeyBind)object18;
                           if (Intrinsics.areEqual(keyBind13.getName(), settingMacro.getId())) {
                              arrayList7.add(object18);
                           }
                        }

                        var value9 = arrayList7;
                        flag36 = false;

                        for (Object object19 : value9) {
                           KeyBind keyBind14 = (KeyBind)object19;
                           keyBind14.setFlag(true);
                        }
                     }
                  } else if (flag) {
                     if (Intrinsics.areEqual(setting6.getObject2(), object23)) {
                        setting6.setObject2(object24);
                        Iterable<KeyBind> items10 = KeybindsWidget.INSTANCE.getItems();
                        flag36 = false;
                        var arrayList8 = new ArrayList();

                        for (Object object20 : items10) {
                           KeyBind keyBind15 = (KeyBind)object20;
                           if (Intrinsics.areEqual(keyBind15.getName(), settingMacro.getId())) {
                              arrayList8.add(object20);
                           }
                        }

                        var value10 = arrayList8;
                        flag36 = false;

                        for (Object object21 : value10) {
                           KeyBind keyBind16 = (KeyBind)object21;
                           keyBind16.setFlag(true);
                        }
                     } else {
                        setting6.setObject2(object23);
                        KeybindsWidget.INSTANCE
                           .getItems()
                           .add(
                              new KeyBind(
                                 settingMacro.getId(),
                                 KeyNames.INSTANCE.computeText(settingMacro.getBind().getValue()),
                                 StringsKt.trim(module2.getText3()).toString() + " -> " + setting2.getText() + " -> " + settingMacro.getPressValue(),
                                 null,
                                 false,
                                 24,
                                 null
                              )
                           );
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   public final Item computeItem(String id) {
      if (map.containsKey(id)) {
         return (Item)map.get(id);
      } else {
         for (Block blockItem : BuiltInRegistries.BLOCK) {
            if (Intrinsics.areEqual(blockItem.getDescriptionId(), "block.minecraft." + id)) {
               map.put(id, blockItem.asItem());
               return blockItem.asItem();
            }
         }

         for (Item itemItem : BuiltInRegistries.ITEM) {
            if (Intrinsics.areEqual(itemItem.getDescriptionId(), "item.minecraft." + id)) {
               map.put(id, itemItem);
               return itemItem;
            }
         }

         return null;
      }
   }
}