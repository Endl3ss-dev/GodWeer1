package org.privates.client.managers.module.impl.misc;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import org.privates.client.api.annotations.Category3;
import org.privates.client.api.events.orbit.EventHandler;
import org.privates.client.managers.events.player.AttackEvent;
import org.privates.client.managers.module.Category;
import org.privates.client.managers.module.Module;
import org.privates.client.managers.module.ModuleInfo;
import org.privates.client.managers.module.settings.impl.BooleanSetting;
import org.privates.client.managers.module.settings.impl.ModeSetting;
import org.privates.client.managers.module.settings.impl.SliderSetting;
import org.privates.client.utils.other.SoundUtil;

@Category3
@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@ModuleInfo(name = "KillSound", category = Category.MISC, description = "Звук при убийстве игрока")
public class KillSound extends Module {

    final ModeSetting sound = new ModeSetting(this, "Звук", "Тянка чето пиздит", "Стоны (дайте мне звуки)");
    final SliderSetting volume = new SliderSetting(this, "Громкость", 35.0f, 5.0f, 100.0f, 5.0f);
    final BooleanSetting enableHitSound = new BooleanSetting(this, "Enable HitSound", true);


    @EventHandler
    public void onAttack(AttackEvent event) {
        if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) return;

        Entity target = event.getTarget();

        if (!target.isAlive()) {
            playSound(target);
        }
    }

    private void playSound(Entity entity) {
        String soundFile;
        if (sound.is("Стоны")) {
            int i = (int) (Math.random() * 4) + 1;
            soundFile = "awp" + i + ".wav";
        } else {
            soundFile = sound.getValue() + ".wav";
        }
        if (sound.is("Тянка чето пиздит")) {
            int i = (int) (Math.random() * 4) + 1;
            soundFile = "sound" + i + ".wav";
        } else {
            soundFile = sound.getValue() + ".wav";
        }
        SoundUtil.playSound(soundFile, volume.getValue());
    }

}