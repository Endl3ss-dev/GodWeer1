// deobf by pusia
package su.catlean.client.features.modules.protect;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.IntIterator;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import net.minecraft.client.gui.screens.inventory.ShulkerBoxScreen;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.LargeFireball;
import net.minecraft.world.entity.vehicle.minecart.MinecartTNT;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.category.SubCategory;
import su.catlean.api.data.SearchInvResult;
import su.catlean.api.event.events.network.PostTasksProcessEvent;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.ClickSlotEvent;
import su.catlean.api.event.events.player.PostSyncEvent;
import su.catlean.api.event.events.player.UpdateSelectedSlotEvent;
import su.catlean.api.event.events.render.HotBarRenderEventPost;
import su.catlean.api.event.events.render.HotBarRenderEventPre;
import su.catlean.api.event.events.render.Render2DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BindSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.settings.NotationSetting;
import su.catlean.api.setting.settings.StringSetting;
import su.catlean.api.setting.values.Bind;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.api.setting.values.NotationValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.PlaceModule;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.equip.ReFiller;
import su.catlean.client.features.modules.misc.Notifier;
import su.catlean.client.features.modules.player.NoInteract;
import su.catlean.client.features.modules.rage.ElytraTarget;
import su.catlean.client.features.modules.world.FreeCam;
import su.catlean.client.services.CrystalPredictor;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.ServerInfoService;
import su.catlean.client.services.ThreadManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.MinecraftAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.chat.ChatUtil;
import su.catlean.utils.input.InputUtil;
import su.catlean.utils.inventory.ContainerUtil;
import su.catlean.utils.inventory.ItemTypeUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.world.WorldUtil;

public final class Offhand extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Offhand.class, "delayBetweenSwaps", "getDelayBetweenSwaps()I", 0), new PropertyReference1Impl(Offhand.class, "totemHealth", "getTotemHealth()F", 0), new PropertyReference1Impl(Offhand.class, "totemElytraHealth", "getTotemElytraHealth()F", 0), new PropertyReference1Impl(Offhand.class, "outdatedSwap", "getOutdatedSwap()Z", 0), new PropertyReference1Impl(Offhand.class, "offhand", "getOffhand()Lsu/catlean/n2;", 0), new PropertyReference1Impl(Offhand.class, "bindSwap", "getBindSwap()Z", 0), new PropertyReference1Impl(Offhand.class, "swapButton", "getSwapButton()Lsu/catlean/dd;", 0), new PropertyReference1Impl(Offhand.class, "swapMode", "getSwapMode()Lsu/catlean/cu;", 0), new PropertyReference1Impl(Offhand.class, "swapGroup", "getSwapGroup()Lsu/catlean/nl;", 0), new MutablePropertyReference1Impl(Offhand.class, "notation", "getNotation()Lsu/catlean/g6;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap1", "getSwap1()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap2", "getSwap2()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap3", "getSwap3()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap4", "getSwap4()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap5", "getSwap5()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap6", "getSwap6()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap7", "getSwap7()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap8", "getSwap8()Ljava/lang/String;", 0), new MutablePropertyReference1Impl(Offhand.class, "swap9", "getSwap9()Ljava/lang/String;", 0), new PropertyReference1Impl(Offhand.class, "hotBarSwap", "getHotBarSwap()Z", 0), new PropertyReference1Impl(Offhand.class, "resetAttackCooldown", "getResetAttackCooldown()Z", 0), new PropertyReference1Impl(Offhand.class, "saveEnchanted", "getSaveEnchanted()Z", 0), new PropertyReference1Impl(Offhand.class, "rcGap", "getRcGap()Lsu/catlean/dc;", 0), new PropertyReference1Impl(Offhand.class, "gapplePriority", "getGapplePriority()Lsu/catlean/v6;", 0), new PropertyReference1Impl(Offhand.class, "crappleSpoof", "getCrappleSpoof()Z", 0), new PropertyReference1Impl(Offhand.class, "deathVerbose", "getDeathVerbose()Z", 0), new PropertyReference1Impl(Offhand.class, "containers", "getContainers()Z", 0), new PropertyReference1Impl(Offhand.class, "safetyFactors", "getSafetyFactors()Lsu/catlean/nl;", 0), new PropertyReference1Impl(Offhand.class, "onCrystalInHand", "getOnCrystalInHand()Z", 0), new PropertyReference1Impl(Offhand.class, "onFalling", "getOnFalling()Z", 0), new PropertyReference1Impl(Offhand.class, "onMace", "getOnMace()Z", 0), new PropertyReference1Impl(Offhand.class, "onExlposiveEntities", "getOnExlposiveEntities()Z", 0), new PropertyReference1Impl(Offhand.class, "onAnchors", "getOnAnchors()Z", 0) };
   @NotNull
   public static final Offhand INSTANCE = new Offhand();
   @NotNull
   private static final IntSetting delayBetweenSwaps = SettingFactory.defaultImpl(INSTANCE, "delay-between-swaps", 0, new IntRange(0, 10), null, null, 24, null);
   @NotNull
   private static final FloatSetting totemHp = SettingFactory.defaultImpl(INSTANCE, "totem-hp", 16.0F, RangesKt.rangeTo(0.0F, 36.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting totemElytraHp = SettingFactory.defaultImpl(
      INSTANCE, "totem-elytra-hp", 16.0F, RangesKt.rangeTo(0.0F, 36.0F), null, null, 24, null
   );
   @NotNull
   private static final BooleanSetting booleanSetting = SettingFactory.defaultImpl(INSTANCE, "pre-1.16.5-swap", false, null, null, 12, null);
   @NotNull
   private static final EnumSetting forceItem = SettingFactory.defaultImpl(INSTANCE, "force-item", Offhand.ForceItem.OFF, null, null, 12, null);
   @NotNull
   private static final BooleanSetting bindSwap = SettingFactory.defaultImpl(
      INSTANCE, "bind-swap", false, null, () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF, 4, null
   );
   @NotNull
   private static final BindSetting swapButton = SettingFactory.defaultImpl(
      INSTANCE,
      "swap-button",
      new Bind(0, false, false, 7, null),
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF && INSTANCE.getBindSwap(),
      4,
      null
   );
   @NotNull
   private static final EnumSetting swap = SettingFactory.defaultImpl(
      INSTANCE, "swap", Offhand.Swap.GAPPLE_SHIELD, null, () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF && INSTANCE.getBindSwap(), 4, null
   );
   @NotNull
   private static final GroupSetting swapItems = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE,
      "swap-items",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF && INSTANCE.getBindSwap() && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM,
      2,
      null
   );
   @NotNull
   private static final NotationSetting notation = SettingFactory.defaultImpl(
      INSTANCE,
      "notation",
      new NotationValue("Hold the bind and click items in inv to add them. Hold the bind and use the hotbar to swap items"),
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap1 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap1",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap2 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap2",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap3 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap3",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap4 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap4",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap5 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap5",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap6 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap6",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap7 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap7",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap8 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap8",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final StringSetting swap9 = SettingFactory.defaultImpl(
      INSTANCE,
      "swap9",
      "",
      null,
      () -> INSTANCE.getOffhand() == Offhand.ForceItem.OFF
         && INSTANCE.getBindSwap()
         && INSTANCE.getSwapMode() == Offhand.Swap.CUSTOM
         && INSTANCE.getSwapGroup().isFlag(),
      4,
      null
   );
   @NotNull
   private static final BooleanSetting hotBarSwap = SettingFactory.defaultImpl(INSTANCE, "hot-bar-swap", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting resetAttackCooldown = SettingFactory.defaultImpl(
      INSTANCE, "reset-attack-cooldown", false, null, () -> INSTANCE.getHotBarSwap(), 4, null
   );
   @NotNull
   private static final BooleanSetting saveEnchanted = SettingFactory.defaultImpl(INSTANCE, "save-enchanted", false, null, null, 12, null);
   @NotNull
   private static final EnumSetting rightClickGapple = SettingFactory.defaultImpl(
      INSTANCE, "right-click-gapple", Offhand.RightClickGapple.OFF, null, null, 12, null
   );
   @NotNull
   private static final EnumSetting gapplePriority = SettingFactory.defaultImpl(
      INSTANCE, "gapple-priority", Offhand.GapplePriority.ENCHANTED, null, () -> INSTANCE.getRcGap() != Offhand.RightClickGapple.OFF, 4, null
   );
   @NotNull
   private static final BooleanSetting crappleSpoof = SettingFactory.defaultImpl(
      INSTANCE, "crapple-spoof", true, null, () -> INSTANCE.getOffhand() == Offhand.ForceItem.GAPPLE, 4, null
   );
   @NotNull
   private static final BooleanSetting deathVerbose = SettingFactory.defaultImpl(INSTANCE, "death-verbose", true, null, null, 12, null);
   @NotNull
   private static final BooleanSetting containers = SettingFactory.defaultImpl(INSTANCE, "containers", true, null, null, 12, null);
   @NotNull
   private static final GroupSetting safetyFactors = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "safety-factors", PlaceModule.Pause.BOOLS, null, 4, null);
   @NotNull
   private static final BooleanSetting onCrystalInHand = SettingFactory.defaultImpl(
      INSTANCE, "on-crystal-in-hand", false, INSTANCE.getSafetyFactors(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting onFalling = SettingFactory.defaultImpl(INSTANCE, "on-falling", true, INSTANCE.getSafetyFactors(), null, 8, null);
   @NotNull
   private static final BooleanSetting onMace = SettingFactory.defaultImpl(INSTANCE, "on-mace", true, INSTANCE.getSafetyFactors(), null, 8, null);
   @NotNull
   private static final BooleanSetting onExplosiveEntities = SettingFactory.defaultImpl(
      INSTANCE, "on-explosive-entities", true, INSTANCE.getSafetyFactors(), null, 8, null
   );
   @NotNull
   private static final BooleanSetting onAnchors = SettingFactory.defaultImpl(INSTANCE, "on-anchors", true, INSTANCE.getSafetyFactors(), null, 8, null);
   @NotNull
   private static final Identifier identifier = Identifier.withDefaultNamespace("hud/hotbar_selection");
   @NotNull
   private static final Identifier identifier2 = Identifier.withDefaultNamespace("hud/hotbar_offhand_left");
   @NotNull
   private static ItemStack itemStack = ItemStack.EMPTY;
   private static int value;
   private static int value2;
   @NotNull
   private static AtomicBoolean atomicBoolean = new AtomicBoolean(false);
   private static int value3;
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @Nullable
   private static ItemStack itemStack2;
   private static long time = 1000000L;
   private static float amount;
   private static float amount2;
   private static float amount3;

   private Offhand() {
      SubCategory var10002 = SubCategories.getSubCategory();
      String[] var3 = new String[]{"auto-totem", "autototem"};
      super("offhand", var10002, CollectionsKt.listOf(var3));
   }

   private final int getDelayBetweenSwaps() {
      return ((Number)delayBetweenSwaps.computeObject(this, $$delegatedProperties[0])).intValue();
   }

   private final float getTotemHealth() {
      return ((Number)totemHp.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final float getTotemElytraHealth() {
      return ((Number)totemElytraHp.computeObject(this, $$delegatedProperties[2])).floatValue();
   }

   private final boolean getOutdatedSwap() {
      return (Boolean)booleanSetting.computeObject(this, $$delegatedProperties[3]);
   }

   private final Offhand.ForceItem getOffhand() {
      return (Offhand.ForceItem)forceItem.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getBindSwap() {
      return (Boolean)bindSwap.computeObject(this, $$delegatedProperties[5]);
   }

   private final Bind getSwapButton() {
      return (Bind)swapButton.computeObject(this, $$delegatedProperties[6]);
   }

   private final Offhand.Swap getSwapMode() {
      return (Offhand.Swap)swap.computeObject(this, $$delegatedProperties[7]);
   }

   private final GroupValue getSwapGroup() {
      return (GroupValue)swapItems.computeObject(this, $$delegatedProperties[8]);
   }

   private final String getSwap1() {
      return (String)swap1.computeObject(this, $$delegatedProperties[10]);
   }

   private final void updateText(String text) {
      swap1.updateObject(this, $$delegatedProperties[10], text);
   }

   private final String getSwap2() {
      return (String)swap2.computeObject(this, $$delegatedProperties[11]);
   }

   private final void updateText2(String text) {
      swap2.updateObject(this, $$delegatedProperties[11], text);
   }

   private final String getSwap3() {
      return (String)swap3.computeObject(this, $$delegatedProperties[12]);
   }

   private final void updateText3(String text) {
      swap3.updateObject(this, $$delegatedProperties[12], text);
   }

   private final String getSwap4() {
      return (String)swap4.computeObject(this, $$delegatedProperties[13]);
   }

   private final void updateText4(String text) {
      swap4.updateObject(this, $$delegatedProperties[13], text);
   }

   private final String getSwap5() {
      return (String)swap5.computeObject(this, $$delegatedProperties[14]);
   }

   private final void updateText5(String text) {
      swap5.updateObject(this, $$delegatedProperties[14], text);
   }

   private final String getSwap6() {
      return (String)swap6.computeObject(this, $$delegatedProperties[15]);
   }

   private final void updateText6(String text) {
      swap6.updateObject(this, $$delegatedProperties[15], text);
   }

   private final String getSwap7() {
      return (String)swap7.computeObject(this, $$delegatedProperties[16]);
   }

   private final void updateText7(String text) {
      swap7.updateObject(this, $$delegatedProperties[16], text);
   }

   private final String getSwap8() {
      return (String)swap8.computeObject(this, $$delegatedProperties[17]);
   }

   private final void updateText8(String text) {
      swap8.updateObject(this, $$delegatedProperties[17], text);
   }

   private final String getSwap9() {
      return (String)swap9.computeObject(this, $$delegatedProperties[18]);
   }

   private final void updateText9(String text) {
      swap9.updateObject(this, $$delegatedProperties[18], text);
   }

   private final boolean getHotBarSwap() {
      return (Boolean)hotBarSwap.computeObject(this, $$delegatedProperties[19]);
   }

   private final boolean getResetAttackCooldown() {
      return (Boolean)resetAttackCooldown.computeObject(this, $$delegatedProperties[20]);
   }

   private final boolean getSaveEnchanted() {
      return (Boolean)saveEnchanted.computeObject(this, $$delegatedProperties[21]);
   }

   private final Offhand.RightClickGapple getRcGap() {
      return (Offhand.RightClickGapple)rightClickGapple.computeObject(this, $$delegatedProperties[22]);
   }

   private final Offhand.GapplePriority getGapplePriority() {
      return (Offhand.GapplePriority)gapplePriority.computeObject(this, $$delegatedProperties[23]);
   }

   private final boolean getCrappleSpoof() {
      return (Boolean)crappleSpoof.computeObject(this, $$delegatedProperties[24]);
   }

   private final boolean getDeathVerbose() {
      return (Boolean)deathVerbose.computeObject(this, $$delegatedProperties[25]);
   }

   private final boolean getContainers() {
      return (Boolean)containers.computeObject(this, $$delegatedProperties[26]);
   }

   private final GroupValue getSafetyFactors() {
      return (GroupValue)safetyFactors.computeObject(this, $$delegatedProperties[27]);
   }

   private final boolean getOnCrystalInHand() {
      return (Boolean)onCrystalInHand.computeObject(this, $$delegatedProperties[28]);
   }

   private final boolean getOnFalling() {
      return (Boolean)onFalling.computeObject(this, $$delegatedProperties[29]);
   }

   private final boolean getOnMace() {
      return (Boolean)onMace.computeObject(this, $$delegatedProperties[30]);
   }

   private final boolean getOnExlposiveEntities() {
      return (Boolean)onExplosiveEntities.computeObject(this, $$delegatedProperties[31]);
   }

   private final boolean getOnAnchors() {
      return (Boolean)onAnchors.computeObject(this, $$delegatedProperties[32]);
   }

   public final int getValue3() {
      return value3;
   }

   public final void setValue3(int value) {
      value3 = value;
   }

   @Flow
   private final void onRender2D(Render2DEvent render2DEvent) {
      if (this.getBindSwap() && this.getSwapMode() == Offhand.Swap.CUSTOM) {
         int var11 = render2DEvent.getContext().guiHeight() - 23;
         int var12 = render2DEvent.getContext().guiHeight() - 16 - 3;
         int var13 = render2DEvent.getContext().guiWidth() / 2 - 91 - 29;
         byte var14 = 21;
         List<ItemStack> var15 = this.getItems();
         if (!var15.isEmpty()) {
            Iterable<ItemStack> var16 = var15;
            int var18 = 0;

            for (Object var20 : var16) {
               int var21 = var18++;
               if (var21 < 0) {
                  CollectionsKt.throwIndexOverflow();
               }

               ItemStack var22 = (ItemStack)var20;
               int var25 = var13 - var21 * var14;
               render2DEvent.getContext().blitSprite(RenderPipelines.GUI_TEXTURED, identifier2, var25, var11, 29, 24);
               if (!var22.isEmpty()) {
                  render2DEvent.getContext().item(var22, var25 + 3, var12);
               }
            }

            if (value == var15.size()) {
               int var26 = value;
               value = var26 + -1;
            }

            int var27 = var13 - (var15.size() - value - 1) * var14;
            render2DEvent.getContext().blitSprite(RenderPipelines.GUI_TEXTURED, identifier, var27 - 1, var11, 24, 23);
         }
      }
   }

   @Flow
   private final void onPostTasksProcess(PostTasksProcessEvent postTasksProcessEvent) {
      if (this.isFlag6()) {
         float var13 = MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount();
         if (var13 < amount3) {
            amount2 = amount;
            amount = amount3 - var13;
         }

         amount3 = var13;
         this.applyScreen(this.getValue());
      }
   }

   @Flow
   private final void onPostSync(PostSyncEvent postSyncEvent) {
      if (this.isFlag() && !MinecraftHolder.getLocalPlayer().isUsingItem()) {
         ((MinecraftAccessor)MinecraftHolder.getMinecraft()).idoItemUse();
      }

      int var11 = value3;
      value3 = var11 + -1;
   }

   @Flow
   private final void onReceivePacket(ReceivePacket receivePacket) {
      if (receivePacket.getPacket() instanceof ClientboundEntityEventPacket
         && MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().level != null
         && this.getDeathVerbose()
         && (
            ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEventId() == 3
               || ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEventId() == 60
         )
         && Intrinsics.areEqual(
            ((ClientboundEntityEventPacket)receivePacket.getPacket()).getEntity((Level)MinecraftHolder.getClientLevel()), MinecraftHolder.getLocalPlayer()
         )) {
         this.applyAbs();
      }
   }

   @Flow
   private final void onUpdateSelectedSlot(UpdateSelectedSlotEvent updateSelectedSlotEvent) {
      if (this.isFlag6() && !atomicBoolean.get()) {
         int var20 = MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot();
         if (this.getSwapMode() == Offhand.Swap.CUSTOM && InputUtil.checkContains(this.getSwapButton().getValue())) {
            List<ItemStack> var21 = this.getItems();
            if (var21.isEmpty()) {
               return;
            }

            Iterable var23 = (Iterable)(new IntRange(0, 8));
            int var24 = 0;
            boolean var10000;
            if (var23 instanceof Collection && ((Collection)var23).isEmpty()) {
               var10000 = false;
            } else {
               Iterator var25 = var23.iterator();

               while (true) {
                  if (!var25.hasNext()) {
                     var10000 = false;
                     break;
                  }

                  int var26 = ((IntIterator)var25).nextInt();
                  if (MinecraftHolder.getMinecraft().options.keyHotbarSlots[var26].consumeClick()
                     || MinecraftHolder.getMinecraft().options.keyHotbarSlots[var26].isDown()) {
                     var10000 = true;
                     break;
                  }
               }
            }

            boolean var22 = var10000;
            if (var22) {
               value = updateSelectedSlotEvent.getSlot() % var21.size();
            } else {
               var24 = updateSelectedSlotEvent.getSlot();
               int var28 = var24 == (var20 + 1) % 9 ? 1 : (var24 == (var20 + 8) % 9 ? -1 : 0);
               value = var28 != 0 ? this.computeValue(value + var28, var21.size()) : this.computeValue(updateSelectedSlotEvent.getSlot(), var21.size());
            }

            updateSelectedSlotEvent.cancel();
         }
      }
   }

   @Flow
   private final void onClickSlot(ClickSlotEvent clickSlotEvent) {
      if (this.getSwapMode() == Offhand.Swap.CUSTOM
         && InputUtil.checkContains(this.getSwapButton().getValue())
         && MinecraftHolder.getMinecraft().gui.screen() != null
         && clickSlotEvent.getAction() == ContainerInput.PICKUP) {
         int var58 = clickSlotEvent.getId();
         if (0 <= var58 ? var58 < 45 : false) {
            String var65 = MinecraftHolder.getLocalPlayer()
               .getInventory()
               .getItem(clickSlotEvent.getId() > 35 ? clickSlotEvent.getId() - 36 : clickSlotEvent.getId())
               .getHoverName()
               .getString();
            if (Intrinsics.areEqual(var65, "Air")) {
               return;
            }

            String[] var60 = new String[]{
               this.getSwap1(),
               this.getSwap2(),
               this.getSwap3(),
               this.getSwap4(),
               this.getSwap5(),
               this.getSwap6(),
               this.getSwap7(),
               this.getSwap8(),
               this.getSwap9()
            };
            var var66 = CollectionsKt.listOf(var60);
            boolean var10000;
            if (var66 instanceof Collection && ((Collection)var66).isEmpty()) {
               var10000 = false;
            } else {
               Iterator<String> var62 = var66.iterator();

               while (true) {
                  if (!var62.hasNext()) {
                     var10000 = false;
                     break;
                  }

                  String var63 = var62.next();
                  String var64 = (String)var63;
                  if (Intrinsics.areEqual(var64, var65)) {
                     var10000 = true;
                     break;
                  }
               }
            }

            boolean var59 = var10000;
            if (Intrinsics.areEqual(this.getSwap1(), var65)) {
               this.updateText("");
            }

            if (Intrinsics.areEqual(this.getSwap2(), var65)) {
               this.updateText2("");
            }

            if (Intrinsics.areEqual(this.getSwap3(), var65)) {
               this.updateText3("");
            }

            if (Intrinsics.areEqual(this.getSwap4(), var65)) {
               this.updateText4("");
            }

            if (Intrinsics.areEqual(this.getSwap5(), var65)) {
               this.updateText5("");
            }

            if (Intrinsics.areEqual(this.getSwap6(), var65)) {
               this.updateText6("");
            }

            if (Intrinsics.areEqual(this.getSwap7(), var65)) {
               this.updateText7("");
            }

            if (Intrinsics.areEqual(this.getSwap8(), var65)) {
               this.updateText8("");
            }

            if (Intrinsics.areEqual(this.getSwap8(), var65)) {
               this.updateText8("");
            }

            if (var59) {
               clickSlotEvent.cancel();
               return;
            }

            switch (value2) {
               case 0:
                  this.updateText(var65);
                  break;
               case 1:
                  this.updateText2(var65);
                  break;
               case 2:
                  this.updateText3(var65);
                  break;
               case 3:
                  this.updateText4(var65);
                  break;
               case 4:
                  this.updateText5(var65);
                  break;
               case 5:
                  this.updateText6(var65);
                  break;
               case 6:
                  this.updateText7(var65);
                  break;
               case 7:
                  this.updateText8(var65);
                  break;
               case 8:
                  this.updateText9(var65);
            }

            int var67 = value2++;
            value2 %= 9;
            clickSlotEvent.cancel();
         }
      }
   }

   @Flow
   private final void onHotBarRenderPre(HotBarRenderEventPre hotBarRenderEventPre) {
      if (MinecraftHolder.getMinecraft().getCameraEntity() != null && this.getSwapMode() == Offhand.Swap.CUSTOM && this.getBindSwap()) {
         itemStack = MinecraftHolder.getLocalPlayer().getOffhandItem();
         MinecraftHolder.getLocalPlayer().setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
      }
   }

   @Flow
   private final void onHotBarRenderPost(HotBarRenderEventPost hotBarRenderEventPost) {
      if (MinecraftHolder.getMinecraft().getCameraEntity() != null && this.getSwapMode() == Offhand.Swap.CUSTOM && this.getBindSwap()) {
         MinecraftHolder.getLocalPlayer().setItemInHand(InteractionHand.OFF_HAND, itemStack);
      }
   }

   private final void applyAbs() {
      int var19 = ServerInfoService.defaultImpl(ServerInfoService.INSTANCE, null, 1, null);
      float var20 = ServerInfoService.INSTANCE.getAmount2();
      float var21 = ServerInfoService.INSTANCE.getAmount();
      long var22 = MinecraftHolder.getTime() - time;
      Object var14 = null;
      byte var15 = 2;
      boolean var16 = false;
      Item var17 = Items.TOTEM_OF_UNDYING;
      int var24 = InventoryManager.defaultImpl(InventoryManager.INSTANCE, var17, var16, var15, var14);
      boolean var25 = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.TOTEM_OF_UNDYING);
      boolean var26 = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getMainHandItem().getItem(), Items.TOTEM_OF_UNDYING);
      boolean var27 = Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem(), Items.TOTEM_OF_UNDYING);
      String var28 = "";
      if (var26) {
         var28 = var28 + "mainhand ";
      }

      if (var27) {
         var28 = var28 + "offhand ";
      }

      if (var25) {
         var28 = var28 + "cursor ";
      }

      boolean var29 = false;
      boolean var30 = var19 > 400 || var20 < 5.0F || Math.abs(var21 - var20) > 5.0F || var22 < 450L;
      if (var24 > 0 || var25 || var27 || var26) {
         var29 = true;
      }

      String var31 = ChatFormatting.GREEN.toString();
      String var32 = ChatFormatting.RED.toString();
      String var33 = ChatFormatting.GRAY.toString();
      boolean var34 = var29 && amount > amount2 && var22 < 500L;
      Module var10000 = this;
      String var10003 = var29 ? var32 : var31;
      String var10005 = var29 ? " (" + var24 + " totems left)" : "";
      String var65;
      if (var29) {
         var65 = var28.length() == 0 ? "inventory" : "";
         String var35 = StringsKt.trim(var28).toString();
         String var36 = var65;
         var65 = "\n totem locations = (" + var36 + var35 + ")";
      } else {
         var65 = "";
      }

      float var10009 = amount;
      float var10011 = amount2;
      String var10016 = var19 > 400 ? var32 + "BAD" : var31 + "OK";
      String var10019 = var20 < 5.0F ? var32 + "BAD" : var31 + "OK";
      String var10022 = var22 < 450L ? var32 + "BAD" : var31 + "OK";
      float var10024 = Math.abs(var21 - var20);
      String var10025 = Math.abs(var21 - var20) > 5.0F ? var32 + "BAD" : var31 + "OK";
      String var37 = var29
         ? (
            !var30 && !(this.getAmount() >= 36.0F)
               ? var32 + "OffHand does not have time to take the totem increase minimum health"
               : var31 + "Unstable internet connection / Server lag"
         )
         : var31 + "No totems + skill issue";
      String var39 = var10025;
      float var40 = var10024;
      String var42 = var10022;
      String var46 = var10019;
      String var49 = var10016;
      float var54 = var10011;
      float var56 = var10009;
      String var58 = var65;
      String var60 = var10005;
      String var62 = var10003;
      ChatUtil.defaultImpl(
         var10000,
         "["
            + var32
            + "Death Analysis"
            + var33
            + "]\n failed ="
            + var62
            + " "
            + var29
            + var60
            + var33
            + var58
            + var33
            + "\n damage = "
            + var56
            + var33
            + "\n prevDamage = "
            + var54
            + var33
            + "\n double pop = "
            + var34
            + " "
            + var33
            + "\n ping = "
            + var19
            + " | "
            + var49
            + var33
            + "\n tps = "
            + var20
            + " | "
            + var46
            + var33
            + "\n lastPop = "
            + var22
            + " | "
            + var42
            + var33
            + "\n tpsSpike = "
            + var40
            + " | "
            + var39
            + var33
            + "\n\n Conclusion: "
            + var37,
         false,
         2,
         null
      );
   }

   private final void applyScreen(int value) {
      if (this.isFlag6()) {
         int var38 = value;
         boolean var39 = MinecraftHolder.getMinecraft().gui.screen() instanceof ShulkerBoxScreen
            || MinecraftHolder.getMinecraft().gui.screen() instanceof ContainerScreen;
         if (this.getContainers() || !var39) {
            if (value != -1 && value3 <= 0) {
               value3 = this.getDelayBetweenSwaps();
               if (value < 9 && this.getHotBarSwap()) {
                  atomicBoolean.set(true);
                  InventoryManager.INSTANCE.updateValue(value);
                  Packet var40 = (Packet)(new ServerboundPlayerActionPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ZERO, Direction.DOWN));
                  PacketUtil.applySend(var40);
                  InventoryManager.INSTANCE.update();
                  atomicBoolean.set(false);
                  ReFiller.INSTANCE.setValue(3);
                  ReFiller.INSTANCE.setValue2(value);
                  if (this.getResetAttackCooldown()) {
                     MinecraftHolder.getLocalPlayer().resetOnlyAttackStrengthTicker();
                  }

                  return;
               }

               if (value < 9) {
                  var38 = value + 36;
               }

               if (!MinecraftHolder.getLocalPlayer().containerMenu.getCarried().isEmpty()) {
                  ContainerUtil.defaultImpl(-999, 0, null, false, 14, null);
               }

               if (this.getOutdatedSwap()) {
                  InventoryManager.INSTANCE.applyScreen(var38, 45);
               } else {
                  boolean var33 = !var39;
                  ContainerInput var34 = ContainerInput.SWAP;
                  byte var35 = 40;
                  ContainerUtil.applyHandleContainerInput(var38, var35, var34, var33);
               }
            }
         }
      }
   }
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private final int getValue() {
      if (!this.isFlag6()) {
         return -1;
      } else {
         Item[] var46 = new Item[]{Items.ENCHANTED_GOLDEN_APPLE};
         InventoryManager var10000 = InventoryManager.INSTANCE;
         SearchInvResult var45 = var10000.computeSearchInvResult2(var46);
         Item[] var47 = new Item[]{Items.GOLDEN_APPLE};
         SearchInvResult var58 = var10000.computeSearchInvResult2(var47);
         Item[] var48 = new Item[]{Items.TOTEM_OF_UNDYING};
         SearchInvResult var59 = var10000.computeSearchInvResult2(var48);
         SearchInvResult var60 = InventoryManager.INSTANCE
            .fetchInventory2(var0 -> Intrinsics.areEqual(var0.getItem(), Items.TOTEM_OF_UNDYING) && !var0.isEnchanted());
         ItemStack var49 = MinecraftHolder.getLocalPlayer().getOffhandItem();
         Item var50 = var49.getItem();
         boolean var51 = Intrinsics.areEqual(var50, Items.GOLDEN_APPLE);
         boolean var52 = Intrinsics.areEqual(var50, Items.ENCHANTED_GOLDEN_APPLE);
         int var53 = -1;
         Offhand.RequiredItem var54 = null;

         var54 = switch (Offhand.WhenMappings.values[this.getOffhand().ordinal()]) {
            case 1 -> this.computeRequiredItem(var50, var51);
            case 2 -> new Offhand.RequiredItem(Items.END_CRYSTAL, false, false, null, 14, null);
            case 3 -> {
               if (this.getCrappleSpoof()) {
                  if (MinecraftHolder.getLocalPlayer().hasEffect(MobEffects.ABSORPTION)
                     && MinecraftHolder.getLocalPlayer().getEffect(MobEffects.ABSORPTION).getAmplifier() > 2) {
                     if (var58.isFlag() || var51) {
                        yield new Offhand.RequiredItem(Items.GOLDEN_APPLE, false, false, null, 14, null);
                     } else if (var45.isFlag() || var52) {
                        yield new Offhand.RequiredItem(Items.ENCHANTED_GOLDEN_APPLE, false, false, null, 14, null);
                     }
                  } else if (var45.isFlag() || var52) {
                     yield new Offhand.RequiredItem(Items.ENCHANTED_GOLDEN_APPLE, false, false, null, 14, null);
                  } else if (var58.isFlag() || var51) {
                     yield new Offhand.RequiredItem(Items.GOLDEN_APPLE, false, false, null, 14, null);
                  }
               } else if (var58.isFlag() || var51) {
                  yield new Offhand.RequiredItem(Items.GOLDEN_APPLE, false, false, null, 14, null);
               } else if (var45.isFlag() || var52) {
                  yield new Offhand.RequiredItem(Items.ENCHANTED_GOLDEN_APPLE, false, false, null, 14, null);
               }
               yield null;
            }
            default -> throw new NoWhenBranchMatchedException();
         };

         if (this.getAmount2() <= this.getAmount() && (var59.isFlag() || Intrinsics.areEqual(var50, Items.TOTEM_OF_UNDYING))) {
            var54 = new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, false, false, null, 14, null);
         }

         if (!Intrinsics.areEqual(var54, Items.TOTEM_OF_UNDYING)) {
            var54 = this.fetchItem(var54);
         }

         if (this.isFlag()
            && (
               this.getRcGap() == Offhand.RightClickGapple.ALWAYS
                  || this.getRcGap() == Offhand.RightClickGapple.ONLY_SAFE && this.getAmount2() > this.getAmount()
            )) {
            if (this.getGapplePriority() == Offhand.GapplePriority.NORMAL) {
               if (var58.isFlag() || var51) {
                  var54 = new Offhand.RequiredItem(Items.GOLDEN_APPLE, false, false, null, 14, null);
               } else if (var45.isFlag() || var52) {
                  var54 = new Offhand.RequiredItem(Items.ENCHANTED_GOLDEN_APPLE, false, false, null, 14, null);
               }
            } else if (var45.isFlag() || var52) {
               var54 = new Offhand.RequiredItem(Items.ENCHANTED_GOLDEN_APPLE, false, false, null, 14, null);
            } else if (var58.isFlag() || var51) {
               var54 = new Offhand.RequiredItem(Items.GOLDEN_APPLE, false, false, null, 14, null);
            }
         }

         if (var54 != null
            && Intrinsics.areEqual(var54.getItem(), Items.TOTEM_OF_UNDYING)
            && !Intrinsics.areEqual(var50, Items.AIR)
            && (!Intrinsics.areEqual(var50, Items.TOTEM_OF_UNDYING) || var49.isEnchanted() && this.getSaveEnchanted())
            && (!Intrinsics.areEqual(var50, Items.FIREWORK_ROCKET) || !ElytraTarget.INSTANCE.getEnabled())) {
            itemStack2 = var49;
         }

         if (this.getOffhand() == Offhand.ForceItem.OFF
            && this.getBindSwap()
            && this.getSwapButton().getValue() != -1
            && InputUtil.checkContains(this.getSwapButton().getValue())) {
            itemStack2 = null;
         }

         if ((var54 == null || Intrinsics.areEqual(var54.getItem(), Items.AIR)) && itemStack2 != null) {
            ItemStack var61 = itemStack2;
            itemStack2 = null;
            int var62 = InventoryManager.INSTANCE.fetchInventory2(var1 -> Intrinsics.areEqual(var1, var61)).getValue();
            int var63 = InventoryManager.INSTANCE.fetchInventory2(var1 -> Intrinsics.areEqual(var1.getItem(), var61.getItem())).getValue();
            if (var62 == -1) {
               return var63 >= 36 ? var63 - 36 : var63;
            } else {
               return var62 >= 36 ? var62 - 36 : var62;
            }
         } else if (var54 == null || Intrinsics.areEqual(var54.getItem(), Items.AIR)) {
            return -1;
         } else if (Intrinsics.areEqual(var54.getName(), "")
            && Intrinsics.areEqual(var54.getItem(), Items.TOTEM_OF_UNDYING)
            && this.getSaveEnchanted()
            && Intrinsics.areEqual(var50, Items.TOTEM_OF_UNDYING)
            && var49.isEnchanted()
            && var60.isFlag()) {
            return var60.getValue() >= 36 ? var60.getValue() - 36 : var60.getValue();
         } else {
            for (int var55 = 44; 8 < var55; var55--) {
               if (var54.getName().length() == 0) {
                  if (Intrinsics.areEqual(var50, var54.getItem()) && !var54.isFlag()) {
                     return -1;
                  }
               } else if (Intrinsics.areEqual(var49.getHoverName().getString(), var54.getName())) {
                  return -1;
               }

               int var56 = var55 >= 36 ? var55 - 36 : var55;
               ItemStack var57 = MinecraftHolder.getLocalPlayer().getInventory().getItem(var56);
               if (var57.getItem().equals(var54.getItem())
                  && (var54.getName().length() == 0 || Intrinsics.areEqual(var54.getName(), var57.getHoverName().getString()))
                  && (!var54.isFlag2() || var57.isEnchanted())) {
                  var53 = var56;
                  break;
               }
            }

            return Intrinsics.areEqual(var54, MinecraftHolder.getLocalPlayer().getMainHandItem().getItem())
                  && MinecraftHolder.getMinecraft().options.keyUse.isDown()
               ? -1
               : var53;
         }
      }
   }

   private final Offhand.RequiredItem fetchItem(Offhand.RequiredItem requiredItem) {
      Offhand.RequiredItem var36 = requiredItem;
      if (this.getOnFalling()
         && this.getAmount2() - ((WorldUtil.INSTANCE.getAmount3() - 3.0F) / 2.0F + 3.5F) < 0.5
         && !MinecraftHolder.getLocalPlayer().isFallFlying()
         && !FreeCam.INSTANCE.getEnabled()) {
         var36 = new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, false, false, null, 14, null);
      }

      if (this.getOnCrystalInHand()) {
         for (AbstractClientPlayer var38 : (Iterable<AbstractClientPlayer>)ThreadManager.INSTANCE.getItems()) {
            if (!PlayerNameUtil.checkGetGameProfile((Player)var38)
               && !Intrinsics.areEqual(var38, MinecraftHolder.getLocalPlayer())
               && WorldUtil.INSTANCE.getVec3().distanceToSqr(this.computeVec3((Entity)var38)) < 36.0
               && (
                  Intrinsics.areEqual(var38.getMainHandItem().getItem(), Items.OBSIDIAN)
                     || Intrinsics.areEqual(var38.getMainHandItem().getItem(), Items.END_CRYSTAL)
                     || Intrinsics.areEqual(var38.getOffhandItem().getItem(), Items.OBSIDIAN)
                     || Intrinsics.areEqual(var38.getOffhandItem().getItem(), Items.END_CRYSTAL)
               )) {
               var36 = new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, false, false, null, 14, null);
            }
         }
      }

      if (this.getOnExlposiveEntities()) {
         for (Entity var46 : Lists.newArrayList(MinecraftHolder.getClientLevel().entitiesForRendering())) {
            if (var46 instanceof LargeFireball && this.computeVec3(var46).distanceTo(this.computeVec3((Entity)MinecraftHolder.getLocalPlayer())) <= 12.0) {
               var36 = new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, false, false, null, 14, null);
               break;
            }

            if (var46.isAlive()
               && !(WorldUtil.INSTANCE.getVec3().distanceToSqr(this.computeVec3(var46)) > 144.0)
               && (var46 instanceof EndCrystal || var46 instanceof PrimedTnt || var46 instanceof MinecartTNT || var46 instanceof Creeper)
               && this.getAmount2() - CrystalPredictor.defaultImpl(CrystalPredictor.INSTANCE, this.computeVec3(var46), null, 0, false, null, null, 62, null)
                  < this.getAmount()) {
               var36 = new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, false, false, null, 14, null);
               break;
            }
         }
      }

      if (this.getOnAnchors()) {
         for (int var44 = -6; var44 < 7; var44++) {
            for (int var47 = -6; var47 < 7; var47++) {
               for (int var39 = -6; var39 < 7; var39++) {
                  if (Intrinsics.areEqual(MinecraftHolder.getClientLevel().getBlockState(new BlockPos(var44, var47, var39)).getBlock(), Blocks.RESPAWN_ANCHOR)) {
                     var36 = new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, false, false, null, 14, null);
                     break;
                  }
               }
            }
         }
      }

      if (this.getOnMace()) {
         Iterable<AbstractClientPlayer> var48 = MinecraftHolder.getClientLevel().players();
         Iterator<AbstractClientPlayer> var40 = var48.iterator();

         AbstractClientPlayer var10000;
         while (true) {
            if (!var40.hasNext()) {
               var10000 = null;
               break;
            }

            AbstractClientPlayer var41 = var40.next();
            AbstractClientPlayer var42 = (AbstractClientPlayer)var41;
            if (INSTANCE.computeVec3((Entity)MinecraftHolder.getLocalPlayer()).distanceTo(INSTANCE.computeVec3((Entity)var42)) < 12.0
               && (var42.getDeltaMovement().y < -0.1F || var42.yo - var42.getY() > 0.5)
               && (Intrinsics.areEqual(var42.getMainHandItem().getItem(), Items.MACE) || Intrinsics.areEqual(var42.getOffhandItem().getItem(), Items.MACE))
               && !Intrinsics.areEqual(var42, MinecraftHolder.getLocalPlayer())
               && !PlayerNameUtil.checkGetGameProfile((Player)var42)) {
               var10000 = var41;
               break;
            }
         }

         AbstractClientPlayer var45 = (AbstractClientPlayer)var10000;
         if (var45 != null) {
            Notifier.INSTANCE.setFlag(true);
            Notifier.INSTANCE.getStopWatch6().update();
            var36 = new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, false, false, null, 14, null);
         } else {
            Notifier.INSTANCE.setFlag(false);
         }
      }

      return var36;
   }

   private final Offhand.RequiredItem computeRequiredItem(Item item, boolean flag) {
      Offhand.RequiredItem var21 = null;
      if (this.getBindSwap() && this.getSwapButton().getValue() != -1) {
         if (this.getSwapMode() == Offhand.Swap.CUSTOM) {
            List<ItemStack> var22 = this.getItems();
            if (!var22.isEmpty()) {
               int var23 = (var22.size() - value - 1) % var22.size();
               if (0 <= var23 ? var23 < var22.size() : false) {
                  ItemStack var24 = (ItemStack)var22.get(var23);
                  var21 = new Offhand.RequiredItem(var24.getItem(), true, false, var24.getHoverName().getString(), 4, null);
               }
            }
         } else if (InputUtil.checkContains(this.getSwapButton().getValue()) && stopWatch.testValue(250)) {
            stopWatch.update();

            var21 = switch (Offhand.WhenMappings.values2[this.getSwapMode().ordinal()]) {
               case 1 -> {
                  if (!MinecraftHolder.getLocalPlayer().getOffhandItem().isEmpty() && !Intrinsics.areEqual(item, Items.SHIELD)) {
                     yield new Offhand.RequiredItem(Items.SHIELD, false, false, null, 14, null);
                  }

                  yield new Offhand.RequiredItem(Items.PLAYER_HEAD, false, false, null, 14, null);
               }
               case 2 -> {
                  if (!MinecraftHolder.getLocalPlayer().getOffhandItem().isEmpty() && !flag) {
                     yield new Offhand.RequiredItem(Items.GOLDEN_APPLE, false, false, null, 14, null);
                  }

                  yield new Offhand.RequiredItem(Items.PLAYER_HEAD, false, false, null, 14, null);
               }
               case 3 -> {
                  if (!MinecraftHolder.getLocalPlayer().getOffhandItem().isEmpty() && !Intrinsics.areEqual(item, Items.SHIELD)) {
                     yield new Offhand.RequiredItem(Items.SHIELD, false, false, null, 14, null);
                  }

                  yield new Offhand.RequiredItem(Items.GOLDEN_APPLE, false, false, null, 14, null);
               }
               case 4 -> {
                  if (!MinecraftHolder.getLocalPlayer().getOffhandItem().isEmpty() && !Intrinsics.areEqual(item, Items.TOTEM_OF_UNDYING)) {
                     yield new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, false, false, null, 14, null);
                  }

                  yield new Offhand.RequiredItem(Items.PLAYER_HEAD, false, false, null, 14, null);
               }
               case 5 -> new Offhand.RequiredItem(Items.TOTEM_OF_UNDYING, true, true, null, 8, null);
               default -> new Offhand.RequiredItem(Items.PLAYER_HEAD, true, false, null, 12, null);
            };
            itemStack2 = var21.getItem().getDefaultInstance();
         }
      }

      return var21;
   }

   private final float getAmount() {
      return MinecraftHolder.getLocalPlayer().isFallFlying() && WorldUtil.INSTANCE.getValue() > 15 ? this.getTotemElytraHealth() : this.getTotemHealth();
   }

   private final boolean isFlag() {
      Block var16 = MinecraftHolder.getMinecraft().hitResult instanceof BlockHitResult
         ? MinecraftHolder.getClientLevel().getBlockState(((BlockHitResult)MinecraftHolder.getMinecraft().hitResult).getBlockPos()).getBlock()
         : Blocks.DIRT;
      boolean var18 = this.getRcGap() != Offhand.RightClickGapple.OFF
         && ItemTypeUtil.testItemStack(MinecraftHolder.getLocalPlayer().getMainHandItem())
         && MinecraftHolder.getMinecraft().options.keyUse.isDown()
         && !(MinecraftHolder.getLocalPlayer().getOffhandItem().getItem() instanceof ShieldItem);
      boolean var17 = !(var16 instanceof BaseEntityBlock) && !Intrinsics.areEqual(var16, Blocks.CRAFTING_TABLE);
      return var18 && (var17 || NoInteract.INSTANCE.getEnabled());
   }

   private final float getAmount2() {
      return MinecraftHolder.getLocalPlayer().getHealth() + MinecraftHolder.getLocalPlayer().getAbsorptionAmount();
   }

   private final int computeValue(int value, int value2) {
      return (value % value2 + value2) % value2;
   }

   private final List<ItemStack> getItems() {
      List<ItemStack> var24 = new ArrayList();
      String[] var25 = new String[]{
         this.getSwap1(),
         this.getSwap2(),
         this.getSwap3(),
         this.getSwap4(),
         this.getSwap5(),
         this.getSwap6(),
         this.getSwap7(),
         this.getSwap8(),
         this.getSwap9()
      };

      for (Object var28 : CollectionsKt.listOf(var25)) {
         String var29 = (String)var28;
         if (var29.length() > 0 && !Intrinsics.areEqual(var29, "Air")) {
            if (Intrinsics.areEqual(MinecraftHolder.getLocalPlayer().getOffhandItem().getHoverName().getString(), var29)) {
               var24.add(MinecraftHolder.getLocalPlayer().getOffhandItem());
            } else {
               for (int var31 = 0; var31 < 36; var31++) {
                  ItemStack var32 = MinecraftHolder.getLocalPlayer().getInventory().getItem(var31);
                  if (Intrinsics.areEqual(var32.getHoverName().getString(), var29)) {
                     var24.add(var32);
                     break;
                  }
               }
            }
         }
      }

      return var24;
   }

   static {
   }

   public static enum ForceItem {
      OFF,
      CRYSTAL,
      GAPPLE;

      private static final Offhand.ForceItem[] forceItems = $values();
      private static final EnumEntries<Offhand.ForceItem> enumEntries = EnumEntriesKt.enumEntries(forceItems);

      private ForceItem() {
      }

      @NotNull
      public static EnumEntries<Offhand.ForceItem> getEnumEntries() {
         return enumEntries;
      }

      private static final Offhand.ForceItem[] $values() {
         return new Offhand.ForceItem[]{OFF, CRYSTAL, GAPPLE};
      }
   }

   public static enum GapplePriority {
      NORMAL,
      ENCHANTED;

      private static final Offhand.GapplePriority[] gapplePrioritys = $values();
      private static final EnumEntries<Offhand.GapplePriority> enumEntries = EnumEntriesKt.enumEntries(gapplePrioritys);

      private GapplePriority() {
      }

      @NotNull
      public static EnumEntries<Offhand.GapplePriority> getEnumEntries() {
         return enumEntries;
      }

      private static final Offhand.GapplePriority[] $values() {
         return new Offhand.GapplePriority[]{NORMAL, ENCHANTED};
      }
   }

   public static final class RequiredItem {
      @NotNull
      private final Item item;
      private final boolean flag;
      private final boolean flag2;
      @NotNull
      private final String name;

      public RequiredItem(Item item, boolean flag, boolean flag2, String name) {
         this.item = item;
         this.flag = flag;
         this.flag2 = flag2;
         this.name = name;
      }

      public RequiredItem(Item item, boolean flag, boolean flag2, String text, int value, DefaultConstructorMarker defaultConstructorMarker) {
         if ((value & 2) != 0) {
            flag = false;
         }

         if ((value & 4) != 0) {
            flag2 = false;
         }

         if ((value & 8) != 0) {
            text = "";
         }

         this(item, flag, flag2, text);
      }

      @NotNull
      public final Item getItem() {
         return this.item;
      }

      public final boolean isFlag() {
         return this.flag;
      }

      public final boolean isFlag2() {
         return this.flag2;
      }

      @NotNull
      public final String getName() {
         return this.name;
      }

      @NotNull
      public final Item getItem2() {
         return this.item;
      }

      public final boolean isFlag3() {
         return this.flag;
      }

      public final boolean isFlag4() {
         return this.flag2;
      }

      @NotNull
      public final String getName2() {
         return this.name;
      }

      @NotNull
      @Override
      public String toString() {
         String var3 = this.name;
         boolean var4 = this.flag2;
         boolean var5 = this.flag;
         Item var6 = this.item;
         return "RequiredItem(item=" + var6 + ", another=" + var5 + ", enchanted=" + var4 + ", name=" + var3 + ")";
      }

      @Override
      public int hashCode() {
         int var5 = this.item.hashCode();
         var5 = var5 * 31 + Boolean.hashCode(this.flag);
         var5 = var5 * 31 + Boolean.hashCode(this.flag2);
         return var5 * 31 + this.name.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (!(object instanceof Offhand.RequiredItem var5)) {
            return false;
         } else if (!Intrinsics.areEqual(this.item, var5.item)) {
            return false;
         } else if (this.flag != var5.flag) {
            return false;
         } else {
            return this.flag2 != var5.flag2 ? false : Intrinsics.areEqual(this.name, var5.name);
         }
      }
   }

   public static enum RightClickGapple {
      OFF,
      ALWAYS,
      ONLY_SAFE;

      private static final Offhand.RightClickGapple[] rightClickGapples = $values();
      private static final EnumEntries<Offhand.RightClickGapple> enumEntries = EnumEntriesKt.enumEntries(rightClickGapples);

      private RightClickGapple() {
      }

      @NotNull
      public static EnumEntries<Offhand.RightClickGapple> getEnumEntries() {
         return enumEntries;
      }

      private static final Offhand.RightClickGapple[] $values() {
         return new Offhand.RightClickGapple[]{OFF, ALWAYS, ONLY_SAFE};
      }
   }

   public static enum Swap {
      GAPPLE_SHIELD,
      BALL_SHIELD,
      GAPPLE_BALL,
      BALL_TOTEM,
      TOTEM_TOTEM,
      BALL_BALL,
      CUSTOM;

      private static final Offhand.Swap[] swaps = $values();
      private static final EnumEntries<Offhand.Swap> enumEntries = EnumEntriesKt.enumEntries(swaps);

      private Swap() {
      }

      @NotNull
      public static EnumEntries<Offhand.Swap> getEnumEntries() {
         return enumEntries;
      }

      private static final Offhand.Swap[] $values() {
         return new Offhand.Swap[]{GAPPLE_SHIELD, BALL_SHIELD, GAPPLE_BALL, BALL_TOTEM, TOTEM_TOTEM, BALL_BALL, CUSTOM};
      }
   }

   public static final class WhenMappings {
      public static final int[] values;
      public static final int[] values2;

      static {
         int[] var2 = new int[Offhand.ForceItem.values().length];

         try {
            var2[Offhand.ForceItem.OFF.ordinal()] = 1;
         } catch (NoSuchFieldError var11) {
         }

         try {
            var2[Offhand.ForceItem.CRYSTAL.ordinal()] = 2;
         } catch (NoSuchFieldError var10) {
         }

         try {
            var2[Offhand.ForceItem.GAPPLE.ordinal()] = 3;
         } catch (NoSuchFieldError var9) {
         }

         values = var2;
         var2 = new int[Offhand.Swap.values().length];

         try {
            var2[Offhand.Swap.BALL_SHIELD.ordinal()] = 1;
         } catch (NoSuchFieldError var8) {
         }

         try {
            var2[Offhand.Swap.GAPPLE_BALL.ordinal()] = 2;
         } catch (NoSuchFieldError var7) {
         }

         try {
            var2[Offhand.Swap.GAPPLE_SHIELD.ordinal()] = 3;
         } catch (NoSuchFieldError var6) {
         }

         try {
            var2[Offhand.Swap.BALL_TOTEM.ordinal()] = 4;
         } catch (NoSuchFieldError var5) {
         }

         try {
            var2[Offhand.Swap.TOTEM_TOTEM.ordinal()] = 5;
         } catch (NoSuchFieldError var4) {
         }

         values2 = var2;
      }
   }
}