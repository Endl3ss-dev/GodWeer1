package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.gen.Heightmap;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.TickEvent;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
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

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "Particles", category = Category.RENDER, description = "Кастомные частицы")
public class Particles extends Module {
    public static Particles getInstance() {
        return Instance.get(Particles.class);
    }

    private final MultiBooleanSetting events = new MultiBooleanSetting(this, "Спавнить при",
            BooleanSetting.of("Атаке", true),
            BooleanSetting.of("Бросок", true),
            BooleanSetting.of("Тотем", true),
            BooleanSetting.of("Движении", false),
            BooleanSetting.of("Бездействии", true)
    );

    private final ModeSetting visiksmode = new ModeSetting(this, "Тип партиклов", "1", "2", "3", "Дождь", "Снег", "Листопад");
    private final ModeSetting particleMode = new ModeSetting(this, "Тип частиц", "Снежок", "Звездачка 2", "Доллар", "Сердечко", "Амонгус", "Круг", "Корона", "Полигон", "Квад", "Череп", "Звезда", "Крест", "Треугольник", "Кристалл", "Куб").setVisible(() -> !visiksmode.is("Дождь") && !visiksmode.is("Снег") && !visiksmode.is("Листопад"));

    private final SliderSetting speed = new SliderSetting(this, "Скорость", 1.5F, 0.1F, 3F, 0.1F);
    private final SliderSetting size = new SliderSetting(this, "Размер", 0.2F, 0.0F, 1F, 0.1F);
    private final SliderSetting attackcos = new SliderSetting(this, "Кол-в атаки", 30, 5, 50, 1).setVisible(() -> events.getValue("Атаке"));
    private final SliderSetting totem = new SliderSetting(this, "Кол-в тотеме", 8, 2, 16, 1).setVisible(() -> events.getValue("Тотем"));
    private final SliderSetting move = new SliderSetting(this, "Кол-в движении", 2, 1, 6, 1).setVisible(() -> events.getValue("Движении"));
    private final SliderSetting brosok = new SliderSetting(this, "Кол-в броске", 6, 1, 16, 1).setVisible(() -> events.getValue("Бросок"));
    private final SliderSetting countAFK = new SliderSetting(this, "Кол-во при бездействии", 5, 1, 25, 1).setVisible(() -> events.getValue("Бездействии"));
    private final SliderSetting range = new SliderSetting(this, "Дистанция при бездействии ", 16, 4, 32, 1).setVisible(() -> events.getValue("Бездействии"));

    private final BooleanSetting rotation = new BooleanSetting(this, "Кручения", false);
    private final BooleanSetting glowEffect = new BooleanSetting(this, "Эффект свечения", true);
    private final SliderSetting glowRadius = new SliderSetting(this, "GlowRadius", 3, 1, 10, 1).setVisible(glowEffect::getValue);
    private final SliderSetting glowPower = new SliderSetting(this, "GlowPower", 0.22F, 0.01F, 1F, 0.01F).setVisible(glowEffect::getValue);
    private final BooleanSetting viefForStinka = new BooleanSetting(this, "Видить через стену", false);
    private final BooleanSetting strongYFactor = new BooleanSetting(this, "Сильный фактор по Y", false);

    private final Namespaced glowTexture = new Namespaced("texture/glow.png");

    private final List<Particle> targetParticles = new ArrayList<>();
    private final List<Particle> worldParticles = new ArrayList<>();
    private final List<Particle> flameParticles = new ArrayList<>();
    private final List<Puddle> puddles = new ArrayList<>();
    private long lastUpdateTime = System.nanoTime();

    private void clear() {
        puddles.clear();
        targetParticles.clear();
        worldParticles.clear();
        flameParticles.clear();
        puddles.clear();
    }

    @Override
    public void toggle() {
        super.toggle();
        clear();
    }

    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        clear();
    }

    private void spawnParticle(List<Particle> particles, Vector3d position, Vector3d velocity) {
        float s = 0.05F + (this.size.getValue() * 0.2F);
        int col = ColorUtil.fade(particles.size() * 100);

        ParticleType type = switch (this.particleMode.getValue()) {
            case "Доллар" -> ParticleType.DOLLAR;
            case "Сердечко" -> ParticleType.HEART;
            case "Звездачка 2" -> ParticleType.STARNEW;
            case "Звездачка" -> ParticleType.STAR;
            case "Бубенец" -> ParticleType.BLOOM;
            case "Снежок" -> ParticleType.SNOOW;
            case "Амонгус" -> ParticleType.AMONGUS;
            case "Круг" -> ParticleType.CIRCLE;
            case "Корона" -> ParticleType.CROWN;
            case "Полигон" -> ParticleType.POLYGON;
            case "Квад" -> ParticleType.QUAD;
            case "Череп" -> ParticleType.SKULL;
            case "Звезда" -> ParticleType.STAR;
            case "Крест" -> ParticleType.CROSS;
            case "Треугольник" -> ParticleType.TRIANGLE;
            case "Кристалл" -> ParticleType.CRYSTAL;
            case "Куб" -> ParticleType.CUBE;
            case "Рандом" -> ParticleType.getRandom();
            default -> ParticleType.getRandom();
        };

        particles.add(new Particle(type, position.add(0, s, 0), velocity, particles.size(), col, s, speed.getValue()));
    }

    private void spawnParticleTotem(List<Particle> particles, Vector3d position, Vector3d velocity, int col) {
        float s = 0.05F + (this.size.getValue() * 0.2F);

        ParticleType type = switch (this.particleMode.getValue()) {
            case "Доллар" -> ParticleType.DOLLAR;
            case "Сердечко" -> ParticleType.HEART;
            case "Звездачка" -> ParticleType.STAR;
            case "Звездачка 2" -> ParticleType.STARNEW;
            case "Бубенец" -> ParticleType.BLOOM;
            case "Снежок" -> ParticleType.SNOOW;
            case "Амонгус" -> ParticleType.AMONGUS;
            case "Круг" -> ParticleType.CIRCLE;
            case "Корона" -> ParticleType.CROWN;
            case "Полигон" -> ParticleType.POLYGON;
            case "Квад" -> ParticleType.QUAD;
            case "Череп" -> ParticleType.SKULL;
            case "Звезда" -> ParticleType.STAR;
            case "Крест" -> ParticleType.CROSS;
            case "Треугольник" -> ParticleType.TRIANGLE;
            case "Кристалл" -> ParticleType.CRYSTAL;
            case "Куб" -> ParticleType.CUBE;
            case "Рандом" -> ParticleType.getRandom();
            default -> ParticleType.getRandom();
        };

        particles.add(new Particle(type, position.add(0, s, 0), velocity, particles.size(), col, s, 2.0F));
    }

    private void spawnWeatherParticle(List<Particle> particles, Vector3d position, Vector3d velocity, ParticleType type, int color, float size, float speedMult) {
        particles.add(new Particle(type, position, velocity, particles.size(), color, size, speedMult));
    }

    @EventHandler
    public void onEvent(AttackEvent event) {
        Entity target = event.getTarget();
        float motion = 1.2F;
        if (events.getValue("Атаке")) {
            for (int i = 0; i < attackcos.getValue(); i++) {
                if (getInstance().visiksmode.is("2")) {
                    motion = 0F;
                    spawnParticle(targetParticles,
                            new Vector3d(target.getPosX(), target.getPosY() + Mathf.randomValue(0, target.getHeight() / 1.1F), target.getPosZ()),
                            new Vector3d(Mathf.randomValue(-0.6F, 0.6F), Mathf.randomValue(-0.1F, 0.1F), Mathf.randomValue(-0.6F, 0.6F)));
                } else if (getInstance().visiksmode.is("3")) {
                    motion = 0.8F;
                    spawnParticle(targetParticles,
                            new Vector3d(target.getPosX(), target.getPosY() + Mathf.randomValue(0, target.getHeight()), target.getPosZ()),
                            new Vector3d(Mathf.randomValue(-motion, motion), Mathf.randomValue(-1, 0.4F), Mathf.randomValue(-motion, motion)));
                } else {
                    motion = 1.35F;
                    spawnParticle(targetParticles,
                            new Vector3d(target.getPosX() + Mathf.randomValue(-0.4F, 0.4F), target.getPosY() + Mathf.randomValue(0, target.getHeight()), target.getPosZ() + Mathf.randomValue(-0.4F, 0.4F)),
                            new Vector3d(Mathf.randomValue(-motion, motion), Mathf.randomValue(-1.25F, 1.25F), Mathf.randomValue(-motion, motion)));
                }
            }
        }
    }

    @EventHandler
    public void onEvent(MotionEvent event) {
        if (events.getValue("Бездействии")) {
            int r = range.getValue().intValue();

            if (visiksmode.is("Дождь")) {
                for (int i = 0; i < countAFK.getValue().intValue(); i++) {
                    Vector3d additional = mc.player.getPositionVec().add(Mathf.randomValue(-r, r), 0, Mathf.randomValue(-r, r));
                    double x = additional.x;
                    double z = additional.z;
                    double y = mc.player.getPosY() + Mathf.randomValue(r / 2, r);
                    Vector3d spawnPos = new Vector3d(x, y, z);

                    int col = ColorUtil.rgba(100, 150, 255, 200);
                    float s = 0.08F + (this.size.getValue() * 0.15F);
                    spawnWeatherParticle(worldParticles, spawnPos,
                            new Vector3d(Mathf.randomValue(-0.05, 0.05), Mathf.randomValue(-1.8, -1.2), Mathf.randomValue(-0.05, 0.05)),
                            ParticleType.RAINDROP, col, s, speed.getValue());
                }
            } else if (visiksmode.is("Снег")) {
                for (int i = 0; i < countAFK.getValue().intValue(); i++) {
                    Vector3d additional = mc.player.getPositionVec().add(Mathf.randomValue(-r, r), 0, Mathf.randomValue(-r, r));
                    double x = additional.x;
                    double z = additional.z;
                    double y = mc.player.getPosY() + Mathf.randomValue(r / 2, r);
                    Vector3d spawnPos = new Vector3d(x, y, z);

                    int col = ColorUtil.rgba(240, 245, 255, 220);
                    float s = 0.12F + (this.size.getValue() * 0.2F);
                    spawnWeatherParticle(worldParticles, spawnPos,
                            new Vector3d(Mathf.randomValue(-0.15, 0.15), Mathf.randomValue(-0.4, -0.2), Mathf.randomValue(-0.15, 0.15)),
                            ParticleType.SNOWFLAKE, col, s, speed.getValue() * 0.5F);
                }
            } else if (visiksmode.is("Листопад")) {
                for (int i = 0; i < countAFK.getValue().intValue(); i++) {
                    Vector3d additional = mc.player.getPositionVec().add(Mathf.randomValue(-r, r), 0, Mathf.randomValue(-r, r));
                    double x = additional.x;
                    double z = additional.z;
                    double y = mc.player.getPosY() + Mathf.randomValue(r / 2, r);
                    Vector3d spawnPos = new Vector3d(x, y, z);

                    int[] leafColors = {
                            ColorUtil.rgba(180, 100, 40, 200),
                            ColorUtil.rgba(200, 120, 30, 200),
                            ColorUtil.rgba(160, 80, 20, 200),
                            ColorUtil.rgba(220, 140, 50, 200)
                    };
                    int col = leafColors[new FastRandom().nextInt(leafColors.length)];
                    float s = 0.15F + (this.size.getValue() * 0.25F);
                    spawnWeatherParticle(worldParticles, spawnPos,
                            new Vector3d(Mathf.randomValue(-0.2, 0.2), Mathf.randomValue(-0.5, -0.3), Mathf.randomValue(-0.2, 0.2)),
                            ParticleType.LEAF, col, s, speed.getValue() * 0.6F);
                }
            } else {
                for (int i = 0; i < countAFK.getValue().intValue(); i++) {
                    Vector3d additional = mc.player.getPositionVec().add(Mathf.randomValue(-r, r), 0, Mathf.randomValue(-r, r));
                    BlockPos pos = mc.world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(additional));
                    double x = pos.getX() + Mathf.randomValue(0, 1);
                    double z = pos.getZ() + Mathf.randomValue(0, 1);
                    double y = mc.player.getPosY() + Mathf.randomValue(mc.player.getHeight(), r);
                    Vector3d spawnPos = new Vector3d(x, y, z);

                    while (!mc.world.isAirBlock(new BlockPos(spawnPos)) && spawnPos.y < mc.world.getHeight()) {
                        spawnPos = spawnPos.add(0, 1, 0);
                    }

                    if (getInstance().visiksmode.is("2")) {
                        spawnParticle(worldParticles, spawnPos, new Vector3d(mc.player.motion.x + Mathf.randomValue(-0.3, 0.3), Mathf.randomValue(-0.9, 0.2F), mc.player.motion.z + Mathf.randomValue(-0.3, 0.3)).mul(1));
                    } else if (getInstance().visiksmode.is("1")) {
                        spawnParticle(worldParticles, spawnPos, new Vector3d(mc.player.motion.x + Mathf.randomValue(-0.5, 0.5), Mathf.randomValue(-0.06, 0.06), mc.player.motion.z + Mathf.randomValue(-0.5, 0.5)));
                    } else {
                        spawnParticle(worldParticles, spawnPos, new Vector3d(mc.player.motion.x + Mathf.randomValue(-0.5, 0.5), Mathf.randomValue(-0.1, 0.06), mc.player.motion.z + Mathf.randomValue(-0.5, 0.5)));
                    }
                }
            }
        }

        if (events.getValue("Движении") && hasPlayerMoved()) {
            for (int i = 0; i < move.getValue(); i++) {
                if (getInstance().visiksmode.is("2")) {
                    spawnParticle(flameParticles,
                            new Vector3d(mc.player.getPosX() + Mathf.randomValue(-0.3, 0.3), mc.player.getPosY() + Mathf.randomValue(0, mc.player.getHeight() / 1.7F), mc.player.getPosZ() + Mathf.randomValue(-0.3, 0.3)),
                            new Vector3d(mc.player.motion.x + Mathf.randomValue(-0.3, 0.3), Mathf.randomValue(-0.1, 0.1), mc.player.motion.z + Mathf.randomValue(-0.3, 0.3)).mul(1));
                } else if (getInstance().visiksmode.is("3")) {
                    spawnParticle(flameParticles,
                            new Vector3d(mc.player.getPosX() + Mathf.randomValue(-0.45, 0.45), mc.player.getPosY() + Mathf.randomValue(0, mc.player.getHeight() / 3), mc.player.getPosZ() + Mathf.randomValue(-0.45, 0.45)),
                            new Vector3d(mc.player.motion.x + Mathf.randomValue(-0.3, 0.1), Mathf.randomValue(-0.3, 0.3), mc.player.motion.z + Mathf.randomValue(-0.3, 0.3)).mul(1));
                } else {
                    spawnParticle(flameParticles,
                            new Vector3d(mc.player.getPosX() + Mathf.randomValue(-0.45, 0.45), mc.player.getPosY() + Mathf.randomValue(0, mc.player.getHeight()), mc.player.getPosZ() + Mathf.randomValue(-0.45, 0.45)),
                            new Vector3d(mc.player.motion.x + Mathf.randomValue(-0.1, 0.1), Mathf.randomValue(-0.1, 0.1), mc.player.motion.z + Mathf.randomValue(-0.1, 0.1)).mul(0.2F));
                }
            }
        }

        if (events.getValue("Бросок")) {
            for (Entity entity : mc.world.getAllEntities()) {
                if (entity instanceof net.minecraft.entity.item.EnderPearlEntity ||
                        entity instanceof net.minecraft.entity.projectile.ArrowEntity ||
                        entity instanceof net.minecraft.entity.projectile.TridentEntity) {

                    if (entity instanceof TridentEntity trident) {
                        if (trident.func_234616_v_() != null && trident.dealtDamage) continue;
                    }

                    boolean isMoving = entity.prevPosX != entity.getPosX() || entity.prevPosY != entity.getPosY() || entity.prevPosZ != entity.getPosZ();
                    if (!isMoving) continue;

                    Vector3d pos = entity.getPositionVec();
                    for (int i = 0; i < brosok.getValue(); i++) {
                        if (getInstance().visiksmode.is("2")) {
                            spawnParticle(flameParticles,
                                    new Vector3d(pos.x + Mathf.randomValue(-0.1, 0.1), pos.y + Mathf.randomValue(-0.1, 0.1), pos.z + Mathf.randomValue(-0.1, 0.1)),
                                    new Vector3d(entity.getMotion().x * 0.1 + Mathf.randomValue(-0.4, 0.4), entity.getMotion().y * 0.1, entity.getMotion().z * 0.1 + Mathf.randomValue(-0.4, 0.4)));
                        } else if (getInstance().visiksmode.is("3")) {
                            spawnParticle(flameParticles,
                                    new Vector3d(pos.x + Mathf.randomValue(-0.2, 0.2), pos.y + Mathf.randomValue(-0.2, 0.2), pos.z + Mathf.randomValue(-0.2, 0.2)),
                                    new Vector3d(Mathf.randomValue(-0.12, 0.12), Mathf.randomValue(-0.6, 0.06), Mathf.randomValue(-0.12, 0.12)));
                        } else {
                            spawnParticle(flameParticles,
                                    new Vector3d(pos.x + Mathf.randomValue(-0.5, 0.5), pos.y + Mathf.randomValue(-0.5, 0.5), pos.z + Mathf.randomValue(-0.5, 0.5)),
                                    new Vector3d(Mathf.randomValue(-0.06, 0.06), Mathf.randomValue(-0.06, 0.06), Mathf.randomValue(-0.06, 0.06)));
                        }
                    }
                }
            }
        }

        if (getInstance().visiksmode.is("3")) {
            removeExpiredParticles(targetParticles, 2000);
        } else {
            removeExpiredParticles(targetParticles, 1000);
        }

        if (visiksmode.is("Дождь")) {
            removeExpiredParticlesWorld(worldParticles, 5000);
        } else {
            removeExpiredParticlesWorld(worldParticles, 2000);
        }

        if (visiksmode.is("2")) {
            removeExpiredParticles(flameParticles, 1200);
        } else {
            removeExpiredParticles(flameParticles, 2000);
        }
    }

    private long totemPartictTime = 0;
    private final long totemSpawnDuration = 2500;
    private boolean spawningTotemParticles = false;
    private Entity currentTotemTarget = null;

    public void spawnTotemParticles(Entity target) {
        if (!events.getValue("Тотем")) return;
        spawningTotemParticles = true;
        totemPartictTime = System.currentTimeMillis();
        currentTotemTarget = target;
    }

    public void updateTotemParticles() {
        if (!spawningTotemParticles || currentTotemTarget == null) return;

        long elapsed = System.currentTimeMillis() - totemPartictTime;
        if (elapsed > totemSpawnDuration) {
            spawningTotemParticles = false;
            currentTotemTarget = null;
            return;
        }

        int[] colors = new int[]{ColorUtil.getColor(221, 218, 127), ColorUtil.getColor(127, 221, 144)};

        for (int i = 0; i < totem.getValue(); i++) {
            int col = colors[Mathf.randomInt(0, colors.length - 1)];
            if (getInstance().visiksmode.is("2")) {
                spawnParticleTotem(flameParticles,
                        new Vector3d(currentTotemTarget.getPosX(), currentTotemTarget.getPosY() + Mathf.random(0, 1.5F), currentTotemTarget.getPosZ()),
                        new Vector3d(Mathf.random(-0.6F, 0.6F), Mathf.random(0.1F, 0.1F), Mathf.random(-0.6F, 0.6F)), col);
            } else if (getInstance().visiksmode.is("3")) {
                spawnParticleTotem(flameParticles,
                        new Vector3d(currentTotemTarget.getPosX() + Mathf.random(-0.1F, -0.1F), currentTotemTarget.getPosY() + Mathf.random(0, 2), currentTotemTarget.getPosZ() + Mathf.random(-0.1F, -0.1F)),
                        new Vector3d(Mathf.random(-0.8F, 0.8F), Mathf.random(-0.7F, 0.7F), Mathf.random(-0.8F, 0.8F)), col);
            } else {
                spawnParticleTotem(flameParticles,
                        new Vector3d(currentTotemTarget.getPosX() + Mathf.random(-0.4F, 0.4F), currentTotemTarget.getPosY() + Mathf.random(0, 2), currentTotemTarget.getPosZ() + Mathf.random(-0.4F, 0.4F)),
                        new Vector3d(Mathf.random(-0.8F, 0.8F), Mathf.random(-0.6F, 0.1F), Mathf.random(-0.8F, 0.8F)), col);
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        updateTotemParticles();
    }

    @EventHandler
    public void onEvent(Render3DPosedEvent event) {
        MatrixStack matrix = event.getMatrix();

        long now = System.nanoTime();
        double deltaTime = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;

        setupRenderState();
        if (visiksmode.is("2")) {
            renderParticles(matrix, targetParticles, 200, 400, deltaTime);
        } else if (getInstance().visiksmode.is("3")) {
            renderParticles(matrix, targetParticles, 1000, 1500, deltaTime);
        } else {
            renderParticles(matrix, targetParticles, 400, 600, deltaTime);
        }
        if (visiksmode.is("2") || getInstance().visiksmode.is("3")) {
            renderParticles(matrix, worldParticles, 500, 800, deltaTime);
        } else {
            renderParticles(matrix, worldParticles, 800, 1500, deltaTime);
        }
        if (visiksmode.is("2") || getInstance().visiksmode.is("3")) {
            renderParticles(matrix, flameParticles, 300, 500, deltaTime);
        } else {
            renderParticles(matrix, flameParticles, 700, 1200, deltaTime);
        }

        if (visiksmode.is("Дождь")) {
            renderPuddles(matrix);
        }

        resetRenderState();
    }

    private boolean hasPlayerMoved() {
        return mc.player.lastTickPosX != mc.player.getPosX() || mc.player.lastTickPosY != mc.player.getPosY() || mc.player.lastTickPosZ != mc.player.getPosZ();
    }

    private void removeExpiredParticles(List<Particle> particles, long lifespan) {
        particles.removeIf(particle -> particle.time().finished(lifespan));
    }

    private void removeExpiredParticlesWorld(List<Particle> particles, long lifespan) {
        particles.removeIf(particle -> !PlayerUtil.isInView(particle.box));
        particles.removeIf(particle -> particle.time().finished(lifespan));
    }

    private void setupRenderState() {
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    }

    private void resetRenderState() {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.clearCurrentColor();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
    }

    private void renderPuddles(MatrixStack matrix) {
        if (puddles.isEmpty()) return;

        puddles.removeIf(Puddle::isExpired);

        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderUtil.bindTexture(new Namespaced("particle/circle.png"));

        for (Puddle puddle : puddles) {
            puddle.update();

            Vector3d pos = puddle.position();
            float size = puddle.size();
            int color = ColorUtil.replAlpha(puddle.color(), puddle.alpha());

            matrix.push();
            RenderUtil3D.setupOrientationMatrix(matrix, (float) pos.x, (float) pos.y, (float) pos.z);
            matrix.rotate(Vector3f.XP.rotationDegrees(90));

            RectUtil.drawRect(matrix, -size, -size, size * 2, size * 2, color, true, true);

            matrix.pop();
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    private static float af(int c) {
        return ((c >>> 24) & 0xFF) / 255f;
    }

    private static float rf(int c) {
        return ((c >>> 16) & 0xFF) / 255f;
    }

    private static float gf(int c) {
        return ((c >>> 8) & 0xFF) / 255f;
    }

    private static float bf(int c) {
        return (c & 0xFF) / 255f;
    }

    private static void drawTexturedQuad(MatrixStack stack, float x, float y, float w, float h, int color) {
        Matrix4f m = stack.getLast().getMatrix();
        BufferBuilder bb = Tessellator.getInstance().getBuffer();
        bb.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR_TEX);
        float r = rf(color), g = gf(color), b = bf(color), a = af(color);
        bb.pos(m, x, y, 0).color(r, g, b, a).tex(0f, 0f).endVertex();
        bb.pos(m, x + w, y, 0).color(r, g, b, a).tex(1f, 0f).endVertex();
        bb.pos(m, x + w, y + h, 0).color(r, g, b, a).tex(1f, 1f).endVertex();
        bb.pos(m, x, y + h, 0).color(r, g, b, a).tex(0f, 1f).endVertex();
        Tessellator.getInstance().draw();
    }

    private static void drawCentered(MatrixStack stack, float halfSize, int color) {
        drawTexturedQuad(stack, -halfSize, -halfSize, halfSize * 2f, halfSize * 2f, color);
    }

    private void renderParticles(MatrixStack matrix, List<Particle> particles, long fadeInTime, long fadeOutTime, double deltaTime) {
        if (particles.isEmpty()) return;

        matrix.push();
        for (Particle p : particles) {
            p.update(true, deltaTime);
            p.animation.update();

            if (p.animation().getValue() != 1 && !p.time().finished(fadeInTime)) {
                p.animation().run(1, 0.5, Easings.CUBIC_OUT, true);
            }
            if (p.animation().getValue() != 0 && p.time().finished(fadeOutTime)) {
                p.animation().run(0, 0.5, Easings.CUBIC_OUT, true);
            }

            int col = ColorUtil.replAlpha(p.color(), p.animation.get());
            Vector3d v = p.position();
            renderParticle(matrix, p, (float) v.x, (float) v.y, (float) v.z, p.size, col);
        }
        matrix.pop();
    }

    private void renderParticle(MatrixStack matrix, Particle particle, float x, float y, float z, float pos, int color) {
        matrix.push();
        RenderUtil3D.setupOrientationMatrix(matrix, x, y, z);
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
        matrix.push();
        matrix.rotate(Vector3f.ZP.rotationDegrees(180F));

        if (rotation.getValue()) {
            matrix.rotate(new Quaternion(new Vector3f(0, 0, 1), particle.rotation, false));
        }

        if (viefForStinka.getValue()) GL11.glDisable(GL11.GL_DEPTH_TEST);

        if (particle.type.is3D()) {
            matrix.push();
            matrix.translate(0, -pos, -pos);

            if (glowEffect.getValue()) {
                RenderUtil.bindTexture(glowTexture);
                float gl = pos * glowRadius.getValue();
                RectUtil.drawRect(matrix, -gl, -gl, gl * 2, gl * 2, ColorUtil.multAlpha(color, glowPower.getValue()), true, true);
            }

            float innerGl = pos * glowRadius.getValue() * 0.5f;
            RectUtil.drawRect(matrix, -innerGl, -innerGl, innerGl * 2, innerGl * 2, ColorUtil.multAlpha(color, glowPower.getValue() * 1.2f), true, true);

            matrix.pop();

            if (particle.type == ParticleType.CRYSTAL) {
                drawCrystal3D(matrix, pos, color);
            } else if (particle.type == ParticleType.CUBE) {
                drawCube3D(matrix, pos, color);
            }
        } else {
            matrix.push();
            matrix.translate(0, -pos, -pos);

            if (glowEffect.getValue()) {
                RenderUtil.bindTexture(glowTexture);
                float gl = pos * glowRadius.getValue();
                RectUtil.drawRect(matrix, -gl, -gl, gl * 2, gl * 2, ColorUtil.multAlpha(color, glowPower.getValue()), true, true);
            }

            RenderUtil.bindTexture(particle.type().texture());
            RectUtil.drawRect(matrix, -pos, -pos, pos * 2, pos * 2, color, true, true);

            if (particle.type().equals(ParticleType.BLOOM)) {
                RectUtil.drawRect(matrix, -pos / 2, -pos / 2, pos, pos, color, true, true);
            }

            matrix.pop();
        }

        if (viefForStinka.getValue()) GL11.glEnable(GL11.GL_DEPTH_TEST);

        matrix.pop();
        matrix.pop();
    }

    private void drawCrystal3D(MatrixStack ms, float size, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float fa = ((color >> 24) & 0xFF) / 255.0f;
        float fr = ((color >> 16) & 0xFF) / 255.0f;
        float fg = ((color >> 8) & 0xFF) / 255.0f;
        float fb = (color & 0xFF) / 255.0f;

        float fillAlpha = fa * 0.4f;
        float edgeAlpha = fa * 0.85f;

        Matrix4f matrix = ms.getLast().getMatrix();
        float top = size * 1.1f;
        float bottom = -size;
        float side = size * 0.7f;

        RenderSystem.disableTexture();
        RenderSystem.disableCull();

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, fillAlpha).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fillAlpha).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fillAlpha).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, fillAlpha).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fillAlpha).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fillAlpha).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, fillAlpha).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fillAlpha).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr * 0.75f, fg * 0.75f, fb * 0.75f, fillAlpha).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, fillAlpha).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr * 0.75f, fg * 0.75f, fb * 0.75f, fillAlpha).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fillAlpha).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha * 0.7f).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fillAlpha * 0.7f).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fillAlpha * 0.7f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha * 0.7f).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha * 0.7f).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fillAlpha * 0.7f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha * 0.7f).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr * 0.5f, fg * 0.5f, fb * 0.5f, fillAlpha * 0.7f).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha * 0.7f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha * 0.7f).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fillAlpha * 0.7f).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr * 0.5f, fg * 0.5f, fb * 0.5f, fillAlpha * 0.7f).endVertex();

        tessellator.draw();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(2.0f);

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, side, 0, 0).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, 0, 0, side).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, -side, 0, 0).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, 0, 0, -side).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, side, 0, 0).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, 0, bottom, 0).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, 0, 0, side).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, 0, bottom, 0).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, -side, 0, 0).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, 0, bottom, 0).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, 0, 0, -side).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, 0, bottom, 0).color(fr, fg, fb, edgeAlpha).endVertex();

        tessellator.draw();

        GL11.glLineWidth(1.0f);

        RenderSystem.enableCull();
        RenderSystem.enableTexture();
    }

    private void drawCube3D(MatrixStack ms, float size, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float fa = ((color >> 24) & 0xFF) / 255.0f;
        float fr = ((color >> 16) & 0xFF) / 255.0f;
        float fg = ((color >> 8) & 0xFF) / 255.0f;
        float fb = (color & 0xFF) / 255.0f;

        float fillAlpha = fa * 0.4f;
        float edgeAlpha = fa * 0.85f;

        Matrix4f matrix = ms.getLast().getMatrix();
        float half = size * 0.5f;

        RenderSystem.disableTexture();
        RenderSystem.disableCull();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(matrix, -half, half, half).color(fr, fg, fb, fillAlpha).endVertex();
        buffer.pos(matrix, half, half, half).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fillAlpha).endVertex();
        buffer.pos(matrix, half, -half, half).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fillAlpha).endVertex();
        buffer.pos(matrix, -half, -half, half).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fillAlpha).endVertex();

        buffer.pos(matrix, half, half, half).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fillAlpha).endVertex();
        buffer.pos(matrix, half, half, -half).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fillAlpha).endVertex();
        buffer.pos(matrix, half, -half, -half).color(fr * 0.75f, fg * 0.75f, fb * 0.75f, fillAlpha).endVertex();
        buffer.pos(matrix, half, -half, half).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fillAlpha).endVertex();

        buffer.pos(matrix, half, half, -half).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fillAlpha).endVertex();
        buffer.pos(matrix, -half, half, -half).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fillAlpha).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fillAlpha).endVertex();
        buffer.pos(matrix, half, -half, -half).color(fr * 0.75f, fg * 0.75f, fb * 0.75f, fillAlpha).endVertex();

        buffer.pos(matrix, -half, half, -half).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fillAlpha).endVertex();
        buffer.pos(matrix, -half, half, half).color(fr, fg, fb, fillAlpha).endVertex();
        buffer.pos(matrix, -half, -half, half).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fillAlpha).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fillAlpha).endVertex();

        buffer.pos(matrix, -half, half, -half).color(fr * 0.95f, fg * 0.95f, fb * 0.95f, fillAlpha).endVertex();
        buffer.pos(matrix, half, half, -half).color(fr * 0.95f, fg * 0.95f, fb * 0.95f, fillAlpha).endVertex();
        buffer.pos(matrix, half, half, half).color(fr * 0.95f, fg * 0.95f, fb * 0.95f, fillAlpha).endVertex();
        buffer.pos(matrix, -half, half, half).color(fr * 0.95f, fg * 0.95f, fb * 0.95f, fillAlpha).endVertex();

        buffer.pos(matrix, -half, -half, half).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha).endVertex();
        buffer.pos(matrix, half, -half, half).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha).endVertex();
        buffer.pos(matrix, half, -half, -half).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fillAlpha).endVertex();

        tessellator.draw();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(2.0f);

        buffer.pos(matrix, -half, half, half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, half, half, half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, half, half, half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, half, -half, half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, half, -half, half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, -half, -half, half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, -half, -half, half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, -half, half, half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, -half, half, -half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, half, half, -half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, half, half, -half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, half, -half, -half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, half, -half, -half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, -half, -half, -half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, -half, half, -half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, -half, half, half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, -half, half, -half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, half, half, half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, half, half, -half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, half, -half, half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, half, -half, -half).color(fr, fg, fb, edgeAlpha).endVertex();

        buffer.pos(matrix, -half, -half, half).color(fr, fg, fb, edgeAlpha).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(fr, fg, fb, edgeAlpha).endVertex();

        tessellator.draw();

        GL11.glLineWidth(1.0f);

        RenderSystem.enableCull();
        RenderSystem.enableTexture();
    }

    @Getter
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum ParticleType {
        DOLLAR("dollar", false),
        HEART("heart", false),
        STAR("star", false),
        BLOOM("glow", false),
        SNOOW("snow", false),
        STARNEW("starnew", false),
        AMONGUS("amongus", false),
        CIRCLE("circle", false),
        CROWN("crown", false),
        POLYGON("polygon", false),
        QUAD("quad", false),
        SKULL("skull", false),
        CROSS("cross", false),
        TRIANGLE("triangle", false),
        CRYSTAL("crystal", true),
        CUBE("cube", true),
        RAINDROP("circle", false),
        SNOWFLAKE("snow", false),
        LEAF("quad", false);

        ResourceLocation texture;
        boolean is3D;

        ParticleType(String name, boolean is3D) {
            this.texture = new Namespaced("particle/" + name + ".png");
            this.is3D = is3D;
        }

        public static ParticleType getRandom() {
            ParticleType[] values = ParticleType.values();
            return values[new FastRandom().nextInt(values.length)];
        }
    }

    @Getter
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class Particle {
        AxisAlignedBB box;
        ParticleType type;
        @NonFinal
        Vector3d position;
        @NonFinal
        Vector3d velocity;
        int index;
        int color;
        float size;
        private static final double BASE_VELOCITY = 0.05;
        double speedMultiplier;

        StopWatch time = new StopWatch();
        Animation animation = new Animation();

        @NonFinal
        private float rotate = 0;
        @NonFinal
        private float rotation = 0;

        public Particle(ParticleType type, final Vector3d position, final Vector3d velocity, final int index, int color, float size, float speedMultiplier) {
            this.box = new AxisAlignedBB(position, position).grow(size);
            this.type = type;
            this.position = position;
            this.velocity = velocity.mul(BASE_VELOCITY);
            this.index = index;
            this.color = color;
            this.size = size;
            this.speedMultiplier = speedMultiplier;
            this.time.reset();
        }

        public void update(boolean physic, double deltaTime) {
            if (Particles.getInstance().rotation.getValue()) {
                this.rotation = (this.rotate % 1000f) / 150f;
                this.rotate += 1;
            }

            if (physic) {
                boolean isRainMode = this.type == ParticleType.RAINDROP;
                boolean isSnowMode = this.type == ParticleType.SNOWFLAKE;
                boolean isLeafMode = this.type == ParticleType.LEAF;

                if (isRainMode) {
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                        if (Math.abs(this.velocity.y) > 0.1) {
                            for (int i = 0; i < 3; i++) {
                                Vector3d splashPos = this.position.add(Mathf.randomValue(-0.2, 0.2), 0, Mathf.randomValue(-0.2, 0.2));
                                Vector3d splashVel = new Vector3d(Mathf.randomValue(-0.3, 0.3), Mathf.randomValue(0.1, 0.4), Mathf.randomValue(-0.3, 0.3));
                                getInstance().worldParticles.add(new Particle(ParticleType.RAINDROP, splashPos, splashVel.mul(BASE_VELOCITY), 0, this.color, this.size * 0.5f, 1.5F));
                            }

                            BlockPos groundPos = new BlockPos(this.position.x, this.position.y, this.position.z);
                            while (PlayerUtil.isBlockSolid(groundPos.getX(), groundPos.getY() - 1, groundPos.getZ()) && groundPos.getY() > 0) {
                                groundPos = groundPos.down();
                            }
                            Vector3d puddlePos = new Vector3d(groundPos.getX() + 0.5, groundPos.getY() + 0.01, groundPos.getZ() + 0.5);
                            getInstance().puddles.add(new Puddle(puddlePos, ColorUtil.rgba(80, 120, 200, 100), 0.3f + Mathf.randomValue(0, 0.2f)));
                        }
                        this.velocity = this.velocity.mul(0.5F, -0.4F, 0.5F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                        this.velocity = this.velocity.mul(0.7F, 0.7F, -0.5F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                        this.velocity = this.velocity.mul(-0.5F, 0.7F, 0.7F);
                    }
                    this.velocity = this.velocity.mul(Math.pow(0.96, deltaTime * 60)).subtract(new Vector3d(0, (0.001) * deltaTime * 60, 0));
                } else if (isSnowMode) {
                    double driftX = Math.sin(this.time.elapsedTime() / 500.0) * 0.002;
                    double driftZ = Math.cos(this.time.elapsedTime() / 700.0) * 0.002;

                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                        this.velocity = this.velocity.mul(0.3F, -0.2F, 0.3F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                        this.velocity = this.velocity.mul(0.5F, 0.5F, -0.3F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                        this.velocity = this.velocity.mul(-0.3F, 0.5F, 0.5F);
                    }

                    this.velocity = this.velocity.add(driftX * deltaTime * 60, 0, driftZ * deltaTime * 60);
                    this.velocity = this.velocity.mul(Math.pow(0.98, deltaTime * 60)).subtract(new Vector3d(0, (0.0005) * deltaTime * 60, 0));
                } else if (isLeafMode) {
                    double swayX = Math.sin(this.time.elapsedTime() / 300.0) * 0.003;
                    double swayZ = Math.cos(this.time.elapsedTime() / 400.0) * 0.003;
                    double spinSpeed = Math.sin(this.time.elapsedTime() / 200.0) * 0.5;

                    if (Particles.getInstance().rotation.getValue()) {
                        this.rotation += (float) spinSpeed;
                    }

                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                        this.velocity = this.velocity.mul(0.4F, -0.3F, 0.4F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                        this.velocity = this.velocity.mul(0.6F, 0.6F, -0.4F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                        this.velocity = this.velocity.mul(-0.4F, 0.6F, 0.6F);
                    }

                    this.velocity = this.velocity.add(swayX * deltaTime * 60, 0, swayZ * deltaTime * 60);
                    this.velocity = this.velocity.mul(Math.pow(0.97, deltaTime * 60)).subtract(new Vector3d(0, (0.0006) * deltaTime * 60, 0));
                } else if (getInstance().visiksmode.is("2")) {
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                        this.velocity = this.velocity.mul(0.8F, 0.8F, -0.3F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                        this.velocity = this.velocity.mul(0.8F, -0.4F, 0.8F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                        this.velocity = this.velocity.mul(-0.3F, 0.8F, 0.8F);
                    }
                    this.velocity = this.velocity.mul(Math.pow(0.999, deltaTime * 60)).subtract(new Vector3d(0, (0.000003) * deltaTime * 60, 0));
                } else if (getInstance().visiksmode.is("3")) {
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                        this.velocity = this.velocity.mul(0.8F, 0.8F, -0.8F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                        this.velocity = this.velocity.mul(0.8F, -1, 0.8F);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                        this.velocity = this.velocity.mul(-0.8F, 0.8F, 0.8F);
                    }
                    this.velocity = this.velocity.mul(Math.pow(0.999, deltaTime * 60)).subtract(new Vector3d(0, (0.0003) * deltaTime * 60, 0));
                } else {
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                        this.velocity = this.velocity.mul(1, 1, -1);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                        this.velocity = this.velocity.mul(1, -1, 1);
                    }
                    if (PlayerUtil.isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                        this.velocity = this.velocity.mul(-1, 1, 1);
                    }
                    this.velocity = this.velocity.mul(Math.pow(0.999, deltaTime * 60)).subtract(new Vector3d(0, 0, 0));
                }
            }
            this.position = this.position.add(this.velocity.mul(deltaTime * 60 * speedMultiplier));
        }
    }

    @Getter
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Puddle {
        final Vector3d position;
        final int color;
        final StopWatch time = new StopWatch();
        float size;
        float alpha;
        final float maxSize;

        public Puddle(Vector3d position, int color, float maxSize) {
            this.position = position;
            this.color = color;
            this.maxSize = maxSize;
            this.size = 0;
            this.alpha = 1.0f;
        }

        public void update() {
            long elapsed = time.elapsedTime();
            if (elapsed < 300) {
                size = (float) (Easings.CUBIC_OUT.ease((double) elapsed / 300.0) * maxSize);
            } else {
                size = maxSize;
            }

            if (elapsed > 3000) {
                alpha = Math.max(0, 1.0f - ((elapsed - 3000) / 2000f));
            }
        }

        public boolean isExpired() {
            return time.elapsedTime() > 5000;
        }
    }

}