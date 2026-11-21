/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  com.mojang.blaze3d.vertex.VertexFormatElement
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 *  org.joml.Vector3f
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.cosmetics.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.cosmetics.CosmeticManager;
import ru.decide.cosmetics.custompet.CustomPetCosmetics;
import ru.decide.cosmetics.custompet.CustomPetPreviews;
import ru.decide.cosmetics.geckolib.GeckolibCosmeticRenderer;
import ru.decide.cosmetics.geo.GeoBone;
import ru.decide.cosmetics.geo.GeoCube;
import ru.decide.cosmetics.geo.GeoModel;
import ru.decide.cosmetics.geo.GeoQuad;
import ru.decide.cosmetics.geo.GeoVertex;
import ru.decide.cosmetics.itemskins.ItemSkin;
import ru.decide.cosmetics.itemskins.ItemSkinGuiPreview;
import ru.decide.cosmetics.itemskins.ItemSkinRegistry;
import ru.decide.cosmetics.maksai.MaksaiCosmetics;
import ru.decide.cosmetics.melee.MeleeGuiPreview;
import ru.decide.cosmetics.melee.MeleeRegistry;
import ru.decide.cosmetics.melee.MeleeSkin;
import ru.decide.cosmetics.model.CosmeticModel;
import ru.decide.utils.render.others.RenderSampler;

@Environment(value=EnvType.CLIENT)
public final class CosmeticGuiPreview {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"DecideVisuals:CosmeticPreview");
    private static final class_2960 SHADER = class_2960.method_60655((String)"decide", (String)"post/cosmeticpreview/preview");
    private static final float MELEE_FIT_FACTOR = 0.9f;
    private static final float MELEE_YAW_OFFSET = -45.0f;
    private static final float MELEE_ROLL = 40.0f;
    private static final float MELEE_TILT = -22.0f;
    private static final VertexFormat VERTEX_FORMAT = VertexFormat.builder().add("Position", VertexFormatElement.POSITION).add("UV0", VertexFormatElement.UV0).add("Color", VertexFormatElement.COLOR).build();
    private static final RenderPipeline PIPELINE = RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[0]).withLocation(class_2960.method_60655((String)"decide", (String)"pipeline/cosmeticpreview")).withVertexShader(SHADER).withFragmentShader(SHADER).withVertexFormat(VERTEX_FORMAT, VertexFormat.class_5596.field_27379).withSampler("uGui").withBlend(BlendFunction.TRANSLUCENT).withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST).withDepthWrite(false).withCull(false).build();
    private static final float ROTATION_SPEED = 28.0f;
    private static final int MAX_PENDING = 32;
    private static final int FLOATS_PER_VERTEX = 5;
    private static final int BYTES_PER_VERTEX = 24;
    private static final int MAX_VERTICES = 200000;
    private static final long LOG_INTERVAL_MS = 2000L;
    private static final class_310 MC = class_310.method_1551();
    private static final List<PendingPreview> PENDING = new ArrayList<PendingPreview>();
    private static GpuBuffer vertexBuffer;
    private static ByteBuffer vertexData;
    private static long lastLogTime;
    private static final Vector3f POSITION;

    public static void clear() {
        PENDING.clear();
    }

    public static void queuePreview(CosmeticManager.CosmeticEntry entry, float x, float y, float w, float h, float clipX, float clipY, float clipW, float clipH) {
        if (entry == null || w <= 0.0f || h <= 0.0f || PENDING.size() >= 32) {
            return;
        }
        PENDING.add(new PendingPreview(entry, x, y, w, h, clipX, clipY, clipW, clipH));
    }

    public static void renderPending() {
        if (PENDING.isEmpty()) {
            return;
        }
        float rotation = (float)(System.currentTimeMillis() % 360000L) / 1000.0f * 28.0f;
        int queued = PENDING.size();
        int drawn = 0;
        Throwable firstError = null;
        String info = "";
        for (PendingPreview pending : PENDING) {
            try {
                String result;
                String string = result = pending.entry().isCape ? CosmeticGuiPreview.drawCape(pending, rotation) : CosmeticGuiPreview.drawModel(pending, rotation);
                if (drawn == 0) {
                    info = result;
                }
                ++drawn;
            }
            catch (Throwable t) {
                if (firstError != null) continue;
                firstError = t;
            }
        }
        PENDING.clear();
        long now = System.currentTimeMillis();
        if (now - lastLogTime > 2000L) {
            lastLogTime = now;
            LOGGER.info("[cosmetic-preview] queued={} drawn={} first={}", new Object[]{queued, drawn, info});
            if (firstError != null) {
                LOGGER.error("[cosmetic-preview] first error", firstError);
            }
        }
    }

    private static String drawModel(PendingPreview pending, float rotationDeg) {
        String maksai;
        String melee;
        String item;
        CosmeticManager.CosmeticEntry entry = pending.entry();
        ItemSkin itemSkin = ItemSkinRegistry.byIndex(entry.index);
        if (itemSkin != null && ItemSkinGuiPreview.available(itemSkin) && (item = CosmeticGuiPreview.drawItemSkin(entry, itemSkin, pending, rotationDeg)) != null) {
            return item;
        }
        MeleeSkin meleeSkin = MeleeRegistry.byIndex(entry.index);
        if (meleeSkin != null && (melee = CosmeticGuiPreview.drawMelee(entry, meleeSkin, pending, rotationDeg)) != null) {
            return melee;
        }
        if (MaksaiCosmetics.isMaksaiEntry(entry.index) && (maksai = CosmeticGuiPreview.drawMaksaiMesh(entry, pending, rotationDeg)) != null) {
            return maksai;
        }
        CustomPetCosmetics.Spec pet = CustomPetCosmetics.specForIndex(entry.index);
        if (pet != null) {
            GeoModel petModel = CustomPetPreviews.model(pet);
            if (petModel != null) {
                return CosmeticGuiPreview.drawGeoModel(entry.name, petModel, pet.texture(), pending, rotationDeg, 0.92f);
            }
            return CosmeticGuiPreview.drawFlat(entry, pending);
        }
        CosmeticModel cosmetic = CosmeticManager.getInstance().getModelForEntry(entry);
        if (cosmetic == null || cosmetic.getTextureId() == null) {
            return CosmeticGuiPreview.drawFlat(entry, pending);
        }
        GeckolibCosmeticRenderer renderer = GeckolibCosmeticRenderer.getInstance();
        GeoModel model = renderer.getOrParseModel(cosmetic);
        if (model == null) {
            return entry.name + ": geo parse failed";
        }
        renderer.applyPose(cosmetic, model);
        return CosmeticGuiPreview.drawGeoModel(entry.name, model, cosmetic.getTextureId(), pending, rotationDeg, 0.62f);
    }

    private static String drawItemSkin(CosmeticManager.CosmeticEntry entry, ItemSkin skin, PendingPreview pending, float rotationDeg) {
        float[] bounds = ItemSkinGuiPreview.bounds(skin);
        if (bounds == null) {
            return null;
        }
        float guiUnit = CosmeticGuiPreview.guiUnit();
        float centerX = (pending.x() + pending.w() * 0.5f) * guiUnit;
        float bob = (float)Math.sin((double)System.currentTimeMillis() / 520.0) * 0.035f * Math.min(pending.w(), pending.h());
        float centerY = (pending.y() + pending.h() * 0.5f + bob) * guiUnit;
        float fbW = MC.method_22683().method_4489();
        float fbH = MC.method_22683().method_4506();
        float fit = Math.min(pending.w(), pending.h()) * 0.82f / bounds[3];
        Matrix4fStack stack = new Matrix4fStack(32);
        stack.pushMatrix();
        stack.translate(centerX, centerY, 0.0f);
        stack.scale(1.0f, -1.0f, 1.0f);
        if (skin.getKind() == ItemSkin.Kind.CROSSBOW) {
            stack.rotateX((float)Math.toRadians(-90.0));
        }
        stack.rotateY((float)Math.toRadians(rotationDeg + 28.0f));
        stack.rotateZ((float)Math.toRadians(-14.0));
        stack.rotateX((float)Math.toRadians(12.0));
        stack.scale(fit, fit, fit);
        stack.translate(-bounds[0], -bounds[1], -bounds[2]);
        Matrix4f base = new Matrix4f((Matrix4fc)stack);
        stack.popMatrix();
        ByteBuffer buffer = CosmeticGuiPreview.beginWrite();
        int[] count = new int[]{0};
        ItemSkinGuiPreview.emit(skin, base, (x, y, z, u, v) -> {
            int offset = buffer.position();
            if (offset + 24 > buffer.capacity()) {
                return;
            }
            buffer.putFloat(x / fbW).putFloat(y / fbH).putFloat(0.0f).putFloat(u).putFloat(v);
            buffer.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)-1);
            count[0] = count[0] + 1;
        });
        return CosmeticGuiPreview.finishDraw(entry.name + ": item3d verts=" + count[0], ItemSkinGuiPreview.texture(skin), count[0], pending.clipX(), pending.clipY(), pending.clipW(), pending.clipH());
    }

    private static String drawMelee(CosmeticManager.CosmeticEntry entry, MeleeSkin skin, PendingPreview pending, float rotationDeg) {
        if (!MeleeGuiPreview.available(skin)) {
            return null;
        }
        float[] bounds = MeleeGuiPreview.bounds(skin);
        if (bounds == null) {
            return CosmeticGuiPreview.drawFlat(entry, pending);
        }
        float guiUnit = CosmeticGuiPreview.guiUnit();
        float centerX = (pending.x() + pending.w() * 0.5f) * guiUnit;
        float centerY = (pending.y() + pending.h() * 0.5f) * guiUnit;
        float fbW = MC.method_22683().method_4489();
        float fbH = MC.method_22683().method_4506();
        float fit = Math.min(pending.w(), pending.h()) * 0.9f / bounds[3];
        Matrix4fStack stack = new Matrix4fStack(32);
        stack.pushMatrix();
        stack.translate(centerX, centerY, 0.0f);
        stack.scale(1.0f, -1.0f, 1.0f);
        stack.rotateY((float)Math.toRadians(rotationDeg + -45.0f));
        stack.rotateZ((float)Math.toRadians(40.0));
        stack.rotateX((float)Math.toRadians(-22.0));
        stack.scale(fit, fit, fit);
        stack.translate(-bounds[0], -bounds[1], -bounds[2]);
        Matrix4f base = new Matrix4f((Matrix4fc)stack);
        stack.popMatrix();
        ByteBuffer buffer = CosmeticGuiPreview.beginWrite();
        int[] count = new int[]{0};
        MeleeGuiPreview.emit(skin, base, (x, y, z, u, v) -> {
            int offset = buffer.position();
            if (offset + 24 > buffer.capacity()) {
                return;
            }
            buffer.putFloat(x / fbW).putFloat(y / fbH).putFloat(0.0f).putFloat(u).putFloat(v);
            buffer.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)-1);
            count[0] = count[0] + 1;
        });
        return CosmeticGuiPreview.finishDraw(entry.name + ": melee verts=" + count[0], skin.getTexture(), count[0], pending.clipX(), pending.clipY(), pending.clipW(), pending.clipH());
    }

    private static String drawGeoModel(String label, GeoModel model, class_2960 texture, PendingPreview pending, float rotationDeg, float fitFactor) {
        model.computeBounds();
        float maxDim = Math.max(0.05f, model.maxDimension);
        float fit = Math.min(pending.w(), pending.h()) * fitFactor / maxDim;
        ByteBuffer buffer = CosmeticGuiPreview.beginWrite();
        Matrix4fStack stack = new Matrix4fStack(32);
        stack.pushMatrix();
        float guiUnit = CosmeticGuiPreview.guiUnit();
        float centerX = (pending.x() + pending.w() * 0.5f) * guiUnit;
        float centerY = (pending.y() + pending.h() * 0.5f) * guiUnit;
        stack.translate(centerX, centerY, 0.0f);
        stack.scale(1.0f, -1.0f, 1.0f);
        stack.rotateY((float)Math.toRadians(rotationDeg));
        stack.scale(fit, fit, fit);
        stack.translate(-model.centerX, -model.centerY, -model.centerZ);
        int vertices = 0;
        for (GeoBone bone : model.topLevelBones) {
            vertices += CosmeticGuiPreview.emitBone(bone, stack, buffer);
        }
        stack.popMatrix();
        return CosmeticGuiPreview.finishDraw(label + ": bones=" + model.topLevelBones.size() + " dim=" + CosmeticGuiPreview.fmt(maxDim) + " fit=" + CosmeticGuiPreview.fmt(fit) + " verts=" + vertices, texture, vertices, pending.clipX(), pending.clipY(), pending.clipW(), pending.clipH());
    }

    private static String drawFlat(CosmeticManager.CosmeticEntry entry, PendingPreview pending) {
        class_2960 texture = entry.getTexture();
        if (texture == null) {
            return entry.name + ": no texture";
        }
        float guiUnit = CosmeticGuiPreview.guiUnit();
        float centerX = (pending.x() + pending.w() * 0.5f) * guiUnit;
        float centerY = (pending.y() + pending.h() * 0.5f) * guiUnit;
        float fbW = MC.method_22683().method_4489();
        float fbH = MC.method_22683().method_4506();
        float size = Math.min(pending.w(), pending.h()) * 0.72f * guiUnit;
        ByteBuffer buffer = CosmeticGuiPreview.beginWrite();
        int vertices = CosmeticGuiPreview.writeScreenQuad(buffer, centerX - size * 0.5f, centerY - size * 0.5f, centerX + size * 0.5f, centerY + size * 0.5f, fbW, fbH, 0.0f, 0.0f, 1.0f, 1.0f);
        return CosmeticGuiPreview.finishDraw(entry.name + ": flat", texture, vertices, pending.clipX(), pending.clipY(), pending.clipW(), pending.clipH());
    }

    private static String drawMaksaiMesh(CosmeticManager.CosmeticEntry entry, PendingPreview pending, float rotationDeg) {
        MaksaiCosmetics.Mesh mesh = MaksaiCosmetics.mesh(entry.index);
        if (mesh == null || mesh.vertices().length < 32) {
            return null;
        }
        float[] verts = mesh.vertices();
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = -3.4028235E38f;
        float maxY = -3.4028235E38f;
        float maxZ = -3.4028235E38f;
        for (int i = 0; i < verts.length; i += 8) {
            float x = verts[i];
            float y = verts[i + 1];
            float z = verts[i + 2];
            if (x < minX) {
                minX = x;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
            if (z < minZ) {
                minZ = z;
            }
            if (!(z > maxZ)) continue;
            maxZ = z;
        }
        float cx = (minX + maxX) * 0.5f;
        float cy = (minY + maxY) * 0.5f;
        float cz = (minZ + maxZ) * 0.5f;
        float maxDim = Math.max(0.01f, Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ)));
        float[] frame = CosmeticGuiPreview.resolveMeshFrame(mesh);
        int[] bones = mesh.boneIndices();
        float guiUnit = CosmeticGuiPreview.guiUnit();
        float centerX = (pending.x() + pending.w() * 0.5f) * guiUnit;
        float centerY = (pending.y() + pending.h() * 0.5f) * guiUnit;
        float fbW = MC.method_22683().method_4489();
        float fbH = MC.method_22683().method_4506();
        float fit = Math.min(pending.w(), pending.h()) * 0.62f / maxDim;
        ByteBuffer buffer = CosmeticGuiPreview.beginWrite();
        Matrix4fStack stack = new Matrix4fStack(32);
        stack.pushMatrix();
        stack.translate(centerX, centerY, 0.0f);
        stack.rotateY((float)Math.toRadians(rotationDeg));
        stack.scale(fit, fit, fit);
        stack.translate(-cx, -cy, -cz);
        Matrix4f matrix = new Matrix4f((Matrix4fc)stack);
        stack.popMatrix();
        int written = 0;
        int count = verts.length / 8;
        int vi = 0;
        while (vi + 3 < count) {
            written += CosmeticGuiPreview.emitMeshTriangle(verts, bones, frame, vi, vi + 1, vi + 2, matrix, buffer, fbW, fbH);
            written += CosmeticGuiPreview.emitMeshTriangle(verts, bones, frame, vi, vi + 2, vi + 3, matrix, buffer, fbW, fbH);
            vi += 4;
        }
        return CosmeticGuiPreview.finishDraw(entry.name + ": maksai-mesh verts=" + written, mesh.texture(), written, pending.clipX(), pending.clipY(), pending.clipW(), pending.clipH());
    }

    private static float[] resolveMeshFrame(MaksaiCosmetics.Mesh mesh) {
        float[][] frames = mesh.frames();
        if (frames.length == 0 || mesh.fps() <= 0) {
            return null;
        }
        double clock = (double)System.currentTimeMillis() / 1000.0 * (double)mesh.fps();
        double wrapped = mesh.loop() ? clock % (double)frames.length : Math.min(clock, (double)(frames.length - 1));
        int index = (int)wrapped;
        float weight = (float)(wrapped - (double)index);
        float[] a = frames[index];
        float[] b = frames[mesh.loop() ? (index + 1) % frames.length : Math.min(index + 1, frames.length - 1)];
        float[] frame = new float[a.length];
        for (int i = 0; i < frame.length; ++i) {
            frame[i] = a[i] + (b[i] - a[i]) * weight;
        }
        return frame;
    }

    private static int emitMeshTriangle(float[] verts, int[] bones, float[] frame, int a, int b, int c, Matrix4f m, ByteBuffer buffer, float fbW, float fbH) {
        if (!CosmeticGuiPreview.writeMeshVertex(verts, bones, frame, a, m, buffer, fbW, fbH)) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeMeshVertex(verts, bones, frame, b, m, buffer, fbW, fbH)) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeMeshVertex(verts, bones, frame, c, m, buffer, fbW, fbH)) {
            return 0;
        }
        return 3;
    }

    private static boolean writeMeshVertex(float[] verts, int[] bones, float[] frame, int vi, Matrix4f m, ByteBuffer buffer, float fbW, float fbH) {
        int offset;
        int k;
        int o = vi * 8;
        float x = verts[o];
        float y = verts[o + 1];
        float z = verts[o + 2];
        float u = verts[o + 3];
        float v = verts[o + 4];
        if (frame != null && bones != null && vi < bones.length && (k = bones[vi] * 12) >= 0 && k + 11 < frame.length) {
            float px = frame[k] * x + frame[k + 1] * y + frame[k + 2] * z + frame[k + 3];
            float py = frame[k + 4] * x + frame[k + 5] * y + frame[k + 6] * z + frame[k + 7];
            float pz = frame[k + 8] * x + frame[k + 9] * y + frame[k + 10] * z + frame[k + 11];
            x = px;
            y = py;
            z = pz;
        }
        if ((offset = buffer.position()) + 24 > buffer.capacity()) {
            buffer.position(offset);
            return false;
        }
        POSITION.set(x, y, z);
        m.transformPosition(POSITION);
        float nx = CosmeticGuiPreview.POSITION.x / fbW;
        float ny = CosmeticGuiPreview.POSITION.y / fbH;
        if (nx < -0.5f || nx > 1.5f || ny < -0.5f || ny > 1.5f) {
            buffer.position(offset);
            return false;
        }
        buffer.putFloat(nx);
        buffer.putFloat(ny);
        buffer.putFloat(0.0f);
        buffer.putFloat(u);
        buffer.putFloat(v);
        buffer.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)-1);
        return true;
    }

    private static int emitBone(GeoBone bone, Matrix4fStack parent, ByteBuffer buffer) {
        if (bone.isHidden) {
            return 0;
        }
        parent.pushMatrix();
        CosmeticGuiPreview.applyBone(bone, parent);
        int written = 0;
        for (GeoCube cube : bone.childCubes) {
            written += CosmeticGuiPreview.emitCube(cube, parent, buffer);
        }
        for (GeoBone child : bone.childBones) {
            written += CosmeticGuiPreview.emitBone(child, parent, buffer);
        }
        parent.popMatrix();
        return written;
    }

    private static void applyBone(GeoBone bone, Matrix4fStack m) {
        m.translate(-bone.getPositionX() / 16.0f, bone.getPositionY() / 16.0f, bone.getPositionZ() / 16.0f);
        m.translate(bone.getPivotX() / 16.0f, bone.getPivotY() / 16.0f, bone.getPivotZ() / 16.0f);
        CosmeticGuiPreview.rotate(m, bone.getRotationX(), bone.getRotationY(), bone.getRotationZ());
        m.scale(bone.getScaleX(), bone.getScaleY(), bone.getScaleZ());
        m.translate(-bone.getPivotX() / 16.0f, -bone.getPivotY() / 16.0f, -bone.getPivotZ() / 16.0f);
    }

    private static int emitCube(GeoCube cube, Matrix4fStack parent, ByteBuffer buffer) {
        parent.pushMatrix();
        parent.translate(cube.pivot.getX() / 16.0f, cube.pivot.getY() / 16.0f, cube.pivot.getZ() / 16.0f);
        CosmeticGuiPreview.rotate(parent, cube.rotation.getX(), cube.rotation.getY(), cube.rotation.getZ());
        parent.translate(-cube.pivot.getX() / 16.0f, -cube.pivot.getY() / 16.0f, -cube.pivot.getZ() / 16.0f);
        Matrix4f m = new Matrix4f((Matrix4fc)parent);
        int written = 0;
        for (GeoQuad quad : cube.quads) {
            if (quad == null || quad.vertices == null || quad.vertices.length < 4) continue;
            written += CosmeticGuiPreview.emitTriangle(quad.vertices[0], quad.vertices[1], quad.vertices[2], m, buffer);
            written += CosmeticGuiPreview.emitTriangle(quad.vertices[0], quad.vertices[2], quad.vertices[3], m, buffer);
        }
        parent.popMatrix();
        return written;
    }

    private static void rotate(Matrix4fStack m, float rx, float ry, float rz) {
        if (rz != 0.0f) {
            m.rotate((Quaternionfc)new Quaternionf(0.0f, 0.0f, (float)Math.sin(rz / 2.0f), (float)Math.cos(rz / 2.0f)));
        }
        if (ry != 0.0f) {
            m.rotate((Quaternionfc)new Quaternionf(0.0f, (float)Math.sin(ry / 2.0f), 0.0f, (float)Math.cos(ry / 2.0f)));
        }
        if (rx != 0.0f) {
            m.rotate((Quaternionfc)new Quaternionf((float)Math.sin(rx / 2.0f), 0.0f, 0.0f, (float)Math.cos(rx / 2.0f)));
        }
    }

    private static int emitTriangle(GeoVertex a, GeoVertex b, GeoVertex c, Matrix4f m, ByteBuffer buffer) {
        float fbH;
        float fbW = MC.method_22683().method_4489();
        if (!CosmeticGuiPreview.writeVertex(a, m, buffer, fbW, fbH = (float)MC.method_22683().method_4506())) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeVertex(b, m, buffer, fbW, fbH)) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeVertex(c, m, buffer, fbW, fbH)) {
            return 0;
        }
        return 3;
    }

    private static boolean writeVertex(GeoVertex vertex, Matrix4f m, ByteBuffer buffer, float fbW, float fbH) {
        int offset = buffer.position();
        if (offset + 24 > buffer.capacity()) {
            buffer.position(offset);
            return false;
        }
        POSITION.set(vertex.position.getX(), vertex.position.getY(), vertex.position.getZ());
        m.transformPosition(POSITION);
        float nx = CosmeticGuiPreview.POSITION.x / fbW;
        float ny = CosmeticGuiPreview.POSITION.y / fbH;
        if (nx < -0.2f || nx > 1.2f || ny < -0.2f || ny > 1.2f) {
            buffer.position(offset);
            return false;
        }
        buffer.putFloat(nx);
        buffer.putFloat(ny);
        buffer.putFloat(0.0f);
        buffer.putFloat(vertex.textureU);
        buffer.putFloat(vertex.textureV);
        buffer.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)-1);
        return true;
    }

    private static String drawCape(PendingPreview pending, float rotationDeg) {
        CosmeticManager.CosmeticEntry entry = pending.entry();
        class_2960 texture = entry.getTexture();
        if (texture == null) {
            return entry.name + ": no texture";
        }
        CosmeticManager.CapeAnimationInfo anim = CosmeticManager.getInstance().getCapeAnimation(texture);
        int frame = anim.getCurrentFrame();
        float halfW = 0.3125f;
        float height = 1.0f;
        float fit = Math.min(pending.w(), pending.h()) * 0.55f;
        float guiUnit = CosmeticGuiPreview.guiUnit();
        float centerX = (pending.x() + pending.w() * 0.5f) * guiUnit;
        float centerY = (pending.y() + pending.h() * 0.5f) * guiUnit;
        float fbW = MC.method_22683().method_4489();
        float fbH = MC.method_22683().method_4506();
        float angle = (float)Math.toRadians(rotationDeg);
        float cos = (float)Math.cos(angle);
        float halfOnScreen = Math.max(0.02f, Math.abs(cos)) * halfW * fit;
        ByteBuffer buffer = CosmeticGuiPreview.beginWrite();
        int vertices = 0;
        vertices += CosmeticGuiPreview.writeScreenQuad(buffer, centerX - halfOnScreen, centerY - height * fit * 0.5f, centerX + halfOnScreen, centerY + height * fit * 0.5f, fbW, fbH, anim.getU(11.0f), anim.getV(0.0f, frame), anim.getU(1.0f), anim.getV(17.0f, frame));
        return CosmeticGuiPreview.finishDraw(entry.name + ": cape frame=" + frame, texture, vertices += CosmeticGuiPreview.writeScreenQuad(buffer, centerX - halfOnScreen, centerY - height * fit * 0.5f, centerX + halfOnScreen, centerY + height * fit * 0.5f, fbW, fbH, anim.getU(1.0f), anim.getV(17.0f, frame), anim.getU(11.0f), anim.getV(0.0f, frame)), pending.clipX(), pending.clipY(), pending.clipW(), pending.clipH());
    }

    private static int writeScreenQuad(ByteBuffer buffer, float x0, float y0, float x1, float y1, float fbW, float fbH, float u0, float v0, float u1, float v1) {
        float nx0 = x0 / fbW;
        float ny0 = y0 / fbH;
        float nx1 = x1 / fbW;
        float ny1 = y1 / fbH;
        if (!CosmeticGuiPreview.writeRaw(buffer, nx0, ny0, u0, v0)) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeRaw(buffer, nx1, ny0, u1, v0)) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeRaw(buffer, nx1, ny1, u1, v1)) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeRaw(buffer, nx0, ny0, u0, v0)) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeRaw(buffer, nx1, ny1, u1, v1)) {
            return 0;
        }
        if (!CosmeticGuiPreview.writeRaw(buffer, nx0, ny1, u0, v1)) {
            return 0;
        }
        return 6;
    }

    private static boolean writeRaw(ByteBuffer buffer, float nx, float ny, float u, float v) {
        int offset = buffer.position();
        if (offset + 24 > buffer.capacity()) {
            buffer.position(offset);
            return false;
        }
        buffer.putFloat(nx).putFloat(ny).putFloat(0.0f).putFloat(u).putFloat(v);
        buffer.put((byte)-1).put((byte)-1).put((byte)-1).put((byte)-1);
        return true;
    }

    private static ByteBuffer beginWrite() {
        CosmeticGuiPreview.ensureBuffer();
        vertexData.clear();
        return vertexData;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static String finishDraw(String info, class_2960 texture, int vertices, float clipX, float clipY, float clipW, float clipH) {
        if (vertices <= 0) {
            return info + " [no verts]";
        }
        CosmeticGuiPreview.ensureBuffer();
        vertexData.position(0);
        vertexData.limit(vertices * 24);
        try {
            GpuTextureView view = MC.method_1531().method_4619(texture).method_71659();
            CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
            encoder.writeToBuffer(vertexBuffer.slice(0L, (long)vertexData.remaining()), vertexData);
            try (RenderPass pass = encoder.createRenderPass(() -> "decide:cosmetic_preview", MC.method_1522().method_71639(), OptionalInt.empty());){
                pass.setPipeline(PIPELINE);
                pass.setVertexBuffer(0, vertexBuffer);
                int fbH = MC.method_22683().method_4506();
                int guiScale = MC.method_22683().method_4495();
                int sx = (int)(clipX * (float)guiScale);
                int sy = (int)((float)fbH - (clipY + clipH) * (float)guiScale);
                int sw = (int)(clipW * (float)guiScale);
                int sh = (int)(clipH * (float)guiScale);
                if (sw > 0 && sh > 0) {
                    pass.enableScissor(sx, sy, sw, sh);
                } else {
                    pass.disableScissor();
                }
                pass.bindTexture("uGui", view, RenderSampler.linear());
                pass.draw(0, vertices);
            }
        }
        catch (Throwable t) {
            return info + " [draw error: " + String.valueOf(t) + "]";
        }
        return info;
    }

    private static synchronized void ensureBuffer() {
        if (vertexBuffer != null) {
            return;
        }
        vertexData = ByteBuffer.allocateDirect(4800000).order(ByteOrder.nativeOrder());
        vertexBuffer = RenderSystem.getDevice().createBuffer(() -> "decide:cosmetic_preview", 40, 4800000L);
    }

    private static float guiUnit() {
        int scaled = MC.method_22683().method_4486();
        return scaled > 0 ? (float)MC.method_22683().method_4489() / (float)scaled : 2.0f;
    }

    private static String fmt(float value) {
        return String.format(Locale.ROOT, "%.2f", Float.valueOf(value));
    }

    private CosmeticGuiPreview() {
    }

    static {
        POSITION = new Vector3f();
    }

    @Environment(value=EnvType.CLIENT)
    private record PendingPreview(CosmeticManager.CosmeticEntry entry, float x, float y, float w, float h, float clipX, float clipY, float clipW, float clipH) {
    }
}

