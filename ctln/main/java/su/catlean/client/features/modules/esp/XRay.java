// deobf by pusia
package su.catlean.client.features.modules.esp;

import java.util.ArrayList;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.reflect.KProperty;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.event.GofraState;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.LightMapUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.event.events.render.XRayBlockEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.SettingHolder;
import su.catlean.api.setting.settings.BlocksSetting;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.api.setting.values.BlocksValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.InventoryManager;
import su.catlean.client.services.ThemeManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.StopWatch;
import su.catlean.utils.inventory.ItemTypeUtil;
import su.catlean.utils.math.MathUtil;
import su.catlean.utils.network.PacketUtil;
import su.catlean.utils.render.Render3D;

public final class XRay extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(XRay.class, "selectedBlocks", "getSelectedBlocks()Lsu/catlean/yt;", 0), new PropertyReference1Impl(XRay.class, "brutForce", "getBrutForce()Z", 0), new PropertyReference1Impl(XRay.class, "fast", "getFast()Z", 0), new PropertyReference1Impl(XRay.class, "delay", "getDelay()I", 0), new PropertyReference1Impl(XRay.class, "radius", "getRadius()I", 0), new PropertyReference1Impl(XRay.class, "up", "getUp()I", 0), new PropertyReference1Impl(XRay.class, "down", "getDown()I", 0) };
   @NotNull
   public static final XRay INSTANCE = new XRay();
   @NotNull
   private static final BlocksSetting blocks;
   @NotNull
   private static final BooleanSetting deobfuscator = SettingFactory.defaultImpl(INSTANCE, "deobfuscator", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting fast = SettingFactory.defaultImpl(INSTANCE, "fast", false, null, () -> INSTANCE.getBrutForce(), 4, null);
   @NotNull
   private static final IntSetting delay = SettingFactory.defaultImpl(INSTANCE, "delay", 25, new IntRange(1, 100), null, () -> INSTANCE.getBrutForce(), 8, null);
   @NotNull
   private static final IntSetting radius = SettingFactory.defaultImpl(INSTANCE, "radius", 5, new IntRange(1, 64), null, () -> INSTANCE.getBrutForce(), 8, null);
   @NotNull
   private static final IntSetting radius2 = SettingFactory.defaultImpl(
      INSTANCE, "radius", 5, new IntRange(1, 32), null, () -> INSTANCE.getBrutForce(), 8, null
   );
   @NotNull
   private static final IntSetting down = SettingFactory.defaultImpl(INSTANCE, "down", 5, new IntRange(1, 32), null, () -> INSTANCE.getBrutForce(), 8, null);
   @NotNull
   private static final StopWatch stopWatch = new StopWatch();
   @NotNull
   private static final ArrayList items = new ArrayList();
   @Nullable
   private static BlockPos blockPos;
   @NotNull
   private static AABB aABB = new AABB(BlockPos.ZERO);
   private static boolean flag;

   private XRay() {
      super("x-ray", SubCategories.getSubCategory7(), null, 4, null);
   }

   private final BlocksValue getSelectedBlocks() {
      return (BlocksValue)blocks.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getBrutForce() {
      return (Boolean)deobfuscator.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getFast() {
      return (Boolean)fast.computeObject(this, $$delegatedProperties[2]);
   }

   private final int getDelay() {
      return ((Number)delay.computeObject(this, $$delegatedProperties[3])).intValue();
   }

   private final int getRadius() {
      return ((Number)radius.computeObject(this, $$delegatedProperties[4])).intValue();
   }

   private final int getUp() {
      return ((Number)radius2.computeObject(this, $$delegatedProperties[5])).intValue();
   }

   private final int getDown() {
      return ((Number)down.computeObject(this, $$delegatedProperties[6])).intValue();
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      GofraState.INSTANCE.setXray(true);
      if (this.getBrutForce() && !flag) {
         items.clear();
         items.addAll(this.getItems());
         aABB = this.getAABB();
      }

      flag = this.getBrutForce();
   }

   @Flow
   private final void onXRayBlock(XRayBlockEvent xRayBlockEvent) {
      if (this.getSelectedBlocks().getBlocks().contains(xRayBlockEvent.getBlock())) {
         xRayBlockEvent.cancel();
      }
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getMinecraft().level != null) {
         items.clear();
         items.addAll(this.getItems());
         GofraState.INSTANCE.setXray(true);
         MinecraftHolder.getMinecraft().levelExtractor.allChanged();
      }
   }

   @Override
   public void update4() {
      if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getMinecraft().level != null) {
         MinecraftHolder.getMinecraft().levelExtractor.allChanged();
         GofraState.INSTANCE.setXray(false);
      }
   }

   @Flow
   private final void onMove(MoveEvent moveEvent) {
      if (this.getBrutForce() && !items.isEmpty()) {
         moveEvent.setZ(0.0);
         moveEvent.setX(0.0);
         moveEvent.cancel();
      }
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (this.getBrutForce()) {
         if (blockPos != null && !items.isEmpty()) {
            Render3D.defaultImpl3(Render3D.INSTANCE, new AABB(blockPos), ThemeManager.INSTANCE.computeColor(0), null, 4, null);
         }

         Render3D.defaultImpl3(Render3D.INSTANCE, aABB, ThemeManager.INSTANCE.computeColor(0), null, 4, null);
         if (!items.isEmpty()) {
            if (ItemTypeUtil.testItemStack2(MinecraftHolder.getLocalPlayer().getMainHandItem())) {
               InventoryManager.INSTANCE.applyGetInventory2(MinecraftHolder.getLocalPlayer().getInventory().getSelectedSlot() % 8 + 1);
            }

            if (stopWatch.testValue2(this.getDelay())) {
               ArrayList items2 = items;
               int defaultImpl;
               if (items.size() - 1 <= 1) {
                  defaultImpl = 0;
               } else {
                  int size = items.size() - 1;
                  Object object = null;
                  byte byteValue = 4;
                  boolean flag2 = false;
                  int value = size;
                  byte byteValue2 = 0;
                  defaultImpl = MathUtil.defaultImpl(byteValue2, value, flag2, byteValue, object);
               }

               BlockPos remove = (BlockPos)items2.remove(defaultImpl);
               MultiPlayerGameMode multiPlayerGameMode = MinecraftHolder.getMultiPlayerGameMode();
               blockPos = remove;
               multiPlayerGameMode.startDestroyBlock(remove, MinecraftHolder.getLocalPlayer().getDirection());
               MinecraftHolder.getMultiPlayerGameMode().stopDestroyBlock();
               Packet serverboundSwingPacket = (Packet)(new ServerboundSwingPacket(InteractionHand.MAIN_HAND));
               PacketUtil.applySend(serverboundSwingPacket);
            }
         }
      }
   }

   @Flow
   private final void onLightMapUpdate(LightMapUpdateEvent lightMapUpdateEvent) {
      lightMapUpdateEvent.cancel();
   }

   private final AABB getAABB() {
      return new AABB(
         MinecraftHolder.getLocalPlayer().getX() - this.getRadius(),
         MinecraftHolder.getLocalPlayer().getY() - this.getDown(),
         MinecraftHolder.getLocalPlayer().getZ() - this.getRadius(),
         MinecraftHolder.getLocalPlayer().getX() + this.getRadius(),
         MinecraftHolder.getLocalPlayer().getY() + this.getUp(),
         MinecraftHolder.getLocalPlayer().getZ() + this.getRadius()
      );
   }

   private final ArrayList<BlockPos> getItems() {
      BlockPos containing = BlockPos.containing((Position)this.computeVec3((Entity)MinecraftHolder.getLocalPlayer()));
      ArrayList<BlockPos> arrayList = new ArrayList();
      int radius3 = containing.getX() - this.getRadius();
      int radius4 = containing.getX() + this.getRadius();
      if (radius3 <= radius4) {
         while (true) {
            int down2 = containing.getY() - this.getDown();
            int value = containing.getY() + this.getUp();
            if (down2 <= value) {
               while (true) {
                  int radius5 = containing.getZ() - this.getRadius();
                  int radius6 = containing.getZ() + this.getRadius();
                  if (radius5 <= radius6) {
                     while (true) {
                        BlockPos blockPos2 = new BlockPos(radius3, down2, radius5);
                        if (!MinecraftHolder.getClientLevel().isEmptyBlock(blockPos2) && (!this.getFast() || radius3 % 2 != 0 && down2 % 2 != 0 && radius5 % 2 != 0)) {
                           arrayList.add(blockPos2);
                        }

                        if (radius5 == radius6) {
                           break;
                        }

                        radius5++;
                     }
                  }

                  if (down2 == value) {
                     break;
                  }

                  down2++;
               }
            }

            if (radius3 == radius4) {
               break;
            }

            radius3++;
         }
      }

      return arrayList;
   }

   static {
      SettingHolder instance = INSTANCE;
      Block[] blocks2 = new Block[]{Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.ANCIENT_DEBRIS};
      blocks = SettingFactory.defaultImpl(instance, "blocks", new BlocksValue(CollectionsKt.mutableListOf(blocks2)), null, null, 12, null);
   }
}