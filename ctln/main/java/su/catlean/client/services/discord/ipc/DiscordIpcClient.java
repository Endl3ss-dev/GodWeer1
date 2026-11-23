// deobf by pusia
package su.catlean.client.services.discord.ipc;

import su.catlean.utils.KotlinCompat;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Locale;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.concurrent.ThreadsKt;
import kotlin.text.StringsKt;
import kotlinx.serialization.SerializationStrategy;
import kotlinx.serialization.json.Json;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.catlean.api.data.Activity;
import su.catlean.api.data.FrameArgs;
import su.catlean.api.data.FramePayload;
import su.catlean.api.data.HandshakePayload;
import su.catlean.api.data.IPCUser;
import su.catlean.api.data.Packet;
import su.catlean.client.services.sources.JsonFormats;

public class DiscordIpcClient {
   @NotNull
   private final String[] texts;
   @Nullable
   private IpcConnection ipcConnection;
   private boolean flag;
   @Nullable
   private Activity activity;
   private final long time;
   @NotNull
   private final IPCUser iPCUser;
   @NotNull
   private IPCUser iPCUser2;

   public DiscordIpcClient() {
      String[] var3 = new String[]{"XDG_RUNTIME_DIR", "TMPDIR", "TMP", "TEMP"};
      this.texts = var3;
      this.time = Instant.now().getEpochSecond();
      this.iPCUser = new IPCUser(null, null, null, 7, null);
      this.iPCUser2 = this.iPCUser;
   }

   public final long getTime() {
      return this.time;
   }

   @NotNull
   public final IPCUser getIPCUser() {
      return this.iPCUser;
   }

   @NotNull
   public final IPCUser getIPCUser2() {
      return this.iPCUser2;
   }

   public final boolean isFlag() {
      return this.ipcConnection != null;
   }

   private final int getValue() {
      return Integer.parseInt(KotlinCompat.substringBefore(ManagementFactory.getRuntimeMXBean().getName(), (char)64, null, 2, null));
   }

   public final void update3() {
      if (this.flag) {
         this.applyEncodeToJsonElement();
         IpcDrafts.INSTANCE.update();
      }
   }

   public final void applyEncodeToJsonElement(long time) {
      if (this.ipcConnection != null) {
         IpcConnection var19 = this.ipcConnection;
         this.getUnit();
      }

      this.ipcConnection = this.getIpcConnection();
      if (this.ipcConnection != null) {
         IpcConnection var23 = this.ipcConnection;
         IpcOpcode var10001 = IpcOpcode.HANDSHAKE;
         Json var21 = JsonFormats.getJson();
         HandshakePayload var22 = new HandshakePayload(1, String.valueOf(time));
         var21.getSerializersModule();
         var23.applyPut(var10001, var21.encodeToJsonElement((SerializationStrategy)HandshakePayload.companion.getKSerializer(), var22));
         this.update();
      }
   }

   public final void updateActivity(Activity presence) {
      this.activity = presence;
   }

   @Nullable
   public final Unit getUnit() {
      Unit var10000;
      if (this.ipcConnection != null) {
         IpcConnection var2 = this.ipcConnection;
         var2.applyClose();
         this.ipcConnection = null;
         this.flag = false;
         this.activity = null;
         this.iPCUser2 = this.iPCUser;
         var10000 = Unit.INSTANCE;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   private final void applyEncodeToJsonElement() {
      if (this.ipcConnection != null) {
         IpcConnection var13 = this.ipcConnection;
         if (this.activity != null) {
            Activity var15 = this.activity;
            IpcOpcode var10001 = IpcOpcode.FRAME;
            Json var17 = JsonFormats.getJson();
            FramePayload var18 = new FramePayload(IpcCommand.SET_ACTIVITY, new FrameArgs(this.getValue(), var15));
            var17.getSerializersModule();
            var13.applyPut(var10001, var17.encodeToJsonElement((SerializationStrategy)FramePayload.companion.getKSerializer(), var18));
         }
      }

      this.activity = null;
   }

   @Nullable
   public final Unit computeUnit(Packet packet) {
      return switch (DiscordIpcClient.WhenMappings.values[packet.getOpcode().ordinal()]) {
         case 1 -> {
            if (packet.getData().getIpcEvent() == IpcEvent.ERROR) {
               System.out
                  .println(
                     "Discord IPC error " + packet.getData().getPacketData().getInteger() + " with message: " + packet.getData().getPacketData().getText()
                  );
               yield Unit.INSTANCE;
            } else if (packet.getData().getIpcCommand() == IpcCommand.DISPATCH) {
               this.flag = true;
               this.iPCUser2 = packet.getData().getPacketData().getIPCUser();
               if (this.activity != null) {
                  Activity var8 = this.activity;
                  this.applyEncodeToJsonElement();
                  yield Unit.INSTANCE;
               } else {
                  yield null;
               }
            } else {
               yield Unit.INSTANCE;
            }
         }
         case 2 -> {
            System.out
               .println("Discord IPC error " + packet.getData().getPacketData().getInteger() + " with message: " + packet.getData().getPacketData().getText());
            yield this.getUnit();
         }
         default -> Unit.INSTANCE;
      };
   }

   private final IpcConnection getIpcConnection() {
      String var10 = System.getProperty("os.name");
      String var9 = var10.toLowerCase(Locale.getDefault());
      if (KotlinCompat.contains(var9, "win", false, 2, null)) {
         byte var21 = 10;

         for (int var23 = 0; var23 < var21; var23++) {
            int var25 = var23;
            DiscordIpcClient var29 = this;

            try {
               DiscordIpcClient var31 = var29;
               return new WindowsPipeConnection("\\\\?\\pipe\\discord-ipc-" + var25);
            } catch (Throwable var18) {
               ResultKt.createFailure(var18);
            }
         }
      } else {
         String[] var11 = this.texts;
         int var12 = 0;
         int var13 = var11.length;

         String var10000;
         while (true) {
            if (var12 >= var13) {
               var10000 = null;
               break;
            }

            String var14 = var11[var12];
            var10000 = System.getenv(var14);
            if (var10000 != null) {
               break;
            }

            var12++;
         }

         var10 = var10000;
         byte var22 = 10;

         for (int var24 = 0; var24 < var22; var24++) {
            var13 = var24;
            DiscordIpcClient var30 = this;

            try {
               DiscordIpcClient var16 = var30;
               String var10002 = var10;
               if (var10 == null) {
                  var10002 = "/tmp";
               }

               UnixSocketConnection var33 = new UnixSocketConnection(var10002 + "/discord-ipc-" + var13);
               return var33;
            } catch (Throwable var19) {
               ResultKt.createFailure(var19);
            }
         }
      }

      return null;
   }

   private final void update() {
      KotlinCompat.thread(true, true, null, "Discord IPC - Update thread", 0, () -> {
         while (this.isFlag()) {
            DiscordIpcClient var9 = this;

            try {
               var9.update3();
               Unit var13 = Unit.INSTANCE;
            } catch (Throwable var12) {
               Object var10 = ResultKt.createFailure(var12);
            }

            try {
               Thread.sleep(5000L);
            } catch (InterruptedException exception) {
               Thread.currentThread().interrupt();
               break;
            }
         }

         return Unit.INSTANCE;
      }, 20, null);
   }

   public static final class WhenMappings {
      public static final int[] values;

      static {
         int[] var0 = new int[IpcOpcode.values().length];

         try {
            var0[IpcOpcode.FRAME.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
         }

         try {
            var0[IpcOpcode.CLOSE.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
         }

         values = var0;
      }
   }
}