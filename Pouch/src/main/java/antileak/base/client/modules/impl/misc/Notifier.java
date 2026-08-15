package antileak.base.client.modules.impl.misc;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import antileak.base.api.events.EventLink;
import antileak.base.api.events.implement.EventUpdate;
import antileak.base.api.storages.implement.helpertstorages.enumvar.ModuleClass;
import antileak.base.api.utils.notification.NotificationManager;
import antileak.base.client.modules.Module;
import antileak.base.client.modules.settings.implement.BooleanSetting;

import java.util.HashSet;
import java.util.Set;

public class Notifier extends Module {

    public static Notifier INSTANCE = new Notifier();

    private final BooleanSetting healthNotif = new BooleanSetting("Здоровье", true);
    private final BooleanSetting potionNotif = new BooleanSetting("Зелья", true);
    private final BooleanSetting itemNotif = new BooleanSetting("Предметы", true);
    private final BooleanSetting friendNotif = new BooleanSetting("Друзья", true);
    private final BooleanSetting enemyNotif = new BooleanSetting("Враг", true);
    private final BooleanSetting enemyItemsNotif = new BooleanSetting("Использование предметов врагов", true);

    private boolean healthNotified = false;
    private final Set<String> notified20Effects = new HashSet<>();
    private final Set<String> notified10Effects = new HashSet<>();
    private final Set<String> notified5Effects = new HashSet<>();
    private final Set<Integer> notifiedItems = new HashSet<>();
    private boolean enemyNotified = false;
    private final Set<String> enemyItemUsage = new HashSet<>();

    private static final String[] USEFUL_ITEMS = {
        "меч крушителя", "арбалет крушителя", "трезубец крушителя",
        "поножи крушителя", "шлем крушителя", "ботинки крушителя",
        "нагрудник крушителя", "талисман крушителя", "талисманк крушителя",
        "лук крушителя", "трапки", "дезориентация", "снег заморозки"
    };

    public Notifier() {
        super("Notifier", "Уведомления о событиях", ModuleCategory.MISC);
        addSettings(healthNotif, potionNotif, itemNotif, friendNotif, enemyNotif, enemyItemsNotif);
    }

    @Override
    public void onDisable() {
        healthNotified = false;
        notified20Effects.clear();
        notified10Effects.clear();
        notified5Effects.clear();
        enemyNotified = false;
        notifiedItems.clear();
        enemyItemUsage.clear();
        super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null) return;

        if (healthNotif.isState()) checkHealth();
        if (potionNotif.isState()) checkPotions();
        if (itemNotif.isState()) checkItems();
        if (enemyNotif.isState()) checkEnemy();
        if (enemyItemsNotif.isState()) checkEnemyItemUsage();
    }

    private void checkHealth() {
        float health = mc.player.getHealth();
        if (health <= 6f && !healthNotified) {
            NotificationManager.pushCustom("§cУ вас осталось 3 сердца!", getCategory().getIcons());
            healthNotified = true;
        } else if (health > 6f) {
            healthNotified = false;
        }
    }

    private void checkPotions() {
        Set<String> current20 = new HashSet<>();
        Set<String> current10 = new HashSet<>();
        Set<String> current5 = new HashSet<>();

        for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
            if (effect.isInfinite()) continue;
            
            int duration = effect.getDuration() / 20;
            String effectName = effect.getEffectType().value().getName().getString();

            if (duration > 0 && duration <= 20) {
                current20.add(effectName);
                if (!notified20Effects.contains(effectName)) {
                    NotificationManager.pushCustom("§e" + effectName + " закончится через 20 сек!", getCategory().getIcons());
                    notified20Effects.add(effectName);
                }
            }
            
            if (duration > 0 && duration <= 10) {
                current10.add(effectName);
                if (!notified10Effects.contains(effectName)) {
                    NotificationManager.pushCustom("§e" + effectName + " закончится через 10 сек!", getCategory().getIcons());
                    notified10Effects.add(effectName);
                }
            }
            
            if (duration > 0 && duration <= 5) {
                current5.add(effectName);
                if (!notified5Effects.contains(effectName)) {
                    NotificationManager.pushCustom("§c" + effectName + " закончится через 5 сек!", getCategory().getIcons());
                    notified5Effects.add(effectName);
                }
            }
        }

        // Убираем эффекты, которых больше нет
        notified20Effects.retainAll(current20);
        notified10Effects.retainAll(current10);
        notified5Effects.retainAll(current5);
    }

    private void checkItems() {
        for (ItemEntity item : mc.world.getEntitiesByClass(ItemEntity.class, mc.player.getBoundingBox().expand(3.0), e -> true)) {
            if (notifiedItems.contains(item.getId())) continue;

            String name = item.getStack().getName().getString().toLowerCase(java.util.Locale.ROOT)
                    .replaceAll("§.", "").trim();

            for (String useful : USEFUL_ITEMS) {
                if (name.contains(useful)) {
                    NotificationManager.pushCustom("§aПоднят: " + item.getStack().getName().getString(), getCategory().getIcons());
                    notifiedItems.add(item.getId());
                    break;
                }
            }
        }

        notifiedItems.removeIf(id -> mc.world.getEntityById(id) == null);
    }

    private void checkEnemy() {
        LivingEntity target = ModuleClass.INSTANCE.aura != null && ModuleClass.INSTANCE.aura.isEnable()
                ? ModuleClass.INSTANCE.aura.getTarget() : null;

        if (target != null && target.isAlive() && target instanceof PlayerEntity) {
            float health = target.getHealth();
            if (health <= 8f && !enemyNotified) {
                NotificationManager.pushCustom("§cПротивник почти убит!", getCategory().getIcons());
                enemyNotified = true;
            }
        } else {
            enemyNotified = false;
        }
    }

    private void checkEnemyItemUsage() {
        if (mc.player == null || mc.world == null) return;

        // Проверяем всех игроков в радиусе
        for (PlayerEntity player : mc.world.getPlayers()) {
            // Пропускаем себя
            if (player == mc.player) continue;
            
            // Пропускаем слишком далеких игроков (> 20 блоков)
            if (mc.player.distanceTo(player) > 20) continue;

            // Проверяем, использует ли игрок предмет
            if (!player.isUsingItem()) {
                // Убираем из списка, если перестал использовать
                enemyItemUsage.remove(player.getName().getString());
                continue;
            }

            // Получаем используемый предмет
            net.minecraft.item.ItemStack activeItem = player.getActiveItem();
            if (activeItem.isEmpty()) continue;

            String playerName = player.getName().getString();
            String itemKey = playerName + ":" + activeItem.getItem().toString();

            // Если уже уведомляли об этом использовании - пропускаем
            if (enemyItemUsage.contains(itemKey)) continue;

            // Проверяем тип предмета и отправляем уведомление
            String itemName = activeItem.getItem().toString();
            String notification = null;
            
            if (itemName.contains("enchanted_golden_apple")) {
                notification = "§6" + playerName + " §fСъел §6§lЗачарованное яблоко!";
            } else if (itemName.contains("golden_apple")) {
                notification = "§e" + playerName + " §fСъел §eЗолотое яблоко";
            } else if (itemName.contains("potion")) {
                // Определяем тип зелья
                String potionType = getPotionType(activeItem);
                if (potionType != null) {
                    notification = "§d" + playerName + " §fВыпил " + potionType;
                }
            }

            if (notification != null) {
                NotificationManager.pushCustom(notification, getCategory().getIcons());
                enemyItemUsage.add(itemKey);
            }
        }

        // Очищаем старые записи (если игрок больше не использует предмет более 5 секунд)
        enemyItemUsage.removeIf(key -> {
            String playerName = key.split(":")[0];
            PlayerEntity player = null;
            for (PlayerEntity p : mc.world.getPlayers()) {
                if (p.getName().getString().equals(playerName)) {
                    player = p;
                    break;
                }
            }
            return player == null || !player.isUsingItem();
        });
    }

    private String getPotionType(net.minecraft.item.ItemStack stack) {
        if (stack.isEmpty()) return null;
        
        // Получаем название предмета
        String displayName = stack.getName().getString();
        
        // Логируем для отладки
        System.out.println("[Notifier] Potion name: " + displayName);
        
        // Проверяем эффекты зелья через PotionContentsComponent
        try {
            net.minecraft.component.type.PotionContentsComponent potionContents = 
                stack.get(net.minecraft.component.DataComponentTypes.POTION_CONTENTS);
            
            if (potionContents != null && potionContents.hasEffects()) {
                boolean hasStrength = false;
                boolean hasHealing = false;
                boolean hasRegeneration = false;
                boolean hasInvisibility = false;
                
                for (var effect : potionContents.getEffects()) {
                    String effectName = effect.getEffectType().value().getName().getString().toLowerCase();
                    System.out.println("[Notifier] Effect: " + effectName);
                    
                    if (effectName.contains("strength") || effectName.contains("сил")) {
                        hasStrength = true;
                    }
                    if (effectName.contains("health") || effectName.contains("healing") || effectName.contains("исцел") || effectName.contains("здоров")) {
                        hasHealing = true;
                    }
                    if (effectName.contains("regeneration") || effectName.contains("регенер")) {
                        hasRegeneration = true;
                    }
                    if (effectName.contains("invisibility") || effectName.contains("невидим")) {
                        hasInvisibility = true;
                    }
                }
                
                // Возвращаем тип зелья по приоритету
                if (hasStrength) return "§cЗелье силы";
                if (hasHealing) return "§aЗелье исцеления";
                if (hasRegeneration) return "§dЗелье регенерации";
                if (hasInvisibility) return "§7Зелье невидимости";
            }
            
        } catch (Exception e) {
            System.out.println("[Notifier] Error reading potion effects: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Если не распознали нужное зелье - возвращаем null (не показываем уведомление)
        System.out.println("[Notifier] Unknown potion, returning null");
        return null;
    }
}
