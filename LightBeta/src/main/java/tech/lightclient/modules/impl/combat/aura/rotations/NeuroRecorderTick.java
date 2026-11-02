package tech.lightclient.modules.impl.combat.aura.rotations;

import tech.lightclient.LightClient;
import tech.lightclient.client.events.impl.client.TickEvent;
import tech.lightclient.client.utils.eventbus.EventHandler;
import tech.lightclient.client.utils.client.IMinecraft;
import tech.lightclient.modules.impl.combat.AuraModule;
import net.minecraft.entity.LivingEntity;

/**
 * Глобальный тикер рекордеров датасетов (neuro / tneuro).
 *
 * Регистрируется один раз при старте клиента через {@link #init()} и живёт
 * всегда, независимо от состояния Aura. Это позволяет записывать датасет
 * вручную (без Aura), просто нанося удары по цели.
 *
 * Если Aura включена и у неё есть активная цель — передаём её рекордерам,
 * иначе они сами ищут ближайшую живую сущность в радиусе 6 блоков.
 */
public final class NeuroRecorderTick implements IMinecraft {

    private NeuroRecorderTick() {}

    /** Вызывается один раз при старте клиента из LightClient.onInitialize(). */
    public static void init() {
        LightClient.getEventBus().subscribe(new NeuroRecorderTick());
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mc.player == null || mc.world == null) return;

        // Цель: берём из Aura если она включена, иначе null →
        // рекордеры сами найдут ближайшую сущность.
        AuraModule aura = AuraModule.getInstance();
        LivingEntity target = (aura != null && aura.isState()) ? AuraModule.getTarget() : null;

        if (NeuroSelfRecorder.isRecording()) {
            NeuroSelfRecorder.tick(target);
        }
        if (TNeuroSelfRecorder.isRecording()) {
            TNeuroSelfRecorder.tick(target);
        }
    }
}
