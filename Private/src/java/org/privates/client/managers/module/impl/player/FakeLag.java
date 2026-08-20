package org.privates.client.managers.module.impl.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ConcurrentLinkedQueue;

@Category1
@ModuleInfo(
        name = "FakeLag",
        category = Category.PLAYER,
        description = "Увеливает ваш пинг на время из-за это-го противнику без DLC сложнее попасть"
)
public class FakeLag extends Module {
    private final ModeSetting lagMode = new ModeSetting(this, "Lag Mode", "Pulse", "Random", "Burst");
    private final SliderSetting lagTime = new SliderSetting(this, "Lag Time", 100, 10, 1000, 10);
    private final SliderSetting normalTime = new SliderSetting(this, "Normal Time", 100, 10, 1000, 10);
    private final SliderSetting randomRange = new SliderSetting(this, "Random Range", 60, 0, 500, 10).setVisible(() -> lagMode.is("Random"));
    private final SliderSetting burstMaxLag = new SliderSetting(this, "Burst Max Lag", 240, 50, 1500, 10).setVisible(() -> lagMode.is("Burst"));
    private final SliderSetting burstPacketCap = new SliderSetting(this, "Burst Packet Cap", 18, 5, 120, 1).setVisible(() -> lagMode.is("Burst"));
    private final BooleanSetting preserveRotation = new BooleanSetting(this, "Preserve Rotation", true);
    private final BooleanSetting showLagHitbox = new BooleanSetting(this, "Show Lag Hitbox", true);
    private final BooleanSetting showLagModel = new BooleanSetting(this, "Show Lag Model", true);
    private final BooleanSetting releaseOnAttack = new BooleanSetting(this, "Release On Attack", true);
    private final SliderSetting attackReleaseDelay = new SliderSetting(this, "Attack Release Delay", 50, 0, 200, 10).setVisible(releaseOnAttack::getValue);
    private final BooleanSetting releaseOnUse = new BooleanSetting(this, "Release On Use", true);
    private final BooleanSetting releaseOnDamage = new BooleanSetting(this, "Release On Damage", true);

    private final Queue<IPacket<?>> packets = new ConcurrentLinkedQueue<>();
    private boolean lagging = true;
    private long lastSwitchTime = 0;
    private long lastAttackTime = 0;
    private int lagCount = 0;
    private float lastYaw = 0;
    private float lastPitch = 0;
    private boolean wasRotationChanged = false;
    private boolean forceRelease = false;
    private boolean isReleasing = false;
    private float lastHealth = 0;

    private Vector3d lagPosition = null;
    private BlockPos lagBlockPos = null;
    private static final int GHOST_ENTITY_ID = -91337;
    private RemoteClientPlayerEntity lagGhost;
    private float lagYaw;
    private float lagPitch;
    private long currentLagWindow;
    private long currentNormalWindow;

    public static FakeLag getInstance() {
        return Instance.get(FakeLag.class);
    }

    @Override
    public void onEnable() {
        try {
            lastSwitchTime = System.currentTimeMillis();
            lastAttackTime = 0;
            lagging = true;
            lagCount = 0;
            packets.clear();
            wasRotationChanged = false;
            forceRelease = false;
            isReleasing = false;
            if (mc.player != null) {
                lastYaw = mc.player.rotationYaw;
                lastPitch = mc.player.rotationPitch;
                lastHealth = mc.player.getHealth();
                lagPosition = mc.player.getPositionVec();
                lagBlockPos = new BlockPos(lagPosition);
                lagYaw = mc.player.rotationYaw;
                lagPitch = mc.player.rotationPitch;
            }
            setupWindows();
            spawnLagGhost();
        } catch (Exception e) {
        }
    }

    @Override
    public void onDisable() {
        try {
            if (mc.player != null && mc.getConnection() != null && !packets.isEmpty()) {
                releasePackets();
            }
            wasRotationChanged = false;
            forceRelease = false;
            isReleasing = false;
            lagPosition = null;
            lagBlockPos = null;
            removeLagGhost();
        } catch (Exception e) {
            packets.clear();
            removeLagGhost();
        }
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        try {
            if (mc.player == null || !isEnabled() || !preserveRotation.getValue()) return;

            if (!lagging && !wasRotationChanged) {
                lastYaw = mc.player.rotationYaw;
                lastPitch = mc.player.rotationPitch;
            }
        } catch (Exception e) {
        }
    }

    @EventHandler
    public void onPacket(PacketEvent event) {
        try {
            if (isReleasing) return;

            if (event.isSend() && mc.player != null && mc.getConnection() != null && isEnabled()) {

                if (isAttackPacket(event.getPacket()) && releaseOnAttack.getValue()) {
                    lastAttackTime = System.currentTimeMillis();

                    if (lagging && !packets.isEmpty()) {
                        packets.add(event.getPacket());
                        event.setCancelled(true);
                        forceRelease = true;
                        return;
                    }

                    return;
                }

                if (isUsePacket(event.getPacket()) && releaseOnUse.getValue()) {
                    if (lagging && !packets.isEmpty()) {
                        packets.add(event.getPacket());
                        event.setCancelled(true);
                        releasePackets();
                        return;
                    }
                    return;
                }

                if (lagging) {
                    packets.add(event.getPacket());
                    event.setCancelled(true);
                }
            }
        } catch (Exception e) {
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        try {
            if (mc.player == null || mc.getConnection() == null || !isEnabled()) return;

            long currentTime = System.currentTimeMillis();
            long deltaTime = currentTime - lastSwitchTime;

            if (releaseOnDamage.getValue()) {
                float currentHealth = mc.player.getHealth();
                if (currentHealth < lastHealth && lagging && !packets.isEmpty()) {
                    forceRelease = true;
                }
                lastHealth = currentHealth;
            }

            if (preserveRotation.getValue()) {
                if (lagging && !wasRotationChanged) {
                    if (!wasRotationChanged) {
                        lastYaw = mc.player.rotationYaw;
                        lastPitch = mc.player.rotationPitch;
                        wasRotationChanged = true;
                    }
                }
            }

            if (lagging) {
                if (!showLagModel.getValue() && lagGhost != null) {
                    removeLagGhost();
                }
                if (lagPosition == null && mc.player != null) {
                    lagPosition = mc.player.getPositionVec();
                    lagBlockPos = new BlockPos(lagPosition);
                    lagYaw = mc.player.rotationYaw;
                    lagPitch = mc.player.rotationPitch;
                }
                if (showLagModel.getValue() && lagGhost == null) {
                    spawnLagGhost();
                }

                boolean shouldForceRelease = forceRelease &&
                        (currentTime - lastAttackTime >= attackReleaseDelay.getValue().intValue());
                boolean releaseByMode = false;
                if (lagMode.is("Pulse")) {
                    releaseByMode = deltaTime >= lagTime.getValue().intValue();
                } else if (lagMode.is("Random")) {
                    releaseByMode = deltaTime >= currentLagWindow;
                } else if (lagMode.is("Burst")) {
                    releaseByMode = deltaTime >= burstMaxLag.getValue().intValue() || packets.size() >= burstPacketCap.getValue().intValue();
                }

                if (releaseByMode || shouldForceRelease) {
                    switchToNormal(currentTime);
                }
            } else {
                long normalWindow = lagMode.is("Random") ? currentNormalWindow : normalTime.getValue().intValue();
                if (deltaTime >= normalWindow) {
                    switchToLag(currentTime);
                }
            }
        } catch (Exception e) {
        }
    }

    private void setupWindows() {
        if (lagMode.is("Random")) {
            int range = randomRange.getValue().intValue();
            int lagBase = lagTime.getValue().intValue();
            int normalBase = normalTime.getValue().intValue();
            currentLagWindow = Math.max(10, lagBase + ThreadLocalRandom.current().nextInt(-range, range + 1));
            currentNormalWindow = Math.max(10, normalBase + ThreadLocalRandom.current().nextInt(-range, range + 1));
        } else {
            currentLagWindow = lagTime.getValue().intValue();
            currentNormalWindow = normalTime.getValue().intValue();
        }
    }

    private void switchToNormal(long currentTime) {
        lagging = false;
        lastSwitchTime = currentTime;
        releasePackets();
        lagCount++;
        wasRotationChanged = false;
        forceRelease = false;
        lagPosition = null;
        lagBlockPos = null;
        removeLagGhost();
        setupWindows();
    }

    private void switchToLag(long currentTime) {
        lagging = true;
        lastSwitchTime = currentTime;
        if (mc.player != null) {
            lagPosition = mc.player.getPositionVec();
            lagBlockPos = new BlockPos(lagPosition);
            lagYaw = mc.player.rotationYaw;
            lagPitch = mc.player.rotationPitch;
        }
        if (showLagModel.getValue()) {
            spawnLagGhost();
        } else {
            removeLagGhost();
        }
        setupWindows();
    }

    private void spawnLagGhost() {
        try {
            if (mc.world == null || mc.player == null || lagPosition == null) return;
            removeLagGhost();

            UUID id = UUID.nameUUIDFromBytes(("fakelag:" + mc.player.getName().getString()).getBytes());
            lagGhost = new RemoteClientPlayerEntity(mc.world, new GameProfile(id, mc.player.getName().getString()));
            lagGhost.copyLocationAndAnglesFrom(mc.player);
            lagGhost.setPosition(lagPosition.x, lagPosition.y, lagPosition.z);
            lagGhost.rotationYaw = lagYaw;
            lagGhost.rotationPitch = lagPitch;
            lagGhost.rotationYawHead = lagYaw;
            lagGhost.renderYawOffset = lagYaw;
            lagGhost.rotationPitchHead = lagPitch;
            lagGhost.setInvisible(false);
            mc.world.addEntity(GHOST_ENTITY_ID, lagGhost);
        } catch (Exception ignored) {
            lagGhost = null;
        }
    }

    private void removeLagGhost() {
        try {
            if (mc.world != null) {
                mc.world.removeEntityFromWorld(GHOST_ENTITY_ID);
            }
        } catch (Exception ignored) {
        }
        lagGhost = null;
    }

    @EventHandler
    public void onRender3D(Render3DLastEvent event) {
        if (!showLagHitbox.getValue() || !isEnabled() || lagBlockPos == null || mc.player == null || mc.world == null)
            return;

        try {
            boolean isGreen = !lagging || forceRelease;
            int fillColor = isGreen ? ColorUtil.getColor(50, 255, 50, 20) : ColorUtil.getColor(255, 50, 50, 20);
            int outlineColor = isGreen ? ColorUtil.getColor(50, 255, 50, 20) : ColorUtil.getColor(255, 50, 50, 25);

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

            for (int i = 0; i < 3; i++) {
                BlockPos pos = lagBlockPos.add(0, i, 0);
                double minX = pos.getX();
                double minY = pos.getY();
                double minZ = pos.getZ();
                double maxX = pos.getX() + 1;
                double maxY = pos.getY() + 1;
                double maxZ = pos.getZ() + 1;

                bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderFilledBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, fillColor);
                tessellator.draw();

                bufferBuilder.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
                RenderUtil3D.renderWireframeBox(bufferBuilder, minX, minY, minZ, maxX, maxY, maxZ, outlineColor);
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

            if (packets.isEmpty() || mc.player == null || mc.getConnection() == null) {
                packets.clear();
                return;
            }

            for (IPacket<?> packet : packets) {
                if (mc.getConnection() != null) {
                    try {
                        mc.getConnection().sendPacket(packet);
                    } catch (Exception ignored) {
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            packets.clear();
            isReleasing = false;
        }
    }

    private boolean isAttackPacket(IPacket<?> packet) {
        String n = packet.getClass().getSimpleName();
        return n.equals("CUseEntityPacket") || n.equals("CPacketUseEntity");
    }

    private boolean isUsePacket(IPacket<?> packet) {
        String n = packet.getClass().getSimpleName();
        return n.equals("CPlayerTryUseItemPacket") || n.equals("CPacketPlayerTryUseItem") ||
                n.equals("CPlayerTryUseItemOnBlockPacket") || n.equals("CPacketPlayerTryUseItemOnBlock");
    }

    public String getDisplayInfo() {
        String mode = lagMode.getValue();
        if (forceRelease) {
            return mode + " Releasing (" + packets.size() + ")";
        }
        return lagging ? mode + " Lag (" + packets.size() + ")" : mode + " Normal";
    }
}
