/*
 * Decompiled with CFR 0.153-SNAPSHOT (eccbb2d).
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.ItemEntity
 *  net.minecraft.item.Item
 *  net.minecraft.item.Items
 *  net.minecraft.util.Formatting
 *  net.minecraft.util.math.Vec3d
 *  sLM.4ZXYEo
 */
package minced.module.player;
import minced.core.EventHandler;

import java.util.function.BooleanSupplier;
import minced.core.MultiBooleanSetting;
import minced.core.BooleanSetting;
import minced.core.EventUpdate;
import minced.core.EventMotion;
import minced.core.Rotation;
import minced.core.ChatUtil;
import minced.core.Module;
import minced.enums.Combat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import sLM._4ZXYEo;

public class AimingItems
extends Module {
    static String ITEM_FOUND_MESSAGE;

    MultiBooleanSetting targetItems = new MultiBooleanSetting(TARGET_ITEMS_SETTING_NAME, new BooleanSetting(PLAYER_HEAD_OPTION_NAME, true), new BooleanSetting(ELYTRA_OPTION_NAME, true));
    static String PLAYER_HEAD_OPTION;
    static String ELYTRA_OPTION;
    static MinecraftClient client = MinecraftClient.getInstance();
    Rotation targetRotation = null;
    boolean notificationShown = false;
    ItemEntity targetItem = null;
    static double ITEM_Y_OFFSET;
    static float YAW_OFFSET;
    static float MAX_WRAPPED_YAW;
    static float FULL_ROTATION_DEGREES;
    static float MIN_WRAPPED_YAW;
    static float YAW_WRAP_INCREMENT;
    static float MIN_PITCH;
    static float MAX_PITCH;
    static String MODULE_NAME;
    static String MODULE_DESCRIPTION;
    static String TARGET_ITEMS_SETTING_NAME;
    static String PLAYER_HEAD_OPTION_NAME;
    static String ELYTRA_OPTION_NAME;

    private boolean isTargetItem(ItemEntity param1) {
        if (param1 == null || param1.getStack().isEmpty()) {
            return false;
        }
        Item v2 = param1.getStack().getItem();
        String name = param1.getStack().getName().getString().toLowerCase();
        String key = v2.getTranslationKey().toLowerCase();
        boolean isHead = (v2 == Items.PLAYER_HEAD || key.contains("head") || name.contains("голов") || name.contains("head")) && this.targetItems.method1898(PLAYER_HEAD_OPTION_NAME);
        boolean isElytra = (v2 == Items.ELYTRA || key.contains("elytra") || name.contains("элитр") || name.contains("elytra")) && this.targetItems.method1898(ELYTRA_OPTION_NAME);
        return isHead || isElytra;
    }

    @EventHandler(2)
    public void onUpdate(EventUpdate param1) {
        if (AimingItems.client.player == null || AimingItems.client.world == null) {
            return;
        }
        boolean found = false;
        double bestDistance = Double.MAX_VALUE;
        ItemEntity bestItem = null;
        for (Entity v4 : AimingItems.client.world.getEntities()) {
            ItemEntity v5;
            if (!(v4 instanceof ItemEntity) || !this.isTargetItem(v5 = (ItemEntity)v4)) continue;
            double distance = AimingItems.client.player.squaredDistanceTo(v5);
            if (distance >= bestDistance) continue;
            bestDistance = distance;
            bestItem = v5;
            found = true;
        }
        if (found && bestItem != null) {
            this.targetItem = bestItem;
            this.targetRotation = this.calculateRotation(bestItem);
            AimingItems.client.player.setYaw(this.targetRotation.method3140());
            AimingItems.client.player.setPitch(this.targetRotation.method3136());
            if (!this.notificationShown) {
                ChatUtil.method3308(_4ZXYEo.ф必("makeConcatWithConstants", new Object[]{}, (String)String.valueOf(Formatting.GREEN), (String)String.valueOf(Formatting.WHITE), (String)String.valueOf(Formatting.GOLD), (String)bestItem.getName().getString(), (String)String.valueOf(Formatting.RESET)), new Object[0]);
                this.notificationShown = true;
            }
        } else {
            this.targetRotation = null;
            this.targetItem = null;
            this.notificationShown = false;
        }
    }

    private Rotation calculateRotation(ItemEntity param1) {
        float v7;
        if (AimingItems.client.player == null) {
            return new Rotation(0.0f, 0.0f);
        }
        Vec3d v2 = AimingItems.client.player.getEyePos();
        Vec3d v3 = param1.getPos().add(0.0, ITEM_Y_OFFSET, 0.0);
        Vec3d v4 = v3.subtract(v2);
        double v5 = Math.sqrt(v4.x * v4.x + v4.z * v4.z);
        float v8 = (float)(-Math.toDegrees(Math.atan2(v4.y, v5)));
        for (v7 = (float)Math.toDegrees(Math.atan2(v4.z, v4.x)) - YAW_OFFSET; v7 > MAX_WRAPPED_YAW; v7 -= FULL_ROTATION_DEGREES) {
        }
        while (v7 < MIN_WRAPPED_YAW) {
            v7 += YAW_WRAP_INCREMENT;
        }
        v8 = Math.max(MIN_PITCH, Math.min(MAX_PITCH, v8));
        return new Rotation(v7, v8);
    }

    public AimingItems() {
        super(MODULE_NAME, MODULE_DESCRIPTION, Combat.Player);
        this.addSettings(this.targetItems);
    }

    @EventHandler(2)
    public void onMotion(EventMotion param1) {
        if (this.targetItem == null || this.targetRotation == null || AimingItems.client.player == null) {
            return;
        }
        param1.method2506(this.targetRotation.method3140());
        param1.method2512(this.targetRotation.method3136());
    }

    @Override
    public void onDisable() {
        this.targetRotation = AimingItems.client.player != null ? new Rotation(AimingItems.client.player.getYaw(), AimingItems.client.player.getPitch()) : null;
        this.targetItem = null;
        this.notificationShown = false;
        super.onDisable();
    }

    static {
        MODULE_NAME = "Aiming Items";
        MODULE_DESCRIPTION = "В радиусе прогруженных чанках летит на выбранные предметы";
        TARGET_ITEMS_SETTING_NAME = "Наводиться на";
        PLAYER_HEAD_OPTION_NAME = "Голову игрока";
        ELYTRA_OPTION_NAME = "Элитру";
        ITEM_FOUND_MESSAGE = "\u0001Предмет найден! \u0001Лечу на предмет \u0001\u0001\u0001";
        // Aim at the visible item stack. ItemEntity#getPos is near its base.
        ITEM_Y_OFFSET = 0.15;
        YAW_OFFSET = 90.0f;
        MAX_WRAPPED_YAW = 180.0f;
        FULL_ROTATION_DEGREES = 360.0f;
        MIN_WRAPPED_YAW = -180.0f;
        YAW_WRAP_INCREMENT = 360.0f;
        MIN_PITCH = -90.0f;
        MAX_PITCH = 90.0f;
        PLAYER_HEAD_OPTION = "Голову игрока";
        ELYTRA_OPTION = "Элитру";
    }
}



