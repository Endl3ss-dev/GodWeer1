package antileak.base.client.modules.impl.player;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.Priority;
import antileak.base.api.events.implement.Event3DRender;
import antileak.base.api.events.implement.EventBinding;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.utils.notification.NotificationManager;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BindSetting;
import antileak.base.client.modules.settings.implement.BooleanSetting;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WardenHelper extends Module {

    public static WardenHelper INSTANCE = new WardenHelper();

    private final BooleanSetting autoGPS = new BooleanSetting("Авто GPS", true);
    private final BooleanSetting notifications = new BooleanSetting("Уведомления", true);
    private final BindSetting resetBind = new BindSetting("Ресет сундуков", -1);

    private final Map<BlockPos, ChestData> chestDataMap = new ConcurrentHashMap<>();
    private final Set<BlockPos> gpsVisited = new HashSet<>();
    private final Set<BlockPos> notified = new HashSet<>();
    private final Set<BlockPos> lootedChests = new HashSet<>(); // Добавлено: отслеживание залученных сундуков
    private BlockPos currentGPSTarget;
    private World lastWorld;
    private long lastScanMs;

    private static final long SCAN_INTERVAL_MS = 1000L;
    private static final int SCAN_RADIUS = 64;
    private static final double WARDEN_X = 2000.0;
    private static final double WARDEN_Z = 2000.0;
    private static final double ZONE_RADIUS = 250.0;
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{2})(?::(\\d{2}))?");
    private static final Pattern SECONDS_PATTERN = Pattern.compile("(\\d+)\\s*(с|s|сек|sec)");

    public WardenHelper() {
        super("Warden Helper", "Warden Helper поможет вам на 1.21.8 анках", ModuleCategory.PLAYER);
        addSettings(autoGPS, notifications, resetBind);
    }

    @Override
    public void onEnable() {
        clearAll();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        clearAll();
        super.onDisable();
    }

    @EventLink
    public void onBinding(EventBinding event) {
        if (resetBind.getKey() != -1 && event.getKey() == resetBind.getKey()) {
            clearAll();
            NotificationManager.pushCustom("Warden Helper: сундуки сброшены!", "h");
        }
    }

    @EventLink
    public void onTick(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        if (mc.world != lastWorld) {
            lastWorld = mc.world;
            clearAll();
            return;
        }

        if (!isInWardenZone()) return;

        long now = System.currentTimeMillis();
        if (now - lastScanMs >= SCAN_INTERVAL_MS) {
            lastScanMs = now;
            scanChests();
            updateTimers();
            updateGPSAndNotifications();
        }
    }

    @EventLink(priority = Priority.HIGH)
    public void onRender3D(Event3DRender event) {
        if (mc.world == null || mc.player == null || chestDataMap.isEmpty()) return;

        MatrixStack matrices = event.getMatrices();
        Vec3d cam = event.getCamera().getPos();

        matrices.push();
        matrices.translate(-cam.x, -cam.y, -cam.z);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder fillBuffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        for (Map.Entry<BlockPos, ChestData> entry : chestDataMap.entrySet()) {
            float[] color = getESPColor(entry.getValue());
            addFilledBox(fillBuffer, matrix, entry.getKey(), color[0], color[1], color[2], 50f / 255f);
        }

        BufferRenderer.drawWithGlobalProgram(fillBuffer.end());

        BufferBuilder lineBuffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

        for (Map.Entry<BlockPos, ChestData> entry : chestDataMap.entrySet()) {
            float[] color = getESPColor(entry.getValue());
            addOutlinedBox(lineBuffer, matrix, entry.getKey(), color[0], color[1], color[2], 200f / 255f);
            addInternalDiagonals(lineBuffer, matrix, entry.getKey(), color[0], color[1], color[2], 120f / 255f);
        }

        BufferRenderer.drawWithGlobalProgram(lineBuffer.end());

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();

        matrices.pop();
    }

    private boolean isInWardenZone() {
        return Math.abs(mc.player.getX() - WARDEN_X) <= ZONE_RADIUS
                && Math.abs(mc.player.getZ() - WARDEN_Z) <= ZONE_RADIUS;
    }

    private void clearAll() {
        chestDataMap.clear();
        gpsVisited.clear();
        notified.clear();
        lootedChests.clear(); // Добавлено: очищаем и залученные
        currentGPSTarget = null;
    }

    private void scanChests() {
        if (mc.world == null || mc.player == null) return;

        BlockPos playerPos = mc.player.getBlockPos();
        int playerChunkX = playerPos.getX() >> 4;
        int playerChunkZ = playerPos.getZ() >> 4;
        int chunkRange = (SCAN_RADIUS >> 4) + 2;
        int radiusSq = SCAN_RADIUS * SCAN_RADIUS;

        chestDataMap.entrySet().removeIf(e ->
                e.getKey().getSquaredDistance(playerPos) > radiusSq);

        Set<BlockPos> currentChests = new HashSet<>();

        for (int cx = -chunkRange; cx <= chunkRange; cx++) {
            for (int cz = -chunkRange; cz <= chunkRange; cz++) {
                WorldChunk chunk = mc.world.getChunk(playerChunkX + cx, playerChunkZ + cz);
                if (chunk == null) continue;

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (!(be instanceof ChestBlockEntity) && !(be instanceof TrappedChestBlockEntity)) continue;

                    BlockPos pos = be.getPos();
                    if (pos.getSquaredDistance(playerPos) > radiusSq) continue;

                    currentChests.add(pos);
                    chestDataMap.computeIfAbsent(pos, ChestData::new);
                }
            }
        }

        chestDataMap.keySet().retainAll(currentChests);
    }

    private void updateTimers() {
        if (mc.world == null) return;

        for (Map.Entry<BlockPos, ChestData> entry : chestDataMap.entrySet()) {
            BlockPos pos = entry.getKey();
            ChestData data = entry.getValue();

            // Expand search area to 3x5x3 blocks to catch armor stands at distance
            Box searchBox = new Box(pos).expand(2.0, 4.0, 2.0);
            boolean found = false;

            for (ArmorStandEntity stand : mc.world.getEntitiesByClass(
                    ArmorStandEntity.class, searchBox, ArmorStandEntity::hasCustomName)) {
                String name = stand.getCustomName().getString()
                        .replaceAll("§[0-9a-fk-or]", "");

                long seconds = parseTimeToSeconds(name);
                if (seconds >= 0) {
                    data.setTimer(seconds);
                    data.lastSeenMs = System.currentTimeMillis();
                    found = true;
                    break;
                }
            }

            // Only mark as "no timer" if we haven't seen a timer in 5 seconds
            // This prevents flickering when armor stands are at render distance
            if (!found && data.hasTimer) {
                long timeSinceLastSeen = System.currentTimeMillis() - data.lastSeenMs;
                if (timeSinceLastSeen > 5000) {
                    data.hasTimer = false;
                }
            }
        }
    }

    private long parseTimeToSeconds(String str) {
        Matcher m1 = TIME_PATTERN.matcher(str);
        if (m1.find()) {
            if (m1.group(3) == null)
                return (long) Integer.parseInt(m1.group(1)) * 60 + Integer.parseInt(m1.group(2));
            return (long) Integer.parseInt(m1.group(1)) * 3600
                    + (long) Integer.parseInt(m1.group(2)) * 60
                    + Integer.parseInt(m1.group(3));
        }

        Matcher m2 = SECONDS_PATTERN.matcher(str);
        if (m2.find()) return Long.parseLong(m2.group(1));

        String digits = str.replaceAll("[^0-9]", "").trim();
        if (digits.isEmpty()) return -1L;

        long val = Long.parseLong(digits);
        return (val > 0 && val < 36000) ? val : -1L;
    }

    private void updateGPSAndNotifications() {
        if (mc.player == null || mc.player.networkHandler == null) return;

        if (currentGPSTarget != null
                && mc.player.squaredDistanceTo(Vec3d.ofCenter(currentGPSTarget)) <= 100.0) {
            mc.player.networkHandler.sendChatCommand("gps off");
            currentGPSTarget = null;
        }

        for (Map.Entry<BlockPos, ChestData> entry : chestDataMap.entrySet()) {
            BlockPos pos = entry.getKey();
            ChestData data = entry.getValue();

            if (!data.hasTimer) continue;

            float t = data.getTimeLeft();

            if (t <= 0.0f) {
                if (autoGPS.isState() && !gpsVisited.contains(pos))
                    sendGPS(pos);

                if (notifications.isState() && !notified.contains(pos)) {
                    NotificationManager.pushCustom(
                            "Warden Helper: сундук готов! [" + pos.getX() + " " + pos.getZ() + "]", "h");
                    notified.add(pos);
                }
            } else if (t <= 20.0f) {
                if (autoGPS.isState() && !gpsVisited.contains(pos))
                    sendGPS(pos);

                if (notifications.isState() && !notified.contains(pos)) {
                    NotificationManager.pushCustom(
                            "Warden Helper: сундук через " + (int) t + " сек! [" + pos.getX() + " " + pos.getZ() + "]", "h");
                    notified.add(pos);
                }
            }
        }
    }

    private void sendGPS(BlockPos pos) {
        if (mc.player == null || mc.player.networkHandler == null) return;

        mc.player.networkHandler.sendChatCommand("gps set " + pos.getX() + " " + pos.getZ());
        gpsVisited.add(pos);
        currentGPSTarget = pos;
    }

    private float[] getESPColor(ChestData data) {
        if (!data.hasTimer) return new float[]{180f / 255f, 180f / 255f, 180f / 255f};

        float t = data.getTimeLeft();

        if (t <= 0.0f) {
            float p = (float) ((Math.sin(System.currentTimeMillis() / 200.0) * 0.3) + 0.7);
            return new float[]{0f, p, p * 0.5f};
        }

        if (t > 120.0f) return new float[]{1f, 60f / 255f, 60f / 255f};

        if (t > 20.0f) {
            float prog = 1.0f - ((t - 20.0f) / 100.0f);
            return new float[]{
                    1f + (220f / 255f - 1f) * prog,
                    60f / 255f + (220f / 255f - 60f / 255f) * prog,
                    60f / 255f + (50f / 255f - 60f / 255f) * prog
            };
        }

        float prog = 1.0f - (t / 20.0f);
        return new float[]{
                220f / 255f + (60f / 255f - 220f / 255f) * prog,
                220f / 255f + (1f - 220f / 255f) * prog,
                50f / 255f + (128f / 255f - 50f / 255f) * prog
        };
    }

    private void addFilledBox(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
        float minX = pos.getX();
        float minY = pos.getY();
        float minZ = pos.getZ();
        float maxX = minX + 1.0f;
        float maxY = minY + 1.0f;
        float maxZ = minZ + 1.0f;

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
    }

    private void addOutlinedBox(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
        float minX = pos.getX();
        float minY = pos.getY();
        float minZ = pos.getZ();
        float maxX = minX + 1.0f;
        float maxY = minY + 1.0f;
        float maxZ = minZ + 1.0f;

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
    }

    private void addInternalDiagonals(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
        float minX = pos.getX();
        float minY = pos.getY();
        float minZ = pos.getZ();
        float maxX = minX + 1.0f;
        float maxY = minY + 1.0f;
        float maxZ = minZ + 1.0f;

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
    }

    private static class ChestData {
        boolean hasTimer = false;
        private long timerEndMs = 0L;
        long lastSeenMs = 0L;

        ChestData(BlockPos pos) {}

        void setTimer(long secondsLeft) {
            this.timerEndMs = System.currentTimeMillis() + secondsLeft * 1000L;
            this.hasTimer = true;
        }

        float getTimeLeft() {
            return (timerEndMs - System.currentTimeMillis()) / 1000.0f;
        }
    }
}
