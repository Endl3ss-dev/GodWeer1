// deobf by pusia
package su.catlean.client.services.discord.ipc;

import kotlinx.serialization.json.JsonPrimitive;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import kotlin.collections.MapsKt;
import kotlin.text.Charsets;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonElement;
import kotlinx.serialization.json.JsonElementKt;
import kotlinx.serialization.json.JsonObject;
import su.catlean.client.services.sources.JsonFormats;
import su.catlean.utils.UnusedObject;

public abstract class IpcConnection {
   public IpcConnection() {
   }

   public final void applyPut(IpcOpcode opcode, JsonElement element) {
      Map<String, JsonPrimitive> mutableMap = MapsKt.toMutableMap((Map)JsonElementKt.getJsonObject(element));
      mutableMap.put("nonce", JsonElementKt.JsonPrimitive(UUID.randomUUID().toString()));
      Json json = JsonFormats.getJson();
      JsonObject jsonObject = new JsonObject(mutableMap);
      json.getSerializersModule();
      String encodeToString = json.encodeToString((SerializationStrategy)JsonObject.Companion.serializer(), jsonObject);
      byte[] bytes = encodeToString.getBytes(Charsets.UTF_8);
      ByteBuffer allocate = ByteBuffer.allocate(bytes.length + 8);
      allocate.putInt(UnusedObject.computeValue(opcode.ordinal()));
      allocate.putInt(UnusedObject.computeValue(bytes.length));
      allocate.put(bytes);
      allocate.flip();
      this.applyArray(allocate);
   }

   public abstract void applyArray(ByteBuffer byteBuffer);

   public abstract void applyClose();
}