package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.block.*;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.gen.Heightmap;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.annotations.Customize;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ColorSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.player.PlayerUtil;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.common.impl.fastrandom.FastRandom;
import org.privates.lib.util.time.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Category1
@Customize
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FireFly", category = Category.RENDER, description = "Разные фигуры или картинки которые летают и спавняться по миру то есть WorldParticles")
public class FireFly extends Module {
    public static FireFly getInstance() {
        return Instance.get(FireFly.class);
    }


    private final SliderSetting count = new SliderSetting(this, "Count", 4, 1, 20, 1);
    private final SliderSetting размер = new SliderSetting(this, "Размер", 0.18F, 0.05F, 1F, 0.05F);
    private final SliderSetting distance = new SliderSetting(this, "Distance", 16, 2, 48, 1);
    private final SliderSetting lifeTime = new SliderSetting(this, "Жизнь", 7000, 500, 20000, 500);

    private final SliderSetting speed = new SliderSetting(this, "Speed", 5.0F, 1F, 25.0F, 1F);
    private final SliderSetting alpha = new SliderSetting(this, "Alpha", 0.85F, 0.1F, 1.0F, 0.01F);

    private final BooleanSetting свечение = new BooleanSetting(this, "Свечение", true);
    private final SliderSetting glowRadius = new SliderSetting(this, "GlowRadius", 3, 1, 10, 1).setVisible(свечение::getValue);
    private final SliderSetting glowPower = new SliderSetting(this, "GlowPower", 0.22F, 0.01F, 1F, 0.01F).setVisible(свечение::getValue);

    private final BooleanSetting onlyWhenMoving = new BooleanSetting(this, "OnlyMoving", false);
    private final BooleanSetting onGround = new BooleanSetting(this, "OnGround", false);
    private final BooleanSetting trail = new BooleanSetting(this, "След", true);
    private final SliderSetting длинаСледа = new SliderSetting(this, "ДлинаСледа", 18, 2, 40, 1).setVisible(trail::getValue);
    private final SliderSetting ширинаСледа = new SliderSetting(this, "ШиринаСледа", 0.1F, 0.01F, 0.25F, 0.01F).setVisible(trail::getValue);

    private final BooleanSetting physics = new BooleanSetting(this, "Physics", false);
    private final BooleanSetting advancedPhysics = new BooleanSetting(this, "AdvPhysics", false).setVisible(physics::getValue);

    private final SliderSetting gravity = new SliderSetting(this, "Гравитация", 0.11F, -0.3F, 0.4F, 0.01F).setVisible(physics::getValue);
    private final SliderSetting air = new SliderSetting(this, "Air", 0.04F, 0, 0.2F, 0.01F).setVisible(physics::getValue);
    private final SliderSetting bounce = new SliderSetting(this, "Bounce", 0.9F, 0, 1F, 0.05F).setVisible(physics::getValue);
    private final SliderSetting ветер = new SliderSetting(this, "Ветер", 1.2F, 0, 3F, 0.05F).setVisible(physics::getValue);
    private final SliderSetting масса = new SliderSetting(this, "Масса", 6.9F, 0.1F, 20F, 0.1F).setVisible(physics::getValue);
    private final SliderSetting friction = new SliderSetting(this, "Friction", 0.992F, 0.8F, 1F, 0.001F).setVisible(physics::getValue);
    private final SliderSetting terminalVelocity = new SliderSetting(this, "TerminalVel", 1F, 0.1F, 3F, 0.05F).setVisible(physics::getValue);
    private final SliderSetting turbulence = new SliderSetting(this, "Turbulence", 0.05F, 0, 0.3F, 0.005F).setVisible(physics::getValue);
    private final SliderSetting buoyancy = new SliderSetting(this, "Buoyancy", 0.07F, -0.1F, 0.3F, 0.005F).setVisible(physics::getValue);
    private final SliderSetting viscosity = new SliderSetting(this, "Viscosity", 0.7F, 0.2F, 1F, 0.01F).setVisible(physics::getValue);

    private final BooleanSetting жидкость = new BooleanSetting(this, "Жидкость", true).setVisible(physics::getValue);
    private final BooleanSetting magnet = new BooleanSetting(this, "Magnet", false).setVisible(physics::getValue);
    private final SliderSetting magnetForce = new SliderSetting(this, "MagnetForce", 0.21F, 0.01F, 1F, 0.01F).setVisible(() -> physics.getValue() && magnet.getValue());
    private final SliderSetting magnetRange = new SliderSetting(this, "MagnetRange", 16F, 2F, 32F, 0.5F).setVisible(() -> physics.getValue() && magnet.getValue());
    private final SliderSetting магнитПотухание = new SliderSetting(this, "МагнитПотухание", 2F, 0.5F, 5F, 0.1F).setVisible(() -> physics.getValue() && magnet.getValue());

    private final BooleanSetting swarm = new BooleanSetting(this, "Swarm", true).setVisible(physics::getValue);
    private final SliderSetting swarmRadius = new SliderSetting(this, "SwarmRadius", 3, 0.3F, 8, 0.25F).setVisible(() -> physics.getValue() && swarm.getValue());
    private final SliderSetting swarmSeparation = new SliderSetting(this, "SwarmSeparation", 0.09F, 0.01F, 0.4F, 0.01F).setVisible(() -> physics.getValue() && swarm.getValue());
    private final SliderSetting swarmAlignment = new SliderSetting(this, "SwarmAlignment", 0.05F, 0, 0.2F, 0.005F).setVisible(() -> physics.getValue() && swarm.getValue());
    private final SliderSetting swarmCohesion = new SliderSetting(this, "SwarmCohesion", 0.03F, 0, 0.2F, 0.005F).setVisible(() -> physics.getValue() && swarm.getValue());

    private final BooleanSetting blockBounce = new BooleanSetting(this, "BlockBounce", true).setVisible(physics::getValue);
    private final BooleanSetting blockStop = new BooleanSetting(this, "BlockStop", false).setVisible(physics::getValue);
    private final SliderSetting surfaceFriction = new SliderSetting(this, "SurfaceFriction", 0.64F, 0.1F, 1F, 0.05F).setVisible(physics::getValue);
    private final SliderSetting collisionDamping = new SliderSetting(this, "CollisionDamping", 0.9F, 0.1F, 1F, 0.05F).setVisible(physics::getValue);

    private final SliderSetting unstuckPower = new SliderSetting(this, "Unstuck", 0.15F, 0.01F, 0.35F, 0.01F).setVisible(physics::getValue);
    private final SliderSetting minVelocity = new SliderSetting(this, "MinVel", 0.001F, 0.0001F, 0.02F, 0.0001F).setVisible(physics::getValue);

    private final BooleanSetting aroundPlayer = new BooleanSetting(this, "Вокруг игрока", false);
    private final SliderSetting playerDist = new SliderSetting(this, "ДистДоИгрока", 7, 1, 24, 1).setVisible(aroundPlayer::getValue);
    private final BooleanSetting toPlayer = new BooleanSetting(this, "ToPlayer", false);
    private final BooleanSetting отИгрока = new BooleanSetting(this, "ОтИгрока", false);
    private final BooleanSetting gather = new BooleanSetting(this, "Gather", false);
    private final BooleanSetting взрыв = new BooleanSetting(this, "Взрыв", false).setVisible(gather::getValue);
    private final SliderSetting gatherTime = new SliderSetting(this, "GatherTime", 60, 1, 240, 1).setVisible(gather::getValue);
    private final SliderSetting explodePower = new SliderSetting(this, "ExplodePower", 2.5F, 0.1F, 10, 0.1F).setVisible(взрыв::getValue);

    private final ModeSetting moveMode = new ModeSetting(this, "Move",
            "Random", "След", "Circle", "Spiral", "Wave", "Хаос", "Точка", "Zigzag");
    private final SliderSetting moveSpeed = new SliderSetting(this, "MoveSpeed", 2, 0.1F, 8, 0.05F);

    private final ModeSetting colorMode = new ModeSetting(this, "Цвет", "Rainbow", "Client", "Custom", "Pulse", "Gradient");
    private final ColorSetting color = new ColorSetting(this, "Color").setVisible(() -> colorMode.is("Custom"));
    private final ColorSetting color1 = new ColorSetting(this, "Color1").setVisible(() -> colorMode.is("Gradient"));
    private final ColorSetting color2 = new ColorSetting(this, "Color2").setVisible(() -> colorMode.is("Gradient"));

    private final ModeSetting форма = new ModeSetting(this, "Форма",
            "Random", "Amongus", "Circle", "Crown", "Dollar", "Heart", "Polygon", "Snow", "Quad", "Skull", "Star", "Cross", "Triangle", "Bloom", "Cube"
    ).set("Snow");

    private final SliderSetting yMin = new SliderSetting(this, "YMin", 0.5F, -6, 20, 0.1F);
    private final SliderSetting yMax = new SliderSetting(this, "YMax", 5.5F, 0, 30, 0.1F);

    private final BooleanSetting bounceTop = new BooleanSetting(this, "BounceTop", true).setVisible(physics::getValue);
    private final SliderSetting maxHeight = new SliderSetting(this, "MaxHeight", 80, 8, 200, 1).setVisible(physics::getValue);

    private final BooleanSetting keepAboveGround = new BooleanSetting(this, "НеПадать", true).setVisible(physics::getValue);
    private final SliderSetting minY = new SliderSetting(this, "MinY", 0.2F, -5, 40, 0.1F).setVisible(keepAboveGround::getValue);

    private final List<Fly> particles = new CopyOnWriteArrayList<>();
    private final FastRandom random = new FastRandom();

    @NonFinal
    private Vector3d windDir = new Vector3d(1, 0, 0);
    @NonFinal
    private long lastWind = 0;
    @NonFinal
    private float globalTime = 0;
    @NonFinal
    private long gatherStart = 0;
    @NonFinal
    private boolean hasExploded = false;

    private void clear() {
        particles.clear();
        gatherStart = 0;
        hasExploded = false;
    }

    @Override
    public void toggle() {
        super.toggle();
        clear();
    }

    @EventHandler
    public void onWorldChange(WorldChangeEvent event) {
        clear();
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        int d = this.distance.getValue().intValue();
        if (onlyWhenMoving.getValue() && !hasMoved()) return;


        if (System.currentTimeMillis() - lastWind > 1700) {
            windDir = new Vector3d(
                    random.nextDouble() * 2 - 1,
                    0,
                    random.nextDouble() * 2 - 1
            ).normalize();
            lastWind = System.currentTimeMillis();
        }

        globalTime += 0.09f;

        float yMinVal = Math.min(this.yMin.getValue(), this.yMax.getValue());
        float yMaxVal = Math.max(this.yMin.getValue(), this.yMax.getValue());

        for (int i = 0; i < count.getValue().intValue(); i++) {
            Vector3d plus = mc.player.getPositionVec().add(
                    Mathf.randomValue(-d, d),
                    0,
                    Mathf.randomValue(-d, d)
            );
            BlockPos pos = mc.world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(plus));
            double spawnY = onGround.getValue() ? pos.getY() + 0.5 : mc.player.getPosY() + Mathf.randomValue(yMinVal, yMaxVal);

            Vector3d spawn = new Vector3d(
                    pos.getX() + Mathf.randomValue(0, 1),
                    Math.min(spawnY, mc.player.getPosY() + maxHeight.getValue()),
                    pos.getZ() + Mathf.randomValue(0, 1)
            );

            Vector3d initVel = getInitVel();
            particles.add(new Fly(
                    getShapeType(),
                    spawn,
                    initVel,
                    particles.size(),
                    0,
                    размер.getValue(),
                    (int) Mathf.step(Mathf.randomValue(0, 360), 15),
                    moveMode.getValue()
            ));
        }
    }


    private Vector3d getInitVel() {
        double mul = speed.getValue();
        return switch (moveMode.getValue()) {
            case "Circle" -> {
                double angle = globalTime * moveSpeed.getValue();
                yield new Vector3d(
                        Math.cos(angle) * mul,
                        onGround.getValue() ? mul : -mul * 0.5,
                        Math.sin(angle) * mul
                );
            }
            case "Spiral" -> {
                double angle = globalTime * moveSpeed.getValue();
                double radius = (globalTime % 20) / 20.0;
                yield new Vector3d(
                        Math.cos(angle) * radius * mul,
                        onGround.getValue() ? mul : -mul * 0.3,
                        Math.sin(angle) * radius * mul
                );
            }
            case "Wave" -> new Vector3d(
                    Math.sin(globalTime * moveSpeed.getValue()) * mul,
                    Math.cos(globalTime * moveSpeed.getValue() * 0.5) * mul * 0.5,
                    Math.cos(globalTime * moveSpeed.getValue()) * mul
            );
            case "Хаос" -> new Vector3d(
                    (random.nextDouble() - 0.5) * mul * 4,
                    (random.nextDouble() - 0.5) * mul * 2,
                    (random.nextDouble() - 0.5) * mul * 4
            );
            case "Точка" -> new Vector3d(0, 0, 0);
            case "Zigzag" -> new Vector3d(
                    Math.sin(globalTime * moveSpeed.getValue() * 4) * mul,
                    0,
                    Math.cos(globalTime * moveSpeed.getValue() * 4) * mul
            );
            case "След" -> {
                Vector3d toPlayer = mc.player.getPositionVec().subtract(new Vector3d(0, 0, 0));
                yield toPlayer.normalize().mul(mul);
            }
            default -> new Vector3d(
                    Mathf.randomValue(-mul, mul),
                    onGround.getValue() ? mul : -mul * 0.5,
                    Mathf.randomValue(-mul, mul)
            );
        };
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        MatrixStack matrix = event.getMatrix();
        setupRender();
        renderParticles(matrix, particles, lifeTime.getValue().doubleValue(), lifeTime.min);
        resetRender();
    }

    private void renderParticles(MatrixStack matrix, List<Fly> particles, double lifeTime, double minLife) {
        particles.removeIf(p -> !PlayerUtil.isInView(p.box()) || p.time().finished(lifeTime + minLife));
        if (particles.isEmpty()) return;


        if (gather.getValue()) {
            if (gatherStart == 0) {
                gatherStart = System.currentTimeMillis();
                hasExploded = false;
            }
            long now = System.currentTimeMillis();
            if (now - gatherStart < gatherTime.getValue() * 1000) {
                particles.forEach(f -> f.gathering = true);
            } else if (взрыв.getValue() && !hasExploded) {
                hasExploded = true;
                particles.forEach(f -> f.exploding = true);
            }
        } else {
            gatherStart = 0;
            hasExploded = false;
            particles.forEach(f -> {
                f.gathering = false;
                f.exploding = false;
            });
        }

        matrix.push();
        for (Fly p : particles) {
            p.update(
                    physics.getValue(),
                    advancedPhysics.getValue(),
                    gravity.getValue(),
                    air.getValue(),
                    bounce.getValue(),
                    жидкость.getValue(),
                    windDir.mul(ветер.getValue()),
                    magnet.getValue(),
                    magnetForce.getValue(),
                    magnetRange.getValue(),
                    магнитПотухание.getValue(),
                    swarm.getValue(),
                    swarmRadius.getValue(),
                    swarmSeparation.getValue(),
                    swarmAlignment.getValue(),
                    swarmCohesion.getValue(),
                    particles,
                    aroundPlayer.getValue(),
                    playerDist.getValue(),
                    toPlayer.getValue(),
                    отИгрока.getValue(),
                    масса.getValue(),
                    friction.getValue(),
                    terminalVelocity.getValue(),
                    turbulence.getValue(),
                    buoyancy.getValue(),
                    viscosity.getValue(),
                    surfaceFriction.getValue(),
                    collisionDamping.getValue(),
                    minVelocity.getValue(),
                    unstuckPower.getValue()
            );

            Animation anim = p.animation();
            anim.update();
            float a = anim.get();

            if (a != alpha.getValue() && !p.time().finished(minLife)) {
                anim.run(alpha.getValue(), (minLife / 1000), Easings.CUBIC_OUT, true);
            }
            if (a != 0.0F && p.time().finished(lifeTime)) {
                anim.run(0.0F, (minLife / 1000), Easings.CUBIC_OUT, true);
            }

            int c = getParticleColor(p, a);

            if (trail.getValue() && p.trail().size() > 1) {
                renderTrail(matrix, p, c);
            }
            renderParticle(matrix, p, c);
        }
        matrix.pop();
    }

    private int getParticleColor(Fly p, float a) {
        return switch (colorMode.getValue()) {
            case "Client" -> ColorUtil.fade(p.index * 111 * Theme.getInstance().getSpeed());
            case "Rainbow" ->
                    ColorUtil.rainbow(Math.max(1, Theme.getInstance().getSpeed()), p.index * 111, 0.8f, 1.0f, a);
            case "Custom" -> ColorUtil.multAlpha(color.getValue().hashCode(), a);
            case "Pulse" ->
                    ColorUtil.fade((int) (Theme.getInstance().getSpeed() * 2.2), p.index * 222, Theme.getInstance().clientColor(), Theme.getInstance().darkColor());
            case "Gradient" ->
                    ColorUtil.overCol(color1.getValue().hashCode(), color2.getValue().hashCode(), (float) (Math.sin(p.time.elapsedTime() / 300.0) + 1) / 2.0f);
            default -> -1;
        };
    }

    private void renderTrail(MatrixStack matrix, Fly p, int c) {
        List<Vector3d> t = p.trail();
        int maxTrail = Math.min(t.size(), длинаСледа.getValue().intValue());


        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glLineWidth(ширинаСледа.getValue());
        GL11.glBegin(GL11.GL_LINE_STRIP);

        for (int i = 0; i < maxTrail; i++) {
            Vector3d pos = t.get(i);
            float a = (1.0f - (float) i / maxTrail) * 0.3f;
            int trailColor = ColorUtil.replAlpha(c, (int) (a * 255));

            GL11.glColor4f(
                    ((trailColor >> 16) & 0xFF) / 255f,
                    ((trailColor >> 8) & 0xFF) / 255f,
                    (trailColor & 0xFF) / 255f,
                    ((trailColor >> 24) & 0xFF) / 255f
            );
            GL11.glVertex3d(pos.x, pos.y, pos.z);
        }

        GL11.glEnd();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    private void renderParticle(MatrixStack matrix, Fly p, int c) {
        Vector3d vec = p.position();
        float x = (float) vec.x;
        float y = (float) vec.y;
        float z = (float) vec.z;
        float s = p.size();


        matrix.push();
        RenderUtil3D.setupOrientationMatrix(matrix, x, y, z);
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
        matrix.push();
        matrix.rotate(Vector3f.ZP.rotationDegrees(180F));
        if (p.type().rotatable()) {
            matrix.rotate(Vector3f.ZP.rotationDegrees(p.rotate()));
        }
        matrix.push();
        matrix.translate(0, -s, -s);

        if (свечение.getValue()) {
            RenderUtil.bindTexture(ShapeType.BLOOM.texture());
            float gl = s * glowRadius.getValue();
            RectUtil.drawRect(matrix, -gl, -gl, gl * 2, gl * 2, ColorUtil.multAlpha(c, glowPower.getValue()), true, true);
        }

        RenderUtil.bindTexture(p.type().texture());
        RectUtil.drawRect(matrix, -s, -s, s * 2, s * 2, c, true, true);

        if (p.type().equals(ShapeType.BLOOM)) {
            RectUtil.drawRect(matrix, -s / 2, -s / 2, s, s, c, true, true);
        }

        matrix.pop();
        matrix.pop();
        matrix.pop();
    }

    private void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
    }

    private void resetRender() {
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.clearCurrentColor();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
    }

    private boolean hasMoved() {
        return mc.player.lastTickPosX != mc.player.getPosX()
                || mc.player.lastTickPosY != mc.player.getPosY()
                || mc.player.lastTickPosZ != mc.player.getPosZ();
    }

    private ShapeType getShapeType() {
        return switch (this.форма.getValue()) {
            case "Amongus" -> ShapeType.AMONGUS;
            case "Circle" -> ShapeType.CIRCLE;
            case "Crown" -> ShapeType.CROWN;
            case "Dollar" -> ShapeType.DOLLAR;
            case "Snow" -> ShapeType.SNOW;

            case "Heart" -> ShapeType.HEART;
            case "Polygon" -> ShapeType.POLYGON;
            case "Quad" -> ShapeType.QUAD;
            case "Skull" -> ShapeType.SKULL;
            case "Star" -> ShapeType.STAR;
            case "Cross" -> ShapeType.CROSS;
            case "Triangle" -> ShapeType.TRIANGLE;
            case "Bloom" -> ShapeType.BLOOM;
            case "Cube" -> ShapeType.CUBE;
            default -> ShapeType.getRandom();
        };
    }

    @Getter
    @Accessors(fluent = true)
    public enum ShapeType {
        AMONGUS("amongus", false),
        CIRCLE("circle", false),
        CROWN("crown", false),
        DOLLAR("dollar", false),
        SNOW("snow", false),

        HEART("heart", false),
        POLYGON("polygon", true),
        QUAD("quad", true),
        SKULL("skull", false),
        STAR("star", true),
        CROSS("cross", true),
        TRIANGLE("triangle", true),
        BLOOM("bloom", false),
        CUBE("cube", true);


        private final ResourceLocation texture;
        private final boolean rotatable;

        ShapeType(String name, boolean rotatable) {
            texture = new Namespaced("particle/" + name + ".png");
            this.rotatable = rotatable;
        }

        public static ShapeType getRandom() {
            ShapeType[] values = ShapeType.values();
            return values[new FastRandom().nextInt(values.length)];
        }
    }

    @Getter
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Fly {
        long spawnTime = System.currentTimeMillis();
        ShapeType type;
        @NonFinal
        AxisAlignedBB box;
        @NonFinal
        Vector3d position;
        @NonFinal
        Vector3d velocity;
        @NonFinal
        Vector3d acceleration;
        @NonFinal
        float rotate;
        @NonFinal
        final List<Vector3d> trail = new ArrayList<>();
        int index;
        int color;
        float size;
        String moveType;
        @NonFinal
        float mass;
        @NonFinal
        boolean inWater = false;
        @NonFinal
        boolean onGround = false;
        @NonFinal
        final double phaseOffset;


        @NonFinal
        boolean gathering = false;
        @NonFinal
        boolean exploding = false;
        @NonFinal
        final float epsilon = 0.035F;

        StopWatch time = new StopWatch();
        Animation animation = new Animation();

        public Fly(ShapeType type, Vector3d pos, Vector3d vel, int index, int color, float size, float rotate, String moveType) {
            this.type = type;
            this.rotate = rotate;
            this.box = new AxisAlignedBB(pos, pos).grow(size);
            this.position = pos;
            this.velocity = vel.mul(0.01F);
            this.acceleration = Vector3d.ZERO;
            this.index = index;
            this.color = color;
            this.size = size;
            this.moveType = moveType;
            this.mass = size * 4.2f;
            this.phaseOffset = Math.random() * Math.PI * 2;
            this.time.reset();
            this.trail.add(pos);
        }

        public void update(boolean physics, boolean adv, double g, double air, float bounceStr,
                           boolean liquid, Vector3d wind, boolean magnet, float magnetF, float magnetRange, float magnetFalloff,
                           boolean swarm, float swarmR, float swarmSep, float swarmAlign, float swarmCohesion, List<Fly> all,
                           boolean nearPlayer, float distToPlayer, boolean toPlayer, boolean awayFromPlayer,
                           float massFact, float frict, float termVel, float turb, float buoy, float visc,
                           float surfFrict, float collDamp, float minVel, float unstuck) {

            mass = size * massFact;

            if (physics) {
                acceleration = new Vector3d(0, -g / mass, 0);

                if (adv) {
                    Vector3d drag = velocity.mul(-air);
                    acceleration = acceleration.add(drag);

                    Vector3d turbulenceForce = new Vector3d(
                            (Math.random() - 0.5) * turb,
                            (Math.random() - 0.5) * turb * 0.5,
                            (Math.random() - 0.5) * turb
                    );
                    acceleration = acceleration.add(turbulenceForce);

                    acceleration = acceleration.add(wind.mul(1.0 / mass));
                    Vector3d moveForce = moveForce();
                    acceleration = acceleration.add(moveForce);

                    if (magnet) {
                        Vector3d toPlayerVec = mc.player.getPositionVec().subtract(position);
                        double dist = toPlayerVec.length();
                        if (dist < magnetRange && dist > 0.5) {
                            double falloff = Math.pow(dist / magnetRange, magnetFalloff);
                            Vector3d magnetVec = toPlayerVec.normalize().mul(magnetF / (falloff + 0.01));
                            acceleration = acceleration.add(magnetVec);
                        }
                    }
                    if (swarm) {
                        Vector3d swarmForce = calcSwarm(all, swarmR, swarmSep, swarmAlign, swarmCohesion);
                        acceleration = acceleration.add(swarmForce);
                    }

                    if (nearPlayer) {
                        Vector3d center = mc.player.getPositionVec();
                        double dist = position.distanceTo(center);
                        if (dist > distToPlayer) {
                            acceleration = acceleration.add(center.subtract(position).normalize().mul(0.04));
                        } else if (dist < distToPlayer * 0.6) {
                            acceleration = acceleration.add(position.subtract(center).normalize().mul(0.03));
                        }
                    }

                    if (toPlayer) {
                        Vector3d toPlayerVec = mc.player.getPositionVec().subtract(position);
                        acceleration = acceleration.add(toPlayerVec.normalize().mul(0.07));
                    }

                    if (awayFromPlayer) {
                        Vector3d fromPlayerVec = position.subtract(mc.player.getPositionVec());
                        acceleration = acceleration.add(fromPlayerVec.normalize().mul(0.1));
                    }

                    if (gathering) {
                        Vector3d gatherPoint = mc.player.getPositionVec();
                        acceleration = acceleration.add(gatherPoint.subtract(position).normalize().mul(0.12));
                    }
                    if (exploding) {
                        Vector3d expl = position.subtract(mc.player.getPositionVec()).normalize().mul(FireFly.getInstance().explodePower.getValue());
                        acceleration = acceleration.add(expl);
                        exploding = false;
                    }
                }

                if (liquid) {
                    Block blockAt = PlayerUtil.getBlock(position.x, position.y, position.z);
                    inWater = blockAt instanceof FlowingFluidBlock ||
                            blockAt.getFluidState(blockAt.getDefaultState()).isTagged(net.minecraft.tags.FluidTags.WATER);

                    if (inWater) {
                        velocity = velocity.mul(visc);
                        acceleration = acceleration.mul(0.5);
                        acceleration = acceleration.add(0, buoy / mass, 0);
                    }
                }

                velocity = velocity.add(acceleration);

                if (velocity.length() > termVel) {
                    velocity = velocity.normalize().mul(termVel);
                }

                double maxY = mc.player.getPosY() + FireFly.getInstance().maxHeight.getValue();
                if (position.y >= maxY - 0.01) {
                    if (Math.abs(velocity.y) < epsilon) {
                        Vector3d nudge = new Vector3d(
                                (Math.random() - 0.5) * unstuck,
                                -unstuck,
                                (Math.random() - 0.5) * unstuck
                        );
                        velocity = velocity.add(nudge);
                    }
                }
                if (velocity.lengthSquared() < minVel) {
                    Vector3d nudge = new Vector3d(
                            (Math.random() - 0.5) * unstuck,
                            (Math.random() - 0.5) * unstuck,
                            (Math.random() - 0.5) * unstuck
                    );
                    velocity = velocity.add(nudge);
                }

                double minAllowedY = mc.world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(position.x, 0, position.z)).getY() + minY();
                if (keepAboveGround()) {
                    if (position.y + velocity.y < minAllowedY) {
                        position = new Vector3d(position.x, minAllowedY, position.z);
                        velocity = new Vector3d(velocity.x, Math.abs(velocity.y) * bounceStr, velocity.z);
                    }
                }

                if (blockBounce()) {
                    handleCollisions(bounceStr, surfFrict, collDamp);
                }
                if (blockStop()) {
                    stopOnBlock();
                }

                if (bounceTop() && position.y > mc.player.getPosY() + maxHeight()) {
                    position = new Vector3d(position.x, mc.player.getPosY() + maxHeight(), position.z);
                    velocity = new Vector3d(velocity.x, -Math.abs(velocity.y) * bounceStr, velocity.z);
                }

                velocity = velocity.mul(frict);
            }

            position = position.add(velocity);
            box = new AxisAlignedBB(position, position).grow(size);

            if (type.rotatable()) {
                rotate += velocity.length() * 46 + 2;
                if (rotate >= 360) rotate -= 360;
            }

            trail.add(0, position);
            if (trail.size() > 40) {
                trail.remove(trail.size() - 1);
            }
        }

        private Vector3d moveForce() {
            double t = System.currentTimeMillis() / 1000.0;
            double x = position.x;
            double z = position.z;

            return switch (moveType) {
                case "След" -> {
                    Vector3d toPlayer = mc.player.getPositionVec().subtract(position);
                    yield toPlayer.normalize().mul(0.018);
                }
                case "Circle" -> {
                    double angle = t + phaseOffset;
                    yield new Vector3d(
                            -Math.sin(angle) * 0.04,
                            Math.cos(t * 0.4) * 0.016,
                            Math.cos(angle) * 0.04
                    );
                }

                case "Spiral" -> {
                    double angle = t * 2 + phaseOffset;
                    double rad = Math.sin(t * 0.25) * 0.1;
                    yield new Vector3d(
                            Math.cos(angle) * rad,
                            0.02,
                            Math.sin(angle) * rad
                    );
                }
                case "Wave" -> new Vector3d(
                        Math.sin(t + x * 0.13) * 0.02,
                        Math.sin(t * 1.2 + phaseOffset) * 0.014,
                        Math.cos(t + z * 0.13) * 0.02
                );
                case "Хаос" -> new Vector3d(
                        (Math.random() - 0.5) * 0.09,
                        (Math.random() - 0.5) * 0.05,
                        (Math.random() - 0.5) * 0.09
                );
                case "Точка" -> Vector3d.ZERO;
                case "Zigzag" -> new Vector3d(
                        Math.sin(t * 4 + phaseOffset) * 0.05,
                        0,
                        Math.cos(t * 4 + phaseOffset) * 0.05
                );
                default -> Vector3d.ZERO;
            };
        }

        private Vector3d calcSwarm(List<Fly> all, float rad, float sepStr, float alignStr, float cohStr) {
            Vector3d sep = Vector3d.ZERO;
            Vector3d align = Vector3d.ZERO;
            Vector3d coh = Vector3d.ZERO;
            int cnt = 0;

            for (Fly o : all) {
                if (o == this) continue;
                double d = position.distanceTo(o.position);
                if (d < rad && d > 0) {
                    Vector3d diff = position.subtract(o.position).normalize().mul(1.0 / d);
                    sep = sep.add(diff);
                    align = align.add(o.velocity);
                    coh = coh.add(o.position);
                    cnt++;
                }
            }

            if (cnt > 0) {
                sep = sep.mul(1.0 / cnt).mul(sepStr);
                align = align.mul(1.0 / cnt).subtract(velocity).mul(alignStr);
                coh = coh.mul(1.0 / cnt).subtract(position).mul(cohStr);
            }
            return sep.add(align).add(coh);
        }

        private void handleCollisions(float bounceStr, float surfFrict, float collDamp) {
            if (isSolid(PlayerUtil.getBlock(position.x + velocity.x, position.y, position.z))) {
                velocity = new Vector3d(-velocity.x * bounceStr * collDamp, velocity.y, velocity.z);
                position = new Vector3d(Math.floor(position.x) + (velocity.x > 0 ? 0 : 1), position.y, position.z);
            }
            if (isSolid(PlayerUtil.getBlock(position.x, position.y + velocity.y, position.z))) {
                onGround = velocity.y < 0;
                velocity = new Vector3d(velocity.x * 0.9, -velocity.y * bounceStr * collDamp, velocity.z * 0.9);
                position = new Vector3d(position.x, Math.floor(position.y) + (velocity.y > 0 ? 0 : 1), position.z);

                if (onGround && Math.abs(velocity.y) < 0.01) {
                    velocity = velocity.mul(surfFrict, 0, surfFrict);
                }
            } else {
                onGround = false;
            }
            if (isSolid(PlayerUtil.getBlock(position.x, position.y, position.z + velocity.z))) {
                velocity = new Vector3d(velocity.x, velocity.y, -velocity.z * bounceStr * collDamp);
                position = new Vector3d(position.x, position.y, Math.floor(position.z) + (velocity.z > 0 ? 0 : 1));
            }
        }

        private void stopOnBlock() {
            if (isSolid(PlayerUtil.getBlock(position.x + velocity.x, position.y, position.z))) {
                velocity = new Vector3d(0, velocity.y, velocity.z);
            }
            if (isSolid(PlayerUtil.getBlock(position.x, position.y + velocity.y, position.z))) {
                velocity = new Vector3d(velocity.x, 0, velocity.z);
            }
            if (isSolid(PlayerUtil.getBlock(position.x, position.y, position.z + velocity.z))) {
                velocity = new Vector3d(velocity.x, velocity.y, 0);
            }
        }

        private boolean isSolid(Block block) {
            return !(block instanceof AirBlock)
                    && !(block instanceof BushBlock)
                    && !(block instanceof AbstractButtonBlock)
                    && !(block instanceof TorchBlock)
                    && !(block instanceof LeverBlock)
                    && !(block instanceof AbstractPressurePlateBlock)
                    && !(block instanceof CarpetBlock)
                    && !(block instanceof FlowingFluidBlock)
                    && !(block instanceof LadderBlock)
                    && !(block instanceof VineBlock)
                    && !(block instanceof SnowBlock)
                    && !(block instanceof AbstractSignBlock);
        }

        private boolean keepAboveGround() {
            return FireFly.getInstance().keepAboveGround.getValue();
        }

        private double minY() {
            return FireFly.getInstance().minY.getValue();
        }

        private boolean blockBounce() {
            return FireFly.getInstance().blockBounce.getValue();
        }

        private boolean blockStop() {
            return FireFly.getInstance().blockStop.getValue();
        }

        private boolean bounceTop() {
            return FireFly.getInstance().bounceTop.getValue();
        }

        private double maxHeight() {
            return FireFly.getInstance().maxHeight.getValue();
        }
    }
}