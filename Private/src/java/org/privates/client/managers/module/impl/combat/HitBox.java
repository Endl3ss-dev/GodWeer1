package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.annotations.Legit;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.rotation.Rotation;
import org.privates.client.managers.component.impl.rotation.RotationComponent;
import org.privates.client.managers.events.other.EntityHitBoxEvent;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.misc.SpookyFarm;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.GCDUtils;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.render.draw.Round;
import org.privates.client.utils.rotation.RotationUtil;
import org.privates.common.impl.taskript.Script;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Category2

@Legit
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "HitBox", category = Category.COMBAT, description = "Увеличивает хит бокс игрока и уменьшает вашу криворукость")
public class HitBox extends Module {
    public static HitBox getInstance() {
        return Instance.get(HitBox.class);
    }

    private final MultiBooleanSetting targets = new MultiBooleanSetting(this, "Сущности",
            BooleanSetting.of("Игроки", true),
            BooleanSetting.of("Мобы", true)
    );

    private final SliderSetting playersSize = new SliderSetting(this, "Игроки", 0.3F, 0F, 2F, 0.05F).setVisible(() -> targets.getValue("Игроки"));
    private final SliderSetting mobsSize = new SliderSetting(this, "Мобы", 0.3F, 0F, 2F, 0.05F).setVisible(() -> targets.getValue("Мобы"));

    // Render settings
    private final BooleanSetting renderHitbox = new BooleanSetting(this, "Рендер хитбокса", true);
    private final ColorSetting hitboxColor = new ColorSetting(this, "Цвет хитбокса", ColorUtil.getColor(255, 255, 255, 80)).setVisible(renderHitbox::getValue);
    private final ColorSetting hoverColor = new ColorSetting(this, "Цвет при наведении", ColorUtil.getColor(255, 85, 85, 120)).setVisible(renderHitbox::getValue);
    private final ColorSetting hitColor = new ColorSetting(this, "Цвет при ударе", ColorUtil.getColor(255, 0, 0, 200)).setVisible(renderHitbox::getValue);
    private final SliderSetting lineWidth = new SliderSetting(this, "Толщина линий", 2.0f, 0.5f, 5.0f, 0.1f).setVisible(renderHitbox::getValue);
    private final BooleanSetting filled = new BooleanSetting(this, "Заливка", true).setVisible(renderHitbox::getValue);
    
    // Animation settings
    private final BooleanSetting hitAnimation = new BooleanSetting(this, "Анимация удара", true).setVisible(renderHitbox::getValue);
    private final SliderSetting hitAnimDuration = new SliderSetting(this, "Длительность анимации", 300f, 100f, 1000f, 50f).setVisible(() -> renderHitbox.getValue() && hitAnimation.getValue());
    private final BooleanSetting hoverAnimation = new BooleanSetting(this, "Анимация наведения", true).setVisible(renderHitbox::getValue);
    private final SliderSetting hoverAnimSpeed = new SliderSetting(this, "Скорость пульсации", 2.0f, 0.5f, 5.0f, 0.5f).setVisible(() -> renderHitbox.getValue() && hoverAnimation.getValue());

    private final BooleanSetting fovMode = new BooleanSetting(this, "FOV режим", false);
    private final SliderSetting fovSize = new SliderSetting(this, "Размер FOV", 45f, 10f, 180f, 1f).setVisible(fovMode::getValue);
    private final SliderSetting correctionSpeed = new SliderSetting(this, "Скорость коррекции", 120f, 50f, 360f, 10f).setVisible(fovMode::getValue);
    private final BooleanSetting showFov = new BooleanSetting(this, "Показать FOV", true).setVisible(fovMode::getValue);
    private final ColorSetting fovColor = new ColorSetting(this, "Цвет FOV", ColorUtil.getColor(255, 255, 255, 100)).setVisible(() -> fovMode.getValue() && showFov.getValue());

    private final Script script = new Script();
    private boolean isCorrectingRotation = false;
    private long correctionStartTime = 0L;
    private LivingEntity targetEntity = null;
    private Vector2f originalRotation = null;
    
    // Render state tracking
    private final Map<LivingEntity, Long> hitTimestamps = new HashMap<>();
    private LivingEntity hoveredEntity = null;
    private final Tessellator tessellator = Tessellator.getInstance();
    private final BufferBuilder bufferBuilder = tessellator.getBuffer();

    @EventHandler
    public void onEvent(EntityHitBoxEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (targets.getValue("Игроки") && event.getEntity() instanceof PlayerEntity)
            event.setSize(playersSize.getValue());
        if (targets.getValue("Мобы") && event.getEntity() instanceof MobEntity)
            event.setSize(mobsSize.getValue());
    }

    @EventHandler
    public void onEvent(AttackEvent event) {
        if (!fovMode.getValue() || isCorrectingRotation) return;

        if (event.getTarget() instanceof LivingEntity target) {
            if (isTargetInFOV(target)) {
                event.setCancelled(true);
                startRotationCorrection(target);
            }
        }
        
        // Track hit for animation
        if (renderHitbox.getValue() && hitAnimation.getValue() && event.getTarget() instanceof LivingEntity) {
            hitTimestamps.put((LivingEntity) event.getTarget(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (!renderHitbox.getValue() || mc.world == null || mc.player == null || mc.getRenderManager() == null) {
            return;
        }

        MatrixStack matrixStack = event.getMatrix();
        float partialTicks = event.getPartialTicks();
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();

        setupRender();

        LivingEntity newHoveredEntity = null;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player || !player.isAlive()) {
                continue;
            }

            // Check if this is a valid target
            boolean isValidTarget = false;
            if (targets.getValue("Игроки") && player instanceof PlayerEntity) {
                isValidTarget = true;
            }

            if (!isValidTarget) continue;

            Vector3d playerPos = RenderUtil3D.interpolate(player, partialTicks).subtract(cameraPos);

            // Get expanded hitbox size
            float expansion = targets.getValue("Игроки") ? playersSize.getValue() : 0;
            float hw = (player.getWidth() / 2.0f) + expansion;
            float hh = player.getHeight() + (expansion * 2);

            AxisAlignedBB expandedBox = new AxisAlignedBB(
                    playerPos.x - hw, playerPos.y, playerPos.z - hw,
                    playerPos.x + hw, playerPos.y + hh, playerPos.z + hw
            );

            // Check if player is looking at this hitbox
            boolean isHovered = isLookingAtBox(expandedBox, cameraPos);
            if (isHovered) {
                newHoveredEntity = player;
            }

            // Calculate animation factors
            float hitAnimFactor = 0f;
            if (hitAnimation.getValue() && hitTimestamps.containsKey(player)) {
                long hitTime = hitTimestamps.get(player);
                long elapsed = System.currentTimeMillis() - hitTime;
                if (elapsed < hitAnimDuration.getValue()) {
                    // Pulse animation: expand then contract
                    float progress = elapsed / hitAnimDuration.getValue();
                    hitAnimFactor = (float) (Math.sin(progress * Math.PI) * 0.15); // 0 -> 0.15 -> 0
                } else {
                    hitTimestamps.remove(player);
                }
            }

            float hoverAnimFactor = 0f;
            if (hoverAnimation.getValue() && isHovered) {
                // Pulsing alpha animation
                float time = System.currentTimeMillis() / 1000f;
                hoverAnimFactor = (float) (Math.sin(time * hoverAnimSpeed.getValue()) * 0.5 + 0.5); // 0 -> 1
            }

            // Determine color based on state
            int baseColor;
            float baseAlpha;
            if (hitAnimFactor > 0) {
                baseColor = hitColor.getValue();
                baseAlpha = ((baseColor >> 24) & 0xFF) / 255.0f;
            } else if (isHovered) {
                baseColor = hoverColor.getValue();
                baseAlpha = ((baseColor >> 24) & 0xFF) / 255.0f;
                // Apply hover animation to alpha
                baseAlpha = baseAlpha * (0.7f + hoverAnimFactor * 0.3f);
            } else {
                baseColor = hitboxColor.getValue();
                baseAlpha = ((baseColor >> 24) & 0xFF) / 255.0f;
            }

            float red = ((baseColor >> 16) & 0xFF) / 255.0f;
            float green = ((baseColor >> 8) & 0xFF) / 255.0f;
            float blue = (baseColor & 0xFF) / 255.0f;

            // Apply hit animation expansion
            float animHw = hw + (hw * hitAnimFactor);
            float animHh = hh + (hh * hitAnimFactor);

            matrixStack.push();
            matrixStack.translate(playerPos.x, playerPos.y, playerPos.z);

            // Draw filled box
            if (filled.getValue()) {
                bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderFilledBox(bufferBuilder, matrixStack, -animHw, 0, -animHw, animHw, animHh, animHw, red, green, blue, baseAlpha);
                tessellator.draw();
            }

            // Draw wireframe
            GL11.glLineWidth(lineWidth.getValue());
            bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderWireframeBox(bufferBuilder, matrixStack, -animHw, 0, -animHw, animHw, animHh, animHw, red, green, blue, 1.0f);
            tessellator.draw();

            matrixStack.pop();
        }

        // Handle mobs if enabled
        if (targets.getValue("Мобы")) {
            for (LivingEntity entity : mc.world.getLoadedEntitiesWithinAABB(MobEntity.class, mc.player.getBoundingBox().grow(20))) {
                if (!entity.isAlive()) continue;

                Vector3d entityPos = RenderUtil3D.interpolate(entity, partialTicks).subtract(cameraPos);

                float expansion = mobsSize.getValue();
                float hw = (entity.getWidth() / 2.0f) + expansion;
                float hh = entity.getHeight() + (expansion * 2);

                AxisAlignedBB expandedBox = new AxisAlignedBB(
                        entityPos.x - hw, entityPos.y, entityPos.z - hw,
                        entityPos.x + hw, entityPos.y + hh, entityPos.z + hw
                );

                boolean isHovered = isLookingAtBox(expandedBox, cameraPos);
                if (isHovered) {
                    newHoveredEntity = entity;
                }

                // Calculate animation factors
                float hitAnimFactor = 0f;
                if (hitAnimation.getValue() && hitTimestamps.containsKey(entity)) {
                    long hitTime = hitTimestamps.get(entity);
                    long elapsed = System.currentTimeMillis() - hitTime;
                    if (elapsed < hitAnimDuration.getValue()) {
                        float progress = elapsed / hitAnimDuration.getValue();
                        hitAnimFactor = (float) (Math.sin(progress * Math.PI) * 0.15);
                    } else {
                        hitTimestamps.remove(entity);
                    }
                }

                float hoverAnimFactor = 0f;
                if (hoverAnimation.getValue() && isHovered) {
                    float time = System.currentTimeMillis() / 1000f;
                    hoverAnimFactor = (float) (Math.sin(time * hoverAnimSpeed.getValue()) * 0.5 + 0.5);
                }

                int baseColor;
                float baseAlpha;
                if (hitAnimFactor > 0) {
                    baseColor = hitColor.getValue();
                    baseAlpha = ((baseColor >> 24) & 0xFF) / 255.0f;
                } else if (isHovered) {
                    baseColor = hoverColor.getValue();
                    baseAlpha = ((baseColor >> 24) & 0xFF) / 255.0f;
                    baseAlpha = baseAlpha * (0.7f + hoverAnimFactor * 0.3f);
                } else {
                    baseColor = hitboxColor.getValue();
                    baseAlpha = ((baseColor >> 24) & 0xFF) / 255.0f;
                }

                float red = ((baseColor >> 16) & 0xFF) / 255.0f;
                float green = ((baseColor >> 8) & 0xFF) / 255.0f;
                float blue = (baseColor & 0xFF) / 255.0f;

                float animHw = hw + (hw * hitAnimFactor);
                float animHh = hh + (hh * hitAnimFactor);

                matrixStack.push();
                matrixStack.translate(entityPos.x, entityPos.y, entityPos.z);

                if (filled.getValue()) {
                    bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                    RenderUtil3D.renderFilledBox(bufferBuilder, matrixStack, -animHw, 0, -animHw, animHw, animHh, animHw, red, green, blue, baseAlpha);
                    tessellator.draw();
                }

                GL11.glLineWidth(lineWidth.getValue());
                bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderWireframeBox(bufferBuilder, matrixStack, -animHw, 0, -animHw, animHw, animHh, animHw, red, green, blue, 1.0f);
                tessellator.draw();

                matrixStack.pop();
            }
        }

        releaseRender();
        hoveredEntity = newHoveredEntity;
    }

    private boolean isLookingAtBox(AxisAlignedBB box, Vector3d cameraPos) {
        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;

        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        float cosYaw = MathHelper.cos(-yawRad - (float) Math.PI);
        float sinYaw = MathHelper.sin(-yawRad - (float) Math.PI);
        float cosPitch = -MathHelper.cos(-pitchRad);
        float sinPitch = MathHelper.sin(-pitchRad);

        Vector3d lookVec = new Vector3d(
                sinYaw * cosPitch,
                sinPitch,
                cosYaw * cosPitch
        );

        Vector3d eyePos = mc.player.getEyePosition(1.0f).subtract(cameraPos);
        double maxDistance = 20.0;
        Vector3d endVec = eyePos.add(lookVec.scale(maxDistance));

        return box.rayTrace(eyePos, endVec).isPresent();
    }

    private void setupRender() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
    }

    private void releaseRender() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        if (!fovMode.getValue() || !showFov.getValue()) return;

        MatrixStack matrix = event.getMatrix();

        float centerX = mc.getMainWindow().getScaledWidth() / 2f;
        float centerY = mc.getMainWindow().getScaledHeight() / 2f;
        float fovRadius = fovSize.getValue() * 2f;

        int color = fovColor.getValue();
        int alpha = isCorrectingRotation ? ColorUtil.replAlpha(color, 0.6f) : color;

        RenderUtil.Rounded.roundedOutline(matrix,
                centerX - fovRadius, centerY - fovRadius,
                fovRadius * 2f, fovRadius * 2f,
                2f, alpha, Round.of(fovRadius));

        if (isCorrectingRotation && targetEntity != null) {
            Vector3d targetPos = targetEntity.getBoundingBox().getCenter();
            Vector3d playerEye = mc.player.getEyePosition(1.0f);
            Vector3d direction = targetPos.subtract(playerEye).normalize();

            float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
            float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y,
                    Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

            float yawDiff = MathHelper.wrapDegrees(targetYaw - mc.player.rotationYaw);
            float pitchDiff = targetPitch - mc.player.rotationPitch;

            float screenX = centerX + yawDiff * 3f;
            float screenY = centerY + pitchDiff * 3f;

            RenderUtil.Rounded.roundedRect(matrix, screenX - 3, screenY - 3, 6, 6,
                    ColorUtil.getColor(255, 0, 0), Round.of(3));
        }
    }


    private boolean isTargetInFOV(LivingEntity entity) {
        if (entity == null || mc.player == null) return false;

        Vector3d playerEye = mc.player.getEyePosition(1.0f);
        Vector3d targetCenter = entity.getBoundingBox().getCenter();
        Vector3d direction = targetCenter.subtract(playerEye).normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y,
                Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float yawDiff = Math.abs(MathHelper.wrapDegrees(targetYaw - mc.player.rotationYaw));
        float pitchDiff = Math.abs(targetPitch - mc.player.rotationPitch);

        float fovRadius = fovSize.getValue();

        return yawDiff <= fovRadius && pitchDiff <= fovRadius;
    }


    private void startRotationCorrection(LivingEntity target) {
        if (isCorrectingRotation || target == null) return;

        isCorrectingRotation = true;
        correctionStartTime = System.currentTimeMillis();
        targetEntity = target;
        originalRotation = new Vector2f(mc.player.rotationYaw, mc.player.rotationPitch);

        performRotationCorrection();
    }


    private void performRotationCorrection() {
        if (!isCorrectingRotation || targetEntity == null) return;

        SpookyFarm auraNew = SpookyFarm.getInstance();
        if (auraNew == null || !auraNew.isEnabled()) {
            isCorrectingRotation = false;
            return;
        }


        script.cleanup().addTickStep(2, () -> {
            if (targetEntity != null && targetEntity.isAlive() &&
                    mc.player.getDistance(targetEntity) <= 6.0f) {

                mc.playerController.attackEntity(mc.player, targetEntity);
                mc.player.swingArm(net.minecraft.util.Hand.MAIN_HAND);
            }

            script.cleanup().addTickStep(3, () -> {
                isCorrectingRotation = false;
                targetEntity = null;
                originalRotation = null;
            });
        });
    }


    private void performFuntimeRotation() {
        Vector2f targetRotation = RotationUtil.calculate(targetEntity);

        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float yawDelta = MathHelper.wrapDegrees(targetRotation.x - currentYaw);
        float pitchDelta = targetRotation.y - currentPitch;

        float speed = 0.4f;
        float newYaw = currentYaw + yawDelta * speed;
        float newPitch = currentPitch + pitchDelta * speed;

        float jitterYaw = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.3f;
        float jitterPitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.2f;

        newYaw += jitterYaw;
        newPitch += jitterPitch;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -90f, 90f));

        RotationComponent.update(new Rotation(newYaw, newPitch), 360, 300, 0, 5);
    }


    private void performGrimRotation() {
        Vector3d targetPos = targetEntity.getPositionVec().add(0, targetEntity.getEyeHeight() * 0.85, 0);
        Vector3d playerEyes = mc.player.getEyePosition(1.0F);
        Vector3d toTarget = targetPos.subtract(playerEyes).normalize();

        float rawYaw = (float) Math.toDegrees(Math.atan2(toTarget.z, toTarget.x)) - 90.0F;
        float rawPitch = (float) -Math.toDegrees(Math.atan2(toTarget.y, Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z)));

        long time = System.currentTimeMillis();
        float microTime = (time % 2000) / 2000.0f;
        double microPhase = Math.sin(microTime * Math.PI * 2) * 0.35;

        float deviationYaw = (float) (microPhase * 1.1);
        float deviationPitch = (float) (microPhase * 0.7);

        float finalYaw = rawYaw + deviationYaw;
        float finalPitch = MathHelper.clamp(rawPitch + deviationPitch, -90.0F, 90.0F);

        finalYaw = GCDUtils.getSensitivity(finalYaw);
        finalPitch = GCDUtils.getSensitivity(finalPitch);

        RotationComponent.update(new Rotation(finalYaw, finalPitch), 180, 120, 0, 3);
    }


    private void performHWRotation() {
        Vector3d targetPos = targetEntity.getPositionVec().add(0, targetEntity.getHeight() * 0.5, 0);
        Vector3d playerEye = mc.player.getEyePosition(1.0F);
        Vector3d direction = targetPos.subtract(playerEye);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float timeMultiplier = 0.001f;
        targetYaw += (float) (Math.sin(System.currentTimeMillis() * timeMultiplier) * 0.8);
        targetPitch += (float) (Math.cos(System.currentTimeMillis() * timeMultiplier) * 0.5);

        float baseYawSpeed = 0.2f + (float) (Math.random() * 0.05f);
        float basePitchSpeed = 0.15f + (float) (Math.random() * 0.03f);

        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float newYaw = currentYaw + MathHelper.wrapDegrees(targetYaw - currentYaw) * baseYawSpeed;
        float newPitch = currentPitch + (targetPitch - currentPitch) * basePitchSpeed;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -90, 90));

        RotationComponent.update(new Rotation(newYaw, newPitch), 180, 110, 0, 4);
    }


    private void performSmoothRotation() {
        Vector3d targetCenter = targetEntity.getBoundingBox().getCenter();
        Vector3d playerEye = mc.player.getEyePosition(1.0f);
        Vector3d direction = targetCenter.subtract(playerEye);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float smoothFactor = 0.3f;
        float currentYaw = mc.player.rotationYaw;
        float currentPitch = mc.player.rotationPitch;

        float newYaw = currentYaw + MathHelper.wrapDegrees(targetYaw - currentYaw) * smoothFactor;
        float newPitch = currentPitch + (targetPitch - currentPitch) * smoothFactor;

        newYaw = GCDUtils.getSensitivity(newYaw);
        newPitch = GCDUtils.getSensitivity(MathHelper.clamp(newPitch, -90f, 90f));

        RotationComponent.update(new Rotation(newYaw, newPitch), 180, 180, 0, 3);
    }


    private void performDefaultRotation() {
        Vector3d targetCenter = targetEntity.getBoundingBox().getCenter();
        Vector3d playerEye = mc.player.getEyePosition(1.0f);
        Vector3d direction = targetCenter.subtract(playerEye);

        float targetYaw = (float) Math.toDegrees(Math.atan2(direction.z, direction.x)) - 90.0f;
        float targetPitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        float jitterYaw = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.2f;
        float jitterPitch = (ThreadLocalRandom.current().nextFloat() - 0.5f) * 0.15f;

        targetYaw += jitterYaw;
        targetPitch += jitterPitch;

        targetYaw = GCDUtils.getSensitivity(targetYaw);
        targetPitch = GCDUtils.getSensitivity(MathHelper.clamp(targetPitch, -90f, 90f));

        RotationComponent.update(new Rotation(targetYaw, targetPitch), correctionSpeed.getValue(), correctionSpeed.getValue() * 0.8f, 0, 5);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        isCorrectingRotation = false;
        targetEntity = null;
        originalRotation = null;
        script.cleanup();
        hitTimestamps.clear();
        hoveredEntity = null;
    }
}