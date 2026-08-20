package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.SwordItem;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL11;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.math.Interpolator;
import org.privates.client.utils.render.color.ColorUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AfterSlash", category = Category.RENDER, description = "После удара оставляет аниме-дугу меча")
public class AfterSlash extends Module {

    final SliderSetting lifeMs = new SliderSetting(this, "Время жизни", 340f, 120f, 900f, 10f);
    final SliderSetting radius = new SliderSetting(this, "Радиус", 0.92f, 0.4f, 1.8f, 0.05f);
    final SliderSetting width = new SliderSetting(this, "Ширина", 0.27f, 0.08f, 0.6f, 0.01f);
    final SliderSetting segments = new SliderSetting(this, "Детализация", 42f, 14f, 96f, 1f);
    final BooleanSetting onlySword = new BooleanSetting(this, "Только меч", true);

    final List<Slash> slashes = new ArrayList<>();

    @EventHandler
    public void onAttack(AttackEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (onlySword.getValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof SwordItem)) {
            return;
        }

        Entity target = event.getTarget();
        if (target == null) return;

        float yaw = mc.player.rotationYaw;
        float pitch = mc.player.rotationPitch;
        boolean left = ThreadLocalRandom.current().nextBoolean();
        slashes.add(new Slash(System.currentTimeMillis(), yaw, pitch, left));
    }

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.player == null || mc.world == null || slashes.isEmpty()) return;

        long now = System.currentTimeMillis();
        Iterator<Slash> it = slashes.iterator();
        while (it.hasNext()) {
            Slash slash = it.next();
            float age = (now - slash.startMs) / lifeMs.getValue();
            if (age >= 1.0f) it.remove();
        }
        if (slashes.isEmpty()) return;

        MatrixStack ms = event.getMatrix();
        Vector3d cam = mc.getRenderManager().info.getProjectedView();
        Vector3d player = new Vector3d(
                Interpolator.lerp(mc.player.lastTickPosX, mc.player.getPosX(), event.getPartialTicks()),
                Interpolator.lerp(mc.player.lastTickPosY, mc.player.getPosY(), event.getPartialTicks()) + mc.player.getEyeHeight() * 0.72f,
                Interpolator.lerp(mc.player.lastTickPosZ, mc.player.getPosZ(), event.getPartialTicks())
        );

        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        for (Slash slash : slashes) {
            float age = (now - slash.startMs) / lifeMs.getValue();
            renderSlash(ms, cam, player, slash, MathHelper.clamp(age, 0f, 1f));
        }

        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    private void renderSlash(MatrixStack ms, Vector3d cam, Vector3d playerPos, Slash slash, float age) {
        float fade = 1.0f - age;
        float eased = (float) (1.0 - Math.pow(age, 2.3));
        float yawRad = (float) Math.toRadians(slash.yaw);
        float side = slash.left ? -1f : 1f;
        float arcSpan = 140f;
        float radiusNow = radius.getValue() * (0.86f + 0.22f * eased);
        float widthNow = width.getValue() * (0.8f + 0.2f * eased);

        Vector3d forward = new Vector3d(-Math.sin(yawRad), 0, Math.cos(yawRad));
        Vector3d right = new Vector3d(forward.z, 0, -forward.x).scale(side);
        Vector3d center = playerPos.add(forward.scale(0.72)).add(0, -0.12 + Math.sin(age * Math.PI) * 0.08, 0);
        Vector3d relCenter = center.subtract(cam);

        int theme = Theme.getInstance().clientColor();
        int core = ColorUtil.replAlpha(theme, (int) (220 * fade));
        int outer = ColorUtil.replAlpha(theme, (int) (30 * fade));

        int count = Math.max(8, segments.getValue().intValue());
        BufferBuilder b = Tessellator.getInstance().getBuffer();
        Matrix4f mat = ms.getLast().getMatrix();
        b.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_COLOR);

        for (int i = 0; i <= count; i++) {
            float t = i / (float) count;
            float a = (-arcSpan * 0.5f + arcSpan * t);
            float aRad = (float) Math.toRadians(a);

            double c = Math.cos(aRad);
            double s = Math.sin(aRad);
            Vector3d dir = right.scale(c).add(forward.scale(s)).normalize();

            float pulse = 1.0f + 0.08f * (float) Math.sin((t * 12.0f) + age * 18.0f);
            Vector3d pOuter = relCenter.add(dir.scale(radiusNow * pulse + widthNow));
            Vector3d pInner = relCenter.add(dir.scale(Math.max(0.02, radiusNow * pulse - widthNow)));

            int mixA = (int) (fade * 255 * (0.3f + 0.7f * (1f - Math.abs(t - 0.5f) * 1.6f)));
            int cCore = ColorUtil.replAlpha(core, Math.max(0, Math.min(255, mixA)));
            int cOuter = ColorUtil.replAlpha(outer, Math.max(0, Math.min(255, mixA / 2)));

            b.pos(mat, (float) pOuter.x, (float) pOuter.y, (float) pOuter.z).color(cOuter).endVertex();
            b.pos(mat, (float) pInner.x, (float) pInner.y, (float) pInner.z).color(cCore).endVertex();
        }

        Tessellator.getInstance().draw();
    }

    private static class Slash {
        final long startMs;
        final float yaw;
        final float pitch;
        final boolean left;

        private Slash(long startMs, float yaw, float pitch, boolean left) {
            this.startMs = startMs;
            this.yaw = yaw;
            this.pitch = pitch;
            this.left = left;
        }
    }
}
