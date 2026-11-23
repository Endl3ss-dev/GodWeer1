// deobf by pusia
package su.catlean.client.features.modules.esp;

import java.awt.Color;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.reflect.KProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.ColorSetting;
import su.catlean.api.setting.settings.EnumSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.ServerInfoService;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.math.Vec3iUtil;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class StorageEsp extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(StorageEsp.class, "shaderMode", "getShaderMode()Lsu/catlean/qz;", 0), new PropertyReference1Impl(StorageEsp.class, "tracers", "getTracers()Z", 0), new PropertyReference1Impl(StorageEsp.class, "chests", "getChests()Z", 0), new PropertyReference1Impl(StorageEsp.class, "chest", "getChest()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "trappedChests", "getTrappedChests()Z", 0), new PropertyReference1Impl(StorageEsp.class, "trappedChest", "getTrappedChest()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "enderChests", "getEnderChests()Z", 0), new PropertyReference1Impl(StorageEsp.class, "enderChest", "getEnderChest()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "furnaces", "getFurnaces()Z", 0), new PropertyReference1Impl(StorageEsp.class, "furnace", "getFurnace()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "dispencers", "getDispencers()Z", 0), new PropertyReference1Impl(StorageEsp.class, "dispencer", "getDispencer()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "hoppers", "getHoppers()Z", 0), new PropertyReference1Impl(StorageEsp.class, "hopper", "getHopper()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "shulkers", "getShulkers()Z", 0), new PropertyReference1Impl(StorageEsp.class, "shulker", "getShulker()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "brewingStands", "getBrewingStands()Z", 0), new PropertyReference1Impl(StorageEsp.class, "brewingStand", "getBrewingStand()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "barrels", "getBarrels()Z", 0), new PropertyReference1Impl(StorageEsp.class, "barrel", "getBarrel()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "chestMinecarts", "getChestMinecarts()Z", 0), new PropertyReference1Impl(StorageEsp.class, "chestMinecart", "getChestMinecart()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "furnaceMinecarts", "getFurnaceMinecarts()Z", 0), new PropertyReference1Impl(StorageEsp.class, "furnaceMinecart", "getFurnaceMinecart()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "hopperMinecarts", "getHopperMinecarts()Z", 0), new PropertyReference1Impl(StorageEsp.class, "hopperMinecart", "getHopperMinecart()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "frames", "getFrames()Z", 0), new PropertyReference1Impl(StorageEsp.class, "emptyFrame", "getEmptyFrame()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "itemFrame", "getItemFrame()Ljava/awt/Color;", 0), new PropertyReference1Impl(StorageEsp.class, "beds", "getBeds()Z", 0) };
   @NotNull
   public static final StorageEsp INSTANCE = new StorageEsp();
   @NotNull
   private static final EnumSetting shaderMode = SettingFactory.defaultImpl(INSTANCE, "shader-mode", StorageEsp.ShaderMode.DEFAULT, null, null, 12, null);
   @NotNull
   private static final BooleanSetting tracers = SettingFactory.defaultImpl(INSTANCE, "tracers", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting chests = SettingFactory.defaultImpl(INSTANCE, "chests", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting chest = SettingFactory.defaultImpl(
      INSTANCE, "chest", new Color(255, 155, 75, 100), null, () -> INSTANCE.getChests(), 4, null
   );
   @NotNull
   private static final BooleanSetting trappedChests = SettingFactory.defaultImpl(INSTANCE, "trapped-chests", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting trappedChest = SettingFactory.defaultImpl(
      INSTANCE, "trapped-chest", new Color(255, 255, 12, 100), null, () -> INSTANCE.getTrappedChests(), 4, null
   );
   @NotNull
   private static final BooleanSetting enderChests = SettingFactory.defaultImpl(INSTANCE, "ender-chests", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting enderChest = SettingFactory.defaultImpl(
      INSTANCE, "ender-chest", new Color(255, 13, 255, 100), null, () -> INSTANCE.getEnderChests(), 4, null
   );
   @NotNull
   private static final BooleanSetting furnaces = SettingFactory.defaultImpl(INSTANCE, "furnaces", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting furnace = SettingFactory.defaultImpl(
      INSTANCE, "furnace", new Color(128, 128, 128, 100), null, () -> INSTANCE.getFurnaces(), 4, null
   );
   @NotNull
   private static final BooleanSetting dispencers = SettingFactory.defaultImpl(INSTANCE, "dispencers", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting dispencer = SettingFactory.defaultImpl(
      INSTANCE, "dispencer", new Color(140, 140, 178, 100), null, () -> INSTANCE.getDispencers(), 4, null
   );
   @NotNull
   private static final BooleanSetting hoppers = SettingFactory.defaultImpl(INSTANCE, "hoppers", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting hopper = SettingFactory.defaultImpl(
      INSTANCE, "hopper", new Color(115, 115, 155, 100), null, () -> INSTANCE.getHoppers(), 4, null
   );
   @NotNull
   private static final BooleanSetting shulkers = SettingFactory.defaultImpl(INSTANCE, "shulkers", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting shulker = SettingFactory.defaultImpl(
      INSTANCE, "shulker", new Color(128, 50, 255, 100), null, () -> INSTANCE.getShulkers(), 4, null
   );
   @NotNull
   private static final BooleanSetting brewingStands = SettingFactory.defaultImpl(INSTANCE, "brewing-stands", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting brewingStand = SettingFactory.defaultImpl(
      INSTANCE, "brewing-stand", new Color(128, 100, 50, 100), null, () -> INSTANCE.getBrewingStands(), 4, null
   );
   @NotNull
   private static final BooleanSetting barrels = SettingFactory.defaultImpl(INSTANCE, "barrels", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting barrel = SettingFactory.defaultImpl(
      INSTANCE, "barrel", new Color(255, 155, 75, 100), null, () -> INSTANCE.getBarrels(), 4, null
   );
   @NotNull
   private static final BooleanSetting chestMinecarts = SettingFactory.defaultImpl(INSTANCE, "chest-minecarts", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting chestMinecart = SettingFactory.defaultImpl(
      INSTANCE, "chest-minecart", new Color(255, 165, 75, 100), null, () -> INSTANCE.getChestMinecarts(), 4, null
   );
   @NotNull
   private static final BooleanSetting furnaceMinecarts = SettingFactory.defaultImpl(INSTANCE, "furnace-minecarts", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting furnaceMinecart = SettingFactory.defaultImpl(
      INSTANCE, "furnace-minecart", new Color(128, 128, 128, 100), null, () -> INSTANCE.getFurnaceMinecarts(), 4, null
   );
   @NotNull
   private static final BooleanSetting hopperMinecarts = SettingFactory.defaultImpl(INSTANCE, "hopper-minecarts", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting hopperMinecart = SettingFactory.defaultImpl(
      INSTANCE, "hopper-minecart", new Color(115, 115, 155, 100), null, () -> INSTANCE.getHopperMinecarts(), 4, null
   );
   @NotNull
   private static final BooleanSetting frames = SettingFactory.defaultImpl(INSTANCE, "frames", true, null, null, 12, null);
   @NotNull
   private static final ColorSetting emptyFrame = SettingFactory.defaultImpl(
      INSTANCE, "empty-frame", new Color(115, 25, 25, 100), null, () -> INSTANCE.getFrames(), 4, null
   );
   @NotNull
   private static final ColorSetting itemFrame = SettingFactory.defaultImpl(
      INSTANCE, "item-frame", new Color(115, 25, 25, 100), null, () -> INSTANCE.getFrames(), 4, null
   );
   @NotNull
   private static final BooleanSetting beds = SettingFactory.defaultImpl(INSTANCE, "beds", true, null, null, 12, null);

   private StorageEsp() {
      super("storage-esp", SubCategories.getSubCategory7(), null, 4, null);
   }

   @NotNull
   public final StorageEsp.ShaderMode getShaderMode() {
      return (StorageEsp.ShaderMode)shaderMode.computeObject(this, $$delegatedProperties[0]);
   }

   private final boolean getTracers() {
      return (Boolean)tracers.computeObject(this, $$delegatedProperties[1]);
   }

   private final boolean getChests() {
      return (Boolean)chests.computeObject(this, $$delegatedProperties[2]);
   }

   private final Color getChest() {
      return (Color)chest.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getTrappedChests() {
      return (Boolean)trappedChests.computeObject(this, $$delegatedProperties[4]);
   }

   private final Color getTrappedChest() {
      return (Color)trappedChest.computeObject(this, $$delegatedProperties[5]);
   }

   private final boolean getEnderChests() {
      return (Boolean)enderChests.computeObject(this, $$delegatedProperties[6]);
   }

   private final Color getEnderChest() {
      return (Color)enderChest.computeObject(this, $$delegatedProperties[7]);
   }

   private final boolean getFurnaces() {
      return (Boolean)furnaces.computeObject(this, $$delegatedProperties[8]);
   }

   private final Color getFurnace() {
      return (Color)furnace.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getDispencers() {
      return (Boolean)dispencers.computeObject(this, $$delegatedProperties[10]);
   }

   private final Color getDispencer() {
      return (Color)dispencer.computeObject(this, $$delegatedProperties[11]);
   }

   private final boolean getHoppers() {
      return (Boolean)hoppers.computeObject(this, $$delegatedProperties[12]);
   }

   private final Color getHopper() {
      return (Color)hopper.computeObject(this, $$delegatedProperties[13]);
   }

   private final boolean getShulkers() {
      return (Boolean)shulkers.computeObject(this, $$delegatedProperties[14]);
   }

   private final Color getShulker() {
      return (Color)shulker.computeObject(this, $$delegatedProperties[15]);
   }

   private final boolean getBrewingStands() {
      return (Boolean)brewingStands.computeObject(this, $$delegatedProperties[16]);
   }

   private final Color getBrewingStand() {
      return (Color)brewingStand.computeObject(this, $$delegatedProperties[17]);
   }

   private final boolean getBarrels() {
      return (Boolean)barrels.computeObject(this, $$delegatedProperties[18]);
   }

   private final Color getBarrel() {
      return (Color)barrel.computeObject(this, $$delegatedProperties[19]);
   }

   private final boolean getChestMinecarts() {
      return (Boolean)chestMinecarts.computeObject(this, $$delegatedProperties[20]);
   }

   private final Color getChestMinecart() {
      return (Color)chestMinecart.computeObject(this, $$delegatedProperties[21]);
   }

   private final boolean getFurnaceMinecarts() {
      return (Boolean)furnaceMinecarts.computeObject(this, $$delegatedProperties[22]);
   }

   private final Color getFurnaceMinecart() {
      return (Color)furnaceMinecart.computeObject(this, $$delegatedProperties[23]);
   }

   private final boolean getHopperMinecarts() {
      return (Boolean)hopperMinecarts.computeObject(this, $$delegatedProperties[24]);
   }

   private final Color getHopperMinecart() {
      return (Color)hopperMinecart.computeObject(this, $$delegatedProperties[25]);
   }

   private final boolean getFrames() {
      return (Boolean)frames.computeObject(this, $$delegatedProperties[26]);
   }

   private final Color getEmptyFrame() {
      return (Color)emptyFrame.computeObject(this, $$delegatedProperties[27]);
   }

   private final Color getItemFrame() {
      return (Color)itemFrame.computeObject(this, $$delegatedProperties[28]);
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      for (BlockEntity item : (Iterable<BlockEntity>)ServerInfoService.INSTANCE.getItems()) {
         Color color = this.computeColor(item);
         if (!Intrinsics.areEqual(color, Color.white)) {
            if (item instanceof BrewingStandBlockEntity) {
               AABB aabb = new AABB(((BrewingStandBlockEntity)item).getBlockPos());
               Render3D.defaultImpl(Render3D.INSTANCE, aabb.contract(0.1, 0.9, 0.1).move(0.05, 0.0, 0.05), color, null, 4, null);
               Render3D.defaultImpl(Render3D.INSTANCE, aabb.contract(0.9, 0.12, 0.9).move(0.45, 0.0, 0.45), color, null, 4, null);
            } else if (item instanceof HopperBlockEntity) {
               AABB aabb2 = new AABB(((HopperBlockEntity)item).getBlockPos());
               Render3D.defaultImpl(Render3D.INSTANCE, aabb2.contract(0.0, 0.62, 0.0).move(0.0, 0.62, 0.0), color, null, 4, null);
               Render3D.defaultImpl(Render3D.INSTANCE, aabb2.contract(0.5, 0.63, 0.5).move(0.25, 0.25, 0.25), color, null, 4, null);
               Render3D.defaultImpl(Render3D.INSTANCE, aabb2.contract(0.76, 0.75, 0.75).move(0.38, 0.0, 0.38), color, null, 4, null);
            } else {
               AABB move = item.getBlockState()
                  .getShape((BlockGetter)MinecraftHolder.getClientLevel(), item.getBlockPos())
                  .bounds()
                  .move(item.getBlockPos());
               Render3D.defaultImpl(Render3D.INSTANCE, move, color, null, 4, null);
            }

            if (this.getTracers()) {
               Vec3 position = new Vec3(0.0, 0.0, 75.0)
                  .xRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().xRot())))
                  .yRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().yRot())))
                  .add(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().position());
               Render3D instance = Render3D.INSTANCE;
               BlockPos blockPos = item.getBlockPos();
               Render3D.defaultImpl(instance, position, Vec3iUtil.computeVec3(blockPos), Render2D.INSTANCE.computeColor(color, 1.0F), null, true, 8, null);
            }
         }
      }

      for (Entity entitiesForRenderingItem : MinecraftHolder.getClientLevel().entitiesForRendering()) {
         Color item2 = this.fetchItem(entitiesForRenderingItem);
         if (!Intrinsics.areEqual(item2, Color.white)) {
            Render3D.defaultImpl(Render3D.INSTANCE, entitiesForRenderingItem.getBoundingBox(), item2, null, 4, null);
            if (this.getTracers()) {
               Vec3 position2 = new Vec3(0.0, 0.0, 75.0)
                  .xRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().xRot())))
                  .yRot(-((float)Math.toRadians(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().yRot())))
                  .add(MinecraftHolder.getMinecraft().gameRenderer.mainCamera().position());
               Render3D.defaultImpl(
                  Render3D.INSTANCE, position2, entitiesForRenderingItem.getBoundingBox().getCenter(), Render2D.INSTANCE.computeColor(item2, 1.0F), null, true, 8, null
               );
            }
         }
      }
   }

   private final Color computeColor(BlockEntity blockEntity) {
      if (blockEntity instanceof TrappedChestBlockEntity) {
         return this.getTrappedChests() ? this.getTrappedChest() : Color.WHITE;
      } else if (blockEntity instanceof ChestBlockEntity && this.getChests()) {
         return this.getChest();
      } else if (blockEntity instanceof EnderChestBlockEntity && this.getEnderChests()) {
         return this.getEnderChest();
      } else if (blockEntity instanceof AbstractFurnaceBlockEntity && this.getFurnaces()) {
         return this.getFurnace();
      } else if (blockEntity instanceof DispenserBlockEntity && this.getDispencers()) {
         return this.getDispencer();
      } else if (blockEntity instanceof HopperBlockEntity && this.getHoppers()) {
         return this.getHopper();
      } else if (blockEntity instanceof ShulkerBoxBlockEntity && this.getShulkers()) {
         return this.getShulker();
      } else if (blockEntity instanceof BrewingStandBlockEntity && this.getBrewingStands()) {
         return this.getBrewingStand();
      } else {
         return blockEntity instanceof BarrelBlockEntity && this.getBarrels() ? this.getBarrel() : Color.WHITE;
      }
   }

   private final Color fetchItem(Entity entity) {
      if (entity instanceof MinecartChest && this.getChestMinecarts()) {
         return this.getChestMinecart();
      } else if (entity instanceof MinecartFurnace && this.getFurnaceMinecarts()) {
         return this.getFurnaceMinecart();
      } else if (entity instanceof MinecartHopper && this.getHopperMinecarts()) {
         return this.getHopperMinecart();
      } else if (entity instanceof ItemFrame && this.getFrames()) {
         return ((ItemFrame)entity).getItem().isEmpty() ? this.getEmptyFrame() : this.getItemFrame();
      } else {
         return Color.WHITE;
      }
   }

   static {
   }

   public static enum ShaderMode {
      DEFAULT,
      LINES,
      DOTS;

      private static final StorageEsp.ShaderMode[] shaderModes = $values();
      private static final EnumEntries<StorageEsp.ShaderMode> enumEntries = EnumEntriesKt.enumEntries(shaderModes);

      private ShaderMode() {
      }

      @NotNull
      public static EnumEntries<StorageEsp.ShaderMode> getEnumEntries() {
         return enumEntries;
      }

      private static final StorageEsp.ShaderMode[] $values() {
         return new StorageEsp.ShaderMode[]{DEFAULT, LINES, DOTS};
      }
   }
}