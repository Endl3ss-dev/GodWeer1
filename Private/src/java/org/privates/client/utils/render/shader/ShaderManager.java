package org.privates.client.utils.render.shader;

import lombok.Getter;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.math.vector.Matrix4f;
import net.mojang.blaze3d.matrix.MatrixStack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.privates.client.api.interfaces.IMinecraft;
import org.privates.client.api.interfaces.IRender;
import org.privates.client.utils.file.FileUtils;
import org.privates.client.utils.render.shader.glsl.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11C.GL_QUADS;
import static org.lwjgl.opengl.GL20.*;

@SuppressWarnings({"UnusedReturnValue", "unused"})
@Getter
public class ShaderManager implements IRender, IMinecraft {
    private final int programID;
    public static ShaderManager fontShader;
    public static ShaderManager kawaseUpBloom;
    public static ShaderManager kawaseDownBloom;
    public static ShaderManager entityChamsShader;
    public static ShaderManager entityTransparentOutlineShader;
    public static ShaderManager entityOutlineShader;
    public static ShaderManager roundedShader;
    public static ShaderManager roundedTextureShader;
    public static ShaderManager roundedOutlineShader;
    public static ShaderManager mainmenuShader;
    public static ShaderManager distortedGlass;
    public static ShaderManager roundedHeadShader2;

    public static ShaderManager roundedVerticalShader;
    public static ShaderManager blur;
    public static ShaderManager KAWASE_UP;
    public static ShaderManager KAWASE_DOWN;
    public static ShaderManager blur2;
    public static ShaderManager crystalFacets;
    public static ShaderManager pixelatedGlass;
    public static ShaderManager animatedWaves;
    public static ShaderManager hexagonalBlur;
    public static ShaderManager frostedGlass;
    public static ShaderManager cobweb;


    public static ShaderManager roundedHead;
    public static ShaderManager cosmicNebula;

    public static ShaderManager liquidGlassShader;
    public static ShaderManager biomorphCells;

    public static ShaderManager blockEnergyGrid;
    public static ShaderManager blockHoloCrystal;
    public static ShaderManager blockGlowWaves;
    public static ShaderManager trailWaves;
    public static ShaderManager saturation;
    public static ShaderManager glowEnhancement;
    public static ShaderManager smoothMotion;
    
    // Season shaders
    public static ShaderManager cosmic;
    public static ShaderManager summer;
    public static ShaderManager winter;
    public static ShaderManager autumn;
    
    // Sky shaders
    public static ShaderManager skyNebula;
    public static ShaderManager skyCosmic;
    public static ShaderManager skyWaves;
    public static ShaderManager skyAurora;

    public static void loadShaders() {
        fontShader = create(new FontGlsl());
        cobweb = create(new CobwebGlsl());
        biomorphCells = create(new BiomorphCellsGlsl());
        cosmicNebula = create(new CosmicNebulaGlsl());
        kawaseUpBloom = create(new KawaseUpBloomGlsl());
        crystalFacets = create(new CrystalFacetsGlsl());
        pixelatedGlass = create(new PixelatedGlassGlsl());
        animatedWaves = create(new AnimatedWavesGlsl());
        hexagonalBlur = create(new HexagonalBlurGlsl());
        frostedGlass = create(new FrostedGlassGlsl());
        kawaseDownBloom = create(new KawaseDownBloomGlsl());

        roundedHeadShader2 = create(new RoundedHeadGlsl2());
        distortedGlass = create(new DistortedGlassGlsl());

        entityChamsShader = create(new EntityChamsGlsl());
        entityTransparentOutlineShader = create(new EntityTransparentOutlineGlsl());
        entityOutlineShader = create(new EntityOutlineGlsl());
        roundedShader = create(new RoundedGlsl());
        roundedTextureShader = create(new RoundedTextureGlsl());
        roundedOutlineShader = create(new RoundedOutline());
        mainmenuShader = create(new MainMenuGlsl());
        roundedVerticalShader = create(new RoundedVerticalGlsl());
        blur = create(new BlurGlsl());
        blur2 = create(new BlurGlsl2());
        KAWASE_UP = create(new KawaseBlurUp());
        KAWASE_DOWN = create(new KawaseBlurDown());

        roundedHead = create(new RoundedHeadGlsl());

        liquidGlassShader = create(new LiquidGlassGlsl());

        blockEnergyGrid = create(new BlockEnergyGridShader());
        blockHoloCrystal = create(new BlockHoloCrystalShader());
        blockGlowWaves = create(new BlockGlowWavesShader());
        trailWaves = create(new TrailWavesShader());
        saturation = create(new SaturationGlsl());
        glowEnhancement = create(new GlowEnhancementGlsl());
        smoothMotion = create(new SmoothMotionGlsl());
        
        // Load season shaders
        cosmic = create(new CosmicShader());
        summer = create(new SummerShader());
        winter = create(new WinterShader());
        autumn = create(new AutumnShader());
        
        // Load sky shaders
        skyNebula = create(new SkyNebulaShader());
        skyCosmic = create(new SkyCosmicShader());
        skyWaves = create(new SkyWavesShader());
        skyAurora = create(new SkyAuroraShader());
    }

    private ShaderManager(IShader fragmentShaderLoc, IShader vertexShaderLoc) {
        int program = glCreateProgram();
        int fragmentShaderID = createShader(new ByteArrayInputStream(fragmentShaderLoc.shader().getBytes()), GL_FRAGMENT_SHADER);
        GL20.glAttachShader(program, fragmentShaderID);
        int vertexShaderID = createShader(new ByteArrayInputStream(vertexShaderLoc.shader().getBytes()), GL_VERTEX_SHADER);
        GL20.glAttachShader(program, vertexShaderID);
        GL20.glLinkProgram(program);
        int status = glGetProgrami(program, GL_LINK_STATUS);
        if (status == 0) throw new IllegalStateException("Shader creation failed");
        this.programID = program;
    }

    public static ShaderManager create(IShader shader) {
        return new ShaderManager(shader, new VertexGlsl());
    }

    public static ShaderManager create(IShader fragShader, IShader vertexShader) {
        return new ShaderManager(fragShader, vertexShader);
    }

    private int createShader(InputStream inputStream, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, FileUtils.readInputStream(inputStream));
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == 0) {
            System.out.println(glGetShaderInfoLog(shader, 4096));
            throw new IllegalStateException(String.format("Shader (%s) failed to compile", shaderType));
        }
        return shader;
    }

    public void load() {
        glUseProgram(programID);
    }

    public void unload() {
        glUseProgram(0);
    }

    public int getUniform(String name) {
        return glGetUniformLocation(programID, name);
    }

    public ShaderManager setUniformf(String name, float... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1 -> glUniform1f(loc, args[0]);
            case 2 -> glUniform2f(loc, args[0], args[1]);
            case 3 -> glUniform3f(loc, args[0], args[1], args[2]);
            case 4 -> glUniform4f(loc, args[0], args[1], args[2], args[3]);
        }
        return this;
    }

    public ShaderManager setUniformi(String name, int... args) {
        int loc = glGetUniformLocation(programID, name);
        switch (args.length) {
            case 1 -> glUniform1i(loc, args[0]);
            case 2 -> glUniform2i(loc, args[0], args[1]);
            case 3 -> glUniform3i(loc, args[0], args[1], args[2]);
            case 4 -> glUniform4i(loc, args[0], args[1], args[2], args[3]);
        }
        return this;
    }

    public void setUniformfb(String name, FloatBuffer buffer) {
        GL30.glUniform1fv(GL30.glGetUniformLocation(programID, name), buffer);
    }

    public ShaderManager setMat4fv(String name, FloatBuffer matrix) {
        int loc = glGetUniformLocation(programID, name);
        glUniformMatrix4fv(loc, false, matrix);
        return this;
    }

    public ShaderManager setMat4fv(String name, float[] matrix) {
        int loc = glGetUniformLocation(programID, name);
        glUniformMatrix4fv(loc, false, matrix);
        return this;
    }

    public ShaderManager setMat4fv(String name, MatrixStack matrix) {
        setMat4fv(name, matrix.getLast().getMatrix());
        return this;
    }

    public ShaderManager setMat4fv(String name, Matrix4f matrix) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        matrix.write(floatBuffer);
        setMat4fv(name, floatBuffer);
        return this;
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer) {
        return createFrameBuffer(framebuffer, false);
    }

    public static boolean needsNewFramebuffer(Framebuffer framebuffer) {
        return framebuffer == null || framebuffer.framebufferWidth != mc.getMainWindow().getWidth()
                || framebuffer.framebufferHeight != mc.getMainWindow().getHeight();
    }

    public static Framebuffer createFrameBuffer(Framebuffer framebuffer, boolean depth) {
        try {
            if (needsNewFramebuffer(framebuffer)) {
                if (framebuffer != null) {
                    framebuffer.deleteFramebuffer();
                }
                return new Framebuffer(mc.getMainWindow().getWidth(), mc.getMainWindow().getHeight(), depth, false);
            }
            return framebuffer;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void draw() {
        draw(0, 0, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
    }

    public static void draw(double x, double y, double width, double height) {
        GL11.glBegin(GL_QUADS);
        GL11.glTexCoord2d(0, 0);
        GL11.glVertex2d(x, y);
        GL11.glTexCoord2d(0, 1);
        GL11.glVertex2d(x, y + height);
        GL11.glTexCoord2d(1, 1);
        GL11.glVertex2d(x + width, y + height);
        GL11.glTexCoord2d(1, 0);
        GL11.glVertex2d(x + width, y);
        GL11.glEnd();
    }
}
