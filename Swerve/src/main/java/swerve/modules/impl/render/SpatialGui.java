package swerve.modules.impl.render;

import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.ui.newgui.GuiRender;
import swerve.utils.color.ColorUtils;
import swerve.utils.render.WorldProjection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.util.Mth;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

// Порт Spatial GUI (github.com/tastytrash/Spatial-GUI, MIT) в Swerve: экран рисуется не плоско,
// а как 3D-панель перед камерой. Приём отличается от оригинала (там кадр экрана уходит
// в отдельный target и рисуется квадом), но результат тот же и без лишних фреймбуферов:
// на время отрисовки экрана подменяется проекция, а матрица панели встраивается в неё:
//
//     P' = P * T * Mv^-1
//
// Экран продолжает рисоваться своими же вызовами, но каждая вершина (в GUI-координатах)
// сперва проходит Mv (GUI-трансляция), затем Mv^-1 (сокращается), затем T (панель в
// пространстве камеры) и P (перспектива). Поэтому текст и предметы остаются нативными,
// без апскейла текстуры, и достаточно одной подмены матрицы.
//
// Курсор: физический курсор ходит по окну, поэтому координаты мыши для экрана считаются
// пересечением луча камеры с плоскостью панели (обратная матрица панели) — клики и ховер
// попадают туда, куда смотрит игрок. Точка попадания дополнительно рисуется на панели.
public final class SpatialGui extends Module {

    private static final float OPEN_DURATION_MS = 190.0f;
    private static final float BASE_SCREEN_FRACTION = 0.62f;
    private static final float OPEN_SCALE_FROM = 0.86f;
    private static final float OPEN_LIFT = 0.22f;
    private static final float OPEN_DISTANCE_BONUS = 0.30f;
    private static final float FALLBACK_FOV_DEGREES = 70.0f;
    private static final float FALLBACK_ASPECT = 16.0f / 9.0f;
    private static final float FALLBACK_NEAR = 0.05f;
    private static final float FALLBACK_FAR = 512.0f;
    private static final float Z_COMPRESSION = 0.0022f;
    private static final float MIN_PROJECTION_M11 = 1.0E-4f;
    private static final float MIN_PLANE_DENOMINATOR = 1.0E-5f;
    private static final float MARKER_RADIUS = 2.6f;
    private static final float MARKER_RING = 1.0f;
    private static final int MARKER_COLOR = ColorUtils.rgba(255, 255, 255, 210);
    private static final int MARKER_RING_COLOR = ColorUtils.rgba(0, 0, 0, 150);

    private static SpatialGui instance;

    private final ModeSetting screenMode = new ModeSetting("Экраны", "Контейнеры", "Контейнеры", "Все");
    private final FloatSetting distance = new FloatSetting("Дистанция", 1.6f, 0.55f, 3.0f, 0.05f);
    private final FloatSetting size = new FloatSetting("Размер", 1.0f, 0.4f, 1.8f, 0.05f);
    private final FloatSetting offsetY = new FloatSetting("Смещение по высоте", 0.0f, -0.8f, 0.8f, 0.02f);
    private final FloatSetting rotateY = new FloatSetting("Поворот", 0.0f, -50.0f, 50.0f, 1.0f);
    private final FloatSetting rotateX = new FloatSetting("Наклон", 0.0f, -40.0f, 40.0f, 1.0f);
    private final BooleanSetting openAnimation = new BooleanSetting("Анимация открытия", true);
    private final BooleanSetting cursorMarker = new BooleanSetting("Метка курсора", true);
    private final BooleanSetting hideHud = new BooleanSetting("Скрывать HUD", false);
    private final BooleanSetting skipChat = new BooleanSetting("В чате как обычно", true);

    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f panelMatrix = new Matrix4f();
    private final Matrix4f inversePanelMatrix = new Matrix4f();
    private final Matrix4f inverseProjectionMatrix = new Matrix4f();
    private final Matrix4f combinedProjection = new Matrix4f();
    private final Matrix4f modelViewInverse = new Matrix4f();
    private final Vector4f nearPoint = new Vector4f();
    private final Vector4f farPoint = new Vector4f();
    private final Vector3f rayOrigin = new Vector3f();
    private final Vector3f rayDirection = new Vector3f();
    private final Vector3f hitPoint = new Vector3f();

    private Screen hookedScreen;
    private long openedAtNanos;
    private float openProgress = 1.0f;
    private float guiWidth = 1.0f;
    private float guiHeight = 1.0f;
    private float mappedX;
    private float mappedY;
    private boolean hasMappedPoint;
    private boolean matricesReady;
    private boolean projectionApplied;

    public SpatialGui() {
        super("SpatialGui", "Инвентарь и контейнеры как 3D-панель перед камерой", ModuleCategory.RENDER);
        addSettings(screenMode, distance, size, offsetY, rotateY, rotateX, openAnimation, cursorMarker, hideHud, skipChat);
        instance = this;
    }

    // Модуль ищется из миксинов без обхода списка модулей на каждый вызов.
    public static SpatialGui get() {
        return instance;
    }

    public static boolean isHidingHud() {
        SpatialGui module = instance;
        return module != null && module.isEnable() && module.hideHud.get() && module.isHooked();
    }

    @Override
    public void onDisable() {
        resetPanel();
        super.onDisable();
    }

    @Override
    public void onTick() {
        // Экран мог закрыться между кадрами — состояние панели сбрасываем сразу.
        Minecraft minecraft = Minecraft.getInstance();
        if (!isEnable() || minecraft.level == null || minecraft.screen == null) {
            resetPanel();
        }
    }

    // [+-] Точка входа из MixinScreenSpatialGui: подготовка матриц кадра для открытого экрана.
    // false — экран рисуется как обычно.
    public boolean beginSpatial(Screen screen) {
        Minecraft minecraft = Minecraft.getInstance();
        // Вложенный вызов (экран рисуется внутри другого экрана) рендерится уже под готовой
        // проекцией — второй раз панель не строим.
        if (projectionApplied || !isEnable() || minecraft.level == null || !canHook(screen)) {
            if (!projectionApplied) {
                resetPanel();
            }
            return false;
        }

        if (hookedScreen != screen) {
            hookedScreen = screen;
            openedAtNanos = System.nanoTime();
            openProgress = openAnimation.get() ? 0.0f : 1.0f;
        }

        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        if (width <= 0 || height <= 0) {
            return false;
        }
        guiWidth = width;
        guiHeight = height;

        updateOpenProgress();
        buildMatrices();
        applyProjection();
        return true;
    }

    public void endSpatial(GuiGraphics graphics) {
        if (!projectionApplied) {
            return;
        }

        if (cursorMarker.get() && hasMappedPoint && graphics != null) {
            drawCursorMarker(graphics.pose());
        }

        RenderSystem.restoreProjectionMatrix();
        RenderSystem.enableCull();
        projectionApplied = false;
    }

    // Замена координат мыши на точку пересечения луча курсора с плоскостью панели.
    // true — координаты валидны, читать getMappedMouseX()/getMappedMouseY().
    public boolean mapMouse(double mouseX, double mouseY) {
        hasMappedPoint = false;
        if (!matricesReady || !isHooked() || guiWidth <= 0.0f || guiHeight <= 0.0f) {
            return false;
        }

        float ndcX = (float) (mouseX / guiWidth) * 2.0f - 1.0f;
        float ndcY = 1.0f - (float) (mouseY / guiHeight) * 2.0f;

        // Луч в пространстве камеры: две точки на near/far плоскостях через обратную проекцию.
        nearPoint.set(ndcX, ndcY, -1.0f, 1.0f).mul(inverseProjectionMatrix);
        farPoint.set(ndcX, ndcY, 1.0f, 1.0f).mul(inverseProjectionMatrix);
        if (Math.abs(nearPoint.w) < MIN_PLANE_DENOMINATOR || Math.abs(farPoint.w) < MIN_PLANE_DENOMINATOR) {
            return false;
        }

        float nearX = nearPoint.x / nearPoint.w;
        float nearY = nearPoint.y / nearPoint.w;
        float nearZ = nearPoint.z / nearPoint.w;
        float farX = farPoint.x / farPoint.w;
        float farY = farPoint.y / farPoint.w;
        float farZ = farPoint.z / farPoint.w;

        rayOrigin.set(nearX, nearY, nearZ);
        rayDirection.set(farX - nearX, farY - nearY, farZ - nearZ);
        if (rayDirection.lengthSquared() < MIN_PLANE_DENOMINATOR) {
            return false;
        }
        rayDirection.normalize();

        // Плоскость панели: точка — перенос матрицы панели, нормаль — её третья колонка
        // (в GUI-пространстве панель лежит в плоскости XY, значит нормаль — локальный +Z).
        float planeX = panelMatrix.m30();
        float planeY = panelMatrix.m31();
        float planeZ = panelMatrix.m32();
        float normalX = panelMatrix.m02();
        float normalY = panelMatrix.m12();
        float normalZ = panelMatrix.m22();
        float normalLength = (float) Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        if (normalLength < MIN_PLANE_DENOMINATOR) {
            return false;
        }
        normalX /= normalLength;
        normalY /= normalLength;
        normalZ /= normalLength;

        float denominator = rayDirection.x * normalX + rayDirection.y * normalY + rayDirection.z * normalZ;
        if (Math.abs(denominator) < MIN_PLANE_DENOMINATOR) {
            return false;
        }

        float offsetX = planeX - rayOrigin.x;
        float offsetY = planeY - rayOrigin.y;
        float offsetZ = planeZ - rayOrigin.z;
        float distanceToPlane = (offsetX * normalX + offsetY * normalY + offsetZ * normalZ) / denominator;
        if (distanceToPlane <= 0.0f) {
            return false;
        }

        hitPoint.set(rayOrigin).fma(distanceToPlane, rayDirection).mulPosition(inversePanelMatrix);
        if (hitPoint.x < -guiWidth * 0.5f || hitPoint.x > guiWidth * 1.5f
                || hitPoint.y < -guiHeight * 0.5f || hitPoint.y > guiHeight * 1.5f) {
            return false;
        }

        mappedX = Mth.clamp(hitPoint.x, 0.0f, guiWidth);
        mappedY = Mth.clamp(hitPoint.y, 0.0f, guiHeight);
        hasMappedPoint = true;
        return true;
    }

    public float getMappedMouseX() {
        return mappedX;
    }

    public float getMappedMouseY() {
        return mappedY;
    }

    public boolean hasMappedPoint() {
        return hasMappedPoint;
    }

    private boolean isHooked() {
        Minecraft minecraft = Minecraft.getInstance();
        return hookedScreen != null && minecraft.screen == hookedScreen && canHook(hookedScreen);
    }

    private boolean canHook(Screen screen) {
        if (screen == null) {
            return false;
        }
        if (!screenMode.is("Все") && !(screen instanceof AbstractContainerScreen<?>)) {
            return false;
        }
        return !skipChat.get() || !(screen instanceof ChatScreen);
    }

    private void updateOpenProgress() {
        if (!openAnimation.get()) {
            openProgress = 1.0f;
            return;
        }

        float elapsedMs = (System.nanoTime() - openedAtNanos) / 1_000_000.0f;
        float linear = Mth.clamp(elapsedMs / OPEN_DURATION_MS, 0.0f, 1.0f);
        float remaining = 1.0f - linear;
        openProgress = 1.0f - remaining * remaining * remaining;
    }

    // Матрица панели: GUI-координаты -> пространство камеры (-Z вперёд, +Y вверх).
    // Размер подобран так, чтобы панель занимала фиксированную долю кадра на заданной
    // дистанции: s = F * 2 * D * tan(fovY / 2) / guiHeight.
    private void buildMatrices() {
        if (!WorldProjection.copyProjection(projectionMatrix)) {
            float guiAspect = guiHeight > 0.0f ? guiWidth / guiHeight : FALLBACK_ASPECT;
            projectionMatrix.identity().perspective((float) Math.toRadians(FALLBACK_FOV_DEGREES),
                    guiAspect > 0.0f ? guiAspect : FALLBACK_ASPECT, FALLBACK_NEAR, FALLBACK_FAR);
        }

        float m11 = Math.max(MIN_PROJECTION_M11, Math.abs(projectionMatrix.m11()));
        float fovY = 2.0f * (float) Math.atan(1.0f / m11);
        float ease = openProgress;
        float panelDistance = distance.get() + (1.0f - ease) * OPEN_DISTANCE_BONUS;
        float visibleHeight = 2.0f * panelDistance * (float) Math.tan(fovY * 0.5f);
        float pixelScale = BASE_SCREEN_FRACTION * size.get() * visibleHeight / Math.max(1.0f, guiHeight);
        float visualScale = pixelScale * (OPEN_SCALE_FROM + (1.0f - OPEN_SCALE_FROM) * ease);

        panelMatrix.identity()
                .translate(0.0f, offsetY.get() + (1.0f - ease) * OPEN_LIFT, -panelDistance)
                .rotateY((float) Math.toRadians(rotateY.get()))
                .rotateX((float) Math.toRadians(rotateX.get()))
                // Y инвертируется: в GUI-координатах ось идёт вниз, в пространстве камеры — вверх.
                // Z сжимается, иначе z-слои интерфейса (тени, всплывающие подсказки) вылетели бы из near/far.
                .scale(visualScale, -visualScale, visualScale * Z_COMPRESSION)
                .translate(-guiWidth * 0.5f, -guiHeight * 0.5f, 0.0f);

        // Вершины экрана уже умножаются на текущую модельную матрицу GUI, поэтому обратная
        // матрица сокращает её в P' = P * T * Mv^-1 и панель встаёт ровно в свой объём.
        modelViewInverse.set(RenderSystem.getModelViewMatrix()).invert();
        combinedProjection.set(projectionMatrix).mul(panelMatrix).mul(modelViewInverse);

        inversePanelMatrix.set(panelMatrix).invert();
        inverseProjectionMatrix.set(projectionMatrix).invert();
        matricesReady = true;
    }

    private void applyProjection() {
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(combinedProjection, ProjectionType.PERSPECTIVE);
        // GUI-квады рисуются без отсечения граней; при наклоне панели витку контуров это критично.
        RenderSystem.disableCull();
        projectionApplied = true;
    }

    private void drawCursorMarker(PoseStack stack) {
        GuiRender.roundedRect(stack, mappedX - MARKER_RADIUS, mappedY - MARKER_RADIUS,
                MARKER_RADIUS * 2.0f, MARKER_RADIUS * 2.0f, MARKER_RADIUS, MARKER_COLOR);
        GuiRender.roundedOutline(stack, mappedX - MARKER_RADIUS, mappedY - MARKER_RADIUS,
                MARKER_RADIUS * 2.0f, MARKER_RADIUS * 2.0f, MARKER_RING, MARKER_RING_COLOR, MARKER_RADIUS);
    }

    private void resetPanel() {
        hookedScreen = null;
        openProgress = 1.0f;
        hasMappedPoint = false;
        matricesReady = false;
        projectionApplied = false;
    }
}
