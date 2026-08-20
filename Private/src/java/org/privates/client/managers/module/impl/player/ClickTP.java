package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.mojang.blaze3d.matrix.MatrixStack;
import net.mojang.blaze3d.vertex.IVertexBuilder;
import org.lwjgl.glfw.GLFW;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.input.MousePressEvent;
import org.privates.client.managers.events.render.Render3DLastEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.chat.ChatUtil;
import org.privates.client.utils.other.Instance;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "ClickTP", category = Category.MOVEMENT)
public class ClickTP extends Module {

    public static ClickTP getInstance() {
        return Instance.get(ClickTP.class);
    }

    BlockRayTraceResult result;

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        result = null;
    }

    @Override
    public void toggle() {
        super.toggle();
    }

    @EventHandler
    public void onMousePress(MousePressEvent event) {
        if (event.getScreen() != null) return;

        if (event.isKey(GLFW.GLFW_MOUSE_BUTTON_RIGHT) && result != null) {
            Vector3d vec = Vector3d.copyCenteredHorizontally(result.getPos().up());

            double x = vec.x;
            double y = vec.y;
            double z = vec.z;

            if (!mc.player.abilities.isFlying) {
                ChatUtil.addText("Включи флай какашка...");
                return;
            }

            for (int i = 0; i < 5; i++) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));
            }
            for (int i = 0; i < 10; i++) {
                mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, true));
            }

            mc.player.setPosition(x, y, z);
        }
    }

    @EventHandler
    public void onRender(Render3DLastEvent event) {
        result = (BlockRayTraceResult) mc.player.pick(100, 1, false);
        if (result.getType() == RayTraceResult.Type.MISS) result = null;

        if (result != null) {
            BlockPos pos = result.getPos();
            AxisAlignedBB box = new AxisAlignedBB(pos);

            Vector3d camera = mc.getRenderManager().info.getProjectedView();
            box = box.offset(-camera.x, -camera.y, -camera.z);

            MatrixStack matrix = event.getMatrix();
            IRenderTypeBuffer.Impl buffer = mc.getRenderTypeBuffers().getBufferSource();
            IVertexBuilder builder = buffer.getBuffer(RenderType.getLines());

            WorldRenderer.drawBoundingBox(matrix, builder, box, 0.5f, 1.0f, 0.5f, 1.0f);

            buffer.finish(RenderType.getLines());
        }
    }
}