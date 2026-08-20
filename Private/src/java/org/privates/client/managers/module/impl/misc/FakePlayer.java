package org.privates.client.managers.module.impl.misc;

import com.mojang.authlib.GameProfile;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.annotations.HolyWorld;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.MotionEvent;
import org.privates.client.managers.events.world.WorldChangeEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.StringSetting;
import org.privates.client.utils.other.Instance;

import java.util.UUID;

@Category3
@HolyWorld
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "FakePlayer", category = Category.MISC, description = "Тайлер дёрден")
public class FakePlayer extends Module {
    public static FakePlayer getInstance() {
        return Instance.get(FakePlayer.class);
    }

    private final StringSetting name = new StringSetting(this, "Имя", "FakePlayer");
    private final BooleanSetting copyInventory = new BooleanSetting(this, "Копировать инвентарь", true);
    private final BooleanSetting copyArmor = new BooleanSetting(this, "Копировать броню", true);
    private final BooleanSetting copyOffhand = new BooleanSetting(this, "Копировать предмет в левой руке", true);
    private final BooleanSetting copyHealth = new BooleanSetting(this, "Копировать здоровье", true);
    private final BooleanSetting copyRotation = new BooleanSetting(this, "Копировать поворот головы", true);
    private final BooleanSetting showNameTag = new BooleanSetting(this, "Показывать имя", true);
    private final BooleanSetting invulnerable = new BooleanSetting(this, "Неуязвимость", true);

    private PlayerEntity fakePlayer;

    @Override
    public void toggle() {
        super.toggle();

        if (mc.player == null || mc.world == null) {
            setEnabled(false);
            return;
        }

        if (isEnabled()) {
            spawnFakePlayer();


        } else {
            removeFakePlayer();


        }
    }

    private void spawnFakePlayer() {
        try {
            if (mc.player == null || mc.world == null) return;

            GameProfile profile = new GameProfile(UUID.randomUUID(), name.getValue());

            fakePlayer = new RemoteClientPlayerEntity(mc.world, profile);

            fakePlayer.setPosition(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ());
            fakePlayer.rotationYaw = mc.player.rotationYaw;
            fakePlayer.rotationYawHead = mc.player.rotationYawHead;
            fakePlayer.rotationPitch = mc.player.rotationPitch;
            fakePlayer.prevRotationYaw = mc.player.prevRotationYaw;
            fakePlayer.prevRotationYawHead = mc.player.prevRotationYawHead;
            fakePlayer.prevRotationPitch = mc.player.prevRotationPitch;

            if (copyInventory.getValue()) {
                for (int i = 0; i < mc.player.inventory.mainInventory.size(); i++) {
                    ItemStack stack = mc.player.inventory.mainInventory.get(i);
                    if (!stack.isEmpty()) {
                        fakePlayer.inventory.mainInventory.set(i, stack.copy());
                    }
                }
            }

            if (copyArmor.getValue()) {
                for (int i = 0; i < mc.player.inventory.armorInventory.size(); i++) {
                    ItemStack stack = mc.player.inventory.armorInventory.get(i);
                    if (!stack.isEmpty()) {
                        fakePlayer.inventory.armorInventory.set(i, stack.copy());
                    }
                }
            }

            if (copyOffhand.getValue()) {
                ItemStack stack = mc.player.inventory.offHandInventory.get(0);
                if (!stack.isEmpty()) {
                    fakePlayer.inventory.offHandInventory.set(0, stack.copy());
                }
            }

            if (copyHealth.getValue()) {
                fakePlayer.setHealth(mc.player.getHealth());
                fakePlayer.setAbsorptionAmount(mc.player.getAbsorptionAmount());
            }

            if (invulnerable.getValue()) {
                fakePlayer.setInvulnerable(true);
            }

            fakePlayer.setCustomNameVisible(showNameTag.getValue());

            mc.world.addEntity(fakePlayer.getEntityId(), fakePlayer);
        } catch (Exception e) {

            setEnabled(false);


        }
    }


    private void removeFakePlayer() {
        if (fakePlayer != null && mc.world != null) {
            fakePlayer.remove();
            fakePlayer = null;
        }
    }

    @EventHandler
    public void onMotion(MotionEvent event) {
        if (fakePlayer != null && copyRotation.getValue()) {
            fakePlayer.rotationYaw = mc.player.rotationYaw;
            fakePlayer.rotationYawHead = mc.player.rotationYawHead;
            fakePlayer.rotationPitch = mc.player.rotationPitch;
            fakePlayer.prevRotationYaw = mc.player.prevRotationYaw;
            fakePlayer.prevRotationYawHead = mc.player.prevRotationYawHead;
            fakePlayer.prevRotationPitch = mc.player.prevRotationPitch;

            fakePlayer.swingProgress = mc.player.swingProgress;
            fakePlayer.limbSwing = mc.player.limbSwing;
            fakePlayer.limbSwingAmount = mc.player.limbSwingAmount;
        }
    }

    @EventHandler
    public void onWorldChange(WorldChangeEvent event) {
        if (isEnabled()) {
            setEnabled(false);
        }
    }
}
