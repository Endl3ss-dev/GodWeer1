package org.privates.client.managers.events.other;

import com.sheluvparis.authenticator.lib.packet.type.ServerPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.privates.client.api.events.Event;

@Getter
@RequiredArgsConstructor
public final class BackendPacketEvent extends Event {
    private final ServerPacket packet;
}