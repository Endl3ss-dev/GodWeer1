package net.skycore.client.module.combat;

import net.skycore.client.setting.BooleanSetting;
import net.skycore.client.setting.NumberSetting;
import net.skycore.client.setting.Setting;

/**
 * 1.8 NoDelay attack clock, ported from MotherHack Aura
 * (MotherHack/src/main/java/fun/motherhack/modules/impl/combat/Aura.java: attacksPerTick,
 * antiKick, antiKickDelay). A burst is handed to the module once per game tick, the vanilla
 * attack cooldown is never consulted, and Anti Kick spaces bursts out in wall-clock time.
 */
final class AuraBurst {
   /** MotherHack allows 500; higher values are kicked by most servers and stall the client. */
   static final int MAX_ATTACKS_PER_TICK = 50;

   final NumberSetting attacksPerTick = new NumberSetting("Attacks per tick",
      "Swings sent in one game tick while the target stays in range.", 5.0, 1.0, MAX_ATTACKS_PER_TICK, 1.0);
   final BooleanSetting antiKick = new BooleanSetting("Anti kick",
      "Space bursts out by a delay instead of attacking on every game tick.", true);
   final NumberSetting antiKickDelay = new NumberSetting("Anti kick delay",
      "Milliseconds between two bursts while Anti kick is enabled.", 50.0, 10.0, 200.0, 10.0, " ms");
   final Setting<?>[] settings = {attacksPerTick, antiKick, antiKickDelay};

   private boolean scheduled;
   private long nextBurst;
   private int lastBurstTick = Integer.MIN_VALUE;

   AuraBurst() {
      antiKickDelay.visibleWhen(antiKick::get);
   }

   /** One burst per tick however often the caller runs, and never before the anti-kick window ends. */
   boolean ready(long now, int tick) {
      if (tick == this.lastBurstTick) {
         return false;
      }
      return !this.scheduled || now - this.nextBurst >= 0;
   }

   int attacks() {
      return this.attacksPerTick.get().intValue();
   }

   void recordBurst(long now, int tick) {
      this.lastBurstTick = tick;
      this.scheduled = this.antiKick.get();
      this.nextBurst = now + Math.round(this.antiKickDelay.get() * 1_000_000.0);
   }

   void reset() {
      this.scheduled = false;
      this.lastBurstTick = Integer.MIN_VALUE;
   }
}
