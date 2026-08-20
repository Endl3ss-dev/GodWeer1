package org.privates.client.managers.module.impl.combat;

import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SPlayerListItemPacket;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.events.other.PacketEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.utils.other.Instance;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Category2

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AntiBot", category = Category.COMBAT, description = "Убирает бота с сервера или не дает Ауре его таргетить")
public class AntiBot extends Module {
    public static AntiBot getInstance() {
        return Instance.get(AntiBot.class);
    }

    private final Set<UUID> botSet = new HashSet<>();

    @EventHandler
    public void onEvent(PacketEvent event) {
        IPacket<?> packet = event.getPacket();
        if (packet instanceof SPlayerListItemPacket wrapper) {
            if (wrapper.getAction().equals(SPlayerListItemPacket.Action.ADD_PLAYER)) {
                wrapper.getPlayerAdditionEntries().forEach(data -> {
                    GameProfile profile = data.getProfile();
                    if (mc.getConnection() != null && mc.getConnection().getPlayerInfo(profile.getId()) == null && data.getPing() > 1 && !profile.isComplete()) {
                        botSet.add(profile.getId());
                    }
                });
            } else if (wrapper.getAction().equals(SPlayerListItemPacket.Action.REMOVE_PLAYER)) {
                wrapper.getPlayers().forEach(data -> botSet.remove(data.getProfile().getId()));
            }
        }
    }

    @EventHandler
    public void onTick(GameUpdateEvent gameUpdateEvent) {
        if (mc.world == null) return;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player.equals(mc.player)) continue;

            if (isMatrixBot(player)) {
                botSet.add(player.getUniqueID());
            } else {
                if (botSet.contains(player.getUniqueID())) {
                    boolean isMatrixBot = false;
                    for (PlayerEntity p : mc.world.getPlayers()) {
                        if (p.equals(player) && isMatrixBot(p)) {
                            isMatrixBot = true;
                            break;
                        }
                    }
                    if (!isMatrixBot) {
                        botSet.remove(player.getUniqueID());
                    }
                }
            }
        }
    }

    private boolean isMatrixBot(PlayerEntity player) {
        if (player == mc.player) return false;

        ItemStack boots = player.inventory.armorInventory.get(0);
        ItemStack leggings = player.inventory.armorInventory.get(1);
        ItemStack chestplate = player.inventory.armorInventory.get(2);
        ItemStack helmet = player.inventory.armorInventory.get(3);

        boolean hasFullArmor = boots.getItem() != Items.AIR && leggings.getItem() != Items.AIR &&
                chestplate.getItem() != Items.AIR && helmet.getItem() != Items.AIR;

        if (!hasFullArmor) return false;

        boolean isEnchantable = boots.isEnchantable() && leggings.isEnchantable() &&
                chestplate.isEnchantable() && helmet.isEnchantable();

        boolean hasOffhand = player.getHeldItemOffhand().getItem() != Items.AIR;
        boolean hasMainhand = player.getHeldItemMainhand().getItem() != Items.AIR;

        boolean isLeatherOrIron = (boots.getItem() == Items.LEATHER_BOOTS || leggings.getItem() == Items.LEATHER_LEGGINGS ||
                chestplate.getItem() == Items.LEATHER_CHESTPLATE || helmet.getItem() == Items.LEATHER_HELMET ||
                boots.getItem() == Items.IRON_BOOTS || leggings.getItem() == Items.IRON_LEGGINGS ||
                chestplate.getItem() == Items.IRON_CHESTPLATE || helmet.getItem() == Items.IRON_HELMET);

        boolean isNotDamaged = !boots.isDamaged() && !leggings.isDamaged() &&
                !chestplate.isDamaged() && !helmet.isDamaged();

        boolean hasFullFood = player.getFoodStats().getFoodLevel() == 20;

        return isEnchantable && !hasOffhand && isLeatherOrIron && hasMainhand && isNotDamaged && hasFullFood;
    }


    @EventHandler
    public void onEvent(WorldChangeEvent event) {
        reset();
    }

    public boolean isBot(LivingEntity entity) {
        if (!(entity instanceof PlayerEntity)) return false;
        return botSet.contains(entity.getUniqueID());
    }

    public void reset() {
        botSet.clear();
    }

    @Override
    public void onEnable() {
        reset();
    }

    @Override
    public void onDisable() {
        reset();
    }
}