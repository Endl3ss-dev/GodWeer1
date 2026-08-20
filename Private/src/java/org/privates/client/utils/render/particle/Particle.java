package org.privates.client.utils.render.particle;

import com.sun.javafx.geom.Vec3f;
import lombok.Data;
import net.minecraft.util.math.MathHelper;
import org.privates.client.utils.animation.Animation;
import org.privates.lib.util.time.StopWatch;

import java.util.List;

@Data
public class Particle {
    private float baseX, baseY, size, motionX, motionY, mass, gravity, gravityMultiplier;
    private float offsetX, offsetY;
    private boolean gravityEnabled, collisionEnabled, delayEnabled;
    private double delay;
    private ParticleQuad particleQuad;
    private final StopWatch time = new StopWatch();
    private final Animation animation = new Animation();

    // 3D Particle fields
    private Vec3f pos;
    private Vec3f velocity;
    private long lifeTime;
    private long startTime;
    private float alpha;

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass, float gravity, float gravityMultiplier, boolean gravityEnabled, boolean collisionEnabled, boolean delayEnabled, double delay) {
        this.baseX = baseX;
        this.baseY = baseY;
        this.size = size;
        this.motionX = motionX;
        this.motionY = motionY;
        this.mass = mass;
        this.particleQuad = new ParticleQuad(baseX, baseY, baseX + size, baseY + size);
        this.gravity = gravity;
        this.gravityMultiplier = gravityMultiplier;
        this.gravityEnabled = gravityEnabled;
        this.collisionEnabled = collisionEnabled;
        this.delayEnabled = delayEnabled;
        this.delay = delay;
        this.time.reset();
    }

    // 3D Particle Constructor
    public Particle(float x, float y, float z, float velocityX, float velocityY, float velocityZ, float size, long lifeTime) {
        this.pos = new Vec3f(x, y, z);
        this.velocity = new Vec3f(velocityX, velocityY, velocityZ);
        this.size = size;
        this.lifeTime = lifeTime;
        this.startTime = System.currentTimeMillis();
        this.alpha = 1.0f;
        // Initialize 2D fields to avoid NullPointer if they are used elsewhere unexpectedly
        this.baseX = x;
        this.baseY = y;
        this.motionX = velocityX;
        this.motionY = velocityY;
        this.mass = 1.0f; // Default mass
        this.gravity = 0.0f; // No gravity for 3D particles by default
        this.gravityMultiplier = 0.0f;
        this.gravityEnabled = false;
        this.collisionEnabled = false;
        this.delayEnabled = false;
        this.delay = 0;
        this.particleQuad = new ParticleQuad(x, y, x + size, y + size);
        this.time.reset();
    }

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass, float gravity, float gravityMultiplier, boolean collisionEnabled, double delay) {
        this(baseX, baseY, size, motionX, motionY, mass, gravity, gravityMultiplier, true, collisionEnabled, true, delay);
    }

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass, float gravity, float gravityMultiplier, double delay) {
        this(baseX, baseY, size, motionX, motionY, mass, gravity, gravityMultiplier, true, false, true, delay);
    }

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass, float gravity, float gravityMultiplier, boolean collisionEnabled) {
        this(baseX, baseY, size, motionX, motionY, mass, gravity, gravityMultiplier, true, collisionEnabled, false, 0);
    }

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass) {
        this(baseX, baseY, size, motionX, motionY, mass, 0, 0, false, false, false, 0);
    }

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass, boolean collisionEnabled, double delay) {
        this(baseX, baseY, size, motionX, motionY, mass, 0, 0, false, collisionEnabled, true, delay);
    }

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass, double delay) {
        this(baseX, baseY, size, motionX, motionY, mass, 0, 0, false, false, true, delay);
    }

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass, boolean collisionEnabled) {
        this(baseX, baseY, size, motionX, motionY, mass, 0, 0, false, collisionEnabled, false, 0);
    }

    public Particle(float baseX, float baseY, float size, float motionX, float motionY, float mass, float gravity, float gravityMultiplier) {
        this(baseX, baseY, size, motionX, motionY, mass, gravity, gravityMultiplier, true, false, false, 0);
    }

    // Getters for 3D particles
    public Vec3f getPos() {
        return pos;
    }

    public float getSize() {
        return size;
    }

    public float getAlpha() {
        return alpha;
    }

    public void update(float deltaTime) {
        // Update for 2D particles
        if (pos == null) { // Check if it's a 2D particle
            animation.update();
            if (gravityEnabled) {
                motionY += gravity * deltaTime;
                motionY *= 1 - deltaTime * gravityMultiplier;
                if (!collisionEnabled) motionX *= 1 - deltaTime * (1F - 0.9F);
            }

            offsetX += motionX * deltaTime;
            offsetY += motionY * deltaTime;

            this.getParticleQuad().set(baseX + offsetX, baseY + offsetY, baseX + offsetX + size, baseY + offsetY + size);
        } else { // Update for 3D particles
            float vx = velocity.x; // Direct field access if available, adjust if different
            float vy = velocity.y;
            float vz = velocity.z;
            pos = new Vec3f(pos.x + vx * deltaTime, pos.y + vy * deltaTime, pos.z + vz * deltaTime);
            float progress = (float) (System.currentTimeMillis() - startTime) / lifeTime;
            alpha = 1.0f - progress;
        }
    }

    public void handleCollision(Particle other) {
        // Existing 2D collision logic
        if (this.pos == null && other.pos == null) {
            ParticleQuad thisBox = this.getParticleQuad();
            ParticleQuad otherBox = other.getParticleQuad();

            if (thisBox.intersects(otherBox)) {
                float dx = this.baseX - other.baseX;
                float dy = this.baseY - other.baseY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance == 0) return;

                float nx = dx / distance;
                float ny = dy / distance;

                float vx = this.motionX - other.motionX;
                float vy = this.motionY - other.motionY;
                float vn = vx * nx + vy * ny;

                if (vn > 0) return;

                float impulse = (2 * vn) / (this.mass + other.mass);

                this.motionX -= impulse * other.mass * nx;
                this.motionY -= impulse * other.mass * ny;
                other.motionX += impulse * this.mass * nx;
                other.motionY += impulse * this.mass * ny;
            }
        } else {
            // No 3D collision logic implemented yet
        }
    }

    public void handleBoundaryCollision(float minX, float minY, float maxX, float maxY) {
        // Existing 2D boundary collision logic
        if (pos == null) {
            ParticleQuad box = getParticleQuad();

            float multiply = -1;

            if (box.getLeft() < minX) {
                baseX = minX;
                motionX *= multiply;
            } else if (box.getRight() > maxX + (size / 2F)) {
                baseX = maxX - (size / 2F);
                motionX *= multiply;
            }

            if (box.getTop() < minY) {
                baseY = minY;
                motionY *= multiply;
            } else if (box.getBottom() > maxY + (size / 2F)) {
                baseY = maxY - (size / 2F);
                motionY *= multiply;
            }
        } else {
            // No 3D boundary collision logic implemented yet
        }
    }

    public void handleShapeCollision(Polygon shape, boolean keepInside) {
        // Existing 2D shape collision logic
        if (pos == null) {
            List<Polygon.Vec2f> vertices = shape.getVertices();
            int numVertices = vertices.size();

            for (int i = 0; i < numVertices; i++) {
                Polygon.Vec2f v1 = vertices.get(i);
                Polygon.Vec2f v2 = vertices.get((i + 1) % numVertices);

                if (lineIntersectsParticle(v1.getX(), v1.getY(), v2.getX(), v2.getY())) {
                    handleCollisionWithLine(v1.getX(), v1.getY(), v2.getX(), v2.getY());
                }
                if (keepInside) keepParticleInsidePolygon(v1.getX(), v1.getY(), v2.getX(), v2.getY());
            }
        } else {
            // No 3D shape collision logic implemented yet
        }
    }

    public void handleShapeCollision(Polygon shape) {
        handleShapeCollision(shape, true);
    }

    private boolean lineIntersectsParticle(float x1, float y1, float x2, float y2) {
        // Existing 2D line intersection logic
        float dx = x2 - x1;
        float dy = y2 - y1;
        float lengthSquared = dx * dx + dy * dy;
        float t = ((baseX - x1) * dx + (baseY - y1) * dy) / lengthSquared;

        if (t < 0.0f) t = 0.0f;
        else if (t > 1.0f) t = 1.0f;

        float nearestX = x1 + t * dx;
        float nearestY = y1 + t * dy;

        float distX = baseX - nearestX;
        float distY = baseY - nearestY;

        return Math.sqrt(distX * distX + distY * distY) <= Math.sqrt(size * size);
    }

    private void handleCollisionWithLine(float x1, float y1, float x2, float y2) {
        // Existing 2D collision with line logic
        float dx = x2 - x1;
        float dy = y2 - y1;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        dx /= length;
        dy /= length;

        float normalX = -dy;
        float normalY = dx;

        float dot = motionX * normalX + motionY * normalY;

        motionX -= 2 * dot * normalX;
        motionY -= 2 * dot * normalY;
    }

    private void keepParticleInsidePolygon(float x1, float y1, float x2, float y2) {
        // Existing 2D keep inside polygon logic
        float dx = x2 - x1;
        float dy = y2 - y1;
        float lengthSquared = dx * dx + dy * dy;
        float t = ((baseX - x1) * dx + (baseY - y1) * dy) / lengthSquared;

        if (t < 0.0f) t = 0.0f;
        else if (t > 1.0f) t = 1.0f;

        float nearestX = x1 + t * dx;
        float nearestY = y1 + t * dy;

        float distX = baseX - nearestX;
        float distY = baseY - nearestY;
        float distance = (float) Math.sqrt(distX * distX + distY * distY);

        if (distance < size / 2) {
            baseX += distX / (distance * distance);
            baseY += distY / (distance * distance);
        }
    }

    public void set(float x, float y, float size) {
        this.baseX = x;
        this.baseY = y;
        this.size = size;
        this.getParticleQuad().set(x + offsetX, y + offsetY, x + offsetX + size, y + offsetY + size);
    }

    public float getBaseX() {
        return baseX + offsetX;
    }

    public float getBaseY() {
        return baseY + offsetY;
    }

    public boolean isFinished() {
        // Check if it's a 2D particle or a 3D particle
        if (pos == null) {
            return delayEnabled && time.finished(delay);
        } else {
            return System.currentTimeMillis() - startTime > lifeTime || alpha <= 0.0f;
        }
    }

    public float getTimePC(long maxMS) {
        return MathHelper.clamp(time.elapsedTime() / (float) maxMS, 0.F, 1.F);
    }
}
