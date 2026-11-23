// deobf by pusia
package su.catlean.client.features.modules.misc;

import net.minecraft.network.PacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import su.catlean.utils.KotlinCompat;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundTakeItemEntityPacket;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.network.syncher.SynchedEntityData.DataValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownSplashPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2f;
import su.catlean.api.event.events.network.PlayerEntityDeathEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.event.events.world.EntityRemove;
import su.catlean.api.event.events.world.EntitySpawn;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.equip.PotionsFormatter;
import su.catlean.client.render.SvgTexture;
import su.catlean.client.render.font.Fonts;
import su.catlean.client.services.ClientDirectories;
import su.catlean.client.services.LocalizationManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.SoundManager;
import su.catlean.client.services.rotation.RotationHelpers;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.chat.MessageType;
import su.catlean.utils.inventory.InventoryUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.WorldUtil;

public final class Notifier extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Notifier.class, "totemPops", "getTotemPops()Z", 0), new PropertyReference1Impl(Notifier.class, "lowArmor", "getLowArmor()Z", 0), new PropertyReference1Impl(Notifier.class, "rangeEnter", "getRangeEnter()Z", 0), new PropertyReference1Impl(Notifier.class, "rangeLeave", "getRangeLeave()Z", 0), new PropertyReference1Impl(Notifier.class, "lag", "getLag()Z", 0), new PropertyReference1Impl(Notifier.class, "rubberBand", "getRubberBand()Z", 0), new PropertyReference1Impl(Notifier.class, "stash", "getStash()Z", 0), new PropertyReference1Impl(Notifier.class, "minChests", "getMinChests()I", 0), new PropertyReference1Impl(Notifier.class, "minShulkers", "getMinShulkers()I", 0), new PropertyReference1Impl(Notifier.class, "mention", "getMention()Z", 0), new PropertyReference1Impl(Notifier.class, "pastEffects", "getPastEffects()Lsu/catlean/q9;", 0), new PropertyReference1Impl(Notifier.class, "splashBuff", "getSplashBuff()Lsu/catlean/q9;", 0), new PropertyReference1Impl(Notifier.class, "ftBypass", "getFtBypass()Z", 0), new PropertyReference1Impl(Notifier.class, "self", "getSelf()Z", 0), new PropertyReference1Impl(Notifier.class, "old", "getOld()Z", 0), new PropertyReference1Impl(Notifier.class, "itemCollect", "getItemCollect()Lsu/catlean/q9;", 0), new PropertyReference1Impl(Notifier.class, "onlyDon", "getOnlyDon()Z", 0), new PropertyReference1Impl(Notifier.class, "useTracker", "getUseTracker()Lsu/catlean/q9;", 0) };
   @NotNull
   public static final Notifier INSTANCE = new Notifier();
   @NotNull
   private static final BooleanSetting totemPops = SettingFactory.defaultImpl(INSTANCE, "totem-pops", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting lowArmor = SettingFactory.defaultImpl(INSTANCE, "low-armor", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rangeEnter = SettingFactory.defaultImpl(INSTANCE, "range-enter", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rangeLeave = SettingFactory.defaultImpl(INSTANCE, "range-leave", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting lag = SettingFactory.defaultImpl(INSTANCE, "lag", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting rubberBand = SettingFactory.defaultImpl(INSTANCE, "rubber-band", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting stash = SettingFactory.defaultImpl(INSTANCE, "stash", false, null, null, 12, null);
   @NotNull
   private static final IntSetting minChests = SettingFactory.defaultImpl(
      INSTANCE, "min-chests", 5, new IntRange(0, 100), null, () -> INSTANCE.getStash(), 8, null
   );
   @NotNull
   private static final IntSetting minShulkers = SettingFactory.defaultImpl(
      INSTANCE, "min-shulkers", 0, new IntRange(0, 100), null, () -> INSTANCE.getStash(), 8, null
   );
   @NotNull
   private static final BooleanSetting mention = SettingFactory.defaultImpl(INSTANCE, "mention", false, null, null, 12, null);
   @NotNull
   private static final EnumSetting pastEffects = SettingFactory.defaultImpl(INSTANCE, "past-effects", Notifier.PastEffects.BOTH, null, null, 12, null);
   @NotNull
   private static final EnumSetting splashBuff = SettingFactory.defaultImpl(INSTANCE, "splash-buff", Notifier.PastEffects.BOTH, null, null, 12, null);
   @NotNull
   private static final BooleanSetting ftBypass = SettingFactory.defaultImpl(
      INSTANCE, "ft-bypass", true, null, () -> INSTANCE.getSplashBuff() != Notifier.PastEffects.OFF, 4, null
   );
   @NotNull
   private static final BooleanSetting self = SettingFactory.defaultImpl(
      INSTANCE, "self", false, null, () -> INSTANCE.getSplashBuff() != Notifier.PastEffects.OFF, 4, null
   );
   @NotNull
   private static final BooleanSetting booleanSetting = SettingFactory.defaultImpl(
      INSTANCE, "pre-1.21", true, null, () -> INSTANCE.getSplashBuff() != Notifier.PastEffects.OFF, 4, null
   );
   @NotNull
   private static final EnumSetting itemCollect = SettingFactory.defaultImpl(INSTANCE, "item-collect", Notifier.PastEffects.BOTH, null, null, 12, null);
   @NotNull
   private static final BooleanSetting onlyDon = SettingFactory.defaultImpl(
      INSTANCE, "only-don", false, null, () -> INSTANCE.getItemCollect() != Notifier.PastEffects.OFF, 4, null
   );
   @NotNull
   private static final EnumSetting useTracker = SettingFactory.defaultImpl(INSTANCE, "use-tracker", Notifier.PastEffects.BOTH, null, null, 12, null);
   @NotNull
   private static final SvgTexture svgTexture = new SvgTexture("armor", 30, 30);
   @NotNull
   private static final SvgTexture svgTexture2 = new SvgTexture("empty-armor", 30, 30);
   @NotNull
   private static final SvgTexture svgTexture3 = new SvgTexture("wifi", 30, 30);
   @NotNull
   private static final SvgTexture svgTexture4 = new SvgTexture("wifi-off", 30, 30);
   @NotNull
   private static final SvgTexture svgTexture5 = new SvgTexture("rewind", 30, 30);
   @NotNull
   private static final SvgTexture svgTexture6 = new SvgTexture("loudspeaker", 30, 30);
   @NotNull
   private static final SvgTexture svgTexture7 = new SvgTexture("loudspeaker-empty", 30, 30);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch2 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch3 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch4 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch5 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch6 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch7 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch8 = new StopWatch();
   @NotNull
   private static final StopWatch stopWatch9 = new StopWatch();
   @NotNull
   private static final List<LevelChunk> items = new ArrayList();
   @NotNull
   private static final List items2 = new ArrayList();
   @NotNull
   private static final ConcurrentHashMap map = new ConcurrentHashMap();
   private static boolean flag;
   private static int value = -999;

   private Notifier() {
      super("notifier", SubCategories.getSubCategory11(), null, 4, null);
   }

   private final boolean getTotemPops() {
      return (Boolean)totemPops.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getLowArmor() {
      return (Boolean)lowArmor.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getRangeEnter() {
      return (Boolean)rangeEnter.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getRangeLeave() {
      return (Boolean)rangeLeave.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getLag() {
      return (Boolean)lag.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getRubberBand() {
      return (Boolean)rubberBand.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getStash() {
      return (Boolean)stash.computeObject(this, $$delegatedProperties[6]);
   }

   private final int getMinChests() {
      return ((Number)minChests.computeObject(this, $$delegatedProperties[7])).intValue();
   }

   private final int getMinShulkers() {
      return ((Number)minShulkers.computeObject(this, $$delegatedProperties[8])).intValue();
   }

   private final boolean getMention() {
      return (Boolean)mention.computeObject(this, $$delegatedProperties[9]);
   }

   private final Notifier.PastEffects getPastEffects() {
      return (Notifier.PastEffects)pastEffects.computeObject(this, $$delegatedProperties[10]);
   }

   private final Notifier.PastEffects getSplashBuff() {
      return (Notifier.PastEffects)splashBuff.computeObject(this, $$delegatedProperties[11]);
   }

   private final boolean getFtBypass() {
      return (Boolean)ftBypass.computeObject(this, $$delegatedProperties[12]);
   }

   private final boolean getSelf() {
      return (Boolean)self.computeObject(this, $$delegatedProperties[13]);
   }

   private final boolean getOld() {
      return (Boolean)booleanSetting.computeObject(this, $$delegatedProperties[14]);
   }

   private final Notifier.PastEffects getItemCollect() {
      return (Notifier.PastEffects)itemCollect.computeObject(this, $$delegatedProperties[15]);
   }

   private final boolean getOnlyDon() {
      return (Boolean)onlyDon.computeObject(this, $$delegatedProperties[16]);
   }

   private final Notifier.PastEffects getUseTracker() {
      return (Notifier.PastEffects)useTracker.computeObject(this, $$delegatedProperties[17]);
   }

   @NotNull
   public final StopWatch getStopWatch6() {
      return stopWatch6;
   }

   public final boolean isFlag() {
      return flag;
   }

   public final void setFlag(boolean flag) {
      Notifier.flag = flag;
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (!stopWatch6.testValue(3000) && flag) {
         this.render(
            render2DEvent.getContext(),
            svgTexture6,
            svgTexture7,
            ChatUtil.defaultImpl(this, "air-alarm-header", new Object[0], false, 4, null),
            ChatUtil.defaultImpl(this, "air-alarm", new Object[0], false, 4, null)
         );
      }

      if (this.getLowArmor()) {
         for (ItemStack inventoryItem : (Iterable<ItemStack>)InventoryUtil.fetchInventory(MinecraftHolder.getLocalPlayer())) {
            if (!inventoryItem.isEmpty() && this.fetchMaxDamage(inventoryItem) < 20 && inventoryItem.getMaxDamage() != 0) {
               this.render(
                  render2DEvent.getContext(),
                  svgTexture,
                  svgTexture2,
                  ChatUtil.defaultImpl(this, "low-armor-title", new Object[0], false, 4, null),
                  ChatUtil.defaultImpl(this, "low-armor-text", new Object[0], false, 4, null)
               );
               if (stopWatch3.testValue2(60000) && Assistant.INSTANCE.getEnabled()) {
                  SoundManager.INSTANCE.getSound13().applyExecuteIfPossible();
               }

               return;
            }
         }
      }

      if (this.getLag()
         && ServerInfoService.INSTANCE.getTime() > 5000L
         && !MinecraftHolder.getMinecraft().isLocalServer()
         && MinecraftHolder.getLocalPlayer().tickCount > 60) {
         GuiGraphicsExtractor context = render2DEvent.getContext();
         SvgTexture svgTexture32 = svgTexture3;
         SvgTexture svgTexture42 = svgTexture4;
         String defaultImpl = ChatUtil.defaultImpl(this, "server-error-title", new Object[0], false, 4, null);
         Module thisValue = this;
         Object[] objects = new Object[]{this.computeText(ServerInfoService.INSTANCE.getTime())};
         this.render(context, svgTexture32, svgTexture42, defaultImpl, ChatUtil.defaultImpl(thisValue, "server-error-text", objects, false, 4, null));
         if (stopWatch.testValue2(30000) && Assistant.INSTANCE.getEnabled()) {
            SoundManager.INSTANCE.getSound12().applyExecuteIfPossible();
         }
      } else if (this.getRubberBand() && WorldUtil.INSTANCE.getTime4() < 3000L && MinecraftHolder.getLocalPlayer().tickCount > 60) {
         GuiGraphicsExtractor context2 = render2DEvent.getContext();
         SvgTexture svgTexture52 = svgTexture5;
         SvgTexture svgTexture53 = svgTexture5;
         String defaultImpl2 = ChatUtil.defaultImpl(this, "flag-title", new Object[0], false, 4, null);
         Module thisValue2 = this;
         Object[] objects2 = new Object[]{this.computeText(WorldUtil.INSTANCE.getTime4())};
         this.render(context2, svgTexture52, svgTexture53, defaultImpl2, ChatUtil.defaultImpl(thisValue2, "flag-text", objects2, false, 4, null));
         if (stopWatch2.testValue2(10000) && Assistant.INSTANCE.getEnabled()) {
            SoundManager.INSTANCE.getSound11().applyExecuteIfPossible();
         }
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (sendPacket.getPacket() instanceof ServerboundChatPacket) {
         stopWatch7.update();
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      try {
         if (receivePacket.getPacket() instanceof ClientboundSetEntityDataPacket) {
            Entity entity = MinecraftHolder.getClientLevel().getEntity(((ClientboundSetEntityDataPacket)receivePacket.getPacket()).id());
            if (entity == null) {
               return;
            }

            Entity entity2 = entity;
            if (!(entity2 instanceof Player) || Intrinsics.areEqual(entity2, MinecraftHolder.getLocalPlayer())) {
               return;
            }

            Iterable<SynchedEntityData.DataValue<?>> packedItems = ((ClientboundSetEntityDataPacket)receivePacket.getPacket()).packedItems();

            for (Object object : packedItems) {
               DataValue dataValue = (DataValue)object;
               if (Intrinsics.areEqual(String.valueOf(dataValue.id()), "8")) {
                  if (!Intrinsics.areEqual(dataValue.value().toString(), "1") && !Intrinsics.areEqual(dataValue.value().toString(), "3")) {
                     Notifier.UsageInfo remove = (Notifier.UsageInfo)map.remove(RotationHelpers.computeText(((Player)entity2).getName()));
                     if (remove == null) {
                        return;
                     }

                     Notifier.UsageInfo usageInfo = remove;
                     if (MinecraftHolder.getTime() - usageInfo.getTime() >= 1000L) {
                        Module instance = INSTANCE;
                        Object[] objects = new Object[]{RotationHelpers.computeText(((Player)entity2).getName()), usageInfo.getItemName()};
                        String defaultImpl = ChatUtil.defaultImpl(instance, "used", objects, false, 4, null);
                        if (INSTANCE.getUseTracker() == Notifier.PastEffects.NOTIFICATION || INSTANCE.getUseTracker() == Notifier.PastEffects.BOTH) {
                           NotificationManager.INSTANCE.applyExecute("", defaultImpl, 5, MessageType.INFO, usageInfo.getI());
                        }

                        if (INSTANCE.getUseTracker() == Notifier.PastEffects.CHAT || INSTANCE.getUseTracker() == Notifier.PastEffects.BOTH) {
                           ChatUtil.defaultImpl(INSTANCE, defaultImpl, false, 2, null);
                        }
                     }

                     Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();
                     var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(players, 10));

                     for (Object object2 : players) {
                        AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object2;
                        arrayList.add(RotationHelpers.computeText(abstractClientPlayer.getName()));
                     }

                     List list = arrayList;
                     map.entrySet().removeIf(arg -> {
                        String key = (String)((java.util.Map.Entry)arg).getKey();
                        return !list.contains(key);
                     });
                  } else {
                     boolean areEqual = Intrinsics.areEqual(dataValue.value().toString(), "3");
                     ItemStack itemInHand = ((Player)entity2).getItemInHand(areEqual ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
                     if (itemInHand != null && (itemInHand.getComponents().has(DataComponents.FOOD) || itemInHand.getComponents().has(DataComponents.POTION_CONTENTS))) {
                        Map<String, Notifier.UsageInfo> map2 = map;
                        String text = RotationHelpers.computeText(((Player)entity2).getName());
                        Notifier.UsageInfo usageInfo2 = new Notifier.UsageInfo(
                           RotationHelpers.computeText(itemInHand.copy().getHoverName()), itemInHand.copy(), MinecraftHolder.getTime()
                        );
                        map2.put(text, usageInfo2);
                     }
                  }
               }
            }
         }

         if (receivePacket.getPacket() instanceof ClientboundLevelEventPacket
            && (
               ((ClientboundLevelEventPacket)receivePacket.getPacket()).getType() == 2007
                  || ((ClientboundLevelEventPacket)receivePacket.getPacket()).getType() == 2002
            )) {
            int data = ((ClientboundLevelEventPacket)receivePacket.getPacket()).getData();
            float value2 = (data >> 16 & 0xFF) / 255.0F;
            float value3 = (data >> 8 & 0xFF) / 255.0F;
            float value4 = (data >> 0 & 0xFF) / 255.0F;
            Color color = new Color(value2, value3, value4);
            if (color.getRGB() == -13083194) {
               return;
            }

            value = color.getRGB();
         }

         if (this.getMention() && receivePacket.getPacket() instanceof ClientboundSystemChatPacket && stopWatch7.testValue(500)) {
            Packet<? extends PacketListener> packet = receivePacket.getPacket();
            if (KotlinCompat.contains(
               ((ClientboundSystemChatPacket)packet).content.getString(), MinecraftHolder.getLocalPlayer().getGameProfile().name(), false, 2, null
            )) {
               NotificationManager.defaultImpl(
                  NotificationManager.INSTANCE,
                  this.getText3(),
                  ChatUtil.defaultImpl(this, "mention", new Object[0], false, 4, null),
                  3,
                  MessageType.WARNING,
                  null,
                  16,
                  null
               );
               if (stopWatch4.testValue2(2500) && Assistant.INSTANCE.getEnabled()) {
                  SoundManager.INSTANCE.getSound14().applyExecuteIfPossible();
               }
            }
         }

         if (this.getItemCollect() != Notifier.PastEffects.OFF && receivePacket.getPacket() instanceof ClientboundTakeItemEntityPacket) {
            Entity entity3 = MinecraftHolder.getClientLevel().getEntity(((ClientboundTakeItemEntityPacket)receivePacket.getPacket()).getItemId());
            Entity entity4 = MinecraftHolder.getClientLevel().getEntity(((ClientboundTakeItemEntityPacket)receivePacket.getPacket()).getPlayerId());
            if (entity3 instanceof ItemEntity && entity4 != null) {
               ItemStack itemStack = ((ItemEntity)entity3).getItem().copy();
               if (!itemStack.isEmpty()) {
                  if (this.getOnlyDon()
                     && !KotlinCompat.contains(itemStack.getHoverName().getString().toLowerCase(Locale.ROOT), "круш", false, 2, null)
                     && !KotlinCompat.contains(itemStack.getHoverName().getString(), "★", false, 2, null)) {
                     return;
                  }

                  String defaultImpl2;
                  if (entity4.getId() == MinecraftHolder.getLocalPlayer().getId()) {
                     Module thisValue = this;
                     Object[] objects2 = new Object[]{RotationHelpers.computeText(itemStack.getHoverName()), itemStack.getCount()};
                     defaultImpl2 = ChatUtil.defaultImpl(thisValue, "collect", objects2, false, 4, null);
                  } else {
                     Module thisValue2 = this;
                     Object[] objects3 = new Object[]{
                        RotationHelpers.computeText(entity4.getName()), RotationHelpers.computeText(itemStack.getHoverName()), itemStack.getCount()
                     };
                     defaultImpl2 = ChatUtil.defaultImpl(thisValue2, "collector", objects3, false, 4, null);
                  }

                  String text2 = defaultImpl2;
                  if (this.getItemCollect() == Notifier.PastEffects.NOTIFICATION || this.getItemCollect() == Notifier.PastEffects.BOTH) {
                     NotificationManager.INSTANCE.applyExecute("", text2, 5, MessageType.INFO, itemStack);
                  }

                  if (this.getItemCollect() == Notifier.PastEffects.CHAT || this.getItemCollect() == Notifier.PastEffects.BOTH) {
                     ChatUtil.defaultImpl(this, text2, false, 2, null);
                  }
               }
            }
         }
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   @Flow
   private final void onEntitySpawn(EntitySpawn entitySpawn) {
      if (this.getRangeEnter() && entitySpawn.getEntity() instanceof Player && !Intrinsics.areEqual(entitySpawn.getEntity(), MinecraftHolder.getLocalPlayer())) {
         Module thisValue = this;
         Module thisValue2 = this;
         Object[] objects = new Object[]{entitySpawn.getEntity().getName().getString()};
         ChatUtil.defaultImpl(thisValue, ChatUtil.defaultImpl(thisValue2, "range-enter", objects, false, 4, null), false, 2, null);
         if (stopWatch8.testValue2(2000) && MinecraftHolder.getLocalPlayer().tickCount > 20 && Assistant.INSTANCE.getEnabled()) {
            SoundManager.INSTANCE.getSound16().applyExecuteIfPossible();
         }
      }
   }

   @Flow
   private final void onEntityRemove(EntityRemove entityRemove) {
      if (this.getSplashBuff() != Notifier.PastEffects.OFF && entityRemove.getEntity() != null && entityRemove.getEntity() instanceof ThrownSplashPotion) {
         var entity = (ThrownSplashPotion)entityRemove.getEntity();
         Iterable<AbstractClientPlayer> players = MinecraftHolder.getClientLevel().players();
         var arrayList = new ArrayList();

         for (Object object : players) {
            AbstractClientPlayer abstractClientPlayer = (AbstractClientPlayer)object;
            if (!Intrinsics.areEqual(abstractClientPlayer, MinecraftHolder.getLocalPlayer()) || INSTANCE.getSelf()) {
               arrayList.add(object);
            }
         }

         for (Object object2 : arrayList) {
            AbstractClientPlayer abstractClientPlayer2 = (AbstractClientPlayer)object2;
            double distanceToSqr = INSTANCE.getOld()
               ? entity.distanceToSqr((Entity)abstractClientPlayer2)
               : entity.getBoundingBox().distanceToSqr(abstractClientPlayer2.getBoundingBox().inflate(ProjectileUtil.computeMargin((Entity)entity)));
            if (distanceToSqr < 16.0) {
               ItemStack itemStack = INSTANCE.getFtBypass()
                     && KotlinCompat.contains(
                        RotationHelpers.computeText(entity.getItem().getHoverName()).toLowerCase(Locale.ROOT), "несоздаваемое", false, 2, null
                     )
                  ? PotionsFormatter.INSTANCE.computeItemStack(value)
                  : entity.getItem();
               double value2 = 1.0 - Math.sqrt(distanceToSqr) / 4.0;
               int value3 = (int)(100 * value2);
               String text = (value3 > 80 ? ChatFormatting.GREEN : (value3 > 50 ? ChatFormatting.YELLOW : ChatFormatting.RED)).toString();
               Module instance = INSTANCE;
               Object[] objects = new Object[]{RotationHelpers.computeText(abstractClientPlayer2.getName()), RotationHelpers.computeText(itemStack.getHoverName()), text, value3};
               String defaultImpl = ChatUtil.defaultImpl(instance, "splash-buff", objects, false, 4, null) + "%";
               if (INSTANCE.getSplashBuff() == Notifier.PastEffects.NOTIFICATION || INSTANCE.getSplashBuff() == Notifier.PastEffects.BOTH) {
                  NotificationManager.INSTANCE.applyExecute("", defaultImpl, 5, MessageType.WARNING, itemStack);
               }

               if (INSTANCE.getSplashBuff() == Notifier.PastEffects.CHAT || INSTANCE.getSplashBuff() == Notifier.PastEffects.BOTH) {
                  ChatUtil.defaultImpl(INSTANCE, defaultImpl, false, 2, null);
                  PotionContents potioN_CONTENTS = (PotionContents)itemStack.get(DataComponents.POTION_CONTENTS);
                  if (potioN_CONTENTS == null) {
                     return;
                  }

                  PotionContents potionContents = potioN_CONTENTS;

                  for (MobEffectInstance allEffect : potionContents.getAllEffects()) {
                     String text2 = switch (allEffect.getAmplifier() + 1) {
                        case 1 -> "I";
                        case 2 -> "II";
                        case 3 -> "III";
                        case 4 -> "IV";
                        case 5 -> "V";
                        case 6 -> "VI";
                        case 7 -> "VII";
                        case 8 -> "VIII";
                        case 9 -> "IX";
                        case 10 -> "X";
                        default -> String.valueOf(allEffect.getAmplifier() + 1);
                     };
                     int duration = (int)(allEffect.getDuration() * value2);
                     String text3;
                     if (allEffect.isInfiniteDuration()) {
                        text3 = "*:*";
                     } else {
                        int value4 = duration / 1200;
                        String text4 = "%02d";
                        Object[] objects2 = new Object[]{duration % 1200 / 20};
                        text3 = value4 + ":" + String.format(text4, Arrays.copyOf(objects2, objects2.length));
                     }

                     String text5 = text3;
                     MinecraftHolder.getLocalPlayer()
                        .sendSystemMessage(
                           Component.nullToEmpty(
                              "•"
                                 + (((MobEffect)allEffect.getEffect().value()).getCategory() == MobEffectCategory.HARMFUL ? ChatFormatting.RED : "")
                                 + " "
                                 + LocalizationManager.INSTANCE.computeText(((MobEffect)allEffect.getEffect().value()).getDescriptionId(), new Object[0])
                                 + " "
                                 + text2
                                 + " "
                                 + ChatFormatting.RESET
                                 + text5
                           )
                        );
                  }
               }
            }
         }
      }

      if (this.getRangeLeave()
         && entityRemove.getEntity() != null
         && entityRemove.getEntity() instanceof Player
         && !Intrinsics.areEqual(entityRemove.getEntity(), MinecraftHolder.getLocalPlayer())) {
         Module thisValue = this;
         Module thisValue2 = this;
         Object[] objects3 = new Object[]{entityRemove.getEntity().getName().getString()};
         ChatUtil.defaultImpl(thisValue, ChatUtil.defaultImpl(thisValue2, "range-leave", objects3, false, 4, null), false, 2, null);
         if (stopWatch8.testValue2(2000) && MinecraftHolder.getLocalPlayer().tickCount > 20 && Assistant.INSTANCE.getEnabled()) {
            SoundManager.INSTANCE.getSound17().applyExecuteIfPossible();
         }
      }
   }

   @Flow
   private final void onPlayerEntityDeath(PlayerEntityDeathEvent playerEntityDeathEvent) {
      if (this.getTotemPops()) {
         Module thisValue = this;
         String string = playerEntityDeathEvent.getPlayer().getName().getString();
         Module thisValue2 = this;
         Object[] objects = new Object[]{playerEntityDeathEvent.getTotemsPopped()};
         ChatUtil.defaultImpl(thisValue, string + " " + ChatUtil.defaultImpl(thisValue2, "death", objects, false, 4, null), false, 2, null);
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (this.getStash()) {
         for (LevelChunk items3Item : (Iterable<LevelChunk>)ServerInfoService.INSTANCE.getItems3()) {
            if (!items.contains(items3Item)) {
               List<BlockEntity> list = items3Item.getBlockEntities().values().stream().toList();
               int value2 = 0;
               int value3 = 0;

               for (BlockEntity blockEntity : (Iterable<BlockEntity>)list) {
                  if (blockEntity instanceof ChestBlockEntity) {
                     value2++;
                  }

                  if (blockEntity instanceof ShulkerBoxBlockEntity) {
                     value3++;
                  }
               }

               if (value2 >= this.getMinChests() && value3 >= this.getMinShulkers()) {
                  items.add(items3Item);
                  int middleBlockZ = items3Item.getPos().getMiddleBlockZ();
                  int middleBlockX = items3Item.getPos().getMiddleBlockX();
                  String text = "Stash: X: " + middleBlockX + " Z: " + middleBlockZ + " Chests: " + value2 + " Shulkers: " + value3;
                  NotificationManager.defaultImpl(NotificationManager.INSTANCE, "Stash Logger", text, 5, MessageType.SUCCESS, null, 16, null);
                  ChatUtil.defaultImpl(this, text, false, 2, null);
                  if (stopWatch9.testValue2(5000)) {
                     SoundManager.INSTANCE.getSound22().applyExecuteIfPossible();
                  }

                  String text2 = "unknown_server";
                  if (MinecraftHolder.getMinecraft().getConnection().getServerData() != null
                     && MinecraftHolder.getMinecraft().getConnection().getServerData().ip != null) {
                     text2 = KotlinCompat.replace(MinecraftHolder.getMinecraft().getConnection().getServerData().ip, ':', '_', false, 4, null);
                  }

                  try {
                     BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File(ClientDirectories.getFile3(), text2 + ".txt"), true));
                     bufferedWriter.append("\n" + text);
                     bufferedWriter.close();
                  } catch (Exception exception) {
                  }
               }
            }
         }
      }

      if (this.getPastEffects() != Notifier.PastEffects.OFF) {
         Iterable items22 = items2;
         boolean flag2 = false;
         var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items22, 10));
         boolean flag3 = false;

         for (Object object : items22) {
            MobEffectInstance mobEffectInstance = (MobEffectInstance)object;
            arrayList.add(mobEffectInstance.getDescriptionId());
         }

         var value4 = arrayList;
         items22 = MinecraftHolder.getLocalPlayer().getActiveEffects();
         Iterable iterable = value4;
         flag2 = false;
         arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(items22, 10));
         flag3 = false;

         for (Object object2 : items22) {
            MobEffectInstance mobEffectInstance2 = (MobEffectInstance)object2;
            arrayList.add(mobEffectInstance2.getDescriptionId());
         }

         items22 = CollectionsKt.minus(iterable, CollectionsKt.toSet(arrayList));
         flag2 = false;

         for (Object object3 : items22) {
            String text3 = (String)object3;
            if (INSTANCE.getPastEffects() == Notifier.PastEffects.NOTIFICATION || INSTANCE.getPastEffects() == Notifier.PastEffects.BOTH) {
               Module instance = INSTANCE;
               Module instance2 = INSTANCE;
               Object[] objects = new Object[]{LocalizationManager.INSTANCE.computeText(text3, new Object[0])};
               ChatUtil.updateModule(
                  instance, ChatUtil.defaultImpl(instance2, "past-effect", objects, false, 4, null), MessageType.WARNING, 2, Items.POTION.getDefaultInstance()
               );
            }

            if (INSTANCE.getPastEffects() == Notifier.PastEffects.CHAT || INSTANCE.getPastEffects() == Notifier.PastEffects.BOTH) {
               Module instance3 = INSTANCE;
               Module instance4 = INSTANCE;
               Object[] objects2 = new Object[]{LocalizationManager.INSTANCE.computeText(text3, new Object[0])};
               ChatUtil.defaultImpl(instance3, ChatUtil.defaultImpl(instance4, "past-effect", objects2, false, 4, null), false, 2, null);
            }
         }

         items2.clear();
         items2.addAll(MinecraftHolder.getLocalPlayer().getActiveEffects());
      }
   }

   private final String computeText(long time) {
      String text = "%.1f";
      Object[] objects = new Object[]{(float)time / 1000.0F};
      return String.format(text, Arrays.copyOf(objects, objects.length));
   }

   private final void render(GuiGraphicsExtractor guiGraphicsExtractor, SvgTexture svgTexture, SvgTexture svgTexture2, String text, String text2) {
      float amount = Math.max(Fonts.getFontRenderer2().computeAmount(text2), Fonts.getFontRenderer9().computeAmount(text));
      float guiScaledWidth = MinecraftHolder.getMinecraft().getWindow().getGuiScaledWidth() / 2.0F - amount / 2.0F - 17.0F;
      int currentTimeMillis = (int)(System.currentTimeMillis() / 3.0 % 360);
      currentTimeMillis = currentTimeMillis >= 180 ? 360 - currentTimeMillis : currentTimeMillis;
      Color alpha = Render2D.INSTANCE.fetchAlpha(Color.RED, Color.YELLOW, currentTimeMillis / 360.0F);
      Fonts.getFontRenderer9().render(guiGraphicsExtractor, text, guiScaledWidth, 8.0F, alpha);
      Fonts.getFontRenderer2().render(guiGraphicsExtractor, text2, guiScaledWidth, 20.0F, alpha);
      SvgTexture.defaultImpl(
         currentTimeMillis > 90 ? svgTexture : svgTexture2,
         (Matrix3x2f)guiGraphicsExtractor.pose(),
         (double)(guiScaledWidth + 5) + amount,
         5.0,
         0.0F,
         0.0F,
         alpha,
         null,
         0.0F,
         216,
         null
      );
   }

   public final void updateText(String name, int value) {
      if (this.getTotemPops()) {
         Module thisValue = this;
         Module thisValue2 = this;
         Object[] objects = new Object[]{value};
         ChatUtil.defaultImpl(thisValue, name + " " + ChatUtil.defaultImpl(thisValue2, "totem", objects, false, 4, null), false, 2, null);
         if (stopWatch5.testValue2(3000) && Assistant.INSTANCE.getEnabled()) {
            SoundManager.INSTANCE.getSound15().applyExecuteIfPossible();
         }
      }
   }

   private final int fetchMaxDamage(ItemStack itemStack) {
      return (int)((itemStack.getMaxDamage() - itemStack.getDamageValue()) / Math.max(0.1, (double)itemStack.getMaxDamage()) * 100.0F);
   }

   static {
   }

   public static enum PastEffects {
      OFF,
      CHAT,
      NOTIFICATION,
      BOTH;

      private static final Notifier.PastEffects[] pastEffectss = $values();
      private static final EnumEntries<Notifier.PastEffects> enumEntries = EnumEntriesKt.enumEntries(pastEffectss);

      private PastEffects() {
      }

      @NotNull
      public static EnumEntries<Notifier.PastEffects> getEnumEntries() {
         return enumEntries;
      }

      private static final Notifier.PastEffects[] $values() {
         return new Notifier.PastEffects[]{OFF, CHAT, NOTIFICATION, BOTH};
      }
   }

   public static final class UsageInfo {
      @NotNull
      private final String itemName;
      @NotNull
      private final ItemStack i;
      private final long time;

      public UsageInfo(String itemName, ItemStack i, long time) {
         this.itemName = itemName;
         this.i = i;
         this.time = time;
      }

      @NotNull
      public final String getItemName() {
         return this.itemName;
      }

      @NotNull
      public final ItemStack getI() {
         return this.i;
      }

      public final long getTime() {
         return this.time;
      }

      @NotNull
      public final String getItemName2() {
         return this.itemName;
      }

      @NotNull
      public final ItemStack getI2() {
         return this.i;
      }

      public final long getTime2() {
         return this.time;
      }

      @NotNull
      @Override
      public String toString() {
         long time2 = this.time;
         ItemStack itemStack = this.i;
         String itemName2 = this.itemName;
         return "UsageInfo(itemName=" + itemName2 + ", i=" + itemStack + ", startTime=" + time2 + ")";
      }

      @Override
      public int hashCode() {
         int hashCode = this.itemName.hashCode();
         hashCode = hashCode * 31 + this.i.hashCode();
         return hashCode * 31 + Long.hashCode(this.time);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof Notifier.UsageInfo usageInfo)) {
            return false;
         } else if (!Intrinsics.areEqual(this.itemName, usageInfo.itemName)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.i, usageInfo.i) ? false : this.time == usageInfo.time;
         }
      }
   }
}