package mods.viaversion.viamcp;

import com.viaversion.viabackwards.protocol.v1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_1to1_16_2.packet.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.packet.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.v1_16_4to1_17.packet.ServerboundPackets1_17;
import lombok.Getter;
import mods.viaversion.vialoadingbase.ViaLoadingBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import mods.viaversion.viamcp.gui.VersionSelectScreen;

import java.io.File;

public class ViaMCP {
    public final static int NATIVE_VERSION = 754;
    public static ViaMCP INSTANCE;
    @Getter
    private final VersionSelectScreen viaScreen;

    public static void create() {
        INSTANCE = new ViaMCP();
    }

    public ViaMCP() {
        ViaLoadingBase.ViaLoadingBaseBuilder.create().runDirectory(new File(Minecraft.getInstance().gameDir, "ViaMCP")).nativeVersion(NATIVE_VERSION).build();
        this.viaScreen = new VersionSelectScreen(Minecraft.getInstance().fontRenderer, 5, 5, 100, 20, ITextComponent.getTextComponentOrEmpty(ProtocolVersion.getProtocol(NATIVE_VERSION).getName()));
        fixTransactions();
    }

    private void fixTransactions() {
        final Protocol1_17To1_16_4 protocol = Via.getManager().getProtocolManager().getProtocol(Protocol1_17To1_16_4.class);
        protocol.registerClientbound(ClientboundPackets1_17.PING, ClientboundPackets1_16_2.CONTAINER_ACK, wrapper -> {}, true);
        protocol.registerServerbound(ServerboundPackets1_16_2.CONTAINER_ACK, ServerboundPackets1_17.PONG, wrapper -> {}, true);
    }
}