package swerve.ui.newgui;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import swerve.Swerve;
import swerve.utils.render.ShaderUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.ShaderProgram;
import net.minecraft.util.Mth;

import org.joml.Matrix4f;

// Kawase-блюр главного фреймбуфера для «стекла» newgui.
// Схема исходного клиента (BlurRenderer2): снапшот мира делается ОДИН РАЗ за кадр до
// отрисовки GUI — иначе каждая следующая панель попадает в блюр предыдущих и экран
// «размазывает сам себя». Пайплайн: копия экрана -> цепочка down-пассов с уменьшением
// буферов вдвое -> обратная цепочка up-пассов; результат — полноразмерный буфер уровня 0.
public final class GuiBlur {

    private static final int MAX_LEVELS = 5;

    private static final PoseStack IDENTITY = new PoseStack();

    private static RenderTarget[] buffers;
    private static int bufferWidth = -1;
    private static int bufferHeight = -1;
    private static boolean frameReady;
    private static boolean shaderWarned;

    private GuiBlur() {
    }

    // Вызывается в начале рендера кадра, пока в главном фреймбуфере только мир.
    public static void beginFrame(float blurStrength) {
        frameReady = false;
        if (blurStrength <= 0.0f) {
            return;
        }
        frameReady = prepare(blurStrength);
    }

    // Сбрасывает кэш в конце кадра.
    public static void endFrame() {
        frameReady = false;
    }

    // Рисует размытый фон области экрана из снапшота кадра. Координаты — в GUI-пикселях.
    public static void draw(PoseStack stack, float x, float y, float width, float height,
                            float radius, float blurStrength) {
        if (!frameReady || buffers == null || buffers[0] == null) {
            return;
        }

        Minecraft client = Minecraft.getInstance();
        float guiWidth = client.getWindow().getGuiScaledWidth();
        float guiHeight = client.getWindow().getGuiScaledHeight();

        CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.ROUNDED_TEXTURE);
        if (shader == null) {
            if (!shaderWarned) {
                shaderWarned = true;
                Swerve.LOGGER.error("Shader 'rounded texture' is not compiled, gui blur is skipped");
            }
            return;
        }

        ShaderUtils.set(shader, "Size", width, height);
        ShaderUtils.set(shader, "Radius", radius, radius, radius, radius);
        ShaderUtils.set(shader, "Smoothness", 1.0f);
        ShaderUtils.set(shader, "ColorModulator", 1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.setShaderTexture(0, buffers[0].getColorTextureId());

        float u0 = x / guiWidth;
        float v0 = 1.0f - y / guiHeight;
        float u1 = (x + width) / guiWidth;
        float v1 = 1.0f - (y + height) / guiHeight;

        Matrix4f matrix = stack.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(matrix, x, y, 0.0f).setUv(u0, v0).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x, y + height, 0.0f).setUv(u0, v1).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x + width, y + height, 0.0f).setUv(u1, v1).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, x + width, y, 0.0f).setUv(u1, v0).setColor(1.0f, 1.0f, 1.0f, 1.0f);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            RenderSystem.setShader(ShaderUtils.ROUNDED_TEXTURE);
            BufferUploader.drawWithShader(mesh);
        }

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static boolean prepare(float blurStrength) {
        Minecraft client = Minecraft.getInstance();
        int guiWidth = client.getWindow().getGuiScaledWidth();
        int guiHeight = client.getWindow().getGuiScaledHeight();
        if (guiWidth <= 0 || guiHeight <= 0) {
            return false;
        }

        int levels = Mth.clamp((int) Math.ceil(blurStrength / 3.0f), 1, MAX_LEVELS);

        ensureBuffers(guiWidth, guiHeight);

        RenderTarget main = client.getMainRenderTarget();

        // Снапшот мира в буфер уровня 0. Квад всех пассов — фуллскрин в GUI-координатах:
        // проекция остаётся GUI-ортой и накрывает таргет любого размера целиком
        // (квад размером с таргет покрывал только долю буфера — остальное было мусором).
        fullscreenPass(buffers[0], main.getColorTextureId(), guiWidth, guiHeight, guiWidth, guiHeight, null, 0.0f);

        // Down-пассы: каждый следующий буфер вдвое меньше предыдущего. Resolution — тексел источника.
        // Размеры уровней вычисляются (gui >> i) — без обращений к полям RenderTarget.
        for (int i = 1; i <= levels; i++) {
            int sourceWidth = Math.max(1, guiWidth >> (i - 1));
            int sourceHeight = Math.max(1, guiHeight >> (i - 1));
            fullscreenPass(buffers[i], buffers[i - 1].getColorTextureId(), guiWidth, guiHeight,
                    sourceWidth, sourceHeight, ShaderUtils.KAWASE_DOWN, 1.0f);
        }

        // Up-пассы в обратную сторону, до полноразмерного буфера уровня 0.
        for (int i = levels; i >= 1; i--) {
            int sourceWidth = Math.max(1, guiWidth >> i);
            int sourceHeight = Math.max(1, guiHeight >> i);
            fullscreenPass(buffers[i - 1], buffers[i].getColorTextureId(), guiWidth, guiHeight,
                    sourceWidth, sourceHeight, ShaderUtils.KAWASE_UP, 2.0f);
        }

        return true;
    }

    private static void ensureBuffers(int width, int height) {
        if (bufferWidth == width && bufferHeight == height && buffers != null && buffers[0] != null) {
            return;
        }

        if (buffers == null) {
            buffers = new RenderTarget[MAX_LEVELS + 1];
        }

        for (int i = 0; i <= MAX_LEVELS; i++) {
            int levelWidth = Math.max(1, width >> i);
            int levelHeight = Math.max(1, height >> i);
            if (buffers[i] == null) {
                buffers[i] = new TextureTarget(levelWidth, levelHeight, false);
            } else {
                buffers[i].resize(levelWidth, levelHeight);
            }
            buffers[i].setFilterMode(GlConst.GL_LINEAR);
        }

        bufferWidth = width;
        bufferHeight = height;
    }

    // Один fullscreen-проход: target <- sourceTextureId (sourceWidth x sourceHeight).
    // Квад — 0..quadWidth/quadHeight (размер гуи-экрана) под текущей GUI-проекцией,
    // UV по вертикали перевёрнуты: в таргете v=1 соответствует верху экрана.
    // program == null — простое копирование.
    private static void fullscreenPass(RenderTarget target, int sourceTextureId,
                                       int quadWidth, int quadHeight,
                                       int sourceWidth, int sourceHeight,
                                       ShaderProgram program, float offset) {
        CompiledShaderProgram shader = null;
        if (program != null) {
            shader = ShaderUtils.get(program);
            if (shader == null) {
                return;
            }
            // Uniform называется Resolution, но в шейдере используется как размер текселя (1/size):
            // halfpixel = Resolution * 0.5 * Offset.
            ShaderUtils.set(shader, "Resolution", 1.0f / sourceWidth, 1.0f / sourceHeight);
            ShaderUtils.set(shader, "Offset", offset);
            ShaderUtils.set(shader, "Saturation", 1.0f);
            ShaderUtils.set(shader, "TintIntensity", 0.0f);
        }

        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();

        target.bindWrite(true);
        RenderSystem.setShaderTexture(0, sourceTextureId);

        if (shader != null) {
            RenderSystem.setShader(program);
        } else {
            RenderSystem.setShader(CoreShaders.POSITION_TEX_COLOR);
        }

        Matrix4f matrix = IDENTITY.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(matrix, 0.0f, 0.0f, 0.0f).setUv(0.0f, 1.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, 0.0f, quadHeight, 0.0f).setUv(0.0f, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, quadWidth, quadHeight, 0.0f).setUv(1.0f, 0.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);
        buffer.addVertex(matrix, quadWidth, 0.0f, 0.0f).setUv(1.0f, 1.0f).setColor(1.0f, 1.0f, 1.0f, 1.0f);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }

        main.bindWrite(true);
        RenderSystem.setShaderTexture(0, 0);
    }
}
