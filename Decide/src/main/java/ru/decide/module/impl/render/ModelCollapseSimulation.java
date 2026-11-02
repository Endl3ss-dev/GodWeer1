/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_12249
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_1922
 *  net.minecraft.class_2338
 *  net.minecraft.class_2338$class_2339
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_265
 *  net.minecraft.class_2680
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587$class_4665
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4608
 *  net.minecraft.class_638
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.textures.GpuTexture;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_12249;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_1922;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.class_638;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.module.impl.render.ModelCollapseSnapshots;
import ru.decide.utils.render.ModelBoxCapture;

@Environment(value=EnvType.CLIENT)
public final class ModelCollapseSimulation {
    private static final int MAX_PILES = 8;
    private static final int MAX_CUBES_PER_PILE = 2800;
    private static final int MAX_CUBES_TOTAL = 6500;
    private static final float MAX_STEP = 0.016666668f;
    private static final float GRAVITY = 12.5f;
    private static final float AIR_DRAG = 0.85f;
    private static final float GROUND_FRICTION = 0.62f;
    private static final float MIN_BOUNCE = 0.55f;
    private static final float REST_SPEED_SQ = 0.09f;
    private static final long SHRINK_MS = 600L;
    private static final long HURT_OVERLAY_MS = 450L;
    private static final float[] UV_SCRATCH = new float[2];
    private static final float[] CLIP = new float[30];
    private static final Vector3f POSITION_SCRATCH = new Vector3f();
    private static final Vector3f NORMAL_SCRATCH = new Vector3f();
    private static final float[] CORNERS_X = new float[8];
    private static final float[] CORNERS_Y = new float[8];
    private static final float[] CORNERS_Z = new float[8];
    private static final int[] FACES = new int[]{1, 3, 7, 5, 0, 2, 6, 4, 2, 3, 7, 6, 0, 1, 5, 4, 4, 5, 7, 6, 0, 1, 3, 2};
    private final List<Pile> piles = new ArrayList<Pile>();
    private final Random random = new Random();
    private final class_2338.class_2339 blockPos = new class_2338.class_2339();
    private long lastFrameNs;

    public boolean isIdle() {
        return this.piles.isEmpty();
    }

    public void clear() {
        this.piles.clear();
        this.lastFrameNs = 0L;
    }

    public int totalCubes() {
        int total = 0;
        for (Pile pile : this.piles) {
            total += pile.cubes.size();
        }
        return total;
    }

    public boolean spawn(class_1309 entity, float partialTick, Settings settings) {
        ModelCollapseSnapshots.Snapshot snapshot = ModelCollapseSnapshots.take(entity.method_5628());
        boolean gone = entity.method_31481() || !entity.method_5805();
        double originX = snapshot != null && gone ? snapshot.x() : class_3532.method_16436((double)partialTick, (double)entity.field_6038, (double)entity.method_23317());
        double originY = snapshot != null && gone ? snapshot.y() : class_3532.method_16436((double)partialTick, (double)entity.field_5971, (double)entity.method_23318());
        double originZ = snapshot != null && gone ? snapshot.z() : class_3532.method_16436((double)partialTick, (double)entity.field_5989, (double)entity.method_23321());
        List<ModelBoxCapture.Box> boxes = snapshot != null && snapshot.boxes() != null ? snapshot.boxes() : ModelBoxCapture.capture(entity, partialTick, false);
        float height = snapshot != null ? snapshot.height() : entity.method_17682();
        class_243 velocity = entity.method_18798();
        float motionX = snapshot != null ? snapshot.motionX() : (float)velocity.field_1352;
        float motionY = snapshot != null ? snapshot.motionY() : (float)velocity.field_1351;
        float motionZ = snapshot != null ? snapshot.motionZ() : (float)velocity.field_1350;
        return this.buildPile(entity.method_5628(), boxes, originX, originY, originZ, height * 0.5f, motionX, motionY, motionZ, settings, false);
    }

    public boolean spawnFrozen(int entityId, ModelCollapseSnapshots.Snapshot snapshot, Settings settings) {
        return this.buildPile(entityId, snapshot.boxes(), snapshot.x(), snapshot.y(), snapshot.z(), snapshot.height() * 0.5f, snapshot.motionX(), snapshot.motionY(), snapshot.motionZ(), settings, true);
    }

    public boolean release(int entityId) {
        for (Pile pile : this.piles) {
            if (pile.entityId != entityId || !pile.frozen) continue;
            pile.frozen = false;
            pile.spawnMs = System.currentTimeMillis();
            return true;
        }
        return false;
    }

    public void dropFrozen(int entityId) {
        this.piles.removeIf(p -> p.entityId == entityId && p.frozen);
    }

    private boolean buildPile(int entityId, List<ModelBoxCapture.Box> captured, double originX, double originY, double originZ, float centerY, float motionX, float motionY, float motionZ, Settings settings, boolean frozen) {
        if (this.piles.size() >= 8) {
            return false;
        }
        List<ModelBoxCapture.Box> boxes = this.dropSameTextureOverlays(captured);
        if (boxes.isEmpty()) {
            return false;
        }
        float cubeSize = class_3532.method_15363((float)settings.cubeSize(), (float)0.02f, (float)0.25f);
        int prospective = 0;
        for (ModelBoxCapture.Box box : boxes) {
            prospective += this.surfaceCellCount(box, cubeSize);
        }
        if (prospective <= 0) {
            return false;
        }
        int budget = Math.min(2800, 6500 - this.totalCubes());
        if (budget < 40) {
            return false;
        }
        float keepChance = Math.min(1.0f, (float)budget / (float)prospective);
        float impulse = Math.max(0.0f, settings.impulse());
        ArrayList<class_2960> textures = new ArrayList<class_2960>();
        ArrayList<float[]> texSizes = new ArrayList<float[]>();
        ArrayList<ShellQuad> shell = new ArrayList<ShellQuad>();
        for (ModelBoxCapture.Box box : boxes) {
            int texIndex = textures.indexOf(box.texture());
            if (texIndex < 0) {
                texIndex = textures.size();
                textures.add(box.texture());
                texSizes.add(this.textureSize(box.texture()));
            }
            this.appendShell(box, texIndex, shell);
        }
        ArrayList<CollapseCube> cubes = new ArrayList<CollapseCube>(Math.min(budget, prospective));
        for (ModelBoxCapture.Box box : boxes) {
            int texIndex = textures.indexOf(box.texture());
            if (texIndex < 0) continue;
            float[] texSize = (float[])texSizes.get(texIndex);
            this.voxelizeBox(box, texIndex, texSize[0], texSize[1], cubeSize, keepChance, originX, originY, originZ, centerY, motionX, motionY, motionZ, impulse, cubes);
            if (cubes.size() < budget) continue;
            break;
        }
        if (cubes.isEmpty()) {
            return false;
        }
        while (cubes.size() > budget) {
            cubes.remove(cubes.size() - 1);
        }
        cubes.sort(Comparator.comparingInt(c -> c.texIndex));
        float f = Float.MAX_VALUE;
        float maxY = -3.4028235E38f;
        for (ShellQuad quad : shell) {
            f = Math.min(f, quad.minY);
            maxY = Math.max(maxY, quad.maxY);
        }
        if (shell.isEmpty()) {
            for (CollapseCube cube : cubes) {
                float local = (float)(cube.y - originY);
                f = Math.min(f, local);
                maxY = Math.max(maxY, local);
            }
        }
        float span = Math.max(1.0E-4f, maxY - f);
        long waveMs = class_3532.method_53062((long)settings.waveMs(), (long)0L, (long)3000L);
        for (CollapseCube cube : cubes) {
            float frac = class_3532.method_15363((float)(((float)(cube.y - originY) - f) / span), (float)0.0f, (float)1.0f);
            if (settings.topDown()) {
                frac = 1.0f - frac;
            }
            float jitter = (this.random.nextFloat() - 0.5f) * 0.06f;
            cube.releaseMs = Math.round(class_3532.method_15363((float)(frac + jitter), (float)0.0f, (float)1.0f) * (float)waveMs);
        }
        Pile pile = new Pile(entityId, System.currentTimeMillis(), Math.max(500L, settings.lifeMs()), originX, originY, originZ, textures, cubes, shell, f, maxY, waveMs, settings.topDown());
        pile.frozen = frozen;
        this.piles.add(pile);
        return true;
    }

    private void appendShell(ModelBoxCapture.Box box, int texIndex, List<ShellQuad> out) {
        int n = box.quadCount();
        float[] verts = box.verts();
        float[] uvs = box.uvs();
        for (int q = 0; q < n; ++q) {
            int o = q * 12;
            float[] pos = new float[12];
            for (int i = 0; i < 4; ++i) {
                pos[i * 3] = box.cx() + verts[o + i * 3];
                pos[i * 3 + 1] = box.cy() + verts[o + i * 3 + 1];
                pos[i * 3 + 2] = box.cz() + verts[o + i * 3 + 2];
            }
            float[] uv = new float[8];
            for (int i = 0; i < 8; ++i) {
                uv[i] = uvs[q * 8 + i];
            }
            float e1y = pos[4] - pos[1];
            float e2z = pos[11] - pos[2];
            float e1z = pos[5] - pos[2];
            float e2y = pos[10] - pos[1];
            float nx = e1y * e2z - e1z * e2y;
            float e2x = pos[9] - pos[0];
            float e1x = pos[3] - pos[0];
            float ny = e1z * e2x - e1x * e2z;
            float nz = e1x * e2y - e1y * e2x;
            float len = (float)Math.sqrt(nx * nx + ny * ny + nz * nz);
            if (len < 1.0E-6f) {
                nx = 0.0f;
                ny = 1.0f;
                nz = 0.0f;
            } else {
                nx /= len;
                ny /= len;
                nz /= len;
            }
            out.add(new ShellQuad(texIndex, pos, uv, box.colors()[q], box.lights()[q], box.overlays()[q], nx, ny, nz));
        }
    }

    public void renderAndStep(EventRender3D event, Settings settings) {
        class_310 client = class_310.method_1551();
        class_638 level = client.field_1687;
        if (level == null) {
            this.clear();
            return;
        }
        if (this.piles.isEmpty()) {
            this.lastFrameNs = 0L;
            return;
        }
        long now = System.currentTimeMillis();
        long nowNs = System.nanoTime();
        float dt = this.lastFrameNs == 0L ? 0.0f : (float)((double)(nowNs - this.lastFrameNs) / 1.0E9);
        this.lastFrameNs = nowNs;
        dt = class_3532.method_15363((float)dt, (float)0.0f, (float)0.1f);
        for (int i = this.piles.size() - 1; i >= 0; --i) {
            Pile pile = this.piles.get(i);
            if (pile.frozen || now - pile.spawnMs <= pile.lifeMs && !pile.cubes.isEmpty()) continue;
            this.piles.remove(i);
        }
        if (this.piles.isEmpty()) {
            return;
        }
        if (dt > 0.0f) {
            int steps = class_3532.method_15340((int)((int)Math.ceil(dt / 0.016666668f)), (int)1, (int)4);
            float h = dt / (float)steps;
            float restitution = class_3532.method_15363((float)settings.restitution(), (float)0.0f, (float)0.9f);
            double voidY = (double)level.method_31607() - 24.0;
            for (Pile pile : this.piles) {
                if (pile.frozen) continue;
                long age = now - pile.spawnMs;
                for (int step = 0; step < steps; ++step) {
                    this.stepPile(level, pile, h, restitution, voidY, age);
                }
                pile.cubes.removeIf(cube -> cube.dead);
            }
        }
        this.render(event, now);
    }

    private void stepPile(class_638 level, Pile pile, float h, float restitution, double voidY, long ageMs) {
        for (CollapseCube cube : pile.cubes) {
            double ny;
            if (cube.sleeping || cube.dead) continue;
            if (!cube.released) {
                if (ageMs < cube.releaseMs) continue;
                cube.released = true;
                for (int escape = 0; escape < 8 && this.collides(level, cube.x, cube.y, cube.z, cube.half); ++escape) {
                    cube.y += 0.06;
                }
            }
            cube.vy -= 12.5f * h;
            float drag = 1.0f - 0.85f * h;
            cube.vx *= drag;
            cube.vy *= drag;
            cube.vz *= drag;
            double half = cube.half;
            boolean grounded = false;
            double nx = cube.x + (double)(cube.vx * h);
            if (cube.vx != 0.0f) {
                if (this.collides(level, nx, cube.y, cube.z, half) && !this.collides(level, cube.x, cube.y, cube.z, half)) {
                    cube.vx = -cube.vx * restitution * 0.7f;
                } else {
                    cube.x = nx;
                }
            }
            double nz = cube.z + (double)(cube.vz * h);
            if (cube.vz != 0.0f) {
                if (this.collides(level, cube.x, cube.y, nz, half) && !this.collides(level, cube.x, cube.y, cube.z, half)) {
                    cube.vz = -cube.vz * restitution * 0.7f;
                } else {
                    cube.z = nz;
                }
            }
            if (this.collides(level, cube.x, ny = cube.y + (double)(cube.vy * h), cube.z, half) && !this.collides(level, cube.x, cube.y, cube.z, half)) {
                if (cube.vy < 0.0f) {
                    grounded = true;
                    float bounced = -cube.vy * restitution;
                    cube.vy = bounced > 0.55f ? bounced : 0.0f;
                    cube.vx *= 0.62f;
                    cube.vz *= 0.62f;
                    cube.angVel *= 0.55f;
                } else {
                    cube.vy = -cube.vy * 0.25f;
                }
            } else {
                cube.y = ny;
            }
            cube.angle += cube.angVel * h;
            float speedSq = cube.vx * cube.vx + cube.vy * cube.vy + cube.vz * cube.vz;
            if (grounded && speedSq < 0.09f) {
                ++cube.restSteps;
                if (cube.restSteps > 6) {
                    cube.sleeping = true;
                    cube.angVel = 0.0f;
                    cube.vx = 0.0f;
                    cube.vy = 0.0f;
                    cube.vz = 0.0f;
                }
            } else {
                cube.restSteps = 0;
            }
            if (!(cube.y < voidY)) continue;
            cube.dead = true;
        }
    }

    private boolean collides(class_638 level, double x, double y, double z, double half) {
        double minX = x - half;
        double minY = y - half;
        double minZ = z - half;
        double maxX = x + half;
        double maxY = y + half;
        double maxZ = z + half;
        int x0 = class_3532.method_15357((double)minX);
        int x1 = class_3532.method_15357((double)maxX);
        int y0 = class_3532.method_15357((double)minY);
        int y1 = class_3532.method_15357((double)maxY);
        int z0 = class_3532.method_15357((double)minZ);
        int z1 = class_3532.method_15357((double)maxZ);
        for (int bx = x0; bx <= x1; ++bx) {
            for (int by = y0; by <= y1; ++by) {
                for (int bz = z0; bz <= z1; ++bz) {
                    class_265 shape;
                    this.blockPos.method_10103(bx, by, bz);
                    class_2680 state = level.method_8320((class_2338)this.blockPos);
                    if (state.method_26215() || (shape = state.method_26220((class_1922)level, (class_2338)this.blockPos)).method_1110()) continue;
                    class_238 bounds = shape.method_1107();
                    if (!(minX < (double)bx + bounds.field_1320) || !(maxX > (double)bx + bounds.field_1323) || !(minY < (double)by + bounds.field_1325) || !(maxY > (double)by + bounds.field_1322) || !(minZ < (double)bz + bounds.field_1324) || !(maxZ > (double)bz + bounds.field_1321)) continue;
                    return true;
                }
            }
        }
        return false;
    }

    private void render(EventRender3D event, long now) {
        class_4184 camera;
        class_310 client = class_310.method_1551();
        class_4184 class_41842 = camera = client.field_1773 == null ? null : client.field_1773.method_19418();
        if (camera == null || event.getMatrixStack() == null) {
            return;
        }
        class_243 cam = camera.method_71156();
        class_4587.class_4665 pose = event.getMatrixStack().method_23760();
        class_4597.class_4598 provider = client.method_22940().method_23000();
        for (Pile pile : this.piles) {
            float scale;
            long shrinkStart;
            long age = pile.frozen ? 0L : now - pile.spawnMs;
            if (age <= (shrinkStart = pile.lifeMs - 600L)) {
                scale = 1.0f;
            } else {
                float t = class_3532.method_15363((float)((float)(age - shrinkStart) / 600.0f), (float)0.0f, (float)1.0f);
                scale = 1.0f - t * t * (3.0f - 2.0f * t);
            }
            if (scale <= 0.02f) continue;
            boolean useOverlay = now - pile.createdMs < 450L;
            float span = Math.max(1.0E-4f, pile.shellMaxY - pile.shellMinY);
            float progress = pile.frozen ? 0.0f : (pile.waveMs <= 0L ? 1.0f : class_3532.method_15363((float)((float)age / (float)pile.waveMs), (float)0.0f, (float)1.0f));
            float frontLocal = pile.topDown ? pile.shellMaxY - progress * span : pile.shellMinY + progress * span;
            double frontWorld = pile.originY + (double)frontLocal;
            boolean keepAbove = !pile.topDown;
            boolean shellAlive = progress < 1.0f && !pile.shell.isEmpty();
            int texCount = pile.textures.size();
            for (int texIndex = 0; texIndex < texCount; ++texIndex) {
                class_1921 layer = class_12249.method_75994((class_2960)pile.textures.get(texIndex));
                class_4588 consumer = provider.method_73477(layer);
                boolean drew = false;
                if (shellAlive) {
                    for (ShellQuad quad : pile.shell) {
                        if (quad.texIndex != texIndex || (!keepAbove ? quad.minY > frontLocal : quad.maxY < frontLocal) || !this.emitShell(consumer, pose, quad, pile, cam.field_1352, cam.field_1351, cam.field_1350, frontLocal, keepAbove, useOverlay)) continue;
                        drew = true;
                    }
                }
                for (CollapseCube cube : pile.cubes) {
                    if (cube.texIndex != texIndex || cube.dead) continue;
                    if (!cube.released && shellAlive) {
                        boolean shattered;
                        boolean bl = keepAbove ? cube.y <= frontWorld : (shattered = cube.y >= frontWorld);
                        if (!shattered) continue;
                    }
                    this.emitCube(consumer, pose, cube, cam.field_1352, cam.field_1351, cam.field_1350, scale, useOverlay);
                    drew = true;
                }
                if (!drew) continue;
                provider.method_22994(layer);
            }
        }
    }

    private boolean emitShell(class_4588 consumer, class_4587.class_4665 pose, ShellQuad quad, Pile pile, double camX, double camY, double camZ, float frontY, boolean keepAbove, boolean useOverlay) {
        int count = 0;
        for (int i = 0; i < 4; ++i) {
            boolean insideJ;
            boolean insideI;
            int j = i + 1 & 3;
            float xi = quad.pos[i * 3];
            float yi = quad.pos[i * 3 + 1];
            float zi = quad.pos[i * 3 + 2];
            float xj = quad.pos[j * 3];
            float yj = quad.pos[j * 3 + 1];
            float zj = quad.pos[j * 3 + 2];
            float ui = quad.uv[i * 2];
            float vi = quad.uv[i * 2 + 1];
            float uj = quad.uv[j * 2];
            float vj = quad.uv[j * 2 + 1];
            boolean bl = keepAbove ? yi >= frontY : (insideI = yi <= frontY);
            boolean bl2 = keepAbove ? yj >= frontY : (insideJ = yj <= frontY);
            if (insideI) {
                int b = count * 5;
                ModelCollapseSimulation.CLIP[b] = xi;
                ModelCollapseSimulation.CLIP[b + 1] = yi;
                ModelCollapseSimulation.CLIP[b + 2] = zi;
                ModelCollapseSimulation.CLIP[b + 3] = ui;
                ModelCollapseSimulation.CLIP[b + 4] = vi;
                ++count;
            }
            if (insideI == insideJ) continue;
            float denom = yj - yi;
            float t = Math.abs(denom) < 1.0E-6f ? 0.0f : class_3532.method_15363((float)((frontY - yi) / denom), (float)0.0f, (float)1.0f);
            int b = count * 5;
            ModelCollapseSimulation.CLIP[b] = xi + (xj - xi) * t;
            ModelCollapseSimulation.CLIP[b + 1] = yi + (yj - yi) * t;
            ModelCollapseSimulation.CLIP[b + 2] = zi + (zj - zi) * t;
            ModelCollapseSimulation.CLIP[b + 3] = ui + (uj - ui) * t;
            ModelCollapseSimulation.CLIP[b + 4] = vi + (vj - vi) * t;
            ++count;
        }
        if (count < 3) {
            return false;
        }
        int overlay = useOverlay ? quad.overlay : class_4608.field_21444;
        Matrix4f matrix = pose.method_23761();
        pose.method_56820(quad.nx, quad.ny, quad.nz, NORMAL_SCRATCH);
        float tnx = ModelCollapseSimulation.NORMAL_SCRATCH.x;
        float tny = ModelCollapseSimulation.NORMAL_SCRATCH.y;
        float tnz = ModelCollapseSimulation.NORMAL_SCRATCH.z;
        int n = count - 1;
        for (int k = 1; k < n; ++k) {
            this.emitShellVertex(consumer, matrix, quad, pile, 0, camX, camY, camZ, overlay, tnx, tny, tnz);
            this.emitShellVertex(consumer, matrix, quad, pile, k, camX, camY, camZ, overlay, tnx, tny, tnz);
            this.emitShellVertex(consumer, matrix, quad, pile, k + 1, camX, camY, camZ, overlay, tnx, tny, tnz);
            this.emitShellVertex(consumer, matrix, quad, pile, k + 1, camX, camY, camZ, overlay, tnx, tny, tnz);
        }
        return true;
    }

    private void emitShellVertex(class_4588 consumer, Matrix4f matrix, ShellQuad quad, Pile pile, int index, double camX, double camY, double camZ, int overlay, float normalX, float normalY, float normalZ) {
        int b = index * 5;
        matrix.transformPosition((float)(pile.originX + (double)CLIP[b] - camX), (float)(pile.originY + (double)CLIP[b + 1] - camY), (float)(pile.originZ + (double)CLIP[b + 2] - camZ), POSITION_SCRATCH);
        consumer.method_22912(ModelCollapseSimulation.POSITION_SCRATCH.x, ModelCollapseSimulation.POSITION_SCRATCH.y, ModelCollapseSimulation.POSITION_SCRATCH.z).method_39415(quad.color).method_22913(CLIP[b + 3], CLIP[b + 4]).method_22922(overlay).method_60803(quad.light).method_22914(normalX, normalY, normalZ);
    }

    private void emitCube(class_4588 consumer, class_4587.class_4665 pose, CollapseCube cube, double camX, double camY, double camZ, float scale, boolean useOverlay) {
        float half = cube.half * scale;
        if (half < 0.003f) {
            return;
        }
        float c = (float)Math.cos(cube.angle);
        float s = (float)Math.sin(cube.angle);
        float t = 1.0f - c;
        float ax = cube.axX;
        float ay = cube.axY;
        float az = cube.axZ;
        float m00 = c + ax * ax * t;
        float m01 = ax * ay * t - az * s;
        float m02 = ax * az * t + ay * s;
        float m10 = ay * ax * t + az * s;
        float m11 = c + ay * ay * t;
        float m12 = ay * az * t - ax * s;
        float m20 = az * ax * t - ay * s;
        float m21 = az * ay * t + ax * s;
        float m22 = c + az * az * t;
        float bx = (float)(cube.x - camX);
        float by = (float)(cube.y - camY);
        float bz = (float)(cube.z - camZ);
        Matrix4f matrix = pose.method_23761();
        for (int i = 0; i < 8; ++i) {
            float lx = (i & 1) != 0 ? half : -half;
            float ly = (i & 2) != 0 ? half : -half;
            float lz = (i & 4) != 0 ? half : -half;
            matrix.transformPosition(bx + m00 * lx + m01 * ly + m02 * lz, by + m10 * lx + m11 * ly + m12 * lz, bz + m20 * lx + m21 * ly + m22 * lz, POSITION_SCRATCH);
            ModelCollapseSimulation.CORNERS_X[i] = ModelCollapseSimulation.POSITION_SCRATCH.x;
            ModelCollapseSimulation.CORNERS_Y[i] = ModelCollapseSimulation.POSITION_SCRATCH.y;
            ModelCollapseSimulation.CORNERS_Z[i] = ModelCollapseSimulation.POSITION_SCRATCH.z;
        }
        int overlay = useOverlay ? cube.overlay : class_4608.field_21444;
        for (int face = 0; face < 6; ++face) {
            int axis = face >> 1;
            float sign = (face & 1) == 0 ? 1.0f : -1.0f;
            switch (axis) {
                case 0: {
                    float nx = m00;
                    float ny = m10;
                    float nz = m20;
                    break;
                }
                case 1: {
                    float nx = m01;
                    float ny = m11;
                    float nz = m21;
                    break;
                }
                default: {
                    float nx = m02;
                    float ny = m12;
                    float nz = m22;
                }
            }
            pose.method_56820(nx *= sign, ny *= sign, nz *= sign, NORMAL_SCRATCH);
            float tnx = ModelCollapseSimulation.NORMAL_SCRATCH.x;
            float tny = ModelCollapseSimulation.NORMAL_SCRATCH.y;
            float tnz = ModelCollapseSimulation.NORMAL_SCRATCH.z;
            int base = face * 4;
            for (int v = 0; v < 4; ++v) {
                int corner = FACES[base + v];
                consumer.method_22912(CORNERS_X[corner], CORNERS_Y[corner], CORNERS_Z[corner]).method_39415(cube.color).method_22913(cube.u, cube.v).method_22922(overlay).method_60803(cube.light).method_22914(tnx, tny, tnz);
            }
        }
    }

    private int surfaceCellCount(ModelBoxCapture.Box box, float cubeSize) {
        int na = this.gridCount(box.half()[0], cubeSize);
        int nb = this.gridCount(box.half()[1], cubeSize);
        int nn = this.gridCount(box.half()[2], cubeSize);
        int total = na * nb * nn;
        if (na <= 2 || nb <= 2 || nn <= 2) {
            return total;
        }
        return total - (na - 2) * (nb - 2) * (nn - 2);
    }

    private int gridCount(float half, float cubeSize) {
        return class_3532.method_15340((int)Math.round(half * 2.0f / cubeSize), (int)1, (int)14);
    }

    private float[] textureSize(class_2960 texture) {
        try {
            GpuTexture gpu = class_310.method_1551().method_1531().method_4619(texture).method_68004();
            return new float[]{gpu.getWidth(0), gpu.getHeight(0)};
        }
        catch (Throwable ignored) {
            return new float[]{0.0f, 0.0f};
        }
    }

    private float snapToTexel(float value, float size) {
        if (size < 1.0f) {
            return value;
        }
        int texel = class_3532.method_15340((int)class_3532.method_15375((float)(value * size)), (int)0, (int)(class_3532.method_15375((float)size) - 1));
        return ((float)texel + 0.5f) / size;
    }

    private void voxelizeBox(ModelBoxCapture.Box box, int texIndex, float texWidth, float texHeight, float cubeSize, float keepChance, double originX, double originY, double originZ, float centerY, float motionX, float motionY, float motionZ, float impulse, List<CollapseCube> out) {
        float[] axes = box.axes();
        float[] half = box.half();
        int na = this.gridCount(half[0], cubeSize);
        int nb = this.gridCount(half[1], cubeSize);
        int nn = this.gridCount(half[2], cubeSize);
        float stepA = half[0] * 2.0f / (float)na;
        float stepB = half[1] * 2.0f / (float)nb;
        float stepN = half[2] * 2.0f / (float)nn;
        float cellSize = Math.max(stepA, Math.max(stepB, stepN));
        for (int ia = 0; ia < na; ++ia) {
            for (int ib = 0; ib < nb; ++ib) {
                for (int ic = 0; ic < nn; ++ic) {
                    float rz;
                    float ry;
                    boolean surface;
                    boolean bl = surface = ia == 0 || ia == na - 1 || ib == 0 || ib == nb - 1 || ic == 0 || ic == nn - 1;
                    if (!surface || keepChance < 1.0f && this.random.nextFloat() > keepChance) continue;
                    float la = -half[0] + ((float)ia + 0.5f) * stepA;
                    float lb = -half[1] + ((float)ib + 0.5f) * stepB;
                    float ln = -half[2] + ((float)ic + 0.5f) * stepN;
                    float localX = axes[0] * la + axes[3] * lb + axes[6] * ln;
                    float localY = axes[1] * la + axes[4] * lb + axes[7] * ln;
                    float localZ = axes[2] * la + axes[5] * lb + axes[8] * ln;
                    float sa = la;
                    float sb = lb;
                    float sn = ln;
                    float da = half[0] - Math.abs(la);
                    float db = half[1] - Math.abs(lb);
                    float dn = half[2] - Math.abs(ln);
                    if (da <= db && da <= dn) {
                        sa = la >= 0.0f ? half[0] : -half[0];
                    } else if (db <= dn) {
                        sb = lb >= 0.0f ? half[1] : -half[1];
                    } else {
                        sn = ln >= 0.0f ? half[2] : -half[2];
                    }
                    float surfX = axes[0] * sa + axes[3] * sb + axes[6] * sn;
                    float surfY = axes[1] * sa + axes[4] * sb + axes[7] * sn;
                    float surfZ = axes[2] * sa + axes[5] * sb + axes[8] * sn;
                    int quad = this.sampleUv(box, surfX, surfY, surfZ, UV_SCRATCH);
                    double wx = originX + (double)box.cx() + (double)localX;
                    double wy = originY + (double)box.cy() + (double)localY;
                    double wz = originZ + (double)box.cz() + (double)localZ;
                    float dirX = box.cx() + localX;
                    float dirY = box.cy() + localY - centerY;
                    float dirZ = box.cz() + localZ;
                    float len = (float)Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
                    if (len < 1.0E-4f) {
                        dirX = this.random.nextFloat() - 0.5f;
                        dirY = this.random.nextFloat() * 0.6f;
                        dirZ = this.random.nextFloat() - 0.5f;
                        len = Math.max(1.0E-4f, (float)Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ));
                    }
                    float burst = impulse * (1.1f + this.random.nextFloat() * 2.1f);
                    float vx = (dirX /= len) * burst * 0.8f + motionX * 2.0f + (this.random.nextFloat() - 0.5f) * 0.5f * impulse;
                    float vy = (dirY /= len) * burst * 0.45f + impulse * (0.5f + this.random.nextFloat() * 1.3f) + (this.random.nextFloat() - 0.5f) * 0.3f;
                    float vz = (dirZ /= len) * burst * 0.8f + motionZ * 2.0f + (this.random.nextFloat() - 0.5f) * 0.5f * impulse;
                    float rx = this.random.nextFloat() * 2.0f - 1.0f;
                    float rl = (float)Math.sqrt(rx * rx + (ry = this.random.nextFloat() * 2.0f - 1.0f) * ry + (rz = this.random.nextFloat() * 2.0f - 1.0f) * rz);
                    if (rl < 1.0E-4f) {
                        rx = 0.0f;
                        ry = 1.0f;
                        rz = 0.0f;
                        rl = 1.0f;
                    }
                    out.add(new CollapseCube(wx, wy, wz, vx, vy + motionY, vz, cellSize * 0.5f * (0.98f + this.random.nextFloat() * 0.12f), this.snapToTexel(UV_SCRATCH[0], texWidth), this.snapToTexel(UV_SCRATCH[1], texHeight), box.colors()[quad], box.lights()[quad], box.overlays()[quad], texIndex, rx /= rl, ry /= rl, rz /= rl, this.random.nextFloat() * (float)Math.PI * 2.0f, (this.random.nextFloat() * 2.0f - 1.0f) * (3.0f + impulse * 5.0f)));
                }
            }
        }
    }

    private int sampleUv(ModelBoxCapture.Box box, float px, float py, float pz, float[] outUv) {
        float[] verts = box.verts();
        float[] uvs = box.uvs();
        float bestDist = Float.MAX_VALUE;
        int bestQuad = 0;
        float bestS = 0.5f;
        float bestT = 0.5f;
        int n = box.quadCount();
        for (int q = 0; q < n; ++q) {
            float cz;
            float ddz;
            float cy;
            float ddy;
            float t;
            float s;
            float cx;
            float ddx;
            float dist;
            int o = q * 12;
            float v0x = verts[o];
            float v0y = verts[o + 1];
            float v0z = verts[o + 2];
            float e1x = verts[o + 3] - v0x;
            float e1y = verts[o + 4] - v0y;
            float e1z = verts[o + 5] - v0z;
            float e2x = verts[o + 9] - v0x;
            float e2y = verts[o + 10] - v0y;
            float e2z = verts[o + 11] - v0z;
            float dx = px - v0x;
            float dy = py - v0y;
            float dz = pz - v0z;
            float len1 = e1x * e1x + e1y * e1y + e1z * e1z;
            float len2 = e2x * e2x + e2y * e2y + e2z * e2z;
            if (len1 < 1.0E-8f || len2 < 1.0E-8f || (dist = (ddx = px - (cx = v0x + e1x * (s = class_3532.method_15363((float)((dx * e1x + dy * e1y + dz * e1z) / len1), (float)0.0f, (float)1.0f)) + e2x * (t = class_3532.method_15363((float)((dx * e2x + dy * e2y + dz * e2z) / len2), (float)0.0f, (float)1.0f)))) * ddx + (ddy = py - (cy = v0y + e1y * s + e2y * t)) * ddy + (ddz = pz - (cz = v0z + e1z * s + e2z * t)) * ddz) >= bestDist) continue;
            bestDist = dist;
            bestQuad = q;
            bestS = s;
            bestT = t;
        }
        float u0 = uvs[bestQuad * 8];
        float v0 = uvs[bestQuad * 8 + 1];
        float u1 = uvs[bestQuad * 8 + 2];
        float v1 = uvs[bestQuad * 8 + 3];
        float u3 = uvs[bestQuad * 8 + 6];
        float v3 = uvs[bestQuad * 8 + 7];
        outUv[0] = u0 + (u1 - u0) * bestS + (u3 - u0) * bestT;
        outUv[1] = v0 + (v1 - v0) * bestS + (v3 - v0) * bestT;
        return bestQuad;
    }

    private List<ModelBoxCapture.Box> dropSameTextureOverlays(List<ModelBoxCapture.Box> boxes) {
        int size = boxes.size();
        if (size < 2) {
            return boxes;
        }
        ArrayList<ModelBoxCapture.Box> kept = new ArrayList<ModelBoxCapture.Box>(size);
        block0: for (int i = 0; i < size; ++i) {
            ModelBoxCapture.Box a = boxes.get(i);
            float[] ah = a.half();
            float va = ah[0] * ah[1] * ah[2];
            for (int j = 0; j < size; ++j) {
                float[] bh;
                float vb;
                float dz;
                float dy;
                float dx;
                if (i == j) continue;
                ModelBoxCapture.Box b = boxes.get(j);
                if (Objects.equals(a.texture(), b.texture()) && !((dx = a.cx() - b.cx()) * dx + (dy = a.cy() - b.cy()) * dy + (dz = a.cz() - b.cz()) * dz > 9.0E-4f) && va > (vb = (bh = b.half())[0] * bh[1] * bh[2]) * 1.05f) continue block0;
            }
            kept.add(a);
        }
        return kept;
    }

    @Environment(value=EnvType.CLIENT)
    private static final class Pile {
        final int entityId;
        long spawnMs;
        final long lifeMs;
        final double originX;
        final double originY;
        final double originZ;
        final List<class_2960> textures;
        final List<CollapseCube> cubes;
        final List<ShellQuad> shell;
        final float shellMinY;
        final float shellMaxY;
        final long waveMs;
        final boolean topDown;
        boolean frozen;
        final long createdMs = System.currentTimeMillis();

        Pile(int entityId, long spawnMs, long lifeMs, double originX, double originY, double originZ, List<class_2960> textures, List<CollapseCube> cubes, List<ShellQuad> shell, float shellMinY, float shellMaxY, long waveMs, boolean topDown) {
            this.entityId = entityId;
            this.spawnMs = spawnMs;
            this.lifeMs = lifeMs;
            this.originX = originX;
            this.originY = originY;
            this.originZ = originZ;
            this.textures = textures;
            this.cubes = cubes;
            this.shell = shell;
            this.shellMinY = shellMinY;
            this.shellMaxY = shellMaxY;
            this.waveMs = waveMs;
            this.topDown = topDown;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public record Settings(float cubeSize, float impulse, long lifeMs, float restitution, long waveMs, boolean topDown) {
    }

    @Environment(value=EnvType.CLIENT)
    private static final class ShellQuad {
        final int texIndex;
        final float[] pos;
        final float[] uv;
        final int color;
        final int light;
        final int overlay;
        final float nx;
        final float ny;
        final float nz;
        final float minY;
        final float maxY;

        ShellQuad(int texIndex, float[] pos, float[] uv, int color, int light, int overlay, float nx, float ny, float nz) {
            this.texIndex = texIndex;
            this.pos = pos;
            this.uv = uv;
            this.color = color;
            this.light = light;
            this.overlay = overlay;
            this.nx = nx;
            this.ny = ny;
            this.nz = nz;
            float lo = Float.MAX_VALUE;
            float hi = -3.4028235E38f;
            for (int i = 0; i < 4; ++i) {
                float y = pos[i * 3 + 1];
                lo = Math.min(lo, y);
                hi = Math.max(hi, y);
            }
            this.minY = lo;
            this.maxY = hi;
        }
    }

    @Environment(value=EnvType.CLIENT)
    private static final class CollapseCube {
        double x;
        double y;
        double z;
        float vx;
        float vy;
        float vz;
        final float half;
        final float u;
        final float v;
        final int color;
        final int light;
        final int overlay;
        final int texIndex;
        final float axX;
        final float axY;
        final float axZ;
        float angle;
        float angVel;
        long releaseMs;
        boolean released;
        boolean dead;
        boolean sleeping;
        int restSteps;

        CollapseCube(double x, double y, double z, float vx, float vy, float vz, float half, float u, float v, int color, int light, int overlay, int texIndex, float axX, float axY, float axZ, float angle, float angVel) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.vx = vx;
            this.vy = vy;
            this.vz = vz;
            this.half = half;
            this.u = u;
            this.v = v;
            this.color = color;
            this.light = light;
            this.overlay = overlay;
            this.texIndex = texIndex;
            this.axX = axX;
            this.axY = axY;
            this.axZ = axZ;
            this.angle = angle;
            this.angVel = angVel;
        }
    }
}

