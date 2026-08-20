package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.other.TickEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.*;
import org.privates.client.utils.other.Instance;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Category3
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "WavePlayer", category = Category.RENDER, description = "Запоминает позу при тотеме и рисует летящий призрак")
public class WavePlayer extends Module {

    public static WavePlayer getInstance() {
        return Instance.get(WavePlayer.class);
    }

    final SliderSetting riseHeight = new SliderSetting(this, "Высота подъема", 4f, 0.2f, 5.0f, 0.1f);
    final SliderSetting duration = new SliderSetting(this, "Время жизни", 3f, 0.2f, 6.0f, 0.1f);
    final ColorSetting color = new ColorSetting(this, "Цвет", 0xFF9F7A99);

    final ModeSetting direction = new ModeSetting(this, "Направление", "Вверх", "Насквозь", "Вправо", "Влево", "По прицелу");

    final MultiBooleanSetting bodyParts = new MultiBooleanSetting(this, "Части тела",
            BooleanSetting.of("Голова", true),
            BooleanSetting.of("Туловище", true),
            BooleanSetting.of("Руки", true),
            BooleanSetting.of("Ноги", true));

    final BooleanSetting onTotem = new BooleanSetting(this, "При тотеме", true);
    final BooleanSetting onJump = new BooleanSetting(this, "При прыжке", false);
    final BooleanSetting onWalk = new BooleanSetting(this, "При ходьбе", false);
    final SliderSetting walkInterval = new SliderSetting(this, "Интервал ходьбы", 0.6f, 0.1f, 3.0f, 0.1f)
            .setVisible(onWalk::getValue);

    final ModeSetting targets = new ModeSetting(this, "Для кого", "Оба", "У меня", "У игроков", "Оба");
    final SliderSetting playersRadius = new SliderSetting(this, "Радиус игроков", 8.0f, 1.0f, 64.0f, 1.0f)
            .setVisible(() -> targets.is("У игроков") || targets.is("Оба"));

    final List<TotemGhost> ghosts = new CopyOnWriteArrayList<>();

    boolean wasOnGround;
    long lastWalkSpawnMs;
    final Map<UUID, PlayerTrack> otherTracks = new HashMap<>();

    final Tessellator tessellator = Tessellator.getInstance();
    final BufferBuilder bufferBuilder = tessellator.getBuffer();

    static final float U = 1.0f / 16.0f;

    @Override
    public void onDisable() {
        super.onDisable();
        ghosts.clear();
        wasOnGround = false;
        lastWalkSpawnMs = 0L;
        otherTracks.clear();
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (mc.world == null || !onTotem.getValue()) return;
        IPacket<?> packet = event.getPacket();
        if (!(packet instanceof SEntityStatusPacket statusPacket)) return;
        if (statusPacket.getOpCode() != 35) return;

        Entity e = statusPacket.getEntity(mc.world);
        if (!(e instanceof AbstractClientPlayerEntity player)) return;
        if (!shouldProcess(player)) return;
        addGhost(player, true);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        long now = System.currentTimeMillis();
        long intervalMs = (long) (walkInterval.getValue() * 1000.0f);
        if (intervalMs < 1L) intervalMs = 1L;

        if (shouldProcessSelf()) {
            boolean onGround = mc.player.isOnGround();

            if (onJump.getValue()) {
                boolean jumpedThisTick = wasOnGround && !onGround && mc.player.getMotion().y > 0.0D;
                if (jumpedThisTick) {
                    addGhost(mc.player, true);
                }
            }

            if (onWalk.getValue()) {
                if (onJump.getValue() && !onGround) {
                    lastWalkSpawnMs = 0L;
                } else {
                    Vector3d v = mc.player.getMotion();
                    double horizontalSpeed = Math.sqrt(v.x * v.x + v.z * v.z);
                    boolean moving = horizontalSpeed > 0.02D;

                    if (moving) {
                        if (lastWalkSpawnMs == 0L) lastWalkSpawnMs = now;
                        if (now - lastWalkSpawnMs >= intervalMs) {
                            lastWalkSpawnMs = now;
                            addGhost(mc.player, false);
                        }
                    } else {
                        lastWalkSpawnMs = 0L;
                    }
                }
            }

            wasOnGround = onGround;
        }

        if (shouldProcessOthers()) {
            updateOtherPlayers(now, intervalMs);
        } else {
            otherTracks.clear();
        }
    }

    void updateOtherPlayers(long now, long intervalMs) {
        if (mc.player == null || mc.world == null) return;

        double radius = playersRadius.getValue();
        double radiusSq = radius * radius;

        otherTracks.entrySet().removeIf(e -> {
            for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
                if (p == mc.player) continue;
                if (p.getUniqueID().equals(e.getKey()) && !p.removed && mc.player.getDistanceSq(p) <= radiusSq) {
                    return false;
                }
            }
            return true;
        });

        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.removed) continue;
            if (mc.player.getDistanceSq(p) > radiusSq) continue;

            PlayerTrack track = otherTracks.computeIfAbsent(p.getUniqueID(), k -> new PlayerTrack());
            boolean onGround = p.isOnGround();

            if (onJump.getValue()) {
                boolean jumpedThisTick = track.wasOnGround && !onGround && p.getMotion().y > 0.0D;
                if (jumpedThisTick) addGhost(p, true);
            }

            if (onWalk.getValue()) {
                if (onJump.getValue() && !onGround) {
                    track.lastWalkSpawnMs = 0L;
                } else {
                    double dx = p.getPosX() - track.lastX;
                    double dz = p.getPosZ() - track.lastZ;
                    double delta = Math.sqrt(dx * dx + dz * dz);
                    boolean moving = delta > 0.003D;

                    if (moving) {
                        if (track.lastWalkSpawnMs == 0L) track.lastWalkSpawnMs = now;
                        if (now - track.lastWalkSpawnMs >= intervalMs) {
                            track.lastWalkSpawnMs = now;
                            addGhost(p, false);
                        }
                    } else {
                        track.lastWalkSpawnMs = 0L;
                    }
                }
            }

            track.wasOnGround = onGround;
            track.lastX = p.getPosX();
            track.lastZ = p.getPosZ();
        }
    }

    boolean shouldProcess(AbstractClientPlayerEntity player) {
        if (mc.player == null) return false;
        if (player == mc.player) return shouldProcessSelf();
        return shouldProcessOthers();
    }

    boolean shouldProcessSelf() {
        return targets.is("У меня") || targets.is("Оба");
    }

    boolean shouldProcessOthers() {
        return targets.is("У игроков") || targets.is("Оба");
    }

    void addGhost(AbstractClientPlayerEntity player, boolean rising) {
        if (mc.world == null) return;
        Vector3d pos = player.getPositionVec();
        float bodyYaw = player.renderYawOffset;
        boolean sneaking = player.isSneaking();
        float walkPhase = player.ticksExisted;
        Vector3d mot = player.getMotion();
        float horizontalSpeed = (float) Math.sqrt(mot.x * mot.x + mot.z * mot.z);
        float walkSpeed = MathHelper.clamp(horizontalSpeed * 8.0f, 0.0f, 1.0f);

        // Calculate direction based on setting
        Vector3d dir = calculateDirection(player);

        ghosts.add(new TotemGhost(pos, bodyYaw, sneaking, walkPhase, walkSpeed, rising, System.currentTimeMillis(), dir));
    }

    Vector3d calculateDirection(AbstractClientPlayerEntity player) {
        String dirMode = direction.getValue();

        switch (dirMode) {
            case "Вверх":
                return new Vector3d(0, 1, 0);

            case "Насквозь":
                // Through the player - from attacker to target
                if (mc.player != null && player != mc.player) {
                    Vector3d toTarget = player.getPositionVec().subtract(mc.player.getPositionVec()).normalize();
                    return toTarget;
                }
                return new Vector3d(0, 1, 0);

            case "Вправо":
                // Right relative to player's facing
                double yawRad = Math.toRadians(player.renderYawOffset + 90);
                return new Vector3d(-Math.sin(yawRad), 0, Math.cos(yawRad));

            case "Влево":
                // Left relative to player's facing
                double yawRadLeft = Math.toRadians(player.renderYawOffset - 90);
                return new Vector3d(-Math.sin(yawRadLeft), 0, Math.cos(yawRadLeft));

            case "По прицелу":
                // Based on where attacker is aiming relative to player center
                if (mc.player != null && player != mc.player) {
                    Vector3d targetCenter = player.getPositionVec().add(0, player.getEyeHeight() / 2, 0);
                    Vector3d playerEye = mc.player.getEyePosition(1.0f);
                    Vector3d lookVec = mc.player.getLookVec();

                    // Find closest point on look ray to target center
                    Vector3d toTarget = targetCenter.subtract(playerEye);
                    double projection = toTarget.dotProduct(lookVec);
                    Vector3d closestPoint = playerEye.add(lookVec.scale(projection));

                    // Direction from target center to closest point
                    Vector3d aimOffset = closestPoint.subtract(targetCenter);

                    // If aiming right of center, go right; if left, go left
                    if (Math.abs(aimOffset.x) > Math.abs(aimOffset.z)) {
                        return aimOffset.x > 0 ? new Vector3d(1, 0, 0) : new Vector3d(-1, 0, 0);
                    } else {
                        return aimOffset.z > 0 ? new Vector3d(0, 0, 1) : new Vector3d(0, 0, -1);
                    }
                }
                return new Vector3d(0, 1, 0);

            default:
                return new Vector3d(0, 1, 0);
        }
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (ghosts.isEmpty() || mc.world == null) return;
        renderGhosts(event);
    }

    void renderGhosts(Render3DPosedEvent event) {
        Vector3d cameraPos = mc.getRenderManager().info.getProjectedView();
        long now = System.currentTimeMillis();
        List<TotemGhost> toRemove = new ArrayList<>();

        setupRender();

        for (TotemGhost ghost : ghosts) {
            float progress = (now - ghost.startTime) / (duration.getValue() * 1000.0f);
            if (progress >= 1.0f) {
                toRemove.add(ghost);
                continue;
            }

            double motionY = ghost.rising ? (riseHeight.getValue() * ease(progress)) : 0.0D;

            // Apply direction-based motion
            Vector3d motion = ghost.direction.scale(ghost.rising ? (riseHeight.getValue() * ease(progress)) : (riseHeight.getValue() * 0.3 * ease(progress)));
            double renderX = ghost.position.x - cameraPos.x + motion.x;
            double renderY = ghost.position.y - cameraPos.y + motion.y;
            double renderZ = ghost.position.z - cameraPos.z + motion.z;

            float alpha = (float) easeOutAlpha(progress);

            int col = resolveColor();
            float r = ((col >> 16) & 0xFF) / 255f;
            float g = ((col >> 8) & 0xFF) / 255f;
            float b = (col & 0xFF) / 255f;

            MatrixStack matrices = event.getMatrix();
            matrices.push();

            matrices.translate(renderX, renderY, renderZ);
            matrices.rotate(Vector3f.YP.rotationDegrees(180.0f - ghost.bodyYaw));

            float effectiveAlpha = MathHelper.clamp(alpha, 0.0f, 0.75f);

            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            renderFlatHumanoid(matrices, r, g, b, effectiveAlpha, ghost.sneaking, ghost.walkPhase, ghost.walkSpeed);
            tessellator.draw();

            matrices.pop();
        }

        releaseRender();

        if (!toRemove.isEmpty()) ghosts.removeAll(toRemove);
    }

    int resolveColor() {
        int c = color.getValue();
        if (c == 0xFF9F7A99 && Theme.getInstance() != null) {
            return Theme.getInstance().clientColor();
        }
        return c;
    }

    void renderFlatHumanoid(MatrixStack matrices, float r, float g, float b, float a,
                            boolean sneaking, float walkPhase, float walkSpeed) {
        matrices.push();
        matrices.scale(-1f, -1f, 1f);
        matrices.translate(0.0D, -1.501D, 0.0D);

        if (sneaking) {
            matrices.translate(0.0D, 0.2D, 0.0D);
            matrices.rotate(Vector3f.XP.rotationDegrees(28.0f));
        }

        Matrix4f matrix = matrices.getLast().getMatrix();

        // Render body parts based on settings
        if (bodyParts.getValue("Туловище")) {
            drawBox(matrix, -4 * U, 0, -2 * U, 8 * U, 12 * U, 4 * U, r, g, b, a);
        }
        if (bodyParts.getValue("Голова")) {
            drawBox(matrix, -4 * U, -8 * U, -4 * U, 8 * U, 8 * U, 8 * U, r, g, b, a);
        }

        float swing = MathHelper.sin(walkPhase * 0.6662f) * 1.4f * walkSpeed;

        if (bodyParts.getValue("Руки")) {
            renderArm(matrices, r, g, b, a, true, -swing);
            renderArm(matrices, r, g, b, a, false, swing);
        }
        if (bodyParts.getValue("Ноги")) {
            renderLeg(matrices, r, g, b, a, true, swing);
            renderLeg(matrices, r, g, b, a, false, -swing);
        }

        matrices.pop();
    }

    void renderArm(MatrixStack matrices, float r, float g, float b, float a,
                   boolean left, float pitchRad) {
        matrices.push();
        float pivotX = (left ? -6.0f : 6.0f) * U;
        float pivotY = 2.0f * U;
        matrices.translate(pivotX, pivotY, 0.0f);
        matrices.rotate(Vector3f.XP.rotation(pitchRad));
        matrices.translate(-pivotX, -pivotY, 0.0f);
        Matrix4f matrix = matrices.getLast().getMatrix();
        float boxX = (left ? -8.0f : 4.0f) * U;
        drawBox(matrix, boxX, -2 * U, -2 * U, 4 * U, 12 * U, 4 * U, r, g, b, a);
        matrices.pop();
    }

    void renderLeg(MatrixStack matrices, float r, float g, float b, float a,
                   boolean left, float pitchRad) {
        matrices.push();
        float pivotX = (left ? -2.0f : 2.0f) * U;
        float pivotY = 12.0f * U;
        matrices.translate(pivotX, pivotY, 0.0f);
        matrices.rotate(Vector3f.XP.rotation(pitchRad));
        matrices.translate(-pivotX, -pivotY, 0.0f);
        Matrix4f matrix = matrices.getLast().getMatrix();
        float boxX = (left ? -4.0f : 0.0f) * U;
        drawBox(matrix, boxX, 12 * U, -2 * U, 4 * U, 12 * U, 4 * U, r, g, b, a);
        matrices.pop();
    }

    void drawBox(Matrix4f matrix, float x, float y, float z,
                 float sx, float sy, float sz,
                 float r, float g, float b, float a) {
        float x2 = x + sx, y2 = y + sy, z2 = z + sz;

        bufferBuilder.pos(matrix, x, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z2).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x2, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x2, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z2).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x, y2, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y2, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y2, z).color(r, g, b, a).endVertex();

        bufferBuilder.pos(matrix, x, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y, z).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x2, y, z2).color(r, g, b, a).endVertex();
        bufferBuilder.pos(matrix, x, y, z2).color(r, g, b, a).endVertex();
    }

    double ease(double t) {
        t = MathHelper.clamp(t, 0.0D, 0.75D);
        return 1.0D - Math.pow(1.0D - t, 3);
    }

    double easeOutAlpha(double t) {
        t = MathHelper.clamp(t, 0.0D, 1.0D);
        double invT = 1.0D - t;
        return 0.75D * Math.pow(invT, 3);
    }

    void setupRender() {
        RenderSystem.pushMatrix();
        RenderSystem.disableTexture();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
    }

    void releaseRender() {
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableTexture();
        RenderSystem.popMatrix();
    }

    static class PlayerTrack {
        boolean wasOnGround;
        long lastWalkSpawnMs;
        double lastX, lastZ;
    }

    record TotemGhost(Vector3d position, float bodyYaw, boolean sneaking, float walkPhase, float walkSpeed,
                      boolean rising, long startTime, Vector3d direction) {
    }
}