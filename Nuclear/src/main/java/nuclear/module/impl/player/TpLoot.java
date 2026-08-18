package nuclear.module.impl.player;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
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
    name = "TpLoot",
    type = ModuleCategory.Player,
    desc = "Teleports to configured valuable item drops"
)
public class TpLoot extends Module {
    private final TextSetting whitelist = new TextSetting("Whitelist", "totem,elytra,shulker,netherite,diamond,crystal");
    private final SliderSetting range = new SliderSetting("Range", 16.0f, 4.0f, 64.0f, 1.0f);
    private final SliderSetting delayMs = new SliderSetting("Delay", 500.0f, 100.0f, 5000.0f, 100.0f);
    private final BooleanSetting onlyGroundItems = new BooleanSetting("Only ground items", true);
    private final BooleanSetting renderTarget = new BooleanSetting("Render target", true);

    private long lastTeleportTime;
    private ItemEntity currentTarget;

    public TpLoot() {
        addSettings(whitelist, range, delayMs, onlyGroundItems, renderTarget);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            currentTarget = null;
            return false;
        }

        if (event instanceof EventUpdate) {
            updateTarget();
            teleportToTarget();
        } else if (event instanceof EventRender && ((EventRender) event).isRender3D()) {
            renderTarget();
        }
        return false;
    }

    private void updateTarget() {
        currentTarget = null;
        double maxDistance = range.getValue().doubleValue();
        double bestDistance = Double.MAX_VALUE;

        for (ItemEntity entity : mc.world.getEntitiesWithinAABB(ItemEntity.class, mc.player.getBoundingBox().grow(maxDistance))) {
            if (!isValuable(entity)) {
                continue;
            }

            double distance = mc.player.getDistanceSq(entity);
            if (distance < bestDistance) {
                bestDistance = distance;
                currentTarget = entity;
            }
        }
    }

    private void teleportToTarget() {
        if (currentTarget == null) {
            return;
        }
        if (System.currentTimeMillis() - lastTeleportTime < delayMs.getValue().longValue()) {
            return;
        }

        mc.player.setPositionAndUpdate(currentTarget.getPosX(), currentTarget.getPosY(), currentTarget.getPosZ());
        lastTeleportTime = System.currentTimeMillis();
    }

    private boolean isValuable(ItemEntity entity) {
        if (entity == null || entity.getItem().isEmpty()) {
            return false;
        }
        if (onlyGroundItems.get() && !entity.isOnGround()) {
            return false;
        }

        ItemStack stack = entity.getItem();
        String displayName = stack.getDisplayName().getString().toLowerCase(Locale.ROOT);
        String registryName = stack.getItem().getRegistryName() == null
            ? ""
            : stack.getItem().getRegistryName().toString().toLowerCase(Locale.ROOT);

        for (String token : getWhitelistTokens()) {
            String trimmed = token.trim();
            if (!trimmed.isEmpty() && (displayName.contains(trimmed) || registryName.contains(trimmed))) {
                return true;
            }
        }
        return stack.isEnchanted();
    }

    private List<String> getWhitelistTokens() {
        return Arrays.asList(whitelist.get().toLowerCase(Locale.ROOT).split("[,;]"));
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
            ColorUtils.rgba(255, 230, 80, 180)
        );
    }
}
