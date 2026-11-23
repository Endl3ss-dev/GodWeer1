// deobf by pusia
package su.catlean.client.features.modules.world;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Rotation;
import su.catlean.api.event.events.client.InputEvent;
import su.catlean.api.event.events.network.SendPacket;
import su.catlean.api.event.events.player.FreecamStateEvent;
import su.catlean.api.event.events.player.KeyboardInputEvent;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.UpdateCrosshairTarget;
import su.catlean.api.event.events.render.SetupTerrainEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.services.RotationManager;
import su.catlean.gofra.Flow;
import su.catlean.mixins.accessors.KeyMappingAccessor;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;
import su.catlean.utils.world.WorldUtil;

public final class FreeCam extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(FreeCam.class, "speed", "getSpeed()F", 0), new PropertyReference1Impl(FreeCam.class, "hspeed", "getHspeed()F", 0), new PropertyReference1Impl(FreeCam.class, "freeze", "getFreeze()Z", 0) };
   @NotNull
   public static final FreeCam INSTANCE = new FreeCam();
   @NotNull
   private static final FloatSetting hSpeed = SettingFactory.defaultImpl(INSTANCE, "h-speed", 1.0F, RangesKt.rangeTo(0.1F, 3.0F), null, null, 24, null);
   @NotNull
   private static final FloatSetting vSpeed = SettingFactory.defaultImpl(INSTANCE, "v-speed", 0.42F, RangesKt.rangeTo(0.1F, 3.0F), null, null, 24, null);
   @NotNull
   private static final BooleanSetting freeze = SettingFactory.defaultImpl(INSTANCE, "freeze", true, null, null, 12, null);
   private static float amount;
   private static float amount2;
   private static float amount3;
   private static float amount4;
   @NotNull
   private static Vec3 vec3 = Vec3.ZERO;
   @NotNull
   private static Vec3 vec32 = Vec3.ZERO;
   private static float amount5;
   private static float amount6;
   private static float amount7;
   @NotNull
   private static Vec3 vec33 = Vec3.ZERO;
   @NotNull
   private static Rotation rotation = new Rotation(0.0F, 0.0F, false, null, 12, null);

   private FreeCam() {
      super("free-cam", SubCategories.getSubCategory8(), null, 4, null);
   }

   private final float getSpeed() {
      return ((Number)hSpeed.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   private final float getHspeed() {
      return ((Number)vSpeed.computeObject(this, $$delegatedProperties[1])).floatValue();
   }

   private final boolean getFreeze() {
      return (Boolean)freeze.computeObject(this, $$delegatedProperties[2]);
   }

   @Override
   public void update2() {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         MinecraftHolder.getMinecraft().smartCull = false;
         amount2 = MinecraftHolder.getLocalPlayer().getXRot();
         amount = MinecraftHolder.getLocalPlayer().getYRot();
         amount4 = amount2;
         amount3 = amount;
         vec3 = new Vec3(
            MinecraftHolder.getLocalPlayer().getX(),
            MinecraftHolder.getLocalPlayer().getY() + MinecraftHolder.getLocalPlayer().getEyeHeight(MinecraftHolder.getLocalPlayer().getPose()),
            MinecraftHolder.getLocalPlayer().getZ()
         );
         vec32 = new Vec3(MinecraftHolder.getLocalPlayer().getX(), MinecraftHolder.getLocalPlayer().getY(), MinecraftHolder.getLocalPlayer().getZ());
         vec33 = MinecraftHolder.getLocalPlayer().getDeltaMovement();
         rotation = new Rotation(MinecraftHolder.getLocalPlayer().getYRot(), MinecraftHolder.getLocalPlayer().getXRot(), false, null, 12, null);
      }
   }

   @Override
   public void update4() {
      if (this.isFlag6()) {
         MinecraftHolder.getMinecraft().smartCull = true;
         if (this.getFreeze()) {
            MinecraftHolder.getLocalPlayer().setDeltaMovement(vec33);
            MinecraftHolder.getLocalPlayer().forceSetRotation(rotation.getAmount(), false, rotation.getAmount2(), false);
         }
      }
   }

   @Flow(
      priority = 10
   )
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         if (MinecraftHolder.getMinecraft().gui.screen() instanceof LevelLoadingScreen
            || MinecraftHolder.getMinecraft().gui.screen() instanceof ServerReconfigScreen) {
            this.applyUnplug();
         }

         amount3 = amount;
         amount4 = amount2;
         amount = MinecraftHolder.getLocalPlayer().getYRot();
         amount2 = MinecraftHolder.getLocalPlayer().getXRot();
         double[] defaultImpl = WorldUtil.defaultImpl(WorldUtil.INSTANCE, this.getSpeed(), false, amount5, amount6, 0.0F, 16, null);
         vec32 = vec3;
         vec3 = vec3.add(defaultImpl[0], amount7, defaultImpl[1]);
         if (MinecraftHolder.getMinecraft().hitResult != null
            && MinecraftHolder.getMinecraft().hitResult.getLocation() != null
            && !WorldUtil.INSTANCE.isFlag4()) {
            RotationManager.INSTANCE.updateRotation(Rotation.vectorUtil.computeRotation(MinecraftHolder.getMinecraft().hitResult.getLocation()), 0);
         }
      } else {
         this.applyUnplug();
      }
   }

   @Flow
   private final void onSetupTerrain(SetupTerrainEvent setupTerrainEvent) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         setupTerrainEvent.cancel();
      } else {
         this.applyUnplug();
      }
   }

   @Flow
   private final void onFreecamState(FreecamStateEvent freecamStateEvent) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         freecamStateEvent.cancel();
         freecamStateEvent.setX(this.getAmount3());
         freecamStateEvent.setY(this.getAmount4());
         freecamStateEvent.setZ(this.getAmount5());
         freecamStateEvent.setYaw(this.getAmount());
         freecamStateEvent.setPitch(this.getAmount2());
      } else {
         this.applyUnplug();
      }
   }

   @Flow
   private final void onUpdateCrosshairTarget(UpdateCrosshairTarget updateCrosshairTarget) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         Profiler.get().pop();
         updateCrosshairTarget.cancel();
         MinecraftHolder.getMinecraft().hitResult = WorldUtil.INSTANCE
            .computeHitResult(this.getAmount(), this.getAmount2(), this.getAmount3(), this.getAmount4(), this.getAmount5());
      } else {
         this.applyUnplug();
      }
   }

   @Flow
   private final void onInput(InputEvent inputEvent) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         if (inputEvent.getKey() != -1) {
            boolean flag = ((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyUp).getKey().getValue() == inputEvent.getKey();
            boolean flag2 = ((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyDown).getKey().getValue() == inputEvent.getKey();
            boolean flag3 = ((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyLeft).getKey().getValue() == inputEvent.getKey();
            boolean flag4 = ((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyRight).getKey().getValue() == inputEvent.getKey();
            boolean flag5 = ((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyJump).getKey().getValue() == inputEvent.getKey();
            boolean flag6 = ((KeyMappingAccessor)MinecraftHolder.getMinecraft().options.keyShift).getKey().getValue() == inputEvent.getKey();
            if (inputEvent.getAction() == InputEvent.Action.Press || inputEvent.getAction() == InputEvent.Action.Hold) {
               if (flag) {
                  amount5 = 1.0F;
               }

               if (flag2) {
                  amount5 = -1.0F;
               }

               if (flag3) {
                  amount6 = 1.0F;
               }

               if (flag4) {
                  amount6 = -1.0F;
               }

               if (flag5) {
                  amount7 = this.getHspeed();
               }

               if (flag6) {
                  amount7 = -this.getHspeed();
               }
            }

            if (inputEvent.getAction() == InputEvent.Action.Release) {
               if (flag) {
                  amount5 = 0.0F;
               }

               if (flag2) {
                  amount5 = 0.0F;
               }

               if (flag3) {
                  amount6 = 0.0F;
               }

               if (flag4) {
                  amount6 = 0.0F;
               }

               if (flag5) {
                  amount7 = 0.0F;
               }

               if (flag6) {
                  amount7 = 0.0F;
               }
            }

            WorldUtil.INSTANCE.applySetDown();
            if (flag || flag2 || flag3 || flag4 || flag5 || flag6) {
               inputEvent.cancel();
            }
         }
      } else {
         this.applyUnplug();
      }
   }

   @Flow
   public final void onKeyboardInput(KeyboardInputEvent e) {
      e.cancel();
   }

   @Flow(
      priority = -10
   )
   private final void onMove(MoveEvent moveEvent) {
      if (MinecraftHolder.getMinecraft().level != null && MinecraftHolder.getMinecraft().player != null) {
         if (this.getFreeze()) {
            moveEvent.setX(0.0);
            moveEvent.setY(0.0);
            moveEvent.setZ(0.0);
            moveEvent.cancel();
         }
      } else {
         this.applyUnplug();
      }
   }

   @Flow
   private final void onSendPacket(SendPacket sendPacket) {
      if (MinecraftHolder.getMinecraft().player != null
         && MinecraftHolder.getMinecraft().level != null
         && this.getFreeze()
         && (sendPacket.getPacket() instanceof ServerboundMovePlayerPacket || sendPacket.getPacket() instanceof ServerboundPlayerInputPacket)) {
         sendPacket.cancel();
      }
   }

   private final float getAmount() {
      return Render2D.INSTANCE.computeAmount(amount3, amount, Render3D.INSTANCE.getAmount4());
   }

   private final float getAmount2() {
      return Render2D.INSTANCE.computeAmount(amount4, amount2, Render3D.INSTANCE.getAmount4());
   }

   private final double getAmount3() {
      return Render2D.INSTANCE.computeAmount(vec32.x, vec3.x, (double)Render3D.INSTANCE.getAmount4());
   }

   private final double getAmount4() {
      return Render2D.INSTANCE.computeAmount(vec32.y, vec3.y, (double)Render3D.INSTANCE.getAmount4());
   }

   private final double getAmount5() {
      return Render2D.INSTANCE.computeAmount(vec32.z, vec3.z, (double)Render3D.INSTANCE.getAmount4());
   }

   static {
   }
}