package org.privates.client.managers.component.impl.aura;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.systems.RenderSystem;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.component.Component;
import org.privates.client.managers.events.render.Render2DEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.impl.combat.AuraNew;
import org.privates.client.utils.animation.Animation;
import org.privates.client.utils.animation.util.Easings;
import org.privates.client.utils.math.Interpolator;
import org.privates.client.utils.math.Mathf;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.*;
import org.privates.client.utils.rotation.AuraUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AuraComponent extends Component {

    private final Namespaced bloomTexture = new Namespaced("texture/glow.png");
    private final Namespaced glowTexture = new Namespaced("texture/glow.png");
    public LivingEntity target;
    public LivingEntity lastTarget;
    public final Animation markerAnimation = new Animation();
    public final Animation crystalAnimation = new Animation();
    private final Vector2f markerPosition = new Vector2f();
    private final List<GhostParticle> ghostParticles = new ArrayList<>();
    private final Random random = new Random();

    private Vector3d ghostBasePosition = null;
    private Vector3d ghostVelocity = new Vector3d(0, 0, 0);

    @EventHandler
    public void onEvent(Render2DEvent event) {
        AuraNew aura = AuraNew.getInstance();
        markerAnimation.update();
        crystalAnimation.update();

        if (aura.target() != null) target = aura.target();
        markerAnimation.run(aura.target() == null ? 0 : 1, 1, Easings.LINEAR, true);
        crystalAnimation.run(aura.target() != null ? 1 : 0, 0.6, Easings.SINE_OUT, true);

        if (markerAnimation.getValue() == 0.0) {
            target = null;
            lastTarget = null;
        }

        if (target != null) {
            lastTarget = target;
        }

        if (target == null && lastTarget == null) {
            ghostBasePosition = null;
            return;
        }

        auraProcess(event);
    }

    private float crystalOrbitRadius = 1.0f;
    private float targetOrbitRadius = 1.0f;
    private long lastHurtTime = 0;
    private boolean isContractingAnimation = false;
    private long contractAnimationStart = 0;
    private static final long CONTRACT_DURATION = 150;
    private static final long EXPAND_DURATION = 300;
    private static final float MIN_ORBIT_RADIUS = 0.3f;
    private static final float DEFAULT_ORBIT_RADIUS = 0.75f;

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        AuraNew aura = AuraNew.getInstance();

        LivingEntity entityToRender = target != null ? target : lastTarget;
        if (entityToRender == null) {
            ghostBasePosition = null;
            return;
        }

        if (markerAnimation.get() == 0.0 && crystalAnimation.get() == 0.0) return;

        MatrixStack matrix = event.getMatrix();
        alpha.update();
        alpha.run(1, 0.5, Easings.LINEAR, true);

    }

    private void auraProcess(Render2DEvent event) {
        AuraNew aura = AuraNew.getInstance();
    }

    private void renderCrystals(MatrixStack matrix, float partialTicks, float animProgress, LivingEntity entityToRender) {
        Vector3d interpolated = RenderUtil3D.interpolate(entityToRender, partialTicks);
        double cx = interpolated.x;
        double cy = interpolated.y + entityToRender.getHeight() * 0.55;
        double cz = interpolated.z;

        RenderSystem.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        renderCrystalRing(matrix, partialTicks, entityToRender, cx, cy, cz, 0.9, 1.8, 0, animProgress);
        renderCrystalRing(matrix, partialTicks, entityToRender, cx, cy, cz, 1.1, 2.0, 0.6, animProgress);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
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
        double cy = interpolated.y + entityToRender.getHeight() * 0.55 - camY;
        double cz = interpolated.z - camZ;

        updateOrbitAnimation(entityToRender);

        boolean poisoned = entityToRender.isPotionActive(Effects.POISON);
        int redColor = ColorUtil.getColor(255, 0, 0, 1.0f);
        float hurtProgress = (float) Math.sin(entityToRender.hurtTime * (12F * Math.PI / 180F)); // Медленнее hurt анимация

        long curMs = System.currentTimeMillis();
        int themeColor = Theme.getInstance().clientColor();
        int hue = poisoned ? ColorUtil.overCol(themeColor, 0xFF00FF00, 0.7f) : themeColor; // Более subtle poison
        hue = ColorUtil.overCol(hue, redColor, hurtProgress * 0.6f); // Снижаем интенсивность hurt

        double t = curMs / 1200.0; // Замедляем время (было /1000, теперь медленнее)
        double pulse = 1.0 + 0.08 * Math.sin(t * 1.2) + 0.02 * Math.sin(t * 2.5); // Более subtle и многослойная пульсация

        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        RenderSystem.enableBlend();
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

        RenderUtil.bindTexture(glowTexture);

        float animatedRadius = (float) (crystalOrbitRadius * pulse);

        renderCrystalOrbit3D(ms, rotation, cx, cy, cz, animatedRadius * 0.95, 0.8, 0, animProgress, t, pulse, hue, hurtProgress, redColor); // Медленная скорость
        renderCrystalOrbit3D(ms, rotation, cx, cy, cz, animatedRadius * 1.05, 1.0, 0.4, animProgress, t, pulse, hue, hurtProgress, redColor); // Ещё медленнее

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
    }

    private void updateOrbitAnimation(LivingEntity entityToRender) {
        long currentTime = System.currentTimeMillis();

        if (entityToRender.hurtTime > 0 && entityToRender.hurtTime == 10) {
            if (currentTime - lastHurtTime > 300) { // Увеличиваем задержку для реже срабатывания
                isContractingAnimation = true;
                contractAnimationStart = currentTime;
                lastHurtTime = currentTime;
            }
        }

        if (isContractingAnimation) {
            long elapsed = currentTime - contractAnimationStart;

            if (elapsed < CONTRACT_DURATION) {
                float progress = elapsed / (float) CONTRACT_DURATION;
                progress = easeOutQuart(progress); // Более плавный easing
                targetOrbitRadius = DEFAULT_ORBIT_RADIUS - (DEFAULT_ORBIT_RADIUS - MIN_ORBIT_RADIUS) * progress;
            } else if (elapsed < CONTRACT_DURATION + EXPAND_DURATION) {
                float expandElapsed = elapsed - CONTRACT_DURATION;
                float progress = expandElapsed / (float) EXPAND_DURATION;
                progress = easeOutBack(progress); // Добавляем overshoot для современного вида
                targetOrbitRadius = MIN_ORBIT_RADIUS + (DEFAULT_ORBIT_RADIUS - MIN_ORBIT_RADIUS) * progress;
            } else {
                isContractingAnimation = false;
                targetOrbitRadius = DEFAULT_ORBIT_RADIUS;
            }
        } else {
            targetOrbitRadius = DEFAULT_ORBIT_RADIUS;
        }

        float lerpSpeed = 0.08f; // Замедляем lerp для большей плавности
        crystalOrbitRadius = crystalOrbitRadius + (targetOrbitRadius - crystalOrbitRadius) * lerpSpeed;
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
                                      double t, double pulse, int hue, float hurtProgress, int redColor) {
        float scale = animProgress;
        float crystalSize = 0.14f * scale;

        float contractFactor = crystalOrbitRadius / DEFAULT_ORBIT_RADIUS;
        float sizeBoost = 1.0f + (1.0f - contractFactor) * 0.3f; // Менее агрессивный boost
        crystalSize *= sizeBoost;

        for (int i = 0; i < 6; i++) {
            double ang = t * speed + i * (2 * Math.PI / 6.0) + Math.sin(t * 0.5 + i) * 0.2; // Добавляем лёгкую волну для плавности

            // Эллиптическая орбита для современного вида
            double ellipseX = orbitR * 1.1 * Math.cos(ang);
            double ellipseZ = orbitR * 0.9 * Math.sin(ang);

            double ox = cx + ellipseX;
            double oy = cy + yOffset + 0.12 * Math.sin(t + i * 0.5); // Медленнее и меньше амплитуда
            double oz = cz + ellipseZ;

            int finalColor = ColorUtil.overCol(hue, redColor, hurtProgress * 0.4f); // Ещё subtle hurt

            float glowIntensity = 0.5f + (1.0f - contractFactor) * 0.3f;
            int glowColor = ColorUtil.multAlpha(finalColor, glowIntensity * animProgress);

            float glowSize = (0.35f + (1.0f - contractFactor) * 0.15f) * scale; // Меньше glow

            ms.push();
            ms.translate(ox, oy, oz);
            ms.rotate(rotation);

            RectUtil.drawRect(ms, -glowSize / 2f, -glowSize / 2f, glowSize, glowSize,
                    glowColor, glowColor, glowColor, glowColor, true, true);

            ms.pop();

            ms.push();
            ms.translate(ox, oy, oz);

            float baseRotSpeed = isContractingAnimation ? 80f : 40f; // Значительно медленнее ротация
            float rotAngle = (float) ((t * baseRotSpeed + i * 60) % 360);
            ms.rotate(Vector3f.YP.rotationDegrees(rotAngle));
            ms.rotate(Vector3f.XP.rotationDegrees(rotAngle * 0.5f)); // Медленнее X ротация

            int crystalColor = ColorUtil.multAlpha(finalColor, animProgress * 0.9f); // Чуть меньше яркости
            drawCrystal3D(ms, crystalSize, crystalColor);

            ms.pop();
        }
    }

    private void drawCrystal3D(MatrixStack ms, float size, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        float a = ((color >> 24) & 0xFF) / 255.0f;
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        Matrix4f matrix = ms.getLast().getMatrix();

        float top = size * 1.1f; // Немного удлиняем для современного вида
        float bottom = -size;
        float side = size * 0.7f;

        RenderSystem.disableTexture();
        RenderSystem.disableCull();

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);

        // Верхние грани с градиентом для глубины
        buffer.pos(matrix, 0, top, 0).color(r, g, b, a).endVertex();
        buffer.pos(matrix, side, 0, 0).color(r * 0.9f, g * 0.9f, b * 0.9f, a).endVertex();
        buffer.pos(matrix, 0, 0, side).color(r * 0.9f, g * 0.9f, b * 0.9f, a).endVertex();

        buffer.pos(matrix, 0, top, 0).color(r, g, b, a).endVertex();
        buffer.pos(matrix, 0, 0, side).color(r * 0.85f, g * 0.85f, b * 0.85f, a).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(r * 0.8f, g * 0.8f, b * 0.8f, a).endVertex();

        buffer.pos(matrix, 0, top, 0).color(r, g, b, a).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(r * 0.8f, g * 0.8f, b * 0.8f, a).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(r * 0.75f, g * 0.75f, b * 0.75f, a).endVertex();

        buffer.pos(matrix, 0, top, 0).color(r, g, b, a).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(r * 0.75f, g * 0.75f, b * 0.75f, a).endVertex();
        buffer.pos(matrix, side, 0, 0).color(r * 0.9f, g * 0.9f, b * 0.9f, a).endVertex();

        // Нижние грани с более тёмным градиентом
        buffer.pos(matrix, 0, bottom, 0).color(r * 0.6f, g * 0.6f, b * 0.6f, a * 0.8f).endVertex();
        buffer.pos(matrix, 0, 0, side).color(r * 0.7f, g * 0.7f, b * 0.7f, a * 0.8f).endVertex();
        buffer.pos(matrix, side, 0, 0).color(r * 0.7f, g * 0.7f, b * 0.7f, a * 0.8f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(r * 0.6f, g * 0.6f, b * 0.6f, a * 0.8f).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(r * 0.6f, g * 0.6f, b * 0.6f, a * 0.8f).endVertex();
        buffer.pos(matrix, 0, 0, side).color(r * 0.7f, g * 0.7f, b * 0.7f, a * 0.8f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(r * 0.6f, g * 0.6f, b * 0.6f, a * 0.8f).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(r * 0.5f, g * 0.5f, b * 0.5f, a * 0.8f).endVertex();
        buffer.pos(matrix, -side, 0, 0).color(r * 0.6f, g * 0.6f, b * 0.6f, a * 0.8f).endVertex();

        buffer.pos(matrix, 0, bottom, 0).color(r * 0.6f, g * 0.6f, b * 0.6f, a * 0.8f).endVertex();
        buffer.pos(matrix, side, 0, 0).color(r * 0.7f, g * 0.7f, b * 0.7f, a * 0.8f).endVertex();
        buffer.pos(matrix, 0, 0, -side).color(r * 0.5f, g * 0.5f, b * 0.5f, a * 0.8f).endVertex();

        tessellator.draw();

        RenderSystem.enableCull();
        RenderSystem.enableTexture();
    }

    private void renderCrystalRing(MatrixStack matrix, float partialTicks, LivingEntity entityToRender,
                                   double cx, double cy, double cz, double orbitR, double speed, double yOffset, float animProgress) {
        boolean poisoned = entityToRender.isPotionActive(Effects.POISON);
        int redColor = ColorUtil.getColor(255, 0, 0, 1.0f);
        float hurtProgress = (float) Math.sin(entityToRender.hurtTime * (18F * Math.PI / 180F));
        int overrideRGB = poisoned ? 0xFF00FF00 : redColor;

        long curMs = System.currentTimeMillis();
        int themeColor = Theme.getInstance().clientColor();
        int hue = ColorUtil.overCol(themeColor, overrideRGB, hurtProgress);

        int lighterThemeColor = ColorUtil.multBright(hue, 1.5f);
        int baseRGB = ColorUtil.multAlpha(lighterThemeColor, 200f / 255f);

        double t = curMs / 1000.0;
        double pulse = 1.0 + 0.1 * Math.sin(t * 3.0);

        float scale = animProgress;

        Vector3d top = new Vector3d(0, 0.14 * scale, 0);
        Vector3d bottom = new Vector3d(0, -0.14 * scale, 0);
        Vector3d px = new Vector3d(0.14 * scale, 0, 0);
        Vector3d nx = new Vector3d(-0.14 * scale, 0, 0);
        Vector3d pz = new Vector3d(0, 0, 0.14 * scale);
        Vector3d nz = new Vector3d(0, 0, -0.14 * scale);

        Vector3d[][] faces = new Vector3d[][]{
                {top, px, pz}, {top, pz, nx}, {top, nx, nz}, {top, nz, px},
                {bottom, pz, px}, {bottom, nx, pz}, {bottom, nz, nx}, {bottom, px, nz}
        };

        float shellScale = 1.15f;

        for (int i = 0; i < 6; i++) {
            double ang = t * speed + i * (2 * Math.PI / 6.0);

            double ox = cx + orbitR * pulse * Math.cos(ang);
            double oy = cy + yOffset + 0.15 * Math.sin(t * 2.5 + i * 0.8);
            double oz = cz + orbitR * pulse * Math.sin(ang);

            Vector3d crystalPos = new Vector3d(ox, oy, oz);
            Vector3d entityCenter = new Vector3d(cx, cy, cz);

            for (Vector3d[] tri : faces) {
                Vector3d l0 = tri[0].scale(shellScale);
                Vector3d l1 = tri[1].scale(shellScale);
                Vector3d l2 = tri[2].scale(shellScale);

                Vector3d v0 = transformCrystalVertex(crystalPos, entityCenter, l0);
                Vector3d v1 = transformCrystalVertex(crystalPos, entityCenter, l1);
                Vector3d v2 = transformCrystalVertex(crystalPos, entityCenter, l2);

                Vector3d n = v1.subtract(v0).crossProduct(v2.subtract(v0)).normalize();
                Vector3d lightDir = new Vector3d(0.6, 1.0, 0.4).normalize();
                double ndl = Math.max(0.1, n.dotProduct(lightDir));
                int faceColor = shadeColor(baseRGB, (float) ndl);

                int shellColor = ColorUtil.multAlpha(faceColor, 0.3f * animProgress);

                Vector2f p0 = Project.project2D(v0.x, v0.y, v0.z);
                Vector2f p1 = Project.project2D(v1.x, v1.y, v1.z);
                Vector2f p2 = Project.project2D(v2.x, v2.y, v2.z);
                if (p0 == null || p1 == null || p2 == null) continue;

                drawFilledTriangle(matrix, p0, p1, p2, shellColor);
            }
        }

        for (int i = 0; i < 6; i++) {
            double ang = t * speed + i * (2 * Math.PI / 6.0);
            double ox = cx + orbitR * pulse * Math.cos(ang);
            double oy = cy + yOffset + 0.15 * Math.sin(t * 2.5 + i * 0.8);
            double oz = cz + orbitR * pulse * Math.sin(ang);

            Vector3d crystalPos = new Vector3d(ox, oy, oz);
            Vector3d entityCenter = new Vector3d(cx, cy, cz);

            Vector2f pCenter = Project.project2D(ox, oy, oz);
            if (pCenter != null) {
                float distance = (float) mc.getRenderManager().info.getProjectedView().distanceTo(new Vector3d(ox, oy, oz));
                float scaleFactor = Math.max(0.5f, 5.0f / distance);
                float glowSize = 30.0f * scaleFactor * scale;

                int finalColor = ColorUtil.overCol(hue, redColor, hurtProgress);
                int fadedColor = ColorUtil.multAlpha(finalColor, 0.05f * animProgress);
                int mainColor = ColorUtil.multAlpha(finalColor, animProgress);

                RenderUtil.bindTexture(glowTexture);
                RectUtil.drawRect(matrix, pCenter.x - glowSize / 2, pCenter.y - glowSize / 2, glowSize, glowSize, fadedColor, fadedColor, fadedColor, fadedColor, true, true);
                RectUtil.drawRect(matrix, pCenter.x - glowSize / 2, pCenter.y - glowSize / 2, glowSize, glowSize, mainColor, mainColor, mainColor, mainColor, true, true);
            }

            for (Vector3d[] tri : faces) {
                Vector3d v0 = transformCrystalVertex(crystalPos, entityCenter, tri[0]);
                Vector3d v1 = transformCrystalVertex(crystalPos, entityCenter, tri[1]);
                Vector3d v2 = transformCrystalVertex(crystalPos, entityCenter, tri[2]);

                Vector3d n = v1.subtract(v0).crossProduct(v2.subtract(v0)).normalize();
                Vector3d lightDir = new Vector3d(0.6, 1.0, 0.4).normalize();
                double ndl = Math.max(0.1, n.dotProduct(lightDir));
                int faceColor = shadeColor(baseRGB, (float) ndl);
                faceColor = ColorUtil.multAlpha(faceColor, animProgress);

                Vector2f p0 = Project.project2D(v0.x, v0.y, v0.z);
                Vector2f p1 = Project.project2D(v1.x, v1.y, v1.z);
                Vector2f p2 = Project.project2D(v2.x, v2.y, v2.z);
                if (p0 == null || p1 == null || p2 == null) continue;

                drawFilledTriangle(matrix, p0, p1, p2, faceColor);
            }
        }
    }

    private Vector3d transformCrystalVertex(Vector3d crystalPos, Vector3d targetPos, Vector3d localPos) {
        Vector3d modelUp = targetPos.subtract(crystalPos).normalize();
        Vector3d modelRight = new Vector3d(0, 1, 0).crossProduct(modelUp).normalize();

        if (modelRight.lengthSquared() < 0.001) {
            modelRight = new Vector3d(1, 0, 0).crossProduct(modelUp).normalize();
        }

        Vector3d modelForward = modelUp.crossProduct(modelRight).normalize();

        double worldX = crystalPos.x + modelRight.x * localPos.x + modelUp.x * localPos.y + modelForward.x * localPos.z;
        double worldY = crystalPos.y + modelRight.y * localPos.x + modelUp.y * localPos.y + modelForward.y * localPos.z;
        double worldZ = crystalPos.z + modelRight.z * localPos.x + modelUp.z * localPos.y + modelForward.z * localPos.z;

        return new Vector3d(worldX, worldY, worldZ);
    }

    private int shadeColor(int rgba, float k) {
        return ColorUtil.multBright(rgba, k);
    }

    private void drawFilledTriangle(MatrixStack matrix, Vector2f p0, Vector2f p1, Vector2f p2, int rgba) {
        RenderSystem.disableTexture();
        RenderSystem.disableCull();

        float a = (float) (rgba >> 24 & 255) / 255.0F;
        float r = (float) (rgba >> 16 & 255) / 255.0F;
        float g = (float) (rgba >> 8 & 255) / 255.0F;
        float b = (float) (rgba & 255) / 255.0F;

        Matrix4f mat = matrix.getLast().getMatrix();
        BufferBuilder buf = Tessellator.getInstance().getBuffer();
        buf.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        buf.pos(mat, p0.x, p0.y, 0).color(r, g, b, a).endVertex();
        buf.pos(mat, p1.x, p1.y, 0).color(r, g, b, a).endVertex();
        buf.pos(mat, p2.x, p2.y, 0).color(r, g, b, a).endVertex();
        Tessellator.getInstance().draw();

        RenderSystem.enableCull();
        RenderSystem.enableTexture();
    }

    private final Namespaced markerLocation = new Namespaced("texture/marker.png");

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

        GLUtil.rotate(matrix, markerPosition.x, markerPosition.y, Vector3f.ZN.rotationDegrees(45F - (angle - 45) + rotate), () -> {
            if (alphaPC != 0.0F) {
                mc.getTextureManager().bindTexture(markerLocation);
                RectUtil.drawRect(matrix, markerPosition.x - size, markerPosition.y - size, size * 2F, size * 2F, color1, color2, color3, color4, true, true);
            }
        });

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        matrix.pop();
    }

    private static class GhostParticle {
        final Vector3d startVec;
        Vector3d velocity;
        final long startTime;
        final boolean canSplit;
        final float sizeFactor;

        GhostParticle(Vector3d startVec, Vector3d velocity, long startTime, boolean canSplit, float sizeFactor) {
            this.startVec = startVec;
            this.velocity = velocity;
            this.startTime = startTime;
            this.canSplit = canSplit;
            this.sizeFactor = sizeFactor;
        }
    }

    private boolean isParticleOccluded(Vector3d cameraPos, Vector3d particlePos, AxisAlignedBB targetBB) {
        return false;
    }

    public Animation alpha = new Animation();
    private final Vector3d previousTargetPos = null;


    private void drawMovingCircle(LivingEntity target, MatrixStack ms, float partialTicks, boolean fadeEnabled) {
        final float speed = 3;
        final float verticalSpeed = 6;
        final float baseSizePx = 0.6F;
        final int trailLength = 25;
        final float radiusConst = 0.65F;
        final float upperPosition = 1.8F;
        final float lowerPosition = 0.1F;

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

        if (ghostBasePosition == null) {
            ghostBasePosition = targetPos;
            ghostVelocity = new Vector3d(0, 0, 0);
        }

        ghostBasePosition = targetPos;

        ghostVelocity = new Vector3d(
                ghostVelocity.x * 0.85 + (targetPos.x - ghostBasePosition.x) * 0.15,
                ghostVelocity.y * 0.85 + (targetPos.y - ghostBasePosition.y) * 0.15,
                ghostVelocity.z * 0.85 + (targetPos.z - ghostBasePosition.z) * 0.15
        );

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
        for (int k = 0; k < 3; k++) {
            for (int j = 0; j < trailLength; j++) {
                float kf = j / (float) trailLength;

                double tj = t - j * 0.05;
                double tvj = tv - j * 0.05;
                double cyc = (Math.sin(tvj) + 1.0) * 0.5;

                double baseAngle = Math.toRadians(k * 90.0 + (tj * 50.0 % 360.0));

                double offX = Math.cos(baseAngle) * radius;
                double offZ = Math.sin(baseAngle) * radius;
                double offY = (k % 2 == 0)
                        ? lowerPosition + (upperPosition - lowerPosition) * cyc
                        : upperPosition - (upperPosition - lowerPosition) * cyc;

                double trailFactor = j * 0.12;
                offX -= ghostVelocity.x * trailFactor;
                offZ -= ghostVelocity.z * trailFactor;
                offY -= ghostVelocity.y * trailFactor * 0.5;

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
                ms.translate(-dynSize / 12f, -dynSize / 12f, 0f);
                ms.rotate(rotation);
                ms.translate(dynSize / 12f, dynSize / 12f, 0f);

                RenderUtil.bindTexture(new Namespaced("texture/glow.png"));
                RectUtil.drawRect(ms, -dynSize / 2f, -dynSize / 12f,
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

        final double radius = 0.6;
        final float speed = 4.0f;
        final float size = 17f;
        final int particleCount = 3;
        final int trailLength = 35;

        long time = System.currentTimeMillis();

        Vector3d cameraPos = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
        AxisAlignedBB targetBB = target.getBoundingBox();

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

                Vector3d particlePos = new Vector3d(worldX, worldY, worldZ);
                if (isParticleOccluded(cameraPos, particlePos, targetBB)) continue;

                Vector2f marker = Project.project2D(worldX, worldY, worldZ);
                if (marker.x == Float.MAX_VALUE && marker.y == Float.MAX_VALUE) continue;

                float alphaTrail = alphaPC * (1.0f - trail * (1.0f / trailLength));
                float dynamicSize = size * (1f + 0.2f * (float) Math.sin(time / 400.0 + i * 0.3));

                int clientColor = ColorUtil.multAlpha(clientColorBase, alphaTrail);

                RectUtil.drawRect(matrix, marker.x - dynamicSize / 2f, marker.y - dynamicSize / 2f, dynamicSize, dynamicSize, clientColor, true, true);
                matrix.pop();
            }
        }

        RenderSystem.enableDepthTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        matrix.pop();
    }

    public void drawFireworks(MatrixStack matrix) {
        matrix.push();
        float alphaPC = markerAnimation.get();

        if (alphaPC == 0.0F || target == null) {
            matrix.pop();
            return;
        }

        Vector3d vec = RenderUtil3D.interpolate(target, mc.getRenderPartialTicks());
        double baseX = vec.x;
        double baseY = vec.y + target.getHeight() / 2.0f;
        double baseZ = vec.z;

        final double radius = 0.6;
        final float speed = 1.7f;
        final float size = 12f;
        final int fireworkCount = 5;
        final int trailLength = 60;
        final float glowScale = 2.5f;

        long time = System.currentTimeMillis();
        float hurtPC = (float) Math.sin(target.hurtTime * (18F * Math.PI / 180F));

        Vector3d cameraPos = mc.gameRenderer.getActiveRenderInfo().getProjectedView();
        AxisAlignedBB targetBB = target.getBoundingBox();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.blendFuncSeparate(770, 771, 1, 0);
        mc.getTextureManager().bindTexture(bloomTexture);

        for (int firework = 0; firework < fireworkCount; firework++) {
            double baseAngle = (2 * Math.PI * firework / fireworkCount);
            double verticalOffset = Math.sin(time / 800.0 + firework * 0.5) * 0.4;

            for (int trail = 0; trail < trailLength; trail++) {
                double angle = baseAngle + (time / 1000.0 * speed) - (trail * 0.08);
                double radiusOffset = radius + Math.sin(time / 600.0 + trail * 0.1) * 0.3;

                double xOffset = Math.cos(angle) * radiusOffset;
                double zOffset = Math.sin(angle) * radiusOffset;
                double yOffset = verticalOffset + Math.sin(trail * 0.2 + time / 400.0) * 0.2;

                double worldX = baseX + xOffset;
                double worldY = baseY + yOffset;
                double worldZ = baseZ + zOffset;

                Vector3d particlePos = new Vector3d(worldX, worldY, worldZ);
                if (isParticleOccluded(cameraPos, particlePos, targetBB)) continue;

                Vector2f marker = Project.project2D(worldX, worldY, worldZ);
                if (marker.x == Float.MAX_VALUE && marker.y == Float.MAX_VALUE) continue;

                float alphaTrail = alphaPC * (1.0f - trail * (0.8f / trailLength));
                float sparkleEffect = (float) Math.abs(Math.sin(time / 150.0 + trail * 0.3 + firework * 0.7));
                float dynamicSize = size * (0.8f + 0.4f * sparkleEffect);

                int fireworkColor = ColorUtil.fade((firework * 72 + trail * 5) % 360);
                int finalColor = ColorUtil.multAlpha(fireworkColor, alphaTrail);

                int red = ColorUtil.getColor(255, 100, 0, alphaTrail * alphaPC);
                int blendedColor = ColorUtil.overCol(finalColor, red, hurtPC);

                matrix.push();
                matrix.translate(marker.x, marker.y, 0.0F);
                matrix.scale(glowScale * (0.8f + sparkleEffect * 0.3f), glowScale * (0.8f + sparkleEffect * 0.3f), 1.0F);
                matrix.translate(-marker.x, -marker.y, 0);

                RectUtil.drawRect(matrix, marker.x - dynamicSize / 2f, marker.y - dynamicSize / 2f,
                        dynamicSize, dynamicSize, blendedColor, true, true);

                if (trail < 5 && sparkleEffect > 0.7f) {
                    float explosionSize = dynamicSize * 1.8f * sparkleEffect;
                    int explosionColor = ColorUtil.multAlpha(ColorUtil.getColor(255, 255, 200, alphaTrail * 0.6f), alphaPC);
                    RectUtil.drawRect(matrix, marker.x - explosionSize / 2f, marker.y - explosionSize / 2f,
                            explosionSize, explosionSize, explosionColor, true, true);
                }

                matrix.pop();
            }

            for (int spark = 0; spark < 8; spark++) {
                double sparkAngle = baseAngle + (spark * Math.PI / 4) + (time / 500.0);
                double sparkRadius = radius * 0.6 + Math.sin(time / 300.0 + spark * 0.4) * 0.2;

                double sparkX = baseX + Math.cos(sparkAngle) * sparkRadius;
                double sparkY = baseY + Math.sin(time / 400.0 + spark * 0.6) * 0.3;
                double sparkZ = baseZ + Math.sin(sparkAngle) * sparkRadius;

                Vector2f sparkMarker = Project.project2D(sparkX, sparkY, sparkZ);
                if (sparkMarker.x == Float.MAX_VALUE && sparkMarker.y == Float.MAX_VALUE) continue;

                float sparkAlpha = alphaPC * (0.6f + 0.4f * (float) Math.sin(time / 200.0 + spark * 0.8));
                float sparkSize = size * 0.4f * (0.8f + 0.4f * (float) Math.sin(time / 180.0 + spark * 0.5));

                int sparkColor = ColorUtil.fade((firework * 60 + spark * 20) % 360);
                int finalSparkColor = ColorUtil.multAlpha(sparkColor, sparkAlpha);

                matrix.push();
                matrix.translate(sparkMarker.x, sparkMarker.y, 0.0F);
                matrix.scale(1.5f, 1.5f, 1.0F);
                matrix.translate(-sparkMarker.x, -sparkMarker.y, 0);

                RectUtil.drawRect(matrix, sparkMarker.x - sparkSize / 2f, sparkMarker.y - sparkSize / 2f,
                        sparkSize, sparkSize, finalSparkColor, true, true);
                matrix.pop();
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

        final float baseSizePx = 0.12F;
        final int particleCount = 32;
        final int trailLength = 5;
        final float ringRadius = entityToRender.getWidth() * 0.75F;

        final EntityRendererManager rm = mc.getRenderManager();
        final ActiveRenderInfo camera = rm.info;
        final Quaternion rotation = camera.getRotation().copy();

        final double camX = camera.getProjectedView().x;
        final double camY = camera.getProjectedView().y;
        final double camZ = camera.getProjectedView().z;

        Vector3d vec = RenderUtil3D.interpolate(entityToRender, mc.getRenderPartialTicks());

        double duration = 2000;
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

            double radiusWobble = Math.sin(time / 400.0 + i * 0.5) * 0.03;

            for (int j = 0; j < trailLength; j++) {
                float kf = j / (float) trailLength;

                double trailWave = Math.sin(time / 300.0 + i * 0.3 + j * 0.5) * 0.02;
                double angle = baseAngle - j * 0.025 - trailWave;

                double currentRadius = ringRadius + radiusWobble;
                double offX = Math.cos(angle) * currentRadius;
                double offZ = Math.sin(angle) * currentRadius;
                double offY = -j * 0.012 + Math.sin(time / 350.0 + i * 0.4) * 0.02;

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
}