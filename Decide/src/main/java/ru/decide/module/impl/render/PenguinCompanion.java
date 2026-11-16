/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.pipeline.BlendFunction
 *  com.mojang.blaze3d.pipeline.RenderPipeline
 *  com.mojang.blaze3d.pipeline.RenderPipeline$Snippet
 *  com.mojang.blaze3d.platform.DepthTestFunction
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.fabricmc.api.EnvType
 *  net.fabricmc.api.Environment
 *  net.minecraft.class_10799
 *  net.minecraft.class_12247
 *  net.minecraft.class_1309
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_290
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_3298
 *  net.minecraft.class_3532
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_7833
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.decide.module.impl.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_10799;
import net.minecraft.class_12247;
import net.minecraft.class_1309;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_290;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_3298;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_7833;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.decide.manager.event_impl.EventRender3D;
import ru.decide.manager.events.orbit.EventHandler;
import ru.decide.module.api.Category;
import ru.decide.module.api.Module;
import ru.decide.module.api.ModuleInfo;
import ru.decide.module.api.settings.impl.SliderSetting;
import ru.decide.utils.math.ChatUtils;

@ModuleInfo(name="Penguin Companion", desc="Renders the gugu gaga penguin model near the player", category=Category.VISUALS)
@Environment(value=EnvType.CLIENT)
public class PenguinCompanion
extends Module {
    private static final Logger LOGGER = LoggerFactory.getLogger((String)"client/PenguinCompanion");
    private static final class_2960 MODEL_ID = class_2960.method_60655((String)"decide", (String)"model_new/companion_mesh.bin");
    private static final String TEXTURE_PREFIX = "model_new/textures/";
    public SliderSetting scale = new SliderSetting(this, "Scale", 2.45f, 0.35f, 4.0f, 0.05f);
    private final class_9799 allocator = new class_9799(0x100000);
    private NanalyMesh mesh;
    private boolean loadAttempted;
    private class_243 companionPos;
    private float companionYaw;
    private float walkCycle;
    private float walkAmount;
    private long lastFrameNanos;
    private static final Map<class_2960, class_1921> LAYERS = new HashMap<class_2960, class_1921>();
    private static final RenderPipeline MODEL_PIPELINE = class_10799.method_67887((RenderPipeline)RenderPipeline.builder((RenderPipeline.Snippet[])new RenderPipeline.Snippet[]{class_10799.field_56864}).withLocation(class_2960.method_60655((String)"decide", (String)"nanaly_model")).withVertexFormat(class_290.field_1575, VertexFormat.class_5596.field_27379).withCull(false).withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST).withDepthWrite(true).withBlend(BlendFunction.TRANSLUCENT).build());

    @Override
    protected void onEnable() {
        this.companionPos = null;
        this.walkCycle = 0.0f;
        this.walkAmount = 0.0f;
    }

    @EventHandler
    public void onRender3D(EventRender3D e) {
        if (PenguinCompanion.mc.field_1724 == null || PenguinCompanion.mc.field_1687 == null) {
            return;
        }
        NanalyMesh model = this.getMesh();
        if (model == null) {
            return;
        }
        class_4587 matrices = e.getMatrixStack();
        class_243 cam = PenguinCompanion.mc.field_1773.method_19418().method_71156();
        class_1309 followTarget = this.getFollowTarget();
        class_243 desiredPos = this.getDesiredPosition(followTarget, e.getTickDelta());
        this.updateCompanionPosition(desiredPos, followTarget);
        class_243 worldPos = this.companionPos;
        class_243 pos = worldPos.method_1020(cam);
        matrices.method_22903();
        matrices.method_22904(pos.field_1352, pos.field_1351, pos.field_1350);
        matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(180.0f - this.companionYaw));
        matrices.method_22905(this.scale.getValue().floatValue(), this.scale.getValue().floatValue(), this.scale.getValue().floatValue());
        Matrix4f matrix = matrices.method_23760().method_23761();
        class_4597.class_4598 immediate = class_4597.method_22991((class_9799)this.allocator);
        for (MeshGroup group : model.groups) {
            class_4588 buffer = immediate.method_73477(PenguinCompanion.layer(group.texture));
            for (int i = 0; i < group.vertices.length; i += 8) {
                float sourceX = group.vertices[i];
                float sourceDepth = group.vertices[i + 1];
                float sourceY = group.vertices[i + 2];
                float u = group.vertices[i + 3];
                float v = group.vertices[i + 4];
                float x = sourceX;
                float y = sourceY - model.minY;
                float z = -(sourceDepth - model.centerDepth);
                float[] folded = PenguinCompanion.foldArms(model, group.texture, sourceX, sourceY, x, y, z);
                float[] animated = PenguinCompanion.animateWalk(model, group.texture, sourceX, sourceY, folded[0], folded[1], folded[2], this.walkCycle, this.walkAmount);
                buffer.method_22918((Matrix4fc)matrix, animated[0], animated[1], animated[2]).method_1336(255, 255, 255, 255).method_22913(u, v);
            }
        }
        immediate.method_22993();
        matrices.method_22909();
    }

    private class_1309 getFollowTarget() {
        return PenguinCompanion.mc.field_1724;
    }

    private class_243 getDesiredPosition(class_1309 followTarget, float tickDelta) {
        class_243 targetPos = followTarget.method_30950(tickDelta);
        if (followTarget == PenguinCompanion.mc.field_1724) {
            float yaw = PenguinCompanion.mc.field_1724.method_36454();
            double radians = Math.toRadians(yaw);
            class_243 look = new class_243(-Math.sin(radians), 0.0, Math.cos(radians));
            return targetPos.method_1019(look.method_1021(1.15));
        }
        class_243 awayFromPlayer = targetPos.method_1020(PenguinCompanion.mc.field_1724.method_30950(tickDelta));
        awayFromPlayer = awayFromPlayer.method_37268() < 1.0E-4 ? new class_243(0.0, 0.0, 1.0) : awayFromPlayer.method_1029();
        return targetPos.method_1019(awayFromPlayer.method_1021(0.95));
    }

    private void updateCompanionPosition(class_243 desiredPos, class_1309 lookTarget) {
        long now = System.nanoTime();
        float delta = this.lastFrameNanos == 0L ? 0.05f : class_3532.method_15363((float)((float)(now - this.lastFrameNanos) / 1.0E9f), (float)0.0f, (float)0.08f);
        this.lastFrameNanos = now;
        class_243 previousPos = this.companionPos;
        if (this.companionPos == null || this.companionPos.method_1025(desiredPos) > 256.0) {
            this.companionPos = desiredPos;
        } else {
            class_243 toDesired = desiredPos.method_1020(this.companionPos);
            double horizontalDistance = Math.sqrt(toDesired.field_1352 * toDesired.field_1352 + toDesired.field_1350 * toDesired.field_1350);
            if (horizontalDistance > 0.24) {
                double speed = lookTarget == PenguinCompanion.mc.field_1724 ? 4.6 : 6.2;
                double step = Math.min(horizontalDistance, speed * (double)delta);
                this.companionPos = this.companionPos.method_1019(new class_243(toDesired.field_1352 / horizontalDistance * step, 0.0, toDesired.field_1350 / horizontalDistance * step));
            }
            this.companionPos = new class_243(this.companionPos.field_1352, desiredPos.field_1351, this.companionPos.field_1350);
        }
        double moved = 0.0;
        if (previousPos != null) {
            double dx = this.companionPos.field_1352 - previousPos.field_1352;
            double dz = this.companionPos.field_1350 - previousPos.field_1350;
            moved = Math.sqrt(dx * dx + dz * dz);
        }
        float targetWalk = delta <= 0.0f ? 0.0f : class_3532.method_15363((float)((float)(moved / (double)delta) * 0.22f), (float)0.0f, (float)1.0f);
        this.walkAmount = class_3532.method_16439((float)0.2f, (float)this.walkAmount, (float)targetWalk);
        if (this.walkAmount < 0.03f) {
            this.walkAmount = 0.0f;
        }
        this.walkCycle += (float)moved * 9.0f;
        class_243 lookPos = lookTarget.method_73189().method_1031(0.0, (double)lookTarget.method_17682() * 0.45, 0.0);
        class_243 lookVector = lookPos.method_1020(this.companionPos);
        float targetYaw = (float)Math.toDegrees(Math.atan2(lookVector.field_1352, lookVector.field_1350));
        this.companionYaw = PenguinCompanion.lerpAngle(this.companionYaw, targetYaw, 0.2f);
    }

    private static float[] foldArms(NanalyMesh model, class_2960 texture, float sourceX, float sourceY, float x, float y, float z) {
        float height = (sourceY - model.minY) / Math.max(model.maxY - model.minY, 1.0E-4f);
        float absX = Math.abs(sourceX);
        String path = texture.method_12832();
        boolean cloth = path.contains("cloth");
        if (!cloth || absX < 0.16f || height < 0.22f || height > 0.72f) {
            return new float[]{x, y, z};
        }
        float side = Math.signum(sourceX);
        float outer = PenguinCompanion.smoothStep(0.16f, 0.42f, absX);
        float vertical = PenguinCompanion.smoothBand(height, 0.22f, 0.34f, 0.58f, 0.72f);
        float influence = outer * vertical * 0.72f;
        float targetX = side * 0.15f;
        float targetZ = z + 0.08f;
        return new float[]{class_3532.method_16439((float)influence, (float)x, (float)targetX), y - 0.015f * influence, class_3532.method_16439((float)influence, (float)z, (float)targetZ)};
    }

    private static float[] animateWalk(NanalyMesh model, class_2960 texture, float sourceX, float sourceY, float x, float y, float z, float cycle, float amount) {
        if (amount <= 0.0f) {
            return new float[]{x, y, z};
        }
        float height = (sourceY - model.minY) / Math.max(model.maxY - model.minY, 1.0E-4f);
        float absX = Math.abs(sourceX);
        float side = sourceX >= 0.0f ? 1.0f : -1.0f;
        float phase = class_3532.method_15374((double)(cycle + (side > 0.0f ? 0.0f : (float)Math.PI)));
        float lift = Math.max(phase, 0.0f);
        String path = texture.method_12832();
        if (path.contains("cloth") && height < 0.2f && absX > 0.025f && absX < 0.22f) {
            float foot = PenguinCompanion.smoothBand(height, 0.0f, 0.03f, 0.16f, 0.22f);
            return new float[]{x + side * 0.01f * lift * foot * amount, y + 0.045f * lift * foot * amount, z + phase * 0.07f * foot * amount};
        }
        if (path.contains("cloth") && height >= 0.22f && height <= 0.62f && absX > 0.16f) {
            float paw = PenguinCompanion.smoothBand(height, 0.22f, 0.34f, 0.54f, 0.66f) * PenguinCompanion.smoothStep(0.16f, 0.38f, absX);
            return new float[]{x + side * 0.012f * phase * paw * amount, y + 0.01f * lift * paw * amount, z - phase * 0.035f * paw * amount};
        }
        if (height > 0.18f && height < 0.78f && absX < 0.34f) {
            float body = PenguinCompanion.smoothBand(height, 0.18f, 0.36f, 0.62f, 0.78f);
            return new float[]{x + side * 0.002f * phase * body * amount, y, z - Math.abs(phase) * 0.01f * body * amount};
        }
        return new float[]{x, y, z};
    }

    private static float lerpAngle(float from, float to, float delta) {
        float diff = class_3532.method_15393((float)(to - from));
        return from + diff * delta;
    }

    private static float smoothStep(float edge0, float edge1, float value) {
        float t = class_3532.method_15363((float)((value - edge0) / Math.max(edge1 - edge0, 1.0E-4f)), (float)0.0f, (float)1.0f);
        return t * t * (3.0f - 2.0f * t);
    }

    private static float smoothBand(float value, float fadeInStart, float fadeInEnd, float fadeOutStart, float fadeOutEnd) {
        return PenguinCompanion.smoothStep(fadeInStart, fadeInEnd, value) * (1.0f - PenguinCompanion.smoothStep(fadeOutStart, fadeOutEnd, value));
    }

    private NanalyMesh getMesh() {
        block12: {
            if (this.mesh != null || this.loadAttempted) {
                return this.mesh;
            }
            this.loadAttempted = true;
            try {
                Optional resource = class_310.method_1551().method_1478().method_14486(MODEL_ID);
                if (resource.isEmpty()) {
                    LOGGER.warn("Nanaly mesh not found: {}", (Object)MODEL_ID);
                    if (PenguinCompanion.mc.field_1724 != null) {
                        ChatUtils.addChatMessage("\u00a7cPenguin Companion: mesh \u043d\u0435 \u043d\u0430\u0439\u0434\u0435\u043d \u00a77" + String.valueOf(MODEL_ID));
                    }
                    return null;
                }
                try (InputStream in = ((class_3298)resource.get()).method_14482();){
                    this.mesh = NanalyMesh.read(in.readAllBytes());
                    LOGGER.info("Loaded Nanaly mesh with {} groups", (Object)this.mesh.groups.length);
                    if (PenguinCompanion.mc.field_1724 != null) {
                        ChatUtils.addChatMessage("\u00a7aPenguin Companion: mesh \u0437\u0430\u0433\u0440\u0443\u0436\u0435\u043d \u00a77(" + this.mesh.groups.length + " groups)");
                    }
                }
            }
            catch (IOException | RuntimeException ex) {
                LOGGER.error("Failed to load Nanaly mesh", (Throwable)ex);
                if (PenguinCompanion.mc.field_1724 == null) break block12;
                ChatUtils.addChatMessage("\u00a7cPenguin Companion: \u043e\u0448\u0438\u0431\u043a\u0430 \u0437\u0430\u0433\u0440\u0443\u0437\u043a\u0438 mesh, \u0441\u043c\u043e\u0442\u0440\u0438 latest.log");
            }
        }
        return this.mesh;
    }

    private static class_1921 layer(class_2960 texture) {
        return LAYERS.computeIfAbsent(texture, id -> class_1921.method_75940((String)("nanaly_model_" + id.method_12832().replace('/', '_').replace('.', '_')), (class_12247)class_12247.method_75927((RenderPipeline)MODEL_PIPELINE).method_75934("Sampler0", id).method_75937().method_75929(0x100000).method_75938()));
    }

    @Environment(value=EnvType.CLIENT)
    private static class NanalyMesh {
        final MeshGroup[] groups;
        final float minY;
        final float maxY;
        final float centerDepth;

        NanalyMesh(MeshGroup[] groups, float minY, float maxY, float centerDepth) {
            this.groups = groups;
            this.minY = minY;
            this.maxY = maxY;
            this.centerDepth = centerDepth;
        }

        static NanalyMesh read(byte[] bytes) {
            ByteBuffer buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
            byte[] magic = new byte[5];
            buf.get(magic);
            if (!"NMSH1".equals(new String(magic, StandardCharsets.US_ASCII))) {
                throw new IllegalArgumentException("Bad Nanaly mesh magic");
            }
            int groupCount = buf.getInt();
            MeshGroup[] groups = new MeshGroup[groupCount];
            float minY = Float.POSITIVE_INFINITY;
            float maxY = Float.NEGATIVE_INFINITY;
            float minDepth = Float.POSITIVE_INFINITY;
            float maxDepth = Float.NEGATIVE_INFINITY;
            for (int groupIndex = 0; groupIndex < groupCount; ++groupIndex) {
                int i;
                int textureNameLength = Short.toUnsignedInt(buf.getShort());
                byte[] textureNameBytes = new byte[textureNameLength];
                buf.get(textureNameBytes);
                String textureName = new String(textureNameBytes, StandardCharsets.UTF_8);
                int vertexCount = buf.getInt();
                float[] vertices = new float[vertexCount * 8];
                for (i = 0; i < vertices.length; ++i) {
                    vertices[i] = buf.getFloat();
                }
                for (i = 0; i < vertices.length; i += 8) {
                    minDepth = Math.min(minDepth, vertices[i + 1]);
                    maxDepth = Math.max(maxDepth, vertices[i + 1]);
                    minY = Math.min(minY, vertices[i + 2]);
                    maxY = Math.max(maxY, vertices[i + 2]);
                }
                class_2960 texture = class_2960.method_60655((String)"decide", (String)(PenguinCompanion.TEXTURE_PREFIX + textureName));
                groups[groupIndex] = new MeshGroup(texture, vertices);
            }
            return new NanalyMesh(groups, minY, maxY, (minDepth + maxDepth) * 0.5f);
        }
    }

    @Environment(value=EnvType.CLIENT)
    private record MeshGroup(class_2960 texture, float[] vertices) {
    }
}

