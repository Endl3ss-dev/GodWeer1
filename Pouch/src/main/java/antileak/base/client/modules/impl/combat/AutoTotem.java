package antileak.base.client.modules.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventPacket;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.impl.movement.Sprint;
import antileak.base.client.modules.settings.implement.BooleanSetting;
import antileak.base.client.modules.settings.implement.FloatSetting;
import antileak.base.api.utils.player.InventoryUtils;

public class AutoTotem extends Module {
    public static final AutoTotem INSTANCE = new AutoTotem();
    private final BooleanSetting returnItem = new BooleanSetting("Возврат предмета", true);
    private final BooleanSetting preferUnenchanted = new BooleanSetting("Сохранять зачарованный", true);
    private final FloatSetting health = new FloatSetting("Здоровье", 4.0F, 0.0F, 20.0F, 0.5F);
    private final BooleanSetting elytra = new BooleanSetting("Элитры", true);
    private final FloatSetting elytraHealth;
    private final BooleanSetting fall = new BooleanSetting("Падение", true);
    private final FloatSetting fallDistance;
    private final BooleanSetting crystals = new BooleanSetting("Кристалы", true);
    private final FloatSetting healthWithBall;
    private int cooldownTicks;
    private ItemStack previousStack;
    private boolean totemWasPopped;

    public AutoTotem() {
        super("AutoTotem", "Автоматически берёт тотем в опасности", ModuleCategory.COMBAT);
        this.elytraHealth = new FloatSetting("Здоровье на элитрах", 10.0F, 0.0F, 20.0F, 0.5F);
        this.fallDistance = new FloatSetting("Количество блоков", 20.0F, 10.0F, 50.0F, 0.1F);
        this.healthWithBall = new FloatSetting("Здоровье с шаром", 10.0F, 0.0F, 20.0F, 0.5F);
        this.cooldownTicks = 0;
        this.previousStack = null;
        this.totemWasPopped = false;
        addSettings(returnItem, preferUnenchanted, health, elytra, elytraHealth, fall, fallDistance, crystals, healthWithBall);
    }

    @EventLink
    public void onPlayerTick(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;
        if (this.cooldownTicks > 0) --this.cooldownTicks;

        ItemStack current = mc.player.getOffHandStack().isEmpty() ? null : mc.player.getOffHandStack();

        if (this.shouldUseTotem()) {
            boolean needsTotem = false;
            
            // Проверяем нужно ли менять тотем
            if (current == null || current.getItem() != Items.TOTEM_OF_UNDYING) {
                // Вообще нет тотема - нужно взять
                needsTotem = true;
            } else if (this.preferUnenchanted.isState() && current.hasEnchantments()) {
                // Настройка "Сохранять зачарованный" включена, в руке зачарованный - проверяем есть ли обычный
                int unenchantedSlot = findUnenchantedTotemSlot();
                if (unenchantedSlot != -1) {
                    needsTotem = true; // Есть обычный - нужно заменить
                }
            }
            
            if (needsTotem) {
                int slot = this.findBestTotemSlot();
                if (slot != -1 && mc.currentScreen == null) {
                    if (this.previousStack == null || this.previousStack.isEmpty()) {
                        this.previousStack = current;
                        this.totemWasPopped = false;
                    }

                    boolean wasSprinting = false;
                    if (mc.player.isSprinting()) {
                        mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
                        mc.player.setSprinting(false);
                        mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                        if (!Sprint.INSTANCE.isEnable()) {
                            mc.options.sprintKey.setPressed(false);
                        }
                        wasSprinting = true;
                    }

                    this.swapToOffhand(InventoryUtils.toContainerSlot(slot));
                    if (wasSprinting) {
                        mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
                    }

                    this.cooldownTicks = 3;
                }
            }
        } else if (this.returnItem.isState() && this.previousStack != null && this.cooldownTicks == 0 && !this.previousStack.isEmpty()) {
            // Возвращаем предмет только если:
            // 1. Тотем сработал (был использован)
            // 2. ИЛИ опасность миновала И здоровье восстановилось выше порога + 2 хп (для гистерезиса)
            float currentHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            float healthThreshold = this.health.getValue().floatValue() + 2.0F; // Добавляем 2 хп чтобы избежать постоянных свапов
            
            boolean safeToReturn = !this.shouldUseTotem() && currentHealth > healthThreshold;
            
            if (this.totemWasPopped || safeToReturn) {
                int slot = this.findItemSlot(this.previousStack);
                if (slot != -1) {
                    boolean wasSprinting = false;
                    if (mc.player.isSprinting()) {
                        mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(false, false, false, false, false, false, false)));
                        mc.player.setSprinting(false);
                        mc.getNetworkHandler().sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
                        if (!Sprint.INSTANCE.isEnable()) {
                            mc.options.sprintKey.setPressed(false);
                        }
                        wasSprinting = true;
                    }

                    this.swapToOffhand(InventoryUtils.toContainerSlot(slot));
                    if (wasSprinting) {
                        mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
                    }

                    this.cooldownTicks = 3;
                }

                this.previousStack = null;
                this.totemWasPopped = false;
            }
        }
    }

    @EventLink
    public void onPacket(EventPacket e) {
        if (!e.getType().equals(EventPacket.Type.RECEIVE)) return;
        Packet<?> packet = e.getPacket();
        if (packet instanceof EntityStatusS2CPacket status) {
            if (status.getStatus() == 35 && status.getEntity(mc.world) == mc.player) {
                this.cooldownTicks = 5;
                this.totemWasPopped = true; // Тотем сработал
            }
        }
    }

    private void swapToOffhand(int slot) {
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slot, 40, SlotActionType.SWAP, mc.player);
        if (mc.currentScreen != null) {
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
        }
    }

    /**
     * Находит только незачарованный тотем (для проверки есть ли замена зачарованному)
     */
    private int findUnenchantedTotemSlot() {
        if (mc.player == null) return -1;
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                boolean isEnchanted = stack.hasEnchantments() || stack.hasGlint();
                if (!isEnchanted) {
                    return i; // Нашли обычный тотем
                }
            }
        }
        
        return -1; // Обычных тотемов нет
    }

    /**
     * Находит лучший слот с тотемом:
     * - Если включено "Сохранять зачарованный", ищет сначала незачарованный тотем
     * - Если незачарованный не найден, возвращает зачарованный
     */
    private int findBestTotemSlot() {
        if (mc.player == null) return -1;
        
        int unenchantedSlot = -1;
        int enchantedSlot = -1;
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == Items.TOTEM_OF_UNDYING) {
                boolean isEnchanted = stack.hasEnchantments() || stack.hasGlint();
                
                if (!isEnchanted) {
                    // Нашли незачарованный тотем
                    if (unenchantedSlot == -1) {
                        unenchantedSlot = i;
                    }
                } else {
                    // Нашли зачарованный тотем
                    if (enchantedSlot == -1) {
                        enchantedSlot = i;
                    }
                }
            }
        }
        
        // Если настройка "Сохранять зачарованный" включена:
        // Приоритет: обычный тотем > зачарованный тотем
        if (this.preferUnenchanted.isState()) {
            if (unenchantedSlot != -1) {
                return unenchantedSlot; // Берём обычный
            }
            return enchantedSlot; // Если нет обычного - берём зачарованный
        } else {
            // Если настройка выключена - берём любой (первый найденный)
            if (unenchantedSlot != -1) return unenchantedSlot;
            return enchantedSlot;
        }
    }

    /**
     * Находит слот с предметом, совпадающим с сохраненным:
     * - Если включено "Сохранять зачарованный", ищет сначала незачарованную версию
     * - Если не найдена, ищет точное совпадение (включая зачарования)
     */
    private int findItemSlot(ItemStack targetStack) {
        if (mc.player == null || targetStack == null || targetStack.isEmpty()) return -1;
        
        int enchantedSlot = -1;
        
        for (int i = 0; i < 36; i++) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (!stack.isEmpty() && stack.getItem() == targetStack.getItem()) {
                boolean targetHasEnchants = targetStack.hasEnchantments();
                boolean currentHasEnchants = stack.hasEnchantments();
                
                if (this.preferUnenchanted.isState() && targetHasEnchants) {
                    // Ищем незачарованную версию предмета
                    if (!currentHasEnchants) {
                        return i;
                    } else if (enchantedSlot == -1) {
                        enchantedSlot = i;
                    }
                } else {
                    // Просто ищем тот же тип предмета
                    return i;
                }
            }
        }
        
        // Если незачарованная версия не найдена, возвращаем зачарованную
        return enchantedSlot;
    }

    private boolean shouldUseTotem() {
        if (mc.player.isInCreativeMode() || mc.player.isSpectator()) return false;

        float healthValue = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (healthValue <= this.health.getValue().floatValue()) return true;

        if (this.fall.isState() && mc.player.fallDistance >= this.fallDistance.getValue().floatValue() && !mc.player.isGliding()) return true;

        if (this.elytra.isState() && mc.player.getInventory().getArmorStack(2).getItem() == Items.ELYTRA && healthValue <= this.elytraHealth.getValue().floatValue()) return true;

        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof EndCrystalEntity crystal)) continue;
            if (!this.crystals.isState()) continue;
            if (!(mc.player.getEyePos().distanceTo(crystal.getBoundingBox().getCenter()) <= 5.0D)) continue;
            if (mc.player.getY() < crystal.getY()) continue;
            if (mc.player.getOffHandStack().getItem() == Items.PLAYER_HEAD && mc.player.getOffHandStack().hasEnchantments() && !(mc.player.getHealth() <= this.healthWithBall.getValue().floatValue())) continue;
            return true;
        }

        return false;
    }

    @Override
    public void onDisable() {
        this.previousStack = null;
        this.cooldownTicks = 0;
        this.totemWasPopped = false;
        super.onDisable();
    }
}
