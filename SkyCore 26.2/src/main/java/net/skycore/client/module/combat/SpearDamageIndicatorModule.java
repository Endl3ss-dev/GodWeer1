// Adapted from Prestige SpearDamageIndicator (dev.zprestige.prestige.fy / S0421)
package net.skycore.client.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.skycore.client.SkycoreClient;
import net.skycore.client.combat.SpearHelper;
import net.skycore.client.event.WorldChangeEvent;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.util.BeginFrameHelper;
import net.skycore.client.util.InterBoldHelper;

public final class SpearDamageIndicatorModule extends ClientModule {
   public static final SpearDamageIndicatorModule INSTANCE = new SpearDamageIndicatorModule();

   private static final float ANIMATION_DURATION_SEC = 0.5F;
   private static final float BACK_C1 = 1.70158F;
   private static final float BACK_C2 = BACK_C1 * 1.525F;
   private static final float CARD_WIDTH = 70.0F;
   private static final float CARD_HEIGHT = 20.0F;
   private static final float CARD_RADIUS = 5.0F;
   private static final float OFFSET_BELOW_CROSSHAIR = 30.0F;
   private static final float BAR_HORIZONTAL_PADDING = 6.0F;
   private static final float BAR_HEIGHT = 4.0F;
   private static final float BAR_BOTTOM_OFFSET = 3.0F;
   private static final float TEXT_SIZE = 7.5F;
   private static final float LETHAL_DAMAGE = 20.0F;
   private static final String LETHAL_LABEL = "LETHAL";
   private static final String[] DAMAGE_LABELS = createDamageLabels();

   private float animProgress;
   private float easedScale;
   private float smoothedDamage;
   private Item cachedSpearItem;
   private float cachedMultiplier = 0.95F;

   public SpearDamageIndicatorModule() {
      super("Spear Damage Indicator", "Shows how much damage you have accumulated for the spear", ModuleCategory.COMBAT);
      SkycoreClient.events().subscribe(WorldChangeEvent.class, this::onWorldChange);
   }

   private static String[] createDamageLabels() {
      String[] labels = new String[20];
      for (int i = 0; i < labels.length; i++) {
         labels[i] = "Damage: " + i + "HP";
      }
      return labels;
   }

   @Override
   protected void onDisable() {
      this.animProgress = 0.0F;
      this.easedScale = 0.0F;
      this.smoothedDamage = 0.0F;
      this.cachedSpearItem = null;
   }

   private void onWorldChange(WorldChangeEvent event) {
      this.animProgress = 0.0F;
      this.easedScale = 0.0F;
      this.smoothedDamage = 0.0F;
      this.cachedSpearItem = null;
   }

   public boolean shouldRender(Minecraft mc, float frameDeltaSeconds) {
      if (!this.isEnabled() || mc.player == null || mc.level == null) {
         this.animProgress = 0.0F;
         this.easedScale = 0.0F;
         return false;
      }

      LocalPlayer player = mc.player;
      ItemStack mainHand = player.getMainHandItem();
      boolean active = player.isUsingItem() && SpearHelper.isSpear(mainHand);

      float step = frameDeltaSeconds / ANIMATION_DURATION_SEC;
      if (active) {
         this.animProgress = Math.min(1.0F, this.animProgress + step);
         float targetDamage = this.computeAccumulatedDamage(mc, player, mainHand);
         float blend = 1.0F - (float)Math.exp(-12.0F * frameDeltaSeconds);
         this.smoothedDamage += (targetDamage - this.smoothedDamage) * blend;
         if (Math.abs(targetDamage - this.smoothedDamage) < 0.01F) {
            this.smoothedDamage = targetDamage;
         }
      } else {
         this.animProgress = Math.max(0.0F, this.animProgress - step);
         if (this.animProgress <= 0.0F) {
            this.smoothedDamage = 0.0F;
            this.easedScale = 0.0F;
            return false;
         }
      }

      this.easedScale = easeInOutBack(this.animProgress);
      return this.easedScale > 0.01F;
   }

   public void renderHud(int guiScaledWidth, int guiScaledHeight) {
      float scale = this.easedScale;
      if (scale <= 0.01F) {
         return;
      }

      float alphaFactor = clamp01(this.animProgress);
      float centerX = guiScaledWidth * 0.5F;
      float centerY = guiScaledHeight * 0.5F + OFFSET_BELOW_CROSSHAIR + CARD_HEIGHT * 0.5F;

      float width = CARD_WIDTH * scale;
      float height = CARD_HEIGHT * scale;
      float x = centerX - width * 0.5F;
      float y = centerY - height * 0.5F;
      float radius = CARD_RADIUS * scale;

      int bgAlpha = Math.round(150.0F * alphaFactor);
      int bgColor = bgAlpha << 24 | 0x0E0E0E;
      BeginFrameHelper.rect(x, y, width, height)
         .color(bgColor)
         .radius(radius)
         .hudGlow(false)
         .blur(10.0F * alphaFactor, 1.0F)
         .draw();

      float damage = Math.max(0.0F, this.smoothedDamage);
      String label;
      if (damage >= LETHAL_DAMAGE) {
         label = LETHAL_LABEL;
      } else {
         int idx = (int)damage;
         if (idx < 0) {
            idx = 0;
         } else if (idx >= DAMAGE_LABELS.length) {
            idx = DAMAGE_LABELS.length - 1;
         }
         label = DAMAGE_LABELS[idx];
      }

      float textSize = TEXT_SIZE * scale;
      float textWidth = InterBoldHelper.forWeight(500).getWidth(label, textSize);
      int textAlpha = Math.round(255.0F * alphaFactor);
      int textColor = textAlpha << 24 | 0xFFFFFF;
      BeginFrameHelper.text(x + (width - textWidth) * 0.5F, y + 3.0F * scale, textSize, label)
         .weight(500)
         .color(textColor)
         .draw();

      float barX = x + BAR_HORIZONTAL_PADDING * scale;
      float barWidth = (CARD_WIDTH - BAR_HORIZONTAL_PADDING * 2.0F) * scale;
      float barHeight = BAR_HEIGHT * scale;
      float barY = y + height - barHeight - BAR_BOTTOM_OFFSET * scale;
      float barRadius = barHeight * 0.5F;

      int trackAlpha = Math.round(90.0F * alphaFactor);
      BeginFrameHelper.rect(barX, barY, barWidth, barHeight)
         .color(trackAlpha << 24)
         .radius(barRadius)
         .hudGlow(false)
         .draw();

      float ratio = clamp01(damage / LETHAL_DAMAGE);
      float fillWidth = barWidth * ratio;
      if (fillWidth > 0.25F) {
         int red = Math.round((1.0F - ratio) * 255.0F);
         int green = Math.round(ratio * 255.0F);
         int fillAlpha = Math.round(220.0F * alphaFactor);
         int fillColor = fillAlpha << 24 | red << 16 | green << 8;
         BeginFrameHelper.rect(barX, barY, fillWidth, barHeight)
            .color(fillColor)
            .radius(barRadius)
            .hudGlow(false)
            .draw();
      }
   }

   private float computeAccumulatedDamage(Minecraft mc, LocalPlayer player, ItemStack spearStack) {
      Item item = spearStack.getItem();
      if (item != this.cachedSpearItem) {
         this.cachedSpearItem = item;
         this.cachedMultiplier = SpearHelper.multiplier(SpearHelper.path(spearStack));
      }

      float yawRad = -player.getYRot() * ((float)Math.PI / 180.0F) - (float)Math.PI;
      float pitchRad = -player.getXRot() * ((float)Math.PI / 180.0F);
      float cosPitch = -Mth.cos(pitchRad);
      double lx = Mth.sin(yawRad) * cosPitch;
      double ly = Mth.sin(pitchRad);
      double lz = Mth.cos(yawRad) * cosPitch;

      Entity playerMover = player.getVehicle() != null ? player.getVehicle() : player;
      Vec3 playerVel = playerMover.getDeltaMovement();
      double pvx = playerVel.x;
      double pvy = playerMover.onGround() ? 0.0 : playerVel.y;
      double pvz = playerVel.z;
      double selfProj = lx * pvx + ly * pvy + lz * pvz;

      double targetProj = 0.0;
      if (mc.hitResult instanceof EntityHitResult entityHitResult) {
         Entity target = entityHitResult.getEntity();
         if (target != null) {
            Entity targetMover = target.getVehicle() != null ? target.getVehicle() : target;
            Vec3 targetVel = targetMover.getDeltaMovement();
            double tvx;
            double tvy;
            double tvz;
            if (targetVel.lengthSqr() > 1.0E-6) {
               tvx = targetVel.x;
               tvy = targetMover.onGround() ? 0.0 : targetVel.y;
               tvz = targetVel.z;
            } else {
               tvx = targetMover.getX() - targetMover.xo;
               tvy = targetMover.onGround() ? 0.0 : targetMover.getY() - targetMover.yo;
               tvz = targetMover.getZ() - targetMover.zo;
            }
            targetProj = lx * tvx + ly * tvy + lz * tvz;
         }
      }

      double closingBlocksPerSecond = Math.max(0.0, (selfProj - targetProj) * SpearHelper.TICKS_PER_SECOND);
      float baseAttackDamage = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
      float kineticBonus = (float)Math.floor(closingBlocksPerSecond * this.cachedMultiplier);
      return baseAttackDamage + kineticBonus;
   }

   private static float easeInOutBack(float t) {
      if (t <= 0.0F) {
         return 0.0F;
      }
      if (t >= 1.0F) {
         return 1.0F;
      }
      if (t < 0.5F) {
         float twoT = 2.0F * t;
         return twoT * twoT * ((BACK_C2 + 1.0F) * twoT - BACK_C2) * 0.5F;
      }
      float u = 2.0F * t - 2.0F;
      return (u * u * ((BACK_C2 + 1.0F) * u + BACK_C2) + 2.0F) * 0.5F;
   }

   private static float clamp01(float value) {
      return value < 0.0F ? 0.0F : (value > 1.0F ? 1.0F : value);
   }
}
