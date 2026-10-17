package swerve.modules.impl.render;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.CompiledShaderProgram;
import net.minecraft.client.renderer.CoreShaders;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import swerve.Swerve;
import swerve.managers.ThemeManager;
import swerve.modules.Module;
import swerve.modules.ModuleCategory;
import swerve.modules.api.setting.BooleanSetting;
import swerve.modules.api.setting.FloatSetting;
import swerve.modules.api.setting.ModeSetting;
import swerve.utils.render.ShaderUtils;

/**
 * Шейдер рук и оружия от первого лица (порты эффектов Arcanic).
 *
 * <p>Руки рисуются в отдельную текстуру, поверх сцены накладывается шейдер
 * с выбранным эффектом. Цвет берётся из палитры меню.
 */
public final class HandShader extends Module {

    private static final String[] MODES = {
        "Сплошной",
        "Радуга",
        "Волна",
        "Пульс",
        "Космос",
        "Туманность",
        "Паутина",
        "Плазма",
        "Стекло",
        "Матрица",
        "Космический взрыв",
        "Чужая туманность",
        "Аква",
        "Фиолетовый",
        "Золотой",
        "Поток",
        "Галактика",
        "Дым",
        "Домовой",
        "Псих",
        "HiDef",
        "Банда",
        "Святой",
        "Бирюзовый",
        "Геймер",
        "Техно",
        "Шелдон",
        "Сеть"
    };

    public final ModeSetting shader = new ModeSetting("Шейдер", "Туманность", MODES);
    public final FloatSetting speed = new FloatSetting("Скорость", 1.0F, 0.0F, 5.0F, 0.1F);
    public final FloatSetting opacity = new FloatSetting("Плотность", 1.0F, 0.1F, 1.0F, 0.05F);
    public final BooleanSetting only = new BooleanSetting("Только рисунок шейдера", false);

    private static final PoseStack QUAD_STACK = new PoseStack();

    private static HandShader instance;
    private static TextureTarget hand;
    private static TextureTarget scene;
    private static int frameWidth = -1;
    private static int frameHeight = -1;
    private static boolean captured;
    private static boolean broken;
    private static float time;
    private static long lastFrame;

    public HandShader() {
        super("HandShader", "Шейдеры рук и оружия от первого лица (эффекты Arcanic)", ModuleCategory.RENDER);
        addSettings(shader, speed, opacity, only);
        instance = this;
    }

    @Override
    public void onDisable() {
        release();
    }

    public static boolean isReady() {
        return instance != null && !broken;
    }

    /** Перед отрисовкой рук: уводим их в отдельную текстуру. */
    public static void begin() {
        HandShader module = instance;
        if (module == null || !module.isEnable() || broken) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options == null || !mc.options.getCameraType().isFirstPerson()) {
            return;
        }
        try {
            RenderTarget main = mc.getMainRenderTarget();
            if (main == null || !ensureTargets(main)) {
                return;
            }
            hand.bindWrite(true);
            RenderSystem.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
            captured = true;
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    /** После отрисовки рук: накладываем шейдер поверх сцены. */
    public static void finish() {
        if (!captured) {
            return;
        }
        captured = false;
        HandShader module = instance;
        if (module == null || hand == null || scene == null) {
            return;
        }
        try {
            Minecraft mc = Minecraft.getInstance();
            RenderTarget main = mc.getMainRenderTarget();
            if (main == null) {
                return;
            }
            CompiledShaderProgram shader = ShaderUtils.get(ShaderUtils.HAND_SHADER);
            if (shader == null) {
                broken = true;
                return;
            }
            main.bindWrite(true);

            float guiWidth = Math.max(1.0F, (float) mc.getWindow().getGuiScaledWidth());
            float guiHeight = Math.max(1.0F, (float) mc.getWindow().getGuiScaledHeight());

            // Копия сцены нужна только режиму «стекло»: он преломляет картинку за руками.
            if (module.shader.is("Стекло")) {
                copyScene(main, guiWidth, guiHeight);
            }

            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.setShaderTexture(0, hand.getColorTextureId());
            RenderSystem.setShaderTexture(1, scene.getColorTextureId());

            int colour = ThemeManager.getColor();
            ShaderUtils.set(shader, "HandColor", channel(colour, 16), channel(colour, 8), channel(colour, 0),
                    module.opacity.get());
            ShaderUtils.set(shader, "Resolution", main.width, main.height);
            ShaderUtils.set(shader, "Time", time);
            ShaderUtils.set(shader, "HandShaderMode", shaderMode(module));
            ShaderUtils.set(shader, "HandShaderOnly", module.only.get() ? 1.0F : 0.0F);

            RenderSystem.setShader(ShaderUtils.HAND_SHADER);
            quad(guiWidth, guiHeight);
            advanceTime(module);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderTexture(0, 0);
            RenderSystem.setShaderTexture(1, 0);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        } catch (Throwable throwable) {
            fail(throwable);
        }
    }

    /** Время для анимаций шейдера: копится по кадрам, чтобы не было скачков. */
    private static void advanceTime(HandShader module) {
        long now = System.nanoTime();
        if (lastFrame != 0L) {
            float delta = (now - lastFrame) / 1_000_000_000.0F;
            if (delta > 0.0F && delta < 1.0F) {
                time += delta * module.speed.get();
                if (time > 3600.0F) {
                    time -= 3600.0F;
                }
            }
        }
        lastFrame = now;
    }

    /** Копирует цвет сцены в отдельную текстуру (в 1.21.4 у RenderTarget нет copyFrom). */
    private static void copyScene(RenderTarget main, float guiWidth, float guiHeight) {
        scene.bindWrite(true);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.setShaderTexture(0, main.getColorTextureId());
        RenderSystem.setShader(CoreShaders.POSITION_TEX);
        texQuad(guiWidth, guiHeight);
        main.bindWrite(true);
    }

    /** Номер эффекта в шейдере: в списке Arcanic пропущены два номера. */
    private static float shaderMode(HandShader module) {
        int mode = module.shader.getIndex();
        if (mode >= 12) {
            mode += 2;
        }
        return mode;
    }

    private static boolean ensureTargets(RenderTarget main) {
        int width = main.width;
        int height = main.height;
        if (width < 2 || height < 2) {
            return false;
        }
        if (hand != null && frameWidth == width && frameHeight == height) {
            return true;
        }
        release();
        frameWidth = width;
        frameHeight = height;
        hand = new TextureTarget(width, height, true);
        scene = new TextureTarget(width, height, false);
        hand.setFilterMode(GL11.GL_LINEAR);
        scene.setFilterMode(GL11.GL_LINEAR);
        return true;
    }

    private static void release() {
        captured = false;
        frameWidth = -1;
        frameHeight = -1;
        hand = destroy(hand);
        scene = destroy(scene);
    }

    private static TextureTarget destroy(TextureTarget target) {
        if (target == null) {
            return null;
        }
        try {
            target.destroyBuffers();
        } catch (Throwable throwable) {
            Swerve.LOGGER.warn("Не удалось освободить буфер шейдера рук: {}", throwable.toString());
        }
        return null;
    }

    private static void fail(Throwable throwable) {
        if (!broken) {
            broken = true;
            Swerve.LOGGER.warn("Шейдер рук отключён: {}", throwable.toString());
        }
        release();
    }

    private static float channel(int argb, int shift) {
        return (float) ((argb >> shift) & 0xFF) / 255.0F;
    }

    /** Полноэкранный квад без цвета — обычная копия текстуры. */
    private static void texQuad(float width, float height) {
        Matrix4f matrix = QUAD_STACK.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        buffer.addVertex(matrix, 0.0F, 0.0F, 0.0F).setUv(0.0F, 1.0F);
        buffer.addVertex(matrix, 0.0F, height, 0.0F).setUv(0.0F, 0.0F);
        buffer.addVertex(matrix, width, height, 0.0F).setUv(1.0F, 0.0F);
        buffer.addVertex(matrix, width, 0.0F, 0.0F).setUv(1.0F, 1.0F);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }

    private static void quad(float width, float height) {
        Matrix4f matrix = QUAD_STACK.last().pose();
        BufferBuilder buffer = Tesselator.getInstance()
                .begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        buffer.addVertex(matrix, 0.0F, 0.0F, 0.0F).setUv(0.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.addVertex(matrix, 0.0F, height, 0.0F).setUv(0.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.addVertex(matrix, width, height, 0.0F).setUv(1.0F, 0.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);
        buffer.addVertex(matrix, width, 0.0F, 0.0F).setUv(1.0F, 1.0F).setColor(1.0F, 1.0F, 1.0F, 1.0F);

        MeshData mesh = buffer.build();
        if (mesh != null) {
            BufferUploader.drawWithShader(mesh);
        }
    }
}
