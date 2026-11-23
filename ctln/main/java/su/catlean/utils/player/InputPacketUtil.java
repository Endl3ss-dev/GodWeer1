// deobf by pusia
package su.catlean.utils.player;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import com.viaversion.viaversion.api.protocol.packet.PacketType;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.Types;
import com.viaversion.viaversion.protocols.v1_21_4to1_21_5.packet.ServerboundPackets1_21_5;
import com.viaversion.viaversion.protocols.v1_21_5to1_21_6.Protocol1_21_5To1_21_6;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import org.jetbrains.annotations.NotNull;
import su.catlean.utils.MinecraftHolder;
import su.catlean.utils.network.PacketUtil;

public final class InputPacketUtil {
   @NotNull
   public static final InputPacketUtil INSTANCE = new InputPacketUtil();

   private InputPacketUtil() {
   }

   public final void applyWrite(boolean flag) {
      boolean var26;
      label42: {
         if (FabricLoader.getInstance().isModLoaded("viafabricplus")) {
            label39: {
               UserConnection var10000 = (UserConnection)CollectionsKt.firstOrNull(Via.getManager().getConnectionManager().getConnections());
               if (var10000 != null) {
                  ProtocolInfo var23 = var10000.getProtocolInfo();
                  if (var23 != null) {
                     ProtocolPipeline var24 = var23.getPipeline();
                     if (var24 != null) {
                        var26 = var24.contains(Protocol1_21_5To1_21_6.class);
                        break label39;
                     }
                  }
               }

               var26 = false;
            }

            if (var26) {
               var26 = true;
               break label42;
            }
         }

         var26 = false;
      }

      boolean var16 = var26;
      if (!var16) {
         Packet var13 = (Packet)(new ServerboundPlayerInputPacket(ClientInputUtil.computeClientInput(MinecraftHolder.getLocalPlayer().input, flag).keyPresses));
         PacketUtil.applySend(var13);
      } else {
         InputPacketUtil var17 = this;

         Object var18;
         try {
            var18 = var17;
            PacketWrapper var20 = PacketWrapper.create(
               (PacketType)ServerboundPackets1_21_5.PLAYER_COMMAND,
               (UserConnection)CollectionsKt.firstOrNull(Via.getManager().getConnectionManager().getConnections())
            );
            var20.write((Type)Types.VAR_INT, MinecraftHolder.getLocalPlayer().getId());
            var20.write((Type)Types.VAR_INT, flag ? 0 : 1);
            var20.write((Type)Types.VAR_INT, 0);
            var20.scheduleSendToServer(Protocol1_21_5To1_21_6.class);
            var18 = Unit.INSTANCE;
         } catch (Throwable var21) {
            var18 = ResultKt.createFailure(var21);
         }

      }

      MinecraftHolder.getLocalPlayer().setShiftKeyDown(flag);
      MinecraftHolder.getMinecraft().options.keyShift.setDown(flag);
   }
}