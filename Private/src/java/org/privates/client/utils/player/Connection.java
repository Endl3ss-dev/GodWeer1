package org.privates.client.utils.player;

import com.mojang.authlib.GameProfile;
import lombok.SneakyThrows;
import net.minecraft.client.network.login.ClientLoginNetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ProtocolType;
import net.minecraft.network.handshake.client.CHandshakePacket;
import net.minecraft.network.login.client.CLoginStartPacket;
import org.privates.client.Excellent;
import org.privates.common.impl.fastping.InetAddressPatcher;

import java.net.InetAddress;

import static org.privates.client.api.interfaces.IMinecraft.mc;

public final class Connection extends AbstractData {
    public static String ip = "localhost";
    public static int port = 0;

    @SneakyThrows
    public static void connectToServer(String ip, int port, GameProfile profile) {
        Connection.ip = ip;
        Connection.port = port;

        InetAddress inetaddress = InetAddress.getByName(ip);
        inetaddress = InetAddressPatcher.patch(ip, inetaddress);
        NetworkManager networkManager = NetworkManager.createNetworkManagerAndConnect(inetaddress, port, mc.gameSettings.isUsingNativeTransport());
        networkManager.setNetHandler(new ClientLoginNetHandler(networkManager, mc, mc.currentScreen, (x) -> Excellent.log(x.getString())));
        networkManager.sendPacket(new CHandshakePacket(ip, port, ProtocolType.LOGIN));
        networkManager.sendPacket(new CLoginStartPacket(profile));
    }
}