package org.privates.client.managers.module.impl.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
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
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;
import org.privates.client.utils.render.color.ColorUtil;
import org.privates.client.utils.render.draw.Project;
import org.privates.client.utils.render.draw.RectUtil;
import org.privates.client.utils.render.draw.RenderUtil3D;
import org.privates.client.utils.render.font.Fonts;

import java.util.HashMap;
import java.util.Map;

@Category1
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ItemESP", category = Category.RENDER, description = "Подсвечивает выброшенные предметы")
public class ItemESP extends Module {

    public static ItemESP getInstance() {
        return Instance.get(ItemESP.class);
    }

    final BooleanSetting nametag = new BooleanSetting(this, "Nametag", true);
    final BooleanSetting box = new BooleanSetting(this, "Box", true);
    final BooleanSetting landingLine = new BooleanSetting(this, "Линия приземления", true);
    final BooleanSetting landingBlock = new BooleanSetting(this, "Подсветка блока приземления", true);
    final BooleanSetting landingIcon = new BooleanSetting(this, "Иконка на блоке приземления", true);
    final SliderSetting range = new SliderSetting(this, "Range", 50f, 10f, 100f, 5f);

    private final Map<Integer, LandingData> landings = new HashMap<>();

    @EventHandler
    public void onRender3D(Render3DPosedEvent event) {
        if (mc.world == null || mc.player == null) return;

        double maxDistSq = range.getValue() * range.getValue();
        landings.clear();

        if (landingLine.getValue()) {
            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(event.getMatrix().getLast().getMatrix());
            RenderSystem.translated(-mc.getRenderManager().renderPosX(), -mc.getRenderManager().renderPosY(), -mc.getRenderManager().renderPosZ());
            RenderSystem.disableTexture();
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
            RenderSystem.lineWidth(1.4F);

            BufferBuilder buffer = Tessellator.getInstance().getBuffer();
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            for (Entity entity : mc.world.getAllEntities()) {
                if (!(entity instanceof ItemEntity item) || !item.isAlive()) continue;
                if (mc.player.getDistanceSq(item) > maxDistSq) continue;
                LandingData data = predictLanding(item, true, buffer);
                if (data != null) {
                    landings.put(item.getEntityId(), data);
                }
            }
            Tessellator.getInstance().draw();

            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
            RenderSystem.popMatrix();
        }

        for (Entity entity : mc.world.getAllEntities()) {
            if (!(entity instanceof ItemEntity item) || !item.isAlive()) continue;
            if (mc.player.getDistanceSq(item) > maxDistSq) continue;

            if (box.getValue()) {
                AxisAlignedBB bb = item.getBoundingBox();
                int fillColor = ColorUtil.getColor(255, 220, 0, 70);
                int lineColor = ColorUtil.getColor(255, 220, 0, 255);
                RenderUtil3D.drawFilledBoundingBox(event.getMatrix(), bb, fillColor, false, false);
                RenderUtil3D.drawBoundingBox(event.getMatrix(), bb, lineColor, 1.2f, false, false);
            }

            if ((landingBlock.getValue() || landingIcon.getValue()) && !landings.containsKey(item.getEntityId())) {
                LandingData data = predictLanding(item, false, null);
                if (data != null) {
                    landings.put(item.getEntityId(), data);
                }
            }

            if (landingBlock.getValue()) {
                LandingData data = landings.get(item.getEntityId());
                if (data != null && data.blockPos != null) {
                    int client = Theme.getInstance().clientColor();
                    int fill = ColorUtil.replAlpha(client, 60);
                    int line = ColorUtil.replAlpha(client, 220);
                    AxisAlignedBB bb = new AxisAlignedBB(data.blockPos);
                    RenderUtil3D.drawFilledBoundingBox(event.getMatrix(), bb, fill, false, false);
                    RenderUtil3D.drawBoundingBox(event.getMatrix(), bb, line, 1.7f, false, false);
                }
            }
        }
    }

    @EventHandler
    public void onRender2D(Render2DEvent event) {
        if (mc.world == null || mc.player == null) return;

        double maxDistSq = range.getValue() * range.getValue();
        MatrixStack matrix = event.getMatrix();
        Theme theme = Theme.getInstance();

        for (Entity entity : mc.world.getAllEntities()) {
            if (!(entity instanceof ItemEntity item) || !item.isAlive()) continue;
            if (mc.player.getDistanceSq(item) > maxDistSq) continue;

            ItemStack stack = item.getItem();

            if (nametag.getValue()) {
                Vector3d pos = RenderUtil3D.interpolate(item, mc.getRenderPartialTicks());
                Vector2f screen = Project.project2D(pos.x, pos.y + item.getHeight() + 0.2, pos.z);
                if (!(screen.x == Float.MAX_VALUE && screen.y == Float.MAX_VALUE)) {
                    String text = stack.getDisplayName().getString() + " x" + stack.getCount();
                    float size = 6.5f;
                    float width = Fonts.SF_MEDIUM.getWidth(text, size);
                    float x = screen.x - width / 2f;
                    float y = screen.y;

                    theme.drawClientRect(matrix, x - 4f, y - 2f, width + 8f, 10f, 0.9f, 3f);
                    RectUtil.drawRect(matrix, x - 3f, y + 8f, width + 6f, 0.6f, ColorUtil.replAlpha(theme.clientColor(), 130));
                    Fonts.SF_MEDIUM.draw(matrix, text, x, y + 0.6f, theme.textColor(), size);
                }
            }

            if (landingIcon.getValue()) {
                LandingData data = landings.get(item.getEntityId());
                if (data == null || data.hitPos == null) continue;

                Vector2f hitScreen = Project.project2D(data.hitPos.x, data.hitPos.y + 0.06, data.hitPos.z);
                if (hitScreen.x == Float.MAX_VALUE && hitScreen.y == Float.MAX_VALUE) continue;

                String countText = String.valueOf(data.stack.getCount());

                float bgW = 16f;
                float bgH = 11f;
                float x = hitScreen.x - bgW / 2f;
                float y = hitScreen.y - 5.5f;

                theme.drawClientRect(matrix, x, y, bgW, bgH, 0.95f, 3.2f);

                // Иконка предмета (текстура ItemStack), а не текстовый символ
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                float iconScale = 0.5f;
                float iconX = x + 0.8f;
                float iconY = y + 1.5f;
                RenderSystem.translatef(iconX, iconY, 0);
                RenderSystem.scalef(iconScale, iconScale, 1.0f);
                mc.getItemRenderer().renderItemAndEffectIntoGUI(data.stack, 0, 0);
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();

                Fonts.SF_MEDIUM.drawRight(matrix, countText, x + bgW - 1.4f, y + 2.1f, theme.textColor(), 5.9f);
            }
        }
    }

    private LandingData predictLanding(ItemEntity item, boolean renderPath, BufferBuilder buffer) {
        Vector3d pos = item.getPositionVec();
        Vector3d motion = item.getMotion();
        int color = ColorUtil.replAlpha(Theme.getInstance().clientColor(), 180);

        for (int i = 0; i < 140; i++) {
            Vector3d prev = pos;
            pos = pos.add(motion);

            RayTraceContext ctx = new RayTraceContext(prev, pos, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, item);
            BlockRayTraceResult hit = mc.world.rayTraceBlocks(ctx);

            if (renderPath && buffer != null) {
                buffer.pos(prev.x, prev.y, prev.z).color(color).endVertex();
                buffer.pos(pos.x, pos.y, pos.z).color(color).endVertex();
            }

            if (hit.getType() == RayTraceResult.Type.BLOCK) {
                return new LandingData(hit.getHitVec(), hit.getPos(), item.getItem().copy());
            }

            if (pos.y < -64.0) break;

            BlockState state = mc.world.getBlockState(new BlockPos(pos));
            float drag = state.getMaterial().isLiquid() ? 0.8f : 0.98f;
            motion = motion.scale(drag);
            if (!item.hasNoGravity()) {
                motion = motion.add(0.0, -0.04, 0.0);
            }
        }

        return null;
    }

    private static class LandingData {
        private final Vector3d hitPos;
        private final BlockPos blockPos;
        private final ItemStack stack;

        private LandingData(Vector3d hitPos, BlockPos blockPos, ItemStack stack) {
            this.hitPos = hitPos;
            this.blockPos = blockPos;
            this.stack = stack;
        }
    }
}
