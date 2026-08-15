package antileak.base.client.modules.impl.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventPacket;
import antileak.base.api.utils.chat.ChatUtils;
import antileak.base.client.modules.Module;

public class TotemTracker extends Module {

    public static TotemTracker INSTANCE = new TotemTracker();

    public TotemTracker() {
        super("TotemTracker", "Отслеживает использование тотемов игроками", ModuleCategory.MISC);
    }

    @EventLink
    public void onPacket(EventPacket event) {
        if (event.getType() != EventPacket.Type.RECEIVE) return;
        if (!(event.getPacket() instanceof EntityStatusS2CPacket packet)) return;
        if (mc.world == null) return;

        if (packet.getStatus() == 35) {
            Entity entity = packet.getEntity(mc.world);
            if (entity instanceof PlayerEntity player) {
                ItemStack main = player.getMainHandStack();
                ItemStack off = player.getOffHandStack();
                ItemStack totem = null;

                if (main.getItem() == Items.TOTEM_OF_UNDYING) {
                    totem = main;
                } else if (off.getItem() == Items.TOTEM_OF_UNDYING) {
                    totem = off;
                }

                if (totem != null) {
                    boolean enchanted = totem.hasEnchantments();
                    String name = player.getName().getString();
                    String totemColor = enchanted ? "§a" : "§c";
                    String typeText = enchanted ? "зачарованный" : "не зачарованный";
                    ChatUtils.sendMessage("Игрок " + name + "§f потерял " + totemColor + typeText + "§f тотем");
                }
            }
        }
    }
}
