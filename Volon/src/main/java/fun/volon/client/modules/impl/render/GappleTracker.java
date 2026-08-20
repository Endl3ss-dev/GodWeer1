package fun.volon.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.ProjectionType;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.Priority;
import fun.volon.api.events.implement.Event3DRender;
import fun.volon.api.events.implement.EventRender;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.api.utils.render.RenderUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;

import java.util.HashMap;
import java.util.Map;

/**
 * GappleTracker — показывает иконку яблока в центре хитбокса игрока
 * пока тот ест золотое или зачарованное яблоко.
 * Иконка появляется при начале поедания и пропадает как только игрок закончил.
 * Цвет фона зависит от темы клиента.
 */
public class GappleTracker extends Module {

    public static GappleTracker INSTANCE = new GappleTracker();

    private final BooleanSetting golden     = new BooleanSetting("Золотое яблоко", true);
    private final BooleanSetting enchanted  = new BooleanSetting("Зачарованное яблоко", true);
    private final FloatSetting   iconSize   = new FloatSetting("Размер иконки", 12.0f, 6.0f, 24.0f, 0.5f);

    // UUID -> текущий ItemStack яблока (или null если не ест)
    private final Map<String, ItemStack> eatingMap = new HashMap<>();

    // Матрица проекции для 3D → 2D
    private final Matrix4f     lastProjMatrix  = new Matrix4f();
    private final Quaternionf  lastCameraRot   = new Quaternionf();
    private Vec3d              lastCameraPos   = Vec3d.ZERO;
    private float              lastTickDelta   = 1.0f;
    private int                lastSW, lastSH;
    private boolean            hasProjection   = false;

    // scratch буферы
    private final Vector3f projScratch = new Vector3f();
    private final Vector4f clipScratch = new Vector4f();

    public GappleTracker() {
        super("GappleTracker", "Показывает иконку яблока над игроком при поедании", ModuleCategory.RENDER);
        addSettings(golden, enchanted, iconSize);
    }

    @Override
    public void onDisable() {
        eatingMap.clear();
        hasProjection = false;
        super.onDisable();
    }

    /** Захватываем матрицы проекции из 3D рендера */
    @EventLink(priority = Priority.HIGH)
    public void onRender3D(Event3DRender event) {
        hasProjection = true;
        lastProjMatrix.set(event.getProjectionMatrix());
        lastCameraPos = event.getCamera().getPos();
        lastCameraRot.set(event.getCamera().getRotation());
        lastTickDelta = event.getTickDelta();
        lastSW = mc.getWindow().getScaledWidth();
        lastSH = mc.getWindow().getScaledHeight();
    }

    @EventLink
    public void onRender2D(EventRender.Default event) {
        if (!hasProjection || mc.world == null || mc.player == null) return;

        MatrixStack matrices = event.getContext().getMatrices();
        float sz = iconSize.get();

        int themeColor = ColorUtils.getThemeColor();
        // Тёмный фон с лёгким цветовым оттенком
        int bgColor = ColorUtils.rgba(8, 8, 10, 210);
        // Внешнее кольцо цвет темы
        int ringColor = ColorUtils.setAlphaColor(themeColor, 230);

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (!player.isAlive()) continue;

            // Определяем ест ли яблоко
            ItemStack apple = null;
            if (player.isUsingItem()) {
                ItemStack active = player.getActiveItem();
                if (!active.isEmpty()) {
                    boolean isGolden    = active.getItem() == Items.GOLDEN_APPLE;
                    boolean isEnchanted = active.getItem() == Items.ENCHANTED_GOLDEN_APPLE;
                    if ((isGolden && golden.isState()) || (isEnchanted && enchanted.isState())) {
                        apple = active;
                    }
                }
            }

            String id = player.getUuidAsString();
            if (apple == null) {
                eatingMap.remove(id);
                continue;
            }
            eatingMap.put(id, apple);

            // Проецируем центр хитбокса игрока (чуть ниже середины — как на фото)
            double px = MathHelper.lerp(lastTickDelta, player.prevX, player.getX());
            double py = MathHelper.lerp(lastTickDelta, player.prevY, player.getY()) + player.getHeight() * 0.4;
            double pz = MathHelper.lerp(lastTickDelta, player.prevZ, player.getZ());

            float[] screen = worldToScreen(px, py, pz);
            if (screen == null) continue;

            float sx = screen[0];
            float sy = screen[1];

            float radius = sz * 0.5f + 4.0f; // радиус круга

            // Внешнее кольцо (тема)
            RenderUtils.drawRingArc(matrices, sx, sy, radius + 2.5f, 3.5f, 0f, 360f, ringColor);

            // Тёмный круглый фон
            RenderUtils.drawRingArc(matrices, sx, sy, radius, radius - 0.5f, 0f, 360f,
                    ColorUtils.rgba(0, 0, 0, 200));
            // Заливка фона
            for (float r = 0; r < radius - 1f; r += 0.5f) {
                RenderUtils.drawRingArc(matrices, sx, sy, r + 0.5f, 0.5f, 0f, 360f, bgColor);
            }

            // Иконка предмета
            float scale = sz / 16.0f;
            matrices.push();
            matrices.translate(sx - sz * 0.5f, sy - sz * 0.5f, 0.0f);
            matrices.scale(scale, scale, 1.0f);
            RenderSystem.enableDepthTest();
            event.getContext().drawItem(apple, 0, 0);
            RenderSystem.disableDepthTest();
            matrices.pop();
        }
    }

    /**
     * Проецирует мировую координату (x,y,z) в экранную (sx, sy).
     * Возвращает null если точка за экраном.
     */
    private float[] worldToScreen(double wx, double wy, double wz) {
        // Переводим в camera-relative координаты
        double rx = wx - lastCameraPos.x;
        double ry = wy - lastCameraPos.y;
        double rz = wz - lastCameraPos.z;

        // Применяем rotation камеры (инвертированная)
        Quaternionf invRot = new Quaternionf(lastCameraRot).conjugate();
        projScratch.set((float) rx, (float) ry, (float) rz);
        invRot.transform(projScratch);

        // Применяем матрицу проекции (clip space)
        clipScratch.set(projScratch.x, projScratch.y, projScratch.z, 1.0f);
        lastProjMatrix.transform(clipScratch);

        if (clipScratch.w <= 0.0f) return null;

        float ndcX = clipScratch.x / clipScratch.w;
        float ndcY = clipScratch.y / clipScratch.w;

        if (ndcX < -1.0f || ndcX > 1.0f || ndcY < -1.0f || ndcY > 1.0f) return null;

        float sx = (ndcX + 1.0f) * 0.5f * lastSW;
        float sy = (1.0f - ndcY) * 0.5f * lastSH;

        return new float[]{sx, sy};
    }
}
