package never.lose.modules.impl.Render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import never.lose.NeverLose;
import never.lose.managers.FriendManager;
import never.lose.modules.api.Module;
import never.lose.modules.api.ModuleCategory;
import never.lose.modules.api.ModuleInfo;
import never.lose.modules.api.setting.BooleanSetting;
import never.lose.utils.time.TimerUtil;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

@ModuleInfo(name = "3DBox", desc = "Отображает контуры игроков", category = ModuleCategory.RENDER)
public class Box3D extends Module {

    public final BooleanSetting pulse = new BooleanSetting("Пульсация", false);

    private static final int FRIEND_COLOR = 0xFF00FF00;
    private static final int HURT_COLOR = 0xFFFF0000;

    private static final float ALPHA_MIN = 30f;
    private static final float ALPHA_MAX = 60f;
    private static final float ALPHA_IDLE = 40f;
    private static final float ALPHA_STEP = 2f;
    private static final long PULSE_DELAY = 25L;

    private final TimerUtil pulseTimer = new TimerUtil();
    private float alpha = 50f;
    private boolean decrease = true;

    @Override
    public void onRender3D(MatrixStack matrices, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null || mc.gameRenderer == null) {
            return;
        }

        updateAlpha();

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cam = camera.getPos();
        Matrix4f matrix = matrices.peek().getPositionMatrix();
        boolean firstPerson = mc.options.getPerspective() == Perspective.FIRST_PERSON;

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (shouldSkip(mc.player, player, firstPerson)) {
                continue;
            }
            renderBox(player, tickDelta, cam, matrix);
        }
    }

    private boolean shouldSkip(ClientPlayerEntity self, PlayerEntity player, boolean firstPerson) {
        return !player.isAlive() || (player == self && firstPerson);
    }

    private void renderBox(PlayerEntity player, float tickDelta, Vec3d cam, Matrix4f matrix) {
        double x = MathHelper.lerp(tickDelta, player.prevX, player.getX()) - cam.x;
        double y = MathHelper.lerp(tickDelta, player.prevY, player.getY()) - cam.y;
        double z = MathHelper.lerp(tickDelta, player.prevZ, player.getZ()) - cam.z;

        Box bb = player.getBoundingBox();
        double offX = -player.getX() + x;
        double offY = -player.getY() + y;
        double offZ = -player.getZ() + z;

        float minX = (float) (bb.minX + offX);
        float minY = (float) (bb.minY + offY);
        float minZ = (float) (bb.minZ + offZ);
        float maxX = (float) (bb.maxX + offX);
        float maxY = (float) (bb.maxY + offY);
        float maxZ = (float) (bb.maxZ + offZ);

        float hurtPercent = MathHelper.sin(player.hurtTime * (float) (Math.PI / 10.0));
        boolean friend = FriendManager.isFriend(player.getName().getString());
        int baseColor = friend ? FRIEND_COLOR
                : NeverLose.getInstance().getThemeManager().getCurrentTheme().colorVisualModules;
        int color = overCol(baseColor, HURT_COLOR, hurtPercent);

        int fillColor = setAlpha(color, (int) alpha);
        int outlineColor = setAlpha(color, 255);

        drawFill(minX, minY, minZ, maxX, maxY, maxZ, matrix, fillColor);
        drawOutline(minX, minY, minZ, maxX, maxY, maxZ, matrix, outlineColor);
    }

    private void updateAlpha() {
        if (pulse.getValue()) {
            if (pulseTimer.hasTimeElapsed(PULSE_DELAY, true)) {
                if (decrease) {
                    alpha -= ALPHA_STEP;
                    if (alpha <= ALPHA_MIN) {
                        alpha = ALPHA_MIN;
                        decrease = false;
                    }
                } else {
                    alpha += ALPHA_STEP;
                    if (alpha >= 50f) {
                        alpha = ALPHA_MAX;
                        decrease = true;
                    }
                }
            }
        } else {
            alpha = ALPHA_IDLE;
        }
    }

    private void drawFill(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                          Matrix4f matrix, int color) {
        setupRender();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        float a = (color >> 24 & 255) / 255f;
        float r = (color >> 16 & 255) / 255f;
        float g = (color >> 8 & 255) / 255f;
        float b = (color & 255) / 255f;
        addBoxVertices(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        cleanupRender();
    }

    private void drawOutline(float minX, float minY, float minZ, float maxX, float maxY, float maxZ,
                             Matrix4f matrix, int color) {
        setupRender();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE);
        RenderSystem.lineWidth(1f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        BufferBuilder buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR);
        float a = (color >> 24 & 255) / 255f;
        float r = (color >> 16 & 255) / 255f;
        float g = (color >> 8 & 255) / 255f;
        float b = (color & 255) / 255f;
        addBoxOutlineVertices(buffer, matrix, minX, minY, minZ, maxX, maxY, maxZ, r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        cleanupRender();
    }

    private void addBoxVertices(BufferBuilder buffer, Matrix4f matrix, float minX, float minY, float minZ,
                                float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
    }

    private void addBoxOutlineVertices(BufferBuilder buffer, Matrix4f matrix, float minX, float minY, float minZ,
                                       float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, minZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, minZ).color(r, g, b, a);

        buffer.vertex(matrix, maxX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, maxX, maxY, maxZ).color(r, g, b, a);

        buffer.vertex(matrix, minX, minY, maxZ).color(r, g, b, a);
        buffer.vertex(matrix, minX, maxY, maxZ).color(r, g, b, a);
    }

    private void setupRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
    }

    private void cleanupRender() {
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private int overCol(int color1, int color2, float percent) {
        percent = MathHelper.clamp(percent, 0f, 1f);
        return pack(
                (int) MathHelper.lerp(percent, red(color1), red(color2)),
                (int) MathHelper.lerp(percent, green(color1), green(color2)),
                (int) MathHelper.lerp(percent, blue(color1), blue(color2)),
                alpha(color1)
        );
    }

    private static int pack(int r, int g, int b, int a) {
        return (MathHelper.clamp(a, 0, 255) << 24)
                | (MathHelper.clamp(r, 0, 255) << 16)
                | (MathHelper.clamp(g, 0, 255) << 8)
                | MathHelper.clamp(b, 0, 255);
    }

    private static int setAlpha(int color, int alpha) {
        return (MathHelper.clamp(alpha, 0, 255) << 24) | (color & 0xFFFFFF);
    }

    private static int red(int color) {
        return (color >> 16) & 255;
    }

    private static int green(int color) {
        return (color >> 8) & 255;
    }

    private static int blue(int color) {
        return color & 255;
    }

    private static int alpha(int color) {
        return (color >> 24) & 255;
    }
}