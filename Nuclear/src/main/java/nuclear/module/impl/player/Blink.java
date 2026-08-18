package nuclear.module.impl.player;

import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CConfirmTeleportPacket;
import net.minecraft.network.play.client.CKeepAlivePacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import nuclear.control.events.Event;
import nuclear.control.events.impl.packet.EventPacket;
import nuclear.control.events.impl.render.EventRender;
import nuclear.module.api.Module;
import nuclear.module.api.ModuleCategory;
import nuclear.module.api.ModuleDescriptor;
import nuclear.module.settings.imp.BooleanSetting;
import nuclear.module.settings.imp.ColorSetting;
import nuclear.utils.render.ColorUtils;
import nuclear.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.List;

@ModuleDescriptor(
    name = "Blink",
    type = ModuleCategory.Player,
    desc = "Delays outgoing packets and releases them when the module is disabled"
)
public class Blink extends Module {
    private final BooleanSetting movementOnly = new BooleanSetting("Movement only", true);
    private final BooleanSetting renderStart = new BooleanSetting("Render start", true);
    private final ColorSetting startColor = new ColorSetting("Start color", ColorUtils.rgba(90, 180, 255, 180));

    private final List<IPacket<?>> bufferedPackets = new ArrayList<IPacket<?>>();
    private Vector3d startPosition;

    public Blink() {
        addSettings(movementOnly, renderStart, startColor);
    }

    @Override
    public boolean onEvent(Event event) {
        if (mc.player == null || mc.world == null) {
            return false;
        }

        if (event instanceof EventPacket) {
            handlePacket((EventPacket) event);
        } else if (event instanceof EventRender) {
            handleRender((EventRender) event);
        }
        return false;
    }

    @Override
    protected void onEnable() {
        bufferedPackets.clear();
        if (mc.player != null) {
            startPosition = mc.player.getPositionVec();
        }
    }

    @Override
    protected void onDisable() {
        if (mc.player != null && mc.player.connection != null) {
            for (IPacket<?> packet : bufferedPackets) {
                mc.player.connection.sendPacketWithoutEvent(packet);
            }
        }
        bufferedPackets.clear();
        startPosition = null;
    }

    private void handlePacket(EventPacket event) {
        if (!event.isSendPacket()) {
            return;
        }

        IPacket<?> packet = event.getPacket();
        if (packet == null || !shouldBuffer(packet)) {
            return;
        }

        bufferedPackets.add(packet);
        event.cancel();
    }

    private boolean shouldBuffer(IPacket<?> packet) {
        if (!movementOnly.get()) {
            return !(packet instanceof CKeepAlivePacket) && !(packet instanceof CConfirmTeleportPacket);
        }

        return packet instanceof CPlayerPacket;
    }

    private void handleRender(EventRender render) {
        if (!renderStart.get() || !render.isRender3D() || startPosition == null) {
            return;
        }

        Vector3d view = mc.getRenderManager().info.getProjectedView();
        AxisAlignedBB box = new AxisAlignedBB(
            startPosition.x - 0.3,
            startPosition.y,
            startPosition.z - 0.3,
            startPosition.x + 0.3,
            startPosition.y + 1.8,
            startPosition.z + 0.3
        ).offset(-view.x, -view.y, -view.z);

        RenderUtils.Render3D.drawBox(box, startColor.get());
    }
}
