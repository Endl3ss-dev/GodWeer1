package org.privates.client.managers.module.impl.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Namespaced;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.platform.GlStateManager;
import net.mojang.blaze3d.systems.RenderSystem;
import org.privates.client.Excellent;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.render.Render3DPosedEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.impl.client.Theme;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.MultiBooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;

import java.util.HashMap;
import java.util.Map;

@Category2
@ModuleInfo(name = "Nimb", description = "Нимб над головой игроков", category = Category.RENDER)
public class Nimb extends Module {

    private final MultiBooleanSetting entity = new MultiBooleanSetting(this, "Показывать на",
            new BooleanSetting("Себя", true),
            new BooleanSetting("Игроки", false),
            new BooleanSetting("Друзья", true)
    );
    private final BooleanSetting friendColor = new BooleanSetting(this, "Зелёный цвет друзьям", true);
    private final SliderSetting radius = new SliderSetting(this, "Радиус", 0.45F, 0.2F, 1.0F, 0.05F);
    private final SliderSetting spriteSize = new SliderSetting(this, "Размер", 0.18F, 0.05F, 0.5F, 0.01F);
    private final SliderSetting speed = new SliderSetting(this, "Скорость", 1.0F, 0.2F, 3.0F, 0.1F);
    private final SliderSetting trailLen = new SliderSetting(this, "Длина", 25.0F, 2.0F, 50.0F, 1.0F);
    private final SliderSetting alphaFade = new SliderSetting(this, "Прозрачность", 12.0F, 0.0F, 20.0F, 1.0F);
    private final BooleanSetting firstPerson = new BooleanSetting(this, "От первого лица", false);

    private static final Namespaced GLOW_TEXTURE = new Namespaced("texture/glow.png");
    private static final int FRIEND_GREEN = 0xFF00C864;
    private static final int GHOST_COUNT = 2;

    private final Map<Integer, float[]> playerAngles = new HashMap<>();
    private long lastUpdateMs = 0L;

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.player == null || mc.world == null) return;
        if (mc.gameSettings.getPointOfView().firstPerson() && !firstPerson.getValue()) return;

        float pt = event.getPartialTicks();
        Vector3d cam = mc.getRenderManager().info.getProjectedView();
        float spd = speed.getValue();
        float r = radius.getValue();
        float sizeW = spriteSize.getValue();
        float fade = alphaFade.getValue();
        int trail = trailLen.getValue().intValue();

        long now = System.currentTimeMillis();
        float dt = (lastUpdateMs != 0L) ? ((now - lastUpdateMs) / 1000.0F) : 0.0F;
        lastUpdateMs = now;

        MatrixStack ms = event.getMatrix();

        RenderSystem.enableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.blendFuncSeparate(770, 1, 0, 1);
        RenderSystem.shadeModel(7425);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.alphaFunc(516, 0.01F);

        mc.getTextureManager().bindTexture(GLOW_TEXTURE);
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

        for (PlayerEntity player : mc.world.getPlayers()) {
            boolean isSelf = player == mc.player;
            boolean isFriend = Excellent.inst().friendManager().isFriend(player.getGameProfile().getName());
            if (!shouldRenderFor(isSelf, isFriend) || !player.isAlive()) continue;

            int color = (isFriend && friendColor.getValue()) ? FRIEND_GREEN : Theme.getInstance().clientColor();

            int id = player.getEntityId();
            float[] ang = playerAngles.computeIfAbsent(id, k -> new float[]{0.0F});
            ang[0] = ang[0] - dt * spd * (float) Math.PI;
            float orbitAngle = ang[0];

            double ix = player.lastTickPosX + (player.getPosX() - player.lastTickPosX) * pt;
            double iy = player.lastTickPosY + (player.getPosY() - player.lastTickPosY) * pt;
            double iz = player.lastTickPosZ + (player.getPosZ() - player.lastTickPosZ) * pt;

            double baseX = ix - cam.x;
            double baseY = iy + player.getHeight() + 0.15D - cam.y;
            double baseZ = iz - cam.z;

            for (int g = 0; g < GHOST_COUNT; g++) {
                float ghostOffset = g / (float) GHOST_COUNT * ((float) Math.PI * 2.0F);

                for (int t = 0; t < trail; t++) {
                    float trailOffset = t / (float) trail * 2.5F;
                    float angle = orbitAngle + ghostOffset + trailOffset;

                    float px = (float) (baseX + Math.sin(angle) * r);
                    float pz = (float) (baseZ + Math.cos(angle) * r);
                    float py = (float) (baseY + (t == 0 ? Math.sin((orbitAngle * 2.0F + ghostOffset)) * 0.05D : 0.0D));

                    float headAlpha = Math.max(0.0F, 1.0F - t / (float) trail * fade / 15.0F);
                    int finalAlpha = (int) (255.0F * headAlpha);

                    int cr = (color >> 16) & 0xFF;
                    int cg = (color >> 8) & 0xFF;
                    int cb = color & 0xFF;

                    ms.push();
                    ms.translate(px, py, pz);
                    float scale = sizeW * (t == 0 ? 1.0F : (1.0F - t / (float) trail * 0.5F));
                    ms.scale(scale, scale, scale);
                    ms.rotate(mc.getRenderManager().info.getRotation().copy());

                    Matrix4f mat = ms.getLast().getMatrix();
                    buffer.pos(mat, -1.0F, 1.0F, 0.0F).tex(0.0F, 1.0F).color(cr, cg, cb, finalAlpha).endVertex();
                    buffer.pos(mat, 1.0F, 1.0F, 0.0F).tex(1.0F, 1.0F).color(cr, cg, cb, finalAlpha).endVertex();
                    buffer.pos(mat, 1.0F, -1.0F, 0.0F).tex(1.0F, 0.0F).color(cr, cg, cb, finalAlpha).endVertex();
                    buffer.pos(mat, -1.0F, -1.0F, 0.0F).tex(0.0F, 0.0F).color(cr, cg, cb, finalAlpha).endVertex();
                    ms.pop();
                }
            }
        }

        Tessellator.getInstance().draw();
        playerAngles.keySet().removeIf(id -> mc.world.getPlayers().stream().noneMatch(p -> p.getEntityId() == id));

        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.shadeModel(7424);
        RenderSystem.disableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();
    }

    private boolean shouldRenderFor(boolean isSelf, boolean isFriend) {
        if (isSelf && entity.getValue("Себя")) return true;
        if (isFriend && entity.getValue("Друзья")) return true;
        return !isSelf && !isFriend && entity.getValue("Игроки");
    }
}
