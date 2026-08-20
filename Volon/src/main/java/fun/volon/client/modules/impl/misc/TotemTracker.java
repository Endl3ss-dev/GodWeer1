package fun.volon.client.modules.impl.misc;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.enchantment.Enchantment;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventPacket;
import fun.volon.api.utils.chat.ChatUtils;
import fun.volon.api.utils.notification.NotificationManager;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;

public class TotemTracker extends Module {

    public static TotemTracker INSTANCE = new TotemTracker();

    private final BooleanSetting chatMessage = new BooleanSetting("Чат сообщение", true);
    private final BooleanSetting notification = new BooleanSetting("Уведомление", true);
    private final BooleanSetting checkEnchant = new BooleanSetting("Проверка зачарования", true);

    public TotemTracker() {
        super("TotemTracker", "Уведомляет при сносе тотема у игроков", ModuleCategory.MISC);
        addSettings(chatMessage, notification, checkEnchant);
    }

    @EventLink
    public void onPacket(EventPacket event) {
        if (mc.world == null || mc.player == null) return;
        if (event.getType() != EventPacket.Type.RECEIVE) return;

        if (!(event.getPacket() instanceof EntityStatusS2CPacket packet)) return;
        // Статус 35 = использование тотема
        if (packet.getStatus() != 35) return;

        mc.execute(() -> {
            if (mc.world == null) return;
            Entity entity = packet.getEntity(mc.world);
            if (!(entity instanceof AbstractClientPlayerEntity player)) return;
            // Не спамим о себе
            if (player == mc.player) return;

            String name = player.getName().getString();
            boolean enchanted = checkEnchant.isState() && hasEnchantedTotem(player);
            String enchantText = enchanted ? "да" : "нет";

            // Чат сообщение
            if (chatMessage.isState()) {
                StringBuilder sb = new StringBuilder();
                sb.append(name).append(" \u00A77потерял тотем!");
                if (checkEnchant.isState()) {
                    sb.append(" \u00A77Зачарован: ").append(enchanted ? "\u00A7aда" : "\u00A7cнет");
                }
                ChatUtils.sendMessage(sb.toString());
            }

            // Уведомление с иконкой тотема (как UseTracer)
            if (notification.isState()) {
                String msg;
                if (checkEnchant.isState()) {
                    msg = name + " потерял тотем | Зачарован: " + enchantText;
                } else {
                    msg = name + " потерял тотем";
                }
                NotificationManager.pushItemNotification(msg, new ItemStack(Items.TOTEM_OF_UNDYING));
            }
        });
    }

    /**
     * Проверяет, есть ли у игрока в инвентаре зачарованный тотем (тотем с любым зачарованием).
     * Проверяются все слоты: броня, оружие, инвентарь.
     */
    private boolean hasEnchantedTotem(AbstractClientPlayerEntity player) {
        // Проверяем обе руки
        if (isEnchantedTotem(player.getMainHandStack())) return true;
        if (isEnchantedTotem(player.getOffHandStack())) return true;
        // Проверяем броню
        for (ItemStack stack : player.getArmorItems()) {
            if (isEnchantedTotem(stack)) return true;
        }
        return false;
    }

    private boolean isEnchantedTotem(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() != Items.TOTEM_OF_UNDYING) return false;
        ItemEnchantmentsComponent enchants = stack.get(DataComponentTypes.ENCHANTMENTS);
        return enchants != null && !enchants.isEmpty();
    }
}
