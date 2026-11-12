package tech.lightclient.modules.impl.render;

import tech.lightclient.client.ClientColors;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.events.impl.client.UpdateEvent;
import tech.lightclient.client.events.impl.render.EventRender;
import tech.lightclient.client.events.impl.world.EventFog;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.color.basic.ColorUtils;
import tech.lightclient.client.utils.render.main.world.WorldGeometryEmitter;
import tech.lightclient.client.utils.render.main.world.WorldRenderLayers;
import tech.lightclient.client.utils.render.main.world.WorldRenderer;
import tech.lightclient.client.utils.render.WetSurfaceRenderer;
import tech.lightclient.mixins.accessors.GameRendererAccessor;
import tech.lightclient.modules.api.Category;
import tech.lightclient.modules.api.Function;
import tech.lightclient.modules.api.FunctionAdd;
import tech.lightclient.modules.api.settings.impl.BooleanSetting;
import tech.lightclient.modules.api.settings.impl.ColorSetting;
import tech.lightclient.modules.api.settings.impl.ModeSetting;
import tech.lightclient.modules.api.settings.impl.SliderSetting;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

@FunctionAdd(name = "Ambience", alias = "Ambience", category = Category.Render, description = "Красивые светлячки и кубы вокруг персонажа")
public class Ambience extends Function {
    private static final Identifier RAIN_SPLASH_TEXTURE = Identifier.of("light", "images/jumpcircle/circle.png");
    private static final double PARTICLE_HEIGHT_OFFSET = 10.0;
    private static final double RAIN_HEIGHT_OFFSET = 38.0;
    private static final double RAIN_IMPACT_OFFSET = 4.0;
    private static final int RAIN_DROP_COUNT = 64;
    private static final double RAIN_RADIUS = 16.0;
    private static final long RAIN_SPLASH_LIFETIME_MS = 450L;

    public static BooleanSetting fireFlies = new BooleanSetting("Партиклы", true);
    public SliderSetting fireFliesCount = new SliderSetting("Количество", 20, 10, 500, 5).setVisible(fireFlies::get);
    public static ModeSetting fireFliesMode = new ModeSetting("Режим", "Картинка", "Картинка", "Кубы").setVisible(fireFlies::get);
    public SliderSetting fireFliesSize = new SliderSetting("Размер", 0.7f, 0.5f, 1, 0.1f)
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Картинка"));
    public SliderSetting fireFliesD = new SliderSetting("Дистанция", 15, 5f, 50f, 0.1f)
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Картинка"));
    public ModeSetting fireFliesTexture = new ModeSetting("Текстура", "Глоу", "Глоу", "Звезда", "Доллар", "Снежинка", "Сердце", "Корона", "Молния")
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Картинка"));
    public SliderSetting fireFliesCubeSize = new SliderSetting("Размер куба", 0.6f, 0.2f, 2.5f, 0.05f)
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Кубы"));
    public SliderSetting fireFliesCubeDistance = new SliderSetting("Дистанция кубов", 15, 5f, 50f, 0.1f)
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Кубы"));
    public SliderSetting fireFliesCubeSpeed = new SliderSetting("Скорость кубов", 1.0f, 0.2f, 3.0f, 0.05f)
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Кубы"));
    public BooleanSetting fireFliesCubeFill = new BooleanSetting("Заполнить центр", true)
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Кубы"));
    public SliderSetting fireFliesCubeFillAlpha = new SliderSetting("Сила заполнения", 1.0f, 0.05f, 1.0f, 0.05f)
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Кубы") && fireFliesCubeFill.get());
    public BooleanSetting fireFliesCubeInnerLines = new BooleanSetting("Доп. линии", true)
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Кубы"));
    public ModeSetting fireFliesCubeDiagonalMode = new ModeSetting("Линии в кубе", "1", "1", "2")
            .setVisible(() -> fireFlies.get() && fireFliesMode.is("Кубы") && fireFliesCubeInnerLines.get());
    public static BooleanSetting fireFliesRotation = new BooleanSetting("Делать ротацию", true).setVisible(fireFlies::get);
    public SliderSetting fireFliesRotationSpeed = new SliderSetting("Скорость ротации", 1.0f, 0.1f, 4.0f, 0.05f)
            .setVisible(() -> fireFlies.get() && fireFliesRotation.get());
    public static ModeSetting fireFliesColorMode = new ModeSetting("Цвет", "Клиент", "Клиент", "Свой", "Гирлянда", "Рандомный").setVisible(fireFlies::get);
    public static ColorSetting fireFliesCustomColor = new ColorSetting("Свой цвет", ColorUtils.rgb(255, 255, 150)).setVisible(() -> fireFlies.get() && fireFliesColorMode.is("Свой"));
    public BooleanSetting changeTime = new BooleanSetting("Менять время", true);
    public SliderSetting modifiedTime = new SliderSetting("Время", 22000, 0, 23000, 1000);
    public static ModeSetting changeFogM = new ModeSetting("Выбрать цвет тумана", "Свой", "Свой", "От темы");
    public static BooleanSetting changeFog = new BooleanSetting("Менять туман", true);
    public static ColorSetting fogColor = new ColorSetting("Цвет тумана", ColorUtils.rgb(18, 18, 35));
    public SliderSetting fogDistance = new SliderSetting("Дистанция тумана", 100, 20, 200, 5).setVisible(changeFog::get);
    public static BooleanSetting customRain = new BooleanSetting("Кастомный дождь", false);
    public final BooleanSetting smoothFloor = new BooleanSetting("Гладкий пол", false);
    private static final Random random = Random.create();
    private static final int[] RANDOM_PALETTE = {
            ColorUtils.rgb(20, 115, 255),
            ColorUtils.rgb(35, 230, 70),
            ColorUtils.rgb(255, 200, 0),
            ColorUtils.rgb(255, 120, 20),
            ColorUtils.rgb(255, 45, 45),
            ColorUtils.rgb(255, 35, 150),
            ColorUtils.rgb(160, 60, 255)
    };
    private final List<FireFly> fireFliesList = new ArrayList<>();
    private final List<RainDrop> rainDrops = new ArrayList<>();
    private final List<RainSplash> rainSplashes = new ArrayList<>();

    public Ambience() {
        addSettings(fireFlies,
                fireFliesCount,
                fireFliesMode,
                fireFliesSize,
                fireFliesD,
                fireFliesTexture,
                fireFliesCubeSize,
                fireFliesCubeDistance,
                fireFliesCubeSpeed,
                fireFliesCubeFill,
                fireFliesCubeFillAlpha,
                fireFliesCubeInnerLines,
                fireFliesCubeDiagonalMode,
                fireFliesRotation,
                fireFliesRotationSpeed,
                fireFliesColorMode,
                fireFliesCustomColor,
                changeTime,
                modifiedTime,
                changeFogM, changeFog, fogColor, fogDistance,
                customRain,
                smoothFloor);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        fireFliesList.clear();
        rainDrops.clear();
        rainSplashes.clear();
        WetSurfaceRenderer.getInstance().reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        fireFliesList.clear();
        rainDrops.clear();
        rainSplashes.clear();
        WetSurfaceRenderer.getInstance().reset();
    }

    /**
     * Гладкий пол — мокрый пол с отражениями. Эталон из C:\Users\katya\Desktop\src (Rain.wetGround).
     * Параметры захардкожены, настройка удалена: доступен только чек-бокс без слайдеров.
     */
    public void renderWetSurfaceFrame(Camera camera, Matrix4f viewMatrix, Matrix4f projectionMatrix) {
        if (nullCheck()) return;
        if (!isState() || !smoothFloor.get() || mc.player == null || mc.world == null) return;
        WetSurfaceRenderer.Parameters parameters = new WetSurfaceRenderer.Parameters();
        parameters.wetness = 1.25f;
        parameters.puddleCoverage = 0.90f;
        parameters.reflectionStrength = 1.55f;
        parameters.maxDistance = 56.0f;
        parameters.rippleStrength = 0.8f;
        parameters.rainAmount = 1.0f;
        parameters.ripples = true;
        parameters.reflectionSteps = 10;
        if (viewMatrix != null && projectionMatrix != null) {
            WetSurfaceRenderer.getInstance().apply(mc, camera, viewMatrix, projectionMatrix, parameters);
        } else {
            WetSurfaceRenderer.getInstance().applyWithFallback(mc, camera, parameters);
        }
    }

    @EventHandler
    public void onPacketReceive(EventPacket event) {
        if (nullCheck()) return;
        if (!event.isReceive()) return;

        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket && changeTime.get()) {
            event.cancel();
        }
    }

    @EventHandler
    public void onFog(EventFog e) {
        if (nullCheck()) return;

        if (changeFog.get()) {
            int colorArgb;
            if (Ambience.changeFogM.is("Свой")) {
                colorArgb = fogColor.get();
            } else {
                colorArgb = ClientColors.ICON.getRGB();
            }

            colorArgb = (colorArgb & 0x00FFFFFF) | 0xFF000000;

            e.setDistance(fogDistance.get().floatValue());
            e.setColor(colorArgb);
            e.cancel();
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (nullCheck()) return;

        if (changeTime.get()) {
            mc.world.setTime(modifiedTime.get().longValue(), modifiedTime.get().longValue(), true);
        }

        Vec3d playerPos = mc.player.getEyePos();

        if (fireFlies.get()) {
            fireFliesList.removeIf(f -> System.currentTimeMillis() - f.spawnTime >= f.lifeTime);

            float targetCount = fireFliesCount.get().floatValue();
            float distance = getFireFliesDistance();
            float speedMultiplier = getFireFliesSpeed();

            int attempts = 0;
            while (fireFliesList.size() < targetCount && attempts < 150) {
                attempts++;

                double theta = 2 * Math.PI * random.nextDouble();
                double phi = Math.acos(2 * random.nextDouble() - 1);

                double x = playerPos.x + (double) distance * Math.sin(phi) * Math.cos(theta);
                double y = playerPos.y + PARTICLE_HEIGHT_OFFSET
                        + (double) distance * Math.sin(phi) * Math.sin(theta);
                double z = playerPos.z + (double) distance * Math.cos(phi);

                if (y < mc.world.getBottomY() || y > playerPos.y + PARTICLE_HEIGHT_OFFSET + 15) continue;

                float motionX = random.nextFloat() * 0.4f - 0.2f;
                float motionY = random.nextFloat() * 0.25f - 0.08f;
                float motionZ = random.nextFloat() * 0.4f - 0.2f;

                fireFliesList.add(new FireFly((float)x, (float)y, (float)z, motionX, motionY, motionZ, System.currentTimeMillis()));
            }

            for (FireFly fireFly : fireFliesList) {
                fireFly.tick(speedMultiplier);
            }
        }

        if (customRain.get()) {
            long now = System.currentTimeMillis();
            rainSplashes.removeIf(splash -> splash.isExpired(now));
            rainDrops.removeIf(drop -> {
                if (drop.hasReachedImpact(playerPos)) {
                    rainSplashes.add(new RainSplash(findRainImpact(drop, mc.world.getBottomY()), now));
                }
                return drop.isFinished(playerPos, mc.world.getBottomY());
            });
            if (rainDrops.size() < RAIN_DROP_COUNT) {
                rainDrops.add(RainDrop.spawn(playerPos));
            }
            for (RainDrop rainDrop : rainDrops) {
                rainDrop.tick();
            }
        } else if (!rainDrops.isEmpty() || !rainSplashes.isEmpty()) {
            rainDrops.clear();
            rainSplashes.clear();
        }
    }

    @EventHandler
    public void onRender3D(EventRender.World event) {
        if (nullCheck()) return;

        if ((!fireFlies.get() || fireFliesList.isEmpty())
                && (!customRain.get() || (rainDrops.isEmpty() && rainSplashes.isEmpty()))) {
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getCameraPos();
        float tickDelta = event.getTicks();

        try (WorldRenderer renderer = WorldRenderer.begin(mc, mc.getRenderTickCounter(), camera,
                event.getMatrixStack().peek().getPositionMatrix(), mc.gameRenderer.getBasicProjectionMatrix(((GameRendererAccessor) mc.gameRenderer).invokeGetFov(camera, tickDelta, true)))) {
            if (customRain.get() && !rainDrops.isEmpty()) {
                renderRain(renderer, camera, cameraPos, tickDelta);
            }
            if (customRain.get() && !rainSplashes.isEmpty()) {
                renderRainSplashes(renderer, camera, cameraPos);
            }
            if (fireFliesMode.is("Кубы")) {
                if (fireFlies.get() && !fireFliesList.isEmpty()) {
                    renderCubes(renderer, camera, cameraPos, tickDelta);
                }
            } else if (fireFlies.get() && !fireFliesList.isEmpty()) {
                renderImages(renderer, camera, cameraPos, tickDelta);
            }
        }
    }

    private void renderRain(WorldRenderer renderer, Camera camera, Vec3d cameraPos, float tickDelta) {
        WorldGeometryEmitter emitter = new WorldGeometryEmitter(
                camera,
                new MatrixStack().peek(),
                renderer.getBuffer(WorldRenderLayers.POSITION_COLOR_QUADS_ADDITIVE())
        );
        int themeColor = ClientColors.ICON.getRGB();
        for (RainDrop drop : rainDrops) {
            Vec3d start = drop.getInterpolatedPosition(tickDelta).subtract(cameraPos);
            double dropLength = drop.getLength() * 1.5;
            Vec3d end = start.add(drop.motionX * 0.08, -dropLength, drop.motionZ * 0.08);
            Vec3d direction = end.subtract(start);
            Vec3d center = start.add(end).multiply(0.5);
            if (direction.lengthSquared() < 1.0E-6 || center.lengthSquared() < 1.0E-6) {
                continue;
            }

            Vec3d side = direction.crossProduct(center);
            if (side.lengthSquared() < 1.0E-6) {
                side = direction.crossProduct(new Vec3d(1.0, 0.0, 0.0));
            }
            side = side.normalize().multiply(0.012);

            emitter.emitQuad(
                    start.subtract(side), start.add(side),
                    end.add(side), end.subtract(side),
                    ColorUtils.injectAlpha(themeColor, 55),
                    ColorUtils.injectAlpha(themeColor, 210),
                    ColorUtils.injectAlpha(themeColor, 210),
                    ColorUtils.injectAlpha(themeColor, 55)
            );
        }
        renderer.flush();
    }

    private void renderRainSplashes(WorldRenderer renderer, Camera camera, Vec3d cameraPos) {
        WorldGeometryEmitter emitter = new WorldGeometryEmitter(
                camera,
                new MatrixStack().peek(),
                renderer.getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE_NO_DEPTH(RAIN_SPLASH_TEXTURE))
        );
        int themeColor = ClientColors.ICON.getRGB();
        long now = System.currentTimeMillis();
        for (RainSplash splash : rainSplashes) {
            float progress = splash.getProgress(now);
            float size = 0.10f + progress * 0.24f;
            int alpha = (int) ((1.0f - progress) * 230.0f);
            if (alpha <= 0) {
                continue;
            }

            emitRainSplash(emitter, splash.position.subtract(cameraPos), size,
                    ColorUtils.injectAlpha(themeColor, alpha));
        }
        renderer.flush();
    }

    private static void emitRainSplash(WorldGeometryEmitter emitter, Vec3d center, float size, int color) {
        float half = size * 0.5f;
        emitter.emitTexturedQuad(
                center.add(-half, 0.0, -half),
                center.add(half, 0.0, -half),
                center.add(half, 0.0, half),
                center.add(-half, 0.0, half),
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                color, color, color, color
        );
    }

    private Vec3d findRainImpact(RainDrop drop, int bottomY) {
        int x = (int) Math.floor(drop.posX);
        int y = (int) Math.floor(drop.posY + RAIN_HEIGHT_OFFSET + 2.0);
        int z = (int) Math.floor(drop.posZ);
        BlockPos position = BlockPos.ofFloored(x, y, z);
        while (position.getY() > bottomY && mc.world.getBlockState(position).isAir()) {
            position = position.down();
        }
        return new Vec3d(position.getX() + 0.5, position.getY() + 1.01, position.getZ() + 0.5);
    }

    private void renderImages(WorldRenderer renderer, Camera camera, Vec3d cameraPos, float tickDelta) {
        Identifier texture = getTexture();
        VertexConsumer consumer = renderer.getBuffer(WorldRenderLayers.TEXTURED_QUADS_ADDITIVE(texture));
        MatrixStack identity = new MatrixStack();
        WorldGeometryEmitter emitter = new WorldGeometryEmitter(camera, identity.peek(), consumer);

        int renderedCount = 0;
        float maxDist = fireFliesD.get().floatValue();
        for (FireFly fireFly : fireFliesList) {
            float x = fireFly.prevPosX + (fireFly.posX - fireFly.prevPosX) * tickDelta;
            float y = fireFly.prevPosY + (fireFly.posY - fireFly.prevPosY) * tickDelta;
            float z = fireFly.prevPosZ + (fireFly.posZ - fireFly.prevPosZ) * tickDelta;
            float size = fireFliesSize.get().floatValue();

            if (!mc.world.isAir(BlockPos.ofFloored(x, y, z))) {
                continue;
            }
            double dx = x - cameraPos.x;
            double dy = y - cameraPos.y;
            double dz = z - cameraPos.z;
            if (dx * dx + dz * dz <= (maxDist + size) * (maxDist + size)) {
                fireFly.render(emitter, camera, size, tickDelta, fireFliesRotation.get(), getRotationMultiplier());
                renderedCount++;
            }
        }

        if (renderedCount > 0) {
            renderer.flush();
        }
    }

    private void renderCubes(WorldRenderer renderer, Camera camera, Vec3d cameraPos, float tickDelta) {
        float cubeSize = fireFliesCubeSize.get().floatValue();
        float half = cubeSize * 0.5f;
        boolean fill = fireFliesCubeFill.get();
        float fillAlpha = fireFliesCubeFillAlpha.get().floatValue();
        int diagonalMode = fireFliesCubeInnerLines.get() ? (fireFliesCubeDiagonalMode.is("2") ? 2 : 1) : 0;
        boolean rotationEnabled = fireFliesRotation.get();
        float rotationMultiplier = getRotationMultiplier();
        float cullHalf = rotationEnabled ? half * 1.8f : half;
        List<CubeRenderData> visibleCubes = new ArrayList<>();
        float maxDist = fireFliesCubeDistance.get().floatValue();

        for (FireFly fireFly : fireFliesList) {
            float x = fireFly.prevPosX + (fireFly.posX - fireFly.prevPosX) * tickDelta;
            float y = fireFly.prevPosY + (fireFly.posY - fireFly.prevPosY) * tickDelta;
            float z = fireFly.prevPosZ + (fireFly.posZ - fireFly.prevPosZ) * tickDelta;

            if (!mc.world.isAir(BlockPos.ofFloored(x, y, z))) {
                continue;
            }

            double dx = x - cameraPos.x;
            double dy = y - cameraPos.y;
            double dz = z - cameraPos.z;
            if (dx * dx + dz * dz > (maxDist + cullHalf) * (maxDist + cullHalf)) {
                continue;
            }

            int lineColor = getFireFlyColor(fireFly, 1.0f);
            int fillColorStart = 0;
            int fillColorEnd = 0;
            if (fill) {
                int alphaInt = Math.max(0, Math.min(255, (int) (fillAlpha * 255.0f)));
                fillColorStart = ColorUtils.injectAlpha(ClientColors.GRADIENT_START.getRGB(), alphaInt);
                fillColorEnd = ColorUtils.injectAlpha(ClientColors.GRADIENT_END.getRGB(), alphaInt);
            }
            float animatedHalf = half * fireFly.getCubeScale();
            if (animatedHalf <= 0.0001f) {
                continue;
            }
            Vec3d[] vertices = fireFly.getCubeVertices(tickDelta, animatedHalf, rotationEnabled, rotationMultiplier);
            visibleCubes.add(new CubeRenderData(vertices, lineColor, fillColorStart, fillColorEnd));
        }

        if (visibleCubes.isEmpty()) {
            return;
        }

        if (fill) {
            MatrixStack fillStack = new MatrixStack();
            WorldGeometryEmitter fillEmitter = new WorldGeometryEmitter(
                    camera,
                    fillStack.peek(),
                    renderer.getBuffer(WorldRenderLayers.POSITION_COLOR_QUADS_ADDITIVE())
            );
            for (CubeRenderData cube : visibleCubes) {
                emitFilledBox(fillEmitter, cube.worldVertices, cameraPos, cube.fillColorStart, cube.fillColorEnd);
            }
        }

        RenderLayer lineLayer = WorldRenderLayers.LINES_ADDITIVE(1.4);
        WorldGeometryEmitter lineEmitter = renderer.lineEmitter(lineLayer);
        for (CubeRenderData cube : visibleCubes) {
            emitCubeOutline(lineEmitter, cube.worldVertices, cameraPos, cube.lineColor);
            if (diagonalMode > 0) {
                emitCubeDiagonals(lineEmitter, cube.worldVertices, cameraPos, cube.lineColor, diagonalMode);
            }
        }

        if (!visibleCubes.isEmpty()) {
            renderer.flush();
        }
    }

    private float getFireFliesDistance() {
        if (fireFliesMode.is("Кубы")) {
            return fireFliesCubeDistance.get().floatValue();
        }
        return fireFliesD.get().floatValue();
    }

    private float getFireFliesSpeed() {
        if (fireFliesMode.is("Кубы")) {
            return fireFliesCubeSpeed.get().floatValue();
        }
        return 1.0f;
    }

    private float getRotationMultiplier() {
        if (!fireFliesRotation.get()) {
            return 0.0f;
        }
        return fireFliesRotationSpeed.get().floatValue();
    }

    private int getFireFlyColor(FireFly fireFly, float alpha) {
        float clampedAlpha = Math.max(0.0f, Math.min(1.0f, alpha));
        int baseColor;
        if (fireFliesColorMode.is("Клиент") || fireFliesColorMode.is("Гирлянда") || fireFliesColorMode.is("Рандомный")) {
            baseColor = fireFly.color;
        } else {
            baseColor = fireFliesCustomColor.get();
        }

        int r = (baseColor >> 16) & 0xFF;
        int g = (baseColor >> 8) & 0xFF;
        int b = baseColor & 0xFF;
        return ColorUtils.rgba(r, g, b, (int) (clampedAlpha * 255));
    }

    private void emitFilledBox(WorldGeometryEmitter emitter, Vec3d[] worldVertices, Vec3d cameraPos, int colorStart, int colorEnd) {
        Vec3d p000 = toCameraSpace(worldVertices[0], cameraPos);
        Vec3d p001 = toCameraSpace(worldVertices[1], cameraPos);
        Vec3d p010 = toCameraSpace(worldVertices[2], cameraPos);
        Vec3d p011 = toCameraSpace(worldVertices[3], cameraPos);
        Vec3d p100 = toCameraSpace(worldVertices[4], cameraPos);
        Vec3d p101 = toCameraSpace(worldVertices[5], cameraPos);
        Vec3d p110 = toCameraSpace(worldVertices[6], cameraPos);
        Vec3d p111 = toCameraSpace(worldVertices[7], cameraPos);

        int c0 = colorStart;
        int c1 = colorEnd;
        int c2 = colorStart;
        int c3 = colorEnd;

        emitter.emitQuad(p000, p100, p110, p010, c0, c1, c2, c3);
        emitter.emitQuad(p001, p011, p111, p101, c0, c1, c2, c3);
        emitter.emitQuad(p000, p001, p101, p100, c0, c1, c2, c3);
        emitter.emitQuad(p010, p110, p111, p011, c0, c1, c2, c3);
        emitter.emitQuad(p000, p010, p011, p001, c0, c1, c2, c3);
        emitter.emitQuad(p100, p101, p111, p110, c0, c1, c2, c3);
    }

    private void emitCubeOutline(WorldGeometryEmitter emitter, Vec3d[] worldVertices, Vec3d cameraPos, int color) {
        Vec3d p000 = toCameraSpace(worldVertices[0], cameraPos);
        Vec3d p001 = toCameraSpace(worldVertices[1], cameraPos);
        Vec3d p010 = toCameraSpace(worldVertices[2], cameraPos);
        Vec3d p011 = toCameraSpace(worldVertices[3], cameraPos);
        Vec3d p100 = toCameraSpace(worldVertices[4], cameraPos);
        Vec3d p101 = toCameraSpace(worldVertices[5], cameraPos);
        Vec3d p110 = toCameraSpace(worldVertices[6], cameraPos);
        Vec3d p111 = toCameraSpace(worldVertices[7], cameraPos);

        emitter.emitLine(p000, p100, color);
        emitter.emitLine(p100, p101, color);
        emitter.emitLine(p101, p001, color);
        emitter.emitLine(p001, p000, color);

        emitter.emitLine(p010, p110, color);
        emitter.emitLine(p110, p111, color);
        emitter.emitLine(p111, p011, color);
        emitter.emitLine(p011, p010, color);

        emitter.emitLine(p000, p010, color);
        emitter.emitLine(p100, p110, color);
        emitter.emitLine(p101, p111, color);
        emitter.emitLine(p001, p011, color);
    }

    private void emitCubeDiagonals(WorldGeometryEmitter emitter, Vec3d[] worldVertices, Vec3d cameraPos, int color, int mode) {
        Vec3d p000 = toCameraSpace(worldVertices[0], cameraPos);
        Vec3d p001 = toCameraSpace(worldVertices[1], cameraPos);
        Vec3d p010 = toCameraSpace(worldVertices[2], cameraPos);
        Vec3d p011 = toCameraSpace(worldVertices[3], cameraPos);
        Vec3d p100 = toCameraSpace(worldVertices[4], cameraPos);
        Vec3d p101 = toCameraSpace(worldVertices[5], cameraPos);
        Vec3d p110 = toCameraSpace(worldVertices[6], cameraPos);
        Vec3d p111 = toCameraSpace(worldVertices[7], cameraPos);

        emitter.emitLine(p000, p111, color);
        emitter.emitLine(p001, p110, color);
        if (mode == 2) {
            emitter.emitLine(p010, p101, color);
            emitter.emitLine(p011, p100, color);
        }
    }


    private Identifier getTexture() {
        switch (fireFliesTexture.get()) {
            case "Глоу":
                return Identifier.of("light", "images/etc/bloom.png");
            case "Звезда":
                return Identifier.of("light", "images/etc/star.png");
            case "Доллар":
                return Identifier.of("light", "images/etc/dollar.png");
            case "Снежинка":
                return Identifier.of("light", "images/etc/snow.png");
            case "Сердце":
                return Identifier.of("light", "images/etc/heart.png");
            case "Корона":
                return Identifier.of("light", "images/etc/crown.png");
            case "Молния":
                return Identifier.of("light", "images/etc/lightning.png");
            default:
                return Identifier.of("light", "images/etc/glow.png");
        }
    }

    private static class FireFly {
        private float posX, posY, posZ;
        private float prevPosX, prevPosY, prevPosZ;
        private float motionX, motionY, motionZ;
        private final long spawnTime;
        private final float rotationSpeed;
        private final float rotationOffsetX;
        private final float rotationOffsetY;
        private final float rotationOffsetZ;
        private final float lifeTime;
        private final int color;

        public FireFly(float posX, float posY, float posZ, float motionX, float motionY, float motionZ, long spawnTime) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.prevPosX = posX;
            this.prevPosY = posY;
            this.prevPosZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.spawnTime = spawnTime;

            float baseSpeed = 35f + random.nextFloat() * 40f;
            this.rotationSpeed = random.nextBoolean() ? baseSpeed : -baseSpeed;
            this.rotationOffsetX = random.nextFloat() * 360.0f;
            this.rotationOffsetY = random.nextFloat() * 360.0f;
            this.rotationOffsetZ = random.nextFloat() * 360.0f;
            this.lifeTime = random.nextBetween(800, 1600);

            if (fireFliesColorMode.is("Гирлянда")) {
                int colorChoice = random.nextInt(3);
                if (colorChoice == 0) {
                    this.color = ColorUtils.rgb(255, 30, 30);
                } else if (colorChoice == 1) {
                    this.color = ColorUtils.rgb(30, 255, 30);
                } else {
                    this.color = ColorUtils.rgb(30, 120, 255);
                }
            } else if (fireFliesColorMode.is("Рандомный")) {
                this.color = RANDOM_PALETTE[random.nextInt(RANDOM_PALETTE.length)];
            } else {
                this.color = ClientColors.ICON.getRGB();
            }
        }

        public void tick(float speedMultiplier) {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;

            float drag = 0.82f;

            motionX *= drag;
            motionY *= drag;
            motionZ *= drag;

            motionY -= 0.0008f;

            posX += motionX * speedMultiplier;
            posY += motionY * speedMultiplier;
            posZ += motionZ * speedMultiplier;

            float lifetime = (System.currentTimeMillis() - spawnTime) / 1000.0f;
            motionY += (float) (Math.sin(lifetime * 1.8f) * 0.0012f) * speedMultiplier;
        }

        public void render(WorldGeometryEmitter emitter, Camera camera, float size, float tickDelta, boolean rotationEnabled, float rotationMultiplier) {
            float x = prevPosX + (posX - prevPosX) * tickDelta;
            float y = prevPosY + (posY - prevPosY) * tickDelta;
            float z = prevPosZ + (posZ - prevPosZ) * tickDelta;

            if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z)) {
                return;
            }

            Vec3d cameraPos = camera.getCameraPos();
            double tX = x - cameraPos.x;
            double tY = y - cameraPos.y;
            double tZ = z - cameraPos.z;

            MatrixStack stack = new MatrixStack();
            stack.translate(tX, tY, tZ);
            stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-camera.getYaw()));
            stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));

            if (rotationEnabled && rotationMultiplier > 0.0f) {
                float rotation = getRotationDegrees(rotationMultiplier);
                stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation));
            }

            float scale = 1.1f * size;
            stack.scale(scale, scale, scale);
            stack.translate(-size / 2, -size / 2, 0);

            float age = Math.min((System.currentTimeMillis() - spawnTime) / this.lifeTime, 1.0f);
            float alpha;
            if (age < 0.167f) {
                alpha = age / 0.167f;
            } else if (age > 0.833f) {
                alpha = (1.0f - (age - 0.833f) / 0.167f);
                alpha = Math.max(alpha, 0.0f);
            } else {
                alpha = 1.0f;
            }

            int finalColor;
            if (fireFliesColorMode.is("Клиент") || fireFliesColorMode.is("Гирлянда") || fireFliesColorMode.is("Рандомный")) {
                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;

                finalColor = ColorUtils.rgba(r, g, b, (int)(alpha * 255));
            } else {
                int customColor = fireFliesCustomColor.get();
                int r = (customColor >> 16) & 0xFF;
                int g = (customColor >> 8) & 0xFF;
                int b = customColor & 0xFF;
                finalColor = ColorUtils.rgba(r, g, b, (int)(alpha * 255));
            }

            Matrix4f localTransform = stack.peek().getPositionMatrix();
            Vec3d v0 = transform(localTransform, 0, size, 0);
            Vec3d v1 = transform(localTransform, size, size, 0);
            Vec3d v2 = transform(localTransform, size, 0, 0);
            Vec3d v3 = transform(localTransform, 0, 0, 0);

            emitter.emitTexturedQuad(
                    v0, v1, v2, v3,
                    1f, 0f,
                    0f, 0f,
                    0f, 1f,
                    1f, 1f,
                    finalColor);
        }

        public Vec3d[] getCubeVertices(float tickDelta, float half, boolean rotationEnabled, float rotationMultiplier) {
            Vec3d center = getInterpolatedPosition(tickDelta);

            Vec3d[] vertices = new Vec3d[] {
                    new Vec3d(-half, -half, -half),
                    new Vec3d(-half, -half, half),
                    new Vec3d(-half, half, -half),
                    new Vec3d(-half, half, half),
                    new Vec3d(half, -half, -half),
                    new Vec3d(half, -half, half),
                    new Vec3d(half, half, -half),
                    new Vec3d(half, half, half)
            };

            if (rotationEnabled && rotationMultiplier > 0.0f) {
                float baseRotation = getRotationDegrees(rotationMultiplier);
                float rotX = baseRotation + rotationOffsetX;
                float rotY = baseRotation * 0.85f + rotationOffsetY;
                float rotZ = baseRotation * 1.15f + rotationOffsetZ;

                for (int i = 0; i < vertices.length; i++) {
                    Vec3d rotated = rotateVector(vertices[i], rotX, rotY, rotZ);
                    vertices[i] = rotated.add(center);
                }
            } else {
                for (int i = 0; i < vertices.length; i++) {
                    vertices[i] = vertices[i].add(center);
                }
            }

            return vertices;
        }

        private Vec3d getInterpolatedPosition(float tickDelta) {
            float x = prevPosX + (posX - prevPosX) * tickDelta;
            float y = prevPosY + (posY - prevPosY) * tickDelta;
            float z = prevPosZ + (posZ - prevPosZ) * tickDelta;
            return new Vec3d(x, y, z);
        }

        private float getRotationDegrees(float rotationMultiplier) {
            float lifetimeSeconds = (System.currentTimeMillis() - spawnTime) / 1000.0f;
            return lifetimeSeconds * rotationSpeed * rotationMultiplier;
        }

        public float getCubeScale() {
            float age = Math.min((System.currentTimeMillis() - spawnTime) / this.lifeTime, 1.0f);

            if (age < 0.167f) {
                float raw = age / 0.167f;
                float t = Math.max(0.0f, Math.min(1.0f, raw));
                t = t * t * (3.0f - 2.0f * t);
                return 1.0f - (float) Math.pow(1.0f - t, 2.0f);
            }

            if (age <= 0.833f) {
                return 1.0f;
            }

            float raw = (age - 0.833f) / 0.167f;
            float t = Math.max(0.0f, Math.min(1.0f, raw));
            t = t * t * (3.0f - 2.0f * t);
            t = 1.0f - (float) Math.pow(1.0f - t, 2.0f);
            return Math.max(0.0f, 1.0f - t);
        }

        private static Vec3d rotateVector(Vec3d vector, float rotXDeg, float rotYDeg, float rotZDeg) {
            Matrix4f rotationMatrix = new Matrix4f()
                    .identity()
                    .rotateXYZ((float) Math.toRadians(rotXDeg), (float) Math.toRadians(rotYDeg), (float) Math.toRadians(rotZDeg));
            return transform(rotationMatrix, vector.x, vector.y, vector.z);
        }
    }

    private static class RainDrop {
        private float posX, posY, posZ;
        private float prevPosX, prevPosY, prevPosZ;
        private float motionX, motionY, motionZ;
        private final float length;

        private RainDrop(float posX, float posY, float posZ, float motionX, float motionY, float motionZ, float length) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.prevPosX = posX;
            this.prevPosY = posY;
            this.prevPosZ = posZ;
            this.motionX = motionX;
            this.motionY = motionY;
            this.motionZ = motionZ;
            this.length = length;
        }

        private static RainDrop spawn(Vec3d playerPos) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = Math.sqrt(random.nextDouble()) * RAIN_RADIUS;
            float x = (float) (playerPos.x + Math.cos(angle) * radius);
            float y = (float) (playerPos.y + RAIN_HEIGHT_OFFSET + 7.0 + random.nextDouble() * 7.0);
            float z = (float) (playerPos.z + Math.sin(angle) * radius);
            float windX = random.nextFloat() * 0.018f - 0.009f;
            float windZ = random.nextFloat() * 0.018f - 0.009f;
            float fallSpeed = 0.24f + random.nextFloat() * 0.09f;
            float length = 0.28f + random.nextFloat() * 0.12f;
            return new RainDrop(x, y, z, windX, -fallSpeed, windZ, length);
        }

        private void tick() {
            prevPosX = posX;
            prevPosY = posY;
            prevPosZ = posZ;

            motionY -= 0.012f;
            motionX *= 0.995f;
            motionY *= 0.998f;
            motionZ *= 0.995f;

            posX += motionX;
            posY += motionY;
            posZ += motionZ;
        }

        private boolean isFinished(Vec3d playerPos, int bottomY) {
            return hasReachedImpact(playerPos)
                    || Math.hypot(posX - playerPos.x, posZ - playerPos.z) > RAIN_RADIUS;
        }

        private boolean hasReachedImpact(Vec3d playerPos) {
            return posY <= playerPos.y - RAIN_IMPACT_OFFSET;
        }

        private Vec3d getInterpolatedPosition(float tickDelta) {
            return new Vec3d(
                    prevPosX + (posX - prevPosX) * tickDelta,
                    prevPosY + (posY - prevPosY) * tickDelta,
                    prevPosZ + (posZ - prevPosZ) * tickDelta
            );
        }

        private float getLength() {
            return length + Math.min(0.16f, Math.abs(motionY) * 0.12f);
        }
    }

    private static class RainSplash {
        private final Vec3d position;
        private final long spawnTime;

        private RainSplash(Vec3d position, long spawnTime) {
            this.position = position;
            this.spawnTime = spawnTime;
        }

        private boolean isExpired(long now) {
            return now - spawnTime >= RAIN_SPLASH_LIFETIME_MS;
        }

        private float getProgress(long now) {
            return Math.max(0.0f, Math.min(1.0f,
                    (now - spawnTime) / (float) RAIN_SPLASH_LIFETIME_MS));
        }
    }

    private static Vec3d transform(Matrix4f matrix, double x, double y, double z) {
        Vector4f vec = new Vector4f((float) x, (float) y, (float) z, 1f);
        matrix.transform(vec);
        return new Vec3d(vec.x, vec.y, vec.z);
    }

    private static Vec3d toCameraSpace(Vec3d worldPos, Vec3d cameraPos) {
        return new Vec3d(worldPos.x - cameraPos.x, worldPos.y - cameraPos.y, worldPos.z - cameraPos.z);
    }

    private static class CubeRenderData {
        private final Vec3d[] worldVertices;
        private final int lineColor;
        private final int fillColorStart;
        private final int fillColorEnd;

        private CubeRenderData(Vec3d[] worldVertices, int lineColor, int fillColorStart, int fillColorEnd) {
            this.worldVertices = worldVertices;
            this.lineColor = lineColor;
            this.fillColorStart = fillColorStart;
            this.fillColorEnd = fillColorEnd;
        }
    }
}


