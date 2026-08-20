package fun.volon.client.modules.impl.player;

import fun.volon.api.events.EventLink;
import fun.volon.api.events.implement.EventUpdate;
import fun.volon.client.modules.Module;

public class AntiAFK extends Module {

    public static AntiAFK INSTANCE = new AntiAFK();

    private long lastActionTime = 0L;
    private long walkStartTime = 0L;
    private long jumpStartTime = 0L;
    private boolean isWalking = false;
    private boolean isJumping = false;
    
    private static final long ACTION_INTERVAL = 5000L; // 5 секунд
    private static final long WALK_DURATION = 2000L;   // 2 секунды
    private static final long JUMP_DURATION = 100L;    // 100 мс для прыжка

    public AntiAFK() {
        super("AntiAFK", "Автоматически нажимает пробел и идет вперед чтобы не кикнуло за АФК", ModuleCategory.PLAYER);
    }

    @Override
    public void onEnable() {
        lastActionTime = System.currentTimeMillis();
        walkStartTime = 0L;
        jumpStartTime = 0L;
        isWalking = false;
        isJumping = false;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        // Отпускаем все клавиши при выключении модуля
        if (mc.player != null && mc.options != null) {
            mc.options.forwardKey.setPressed(false);
            mc.options.jumpKey.setPressed(false);
        }
        isWalking = false;
        isJumping = false;
        super.onDisable();
    }

    @EventLink
    public void onUpdate(EventUpdate event) {
        if (mc.player == null || mc.world == null || mc.options == null) return;

        long currentTime = System.currentTimeMillis();

        // Обработка прыжка
        if (isJumping) {
            if (currentTime - jumpStartTime >= JUMP_DURATION) {
                mc.options.jumpKey.setPressed(false);
                isJumping = false;
            }
        }

        // Если сейчас идем вперед
        if (isWalking) {
            // Проверяем, прошло ли 2 секунды
            if (currentTime - walkStartTime >= WALK_DURATION) {
                // Отпускаем клавишу W
                mc.options.forwardKey.setPressed(false);
                isWalking = false;
                lastActionTime = currentTime;
            }
        } else if (!isJumping) {
            // Проверяем, прошло ли 5 секунд с последнего действия
            if (currentTime - lastActionTime >= ACTION_INTERVAL) {
                // Нажимаем пробел (прыжок)
                mc.options.jumpKey.setPressed(true);
                isJumping = true;
                jumpStartTime = currentTime;
                
                // Начинаем идти вперед
                mc.options.forwardKey.setPressed(true);
                isWalking = true;
                walkStartTime = currentTime;
            }
        }
    }
}
