// deobf by pusia
package su.catlean.client.features.modules.rage;

import net.minecraft.network.PacketListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import su.catlean.api.data.Rotation;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PostSyncEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.world.FireWorkVectorEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.Aura;
import su.catlean.client.features.modules.attack.aura.AuraMovementHandler;
import su.catlean.client.features.modules.basic.GuiMove;
import su.catlean.client.features.modules.equip.ElytraSwap;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.NotificationManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.ThreadManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class ElytraTarget extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(ElytraTarget.class, "predict", "getPredict()F", 0), new PropertyReference1Impl(ElytraTarget.class, "alwaysPredict", "getAlwaysPredict()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(ElytraTarget.class, "offHandFirework", "getOffHandFirework()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "bravoHvhSwap", "getBravoHvhSwap()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "minSpeed", "getMinSpeed()I", 0), new PropertyReference1Impl(ElytraTarget.class, "pauseWhileEating", "getPauseWhileEating()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "peekAssist", "getPeekAssist()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "fakeLag", "getFakeLag()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "onlyGroundPeek", "getOnlyGroundPeek()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "boost", "getBoost()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "customSpeed", "getCustomSpeed()Z", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees0", "getDegrees0()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees5", "getDegrees5()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees10", "getDegrees10()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees15", "getDegrees15()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees20", "getDegrees20()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees25", "getDegrees25()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees30", "getDegrees30()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees35", "getDegrees35()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees40", "getDegrees40()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees45", "getDegrees45()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees50", "getDegrees50()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees55", "getDegrees55()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees60", "getDegrees60()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees65", "getDegrees65()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees70", "getDegrees70()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees75", "getDegrees75()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees80", "getDegrees80()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees85", "getDegrees85()F", 0), new PropertyReference1Impl(ElytraTarget.class, "degrees90", "getDegrees90()F", 0) };
   @NotNull
   public static final ElytraTarget INSTANCE = new ElytraTarget();
   @NotNull
   private static final FloatSetting predictTicks = SettingFactory.defaultImpl(
      INSTANCE, "predict-ticks", 0.0F, RangesKt.rangeTo(0.0F, 5.0F), null, null, 24, null
   );
   @NotNull
   private static final BooleanSetting alwaysPredict = SettingFactory.defaultImpl(INSTANCE, "always-predict", false, null, null, 12, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 10, new IntRange(0, 30), null, null, 24, null);
   @NotNull
   private static final BooleanSetting offhandFirework = SettingFactory.defaultImpl(INSTANCE, "offhand-firework", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting bravoHvhSwap = SettingFactory.defaultImpl(
      INSTANCE, "bravo-hvh-swap", true, null, () -> INSTANCE.getOffHandFirework(), 4, null
   );
   @NotNull
   private static final IntSetting minSpeed = SettingFactory.defaultImpl(INSTANCE, "min-speed", 120, new IntRange(0, 200), null, null, 24, null);
   @NotNull
   private static final BooleanSetting pauseWhileEating = SettingFactory.defaultImpl(INSTANCE, "pause-while-eating", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting peekAssist = SettingFactory.defaultImpl(INSTANCE, "peek-assist", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting fakeLag = SettingFactory.defaultImpl(INSTANCE, "fake-lag", true, null, () -> INSTANCE.getPeekAssist(), 4, null);
   @NotNull
   private static final BooleanSetting onlyGroundPeek = SettingFactory.defaultImpl(
      INSTANCE, "only-ground-peek", true, null, () -> INSTANCE.getPeekAssist(), 4, null
   );
   @NotNull
   private static final BooleanSetting boost = SettingFactory.defaultImpl(INSTANCE, "boost", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting customSpeed = SettingFactory.defaultImpl(INSTANCE, "custom-speed", false, null, () -> INSTANCE.getBoost(), 4, null);
   @NotNull
   private static final FloatSetting degrees0 = SettingFactory.defaultImpl(
      INSTANCE, "0-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees5 = SettingFactory.defaultImpl(
      INSTANCE, "5-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees10 = SettingFactory.defaultImpl(
      INSTANCE, "10-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees15 = SettingFactory.defaultImpl(
      INSTANCE, "15-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees20 = SettingFactory.defaultImpl(
      INSTANCE, "20-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees25 = SettingFactory.defaultImpl(
      INSTANCE, "25-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees30 = SettingFactory.defaultImpl(
      INSTANCE, "30-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees35 = SettingFactory.defaultImpl(
      INSTANCE, "35-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees40 = SettingFactory.defaultImpl(
      INSTANCE, "40-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees45 = SettingFactory.defaultImpl(
      INSTANCE, "45-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees50 = SettingFactory.defaultImpl(
      INSTANCE, "50-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees55 = SettingFactory.defaultImpl(
      INSTANCE, "55-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees60 = SettingFactory.defaultImpl(
      INSTANCE, "60-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees65 = SettingFactory.defaultImpl(
      INSTANCE, "65-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees70 = SettingFactory.defaultImpl(
      INSTANCE, "70-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees75 = SettingFactory.defaultImpl(
      INSTANCE, "75-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees80 = SettingFactory.defaultImpl(
      INSTANCE, "80-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees85 = SettingFactory.defaultImpl(
      INSTANCE, "85-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final FloatSetting degrees90 = SettingFactory.defaultImpl(
      INSTANCE, "90-degrees", 0.15F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getBoost() && INSTANCE.getCustomSpeed(), 8, null
   );
   @NotNull
   private static final PlayerUtil playerUtil = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil2 = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil3 = new PlayerUtil();
   @NotNull
   private static final PlayerUtil playerUtil4 = new PlayerUtil();
   private static boolean flag;
   @Nullable
   private static Rotation rotation;
   @Nullable
   private static Direction direction;
   private static boolean flag2;
   @Nullable
   private static AABB aABB;
   @Nullable
   private static AABB aABB2;
   @Nullable
   private static Vec3 vec3;
   @NotNull
   private static final HashMap map;
   @NotNull
   private static HashMap map2;

   private ElytraTarget() {
      super("elytra-target", SubCategories.getSubCategory5(), null, 4, null);
   }

   private final float getPredict() {
      return ((Number)predictTicks.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   private final boolean getAlwaysPredict() {
      return (Boolean)alwaysPredict.computeObject(this, $$delegatedProperties[1]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   private final boolean getOffHandFirework() {
      return (Boolean)offhandFirework.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getBravoHvhSwap() {
      return (Boolean)bravoHvhSwap.computeObject(this, $$delegatedProperties[4]);
   }

   private final int getMinSpeed() {
      return ((Number)minSpeed.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final boolean getPauseWhileEating() {
      return (Boolean)pauseWhileEating.computeObject(this, $$delegatedProperties[6]);
   }

   private final boolean getPeekAssist() {
      return (Boolean)peekAssist.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getFakeLag() {
      return (Boolean)fakeLag.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getOnlyGroundPeek() {
      return (Boolean)onlyGroundPeek.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getBoost() {
      return (Boolean)boost.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getCustomSpeed() {
      return (Boolean)customSpeed.computeObject(this, $$delegatedProperties[11]);
   }

   private final float getDegrees0() {
      return ((Number)degrees0.computeObject(this, $$delegatedProperties[12])).floatValue();
   }

   private final float getDegrees5() {
      return ((Number)degrees5.computeObject(this, $$delegatedProperties[13])).floatValue();
   }

   private final float getDegrees10() {
      return ((Number)degrees10.computeObject(this, $$delegatedProperties[14])).floatValue();
   }

   private final float getDegrees15() {
      return ((Number)degrees15.computeObject(this, $$delegatedProperties[15])).floatValue();
   }

   private final float getDegrees20() {
      return ((Number)degrees20.computeObject(this, $$delegatedProperties[16])).floatValue();
   }

   private final float getDegrees25() {
      return ((Number)degrees25.computeObject(this, $$delegatedProperties[17])).floatValue();
   }

   private final float getDegrees30() {
      return ((Number)degrees30.computeObject(this, $$delegatedProperties[18])).floatValue();
   }

   private final float getDegrees35() {
      return ((Number)degrees35.computeObject(this, $$delegatedProperties[19])).floatValue();
   }

   private final float getDegrees40() {
      return ((Number)degrees40.computeObject(this, $$delegatedProperties[20])).floatValue();
   }

   private final float getDegrees45() {
      return ((Number)degrees45.computeObject(this, $$delegatedProperties[21])).floatValue();
   }

   private final float getDegrees50() {
      return ((Number)degrees50.computeObject(this, $$delegatedProperties[22])).floatValue();
   }

   private final float getDegrees55() {
      return ((Number)degrees55.computeObject(this, $$delegatedProperties[23])).floatValue();
   }

   private final float getDegrees60() {
      return ((Number)degrees60.computeObject(this, $$delegatedProperties[24])).floatValue();
   }

   private final float getDegrees65() {
      return ((Number)degrees65.computeObject(this, $$delegatedProperties[25])).floatValue();
   }

   private final float getDegrees70() {
      return ((Number)degrees70.computeObject(this, $$delegatedProperties[26])).floatValue();
   }

   private final float getDegrees75() {
      return ((Number)degrees75.computeObject(this, $$delegatedProperties[27])).floatValue();
   }

   private final float getDegrees80() {
      return ((Number)degrees80.computeObject(this, $$delegatedProperties[28])).floatValue();
   }

   private final float getDegrees85() {
      return ((Number)degrees85.computeObject(this, $$delegatedProperties[29])).floatValue();
   }

   private final float getDegrees90() {
      return ((Number)degrees90.computeObject(this, $$delegatedProperties[30])).floatValue();
   }

   @NotNull
   public final HashMap getMap() {
      return map;
   }

   @Override
   public void update4() {
      if (flag2 && Blink.INSTANCE.getEnabled()) {
         NotificationManager.INSTANCE.setFlag(true);
         Blink.INSTANCE.applyUnplug();
         NotificationManager.INSTANCE.setFlag(false);
      }
   }

   @Flow
   private final void onPostSync(PostSyncEvent postSyncEvent) {
      if (this.getFakeLag()) {
         Blink.INSTANCE.update();
      }

      boolean flag3 = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getInventory().getItem(38).getItem(), Items.ELYTRA)
         && MinecraftHolder.getLocalPlayer().isFallFlying()
         && Aura.INSTANCE.getEntity() != null
         && Aura.INSTANCE.getEntity() instanceof Player
         && (!this.getOnlyGroundPeek() || !((Player)Aura.INSTANCE.getEntity()).isFallFlying());
      if (!flag3 && Blink.INSTANCE.getEnabled()) {
         NotificationManager.INSTANCE.setFlag(true);
         Blink.INSTANCE.applyUnplug();
         flag2 = false;
         NotificationManager.INSTANCE.setFlag(false);
      }

      if (AuraMovementHandler.INSTANCE.getValue() == AuraMovementHandler.INSTANCE.getValue2()
         && this.getPeekAssist()
         && !ElytraFreeze.INSTANCE.getEnabled()
         && flag3) {
         flag = true;
         rotation = this.getRotation();
         playerUtil2.update();
         ThreadManager instance = ThreadManager.INSTANCE;
         short shortValue = 325;
         instance.getExecutorService().execute(new ElytraTarget.Task(shortValue));
      }

      if (playerUtil3.testValue(5) && !MinecraftHolder.getLocalPlayer().onGround()) {
         InventoryManager instance2 = InventoryManager.INSTANCE;
         Item[] items = new Item[]{Items.FIREWORK_ROCKET};
         if (instance2.computeSearchInvResult2(items).isFlag() && MinecraftHolder.getLocalPlayer().tryToStartFallFlying()) {
            ElytraSwap.INSTANCE.setFlag(true);
            Packet serverboundPlayerCommandPacket = (Packet)(new ServerboundPlayerCommandPacket((Entity)MinecraftHolder.getLocalPlayer(), Action.START_FALL_FLYING));
            PacketUtil.applySend(serverboundPlayerCommandPacket);
            playerUtil3.update();
         }
      }
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (flag && rotation != null) {
         RotationManager.INSTANCE.updateRotation(rotation, 200);
      }

      if (!this.getPauseWhileEating()
         || !MinecraftHolder.getLocalPlayer().isUsingItem()
         || !MinecraftHolder.getLocalPlayer().getMainHandItem().getComponents().has(DataComponents.FOOD)
            && !MinecraftHolder.getLocalPlayer().getMainHandItem().getComponents().has(DataComponents.POTION_CONTENTS)) {
         if (MinecraftHolder.getLocalPlayer().isFallFlying()
            && playerUtil.testValue(this.getDelay())
            && WorldUtil.INSTANCE.getAmount2() * 72.0F < this.getMinSpeed()) {
            this.applyGetItem(this.getOffHandFirework(), this.getBravoHvhSwap());
            playerUtil.update();
         }
      }
   }

   @Flow
   private final void onFireWorkVelocity(FireWorkVelocityEvent fireWorkVelocityEvent) {
      fireWorkVelocityEvent.cancel();
      Vec3 vector = FireWorkVectorEvent.INSTANCE.call(MinecraftHolder.getLocalPlayer().getLookAngle())
         ? FireWorkVectorEvent.INSTANCE.getVector()
         : MinecraftHolder.getLocalPlayer().getLookAngle();
      if (this.getCustomSpeed()) {
         this.update();
      }

      float degrees = Math.abs((float)Math.toDegrees(-Math.atan2(vector.y, Math.hypot(vector.x, vector.z))));
      float value;
      if (!Blink.INSTANCE.getEnabled() && this.getBoost()) {
         Iterable entrySet = (this.getCustomSpeed() ? map2 : map).entrySet();
         Iterator iterator = entrySet.iterator();
         Object object;
         if (!iterator.hasNext()) {
            object = null;
         } else {
            Object object2 = iterator.next();
            if (!iterator.hasNext()) {
               object = object2;
            } else {
               Entry entry = (Entry)object2;
               float value2 = Math.abs(degrees - ((Number)entry.getKey()).floatValue());

               do {
                  Object object3 = iterator.next();
                  Entry entry2 = (Entry)object3;
                  float value3 = Math.abs(degrees - ((Number)entry2.getKey()).floatValue());
                  if (Float.compare(value2, value3) > 0) {
                     object2 = object3;
                     value2 = value3;
                  }
               } while (iterator.hasNext());

               object = object2;
            }
         }

         value = (Entry)object != null ? ((Number)((Entry)object).getValue()).floatValue() : 0.1F;
      } else {
         value = 0.1F;
      }

      float value4 = value;
      float value5 = 1.5F;
      Vec3 deltaMovement = MinecraftHolder.getLocalPlayer().getDeltaMovement();
      fireWorkVelocityEvent.setVelocity(
         deltaMovement.add(
            vector.x * value4 + (vector.x * value5 - deltaMovement.x) * 0.5,
            vector.y * value4 + (vector.y * value5 - deltaMovement.y) * 0.5,
            vector.z * value4 + (vector.z * value5 - deltaMovement.z) * 0.5
         )
      );
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (aABB2 != null
         && aABB != null
         && Aura.INSTANCE.getEntity() != null
         && Aura.INSTANCE.getEntity() instanceof Player
         && ((Player)Aura.INSTANCE.getEntity()).isFallFlying()
         && this.getPredict() != 0.0F) {
         Object object = null;
         byte byteValue = 4;
         float value = 0.0F;
         AABB aABB3 = aABB;
         AABB defaultImpl = Render3D.defaultImpl(Render3D.INSTANCE, aABB2, aABB3, value, byteValue, object);
         Render3D.defaultImpl2(Render3D.INSTANCE, defaultImpl, ThemeManager.INSTANCE.computeColor(0), null, 4, null);
         Render3D.defaultImpl3(Render3D.INSTANCE, defaultImpl, Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.computeColor(0), 1.0F), null, 4, null);
      }
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundAnimatePacket && Aura.INSTANCE.getEntity() != null) {
         Packet<? extends PacketListener> packet = receivePacket.getPacket();
         if (((ClientboundAnimatePacket)packet).getId() == Aura.INSTANCE.getEntity().getId()
            && (((ClientboundAnimatePacket)packet).getAction() == 0 || ((ClientboundAnimatePacket)packet).getAction() == 3)) {
            playerUtil4.update();
         }
      }
   }

   public final void applyGetItem(boolean flag, boolean flag2) {
      InventoryManager instance = InventoryManager.INSTANCE;
      Item[] items = new Item[]{Items.FIREWORK_ROCKET};
      SearchInvResult searchInvResult = instance.computeSearchInvResult(items);
      instance = InventoryManager.INSTANCE;
      Item[] items2 = new Item[]{Items.FIREWORK_ROCKET};
      SearchInvResult searchInvResult2 = instance.computeSearchInvResult2(items2);
      if (flag && !Blink.INSTANCE.getEnabled()) {
         if (!Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FIREWORK_ROCKET)) {
            if (searchInvResult2.isFlag()) {
               GuiMove.INSTANCE.setFlag5(true);
               if (flag2) {
                  ContainerUtil.defaultImpl(searchInvResult2.getValue(), 0, null, false, 14, null);
                  ContainerUtil.defaultImpl(45, 0, null, false, 14, null);
                  ContainerUtil.defaultImpl(searchInvResult2.getValue(), 0, null, false, 14, null);
               } else {
                  int value = searchInvResult2.getValue();
                  Object object = null;
                  byte byteValue = 8;
                  boolean flag3 = false;
                  ContainerInput swap = ContainerInput.SWAP;
                  byte byteValue2 = 40;
                  int value2 = value;
                  ContainerUtil.defaultImpl(value2, byteValue2, swap, flag3, byteValue, object);
               }

               ContainerUtil.defaultImpl(
                  Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FIREWORK_ROCKET)
                     ? InteractionHand.OFF_HAND
                     : InteractionHand.MAIN_HAND,
                  0.0F,
                  0.0F,
                  6,
                  null
               );
               ContainerUtil.applyHandleContainerInput(searchInvResult2.getValue(), 40, ContainerInput.SWAP, true);
               GuiMove.INSTANCE.setFlag5(false);
            } else {
               this.updateText10(ChatUtil.computeText(this, "no-fireworks", new Object[0], true));
            }
         } else {
            ContainerUtil.defaultImpl(
               Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FIREWORK_ROCKET)
                  ? InteractionHand.OFF_HAND
                  : InteractionHand.MAIN_HAND,
               0.0F,
               0.0F,
               6,
               null
            );
         }
      } else {
         InventoryManager.INSTANCE.applyGetInventory();
         if (searchInvResult.isFlag()) {
            searchInvResult.update();
         } else if (searchInvResult2.isFlag()) {
            ContainerUtil.applyHandleContainerInput(
               searchInvResult2.getValue(), MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() % 8 + 1, ContainerInput.SWAP, true
            );
            InventoryManager.INSTANCE.applyGetInventory2(MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() % 8 + 1);
         } else if (!Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FIREWORK_ROCKET)) {
            this.updateText10(ChatUtil.computeText(this, "no-fireworks", new Object[0], true));
            return;
         }

         ContainerUtil.defaultImpl(
            Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.FIREWORK_ROCKET)
               ? InteractionHand.OFF_HAND
               : InteractionHand.MAIN_HAND,
            0.0F,
            0.0F,
            6,
            null
         );
         InventoryManager.INSTANCE.update();
         if (!searchInvResult.isFlag() && searchInvResult2.isFlag()) {
            int value3 = searchInvResult2.getValue();
            int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            boolean flag4 = true;
            ContainerInput swap2 = ContainerInput.SWAP;
            int value4 = selectedSlot;
            int value5 = value3;
            ContainerUtil.applyHandleContainerInput(value5, value4, swap2, flag4);
         }
      }
   }

   public final void updateAABB(AABB box) {
      aABB2 = aABB;
      aABB = box;
   }

   private final Rotation getRotation() {
      for (Direction enumEntry : (Iterable<Direction>)ElytraTarget.EntriesMappings.enumEntries) {
         Vector3f vector3f = enumEntry.step().mul(14.0F);
         Rotation rotation2 = InteractionManager.INSTANCE.computeRotation(enumEntry);
         if (enumEntry != direction) {
            BlockHitResult clip = MinecraftHolder.getClientLevel()
               .clip(
                  new ClipContext(
                     this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()),
                     this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(vector3f.x, vector3f.y, vector3f.z),
                     Block.COLLIDER,
                     Fluid.NONE,
                     (Entity)MinecraftHolder.getLocalPlayer()
                  )
               );
            BlockHitResult clip2 = MinecraftHolder.getClientLevel()
               .clip(
                  new ClipContext(
                     this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).add(0.0, MinecraftHolder.getLocalPlayer().getBoundingBox().getYsize(), 0.0),
                     this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())
                        .add(vector3f.x, vector3f.y + MinecraftHolder.getLocalPlayer().getBoundingBox().getYsize(), vector3f.z),
                     Block.COLLIDER,
                     Fluid.NONE,
                     (Entity)MinecraftHolder.getLocalPlayer()
                  )
               );
            if (clip.getType() == Type.MISS && clip2.getType() == Type.MISS) {
               direction = enumEntry;
               rotation = rotation2;
               return rotation2;
            }
         }
      }

      return new Rotation(WorldUtil.INSTANCE.getAmount4(), -88.0F, false, null, 12, null);
   }

   private final void update() {
      Pair[] pairs = new Pair[]{
         TuplesKt.to(0.0F, this.getDegrees0()),
         TuplesKt.to(5.0F, this.getDegrees5()),
         TuplesKt.to(10.0F, this.getDegrees10()),
         TuplesKt.to(15.0F, this.getDegrees15()),
         TuplesKt.to(20.0F, this.getDegrees20()),
         TuplesKt.to(25.0F, this.getDegrees25()),
         TuplesKt.to(30.0F, this.getDegrees30()),
         TuplesKt.to(35.0F, this.getDegrees35()),
         TuplesKt.to(40.0F, this.getDegrees40()),
         TuplesKt.to(45.0F, this.getDegrees45()),
         TuplesKt.to(50.0F, this.getDegrees50()),
         TuplesKt.to(55.0F, this.getDegrees55()),
         TuplesKt.to(60.0F, this.getDegrees60()),
         TuplesKt.to(65.0F, this.getDegrees65()),
         TuplesKt.to(70.0F, this.getDegrees70()),
         TuplesKt.to(75.0F, this.getDegrees75()),
         TuplesKt.to(80.0F, this.getDegrees80()),
         TuplesKt.to(85.0F, this.getDegrees85()),
         TuplesKt.to(90.0F, this.getDegrees90())
      };
      map2 = MapsKt.hashMapOf(pairs);
   }

   public final float getAmount() {
      Entity entity = Aura.INSTANCE.getEntity();
      if (entity == null) {
         return 0.0F;
      } else {
         Entity entity2 = entity;
         if (vec3 == null) {
            vec3 = this.computeVec3(entity2);
         } else if (vec3.distanceTo(this.computeVec3(entity2)) < 0.1F) {
            vec3 = this.computeVec3(entity2);
            return 0.0F;
         }

         return !playerUtil4.testValue(15) && !this.getAlwaysPredict() ? 0.0F : this.getPredict();
      }
   }

   public static final void setFlag(boolean flag) {
      ElytraTarget.flag = flag;
   }

   public static final void setFlag2(boolean flag) {
      flag2 = flag;
   }

   public static final boolean testElytraTarget(ElytraTarget elytraTarget) {
      return elytraTarget.getFakeLag();
   }

   static {
      Pair[] pairs = new Pair[]{
         TuplesKt.to(0.0F, 0.155F),
         TuplesKt.to(5.0F, 0.16F),
         TuplesKt.to(10.0F, 0.19F),
         TuplesKt.to(15.0F, 0.22F),
         TuplesKt.to(20.0F, 0.22F),
         TuplesKt.to(25.0F, 0.27F),
         TuplesKt.to(30.0F, 0.28F),
         TuplesKt.to(35.0F, 0.34F),
         TuplesKt.to(40.0F, 0.37F),
         TuplesKt.to(45.0F, 0.37F),
         TuplesKt.to(50.0F, 0.34F),
         TuplesKt.to(55.0F, 0.34F),
         TuplesKt.to(60.0F, 0.2F),
         TuplesKt.to(65.0F, 0.18F),
         TuplesKt.to(70.0F, 0.16F),
         TuplesKt.to(75.0F, 0.16F),
         TuplesKt.to(80.0F, 0.13F),
         TuplesKt.to(85.0F, 0.13F),
         TuplesKt.to(90.0F, 0.13F)
      };
      map = MapsKt.hashMapOf(pairs);
      Pair[] pairs2 = new Pair[]{
         TuplesKt.to(0.0F, 0.155F),
         TuplesKt.to(5.0F, 0.16F),
         TuplesKt.to(10.0F, 0.19F),
         TuplesKt.to(15.0F, 0.22F),
         TuplesKt.to(20.0F, 0.22F),
         TuplesKt.to(25.0F, 0.27F),
         TuplesKt.to(30.0F, 0.28F),
         TuplesKt.to(35.0F, 0.34F),
         TuplesKt.to(40.0F, 0.37F),
         TuplesKt.to(45.0F, 0.37F),
         TuplesKt.to(50.0F, 0.34F),
         TuplesKt.to(55.0F, 0.34F),
         TuplesKt.to(60.0F, 0.2F),
         TuplesKt.to(65.0F, 0.18F),
         TuplesKt.to(70.0F, 0.16F),
         TuplesKt.to(75.0F, 0.16F),
         TuplesKt.to(80.0F, 0.13F),
         TuplesKt.to(85.0F, 0.13F),
         TuplesKt.to(90.0F, 0.13F)
      };
      map2 = MapsKt.hashMapOf(pairs2);
   }

   public static final class EntriesMappings {
      public static final EnumEntries enumEntries = EnumEntriesKt.enumEntries((Enum[])Direction.values());
   }

   public static final class Task implements Runnable {
      public final int value;

      public Task(int value) {
         this.value = value;
      }

      @Override
      public final void run() {
         try {
            Thread.sleep(this.value);
         } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return;
         }
         ElytraTarget.setFlag(false);
         NotificationManager.INSTANCE.setFlag(true);
         Blink.INSTANCE.applyUnplug();
         ElytraTarget.setFlag2(false);
         if (ElytraTarget.testElytraTarget(ElytraTarget.INSTANCE)) {
            Blink.INSTANCE.applyPlug();
            ElytraTarget.setFlag2(true);
         }

         NotificationManager.INSTANCE.setFlag(false);
      }
   }
}