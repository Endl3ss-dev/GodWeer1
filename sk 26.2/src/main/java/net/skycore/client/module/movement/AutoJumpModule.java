package net.skycore.client.module.movement;

import net.minecraft.client.Minecraft;
import net.skycore.client.SkycoreClient;
import net.skycore.client.module.ClientModule;
import net.skycore.client.module.ModuleCategory;
import net.skycore.client.module.combat.AttackAuraModule;

public final class AutoJumpModule extends ClientModule {
   public static final AutoJumpModule INSTANCE = new AutoJumpModule();

   @Override
   protected void onTick() {
      Minecraft mc = Minecraft.getInstance();
      if (mc.player == null || !mc.player.onGround() || mc.isPaused()) return;

      AttackAuraModule aura = SkycoreClient.modules().get(AttackAuraModule.class).orElse(null);
      if (aura != null && aura.hasValidTargetForAutoJump()) {
         mc.player.jumpFromGround();
      }
   }

   private AutoJumpModule() {
      super("Auto Jump", "Jumps only while Attack Aura is enabled and has a valid target.", ModuleCategory.MOVEMENT);
   }
}
