package nuclear.module.impl.render;

import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import nuclear.control.Manager;
import nuclear.control.events.Event;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.SliderSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

@ModuleDescriptor(
    name = "CrystalDmgEsp",
    type = ModuleCategory.Render,
    desc = "Colors players by the estimated damage from nearby crystals"
)
public class CrystalDmgEsp extends Module {
    private final SliderSetting crystalRange = new SliderSetting("Crystal range", 12.0f, 4.0f, 24.0f, 1.0f);
    private final BooleanSetting includeFriends = new BooleanSetting("Include friends", false);
    private final BooleanSetting includeSelf = new BooleanSetting("Include self", false);

    public CrystalDmgEsp() {
        addSettings(crystalRange, includeFriends, includeSelf);
    }

    @Override
    public boolean onEvent(Event event) {
        if (!(event instanceof EventRender) || !((EventRender) event).isRender3D() || mc.player == null || mc.world == null) {
            return false;
        }

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!shouldRender(player)) {
                continue;
            }

            double damage = estimateDamage(player);
            renderEntityBox(player, getDamageColor(damage));
        }
        return false;
    }

    private boolean shouldRender(PlayerEntity player) {
        if (player == null || !player.isAlive()) {
            return false;
        }
        if (player == mc.player) {
            return includeSelf.get();
        }
        if (!includeFriends.get() && Manager.FRIEND_MANAGER != null && Manager.FRIEND_MANAGER.isFriend(player.getName().getString())) {
            return false;
        }
        return true;
    }

    private double estimateDamage(PlayerEntity player) {
        double best = 0.0;
        double maxRange = crystalRange.getValue().doubleValue();

        for (EnderCrystalEntity crystal : mc.world.getEntitiesWithinAABB(EnderCrystalEntity.class, player.getBoundingBox().grow(maxRange))) {
            double distance = player.getDistance(crystal);
            if (distance > maxRange) {
                continue;
            }

            double rawDamage = Math.max(0.0, 12.0 - distance * 2.2);
            rawDamage *= Math.max(0.25, 1.0 - player.getTotalArmorValue() / 40.0);
            if (rawDamage > best) {
                best = rawDamage;
            }
        }

        return best;
    }

    private int getDamageColor(double damage) {
        if (damage >= 8.0) {
            return ColorUtils.rgba(255, 70, 70, 190);
        }
        if (damage >= 5.0) {
            return ColorUtils.rgba(255, 145, 60, 190);
        }
        if (damage >= 2.0) {
            return ColorUtils.rgba(255, 220, 70, 190);
        }
        return ColorUtils.rgba(80, 220, 120, 190);
    }

    private void renderEntityBox(PlayerEntity player, int color) {
        RenderUtils.Render3D.drawBox(
            player.getBoundingBox().offset(
                -mc.getRenderManager().info.getProjectedView().x,
                -mc.getRenderManager().info.getProjectedView().y,
                -mc.getRenderManager().info.getProjectedView().z
            ),
            color
        );
    }
}
