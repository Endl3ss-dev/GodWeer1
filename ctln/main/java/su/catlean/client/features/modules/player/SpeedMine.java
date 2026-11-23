// deobf by pusia
package su.catlean.client.features.modules.player;

import java.awt.Color;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundSetCarriedItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.enums.AutoSwitchMode;
import su.catlean.api.event.events.network.AfterReceivePacket;
import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.player.AttackBlockEvent;
import su.catlean.api.event.events.player.CollisionEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.world.UpdateBlockBreakingProgressEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.AutoCrystal;
import su.catlean.client.features.modules.equip.ToolSaver;
import su.catlean.client.features.modules.world.FreeCam;
import su.catlean.client.features.modules.world.SpeedMine$Engine;
import su.catlean.client.services.InventoryManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.InteractionManagerAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.async.TaskScheduler;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.inventory.EnchantmentUtil;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class SpeedMine extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(SpeedMine.class, "mode", "getMode()Lsu/catlean/vp;", 0), new PropertyReference1Impl(SpeedMine.class, "timing", "getTiming()Lsu/catlean/yc;", 0), new PropertyReference1Impl(SpeedMine.class, "rebreak", "getRebreak()Lsu/catlean/ka;", 0), new PropertyReference1Impl(SpeedMine.class, "awaitAutoCrystal", "getAwaitAutoCrystal()Z", 0), new PropertyReference1Impl(SpeedMine.class, "instantAsync", "getInstantAsync()Z", 0), new PropertyReference1Impl(SpeedMine.class, "doubleMine", "getDoubleMine()Z", 0), new PropertyReference1Impl(SpeedMine.class, "doubleMinePacketOrder", "getDoubleMinePacketOrder()Lsu/catlean/kb;", 0), new PropertyReference1Impl(SpeedMine.class, "startMode", "getStartMode()Lsu/catlean/vd;", 0), new PropertyReference1Impl(SpeedMine.class, "switchMode", "getSwitchMode()Lsu/catlean/hq;", 0), new PropertyReference1Impl(SpeedMine.class, "swapDelay", "getSwapDelay()Z", 0), new PropertyReference1Impl(SpeedMine.class, "swapTiming", "getSwapTiming()Lsu/catlean/_x;", 0), new PropertyReference1Impl(SpeedMine.class, "factor", "getFactor()F", 0), new PropertyReference1Impl(SpeedMine.class, "speed", "getSpeed()F", 0), new PropertyReference1Impl(SpeedMine.class, "rotate", "getRotate()Z", 0), new PropertyReference1Impl(SpeedMine.class, "await", "getAwait()Z", 0), new PropertyReference1Impl(SpeedMine.class, "phase", "getPhase()Z", 0), new PropertyReference1Impl(SpeedMine.class, "resetOnSwitch", "getResetOnSwitch()Z", 0), new PropertyReference1Impl(SpeedMine.class, "pauseEat", "getPauseEat()Lsu/catlean/tp;", 0), new PropertyReference1Impl(SpeedMine.class, "renderGroup", "getRenderGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(SpeedMine.class, "renderMode", "getRenderMode()Lsu/catlean/m3;", 0), new PropertyReference1Impl(SpeedMine.class, "selection", "getSelection()Lsu/catlean/l7;", 0), new PropertyReference1Impl(SpeedMine.class, "startColor", "getStartColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(SpeedMine.class, "endColor", "getEndColor()Ljava/awt/Color;", 0) };
   @NotNull
   public static final SpeedMine INSTANCE = new SpeedMine();
   @NotNull
   private static final EnumSetting mode = SettingFactory.defaultImpl(INSTANCE, "mode", SpeedMine.Mode.PACKET, null, null, 12, null);
   @NotNull
   private static final EnumSetting timing = SettingFactory.defaultImpl(
      INSTANCE, "timing", SpeedMine.Timing.SEQUENTIAL, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE && INSTANCE.getRotate(), 4, null
   );
   @NotNull
   private static final EnumSetting rebreak = SettingFactory.defaultImpl(
      INSTANCE, "rebreak", SpeedMine.Rebreak.OFF, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final BooleanSetting awaitAutoCrystal = SettingFactory.defaultImpl(
      INSTANCE, "await-auto-crystal", true, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE && INSTANCE.getRebreak() == SpeedMine.Rebreak.GRIM, 4, null
   );
   @NotNull
   private static final BooleanSetting instantAsync = SettingFactory.defaultImpl(
      INSTANCE, "instant-async", true, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE && INSTANCE.getRebreak() == SpeedMine.Rebreak.GRIM, 4, null
   );
   @NotNull
   private static final BooleanSetting doubleMine = SettingFactory.defaultImpl(
      INSTANCE, "double-mine", false, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final EnumSetting packetOrder = SettingFactory.defaultImpl(
      INSTANCE, "packet-order", SpeedMine.PacketOrder.NEW, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE && INSTANCE.getDoubleMine(), 4, null
   );
   @NotNull
   private static final EnumSetting startMode = SettingFactory.defaultImpl(
      INSTANCE, "start-mode", SpeedMine.StartMode.ABORT, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE && !INSTANCE.getDoubleMine(), 4, null
   );
   @NotNull
   private static final EnumSetting switchMode = SettingFactory.defaultImpl(
      INSTANCE, "switch-mode", SpeedMine.SwitchMode.Alternative, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final BooleanSetting booleanSetting = SettingFactory.defaultImpl(
      INSTANCE, "1.21", false, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final EnumSetting swapTiming = SettingFactory.defaultImpl(
      INSTANCE, "swap-timing", SpeedMine.SwapTiming.ONCE, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final FloatSetting factor = SettingFactory.defaultImpl(
      INSTANCE, "factor", 1.0F, RangesKt.rangeTo(0.5F, 2.0F), null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 8, null
   );
   @NotNull
   private static final FloatSetting speed = SettingFactory.defaultImpl(
      INSTANCE, "speed", 0.5F, RangesKt.rangeTo(0.0F, 1.0F), null, () -> INSTANCE.getMode() == SpeedMine.Mode.DAMAGE, 8, null
   );
   @NotNull
   private static final BooleanSetting rotate = SettingFactory.defaultImpl(
      INSTANCE, "rotate", true, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final BooleanSetting await = SettingFactory.defaultImpl(
      INSTANCE, "await", true, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final BooleanSetting phase = SettingFactory.defaultImpl(
      INSTANCE, "phase", true, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final BooleanSetting resetOnSwitch = SettingFactory.defaultImpl(
      INSTANCE, "reset-on-switch", true, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final EnumSetting onEating = SettingFactory.defaultImpl(
      INSTANCE, "on-eating", SpeedMine.OnEating.NONE, null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 4, null
   );
   @NotNull
   private static final GroupSetting render = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "render", null, () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE, 2, null);
   @NotNull
   private static final EnumSetting renderMode = SettingFactory.computeEnumSetting(
      INSTANCE, "render-mode", SpeedMine.RenderMode.Shrink, INSTANCE.getRenderGroup(), () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE
   );
   @NotNull
   private static final EnumSetting selection = SettingFactory.computeEnumSetting(
      INSTANCE, "selection", SpeedMine.Selection.AIR_REBREAK, INSTANCE.getRenderGroup(), () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE
   );
   @NotNull
   private static final ColorSetting start = SettingFactory.computeColorSetting(
      INSTANCE, "start", new Color(255, 0, 0, 200), INSTANCE.getRenderGroup(), () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE
   );
   @NotNull
   private static final ColorSetting end = SettingFactory.computeColorSetting(
      INSTANCE, "end", new Color(47, 255, 0, 200), INSTANCE.getRenderGroup(), () -> INSTANCE.getMode() != SpeedMine.Mode.DAMAGE
   );
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @Nullable
   private static BlockPos blockPos;
   @Nullable
   private static BlockPos blockPos2;
   @NotNull
   private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
   @NotNull
   private static LinkedList<SpeedMine$Engine> linkedList = new LinkedList();
   @NotNull
   private static ConcurrentHashMap map = new ConcurrentHashMap();

   private SpeedMine() {
      super("speed-mine", SubCategories.getSubCategory10(), null, 4, null);
   }

   @NotNull
   public final SpeedMine.Mode getMode() {
      return (SpeedMine.Mode)mode.computeObject(this, $$delegatedProperties[0]);
   }

   @NotNull
   public final SpeedMine.Timing getTiming() {
      return (SpeedMine.Timing)timing.computeObject(this, $$delegatedProperties[1]);
   }

   @NotNull
   public final SpeedMine.Rebreak getRebreak() {
      return (SpeedMine.Rebreak)rebreak.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getAwaitAutoCrystal() {
      return (Boolean)awaitAutoCrystal.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getInstantAsync() {
      return (Boolean)instantAsync.computeObject(this, $$delegatedProperties[4]);
   }

   public final boolean getDoubleMine() {
      return (Boolean)doubleMine.computeObject(this, $$delegatedProperties[5]);
   }

   private final SpeedMine.PacketOrder getDoubleMinePacketOrder() {
      return (SpeedMine.PacketOrder)packetOrder.computeObject(this, $$delegatedProperties[6]);
   }

   private final SpeedMine.StartMode getStartMode() {
      return (SpeedMine.StartMode)startMode.computeObject(this, $$delegatedProperties[7]);
   }

   @NotNull
   public final SpeedMine.SwitchMode getSwitchMode() {
      return (SpeedMine.SwitchMode)switchMode.computeObject(this, $$delegatedProperties[8]);
   }

   private final boolean getSwapDelay() {
      return (Boolean)booleanSetting.computeObject(this, $$delegatedProperties[9]);
   }

   private final SpeedMine.SwapTiming getSwapTiming() {
      return (SpeedMine.SwapTiming)swapTiming.computeObject(this, $$delegatedProperties[10]);
   }

   private final float getFactor() {
      return ((Number)factor.computeObject(this, $$delegatedProperties[11])).floatValue();
   }

   private final float getSpeed() {
      return ((Number)speed.computeObject(this, $$delegatedProperties[12])).floatValue();
   }

   private final boolean getRotate() {
      return (Boolean)rotate.computeObject(this, $$delegatedProperties[13]);
   }

   private final boolean getAwait() {
      return (Boolean)await.computeObject(this, $$delegatedProperties[14]);
   }

   private final boolean getPhase() {
      return (Boolean)phase.computeObject(this, $$delegatedProperties[15]);
   }

   public final boolean getResetOnSwitch() {
      return (Boolean)resetOnSwitch.computeObject(this, $$delegatedProperties[16]);
   }

   private final SpeedMine.OnEating getPauseEat() {
      return (SpeedMine.OnEating)onEating.computeObject(this, $$delegatedProperties[17]);
   }

   private final GroupValue getRenderGroup() {
      return (GroupValue)render.computeObject(this, $$delegatedProperties[18]);
   }

   private final SpeedMine.RenderMode getRenderMode() {
      return (SpeedMine.RenderMode)renderMode.computeObject(this, $$delegatedProperties[19]);
   }

   private final SpeedMine.Selection getSelection() {
      return (SpeedMine.Selection)selection.computeObject(this, $$delegatedProperties[20]);
   }

   private final Color getStartColor() {
      return (Color)start.computeObject(this, $$delegatedProperties[21]);
   }

   private final Color getEndColor() {
      return (Color)end.computeObject(this, $$delegatedProperties[22]);
   }

   @NotNull
   public final AtomicBoolean getAtomicBoolean() {
      return atomicBoolean;
   }

   @NotNull
   public final LinkedList<SpeedMine$Engine> getLinkedList() {
      return linkedList;
   }

   @Override
   public void update4() {
      this.applyClear(AutoSwitchMode.TOGGLE);
   }

   @Override
   public void update2() {
      this.applyClear(AutoSwitchMode.TOGGLE);
   }

   private final void applyClear(AutoSwitchMode autoSwitchMode) {
      for (Object linked : linkedList) {
         SpeedMine$Engine engine = (SpeedMine$Engine)linked;
         engine.applyClear(autoSwitchMode);
      }

      linkedList.clear();
      blockPos = null;
      atomicBoolean.set(false);
   }

   @Flow(
      priority = -10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (!MinecraftHolder.getLocalPlayer().getAbilities().instabuild && !atomicBoolean.get()) {
         if (MinecraftHolder.getLocalPlayer().isUsingItem()) {
            if (this.getPauseEat() == SpeedMine.OnEating.PAUSE) {
               return;
            }

            if (this.getPauseEat() == SpeedMine.OnEating.RESET) {
               this.applyClear(AutoSwitchMode.EATING);
            }
         }

         if (this.getMode() == SpeedMine.Mode.DAMAGE
            && ((InteractionManagerAccessor)MinecraftHolder.getMultiPlayerGameMode()).getCurBlockDamageMP() < this.getSpeed()) {
            ((InteractionManagerAccessor)MinecraftHolder.getMultiPlayerGameMode()).setCurBlockDamageMP(this.getSpeed());
         }

         for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)map.entrySet()) {
            if (MinecraftHolder.getTime() - ((Number)entrySetItem.getValue()).longValue() > 200L
               || MinecraftHolder.getClientLevel().isEmptyBlock((BlockPos)entrySetItem.getKey())) {
               map.remove(entrySetItem.getKey());
            }
         }

         linkedList.removeIf(arg -> SpeedMine$Engine.defaultImpl((SpeedMine$Engine)arg, false, 1, null));
      }
   }

   @Flow
   private final void onUpdateBlockBreakingProgress(UpdateBlockBreakingProgressEvent updateBlockBreakingProgressEvent) {
      if (this.getMode() == SpeedMine.Mode.PACKET) {
         updateBlockBreakingProgressEvent.cancel();
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.getMode() != SpeedMine.Mode.DAMAGE) {
         for (Object linked : linkedList) {
            SpeedMine$Engine engine = (SpeedMine$Engine)linked;
            if (INSTANCE.getSelection() == SpeedMine.Selection.ALWAYS
               || INSTANCE.getSelection() == SpeedMine.Selection.REBREAK && INSTANCE.getRebreak() != SpeedMine.Rebreak.OFF
               || INSTANCE.getSelection() == SpeedMine.Selection.AIR_REBREAK
                  && INSTANCE.getRebreak() != SpeedMine.Rebreak.OFF
                  && MinecraftHolder.getClientLevel().isEmptyBlock(engine.getPos())) {
               Render3D.defaultImpl3(Render3D.INSTANCE, new AABB(engine.getPos()), Render2D.INSTANCE.computeColor(INSTANCE.getStartColor(), 1.0F), null, 4, null);
            }

            if (!MinecraftHolder.getClientLevel().isEmptyBlock(engine.getPos())) {
               float amount = Math.clamp(Render2D.INSTANCE.computeAmount(engine.getAmount2(), engine.getAmount(), Render3D.INSTANCE.getAmount4()), 0.0F, 1.0F);
               if (INSTANCE.getPauseEat() == SpeedMine.OnEating.PAUSE && MinecraftHolder.getLocalPlayer().isUsingItem()) {
                  amount = engine.getAmount();
               }

               if (engine.getAmount() >= 1.0F) {
                  amount = 1.0F;
               }
               AABB aabb = switch (SpeedMine.WhenMappings.values[INSTANCE.getRenderMode().ordinal()]) {
                  case 1 -> new AABB(engine.getPos());
                  case 2 -> new AABB(
                     engine.getPos().getX(),
                     engine.getPos().getY(),
                     engine.getPos().getZ(),
                     engine.getPos().getX() + 1,
                     engine.getPos().getY() + amount,
                     engine.getPos().getZ() + 1
                  );
                  case 3 -> new AABB(
                        engine.getPos().getX(),
                        engine.getPos().getY(),
                        engine.getPos().getZ(),
                        engine.getPos().getX(),
                        engine.getPos().getY(),
                        engine.getPos().getZ()
                     )
                     .contract(amount, amount, amount)
                     .move(0.5 + amount * 0.5, 0.5 + amount * 0.5, 0.5 + amount * 0.5);
                  default -> throw new NoWhenBranchMatchedException();
               };
               Render3D.defaultImpl2(
                  Render3D.INSTANCE, aabb, Render2D.INSTANCE.fetchAlpha(INSTANCE.getStartColor(), INSTANCE.getEndColor(), amount), null, 4, null
               );
               Render3D.defaultImpl3(
                  Render3D.INSTANCE,
                  aabb,
                  Render2D.INSTANCE
                     .fetchAlpha(
                        Render2D.INSTANCE.computeColor(INSTANCE.getStartColor(), 1.0F), Render2D.INSTANCE.computeColor(INSTANCE.getEndColor(), 1.0F), amount
                     ),
                  null,
                  4,
                  null
               );
            }
         }
      }
   }

   @Flow
   private final void onAttackBlock(AttackBlockEvent attackBlockEvent) {
      if (this.checkBlockInteractionRange(attackBlockEvent.getPos())
         && !MinecraftHolder.getLocalPlayer().getAbilities().instabuild
         && this.getMode() != SpeedMine.Mode.DAMAGE) {
         attackBlockEvent.cancel();
         this.applyGetAbilities(attackBlockEvent.getPos(), attackBlockEvent.getDirection());
      }
   }

   public final void applyGetAbilities(BlockPos pos, Direction direction) {
      if (this.checkBlockInteractionRange(pos) && !MinecraftHolder.getLocalPlayer().getAbilities().instabuild && this.getMode() != SpeedMine.Mode.DAMAGE) {
         if (!this.checkIsEmpty(pos)) {
            if (!this.getDoubleMine() && !linkedList.isEmpty()) {
               ((SpeedMine$Engine)linkedList.removeFirst()).applyClear();
            }

            if (this.getDoubleMine() && linkedList.size() >= 2) {
               Iterable<SpeedMine$Engine> linkedList2 = linkedList;
               Iterator<SpeedMine$Engine> iterator = linkedList2.iterator();

               SpeedMine$Engine object;
               SpeedMine$Engine engine;
               do {
                  if (!iterator.hasNext()) {
                     throw new NoSuchElementException("Collection contains no element matching the predicate.");
                  }

                  object = iterator.next();
                  engine = (SpeedMine$Engine)object;
               } while (!Intrinsics.areEqual(engine.getPos(), blockPos2));

               SpeedMine$Engine engine2 = (SpeedMine$Engine)object;
               engine2.applyClear();
               linkedList.remove(engine2);
            }

            blockPos2 = pos;
            if (linkedList.size() >= 2) {
               // [deobf] original threw a checked Exception from Kotlin (unchecked there)
               throw new RuntimeException("Already mining!");
            }

            linkedList.add(new SpeedMine$Engine(pos, direction));
            MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
         }
      }
   }

   @Flow
   private final void onAfterReceivePacket(AfterReceivePacket afterReceivePacket) {
      if (afterReceivePacket.getPacket() instanceof ClientboundBlockUpdatePacket
         && this.getRebreak() == SpeedMine.Rebreak.GRIM
         && this.getInstantAsync()
         && Intrinsics.areEqual(((ClientboundBlockUpdatePacket)afterReceivePacket.getPacket()).getBlockState().getBlock(), Blocks.OBSIDIAN)) {
         for (SpeedMine$Engine linked : (Iterable<su.catlean.client.features.modules.world.SpeedMine$Engine>)linkedList) {
            if (linked.isFlag()
               && Intrinsics.areEqual(linked.getPos(), ((ClientboundBlockUpdatePacket)afterReceivePacket.getPacket()).getPos())
               && (!this.checkGetY(((ClientboundBlockUpdatePacket)afterReceivePacket.getPacket()).getPos()) || !this.getAwaitAutoCrystal())) {
               linked.checkGetBlockState(true);
            }
         }
      }
   }

   @Flow
   private final void onAfterSendPacket(AfterSendPacket afterSendPacket) {
      if (afterSendPacket.getPacket() instanceof ServerboundSetCarriedItemPacket
         && this.getResetOnSwitch()
         && this.getSwitchMode() != SpeedMine.SwitchMode.Silent
         && this.getRebreak() != SpeedMine.Rebreak.GRIM) {
         TaskScheduler.defaultImpl(TaskScheduler.INSTANCE, 0, (byte)0, false, () -> {
            for (Object linked : linkedList) {
               SpeedMine$Engine engine = (SpeedMine$Engine)linked;
               engine.applyClear(AutoSwitchMode.SWITCH);
            }
         }, 6, null);
      }
   }

   @Flow
   private final void onCollision(CollisionEvent collisionEvent) {
      if (this.isFlag()) {
         Map map2 = map;
         Map<Object, Object> linkedHashMap = new LinkedHashMap();

         for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)map2.entrySet()) {
            if (Intrinsics.areEqual(collisionEvent.getPos(), entrySetItem.getKey())) {
               linkedHashMap.put(entrySetItem.getKey(), entrySetItem.getValue());
            }
         }

         for (Map.Entry<Object, Object> entrySetItem2 : linkedHashMap.entrySet()) {
            if (Intrinsics.areEqual(collisionEvent.getPos(), entrySetItem2.getKey())) {
               collisionEvent.setState(Blocks.AIR.defaultBlockState());
            }
         }
      }
   }

   public final boolean isFlag() {
      if (this.getEnabled() && this.getPhase()) {
         Map map2 = map;
         boolean flag;
         if (map2.isEmpty()) {
            flag = false;
         } else {
            label41: {
               for (Entry entrySetItem : (Iterable<java.util.Map.Entry>)map2.entrySet()) {
                  if (MinecraftHolder.getTime() - ((Number)entrySetItem.getValue()).longValue() < 200L) {
                     flag = true;
                     break label41;
                  }
               }

               flag = false;
            }
         }

         if (flag) {
            return true;
         }
      }

      return false;
   }

   public final boolean checkIsEmpty(BlockPos blockPos) {
      var linkedList2 = linkedList;
      boolean flag;
      if (linkedList2 instanceof Collection && linkedList2.isEmpty()) {
         flag = false;
      } else {
         Iterator<SpeedMine$Engine> iterator = linkedList2.iterator();

         while (true) {
            if (!iterator.hasNext()) {
               flag = false;
               break;
            }

            SpeedMine$Engine object = iterator.next();
            SpeedMine$Engine engine = (SpeedMine$Engine)object;
            if (Intrinsics.areEqual(engine.getPos(), blockPos)) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private final void update() {
      Packet serverboundContainerClosePacket = (Packet)(new ServerboundContainerClosePacket(MinecraftHolder.getLocalPlayer().containerMenu.containerId));
      PacketUtil.applySend(serverboundContainerClosePacket);
   }

   private final float computeAmount(BlockState blockState, BlockPos blockPos, int value, boolean flag) {
      if (Intrinsics.areEqual(blockState, Blocks.AIR.defaultBlockState())) {
         return 0.0F;
      } else {
         float destroySpeed = blockState.getDestroySpeed((BlockGetter)MinecraftHolder.getClientLevel(), blockPos);
         if (destroySpeed < 0.0F) {
            return 0.0F;
         } else {
            ItemStack mainHandItem = value == -1 ? MinecraftHolder.getLocalPlayer().getMainHandItem() : MinecraftHolder.getLocalPlayer().getInventory().getItem(value);
            return this.fetchAmplifier(blockState, value, flag)
               / destroySpeed
               / (blockState.requiresCorrectToolForDrops() && !mainHandItem.isCorrectToolForDrops(blockState) ? 100.0F : 30.0F);
         }
      }
   }

   private final boolean checkGetY(BlockPos blockPos) {
      return AutoCrystal.INSTANCE.getEnabled() && AutoCrystal.INSTANCE.getPlayer() != null && blockPos.getY() > AutoCrystal.INSTANCE.getPlayer().getY() + 1;
   }

   private final float fetchInventory(BlockState blockState, int value) {
      float value2 = 1.0F;
      if (value != -1 && !MinecraftHolder.getLocalPlayer().getInventory().getItem(value).isEmpty()) {
         value2 *= MinecraftHolder.getLocalPlayer().getInventory().getItem(value).getDestroySpeed(blockState);
      }

      return value2;
   }

   private final float fetchAmplifier(BlockState blockState, int value, boolean flag) {
      float inventory = this.fetchInventory(blockState, value);
      if (inventory > 1.0F && value != -1) {
         ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(value);
         int itemEnchantmentLevel = EnchantmentUtil.fetchItemEnchantmentLevel(item, Enchantments.EFFICIENCY);
         if (itemEnchantmentLevel > 0 && !item.isEmpty()) {
            inventory += (float)(StrictMath.pow(itemEnchantmentLevel, 2.0) + 1);
         }
      }

      if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.HASTE)) {
         inventory *= 1 + (MinecraftHolder.getLocalPlayer().getEffect(MobEffects.HASTE).getAmplifier() + 1) * 0.2F;
      }

      if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.MINING_FATIGUE)) {
         inventory *= (float)Math.pow(0.3F, MinecraftHolder.getLocalPlayer().getEffect(MobEffects.MINING_FATIGUE).getAmplifier() + 1);
      }

      if (MinecraftHolder.getLocalPlayer().isUnderWater()) {
         inventory *= (float)MinecraftHolder.getLocalPlayer().getAttribute(Attributes.SUBMERGED_MINING_SPEED).getValue();
      }

      if (!flag && !FreeCam.INSTANCE.getEnabled()) {
         inventory /= 5.0F;
      }

      return inventory < 0.0F ? 0.0F : inventory * this.getFactor();
   }

   private final int computeValue(BlockPos blockPos) {
      int blockState = this.fetchBlockState(blockPos, true);
      return blockState != -1 ? blockState : this.fetchBlockState(blockPos, false);
   }

   private final int fetchBlockState(BlockPos blockPos, boolean flag) {
      int value = -1;
      float maX_VALUE = flag ? Float.MAX_VALUE : Float.MIN_VALUE;
      BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(blockPos);
      if (blockState.getBlock() instanceof AirBlock) {
         return -1;
      } else {
         for (int i = 9; i < 45; i++) {
            ItemStack item = MinecraftHolder.getLocalPlayer().getInventory().getItem(i >= 36 ? i - 36 : i);
            if (!Intrinsics.areEqual(item, ItemStack.EMPTY)
               && (item.isCorrectToolForDrops(blockState) || !flag)
               && (item.getMaxDamage() - item.getDamageValue() > 4 || !ToolSaver.INSTANCE.getEnabled())) {
               float itemEnchantmentLevel = EnchantmentUtil.fetchItemEnchantmentLevel(item, Enchantments.EFFICIENCY)
                  + item.getDestroySpeed(MinecraftHolder.getClientLevel().getBlockState(blockPos));
               if (!flag) {
                  if (itemEnchantmentLevel > maX_VALUE) {
                     maX_VALUE = itemEnchantmentLevel;
                     value = i;
                  }
               } else if (itemEnchantmentLevel < maX_VALUE) {
                  maX_VALUE = itemEnchantmentLevel;
                  value = i;
               }
            }
         }

         return value >= 36 ? value - 36 : value;
      }
   }

   private final boolean checkBlockInteractionRange(BlockPos blockPos) {
      BlockState blockState = MinecraftHolder.getClientLevel().getBlockState(blockPos);
      return blockState.getBlock().defaultDestroyTime() != -1.0F
         && (
            MinecraftHolder.getLocalPlayer().getEyePosition().distanceTo(Vec3iUtil.computeVec3(blockPos))
                  <= MinecraftHolder.getLocalPlayer().blockInteractionRange() + 0.5F
               || this.getDoubleMine() && !Intrinsics.areEqual(blockPos, blockPos2)
         );
   }

   private final void applyGetInventory(int value, int value2) {
      if (this.getSwitchMode() != SpeedMine.SwitchMode.Alternative && value < 9) {
         if (this.getSwitchMode() == SpeedMine.SwitchMode.Silent) {
            InventoryManager.INSTANCE.applyGetConnection(value);
         } else {
            InventoryManager.INSTANCE.applyGetInventory2(value);
         }
      } else {
         if (value2 == -1) {
            int value3 = value < 9 ? value + 36 : value;
            int selectedSlot = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            Object object = null;
            byte byteValue = 8;
            boolean flag = false;
            ContainerInput swap = ContainerInput.SWAP;
            int value4 = selectedSlot;
            int value5 = value3;
            ContainerUtil.defaultImpl(value5, value4, swap, flag, byteValue, object);
         } else {
            int value22 = value2 < 9 ? value2 + 36 : value2;
            int selectedSlot2 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
            Object object2 = null;
            byte byteValue2 = 8;
            boolean flag2 = false;
            ContainerInput swap2 = ContainerInput.SWAP;
            int value6 = selectedSlot2;
            int value7 = value22;
            ContainerUtil.defaultImpl(value7, value6, swap2, flag2, byteValue2, object2);
         }

         this.update();
      }

      AutoCrystal.INSTANCE.getAtomicBoolean().set(false);
      atomicBoolean.set(false);
   }

   public final boolean isFlag2() {
      return this.getDoubleMine() ? linkedList.size() > 1 : !linkedList.isEmpty() && ((SpeedMine$Engine)CollectionsKt.first(linkedList)).getAmount2() != -1.0F;
   }

   @NotNull
   public final List<Object> computeItems(boolean flag) {
      if (linkedList.isEmpty()) {
         return CollectionsKt.emptyList();
      } else {
         if (linkedList.size() == 1) {
            SpeedMine$Engine first = (SpeedMine$Engine)CollectionsKt.first(linkedList);
            if (INSTANCE.checkIsEmptyBlock(first)) {
               return CollectionsKt.listOf(first.getPos());
            }
         } else if (linkedList.size() == 2) {
            Iterable<SpeedMine$Engine> linkedList2 = linkedList;
            Iterator<SpeedMine$Engine> iterator = linkedList2.iterator();

            SpeedMine$Engine object;
            SpeedMine$Engine engine;
            do {
               if (!iterator.hasNext()) {
                  throw new NoSuchElementException("Collection contains no element matching the predicate.");
               }

               object = iterator.next();
               engine = (SpeedMine$Engine)object;
            } while (!Intrinsics.areEqual(engine.getPos(), blockPos2));

            SpeedMine$Engine engine2 = (SpeedMine$Engine)object;
            if (this.checkIsEmptyBlock(engine2)) {
               BlockPos[] blockPoss = new BlockPos[]{((SpeedMine$Engine)CollectionsKt.first(linkedList)).getPos(), engine2.getPos()};
               return CollectionsKt.listOf(blockPoss);
            }
         }

         return !stopWatch.testValue(200) && blockPos != null && flag ? CollectionsKt.listOf(blockPos) : CollectionsKt.emptyList();
      }
   }

   public static List<Object> defaultImpl(SpeedMine speedMine, boolean flag, int value, Object object) {
      if ((value & 1) != 0) {
         flag = true;
      }

      return speedMine.computeItems(flag);
   }

   private final boolean checkIsEmptyBlock(SpeedMine$Engine engine) {
      return engine.getAmount() < 1.0F
         && engine.getAmount() + (engine.getAmount() - engine.getAmount2()) * 2.5F >= 1.0F
         && !MinecraftHolder.getClientLevel().isEmptyBlock(engine.getPos());
   }

   public static final int computeValue(SpeedMine speedMine, BlockPos blockPos) {
      return speedMine.computeValue(blockPos);
   }

   public static final SpeedMine.SwapTiming computeSwapTiming(SpeedMine speedMine) {
      return speedMine.getSwapTiming();
   }

   public static final void updateSpeedMine(SpeedMine speedMine, int value, int value2) {
      speedMine.applyGetInventory(value, value2);
   }

   public static final SpeedMine.PacketOrder computePacketOrder(SpeedMine speedMine) {
      return speedMine.getDoubleMinePacketOrder();
   }

   public static final SpeedMine.StartMode computeStartMode(SpeedMine speedMine) {
      return speedMine.getStartMode();
   }

   public static final boolean testSpeedMine(SpeedMine speedMine) {
      return speedMine.getSwapDelay();
   }

   public static final BlockPos getBlockPos2() {
      return blockPos2;
   }

   public static final boolean testSpeedMine(SpeedMine speedMine, BlockPos blockPos) {
      return speedMine.checkBlockInteractionRange(blockPos);
   }

   public static final float computeAmount(SpeedMine speedMine, BlockState blockState, BlockPos blockPos, int value, boolean flag) {
      return speedMine.computeAmount(blockState, blockPos, value, flag);
   }

   public static final SpeedMine.OnEating computeOnEating(SpeedMine speedMine) {
      return speedMine.getPauseEat();
   }

   public static final ConcurrentHashMap getMap() {
      return map;
   }

   public static final boolean testSpeedMine2(SpeedMine speedMine) {
      return speedMine.getAwait();
   }

   public static final boolean testSpeedMine3(SpeedMine speedMine) {
      return speedMine.getRotate();
   }

   public static final boolean testSpeedMine2(SpeedMine speedMine, BlockPos blockPos) {
      return speedMine.checkGetY(blockPos);
   }

   public static final boolean testSpeedMine4(SpeedMine speedMine) {
      return speedMine.getAwaitAutoCrystal();
   }

   public static final StopWatch getStopWatch() {
      return stopWatch;
   }

   public static final void setBlockPos(BlockPos blockPos) {
      SpeedMine.blockPos = blockPos;
   }

   static {
   }

   public static enum Mode {
      PACKET,
      DAMAGE;

      private static final SpeedMine.Mode[] modes = $values();
      private static final EnumEntries<SpeedMine.Mode> enumEntries = EnumEntriesKt.enumEntries(modes);

      private Mode() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.Mode> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.Mode[] $values() {
         return new SpeedMine.Mode[]{PACKET, DAMAGE};
      }
   }

   public static enum OnEating {
      PAUSE,
      GRIM,
      RESET,
      NONE;

      private static final SpeedMine.OnEating[] onEatings = $values();
      private static final EnumEntries<SpeedMine.OnEating> enumEntries = EnumEntriesKt.enumEntries(onEatings);

      private OnEating() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.OnEating> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.OnEating[] $values() {
         return new SpeedMine.OnEating[]{PAUSE, GRIM, RESET, NONE};
      }
   }

   public static enum PacketOrder {
      OLD,
      NEW;

      private static final SpeedMine.PacketOrder[] packetOrders = $values();
      private static final EnumEntries<SpeedMine.PacketOrder> enumEntries = EnumEntriesKt.enumEntries(packetOrders);

      private PacketOrder() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.PacketOrder> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.PacketOrder[] $values() {
         return new SpeedMine.PacketOrder[]{OLD, NEW};
      }
   }

   public static enum Rebreak {
      OFF,
      GRIM,
      NCP_AIR;

      private static final SpeedMine.Rebreak[] rebreaks = $values();
      private static final EnumEntries<SpeedMine.Rebreak> enumEntries = EnumEntriesKt.enumEntries(rebreaks);

      private Rebreak() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.Rebreak> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.Rebreak[] $values() {
         return new SpeedMine.Rebreak[]{OFF, GRIM, NCP_AIR};
      }
   }

   public static enum RenderMode {
      Block,
      Shrink,
      Grow;

      private static final SpeedMine.RenderMode[] renderModes = $values();
      private static final EnumEntries<SpeedMine.RenderMode> enumEntries = EnumEntriesKt.enumEntries(renderModes);

      private RenderMode() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.RenderMode> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.RenderMode[] $values() {
         return new SpeedMine.RenderMode[]{Block, Shrink, Grow};
      }
   }

   public static enum Selection {
      ALWAYS,
      AIR_REBREAK,
      REBREAK,
      OFF;

      private static final SpeedMine.Selection[] selections = $values();
      private static final EnumEntries<SpeedMine.Selection> enumEntries = EnumEntriesKt.enumEntries(selections);

      private Selection() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.Selection> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.Selection[] $values() {
         return new SpeedMine.Selection[]{ALWAYS, AIR_REBREAK, REBREAK, OFF};
      }
   }

   public static enum StartMode {
      ABORT,
      STOP,
      NONE;

      private static final SpeedMine.StartMode[] startModes = $values();
      private static final EnumEntries<SpeedMine.StartMode> enumEntries = EnumEntriesKt.enumEntries(startModes);

      private StartMode() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.StartMode> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.StartMode[] $values() {
         return new SpeedMine.StartMode[]{ABORT, STOP, NONE};
      }
   }

   public static enum SwapTiming {
      ONCE,
      ALWAYS;

      private static final SpeedMine.SwapTiming[] swapTimings = $values();
      private static final EnumEntries<SpeedMine.SwapTiming> enumEntries = EnumEntriesKt.enumEntries(swapTimings);

      private SwapTiming() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.SwapTiming> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.SwapTiming[] $values() {
         return new SpeedMine.SwapTiming[]{ONCE, ALWAYS};
      }
   }

   public static enum SwitchMode {
      Silent,
      Normal,
      Alternative;

      private static final SpeedMine.SwitchMode[] switchModes = $values();
      private static final EnumEntries<SpeedMine.SwitchMode> enumEntries = EnumEntriesKt.enumEntries(switchModes);

      private SwitchMode() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.SwitchMode> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.SwitchMode[] $values() {
         return new SpeedMine.SwitchMode[]{Silent, Normal, Alternative};
      }
   }

   public static enum Timing {
      VANILLA,
      SEQUENTIAL,
      GRIM;

      private static final SpeedMine.Timing[] timings = $values();
      private static final EnumEntries<SpeedMine.Timing> enumEntries = EnumEntriesKt.enumEntries(timings);

      private Timing() {
      }

      @NotNull
      public static EnumEntries<SpeedMine.Timing> getEnumEntries() {
         return enumEntries;
      }

      private static final SpeedMine.Timing[] $values() {
         return new SpeedMine.Timing[]{VANILLA, SEQUENTIAL, GRIM};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] values2 = new int[SpeedMine.RenderMode.values().length];

         try {
            values2[SpeedMine.RenderMode.Block.ordinal()] = 1;
         } catch (NoSuchFieldError exception) {
         }

         try {
            values2[SpeedMine.RenderMode.Grow.ordinal()] = 2;
         } catch (NoSuchFieldError exception2) {
         }

         try {
            values2[SpeedMine.RenderMode.Shrink.ordinal()] = 3;
         } catch (NoSuchFieldError exception3) {
         }

         values = values2;
      }
   }
}