package org.privates.client.managers.module.impl.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import org.privates.client.api.annotations.Category2;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.other.GameUpdateEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;

@Category2
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "AirPlace", category = Category.PLAYER, description = "Позволяет использовать блоки в воздухе")
public class AirPlace extends Module {

    final SliderSetting range = new SliderSetting(this, "Дистанция", 5f, 0f, 6f, 0.1f);
    final BooleanSetting swing = new BooleanSetting(this, "Анимация руки", true);

    BlockRayTraceResult hit;

    @EventHandler
    public void onUpdate(GameUpdateEvent event) {
        RayTraceResult hitResult = mc.objectMouseOver;
        if (hitResult instanceof BlockRayTraceResult) {
            hit = (BlockRayTraceResult) hitResult;
        } else {
            hit = null;
            return;
        }

        // Проверяем, есть ли блок или ведро воды/лавы в любой руке
        boolean main = isUsableItem(mc.player.getHeldItemMainhand().getItem());
        boolean off = isUsableItem(mc.player.getHeldItemOffhand().getItem());

        if (mc.gameSettings.keyBindUseItem.isKeyDown() && (main || off)) {
            Hand hand = main ? Hand.MAIN_HAND : Hand.OFF_HAND;
            mc.player.connection.sendPacket(
                    new CPlayerTryUseItemOnBlockPacket(hand, hit)
            );
            if (swing.getValue()) {
                mc.player.swingArm(hand);
            } else {
                mc.player.connection.sendPacket(new CAnimateHandPacket(hand));
            }
        }
    }

    // Можно добавить сюда любые предметы, которые нужно поддерживать
    private boolean isUsableItem(net.minecraft.item.Item item) {
        return item instanceof BlockItem
                || item instanceof BucketItem
                || item == Items.WATER_BUCKET
                || item == Items.LAVA_BUCKET;
    }
}
