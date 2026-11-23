// deobf by pusia
package su.catlean.client.features.modules.cosmetic;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import kotlin.jvm.internal.MutablePropertyReference1Impl;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionfc;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.render.HandModifyEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.GroupSetting;
import su.catlean.api.setting.values.GroupValue;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.render.Render2D;
import su.catlean.utils.render.Render3D;

public final class ViewModel extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new MutablePropertyReference1Impl(ViewModel.class, "syncHands", "getSyncHands()Z", 0), new MutablePropertyReference1Impl(ViewModel.class, "mainHand", "getMainHand()Lsu/catlean/nl;", 0), new MutablePropertyReference1Impl(ViewModel.class, "x", "getX()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "y", "getY()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "z", "getZ()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "scale", "getScale()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "yaw", "getYaw()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "pitch", "getPitch()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "roll", "getRoll()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "rotateYaw", "getRotateYaw()Z", 0), new MutablePropertyReference1Impl(ViewModel.class, "rotatePitch", "getRotatePitch()Z", 0), new MutablePropertyReference1Impl(ViewModel.class, "rotateRoll", "getRotateRoll()Z", 0), new MutablePropertyReference1Impl(ViewModel.class, "rotationSpeed", "getRotationSpeed()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "offHand", "getOffHand()Lsu/catlean/nl;", 0), new MutablePropertyReference1Impl(ViewModel.class, "offX", "getOffX()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "offY", "getOffY()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "offZ", "getOffZ()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "scaleOff", "getScaleOff()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "offYaw", "getOffYaw()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "offPitch", "getOffPitch()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "offRoll", "getOffRoll()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "offRotateYaw", "getOffRotateYaw()Z", 0), new MutablePropertyReference1Impl(ViewModel.class, "offRotatePitch", "getOffRotatePitch()Z", 0), new MutablePropertyReference1Impl(ViewModel.class, "offRotateRoll", "getOffRotateRoll()Z", 0), new MutablePropertyReference1Impl(ViewModel.class, "offRotationSpeed", "getOffRotationSpeed()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "eatMod", "getEatMod()Lsu/catlean/nl;", 0), new MutablePropertyReference1Impl(ViewModel.class, "eatX", "getEatX()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "eatY", "getEatY()F", 0), new MutablePropertyReference1Impl(ViewModel.class, "eatShake", "getEatShake()F", 0) };
   @NotNull
   public static final ViewModel INSTANCE = new ViewModel();
   @NotNull
   private static final BooleanSetting syncHands = SettingFactory.defaultImpl(INSTANCE, "sync-hands", true, null, null, 12, null);
   @NotNull
   private static final GroupSetting mainHand = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "main-hand", null, null, 6, null);
   @NotNull
   private static final FloatSetting x = SettingFactory.defaultImpl(INSTANCE, "x", 0.0F, RangesKt.rangeTo(-3.0F, 3.0F), INSTANCE.getMainHand(), null, 16, null);
   @NotNull
   private static final FloatSetting y = SettingFactory.defaultImpl(INSTANCE, "y", 0.0F, RangesKt.rangeTo(-3.0F, 3.0F), INSTANCE.getMainHand(), null, 16, null);
   @NotNull
   private static final FloatSetting z = SettingFactory.defaultImpl(INSTANCE, "z", 0.0F, RangesKt.rangeTo(-3.0F, 3.0F), INSTANCE.getMainHand(), null, 16, null);
   @NotNull
   private static final FloatSetting scale = SettingFactory.defaultImpl(
      INSTANCE, "scale", 1.0F, RangesKt.rangeTo(0.1F, 1.5F), INSTANCE.getMainHand(), null, 16, null
   );
   @NotNull
   private static final FloatSetting yaw = SettingFactory.defaultImpl(
      INSTANCE, "yaw", 0.0F, RangesKt.rangeTo(-180.0F, 180.0F), INSTANCE.getMainHand(), null, 16, null
   );
   @NotNull
   private static final FloatSetting pitch = SettingFactory.defaultImpl(
      INSTANCE, "pitch", 0.0F, RangesKt.rangeTo(-180.0F, 180.0F), INSTANCE.getMainHand(), null, 16, null
   );
   @NotNull
   private static final FloatSetting roll = SettingFactory.defaultImpl(
      INSTANCE, "roll", 0.0F, RangesKt.rangeTo(-180.0F, 180.0F), INSTANCE.getMainHand(), null, 16, null
   );
   @NotNull
   private static final BooleanSetting rotateYaw = SettingFactory.defaultImpl(INSTANCE, "rotate-yaw", false, INSTANCE.getMainHand(), null, 8, null);
   @NotNull
   private static final BooleanSetting rotatePitch = SettingFactory.defaultImpl(INSTANCE, "rotate-pitch", false, INSTANCE.getMainHand(), null, 8, null);
   @NotNull
   private static final BooleanSetting rotateRoll = SettingFactory.defaultImpl(INSTANCE, "rotate-roll", false, INSTANCE.getMainHand(), null, 8, null);
   @NotNull
   private static final FloatSetting rotationSpeed = SettingFactory.defaultImpl(
      INSTANCE, "rotation-speed", 1.0F, RangesKt.rangeTo(1.0F, 5.0F), INSTANCE.getMainHand(), null, 16, null
   );
   @NotNull
   private static final GroupSetting offHand = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "off-hand", null, () -> !INSTANCE.getSyncHands(), 2, null);
   @NotNull
   private static final FloatSetting offX = SettingFactory.computeFloatSetting(
      INSTANCE, "off-x", 0.0F, RangesKt.rangeTo(-3.0F, 3.0F), INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final FloatSetting offY = SettingFactory.computeFloatSetting(
      INSTANCE, "off-y", 0.0F, RangesKt.rangeTo(-3.0F, 3.0F), INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final FloatSetting offZ = SettingFactory.computeFloatSetting(
      INSTANCE, "off-z", 0.0F, RangesKt.rangeTo(-3.0F, 3.0F), INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final FloatSetting offScale = SettingFactory.computeFloatSetting(
      INSTANCE, "off-scale", 1.0F, RangesKt.rangeTo(0.1F, 1.5F), INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final FloatSetting offYaw = SettingFactory.computeFloatSetting(
      INSTANCE, "off-yaw", 0.0F, RangesKt.rangeTo(-180.0F, 180.0F), INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final FloatSetting offPitch = SettingFactory.computeFloatSetting(
      INSTANCE, "off-pitch", 0.0F, RangesKt.rangeTo(-180.0F, 180.0F), INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final FloatSetting offRoll = SettingFactory.computeFloatSetting(
      INSTANCE, "off-roll", 0.0F, RangesKt.rangeTo(-180.0F, 180.0F), INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final BooleanSetting offRotateYaw = SettingFactory.computeBooleanSetting(
      INSTANCE, "off-rotate-yaw", false, INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final BooleanSetting offRotatePitch = SettingFactory.computeBooleanSetting(
      INSTANCE, "off-rotate-pitch", false, INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final BooleanSetting offRotateRoll = SettingFactory.computeBooleanSetting(
      INSTANCE, "off-rotate-roll", false, INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final FloatSetting offRotationSpeed = SettingFactory.computeFloatSetting(
      INSTANCE, "off-rotation-speed", 1.0F, RangesKt.rangeTo(1.0F, 5.0F), INSTANCE.getOffHand(), () -> !INSTANCE.getSyncHands()
   );
   @NotNull
   private static final GroupSetting eating = SettingFactory.defaultImpl((su.catlean.api.setting.SettingHolder)INSTANCE, "eating", null, null, 6, null);
   @NotNull
   private static final FloatSetting eatX = SettingFactory.defaultImpl(
      INSTANCE, "eat-x", 1.0F, RangesKt.rangeTo(-1.0F, 2.0F), INSTANCE.getEatMod(), null, 16, null
   );
   @NotNull
   private static final FloatSetting eatY = SettingFactory.defaultImpl(
      INSTANCE, "eat-y", 1.0F, RangesKt.rangeTo(-1.0F, 2.0F), INSTANCE.getEatMod(), null, 16, null
   );
   @NotNull
   private static final FloatSetting eatShake = SettingFactory.defaultImpl(
      INSTANCE, "eat-shake", 0.005F, RangesKt.rangeTo(0.0F, 0.2F), INSTANCE.getEatMod(), null, 16, null
   );
   private static float amount;
   private static float amount2;
   private static float amount3;
   private static float amount4;
   private static float amount5;
   private static float amount6;

   private ViewModel() {
      super("view-model", SubCategories.getSubCategory6(), null, 4, null);
   }

   private final boolean getSyncHands() {
      return (Boolean)syncHands.computeObject(this, $$delegatedProperties[0]);
   }

   private final GroupValue getMainHand() {
      return (GroupValue)mainHand.computeObject(this, $$delegatedProperties[1]);
   }

   private final float getX() {
      return ((Number)x.computeObject(this, $$delegatedProperties[2])).floatValue();
   }

   private final float getY() {
      return ((Number)y.computeObject(this, $$delegatedProperties[3])).floatValue();
   }

   private final float getZ() {
      return ((Number)z.computeObject(this, $$delegatedProperties[4])).floatValue();
   }

   public final float getScale() {
      return ((Number)scale.computeObject(this, $$delegatedProperties[5])).floatValue();
   }

   private final float getYaw() {
      return ((Number)yaw.computeObject(this, $$delegatedProperties[6])).floatValue();
   }

   private final void updateAmount(float amount) {
      yaw.updateObject(this, $$delegatedProperties[6], amount);
   }

   private final float getPitch() {
      return ((Number)pitch.computeObject(this, $$delegatedProperties[7])).floatValue();
   }

   private final void updateAmount2(float amount) {
      pitch.updateObject(this, $$delegatedProperties[7], amount);
   }

   private final float getRoll() {
      return ((Number)roll.computeObject(this, $$delegatedProperties[8])).floatValue();
   }

   private final void updateAmount3(float amount) {
      roll.updateObject(this, $$delegatedProperties[8], amount);
   }

   private final boolean getRotateYaw() {
      return (Boolean)rotateYaw.computeObject(this, $$delegatedProperties[9]);
   }

   private final boolean getRotatePitch() {
      return (Boolean)rotatePitch.computeObject(this, $$delegatedProperties[10]);
   }

   private final boolean getRotateRoll() {
      return (Boolean)rotateRoll.computeObject(this, $$delegatedProperties[11]);
   }

   private final float getRotationSpeed() {
      return ((Number)rotationSpeed.computeObject(this, $$delegatedProperties[12])).floatValue();
   }

   private final GroupValue getOffHand() {
      return (GroupValue)offHand.computeObject(this, $$delegatedProperties[13]);
   }

   private final float getOffX() {
      return ((Number)offX.computeObject(this, $$delegatedProperties[14])).floatValue();
   }

   private final void updateAmount4(float amount) {
      offX.updateObject(this, $$delegatedProperties[14], amount);
   }

   private final float getOffY() {
      return ((Number)offY.computeObject(this, $$delegatedProperties[15])).floatValue();
   }

   private final void updateAmount5(float amount) {
      offY.updateObject(this, $$delegatedProperties[15], amount);
   }

   private final float getOffZ() {
      return ((Number)offZ.computeObject(this, $$delegatedProperties[16])).floatValue();
   }

   private final void updateAmount6(float amount) {
      offZ.updateObject(this, $$delegatedProperties[16], amount);
   }

   private final float getScaleOff() {
      return ((Number)offScale.computeObject(this, $$delegatedProperties[17])).floatValue();
   }

   private final void updateAmount7(float amount) {
      offScale.updateObject(this, $$delegatedProperties[17], amount);
   }

   private final float getOffYaw() {
      return ((Number)offYaw.computeObject(this, $$delegatedProperties[18])).floatValue();
   }

   private final void updateAmount8(float amount) {
      offYaw.updateObject(this, $$delegatedProperties[18], amount);
   }

   private final float getOffPitch() {
      return ((Number)offPitch.computeObject(this, $$delegatedProperties[19])).floatValue();
   }

   private final void updateAmount9(float amount) {
      offPitch.updateObject(this, $$delegatedProperties[19], amount);
   }

   private final float getOffRoll() {
      return ((Number)offRoll.computeObject(this, $$delegatedProperties[20])).floatValue();
   }

   private final void updateAmount10(float amount) {
      offRoll.updateObject(this, $$delegatedProperties[20], amount);
   }

   private final boolean getOffRotateYaw() {
      return (Boolean)offRotateYaw.computeObject(this, $$delegatedProperties[21]);
   }

   private final boolean getOffRotatePitch() {
      return (Boolean)offRotatePitch.computeObject(this, $$delegatedProperties[22]);
   }

   private final boolean getOffRotateRoll() {
      return (Boolean)offRotateRoll.computeObject(this, $$delegatedProperties[23]);
   }

   private final float getOffRotationSpeed() {
      return ((Number)offRotationSpeed.computeObject(this, $$delegatedProperties[24])).floatValue();
   }

   private final GroupValue getEatMod() {
      return (GroupValue)eating.computeObject(this, $$delegatedProperties[25]);
   }

   private final float getEatX() {
      return ((Number)eatX.computeObject(this, $$delegatedProperties[26])).floatValue();
   }

   private final float getEatY() {
      return ((Number)eatY.computeObject(this, $$delegatedProperties[27])).floatValue();
   }

   private final float getEatShake() {
      return ((Number)eatShake.computeObject(this, $$delegatedProperties[28])).floatValue();
   }

   private final float computeAmount(float amount, float amount2) {
      return amount - amount2 <= 180.0F && amount - amount2 > -180.0F ? amount - amount2 : 180.0F;
   }

   @Flow
   public final void onPlayerUpdate(PlayerUpdateEvent e) {
      amount = this.getPitch();
      amount2 = this.getYaw();
      amount3 = this.getRoll();
      amount4 = this.getOffPitch();
      amount5 = this.getOffYaw();
      amount6 = this.getOffRoll();
      if (this.getRotateYaw()) {
         this.updateAmount(this.computeAmount(this.getYaw(), this.getRotationSpeed()));
      }

      if (this.getRotatePitch()) {
         this.updateAmount2(this.computeAmount(this.getPitch(), this.getRotationSpeed()));
      }

      if (this.getRotateRoll()) {
         this.updateAmount3(this.computeAmount(this.getRoll(), this.getRotationSpeed()));
      }

      if (this.getOffRotateYaw()) {
         this.updateAmount8(this.computeAmount(this.getOffYaw(), this.getOffRotationSpeed()));
      }

      if (this.getOffRotatePitch()) {
         this.updateAmount9(this.computeAmount(this.getOffPitch(), this.getOffRotationSpeed()));
      }

      if (this.getOffRotateRoll()) {
         this.updateAmount10(this.computeAmount(this.getOffRoll(), this.getOffRotationSpeed()));
      }
   }

   @Flow
   private final void onHandModify(HandModifyEvent handModifyEvent) {
      if (this.getSyncHands()) {
         this.updateAmount4(this.getX());
         this.updateAmount5(this.getY());
         this.updateAmount6(this.getZ());
         this.updateAmount7(this.getScale());
         this.updateAmount8(-this.getYaw());
         this.updateAmount9(this.getPitch());
         this.updateAmount10(-this.getRoll());
      }

      if (handModifyEvent.getEating()) {
         this.render(handModifyEvent.getStack(), Render3D.INSTANCE.getAmount4(), handModifyEvent.getHand(), handModifyEvent.getItem());
         handModifyEvent.cancel();
      } else if (handModifyEvent.getHand() == InteractionHand.MAIN_HAND) {
         handModifyEvent.getStack().translate(this.getX(), this.getY(), this.getZ());
         handModifyEvent.getStack().scale(this.getScale(), this.getScale(), this.getScale());
         handModifyEvent.getStack()
            .mulPose(
               (Quaternionfc)Axis.XP
                  .rotationDegrees(
                     Render2D.INSTANCE
                        .computeAmount(amount, this.getPitch(), Math.abs(amount - this.getPitch()) > 90.0F ? 1.0F : Render3D.INSTANCE.getAmount4())
                  )
            );
         handModifyEvent.getStack()
            .mulPose(
               (Quaternionfc)Axis.YP
                  .rotationDegrees(
                     Render2D.INSTANCE.computeAmount(amount2, this.getYaw(), Math.abs(amount2 - this.getYaw()) > 90.0F ? 1.0F : Render3D.INSTANCE.getAmount4())
                  )
            );
         handModifyEvent.getStack()
            .mulPose(
               (Quaternionfc)Axis.ZP
                  .rotationDegrees(
                     Render2D.INSTANCE
                        .computeAmount(amount3, this.getRoll(), Math.abs(amount3 - this.getRoll()) > 90.0F ? 1.0F : Render3D.INSTANCE.getAmount4())
                  )
            );
      } else {
         handModifyEvent.getStack().translate(-this.getOffX(), this.getOffY(), this.getOffZ());
         handModifyEvent.getStack().scale(this.getScaleOff(), this.getScaleOff(), this.getScaleOff());
         handModifyEvent.getStack()
            .mulPose((Quaternionfc)Axis.XP.rotationDegrees(Render2D.INSTANCE.computeAmount(amount4, this.getOffPitch(), Render3D.INSTANCE.getAmount4())));
         handModifyEvent.getStack()
            .mulPose((Quaternionfc)Axis.YP.rotationDegrees(Render2D.INSTANCE.computeAmount(amount5, this.getOffYaw(), Render3D.INSTANCE.getAmount4())));
         handModifyEvent.getStack()
            .mulPose((Quaternionfc)Axis.ZP.rotationDegrees(Render2D.INSTANCE.computeAmount(amount6, this.getOffRoll(), Render3D.INSTANCE.getAmount4())));
      }
   }

   private final void render(PoseStack poseStack, float amount, InteractionHand interactionHand, ItemStack itemStack) {
      float useItemRemainingTicks = MinecraftHolder.getLocalPlayer().getUseItemRemainingTicks() - amount + 1.0F;
      float useDuration = useItemRemainingTicks / itemStack.getUseDuration((LivingEntity)MinecraftHolder.getLocalPlayer());
      float eatShake2 = 0.0F;
      if (useDuration < 0.8F) {
         eatShake2 = Mth.abs((float)Math.cos(useItemRemainingTicks / 4.0F * (float) Math.PI) * this.getEatShake());
         poseStack.translate(0.0F, eatShake2, 0.0F);
      }

      eatShake2 = 1.0F - (float)Math.pow(useDuration, 27.0F);
      int value = interactionHand == InteractionHand.MAIN_HAND ? 1 : -1;
      poseStack.translate(eatShake2 * 0.6F * value * this.getEatX(), eatShake2 * -0.5F * this.getEatY(), eatShake2 * 0.0F);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(value * eatShake2 * 90.0F));
      poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(eatShake2 * 10.0F));
      poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(value * eatShake2 * 30.0F));
   }

   public final void render(PoseStack matrices) {
      if (this.getEnabled()) {
         matrices.translate(this.getX(), this.getY(), this.getZ());
      }
   }

   public final void render2(PoseStack matrices) {
      if (this.getEnabled()) {
         matrices.translate(-this.getX(), -this.getY(), -this.getZ());
      }
   }

   static {
   }
}