package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.network.play.server.SEntityTeleportPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.impl.target.TargetComponent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Category1

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(
        name = "Backtrack",
        category = Category.COMBAT,
        description = "Задерживает хитбокс врага в прошлой позиции для легкого попадания"
)
public class BackTrack extends Module {

    public static BackTrack getInstance() {
        return Instance.get(BackTrack.class);
    }

    private final SliderSetting lagTime = new SliderSetting(this, "Lag Time", 200, 50, 1000, 10);
    private final SliderSetting maxDelay = new SliderSetting(this, "Max Delay", 500, 100, 1000, 50);
    private final BooleanSetting showLagHitbox = new BooleanSetting(this, "Show Lag Hitbox", true);
    private final BooleanSetting releaseOnAttack = new BooleanSetting(this, "Release On Attack", true);
    private final BooleanSetting onlyOnTarget = new BooleanSetting(this, "Only On Target", true);
    private final SliderSetting searchRange = new SliderSetting(this, "Search Range", 6.0f, 3.0f, 10.0f, 0.5f);

    private final Queue<PacketData> delayedPackets = new ConcurrentLinkedQueue<>();
    private LivingEntity currentTarget = null;
    private Vector3d lagPosition = null;
    private AxisAlignedBB lagHitbox = null;
    private BlockPos lagBlockPos = null;
    private long lastPacketTime = 0;
    private boolean isReleasing = false;

    private static class PacketData {
        IPacket<?> packet;
        long timestamp;
        int entityId;

        PacketData(IPacket<?> packet, long timestamp, int entityId) {
            this.packet = packet;
            this.timestamp = timestamp;
            this.entityId = entityId;
        }
    }

    @Override
    public void onEnable() {
        try {
            delayedPackets.clear();
            currentTarget = null;
            lagPosition = null;
            lagHitbox = null;
            lagBlockPos = null;
            lastPacketTime = 0;
            isReleasing = false;
        } catch (Exception e) {
        }
    }

    @Override
    public void onDisable() {
        try {
            releasePackets();
            currentTarget = null;
            lagPosition = null;
            lagHitbox = null;
            lagBlockPos = null;
        } catch (Exception e) {
            delayedPackets.clear();
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        try {
            if (isReleasing || mc.player == null || mc.world == null) return;

            if (event.isReceive()) {
                IPacket<?> packet = event.getPacket();

                if (packet instanceof SEntityPacket || packet instanceof SEntityTeleportPacket) {
                    int entityId = getEntityId(packet);

                    if (onlyOnTarget.getValue()) {
                        if (currentTarget != null && entityId == currentTarget.getEntityId()) {
                            if (lagPosition == null) {
                                lagPosition = currentTarget.getPositionVec();
                                lagHitbox = currentTarget.getBoundingBox();
                                lagBlockPos = new BlockPos(lagPosition);
                            }

                            delayedPackets.add(new PacketData(packet, System.currentTimeMillis(), entityId));
                            event.setCancelled(true);
                            lastPacketTime = System.currentTimeMillis();
                        }
                    } else {
                        delayedPackets.add(new PacketData(packet, System.currentTimeMillis(), entityId));
                        event.setCancelled(true);
                        lastPacketTime = System.currentTimeMillis();
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        try {
            if (mc.player == null || mc.world == null) return;

            currentTarget = TargetComponent.getTarget(searchRange.getValue());

            if (currentTarget == null) {
                if (!delayedPackets.isEmpty()) {
                    releasePackets();
                }
                lagPosition = null;
                lagHitbox = null;
                lagBlockPos = null;
                return;
            }

            long currentTime = System.currentTimeMillis();

            if (releaseOnAttack.getValue()) {
                AuraNew aura = AuraNew.getInstance();
                if (aura != null && aura.isEnabled() && currentTime - aura.lastAttackTime < 50) {
                    releasePackets();
                    return;
                }
            }

            delayedPackets.removeIf(packetData -> {
                long age = currentTime - packetData.timestamp;
                if (age >= Math.min(lagTime.getValue().intValue(), maxDelay.getValue().intValue())) {
                    processPacket(packetData.packet);
                    return true;
                }
                return false;
            });

        } catch (Exception e) {
        }
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (!showLagHitbox.getValue() || !isEnabled() || lagHitbox == null || lagBlockPos == null || mc.player == null || mc.world == null)
            return;

        try {
            boolean isGreen = delayedPackets.isEmpty();
            int fillColor = isGreen ? ColorUtil.getColor(50, 255, 50, 180) : ColorUtil.getColor(255, 50, 50, 180);
            int outlineColor = isGreen ? ColorUtil.getColor(50, 255, 50, 255) : ColorUtil.getColor(255, 50, 50, 255);

            MatrixStack matrixStack = event.getMatrix();
            matrixStack.push();
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.getLast().getMatrix());

            double renderX = mc.getRenderManager().renderPosX();
            double renderY = mc.getRenderManager().renderPosY();
            double renderZ = mc.getRenderManager().renderPosZ();
            RenderSystem.translated(-renderX, -renderY, -renderZ);

            RenderSystem.disableTexture();
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.disableCull();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            RenderSystem.lineWidth(2.0f);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();

            double x = lagHitbox.minX;
            double y = lagHitbox.minY;
            double z = lagHitbox.minZ;
            double width = lagHitbox.getXSize();
            double height = lagHitbox.getYSize();
            double depth = lagHitbox.getZSize();

            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderFilledBox(bufferBuilder, x, y, z, x + width, y + height, z + depth, fillColor);
            tessellator.draw();

            bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            RenderUtil3D.renderWireframeBox(bufferBuilder, x, y, z, x + width, y + height, z + depth, outlineColor);
            tessellator.draw();

            for (int i = 0; i < 3; i++) {
                BlockPos pos = lagBlockPos.add(0, i, 0);
                double minX = pos.getX();
                double minY = pos.getY();
                double minZ = pos.getZ();
                double maxX = pos.getX() + 1;
                double maxY = pos.getY() + 1;
                double maxZ = pos.getZ() + 1;

                int blockFillColor = isGreen ? ColorUtil.getColor(50, 255, 50, 100) : ColorUtil.getColor(255, 50, 50, 100);
                int blockOutlineColor = isGreen ? ColorUtil.getColor(50, 255, 50, 200) : ColorUtil.getColor(255, 50, 50, 200);

                bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderFilledBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, blockFillColor);
                tessellator.draw();

                bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderWireframeBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, blockOutlineColor);
                tessellator.draw();
            }

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            RenderSystem.enableCull();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            RenderSystem.popMatrix();
            matrixStack.pop();

        } catch (Exception e) {
        }
    }

    private void releasePackets() {
        if (isReleasing) return;

        try {
            isReleasing = true;

            if (delayedPackets.isEmpty()) {
                return;
            }

            for (PacketData packetData : delayedPackets) {
                processPacket(packetData.packet);
            }

            delayedPackets.clear();
            lagPosition = null;
            lagHitbox = null;
            lagBlockPos = null;

        } catch (Exception e) {
        } finally {
            isReleasing = false;
        }
    }

    private void processPacket(IPacket<?> packet) {
        try {
            if (mc.getConnection() != null) {
                if (packet instanceof SEntityPacket) {
                    mc.getConnection().handleEntityMovement((SEntityPacket) packet);
                } else if (packet instanceof SEntityTeleportPacket) {
                    mc.getConnection().handleEntityTeleport((SEntityTeleportPacket) packet);
                }
            }
        } catch (Exception e) {
        }
    }

    private int getEntityId(IPacket<?> packet) {
        try {
            if (packet instanceof SEntityPacket) {
                return ((SEntityPacket) packet).getEntity(mc.world).getEntityId();
            } else if (packet instanceof SEntityTeleportPacket) {
                return ((SEntityTeleportPacket) packet).getEntityId();
            }
        } catch (Exception e) {
        }
        return -1;
    }

    public String getDisplayInfo() {
        if (delayedPackets.isEmpty()) {
            return "Ready";
        }
        return "Lag (" + delayedPackets.size() + ")";
    }
}