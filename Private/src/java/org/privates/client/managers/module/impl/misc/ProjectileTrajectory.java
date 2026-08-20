package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.*;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

import java.util.ArrayList;
import java.util.List;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ProjectileTrajectory", category = Category.MISC, description = "")
public class ProjectileTrajectory extends Module {

    public static ProjectileTrajectory getInstance() {
        return Instance.get(ProjectileTrajectory.class);
    }

    private final ModeSetting renderMode = new ModeSetting(this, "Режим рендера", "Круг", "Круг", "Взрыв", "Пульсация", "Спираль", "Звезда");
    private final SliderSetting radiusSize = new SliderSetting(this, "Размер", 1.2f, 0.3f, 4.0f, 0.1f);
    private final BooleanSetting rainbow = new BooleanSetting(this, "Радужный", true);
    private final BooleanSetting showTrail = new BooleanSetting(this, "Показать траекторию", false);
    private final SliderSetting trailLength = new SliderSetting(this, "Длина траектории", 30, 10, 100, 5);
    private final BooleanSetting glow = new BooleanSetting(this, "Свечение", true);
    private final SliderSetting animationSpeed = new SliderSetting(this, "Скорость анимации", 1.0f, 0.1f, 3.0f, 0.1f);

    private final List<Vector3d> trajectoryPoints = new ArrayList<>();
    private final long startTime = System.currentTimeMillis();

    @EventHandler
    public void onRender3DLast(Render3DLastEvent event) {
        if (mc.player == null || mc.world == null) return;

        ItemStack heldItem = mc.player.getHeldItem(Hand.MAIN_HAND);
        if (!isValidProjectileItem(heldItem)) {
            heldItem = mc.player.getHeldItem(Hand.OFF_HAND);
            if (!isValidProjectileItem(heldItem)) return;
        }

        renderProjectileImpact(event, heldItem);
    }

    private boolean isValidProjectileItem(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof BowItem ||
                item instanceof CrossbowItem ||
                item instanceof TridentItem ||
                item instanceof SnowballItem ||
                item instanceof EggItem ||
                item instanceof EnderPearlItem ||
                item instanceof PotionItem;
    }

    private void renderProjectileImpact(Render3DLastEvent event, ItemStack stack) {
        MatrixStack matrix = event.getMatrix();
        float partialTicks = event.getPartialTicks();

        Vector3d startPos = getHandPosition(partialTicks);
        Vector3d lookVec = getLookVector();
        Vector3d impactPoint = calculateImpactPoint(stack, startPos, lookVec);

        if (impactPoint == null) return;

        RenderSystem.pushMatrix();
        RenderSystem.multMatrix(matrix.getLast().getMatrix());
        RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(), -mc.getRenderManager().renderPosZ());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();

        if (showTrail.getValue() && !trajectoryPoints.isEmpty()) {
            renderTrajectoryTrail();
        }

        float[] color = getAnimatedColor();
        GL11.glColor4f(color[0], color[1], color[2], 0.6f);

        float size = stack.getItem() instanceof TridentItem ? radiusSize.getValue() * 2.5f : radiusSize.getValue();

        switch (renderMode.getValue()) {
            case "Круг":
                renderAnimatedCircle(impactPoint, size);
                break;
            case "Взрыв":
                renderExplosionEffect(impactPoint, size);
                break;
            case "Пульсация":
                renderPulsingCircle(impactPoint, size);
                break;
            case "Спираль":
                renderSpiralEffect(impactPoint, size);
                break;
            case "Звезда":
                renderStarEffect(impactPoint, size);
                break;
        }

        if (glow.getValue()) {
            GL11.glColor4f(color[0], color[1], color[2], 0.2f);
            renderAnimatedCircle(impactPoint, size * 1.5f);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
    }

    private void renderTrajectoryTrail() {
        GL11.glLineWidth(2.0f);
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i < trajectoryPoints.size(); i++) {
            Vector3d point = trajectoryPoints.get(i);
            float alpha = (float) i / trajectoryPoints.size() * 0.5f;
            float[] color = getAnimatedColor();
            GL11.glColor4f(color[0], color[1], color[2], alpha);
            GL11.glVertex3d(point.x, point.y, point.z);
        }

        GL11.glEnd();
    }

    private float[] getAnimatedColor() {
        if (!rainbow.getValue()) {
            return new float[]{0.0f, 1.0f, 0.3f};
        }

        long time = System.currentTimeMillis() - startTime;
        float hue = (time * animationSpeed.getValue() * 0.001f) % 1.0f;

        return hsvToRgb(hue, 1.0f, 1.0f);
    }

    private float[] hsvToRgb(float h, float s, float v) {
        int i = (int) (h * 6);
        float f = h * 6 - i;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);

        switch (i % 6) {
            case 0:
                return new float[]{v, t, p};
            case 1:
                return new float[]{q, v, p};
            case 2:
                return new float[]{p, v, t};
            case 3:
                return new float[]{p, q, v};
            case 4:
                return new float[]{t, p, v};
            case 5:
                return new float[]{v, p, q};
            default:
                return new float[]{1, 1, 1};
        }
    }

    private void renderAnimatedCircle(Vector3d center, float radius) {
        long time = System.currentTimeMillis() - startTime;
        float rotation = (time * animationSpeed.getValue() * 0.005f) % 360;

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3d(center.x, center.y + 0.02, center.z);

        for (int i = 0; i <= 360; i += 8) {
            double rad = Math.toRadians(i + rotation);
            double x = center.x + Math.cos(rad) * radius;
            double z = center.z + Math.sin(rad) * radius;
            GL11.glVertex3d(x, center.y + 0.02, z);
        }

        GL11.glEnd();
    }

    private void renderPulsingCircle(Vector3d center, float baseRadius) {
        long time = System.currentTimeMillis() - startTime;
        float pulse = (float) (Math.sin(time * animationSpeed.getValue() * 0.01) * 0.3 + 1.0);
        float radius = baseRadius * pulse;

        GL11.glBegin(GL11.GL_TRIANGLE_FAN);
        GL11.glVertex3d(center.x, center.y + 0.02, center.z);

        for (int i = 0; i <= 360; i += 8) {
            double rad = Math.toRadians(i);
            double x = center.x + Math.cos(rad) * radius;
            double z = center.z + Math.sin(rad) * radius;
            GL11.glVertex3d(x, center.y + 0.02, z);
        }

        GL11.glEnd();
    }

    private void renderExplosionEffect(Vector3d center, float radius) {
        long time = System.currentTimeMillis() - startTime;
        float rotation = (time * animationSpeed.getValue() * 0.01f) % 360;

        for (int ring = 0; ring < 3; ring++) {
            float ringRadius = radius * (0.5f + ring * 0.3f);
            float ringRotation = rotation + ring * 120;

            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            GL11.glVertex3d(center.x, center.y + 0.02, center.z);

            for (int i = 0; i <= 360; i += 45) {
                double rad = Math.toRadians(i + ringRotation);
                double x = center.x + Math.cos(rad) * ringRadius;
                double z = center.z + Math.sin(rad) * ringRadius;
                GL11.glVertex3d(x, center.y + 0.02, z);
            }

            GL11.glEnd();
        }
    }

    private void renderSpiralEffect(Vector3d center, float radius) {
        long time = System.currentTimeMillis() - startTime;
        float rotation = (time * animationSpeed.getValue() * 0.008f) % 360;

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        for (int i = 0; i <= 720; i += 10) {
            double rad = Math.toRadians(i + rotation);
            float spiralRadius = radius * (1.0f - (float) i / 720);

            double x1 = center.x + Math.cos(rad) * spiralRadius;
            double z1 = center.z + Math.sin(rad) * spiralRadius;
            double x2 = center.x + Math.cos(rad) * (spiralRadius * 0.8f);
            double z2 = center.z + Math.sin(rad) * (spiralRadius * 0.8f);

            GL11.glVertex3d(x1, center.y + 0.02, z1);
            GL11.glVertex3d(x2, center.y + 0.02, z2);
        }

        GL11.glEnd();
    }

    private void renderStarEffect(Vector3d center, float radius) {
        long time = System.currentTimeMillis() - startTime;
        float rotation = (time * animationSpeed.getValue() * 0.006f) % 360;

        GL11.glBegin(GL11.GL_TRIANGLES);

        for (int i = 0; i < 8; i++) {
            double angle1 = Math.toRadians(i * 45 + rotation);
            double angle2 = Math.toRadians((i + 1) * 45 + rotation);

            double x1 = center.x + Math.cos(angle1) * radius;
            double z1 = center.z + Math.sin(angle1) * radius;
            double x2 = center.x + Math.cos(angle2) * radius;
            double z2 = center.z + Math.sin(angle2) * radius;

            GL11.glVertex3d(center.x, center.y + 0.02, center.z);
            GL11.glVertex3d(x1, center.y + 0.02, z1);
            GL11.glVertex3d(x2, center.y + 0.02, z2);

            double midAngle = (angle1 + angle2) / 2;
            double xMid = center.x + Math.cos(midAngle) * radius * 0.5;
            double zMid = center.z + Math.sin(midAngle) * radius * 0.5;

            GL11.glVertex3d(x1, center.y + 0.02, z1);
            GL11.glVertex3d(xMid, center.y + 0.02, zMid);
            GL11.glVertex3d(x2, center.y + 0.02, z2);
        }

        GL11.glEnd();
    }

    private Vector3d getHandPosition(float partialTicks) {
        Entity viewEntity = mc.getRenderViewEntity();
        double x = viewEntity.lastTickPosX + (viewEntity.getPosX() - viewEntity.lastTickPosX) * partialTicks;
        double y = viewEntity.lastTickPosY + (viewEntity.getPosY() - viewEntity.lastTickPosY) * partialTicks + viewEntity.getEyeHeight();
        double z = viewEntity.lastTickPosZ + (viewEntity.getPosZ() - viewEntity.lastTickPosZ) * partialTicks;

        y -= 0.15;

        float yaw = viewEntity.rotationYaw * 0.017453292F;
        float pitch = viewEntity.rotationPitch * 0.017453292F;
        float offX = -MathHelper.sin(yaw) * MathHelper.cos(pitch) * 0.3f;
        float offZ = MathHelper.cos(yaw) * MathHelper.cos(pitch) * 0.3f;

        return new Vector3d(x + offX, y, z + offZ);
    }

    private Vector3d getLookVector() {
        float pitch = mc.player.rotationPitch;
        float yaw = mc.player.rotationYaw;
        float pitchRadians = pitch * 0.017453292F;
        float yawRadians = -yaw * 0.017453292F;

        float cosYaw = MathHelper.cos(yawRadians);
        float sinYaw = MathHelper.sin(yawRadians);
        float cosPitch = MathHelper.cos(pitchRadians);
        float sinPitch = MathHelper.sin(pitchRadians);

        return new Vector3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }

    private Vector3d calculateImpactPoint(ItemStack stack, Vector3d startPos, Vector3d lookVec) {
        float velocity = getProjectileVelocity(stack);
        Vector3d motion = lookVec.scale(velocity);
        float gravity = getProjectileGravity(stack);
        Vector3d position = startPos;
        Vector3d lastPosition;

        trajectoryPoints.clear();
        RayTraceContext.FluidMode fluidMode = RayTraceContext.FluidMode.NONE;

        for (int i = 0; i < 300; i++) {
            lastPosition = position;
            position = position.add(motion);

            if (showTrail.getValue() && i % 3 == 0 && trajectoryPoints.size() < trailLength.getValue()) {
                trajectoryPoints.add(position);
            }

            if (isInWater(position) && !(stack.getItem() instanceof TridentItem)) {
                motion = motion.scale(0.8f);
            } else {
                motion = motion.scale(0.99f);
            }

            motion = motion.add(0, -gravity, 0);

            RayTraceResult rayTrace = mc.world.rayTraceBlocks(
                    new RayTraceContext(lastPosition, position, RayTraceContext.BlockMode.COLLIDER, fluidMode, mc.player));

            if (rayTrace.getType() != RayTraceResult.Type.MISS) {
                return rayTrace.getHitVec();
            }

            if (position.y <= 0 || i == 299) {
                return position;
            }
        }

        return null;
    }

    private boolean isInWater(Vector3d position) {
        BlockPos pos = new BlockPos(position);
        return mc.world.getBlockState(pos).getBlock() == Blocks.WATER;
    }

    private float getProjectileVelocity(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof BowItem) {
            float chargeTime = Math.min(mc.player.getItemInUseMaxCount(), 20) / 20.0F;
            return (chargeTime * chargeTime + chargeTime * 2.0F) * 1.5F;
        } else if (item instanceof CrossbowItem) {
            return 3.15F;
        } else if (item instanceof TridentItem) {
            return 2.5F;
        } else if (item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderPearlItem) {
            return 1.5F;
        } else if (item instanceof PotionItem) {
            return 0.5F;
        }

        return 1.5F;
    }

    private float getProjectileGravity(ItemStack stack) {
        Item item = stack.getItem();

        if (item instanceof TridentItem) {
            return 0.05F;
        } else if (item instanceof BowItem || item instanceof CrossbowItem) {
            return 0.05F;
        } else if (item instanceof SnowballItem || item instanceof EggItem || item instanceof EnderPearlItem) {
            return 0.03F;
        } else if (item instanceof PotionItem) {
            return 0.05F;
        }

        return 0.03F;
    }
}