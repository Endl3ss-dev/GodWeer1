package fun.volon.client.modules.impl.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.util.Identifier;
import fun.volon.api.utils.namespaced.Namespaced;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.Event3DRender;
import fun.volon.api.events.implement.EventAttackEntity;
import fun.volon.api.events.implement.EventPacket;
import fun.volon.api.utils.color.ColorUtils;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class HitMarker extends Module {

    public static HitMarker INSTANCE = new HitMarker();

    private final FloatSetting size = new FloatSetting("Размер", 0.5f, 0.1f, 2.0f, 0.05f);
    private final FloatSetting fadeInTime = new FloatSetting("Время появления", 100f, 50f, 500f, 10f);
    private final FloatSetting displayTime = new FloatSetting("Время показа", 300f, 100f, 1000f, 50f);
    private final FloatSetting fadeOutTime = new FloatSetting("Время исчезновения", 200f, 50f, 500f, 10f);
    private final BooleanSetting glow = new BooleanSetting("Свечение", true);
    private final BooleanSetting scaleAnim = new BooleanSetting("Анимация масштаба", true);
    private final BooleanSetting ghostEffect = new BooleanSetting("Эффект силуэта", true);
    private final BooleanSetting totemEffect = new BooleanSetting("Эффекты при тотеме", true);

    private final ArrayList<HitMarkerData> hitMarkers = new ArrayList<>();
    private final ArrayList<HitGhostData> hitGhosts = new ArrayList<>();
    private final List<TotemSpiralEffect> totemSpirals = new CopyOnWriteArrayList<>();

    // Размер глоу спрайта относительно блока
    private static final float GLOW_INTENSITY = 0.38f;

    public HitMarker() {
        super("HitMarker", "Показывает маркер при ударе", ModuleCategory.RENDER);
        addSettings(size, fadeInTime, displayTime, fadeOutTime, glow, scaleAnim, ghostEffect, totemEffect);
    }

    @Override
    public void onDisable() {
        hitMarkers.clear();
        hitGhosts.clear();
        totemSpirals.clear();
        super.onDisable();
    }

    private Identifier getTexture() {
        return Namespaced.of("textures/cross/cross.png");
    }

    private Identifier getGlowTexture() {
        return Namespaced.of("textures/targetesp/bloom.png");
    }

    @EventLink
    public void onAttack(EventAttackEntity event) {
        if (mc.player == null || mc.world == null) return;

        Entity target = event.getTarget();
        if (target != null) {
            synchronized (hitMarkers) {
                hitMarkers.add(new HitMarkerData(
                        resolveHitPosition(event.getPlayer(), target),
                        System.currentTimeMillis(),
                        (long) fadeInTime.get(),
                        (long) displayTime.get(),
                        (long) fadeOutTime.get()
                ));
            }

            if (ghostEffect.isState() && target instanceof LivingEntity living) {
                // Захватываем позу цели
                float partialTicks = mc.getRenderTickCounter().getTickDelta(true);
                double tx = MathHelper.lerp(partialTicks, target.prevX, target.getX());
                double ty = MathHelper.lerp(partialTicks, target.prevY, target.getY());
                double tz = MathHelper.lerp(partialTicks, target.prevZ, target.getZ());

                Vec3d velocity = target.getVelocity();
                float speed = (float) Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);

                // Направление движения цели (нормализованное)
                Vec3d moveDir;
                if (speed > 0.01f) {
                    moveDir = new Vec3d(velocity.x / speed, 0, velocity.z / speed);
                } else {
                    // Если цель стоит — силуэт летит от атакующего
                    Vec3d toTarget = new Vec3d(target.getX() - event.getPlayer().getX(),
                            0, target.getZ() - event.getPlayer().getZ());
                    double len = toTarget.length();
                    moveDir = len > 0.01 ? toTarget.multiply(1.0 / len) : new Vec3d(0, 0, 1);
                }

                float bodyYaw = MathHelper.lerp(partialTicks,
                        ((LivingEntity) target).prevBodyYaw, ((LivingEntity) target).bodyYaw);
                float headYaw = bodyYaw;
                float headPitch = MathHelper.lerp(partialTicks, target.prevPitch, target.getPitch());
                float limbSwing = living.limbAnimator.getPos(partialTicks);
                float limbSwingAmt = living.limbAnimator.getSpeed(partialTicks);
                boolean sneaking = target.isSneaking();

                synchronized (hitGhosts) {
                    hitGhosts.add(new HitGhostData(
                            new Vec3d(tx, ty, tz),
                            moveDir,
                            bodyYaw, headYaw - bodyYaw, headPitch,
                            limbSwing, limbSwingAmt, sneaking,
                            System.currentTimeMillis()
                    ));
                }
            }
        }
    }

    @EventLink
    public void onPacket(EventPacket event) {
        if (!totemEffect.isState()) return;
        if (mc.world == null || mc.player == null) return;
        if (event.getType() != EventPacket.Type.RECEIVE) return;
        if (!(event.getPacket() instanceof EntityStatusS2CPacket packet)) return;
        if (packet.getStatus() != 35) return;

        mc.execute(() -> {
            if (mc.world == null) return;
            Entity entity = packet.getEntity(mc.world);
            if (!(entity instanceof LivingEntity living)) return;

            float pt = mc.getRenderTickCounter().getTickDelta(true);
            double cx = MathHelper.lerp(pt, entity.prevX, entity.getX());
            double cy = MathHelper.lerp(pt, entity.prevY, entity.getY()) + entity.getHeight() * 0.6;
            double cz = MathHelper.lerp(pt, entity.prevZ, entity.getZ());
            totemSpirals.add(new TotemSpiralEffect(new Vec3d(cx, cy, cz), System.currentTimeMillis()));
        });
    }

    private Vec3d resolveHitPosition(Entity attacker, Entity target) {
        Vec3d fallback = new Vec3d(
                target.getX(),
                target.getY() + target.getHeight() / 2.0,
                target.getZ()
        );
        if (attacker == null) return fallback;

        Vec3d eyePos = attacker.getCameraPosVec(1.0F);
        Vec3d lookVec = attacker.getRotationVec(1.0F);
        Vec3d targetCenter = target.getBoundingBox().getCenter();
        double distance = Math.max(eyePos.distanceTo(targetCenter) + 1.0, 6.0);
        Vec3d reachPos = eyePos.add(lookVec.multiply(distance));

        Optional<Vec3d> hitPos = target.getBoundingBox().raycast(eyePos, reachPos);
        if (hitPos.isPresent()) {
            return hitPos.get();
        }
        return eyePos.add(lookVec.multiply(eyePos.distanceTo(targetCenter)));
    }

    @EventLink
    public void onRender3D(Event3DRender e) {
        if (mc.player == null || mc.world == null) return;

        synchronized (hitMarkers) { hitMarkers.removeIf(HitMarkerData::isDead); }
        synchronized (hitGhosts)  { hitGhosts.removeIf(HitGhostData::isDead); }

        Vec3d camera = mc.gameRenderer.getCamera().getPos();
        MatrixStack matrices = e.getMatrices();

        // --- Рисуем обычные хитмаркеры (крестик убран, только силуэт) ---
        if (!hitMarkers.isEmpty()) {
            // Крестик больше не рисуем — очищаем список и идём дальше
            synchronized (hitMarkers) { hitMarkers.clear(); }
        }

        // --- Рисуем силуэты ---
        if (ghostEffect.isState() && !hitGhosts.isEmpty()) {
            ArrayList<HitGhostData> ghostList;
            synchronized (hitGhosts) { ghostList = new ArrayList<>(hitGhosts); }

            int themeColor = ColorUtils.getThemeColor();
            float tr = ColorUtils.redf(themeColor);
            float tg = ColorUtils.greenf(themeColor);
            float tb = ColorUtils.bluef(themeColor);

            for (HitGhostData ghost : ghostList) {
                float progress = ghost.getProgress();
                float alpha = ghost.getAlpha();
                if (alpha <= 0) continue;

                // Силуэт летит назад — против направления движения цели
                // Смещение нарастает со временем
                double flyDist = progress * 2.5; // максимум 2.5 блока назад
                double offsetX = -ghost.moveDir.x * flyDist;
                double offsetZ = -ghost.moveDir.z * flyDist;
                // Небольшой подъём вверх в начале, потом оседает
                double offsetY = Math.sin(progress * Math.PI) * 0.6;

                double renderX = ghost.position.x + offsetX - camera.x;
                double renderY = ghost.position.y + offsetY - camera.y;
                double renderZ = ghost.position.z + offsetZ - camera.z;

                // Пульсация свечения
                float pulse = 1.0f + 0.25f * MathHelper.sin((float)(System.currentTimeMillis() / 80.0));
                float glowAlpha = alpha * GLOW_INTENSITY * pulse;

                matrices.push();
                matrices.translate(renderX, renderY, renderZ);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-ghost.bodyYaw));

                renderGhostGlow(matrices, tr, tg, tb, glowAlpha, alpha, ghost);

                matrices.pop();
            }
        }

        // --- Рисуем спирали тотема ---
        if (totemEffect.isState() && !totemSpirals.isEmpty()) {
            totemSpirals.removeIf(TotemSpiralEffect::isDead);
            for (TotemSpiralEffect spiral : totemSpirals) {
                renderTotemSpiral(matrices, camera, spiral);
            }
        }
    }

    /** Рендер желто-зелёных спиральных лучей при сносе тотема */
    private void renderTotemSpiral(MatrixStack matrices, Vec3d camera, TotemSpiralEffect spiral) {
        float progress = spiral.getProgress();
        float alpha    = spiral.getAlpha();
        if (alpha <= 0.01f) return;

        long now = System.currentTimeMillis();
        float rx = (float)(spiral.origin.x - camera.x);
        float ry = (float)(spiral.origin.y - camera.y);
        float rz = (float)(spiral.origin.z - camera.z);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        RenderSystem.lineWidth(1.6f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        matrices.push();
        matrices.translate(rx, ry, rz);

        int NUM_SPIRALS = 6;
        for (int s = 0; s < NUM_SPIRALS; s++) {
            float phaseOffset = (float)(s * Math.PI * 2.0 / NUM_SPIRALS);
            // Спираль: начинается снаружи (разлёт) и всасывается к центру
            // progress 0→0.4: разлёт, 0.4→1.0: всасывание
            float maxRadius;
            float t; // позиция по радиусу 0=центр 1=макс
            if (progress < 0.35f) {
                t = progress / 0.35f; // расширяемся
                maxRadius = 1.8f + (float)Math.sin(t * Math.PI * 0.5f) * 0.5f;
            } else {
                t = 1.0f - (progress - 0.35f) / 0.65f; // всасываемся обратно
                maxRadius = 1.8f * t;
            }

            // Цвет: чередуем жёлтый и зелёный
            boolean yellow = (s % 2 == 0);
            int cr = yellow ? 255 : 80;
            int cg = yellow ? 220 : 255;
            int cb = yellow ? 30  : 60;
            int ca = (int)(alpha * 255);

            int STEPS = 32;
            BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            Matrix4f mat = matrices.peek().getPositionMatrix();
            float spiralSpeed = (float)(now / 400.0) * (yellow ? 1f : -1f);

            for (int i = 0; i <= STEPS; i++) {
                float frac = i / (float)STEPS;
                float angle = phaseOffset + frac * (float)(Math.PI * 4.0) + spiralSpeed;
                float r2 = maxRadius * frac;
                float px = MathHelper.cos(angle) * r2;
                float pz = MathHelper.sin(angle) * r2;
                // Вертикальная волна вдоль спирали
                float py = MathHelper.sin(frac * (float)Math.PI * 3.0f + spiralSpeed * 0.5f) * 0.4f * t;
                // Затухание по краям спирали
                float edgeFade = MathHelper.clamp(1.0f - Math.abs(frac - 0.5f) * 2.0f, 0.1f, 1.0f);
                int fa = (int)(ca * edgeFade);
                buf.vertex(mat, px, py, pz).color(cr, cg, cb, fa);
            }
            BufferRenderer.drawWithGlobalProgram(buf.end());

            // Второй слой — более тонкая внутренняя спираль (ярче белая)
            if (t > 0.1f) {
                BufferBuilder buf2 = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
                for (int i = 0; i <= STEPS; i++) {
                    float frac = i / (float)STEPS;
                    float angle = phaseOffset + frac * (float)(Math.PI * 4.0) + spiralSpeed + 0.15f;
                    float r2 = maxRadius * frac * 0.7f;
                    float px = MathHelper.cos(angle) * r2;
                    float pz = MathHelper.sin(angle) * r2;
                    float py = MathHelper.sin(frac * (float)Math.PI * 3.0f + spiralSpeed * 0.5f + 1.0f) * 0.25f * t;
                    int fa = (int)(ca * 0.5f * MathHelper.clamp(1.0f - Math.abs(frac - 0.5f) * 2.0f, 0.0f, 1.0f));
                    buf2.vertex(mat, px, py, pz).color(255, 255, 200, fa);
                }
                BufferRenderer.drawWithGlobalProgram(buf2.end());
            }
        }

        // Центральная вспышка при максимальном расширении (progress ~0.35)
        if (progress > 0.15f && progress < 0.6f) {
            float flashT = 1.0f - Math.abs((progress - 0.35f) / 0.25f);
            flashT = MathHelper.clamp(flashT, 0f, 1f);
            float flashRadius = 0.35f * flashT;
            int flashA = (int)(alpha * flashT * 180);
            BufferBuilder fb = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
            Matrix4f mat = matrices.peek().getPositionMatrix();
            int FSEG = 24;
            for (int i = 0; i <= FSEG; i++) {
                float angle = i / (float)FSEG * (float)(Math.PI * 2.0);
                float px = MathHelper.cos(angle) * flashRadius;
                float pz = MathHelper.sin(angle) * flashRadius;
                fb.vertex(mat, px, 0, pz).color(255, 255, 180, flashA);
            }
            BufferRenderer.drawWithGlobalProgram(fb.end());
        }

        matrices.pop();

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }
    private void renderGhostGlow(MatrixStack matrices, float r, float g, float b,
                                 float glowAlpha, float solidAlpha, HitGhostData ghost) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.setShaderTexture(0, getGlowTexture());
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);

        float unit = 1.0f / 16.0f;
        float sneakOffset = ghost.sneaking ? 0.25f : 0f;
        float limbSwingAmount = Math.min(1.0f, ghost.limbSwingAmount);
        float legSwing  = MathHelper.cos(ghost.limbSwing * 0.6662f) * 1.4f * limbSwingAmount;
        float armSwing  = MathHelper.cos(ghost.limbSwing * 0.6662f + (float) Math.PI) * 1.4f * limbSwingAmount;

        // Голова
        matrices.push();
        matrices.translate(0, 24 * unit - sneakOffset, 0);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(ghost.netHeadYaw));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(ghost.headPitch));
        renderGlowBox(matrices, -4*unit, -8*unit, -4*unit, 8*unit, 8*unit, 8*unit, r, g, b, glowAlpha);
        matrices.pop();

        // Тело
        matrices.push();
        if (ghost.sneaking) {
            matrices.translate(0, 12*unit, 0);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(25f));
            matrices.translate(0, -12*unit, 0);
        }
        renderGlowBox(matrices, -4*unit, 12*unit - sneakOffset, -2*unit, 8*unit, 12*unit, 4*unit, r, g, b, glowAlpha);
        matrices.pop();

        float armW = 3 * unit;
        // Левая рука
        matrices.push();
        matrices.translate(-4*unit - armW/2, 22*unit - sneakOffset, 0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(armSwing * (180f / (float)Math.PI)));
        renderGlowBox(matrices, -armW/2, -10*unit, -2*unit, armW, 12*unit, 4*unit, r, g, b, glowAlpha);
        matrices.pop();

        // Правая рука
        matrices.push();
        matrices.translate(4*unit + armW/2, 22*unit - sneakOffset, 0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-armSwing * (180f / (float)Math.PI)));
        renderGlowBox(matrices, -armW/2, -10*unit, -2*unit, armW, 12*unit, 4*unit, r, g, b, glowAlpha);
        matrices.pop();

        // Левая нога
        matrices.push();
        matrices.translate(-2*unit, 12*unit, 0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(legSwing * (180f / (float)Math.PI)));
        renderGlowBox(matrices, -2*unit, -12*unit, -2*unit, 4*unit, 12*unit, 4*unit, r, g, b, glowAlpha);
        matrices.pop();

        // Правая нога
        matrices.push();
        matrices.translate(2*unit, 12*unit, 0);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-legSwing * (180f / (float)Math.PI)));
        renderGlowBox(matrices, -2*unit, -12*unit, -2*unit, 4*unit, 12*unit, 4*unit, r, g, b, glowAlpha);
        matrices.pop();

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
    }

    /** Рисует один блок свечения через bloom текстуру */
    private void renderGlowBox(MatrixStack matrices, float x, float y, float z,
                               float width, float height, float depth,
                               float r, float g, float b, float alpha) {
        float cx = x + width / 2f;
        float cy = y + height / 2f;
        float cz = z + depth / 2f;
        float sz = Math.max(width, Math.max(height, depth)) * 1.8f;

        int ri = (int)(r * 255);
        int gi = (int)(g * 255);
        int bi = (int)(b * 255);
        int ai = (int)(MathHelper.clamp(alpha, 0f, 1f) * 255);

        // Передняя / задняя грань
        renderGlowFace(matrices.peek().getPositionMatrix(), cx, cy, cz + depth/2f + 0.01f, sz, ri, gi, bi, ai);
        renderGlowFace(matrices.peek().getPositionMatrix(), cx, cy, cz - depth/2f - 0.01f, sz, ri, gi, bi, ai);

        // Боковые грани (90° по Y)
        matrices.push();
        matrices.translate(cx, cy, cz);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
        renderGlowFace(matrices.peek().getPositionMatrix(), 0, 0, depth/2f + 0.01f, sz, ri, gi, bi, ai);
        renderGlowFace(matrices.peek().getPositionMatrix(), 0, 0, -depth/2f - 0.01f, sz, ri, gi, bi, ai);
        matrices.pop();

        // Верх / низ (90° по X)
        matrices.push();
        matrices.translate(cx, cy, cz);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
        renderGlowFace(matrices.peek().getPositionMatrix(), 0, 0, height/2f + 0.01f, sz, ri, gi, bi, ai);
        renderGlowFace(matrices.peek().getPositionMatrix(), 0, 0, -height/2f - 0.01f, sz, ri, gi, bi, ai);
        matrices.pop();

        // Заливка внутри (чуть темнее)
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        renderSolidBox(matrices.peek().getPositionMatrix(), x, y, z, width, height, depth, ri, gi, bi, (int)(ai * 0.35f));
        RenderSystem.setShaderTexture(0, getGlowTexture());
        RenderSystem.setShader(ShaderProgramKeys.POSITION_TEX_COLOR);
    }

    private void renderGlowFace(Matrix4f mat, float x, float y, float z, float sz, int r, int g, int b, int a) {
        float h = sz / 2f;
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buf.vertex(mat, x-h, y-h, z).texture(0,0).color(r,g,b,a);
        buf.vertex(mat, x-h, y+h, z).texture(0,1).color(r,g,b,a);
        buf.vertex(mat, x+h, y+h, z).texture(1,1).color(r,g,b,a);
        buf.vertex(mat, x+h, y-h, z).texture(1,0).color(r,g,b,a);
        BufferRenderer.drawWithGlobalProgram(buf.end());
    }

    private void renderSolidBox(Matrix4f mat, float x, float y, float z,
                                float w, float h, float d, int r, int g, int b, int a) {
        float x2 = x+w, y2 = y+h, z2 = z+d;
        BufferBuilder buf = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buf.vertex(mat,x,y,z2).color(r,g,b,a); buf.vertex(mat,x2,y,z2).color(r,g,b,a);
        buf.vertex(mat,x2,y2,z2).color(r,g,b,a); buf.vertex(mat,x,y2,z2).color(r,g,b,a);
        buf.vertex(mat,x2,y,z).color(r,g,b,a);  buf.vertex(mat,x,y,z).color(r,g,b,a);
        buf.vertex(mat,x,y2,z).color(r,g,b,a);  buf.vertex(mat,x2,y2,z).color(r,g,b,a);
        buf.vertex(mat,x,y,z).color(r,g,b,a);   buf.vertex(mat,x,y,z2).color(r,g,b,a);
        buf.vertex(mat,x,y2,z2).color(r,g,b,a); buf.vertex(mat,x,y2,z).color(r,g,b,a);
        buf.vertex(mat,x2,y,z2).color(r,g,b,a); buf.vertex(mat,x2,y,z).color(r,g,b,a);
        buf.vertex(mat,x2,y2,z).color(r,g,b,a); buf.vertex(mat,x2,y2,z2).color(r,g,b,a);
        buf.vertex(mat,x,y2,z).color(r,g,b,a);  buf.vertex(mat,x,y2,z2).color(r,g,b,a);
        buf.vertex(mat,x2,y2,z2).color(r,g,b,a);buf.vertex(mat,x2,y2,z).color(r,g,b,a);
        buf.vertex(mat,x,y,z2).color(r,g,b,a);  buf.vertex(mat,x,y,z).color(r,g,b,a);
        buf.vertex(mat,x2,y,z).color(r,g,b,a);  buf.vertex(mat,x2,y,z2).color(r,g,b,a);
        BufferRenderer.drawWithGlobalProgram(buf.end());
    }

    // ── Data classes ──────────────────────────────────────────────────────────

    static class HitMarkerData {
        Vec3d position;
        long birthTime, fadeInTime, displayTime, fadeOutTime;

        HitMarkerData(Vec3d position, long birthTime, long fadeInTime, long displayTime, long fadeOutTime) {
            this.position = position;
            this.birthTime = birthTime;
            this.fadeInTime = fadeInTime;
            this.displayTime = displayTime;
            this.fadeOutTime = fadeOutTime;
        }

        boolean isDead() {
            return System.currentTimeMillis() - birthTime >= fadeInTime + displayTime + fadeOutTime;
        }

        float getAlpha() {
            long elapsed = System.currentTimeMillis() - birthTime;
            if (elapsed < fadeInTime) return easeOutCubic((float) elapsed / fadeInTime);
            if (elapsed < fadeInTime + displayTime) return 1.0f;
            long fo = elapsed - fadeInTime - displayTime;
            return 1.0f - easeInCubic(Math.min(1.0f, (float) fo / fadeOutTime));
        }

        float getScaleMultiplier() {
            long elapsed = System.currentTimeMillis() - birthTime;
            if (elapsed < fadeInTime) return 0.5f + 0.5f * easeOutBack((float) elapsed / fadeInTime);
            if (elapsed < fadeInTime + displayTime) return 1.0f;
            long fo = elapsed - fadeInTime - displayTime;
            return 1.0f - 0.3f * easeInCubic(Math.min(1.0f, (float) fo / fadeOutTime));
        }

        private float easeOutCubic(float x) { return 1.0f - (float) Math.pow(1.0 - x, 3); }
        private float easeInCubic(float x)  { return x * x * x; }
        private float easeOutBack(float x) {
            float c1 = 1.70158f, c3 = c1 + 1.0f;
            return 1.0f + c3 * (float) Math.pow(x - 1.0, 3) + c1 * (float) Math.pow(x - 1.0, 2);
        }
    }

    static class HitGhostData {
        /** Позиция в момент удара */
        final Vec3d position;
        /** Направление движения цели (нормализованное, горизонтальное) */
        final Vec3d moveDir;
        final float bodyYaw, netHeadYaw, headPitch;
        final float limbSwing, limbSwingAmount;
        final boolean sneaking;
        final long birthTime;
        private static final long DURATION_MS = 2000L;

        HitGhostData(Vec3d position, Vec3d moveDir,
                     float bodyYaw, float netHeadYaw, float headPitch,
                     float limbSwing, float limbSwingAmount, boolean sneaking,
                     long birthTime) {
            this.position = position;
            this.moveDir = moveDir;
            this.bodyYaw = bodyYaw;
            this.netHeadYaw = netHeadYaw;
            this.headPitch = headPitch;
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.sneaking = sneaking;
            this.birthTime = birthTime;
        }

        boolean isDead() {
            return System.currentTimeMillis() - birthTime >= DURATION_MS;
        }

        /** 0..1 прогресс жизни */
        float getProgress() {
            return Math.min(1.0f, (float)(System.currentTimeMillis() - birthTime) / DURATION_MS);
        }

        /** Прозрачность: появляется быстро, плавно исчезает */
        float getAlpha() {
            float p = getProgress();
            if (p < 0.08f) return p / 0.08f;         // быстрое появление
            return 1.0f - ((p - 0.08f) / 0.92f);      // плавное исчезновение
        }
    }

    // ── Totem spiral effect ───────────────────────────────────────────────────

    static class TotemSpiralEffect {
        final Vec3d origin;
        final long birthTime;
        private static final long DURATION_MS = 2200L;

        TotemSpiralEffect(Vec3d origin, long birthTime) {
            this.origin    = origin;
            this.birthTime = birthTime;
        }

        boolean isDead() {
            return System.currentTimeMillis() - birthTime >= DURATION_MS;
        }

        float getProgress() {
            return Math.min(1.0f, (float)(System.currentTimeMillis() - birthTime) / DURATION_MS);
        }

        float getAlpha() {
            float p = getProgress();
            if (p < 0.06f) return p / 0.06f;
            if (p < 0.80f) return 1.0f;
            return 1.0f - (p - 0.80f) / 0.20f;   // плавное угасание в конце
        }
    }
}
