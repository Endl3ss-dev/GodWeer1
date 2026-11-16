package tech.lightclient.ui.hud;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.EventPacket;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.render.drag.DraggingManager;
import tech.lightclient.modules.impl.render.Interface;
import tech.lightclient.modules.impl.render.Notifications;
import tech.lightclient.ui.hud.star.SAnim;
import tech.lightclient.ui.hud.star.SEasing;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static tech.lightclient.client.utils.client.IMinecraft.mc;

public class NotificationsOverlay {
    private static final long LIFETIME_TAIL_MS = 380L;
    private static final int MAX_NOTIFICATIONS = 8;
    private static final List<Notification> notifications = new ArrayList<>();

    public static void push(String text, int durationMs) {
        push(text, durationMs, Kind.INFO);
    }

    public static void push(String text, int durationMs, Kind kind) {
        if (text == null || text.isEmpty()) return;
        add(new Notification(text, Math.max(300, durationMs), kind));
    }

    public static void push(String text, int durationMs, Kind kind, String accentText) {
        push(text, durationMs, kind);
    }

    public static void push(String text, int durationMs, Kind kind, String accentText, String customIcon) {
        push(text, durationMs, kind);
    }

    public static void push(String text, int durationMs, ItemStack itemStack) {
        push(text, durationMs, Kind.INFORMATION);
    }

    public static void push(String text, int durationMs, ItemStack itemStack, String accentText) {
        push(text, durationMs, Kind.COOLDOWN);
    }

    public static void push(Text textObject, int durationMs, Kind kind, ItemStack itemStack) {
        if (textObject == null) return;
        add(new Notification(textObject.getString(), Math.max(300, durationMs), kind));
    }

    private static void add(Notification notification) {
        notifications.add(0, notification);
        while (notifications.size() > MAX_NOTIFICATIONS) {
            notifications.remove(notifications.size() - 1);
        }
    }

    /**
     * Активные уведомления в порядке от старых к новым: последнее — самое свежее.
     * Попутно удаляет устаревшие записи, поэтому вызывается каждый кадр из динамического острова.
     */
    public static List<Notification> activeNotifications() {
        long now = System.currentTimeMillis();
        notifications.removeIf(n -> now - n.startMs >= n.durationMs + LIFETIME_TAIL_MS);
        if (notifications.isEmpty() || !notificationsEnabled()) {
            return List.of();
        }
        List<Notification> ordered = new ArrayList<>(notifications);
        Collections.reverse(ordered);
        return ordered;
    }

    public static boolean hasActiveNotification() {
        return !activeNotifications().isEmpty();
    }

    private static boolean notificationsEnabled() {
        try {
            var value = Interface.elements.getByName("Нотификации");
            return value == null || value.get();
        } catch (Throwable ignored) {
            return true;
        }
    }

    public static boolean isHudSettingEnabled(String key, boolean defaultValue) {
        return DraggingManager.getHudBoolean(Notifications.SETTINGS_SCOPE, key, defaultValue);
    }

    public enum Kind {
        ON,
        OFF,
        INFO,
        COOLDOWN,
        INFORMATION,
        PICKUP
    }

    public static class Notification {
        private final String text;
        private final int durationMs;
        private final long startMs;
        private final Kind kind;
        private final SAnim showing = new SAnim(500L, 0.0f, SEasing.BAKEK_SIZE);

        private Notification(String text, int durationMs, Kind kind) {
            this.text = text;
            this.durationMs = durationMs;
            this.startMs = System.currentTimeMillis();
            this.kind = kind;
        }

        public String getText() {
            return text;
        }

        public Kind getKind() {
            return kind;
        }

        public SAnim getShowing() {
            return showing;
        }

        public int getColorArgb() {
            return switch (kind) {
                case ON -> 0xFF4ADE80;
                case OFF -> 0xFFF87171;
                case COOLDOWN -> 0xFFFB923C;
                case PICKUP -> 0xFFC084FC;
                default -> 0xFFFACC15;
            };
        }

        /**
         * Индикатор PICKUP больше не рисуется кругом — вместо него показывается логотип клиента.
         */
        public boolean isLogo() {
            return kind == Kind.PICKUP;
        }

        /**
         * Доля оставшегося времени уведомления в диапазоне [0, 1].
         * Используется как таймер-индикатор: круг тает по мере истечения времени.
         */
        public float getProgress() {
            long elapsed = System.currentTimeMillis() - startMs;
            float p = 1.0f - (float) elapsed / (float) durationMs;
            return Math.max(0.0f, Math.min(1.0f, p));
        }
    }

    @EventHandler
    public void onPacketReceive(EventPacket event) {
        if (mc.world == null || mc.player == null) return;

        boolean check1 = LightClient.getFunctionManager().getNotifications().isState();
        boolean check2 = isHudSettingEnabled(Notifications.SETTING_ITEM_PICKUP, false);
        boolean check3 = isHudSettingEnabled(Notifications.SETTING_ITEM_PICKUP_SHULKER, false);
        boolean onlyDonItems = isHudSettingEnabled(Notifications.SETTING_ONLY_DON_ITEMS, false);

        if (!check1) {
            return;
        }

        if (event.getPacket() instanceof ItemPickupAnimationS2CPacket pickupPacket && check2) {
            if (pickupPacket.getCollectorEntityId() == mc.player.getId()) {
                Entity entity = mc.world.getEntityById(pickupPacket.getEntityId());
                if (entity instanceof ItemEntity itemEntity) {
                    ItemStack itemStack = itemEntity.getStack();
                    if (onlyDonItems && !shouldNotifySpecialItem(itemStack)) {
                        return;
                    }

                    push(Text.literal("Подобран предмет: ").append(itemStack.getName()), 1000, Kind.PICKUP, itemStack.getItem().getDefaultStack());
                }
            }
        } else if (event.getPacket() instanceof ScreenHandlerSlotUpdateS2CPacket slot
                && check3) {
            int slotId = slot.getSlot();
            ContainerComponent updatedContainer = slot.getStack().get(DataComponentTypes.CONTAINER);
            if (updatedContainer != null && slotId < Objects.requireNonNull(mc.player).currentScreenHandler.slots.size() && slot.getSyncId() == 0) {
                ContainerComponent currentContainer = mc.player.currentScreenHandler.getSlot(slotId).getStack().get(DataComponentTypes.CONTAINER);
                if (currentContainer != null) {
                    updatedContainer.stream()
                            .filter(stack -> currentContainer.stream()
                                    .noneMatch(s -> Objects.equals(s.getComponents(), stack.getComponents())
                                            && s.toString().equals(stack.toString())))
                            .forEach(stack -> {
                                if (onlyDonItems && !shouldNotifySpecialItem(stack)) {
                                    return;
                                }
                                push(Text.literal("Предмет выложен в шалкер: ").append(stack.getName()), 1000, Kind.PICKUP, stack.getItem().getDefaultStack());
                            });
                }
            }
        }
    }

    private static boolean shouldNotifySpecialItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return hasCustomColoredName(stack) || isAllowedNetheriteItem(stack);
    }

    private static boolean hasCustomColoredName(ItemStack stack) {
        Text name = stack.getName();
        if (name == null) {
            return false;
        }

        final boolean[] colored = {false};
        name.visit((style, textPart) -> {
            Style resolved = style == null ? Style.EMPTY : style;
            if (resolved.getColor() != null) {
                colored[0] = true;
                return java.util.Optional.of(textPart);
            }
            return java.util.Optional.empty();
        }, Style.EMPTY);
        return colored[0];
    }

    private static boolean isAllowedNetheriteItem(ItemStack stack) {
        return stack.isOf(Items.NETHERITE_HELMET)
                || stack.isOf(Items.NETHERITE_CHESTPLATE)
                || stack.isOf(Items.NETHERITE_LEGGINGS)
                || stack.isOf(Items.NETHERITE_BOOTS)
                || stack.isOf(Items.NETHERITE_SWORD)
                || stack.isOf(Items.NETHERITE_AXE)
                || stack.isOf(Items.NETHERITE_PICKAXE)
                || stack.isOf(Items.NETHERITE_SHOVEL);
    }
}
