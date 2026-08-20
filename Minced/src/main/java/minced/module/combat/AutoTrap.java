/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.util.Hand
 *  net.minecraft.util.hit.BlockHitResult
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Direction
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.BlockView
 */
package minced.module.combat;
import minced.core.EventHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import minced.core.BindSetting;
import minced.core.BooleanSetting;
import minced.core.SliderSetting;
import minced.core.ModeSetting;
import minced.core.EventUpdate;
import minced.core.Rotation;
import minced.core.InventoryUtil;
import minced.core.EventKey;
import minced.core.AimRotation;
import minced.core.Minced;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.BlockView;

public class AutoTrap
extends Module {
    static MinecraftClient client = MinecraftClient.getInstance();
    BooleanSetting useInventory;
    static double HIT_OFFSET_X;
    static double HIT_OFFSET_Y;
    static double HIT_OFFSET_Z;
    boolean placementKeyHeld = false;
    boolean placementPending = false;
    BlockPos pendingBlockPos;
    Direction pendingDirection;
    boolean jumpBeforePlacement = false;
    boolean pendingCobweb;
    List placedPositions;
    int placementIndex = 0;
    List placementQueue;
    ModeSetting blockMode;
    static String COBWEB_MODE;
    static String BOTH_MODE_FOR_COBWEB;
    BooleanSetting jumpForUpperBlocks;
    static double YAW_OFFSET;
    static float ROTATION_SPEED;
    static String MODULE_NAME;
    static String MODULE_DESCRIPTION;
    static String TARGET_MODE_NAME;
    static String DEFAULT_TARGET_MODE;
    static String SINGLE_TARGET_MODE_OPTION;
    static String MULTI_TARGET_MODE_OPTION;
    ModeSetting targetMode = new ModeSetting(TARGET_MODE_NAME, DEFAULT_TARGET_MODE, SINGLE_TARGET_MODE_OPTION, MULTI_TARGET_MODE_OPTION);
    static String BLOCK_MODE_NAME;
    static String DEFAULT_BLOCK_MODE;
    static String COBWEB_MODE_OPTION;
    static String OBSIDIAN_MODE_OPTION;
    static String BOTH_BLOCKS_MODE_OPTION;
    static String RANGE_SETTING_NAME;
    static float DEFAULT_RANGE;
    static float MAX_RANGE;
    static float RANGE_STEP;
    SliderSetting range;
    static String JUMP_SETTING_NAME;
    static String INVENTORY_SETTING_NAME;
    static String BIND_SETTING_NAME;
    BindSetting placementBind;
    static double AIM_OFFSET_X;
    static double AIM_OFFSET_Y;
    static double AIM_OFFSET_Z;
    static String SINGLE_TARGET_MODE;
    static String OBSIDIAN_MODE_FOR_JUMP;
    static String BOTH_MODE_FOR_JUMP;
    static String COBWEB_REQUIRED_MODE;
    static String OBSIDIAN_REQUIRED_MODE;
    static String COBWEB_PLACEMENT_MODE;
    static String BOTH_MODE_FOR_COBWEB_PLACEMENT;
    static String OBSIDIAN_PLACEMENT_MODE;
    static String BOTH_MODE_FOR_OBSIDIAN_PLACEMENT;

    private boolean isObsidian(Item param1) {
        return param1 == Items.OBSIDIAN || param1 == Items.CRYING_OBSIDIAN;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void placeBlock(BlockPos param1, Direction param2, boolean param3) {
        int v5;
        int v92;
        if (AutoTrap.client.player == null || AutoTrap.client.world == null || AutoTrap.client.interactionManager == null) {
            return;
        }
        int v4 = AutoTrap.client.player.getInventory().selectedSlot;
        boolean v6 = false;
        int v7 = -1;
        int v8 = -1;
        if (param3) {
            v92 = this.findCobwebInHotbar();
            if (v92 != -1) {
                v5 = v92;
            } else {
                if (!((Boolean)this.useInventory.getValue()).booleanValue()) return;
                v7 = this.findCobwebInInventory();
                if (v7 == -1) {
                    return;
                }
                v8 = this.getTemporaryHotbarSlot(v4);
                this.swapInventorySlot(v7, v8);
                v5 = v8;
                v6 = true;
            }
        } else {
            v92 = this.findObsidianInHotbar();
            if (v92 != -1) {
                v5 = v92;
            } else {
                if (!((Boolean)this.useInventory.getValue()).booleanValue()) return;
                v7 = this.findObsidianInInventory();
                if (v7 == -1) {
                    return;
                }
                v8 = this.getTemporaryHotbarSlot(v4);
                this.swapInventorySlot(v7, v8);
                v5 = v8;
                v6 = true;
            }
        }
        if (v5 != v4) {
            InventoryUtil.method3313(v5);
        }
        BlockPos v92_166 = param1.offset(param2);
        Vec3d v10 = Vec3d.ofCenter((Vec3i)v92_166).add((double)param2.getOpposite().getOffsetX() * HIT_OFFSET_X, (double)param2.getOpposite().getOffsetY() * HIT_OFFSET_Y, (double)param2.getOpposite().getOffsetZ() * HIT_OFFSET_Z);
        AutoTrap.client.interactionManager.interactBlock(AutoTrap.client.player, Hand.MAIN_HAND, new BlockHitResult(v10, param2.getOpposite(), v92_166, false));
        AutoTrap.client.player.swingHand(Hand.MAIN_HAND);
        if (v5 != v4) {
            InventoryUtil.method3313(v4);
        }
        if (!v6 || v7 == -1 || v8 == -1) return;
        this.swapInventorySlot(v7, v8);
    }

    private boolean isReplaceable(BlockPos param1) {
        return AutoTrap.client.world.getBlockState(param1).isReplaceable();
    }

    @Override
    public void onDisable() {
        this.resetPlacementState();
        this.placementKeyHeld = false;
        super.onDisable();
    }

    private int findObsidianInInventory() {
        for (int v1 = 9; v1 < 36; ++v1) {
            ItemStack v2 = AutoTrap.client.player.getInventory().getStack(v1);
            if (v2.isEmpty() || !this.isObsidian(v2.getItem())) continue;
            return v1;
        }
        return -1;
    }

    private boolean hasCobweb() {
        return this.findCobwebInHotbar() != -1 || this.findCobwebInInventory() != -1;
    }

    private boolean isAtOrAboveEyeLevel(BlockPos param1) {
        double v2 = AutoTrap.client.player.getEyePos().y;
        return (double)param1.getY() >= v2;
    }

    private void swapInventorySlot(int param1, int param2) {
        if (AutoTrap.client.player == null || AutoTrap.client.interactionManager == null) {
            return;
        }
        int v3 = AutoTrap.client.player.currentScreenHandler.syncId;
        int v4 = param1 < 9 ? param1 + 36 : param1;
        AutoTrap.client.interactionManager.clickSlot(v3, v4, param2, SlotActionType.SWAP, (PlayerEntity)AutoTrap.client.player);
    }

    private boolean intersectsTarget(BlockPos param1, List param2) {
        for (PlayerEntity v4 : (Iterable<PlayerEntity>)(param2)) {
            BlockPos v5 = v4.getBlockPos();
            if (!param1.equals((Object)v5) && !param1.equals((Object)v5.up())) continue;
            return true;
        }
        return false;
    }

    @EventHandler(2)
    public void onUpdate(EventUpdate param1) {
        if (AutoTrap.client.player == null || AutoTrap.client.world == null || AutoTrap.client.interactionManager == null) {
            this.resetPlacementState();
            return;
        }
        if (!this.placementKeyHeld) {
            this.resetPlacementState();
            return;
        }
        if (!this.hasRequiredBlocks()) {
            this.resetPlacementState();
            return;
        }
        if (this.placementPending && this.pendingBlockPos != null && this.pendingDirection != null) {
            if (this.jumpBeforePlacement) {
                if (!AutoTrap.client.player.isOnGround() && AutoTrap.client.player.getVelocity().y > 0.0) {
                    this.placeBlock(this.pendingBlockPos, this.pendingDirection, this.pendingCobweb);
                    this.placedPositions.add(this.pendingBlockPos);
                    this.pendingBlockPos = null;
                    this.pendingDirection = null;
                    this.placementPending = false;
                    this.jumpBeforePlacement = false;
                    ++this.placementIndex;
                } else if (AutoTrap.client.player.isOnGround()) {
                    AutoTrap.client.player.jump();
                }
                return;
            }
            this.placeBlock(this.pendingBlockPos, this.pendingDirection, this.pendingCobweb);
            this.placedPositions.add(this.pendingBlockPos);
            this.pendingBlockPos = null;
            this.pendingDirection = null;
            this.placementPending = false;
            ++this.placementIndex;
            return;
        }
        List v2 = this.findTargets();
        if (v2.isEmpty()) {
            this.resetPlacementState();
            return;
        }
        if (this.placementQueue.isEmpty() || this.placementIndex >= this.placementQueue.size()) {
            this.placementQueue.clear();
            this.placedPositions.clear();
            this.placementIndex = 0;
            for (PlayerEntity v4 : (Iterable<PlayerEntity>)(v2)) {
                for (BlockPos v6 : (Iterable<BlockPos>)(this.collectTrapPositions((LivingEntity)v4))) {
                    if (this.placementQueue.contains(v6)) continue;
                    this.placementQueue.add(v6);
                }
            }
        }
        while (this.placementIndex < this.placementQueue.size()) {
            BlockPos v6;
            BlockPos v3 = (BlockPos)this.placementQueue.get(this.placementIndex);
            if (!this.isReplaceable(v3)) {
                ++this.placementIndex;
                continue;
            }
            boolean v4 = this.intersectsTarget(v3, v2);
            boolean v5 = v4 && (this.blockMode.isMode(COBWEB_MODE) || this.blockMode.isMode(BOTH_MODE_FOR_COBWEB)) && this.hasCobweb();
            Direction v6s = this.findSupportDirection(v3);
            if (v6s == null) {
                Direction v8;
                BlockPos v7 = v3.down();
                if (this.isReplaceable(v7) && !this.placedPositions.contains(v7) && (v8 = this.findSupportDirection(v7)) != null) {
                    this.schedulePlacement(v7, v8, false);
                    this.placementQueue.add(this.placementIndex, v7);
                    return;
                }
                ++this.placementIndex;
                continue;
            }
            this.jumpBeforePlacement = (Boolean)this.jumpForUpperBlocks.getValue() != false && !v5 && this.isAtOrAboveEyeLevel(v3);
            this.schedulePlacement(v3, (Direction)v6s, v5);
            if (this.jumpBeforePlacement && AutoTrap.client.player.isOnGround()) {
                AutoTrap.client.player.jump();
            }
            return;
        }
        this.placementQueue.clear();
        this.placementIndex = 0;
    }

    private void resetPlacementState() {
        this.placementPending = false;
        this.jumpBeforePlacement = false;
        this.pendingBlockPos = null;
        this.pendingDirection = null;
        this.placementQueue.clear();
        this.placedPositions.clear();
        this.placementIndex = 0;
    }

    private int getTemporaryHotbarSlot(int param1) {
        return (param1 + 1) % 9;
    }

    private void rotateTo(Vec3d param1) {
        Vec3d v2 = AutoTrap.client.player.getCameraPosVec(client.getRenderTickCounter().getTickDelta(true));
        Vec3d v3 = param1.subtract(v2);
        float v4 = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(v3.z, v3.x)) - YAW_OFFSET));
        float v5 = (float)(-Math.toDegrees(Math.atan2(v3.y, Math.hypot(v3.x, v3.z))));
        AimRotation.method367(new Rotation(v4, v5), ROTATION_SPEED, 1, 6);
    }

    public AutoTrap() {
        super(MODULE_NAME, MODULE_DESCRIPTION, Combat.Combat);
        this.blockMode = new ModeSetting(BLOCK_MODE_NAME, DEFAULT_BLOCK_MODE, COBWEB_MODE_OPTION, OBSIDIAN_MODE_OPTION, BOTH_BLOCKS_MODE_OPTION);
        this.range = new SliderSetting(RANGE_SETTING_NAME, DEFAULT_RANGE, 1.0f, MAX_RANGE, RANGE_STEP);
        this.jumpForUpperBlocks = new BooleanSetting(JUMP_SETTING_NAME, true, () -> this.blockMode.isMode(OBSIDIAN_MODE_FOR_JUMP) || this.blockMode.isMode(BOTH_MODE_FOR_JUMP));
        this.useInventory = new BooleanSetting(INVENTORY_SETTING_NAME, false);
        this.placementBind = new BindSetting(BIND_SETTING_NAME);
        this.placementQueue = new ArrayList();
        this.placedPositions = new ArrayList();
        this.addSettings(this.targetMode, this.blockMode, this.range, this.jumpForUpperBlocks, this.useInventory, this.placementBind);
    }

    private void schedulePlacement(BlockPos param1, Direction param2, boolean param3) {
        this.pendingBlockPos = param1;
        this.pendingDirection = param2;
        this.pendingCobweb = param3;
        this.placementPending = true;
        BlockPos v4 = param1.offset(param2);
        Vec3d v5 = Vec3d.ofCenter((Vec3i)v4).add((double)param2.getOpposite().getOffsetX() * AIM_OFFSET_X, (double)param2.getOpposite().getOffsetY() * AIM_OFFSET_Y, (double)param2.getOpposite().getOffsetZ() * AIM_OFFSET_Z);
        this.rotateTo(v5);
    }

    private boolean isCobweb(Item param1) {
        return param1 == Items.COBWEB;
    }

    private int findObsidianInHotbar() {
        for (int v1 = 0; v1 < 9; ++v1) {
            ItemStack v2 = AutoTrap.client.player.getInventory().getStack(v1);
            if (v2.isEmpty() || !this.isObsidian(v2.getItem())) continue;
            return v1;
        }
        return -1;
    }

    private Direction findSupportDirection(BlockPos param1) {
        for (BlockPos v3 : (Iterable<BlockPos>)(this.placedPositions)) {
            for (Direction v7 : Direction.values()) {
                if (!param1.offset(v7).equals((Object)v3)) continue;
                return v7;
            }
        }
        for (Direction v5 : Direction.values()) {
            Direction v7;
            BlockPos v6 = param1.offset(v5);
            BlockState v7s = AutoTrap.client.world.getBlockState(v6);
            if (v7s.isAir() || !v7s.isSolidBlock((BlockView)AutoTrap.client.world, v6) && !v7s.isOf(Blocks.COBWEB)) continue;
            return v5;
        }
        return null;
    }

    private int findCobwebInHotbar() {
        for (int v1 = 0; v1 < 9; ++v1) {
            ItemStack v2 = AutoTrap.client.player.getInventory().getStack(v1);
            if (v2.isEmpty() || !this.isCobweb(v2.getItem())) continue;
            return v1;
        }
        return -1;
    }

    private int findCobwebInInventory() {
        for (int v1 = 9; v1 < 36; ++v1) {
            ItemStack v2 = AutoTrap.client.player.getInventory().getStack(v1);
            if (v2.isEmpty() || !this.isCobweb(v2.getItem())) continue;
            return v1;
        }
        return -1;
    }

    private List findTargets() {
        Vec3d v1 = AutoTrap.client.player.getPos();
        double v2 = ((Float)this.range.getValue()).floatValue();
        Box v4 = new Box(v1.x - v2, v1.y - v2, v1.z - v2, v1.x + v2, v1.y + v2, v1.z + v2);
        List v5 = AutoTrap.client.world.getEntitiesByClass(PlayerEntity.class, v4, param0 -> param0.isAlive() && param0 != AutoTrap.client.player && !Minced.getInstance().getFriendManager().cfr_renamed_4(param0.getName().getString()));
        v5.sort(Comparator.comparingDouble(param1 -> ((PlayerEntity)param1).squaredDistanceTo(v1)));
        if (this.targetMode.isMode(SINGLE_TARGET_MODE)) {
            return v5.isEmpty() ? List.of() : List.of((PlayerEntity)v5.getFirst());
        }
        return v5;
    }

    @EventHandler(2)
    public void onKey(EventKey param1) {
        if (AutoTrap.client.player == null || AutoTrap.client.currentScreen != null) {
            return;
        }
        if ((Integer)this.placementBind.getValue() != -1 && param1.method3595() == ((Integer)this.placementBind.getValue()).intValue()) {
            this.placementKeyHeld = param1.method3599();
            if (!this.placementKeyHeld) {
                this.resetPlacementState();
            }
        }
    }

    private boolean hasRequiredBlocks() {
        if (this.blockMode.isMode(COBWEB_REQUIRED_MODE)) {
            return this.hasCobweb();
        }
        if (this.blockMode.isMode(OBSIDIAN_REQUIRED_MODE)) {
            return this.hasObsidian();
        }
        return this.hasCobweb() || this.hasObsidian();
    }

    private boolean hasObsidian() {
        return this.findObsidianInHotbar() != -1 || this.findObsidianInInventory() != -1;
    }

    private List collectTrapPositions(LivingEntity param1) {
        BlockPos v2 = param1.getBlockPos();
        BlockPos v3 = v2.up();
        BlockPos v4 = v3.up();
        ArrayList<BlockPos> v5 = new ArrayList<BlockPos>();
        if (this.blockMode.isMode(COBWEB_PLACEMENT_MODE) || this.blockMode.isMode(BOTH_MODE_FOR_COBWEB_PLACEMENT)) {
            if (this.isReplaceable(v2)) {
                v5.add(v2);
            }
            if (this.isReplaceable(v3)) {
                v5.add(v3);
            }
        }
        if (this.blockMode.isMode(OBSIDIAN_PLACEMENT_MODE) || this.blockMode.isMode(BOTH_MODE_FOR_OBSIDIAN_PLACEMENT)) {
            BlockPos v6 = v4.up();
            ArrayList<BlockPos> v7 = new ArrayList<BlockPos>();
            v7.add(v2.north());
            v7.add(v2.south());
            v7.add(v2.east());
            v7.add(v2.west());
            v7.add(v3.north());
            v7.add(v3.south());
            v7.add(v3.east());
            v7.add(v3.west());
            v7.add(v4);
            v7.add(v6);
            for (BlockPos v9 : v7) {
                if (!this.isReplaceable(v9) || v5.contains(v9)) continue;
                v5.add(v9);
            }
        }
        return v5;
    }

    static {
        MODULE_NAME = "Auto Trap";
        MODULE_DESCRIPTION = "Автоматически закрывает человека в паутину/блоки";
        TARGET_MODE_NAME = "Режим";
        DEFAULT_TARGET_MODE = "Одиночный";
        SINGLE_TARGET_MODE_OPTION = "Одиночный";
        MULTI_TARGET_MODE_OPTION = "Мульти";
        BLOCK_MODE_NAME = "Блоки";
        DEFAULT_BLOCK_MODE = "Паутина";
        COBWEB_MODE_OPTION = "Паутина";
        OBSIDIAN_MODE_OPTION = "Обсидиан";
        BOTH_BLOCKS_MODE_OPTION = "Оба";
        RANGE_SETTING_NAME = "Дистанция";
        DEFAULT_RANGE = 4.0f;
        MAX_RANGE = 8.0f;
        RANGE_STEP = 0.5f;
        JUMP_SETTING_NAME = "Прыгать для верха";
        INVENTORY_SETTING_NAME = "Работать из инвентаря";
        BIND_SETTING_NAME = "Кнопка установки";
        COBWEB_MODE = "Паутина";
        BOTH_MODE_FOR_COBWEB = "Оба";
        AIM_OFFSET_X = 0.5;
        AIM_OFFSET_Y = 0.5;
        AIM_OFFSET_Z = 0.5;
        HIT_OFFSET_X = 0.5;
        HIT_OFFSET_Y = 0.5;
        HIT_OFFSET_Z = 0.5;
        YAW_OFFSET = 90.0;
        ROTATION_SPEED = 180.0f;
        COBWEB_PLACEMENT_MODE = "Паутина";
        BOTH_MODE_FOR_COBWEB_PLACEMENT = "Оба";
        OBSIDIAN_PLACEMENT_MODE = "Обсидиан";
        BOTH_MODE_FOR_OBSIDIAN_PLACEMENT = "Оба";
        SINGLE_TARGET_MODE = "Одиночный";
        COBWEB_REQUIRED_MODE = "Паутина";
        OBSIDIAN_REQUIRED_MODE = "Обсидиан";
        OBSIDIAN_MODE_FOR_JUMP = "Обсидиан";
        BOTH_MODE_FOR_JUMP = "Оба";
    }
}




