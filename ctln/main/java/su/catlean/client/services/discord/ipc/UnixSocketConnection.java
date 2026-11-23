// deobf by pusia
package su.catlean.client.services.discord.ipc;

import su.catlean.utils.KotlinCompat;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.concurrent.ThreadsKt;
import kotlin.text.Charsets;
import kotlinx.serialization.DeserializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Packet;
import su.catlean.api.data.PacketPayload;
import su.catlean.client.services.sources.JsonFormats;
import su.catlean.utils.UnusedObject;

public final class UnixSocketConnection extends IpcConnection {
   private final Selector selector;
   private final SocketChannel socketChannel;
   @Nullable
   private IpcOpcode ipcOpcode;
   @Nullable
   private Integer integer;
   @Nullable
   private ByteBuffer byteBuffer;
   private final ByteBuffer byteBuffer2;

   public UnixSocketConnection(String name) {
      try {
         this.selector = Selector.open();
      } catch (java.io.IOException exception) {
         throw new RuntimeException(exception);
      }
      try {
         this.socketChannel = SocketChannel.open(UnixDomainSocketAddress.of(name));
         this.byteBuffer2 = ByteBuffer.allocate(4);
         this.socketChannel.configureBlocking(false);
         this.socketChannel.register(this.selector, 1);
      } catch (java.io.IOException exception) {
         throw new RuntimeException(exception);
      }
      KotlinCompat.thread(true, false, null, "Discord IPC - Socket thread", 0, () -> {
         this.applyRemoveIf();
         return Unit.INSTANCE;
      }, 22, null);
   }

   private final void applyRemoveIf() {
      UnixSocketConnection thisValue = this;

      try {
         UnixSocketConnection unixSocketConnection = thisValue;

         while (unixSocketConnection.selector.select() > 0) {
            unixSocketConnection.selector.selectedKeys().removeIf(arg -> {
               if (arg.isReadable()) {
                  Packet packet = unixSocketConnection.computePacket(unixSocketConnection.socketChannel);
                  if (packet != null) {
                     DiscordPresence.INSTANCE.computeUnit(packet);
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
            });
         }

         Unit instance = Unit.INSTANCE;
      } catch (Throwable exception) {
         Object failure = ResultKt.createFailure(exception);
      }
   }

   @Override
   public void applyArray(ByteBuffer buffer) {
      UnixSocketConnection thisValue = this;

      try {
         UnixSocketConnection unixSocketConnection = thisValue;
         Integer write = unixSocketConnection.socketChannel.write(buffer);
      } catch (Throwable exception) {
         Object failure = ResultKt.createFailure(exception);
      }
   }

   @Override
   public void applyClose() {
      UnixSocketConnection thisValue = this;

      try {
         UnixSocketConnection unixSocketConnection = thisValue;
         unixSocketConnection.socketChannel.close();
         unixSocketConnection.selector.close();
         Unit instance = Unit.INSTANCE;
      } catch (Throwable exception) {
         Object failure = ResultKt.createFailure(exception);
      }
   }

   private final Packet computePacket(ReadableByteChannel readableByteChannel) {
      if (this.ipcOpcode == null) {
         if (!this.checkFlip(readableByteChannel)) {
            return null;
         }

         this.ipcOpcode = (IpcOpcode)CollectionsKt.getOrNull((List)IpcOpcode.getEnumEntries(), UnusedObject.computeValue(this.byteBuffer2.getInt(0)));
         this.byteBuffer2.clear();
      }

      if (this.integer == null) {
         if (!this.checkFlip(readableByteChannel)) {
            return null;
         }

         this.integer = UnusedObject.computeValue(this.byteBuffer2.getInt(0));
         this.byteBuffer = ByteBuffer.allocate(this.integer);
         this.byteBuffer2.clear();
      }

      try {
         readableByteChannel.read(this.byteBuffer);
      } catch (java.io.IOException exception) {
         return null;
      }
      if (this.byteBuffer.hasRemaining()) {
         return null;
      } else {
         if (this.byteBuffer != null) {
            this.byteBuffer.flip();
         }

         IpcOpcode ipcOpcode2 = this.ipcOpcode;
         Json json = JsonFormats.getJson();
         String text = Charsets.UTF_8.decode(this.byteBuffer).toString();
         json.getSerializersModule();
         Packet packet = new Packet(ipcOpcode2, (PacketPayload)json.decodeFromString((DeserializationStrategy)PacketPayload.companion.getKSerializer(), text));
         this.ipcOpcode = null;
         this.integer = null;
         this.byteBuffer = null;
         return packet;
      }
   }

   private final boolean checkFlip(ReadableByteChannel readableByteChannel) {
      try {
         readableByteChannel.read(this.byteBuffer2);
      } catch (java.io.IOException exception) {
         return false;
      }
      if (this.byteBuffer2.hasRemaining()) {
         return false;
      } else {
         this.byteBuffer2.flip();
         return true;
      }
   }
}