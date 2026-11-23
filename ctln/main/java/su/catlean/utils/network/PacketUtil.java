// deobf by pusia
package su.catlean.utils.network;

import net.minecraft.network.protocol.game.ServerGamePacketListener;
import kotlin.Unit;
import kotlin.jdk7.AutoCloseableKt;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.network.protocol.Packet;
import su.catlean.utils.MinecraftHolder;

public final class PacketUtil {
   public static final void applySend(Packet packet) {
      MinecraftHolder.getClientPacketListener().send(packet);
   }

   public static final void applyCurrentSequence(PredictiveAction packetCreator) {
      var startPredicting = (AutoCloseable)MinecraftHolder.getClientLevel().getBlockStatePredictionHandler().startPredicting();
      Throwable throwable = null;

      try {
         BlockStatePredictionHandler blockStatePredictionHandler = (BlockStatePredictionHandler)startPredicting;
         Packet<ServerGamePacketListener> predict = packetCreator.predict(blockStatePredictionHandler.currentSequence());
         applySend(predict);
         Unit instance = Unit.INSTANCE;
      } catch (Throwable exception) {
         throwable = exception;
         throw exception;
      } finally {
         AutoCloseableKt.closeFinally(startPredicting, throwable);
      }
   }

   public static final void applySendCommand(String command) {
      MinecraftHolder.getClientPacketListener().sendCommand(command);
   }
}