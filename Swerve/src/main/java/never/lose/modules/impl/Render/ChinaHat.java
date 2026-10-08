package never.lose.modules.impl.Render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import never.lose.NeverLose;
import never.lose.managers.FriendManager;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BooleanSetting;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "ChinaHat", desc = "Отображает китайскую шляпу над головой", category = ModuleCategory.RENDER)
public class ChinaHat extends Module {

    public final BooleanSetting viewOnFriends = new BooleanSetting("Отображать на друзьях", true);

    private static final float PI_STEP = MathHelper.TAU / 90.0F;

    @Override
    public void onRender3D(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null || mc.gameRenderer == null) {
            return;
        }

        Camera camera = mc.gameRenderer.getCamera();
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (!shouldRender(mc.player, player)) {
                continue;
            }
            renderHat(matrices, camera, player, tickDelta);
        }
    }

    private boolean shouldRender(ClientPlayerEntity self, PlayerEntity player) {
        if (player == self) {
            return true;
        }
        return viewOnFriends.getValue() && FriendManager.isFriend(player.getName().getString());
    }

    private void renderHat(MatrixStack matrices, Camera camera, PlayerEntity player, float tickDelta) {
        double x = MathHelper.lerp(tickDelta, player.prevX, player.getX()) - camera.getPos().x;
        double y = MathHelper.lerp(tickDelta, player.prevY, player.getY()) - camera.getPos().y + player.getHeight() + 0.08;
        double z = MathHelper.lerp(tickDelta, player.prevZ, player.getZ()) - camera.getPos().z;
        float radius = (float) (player.getBoundingBox().maxX - player.getBoundingBox().minX) * 0.55F;
        float offset = player.getInventory().getArmorStack(3).isEmpty() ? 0.12F : 0.2F;

        matrices.push();
        matrices.translate(x, y + offset, z);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-player.getYaw(tickDelta)));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        Matrix4f matrix = matrices.peek().getPositionMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        int themeColor = NeverLose.getInstance().getThemeManager().getCurrentTheme().colorClient;
        for (int i = 0; i <= 180; i++) {
            float iPi = i * PI_STEP;
            float px = MathHelper.sin(iPi) * radius;
            float pz = MathHelper.cos(iPi) * radius;
            int outline = fade(themeColor, i * 16);
            int cone = withAlpha(outline, 90);
            buffer.vertex(matrix, px, 0.0F, pz).color(cone);
            buffer.vertex(matrix, 0.0F, 0.28F, 0.0F).color(themeColor);
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);
        for (int i = 0; i <= 180; i++) {
            float iPi = i * PI_STEP;
            float px = MathHelper.sin(iPi) * radius;
            float pz = MathHelper.cos(iPi) * radius;
            buffer.vertex(matrix, px, 0.0F, pz).color(fade(themeColor, i * 16));
        }
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        matrices.pop();
    }

    private static int fade(int color, int index) {
        float factor = (MathHelper.sin((System.currentTimeMillis() / 4.0F + index) * 0.01F) + 1.0F) * 0.25F + 0.5F;
        int a = (color >> 24) & 0xFF;
        int r = (int) (((color >> 16) & 0xFF) * factor);
        int g = (int) (((color >> 8) & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static int withAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }
}
