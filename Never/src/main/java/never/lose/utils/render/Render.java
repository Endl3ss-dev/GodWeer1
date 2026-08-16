package never.lose.utils.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import never.lose.NeverLose;
import never.lose.utils.render.builders.Builder;
import never.lose.utils.render.builders.states.QuadColorState;
import never.lose.utils.render.builders.states.QuadRadiusState;
import never.lose.utils.render.builders.states.SizeState;
import never.lose.utils.render.msdf.MsdfFont;
import never.lose.utils.client.IClient;
import never.lose.utils.math.Matrix4Stack;
import never.lose.utils.text.RWPrefix;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static never.lose.NeverLose.mc;

public final class Render {

    private Render() {}

    public static final Matrix4f matrix = new Matrix4f();
    public static final Matrix4Stack matrixStack = new Matrix4Stack();
    public static final Matrix4f lastProjMat = new Matrix4f();
    public static final Matrix4f lastModMat = new Matrix4f();
    public static final Matrix4f lastWorldSpaceMatrix = new Matrix4f();
    public static MatrixStack worldStack = new MatrixStack();

    private static final Map<Integer, Color> COLOR_CACHE = new HashMap<>();

    public static Color injectAlpha(Color color, float alpha) {
        int rgba = (color.getRGB() & 0x00FFFFFF) | (MathHelper.clamp((int) alpha, 0, 255) << 24);
        return COLOR_CACHE.computeIfAbsent(rgba, k -> new Color(rgba, true));
    }

    public static void round(float x, float y, float width, float height, float r1, float r2, float r3, float r4, float smoothness, Color c1, Color c2, Color c3, Color c4) {
        Builder.rectangle()
                .size(new SizeState(width, height))
                .color(new QuadColorState(c1, c2, c3, c4))
                .radius(new QuadRadiusState(r1, r2, r3, r4))
                .smoothness(smoothness)
                .build().render(matrixStack.matrix(), x, y);
    }

    public static void border(float x, float y, float width, float height, float r1, float r2, float r3, float r4, float smoothness, Color c1, Color c2, Color c3, Color c4) {
        Builder.border()
                .size(new SizeState(width, height))
                .color(new QuadColorState(c1, c2, c3, c4))
                .radius(new QuadRadiusState(r1, r2, r3, r4))
                .smoothness(smoothness, smoothness)
                .build().render(matrixStack.matrix(), x, y);
    }

    public static void texture(AbstractTexture texture, float x, float y, float width, float height, float u, float v, float texWidth, float texHeight, float r1, float r2, float r3, float r4, Color c1, Color c2, Color c3, Color c4) {
        Builder.texture()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(r1, r2, r3, r4))
                .texture(u, v, texWidth, texHeight, texture)
                .color(new QuadColorState(c1, c2, c3, c4))
                .build().render(matrixStack.matrix(), x, y);
    }

    public static void texture(int texture, float x, float y, float width, float height, float u, float v, float texWidth, float texHeight, float r1, float r2, float r3, float r4, Color c1, Color c2, Color c3, Color c4) {
        Builder.texture()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(r1, r2, r3, r4))
                .texture(u, v, texWidth, texHeight, texture)
                .color(new QuadColorState(c1, c2, c3, c4))
                .build().render(matrixStack.matrix(), x, y);
    }

    public static void border(float x, float y, float width, float height, float r1, float r2, float r3, float r4, float smoothness, float thickness, Color c1, Color c2, Color c3, Color c4) {
        Builder.border()
                .size(new SizeState(width, height))
                .color(new QuadColorState(c1, c2, c3, c4))
                .radius(new QuadRadiusState(r1, r2, r3, r4))
                .smoothness(smoothness, smoothness)
                .build().render(matrixStack.matrix(), x, y);
    }

    public static void border(float x, float y, float width, float height, float r1, float r2, float r3, float r4, float smoothness, float smoothness2, float thickness, Color c1, Color c2, Color c3, Color c4) {
        Builder.border()
                .size(new SizeState(width, height))
                .color(new QuadColorState(c1, c2, c3, c4))
                .radius(new QuadRadiusState(r1, r2, r3, r4))
                .smoothness(smoothness, smoothness2)
                .build().render(matrixStack.matrix(), x, y);
    }

    public static void outline(float x, float y, float width, float height, float r1, float r2, float r3, float r4, float smoothness, Color c1, Color c2, Color c3, Color c4, Color cb1, Color cb2, Color cb3, Color cb4) {
        round(x, y, width, height, r1, r2, r3, r4, smoothness, c1, c2, c3, c4);
        border(x, y, width, height, r1, r2, r3, r4, smoothness, cb1, cb2, cb3, cb4);
    }

    public static void outline(float x, float y, float width, float height, float r1, float r2, float r3, float r4, float smoothness, float thickness, Color c1, Color c2, Color c3, Color c4, Color cb1, Color cb2, Color cb3, Color cb4) {
        round(x, y, width, height, r1, r2, r3, r4, smoothness, c1, c2, c3, c4);
        border(x, y, width, height, r1, r2, r3, r4, smoothness, thickness, cb1, cb2, cb3, cb4);
    }

    public static void blur(float x, float y, float width, float height, float r1, float r2, float r3, float r4, float smoothness, float blur, Color c1, Color c2, Color c3, Color c4) {
        Builder.blur()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(r1, r2, r3, r4))
                .blurRadius(blur)
                .smoothness(smoothness)
                .color(new QuadColorState(c1, c2, c3, c4))
                .build().render(matrixStack.matrix(), x, y);
    }

    public static void text(Object object, float x, float y, String font, float size, Color c) {
        MsdfFont font1 = NeverLose.getInstance().getFontManager().getFont(font);
        if (font1 != null) {
            Builder.text()
                    .font(font1)
                    .text(object.toString())
                    .color(c)
                    .size(size)
                    .thickness(0F)
                    .spacing(0.3F)
                    .build().render(matrixStack.matrix(), x, y);
        }
    }

    public static void text(String object, float x, float y, String font, float size, Color c) {
        MsdfFont font1 = NeverLose.getInstance().getFontManager().getFont(font);
        if (font1 != null) {
            Builder.text()
                    .font(font1)
                    .text(object)
                    .color(c)
                    .size(size)
                    .thickness(0F)
                    .spacing(0.3F)
                    .build().render(matrixStack.matrix(), x, y);
        }
    }

    public static void textCentered(Object object, float x, float y, String font, float size, Color c) {
        MsdfFont font1 = NeverLose.getInstance().getFontManager().getFont(font);
        if (font1 != null) {
            Builder.text()
                    .font(font1)
                    .text(object.toString())
                    .color(c)
                    .size(size)
                    .thickness(0)
                    .spacing(0.3F)
                    .build().render(matrixStack.matrix(), x - textWidth(object.toString(), font, size) / 2F, y);
        }
    }

    public static void textGradient(Object object, float x, float y, String font, float size) {
        Builder.text()
                .font(NeverLose.getInstance().getFontManager().getFont(font))
                .text(object.toString())
                .size(size)
                .thickness(0.001f)
                .spacing(0.3F)
                .build().renderGradient(matrixStack.matrix(), x, y, 0);
    }

    public static void textCenteredGradient(Object object, float x, float y, String font, float size) {
        Builder.text()
                .font(NeverLose.getInstance().getFontManager().getFont(font))
                .text(object.toString())
                .size(size)
                .thickness(0.001f)
                .spacing(0.3F)
                .build().renderGradient(matrixStack.matrix(), x - textWidth(object.toString(), font, size) / 2F, y, 0);
    }

    public static BufferBuilder startFont() {
        return Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
    }

    public static void textCenteredRaw(Object object, float x, float y, String font, float size, Color c, BufferBuilder builder) {
        MsdfFont font1 = NeverLose.getInstance().getFontManager().getFont(font);
        Builder.text()
                .font(font1)
                .text(object.toString())
                .color(c)
                .size(size)
                .thickness(0.05f)
                .spacing(0.5F)
                .build().render(matrixStack.matrix(), builder, x - textWidth(object.toString(), font, size) / 2F, y, 0);
    }

    public static void textRaw(Text object, float x, float y, String font, float size, BufferBuilder builder) {
        float off = 0;
        if (!object.getSiblings().isEmpty()) {
            for (Text text_ : object.getSiblings()) {
                if (!text_.getSiblings().isEmpty()) {
                    for (Text text : text_.getSiblings()) {
                        off += mctextAccept(text, x + off, y, font, size, builder, true);
                    }
                } else {
                    off += mctextAccept(text_, x + off, y, font, size, builder, true);
                }
            }
        } else {
            mctextAccept(object, x + off, y, font, size, builder, true);
        }
    }

    private static float mctextAccept(Text text, float x, float y, String font, float size, BufferBuilder builder, boolean draw) {
        for (RWPrefix prefix : RWPrefix.values()) {
            if (text.getString().contains(prefix.symbol)) {
                text = Text.of(prefix.name()).copy().withColor(prefix.colorprefix.getRGB()).append(text.getString().replace(prefix.symbol + " ", ""));
            }
        }
        AtomicReference<Float> off = new AtomicReference<>(0f);
        String content = text.getString();
        String stripped = StringHelper.stripTextFormat(content).replace("\u26a1", "~");

        for (int i = 0; i < stripped.length(); i++) {
            char c = stripped.charAt(i);
            String charStr = String.valueOf(c);
            Color color = text.getStyle().getColor() == null ? Color.WHITE : new Color(text.getStyle().getColor().getRgb());

            if (draw) {
                textRaw(charStr, x + off.get(), y, font, size, color, builder);
            }

            float charWidth = textWidth(charStr, font, size);
            if (c == ' ') {
                charWidth = size * 0.5f;
            }

            float finalCharWidth = charWidth;
            off.updateAndGet(v -> v + finalCharWidth);
        }

        return off.get();
    }

    public static void textCenteredRaw(Text object, float x, float y, String font, float size, BufferBuilder builder) {
        textRaw(object, x - (textWidth(StringHelper.stripInvalidChars(object.getString()), font, size) + 2) / 2, y, font, size, builder);
    }

    public static float textWidth(Text object, String font, float size) {
        float off = 0;
        for (Text text_ : object.getSiblings()) {
            if (!text_.getSiblings().isEmpty()) {
                for (Text text : text_.getSiblings()) {
                    off += mctextAccept(text, off, 0, font, size, null, false);
                }
            } else {
                off += mctextAccept(text_, off, 0, font, size, null, false);
            }
        }
        return off + 8;
    }

    public static float textHeight(String text, String font, float size) {
        MsdfFont fontInstance = NeverLose.getInstance().getFontManager().getFont(font);
        if (fontInstance == null) {
            return 0;
        }
        String[] lines = text.split("\n");
        int lineCount = lines.length;
        float lineHeight = fontInstance.getAtlas().height();
        return lineHeight * lineCount;
    }

    public static void textRaw(Object object, float x, float y, String font, float size, Color c, BufferBuilder builder) {
        MsdfFont font1 = NeverLose.getInstance().getFontManager().getFont(font);
        Builder.text()
                .font(font1)
                .text(object.toString())
                .color(c)
                .size(size)
                .thickness(0.05f)
                .spacing(0.5F)
                .build().render(matrixStack.matrix(), builder, x, y, 0);
    }

    public static void endFont(BufferBuilder builder, String font) {
        MsdfFont font1 = NeverLose.getInstance().getFontManager().getFont(font);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShaderTexture(0, font1.getTextureId());
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);

        BuiltBuffer buf = builder.endNullable();
        if (buf != null) {
            BufferRenderer.drawWithGlobalProgram(buf);
        }

        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    public static float textWidth(Object object, String font, float size) {
        return NeverLose.getInstance().getFontManager().getFont(font).getWidth(object.toString(), size);
    }

    public static void rectBuilder(BufferBuilder bufferBuilder, Matrix4f matrix, float x, float y, float x1, float y1, Color c1, Color c2, Color c3, Color c4) {
        bufferBuilder.vertex(matrix, x, y1, 0.0F).color(c1.getRGB());
        bufferBuilder.vertex(matrix, x1, y1, 0.0F).color(c2.getRGB());
        bufferBuilder.vertex(matrix, x1, y, 0.0F).color(c3.getRGB());
        bufferBuilder.vertex(matrix, x, y, 0.0F).color(c4.getRGB());
    }

    public static float getRenderHurtTime(LivingEntity hurt) {
        return (float) hurt.hurtTime - (hurt.hurtTime != 0 ? IClient.getPartialTicksI() : 0.0f);
    }

    public static float getHurtPercent(LivingEntity hurt) {
        return getRenderHurtTime(hurt) / 20.0f;
    }

    public static void circleTheme(float x, float y, float radius, int start, int end, float thickness, int vertices) {
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        GL11.glEnable(GL13.GL_MULTISAMPLE);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        for (int i = start; i < end; i += vertices) {
            float sin = (float) (Math.sin(Math.toRadians(i)) * radius);
            float cos = (float) (Math.cos(Math.toRadians(i)) * radius);
            builder.vertex(x + sin, y + cos, 0).color(NeverLose.getInstance().getThemeManager().getColor(i).getRGB());
            builder.vertex(x + sin * thickness, y + cos * thickness, 0).color(NeverLose.getInstance().getThemeManager().getColor(i).getRGB());
        }

        BuiltBuffer buffer = builder.endNullable();
        if (buffer != null) {
            BufferRenderer.drawWithGlobalProgram(buffer);
        }
        GL11.glDisable(GL13.GL_MULTISAMPLE);
    }

    public static void circle(float x, float y, float width, float height, float r, float thickness, float start, float end, Color c1, Color c2) {
        Builder.circle().set(width, height, r, thickness, start, end, c1, c2).build().render(matrixStack.matrix(), x, y, 0);
    }

    public static void texture(float x, float y, float width, float height, Identifier texture, float u, float v, float tw, float wh, float r, Color a, Color b, Color c, Color d) {
        AbstractTexture abstractTexture = MinecraftClient.getInstance().getTextureManager().getTexture(texture);
        Builder.texture()
                .size(new SizeState(width, height))
                .radius(new QuadRadiusState(r, r, r, r))
                .texture(u, v, tw, wh, abstractTexture)
                .color(new QuadColorState(a, b, c, d))
                .build().render(matrixStack.matrix(), x, y, 0);
    }

    public static Color getScreenColorAt(float mouseX, float mouseY) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getFramebuffer() == null) {
            return null;
        }

        Framebuffer framebuffer = client.getFramebuffer();
        int displayWidth = client.getWindow().getWidth();
        int displayHeight = client.getWindow().getHeight();
        double scaleFactor = client.getWindow().getScaleFactor();
        int x = (int) (mouseX * scaleFactor);
        int y = (int) (mouseY * scaleFactor);
        y = displayHeight - y;
        x = Math.max(0, Math.min(x, displayWidth - 1));
        y = Math.max(0, Math.min(y, displayHeight - 1));

        ByteBuffer buffer = ByteBuffer.allocateDirect(4);
        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);

        try {
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, framebuffer.fbo);
            GL11.glReadPixels(x, y, 1, 1, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
            GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);

            int r = buffer.get(0) & 0xFF;
            int g = buffer.get(1) & 0xFF;
            int b = buffer.get(2) & 0xFF;
            int a = buffer.get(3) & 0xFF;

            return new Color(r, g, b, a);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    public static void box(MatrixStack position, Vec3d pos, int color) {
        if (pos == null) {
            return;
        }
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        position.push();

        RenderSystem.disableDepthTest();
        RenderSystem.lineWidth(5);
        Box box_ = new Box(pos.subtract(0.3, 0.3, 0.3), pos.add(0.3, 0.3, 0.3));
        box(position, box_, color);
        Render.drawLines(position);
        position.pop();
    }

    public static void box(MatrixStack position, Box pos, int color) {
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        position.push();

        RenderSystem.lineWidth(2);

        Render.line(pos.getMinPos(), pos.getMinPos().add(pos.getLengthX(), 0, 0), color, color);
        Render.line(pos.getMinPos(), pos.getMinPos().add(0, pos.getLengthY(), 0), color, color);
        Render.line(pos.getMinPos(), pos.getMinPos().add(0, 0, pos.getLengthZ()), color, color);

        Render.line(pos.getMaxPos(), pos.getMaxPos().subtract(pos.getLengthX(), 0, 0), color, color);
        Render.line(pos.getMaxPos(), pos.getMaxPos().subtract(0, pos.getLengthY(), 0), color, color);
        Render.line(pos.getMaxPos(), pos.getMaxPos().subtract(0, 0, pos.getLengthZ()), color, color);

        Render.line(pos.getMinPos().add(0, pos.getLengthY(), 0), pos.getMinPos().add(0, pos.getLengthY(), pos.getLengthZ()), color, color);
        Render.line(pos.getMinPos().add(0, pos.getLengthY(), 0), pos.getMinPos().add(pos.getLengthX(), pos.getLengthY(), 0), color, color);

        Render.line(pos.getMaxPos().subtract(0, pos.getLengthY(), 0), pos.getMaxPos().subtract(0, pos.getLengthY(), pos.getLengthZ()), color, color);
        Render.line(pos.getMaxPos().subtract(0, pos.getLengthY(), 0), pos.getMaxPos().subtract(pos.getLengthX(), pos.getLengthY(), 0), color, color);

        Render.line(pos.getMinPos().add(pos.getLengthX(), pos.getLengthY(), 0), pos.getMinPos().add(pos.getLengthX(), 0, 0), color, color);
        Render.line(pos.getMinPos().add(0, pos.getLengthY(), pos.getLengthZ()), pos.getMinPos().add(0, 0, pos.getLengthZ()), color, color);

        Render.drawLines(position);

        int col = Render.injectAlpha(new Color(color), 50).getRGB();
        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        builder.vertex(position.peek(), (float) pos.minX, (float) pos.minY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.minX, (float) pos.maxY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.maxY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.minY, (float) pos.minZ).color(col);

        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.minY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.maxY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.minX, (float) pos.maxY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.minX, (float) pos.minY, (float) pos.maxZ).color(col);

        builder.vertex(position.peek(), (float) pos.minX, (float) pos.minY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.minX, (float) pos.minY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.minX, (float) pos.maxY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.minX, (float) pos.maxY, (float) pos.minZ).color(col);

        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.minY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.minY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.maxY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.maxY, (float) pos.maxZ).color(col);

        builder.vertex(position.peek(), (float) pos.minX, (float) pos.maxY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.minX, (float) pos.maxY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.maxY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.maxY, (float) pos.minZ).color(col);

        builder.vertex(position.peek(), (float) pos.minX, (float) pos.minY, (float) pos.maxZ).color(col);
        builder.vertex(position.peek(), (float) pos.minX, (float) pos.minY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.minY, (float) pos.minZ).color(col);
        builder.vertex(position.peek(), (float) pos.maxX, (float) pos.minY, (float) pos.maxZ).color(col);

        RenderSystem.enableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        BufferRenderer.drawWithGlobalProgram(builder.end());

        position.pop();
    }

    public static @NotNull Vector3f getNormal(float x1, float y1, float z1, float x2, float y2, float z2) {
        float xNormal = x2 - x1;
        float yNormal = y2 - y1;
        float zNormal = z2 - z1;
        float normalSqrt = MathHelper.sqrt(xNormal * xNormal + yNormal * yNormal + zNormal * zNormal);
        return new Vector3f(xNormal / normalSqrt, yNormal / normalSqrt, zNormal / normalSqrt);
    }

    public static Color getContrastColor(Color rgbColor) {
        return new Color(255 - rgbColor.getRed(), 255 - rgbColor.getGreen(), 255 - rgbColor.getBlue());
    }

    private record LineVertex(Vec3d pos1, Vec3d pos2, int color1, int color2) { }

    static ArrayList<LineVertex> lineVertices = new ArrayList<>();

    public static void line(Vec3d pos1, Vec3d pos2, int color1, int color2) {
        lineVertices.add(new LineVertex(pos1, pos2, color1, color2));
    }

    public static void drawLines(MatrixStack stack) {
        stack.translate(-mc.gameRenderer.getCamera().getPos().x, -mc.gameRenderer.getCamera().getPos().y, -mc.gameRenderer.getCamera().getPos().z);

        BufferBuilder builder = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        for (LineVertex vertex : lineVertices) {
            Vector3f normal = getNormal((float) vertex.pos1.x, (float) vertex.pos1.y, (float) vertex.pos1.z, (float) vertex.pos2.x, (float) vertex.pos2.y, (float) vertex.pos2.z);
        builder.vertex(stack.peek(), (float) vertex.pos1.x, (float) vertex.pos1.y, (float) vertex.pos1.z).color(vertex.color1).normal(normal.x, normal.y, normal.z);
        builder.vertex(stack.peek(), (float) vertex.pos2.x, (float) vertex.pos2.y, (float) vertex.pos2.z).color(vertex.color1).normal(normal.x, normal.y, normal.z);
        }
        lineVertices.clear();
        BuiltBuffer buffer = builder.endNullable();
        if (buffer != null) {
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            BufferRenderer.drawWithGlobalProgram(buffer);
        }
    }
}