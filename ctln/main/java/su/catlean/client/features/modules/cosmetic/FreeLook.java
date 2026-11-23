// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import net.minecraft.client.CameraType;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.player.EventPlayerTravel;
import su.catlean.api.event.events.player.EventPostPlayerTravel;
import su.catlean.api.event.events.player.JumpEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.UpdateCrosshairTarget;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.RotationManager;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.world.WorldUtil;

public final class FreeLook extends Module {
   @NotNull
   public static final FreeLook INSTANCE = new FreeLook();
   @NotNull
   private static Rotation rotation = new Rotation(0.0F, 0.0F, false, null, 12, null);
   @NotNull
   private static Rotation rotation2 = new Rotation(0.0F, 0.0F, false, null, 12, null);

   private FreeLook() {
      super("free-look", SubCategories.getSubCategory6(), null, 4, null);
   }

   @NotNull
   public final Rotation getRotation() {
      return rotation;
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getMinecraft().level != null) {
         rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
         MinecraftHolder.getMinecraft().options.setCameraType(CameraType.THIRD_PERSON_BACK);
      } else {
         this.applyUnplug();
      }
   }

   @Override
   public void update4() {
      if (MinecraftHolder.getMinecraft().player != null && MinecraftHolder.getMinecraft().level != null) {
         MinecraftHolder.getMinecraft().options.setCameraType(CameraType.FIRST_PERSON);
         MinecraftHolder.getLocalPlayer().setYRot(rotation.getAmount());
         MinecraftHolder.getLocalPlayer().setXRot(rotation.getAmount2());
      }
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      RotationManager.INSTANCE.updateRotation(rotation, 0);
   }

   @Flow
   private final void onJump(JumpEvent jumpEvent) {
      jumpEvent.setYaw(rotation.getAmount());
      jumpEvent.cancel();
   }

   @Flow
   public final void onPlayerTravel(EventPlayerTravel e) {
      rotation2 = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
      MinecraftHolder.getLocalPlayer().setYRot(rotation.getAmount());
      MinecraftHolder.getLocalPlayer().setXRot(rotation.getAmount2());
   }

   @Flow
   public final void onPostPlayerTravel(EventPostPlayerTravel e) {
      MinecraftHolder.getLocalPlayer().setYRot(rotation2.getAmount());
      MinecraftHolder.getLocalPlayer().setXRot(rotation2.getAmount2());
   }

   @Flow
   private final void onUpdateCrosshairTarget(UpdateCrosshairTarget updateCrosshairTarget) {
      Profiler.get().pop();
      updateCrosshairTarget.cancel();
      if (MinecraftHolder.getMinecraft().getCameraEntity() != null
         && MinecraftHolder.getMinecraft().level != null
         && MinecraftHolder.getMinecraft().player != null) {
         HitResult hitResult = WorldUtil.INSTANCE
            .computeHitResult(
               MinecraftHolder.getLocalPlayer().blockInteractionRange(),
               MinecraftHolder.getLocalPlayer().entityInteractionRange(),
               rotation.getAmount(),
               rotation.getAmount2()
            );
         MinecraftHolder.getMinecraft().hitResult = hitResult;
         MinecraftHolder.getMinecraft().crosshairPickEntity = hitResult instanceof EntityHitResult ? ((EntityHitResult)hitResult).getEntity() : null;
      }
   }
}