package fun.volon.client.modules.impl.render;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import fun.volon.Volon;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.Event3DRender;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.api.utils.render.HudBlockOverlayShaderRenderer;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.FloatSetting;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FireworkESP extends Module {

    public static FireworkESP INSTANCE = new FireworkESP();

    private final FloatSetting interval = new FloatSetting("Интервал (мс)", 100.0f, 10.0f, 1000.0f, 10.0f);
    private final FloatSetting lifetime = new FloatSetting("Время жизни (мс)", 1000.0f, 100.0f, 5000.0f, 100.0f);

    private final Matrix4f lastProjectionMatrix = new Matrix4f();
    private final Quaternionf lastCameraRotation = new Quaternionf();
    private Vec3d lastCameraPos = Vec3d.ZERO;
    private float lastTickDelta;
    private final Map<Integer, FireworkData> fireworks = new HashMap<>();

    public FireworkESP() {
        super("FireworkESP", "Показывает теги и трейлы фейерверков", ModuleCategory.RENDER);
        addSettings(interval, lifetime);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        fireworks.clear();
    }

    @EventLink
    public void onRender3D(Event3DRender event) {
        lastProjectionMatrix.set(event.getProjectionMatrix());
        lastCameraPos = event.getCamera().getPos();
        lastCameraRotation.set(event.getCamera().getRotation());
        lastTickDelta = event.getTickDelta();

        if (mc.world == null) return;

        long currentTime = System.currentTimeMillis();

        fireworks.entrySet().removeIf(entry -> {
            Entity entity = mc.world.getEntityById(entry.getKey());
            boolean isDead = (entity == null || !entity.isAlive());
            entry.getValue().points.removeIf(p -> currentTime - p.timestamp > lifetime.get());
            return isDead && entry.getValue().points.isEmpty();
        });

        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof FireworkRocketEntity firework && entity.isAlive()) {
                FireworkData data = fireworks.computeIfAbsent(entity.getId(), k -> new FireworkData());

                // Определяем владельца — смотрим ближайшего игрока с фейерверком в руке
                if (!data.ownerResolved) {
                    data.isFriend = resolveIsFriend(firework);
                    data.ownerResolved = true;
                }

                if (currentTime - data.lastSpawnTime >= interval.get()) {
                    Vec3d pos = new Vec3d(
                            MathHelper.lerp(lastTickDelta, entity.lastRenderX, entity.getX()),
                            MathHelper.lerp(lastTickDelta, entity.lastRenderY, entity.getY()) + 0.5,
                            MathHelper.lerp(lastTickDelta, entity.lastRenderZ, entity.getZ())
                    );
                    float ageInSeconds = entity.age / 20.0f;
                    data.points.add(new TrailPoint(pos, currentTime, ageInSeconds));
                    data.lastSpawnTime = currentTime;
                }
            }
        }
    }

    @EventLink
    public void onRender2D(EventRender.Default event) {
        if (mc.player == null || mc.world == null) return;

        MatrixStack matrices = event.getContext().getMatrices();
        ItemStack icon = new ItemStack(Items.FIREWORK_ROCKET);
        long currentTime = System.currentTimeMillis();

        boolean liquidGlass = RenderUtils.isLiquidGlassHudActive();
        if (liquidGlass) HudBlockOverlayShaderRenderer.INSTANCE.beginFrame();
        try {
            for (Map.Entry<Integer, FireworkData> entry : fireworks.entrySet()) {
                FireworkData data = entry.getValue();

                for (TrailPoint p : data.points) {
                    Vec3d screen = worldToScreen(p.pos);
                    if (screen == null) continue;

                    float progress = 1.0f - ((float) (currentTime - p.timestamp) / lifetime.get());
                    progress = MathHelper.clamp(progress, 0.0f, 1.0f);

                    renderIconRect(event, matrices, icon, screen, progress, data.isFriend);
                }

                Entity entity = mc.world.getEntityById(entry.getKey());
                if (entity instanceof FireworkRocketEntity && entity.isAlive()) {
                    Vec3d currentPos = new Vec3d(
                            MathHelper.lerp(lastTickDelta, entity.lastRenderX, entity.getX()),
                            MathHelper.lerp(lastTickDelta, entity.lastRenderY, entity.getY()) + 0.5,
                            MathHelper.lerp(lastTickDelta, entity.lastRenderZ, entity.getZ())
                    );
                    Vec3d screen = worldToScreen(currentPos);
                    if (screen != null) {
                        renderIconRect(event, matrices, icon, screen, 1.0f, data.isFriend);
                    }
                }
            }
        } finally {
            if (liquidGlass) HudBlockOverlayShaderRenderer.INSTANCE.finishFrame();
        }
    }

    private void renderIconRect(EventRender.Default event, MatrixStack matrices, ItemStack icon, Vec3d screen, float progress, boolean isFriend) {
        float iconScale = 0.75f;
        float iconPx    = 16.0f * iconScale;
        float padding   = 4.0f;
        float boxSize   = iconPx + padding * 2;
        float radius    = 4.0f;

        float animScale = 0.45f + 0.55f * progress;
        int   alpha     = (int)(220 * progress);
        if (alpha <= 5) return;

        // Зелёный фон для друзей, тёмный для остальных
        int bgColor = isFriend
                ? (alpha << 24) | 0x1A4A1A   // тёмно-зелёный
                : (alpha << 24) | 0x0A0A0A;  // тёмный

        matrices.push();
        matrices.translate(screen.x, screen.y, 0);
        matrices.scale(animScale, animScale, 1.0f);

        RenderUtils.drawRoundedRect(matrices,
                -boxSize * 0.5f, -boxSize * 0.5f,
                boxSize, boxSize, radius, bgColor);

        // Для друга — тонкая зелёная обводка
        if (isFriend) {
            RenderUtils.drawRoundedRectOutline(matrices,
                    -boxSize * 0.5f, -boxSize * 0.5f,
                    boxSize, boxSize, radius, radius, radius, radius,
                    1.0f,
                    (alpha << 24) | 0x55FF55,
                    (alpha << 24) | 0x55FF55,
                    (alpha << 24) | 0x55FF55,
                    (alpha << 24) | 0x55FF55);
        }

        matrices.push();
        matrices.translate(-iconPx * 0.5f, -iconPx * 0.5f, 0);
        matrices.scale(iconScale, iconScale, 1.0f);
        event.getContext().drawItem(icon, 0, 0);
        matrices.pop();

        matrices.pop();
    }

    /** Определяет, принадлежит ли фейерверк игроку из френдлиста */
    private boolean resolveIsFriend(FireworkRocketEntity firework) {
        if (mc.world == null || Volon.INSTANCE.friendStorage == null) return false;
        Vec3d fw = firework.getPos();
        double bestDist = 8.0 * 8.0;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            double dist = player.squaredDistanceTo(fw.x, fw.y, fw.z);
            if (dist < bestDist) {
                bestDist = dist;
                if (Volon.INSTANCE.friendStorage.isFriend(player.getName().getString())) return true;
            }
        }
        return false;
    }

    private Vec3d worldToScreen(Vec3d worldPos) {
        Vector3f relative = new Vector3f(
                (float) (worldPos.x - lastCameraPos.x),
                (float) (worldPos.y - lastCameraPos.y),
                (float) (worldPos.z - lastCameraPos.z)
        );

        relative.rotate(new Quaternionf(lastCameraRotation).conjugate());

        Vector4f clip = new Vector4f(relative.x, relative.y, relative.z, 1.0f);
        lastProjectionMatrix.transform(clip);

        float w = clip.w;
        if (w <= 0.00001f) return null;

        float ndcX = clip.x / w;
        float ndcY = clip.y / w;
        float ndcZ = clip.z / w;

        float screenX = (ndcX * 0.5f + 0.5f) * mc.getWindow().getScaledWidth();
        float screenY = (1.0f - (ndcY * 0.5f + 0.5f)) * mc.getWindow().getScaledHeight();

        if (Float.isNaN(screenX) || Float.isNaN(screenY) || Float.isInfinite(screenX) || Float.isInfinite(screenY)) {
            return null;
        }

        return new Vec3d(screenX, screenY, ndcZ);
    }

    private static class FireworkData {
        long lastSpawnTime;
        boolean isFriend = false;
        boolean ownerResolved = false;
        final List<TrailPoint> points = new ArrayList<>();
    }

    private static class TrailPoint {
        final Vec3d pos;
        final long timestamp;
        final float ageSec;

        TrailPoint(Vec3d pos, long timestamp, float ageSec) {
            this.pos = pos;
            this.timestamp = timestamp;
            this.ageSec = ageSec;
        }
    }
}