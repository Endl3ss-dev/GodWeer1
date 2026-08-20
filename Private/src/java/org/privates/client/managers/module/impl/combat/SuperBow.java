package org.privates.client.managers.module.impl.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket.Action;
import net.minecraft.network.play.client.CPlayerPacket;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.SliderSetting;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "SuperBow", category = Category.COMBAT, description = "спастил с рок стара")
public class SuperBow extends Module {

    private final SliderSetting power = new SliderSetting(this, "Сила", 10, 10, 50, 1);

    // Список всех метательных предметов
    private static final Item[] THROWABLE_ITEMS = new Item[]{
            Items.BOW,
            Items.TRIDENT,
            Items.SNOWBALL,
            Items.EGG,
            Items.ENDER_PEARL,
            Items.EXPERIENCE_BOTTLE,
            Items.SPLASH_POTION,
            Items.LINGERING_POTION
    };

    @EventHandler
    public void onPacket(PacketEvent event) {
        if (!(event.getPacket() instanceof CPlayerDiggingPacket packet)) return;
        if (packet.getAction() != Action.RELEASE_USE_ITEM) return;
        if (mc.player == null) return;

        Item heldItem = mc.player.getActiveItemStack().getItem();
        if (!isThrowable(heldItem)) return;

        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_SPRINTING));

        for (int i = 0; i < power.getValue(); i++) {
            sendPosRot(mc.player.getPosY() + 1e-10, false);
            sendPosRot(mc.player.getPosY() - 1e-10, true);
        }
    }

    private boolean isThrowable(Item item) {
        for (Item i : THROWABLE_ITEMS) {
            if (i == item) return true;
        }
        return false;
    }

    private void sendPosRot(double y, boolean ground) {
        mc.player.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(
                mc.player.getPosX(),
                y,
                mc.player.getPosZ(),
                mc.player.rotationYaw,
                mc.player.rotationPitch,
                ground
        ));
    }
}
