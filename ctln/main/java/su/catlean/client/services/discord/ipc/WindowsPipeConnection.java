// deobf by pusia
package su.catlean.client.services.discord.ipc;

import su.catlean.utils.KotlinCompat;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.concurrent.ThreadsKt;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import su.catlean.api.data.Packet;
import su.catlean.api.data.PacketPayload;
import su.catlean.client.services.sources.JsonFormats;

public final class WindowsPipeConnection extends IpcConnection {
   @NotNull
   private final RandomAccessFile randomAccessFile;

   public WindowsPipeConnection(String name) {
      try {
         this.randomAccessFile = new RandomAccessFile(name, "rw");
      } catch (java.io.FileNotFoundException exception) {
         throw new RuntimeException(exception);
      }
      KotlinCompat.thread(true, false, null, "Discord IPC - Pipe thread", 0, () -> {
         this.applyAllocate();
         return Unit.INSTANCE;
      }, 22, null);
   }

   @Override
   public void applyArray(ByteBuffer buffer) {
      WindowsPipeConnection thisValue = this;

      Object instance;
      try {
         instance = thisValue;
         ((WindowsPipeConnection)instance).randomAccessFile.write(buffer.array());
         instance = Unit.INSTANCE;
      } catch (Throwable exception) {
         instance = ResultKt.createFailure(exception);
      }

      if (!(instance instanceof Unit)) {
         DiscordPresence.INSTANCE.getUnit();
      }
   }

   private final void applyAllocate() {
      ByteBuffer allocate = ByteBuffer.allocate(4);

      while (true) {
         this.applyGetChannel(allocate);
         IpcOpcode reverseBytes = (IpcOpcode)IpcOpcode.getEnumEntries().get(Integer.reverseBytes(allocate.getInt(0)));
         this.applyGetChannel(allocate);
         int reverseBytes2 = Integer.reverseBytes(allocate.getInt(0));
         ByteBuffer allocate2 = ByteBuffer.allocate(reverseBytes2);
         this.applyGetChannel(allocate2);
         String text = Charset.defaultCharset().decode(allocate2.rewind()).toString();
         DiscordPresence instance = DiscordPresence.INSTANCE;
         Json json = JsonFormats.getJson();
         json.getSerializersModule();
         instance.computeUnit(
            new Packet(reverseBytes, (PacketPayload)json.decodeFromString((DeserializationStrategy)PacketPayload.companion.getKSerializer(), text))
         );
      }
   }

   private final void applyGetChannel(ByteBuffer byteBuffer) {
      byteBuffer.rewind();

      try {
         while (this.randomAccessFile.length() < byteBuffer.remaining()) {
            Thread.onSpinWait();
            Thread.sleep(100L);
         }

         while (byteBuffer.hasRemaining()) {
            this.randomAccessFile.getChannel().read(byteBuffer);
         }
      } catch (java.io.IOException | InterruptedException exception) {
         if (exception instanceof InterruptedException) {
            Thread.currentThread().interrupt();
         }
         throw new RuntimeException(exception);
      }
   }

   @Override
   public void applyClose() {
      WindowsPipeConnection thisValue = this;

      try {
         WindowsPipeConnection windowsPipeConnection = thisValue;
         windowsPipeConnection.randomAccessFile.close();
         Unit instance = Unit.INSTANCE;
      } catch (Throwable exception) {
         Object failure = ResultKt.createFailure(exception);
      }
   }
}