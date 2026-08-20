package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Items;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.math.MathHelper;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.UpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.Instance;

import java.util.Iterator;
@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AutoPilot", category = Category.PLAYER)
public class AutoPilot extends Module {

    private final ModeSetting mode = new ModeSetting(this, "Режим", "RW", "Резня", "Вулкан");
    private final SliderSetting distance = new SliderSetting(this, "Дистанция", 30F, 5F, 100F, 1F);

    public static AutoPilot getInstance() {
        return Instance.get(AutoPilot.class);
    }

    @EventHandler
    public void onEvent(UpdateEvent event) {
        boolean skullItemNoNull = false;
        boolean eggItemNoNull = false;
        boolean elytraItemNoNull = false;
        boolean soulLanternNoNull = false;
        boolean nautilusShellNoNull = false;
        boolean gunpowderNoNull = false;

        // Первый проход для проверки наличия предметов в мире
        Iterator<Entity> entityIterator = mc.world.getAllEntities().iterator();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            if (entity instanceof ItemEntity itemEntity) {
                if (itemEntity.getItem().getItem() instanceof SkullItem) {
                    skullItemNoNull = true;
                }
                if (itemEntity.getItem().getItem() instanceof ElytraItem) {
                    elytraItemNoNull = true;
                }
                if (itemEntity.getItem().getItem() instanceof SpawnEggItem) {
                    eggItemNoNull = true;
                }
                if (itemEntity.getItem().getItem() == Items.SOUL_LANTERN) {
                    soulLanternNoNull = true;
                }
                if (itemEntity.getItem().getItem() == Items.NAUTILUS_SHELL) {
                    nautilusShellNoNull = true;
                }
                if (itemEntity.getItem().getItem() == Items.GUNPOWDER) {
                    gunpowderNoNull = true;
                }
            }
        }

        // Второй проход для поворота к предметам в зависимости от режима
        entityIterator = mc.world.getAllEntities().iterator();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();
            if (entity instanceof ItemEntity itemEntity && entity.getDistance(mc.player) <= distance.getValue()) {

                if (mode.is("RW")) {
                    // Режим RW: приоритет на череп, элитры, яйцо спавна
                    if (itemEntity.getItem().getItem() instanceof SkullItem) {
                        rotateToEntity(entity);
                        break;
                    }
                    if (itemEntity.getItem().getItem() instanceof ElytraItem && !skullItemNoNull) {
                        rotateToEntity(entity);
                        break;
                    }
                    if (itemEntity.getItem().getItem() instanceof SpawnEggItem && !elytraItemNoNull && !skullItemNoNull) {
                        rotateToEntity(entity);
                        break;
                    }
                } else if (mode.is("Резня")) {
                    // Режим Резня: приоритет на череп, фонарь душ, элитры, яйцо спавна
                    if (itemEntity.getItem().getItem() instanceof SkullItem) {
                        rotateToEntity(entity);
                        break;
                    }
                    if (itemEntity.getItem().getItem() == Items.SOUL_LANTERN && !skullItemNoNull) {
                        rotateToEntity(entity);
                        break;
                    }
                    if (itemEntity.getItem().getItem() instanceof ElytraItem && !skullItemNoNull && !soulLanternNoNull) {
                        rotateToEntity(entity);
                        break;
                    }
                    if (itemEntity.getItem().getItem() instanceof SpawnEggItem && !elytraItemNoNull && !skullItemNoNull && !soulLanternNoNull) {
                        rotateToEntity(entity);
                        break;
                    }
                } else if (mode.is("Вулкан")) {
                    // Режим Вулкан: приоритет на раковину наутилуса и порох
                    if (itemEntity.getItem().getItem() == Items.NAUTILUS_SHELL) {
                        rotateToEntity(entity);
                        break;
                    }
                    if (itemEntity.getItem().getItem() == Items.GUNPOWDER && !nautilusShellNoNull) {
                        rotateToEntity(entity);
                        break;
                    }
                }
            }
        }
    }

    private void rotateToEntity(Entity entity) {
        float[] rotations = calculateRotations(entity);
        mc.player.rotationYaw = rotations[0];
        mc.player.rotationPitch = rotations[1];
    }

    private float[] calculateRotations(Entity entity) {
        double x = entity.getPosX() - mc.player.getPosX();
        double y = entity.getPosY() - mc.player.getPosY() - 1.5D;
        double z = entity.getPosZ() - mc.player.getPosZ();
        double distance = MathHelper.sqrt(x * x + z * z);
        float yaw = (float) (MathHelper.atan2(z, x) * 57.29577951308232D - 90.0D);
        float pitch = (float) (-MathHelper.atan2(y, distance) * 57.29577951308232D);
        return new float[]{yaw, pitch};
    }
}
