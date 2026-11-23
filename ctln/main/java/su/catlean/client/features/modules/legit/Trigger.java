// deobf by pusia
package su.catlean.client.features.modules.legit;

import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.setting.SettingFactory;
import su.catlean.api.setting.settings.BooleanSetting;
import su.catlean.api.setting.settings.FloatSetting;
import su.catlean.api.setting.settings.IntSetting;
import su.catlean.client.features.modules.Module;
import su.catlean.client.features.modules.SubCategories;
import su.catlean.client.features.modules.attack.aura.AuraMovementHandler;
import su.catlean.gofra.Flow;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.player.PlayerNameUtil;
import su.catlean.utils.world.WorldUtil;

public final class Trigger extends Module {
   @NotNull
   public static final KProperty[] $$delegatedProperties = new KProperty[]{ new PropertyReference1Impl(Trigger.class, "attackRange", "getAttackRange()F", 0), new PropertyReference1Impl(Trigger.class, "reactVal", "getReactVal()I", 0), new PropertyReference1Impl(Trigger.class, "whileAttackPressed", "getWhileAttackPressed()Z", 0), new PropertyReference1Impl(Trigger.class, "autoJump", "getAutoJump()Z", 0), new PropertyReference1Impl(Trigger.class, "ignoreWalls", "getIgnoreWalls()Z", 0), new PropertyReference1Impl(Trigger.class, "pauseEating", "getPauseEating()Z", 0) };
   @NotNull
   public static final Trigger INSTANCE = new Trigger();
   @NotNull
   private static final FloatSetting range = SettingFactory.defaultImpl(INSTANCE, "range", 3.0F, RangesKt.rangeTo(1.0F, 7.0F), null, null, 24, null);
   @NotNull
   private static final IntSetting reactVal = SettingFactory.defaultImpl(INSTANCE, "react-val", 1, new IntRange(0, 10), null, null, 24, null);
   @NotNull
   private static final BooleanSetting whileAttackPressed = SettingFactory.defaultImpl(INSTANCE, "while-attack-pressed", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting autoJump = SettingFactory.defaultImpl(INSTANCE, "auto-jump", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting ignoreWalls = SettingFactory.defaultImpl(INSTANCE, "ignore-walls", false, null, null, 12, null);
   @NotNull
   private static final BooleanSetting pauseWhileEating = SettingFactory.defaultImpl(INSTANCE, "pause-while-eating", false, null, null, 12, null);
   private static int value;

   private Trigger() {
      super("trigger", SubCategories.getSubCategory3(), null, 4, null);
   }

   private final float getAttackRange() {
      return ((Number)range.computeObject(this, $$delegatedProperties[0])).floatValue();
   }

   private final int getReactVal() {
      return ((Number)reactVal.computeObject(this, $$delegatedProperties[1])).intValue();
   }

   private final boolean getWhileAttackPressed() {
      return (Boolean)whileAttackPressed.computeObject(this, $$delegatedProperties[2]);
   }

   private final boolean getAutoJump() {
      return (Boolean)autoJump.computeObject(this, $$delegatedProperties[3]);
   }

   private final boolean getIgnoreWalls() {
      return (Boolean)ignoreWalls.computeObject(this, $$delegatedProperties[4]);
   }

   private final boolean getPauseEating() {
      return (Boolean)pauseWhileEating.computeObject(this, $$delegatedProperties[5]);
   }

   @Flow
   private final void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent) {
      if (!this.getWhileAttackPressed() || MinecraftHolder.getMinecraft().options.keyAttack.isDown()) {
         if (!MinecraftHolder.getLocalPlayer().isUsingItem() || !this.getPauseEating()) {
            if (!MinecraftHolder.getMinecraft().options.keyJump.isDown() && MinecraftHolder.getLocalPlayer().onGround() && this.getAutoJump()) {
               MinecraftHolder.getLocalPlayer().jumpFromGround();
            }

            Entity defaultImpl = WorldUtil.defaultImpl(
               WorldUtil.INSTANCE,
               MinecraftHolder.getLocalPlayer().getYRot(),
               MinecraftHolder.getLocalPlayer().getXRot(),
               this.getAttackRange(),
               this.getIgnoreWalls(),
               false,
               16,
               null
            );
            if (defaultImpl != null && (!(defaultImpl instanceof Player) || !PlayerNameUtil.checkGetGameProfile((Player)defaultImpl))) {
               if (value >= this.getReactVal() && AuraMovementHandler.INSTANCE.isFlag()) {
                  MinecraftHolder.getMultiPlayerGameMode().attack((Player)MinecraftHolder.getLocalPlayer(), defaultImpl);
                  MinecraftHolder.getLocalPlayer().swing(InteractionHand.MAIN_HAND);
               }

               int value2 = value++;
            } else {
               value = 0;
            }
         }
      }
   }

   static {
   }
}