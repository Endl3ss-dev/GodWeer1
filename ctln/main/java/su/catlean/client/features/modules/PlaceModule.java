// deobf by pusia
package su.catlean.client.features.modules;

import net.minecraft.world.item.Item;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ServerboundAttackPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.category.SubCategory;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.enums.TargetFilter;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.player.AutoMend;
import su.catlean.client.features.modules.player.BoxShrinker;
import su.catlean.client.features.modules.player.SpeedMine;
import su.catlean.client.features.modules.rage.Blink;
import su.catlean.client.render.BoxRenderCommand;
import su.catlean.client.services.InteractionManager;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.RotationManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.client.services.rotation.RotationAction;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.world.WorldUtil;

public abstract class PlaceModule extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(PlaceModule.class, "timing", "getTiming()Lsu/catlean/u_;", 0), new MutablePropertyReference1Impl(PlaceModule.class, "blocksPerAction", "getBlocksPerAction()I", 0), new MutablePropertyReference1Impl(PlaceModule.class, "delayPerAction", "getDelayPerAction()I", 0), new MutablePropertyReference1Impl(PlaceModule.class, "rotate", "getRotate()Z", 0), new MutablePropertyReference1Impl(PlaceModule.class, "grimSilent", "getGrimSilent()Z", 0), new MutablePropertyReference1Impl(PlaceModule.class, "placeMode", "getPlaceMode()Lsu/catlean/eb;", 0), new MutablePropertyReference1Impl(PlaceModule.class, "interact", "getInteract()Lsu/catlean/tm;", 0), new MutablePropertyReference1Impl(PlaceModule.class, "switch", "getSwitch()Lsu/catlean/iu;", 0), new MutablePropertyReference1Impl(PlaceModule.class, "range", "getRange()F", 0), new MutablePropertyReference1Impl(PlaceModule.class, "wallRange", "getWallRange()F", 0), new PropertyReference1Impl(PlaceModule.class, "pauseGroup", "getPauseGroup()Lsu/catlean/nl;", 0), new PropertyReference1Impl(PlaceModule.class, "mining", "getMining()Z", 0), new PropertyReference1Impl(PlaceModule.class, "eating", "getEating()Z", 0), new PropertyReference1Impl(PlaceModule.class, "inventory", "getInventory()Z", 0), new PropertyReference1Impl(PlaceModule.class, "blink", "getBlink()Z", 0), new MutablePropertyReference1Impl(PlaceModule.class, "render", "getRender()Lsu/catlean/p;", 0), new MutablePropertyReference1Impl(PlaceModule.class, "renderAnimation", "getRenderAnimation()Lsu/catlean/yp;", 0), new PropertyReference1Impl(PlaceModule.class, "syncColors", "getSyncColors()Z", 0), new PropertyReference1Impl(PlaceModule.class, "fillColor", "getFillColor()Ljava/awt/Color;", 0), new PropertyReference1Impl(PlaceModule.class, "outlineColor", "getOutlineColor()Ljava/awt/Color;", 0) };
   private final int value;
   private boolean flag;
   @NotNull
   private final EnumSetting timing;
   @NotNull
   private final IntSetting blocksPerAction;
   @NotNull
   private final IntSetting delayPerAction;
   @NotNull
   private final BooleanSetting rotate;
   @NotNull
   private final BooleanSetting grimSilent;
   @NotNull
   private final EnumSetting placeMode;
   @NotNull
   private final EnumSetting interact;
   @NotNull
   private final EnumSetting switchValue;
   @NotNull
   private final FloatSetting range;
   @NotNull
   private final FloatSetting wallRange;
   @NotNull
   private final GroupSetting pause;
   @NotNull
   private final BooleanSetting mining;
   @NotNull
   private final BooleanSetting eating;
   @NotNull
   private final BooleanSetting inventory;
   @NotNull
   private final BooleanSetting blink;
   @NotNull
   private final EnumSetting render;
   @NotNull
   private final EnumSetting animation;
   @NotNull
   private final BooleanSetting syncColors;
   @NotNull
   private final ColorSetting fillColor;
   @NotNull
   private final ColorSetting outlineColor;
   @NotNull
   private final StopWatch stopWatch;
   private int value2;

   public PlaceModule(String id, SubCategory category, int value, boolean flag) {
      super(id, category, null, 4, null);
      this.value = value;
      this.flag = flag;
      this.timing = SettingFactory.defaultImpl(this, "timing", PlaceModule.Timing.Vanilla, null, null, 12, null);
      this.blocksPerAction = SettingFactory.defaultImpl(
         this, "blocks-per-action", 6, new IntRange(2, 20), null, () -> this.getTiming() == PlaceModule.Timing.Instant, 8, null
      );
      this.delayPerAction = SettingFactory.defaultImpl(
         this, "delay-per-action", 0, new IntRange(0, 10), null, () -> this.getTiming() == PlaceModule.Timing.Instant, 8, null
      );
      this.rotate = SettingFactory.defaultImpl(this, "rotate", true, null, null, 12, null);
      this.grimSilent = SettingFactory.defaultImpl(
         this, "grim-silent", false, null, () -> this.getRotate() && this.getTiming() == PlaceModule.Timing.Instant, 4, null
      );
      this.placeMode = SettingFactory.defaultImpl(this, "place-mode", PlaceModule.PlaceMode.Normal, null, null, 12, null);
      this.interact = SettingFactory.defaultImpl(this, "interact", PlaceModule.Interact.Strict, null, null, 12, null);
      this.switchValue = SettingFactory.defaultImpl(this, "switch", PlaceModule.Switch.Silent, null, null, 12, null);
      this.range = SettingFactory.defaultImpl(this, "range", 4.0F, RangesKt.rangeTo(2.0F, 10.0F), null, () -> this.flag, 8, null);
      this.wallRange = SettingFactory.defaultImpl(this, "wall-range", 4.0F, RangesKt.rangeTo(2.0F, 10.0F), null, () -> this.flag, 8, null);
      this.pause = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)this, "pause", PlaceModule.Pause.BOOLS, null, 4, null);
      this.mining = SettingFactory.defaultImpl(this, "mining", false, this.getPauseGroup(), null, 8, null);
      this.eating = SettingFactory.defaultImpl(this, "eating", false, this.getPauseGroup(), null, 8, null);
      this.inventory = SettingFactory.defaultImpl(this, "inventory", false, this.getPauseGroup(), null, 8, null);
      this.blink = SettingFactory.defaultImpl(this, "blink", false, this.getPauseGroup(), null, 8, null);
      this.render = SettingFactory.defaultImpl(this, "render", PlaceModule.Render.BOTH, null, null, 12, null);
      this.animation = SettingFactory.defaultImpl(this, "animation", PlaceModule.Animation.Fade, null, null, 12, null);
      this.syncColors = SettingFactory.defaultImpl(this, "sync-colors", false, null, null, 12, null);
      this.fillColor = SettingFactory.defaultImpl(this, "fill-color", Color.WHITE, null, () -> !this.getSyncColors(), 4, null);
      this.outlineColor = SettingFactory.defaultImpl(this, "outline-color", Color.WHITE, null, () -> !this.getSyncColors(), 4, null);
      this.stopWatch = new StopWatch();
   }

   public PlaceModule(String text, SubCategory subCategory, int value, boolean flag, int value2, DefaultConstructorMarker defaultConstructorMarker) {
      if ((value2 & 4) != 0) {
         value = 150;
      }

      if ((value2 & 8) != 0) {
         flag = true;
      }

      this(text, subCategory, value, flag);
   }

   public final int getValue() {
      return this.value;
   }

   private final PlaceModule.Timing getTiming() {
      return (PlaceModule.Timing)this.timing.computeObject(this, $$delegatedProperties[0]);
   }

   private final int getBlocksPerAction() {
      return ((Number)this.blocksPerAction.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final int getDelayPerAction() {
      return ((Number)this.delayPerAction.computeObject(this, $$delegatedProperties[2])).intValue();
   }

   public final boolean getRotate() {
      return (Boolean)this.rotate.computeObject(this, $$delegatedProperties[3]);
   }

   public final boolean getGrimSilent() {
      return (Boolean)this.grimSilent.computeObject(this, $$delegatedProperties[4]);
   }

   @NotNull
   public final PlaceModule.PlaceMode getPlaceMode() {
      return (PlaceModule.PlaceMode)this.placeMode.computeObject(this, $$delegatedProperties[5]);
   }

   @NotNull
   public final PlaceModule.Interact getInteract() {
      return (PlaceModule.Interact)this.interact.computeObject(this, $$delegatedProperties[6]);
   }

   @NotNull
   public final PlaceModule.Switch getSwitch() {
      return (PlaceModule.Switch)this.switchValue.computeObject(this, $$delegatedProperties[7]);
   }

   public final float getRange() {
      return ((Number)this.range.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   public final float getWallRange() {
      return ((Number)this.wallRange.computeObject(this, $$delegatedProperties[9])).floatValue();
   }

   @NotNull
   public final GroupValue getPauseGroup() {
      return (GroupValue)this.pause.computeObject(this, $$delegatedProperties[10]);
   }

   public final boolean getMining() {
      return (Boolean)this.mining.computeObject(this, $$delegatedProperties[11]);
   }

   public final boolean getEating() {
      return (Boolean)this.eating.computeObject(this, $$delegatedProperties[12]);
   }

   public final boolean getInventory() {
      return (Boolean)this.inventory.computeObject(this, $$delegatedProperties[13]);
   }

   public final boolean getBlink() {
      return (Boolean)this.blink.computeObject(this, $$delegatedProperties[14]);
   }

   @NotNull
   public final PlaceModule.Render getRender() {
      return (PlaceModule.Render)this.render.computeObject(this, $$delegatedProperties[15]);
   }

   @NotNull
   public final PlaceModule.Animation getRenderAnimation() {
      return (PlaceModule.Animation)this.animation.computeObject(this, $$delegatedProperties[16]);
   }

   public final boolean getSyncColors() {
      return (Boolean)this.syncColors.computeObject(this, $$delegatedProperties[17]);
   }

   @NotNull
   public final Color getFillColor() {
      return (Color)this.fillColor.computeObject(this, $$delegatedProperties[18]);
   }

   @NotNull
   public final Color getOutlineColor() {
      return (Color)this.outlineColor.computeObject(this, $$delegatedProperties[19]);
   }

   @NotNull
   public final StopWatch getStopWatch() {
      return this.stopWatch;
   }

   @Flow
   public final void onReceivePacket(ReceivePacket e) {
      if (e.getPacket() instanceof ClientboundOpenScreenPacket && !this.stopWatch.testValue(250)) {
         Packet serverboundContainerClosePacket = (Packet)(new ServerboundContainerClosePacket(((ClientboundOpenScreenPacket)e.getPacket()).getContainerId()));
         PacketUtil.applySend(serverboundContainerClosePacket);
         e.cancel();
      }
   }

   public final boolean checkAsItem(List positions, boolean flag, boolean flag2, List blocks, boolean flag3) {
      Iterable blocks2 = blocks;
      var arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(blocks2, 10));

      for (Object object : blocks2) {
         Block block = (Block)object;
         arrayList.add(block.asItem());
      }

      List list = arrayList;
      return this.checkIsEmpty(positions, flag, flag2, list, flag3);
   }

   public static boolean defaultImpl(PlaceModule placeModule, List items, boolean flag, boolean flag2, List items2, boolean flag3, int value, Object object) {
      if (object != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: place");
      } else {
         if ((value & 2) != 0) {
            flag = false;
         }

         if ((value & 4) != 0) {
            flag2 = false;
         }

         if ((value & 8) != 0) {
            items2 = placeModule.getItems3();
         }

         if ((value & 16) != 0) {
            flag3 = false;
         }

         return placeModule.checkAsItem(items, flag, flag2, items2, flag3);
      }
   }

   public final boolean checkIsEmpty(List positions, boolean flag, boolean flag2, List blocks, boolean flag3) {
      if (this.value2 > 0) {
         int value22 = this.value2;
         this.value2 = value22 + -1;
         return false;
      } else if (positions.isEmpty()) {
         return false;
      } else {
         int blocksPerAction2 = this.getBlocksPerAction();
         boolean flag4 = false;

         for (BlockPos position : (Iterable<BlockPos>)positions) {
            if (blocksPerAction2 <= 0) {
               break;
            }

            if (this.checkGetId(position, blocks, flag, flag2, flag3)) {
               flag4 = true;
               if (this.getTiming() != PlaceModule.Timing.Instant) {
                  break;
               }

               blocksPerAction2--;
               this.value2 = this.getDelayPerAction();
            }
         }

         return flag4;
      }
   }

   public static boolean defaultImpl2(PlaceModule placeModule, List items, boolean flag, boolean flag2, List items2, boolean flag3, int value, Object object) {
      if (object != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: placeItems");
      } else {
         if ((value & 2) != 0) {
            flag = false;
         }

         if ((value & 4) != 0) {
            flag2 = false;
         }

         if ((value & 8) != 0) {
            items2 = placeModule.getItems4();
         }

         if ((value & 16) != 0) {
            flag3 = false;
         }

         return placeModule.checkIsEmpty(items, flag, flag2, items2, flag3);
      }
   }

   private final boolean checkGetId(BlockPos blockPos, List items, boolean flag, boolean flag2, boolean flag3) {
      SearchInvResult inventory2 = flag3
         ? InventoryManager.INSTANCE.fetchInventory(arg -> arg.getItem() instanceof BlockItem && !items.contains(arg.getItem()))
         : InventoryManager.INSTANCE.computeSearchInvResult(items);
      if (!inventory2.isFlag()) {
         return false;
      } else if (this.isFlag()) {
         return false;
      } else {
         RotationAction defaultImpl = InteractionManager.defaultImpl(
            InteractionManager.INSTANCE,
            blockPos,
            inventory2.getValue(),
            flag2 ? PlaceModule.Interact.Vanilla : this.getInteract(),
            this.getPlaceMode(),
            this.getTargetFilter(),
            this.getRange(),
            this.getWallRange(),
            this.value,
            this.getSwitch(),
            null,
            this.getGrimSilent(),
            512,
            null
         );
         if (defaultImpl == null) {
            return false;
         } else {
            Entity entitiesOfClass = this.fetchEntitiesOfClass(blockPos);
            if (entitiesOfClass != null) {
               if (!this.testEntity(entitiesOfClass)) {
                  return false;
               }

               ServerboundAttackPacket serverboundAttackPacket = new ServerboundAttackPacket(entitiesOfClass.getId());
               PacketUtil.applySend((Packet)serverboundAttackPacket);
               MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
            }

            if (this.getRotate()) {
               if (this.getTiming() == PlaceModule.Timing.Instant) {
                  RotationManager.INSTANCE.updateRotationAction2(defaultImpl);
               } else {
                  RotationManager.INSTANCE.updateRotationAction(defaultImpl);
               }
            }

            if (flag || !this.getRotate()) {
               if (this.getRotate() && this.getGrimSilent() && this.getTiming() == PlaceModule.Timing.Instant) {
                  RotationManager.INSTANCE.applyGetX(defaultImpl.getRotation().getAmount(), defaultImpl.getRotation().getAmount2());
               }

               defaultImpl.getAction().invoke();
               if (this.getRotate() && this.getGrimSilent() && this.getTiming() == PlaceModule.Timing.Instant) {
                  RotationManager.INSTANCE.applyGetX(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot());
               }

               defaultImpl.updateCallback(() -> Unit.INSTANCE);
            }

            if (this.getRender() != PlaceModule.Render.NONE) {
               BoxRenderCommand instance = BoxRenderCommand.INSTANCE;
               Color color = this.getSyncColors() ? Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F) : this.getOutlineColor();
               Color color2 = this.getSyncColors() ? Render2D.INSTANCE.computeColor(ThemeManager.INSTANCE.getColor2(), 1.0F) : this.getFillColor();
               PlaceModule.Render render2 = this.getRender();
               PlaceModule.Animation renderAnimation = this.getRenderAnimation();
               Color color3 = color2;
               instance.applyPut(blockPos, color, color3, renderAnimation, render2);
            }

            AutoMend.INSTANCE.getStopWatch().update();
            this.stopWatch.update();
            return true;
         }
      }
   }

   public boolean testEntity(Entity crystal) {
      return !this.isFlag();
   }

   private final Entity fetchEntitiesOfClass(BlockPos blockPos) {
      Iterable<Entity> entitiesOfClass = MinecraftHolder.getClientLevel().getEntitiesOfClass(Entity.class, new AABB(blockPos));
      Iterator<Entity> iterator = entitiesOfClass.iterator();

      Entity object;
      while (true) {
         if (iterator.hasNext()) {
            Entity object2 = iterator.next();
            Entity entity = (Entity)object2;
            if (!(entity instanceof EndCrystal)) {
               continue;
            }

            object = object2;
            break;
         }

         object = null;
         break;
      }

      return (Entity)object;
   }

   public final boolean checkGetEntitiesOfClass(BlockPos bp) {
      return !MinecraftHolder.getClientLevel()
         .getEntitiesOfClass(LivingEntity.class, BoxShrinker.INSTANCE.getEnabled() ? new AABB(bp).inflate(-1.0E-12, 0.0, -1.0E-12) : new AABB(bp))
         .isEmpty();
   }

   @NotNull
   public List<Block> getItems3() {
      Block[] blocks = new Block[]{Blocks.OBSIDIAN, Blocks.ENDER_CHEST, Blocks.NETHERITE_BLOCK, Blocks.RESPAWN_ANCHOR};
      return CollectionsKt.listOf(blocks);
   }

   @NotNull
   public List getItems4() {
      return CollectionsKt.emptyList();
   }

   @NotNull
   public TargetFilter getTargetFilter() {
      return TargetFilter.ONLY_CRYSTALS;
   }

   public boolean isFlag() {
      if (MinecraftHolder.getMultiPlayerGameMode().isDestroying() && this.getMining()) {
         return true;
      } else if (Blink.INSTANCE.getEnabled() && this.getBlink()) {
         return true;
      } else if (MinecraftHolder.getLocalPlayer().isUsingItem() && this.getEating()) {
         return true;
      } else {
         return SpeedMine.INSTANCE.getAtomicBoolean().get() ? true : WorldUtil.INSTANCE.isFlag2() && this.getInventory();
      }
   }

   static {
   }

   public static enum Animation {
      Fade,
      Hover,
      Decrease,
      Static,
      Flash,
      Grow,
      FILL,
      TNT,
      Pull;

      private static final PlaceModule.Animation[] animations = $values();
      private static final EnumEntries<PlaceModule.Animation> enumEntries = EnumEntriesKt.enumEntries(animations);

      private Animation() {
      }

      @NotNull
      public static EnumEntries<PlaceModule.Animation> getEnumEntries() {
         return enumEntries;
      }

      private static final PlaceModule.Animation[] $values() {
         return new PlaceModule.Animation[]{Fade, Hover, Decrease, Static, Flash, Grow, FILL, TNT, Pull};
      }
   }

   public static enum Interact {
      Vanilla,
      Strict,
      AirPlace,
      GRIM;

      private static final PlaceModule.Interact[] interacts = $values();
      private static final EnumEntries<PlaceModule.Interact> enumEntries = EnumEntriesKt.enumEntries(interacts);

      private Interact() {
      }

      @NotNull
      public static EnumEntries<PlaceModule.Interact> getEnumEntries() {
         return enumEntries;
      }

      private static final PlaceModule.Interact[] $values() {
         return new PlaceModule.Interact[]{Vanilla, Strict, AirPlace, GRIM};
      }
   }

   public static enum Pause {
      DEFAULT,
      ALWAYS,
      BOOLS;

      private static final PlaceModule.Pause[] pauses = $values();
      private static final EnumEntries<PlaceModule.Pause> enumEntries = EnumEntriesKt.enumEntries(pauses);

      private Pause() {
      }

      @NotNull
      public static EnumEntries<PlaceModule.Pause> getEnumEntries() {
         return enumEntries;
      }

      private static final PlaceModule.Pause[] $values() {
         return new PlaceModule.Pause[]{DEFAULT, ALWAYS, BOOLS};
      }
   }

   public static enum PlaceMode {
      Packet,
      Normal;

      private static final PlaceModule.PlaceMode[] placeModes = $values();
      private static final EnumEntries<PlaceModule.PlaceMode> enumEntries = EnumEntriesKt.enumEntries(placeModes);

      private PlaceMode() {
      }

      @NotNull
      public static EnumEntries<PlaceModule.PlaceMode> getEnumEntries() {
         return enumEntries;
      }

      private static final PlaceModule.PlaceMode[] $values() {
         return new PlaceModule.PlaceMode[]{Packet, Normal};
      }
   }

   public static enum Render {
      SHADER,
      FILL,
      LINE,
      BOTH,
      NONE;

      private static final PlaceModule.Render[] renders = $values();
      private static final EnumEntries<PlaceModule.Render> enumEntries = EnumEntriesKt.enumEntries(renders);

      private Render() {
      }

      @NotNull
      public static EnumEntries<PlaceModule.Render> getEnumEntries() {
         return enumEntries;
      }

      private static final PlaceModule.Render[] $values() {
         return new PlaceModule.Render[]{SHADER, FILL, LINE, BOTH, NONE};
      }
   }

   public static enum Switch {
      Silent,
      Normal,
      Alternative;

      private static final PlaceModule.Switch[] switchValues = $values();
      private static final EnumEntries<PlaceModule.Switch> enumEntries = EnumEntriesKt.enumEntries(switchValues);

      private Switch() {
      }

      @NotNull
      public static EnumEntries<PlaceModule.Switch> getEnumEntries() {
         return enumEntries;
      }

      private static final PlaceModule.Switch[] $values() {
         return new PlaceModule.Switch[]{Silent, Normal, Alternative};
      }
   }

   public static enum Timing {
      Vanilla,
      Instant;

      private static final PlaceModule.Timing[] timings = $values();
      private static final EnumEntries<PlaceModule.Timing> enumEntries = EnumEntriesKt.enumEntries(timings);

      private Timing() {
      }

      @NotNull
      public static EnumEntries<PlaceModule.Timing> getEnumEntries() {
         return enumEntries;
      }

      private static final PlaceModule.Timing[] $values() {
         return new PlaceModule.Timing[]{Vanilla, Instant};
      }
   }
}