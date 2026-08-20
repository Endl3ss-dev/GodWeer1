package fun.volon.client.modules.impl.combat;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import fun.volon.api.events.Event;
import fun.volon.api.events.EventLink;
import fun.volon.api.events.Priority;
import fun.volon.api.events.implement.EventPacket;
import fun.volon.client.modules.Module;
import fun.volon.client.modules.settings.implement.BooleanSetting;
import fun.volon.client.modules.settings.implement.FloatSetting;

public class AutoMace extends Module {
    
    public static AutoMace INSTANCE = new AutoMace();
    
    private final BooleanSetting onlyOnGround = new BooleanSetting("Только на земле", true);
    private final FloatSetting switchDelay = new FloatSetting("Задержка переключения", 50f, 0f, 200f, 1f);
    
    private boolean isAttacking = false;
    private long lastSwitchTime = 0;
    
    public AutoMace() {
        super("AutoMace", "Автоматически бьет булавой и возвращает предмет", ModuleCategory.COMBAT);
        addSettings(onlyOnGround, switchDelay);
    }
    
    @EventLink(priority = Priority.HIGH)
    public void onEvent(Event event) {
        if (mc.player == null || mc.world == null) return;
        
        if (event instanceof EventPacket) {
            EventPacket packetEvent = (EventPacket) event;
            
            if (packetEvent.getType() == EventPacket.Type.SEND) {
                Packet<?> packet = packetEvent.getPacket();
                
                if (packet instanceof PlayerInteractEntityC2SPacket) {
                    if (isAttacking) return;
                    
                    // Проверка на землю если включена настройка
                    if (onlyOnGround.isState() && !mc.player.isOnGround()) {
                        return;
                    }
                    
                    // Проверяем задержку
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastSwitchTime < (long) switchDelay.getValue()) {
                        return;
                    }
                    
                    int maceSlot = findItemInHotbar(Items.MACE);
                    
                    if (maceSlot == -1) return;
                    
                    int currentSlot = mc.player.getInventory().selectedSlot;
                    
                    if (maceSlot == currentSlot) return;
                    
                    packetEvent.setCancelled(true);
                    
                    isAttacking = true;
                    lastSwitchTime = currentTime;
                    
                    // Переключаемся на булаву
                    mc.player.getInventory().selectedSlot = maceSlot;
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(maceSlot));
                    
                    // Отправляем пакет атаки
                    mc.player.networkHandler.sendPacket(packet);
                    
                    // Возвращаемся к предыдущему слоту
                    mc.player.getInventory().selectedSlot = currentSlot;
                    mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(currentSlot));
                    
                    isAttacking = false;
                }
            }
        }
    }
    
    private int findItemInHotbar(net.minecraft.item.Item item) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == item) {
                return i;
            }
        }
        return -1;
    }
    
    @Override
    public void onEnable() {
        super.onEnable();
        isAttacking = false;
        lastSwitchTime = 0;
    }
    
    @Override
    public void onDisable() {
        super.onDisable();
        isAttacking = false;
    }
}
