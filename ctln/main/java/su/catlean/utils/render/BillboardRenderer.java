// deobf by pusia
package su.catlean.utils.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.ArrayList;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.Render3DEvent;
import su.catlean.client.render.ParticleRenderer;
import su.catlean.client.render.Pipelines;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;

public final class BillboardRenderer {
   @NotNull
   public static final BillboardRenderer INSTANCE = new BillboardRenderer();
   @NotNull
   private static final ArrayList<ParticleRenderer> items = new ArrayList();

   private BillboardRenderer() {
   }

   @Flow
   private final void onRender3D(Render3DEvent render3DEvent) {
      if (!items.isEmpty()) {
         render3DEvent.getStack().pushPose();
         PoseUtil.render(render3DEvent.getStack(), (Quaternionfc)Axis.YP.rotation(-Render3D.INSTANCE.getAmount2()));
         PoseUtil.render(render3DEvent.getStack(), (Quaternionfc)Axis.XP.rotation(-Render3D.INSTANCE.getAmount3()));
         GpuTextureView textureView = MinecraftHolder.getMinecraft().getTextureManager().getTexture(AtlasTexture.getCatlean()).getTextureView();
         MeshBuilder meshBuilder = new MeshBuilder(DefaultVertexFormat.POSITION_TEX_COLOR, items.size(), false, 4, null);
         PoseStack poseStack = new PoseStack();
         PoseUtil.render(poseStack, (Quaternionfc)Axis.XP.rotation(Render3D.INSTANCE.getAmount3()));
         PoseUtil.render(poseStack, (Quaternionfc)Axis.YP.rotation(Render3D.INSTANCE.getAmount2()));
         Quaternionf rotateYXZ = new Quaternionf().rotateYXZ(-Render3D.INSTANCE.getAmount(), Render3D.INSTANCE.getAmount3(), 0.0F);

         for (Object item : items) {
            ParticleRenderer particleRenderer = (ParticleRenderer)item;
            if (Render3D.INSTANCE.getFrustum().pointInFrustum(particleRenderer.getPos().x, particleRenderer.getPos().y, particleRenderer.getPos().z)) {
               particleRenderer.render(poseStack, (Quaternionfc)rotateYXZ, meshBuilder);
            }
         }

         MeshBuilder.defaultImpl(
            meshBuilder,
            Pipelines.INSTANCE.getRenderPipeline19(),
            null,
            null,
            render3DEvent.getStack().last().pose(),
            null,
            MapsKt.mapOf(TuplesKt.to("Sampler0", textureView)),
            null,
            86,
            null
         );
         render3DEvent.getStack().popPose();
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      items.removeIf(arg -> ((ParticleRenderer)arg).isFlag());
   }

   public final boolean testParticleRenderer(ParticleRenderer p) {
      return items.add(p);
   }
}