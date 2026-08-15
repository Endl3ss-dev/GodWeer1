package antileak.base.client.modules.impl.movement;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventBinding;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BindSetting;

public class WindHop extends Module {

    public static final WindHop INSTANCE = new WindHop();

    private static final double WALL_CHECK_DISTANCE = 2.5;
    private static final double MIN_UPWARD_VELOCITY = 0.05;

    private final BindSetting jumpBind = new BindSetting("Бинд прыжка", -1);

    private boolean bindQueued = false;

    public WindHop() {
        super("WindHop", "Автоматически использует заряд ветра для прыжка или отскока от стен", ModuleCategory.MOVEMENT);
        addSettings(jumpBind);
    }

    @Override
    public void onEnable() {
        this.bindQueued = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.bindQueued = false;
        super.onDisable();
    }

    @EventLink
    public void onBinding(final EventBinding event) {
        if (mc.player == null || mc.world == null) return;
        
        if (jumpBind.getKey() != -1 && event.getKey() == jumpBind.getKey()) {
            this.bindQueued = true;
        }
    }

    @EventLink
    public void onUpdate(final EventUpdate event) {
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        
        if (this.bindQueued) {
            this.bindQueued = false;
            this.useWindCharge();
        }
    }

    private void useWindCharge() {
        int slot = this.findWindChargeSlot();
        if (slot == -1 || mc.getNetworkHandler() == null) return;

        boolean offhand = slot == 40;
        boolean hotbar = slot < 9;
        int previousSlot = mc.player.getInventory().selectedSlot;
        Hand hand = offhand ? Hand.OFF_HAND : Hand.MAIN_HAND;

        float yaw = mc.player.getYaw();
        float pitch = this.resolvePitch(yaw);

        // Переключаем слот если нужно
        if (hotbar && !offhand && slot != previousSlot) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(slot));
        }

        // Используем заряд ветра
        mc.getNetworkHandler().sendPacket(new PlayerInteractItemC2SPacket(hand, 0, yaw, pitch));
        mc.player.swingHand(hand);

        // Возвращаем исходный слот
        if (hotbar && !offhand && slot != previousSlot) {
            mc.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(previousSlot));
        }
    }

    private float resolvePitch(float yaw) {
        // Если в воздухе, летим вверх и нет блоков снизу - пытаемся найти стену для отскока
        if (!mc.player.isOnGround() 
                && this.blockBelowOffset(0.0, -0.6, 0.0) == Blocks.AIR 
                && mc.player.getVelocity().y > MIN_UPWARD_VELOCITY) {
            
            // Проверяем все стороны на наличие стены
            for (int angle = 0; angle < 360; angle += 45) {
                if (this.raycastWall(yaw, (float) angle, (float) WALL_CHECK_DISTANCE)) {
                    return normalizeYaw(yaw + (float) angle);
                }
            }
        }

        // По умолчанию прыгаем вниз (pitch = 90 = вниз)
        return 90.0F;
    }

    private boolean raycastWall(float yaw, float angleOffset, float distance) {
        Vec3d eye = mc.player.getEyePos();
        
        // Создаем вектор направления от текущего угла взгляда + смещение
        Vec3d direction = Vec3d.fromPolar(0.0F, normalizeYaw(yaw + angleOffset)).multiply(distance);
        
        // Проверяем есть ли блок в этом направлении
        HitResult hit = mc.world.raycast(new RaycastContext(
                eye, 
                eye.add(direction), 
                RaycastContext.ShapeType.COLLIDER, 
                RaycastContext.FluidHandling.NONE, 
                mc.player
        ));
        
        return hit.getType() == HitResult.Type.BLOCK;
    }

    private Block blockBelowOffset(double x, double y, double z) {
        if (mc.player == null || mc.world == null) return Blocks.AIR;
        
        BlockPos pos = BlockPos.ofFloored(mc.player.getPos().add(x, y, z));
        return mc.world.getBlockState(pos).getBlock();
    }

    private int findWindChargeSlot() {
        if (mc.player == null) return -1;

        // Проверяем оффхенд
        if (mc.player.getOffHandStack().isOf(Items.WIND_CHARGE)) {
            return 40;
        }

        // Проверяем хотбар
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).isOf(Items.WIND_CHARGE)) {
                return i;
            }
        }

        return -1;
    }

    private static float normalizeYaw(float yaw) {
        while (yaw > 180.0F) {
            yaw -= 360.0F;
        }
        while (yaw < -180.0F) {
            yaw += 360.0F;
        }
        return yaw;
    }
}
