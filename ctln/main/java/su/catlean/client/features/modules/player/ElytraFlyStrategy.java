// deobf by pusia
package su.catlean.client.features.modules.player;

import su.catlean.api.event.events.network.AfterSendPacket;
import su.catlean.api.event.events.network.ReceivePacket;
import su.catlean.api.event.events.player.AfterElytraEvent;
import su.catlean.api.event.events.player.MoveEvent;
import su.catlean.api.event.events.player.PlayerUpdateEvent;
import su.catlean.api.event.events.player.PreElytraEvent;
import su.catlean.api.event.events.player.PreSyncEvent;
import su.catlean.api.event.events.player.SetPoseEvent;
import su.catlean.api.event.events.world.FireWorkVelocityEvent;

public interface ElytraFlyStrategy {
   void onPreSync(PreSyncEvent preSyncEvent);

   void onPreElytra(PreElytraEvent preElytraEvent);

   void onAfterElytra(AfterElytraEvent afterElytraEvent);

   void onMove(MoveEvent moveEvent);

   void onAfterSendPacket(AfterSendPacket afterSendPacket);

   void onReceivePacket(ReceivePacket receivePacket);

   void onPlayerUpdate(PlayerUpdateEvent playerUpdateEvent);

   void update();

   void update2();

   void onFireWorkVelocity(FireWorkVelocityEvent fireWorkVelocityEvent);

   void onSetPose(SetPoseEvent setPoseEvent);

   void onReceivePacket2(ReceivePacket receivePacket);
}