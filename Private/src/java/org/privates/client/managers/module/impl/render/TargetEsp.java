package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category1;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Interpolator;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.*;
import org.privates.client.utils.rotation.AuraUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "TargetEsp", category = Category.RENDER, description = "Визуальные эффекты на цели")
public class TargetEsp extends Module {
    public static TargetEsp getInstance() {
        return Instance.get(TargetEsp.class);
    }

    private final ModeSetting mode = new ModeSetting(this, "Режим", "Обычный", "Души", "Призраки", "New Призраки", "Круг", "Нимб", "Кристаллы", "Кристаллы 2", "Кубы", "Кубы 2", "Спираль", "Орбита", "Вихрь", "Аура", "Желе");


    private final ModeSetting activationMode = new ModeSetting(this, "Активация Общие", "При наводке", "При AttackAura");

    private final BooleanSetting checkOcclusion = new BooleanSetting(this, "Проверка видимости Общие", false);

    private final ModeSetting markerTexture = new ModeSetting(this, "Текстура Обычный", "marker", "marker2", "bubble", "logo")
            .setVisible(() -> mode.is("Обычный"));
    private final BooleanSetting cubeEnableAnimation = new BooleanSetting(this, "Анимация Кубы", true)
            .setVisible(() -> mode.is("Кубы"));
    private final SliderSetting cubeCount = new SliderSetting(this, "Количество кубов Кубы", 6, 3, 12, 1)
            .setVisible(() -> mode.is("Кубы"));
    private final SliderSetting cubeSize = new SliderSetting(this, "Размер Кубы", 0.14f, 0.08f, 0.25f, 0.01f)
            .setVisible(() -> mode.is("Кубы"));
    private final SliderSetting cubeOrbitRadius = new SliderSetting(this, "Радиус орбиты Кубы", 0.45f, 0.2f, 0.8f, 0.05f)
            .setVisible(() -> mode.is("Кубы"));
    private final SliderSetting cubeAnimationSpeed = new SliderSetting(this, "Скорость анимации Кубы", 40f, 20f, 100f, 5f)
            .setVisible(() -> mode.is("Кубы"));
    private final ModeSetting cubeAnimationType = new ModeSetting(this, "Тип анимации Кубы", "Плавная", "Быстрая", "Пульсация")
            .setVisible(() -> mode.is("Кубы"));
    private final BooleanSetting ghostAnimation = new BooleanSetting(this, "Анимация движения Призраки", true)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostSpeed = new SliderSetting(this, "Скорость Призраки", 3f, 1f, 10f, 0.5f)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostVerticalSpeed = new SliderSetting(this, "Вертикальная скорость Призраки", 6f, 1f, 15f, 0.5f)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostSize = new SliderSetting(this, "Размер Призраки", 0.6f, 0.3f, 1.5f, 0.1f)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostTrailLength = new SliderSetting(this, "Длина шлейфа Призраки", 25, 10, 50, 1)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostRadius = new SliderSetting(this, "Радиус Призраки", 0.65f, 0.3f, 1.5f, 0.05f)
            .setVisible(() -> mode.is("Призраки"));
    private final SliderSetting ghostStreams = new SliderSetting(this, "Количество потоков Призраки", 3, 1, 6, 1)
            .setVisible(() -> mode.is("Призраки"));

    private final SliderSetting newGhostSpeed = new SliderSetting(this, "Скорость New Призраки", 3f, 1f, 10f, 0.5f)
            .setVisible(() -> mode.is("New Призраки"));
    private final SliderSetting newGhostVerticalSpeed = new SliderSetting(this, "Вертикальная скорость New Призраки", 6f, 1f, 15f, 0.5f)
            .setVisible(() -> mode.is("New Призраки"));
    private final SliderSetting newGhostSize = new SliderSetting(this, "Размер New Призраки", 0.6f, 0.3f, 1.5f, 0.1f)
            .setVisible(() -> mode.is("New Призраки"));
    private final SliderSetting newGhostTrailLength = new SliderSetting(this, "Длина trail New Призраки", 30, 10, 60, 1)
            .setVisible(() -> mode.is("New Призраки"));
    private final SliderSetting newGhostRadius = new SliderSetting(this, "Радиус New Призраки", 0.65f, 0.3f, 1.5f, 0.05f)
            .setVisible(() -> mode.is("New Призраки"));
    private final SliderSetting newGhostStreams = new SliderSetting(this, "Количество потоков New Призраки", 3, 1, 6, 1)
            .setVisible(() -> mode.is("New Призраки"));

    private final SliderSetting soulSpeed = new SliderSetting(this, "Скорость Души", 4f, 1f, 10f, 0.5f)
            .setVisible(() -> mode.is("Души"));
    private final SliderSetting soulSize = new SliderSetting(this, "Размер Души", 17f, 5f, 30f, 1f)
            .setVisible(() -> mode.is("Души"));
    private final SliderSetting soulCount = new SliderSetting(this, "Количество душ Души", 3, 1, 8, 1)
            .setVisible(() -> mode.is("Души"));
    private final SliderSetting soulTrailLength = new SliderSetting(this, "Длина шлейфа Души", 35, 10, 60, 1)
            .setVisible(() -> mode.is("Души"));
    private final SliderSetting soulRadius = new SliderSetting(this, "Радиус Души", 0.6f, 0.3f, 1.5f, 0.05f)
            .setVisible(() -> mode.is("Души"));

    private final SliderSetting ringParticleCount = new SliderSetting(this, "Количество частиц Круг", 32, 16, 64, 1)
            .setVisible(() -> mode.is("Круг"));
    private final SliderSetting ringTrailLength = new SliderSetting(this, "Длина шлейфа Круг", 5, 2, 10, 1)
            .setVisible(() -> mode.is("Круг"));
    private final SliderSetting ringSize = new SliderSetting(this, "Размер Круг", 0.12f, 0.05f, 0.3f, 0.01f)
            .setVisible(() -> mode.is("Круг"));
    private final SliderSetting ringAnimationSpeed = new SliderSetting(this, "Скорость анимации Круг", 2000, 1000, 5000, 100)
            .setVisible(() -> mode.is("Круг"));
    private final SliderSetting haloParticleCount = new SliderSetting(this, "Частицы нимба Нимб", 44, 18, 96, 1)
            .setVisible(() -> mode.is("Нимб"));
    private final SliderSetting haloRadius = new SliderSetting(this, "Радиус нимба Нимб", 0.35f, 0.05f, 1.0f, 0.05f)
            .setVisible(() -> mode.is("Нимб"));
    private final SliderSetting haloHeight = new SliderSetting(this, "Высота нимба Нимб", 0.9f, 0.4f, 1.4f, 0.05f)
            .setVisible(() -> mode.is("Нимб"));
    private final SliderSetting haloSize = new SliderSetting(this, "Размер нимба Нимб", 0.13f, 0.06f, 0.32f, 0.01f)
            .setVisible(() -> mode.is("Нимб"));
    private final SliderSetting haloLength = new SliderSetting(this, "Длина нимба Нимб", 2.0f, 0.8f, 4.0f, 0.1f)
            .setVisible(() -> mode.is("Нимб"));
    private final SliderSetting haloSpeed = new SliderSetting(this, "Скорость нимба Нимб", 1.8f, 0.2f, 6.0f, 0.1f)
            .setVisible(() -> mode.is("Нимб"));
    private final SliderSetting haloTilt = new SliderSetting(this, "Угол нимба Нимб", 90f, 0f, 250f, 5f)
            .setVisible(() -> mode.is("Нимб"));

    private final BooleanSetting crystalEnableAnimation = new BooleanSetting(this, "Анимация Кристаллы", true)
            .setVisible(() -> mode.is("Кристаллы"));
    private final SliderSetting crystalCount = new SliderSetting(this, "Количество кристаллов Кристаллы", 6, 3, 12, 1)
            .setVisible(() -> mode.is("Кристаллы"));
    private final SliderSetting crystalSize = new SliderSetting(this, "Размер Кристаллы", 0.14f, 0.08f, 0.25f, 0.01f)
            .setVisible(() -> mode.is("Кристаллы"));
    private final SliderSetting crystalOrbitRadius = new SliderSetting(this, "Радиус орбиты Кристаллы", 0.45f, 0.2f, 0.8f, 0.05f)
            .setVisible(() -> mode.is("Кристаллы"));
    private final SliderSetting crystalAnimationSpeed = new SliderSetting(this, "Скорость анимации Кристаллы", 40f, 20f, 100f, 5f)
            .setVisible(() -> mode.is("Кристаллы"));
    private final ModeSetting crystalAnimationType = new ModeSetting(this, "Тип анимации Кристаллы", "Плавная", "Быстрая", "Пульсация")
            .setVisible(() -> mode.is("Кристаллы"));

    private final SliderSetting crystal2Count = new SliderSetting(this, "Количество кристаллов Кристаллы 2", 14, 8, 20, 1)
            .setVisible(() -> mode.is("Кристаллы 2"));
    private final SliderSetting crystal2Size = new SliderSetting(this, "Размер Кристаллы 2", 0.18f, 0.1f, 0.35f, 0.01f)
            .setVisible(() -> mode.is("Кристаллы 2"));
    private final SliderSetting crystal2Radius = new SliderSetting(this, "Радиус Кристаллы 2", 0.25f, 0.1f, 0.5f, 0.05f)
            .setVisible(() -> mode.is("Кристаллы 2"));
    private final SliderSetting crystal2Speed = new SliderSetting(this, "Скорость Кристаллы 2", 6f, 2f, 15f, 0.5f)
            .setVisible(() -> mode.is("Кристаллы 2"));
    private final SliderSetting crystal2Alpha = new SliderSetting(this, "Прозрачность Кристаллы 2", 0.7f, 0.1f, 1.0f, 0.05f)
            .setVisible(() -> mode.is("Кристаллы 2"));
    private final SliderSetting crystal2Detail = new SliderSetting(this, "Детализация Кристаллы 2", 1.0f, 0.5f, 2.0f, 0.1f)
            .setVisible(() -> mode.is("Кристаллы 2"));
    private final SliderSetting crystal2GlowRadius = new SliderSetting(this, "Glow радиус Кристаллы 2", 3.6f, 1.0f, 8.0f, 0.2f)
            .setVisible(() -> mode.is("Кристаллы 2"));
    private final SliderSetting crystal2GlowPower = new SliderSetting(this, "Glow power Кристаллы 2", 0.3f, 0.1f, 1.0f, 0.05f)
            .setVisible(() -> mode.is("Кристаллы 2"));

    private final SliderSetting cube2Count = new SliderSetting(this, "Количество кубов Кубы 2", 14, 8, 20, 1)
            .setVisible(() -> mode.is("Кубы 2"));
    private final SliderSetting cube2Size = new SliderSetting(this, "Размер Кубы 2", 0.18f, 0.1f, 0.35f, 0.01f)
            .setVisible(() -> mode.is("Кубы 2"));
    private final SliderSetting cube2Radius = new SliderSetting(this, "Радиус Кубы 2", 0.25f, 0.1f, 0.5f, 0.05f)
            .setVisible(() -> mode.is("Кубы 2"));
    private final SliderSetting cube2Speed = new SliderSetting(this, "Скорость Кубы 2", 6f, 2f, 15f, 0.5f)
            .setVisible(() -> mode.is("Кубы 2"));
    private final SliderSetting cube2Alpha = new SliderSetting(this, "Прозрачность Кубы 2", 0.7f, 0.1f, 1.0f, 0.05f)
            .setVisible(() -> mode.is("Кубы 2"));
    private final SliderSetting cube2Detail = new SliderSetting(this, "Детализация Кубы 2", 1.0f, 0.5f, 2.0f, 0.1f)
            .setVisible(() -> mode.is("Кубы 2"));
    private final SliderSetting cube2GlowRadius = new SliderSetting(this, "Glow радиус Кубы 2", 3.6f, 1.0f, 8.0f, 0.2f)
            .setVisible(() -> mode.is("Кубы 2"));
    private final SliderSetting cube2GlowPower = new SliderSetting(this, "Glow power Кубы 2", 0.3f, 0.1f, 1.0f, 0.05f)
            .setVisible(() -> mode.is("Кубы 2"));

    private final SliderSetting spiralSpeed = new SliderSetting(this, "Скорость Спираль", 2f, 0.5f, 5f, 0.5f)
            .setVisible(() -> mode.is("Спираль"));
    private final SliderSetting spiralSize = new SliderSetting(this, "Размер Спираль", 0.15f, 0.05f, 0.3f, 0.01f)
            .setVisible(() -> mode.is("Спираль"));
    private final SliderSetting spiralHeight = new SliderSetting(this, "Высота Спираль", 2.5f, 1f, 4f, 0.1f)
            .setVisible(() -> mode.is("Спираль"));
    private final SliderSetting spiralRadius = new SliderSetting(this, "Радиус Спираль", 0.6f, 0.3f, 1.2f, 0.05f)
            .setVisible(() -> mode.is("Спираль"));
    private final SliderSetting spiralParticles = new SliderSetting(this, "Количество частиц Спираль", 30, 15, 60, 1)
            .setVisible(() -> mode.is("Спираль"));

    private final SliderSetting orbitSpeed = new SliderSetting(this, "Скорость орбиты Орбита", 1.5f, 0.5f, 5f, 0.5f)
            .setVisible(() -> mode.is("Орбита"));
    private final SliderSetting orbitSize = new SliderSetting(this, "Размер Орбита", 0.2f, 0.1f, 0.4f, 0.05f)
            .setVisible(() -> mode.is("Орбита"));
    private final SliderSetting orbitRadius = new SliderSetting(this, "Радиус Орбита", 0.8f, 0.4f, 1.5f, 0.05f)
            .setVisible(() -> mode.is("Орбита"));
    private final SliderSetting orbitCount = new SliderSetting(this, "Количество орбит Орбита", 2, 1, 4, 1)
            .setVisible(() -> mode.is("Орбита"));
    private final SliderSetting orbitParticles = new SliderSetting(this, "Частицы на орбиту Орбита", 8, 4, 16, 1)
            .setVisible(() -> mode.is("Орбита"));

    private final SliderSetting vortexSpeed = new SliderSetting(this, "Скорость вихря Вихрь", 3f, 1f, 8f, 0.5f)
            .setVisible(() -> mode.is("Вихрь"));
    private final SliderSetting vortexSize = new SliderSetting(this, "Размер Вихрь", 0.12f, 0.05f, 0.25f, 0.01f)
            .setVisible(() -> mode.is("Вихрь"));
    private final SliderSetting vortexRadius = new SliderSetting(this, "Радиус Вихрь", 0.7f, 0.3f, 1.5f, 0.05f)
            .setVisible(() -> mode.is("Вихрь"));
    private final SliderSetting vortexParticles = new SliderSetting(this, "Количество частиц Вихрь", 40, 20, 80, 1)
            .setVisible(() -> mode.is("Вихрь"));
    private final SliderSetting vortexLayers = new SliderSetting(this, "Количество слоев Вихрь", 5, 3, 10, 1)
            .setVisible(() -> mode.is("Вихрь"));

    private final SliderSetting auraSpeed = new SliderSetting(this, "Скорость пульсации Аура", 2f, 0.5f, 5f, 0.5f)
            .setVisible(() -> mode.is("Аура"));
    private final SliderSetting auraSize = new SliderSetting(this, "Размер Аура", 0.18f, 0.08f, 0.35f, 0.01f)
            .setVisible(() -> mode.is("Аура"));
    private final SliderSetting auraRadius = new SliderSetting(this, "Радиус Аура", 0.9f, 0.5f, 1.8f, 0.05f)
            .setVisible(() -> mode.is("Аура"));
    private final SliderSetting auraWaves = new SliderSetting(this, "Количество волн Аура", 3, 1, 6, 1)
            .setVisible(() -> mode.is("Аура"));
    private final SliderSetting auraParticles = new SliderSetting(this, "Частицы на волну Аура", 24, 12, 48, 1)
            .setVisible(() -> mode.is("Аура"));
    private final BooleanSetting jelloThroughWalls = new BooleanSetting(this, "Through walls Желе", true)
            .setVisible(() -> mode.is("Желе"));
    private final BooleanSetting damageRed = new BooleanSetting(this, "Damage red Желе", true)
            .setVisible(() -> mode.is("Желе"));
    private final SliderSetting jelloSize = new SliderSetting(this, "Jello size Желе", 0.2f, 0.08f, 0.45f, 0.01f)
            .setVisible(() -> mode.is("Желе"));
    private final SliderSetting jelloLayers = new SliderSetting(this, "Jello layers Желе", 14, 6, 22, 1)
            .setVisible(() -> mode.is("Желе"));

    private final Namespaced bloomTexture = new Namespaced("texture/glow.png");
    private final Namespaced glowTexture = new Namespaced("texture/glow.png");

    public LivingEntity target;
    public LivingEntity lastTarget;
    public final Animation markerAnimation = new Animation();
    public final Animation crystalAnimation = new Animation();
    private final Vector2f markerPosition = new Vector2f();

    private Vector3d ghostBasePosition = null;
    private Vector3d ghostVelocity = new Vector3d(0, 0, 0);

    private float crystalCurrentRadius = 1.0f;
    private float crystalTargetRadius = 1.0f;
    private long lastHurtTime = 0;
    private boolean isContractingAnimation = false;
    private long contractAnimationStart = 0;
    private static final long CONTRACT_DURATION = 150;
    private static final long EXPAND_DURATION = 300;
    private float cubeCurrentRadius = 1.0f;
    private float cubeTargetRadius = 1.0f;
    private float jelloMoving = 0f;
    private float damageFlashIntensity = 0f;
    private long lastDamageTime = 0L;
    private int lastDamageEntityId = -1;
    private int lastHurtTick = 0;
    private static final long DAMAGE_FLASH_DURATION = 260L;

    public Animation alpha = new Animation();

    private static class TrailPoint {
        Vector3d position;
        long createTime;
        int streamId;

        TrailPoint(Vector3d position, int streamId) {
            this.position = position;
            this.createTime = System.currentTimeMillis();
            this.streamId = streamId;
        }

        float getLifeProgress(float maxLifeTime) {
            long age = System.currentTimeMillis() - createTime;
            return Math.min(1.0f, age / maxLifeTime);
        }
    }

    private final List<List<TrailPoint>> newGhostTrails = new ArrayList<>();

    @Override
    public void toggle() {
        super.toggle();
        newGhostTrails.clear();
    }

    @EventHandler
    public void onEvent(Render2DEvent event) {
        AuraNew aura = AuraNew.getInstance();
        markerAnimation.update();
        crystalAnimation.update();

        LivingEntity currentTarget = null;
        boolean shouldActivate = false;

        if (activationMode.is("При наводке")) {
            if (mc.objectMouseOver != null && mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY) {
                EntityRayTraceResult entityRayTrace = (EntityRayTraceResult) mc.objectMouseOver;
                if (entityRayTrace.getEntity() instanceof LivingEntity) {
                    currentTarget = (LivingEntity) entityRayTrace.getEntity();
                    shouldActivate = true;
                }
            }
        } else if (activationMode.is("При AttackAura")) {
            if (aura.target() != null) {
                currentTarget = aura.target();
                shouldActivate = true;
            }
        }

        if (shouldActivate && currentTarget != null) {
            target = currentTarget;
        }

        markerAnimation.run(shouldActivate ? 1 : 0, 1, Easings.LINEAR, true);
        crystalAnimation.run(shouldActivate ? 1 : 0, 0.6, Easings.SINE_OUT, true);

        if (markerAnimation.getValue() == 0.0) {
            target = null;
            lastTarget = null;
            newGhostTrails.clear();
        }

        if (target != null) lastTarget = target;

        if (target == null && lastTarget == null) {
            ghostBasePosition = null;
            newGhostTrails.clear();
            return;
        }

        if (mode.is("Души")) drawSouls(event.getMatrix());
        if (mode.is("Обычный")) drawMarker(event.getMatrix());
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        LivingEntity entityToRender = target != null ? target : lastTarget;
        if (entityToRender == null) {
            ghostBasePosition = null;
            newGhostTrails.clear();
            return;
        }

        if (entityToRender.getEntityId() != lastDamageEntityId) {
            lastDamageEntityId = entityToRender.getEntityId();
            lastHurtTick = entityToRender.hurtTime;
        }
        if (entityToRender.hurtTime > lastHurtTick) {
            lastDamageTime = System.currentTimeMillis();
        }
        lastHurtTick = entityToRender.hurtTime;

        if (markerAnimation.get() == 0.0 && crystalAnimation.get() == 0.0) return;

        MatrixStack matrix = event.getMatrix();
        alpha.update();
        alpha.run(1, 0.5, Easings.LINEAR, true);

        if (mode.is("Призраки")) drawMovingCircle(entityToRender, matrix, event.getPartialTicks());
        if (mode.is("New Призраки")) drawNewGhosts(entityToRender, matrix, event.getPartialTicks());
        if (mode.is("Круг")) drawRing(matrix);
        if (mode.is("Нимб")) drawHalo(matrix, event.getPartialTicks(), entityToRender);
        if (mode.is("Кристаллы")) {
            float animProgress = crystalAnimation.get();
            if (animProgress > 0) renderCrystals3D(matrix, event.getPartialTicks(), animProgress, entityToRender);
        }
        if (mode.is("Кристаллы 2")) {
            float animProgress = crystalAnimation.get();
            if (animProgress > 0) renderCrystals2(matrix, event.getPartialTicks(), animProgress, entityToRender);
        }
        if (mode.is("Кубы")) {
            float animProgress = crystalAnimation.get();
            if (animProgress > 0) renderCubes3D(matrix, event.getPartialTicks(), animProgress, entityToRender);
        }
        if (mode.is("Кубы 2")) {
            float animProgress = crystalAnimation.get();
            if (animProgress > 0) renderCubes2(matrix, event.getPartialTicks(), animProgress, entityToRender);
        }
        if (mode.is("Спираль")) drawSpiral(matrix, event.getPartialTicks(), entityToRender);
        if (mode.is("Орбита")) drawOrbit(matrix, event.getPartialTicks(), entityToRender);
        if (mode.is("Вихрь")) drawVortex(matrix, event.getPartialTicks(), entityToRender);
        if (mode.is("Аура")) drawAura(matrix, event.getPartialTicks(), entityToRender);
        if (mode.is("Желе")) renderJello(event, entityToRender);
    }

    private boolean isOccluded(Vector3d particlePos, LivingEntity target) {
        if (!checkOcclusion.getValue()) return false;

        Vector3d cameraPos = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

        RayTraceResult result = mc.world.rayTraceBlocks(
                new net.minecraft.util.math.RayTraceContext(
                        cameraPos,
                        particlePos,
                        net.minecraft.util.math.RayTraceContext.BlockMode.COLLIDER,
                        net.minecraft.util.math.RayTraceContext.FluidMode.NONE,
                        mc.player
                )
        );

        if (result.getType() == RayTraceResult.Type.BLOCK) {
            double blockDistance = cameraPos.distanceTo(result.getHitVec());
            double particleDistance = cameraPos.distanceTo(particlePos);
            if (blockDistance < particleDistance - 0.1) {
                return true;
            }
        }

        Vector3d targetCenter = target.getPositionVec().add(0, target.getHeight() / 2, 0);
        Vector3d cameraToParticle = particlePos.subtract(cameraPos).normalize();
        Vector3d cameraToTarget = targetCenter.subtract(cameraPos).normalize();

        double particleDistance = cameraPos.distanceTo(particlePos);
        double targetDistance = cameraPos.distanceTo(targetCenter);

        if (particleDistance > targetDistance) {
            double dotProduct = cameraToParticle.dotProduct(cameraToTarget);

            if (dotProduct > 0.95) {
                Vector3d particleToCamera = particlePos.subtract(cameraPos);
                Vector3d projection = cameraToTarget.scale(particleToCamera.dotProduct(cameraToTarget));
                Vector3d perpendicular = particleToCamera.subtract(projection);
                double perpendicularDist = perpendicular.length();

                return perpendicularDist < target.getWidth() * 0.7;
            }
        }

        return false;
    }

    private void renderCubes3D(MatrixStack ms, float partialTicks, float animProgress, LivingEntity entityToRender) {
        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d interpolated = RenderUtil3D.interpolate(entityToRender, partialTicks);
        double cx = interpolated.x - camX;
        double cy = interpolated.y + entityToRender.getHeight() * 0.5 - camY;
        double cz = interpolated.z - camZ;

        if (cubeEnableAnimation.getValue()) {
            updateCubeAnimation(entityToRender);
        } else {
            cubeCurrentRadius = cubeOrbitRadius.getValue();
            cubeTargetRadius = cubeOrbitRadius.getValue();
        }

        boolean poisoned = entityToRender.isPotionActive(Effects.POISON);
        int redColor = ColorUtil.getColor(255, 0, 0, 1.0f);
        float hurtProgress = (float) Math.sin(entityToRender.hurtTime * (12F * Math.PI / 180F));

        long curMs = System.currentTimeMillis();
        int themeColor = Theme.getInstance().clientColor();
        int hue = poisoned ? ColorUtil.overCol(themeColor, 0xFF00FF00, 0.7f) : themeColor;
        hue = ColorUtil.overCol(hue, redColor, hurtProgress * 0.6f);

        double t = curMs / 1200.0;
        double pulse = 1.0 + 0.08 * Math.sin(t * 1.2) + 0.02 * Math.sin(t * 2.5);

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderSystem.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        RenderUtil.bindTexture(glowTexture);

        float animatedRadius = (float) (cubeCurrentRadius * pulse);

        float entityHeight = entityToRender.getHeight();
        for (int layer = 0; layer < 3; layer++) {
            float yLayerOffset = (layer - 1) * (entityHeight / 3f);
            renderCubeOrbit3D(ms, rotation, cx, cy + yLayerOffset, cz, animatedRadius * 0.95, 0.8, layer, animProgress, t, pulse, hue, hurtProgress, redColor, interpolated, entityToRender);
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private void updateCubeAnimation(LivingEntity entityToRender) {
        long currentTime = System.currentTimeMillis();
        float minRadius = cubeOrbitRadius.getValue() * 0.4f;
        float maxRadius = cubeOrbitRadius.getValue();

        if (entityToRender.hurtTime > 0 && entityToRender.hurtTime == 10) {
            if (currentTime - lastHurtTime > 300) {
                isContractingAnimation = true;
                contractAnimationStart = currentTime;
                lastHurtTime = currentTime;
            }
        }

        if (isContractingAnimation) {
            long elapsed = currentTime - contractAnimationStart;

            if (elapsed < CONTRACT_DURATION) {
                float progress = elapsed / (float) CONTRACT_DURATION;
                progress = easeOutQuart(progress);
                cubeTargetRadius = maxRadius - (maxRadius - minRadius) * progress;
            } else if (elapsed < CONTRACT_DURATION + EXPAND_DURATION) {
                float expandElapsed = elapsed - CONTRACT_DURATION;
                float progress = expandElapsed / (float) EXPAND_DURATION;
                progress = easeOutBack(progress);
                cubeTargetRadius = minRadius + (maxRadius - minRadius) * progress;
            } else {
                isContractingAnimation = false;
                cubeTargetRadius = maxRadius;
            }
        } else {
            cubeTargetRadius = maxRadius;
        }

        float lerpSpeed = 0.08f;
        cubeCurrentRadius = cubeCurrentRadius + (cubeTargetRadius - cubeCurrentRadius) * lerpSpeed;
    }

    private void renderCubeOrbit3D(MatrixStack ms, Quaternion rotation, double cx, double cy, double cz,
                                   double orbitR, double speed, int layer, float animProgress,
                                   double t, double pulse, int hue, float hurtProgress, int redColor,
                                   Vector3d targetWorldPos, LivingEntity target) {
        float scale = animProgress;
        float cubeSize = this.cubeSize.getValue() * scale;

        float contractFactor = cubeCurrentRadius / cubeOrbitRadius.getValue();
        float sizeBoost = 1.0f + (1.0f - contractFactor) * 0.3f;
        cubeSize *= sizeBoost;

        float speedMult = cubeAnimationType.is("Быстрая") ? 2f : cubeAnimationType.is("Пульсация") ? 0.5f : 1f;
        float baseSpeed = cubeAnimationSpeed.getValue();

        int count = cubeCount.getValue().intValue();

        Vector3d cameraPos = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

        for (int i = 0; i < count; i++) {
            double baseAngle = i * (2 * Math.PI / count);

            double chaoticAngleOffset = (noise(i * 999.0 + layer * 333, 0) - 0.5) * 1.5;
            double chaoticRadiusOffset = (noise(i * 777.0 + layer * 111, 0) - 0.5) * orbitR * 0.6;
            double chaoticHeightOffset = (noise(i * 555.0 + layer * 222, 0) - 0.5) * target.getHeight() * 0.5;

            double ang = t * speed * speedMult + baseAngle + chaoticAngleOffset + Math.sin(t * 0.5 + i) * 0.2;

            double slowTime = t * 0.1;

            double dynamicRandomX = (noise(i * 123.45 + layer * 50, slowTime) - 0.5) * 0.3;
            double dynamicRandomZ = (noise(i * 678.90 + layer * 50, slowTime) - 0.5) * 0.3;
            double dynamicRandomY = (noise(i * 345.67 + layer * 50, slowTime * 0.5) - 0.5) * 0.2;

            double waveX = Math.sin(slowTime * 0.4 + i * 1.1) * 0.15;
            double waveZ = Math.cos(slowTime * 0.4 + i * 1.1) * 0.15;

            double currentRadius = orbitR + chaoticRadiusOffset;

            double ellipseX = currentRadius * 1.1 * Math.cos(ang) + dynamicRandomX + waveX;
            double ellipseZ = currentRadius * 0.9 * Math.sin(ang) + dynamicRandomZ + waveZ;

            double ox = cx + ellipseX;
            double oy = cy + chaoticHeightOffset + 0.12 * Math.sin(t + i * 0.5) + dynamicRandomY;
            double oz = cz + ellipseZ;

            Vector3d particleWorldPos = new Vector3d(
                    targetWorldPos.x + ellipseX,
                    targetWorldPos.y + target.getHeight() * 0.5 + (layer - 1) * (target.getHeight() / 3f) + chaoticHeightOffset + 0.12 * Math.sin(t + i * 0.5) + dynamicRandomY,
                    targetWorldPos.z + ellipseZ
            );

            if (isOccluded(particleWorldPos, target)) continue;

            int finalColor = ColorUtil.overCol(hue, redColor, hurtProgress * 0.4f);

            float glowIntensity = 0.8f + (1.0f - contractFactor) * 0.5f;
            int glowColor = ColorUtil.multAlpha(finalColor, glowIntensity * animProgress);

            float glowSize = (0.45f + (1.0f - contractFactor) * 0.4f) * scale;

            ms.push();
            ms.translate(ox, oy, oz);
            ms.rotate(rotation);

            RectUtil.drawRect(ms, -glowSize / 2f, -glowSize / 2f, glowSize, glowSize,
                    glowColor, glowColor, glowColor, glowColor, true, true);

            ms.pop();

            float innerGlowSize = glowSize * 0.5f;
            int innerGlowColor = ColorUtil.multAlpha(finalColor, glowIntensity * animProgress * 1.2f);

            ms.push();
            ms.translate(ox, oy, oz);
            ms.rotate(rotation);

            RectUtil.drawRect(ms, -innerGlowSize / 2f, -innerGlowSize / 2f, innerGlowSize, innerGlowSize,
                    innerGlowColor, innerGlowColor, innerGlowColor, innerGlowColor, true, true);

            ms.pop();

            ms.push();
            ms.translate(ox, oy, oz);

            float rotSpeed = isContractingAnimation ? 80f : baseSpeed;
            float rotAngle = (float) ((t * rotSpeed + i * 60) % 360);
            ms.rotate(Vector3f.YP.rotationDegrees(rotAngle));
            ms.rotate(Vector3f.XP.rotationDegrees(rotAngle * 0.5f));

            int cubeColor = ColorUtil.multAlpha(finalColor, animProgress * 0.4f);
            int edgeColor = ColorUtil.multAlpha(finalColor, animProgress * 0.85f);
            drawCube3D(ms, cubeSize, cubeColor, edgeColor);

            ms.pop();
        }
    }


    private double noise(double x, double seed) {
        double n = Math.sin(x * 12.9898);
        return n - Math.floor(n);
    }

    private void renderCrystals3D(MatrixStack ms, float partialTicks, float animProgress, LivingEntity entityToRender) {
        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d interpolated = RenderUtil3D.interpolate(entityToRender, partialTicks);
        double cx = interpolated.x - camX;
        double cy = interpolated.y + entityToRender.getHeight() * 0.5 - camY;
        double cz = interpolated.z - camZ;

        if (crystalEnableAnimation.getValue()) {
            updateOrbitAnimation(entityToRender);
        } else {
            crystalCurrentRadius = crystalOrbitRadius.getValue();
            crystalTargetRadius = crystalOrbitRadius.getValue();
        }

        boolean poisoned = entityToRender.isPotionActive(Effects.POISON);
        int redColor = ColorUtil.getColor(255, 0, 0, 1.0f);
        float hurtProgress = (float) Math.sin(entityToRender.hurtTime * (12F * Math.PI / 180F));

        long curMs = System.currentTimeMillis();
        int themeColor = Theme.getInstance().clientColor();
        int hue = poisoned ? ColorUtil.overCol(themeColor, 0xFF00FF00, 0.7f) : themeColor;
        hue = ColorUtil.overCol(hue, redColor, hurtProgress * 0.6f);

        double t = curMs / 1200.0;
        double pulse = 1.0 + 0.08 * Math.sin(t * 1.2) + 0.02 * Math.sin(t * 2.5);

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderSystem.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        RenderUtil.bindTexture(glowTexture);

        float animatedRadius = (float) (crystalCurrentRadius * pulse);

        float entityHeight = entityToRender.getHeight();
        for (int layer = 0; layer < 3; layer++) {
            float yLayerOffset = (layer - 1) * (entityHeight / 3f);
            renderCrystalOrbit3D(ms, rotation, cx, cy + yLayerOffset, cz, animatedRadius * 0.95, 0.8, layer, animProgress, t, pulse, hue, hurtProgress, redColor, interpolated, entityToRender);
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private void drawCube3D(MatrixStack ms, float size, int fillColor, int edgeColor) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float fa = ((fillColor >> 24) & 0xFF) / 255.0f;
        float fr = ((fillColor >> 16) & 0xFF) / 255.0f;
        float fg = ((fillColor >> 8) & 0xFF) / 255.0f;
        float fb = (fillColor & 0xFF) / 255.0f;

        float ea = ((edgeColor >> 24) & 0xFF) / 255.0f;
        float er = ((edgeColor >> 16) & 0xFF) / 255.0f;
        float eg = ((edgeColor >> 8) & 0xFF) / 255.0f;
        float eb = (edgeColor & 0xFF) / 255.0f;

        Matrix4f matrix = ms.getLast().getMatrix();

        float half = size * 0.5f;

        RenderSystem.disableTexture();
        RenderSystem.disableCull();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(matrix, -half, half, half).color(fr, fg, fb, fa).endVertex();
        buffer.pos(matrix, half, half, half).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fa).endVertex();
        buffer.pos(matrix, half, -half, half).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fa).endVertex();
        buffer.pos(matrix, -half, -half, half).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fa).endVertex();

        buffer.pos(matrix, half, half, half).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fa).endVertex();
        buffer.pos(matrix, half, half, -half).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fa).endVertex();
        buffer.pos(matrix, half, -half, -half).color(fr * 0.75f, fg * 0.75f, fb * 0.75f, fa).endVertex();
        buffer.pos(matrix, half, -half, half).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fa).endVertex();

        buffer.pos(matrix, half, half, -half).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fa).endVertex();
        buffer.pos(matrix, -half, half, -half).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fa).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fa).endVertex();
        buffer.pos(matrix, half, -half, -half).color(fr * 0.75f, fg * 0.75f, fb * 0.75f, fa).endVertex();

        buffer.pos(matrix, -half, half, -half).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fa).endVertex();
        buffer.pos(matrix, -half, half, half).color(fr, fg, fb, fa).endVertex();
        buffer.pos(matrix, -half, -half, half).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fa).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fa).endVertex();

        buffer.pos(matrix, -half, half, -half).color(fr * 0.95f, fg * 0.95f, fb * 0.95f, fa).endVertex();
        buffer.pos(matrix, half, half, -half).color(fr * 0.95f, fg * 0.95f, fb * 0.95f, fa).endVertex();
        buffer.pos(matrix, half, half, half).color(fr * 0.95f, fg * 0.95f, fb * 0.95f, fa).endVertex();
        buffer.pos(matrix, -half, half, half).color(fr * 0.95f, fg * 0.95f, fb * 0.95f, fa).endVertex();

        buffer.pos(matrix, -half, -half, half).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa).endVertex();
        buffer.pos(matrix, half, -half, half).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa).endVertex();
        buffer.pos(matrix, half, -half, -half).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa).endVertex();

        tessellator.draw();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(2.0f);

        buffer.pos(matrix, -half, half, half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, half, half, half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, half, half, half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, half, -half, half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, half, -half, half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, -half, -half, half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, -half, -half, half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, -half, half, half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, -half, half, -half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, half, half, -half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, half, half, -half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, half, -half, -half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, half, -half, -half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, -half, -half, -half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, -half, half, -half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, -half, half, half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, -half, half, -half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, half, half, half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, half, half, -half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, half, -half, half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, half, -half, -half).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, -half, -half, half).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(er, eg, eb, ea).endVertex();

        tessellator.draw();

        GL11.glLineWidth(1.0f);

        RenderSystem.enableCull();
        RenderSystem.enableTexture();
    }

    private void renderCrystalOrbit3D(MatrixStack ms, Quaternion rotation, double cx, double cy, double cz,
                                      double orbitR, double speed, int layer, float animProgress,
                                      double t, double pulse, int hue, float hurtProgress, int redColor,
                                      Vector3d targetWorldPos, LivingEntity target) {
        float scale = animProgress;
        float crystalSize = this.crystalSize.getValue() * scale;

        float contractFactor = crystalCurrentRadius / crystalOrbitRadius.getValue();
        float sizeBoost = 1.0f + (1.0f - contractFactor) * 0.3f;
        crystalSize *= sizeBoost;

        float speedMult = crystalAnimationType.is("Быстрая") ? 2f : crystalAnimationType.is("Пульсация") ? 0.5f : 1f;
        float baseSpeed = crystalAnimationSpeed.getValue();

        int count = crystalCount.getValue().intValue();

        Vector3d cameraPos = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

        for (int i = 0; i < count; i++) {
            double baseAngle = i * (2 * Math.PI / count);

            double chaoticAngleOffset = (noise(i * 999.0 + layer * 333, 0) - 0.5) * 1.5;
            double chaoticRadiusOffset = (noise(i * 777.0 + layer * 111, 0) - 0.5) * orbitR * 0.6;
            double chaoticHeightOffset = (noise(i * 555.0 + layer * 222, 0) - 0.5) * target.getHeight() * 0.5;

            double ang = t * speed * speedMult + baseAngle + chaoticAngleOffset + Math.sin(t * 0.5 + i) * 0.2;

            double slowTime = t * 0.1;

            double dynamicRandomX = (noise(i * 120.45 + layer * 50, slowTime) - 0.5) * 0.3;
            double dynamicRandomZ = (noise(i * 450.90 + layer * 50, slowTime) - 0.5) * 0.3;
            double dynamicRandomY = (noise(i * 800.67 + layer * 50, slowTime * 0.5) - 0.5) * 0.2;

            double waveX = Math.sin(slowTime * 0.4 + i * 1.1) * 0.15;
            double waveZ = Math.cos(slowTime * 0.4 + i * 1.1) * 0.15;

            double currentRadius = orbitR + chaoticRadiusOffset;

            double ellipseX = currentRadius * 1.1 * Math.cos(ang) + dynamicRandomX + waveX;
            double ellipseZ = currentRadius * 0.9 * Math.sin(ang) + dynamicRandomZ + waveZ;

            double ox = cx + ellipseX;
            double oy = cy + chaoticHeightOffset + 0.12 * Math.sin(t + i * 0.5) + dynamicRandomY;
            double oz = cz + ellipseZ;

            Vector3d particleWorldPos = new Vector3d(
                    targetWorldPos.x + ellipseX,
                    targetWorldPos.y + target.getHeight() * 0.5 + (layer - 1) * (target.getHeight() / 3f) + chaoticHeightOffset + 0.12 * Math.sin(t + i * 0.5) + dynamicRandomY,
                    targetWorldPos.z + ellipseZ
            );

            if (isOccluded(particleWorldPos, target)) continue;

            int finalColor = ColorUtil.overCol(hue, redColor, hurtProgress * 0.4f);

            float glowIntensity = 0.8f + (1.0f - contractFactor) * 0.5f;
            int glowColor = ColorUtil.multAlpha(finalColor, glowIntensity * animProgress);

            float glowSize = (0.5f + (1.0f - contractFactor) * 0.4f) * scale;

            ms.push();
            ms.translate(ox, oy, oz);
            ms.rotate(rotation);

            RectUtil.drawRect(ms, -glowSize / 2f, -glowSize / 2f, glowSize, glowSize,
                    glowColor, glowColor, glowColor, glowColor, true, true);

            ms.pop();

            float innerGlowSize = glowSize * 0.5f;
            int innerGlowColor = ColorUtil.multAlpha(finalColor, glowIntensity * animProgress * 1.2f);

            ms.push();
            ms.translate(ox, oy, oz);
            ms.rotate(rotation);

            RectUtil.drawRect(ms, -innerGlowSize / 2f, -innerGlowSize / 2f, innerGlowSize, innerGlowSize,
                    innerGlowColor, innerGlowColor, innerGlowColor, innerGlowColor, true, true);

            ms.pop();

            ms.push();
            ms.translate(ox, oy, oz);

            float rotSpeed = isContractingAnimation ? 80f : baseSpeed;
            float rotAngle = (float) ((t * rotSpeed + i * 60) % 360);
            ms.rotate(Vector3f.YP.rotationDegrees(rotAngle));
            ms.rotate(Vector3f.XP.rotationDegrees(rotAngle * 0.5f));

            int crystalColor = ColorUtil.multAlpha(finalColor, animProgress * 0.4f);
            int edgeColor = ColorUtil.multAlpha(finalColor, animProgress * 0.85f);
            drawCrystal3D(ms, crystalSize, crystalColor, edgeColor);

            ms.pop();
        }
    }

    private void updateOrbitAnimation(LivingEntity entityToRender) {
        long currentTime = System.currentTimeMillis();
        float minRadius = crystalOrbitRadius.getValue() * 0.4f;
        float maxRadius = crystalOrbitRadius.getValue();

        if (entityToRender.hurtTime > 0 && entityToRender.hurtTime == 10) {
            if (currentTime - lastHurtTime > 300) {
                isContractingAnimation = true;
                contractAnimationStart = currentTime;
                lastHurtTime = currentTime;
            }
        }

        if (isContractingAnimation) {
            long elapsed = currentTime - contractAnimationStart;

            if (elapsed < CONTRACT_DURATION) {
                float progress = elapsed / (float) CONTRACT_DURATION;
                progress = easeOutQuart(progress);
                crystalTargetRadius = maxRadius - (maxRadius - minRadius) * progress;
            } else if (elapsed < CONTRACT_DURATION + EXPAND_DURATION) {
                float expandElapsed = elapsed - CONTRACT_DURATION;
                float progress = expandElapsed / (float) EXPAND_DURATION;
                progress = easeOutBack(progress);
                crystalTargetRadius = minRadius + (maxRadius - minRadius) * progress;
            } else {
                isContractingAnimation = false;
                crystalTargetRadius = maxRadius;
            }
        } else {
            crystalTargetRadius = maxRadius;
        }

        float lerpSpeed = 0.08f;
        crystalCurrentRadius = crystalCurrentRadius + (crystalTargetRadius - crystalCurrentRadius) * lerpSpeed;
    }

    private float easeOutQuart(float t) {
        return 1 - (float) Math.pow(1 - t, 4);
    }

    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + (float) Math.pow(-t, 3) * ((c3 * t) + c1);
    }

    private void renderCrystalOrbit3D(MatrixStack ms, Quaternion rotation, double cx, double cy, double cz,
                                      double orbitR, double speed, double yOffset, float animProgress,
                                      double t, double pulse, int hue, float hurtProgress, int redColor,
                                      Vector3d targetWorldPos, LivingEntity target) {
        float scale = animProgress;
        float crystalSize = this.crystalSize.getValue() * scale;

        float contractFactor = crystalCurrentRadius / crystalOrbitRadius.getValue();
        float sizeBoost = 1.0f + (1.0f - contractFactor) * 0.3f;
        crystalSize *= sizeBoost;

        float speedMult = crystalAnimationType.is("Быстрая") ? 2f : crystalAnimationType.is("Пульсация") ? 0.5f : 1f;
        float baseSpeed = crystalAnimationSpeed.getValue();

        int count = crystalCount.getValue().intValue();

        Vector3d cameraPos = mc.gameRenderer.getActiveRenderInfo().getProjectedView();

        for (int i = 0; i < count; i++) {
            double ang = t * speed * speedMult + i * (2 * Math.PI / count);

            double randomOffsetX = (noise(i * 123.45, t * 0.3) - 0.5) * 0.4;
            double randomOffsetZ = (noise(i * 678.90, t * 0.3) - 0.5) * 0.4;
            double randomOffsetY = (noise(i * 345.67, t * 0.5) - 0.5) * 0.3;

            double waveX = Math.sin(t * 0.8 + i * 0.7) * 0.2;
            double waveZ = Math.cos(t * 0.8 + i * 0.7) * 0.2;

            double ellipseX = (orbitR * 1.1 + randomOffsetX + waveX) * Math.cos(ang);
            double ellipseZ = (orbitR * 0.9 + randomOffsetZ + waveZ) * Math.sin(ang);

            double ox = cx + ellipseX;
            double oy = cy + yOffset + 0.12 * Math.sin(t + i * 0.5) + randomOffsetY;
            double oz = cz + ellipseZ;

            Vector3d particleWorldPos = new Vector3d(
                    targetWorldPos.x + ellipseX,
                    targetWorldPos.y + target.getHeight() * 0.5 + yOffset + 0.12 * Math.sin(t + i * 0.5) + randomOffsetY,
                    targetWorldPos.z + ellipseZ
            );

            if (isOccluded(particleWorldPos, target)) continue;

            int finalColor = ColorUtil.overCol(hue, redColor, hurtProgress * 0.4f);

            float glowIntensity = 0.8f + (1.0f - contractFactor) * 0.5f;
            int glowColor = ColorUtil.multAlpha(finalColor, glowIntensity * animProgress);

            float glowSize = (0.7f + (1.0f - contractFactor) * 0.4f) * scale;

            ms.push();
            ms.translate(ox, oy, oz);
            ms.rotate(rotation);

            RectUtil.drawRect(ms, -glowSize / 2f, -glowSize / 2f, glowSize, glowSize,
                    glowColor, glowColor, glowColor, glowColor, true, true);

            ms.pop();

            float innerGlowSize = glowSize * 0.5f;
            int innerGlowColor = ColorUtil.multAlpha(finalColor, glowIntensity * animProgress * 1.2f);

            ms.push();
            ms.translate(ox, oy, oz);
            ms.rotate(rotation);

            RectUtil.drawRect(ms, -innerGlowSize / 2f, -innerGlowSize / 2f, innerGlowSize, innerGlowSize,
                    innerGlowColor, innerGlowColor, innerGlowColor, innerGlowColor, true, true);

            ms.pop();

            ms.push();
            ms.translate(ox, oy, oz);

            float rotSpeed = isContractingAnimation ? 80f : baseSpeed;
            float rotAngle = (float) ((t * rotSpeed + i * 60) % 360);
            ms.rotate(Vector3f.YP.rotationDegrees(rotAngle));
            ms.rotate(Vector3f.XP.rotationDegrees(rotAngle * 0.5f));

            int crystalColor = ColorUtil.multAlpha(finalColor, animProgress * 0.4f);
            int edgeColor = ColorUtil.multAlpha(finalColor, animProgress * 0.85f);
            drawCrystal3D(ms, crystalSize, crystalColor, edgeColor);

            ms.pop();
        }
    }

    private void drawCrystal3D(MatrixStack ms, float size, int fillColor, int edgeColor) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float fa = ((fillColor >> 24) & 0xFF) / 255.0f;
        float fr = ((fillColor >> 16) & 0xFF) / 255.0f;
        float fg = ((fillColor >> 8) & 0xFF) / 255.0f;
        float fb = (fillColor & 0xFF) / 255.0f;

        float ea = ((edgeColor >> 24) & 0xFF) / 255.0f;
        float er = ((edgeColor >> 16) & 0xFF) / 255.0f;
        float eg = ((edgeColor >> 8) & 0xFF) / 255.0f;
        float eb = (edgeColor & 0xFF) / 255.0f;

        Matrix4f matrix = ms.getLast().getMatrix();

        float top = size * 1.1f;
        float bottom = -size;
        float side = size * 0.7f;

        RenderSystem.disableTexture();
        RenderSystem.disableCull();

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, fa).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fa).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fa).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, fa).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr * 0.85f, fg * 0.85f, fb * 0.85f, fa).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fa).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, fa).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr * 0.8f, fg * 0.8f, fb * 0.8f, fa).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr * 0.75f, fg * 0.75f, fb * 0.75f, fa).endVertex();

        buffer.pos(matrix, 0, top, 0).color(fr, fg, fb, fa).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr * 0.75f, fg * 0.75f, fb * 0.75f, fa).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr * 0.9f, fg * 0.9f, fb * 0.9f, fa).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa * 0.7f).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fa * 0.7f).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fa * 0.7f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa * 0.7f).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa * 0.7f).endVertex();
        buffer.pos(matrix, 0, 0, side).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fa * 0.7f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa * 0.7f).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr * 0.5f, fg * 0.5f, fb * 0.5f, fa * 0.7f).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa * 0.7f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(fr * 0.6f, fg * 0.6f, fb * 0.6f, fa * 0.7f).endVertex();
        buffer.pos(matrix, side, 0, 0).color(fr * 0.7f, fg * 0.7f, fb * 0.7f, fa * 0.7f).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(fr * 0.5f, fg * 0.5f, fb * 0.5f, fa * 0.7f).endVertex();

        tessellator.draw();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        GL11.glLineWidth(2.0f);

        buffer.pos(matrix, 0, top, 0).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, side, 0, 0).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, 0, top, 0).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, 0, 0, side).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, 0, top, 0).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, 0, top, 0).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, side, 0, 0).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, 0, 0, side).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, 0, 0, side).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, -side, 0, 0).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, 0, 0, -side).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, side, 0, 0).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, side, 0, 0).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, 0, bottom, 0).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, 0, 0, side).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, 0, bottom, 0).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, -side, 0, 0).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, 0, bottom, 0).color(er, eg, eb, ea).endVertex();

        buffer.pos(matrix, 0, 0, -side).color(er, eg, eb, ea).endVertex();
        buffer.pos(matrix, 0, bottom, 0).color(er, eg, eb, ea).endVertex();

        tessellator.draw();

        GL11.glLineWidth(1.0f);

        RenderSystem.enableCull();
        RenderSystem.enableTexture();
    }

    private void drawNewGhosts(LivingEntity target, MatrixStack ms, float partialTicks) {
        final float speed = newGhostSpeed.getValue();
        final float verticalSpeed = newGhostVerticalSpeed.getValue();
        final float baseSizePx = newGhostSize.getValue();
        final int maxTrailPoints = newGhostTrailLength.getValue().intValue();
        final float radiusConst = newGhostRadius.getValue();
        final float upperPosition = 1.8F;
        final float lowerPosition = 0.1F;
        final int streams = newGhostStreams.getValue().intValue();

        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d targetPos = new Vector3d(
                MathHelper.lerp(partialTicks, target.lastTickPosX, target.getPosX()),
                MathHelper.lerp(partialTicks, target.lastTickPosY, target.getPosY()),
                MathHelper.lerp(partialTicks, target.lastTickPosZ, target.getPosZ())
        );

        while (newGhostTrails.size() < streams) {
            newGhostTrails.add(new ArrayList<>());
        }

        final double t = System.currentTimeMillis() / (500.0 / speed);
        final double tv = System.currentTimeMillis() / (1000.0 / verticalSpeed);

        float radius = radiusConst;
        for (int k = 0; k < streams; k++) {
            double tj = t;
            double tvj = tv;
            double cyc = (Math.sin(tvj) + 1.0) * 0.5;

            double baseAngle = Math.toRadians(k * (360.0 / streams) + (tj * 50.0 % 360.0));

            double offX = Math.cos(baseAngle) * radius;
            double offZ = Math.sin(baseAngle) * radius;
            double offY = (k % 2 == 0)
                    ? lowerPosition + (upperPosition - lowerPosition) * cyc
                    : upperPosition - (upperPosition - lowerPosition) * cyc;

            Vector3d currentPoint = new Vector3d(
                    targetPos.x + offX,
                    targetPos.y + offY,
                    targetPos.z + offZ
            );

            List<TrailPoint> streamTrail = newGhostTrails.get(k);
            streamTrail.add(0, new TrailPoint(currentPoint, k));

            float maxLifeTime = 1000f;
            Iterator<TrailPoint> iterator = streamTrail.iterator();
            while (iterator.hasNext()) {
                TrailPoint point = iterator.next();
                if (point.getLifeProgress(maxLifeTime) >= 1.0f) {
                    iterator.remove();
                }
            }

            while (streamTrail.size() > maxTrailPoints) {
                streamTrail.remove(streamTrail.size() - 1);
            }

            radius *= -1.0f;
        }

        final float aPC = this.alpha.get();
        final float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));
        final int red = ColorUtil.getColor(190, 100, 100, (int) (255 * aPC));

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderSystem.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        RenderUtil.bindTexture(glowTexture);

        for (int k = 0; k < streams; k++) {
            List<TrailPoint> streamTrail = newGhostTrails.get(k);

            for (int j = 0; j < streamTrail.size(); j++) {
                TrailPoint point = streamTrail.get(j);
                Vector3d trailPos = point.position;

                float lifeProgress = point.getLifeProgress(1000f);
                float kf = j / (float) Math.max(streamTrail.size(), 1);

                if (isOccluded(trailPos, target)) continue;

                float ageFade = 1.0f - (lifeProgress * lifeProgress);
                float sizeFactor = (1.0f - (kf * 0.6f)) * ageFade;
                float dynSize = baseSizePx * sizeFactor;
                int dynAlpha = (int) (200 * aPC * sizeFactor);

                if (dynAlpha <= 5) continue;

                int color = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 2 + k * 30), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));
                int color2 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 4 + k * 30), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));
                int color3 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 8 + k * 30), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));
                int color4 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 16 + k * 30), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));

                ms.push();
                ms.translate(trailPos.x - camX, trailPos.y - camY, trailPos.z - camZ);
                ms.rotate(rotation);

                RectUtil.drawRect(ms, -dynSize / 2f, -dynSize / 2f,
                        dynSize, dynSize,
                        color, color2, color3, color4, true, true);

                ms.pop();
            }
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    public void drawMarker(MatrixStack matrix) {
        AuraNew aura = AuraNew.getInstance();
        matrix.push();
        float alphaPC = markerAnimation.get();
        Vector3d vec = RenderUtil3D.interpolate(target, mc.getRenderPartialTicks());
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        Vector2f marker = Project.project2D(x, y + ((target.getEyeHeight() + 0.4F) * 0.5F), z);
        if (marker.x == Float.MAX_VALUE && marker.y == Float.MAX_VALUE) {
            matrix.pop();
            return;
        }
        markerPosition.lerp(marker, target.isAlive() ? 0.5F : 0.05F);

        final float size = 50;

        long currentTimeMillis = System.currentTimeMillis();
        float angle = (float) Mathf.clamp(0, 90, ((Math.sin(currentTimeMillis / 500D) + 1F) / 2F) * 90);
        float scale = (float) Mathf.clamp(0.9, 1.1, ((Math.sin(currentTimeMillis / 500D) + 1F) / 2F) * 1.25);
        float rotate = (float) Mathf.clamp(0, 360, ((Math.sin(currentTimeMillis / 1000D) + 1F) / 2F) * 360);

        matrix.translate(markerPosition.x, markerPosition.y, 0.0F);
        matrix.scale(scale, scale, 1F);
        matrix.translate(-markerPosition.x, -markerPosition.y, 0.0F);

        float distanceFactor = (float) (1F - AuraUtil.getStrictDistance(target) / aura.attackDistance());
        float sc = Mathf.clamp(0.75F, 1F, distanceFactor);
        sc = Interpolator.lerp(scale, sc, 0.5F);

        matrix.translate(markerPosition.x, markerPosition.y, 0.0F);
        matrix.scale(sc, sc, sc);
        matrix.translate(-markerPosition.x, -markerPosition.y, 0.0F);

        float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));

        int red = ColorUtil.getColor(255, 0, 0, alphaPC);

        int color1 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(100), alphaPC), red, hurtPC);
        int color2 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(90), alphaPC), red, hurtPC);
        int color3 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(180), alphaPC), red, hurtPC);
        int color4 = ColorUtil.overCol(ColorUtil.multAlpha(ColorUtil.fade(270), alphaPC), red, hurtPC);

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        Namespaced texture = new Namespaced("texture/" + markerTexture.getValue() + ".png");

        GLUtil.rotate(matrix, markerPosition.x, markerPosition.y, Vector3f.ZN.rotationDegrees(45F - (angle - 45) + rotate), () -> {
            if (alphaPC != 0.0F) {
                mc.getTextureManager().bindTexture(texture);
                RectUtil.drawRect(matrix, markerPosition.x - size, markerPosition.y - size, size * 2F, size * 2F, color1, color2, color3, color4, true, true);
            }
        });

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrix.pop();
    }

    private void drawMovingCircle(LivingEntity target, MatrixStack ms, float partialTicks) {
        final float speed = ghostSpeed.getValue();
        final float verticalSpeed = ghostVerticalSpeed.getValue();
        final float baseSizePx = ghostSize.getValue();
        final int trailLength = ghostTrailLength.getValue().intValue();
        final float radiusConst = ghostRadius.getValue();
        final float upperPosition = 1.8F;
        final float lowerPosition = 0.1F;
        final int streams = ghostStreams.getValue().intValue();

        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d targetPos = new Vector3d(
                MathHelper.lerp(partialTicks, target.lastTickPosX, target.getPosX()),
                MathHelper.lerp(partialTicks, target.lastTickPosY, target.getPosY()),
                MathHelper.lerp(partialTicks, target.lastTickPosZ, target.getPosZ())
        );

        if (ghostAnimation.getValue()) {
            if (ghostBasePosition == null) {
                ghostBasePosition = targetPos;
                ghostVelocity = new Vector3d(0, 0, 0);
            }

            ghostVelocity = new Vector3d(
                    ghostVelocity.x * 0.85 + (targetPos.x - ghostBasePosition.x) * 0.15,
                    ghostVelocity.y * 0.85 + (targetPos.y - ghostBasePosition.y) * 0.15,
                    ghostVelocity.z * 0.85 + (targetPos.z - ghostBasePosition.z) * 0.15
            );

            ghostBasePosition = targetPos;
        } else {
            ghostBasePosition = targetPos;
            ghostVelocity = new Vector3d(0, 0, 0);
        }

        final double bx = ghostBasePosition.x - camX;
        final double by = ghostBasePosition.y - camY;
        final double bz = ghostBasePosition.z - camZ;

        final double t = System.currentTimeMillis() / (500.0 / speed);
        final double tv = System.currentTimeMillis() / (1000.0 / verticalSpeed);

        final float aPC = this.alpha.get();
        final float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));
        final int red = ColorUtil.getColor(190, 100, 100, (int) (255 * aPC));

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        float radius = radiusConst;
        for (int k = 0; k < streams; k++) {
            for (int j = 0; j < trailLength; j++) {
                float kf = j / (float) trailLength;

                double tj = t - j * 0.05;
                double tvj = tv - j * 0.05;
                double cyc = (Math.sin(tvj) + 1.0) * 0.5;

                double baseAngle = Math.toRadians(k * (360.0 / streams) + (tj * 50.0 % 360.0));

                double offX = Math.cos(baseAngle) * radius;
                double offZ = Math.sin(baseAngle) * radius;
                double offY = (k % 2 == 0)
                        ? lowerPosition + (upperPosition - lowerPosition) * cyc
                        : upperPosition - (upperPosition - lowerPosition) * cyc;

                if (ghostAnimation.getValue()) {
                    double trailFactor = j * 0.12;
                    offX -= ghostVelocity.x * trailFactor;
                    offZ -= ghostVelocity.z * trailFactor;
                    offY -= ghostVelocity.y * trailFactor * 0.5;
                }

                Vector3d particleWorldPos = new Vector3d(
                        ghostBasePosition.x + offX,
                        ghostBasePosition.y + offY,
                        ghostBasePosition.z + offZ
                );

                if (isOccluded(particleWorldPos, target)) continue;

                float sizeFactor = 1.0f - (kf * 0.6f);
                float dynSize = baseSizePx * sizeFactor;
                int dynAlpha = (int) (200 * alpha.getValue() * sizeFactor);

                int color = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 2), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));
                int color2 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 4), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));
                int color3 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 8), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));
                int color4 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 16), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));

                ms.push();
                ms.translate(bx + offX, by + offY, bz + offZ);
                ms.rotate(rotation);

                RenderUtil.bindTexture(new Namespaced("texture/glow.png"));
                RectUtil.drawRect(ms, -dynSize / 2f, -dynSize / 2f,
                        dynSize, dynSize,
                        color, color2, color3, color4, true, true);

                ms.pop();
            }
            radius *= -1.0f;
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    public void drawSouls(MatrixStack matrix) {
        matrix.push();
        float alphaPC = markerAnimation.get();

        if (alphaPC == 0.0F || target == null) {
            matrix.pop();
            return;
        }

        Vector3d vec = RenderUtil3D.interpolate(target, mc.getRenderPartialTicks());
        double baseX = vec.x;
        double baseY = vec.y + target.getEyeHeight() - 0.2f;
        double baseZ = vec.z;

        final double radius = soulRadius.getValue();
        final float speed = soulSpeed.getValue();
        final float size = soulSize.getValue();
        final int particleCount = soulCount.getValue().intValue();
        final int trailLength = soulTrailLength.getValue().intValue();

        long time = System.currentTimeMillis();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        mc.getTextureManager().bindTexture(glowTexture);

        int clientColorBase = Theme.getInstance().clientColor();

        for (int i = 0; i < particleCount; i++) {
            double baseAngle = (2 * Math.PI * i / particleCount);

            for (int trail = 0; trail < trailLength; trail++) {
                double angle = baseAngle + (time / 1000.0 * speed) - (trail * 0.05);
                double xOffset = Math.cos(angle) * radius;
                double zOffset = Math.sin(angle) * radius;

                double worldY = baseY - (trail * 0.02f);
                double worldX = baseX + xOffset;
                double worldZ = baseZ + zOffset;

                Vector3d particleWorldPos = new Vector3d(worldX, worldY, worldZ);
                if (isOccluded(particleWorldPos, target)) continue;

                Vector2f marker = Project.project2D(worldX, worldY, worldZ);
                if (marker.x == Float.MAX_VALUE && marker.y == Float.MAX_VALUE) continue;

                float alphaTrail = alphaPC * (1.0f - trail * (1.0f / trailLength));
                float dynamicSize = size * (1f + 0.2f * (float) Math.sin(time / 400.0 + i * 0.3));

                int clientColor = ColorUtil.multAlpha(clientColorBase, alphaTrail);

                RectUtil.drawRect(matrix, marker.x - dynamicSize / 2f, marker.y - dynamicSize / 2f, dynamicSize, dynamicSize, clientColor, true, true);
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        matrix.pop();
    }

    public void drawRing(MatrixStack ms) {
        LivingEntity entityToRender = target != null ? target : lastTarget;
        if (entityToRender == null || markerAnimation.get() == 0.0) return;

        final float baseSizePx = ringSize.getValue();
        final int particleCount = ringParticleCount.getValue().intValue();
        final int trailLength = ringTrailLength.getValue().intValue();
        final float ringRadius = entityToRender.getWidth() * 0.75F;

        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d vec = RenderUtil3D.interpolate(entityToRender, mc.getRenderPartialTicks());

        double duration = ringAnimationSpeed.getValue();
        double elapsed = (System.currentTimeMillis() % duration);
        boolean side = elapsed > (duration / 2);
        double progress = elapsed / (duration / 2);

        if (side) progress -= 1;
        else progress = 1 - progress;

        progress = (progress < 0.5) ? 2 * progress * progress : 1 - Math.pow((-2 * progress + 2), 2) / 2;

        double ringY = vec.y + (entityToRender.getHeight() * progress);

        final double bx = vec.x - camX;
        final double by = ringY - camY;
        final double bz = vec.z - camZ;

        final float aPC = markerAnimation.get();
        final float hurtPC = (float) Math.sin(entityToRender.hurtTime * (18F * Math.PI / 180F));
        final int red = ColorUtil.getColor(190, 100, 100, (int) (255 * aPC));

        long time = System.currentTimeMillis();
        double rotationOffset = (time % 3000) / 3000.0 * Math.PI * 2;

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderUtil.bindTexture(new Namespaced("texture/glow.png"));

        for (int i = 0; i < particleCount; i++) {
            double baseAngle = Math.toRadians(i * (360.0 / particleCount)) + rotationOffset;

            double flickerSpeed = 0.3 + (i % 5) * 0.1;
            double flicker = 0.7 + 0.3 * Math.sin(time * flickerSpeed / 100.0 + i * 1.7);

            for (int j = 0; j < trailLength; j++) {
                float kf = j / (float) trailLength;

                double angle = baseAngle - j * (0.025 / trailLength);

                double offX = Math.cos(angle) * ringRadius;
                double offZ = Math.sin(angle) * ringRadius;
                double offY = -j * (0.012 / trailLength);

                Vector3d particleWorldPos = new Vector3d(
                        vec.x + offX,
                        ringY + offY,
                        vec.z + offZ
                );

                if (isOccluded(particleWorldPos, entityToRender)) continue;

                float sizeFactor = 1.0f - (kf * 0.7f);
                float sizeFlicker = (float) (0.85 + 0.15 * Math.sin(time / 150.0 + i + j));
                float dynSize = baseSizePx * sizeFactor * sizeFlicker;

                int dynAlpha = (int) (200 * aPC * sizeFactor * flicker);

                int color = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(i * 11 + (int) (time / 20) % 360), aPC),
                        red,
                        hurtPC
                ), MathHelper.clamp(dynAlpha, 0, 255));

                ms.push();
                ms.translate(bx + offX, by + offY, bz + offZ);
                ms.rotate(rotation);

                RectUtil.drawRect(ms, -dynSize / 2f, -dynSize / 2f,
                        dynSize, dynSize,
                        color, color, color, color, true, true);

                ms.pop();
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private void drawHalo(MatrixStack ms, float partialTicks, LivingEntity target) {
        if (target == null || markerAnimation.get() == 0.0) return;

        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d vec = RenderUtil3D.interpolate(target, partialTicks);

        final float aPC = markerAnimation.get();
        final float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));
        final int red = ColorUtil.getColor(190, 100, 100, (int) (255 * aPC));

        // Exactly 3 ghost parts, like ghost style but arranged as halo.
        final int ghostCount = 3;
        final int trailLength = MathHelper.clamp(haloParticleCount.getValue().intValue(), 10, 42);
        final float radius = target.getWidth() * 0.72f + haloRadius.getValue();
        final float baseSizePx = haloSize.getValue();
        final float speed = haloSpeed.getValue();
        final float tilt = haloTilt.getValue() / 90.0f;
        final float lengthMul = haloLength.getValue();
        final double centerY = target.getHeight() * haloHeight.getValue();
        final double verticalAmplitude = Math.max(0.06, target.getHeight() * 0.22);

        final double t = System.currentTimeMillis() / (500.0 / speed);
        final double tv = System.currentTimeMillis() / (900.0 / speed);
        final double baseY = centerY + Math.sin(tv) * verticalAmplitude;

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderUtil.bindTexture(glowTexture);

        for (int ghost = 0; ghost < ghostCount; ghost++) {
            final double ghostShift = Math.toRadians(ghost * (360.0 / ghostCount));

            for (int j = 0; j < trailLength; j++) {
                float kf = j / (float) trailLength;

                double tj = t - j * 0.05;
                double angle = tj * 0.95 + ghostShift;

                double offX = Math.cos(angle) * radius;
                double offZ = Math.sin(angle) * radius;
                double offY = baseY + Math.sin(angle) * (radius * 0.22 * tilt);

                Vector3d particleWorldPos = new Vector3d(vec.x + offX, vec.y + offY, vec.z + offZ);
                if (isOccluded(particleWorldPos, target)) continue;

                // Ghost-like elastic behavior
                float sizeFactor = 1.0f - (kf * 0.55f);
                float stretch = 0.9f + 0.25f * (float) Math.sin(tj * 2.0 + j * 0.35);
                float dynSize = baseSizePx * sizeFactor;
                float ghostWidth = dynSize * (2.0f + stretch * 0.35f);
                float ghostLength = dynSize * (1.2f + (1.25f - stretch) * 0.25f) * lengthMul;

                int dynAlpha = (int) (210 * aPC * sizeFactor);
                dynAlpha = MathHelper.clamp(dynAlpha, 0, 255);

                int color1 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 2 + ghost * 31), aPC), red, hurtPC), dynAlpha);
                int color2 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 4 + ghost * 31), aPC), red, hurtPC), dynAlpha);
                int color3 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 8 + ghost * 31), aPC), red, hurtPC), dynAlpha);
                int color4 = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(j * 16 + ghost * 31), aPC), red, hurtPC), dynAlpha);

                ms.push();
                ms.translate(particleWorldPos.x - camX, particleWorldPos.y - camY, particleWorldPos.z - camZ);
                ms.rotate(rotation);
                RectUtil.drawRect(ms, -ghostWidth / 2f, -ghostLength / 2f, ghostWidth, ghostLength, color1, color2, color3, color4, true, true);
                ms.pop();
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private int applyDamageFlash(int color) {
        if (!damageRed.getValue()) return color;

        float targetIntensity = 0f;
        long timeSinceDamage = System.currentTimeMillis() - lastDamageTime;
        if (timeSinceDamage < DAMAGE_FLASH_DURATION) {
            float progress = (float) timeSinceDamage / DAMAGE_FLASH_DURATION;
            targetIntensity = 1.0f - easeOutCubic(progress);
        }

        damageFlashIntensity = MathHelper.lerp(0.1f, damageFlashIntensity, targetIntensity);
        if (damageFlashIntensity < 0.05f) return color;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) & 0xFF;

        int finalR = (int) MathHelper.lerp(damageFlashIntensity, r, 255);
        int finalG = (int) MathHelper.lerp(damageFlashIntensity, g, 50);
        int finalB = (int) MathHelper.lerp(damageFlashIntensity, b, 50);
        return (a << 24) | (finalR << 16) | (finalG << 8) | finalB;
    }

    private float easeOutQuad(float t) {
        return 1f - (1f - t) * (1f - t);
    }

    private void renderJello(Render3DPosedEvent event, LivingEntity entityToRender) {
        jelloMoving += 4f;

        float alphaValue = markerAnimation.get();
        if (alphaValue <= 0.0F) return;
        if (entityToRender == null) return;

        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        Vector3d vec = RenderUtil3D.interpolate(entityToRender, event.getPartialTicks());
        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        final float entityWidth = entityToRender.getWidth() * 1.65f;
        final float entityHeight = entityToRender.getHeight() - 0.15f;
        final float alphaAnim = (target != null) ? easeOutCubic(alphaValue) : easeOutQuad(alphaValue);
        final int layers = jelloLayers.getValue().intValue();
        final float baseSize = jelloSize.getValue();
        final int color = applyDamageFlash(Theme.getInstance().clientColor());
        final int cr = (color >> 16) & 0xFF;
        final int cg = (color >> 8) & 0xFF;
        final int cb = color & 0xFF;

        MatrixStack ms = event.getMatrix();

        GL11.glDepthMask(false);
        if (jelloThroughWalls.getValue()) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
        RenderSystem.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderUtil.bindTexture(glowTexture);

        float scale = Math.max(0.5f, 0.7f - 0.2f * alphaAnim);
        for (int i = 0; i < 360; i += 2) {
            double rad = Math.toRadians(i + jelloMoving);
            float xOffset = (float) (Math.cos(rad) * entityWidth * scale);
            float zOffset = (float) (Math.sin(rad) * entityWidth * scale);

            for (int j = 0; j < layers; ++j) {
                float yOffsetLayer = entityHeight / 1.7f + (entityHeight / 2.0f) * (float) Math.cos(Math.toRadians(jelloMoving / 1.5f + j * 2.0f));
                int finalAlpha = (int) (255 * alphaAnim * ((float) j / layers) * 0.08f);
                finalAlpha = MathHelper.clamp(finalAlpha, 0, 255);
                int finalColor = ColorUtil.getColor(cr, cg, cb, finalAlpha);

                Vector3d particleWorldPos = new Vector3d(
                        vec.x + xOffset,
                        vec.y + yOffsetLayer,
                        vec.z + zOffset
                );
                if (isOccluded(particleWorldPos, entityToRender)) continue;

                ms.push();
                ms.translate(vec.x - camX + xOffset, vec.y - camY + yOffsetLayer, vec.z - camZ + zOffset);
                ms.rotate(rotation);
                RectUtil.drawRect(ms, -baseSize / 2.0f, -baseSize / 2.0f, baseSize, baseSize,
                        finalColor, finalColor, finalColor, finalColor, true, true);
                ms.pop();
            }
        }

        float sizeLarge = baseSize * 1.08f;
        int headAlpha = MathHelper.clamp((int) (255 * alphaAnim * 0.22f), 0, 255);
        int headColor = ColorUtil.getColor(cr, cg, cb, headAlpha);

        for (int i = 0; i < 360; i += 2) {
            double rad = Math.toRadians(i + jelloMoving);
            float xOffset = (float) (Math.cos(rad) * entityWidth * scale);
            float zOffset = (float) (Math.sin(rad) * entityWidth * scale);
            float yOffset = entityHeight / 1.75f + (entityHeight / 2.0f) * (float) Math.cos(Math.toRadians(jelloMoving / 1.5f + 30.0f));

            Vector3d particleWorldPos = new Vector3d(
                    vec.x + xOffset,
                    vec.y + yOffset,
                    vec.z + zOffset
            );
            if (isOccluded(particleWorldPos, entityToRender)) continue;

            ms.push();
            ms.translate(vec.x - camX + xOffset, vec.y - camY + yOffset, vec.z - camZ + zOffset);
            ms.rotate(rotation);
            RectUtil.drawRect(ms, -sizeLarge / 2.0f, -sizeLarge / 2.0f, sizeLarge, sizeLarge,
                    headColor, headColor, headColor, headColor, true, true);
            ms.pop();
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private void drawSpiral(MatrixStack ms, float partialTicks, LivingEntity target) {
        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d vec = RenderUtil3D.interpolate(target, partialTicks);
        final double bx = vec.x - camX;
        final double by = vec.y - camY;
        final double bz = vec.z - camZ;

        final float aPC = alpha.get();
        final float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));
        final int red = ColorUtil.getColor(190, 100, 100, (int) (255 * aPC));

        long time = System.currentTimeMillis();
        float speed = spiralSpeed.getValue();
        float size = spiralSize.getValue();
        float height = spiralHeight.getValue();
        float radius = spiralRadius.getValue();
        int particles = spiralParticles.getValue().intValue();

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderUtil.bindTexture(glowTexture);

        for (int i = 0; i < particles; i++) {
            float progress = i / (float) particles;
            double angle = progress * Math.PI * 4 + (time / 1000.0 * speed);

            double x = bx + Math.cos(angle) * radius * (1 - progress * 0.3);
            double y = by + progress * height;
            double z = bz + Math.sin(angle) * radius * (1 - progress * 0.3);

            Vector3d particleWorldPos = new Vector3d(
                    vec.x + Math.cos(angle) * radius * (1 - progress * 0.3),
                    vec.y + progress * height,
                    vec.z + Math.sin(angle) * radius * (1 - progress * 0.3)
            );

            if (isOccluded(particleWorldPos, target)) continue;

            float alpha = aPC * (1f - progress * 0.5f);
            int color = ColorUtil.replAlpha(ColorUtil.overCol(
                    ColorUtil.multAlpha(ColorUtil.fade((int) (progress * 360)), aPC),
                    red,
                    hurtPC
            ), (int) (alpha * 255));

            ms.push();
            ms.translate(x, y, z);
            ms.rotate(rotation);

            RectUtil.drawRect(ms, -size / 2f, -size / 2f, size, size, color, color, color, color, true, true);

            ms.pop();
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private void drawOrbit(MatrixStack ms, float partialTicks, LivingEntity target) {
        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d vec = RenderUtil3D.interpolate(target, partialTicks);
        final double bx = vec.x - camX;
        final double by = vec.y + target.getHeight() / 2f - camY;
        final double bz = vec.z - camZ;

        final float aPC = alpha.get();
        final float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));
        final int red = ColorUtil.getColor(190, 100, 100, (int) (255 * aPC));

        long time = System.currentTimeMillis();
        float speed = orbitSpeed.getValue();
        float size = orbitSize.getValue();
        float radius = orbitRadius.getValue();
        int orbits = orbitCount.getValue().intValue();
        int particles = orbitParticles.getValue().intValue();

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderUtil.bindTexture(glowTexture);

        for (int orbit = 0; orbit < orbits; orbit++) {
            float orbitAngle = (float) (orbit * Math.PI / orbits);

            for (int i = 0; i < particles; i++) {
                double angle = i * (2 * Math.PI / particles) + (time / 1000.0 * speed) + orbit * 0.5;

                double x = bx + Math.cos(angle) * radius * Math.cos(orbitAngle);
                double y = by + Math.sin(angle) * radius;
                double z = bz + Math.cos(angle) * radius * Math.sin(orbitAngle);

                Vector3d particleWorldPos = new Vector3d(
                        vec.x + Math.cos(angle) * radius * Math.cos(orbitAngle),
                        vec.y + target.getHeight() / 2f + Math.sin(angle) * radius,
                        vec.z + Math.cos(angle) * radius * Math.sin(orbitAngle)
                );

                if (isOccluded(particleWorldPos, target)) continue;

                int color = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(i * (360 / particles) + orbit * 60), aPC),
                        red,
                        hurtPC
                ), (int) (aPC * 255));

                ms.push();
                ms.translate(x, y, z);
                ms.rotate(rotation);

                RectUtil.drawRect(ms, -size / 2f, -size / 2f, size, size, color, color, color, color, true, true);

                ms.pop();
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private void drawVortex(MatrixStack ms, float partialTicks, LivingEntity target) {
        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d vec = RenderUtil3D.interpolate(target, partialTicks);
        final double bx = vec.x - camX;
        final double by = vec.y + target.getHeight() - camY;
        final double bz = vec.z - camZ;

        final float aPC = alpha.get();
        final float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));
        final int red = ColorUtil.getColor(190, 100, 100, (int) (255 * aPC));

        long time = System.currentTimeMillis();
        float speed = vortexSpeed.getValue();
        float size = vortexSize.getValue();
        float radius = vortexRadius.getValue();
        int particles = vortexParticles.getValue().intValue();
        int layers = vortexLayers.getValue().intValue();

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderUtil.bindTexture(glowTexture);

        for (int layer = 0; layer < layers; layer++) {
            float layerProgress = layer / (float) layers;
            float layerY = -layerProgress * target.getHeight();

            for (int i = 0; i < particles; i++) {
                float particleProgress = i / (float) particles;
                double angle = particleProgress * Math.PI * 2 + (time / 1000.0 * speed) + layer * 0.8;

                float currentRadius = radius * (1 - layerProgress * 0.5f);
                double x = bx + Math.cos(angle) * currentRadius;
                double y = by + layerY + Math.sin(time / 500.0 + i * 0.1) * 0.1;
                double z = bz + Math.sin(angle) * currentRadius;

                Vector3d particleWorldPos = new Vector3d(
                        vec.x + Math.cos(angle) * currentRadius,
                        vec.y + target.getHeight() + layerY + Math.sin(time / 500.0 + i * 0.1) * 0.1,
                        vec.z + Math.sin(angle) * currentRadius
                );

                if (isOccluded(particleWorldPos, target)) continue;

                float alpha = aPC * (1f - layerProgress * 0.3f);
                int color = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade((int) (particleProgress * 360 + layer * 40)), aPC),
                        red,
                        hurtPC
                ), (int) (alpha * 255));

                ms.push();
                ms.translate(x, y, z);
                ms.rotate(rotation);

                RectUtil.drawRect(ms, -size / 2f, -size / 2f, size, size, color, color, color, color, true, true);

                ms.pop();
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private void drawAura(MatrixStack ms, float partialTicks, LivingEntity target) {
        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d vec = RenderUtil3D.interpolate(target, partialTicks);
        final double bx = vec.x - camX;
        final double by = vec.y + target.getHeight() / 2f - camY;
        final double bz = vec.z - camZ;

        final float aPC = alpha.get();
        final float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));
        final int red = ColorUtil.getColor(190, 100, 100, (int) (255 * aPC));

        long time = System.currentTimeMillis();
        float speed = auraSpeed.getValue();
        float size = auraSize.getValue();
        float radius = auraRadius.getValue();
        int waves = auraWaves.getValue().intValue();
        int particles = auraParticles.getValue().intValue();

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        RenderUtil.bindTexture(glowTexture);

        for (int wave = 0; wave < waves; wave++) {
            float waveProgress = (float) ((time / 1000.0 * speed + wave * 0.5) % 1.0);
            float currentRadius = radius * waveProgress;
            float waveAlpha = (1f - waveProgress) * aPC;

            for (int i = 0; i < particles; i++) {
                double angle = i * (2 * Math.PI / particles);

                double x = bx + Math.cos(angle) * currentRadius;
                double y = by + Math.sin(time / 800.0 + i * 0.1) * 0.2;
                double z = bz + Math.sin(angle) * currentRadius;

                Vector3d particleWorldPos = new Vector3d(
                        vec.x + Math.cos(angle) * currentRadius,
                        vec.y + target.getHeight() / 2f + Math.sin(time / 800.0 + i * 0.1) * 0.2,
                        vec.z + Math.sin(angle) * currentRadius
                );

                if (isOccluded(particleWorldPos, target)) continue;

                int color = ColorUtil.replAlpha(ColorUtil.overCol(
                        ColorUtil.multAlpha(ColorUtil.fade(i * (360 / particles) + wave * 80), waveAlpha),
                        red,
                        hurtPC
                ), (int) (waveAlpha * 255));

                float dynamicSize = size * (1f + waveProgress * 0.5f);

                ms.push();
                ms.translate(x, y, z);
                ms.rotate(rotation);

                RectUtil.drawRect(ms, -dynamicSize / 2f, -dynamicSize / 2f, dynamicSize, dynamicSize, color, color, color, color, true, true);

                ms.pop();
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private float easeOutCubic(float t) {
        return 1 - (float) Math.pow(1 - t, 3);
    }

    private void renderCrystals2(MatrixStack matrices, float partialTicks, float animProgress, LivingEntity entityToRender) {
        float alpha = animProgress;
        if (alpha <= 0.0F) return;

        float easedAnim = easeOutCubic(alpha);
        float tickDelta = partialTicks;

        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;

        Vector3d targetPos = RenderUtil3D.interpolate(entityToRender, partialTicks);
        Vector3d cameraPos = camera.getProjectedView();
        Vector3d renderPos = targetPos.subtract(cameraPos);

        float time = (mc.player.ticksExisted + tickDelta) * crystal2Speed.getValue();
        float entityHeight = entityToRender.getHeight();
        float entityWidth = entityToRender.getWidth();
        float halfWidth = entityWidth * 0.5f;

        matrices.push();
        matrices.translate(renderPos.x, renderPos.y, renderPos.z);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        GL11.glDepthMask(false);

        int crystalCount = crystal2Count.getValue().intValue();
        for (int i = 0; i < crystalCount; i++) {
            float seed1 = (float) Math.sin(i * 1.7f + 0.3f) * 0.5f + 0.5f;
            float seed2 = (float) Math.cos(i * 2.3f + 0.7f) * 0.5f + 0.5f;
            float seed3 = (float) Math.sin(i * 3.1f + 1.1f) * 0.5f + 0.5f;

            float angleOffset = i * (360f / crystalCount) + seed1 * 12f;
            float angle = time + angleOffset;

            float radius = halfWidth + crystal2Radius.getValue() + seed3 * 0.15f;
            float x = radius * (float) Math.cos(Math.toRadians(angle));
            float z = radius * (float) Math.sin(Math.toRadians(angle));
            float y = seed2 * entityHeight;

            float crystalScale = crystal2Size.getValue() * easedAnim;

            int color = ColorUtil.multAlpha(ColorUtil.fade(i * 26), alpha * crystal2Alpha.getValue());
            drawCrystal2B(matrices, camera, x, y, z, crystalScale * crystal2GlowRadius.getValue(), color, alpha * crystal2Alpha.getValue() * crystal2GlowPower.getValue());
        }

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableTexture();

        for (int i = 0; i < crystalCount; i++) {
            float seed1 = (float) Math.sin(i * 1.7f + 0.3f) * 0.5f + 0.5f;
            float seed2 = (float) Math.cos(i * 2.3f + 0.7f) * 0.5f + 0.5f;
            float seed3 = (float) Math.sin(i * 3.1f + 1.1f) * 0.5f + 0.5f;

            float angleOffset = i * (360f / crystalCount) + seed1 * 12f;
            float angle = time + angleOffset;

            float radius = halfWidth + crystal2Radius.getValue() + seed3 * 0.15f;
            float x = radius * (float) Math.cos(Math.toRadians(angle));
            float z = radius * (float) Math.sin(Math.toRadians(angle));
            float y = seed2 * entityHeight;

            float crystalScale = crystal2Size.getValue() * easedAnim;
            float dirAngle = angle;

            int color = ColorUtil.multAlpha(ColorUtil.fade(i * 26), alpha * crystal2Alpha.getValue());
            drawCrystal2H(matrices, x, y, z, crystalScale, dirAngle, color, alpha * crystal2Alpha.getValue());
        }

        RenderSystem.enableTexture();

        matrices.pop();

        RenderSystem.enableCull();
        GL11.glDepthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void drawCrystal2B(MatrixStack matrices, ActiveRenderInfo camera, float x, float y, float z, float size, int color, float alpha) {
        MatrixStack tempMatrix = new MatrixStack();
        tempMatrix.getLast().getMatrix().mul(matrices.getLast().getMatrix());
        tempMatrix.translate(x, y, z);
        tempMatrix.rotate(Vector3f.YP.rotationDegrees(-camera.getYaw()));
        tempMatrix.rotate(Vector3f.XP.rotationDegrees(camera.getPitch()));

        MatrixStack.Entry entry = tempMatrix.getLast();
        int finalColor = ColorUtil.multAlpha(color, alpha);

        RenderUtil.bindTexture(bloomTexture);
        RectUtil.drawRect(tempMatrix, -size / 2, -size / 2, size, size, finalColor, finalColor, finalColor, finalColor, true, true);
    }

    private void drawCrystal2H(MatrixStack matrices, float x, float y, float z, float scale, float yaw, int color, float alpha) {
        matrices.push();
        matrices.translate(x, y, z);
        matrices.rotate(Vector3f.YP.rotationDegrees(-yaw + 90f));
        matrices.scale(scale, scale, scale);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.getLast().getMatrix();

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (180 * alpha);

        int rL = Math.min(255, (int) (r * 1.3f));
        int gL = Math.min(255, (int) (g * 1.3f));
        int bL = Math.min(255, (int) (b * 1.3f));

        int rD = (int) (r * 0.6f);
        int gD = (int) (g * 0.6f);
        int bD = (int) (b * 0.6f);

        float detail = crystal2Detail.getValue();
        float w = 0.5f * detail;
        float h = detail;

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(matrix, 0, 0, h).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, -w, 0, 0).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, 0, w, 0).color(rL, gL, bL, a).endVertex();

        buffer.pos(matrix, 0, 0, h).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, 0, w, 0).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, w, 0, 0).color(rL, gL, bL, a).endVertex();

        buffer.pos(matrix, 0, 0, h).color(r, g, b, a).endVertex();
        buffer.pos(matrix, w, 0, 0).color(r, g, b, a).endVertex();
        buffer.pos(matrix, 0, -w, 0).color(r, g, b, a).endVertex();

        buffer.pos(matrix, 0, 0, h).color(r, g, b, a).endVertex();
        buffer.pos(matrix, 0, -w, 0).color(r, g, b, a).endVertex();
        buffer.pos(matrix, -w, 0, 0).color(r, g, b, a).endVertex();

        buffer.pos(matrix, 0, 0, -h).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, 0, w, 0).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, -w, 0, 0).color(rD, gD, bD, a).endVertex();

        buffer.pos(matrix, 0, 0, -h).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, w, 0, 0).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, 0, w, 0).color(rD, gD, bD, a).endVertex();

        buffer.pos(matrix, 0, 0, -h).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, 0, -w, 0).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, w, 0, 0).color(rD, gD, bD, a).endVertex();

        buffer.pos(matrix, 0, 0, -h).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, -w, 0, 0).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, 0, -w, 0).color(rD, gD, bD, a).endVertex();

        tessellator.draw();

        matrices.pop();
    }

    private void renderCubes2(MatrixStack matrices, float partialTicks, float animProgress, LivingEntity entityToRender) {
        float alpha = animProgress;
        if (alpha <= 0.0F) return;

        float easedAnim = easeOutCubic(alpha);
        float tickDelta = partialTicks;

        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;

        Vector3d targetPos = RenderUtil3D.interpolate(entityToRender, partialTicks);
        Vector3d cameraPos = camera.getProjectedView();
        Vector3d renderPos = targetPos.subtract(cameraPos);

        float time = (mc.player.ticksExisted + tickDelta) * cube2Speed.getValue();
        float entityHeight = entityToRender.getHeight();
        float entityWidth = entityToRender.getWidth();
        float halfWidth = entityWidth * 0.5f;

        matrices.push();
        matrices.translate(renderPos.x, renderPos.y, renderPos.z);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        GL11.glDepthMask(false);

        int cubeCount = cube2Count.getValue().intValue();
        for (int i = 0; i < cubeCount; i++) {
            float seed1 = (float) Math.sin(i * 1.7f + 0.3f) * 0.5f + 0.5f;
            float seed2 = (float) Math.cos(i * 2.3f + 0.7f) * 0.5f + 0.5f;
            float seed3 = (float) Math.sin(i * 3.1f + 1.1f) * 0.5f + 0.5f;

            float angleOffset = i * (360f / cubeCount) + seed1 * 12f;
            float angle = time + angleOffset;

            float radius = halfWidth + cube2Radius.getValue() + seed3 * 0.15f;
            float x = radius * (float) Math.cos(Math.toRadians(angle));
            float z = radius * (float) Math.sin(Math.toRadians(angle));
            float y = seed2 * entityHeight;

            float cubeScale = cube2Size.getValue() * easedAnim;

            int color = ColorUtil.multAlpha(ColorUtil.fade(i * 26), alpha * cube2Alpha.getValue());
            drawCube2B(matrices, camera, x, y, z, cubeScale * cube2GlowRadius.getValue(), color, alpha * cube2Alpha.getValue() * cube2GlowPower.getValue());
        }

        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableTexture();

        for (int i = 0; i < cubeCount; i++) {
            float seed1 = (float) Math.sin(i * 1.7f + 0.3f) * 0.5f + 0.5f;
            float seed2 = (float) Math.cos(i * 2.3f + 0.7f) * 0.5f + 0.5f;
            float seed3 = (float) Math.sin(i * 3.1f + 1.1f) * 0.5f + 0.5f;

            float angleOffset = i * (360f / cubeCount) + seed1 * 12f;
            float angle = time + angleOffset;

            float radius = halfWidth + cube2Radius.getValue() + seed3 * 0.15f;
            float x = radius * (float) Math.cos(Math.toRadians(angle));
            float z = radius * (float) Math.sin(Math.toRadians(angle));
            float y = seed2 * entityHeight;

            float cubeScale = cube2Size.getValue() * easedAnim;
            float dirAngle = angle;

            int color = ColorUtil.multAlpha(ColorUtil.fade(i * 26), alpha * cube2Alpha.getValue());
            drawCube2H(matrices, x, y, z, cubeScale, dirAngle, color, alpha * cube2Alpha.getValue());
        }

        RenderSystem.enableTexture();

        matrices.pop();

        RenderSystem.enableCull();
        GL11.glDepthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    private void drawCube2B(MatrixStack matrices, ActiveRenderInfo camera, float x, float y, float z, float size, int color, float alpha) {
        MatrixStack tempMatrix = new MatrixStack();
        tempMatrix.getLast().getMatrix().mul(matrices.getLast().getMatrix());
        tempMatrix.translate(x, y, z);
        tempMatrix.rotate(Vector3f.YP.rotationDegrees(-camera.getYaw()));
        tempMatrix.rotate(Vector3f.XP.rotationDegrees(camera.getPitch()));

        MatrixStack.Entry entry = tempMatrix.getLast();
        int finalColor = ColorUtil.multAlpha(color, alpha);

        RenderUtil.bindTexture(bloomTexture);
        RectUtil.drawRect(tempMatrix, -size / 2, -size / 2, size, size, finalColor, finalColor, finalColor, finalColor, true, true);
    }

    private void drawCube2H(MatrixStack matrices, float x, float y, float z, float scale, float yaw, int color, float alpha) {
        matrices.push();
        matrices.translate(x, y, z);
        matrices.rotate(Vector3f.YP.rotationDegrees(-yaw + 90f));
        matrices.scale(scale, scale, scale);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f matrix = matrices.getLast().getMatrix();

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (180 * alpha);

        int rL = Math.min(255, (int) (r * 1.3f));
        int gL = Math.min(255, (int) (g * 1.3f));
        int bL = Math.min(255, (int) (b * 1.3f));

        int rD = (int) (r * 0.6f);
        int gD = (int) (g * 0.6f);
        int bD = (int) (b * 0.6f);

        float detail = cube2Detail.getValue();
        float half = 0.5f * detail;

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);

        buffer.pos(matrix, -half, half, half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, half, half, half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, half, -half, half).color(r, g, b, a).endVertex();
        buffer.pos(matrix, -half, -half, half).color(r, g, b, a).endVertex();

        buffer.pos(matrix, half, half, half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, half, half, -half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, half, -half, -half).color(r, g, b, a).endVertex();
        buffer.pos(matrix, half, -half, half).color(r, g, b, a).endVertex();

        buffer.pos(matrix, half, half, -half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, -half, half, -half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(r, g, b, a).endVertex();
        buffer.pos(matrix, half, -half, -half).color(r, g, b, a).endVertex();

        buffer.pos(matrix, -half, half, -half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, -half, half, half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, -half, -half, half).color(r, g, b, a).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(r, g, b, a).endVertex();

        buffer.pos(matrix, -half, half, -half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, half, half, -half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, half, half, half).color(rL, gL, bL, a).endVertex();
        buffer.pos(matrix, -half, half, half).color(rL, gL, bL, a).endVertex();

        buffer.pos(matrix, -half, -half, half).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, half, -half, half).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, half, -half, -half).color(rD, gD, bD, a).endVertex();
        buffer.pos(matrix, -half, -half, -half).color(rD, gD, bD, a).endVertex();

        tessellator.draw();

        matrices.pop();
    }
}





