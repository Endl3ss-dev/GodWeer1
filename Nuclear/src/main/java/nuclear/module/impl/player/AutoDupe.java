package nuclear.module.impl.player;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3d;
import nuclear.control.events.Event;
import nuclear.control.events.impl.player.EventUpdate;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.module.settings.imp.TextSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@ModuleDescriptor(
    name = "AutoDupe",
    type = ModuleCategory.Player,
    desc = "Collects configured items from nearby containers and dropped stacks"
)
public class AutoDupe extends Module {
    private final TextSetting targets = new TextSetting("Targets", "shulker,totem,crystal,elytra");
    private final SliderSetting range = new SliderSetting("Range", 8.0f, 2.0f, 24.0f, 0.5f);
    private final SliderSetting delayMs = new SliderSetting("Delay", 120.0f, 50.0f, 1000.0f, 50.0f);
    private final BooleanSetting autoLootContainers = new BooleanSetting("Loot containers", true);
    private final BooleanSetting moveToDrops = new BooleanSetting("Move to drops", true);
    private final BooleanSetting renderTarget = new BooleanSetting("Render target", true);

    private long lastActionTime;
    private ItemEntity currentTarget;

    public AutoDupe() {
        addSettings(targets, range, delayMs, autoLootContainers, moveToDrops, renderTarget);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            currentTarget = null;
            return false;
        }

        if (event instanceof EventUpdate) {
            handleContainerLoot();
            updateDropTarget();
            moveToTarget();
        } else if (event instanceof EventRender && ((EventRender) event).isRender3D()) {
            renderTarget();
        }
        return false;
    }

    private void handleContainerLoot() {
        if (!autoLootContainers.get() || !(mc.player.openContainer instanceof ChestContainer)) {
            return;
        }
        if (System.currentTimeMillis() - lastActionTime < delayMs.getValue().longValue()) {
            return;
        }

        ChestContainer chest = (ChestContainer) mc.player.openContainer;
        int size = chest.getLowerChestInventory().getSizeInventory();
        for (int slot = 0; slot < size; slot++) {
            ItemStack stack = chest.getLowerChestInventory().getStackInSlot(slot);
            if (!matchesTarget(stack)) {
                continue;
            }

            mc.playerController.windowClick(chest.windowId, slot, 0, ClickType.QUICK_MOVE, mc.player);
            lastActionTime = System.currentTimeMillis();
            return;
        }
    }

    private void updateDropTarget() {
        currentTarget = null;
        double maxDistance = range.getValue().doubleValue();
        double bestDistance = Double.MAX_VALUE;

        for (ItemEntity entity : mc.world.getEntitiesWithinAABB(ItemEntity.class, mc.player.getBoundingBox().grow(maxDistance))) {
            if (!matchesTarget(entity.getItem())) {
                continue;
            }

            double distance = mc.player.getDistanceSq(entity);
            if (distance < bestDistance) {
                bestDistance = distance;
                currentTarget = entity;
            }
        }
    }

    private void moveToTarget() {
        if (!moveToDrops.get() || currentTarget == null) {
            return;
        }

        Vector3d diff = currentTarget.getPositionVec().subtract(mc.player.getPositionVec());
        if (diff.lengthSquared() < 1.0E-4 || diff.length() < 1.2) {
            return;
        }

        Vector3d direction = diff.normalize().scale(0.18);
        mc.player.setMotion(direction.x, mc.player.getMotion().y, direction.z);
    }

    private boolean matchesTarget(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }

        String displayName = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
        String registryName = stack.getItem().getRegistryName() == null
            ? ""
            : stack.getItem().getRegistryName().toString().toLowerCase(Locale.ROOT);

        for (String token : getTokens()) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty() && (displayName.contains(trimmed) || registryName.contains(trimmed))) {
                return true;
            }
        }
        return stack.isEnchanted();
    }

    private List<String> getTokens() {
        return Arrays.asList(targets.get().toLowerCase(Locale.ROOT).split("[,;]"));
    }

    private void renderTarget() {
        if (!renderTarget.get() || currentTarget == null) {
            return;
        }
        RenderUtils.Render3D.drawBox(
            currentTarget.getBoundingBox().offset(
                -mc.getRenderManager().info.getProjectedView().x,
                -mc.getRenderManager().info.getProjectedView().y,
                -mc.getRenderManager().info.getProjectedView().z
            ),
            ColorUtils.rgba(140, 220, 255, 180)
        );
    }
}
