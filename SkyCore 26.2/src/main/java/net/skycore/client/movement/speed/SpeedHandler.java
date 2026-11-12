package net.skycore.client.movement.speed;

import net.minecraft.client.player.LocalPlayer;
import net.skycore.client.event.PacketReceiveEvent;
import net.skycore.client.event.PacketSendEvent;
import net.skycore.client.event.PlayerInputEvent;
import net.skycore.client.module.movement.SpeedModule;

public abstract class SpeedHandler {
   protected final SpeedModule module;

   protected SpeedHandler(SpeedModule module) {
      this.module = module;
   }

   public void onEnable() {
   }

   public void onDisable() {
   }

   public void onTickStart(LocalPlayer player) {
   }

   public void onTickEnd(LocalPlayer player) {
   }

   public void onTravelPre(LocalPlayer player) {
   }

   public void onInput(PlayerInputEvent event) {
   }

   public void onPacketSend(PacketSendEvent event) {
   }

   public void onPacketReceive(PacketReceiveEvent event) {
   }

   /** Тик для фазовых бустов (Releon ReallyWorld/HolyWorld/FunTime/SpookyTime). */
   public void onClientTick() {
   }

   /** Активен ли собственный таймер спида (приоритет над Timer-модулем). */
   public boolean hasTimerOverride() {
      return false;
   }

   public float timerMultiplier() {
      return 1.0F;
   }
}
