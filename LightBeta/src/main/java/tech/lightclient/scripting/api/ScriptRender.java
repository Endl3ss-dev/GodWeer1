package tech.lightclient.scripting.api;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import tech.lightclient.client.events.api.Event;
import tech.lightclient.client.utils.math.ProjectionUtil;
import tech.lightclient.client.utils.render.main.core.Renderer2D;
import tech.lightclient.client.utils.render.main.text.FontRegistry;
import tech.lightclient.client.utils.render.main.world.WorldGeometryEmitter;
import tech.lightclient.client.utils.render.main.world.WorldLineRenderer;
import tech.lightclient.client.utils.render.main.world.WorldRenderLayers;
import tech.lightclient.client.utils.render.main.world.WorldRenderer;

import java.util.List;

/** HUD and in-world drawing, mirroring the "Интерфейс" docs. */
public final class ScriptRender {

    // ---- 2D --------------------------------------------------------------------------------

    /** Drawing on the HUD. Coordinates are in real framebuffer pixels. */
    public static final class Draw2D {
        private final Renderer2D r;

        public Draw2D(Renderer2D r) {
            this.r = r;
        }

        public void rect(float x, float y, float w, float h, int argb) {
            r.rect(x, y, w, h, argb);
        }

        public void roundedRect(float x, float y, float w, float h, float rounding, int argb) {
            r.rect(x, y, w, h, rounding, argb);
        }

        public void outline(float x, float y, float w, float h, int argb, float thickness) {
            r.rectOutline(x, y, w, h, argb, thickness);
        }

        public void roundedOutline(float x, float y, float w, float h, float rounding, int argb, float thickness) {
            r.rectOutline(x, y, w, h, rounding, argb, thickness);
        }

        public void circle(float cx, float cy, float radius, int argb) {
            r.circle(cx, cy, radius, 0f, 1f, argb);
        }

        public void arc(float cx, float cy, float radius, float startDegrees, float portion, int argb) {
            r.circle(cx, cy, radius, startDegrees, portion, argb);
        }

        public void gradient(float x, float y, float w, float h, int topLeft, int topRight, int bottomRight, int bottomLeft) {
            r.gradient(x, y, w, h, topLeft, topRight, bottomRight, bottomLeft);
        }

        public void blur(float x, float y, float w, float h, float rounding) {
            r.blur(x, y, w, h, rounding);
        }

        public void shadow(float x, float y, float w, float h, float rounding, float blur, float spread, int argb) {
            r.shadow(x, y, w, h, rounding, blur, spread, argb);
        }

        public void text(float x, float y, float size, Object value, int argb) {
            r.text(FontRegistry.SF_MEDIUM, x, y, size, stringOf(value), argb);
        }

        public void boldText(float x, float y, float size, Object value, int argb) {
            r.text(FontRegistry.SF_BOLD, x, y, size, stringOf(value), argb);
        }

        public void centredText(float centreX, float y, float size, Object value, int argb) {
            r.centredText(FontRegistry.SF_MEDIUM, centreX, y, size, stringOf(value), argb);
        }

        public float textWidth(Object value, float size) {
            return FontRegistry.SF_MEDIUM.getWidth(stringOf(value), size);
        }

        public void pushClip(float x, float y, float w, float h) {
            r.pushClipRect((int) x, (int) y, (int) w, (int) h);
        }

        public void popClip() {
            r.popClipRect();
        }

        /** Draws a compiled shader over a rectangle. */
        public void shader(ScriptShader shader, float x, float y, float w, float h) {
            if (shader != null) shader.draw(r, x, y, w, h);
        }

        /**
         * Projects a world position onto the screen, for labelling things in the world.
         * Returns null when the point is behind the camera or off screen.
         */
        public Vec3d project(Vec3d worldPosition) {
            try {
                Vec3d screen = ProjectionUtil.worldSpaceToScreenSpace(worldPosition);
                if (screen == null) return null;
                // worldSpaceToScreenSpace marks "behind the camera" with a negative z.
                return screen.z < 0 || screen.z >= 1 ? null : screen;
            } catch (Throwable t) {
                return null;
            }
        }

        private static String stringOf(Object value) {
            if (value instanceof ScriptText st) return st.string();
            return String.valueOf(value);
        }
    }

    /** Fired every frame on the client's overlay layer. */
    public static final class Event2D extends Event {
        private final Draw2D render;
        private final int width;
        private final int height;
        private final float tickDelta;

        public Event2D(Renderer2D renderer, int width, int height, float tickDelta) {
            this.render = new Draw2D(renderer);
            this.width = width;
            this.height = height;
            this.tickDelta = tickDelta;
        }

        public Draw2D render() { return render; }

        public int width() { return width; }

        public int height() { return height; }

        public float tickDelta() { return tickDelta; }
    }

    // ---- 3D --------------------------------------------------------------------------------

    /**
     * Drawing in the world. All coordinates are world coordinates, the same ones
     * {@code player.position()} returns.
     */
    public static final class Draw3D {
        private final WorldRenderer renderer;
        private final Camera camera;
        private final Vec3d cameraPos;

        public Draw3D(WorldRenderer renderer, Camera camera) {
            this.renderer = renderer;
            this.camera = camera;
            this.cameraPos = camera.getCameraPos();
        }

        private WorldGeometryEmitter emitter(boolean throughWalls) {
            return new WorldGeometryEmitter(camera, new net.minecraft.client.util.math.MatrixStack().peek(),
                    renderer.getBuffer(throughWalls
                            ? WorldRenderLayers.POSITION_COLOR_QUADS_ALPHA_SRC_ONE_NO_DEPTH()
                            : WorldRenderLayers.POSITION_COLOR_QUADS()));
        }

        /** Edges of a box. */
        public void box(Box box, int argb, boolean throughWalls) {
            if (box == null) return;
            WorldLineRenderer.box(emitter(throughWalls), box, cameraPos, argb, 2.0);
            renderer.flush();
        }

        /** Solid box. */
        public void filledBox(Box box, int argb, boolean throughWalls) {
            if (box == null) return;
            renderer.drawCube(
                    new Vec3d(box.minX, box.minY, box.minZ).subtract(cameraPos),
                    new Vec3d(box.maxX, box.maxY, box.maxZ).subtract(cameraPos),
                    argb, !throughWalls);
            renderer.flush();
        }

        /** Box around an entity, already smoothed between ticks. */
        public void entityBox(WorldApi.ScriptEntity entity, int argb, boolean throughWalls) {
            if (entity == null) return;
            Vec3d offset = entity.renderPosition().subtract(entity.position());
            box(entity.box().offset(offset), argb, throughWalls);
        }

        public void line(Vec3d from, Vec3d to, int argb, boolean throughWalls) {
            if (from == null || to == null) return;
            WorldLineRenderer.line(emitter(throughWalls), from, to, cameraPos, argb, 2.0);
            renderer.flush();
        }

        /** Line from the crosshair to a point. */
        public void tracer(Vec3d target, int argb, boolean throughWalls) {
            if (target == null) return;
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player == null) return;
            Vec3d start = camera.getCameraPos().add(
                    Vec3d.fromPolar(camera.getPitch(), camera.getYaw()).multiply(0.1));
            line(start, target, argb, throughWalls);
        }

        public void triangle(Vec3d a, Vec3d b, Vec3d c, int argb, boolean throughWalls) {
            if (a == null || b == null || c == null) return;
            renderer.drawQuad(a.subtract(cameraPos), b.subtract(cameraPos),
                    c.subtract(cameraPos), c.subtract(cameraPos), argb, !throughWalls);
            renderer.flush();
        }

        public void quad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, int argb, boolean throughWalls) {
            if (a == null || b == null || c == null || d == null) return;
            renderer.drawQuad(a.subtract(cameraPos), b.subtract(cameraPos),
                    c.subtract(cameraPos), d.subtract(cameraPos), argb, !throughWalls);
            renderer.flush();
        }

        /**
         * Convex polygon, fanned out from the first point. Points must run in order along the
         * outline; a concave outline will not come out right.
         */
        public void polygon(List<Vec3d> points, int argb, boolean throughWalls) {
            if (points == null || points.size() < 3) return;
            Vec3d first = points.get(0);
            for (int i = 1; i + 1 < points.size(); i++) {
                triangle(first, points.get(i), points.get(i + 1), argb, throughWalls);
            }
        }
    }

    /** Fired every frame while the world is drawn. */
    public static final class Event3D extends Event {
        private final Draw3D render;
        private final float tickDelta;
        private final Vec3d camera;

        public Event3D(WorldRenderer renderer, Camera camera, float tickDelta) {
            this.render = new Draw3D(renderer, camera);
            this.tickDelta = tickDelta;
            this.camera = camera.getCameraPos();
        }

        public Draw3D render() { return render; }

        public float tickDelta() { return tickDelta; }

        public Vec3d camera() { return camera; }
    }

    private ScriptRender() {}
}
