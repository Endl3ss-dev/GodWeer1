package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "CrystalDmgEsp", description = "Подсвечивает энтити по урону от кристалла", category = Category.RENDER)
public class CrystalDmgEsp extends Module {

    final BooleanSetting fillBox = new BooleanSetting(this, "Заливка", true);
    final BooleanSetting drawOutline = new BooleanSetting(this, "Обводка", true);
    final ColorSetting cannotExplodeColor = new ColorSetting(this, "Нельзя взорвать", new Color(255, 0, 0, 180).getRGB());
    final ColorSetting canExplodeColor = new ColorSetting(this, "Можно взорвать", new Color(0, 255, 0, 180).getRGB());
    final SliderSetting lineWidth = new SliderSetting(this, "Толщина линий", 2.0F, 1.0F, 5.0F, 0.5F).setVisible(drawOutline::getValue);

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.world == null || mc.player == null) return;

        List<EnderCrystalEntity> crystals = new ArrayList<>();
        for (Entity e : mc.world.getAllEntities()) {
            if (e instanceof EnderCrystalEntity crystal && e.isAlive()) {
                crystals.add(crystal);
            }
        }
        if (crystals.isEmpty()) return;

        float pt = event.getPartialTicks();

        for (EnderCrystalEntity crystal : crystals) {
            Vector3d crystalPos = crystal.getPositionVec();
            double radius = 5.6D;
            AxisAlignedBB searchBox = new AxisAlignedBB(
                    crystalPos.x - radius, crystalPos.y - radius, crystalPos.z - radius,
                    crystalPos.x + radius, crystalPos.y + radius, crystalPos.z + radius
            );

            for (Entity nearby : mc.world.getEntitiesWithinAABBExcludingEntity(crystal, searchBox)) {
                if (!(nearby instanceof PlayerEntity player)) continue;
                if (!player.isAlive() || player == mc.player) continue;
                if (Excellent.inst().friendManager().isFriend(player.getGameProfile().getName())) continue;
                if (crystalPos.distanceTo(player.getPositionVec()) > radius) continue;

                float damage = calculateCrystalDamage(crystal, player);
                int color = damage <= 3.0F ? cannotExplodeColor.getValue() : canExplodeColor.getValue();
                renderEntityBox(event, player, color, pt);
            }
        }
    }

    private void renderEntityBox(Render3DPosedEvent event, Entity entity, int color, float pt) {
        AxisAlignedBB bb = interpolateBoundingBox(entity, pt);
        if (fillBox.getValue()) {
            int fill = ColorUtil.replAlpha(color, (int) (((color >>> 24) & 0xFF) * 0.30f));
            RenderUtil3D.drawFilledBoundingBox(event.getMatrix(), bb, fill, false, false);
        }
        if (drawOutline.getValue()) {
            RenderUtil3D.drawBoundingBox(event.getMatrix(), bb, color, lineWidth.getValue(), false, false);
        }
    }

    private AxisAlignedBB interpolateBoundingBox(Entity entity, float pt) {
        AxisAlignedBB bb = entity.getBoundingBox();
        double ix = entity.lastTickPosX + (entity.getPosX() - entity.lastTickPosX) * pt;
        double iy = entity.lastTickPosY + (entity.getPosY() - entity.lastTickPosY) * pt;
        double iz = entity.lastTickPosZ + (entity.getPosZ() - entity.lastTickPosZ) * pt;
        double dx = ix - entity.getPosX();
        double dy = iy - entity.getPosY();
        double dz = iz - entity.getPosZ();
        return bb.offset(dx, dy, dz);
    }

    private float calculateCrystalDamage(EnderCrystalEntity crystal, PlayerEntity target) {
        Vector3d crystalPos = crystal.getPositionVec();
        float explosionRadius = 6.0F;
        float diameter = explosionRadius * 2.0F;

        AxisAlignedBB bb = target.getBoundingBox();
        double cx = (bb.minX + bb.maxX) / 2.0D;
        double cy = (bb.minY + bb.maxY) / 2.0D;
        double cz = (bb.minZ + bb.maxZ) / 2.0D;
        double eyeY = target.getPosY() + target.getEyeHeight();

        double normalizedDist = Math.sqrt(crystalPos.squareDistanceTo(new Vector3d(cx, eyeY, cz))) / diameter;
        if (normalizedDist > 1.0D) return 0.0F;

        double exposure = calculateExposure(crystalPos, target);
        if (exposure < 0.3D) return Math.max(1.0F, (float) (exposure * 10.0D));

        double mult = (1.0D - normalizedDist) * exposure;
        if (mult < 0.05D) return 1.0F;

        float damage = (float) ((mult * mult + mult) / 2.0D * 7.0D * explosionRadius + 1.0D);

        int protection = 0;
        for (ItemStack stack : target.inventory.armorInventory) {
            if (!stack.isEmpty()) {
                protection += EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
            }
        }

        damage *= Math.max(0.3F, 1.0F - protection * 0.08F);
        return Math.max(0.0F, damage);
    }

    private double calculateExposure(Vector3d crystalPos, Entity target) {
        if (mc.world == null) return 0.0D;
        AxisAlignedBB bb = target.getBoundingBox();

        List<Vector3d> pts = List.of(
                new Vector3d((bb.minX + bb.maxX) / 2.0D, (bb.minY + bb.maxY) / 2.0D, (bb.minZ + bb.maxZ) / 2.0D),
                new Vector3d((bb.minX + bb.maxX) / 2.0D, bb.maxY - 0.1D, (bb.minZ + bb.maxZ) / 2.0D),
                new Vector3d((bb.minX + bb.maxX) / 2.0D, bb.minY + 0.1D, (bb.minZ + bb.maxZ) / 2.0D),
                new Vector3d(bb.minX, bb.minY, bb.minZ),
                new Vector3d(bb.maxX, bb.minY, bb.minZ),
                new Vector3d(bb.minX, bb.maxY, bb.minZ),
                new Vector3d(bb.maxX, bb.maxY, bb.minZ),
                new Vector3d(bb.minX, bb.minY, bb.maxZ),
                new Vector3d(bb.maxX, bb.minY, bb.maxZ),
                new Vector3d(bb.minX, bb.maxY, bb.maxZ),
                new Vector3d(bb.maxX, bb.maxY, bb.maxZ)
        );

        int hit = 0;
        for (Vector3d from : pts) {
            BlockRayTraceResult blockRayTraceResult = mc.world.rayTraceBlocks(new RayTraceContext(
                    from, crystalPos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, target
            ));
            if (blockRayTraceResult != null && blockRayTraceResult.getType() == RayTraceResult.Type.MISS) hit++;
        }
        return hit / (double) pts.size();
    }
}
