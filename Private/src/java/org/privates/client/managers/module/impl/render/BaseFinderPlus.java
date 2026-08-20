package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.item.PaintingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BindSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.scroll.ScrollUtil;

import java.util.ArrayList;
import java.util.List;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "BaseFinderPlus", category = Category.MISC, description = "Нахождение баз противника")
public class BaseFinderPlus extends Module {
    public final Minecraft mc = Minecraft.getInstance();
    public final List<FoundObject> foundObjects = new ArrayList<>();
    public final ScrollUtil scroll = new ScrollUtil();
    public final ModeSetting displayMode = new ModeSetting(this, "Режим отображения", "ГУИ", "На экране");
    public final BindSetting openGuiKey = new BindSetting(this, "Открыть GUI");
    public final BindSetting clearListKey = new BindSetting(this, "Очистить список");

    private int overlayScrollOffset = 0;

    @Override
    public void onEnable() {
        scroll.reset();
        overlayScrollOffset = 0;
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (!isEnabled() || mc.player == null || mc.world == null) return;

        if (displayMode.is("ГУИ") && GLFW.glfwGetKey(mc.getMainWindow().getHandle(), openGuiKey.getValue()) == GLFW.GLFW_PRESS) {
            mc.displayScreen(new BaseFinderPlusScreen(this));
            return;
        }

        if (GLFW.glfwGetKey(mc.getMainWindow().getHandle(), clearListKey.getValue()) == GLFW.GLFW_PRESS) {
            foundObjects.clear();
            return;
        }

        String worldName = mc.world != null && mc.world.getDimensionKey() != null ?
                mc.world.getDimensionKey().getLocation().getPath() : "unknown";

        for (Entity entity : mc.world.getAllEntities()) {
            FoundObject obj = checkEntity(entity, worldName);
            if (obj != null && !containsObject(obj)) foundObjects.add(obj);
        }

        for (TileEntity tile : mc.world.loadedTileEntityList) {
            FoundObject obj = checkTile(tile, worldName);
            if (obj != null && !containsObject(obj)) foundObjects.add(obj);
        }
    }

    @EventHandler
    public void onRender(Render2DEvent event) {
        if (!isEnabled() || !displayMode.is("На экране") || foundObjects.isEmpty()) return;

        MatrixStack matrix = event.getMatrix();
        FontRenderer font = mc.fontRenderer;
        int startY = 70;
        int lineHeight = 12;
        int maxLines = 20;

        int totalObjects = foundObjects.size();
        int start = overlayScrollOffset;
        int end = Math.min(totalObjects, start + maxLines);

        font.drawStringWithShadow(matrix, "BaseFinder+ (" + totalObjects + " объектов)", 10, 50, ColorUtil.WHITE);

        for (int i = start; i < end; i++) {
            FoundObject obj = foundObjects.get(i);
            int y = startY + (i - start) * lineHeight;

            String text = String.format("%s - %d %d %d",
                    obj.name, obj.pos.getX(), obj.pos.getY(), obj.pos.getZ());

            font.drawStringWithShadow(matrix, text, 10, y, ColorUtil.WHITE);
        }

        if (totalObjects > maxLines) {
            String scrollInfo = String.format("(%d-%d из %d)", start + 1, end, totalObjects);
            font.drawStringWithShadow(matrix, scrollInfo, 10, startY + maxLines * lineHeight + 5,
                    ColorUtil.getColor(180, 180, 180));
        }
    }

    public void scrollOverlay(int direction) {
        if (!displayMode.is("На экране")) return;

        int maxLines = 20;
        int maxScroll = Math.max(0, foundObjects.size() - maxLines);
        overlayScrollOffset += direction;
        if (overlayScrollOffset < 0) overlayScrollOffset = 0;
        if (overlayScrollOffset > maxScroll) overlayScrollOffset = maxScroll;
    }

    private boolean containsObject(FoundObject obj) {
        for (FoundObject o : foundObjects) {
            if (o.name.equals(obj.name) && o.pos.equals(obj.pos) && o.worldName.equals(obj.worldName)) {
                return true;
            }
        }
        return false;
    }

    FoundObject checkEntity(Entity entity, String worldName) {
        BlockPos pos = entity.getPosition();

        if (entity instanceof PlayerEntity && entity != mc.player) {
            return new FoundObject("Игрок", pos, "minecraft:textures/entity/steve.png", worldName);
        }

        if (entity instanceof ItemEntity item) {
            String itemName = item.getItem().getDisplayName().getString();
            return new FoundObject("Предмет: " + itemName, pos, "minecraft:textures/item/diamond.png", worldName);
        }

        if (entity instanceof ItemFrameEntity) {
            return new FoundObject("Рамка для предметов", pos, "minecraft:textures/entity/item_frame/background.png", worldName);
        }

        if (entity instanceof PaintingEntity) {
            return new FoundObject("Картина", pos, "minecraft:textures/painting/kebab.png", worldName);
        }

        if (entity instanceof BlazeEntity) {
            return new FoundObject("Всплох", pos, "minecraft:textures/entity/blaze.png", worldName);
        }

        if (entity instanceof SlimeEntity) {
            return new FoundObject("Слизень", pos, "minecraft:textures/entity/slime/slime.png", worldName);
        }

        if (entity instanceof EndermanEntity) {
            return new FoundObject("Эндермен", pos, "minecraft:textures/entity/enderman/enderman.png", worldName);
        }

        if (entity instanceof CreeperEntity) {
            return new FoundObject("Крипер", pos, "minecraft:textures/entity/creeper/creeper.png", worldName);
        }

        if (entity instanceof SkeletonEntity) {
            return new FoundObject("Скелет", pos, "minecraft:textures/entity/skeleton/skeleton.png", worldName);
        }

        if (entity instanceof WitherSkeletonEntity) {
            return new FoundObject("Визер-скелет", pos, "minecraft:textures/entity/skeleton/wither_skeleton.png", worldName);
        }

        if (entity instanceof ZombieEntity) {
            return new FoundObject("Зомби", pos, "minecraft:textures/entity/zombie/zombie.png", worldName);
        }

        if (entity instanceof SpiderEntity) {
            return new FoundObject("Паук", pos, "minecraft:textures/entity/spider/spider.png", worldName);
        }

        if (entity instanceof SnowGolemEntity) {
            return new FoundObject("Снеговик", pos, "minecraft:textures/entity/snow_golem.png", worldName);
        }

        if (entity instanceof IronGolemEntity) {
            return new FoundObject("Железный голем", pos, "minecraft:textures/entity/iron_golem/iron_golem.png", worldName);
        }

        if (entity instanceof VillagerEntity) {
            return new FoundObject("Житель", pos, "minecraft:textures/entity/villager/villager.png", worldName);
        }

        if (entity instanceof HorseEntity) {
            return new FoundObject("Лошадь", pos, "minecraft:textures/entity/horse/horse_white.png", worldName);
        }

        if (entity instanceof WolfEntity) {
            return new FoundObject("Волк", pos, "minecraft:textures/entity/wolf/wolf.png", worldName);
        }

        if (entity instanceof CatEntity) {
            return new FoundObject("Кот", pos, "minecraft:textures/entity/cat/tabby.png", worldName);
        }

        return null;
    }

    FoundObject checkTile(TileEntity tile, String worldName) {
        BlockPos pos = tile.getPos();

        if (tile instanceof ChestTileEntity) {
            return new FoundObject("Сундук", pos, "minecraft:textures/entity/chest/normal.png", worldName);
        }

        if (tile instanceof EnderChestTileEntity) {
            return new FoundObject("Эндер-сундук", pos, "minecraft:textures/entity/chest/ender.png", worldName);
        }

        if (tile instanceof BarrelTileEntity) {
            return new FoundObject("Бочка", pos, "minecraft:textures/block/barrel_side.png", worldName);
        }

        if (tile instanceof ShulkerBoxTileEntity) {
            return new FoundObject("Шалкер-ящик", pos, "minecraft:textures/entity/shulker/shulker.png", worldName);
        }

        if (tile instanceof FurnaceTileEntity) {
            return new FoundObject("Печь", pos, "minecraft:textures/block/furnace_front.png", worldName);
        }

        if (tile instanceof BlastFurnaceTileEntity) {
            return new FoundObject("Доменная печь", pos, "minecraft:textures/block/blast_furnace_front.png", worldName);
        }

        if (tile instanceof SmokerTileEntity) {
            return new FoundObject("Коптильня", pos, "minecraft:textures/block/smoker_front.png", worldName);
        }

        if (tile instanceof MobSpawnerTileEntity) {
            return new FoundObject("Рассадник", pos, "minecraft:textures/block/spawner.png", worldName);
        }

        if (tile instanceof BrewingStandTileEntity) {
            return new FoundObject("Зельеварка", pos, "minecraft:textures/block/brewing_stand.png", worldName);
        }

        if (tile instanceof DispenserTileEntity) {
            return new FoundObject("Раздатчик", pos, "minecraft:textures/block/dispenser_front.png", worldName);
        }

        if (tile instanceof DropperTileEntity) {
            return new FoundObject("Выбрасыватель", pos, "minecraft:textures/block/dropper_front.png", worldName);
        }

        if (tile instanceof HopperTileEntity) {
            return new FoundObject("Воронка", pos, "minecraft:textures/block/hopper_outside.png", worldName);
        }

        if (tile instanceof BedTileEntity) {
            return new FoundObject("Кровать", pos, "minecraft:textures/entity/bed/red.png", worldName);
        }

        if (tile instanceof BeaconTileEntity) {
            return new FoundObject("Маяк", pos, "minecraft:textures/block/beacon.png", worldName);
        }

        if (tile instanceof ConduitTileEntity) {
            return new FoundObject("Кондуит", pos, "minecraft:textures/entity/conduit/wind.png", worldName);
        }

        if (tile instanceof EnchantingTableTileEntity) {
            return new FoundObject("Стол зачарований", pos, "minecraft:textures/block/enchanting_table_top.png", worldName);
        }


        if (tile instanceof LecternTileEntity) {
            return new FoundObject("Кафедра", pos, "minecraft:textures/block/lectern_front.png", worldName);
        }

        if (tile instanceof CampfireTileEntity) {
            return new FoundObject("Костёр", pos, "minecraft:textures/block/campfire_fire.png", worldName);
        }


        if (tile instanceof JukeboxTileEntity) {
            return new FoundObject("Проигрыватель", pos, "minecraft:textures/block/jukebox_top.png", worldName);
        }

        return null;
    }

    public record FoundObject(String name, BlockPos pos, String png, String worldName) {
    }

    public void copyCoords(BlockPos pos) {
        mc.keyboardListener.setClipboardString(pos.getX() + " " + pos.getY() + " " + pos.getZ());
    }

    public void sendGPS(BlockPos pos) {
        mc.player.sendChatMessage(".gps set " + pos.getX() + " " + pos.getZ());
    }
}